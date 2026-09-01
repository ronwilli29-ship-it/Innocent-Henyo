package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
  @PrimaryKey val id: Int = 1,
  val name: String = "Alex Rivera",
  val age: Int = 28,
  val gender: String = "Female", // "Female", "Male", "Other"
  val heightCm: Float = 170f,
  val currentWeightKg: Float = 68f,
  val targetWeightKg: Float = 63f,
  val activityLevel: String = "Moderately Active", // "Sedentary", "Lightly Active", "Moderately Active", "Very Active"
  val dietPreference: String = "Balanced", // "Balanced", "High Protein", "Keto / Low Carb", "Mediterranean", "Vegetarian", "Vegan"
  val goal: String = "Weight Loss", // "Weight Loss", "Maintain Weight", "Muscle Gain"
  val dailyCalorieTarget: Int = 2000,
  val carbTargetGrams: Int = 225,
  val proteinTargetGrams: Int = 125,
  val fatTargetGrams: Int = 67,
  val waterTargetMl: Int = 2500,
  val dailyStepTarget: Int = 8500,
  val activeMinutesTarget: Int = 45,
  val streakDays: Int = 5
) {
  fun calculateBmr(): Float {
    // Mifflin-St Jeor Equation
    return if (gender.equals("Male", ignoreCase = true)) {
      (10 * currentWeightKg) + (6.25f * heightCm) - (5 * age) + 5
    } else {
      (10 * currentWeightKg) + (6.25f * heightCm) - (5 * age) - 161
    }
  }

  fun calculateTdee(): Float {
    val bmr = calculateBmr()
    val multiplier = when (activityLevel) {
      "Sedentary" -> 1.2f
      "Lightly Active" -> 1.375f
      "Moderately Active" -> 1.55f
      "Very Active" -> 1.725f
      else -> 1.4f
    }
    return bmr * multiplier
  }

  fun calculateRecommendedCalories(): Int {
    val tdee = calculateTdee()
    return when (goal) {
      "Weight Loss" -> (tdee - 450).toInt().coerceAtLeast(1200)
      "Muscle Gain" -> (tdee + 350).toInt()
      else -> tdee.toInt()
    }
  }
}
