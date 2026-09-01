package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ui.theme.EmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealDialog(
  initialDate: String,
  onDismiss: () -> Unit,
  onAddMeal: (
    mealType: String,
    name: String,
    calories: Int,
    carbs: Int,
    protein: Int,
    fat: Int,
    prepTime: Int,
    ingredients: String,
    instructions: String,
    dietCategory: String
  ) -> Unit
) {
  var mealType by remember { mutableStateOf("Lunch") }
  var mealTypeExpanded by remember { mutableStateOf(false) }
  val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")

  var name by remember { mutableStateOf("") }
  var caloriesText by remember { mutableStateOf("") }
  var carbsText by remember { mutableStateOf("") }
  var proteinText by remember { mutableStateOf("") }
  var fatText by remember { mutableStateOf("") }
  var prepTimeText by remember { mutableStateOf("15") }
  var ingredientsText by remember { mutableStateOf("") }
  var instructionsText by remember { mutableStateOf("") }
  var dietCategory by remember { mutableStateOf("Balanced") }

  var isError by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Log Custom Meal",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .testTag("dialog_add_meal"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Meal Type Selector
        ExposedDropdownMenuBox(
          expanded = mealTypeExpanded,
          onExpandedChange = { mealTypeExpanded = it }
        ) {
          OutlinedTextField(
            value = mealType,
            onValueChange = {},
            readOnly = true,
            label = { Text("Meal Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mealTypeExpanded) },
            modifier = Modifier
              .fillMaxWidth()
              .menuAnchor(),
            shape = RoundedCornerShape(12.dp)
          )
          ExposedDropdownMenu(
            expanded = mealTypeExpanded,
            onDismissRequest = { mealTypeExpanded = false }
          ) {
            mealTypes.forEach { type ->
              DropdownMenuItem(
                text = { Text(type) },
                onClick = {
                  mealType = type
                  mealTypeExpanded = false
                }
              )
            }
          }
        }

        // Meal Name
        OutlinedTextField(
          value = name,
          onValueChange = {
            name = it
            isError = false
          },
          label = { Text("Meal / Food Name *") },
          placeholder = { Text("e.g. Grilled Chicken Wrap") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_meal_name"),
          shape = RoundedCornerShape(12.dp),
          isError = isError && name.isBlank()
        )

        // Calories & Prep Time
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = caloriesText,
            onValueChange = { caloriesText = it.filter { char -> char.isDigit() } },
            label = { Text("Calories (kcal) *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
              .weight(1f)
              .testTag("input_meal_calories"),
            shape = RoundedCornerShape(12.dp),
            isError = isError && caloriesText.isBlank()
          )

          OutlinedTextField(
            value = prepTimeText,
            onValueChange = { prepTimeText = it.filter { char -> char.isDigit() } },
            label = { Text("Prep (mins)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
          )
        }

        // Macros (Protein, Carbs, Fat)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          OutlinedTextField(
            value = proteinText,
            onValueChange = { proteinText = it.filter { char -> char.isDigit() } },
            label = { Text("Protein (g)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
          )
          OutlinedTextField(
            value = carbsText,
            onValueChange = { carbsText = it.filter { char -> char.isDigit() } },
            label = { Text("Carbs (g)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
          )
          OutlinedTextField(
            value = fatText,
            onValueChange = { fatText = it.filter { char -> char.isDigit() } },
            label = { Text("Fat (g)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
          )
        }

        // Ingredients / Notes
        OutlinedTextField(
          value = ingredientsText,
          onValueChange = { ingredientsText = it },
          label = { Text("Ingredients / Items (one per line)") },
          placeholder = { Text("Tortilla\nChicken Breast\nAvocado\nLettuce") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          minLines = 2,
          maxLines = 4
        )

        // Cooking instructions
        OutlinedTextField(
          value = instructionsText,
          onValueChange = { instructionsText = it },
          label = { Text("Cooking notes / instructions (optional)") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          minLines = 2,
          maxLines = 3
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isBlank() || caloriesText.isBlank()) {
            isError = true
            return@Button
          }
          val cal = caloriesText.toIntOrNull() ?: 300
          val carbs = carbsText.toIntOrNull() ?: 0
          val protein = proteinText.toIntOrNull() ?: 0
          val fat = fatText.toIntOrNull() ?: 0
          val prep = prepTimeText.toIntOrNull() ?: 10

          onAddMeal(
            mealType,
            name.trim(),
            cal,
            carbs,
            protein,
            fat,
            prep,
            ingredientsText.trim(),
            instructionsText.trim(),
            dietCategory
          )
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("btn_confirm_add_meal")
      ) {
        Text("Save & Log")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
