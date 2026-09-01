package com.example.ui.meals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.gemini.AiMealPlanResult
import com.example.data.model.GroceryItem
import com.example.data.model.MealPlanItem
import com.example.data.model.UserProfile
import com.example.ui.AiMealPlanUiState
import com.example.ui.theme.VibrantMintAccent
import com.example.ui.theme.VibrantMintContainer
import com.example.ui.theme.VibrantMintDark
import com.example.ui.theme.VibrantMintOnContainer
import com.example.ui.theme.VibrantPurpleDark
import com.example.ui.theme.VibrantPurplePrimary
import com.example.ui.theme.VibrantRoseAccent
import com.example.ui.theme.VibrantRoseContainer
import com.example.ui.theme.VibrantRoseDark
import com.example.ui.theme.VibrantRoseOnContainer
import com.example.ui.theme.VibrantSkyBlueAccent
import com.example.ui.theme.VibrantSkyBlueContainer
import com.example.ui.theme.VibrantSkyBlueDark
import com.example.ui.theme.VibrantSkyBlueOnContainer
import com.example.ui.theme.VibrantSunsetContainer
import com.example.ui.theme.VibrantSunsetOrange
import com.example.ui.theme.VibrantSunsetOnContainer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MealPlannerScreen(
  userProfile: UserProfile,
  selectedDate: String,
  meals: List<MealPlanItem>,
  groceries: List<GroceryItem>,
  aiMealPlanState: AiMealPlanUiState = AiMealPlanUiState.Idle,
  onDateSelected: (String) -> Unit,
  onToggleMealLogged: (MealPlanItem) -> Unit,
  onMealClick: (MealPlanItem) -> Unit,
  onSwapMealClick: (MealPlanItem) -> Unit,
  onDeleteMealClick: (MealPlanItem) -> Unit,
  onRegeneratePlan: () -> Unit,
  onGenerateAiPlan: () -> Unit = {},
  onApplyAiPlan: (AiMealPlanResult) -> Unit = {},
  onDismissAiPlan: () -> Unit = {},
  onOpenAddMealDialog: () -> Unit,
  onToggleGrocery: (GroceryItem) -> Unit,
  onAddGrocery: (String, String) -> Unit,
  onDeleteGrocery: (GroceryItem) -> Unit,
  onClearCheckedGroceries: () -> Unit,
  onGenerateWeeklyGroceries: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Meal Plan, 1: Grocery List
  var showAddGroceryDialog by remember { mutableStateOf(false) }

  // AI Meal Plan Dialog / Sheet
  if (aiMealPlanState !is AiMealPlanUiState.Idle) {
    AiMealPlanDialog(
      state = aiMealPlanState,
      userProfile = userProfile,
      onDismiss = onDismissAiPlan,
      onApply = onApplyAiPlan,
      onRetry = onGenerateAiPlan
    )
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .testTag("screen_meal_planner")
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Top Screen Header
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 14.dp)
      ) {
        Text(
          text = "Personalized Meals",
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.ExtraBold
        )
        Text(
          text = "${userProfile.dietPreference} Plan • ${userProfile.dailyCalorieTarget} kcal Target",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      // Tab switcher: Meals vs Grocery List
      TabRow(
        selectedTabIndex = selectedTab,
        modifier = Modifier.fillMaxWidth(),
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = VibrantPurplePrimary
          )
        }
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Meal Plan", fontWeight = FontWeight.Bold, color = if (selectedTab == 0) VibrantPurplePrimary else MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Grocery List (${groceries.count { !it.isChecked }})", fontWeight = FontWeight.Bold, color = if (selectedTab == 1) VibrantPurplePrimary else MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
        )
      }

      if (selectedTab == 0) {
        // MEAL PLAN TAB
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(bottom = 96.dp)
        ) {
          // Date Selector
          item {
            Spacer(modifier = Modifier.height(12.dp))
            MealPlannerDateStrip(
              selectedDate = selectedDate,
              onDateSelected = onDateSelected
            )
            Spacer(modifier = Modifier.height(14.dp))
          }

          // Gemini AI Smart Meal Plan Hero Banner
          item {
            GeminiAiMealBanner(
              userProfile = userProfile,
              onGenerateAiPlan = onGenerateAiPlan
            )
            Spacer(modifier = Modifier.height(14.dp))
          }

          // Plan Control Banner
          item {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
              shape = RoundedCornerShape(22.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                val totalCal = meals.sumOf { it.calories }
                val totalProtein = meals.sumOf { it.protein }
                val totalCarbs = meals.sumOf { it.carbs }
                val totalFat = meals.sumOf { it.fat }

                Column {
                  Text(
                    text = "Day Nutrition Total",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = VibrantPurplePrimary
                  )
                  Text(
                    text = "$totalCal / ${userProfile.dailyCalorieTarget} kcal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                  )
                  Text(
                    text = "P: ${totalProtein}g • C: ${totalCarbs}g • F: ${totalFat}g",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                OutlinedButton(
                  onClick = onRegeneratePlan,
                  shape = RoundedCornerShape(14.dp),
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                  modifier = Modifier.testTag("btn_regenerate_plan")
                ) {
                  Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = VibrantPurplePrimary)
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Refresh", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VibrantPurplePrimary)
                }
              }
            }
            Spacer(modifier = Modifier.height(14.dp))
          }

          if (meals.isEmpty()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(32.dp),
                contentAlignment = Alignment.Center
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    "No meals created for this date.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Spacer(modifier = Modifier.height(12.dp))
                  Button(
                    onClick = onRegeneratePlan,
                    colors = ButtonDefaults.buttonColors(containerColor = VibrantPurplePrimary),
                    shape = RoundedCornerShape(16.dp)
                  ) {
                    Text("Generate Smart Plan", fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          } else {
            items(meals) { meal ->
              Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                DetailedMealCard(
                  meal = meal,
                  onToggleLogged = { onToggleMealLogged(meal) },
                  onCardClick = { onMealClick(meal) },
                  onSwapClick = { onSwapMealClick(meal) },
                  onDeleteClick = { onDeleteMealClick(meal) }
                )
              }
            }
          }
        }
      } else {
        // GROCERY LIST TAB
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(bottom = 96.dp)
        ) {
          // Action Bar: Auto generate + Clear
          item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Button(
                onClick = onGenerateWeeklyGroceries,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VibrantPurplePrimary)
              ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Sync From Plan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }

              OutlinedButton(
                onClick = onClearCheckedGroceries,
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
              ) {
                Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Clear Checked", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
            Spacer(modifier = Modifier.height(14.dp))
          }

          if (groceries.isEmpty()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(40.dp),
                contentAlignment = Alignment.Center
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Spacer(modifier = Modifier.height(12.dp))
                  Text(
                    "Your grocery list is empty",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    "Tap 'Sync From Plan' to generate shopping ingredients from this week's meals.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                  )
                }
              }
            }
          } else {
            // Group groceries by category
            val grouped = groceries.groupBy { it.category }
            grouped.forEach { (category, items) ->
              item {
                Text(
                  text = category.uppercase(),
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = VibrantPurplePrimary,
                  letterSpacing = 0.8.sp,
                  modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
              }

              items(items) { groceryItem ->
                GroceryRowItem(
                  item = groceryItem,
                  onToggle = { onToggleGrocery(groceryItem) },
                  onDelete = { onDeleteGrocery(groceryItem) }
                )
              }

              item {
                Spacer(modifier = Modifier.height(8.dp))
              }
            }
          }
        }
      }
    }

    // Floating Action Button
    FloatingActionButton(
      onClick = {
        if (selectedTab == 0) onOpenAddMealDialog() else showAddGroceryDialog = true
      },
      containerColor = VibrantPurplePrimary,
      contentColor = Color.White,
      shape = CircleShape,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 20.dp, bottom = 100.dp)
        .testTag("fab_add_meal_or_grocery")
    ) {
      Icon(Icons.Default.Add, contentDescription = "Add")
    }

    // Add Grocery Dialog
    if (showAddGroceryDialog) {
      AddGroceryItemDialog(
        onDismiss = { showAddGroceryDialog = false },
        onAdd = { name, category ->
          onAddGrocery(name, category)
          showAddGroceryDialog = false
        }
      )
    }
  }
}

@Composable
private fun DetailedMealCard(
  meal: MealPlanItem,
  onToggleLogged: () -> Unit,
  onCardClick: () -> Unit,
  onSwapClick: () -> Unit,
  onDeleteClick: () -> Unit
) {
  val (cardBg, onCardText, accentColor) = when (meal.mealType.lowercase()) {
    "breakfast" -> Triple(VibrantSkyBlueContainer, VibrantSkyBlueOnContainer, VibrantSkyBlueDark)
    "lunch" -> Triple(VibrantMintContainer, VibrantMintOnContainer, VibrantMintDark)
    "dinner" -> Triple(VibrantRoseContainer, VibrantRoseOnContainer, VibrantRoseDark)
    else -> Triple(VibrantSunsetContainer, VibrantSunsetOnContainer, VibrantSunsetOrange)
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(22.dp))
      .clickable { onCardClick() }
      .testTag("meal_item_card_${meal.id}"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = cardBg),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color.White
        ) {
          Text(
            text = meal.mealType.uppercase(),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = accentColor,
            letterSpacing = 0.8.sp
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onSwapClick, modifier = Modifier.size(32.dp)) {
            Icon(
              Icons.Default.SwapHoriz,
              contentDescription = "Swap",
              tint = accentColor,
              modifier = Modifier.size(20.dp)
            )
          }
          IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
            Icon(
              Icons.Default.Delete,
              contentDescription = "Delete",
              tint = accentColor.copy(alpha = 0.7f),
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = meal.name,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold,
        color = onCardText
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Nutrition and prep row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color.White.copy(alpha = 0.7f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.LocalFireDepartment,
              contentDescription = null,
              tint = accentColor,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "${meal.calories} kcal",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = onCardText
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color.White.copy(alpha = 0.7f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.Schedule,
              contentDescription = null,
              tint = onCardText.copy(alpha = 0.8f),
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "${meal.prepTimeMinutes}m",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Medium,
              color = onCardText
            )
          }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
          text = "P: ${meal.protein}g • C: ${meal.carbs}g • F: ${meal.fat}g",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Medium,
          color = onCardText.copy(alpha = 0.8f)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Bottom action bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Tap to view full recipe & cooking steps",
          style = MaterialTheme.typography.labelSmall,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          color = accentColor
        )

        Button(
          onClick = onToggleLogged,
          colors = ButtonDefaults.buttonColors(
            containerColor = if (meal.isLogged) Color.White else accentColor,
            contentColor = if (meal.isLogged) accentColor else Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
          modifier = Modifier.height(34.dp)
        ) {
          Icon(
            if (meal.isLogged) Icons.Default.Check else Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (meal.isLogged) "Eaten" else "Log",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
private fun GroceryRowItem(
  item: GroceryItem,
  onToggle: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp)
      .clip(RoundedCornerShape(16.dp))
      .clickable { onToggle() },
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (item.isChecked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onToggle, modifier = Modifier.size(28.dp)) {
        Icon(
          if (item.isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
          contentDescription = "Toggle",
          tint = if (item.isChecked) VibrantMintDark else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = item.name,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.SemiBold,
          textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
          color = if (item.isChecked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
        )
        if (item.amount.isNotBlank()) {
          Text(
            text = item.amount,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
        Icon(
          Icons.Default.Delete,
          contentDescription = "Delete",
          tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}

@Composable
private fun MealPlannerDateStrip(
  selectedDate: String,
  onDateSelected: (String) -> Unit
) {
  val today = remember { LocalDate.now() }
  val days = remember {
    (-3..3).map { today.plusDays(it.toLong()) }
  }

  LazyRow(
    modifier = Modifier.fillMaxWidth(),
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    items(days) { date ->
      val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
      val isSelected = dateStr == selectedDate

      Surface(
        modifier = Modifier
          .clip(RoundedCornerShape(18.dp))
          .clickable { onDateSelected(dateStr) },
        shape = RoundedCornerShape(18.dp),
        color = if (isSelected) VibrantPurplePrimary else MaterialTheme.colorScheme.surface,
        shadowElevation = if (isSelected) 3.dp else 0.dp
      ) {
        Column(
          modifier = Modifier
            .width(52.dp)
            .padding(vertical = 10.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }
  }
}

@Composable
private fun AddGroceryItemDialog(
  onDismiss: () -> Unit,
  onAdd: (name: String, category: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Fresh Produce") }
  val categories = listOf("Fresh Produce", "Proteins & Dairy", "Pantry & Grains", "Seasoning & Oils")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Add Grocery Item", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Item Name") },
          placeholder = { Text("e.g. Greek Yogurt, Apples") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp)
        )

        Text("Category", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categories.take(2).forEach { cat ->
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (category == cat) VibrantPurplePrimary else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .weight(1f)
                .clickable { category = cat }
            ) {
              Text(
                text = cat,
                modifier = Modifier.padding(vertical = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (category == cat) Color.White else MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categories.drop(2).forEach { cat ->
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (category == cat) VibrantPurplePrimary else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .weight(1f)
                .clickable { category = cat }
            ) {
              Text(
                text = cat,
                modifier = Modifier.padding(vertical = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (category == cat) Color.White else MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) onAdd(name.trim(), category)
        },
        colors = ButtonDefaults.buttonColors(containerColor = VibrantPurplePrimary),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text("Add Item", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}

@Composable
fun GeminiAiMealBanner(
  userProfile: UserProfile,
  onGenerateAiPlan: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          brush = Brush.horizontalGradient(
            colors = listOf(
              VibrantPurplePrimary.copy(alpha = 0.95f),
              VibrantSkyBlueDark.copy(alpha = 0.92f)
            )
          )
        )
        .padding(18.dp)
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = CircleShape,
              color = Color.White.copy(alpha = 0.25f),
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
            Column {
              Text(
                text = "Gemini AI Nutritionist",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
              )
              Text(
                text = "Adaptive Meal Planning",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.85f)
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.2f)
          ) {
            Text(
              text = userProfile.goal,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Generate a custom 1-day meal plan calculated for your ${userProfile.dietPreference} diet, ${userProfile.dailyCalorieTarget} kcal target, and logged workout recovery.",
          style = MaterialTheme.typography.bodySmall,
          color = Color.White.copy(alpha = 0.92f),
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
          onClick = onGenerateAiPlan,
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = VibrantPurplePrimary
          ),
          shape = RoundedCornerShape(16.dp),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("btn_gemini_generate_plan")
        ) {
          Icon(
            Icons.Default.AutoAwesome,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = VibrantPurplePrimary
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Generate AI Personalized Plan",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            color = VibrantPurplePrimary
          )
        }
      }
    }
  }
}

@Composable
fun AiMealPlanDialog(
  state: AiMealPlanUiState,
  userProfile: UserProfile,
  onDismiss: () -> Unit,
  onApply: (AiMealPlanResult) -> Unit,
  onRetry: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("ai_meal_plan_dialog"),
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = VibrantPurplePrimary,
          modifier = Modifier.size(24.dp)
        )
        Text(
          text = when (state) {
            is AiMealPlanUiState.Generating -> "Crafting Your Plan..."
            is AiMealPlanUiState.Success -> "Personalized AI Meal Plan"
            is AiMealPlanUiState.Error -> "AI Generation"
            else -> "AI Meal Planner"
          },
          fontWeight = FontWeight.ExtraBold
        )
      }
    },
    text = {
      Box(modifier = Modifier.fillMaxWidth()) {
        when (state) {
          is AiMealPlanUiState.Generating -> {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
              CircularProgressIndicator(
                color = VibrantPurplePrimary,
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp
              )
              Text(
                text = "Gemini is analyzing your nutritional profile...",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
              Text(
                text = "Integrating ${userProfile.goal} target, ${userProfile.dietPreference} requirements, and recent workout activity demands.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }

          is AiMealPlanUiState.Error -> {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Text(
                text = "Unable to complete AI request: ${state.message}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
              )
              Text(
                text = "You can retry or use our smart adaptive nutrition generator.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          is AiMealPlanUiState.Success -> {
            val plan = state.planResult
            LazyColumn(
              modifier = Modifier
                .fillMaxWidth()
                .height(420.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              // Nutritionist Rationale
              item {
                Surface(
                  shape = RoundedCornerShape(16.dp),
                  color = VibrantMintContainer
                ) {
                  Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = VibrantMintDark,
                        modifier = Modifier.size(16.dp)
                      )
                      Text(
                        text = "Nutritionist Rationale",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = VibrantMintDark
                      )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = plan.rationale,
                      style = MaterialTheme.typography.bodySmall,
                      color = VibrantMintOnContainer,
                      lineHeight = 18.sp
                    )
                  }
                }
              }

              // Activity Adaptation Note
              if (plan.activityAdaptationNote.isNotBlank()) {
                item {
                  Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = VibrantSkyBlueContainer
                  ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                      Text(
                        text = "⚡ Activity & Recovery Adaptation",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = VibrantSkyBlueDark
                      )
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = plan.activityAdaptationNote,
                        style = MaterialTheme.typography.bodySmall,
                        color = VibrantSkyBlueOnContainer,
                        lineHeight = 18.sp
                      )
                    }
                  }
                }
              }

              // Macro Summary Strip
              item {
                Surface(
                  shape = RoundedCornerShape(16.dp),
                  color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                  ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text("Calories", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                      Text("${plan.totalCalories} kcal", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text("Protein", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                      Text("${plan.totalProtein}g", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text("Carbs", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                      Text("${plan.totalCarbs}g", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text("Fat", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                      Text("${plan.totalFat}g", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                  }
                }
              }

              // Meals List
              items(plan.meals) { meal ->
                Card(
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                  elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (meal.mealType) {
                          "Breakfast" -> VibrantSunsetContainer
                          "Lunch" -> VibrantMintContainer
                          "Dinner" -> VibrantPurplePrimary.copy(alpha = 0.15f)
                          else -> VibrantRoseContainer
                        }
                      ) {
                        Text(
                          text = meal.mealType,
                          style = MaterialTheme.typography.labelSmall,
                          fontWeight = FontWeight.Bold,
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                      }
                      Text(
                        text = "${meal.calories} kcal • ${meal.prepTimeMinutes}m",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                      text = meal.name,
                      style = MaterialTheme.typography.titleSmall,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = "P: ${meal.protein}g • C: ${meal.carbs}g • F: ${meal.fat}g",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (meal.instructions.contains("Workout Synergy:")) {
                      Spacer(modifier = Modifier.height(4.dp))
                      val synergy = meal.instructions.substringAfter("Workout Synergy:").trim()
                      Text(
                        text = "💡 $synergy",
                        style = MaterialTheme.typography.labelSmall,
                        color = VibrantPurplePrimary,
                        fontSize = 11.sp
                      )
                    }
                  }
                }
              }
            }
          }

          else -> {}
        }
      }
    },
    confirmButton = {
      when (state) {
        is AiMealPlanUiState.Success -> {
          Button(
            onClick = { onApply(state.planResult) },
            colors = ButtonDefaults.buttonColors(containerColor = VibrantPurplePrimary),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.testTag("btn_apply_ai_plan")
          ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Apply to My Plan", fontWeight = FontWeight.Bold)
          }
        }

        is AiMealPlanUiState.Error -> {
          Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = VibrantPurplePrimary),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.testTag("btn_retry_ai_plan")
          ) {
            Text("Retry Generation", fontWeight = FontWeight.Bold)
          }
        }

        else -> {}
      }
    },
    dismissButton = {
      if (state !is AiMealPlanUiState.Generating) {
        TextButton(onClick = onDismiss) {
          Text("Cancel", fontWeight = FontWeight.SemiBold)
        }
      }
    }
  )
}

