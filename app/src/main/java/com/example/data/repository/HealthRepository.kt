package com.example.data.repository

import android.content.Context
import com.example.data.gemini.AiMealPlanResult
import com.example.data.gemini.GeminiMealPlannerService
import com.example.data.local.AppDao
import com.example.data.local.AppDatabase
import com.example.data.model.ActivityLog
import com.example.data.model.BadgeAchievement
import com.example.data.model.DailyStepAndWater
import com.example.data.model.DailySummary
import com.example.data.model.DayTrend
import com.example.data.model.GroceryItem
import com.example.data.model.HealthInsight
import com.example.data.model.MacroRatio
import com.example.data.model.MealPlanItem
import com.example.data.model.RecipePreset
import com.example.data.model.ReminderItem
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class HealthRepository(
  private val dao: AppDao,
  private val context: Context,
  private val geminiService: GeminiMealPlannerService = GeminiMealPlannerService()
) {

  val userProfileFlow: Flow<UserProfile?> = dao.getUserProfileFlow()
  val allRemindersFlow: Flow<List<ReminderItem>> = dao.getAllReminders()
  val groceryItemsFlow: Flow<List<GroceryItem>> = dao.getAllGroceryItems()

  fun getMealsForDate(date: String): Flow<List<MealPlanItem>> = dao.getMealsForDate(date)
  fun getActivitiesForDate(date: String): Flow<List<ActivityLog>> = dao.getActivitiesForDate(date)
  fun getStepAndWaterForDate(date: String): Flow<DailyStepAndWater?> = dao.getStepAndWaterForDate(date)

  suspend fun initializeSeedDataIfNeeded() {
    val existingProfile = dao.getUserProfileOnce()
    if (existingProfile == null) {
      val defaultProfile = UserProfile()
      dao.insertOrUpdateProfile(defaultProfile)

      // Seed 7 days of meal plans (3 days before today, today, 3 days after)
      val today = LocalDate.now()
      val formatter = DateTimeFormatter.ISO_LOCAL_DATE
      for (offset in -3..3) {
        val date = today.plusDays(offset.toLong()).format(formatter)
        val meals = MealPlanGenerator.generateDailyPlan(
          date = date,
          dietPreference = defaultProfile.dietPreference,
          targetCalories = defaultProfile.dailyCalorieTarget
        )
        // Mark past and some of today's meals as logged for a realistic experience
        val updatedMeals = if (offset < 0) {
          meals.map { it.copy(isLogged = true) }
        } else if (offset == 0) {
          meals.mapIndexed { index, meal ->
            if (index < 2) meal.copy(isLogged = true) else meal
          }
        } else {
          meals
        }
        dao.insertMeals(updatedMeals)

        // Seed steps & water
        val steps = when (offset) {
          -3 -> 8900
          -2 -> 10200
          -1 -> 7600
          0 -> 6450
          else -> 0
        }
        val water = when (offset) {
          -3 -> 2250
          -2 -> 2750
          -1 -> 2500
          0 -> 1750
          else -> 0
        }
        if (steps > 0 || water > 0) {
          dao.insertOrUpdateStepAndWater(DailyStepAndWater(date = date, stepCount = steps, waterIntakeMl = water))
        }

        // Seed some past activities
        if (offset == -3) {
          dao.insertActivity(
            ActivityLog(
              date = date,
              activityType = "Running",
              durationMinutes = 35,
              caloriesBurned = 360,
              intensity = "High",
              distanceKm = 4.8f,
              notes = "Morning park run with good pace"
            )
          )
        } else if (offset == -2) {
          dao.insertActivity(
            ActivityLog(
              date = date,
              activityType = "Strength Training",
              durationMinutes = 45,
              caloriesBurned = 280,
              intensity = "Moderate",
              notes = "Upper body push routine"
            )
          )
        } else if (offset == -1) {
          dao.insertActivity(
            ActivityLog(
              date = date,
              activityType = "Yoga",
              durationMinutes = 30,
              caloriesBurned = 140,
              intensity = "Low",
              notes = "Vinyasa flow & recovery stretching"
            )
          )
        } else if (offset == 0) {
          dao.insertActivity(
            ActivityLog(
              date = date,
              activityType = "Brisk Walking",
              durationMinutes = 25,
              caloriesBurned = 120,
              intensity = "Moderate",
              distanceKm = 2.2f,
              notes = "Midday walk outdoors"
            )
          )
        }
      }

      // Seed Reminders
      dao.insertReminders(
        listOf(
          ReminderItem(type = "BREAKFAST", title = "Energizing Breakfast", message = "Time to fuel up for the morning with healthy nutrients!", hour = 8, minute = 0, isEnabled = true),
          ReminderItem(type = "WATER", title = "Hydration Boost", message = "Drink a glass of fresh water to keep metabolic rate peak!", hour = 11, minute = 0, isEnabled = true),
          ReminderItem(type = "LUNCH", title = "Mindful Lunch", message = "Enjoy your planned balanced lunch.", hour = 12, minute = 30, isEnabled = true),
          ReminderItem(type = "WORKOUT", title = "Daily Movement", message = "Ready for your daily workout or brisk walk?", hour = 17, minute = 30, isEnabled = true),
          ReminderItem(type = "DINNER", title = "Nourishing Dinner", message = "Time for your evening dinner meal.", hour = 19, minute = 0, isEnabled = true),
          ReminderItem(type = "EVENING_REVIEW", title = "Evening Progress Check", message = "Log any remaining meals and review your streaks!", hour = 21, minute = 0, isEnabled = true)
        )
      )

      // Seed initial grocery list from today's meals
      val todayDate = today.format(formatter)
      val todayMeals = dao.getMealsForDate(todayDate).firstOrNull() ?: emptyList()
      val groceries = MealPlanGenerator.extractGroceryItems(todayMeals)
      if (groceries.isNotEmpty()) {
        dao.insertGroceryItems(groceries)
      }
    }
  }

  // Profile operations
  suspend fun updateProfile(profile: UserProfile) {
    dao.insertOrUpdateProfile(profile)
  }

  // Meal operations
  suspend fun addMeal(meal: MealPlanItem): Long = dao.insertMeal(meal)

  suspend fun updateMeal(meal: MealPlanItem) = dao.updateMeal(meal)

  suspend fun deleteMeal(meal: MealPlanItem) = dao.deleteMeal(meal)

  suspend fun toggleMealLogged(id: Long, isLogged: Boolean) {
    dao.updateMealLoggedStatus(id, isLogged)
  }

  suspend fun regenerateDayMealPlan(date: String, dietPreference: String, targetCalories: Int) {
    dao.deleteMealsForDate(date)
    val newPlan = MealPlanGenerator.generateDailyPlan(date, dietPreference, targetCalories)
    dao.insertMeals(newPlan)
  }

  suspend fun generateGeminiMealPlan(date: String): Result<AiMealPlanResult> {
    val profile = dao.getUserProfileOnce() ?: UserProfile()
    val activities = dao.getActivitiesForDateOnce(date)
    val stepWater = dao.getStepAndWaterOnce(date)
    val steps = stepWater?.stepCount ?: 0

    return geminiService.suggestPersonalizedMealPlan(
      date = date,
      userProfile = profile,
      recentActivities = activities,
      todayStepCount = steps
    )
  }

  suspend fun applyMealPlan(date: String, meals: List<MealPlanItem>, autoUpdateGroceries: Boolean = true) {
    dao.deleteMealsForDate(date)
    dao.insertMeals(meals)
    if (autoUpdateGroceries) {
      val newGroceries = MealPlanGenerator.extractGroceryItems(meals)
      if (newGroceries.isNotEmpty()) {
        dao.insertGroceryItems(newGroceries)
      }
    }
  }

  suspend fun swapMealWithPreset(mealToReplace: MealPlanItem, preset: RecipePreset) {
    val updated = mealToReplace.copy(
      name = preset.name,
      calories = preset.calories,
      carbs = preset.carbs,
      protein = preset.protein,
      fat = preset.fat,
      prepTimeMinutes = preset.prepTimeMinutes,
      ingredients = preset.ingredients.joinToString("\n"),
      instructions = preset.instructions,
      dietCategory = preset.dietCategory,
      isLogged = false
    )
    dao.updateMeal(updated)
  }

  // Activity operations
  suspend fun logActivity(activity: ActivityLog): Long = dao.insertActivity(activity)

  suspend fun deleteActivity(activity: ActivityLog) = dao.deleteActivity(activity)

  // Step and Water operations
  suspend fun addWater(date: String, amountMl: Int) {
    val current = dao.getStepAndWaterOnce(date) ?: DailyStepAndWater(date = date)
    val updated = current.copy(waterIntakeMl = (current.waterIntakeMl + amountMl).coerceAtLeast(0))
    dao.insertOrUpdateStepAndWater(updated)
  }

  suspend fun addSteps(date: String, count: Int) {
    val current = dao.getStepAndWaterOnce(date) ?: DailyStepAndWater(date = date)
    val updated = current.copy(stepCount = (current.stepCount + count).coerceAtLeast(0))
    dao.insertOrUpdateStepAndWater(updated)
  }

  suspend fun setStepsAndWater(date: String, steps: Int, waterMl: Int) {
    dao.insertOrUpdateStepAndWater(DailyStepAndWater(date = date, stepCount = steps, waterIntakeMl = waterMl))
  }

  // Reminder operations
  suspend fun updateReminder(reminder: ReminderItem) = dao.updateReminder(reminder)
  suspend fun addReminder(reminder: ReminderItem) = dao.insertReminder(reminder)
  suspend fun deleteReminder(reminder: ReminderItem) = dao.deleteReminder(reminder)

  // Grocery operations
  suspend fun toggleGroceryItem(item: GroceryItem) {
    dao.updateGroceryItem(item.copy(isChecked = !item.isChecked))
  }

  suspend fun addGroceryItem(name: String, category: String) {
    dao.insertGroceryItem(GroceryItem(name = name, category = category))
  }

  suspend fun deleteGroceryItem(item: GroceryItem) = dao.deleteGroceryItem(item)

  suspend fun clearCheckedGroceries() = dao.clearCheckedGroceryItems()

  suspend fun generateGroceriesFromDateRange(startDate: String, endDate: String) {
    val meals = dao.getMealsForDateRange(startDate, endDate).firstOrNull() ?: emptyList()
    val newGroceries = MealPlanGenerator.extractGroceryItems(meals)
    dao.clearAllGroceryItems()
    dao.insertGroceryItems(newGroceries)
  }

  // Analytics query helpers
  fun getDailySummaryFlow(date: String): Flow<DailySummary> {
    return combine(
      dao.getUserProfileFlow(),
      dao.getMealsForDate(date),
      dao.getActivitiesForDate(date),
      dao.getStepAndWaterForDate(date)
    ) { profile, meals, activities, stepWater ->
      val user = profile ?: UserProfile()
      val loggedMeals = meals.filter { it.isLogged }
      val caloriesConsumed = loggedMeals.sumOf { (it.calories * it.servings).toInt() }
      val carbs = loggedMeals.sumOf { (it.carbs * it.servings).toInt() }
      val protein = loggedMeals.sumOf { (it.protein * it.servings).toInt() }
      val fat = loggedMeals.sumOf { (it.fat * it.servings).toInt() }

      val activityBurn = activities.sumOf { it.caloriesBurned }
      val activeMinutes = activities.sumOf { it.durationMinutes }
      val steps = stepWater?.stepCount ?: 0
      val water = stepWater?.waterIntakeMl ?: 0

      // Add estimated calories from steps (approx 0.04 kcal per step)
      val stepBurn = (steps * 0.04f).toInt()
      val totalBurn = activityBurn + stepBurn

      DailySummary(
        date = date,
        caloriesConsumed = caloriesConsumed,
        caloriesBurned = totalBurn,
        netCalories = caloriesConsumed - totalBurn,
        targetCalories = user.dailyCalorieTarget,
        carbsGrams = carbs,
        proteinGrams = protein,
        fatGrams = fat,
        targetCarbs = user.carbTargetGrams,
        targetProtein = user.proteinTargetGrams,
        targetFat = user.fatTargetGrams,
        steps = steps,
        targetSteps = user.dailyStepTarget,
        waterMl = water,
        targetWaterMl = user.waterTargetMl,
        activeMinutes = activeMinutes,
        targetActiveMinutes = user.activeMinutesTarget
      )
    }
  }

  fun getTrendDataFlow(daysCount: Int = 7): Flow<List<DayTrend>> {
    val today = LocalDate.now()
    val startDate = today.minusDays((daysCount - 1).toLong()).format(DateTimeFormatter.ISO_LOCAL_DATE)
    val endDate = today.format(DateTimeFormatter.ISO_LOCAL_DATE)

    return combine(
      dao.getMealsForDateRange(startDate, endDate),
      dao.getActivitiesForDateRange(startDate, endDate),
      dao.getStepAndWaterForDateRange(startDate, endDate)
    ) { meals, activities, stepWaters ->
      val trends = mutableListOf<DayTrend>()
      val formatter = DateTimeFormatter.ISO_LOCAL_DATE

      for (i in (daysCount - 1) downTo 0) {
        val currentDay = today.minusDays(i.toLong())
        val dateStr = currentDay.format(formatter)
        val dayLabel = currentDay.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())

        val dayMeals = meals.filter { it.date == dateStr && it.isLogged }
        val dayActivities = activities.filter { it.date == dateStr }
        val dayStepWater = stepWaters.find { it.date == dateStr }

        val consumed = dayMeals.sumOf { (it.calories * it.servings).toInt() }
        val actBurn = dayActivities.sumOf { it.caloriesBurned }
        val steps = dayStepWater?.stepCount ?: 0
        val water = dayStepWater?.waterIntakeMl ?: 0
        val actMins = dayActivities.sumOf { it.durationMinutes }
        val totalBurn = actBurn + (steps * 0.04f).toInt()

        trends.add(
          DayTrend(
            date = dateStr,
            dayLabel = dayLabel,
            caloriesConsumed = consumed,
            caloriesBurned = totalBurn,
            steps = steps,
            waterMl = water,
            activeMinutes = actMins
          )
        )
      }
      trends
    }
  }

  fun generateInsights(
    summary: DailySummary,
    profile: UserProfile,
    trends: List<DayTrend>
  ): List<HealthInsight> {
    val list = mutableListOf<HealthInsight>()

    // Calorie & Macro Insight
    if (summary.caloriesConsumed > 0) {
      val remaining = summary.targetCalories - summary.caloriesConsumed
      if (remaining > 300) {
        list.add(
          HealthInsight(
            title = "Calorie Budget on Track",
            description = "You have ${remaining} kcal remaining for today. Consider a high-protein snack if you feel hungry after your workout.",
            category = "Nutrition",
            iconType = "Flame"
          )
        )
      } else if (remaining in -100..300) {
        list.add(
          HealthInsight(
            title = "Perfect Energy Balance",
            description = "Your energy intake matches your daily goal of ${summary.targetCalories} kcal with high precision. Outstanding consistency!",
            category = "Nutrition",
            iconType = "Star"
          )
        )
      } else {
        list.add(
          HealthInsight(
            title = "Calorie Surplus Detected",
            description = "You're slightly above your target by ${-remaining} kcal. A light 20-minute evening walk will help burn excess calories.",
            category = "Nutrition",
            iconType = "Flame"
          )
        )
      }
    } else {
      list.add(
        HealthInsight(
          title = "Ready to Fuel Your Day",
          description = "Check your planned meals to stay aligned with your ${profile.goal} goal today.",
          category = "Nutrition",
          iconType = "Flame"
        )
      )
    }

    // Hydration Insight
    val waterPercent = if (summary.targetWaterMl > 0) (summary.waterMl * 100) / summary.targetWaterMl else 0
    if (waterPercent < 50) {
      list.add(
        HealthInsight(
          title = "Hydration Reminder",
          description = "You've logged ${summary.waterMl}ml of water (${waterPercent}% of target). Drink 2 more glasses to optimize digestion and energy.",
          category = "Hydration",
          iconType = "Droplet"
        )
      )
    } else {
      list.add(
        HealthInsight(
          title = "Great Hydration Habit",
          description = "You reached ${waterPercent}% of your hydration target! Proper water intake accelerates recovery and improves sleep quality.",
          category = "Hydration",
          iconType = "Droplet"
        )
      )
    }

    // Activity & Movement Insight
    if (summary.activeMinutes >= summary.targetActiveMinutes) {
      list.add(
        HealthInsight(
          title = "Activity Target Crushed",
          description = "You logged ${summary.activeMinutes} active minutes today, beating your daily target of ${summary.targetActiveMinutes}m. Awesome work!",
          category = "Activity",
          iconType = "Heart"
        )
      )
    } else {
      val needed = summary.targetActiveMinutes - summary.activeMinutes
      list.add(
        HealthInsight(
          title = "Keep Moving",
          description = "${needed} more active minutes needed to hit your daily movement goal. A quick stretch or brisk stroll counts!",
          category = "Activity",
          iconType = "Heart"
        )
      )
    }

    return list
  }

  fun getAchievements(
    profile: UserProfile,
    summary: DailySummary,
    trends: List<DayTrend>
  ): List<BadgeAchievement> {
    val highStepDays = trends.count { it.steps >= profile.dailyStepTarget }
    val loggedMealDays = trends.count { it.caloriesConsumed > 1000 }
    val hydrationDays = trends.count { it.waterMl >= profile.waterTargetMl }

    return listOf(
      BadgeAchievement(
        id = "streak_starter",
        title = "Consistency Champion",
        description = "Maintain a healthy log streak for 5 consecutive days",
        isUnlocked = profile.streakDays >= 5,
        progressPercent = (profile.streakDays / 5f).coerceIn(0f, 1f),
        iconSymbol = "🔥"
      ),
      BadgeAchievement(
        id = "hydration_hero",
        title = "Hydration Hero",
        description = "Hit your daily water intake goal 3 times this week",
        isUnlocked = hydrationDays >= 3,
        progressPercent = (hydrationDays / 3f).coerceIn(0f, 1f),
        iconSymbol = "💧"
      ),
      BadgeAchievement(
        id = "step_master",
        title = "10k Steps Club",
        description = "Achieve daily step targets across active days",
        isUnlocked = highStepDays >= 4,
        progressPercent = (highStepDays / 4f).coerceIn(0f, 1f),
        iconSymbol = "👟"
      ),
      BadgeAchievement(
        id = "meal_planner_pro",
        title = "Nutrition Master",
        description = "Log your planned meals with balanced macro distribution",
        isUnlocked = loggedMealDays >= 5,
        progressPercent = (loggedMealDays / 5f).coerceIn(0f, 1f),
        iconSymbol = "🥗"
      ),
      BadgeAchievement(
        id = "active_warrior",
        title = "Active Warrior",
        description = "Log at least 150 total active workout minutes this week",
        isUnlocked = trends.sumOf { it.activeMinutes } >= 150,
        progressPercent = (trends.sumOf { it.activeMinutes } / 150f).coerceIn(0f, 1f),
        iconSymbol = "⚡"
      )
    )
  }
}
