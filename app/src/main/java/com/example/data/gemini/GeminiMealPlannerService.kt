package com.example.data.gemini

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ActivityLog
import com.example.data.model.MealPlanItem
import com.example.data.model.UserProfile
import com.example.data.repository.MealPlanGenerator
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AiMealPlanResult(
  val date: String,
  val rationale: String,
  val activityAdaptationNote: String,
  val totalCalories: Int,
  val totalProtein: Int,
  val totalCarbs: Int,
  val totalFat: Int,
  val meals: List<MealPlanItem>,
  val isAiGenerated: Boolean
)

class GeminiMealPlannerService(
  private val apiService: GeminiApiService = GeminiApiService.create()
) {
  private val moshi: Moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

  suspend fun suggestPersonalizedMealPlan(
    date: String,
    userProfile: UserProfile,
    recentActivities: List<ActivityLog>,
    todayStepCount: Int = 0
  ): Result<AiMealPlanResult> = withContext(Dispatchers.IO) {
    try {
      val apiKey = BuildConfig.GEMINI_API_KEY
      if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
        Log.w("GeminiMealPlanner", "GEMINI_API_KEY not configured. Using smart adaptive fallback.")
        return@withContext Result.success(createAdaptiveFallbackPlan(date, userProfile, recentActivities, todayStepCount))
      }

      val prompt = buildPersonalizedMealPrompt(date, userProfile, recentActivities, todayStepCount)
      val systemInstructionText = """
        You are an elite sports nutritionist and registered dietitian.
        Your task is to design a complete 1-day personalized meal plan (Breakfast, Lunch, Dinner, and 1 Healthy Snack)
        tailored strictly to the user's specific nutritional goals, dietary preferences, and recent physical activity history.
        You must return a valid JSON object matching the requested schema exactly, with realistic ingredients, clear cooking instructions, accurate macro breakdown, and scientific rationale connecting the meals to the user's workout demands.
      """.trimIndent()

      val request = GeminiGenerateContentRequest(
        contents = listOf(
          GeminiContent(
            parts = listOf(GeminiPart(text = prompt)),
            role = "user"
          )
        ),
        generationConfig = GeminiGenerationConfig(
          responseMimeType = "application/json",
          temperature = 0.6f
        ),
        systemInstruction = GeminiContent(
          parts = listOf(GeminiPart(text = systemInstructionText))
        )
      )

      val response = apiService.generateContent(apiKey = apiKey, request = request)
      val candidateText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

      if (candidateText.isNullOrBlank()) {
        Log.w("GeminiMealPlanner", "Empty response from Gemini API. Falling back to adaptive plan.")
        return@withContext Result.success(createAdaptiveFallbackPlan(date, userProfile, recentActivities, todayStepCount))
      }

      val planSchema = parseGeminiResponse(candidateText)
      if (planSchema != null && planSchema.meals.isNotEmpty()) {
        val mealPlanItems = planSchema.meals.map { dto ->
          MealPlanItem(
            date = date,
            mealType = dto.mealType,
            name = dto.name,
            calories = dto.calories,
            carbs = dto.carbs,
            protein = dto.protein,
            fat = dto.fat,
            prepTimeMinutes = dto.prepTimeMinutes.coerceAtLeast(5),
            ingredients = dto.ingredients.joinToString("\n"),
            instructions = if (dto.workoutBenefit.isNotBlank()) {
              "${dto.instructions}\n\n💡 Workout Synergy: ${dto.workoutBenefit}"
            } else {
              dto.instructions
            },
            dietCategory = dto.dietCategory.ifBlank { userProfile.dietPreference },
            isLogged = false,
            servings = 1.0f
          )
        }

        val totalCal = mealPlanItems.sumOf { it.calories }
        val totalP = mealPlanItems.sumOf { it.protein }
        val totalC = mealPlanItems.sumOf { it.carbs }
        val totalF = mealPlanItems.sumOf { it.fat }

        val result = AiMealPlanResult(
          date = date,
          rationale = planSchema.nutritionistRationale.ifBlank {
            "Customized specifically for your ${userProfile.goal} goal and ${userProfile.dietPreference} diet."
          },
          activityAdaptationNote = planSchema.activityAdaptationNote.ifBlank {
            "Nutrient timing adjusted to support energy recovery and lean muscle repair."
          },
          totalCalories = if (planSchema.totalCalories > 0) planSchema.totalCalories else totalCal,
          totalProtein = if (planSchema.totalProteinGrams > 0) planSchema.totalProteinGrams else totalP,
          totalCarbs = if (planSchema.totalCarbsGrams > 0) planSchema.totalCarbsGrams else totalC,
          totalFat = if (planSchema.totalFatGrams > 0) planSchema.totalFatGrams else totalF,
          meals = mealPlanItems,
          isAiGenerated = true
        )
        Result.success(result)
      } else {
        Result.success(createAdaptiveFallbackPlan(date, userProfile, recentActivities, todayStepCount))
      }
    } catch (e: Exception) {
      Log.e("GeminiMealPlanner", "Error calling Gemini API: ${e.message}", e)
      Result.success(createAdaptiveFallbackPlan(date, userProfile, recentActivities, todayStepCount))
    }
  }

  private fun parseGeminiResponse(jsonText: String): GeminiMealPlanSchema? {
    return try {
      val cleanedJson = jsonText.trim()
        .removePrefix("```json")
        .removePrefix("```")
        .removeSuffix("```")
        .trim()
      val adapter = moshi.adapter(GeminiMealPlanSchema::class.java)
      adapter.fromJson(cleanedJson)
    } catch (e: Exception) {
      Log.e("GeminiMealPlanner", "Failed to parse JSON response: $jsonText", e)
      null
    }
  }

  private fun buildPersonalizedMealPrompt(
    date: String,
    profile: UserProfile,
    activities: List<ActivityLog>,
    stepCount: Int
  ): String {
    val activitiesDescription = if (activities.isEmpty()) {
      "No high-intensity workouts logged yet for today (Logged Steps: $stepCount)."
    } else {
      activities.joinToString("\n") { act ->
        "- ${act.activityType} (${act.intensity} intensity, ${act.durationMinutes} mins, ${act.caloriesBurned} kcal burned${if (act.distanceKm != null) ", ${act.distanceKm} km" else ""})${if (act.notes.isNotBlank()) " Notes: ${act.notes}" else ""}"
      } + "\n- Daily Steps: $stepCount"
    }

    val totalActivityBurn = activities.sumOf { it.caloriesBurned }

    return """
      Please generate a comprehensive 1-day personalized meal plan for Date: $date.
      
      ### User Profile & Biometrics:
      - Name: ${profile.name}
      - Age: ${profile.age} | Gender: ${profile.gender}
      - Height: ${profile.heightCm} cm | Weight: ${profile.currentWeightKg} kg (Target: ${profile.targetWeightKg} kg)
      - Primary Fitness Goal: ${profile.goal}
      - Dietary Preference: ${profile.dietPreference}
      - Target Daily Calories: ${profile.dailyCalorieTarget} kcal
      - Target Daily Macros: Protein: ${profile.proteinTargetGrams}g, Carbs: ${profile.carbTargetGrams}g, Fat: ${profile.fatTargetGrams}g
      - Activity Level: ${profile.activityLevel}
      
      ### Recent Activity History & Workout Demands:
      $activitiesDescription
      Total direct workout calories burned: $totalActivityBurn kcal.
      
      ### Requirements:
      1. Provide exactly 4 meal items: "Breakfast", "Lunch", "Dinner", "Snack".
      2. Sum of calories should closely match target (${profile.dailyCalorieTarget} kcal ± 5%).
      3. Tailor ingredients and macro distribution to aid recovery from the logged activities (e.g. running, strength training) while respecting the ${profile.dietPreference} guidelines and ${profile.goal} goal.
      4. Return your output in the following JSON schema:
      {
        "nutritionistRationale": "2-3 sentences explaining how this plan specifically addresses the user's nutritional goal and activity history.",
        "activityAdaptationNote": "Specific explanation of how post-workout recovery or daily movement was factored into protein/carbohydrate distribution.",
        "totalCalories": 2000,
        "totalProteinGrams": 125,
        "totalCarbsGrams": 225,
        "totalFatGrams": 67,
        "meals": [
          {
            "mealType": "Breakfast",
            "name": "Recipe Name",
            "calories": 480,
            "protein": 32,
            "carbs": 50,
            "fat": 14,
            "prepTimeMinutes": 15,
            "ingredients": ["1 cup Rolled Oats", "1 scoop Whey Protein", "1/2 cup Fresh Berries", "1 tbsp Chia Seeds"],
            "instructions": "Step 1: Combine oats and water... Step 2: Stir in protein powder...",
            "dietCategory": "${profile.dietPreference}",
            "workoutBenefit": "Provides complex slow-release carbohydrates and leucine for muscle protein synthesis."
          }
        ]
      }
    """.trimIndent()
  }

  private fun createAdaptiveFallbackPlan(
    date: String,
    profile: UserProfile,
    activities: List<ActivityLog>,
    stepCount: Int
  ): AiMealPlanResult {
    val totalBurn = activities.sumOf { it.caloriesBurned }
    val workoutNames = activities.map { it.activityType }.distinct()

    val fallbackMeals = MealPlanGenerator.generateDailyPlan(
      date = date,
      dietPreference = profile.dietPreference,
      targetCalories = profile.dailyCalorieTarget
    )

    val rationale = if (workoutNames.isNotEmpty()) {
      "Personalized for your ${profile.dietPreference} preference, calibrated to fuel and recover from your ${workoutNames.joinToString(", ")} sessions (${totalBurn} kcal burned)."
    } else {
      "Nutritionally optimized for your ${profile.goal} target and ${profile.dailyCalorieTarget} kcal energy goal."
    }

    val adaptation = if (totalBurn > 250) {
      "Enhanced with bioavailable protein and essential micronutrients to accelerate muscular recovery and glycogen restoration."
    } else {
      "Balanced glycemic load to sustain steady all-day focus and metabolic health."
    }

    return AiMealPlanResult(
      date = date,
      rationale = rationale,
      activityAdaptationNote = adaptation,
      totalCalories = fallbackMeals.sumOf { it.calories },
      totalProtein = fallbackMeals.sumOf { it.protein },
      totalCarbs = fallbackMeals.sumOf { it.carbs },
      totalFat = fallbackMeals.sumOf { it.fat },
      meals = fallbackMeals,
      isAiGenerated = false
    )
  }
}
