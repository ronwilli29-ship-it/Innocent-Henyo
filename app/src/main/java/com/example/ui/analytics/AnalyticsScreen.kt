package com.example.ui.analytics

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BadgeAchievement
import com.example.data.model.DailySummary
import com.example.data.model.DayTrend
import com.example.data.model.HealthInsight
import com.example.data.model.UserProfile
import com.example.ui.components.ActivityTrendsCard
import com.example.ui.components.BadgeAchievementCard
import com.example.ui.components.CalorieTrendBarChart
import com.example.ui.components.MacroDonutChartCard
import com.example.ui.theme.VibrantLavenderContainer
import com.example.ui.theme.VibrantLavenderOnContainer
import com.example.ui.theme.VibrantMintDark
import com.example.ui.theme.VibrantPurpleDark
import com.example.ui.theme.VibrantPurplePrimary
import com.example.ui.theme.VibrantRoseDark
import com.example.ui.theme.VibrantRosePink
import com.example.ui.theme.VibrantSkyBlueAccent
import com.example.ui.theme.VibrantSunsetOrange

@Composable
fun AnalyticsScreen(
  userProfile: UserProfile,
  dailySummary: DailySummary,
  trends: List<DayTrend>,
  insights: List<HealthInsight>,
  achievements: List<BadgeAchievement>,
  analyticsDays: Int,
  onDaysRangeChanged: (Int) -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("screen_analytics"),
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
          text = "Progress & Analytics",
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.ExtraBold
        )
        Text(
          text = "Deep dive into your nutrition balance, activity, and streaks",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Time Range selector
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChip(
          selected = analyticsDays == 7,
          onClick = { onDaysRangeChanged(7) },
          label = { Text("Last 7 Days", fontWeight = FontWeight.Bold) },
          shape = RoundedCornerShape(12.dp),
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = VibrantPurplePrimary,
            selectedLabelColor = Color.White
          )
        )
        FilterChip(
          selected = analyticsDays == 30,
          onClick = { onDaysRangeChanged(30) },
          label = { Text("Last 30 Days", fontWeight = FontWeight.Bold) },
          shape = RoundedCornerShape(12.dp),
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = VibrantPurplePrimary,
            selectedLabelColor = Color.White
          )
        )
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // Consistency & Streak Score Card
    item {
      Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        StreakScoreHeroCard(userProfile = userProfile, trends = trends)
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Calorie Balance Trend Chart
    item {
      Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        CalorieTrendBarChart(
          trends = trends,
          targetCalorie = userProfile.dailyCalorieTarget
        )
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Macronutrient Distribution Donut
    item {
      Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        val totalCarbs = trends.sumOf { (it.caloriesConsumed * 0.45f / 4).toInt() }.coerceAtLeast(dailySummary.carbsGrams)
        val totalProtein = trends.sumOf { (it.caloriesConsumed * 0.30f / 4).toInt() }.coerceAtLeast(dailySummary.proteinGrams)
        val totalFat = trends.sumOf { (it.caloriesConsumed * 0.25f / 9).toInt() }.coerceAtLeast(dailySummary.fatGrams)

        MacroDonutChartCard(
          carbs = totalCarbs,
          protein = totalProtein,
          fat = totalFat
        )
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Active Minutes Trend Line Chart
    item {
      Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        ActivityTrendsCard(trends = trends)
      }
      Spacer(modifier = Modifier.height(20.dp))
    }

    // Health Coach Recommendations & Tips
    item {
      Text(
        text = "Personalized Health Insights",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(horizontal = 20.dp)
      )
      Spacer(modifier = Modifier.height(8.dp))
    }

    items(insights) { insight ->
      Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        InsightCardItem(insight = insight)
      }
    }

    item {
      Spacer(modifier = Modifier.height(20.dp))
    }

    // Milestones & Achievement Badges
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Badges & Milestones",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.ExtraBold
        )
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = VibrantLavenderContainer
        ) {
          val unlockedCount = achievements.count { it.isUnlocked }
          Text(
            text = "$unlockedCount/${achievements.size} Unlocked",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = VibrantPurpleDark
          )
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
    }

    items(achievements) { badge ->
      Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        BadgeAchievementCard(badge = badge)
      }
    }
  }
}

@Composable
private fun StreakScoreHeroCard(
  userProfile: UserProfile,
  trends: List<DayTrend>
) {
  val avgCalories = if (trends.isNotEmpty()) trends.map { it.caloriesConsumed }.average().toInt() else 0
  val avgSteps = if (trends.isNotEmpty()) trends.map { it.steps }.average().toInt() else 0
  val totalBurn = trends.sumOf { it.caloriesBurned }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(26.dp),
    colors = CardDefaults.cardColors(containerColor = VibrantLavenderContainer),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Health Consistency Score",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = VibrantLavenderOnContainer
          )
          Text(
            text = "Based on your daily meals, workouts & water",
            style = MaterialTheme.typography.bodySmall,
            color = VibrantLavenderOnContainer.copy(alpha = 0.75f)
          )
        }

        Surface(
          shape = RoundedCornerShape(50),
          color = Color.White
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("⚡", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "92/100",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.ExtraBold,
              color = VibrantPurpleDark
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        SummaryMiniCol(
          label = "Avg Daily Food",
          value = "$avgCalories kcal",
          color = VibrantPurpleDark,
          modifier = Modifier.weight(1f)
        )
        SummaryMiniCol(
          label = "Total Exercise",
          value = "$totalBurn kcal",
          color = VibrantRosePink,
          modifier = Modifier.weight(1f)
        )
        SummaryMiniCol(
          label = "Avg Daily Steps",
          value = "$avgSteps",
          color = VibrantSunsetOrange,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
private fun SummaryMiniCol(
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
      style = MaterialTheme.typography.titleSmall,
      fontWeight = FontWeight.ExtraBold,
      color = color
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      fontSize = 11.sp,
      color = VibrantLavenderOnContainer.copy(alpha = 0.75f)
    )
  }
}

@Composable
private fun InsightCardItem(insight: HealthInsight) {
  val (cardBg, accentColor) = when (insight.category) {
    "Nutrition" -> Pair(MaterialTheme.colorScheme.surface, VibrantMintDark)
    "Hydration" -> Pair(MaterialTheme.colorScheme.surface, VibrantSkyBlueAccent)
    "Activity" -> Pair(MaterialTheme.colorScheme.surface, VibrantRoseDark)
    else -> Pair(MaterialTheme.colorScheme.surface, VibrantSunsetOrange)
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = cardBg),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.Top
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(accentColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(20.dp)
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
            text = insight.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.ExtraBold
          )
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = accentColor.copy(alpha = 0.12f)
          ) {
            Text(
              text = insight.category,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
              style = MaterialTheme.typography.labelSmall,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = accentColor
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = insight.description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 18.sp
        )
      }
    }
  }
}
