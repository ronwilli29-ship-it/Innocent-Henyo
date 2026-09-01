package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MealPlanItem
import com.example.data.model.RecipePreset
import com.example.data.repository.MealPlanGenerator
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SunsetOrange

@Composable
fun SwapMealDialog(
  meal: MealPlanItem,
  dietPreference: String,
  onDismiss: () -> Unit,
  onSelectAlternative: (RecipePreset) -> Unit
) {
  val alternatives = MealPlanGenerator.getAlternativeMeals(
    mealType = meal.mealType,
    currentRecipeName = meal.name,
    dietPreference = dietPreference
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Swap ${meal.mealType}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Choose an alternative recipe",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("dialog_swap_meal")
      ) {
        if (alternatives.isEmpty()) {
          Text(
            "No alternative recipes available in this category.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        } else {
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .height(350.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(alternatives) { preset ->
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(14.dp))
                  .clickable {
                    onSelectAlternative(preset)
                    onDismiss()
                  },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = preset.name,
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.weight(1f)
                    )
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = EmeraldPrimary.copy(alpha = 0.15f)
                    ) {
                      Text(
                        text = preset.dietCategory,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.SemiBold
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(6.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                        Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = SunsetOrange,
                        modifier = Modifier.size(14.dp)
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        text = "${preset.calories} kcal",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }

                    Text(
                      text = "P: ${preset.protein}g • C: ${preset.carbs}g • F: ${preset.fat}g",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }
            }
          }
        }
      }
    },
    confirmButton = {},
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
