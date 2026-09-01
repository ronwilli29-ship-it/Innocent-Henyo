package com.example.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.VibrantLavenderContainer
import com.example.ui.theme.VibrantLavenderOnContainer
import com.example.ui.theme.VibrantMintDark
import com.example.ui.theme.VibrantPurpleDark
import com.example.ui.theme.VibrantPurplePrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
  userProfile: UserProfile,
  onSaveProfile: (UserProfile) -> Unit
) {
  var name by remember(userProfile) { mutableStateOf(userProfile.name) }
  var ageText by remember(userProfile) { mutableStateOf(userProfile.age.toString()) }
  var gender by remember(userProfile) { mutableStateOf(userProfile.gender) }
  var heightText by remember(userProfile) { mutableStateOf(userProfile.heightCm.toInt().toString()) }
  var weightText by remember(userProfile) { mutableStateOf(userProfile.currentWeightKg.toString()) }
  var targetWeightText by remember(userProfile) { mutableStateOf(userProfile.targetWeightKg.toString()) }

  var goal by remember(userProfile) { mutableStateOf(userProfile.goal) }
  var dietPreference by remember(userProfile) { mutableStateOf(userProfile.dietPreference) }
  var activityLevel by remember(userProfile) { mutableStateOf(userProfile.activityLevel) }

  var calorieTargetText by remember(userProfile) { mutableStateOf(userProfile.dailyCalorieTarget.toString()) }
  var waterTargetText by remember(userProfile) { mutableStateOf(userProfile.waterTargetMl.toString()) }
  var stepsTargetText by remember(userProfile) { mutableStateOf(userProfile.dailyStepTarget.toString()) }

  var saveFeedback by remember { mutableStateOf(false) }

  // Recalculate estimated BMR / TDEE
  val age = ageText.toIntOrNull() ?: 28
  val heightCm = heightText.toFloatOrNull() ?: 172f
  val weightKg = weightText.toFloatOrNull() ?: 68f

  // Mifflin-St Jeor Formula
  val bmr = if (gender.lowercase() == "female") {
    (10 * weightKg + 6.25f * heightCm - 5 * age - 161).toInt()
  } else {
    (10 * weightKg + 6.25f * heightCm - 5 * age + 5).toInt()
  }

  val activityMultiplier = when (activityLevel) {
    "Sedentary" -> 1.2f
    "Lightly Active" -> 1.375f
    "Moderately Active" -> 1.55f
    "Very Active" -> 1.725f
    else -> 1.375f
  }

  val tdee = (bmr * activityMultiplier).toInt()
  val recommendedCalories = when (goal) {
    "Weight Loss" -> (tdee - 450).coerceAtLeast(1400)
    "Muscle Gain" -> tdee + 350
    else -> tdee
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("screen_profile"),
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
  ) {
    item {
      Text(
        text = "Personal Health Profile",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.ExtraBold
      )
      Text(
        text = "Customize your body stats, diet preference, and nutrition targets",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(18.dp))
    }

    // TDEE & Smart Calculator Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = VibrantLavenderContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = VibrantPurplePrimary)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Smart Metabolism Estimate", fontWeight = FontWeight.ExtraBold, color = VibrantLavenderOnContainer)
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = VibrantPurplePrimary,
              modifier = Modifier.clickable {
                calorieTargetText = recommendedCalories.toString()
              }
            ) {
              Text(
                text = "Apply: $recommendedCalories kcal",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Basal Metabolic Rate (BMR): $bmr kcal • Daily Maintenance (TDEE): $tdee kcal",
            style = MaterialTheme.typography.bodySmall,
            color = VibrantLavenderOnContainer.copy(alpha = 0.75f)
          )
        }
      }
      Spacer(modifier = Modifier.height(20.dp))
    }

    // Basic Info Section
    item {
      Text(text = "Body & Biometrics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Your Name") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
      )
      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = ageText,
          onValueChange = { ageText = it.filter { c -> c.isDigit() } },
          label = { Text("Age") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp)
        )

        // Gender Selector
        Row(
          modifier = Modifier.weight(1f),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          listOf("Female", "Male").forEach { g ->
            FilterChip(
              selected = gender.equals(g, ignoreCase = true),
              onClick = { gender = g },
              label = { Text(g, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
              shape = RoundedCornerShape(12.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = VibrantPurplePrimary,
                selectedLabelColor = Color.White
              ),
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = heightText,
          onValueChange = { heightText = it.filter { c -> c.isDigit() } },
          label = { Text("Height (cm)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp)
        )
        OutlinedTextField(
          value = weightText,
          onValueChange = { weightText = it },
          label = { Text("Weight (kg)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp)
        )
        OutlinedTextField(
          value = targetWeightText,
          onValueChange = { targetWeightText = it },
          label = { Text("Target (kg)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp)
        )
      }
      Spacer(modifier = Modifier.height(20.dp))
    }

    // Goal Selection
    item {
      Text(text = "Primary Health Goal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
      Spacer(modifier = Modifier.height(8.dp))

      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf("Weight Loss", "Maintain Weight", "Muscle Gain", "Improve Endurance").forEach { g ->
          FilterChip(
            selected = goal == g,
            onClick = { goal = g },
            label = { Text(g, fontWeight = FontWeight.Bold) },
            shape = RoundedCornerShape(12.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = VibrantPurplePrimary,
              selectedLabelColor = Color.White
            )
          )
        }
      }
      Spacer(modifier = Modifier.height(20.dp))
    }

    // Diet Preference
    item {
      Text(text = "Dietary Preference", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
      Spacer(modifier = Modifier.height(8.dp))

      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf("Balanced", "High Protein", "Keto / Low Carb", "Mediterranean", "Vegetarian", "Vegan").forEach { diet ->
          FilterChip(
            selected = dietPreference == diet,
            onClick = { dietPreference = diet },
            label = { Text(diet, fontWeight = FontWeight.Bold) },
            shape = RoundedCornerShape(12.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = VibrantPurplePrimary,
              selectedLabelColor = Color.White
            )
          )
        }
      }
      Spacer(modifier = Modifier.height(20.dp))
    }

    // Activity Level
    item {
      Text(text = "Activity Level", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
      Spacer(modifier = Modifier.height(8.dp))

      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf("Sedentary", "Lightly Active", "Moderately Active", "Very Active").forEach { level ->
          FilterChip(
            selected = activityLevel == level,
            onClick = { activityLevel = level },
            label = { Text(level, fontWeight = FontWeight.Bold) },
            shape = RoundedCornerShape(12.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = VibrantPurplePrimary,
              selectedLabelColor = Color.White
            )
          )
        }
      }
      Spacer(modifier = Modifier.height(20.dp))
    }

    // Target Goals Override
    item {
      Text(text = "Daily Nutrition & Habit Targets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = calorieTargetText,
          onValueChange = { calorieTargetText = it.filter { c -> c.isDigit() } },
          label = { Text("Calories (kcal)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp)
        )
        OutlinedTextField(
          value = waterTargetText,
          onValueChange = { waterTargetText = it.filter { c -> c.isDigit() } },
          label = { Text("Water (ml)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp)
        )
        OutlinedTextField(
          value = stepsTargetText,
          onValueChange = { stepsTargetText = it.filter { c -> c.isDigit() } },
          label = { Text("Steps Target") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp)
        )
      }
      Spacer(modifier = Modifier.height(24.dp))
    }

    // Save Button
    item {
      Button(
        onClick = {
          val calTarget = calorieTargetText.toIntOrNull() ?: 2000
          val proteinG = (calTarget * 0.30f / 4).toInt()
          val carbsG = (calTarget * 0.45f / 4).toInt()
          val fatG = (calTarget * 0.25f / 9).toInt()

          val updated = userProfile.copy(
            name = name.trim().ifBlank { "User" },
            age = ageText.toIntOrNull() ?: 28,
            gender = gender,
            heightCm = heightText.toFloatOrNull() ?: 172f,
            currentWeightKg = weightText.toFloatOrNull() ?: 68f,
            targetWeightKg = targetWeightText.toFloatOrNull() ?: 65f,
            goal = goal,
            dietPreference = dietPreference,
            activityLevel = activityLevel,
            dailyCalorieTarget = calTarget,
            proteinTargetGrams = proteinG,
            carbTargetGrams = carbsG,
            fatTargetGrams = fatG,
            waterTargetMl = waterTargetText.toIntOrNull() ?: 2500,
            dailyStepTarget = stepsTargetText.toIntOrNull() ?: 8000
          )
          onSaveProfile(updated)
          saveFeedback = true
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("btn_save_profile"),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = VibrantPurplePrimary)
      ) {
        Icon(Icons.Default.Save, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(if (saveFeedback) "Profile Saved Successfully! ✓" else "Save Health Profile", fontWeight = FontWeight.Bold)
      }
      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}
