package com.example.ui.components

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DayTrend
import com.example.ui.theme.VibrantMintDark
import com.example.ui.theme.VibrantPurplePrimary
import com.example.ui.theme.VibrantRosePink
import com.example.ui.theme.VibrantSkyBlueAccent
import com.example.ui.theme.VibrantSunsetOrange

@Composable
fun CalorieTrendBarChart(
  trends: List<DayTrend>,
  targetCalorie: Int = 2000,
  modifier: Modifier = Modifier
) {
  var selectedIndex by remember { mutableStateOf<Int?>(null) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("chart_calorie_trend"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Calorie Balance Trend",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
          )
          Text(
            text = "Intake (Mint) vs Burn (Rose)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Legend
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          LegendDot(color = VibrantMintDark, label = "Food")
          LegendDot(color = VibrantRosePink, label = "Burn")
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      if (trends.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
          contentAlignment = Alignment.Center
        ) {
          Text("No trend data logged yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      } else {
        val maxCal = trends.maxOfOrNull { maxOf(it.caloriesConsumed, it.caloriesBurned, targetCalorie) } ?: 2500
        val maxVal = (maxCal * 1.15f).coerceAtLeast(1000f)

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
        ) {
          Canvas(
            modifier = Modifier
              .fillMaxWidth()
              .height(130.dp)
          ) {
            val width = size.width
            val height = size.height
            val barGroupWidth = width / trends.size
            val barWidth = (barGroupWidth * 0.32f).coerceAtLeast(6f)

            // Target reference line
            val targetY = height - (targetCalorie / maxVal * height)
            drawLine(
              color = Color.Gray.copy(alpha = 0.25f),
              start = Offset(0f, targetY),
              end = Offset(width, targetY),
              strokeWidth = 2f,
              cap = StrokeCap.Round
            )

            trends.forEachIndexed { i, trend ->
              val groupCenterX = i * barGroupWidth + barGroupWidth / 2

              val foodHeight = (trend.caloriesConsumed / maxVal * height).coerceAtLeast(4f)
              val burnHeight = (trend.caloriesBurned / maxVal * height).coerceAtLeast(4f)

              val foodX = groupCenterX - barWidth - 2f
              val burnX = groupCenterX + 2f

              // Food Intake Bar
              drawRoundRect(
                color = VibrantMintDark,
                topLeft = Offset(foodX, height - foodHeight),
                size = Size(barWidth, foodHeight),
                cornerRadius = CornerRadius(8f, 8f)
              )

              // Burn Bar
              drawRoundRect(
                color = VibrantRosePink,
                topLeft = Offset(burnX, height - burnHeight),
                size = Size(barWidth, burnHeight),
                cornerRadius = CornerRadius(8f, 8f)
              )
            }
          }

          // X-Axis Labels
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            trends.forEachIndexed { index, item ->
              Text(
                text = item.dayLabel,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal,
                color = if (selectedIndex == index) VibrantPurplePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable {
                  selectedIndex = if (selectedIndex == index) null else index
                }
              )
            }
          }
        }

        // Tooltip display if tapped
        selectedIndex?.let { idx ->
          if (idx in trends.indices) {
            val item = trends[idx]
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "${item.date} (${item.dayLabel})",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Eaten: ${item.caloriesConsumed} kcal | Burned: ${item.caloriesBurned} kcal",
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
}

@Composable
fun MacroDonutChartCard(
  carbs: Int,
  protein: Int,
  fat: Int,
  modifier: Modifier = Modifier
) {
  val totalGrams = (carbs + protein + fat).coerceAtLeast(1)
  val carbPct = (carbs * 100) / totalGrams
  val proteinPct = (protein * 100) / totalGrams
  val fatPct = (fat * 100) / totalGrams

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("chart_macro_donut"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Text(
        text = "Macro Distribution",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold
      )
      Text(
        text = "Caloric breakdown from consumed foods",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(16.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Donut ring
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.size(110.dp)
        ) {
          Canvas(modifier = Modifier.size(110.dp)) {
            val stroke = 18.dp.toPx()
            val arcSize = Size(size.width - stroke, size.height - stroke)
            val topLeft = Offset(stroke / 2, stroke / 2)

            val carbSweep = (carbPct / 100f) * 360f
            val proteinSweep = (proteinPct / 100f) * 360f
            val fatSweep = (fatPct / 100f) * 360f

            var startAngle = -90f

            // Carbs
            drawArc(
              color = VibrantSkyBlueAccent,
              startAngle = startAngle,
              sweepAngle = carbSweep,
              useCenter = false,
              topLeft = topLeft,
              size = arcSize,
              style = Stroke(width = stroke)
            )
            startAngle += carbSweep

            // Protein
            drawArc(
              color = VibrantPurplePrimary,
              startAngle = startAngle,
              sweepAngle = proteinSweep,
              useCenter = false,
              topLeft = topLeft,
              size = arcSize,
              style = Stroke(width = stroke)
            )
            startAngle += proteinSweep

            // Fat
            drawArc(
              color = VibrantSunsetOrange,
              startAngle = startAngle,
              sweepAngle = fatSweep,
              useCenter = false,
              topLeft = topLeft,
              size = arcSize,
              style = Stroke(width = stroke)
            )
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "${carbs * 4 + protein * 4 + fat * 9}",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold
            )
            Text(
              text = "kcal",
              style = MaterialTheme.typography.labelSmall,
              fontSize = 9.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Macro List items
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          MacroPercentageRow(label = "Protein", grams = protein, pct = proteinPct, color = VibrantPurplePrimary)
          MacroPercentageRow(label = "Carbohydrates", grams = carbs, pct = carbPct, color = VibrantSkyBlueAccent)
          MacroPercentageRow(label = "Healthy Fats", grams = fat, pct = fatPct, color = VibrantSunsetOrange)
        }
      }
    }
  }
}

@Composable
private fun MacroPercentageRow(
  label: String,
  grams: Int,
  pct: Int,
  color: Color
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(10.dp)
          .clip(CircleShape)
          .background(color)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface
      )
    }

    Text(
      text = "$grams g ($pct%)",
      style = MaterialTheme.typography.bodySmall,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}

@Composable
fun ActivityTrendsCard(
  trends: List<DayTrend>,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("chart_activity_trend"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Active Minutes Trend",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
          )
          Text(
            text = "Daily exercise and active minutes",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = VibrantRosePink.copy(alpha = 0.15f)
        ) {
          val totalMins = trends.sumOf { it.activeMinutes }
          Text(
            text = "$totalMins mins total",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = VibrantRosePink
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      val maxMins = (trends.maxOfOrNull { it.activeMinutes } ?: 60).coerceAtLeast(45).toFloat()

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(120.dp)
      ) {
        Canvas(
          modifier = Modifier
            .fillMaxWidth()
            .height(95.dp)
        ) {
          val width = size.width
          val height = size.height
          val stepX = width / (trends.size - 1).coerceAtLeast(1)

          val points = trends.mapIndexed { index, trend ->
            val x = index * stepX
            val y = height - (trend.activeMinutes / maxMins * height)
            Offset(x, y)
          }

          // Draw fill gradient path
          if (points.isNotEmpty()) {
            val fillPath = Path().apply {
              moveTo(points.first().x, height)
              points.forEach { lineTo(it.x, it.y) }
              lineTo(points.last().x, height)
              close()
            }

            drawPath(
              path = fillPath,
              brush = Brush.verticalGradient(
                colors = listOf(VibrantRosePink.copy(alpha = 0.35f), Color.Transparent)
              )
            )

            // Draw line
            val linePath = Path().apply {
              moveTo(points.first().x, points.first().y)
              for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
              }
            }

            drawPath(
              path = linePath,
              color = VibrantRosePink,
              style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw point dots
            points.forEach { pt ->
              drawCircle(
                color = Color.White,
                radius = 5.dp.toPx(),
                center = pt
              )
              drawCircle(
                color = VibrantRosePink,
                radius = 3.5.dp.toPx(),
                center = pt
              )
            }
          }
        }

        // Labels
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          trends.forEach { item ->
            Text(
              text = item.dayLabel,
              style = MaterialTheme.typography.labelSmall,
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}

@Composable
private fun LegendDot(color: Color, label: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      fontSize = 10.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}
