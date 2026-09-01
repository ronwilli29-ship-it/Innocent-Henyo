package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailySummary
import com.example.ui.theme.ColorActivity
import com.example.ui.theme.ColorCalories
import com.example.ui.theme.ColorCarbs
import com.example.ui.theme.ColorFat
import com.example.ui.theme.ColorProtein
import com.example.ui.theme.ColorWater
import com.example.ui.theme.VibrantLavenderContainer
import com.example.ui.theme.VibrantLavenderOnContainer
import com.example.ui.theme.VibrantMintAccent
import com.example.ui.theme.VibrantMintContainer
import com.example.ui.theme.VibrantMintDark
import com.example.ui.theme.VibrantMintOnContainer
import com.example.ui.theme.VibrantPurpleDark
import com.example.ui.theme.VibrantPurplePrimary
import com.example.ui.theme.VibrantRoseAccent
import com.example.ui.theme.VibrantSkyBlueAccent
import com.example.ui.theme.VibrantSkyBlueContainer
import com.example.ui.theme.VibrantSkyBlueDark
import com.example.ui.theme.VibrantSkyBlueOnContainer
import com.example.ui.theme.VibrantSunsetOrange

@Composable
fun CalorieHeroCard(
  summary: DailySummary,
  modifier: Modifier = Modifier,
  onLogMealClick: () -> Unit,
  onLogWorkoutClick: () -> Unit
) {
  val remaining = summary.targetCalories - summary.caloriesConsumed
  val calorieProgress = if (summary.targetCalories > 0) {
    (summary.caloriesConsumed.toFloat() / summary.targetCalories).coerceIn(0f, 1.25f)
  } else 0f

  val percentage = if (summary.targetCalories > 0) {
    ((summary.caloriesConsumed.toFloat() / summary.targetCalories) * 100).toInt().coerceIn(0, 999)
  } else 0

  val animatedProgress by animateFloatAsState(
    targetValue = calorieProgress,
    animationSpec = tween(durationMillis = 800),
    label = "CalorieProgress"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("calorie_hero_card"),
    shape = RoundedCornerShape(28.dp),
    colors = CardDefaults.cardColors(
      containerColor = VibrantLavenderContainer
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(22.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Daily Progress",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = VibrantLavenderOnContainer
          )
          Text(
            text = "Target: ${summary.targetCalories} kcal",
            style = MaterialTheme.typography.bodySmall,
            color = VibrantLavenderOnContainer.copy(alpha = 0.7f)
          )
        }

        Surface(
          shape = RoundedCornerShape(50),
          color = Color.White.copy(alpha = 0.6f)
        ) {
          Text(
            text = "$percentage%",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = VibrantLavenderOnContainer
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Calorie Circular Ring
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.size(110.dp)
        ) {
          val strokeWidth = 10.dp
          val primaryColor = VibrantPurplePrimary
          val trackColor = Color.White.copy(alpha = 0.45f)

          Canvas(modifier = Modifier.size(110.dp)) {
            val strokePx = strokeWidth.toPx()
            val radius = (size.minDimension - strokePx) / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Background Track
            drawCircle(
              color = trackColor,
              radius = radius,
              center = center,
              style = Stroke(width = strokePx)
            )

            // Active Arc
            val sweep = animatedProgress * 360f
            drawArc(
              color = if (animatedProgress <= 1f) primaryColor else VibrantRoseAccent,
              startAngle = -90f,
              sweepAngle = sweep.coerceAtMost(360f),
              useCenter = false,
              topLeft = Offset(strokePx / 2, strokePx / 2),
              size = Size(size.width - strokePx, size.height - strokePx),
              style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "${summary.caloriesConsumed}",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = VibrantLavenderOnContainer
            )
            Text(
              text = "kcal eaten",
              style = MaterialTheme.typography.labelSmall,
              fontSize = 10.sp,
              color = VibrantLavenderOnContainer.copy(alpha = 0.7f)
            )
          }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Breakdown stats
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          VibrantMetricRow(
            label = "Calories",
            value = "${summary.caloriesConsumed} kcal",
            color = VibrantPurplePrimary,
            icon = Icons.Default.LocalFireDepartment
          )
          VibrantMetricRow(
            label = "Burned",
            value = "${summary.caloriesBurned} kcal",
            color = ColorProtein,
            icon = Icons.Default.DirectionsRun
          )
          VibrantMetricRow(
            label = "Water",
            value = "${String.format("%.1f", summary.waterMl / 1000f)} L",
            color = VibrantSkyBlueAccent,
            icon = Icons.Default.Opacity
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Macro Progress Bars
      MacroSplitRow(
        carbs = summary.carbsGrams,
        targetCarbs = summary.targetCarbs,
        protein = summary.proteinGrams,
        targetProtein = summary.targetProtein,
        fat = summary.fatGrams,
        targetFat = summary.targetFat
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Quick action row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = onLogMealClick,
          modifier = Modifier
            .weight(1f)
            .height(44.dp)
            .testTag("btn_quick_log_meal"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = VibrantPurplePrimary,
            contentColor = Color.White
          )
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Log Meal", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        Button(
          onClick = onLogWorkoutClick,
          modifier = Modifier
            .weight(1f)
            .height(44.dp)
            .testTag("btn_quick_log_workout"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.75f),
            contentColor = VibrantPurpleDark
          )
        ) {
          Icon(
            Icons.Default.DirectionsRun,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = VibrantPurpleDark
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            "Log Activity",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = VibrantPurpleDark
          )
        }
      }
    }
  }
}

@Composable
private fun VibrantMetricRow(
  label: String,
  value: String,
  color: Color,
  icon: androidx.compose.ui.graphics.vector.ImageVector
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(26.dp)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          icon,
          contentDescription = null,
          tint = color,
          modifier = Modifier.size(15.dp)
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        color = VibrantLavenderOnContainer.copy(alpha = 0.75f),
        fontWeight = FontWeight.Medium
      )
    }
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium,
      fontWeight = FontWeight.Bold,
      color = VibrantLavenderOnContainer
    )
  }
}

@Composable
fun MacroSplitRow(
  carbs: Int,
  targetCarbs: Int,
  protein: Int,
  targetProtein: Int,
  fat: Int,
  targetFat: Int
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    MacroItem(
      label = "Protein",
      current = protein,
      target = targetProtein,
      color = ColorProtein,
      modifier = Modifier.weight(1f)
    )
    MacroItem(
      label = "Carbs",
      current = carbs,
      target = targetCarbs,
      color = ColorCarbs,
      modifier = Modifier.weight(1f)
    )
    MacroItem(
      label = "Fats",
      current = fat,
      target = targetFat,
      color = ColorFat,
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
fun MacroItem(
  label: String,
  current: Int,
  target: Int,
  color: Color,
  modifier: Modifier = Modifier
) {
  val progress = if (target > 0) (current.toFloat() / target).coerceIn(0f, 1f) else 0f

  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    color = Color.White.copy(alpha = 0.6f)
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = label,
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = VibrantLavenderOnContainer
        )
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "${current}g",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.ExtraBold,
        color = VibrantLavenderOnContainer
      )
      Text(
        text = "of ${target}g",
        style = MaterialTheme.typography.labelSmall,
        fontSize = 10.sp,
        color = VibrantLavenderOnContainer.copy(alpha = 0.7f)
      )

      Spacer(modifier = Modifier.height(6.dp))

      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(5.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = color,
        trackColor = Color.White.copy(alpha = 0.5f)
      )
    }
  }
}

@Composable
fun QuickHabitCardsRow(
  waterMl: Int,
  targetWaterMl: Int,
  steps: Int,
  targetSteps: Int,
  onAddWater: (Int) -> Unit,
  onAddSteps: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Water Tracker Card (Vibrant Sky Blue #D3E4FF)
    Card(
      modifier = Modifier
        .weight(1f)
        .testTag("water_card"),
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(containerColor = VibrantSkyBlueContainer),
      elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color.White),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.WaterDrop,
              contentDescription = "Water",
              tint = VibrantSkyBlueAccent,
              modifier = Modifier.size(20.dp)
            )
          }

          IconButton(
            onClick = { onAddWater(250) },
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.8f))
              .testTag("btn_add_water_250")
          ) {
            Icon(
              Icons.Default.Add,
              contentDescription = "Add 250ml",
              tint = VibrantSkyBlueDark,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "${waterMl} ml",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.ExtraBold,
          color = VibrantSkyBlueOnContainer
        )
        Text(
          text = "Goal: ${targetWaterMl} ml",
          style = MaterialTheme.typography.labelSmall,
          color = VibrantSkyBlueOnContainer.copy(alpha = 0.75f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        val waterProgress = if (targetWaterMl > 0) (waterMl.toFloat() / targetWaterMl).coerceIn(0f, 1f) else 0f
        LinearProgressIndicator(
          progress = { waterProgress },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = VibrantSkyBlueDark,
          trackColor = Color.White.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "+250ml (1 cup)",
          style = MaterialTheme.typography.labelSmall,
          fontSize = 11.sp,
          color = VibrantSkyBlueDark,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.clickable { onAddWater(250) }
        )
      }
    }

    // Steps Tracker Card (Vibrant Mint #C7EBC5)
    Card(
      modifier = Modifier
        .weight(1f)
        .testTag("steps_card"),
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(containerColor = VibrantMintContainer),
      elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color.White),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.DirectionsRun,
              contentDescription = "Steps",
              tint = VibrantMintAccent,
              modifier = Modifier.size(20.dp)
            )
          }

          IconButton(
            onClick = { onAddSteps(1000) },
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.8f))
              .testTag("btn_add_steps_1000")
          ) {
            Icon(
              Icons.Default.Add,
              contentDescription = "Add 1000 steps",
              tint = VibrantMintDark,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "${steps} steps",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.ExtraBold,
          color = VibrantMintOnContainer
        )
        Text(
          text = "Goal: ${targetSteps}",
          style = MaterialTheme.typography.labelSmall,
          color = VibrantMintOnContainer.copy(alpha = 0.75f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        val stepProgress = if (targetSteps > 0) (steps.toFloat() / targetSteps).coerceIn(0f, 1f) else 0f
        LinearProgressIndicator(
          progress = { stepProgress },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = VibrantMintDark,
          trackColor = Color.White.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "+1,000 steps",
          style = MaterialTheme.typography.labelSmall,
          fontSize = 11.sp,
          color = VibrantMintDark,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.clickable { onAddSteps(1000) }
        )
      }
    }
  }
}
