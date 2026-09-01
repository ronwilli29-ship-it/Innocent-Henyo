package com.example.data.model

data class RecipePreset(
  val name: String,
  val mealType: String, // "Breakfast", "Lunch", "Dinner", "Snack"
  val dietCategory: String, // "Balanced", "High Protein", "Keto / Low Carb", "Mediterranean", "Vegetarian", "Vegan"
  val calories: Int,
  val carbs: Int,
  val protein: Int,
  val fat: Int,
  val prepTimeMinutes: Int,
  val ingredients: List<String>,
  val instructions: String,
  val tags: List<String> = emptyList()
)

object RecipePresetsData {
  val ALL_RECIPES = listOf(
    // Balanced
    RecipePreset(
      name = "Avocado & Poached Egg Sourdough",
      mealType = "Breakfast",
      dietCategory = "Balanced",
      calories = 420,
      carbs = 38,
      protein = 18,
      fat = 22,
      prepTimeMinutes = 12,
      ingredients = listOf("2 slices artisanal sourdough", "1 ripe avocado, mashed", "2 organic eggs", "Chili flakes & microgreens", "1 tsp extra virgin olive oil"),
      instructions = "Toast the sourdough slices until golden. Poach eggs in simmering water for 3-4 minutes. Spread creamy avocado over toast, crown with poached eggs, and season with chili flakes and microgreens.",
      tags = listOf("Quick", "Heart Healthy", "Fiber Rich")
    ),
    RecipePreset(
      name = "Mediterranean Grilled Chicken Quinoa Bowl",
      mealType = "Lunch",
      dietCategory = "Balanced",
      calories = 550,
      carbs = 52,
      protein = 44,
      fat = 16,
      prepTimeMinutes = 20,
      ingredients = listOf("160g chicken breast, seasoned & grilled", "1 cup cooked fluffy quinoa", "1 cup English cucumber & cherry tomatoes", "2 tbsp kalamata olives", "2 tbsp crumbled Greek feta", "Lemon oregano vinaigrette"),
      instructions = "Toss warm quinoa with cucumber, tomatoes, and kalamata olives. Slice the grilled chicken breast and place alongside. Sprinkle with feta and drizzle lemon oregano dressing.",
      tags = listOf("High Fiber", "Balanced Energy", "Meal Prep Friendly")
    ),
    RecipePreset(
      name = "Pan-Seared Salmon with Roasted Asparagus & Sweet Potato",
      mealType = "Dinner",
      dietCategory = "Balanced",
      calories = 580,
      carbs = 40,
      protein = 42,
      fat = 26,
      prepTimeMinutes = 25,
      ingredients = listOf("180g wild salmon fillet", "1 bunch tender asparagus spears", "1 medium sweet potato, cubed", "1 tbsp olive oil", "Fresh dill & lemon wedges", "Garlic herb seasoning"),
      instructions = "Roast cubed sweet potatoes and asparagus at 200°C for 20 mins. Sear salmon skin-side down for 4 mins, flip for 3 mins until flaky. Serve with fresh lemon and fresh dill.",
      tags = listOf("Omega-3", "Anti-inflammatory", "Gluten-Free")
    ),
    RecipePreset(
      name = "Greek Yogurt Parfait with Mixed Berries & Chia",
      mealType = "Snack",
      dietCategory = "Balanced",
      calories = 210,
      carbs = 24,
      protein = 18,
      fat = 4,
      prepTimeMinutes = 5,
      ingredients = listOf("1 cup 0% Greek yogurt", "1/2 cup fresh raspberries & blueberries", "1 tbsp chia seeds", "1 tsp pure honey or maple syrup"),
      instructions = "Layer Greek yogurt in a glass with fresh berries and chia seeds. Drizzle with raw honey for subtle natural sweetness.",
      tags = listOf("Probiotic", "No Cook", "High Protein")
    ),

    // High Protein
    RecipePreset(
      name = "Power Egg White Omelet with Turkey Bacon & Spinach",
      mealType = "Breakfast",
      dietCategory = "High Protein",
      calories = 380,
      carbs = 10,
      protein = 46,
      fat = 14,
      prepTimeMinutes = 15,
      ingredients = listOf("1 cup liquid egg whites + 1 whole egg", "3 slices lean turkey bacon, diced", "1.5 cups baby spinach", "1/4 cup reduced-fat mozzarella", "1 tsp olive oil"),
      instructions = "Sauté turkey bacon and baby spinach until wilted. Pour egg mixture over skillet on medium heat. Fold over when set and melt mozzarella inside.",
      tags = listOf("Muscle Building", "Low Carb", "Fast Prep")
    ),
    RecipePreset(
      name = "Tuna Steak & Edamame Brown Rice Bowl",
      mealType = "Lunch",
      dietCategory = "High Protein",
      calories = 520,
      carbs = 48,
      protein = 52,
      fat = 12,
      prepTimeMinutes = 15,
      ingredients = listOf("180g seared ahi tuna or solid light tuna", "1/2 cup shelled edamame", "3/4 cup steamed brown rice", "1/2 cup shredded carrots & red cabbage", "Sesame ginger soy glaze"),
      instructions = "Sear tuna steak for 1-2 minutes per side. Arrange over brown rice with steamed edamame, crunchy vegetables, and drizzle with sesame ginger sauce.",
      tags = listOf("Ultra High Protein", "Lean", "Clean Fuel")
    ),
    RecipePreset(
      name = "Lean Flank Steak with Garlic Roasted Broccoli & Quinoa",
      mealType = "Dinner",
      dietCategory = "High Protein",
      calories = 590,
      carbs = 36,
      protein = 56,
      fat = 22,
      prepTimeMinutes = 25,
      ingredients = listOf("200g lean flank steak, marinated", "2 cups roasted broccoli florets", "1/2 cup cooked quinoa", "2 cloves minced garlic", "1 tbsp olive oil", "Sea salt & cracked pepper"),
      instructions = "Grill or pan-sear flank steak to medium rare (4-5 min/side). Rest 5 minutes then slice across grain. Serve with charred garlic broccoli and quinoa.",
      tags = listOf("Iron Rich", "Strength Fuel", "Nutrient Dense")
    ),
    RecipePreset(
      name = "Vanilla Whey Protein Shake with Peanut Butter & Almond Milk",
      mealType = "Snack",
      dietCategory = "High Protein",
      calories = 280,
      carbs = 12,
      protein = 35,
      fat = 9,
      prepTimeMinutes = 3,
      ingredients = listOf("1.5 scoops whey isolate protein", "1 tbsp natural creamy peanut butter", "300ml unsweetened almond milk", "1/2 cup crushed ice"),
      instructions = "Blend all ingredients in a high-speed blender until silky smooth. Consume immediately post-workout or as a sustaining snack.",
      tags = listOf("Post-Workout", "Quick Recovery")
    ),

    // Keto / Low Carb
    RecipePreset(
      name = "Keto Avocado Egg Boats with Cheddar & Bacon",
      mealType = "Breakfast",
      dietCategory = "Keto / Low Carb",
      calories = 460,
      carbs = 6,
      protein = 22,
      fat = 38,
      prepTimeMinutes = 18,
      ingredients = listOf("1 large ripe avocado, halved & pitted", "2 small whole eggs", "2 tbsp sharp cheddar cheese", "1 tbsp crispy bacon crumbles", "Fresh chives"),
      instructions = "Scoop out a tablespoon of avocado flesh from each half. Crack an egg into each hollow, top with cheese and bacon. Bake at 200°C for 14-16 minutes until whites set.",
      tags = listOf("Ketogenic", "Healthy Fats", "Zero Sugar")
    ),
    RecipePreset(
      name = "Cobb Salad with Chicken, Bacon, Egg & Blue Cheese",
      mealType = "Lunch",
      dietCategory = "Keto / Low Carb",
      calories = 560,
      carbs = 8,
      protein = 46,
      fat = 38,
      prepTimeMinutes = 12,
      ingredients = listOf("150g shredded roasted chicken", "2 hardboiled eggs, quartered", "2 slices crispy bacon", "1/2 avocado, diced", "3 cups romaine lettuce", "2 tbsp blue cheese dressing"),
      instructions = "Arrange romaine lettuce base in a wide bowl. Arrange neat rows of chicken, hardboiled eggs, crispy bacon, avocado, and crumbled blue cheese. Dress generously.",
      tags = listOf("Keto Classic", "Satisfying", "Low Carb")
    ),
    RecipePreset(
      name = "Creamy Tuscan Garlic Butter Shrimp with Zucchini Noodles",
      mealType = "Dinner",
      dietCategory = "Keto / Low Carb",
      calories = 510,
      carbs = 9,
      protein = 38,
      fat = 36,
      prepTimeMinutes = 20,
      ingredients = listOf("200g wild shrimp, peeled & deveined", "2 medium zucchini, spiralized", "1/4 cup sun-dried tomatoes", "2 cups baby spinach", "1/3 cup heavy cream", "2 tbsp grass-fed butter & garlic"),
      instructions = "Sauté shrimp in butter and garlic until pink, set aside. In the same pan simmer heavy cream, sun-dried tomatoes, and spinach until thickened. Toss in zoodles and shrimp for 2 mins.",
      tags = listOf("Gourmet", "Under 10g Carbs", "Keto Delight")
    ),
    RecipePreset(
      name = "Roasted Almonds & String Cheese with Cucumber Slices",
      mealType = "Snack",
      dietCategory = "Keto / Low Carb",
      calories = 230,
      carbs = 4,
      protein = 12,
      fat = 18,
      prepTimeMinutes = 2,
      ingredients = listOf("25g dry roasted salted almonds", "1 whole mozzarella string cheese", "1 mini cucumber, sliced with sea salt"),
      instructions = "Enjoy crisp cucumber rounds with savory roasted almonds and protein-rich string cheese.",
      tags = listOf("Grab & Go", "Zero Prep", "Keto Snack")
    ),

    // Mediterranean
    RecipePreset(
      name = "Greek Shakshuka with Feta & Crusty Bread",
      mealType = "Breakfast",
      dietCategory = "Mediterranean",
      calories = 410,
      carbs = 36,
      protein = 20,
      fat = 20,
      prepTimeMinutes = 20,
      ingredients = listOf("2 fresh eggs", "1 cup spiced tomato-bell pepper sauce", "30g authentic Greek feta", "1 slice whole grain artisan bread", "Fresh cilantro & parsley", "1 tbsp olive oil"),
      instructions = "Simmer tomato pepper sauce with cumin and paprika in a small cast-iron skillet. Make two wells, crack eggs inside, and cook on low heat until whites solidify. Crumble feta on top.",
      tags = listOf("Hearty", "Rich Antioxidants", "Comfort Food")
    ),
    RecipePreset(
      name = "Mediterranean Chickpea & Grilled Salmon Salad",
      mealType = "Lunch",
      dietCategory = "Mediterranean",
      calories = 540,
      carbs = 38,
      protein = 42,
      fat = 24,
      prepTimeMinutes = 18,
      ingredients = listOf("150g grilled salmon fillet", "3/4 cup cooked chickpeas", "1 cup arugula and mixed greens", "1/4 cup kalamata olives & cucumbers", "Extra virgin olive oil and lemon dressing"),
      instructions = "Combine crisp greens, seasoned chickpeas, kalamata olives, and cucumber in a bowl. Top with warm grilled salmon and a splash of cold-pressed olive oil.",
      tags = listOf("Longevity Diet", "High Fiber", "Clean Fats")
    ),
    RecipePreset(
      name = "Herb-Crusted Cod with Roasted Ratatouille",
      mealType = "Dinner",
      dietCategory = "Mediterranean",
      calories = 460,
      carbs = 28,
      protein = 40,
      fat = 18,
      prepTimeMinutes = 30,
      ingredients = listOf("200g wild Pacific cod fillet", "1 cup eggplant, zucchini & bell peppers, cubed", "1/2 cup crushed San Marzano tomatoes", "2 tbsp whole wheat breadcrumbs with thyme & rosemary", "1.5 tbsp olive oil"),
      instructions = "Roast mixed Mediterranean vegetables with crushed tomatoes and olive oil. Coat cod with herb breadcrumbs and bake at 190°C for 15 minutes until golden and flaky.",
      tags = listOf("Heart Healthy", "Light & Vibrant")
    ),
    RecipePreset(
      name = "Kalamata Olive Tapenade with Whole Grain Seed Crackers",
      mealType = "Snack",
      dietCategory = "Mediterranean",
      calories = 190,
      carbs = 18,
      protein = 6,
      fat = 11,
      prepTimeMinutes = 5,
      ingredients = listOf("2 tbsp olive tapenade with capers & garlic", "6 whole grain flax & sesame seed crackers", "Handful of grape tomatoes"),
      instructions = "Spread savory olive tapenade over crunchy seed crackers and enjoy with sweet grape tomatoes.",
      tags = listOf("Polyphenols", "Crispy & Savory")
    ),

    // Vegetarian & Vegan
    RecipePreset(
      name = "Overnight Oats with Chia, Almond Butter & Berries",
      mealType = "Breakfast",
      dietCategory = "Vegetarian",
      calories = 390,
      carbs = 54,
      protein = 16,
      fat = 14,
      prepTimeMinutes = 5,
      ingredients = listOf("1/2 cup rolled oats", "1 cup unsweetened oat milk", "1 tbsp chia seeds", "1 tbsp creamy almond butter", "1/2 cup mixed blackberries & strawberries", "1 tsp maple syrup"),
      instructions = "Soak rolled oats and chia seeds in oat milk overnight in a mason jar. In the morning, top with creamy almond butter, fresh berries, and maple syrup.",
      tags = listOf("Plant Powered", "Overnight Prep", "Digestive Health")
    ),
    RecipePreset(
      name = "Tofu Crunch Buddha Bowl with Creamy Tahini Dressing",
      mealType = "Lunch",
      dietCategory = "Vegetarian",
      calories = 520,
      carbs = 56,
      protein = 26,
      fat = 22,
      prepTimeMinutes = 20,
      ingredients = listOf("180g extra-firm tofu, cubed & pan-crisped", "1/2 cup warm quinoa", "1 cup roasted sweet potatoes & kale", "1/4 cup steamed edamame", "2 tbsp lemon garlic tahini dressing"),
      instructions = "Arrange warm quinoa base with crispy golden tofu, roasted sweet potatoes, and kale. Drizzle creamy lemon garlic tahini sauce and sesame seeds.",
      tags = listOf("100% Plant Based", "Colorful", "Complete Protein")
    ),
    RecipePreset(
      name = "Lentil & Sweet Potato Coconut Curry with Jasmine Rice",
      mealType = "Dinner",
      dietCategory = "Vegetarian",
      calories = 560,
      carbs = 76,
      protein = 22,
      fat = 18,
      prepTimeMinutes = 25,
      ingredients = listOf("1 cup cooked red lentils", "1 cup cubed sweet potatoes", "1/2 cup light coconut milk", "1 cup baby spinach", "3/4 cup cooked fragrant jasmine rice", "Curry spices & ginger garlic paste"),
      instructions = "Simmer red lentils, sweet potatoes, and warming curry spices in light coconut milk until rich and fragrant. Fold in spinach until wilted. Serve hot over jasmine rice.",
      tags = listOf("Rich Flavor", "Warming", "High Fiber")
    ),
    RecipePreset(
      name = "Roasted Garlic Hummus with Crunchy Bell Pepper & Carrot Sticks",
      mealType = "Snack",
      dietCategory = "Vegetarian",
      calories = 180,
      carbs = 22,
      protein = 8,
      fat = 7,
      prepTimeMinutes = 4,
      ingredients = listOf("1/3 cup roasted garlic chickpea hummus", "1 red bell pepper, cut into batons", "1 medium carrot, sliced", "Sprinkle of smoked paprika"),
      instructions = "Dip vibrant fresh crunchy bell pepper and carrot batons into roasted garlic hummus sprinkled with smoked paprika.",
      tags = listOf("Crunchy", "Clean Eating", "Low Calorie")
    )
  )
}
