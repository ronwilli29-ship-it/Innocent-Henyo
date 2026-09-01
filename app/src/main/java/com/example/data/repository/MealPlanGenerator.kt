package com.example.data.repository

import com.example.data.model.GroceryItem
import com.example.data.model.MealPlanItem
import com.example.data.model.RecipePreset
import com.example.data.model.RecipePresetsData
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object MealPlanGenerator {

  fun generateDailyPlan(
    date: String,
    dietPreference: String,
    targetCalories: Int
  ): List<MealPlanItem> {
    val recipesForDiet = RecipePresetsData.ALL_RECIPES.filter {
      it.dietCategory.equals(dietPreference, ignoreCase = true) ||
          it.dietCategory.equals("Balanced", ignoreCase = true)
    }

    val breakfasts = recipesForDiet.filter { it.mealType == "Breakfast" }.ifEmpty {
      RecipePresetsData.ALL_RECIPES.filter { it.mealType == "Breakfast" }
    }
    val lunches = recipesForDiet.filter { it.mealType == "Lunch" }.ifEmpty {
      RecipePresetsData.ALL_RECIPES.filter { it.mealType == "Lunch" }
    }
    val dinners = recipesForDiet.filter { it.mealType == "Dinner" }.ifEmpty {
      RecipePresetsData.ALL_RECIPES.filter { it.mealType == "Dinner" }
    }
    val snacks = recipesForDiet.filter { it.mealType == "Snack" }.ifEmpty {
      RecipePresetsData.ALL_RECIPES.filter { it.mealType == "Snack" }
    }

    // Use date hash for consistent yet varying daily rotation
    val seed = date.hashCode()
    val breakfast = breakfasts[(Math.abs(seed) % breakfasts.size)]
    val lunch = lunches[(Math.abs(seed + 1) % lunches.size)]
    val dinner = dinners[(Math.abs(seed + 2) % dinners.size)]
    val snack = snacks[(Math.abs(seed + 3) % snacks.size)]

    return listOf(
      presetToMealPlanItem(date, breakfast),
      presetToMealPlanItem(date, lunch),
      presetToMealPlanItem(date, dinner),
      presetToMealPlanItem(date, snack)
    )
  }

  fun getAlternativeMeals(
    mealType: String,
    currentRecipeName: String,
    dietPreference: String
  ): List<RecipePreset> {
    return RecipePresetsData.ALL_RECIPES.filter {
      it.mealType == mealType && it.name != currentRecipeName
    }
  }

  fun presetToMealPlanItem(date: String, preset: RecipePreset): MealPlanItem {
    return MealPlanItem(
      date = date,
      mealType = preset.mealType,
      name = preset.name,
      calories = preset.calories,
      carbs = preset.carbs,
      protein = preset.protein,
      fat = preset.fat,
      prepTimeMinutes = preset.prepTimeMinutes,
      ingredients = preset.ingredients.joinToString("\n"),
      instructions = preset.instructions,
      dietCategory = preset.dietCategory,
      isLogged = false,
      servings = 1.0f
    )
  }

  fun extractGroceryItems(mealItems: List<MealPlanItem>): List<GroceryItem> {
    val items = mutableListOf<GroceryItem>()
    val seenNames = mutableSetOf<String>()

    for (meal in mealItems) {
      val lines = meal.ingredients.split("\n")
      for (line in lines) {
        val trimmed = line.trim()
        if (trimmed.isNotEmpty() && !seenNames.contains(trimmed.lowercase())) {
          seenNames.add(trimmed.lowercase())
          val category = categorizeIngredient(trimmed)
          items.add(
            GroceryItem(
              name = trimmed,
              amount = "For ${meal.name}",
              category = category,
              isChecked = false
            )
          )
        }
      }
    }
    return items
  }

  private fun categorizeIngredient(ingredient: String): String {
    val lower = ingredient.lowercase()
    return when {
      lower.contains("chicken") || lower.contains("salmon") || lower.contains("egg") ||
          lower.contains("beef") || lower.contains("steak") || lower.contains("tofu") ||
          lower.contains("tuna") || lower.contains("shrimp") || lower.contains("bacon") ||
          lower.contains("cod") || lower.contains("yogurt") || lower.contains("cheese") ||
          lower.contains("milk") || lower.contains("whey") || lower.contains("edamame") ||
          lower.contains("lentils") -> "Proteins & Dairy"

      lower.contains("avocado") || lower.contains("spinach") || lower.contains("cucumber") ||
          lower.contains("tomato") || lower.contains("asparagus") || lower.contains("broccoli") ||
          lower.contains("berries") || lower.contains("lemon") || lower.contains("carrot") ||
          lower.contains("kale") || lower.contains("arugula") || lower.contains("zucchini") ||
          lower.contains("pepper") || lower.contains("garlic") || lower.contains("cilantro") ||
          lower.contains("parsley") || lower.contains("microgreens") -> "Fresh Produce"

      lower.contains("quinoa") || lower.contains("rice") || lower.contains("bread") ||
          lower.contains("sourdough") || lower.contains("oats") || lower.contains("crackers") ||
          lower.contains("chia") || lower.contains("almonds") || lower.contains("chickpea") -> "Pantry & Grains"

      else -> "Seasoning & Oils"
    }
  }
}
