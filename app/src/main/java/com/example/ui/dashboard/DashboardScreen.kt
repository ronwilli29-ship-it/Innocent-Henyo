package com.example.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActivityLog
import com.example.data.model.DailySummary
import com.example.data.model.HealthInsight
import com.example.data.model.MealPlanItem
import com.example.data.model.UserProfile
import com.example.ui.components.CalorieHeroCard
import com.example.ui.components.QuickHabitCardsRow
import com.example.ui.theme.ColorActivity
import com.example.ui.theme.ColorCalories
import com.example.ui.theme.ColorProtein
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
import com.example.ui.theme.VibrantRosePink
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
fun DashboardScreen(
  userProfile: UserProfile,
  summary: DailySummary,
  selectedDate: String,
  meals: List<MealPlanItem>,
  activities: List<ActivityLog>,
  insights: List<HealthInsight>,
  onDateSelected: (String) -> Unit,
  onLogMealClick: () -> Unit,
  onLogWorkoutClick: () -> Unit,
  onAddWater: (Int) -> Unit,
  onAddSteps: (Int) -> Unit,
  onToggleMealLogged: (MealPlanItem) -> Unit,
  onMealClick: (MealPlanItem) -> Unit,
  onProfileClick: () -> Unit,
  onNavigateToMeals: () -> Unit,
  onNavigateToActivity: () -> Unit
) {
  val today = remember { LocalDate.now() }
  val currentSelectedDate = remember(selectedDate) {
    try {
      LocalDate.parse(selectedDate)
    } catch (e: Exception) {
      today
    }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("screen_dashboard"),
    contentPadding = PaddingValues(bottom = 96.dp)
  ) {
    // Header & Greeting with Gradient Avatar
    item {
      DashboardHeader(
        profile = userProfile,
        selectedDate = currentSelectedDate,
        onPreviousDay = {
          val prev = currentSelectedDate.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
          onDateSelected(prev)
        },
        onNextDay = {
          val next = currentSelectedDate.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
          onDateSelected(next)
        },
        onProfileClick = onProfileClick
      )
    }

    // 7-Day Quick Date Strip
    item {
      DateSelectorStrip(
        selectedDate = selectedDate,
        onDateSelected = onDateSelected
      )
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Main Calorie & Energy Balance Hero Card (Vibrant Lavender)
    item {
      Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        CalorieHeroCard(
          summary = summary,
          onLogMealClick = onLogMealClick,
          onLogWorkoutClick = onLogWorkoutClick
        )
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Water (Vibrant Sky Blue) and Steps (Vibrant Mint) Habit Cards
    item {
      Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        QuickHabitCardsRow(
          waterMl = summary.waterMl,
          targetWaterMl = summary.targetWaterMl,
          steps = summary.steps,
          targetSteps = summary.targetSteps,
          onAddWater = onAddWater,
          onAddSteps = onAddSteps
        )
      }
      Spacer(modifier = Modifier.height(20.dp))
    }

    // Today's Meals Section Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Today's Meals",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "${meals.count { it.isLogged }}/${meals.size} logged",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        TextButton(onClick = onNavigateToMeals) {
          Text("See All", color = VibrantPurplePrimary, fontWeight = FontWeight.Bold)
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
    }

    // Meal Plan Items List (Vibrant Pastel Containers)
    if (meals.isEmpty()) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(VibrantMintContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.Restaurant,
                contentDescription = null,
                tint = VibrantMintDark,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "No meals planned for today",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))
            TextButton(onClick = onNavigateToMeals) {
              Text("Generate Meal Plan", color = VibrantPurplePrimary, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    } else {
      items(meals) { meal ->
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
          DashboardMealCard(
            meal = meal,
            onToggleLogged = { onToggleMealLogged(meal) },
            onClick = { onMealClick(meal) }
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(20.dp))
    }

    // Next Workout / Activity Section Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Activity & Workouts",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "${summary.activeMinutes} mins • ${summary.caloriesBurned} kcal burned",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        TextButton(onClick = onNavigateToActivity) {
          Text("Manage", color = VibrantPurplePrimary, fontWeight = FontWeight.Bold)
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
    }

    // Activity items
    if (activities.isEmpty()) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onLogWorkoutClick() },
          shape = RoundedCornerShape(26.dp),
          colors = CardDefaults.cardColors(containerColor = VibrantRoseContainer),
          elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .clip(RoundedCornerShape(16.dp))
                  .background(Color.White),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  Icons.Default.FitnessCenter,
                  contentDescription = null,
                  tint = VibrantRosePink,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.width(14.dp))
              Column {
                Text(
                  "Next Activity",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = VibrantRoseDark
                )
                Text(
                  "Log a workout or run",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = VibrantRoseOnContainer
                )
                Text(
                  "Keep your daily streak active!",
                  style = MaterialTheme.typography.bodySmall,
                  color = VibrantRoseOnContainer.copy(alpha = 0.75f)
                )
              }
            }

            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(VibrantRosePink),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Add, contentDescription = "Log", tint = Color.White, modifier = Modifier.size(20.dp))
            }
          }
        }
      }
    } else {
      items(activities) { act ->
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
          DashboardActivityCard(activity = act)
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(20.dp))
    }

    // Smart Health Coach Insight Card
    if (insights.isNotEmpty()) {
      item {
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
          val insight = insights.first()
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(
              containerColor = VibrantSkyBlueContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
              verticalAlignment = Alignment.Top
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(RoundedCornerShape(14.dp))
                  .background(Color.White),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = VibrantSkyBlueAccent,
                  modifier = Modifier.size(22.dp)
                )
              }

              Spacer(modifier = Modifier.width(14.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "HEALTH COACH INSIGHT",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = VibrantSkyBlueDark,
                  letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                  text = insight.title,
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = VibrantSkyBlueOnContainer
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                  text = insight.description,
                  style = MaterialTheme.typography.bodySmall,
                  color = VibrantSkyBlueOnContainer.copy(alpha = 0.8f),
                  lineHeight = 18.sp
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
private fun DashboardHeader(
  profile: UserProfile,
  selectedDate: LocalDate,
  onPreviousDay: () -> Unit,
  onNextDay: () -> Unit,
  onProfileClick: () -> Unit
) {
  val isToday = selectedDate == LocalDate.now()
  val dateTitle = if (isToday) {
    "Today, ${selectedDate.format(DateTimeFormatter.ofPattern("MMM d"))}"
  } else {
    selectedDate.format(DateTimeFormatter.ofPattern("EEE, MMM d"))
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 16.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMM d")).uppercase(),
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "Hi, ${profile.name.split(" ").firstOrNull() ?: "Friend"}",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.ExtraBold,
          color = MaterialTheme.colorScheme.onBackground
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Streak Badge (Vibrant Sunset #FFEDD5)
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = VibrantSunsetContainer,
          modifier = Modifier.clip(RoundedCornerShape(16.dp))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("🔥", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${profile.streakDays}d Streak",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = VibrantSunsetOrange
            )
          }
        }

        // Vibrant Gradient Profile Avatar Button
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(
              brush = Brush.linearGradient(
                listOf(Color(0xFFFB923C), Color(0xFFFB7185))
              )
            )
            .border(2.dp, Color.White, CircleShape)
            .clickable { onProfileClick() }
            .testTag("btn_profile_avatar"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            Icons.Default.Person,
            contentDescription = "Profile",
            tint = Color.White,
            modifier = Modifier.size(22.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Day navigator
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onPreviousDay, modifier = Modifier.size(32.dp)) {
        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Day", tint = MaterialTheme.colorScheme.onSurface)
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          Icons.Default.CalendarToday,
          contentDescription = null,
          modifier = Modifier.size(16.dp),
          tint = VibrantPurplePrimary
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = dateTitle,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
      }

      IconButton(onClick = onNextDay, modifier = Modifier.size(32.dp)) {
        Icon(Icons.Default.ChevronRight, contentDescription = "Next Day", tint = MaterialTheme.colorScheme.onSurface)
      }
    }
  }
}

@Composable
private fun DateSelectorStrip(
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
      val isToday = date == today

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
          if (isToday) {
            Box(
              modifier = Modifier
                .padding(top = 3.dp)
                .size(5.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color.White else VibrantPurplePrimary)
            )
          }
        }
      }
    }
  }
}

@Composable
fun DashboardMealCard(
  meal: MealPlanItem,
  onToggleLogged: () -> Unit,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  // Vibrant thematic colors per meal type
  val (cardBg, onCardText, accentDark) = when (meal.mealType.lowercase()) {
    "breakfast" -> Triple(VibrantSkyBlueContainer, VibrantSkyBlueOnContainer, VibrantSkyBlueDark)
    "lunch" -> Triple(VibrantMintContainer, VibrantMintOnContainer, VibrantMintDark)
    "dinner" -> Triple(VibrantRoseContainer, VibrantRoseOnContainer, VibrantRoseDark)
    else -> Triple(VibrantSunsetContainer, VibrantSunsetOnContainer, VibrantSunsetOrange)
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(22.dp))
      .clickable { onClick() }
      .testTag("meal_card_${meal.id}"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(
      containerColor = cardBg
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Checkbox / Logged Circle
      IconButton(
        onClick = onToggleLogged,
        modifier = Modifier
          .size(38.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(
            if (meal.isLogged) accentDark else Color.White
          )
          .testTag("btn_check_meal_${meal.id}")
      ) {
        Icon(
          if (meal.isLogged) Icons.Default.Check else Icons.Default.RadioButtonUnchecked,
          contentDescription = if (meal.isLogged) "Marked Eaten" else "Mark Eaten",
          tint = if (meal.isLogged) Color.White else accentDark,
          modifier = Modifier.size(18.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = meal.mealType.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = accentDark,
            fontSize = 10.sp,
            letterSpacing = 0.8.sp
          )

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.LocalFireDepartment,
              contentDescription = null,
              tint = accentDark,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
              text = "${meal.calories} kcal",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = onCardText
            )
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = meal.name,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold,
          color = onCardText,
          maxLines = 1
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "P: ${meal.protein}g • C: ${meal.carbs}g • F: ${meal.fat}g",
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = onCardText.copy(alpha = 0.75f)
          )
          Text(
            text = "• ${meal.prepTimeMinutes}m prep",
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = onCardText.copy(alpha = 0.75f)
          )
        }
      }
    }
  }
}

@Composable
fun DashboardActivityCard(
  activity: ActivityLog,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
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

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = activity.activityType,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = VibrantRoseOnContainer
          )

          Text(
            text = "${activity.caloriesBurned} kcal",
            style = MaterialTheme.typography.bodyMedium,
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
          Text(
            text = "• ${activity.intensity} intensity",
            style = MaterialTheme.typography.bodySmall,
            color = VibrantRoseOnContainer.copy(alpha = 0.8f)
          )
        }

        if (activity.notes.isNotBlank()) {
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = activity.notes,
            style = MaterialTheme.typography.labelSmall,
            color = VibrantRoseOnContainer.copy(alpha = 0.7f),
            maxLines = 1
          )
        }
      }
    }
  }
}
