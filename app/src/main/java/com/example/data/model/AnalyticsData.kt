package com.example.data.model

data class DailySummary(
  val date: String,
  val caloriesConsumed: Int,
  val caloriesBurned: Int,
  val netCalories: Int,
  val targetCalories: Int,
  val carbsGrams: Int,
  val proteinGrams: Int,
  val fatGrams: Int,
  val targetCarbs: Int,
  val targetProtein: Int,
  val targetFat: Int,
  val steps: Int,
  val targetSteps: Int,
  val waterMl: Int,
  val targetWaterMl: Int,
  val activeMinutes: Int,
  val targetActiveMinutes: Int
)

data class DayTrend(
  val date: String, // "YYYY-MM-DD"
  val dayLabel: String, // "Mon", "Tue", etc.
  val caloriesConsumed: Int,
  val caloriesBurned: Int,
  val steps: Int,
  val waterMl: Int,
  val activeMinutes: Int
)

data class MacroRatio(
  val proteinPercent: Int,
  val carbsPercent: Int,
  val fatPercent: Int
)

data class HealthInsight(
  val title: String,
  val description: String,
  val category: String, // "Nutrition", "Activity", "Hydration", "Consistency"
  val iconType: String // "Flame", "Droplet", "Heart", "Star"
)

data class BadgeAchievement(
  val id: String,
  val title: String,
  val description: String,
  val isUnlocked: Boolean,
  val progressPercent: Float,
  val iconSymbol: String
)
