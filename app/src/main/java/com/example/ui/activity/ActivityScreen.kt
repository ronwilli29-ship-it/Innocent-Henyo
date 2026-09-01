package com.example.ui.activity

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActivityLog
import com.example.data.model.DailySummary
import com.example.data.model.UserProfile
import com.example.ui.theme.VibrantMintAccent
import com.example.ui.theme.VibrantMintContainer
import com.example.ui.theme.VibrantMintDark
import com.example.ui.theme.VibrantPurpleDark
import com.example.ui.theme.VibrantPurplePrimary
import com.example.ui.theme.VibrantRoseAccent
import com.example.ui.theme.VibrantRoseContainer
import com.example.ui.theme.VibrantRoseDark
import com.example.ui.theme.VibrantRoseOnContainer
import com.example.ui.theme.VibrantRosePink
import com.example.ui.theme.VibrantSkyBlueAccent
import com.example.ui.theme.VibrantSkyBlueContainer
import com.example.ui.theme.VibrantSunsetContainer
import com.example.ui.theme.VibrantSunsetOrange
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ActivityScreen(
  userProfile: UserProfile,
  summary: DailySummary,
  selectedDate: String,
  activities: List<ActivityLog>,
  onDateSelected: (String) -> Unit,
  onOpenLogActivityDialog: () -> Unit,
  onDeleteActivity: (ActivityLog) -> Unit,
  onQuickLogPreset: (type: String, mins: Int, burn: Int, intensity: String) -> Unit,
  onAddSteps: (Int) -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .testTag("screen_activity")
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(bottom = 96.dp)
    ) {
      // Header
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
          Text(
            text = "Activity & Movement",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
          )
          Text(
            text = "Track workouts, active minutes, and calorie burn",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Date Selector
      item {
        ActivityDateStrip(selectedDate = selectedDate, onDateSelected = onDateSelected)
        Spacer(modifier = Modifier.height(14.dp))
      }

      // Hero Movement Summary Card (Vibrant Rose Container #FFDAD6)
      item {
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
          ActivitySummaryHeroCard(
            summary = summary,
            userProfile = userProfile,
            onOpenLog = onOpenLogActivityDialog
          )
        }
        Spacer(modifier = Modifier.height(20.dp))
      }

      // Quick Workout Presets Strip
      item {
        Text(
          text = "Quick Workout Presets",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.ExtraBold,
          modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
          modifier = Modifier.fillMaxWidth(),
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          val presets = listOf(
            Triple("Running", "30m • 310 kcal", Icons.Default.DirectionsRun) to (Pair(30, 310) to VibrantRoseContainer),
            Triple("Strength", "45m • 260 kcal", Icons.Default.FitnessCenter) to (Pair(45, 260) to VibrantSkyBlueContainer),
            Triple("Walking", "20m • 100 kcal", Icons.Default.DirectionsWalk) to (Pair(20, 100) to VibrantMintContainer),
            Triple("Cycling", "35m • 280 kcal", Icons.Default.DirectionsBike) to (Pair(35, 280) to VibrantSunsetContainer),
            Triple("Yoga", "25m • 110 kcal", Icons.Default.SelfImprovement) to (Pair(25, 110) to VibrantRoseContainer)
          )

          items(presets) { (info, data) ->
            val (stats, bgContainer) = data
            Card(
              modifier = Modifier
                .width(136.dp)
                .clip(RoundedCornerShape(20.dp))
                .clickable {
                  onQuickLogPreset(info.first, stats.first, stats.second, "Moderate")
                },
              shape = RoundedCornerShape(20.dp),
              colors = CardDefaults.cardColors(containerColor = bgContainer),
              elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
              Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.Center
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(info.third, contentDescription = null, tint = VibrantPurplePrimary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                  text = info.first,
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.ExtraBold,
                  maxLines = 1
                )
                Text(
                  text = info.second,
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                  fontSize = 11.sp
                )
              }
            }
          }
        }
        Spacer(modifier = Modifier.height(22.dp))
      }

      // Activity History List Header
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Logged Sessions (${activities.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
      }

      if (activities.isEmpty()) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                Icons.Default.DirectionsRun,
                contentDescription = null,
                tint = VibrantRosePink,
                modifier = Modifier.size(44.dp)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                "No workouts recorded for today",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                "Log your exercise or choose a quick preset above.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(14.dp))
              Button(
                onClick = onOpenLogActivityDialog,
                colors = ButtonDefaults.buttonColors(containerColor = VibrantRosePink),
                shape = RoundedCornerShape(16.dp)
              ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Log Exercise", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      } else {
        items(activities) { act ->
          Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
            ActivityLogCard(
              activity = act,
              onDelete = { onDeleteActivity(act) }
            )
          }
        }
      }
    }

    // FAB to Log Activity
    FloatingActionButton(
      onClick = onOpenLogActivityDialog,
      containerColor = VibrantRosePink,
      contentColor = Color.White,
      shape = CircleShape,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 20.dp, bottom = 100.dp)
        .testTag("fab_log_activity")
    ) {
      Icon(Icons.Default.Add, contentDescription = "Log Workout")
    }
  }
}

@Composable
private fun ActivitySummaryHeroCard(
  summary: DailySummary,
  userProfile: UserProfile,
  onOpenLog: () -> Unit
) {
  val activeProgress = if (summary.targetActiveMinutes > 0) {
    (summary.activeMinutes.toFloat() / summary.targetActiveMinutes).coerceIn(0f, 1f)
  } else 0f

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(28.dp),
    colors = CardDefaults.cardColors(containerColor = VibrantRoseContainer),
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
            text = "Active Movement Goal",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = VibrantRoseOnContainer
          )
          Text(
            text = "${summary.activeMinutes} of ${summary.targetActiveMinutes} mins completed",
            style = MaterialTheme.typography.bodySmall,
            color = VibrantRoseOnContainer.copy(alpha = 0.75f)
          )
        }

        Surface(
          shape = RoundedCornerShape(50),
          color = Color.White.copy(alpha = 0.75f)
        ) {
          Text(
            text = "${(activeProgress * 100).toInt()}%",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = VibrantRosePink
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      LinearProgressIndicator(
        progress = { activeProgress },
        modifier = Modifier
          .fillMaxWidth()
          .height(10.dp)
          .clip(RoundedCornerShape(5.dp)),
        color = VibrantRosePink,
        trackColor = Color.White.copy(alpha = 0.5f)
      )

      Spacer(modifier = Modifier.height(18.dp))

      // 3 Stat pillars
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        ActivityStatPillar(
          label = "Calories Burned",
          value = "${summary.caloriesBurned} kcal",
          color = VibrantRosePink,
          modifier = Modifier.weight(1f)
        )
        ActivityStatPillar(
          label = "Daily Steps",
          value = "${summary.steps}",
          color = VibrantSunsetOrange,
          modifier = Modifier.weight(1f)
        )
        ActivityStatPillar(
          label = "Distance",
          value = String.format(Locale.getDefault(), "%.1f km", summary.steps * 0.00075f),
          color = VibrantSkyBlueAccent,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
private fun ActivityStatPillar(
  label: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = value,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.ExtraBold,
      color = color
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = VibrantRoseOnContainer.copy(alpha = 0.75f)
    )
  }
}

@Composable
private fun ActivityLogCard(
  activity: ActivityLog,
  onDelete: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = VibrantRoseContainer),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(Color.White),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          Icons.Default.DirectionsRun,
          contentDescription = null,
          tint = VibrantRosePink,
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = activity.activityType,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.ExtraBold,
            color = VibrantRoseOnContainer
          )

          Text(
            text = "${activity.caloriesBurned} kcal",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.ExtraBold,
            color = VibrantRosePink
          )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "${activity.durationMinutes} mins",
            style = MaterialTheme.typography.bodySmall,
            color = VibrantRoseOnContainer.copy(alpha = 0.8f)
          )
          if (activity.distanceKm != null && activity.distanceKm > 0) {
            Text(
              text = "• ${activity.distanceKm} km",
              style = MaterialTheme.typography.bodySmall,
              color = VibrantRoseOnContainer.copy(alpha = 0.8f)
            )
          }
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White.copy(alpha = 0.75f)
          ) {
            Text(
              text = activity.intensity,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              style = MaterialTheme.typography.labelSmall,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = VibrantRoseDark
            )
          }
        }

        if (activity.notes.isNotBlank()) {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = activity.notes,
            style = MaterialTheme.typography.labelSmall,
            color = VibrantRoseOnContainer.copy(alpha = 0.7f)
          )
        }
      }

      IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
        Icon(
          Icons.Default.Delete,
          contentDescription = "Delete",
          tint = VibrantRoseDark.copy(alpha = 0.7f),
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}

@Composable
private fun ActivityDateStrip(
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
