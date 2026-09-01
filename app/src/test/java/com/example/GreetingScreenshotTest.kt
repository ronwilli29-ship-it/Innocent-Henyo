package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.DailySummary
import com.example.ui.components.CalorieHeroCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testDashboardCard_screenshot() {
    val sampleSummary = DailySummary(
      date = "2026-08-31",
      caloriesConsumed = 1450,
      caloriesBurned = 380,
      netCalories = 1070,
      targetCalories = 2000,
      carbsGrams = 160,
      proteinGrams = 95,
      fatGrams = 48,
      targetCarbs = 225,
      targetProtein = 125,
      targetFat = 67,
      steps = 6400,
      targetSteps = 8500,
      waterMl = 1800,
      targetWaterMl = 2500,
      activeMinutes = 35,
      targetActiveMinutes = 45
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        CalorieHeroCard(
          summary = sampleSummary,
          onLogMealClick = {},
          onLogWorkoutClick = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/dashboard_hero.png")
  }
}
