package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SunsetOrange

data class ActivityPreset(
  val name: String,
  val metValue: Float, // Metabolic Equivalent of Task
  val icon: androidx.compose.ui.graphics.vector.ImageVector,
  val hasDistance: Boolean = false
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LogActivityDialog(
  userWeightKg: Float = 68f,
  onDismiss: () -> Unit,
  onLogActivity: (
    type: String,
    durationMinutes: Int,
    caloriesBurned: Int,
    intensity: String,
    distanceKm: Float?,
    notes: String
  ) -> Unit
) {
  val activities = listOf(
    ActivityPreset("Running", 9.8f, Icons.Default.DirectionsRun, true),
    ActivityPreset("Brisk Walking", 3.8f, Icons.Default.DirectionsWalk, true),
    ActivityPreset("Cycling", 7.5f, Icons.Default.DirectionsBike, true),
    ActivityPreset("Strength Training", 5.5f, Icons.Default.FitnessCenter, false),
    ActivityPreset("HIIT", 8.5f, Icons.Default.LocalFireDepartment, false),
    ActivityPreset("Yoga", 3.0f, Icons.Default.SelfImprovement, false),
    ActivityPreset("Swimming", 7.0f, Icons.Default.Pool, false),
    ActivityPreset("Pilates", 4.0f, Icons.Default.SportsGymnastics, false)
  )

  var selectedActivity by remember { mutableStateOf(activities[0]) }
  var durationMinutes by remember { mutableFloatStateOf(30f) }
  var intensity by remember { mutableStateOf("Moderate") } // Low, Moderate, High
  var customCaloriesText by remember { mutableStateOf("") }
  var distanceText by remember { mutableStateOf("") }
  var notesText by remember { mutableStateOf("") }

  // Auto estimate calories = MET * weight_kg * (duration_mins / 60) * intensityMultiplier
  val intensityMultiplier = when (intensity) {
    "Low" -> 0.85f
    "High" -> 1.2f
    else -> 1.0f
  }
  val estimatedCalories = (selectedActivity.metValue * userWeightKg * (durationMinutes / 60f) * intensityMultiplier).toInt()

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Log Workout & Activity",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .testTag("dialog_log_activity"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "Select Activity Type",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Activity Presets Chips
        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          activities.forEach { act ->
            FilterChip(
              selected = selectedActivity.name == act.name,
              onClick = { selectedActivity = act },
              label = { Text(act.name, fontSize = 12.sp) },
              leadingIcon = {
                Icon(
                  act.icon,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = EmeraldPrimary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Duration Slider
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Duration",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "${durationMinutes.toInt()} mins",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = EmeraldPrimary
            )
          }

          Slider(
            value = durationMinutes,
            onValueChange = { durationMinutes = it },
            valueRange = 5f..120f,
            steps = 22,
            modifier = Modifier.fillMaxWidth()
          )
        }

        // Intensity Selector
        Column {
          Text(
            text = "Intensity Level",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf("Low", "Moderate", "High").forEach { level ->
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (intensity == level) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                  .weight(1f)
                  .clickable { intensity = level }
              ) {
                Text(
                  text = level,
                  modifier = Modifier.padding(vertical = 8.dp),
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = if (intensity == level) FontWeight.Bold else FontWeight.Normal,
                  color = if (intensity == level) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }

        // Calorie Calculation Badge
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = SunsetOrange.copy(alpha = 0.12f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = SunsetOrange)
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "Estimated Energy Burn",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = "${customCaloriesText.toIntOrNull() ?: estimatedCalories} kcal",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = SunsetOrange
                )
              }
            }

            OutlinedTextField(
              value = customCaloriesText,
              onValueChange = { customCaloriesText = it.filter { c -> c.isDigit() } },
              placeholder = { Text("Override", fontSize = 11.sp) },
              modifier = Modifier.width(100.dp),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true
            )
          }
        }

        // Distance (if applicable)
        if (selectedActivity.hasDistance) {
          OutlinedTextField(
            value = distanceText,
            onValueChange = { distanceText = it },
            label = { Text("Distance (km)") },
            placeholder = { Text("e.g. 5.2") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
        }

        // Notes
        OutlinedTextField(
          value = notesText,
          onValueChange = { notesText = it },
          label = { Text("Workout Notes (optional)") },
          placeholder = { Text("e.g. Felt energetic, increased pace") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          maxLines = 2
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val cal = customCaloriesText.toIntOrNull() ?: estimatedCalories
          val dist = distanceText.toFloatOrNull()
          onLogActivity(
            selectedActivity.name,
            durationMinutes.toInt(),
            cal,
            intensity,
            dist,
            notesText.trim()
          )
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("btn_confirm_log_activity")
      ) {
        Text("Save Activity")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
