package com.example

import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.UserProfile
import com.example.data.repository.MealPlanGenerator
import com.example.reminder.NotificationHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun testAppNameString() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("NutriPulse", appName)
  }

  @Test
  fun testMealPlanGenerator() {
    val date = LocalDate.now().toString()
    val meals = MealPlanGenerator.generateDailyPlan(
      date = date,
      dietPreference = "Balanced",
      targetCalories = 2000
    )

    assertEquals(4, meals.size)
    val types = meals.map { it.mealType }
    assertTrue(types.contains("Breakfast"))
    assertTrue(types.contains("Lunch"))
    assertTrue(types.contains("Dinner"))
    assertTrue(types.contains("Snack"))

    val totalCal = meals.sumOf { it.calories }
    assertTrue("Total calories should be close to 2000 kcal, got $totalCal", totalCal in 1700..2300)
  }

  @Test
  fun testUserProfileCalorieCalculations() {
    val profile = UserProfile(
      gender = "Female",
      currentWeightKg = 68f,
      heightCm = 170f,
      age = 28,
      goal = "Weight Loss",
      activityLevel = "Moderately Active"
    )

    val bmr = profile.calculateBmr()
    assertTrue("BMR should be positive", bmr > 1200f)

    val tdee = profile.calculateTdee()
    assertTrue("TDEE should be greater than BMR", tdee > bmr)

    val recommended = profile.calculateRecommendedCalories()
    assertTrue("Weight loss calories should be less than TDEE", recommended < tdee)
  }

  @Test
  fun testNotificationChannelCreation() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    NotificationHelper.createNotificationChannel(context)

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channel = manager.getNotificationChannel(NotificationHelper.CHANNEL_ID_REMINDERS)
    assertNotNull(channel)
    assertEquals(NotificationHelper.CHANNEL_NAME_REMINDERS, channel.name)
  }

  @Test
  fun testGeminiMealPlannerServiceFallback() {
    val service = com.example.data.gemini.GeminiMealPlannerService()
    val profile = UserProfile(
      goal = "Muscle Gain",
      dietPreference = "High-Protein",
      dailyCalorieTarget = 2400
    )
    val result = kotlinx.coroutines.runBlocking {
      service.suggestPersonalizedMealPlan(
        date = "2026-08-31",
        userProfile = profile,
        recentActivities = emptyList(),
        todayStepCount = 8500
      )
    }

    assertTrue(result.isSuccess)
    val plan = result.getOrNull()
    assertNotNull(plan)
    assertEquals(4, plan!!.meals.size)
    assertTrue(plan.rationale.isNotEmpty())
    assertTrue("Total calories should be positive and reasonable", plan.totalCalories > 1000)
  }
}

