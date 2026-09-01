package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MealPlanItem
import com.example.ui.theme.ColorCarbs
import com.example.ui.theme.ColorFat
import com.example.ui.theme.ColorProtein
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SunsetOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailSheet(
  meal: MealPlanItem,
  onDismiss: () -> Unit,
  onToggleLogged: (MealPlanItem) -> Unit,
  onSwapClick: (MealPlanItem) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 12.dp)
        .padding(bottom = 32.dp)
        .testTag("sheet_recipe_detail")
    ) {
      // Header Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = EmeraldPrimary.copy(alpha = 0.15f)
        ) {
          Text(
            text = "${meal.mealType} • ${meal.dietCategory}",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = EmeraldPrimary
          )
        }

        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = meal.name,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Nutrition & Prep Badges
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        NutritionChip(
          label = "Calories",
          value = "${meal.calories} kcal",
          icon = Icons.Default.LocalFireDepartment,
          tint = SunsetOrange,
          modifier = Modifier.weight(1f)
        )
        NutritionChip(
          label = "Prep Time",
          value = "${meal.prepTimeMinutes} mins",
          icon = Icons.Default.Schedule,
          tint = EmeraldPrimary,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Macro breakdown
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        MacroSmallBadge(label = "Protein", value = "${meal.protein}g", color = ColorProtein, modifier = Modifier.weight(1f))
        MacroSmallBadge(label = "Carbs", value = "${meal.carbs}g", color = ColorCarbs, modifier = Modifier.weight(1f))
        MacroSmallBadge(label = "Fats", value = "${meal.fat}g", color = ColorFat, modifier = Modifier.weight(1f))
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Ingredients Section
      Text(
        text = "Ingredients",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(8.dp))

      val ingredientsList = meal.ingredients.split("\n").filter { it.isNotBlank() }
      ingredientsList.forEach { ingredient ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(EmeraldPrimary)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = ingredient.trim(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Instructions Section
      Text(
        text = "Preparation & Cooking",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(8.dp))

      Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = meal.instructions.ifBlank { "Combine ingredients, season to taste, and serve according to dietary preferences." },
          style = MaterialTheme.typography.bodyMedium,
          lineHeight = 22.sp,
          modifier = Modifier.padding(14.dp)
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = { onSwapClick(meal) },
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("btn_swap_meal"),
          shape = RoundedCornerShape(14.dp)
        ) {
          Icon(Icons.Default.SwapHoriz, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Swap Meal")
        }

        Button(
          onClick = {
            onToggleLogged(meal)
            onDismiss()
          },
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("btn_toggle_logged_sheet"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (meal.isLogged) MaterialTheme.colorScheme.secondaryContainer else EmeraldPrimary
          )
        ) {
          Icon(
            if (meal.isLogged) Icons.Default.Close else Icons.Default.Check,
            contentDescription = null,
            tint = if (meal.isLogged) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimary
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            if (meal.isLogged) "Mark Unlogged" else "Mark Eaten",
            color = if (meal.isLogged) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimary
          )
        }
      }
    }
  }
}

@Composable
private fun NutritionChip(
  label: String,
  value: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  tint: androidx.compose.ui.graphics.Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(tint.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = label,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = value,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
private fun MacroSmallBadge(
  label: String,
  value: String,
  color: androidx.compose.ui.graphics.Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    color = color.copy(alpha = 0.12f)
  ) {
    Column(
      modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        fontSize = 10.sp,
        color = color,
        fontWeight = FontWeight.SemiBold
      )
      Text(
        text = value,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        color = color
      )
    }
  }
}
