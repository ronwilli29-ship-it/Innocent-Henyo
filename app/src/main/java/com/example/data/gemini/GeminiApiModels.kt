package com.example.data.gemini

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// --- Gemini REST API Request / Response Data Classes ---

@JsonClass(generateAdapter = true)
data class GeminiGenerateContentRequest(
  @Json(name = "contents") val contents: List<GeminiContent>,
  @Json(name = "generationConfig") val generationConfig: GeminiGenerationConfig? = null,
  @Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
  @Json(name = "parts") val parts: List<GeminiPart>,
  @Json(name = "role") val role: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
  @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
  @Json(name = "responseMimeType") val responseMimeType: String? = "application/json",
  @Json(name = "temperature") val temperature: Float? = 0.7f,
  @Json(name = "topP") val topP: Float? = 0.95f,
  @Json(name = "topK") val topK: Int? = 40
)

@JsonClass(generateAdapter = true)
data class GeminiGenerateContentResponse(
  @Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
  @Json(name = "content") val content: GeminiContent? = null,
  @Json(name = "finishReason") val finishReason: String? = null
)

// --- Structured Output Schema for Meal Plan Generation ---

@JsonClass(generateAdapter = true)
data class GeminiMealPlanSchema(
  @Json(name = "nutritionistRationale") val nutritionistRationale: String = "",
  @Json(name = "activityAdaptationNote") val activityAdaptationNote: String = "",
  @Json(name = "totalCalories") val totalCalories: Int = 0,
  @Json(name = "totalProteinGrams") val totalProteinGrams: Int = 0,
  @Json(name = "totalCarbsGrams") val totalCarbsGrams: Int = 0,
  @Json(name = "totalFatGrams") val totalFatGrams: Int = 0,
  @Json(name = "meals") val meals: List<GeminiMealDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class GeminiMealDto(
  @Json(name = "mealType") val mealType: String, // Breakfast, Lunch, Dinner, Snack
  @Json(name = "name") val name: String,
  @Json(name = "calories") val calories: Int,
  @Json(name = "protein") val protein: Int,
  @Json(name = "carbs") val carbs: Int,
  @Json(name = "fat") val fat: Int,
  @Json(name = "prepTimeMinutes") val prepTimeMinutes: Int,
  @Json(name = "ingredients") val ingredients: List<String> = emptyList(),
  @Json(name = "instructions") val instructions: String = "",
  @Json(name = "dietCategory") val dietCategory: String = "Balanced",
  @Json(name = "workoutBenefit") val workoutBenefit: String = ""
)
