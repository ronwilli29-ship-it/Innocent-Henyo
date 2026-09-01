package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_plan_items")
data class MealPlanItem(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val date: String, // format: "YYYY-MM-DD"
  val mealType: String, // "Breakfast", "Lunch", "Dinner", "Snack"
  val name: String,
  val calories: Int,
  val carbs: Int, // in grams
  val protein: Int, // in grams
  val fat: Int, // in grams
  val prepTimeMinutes: Int,
  val ingredients: String, // newline-separated or comma-separated
  val instructions: String,
  val dietCategory: String, // "Balanced", "High Protein", "Keto", etc.
  val isLogged: Boolean = false,
  val servings: Float = 1.0f
)
