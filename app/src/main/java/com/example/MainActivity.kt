package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.MealPlanItem
import com.example.reminder.NotificationHelper
import com.example.ui.MainViewModel
import com.example.ui.activity.ActivityScreen
import com.example.ui.analytics.AnalyticsScreen
import com.example.ui.components.AddMealDialog
import com.example.ui.components.LogActivityDialog
import com.example.ui.components.RecipeDetailSheet
import com.example.ui.components.SwapMealDialog
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.meals.MealPlannerScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.reminders.RemindersScreen
import com.example.ui.theme.VibrantLavenderContainer
import com.example.ui.theme.VibrantLavenderOnContainer
import com.example.ui.theme.VibrantPurpleDark
import com.example.ui.theme.VibrantPurplePrimary
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

enum class ScreenNav(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
  DASHBOARD("Today", Icons.Default.Dashboard),
  MEALS("Meals", Icons.Default.Restaurant),
  ACTIVITY("Activity", Icons.Default.DirectionsRun),
  ANALYTICS("Analytics", Icons.Default.Analytics),
  REMINDERS("Alerts", Icons.Default.Notifications),
  PROFILE("Profile", Icons.Default.Person)
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Create Notification Channel
    NotificationHelper.createNotificationChannel(this)

    setContent {
      MyApplicationTheme {
        MainAppRoot()
      }
    }
  }
}

@Composable
fun MainAppRoot(viewModel: MainViewModel = viewModel()) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  // Runtime permission launcher for notifications (Android 13+)
  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (!isGranted) {
      scope.launch {
        snackbarHostState.showSnackbar("Notifications disabled. Enable in Settings for meal & hydration alerts.")
      }
    }
  }

  LaunchedEffect(Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }
  }

  // ViewModel state observers
  val userProfile by viewModel.userProfile.collectAsState()
  val selectedDate by viewModel.selectedDate.collectAsState()
  val dailySummary by viewModel.dailySummary.collectAsState()
  val dayMeals by viewModel.dayMeals.collectAsState()
  val dayActivities by viewModel.dayActivities.collectAsState()
  val dayTrends by viewModel.dayTrends.collectAsState()
  val groceries by viewModel.groceries.collectAsState()
  val reminders by viewModel.reminders.collectAsState()
  val insights by viewModel.healthInsights.collectAsState()
  val achievements by viewModel.badgeAchievements.collectAsState()
  val analyticsDays by viewModel.analyticsDays.collectAsState()
  val aiMealPlanState by viewModel.aiMealPlanState.collectAsState()

  // Navigation State
  var currentScreen by remember { mutableStateOf(ScreenNav.DASHBOARD) }

  // Dialog & Sheet States
  var selectedMealForDetail by remember { mutableStateOf<MealPlanItem?>(null) }
  var selectedMealForSwap by remember { mutableStateOf<MealPlanItem?>(null) }
  var showAddMealDialog by remember { mutableStateOf(false) }
  var showLogActivityDialog by remember { mutableStateOf(false) }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = Modifier.testTag("main_bottom_nav")
      ) {
        val navItems = listOf(
          ScreenNav.DASHBOARD,
          ScreenNav.MEALS,
          ScreenNav.ACTIVITY,
          ScreenNav.ANALYTICS,
          ScreenNav.REMINDERS
        )

        navItems.forEach { screen ->
          val isSelected = currentScreen == screen
          NavigationBarItem(
            selected = isSelected,
            onClick = { currentScreen = screen },
            icon = {
              Icon(
                screen.icon,
                contentDescription = screen.title,
                modifier = Modifier.size(22.dp)
              )
            },
            label = { Text(screen.title) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = VibrantPurpleDark,
              selectedTextColor = VibrantPurpleDark,
              indicatorColor = VibrantLavenderContainer,
              unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
              unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            ),
            modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentScreen) {
        ScreenNav.DASHBOARD -> {
          DashboardScreen(
            userProfile = userProfile,
            summary = dailySummary,
            selectedDate = selectedDate,
            meals = dayMeals,
            activities = dayActivities,
            insights = insights,
            onDateSelected = { viewModel.setSelectedDate(it) },
            onLogMealClick = { showAddMealDialog = true },
            onLogWorkoutClick = { showLogActivityDialog = true },
            onAddWater = { viewModel.addWater(it) },
            onAddSteps = { viewModel.addSteps(it) },
            onToggleMealLogged = { viewModel.toggleMealLogged(it) },
            onMealClick = { selectedMealForDetail = it },
            onProfileClick = { currentScreen = ScreenNav.PROFILE },
            onNavigateToMeals = { currentScreen = ScreenNav.MEALS },
            onNavigateToActivity = { currentScreen = ScreenNav.ACTIVITY }
          )
        }

        ScreenNav.MEALS -> {
          MealPlannerScreen(
            userProfile = userProfile,
            selectedDate = selectedDate,
            meals = dayMeals,
            groceries = groceries,
            aiMealPlanState = aiMealPlanState,
            onDateSelected = { viewModel.setSelectedDate(it) },
            onToggleMealLogged = { viewModel.toggleMealLogged(it) },
            onMealClick = { selectedMealForDetail = it },
            onSwapMealClick = { selectedMealForSwap = it },
            onDeleteMealClick = { viewModel.deleteMeal(it) },
            onRegeneratePlan = {
              viewModel.regenerateDayMealPlan(selectedDate)
              scope.launch { snackbarHostState.showSnackbar("Generated fresh meals for $selectedDate!") }
            },
            onGenerateAiPlan = {
              viewModel.generateAiMealPlan(selectedDate)
            },
            onApplyAiPlan = { plan ->
              viewModel.applyAiMealPlan(plan)
              scope.launch { snackbarHostState.showSnackbar("Personalized AI meal plan applied & groceries updated!") }
            },
            onDismissAiPlan = {
              viewModel.clearAiMealPlanState()
            },
            onOpenAddMealDialog = { showAddMealDialog = true },
            onToggleGrocery = { viewModel.toggleGroceryItem(it) },
            onAddGrocery = { name, cat -> viewModel.addGroceryItem(name, cat) },
            onDeleteGrocery = { viewModel.deleteGroceryItem(it) },
            onClearCheckedGroceries = { viewModel.clearCheckedGroceries() },
            onGenerateWeeklyGroceries = {
              viewModel.generateWeeklyGroceries()
              scope.launch { snackbarHostState.showSnackbar("Grocery list updated from your meals!") }
            }
          )
        }

        ScreenNav.ACTIVITY -> {
          ActivityScreen(
            userProfile = userProfile,
            summary = dailySummary,
            selectedDate = selectedDate,
            activities = dayActivities,
            onDateSelected = { viewModel.setSelectedDate(it) },
            onOpenLogActivityDialog = { showLogActivityDialog = true },
            onDeleteActivity = { viewModel.deleteActivity(it) },
            onQuickLogPreset = { type, mins, burn, intensity ->
              viewModel.logActivity(type, mins, burn, intensity, null, "Quick log")
              scope.launch { snackbarHostState.showSnackbar("Logged $type ($burn kcal burned)!") }
            },
            onAddSteps = { viewModel.addSteps(it) }
          )
        }

        ScreenNav.ANALYTICS -> {
          AnalyticsScreen(
            userProfile = userProfile,
            dailySummary = dailySummary,
            trends = dayTrends,
            insights = insights,
            achievements = achievements,
            analyticsDays = analyticsDays,
            onDaysRangeChanged = { viewModel.setAnalyticsRangeDays(it) }
          )
        }

        ScreenNav.REMINDERS -> {
          RemindersScreen(
            reminders = reminders,
            onToggleReminder = { rem, enabled ->
              viewModel.toggleReminder(rem, enabled)
            },
            onUpdateReminderTime = { rem, h, m ->
              viewModel.updateReminderTime(rem, h, m)
            },
            onAddReminder = { title, msg, h, m, cat ->
              viewModel.addReminder(title, msg, h, m, cat)
            },
            onDeleteReminder = { viewModel.deleteReminder(it) },
            onTestNotification = { title, msg ->
              NotificationHelper.showNotification(
                context = context,
                notificationId = (System.currentTimeMillis() % 10000).toInt(),
                title = title,
                message = msg
              )
              scope.launch { snackbarHostState.showSnackbar("Test alert notification fired!") }
            }
          )
        }

        ScreenNav.PROFILE -> {
          ProfileScreen(
            userProfile = userProfile,
            onSaveProfile = { updated ->
              viewModel.updateProfile(updated)
              scope.launch { snackbarHostState.showSnackbar("Profile and nutrition targets updated!") }
            }
          )
        }
      }
    }
  }

  // Recipe Detail Modal Bottom Sheet
  selectedMealForDetail?.let { meal ->
    RecipeDetailSheet(
      meal = meal,
      onDismiss = { selectedMealForDetail = null },
      onToggleLogged = { item ->
        viewModel.toggleMealLogged(item)
      },
      onSwapClick = { item ->
        selectedMealForDetail = null
        selectedMealForSwap = item
      }
    )
  }

  // Swap Meal Dialog
  selectedMealForSwap?.let { meal ->
    SwapMealDialog(
      meal = meal,
      dietPreference = userProfile.dietPreference,
      onDismiss = { selectedMealForSwap = null },
      onSelectAlternative = { newPreset ->
        viewModel.swapMealWithPreset(meal, newPreset)
        scope.launch { snackbarHostState.showSnackbar("Swapped to ${newPreset.name}!") }
      }
    )
  }

  // Add Custom Meal Dialog
  if (showAddMealDialog) {
    AddMealDialog(
      initialDate = selectedDate,
      onDismiss = { showAddMealDialog = false },
      onAddMeal = { mealType, name, cal, carbs, protein, fat, prep, ingredients, instructions, dietCategory ->
        viewModel.addCustomMeal(
          mealType = mealType,
          name = name,
          calories = cal,
          carbs = carbs,
          protein = protein,
          fat = fat,
          prepTimeMinutes = prep,
          ingredients = ingredients,
          instructions = instructions,
          dietCategory = dietCategory
        )
        scope.launch { snackbarHostState.showSnackbar("Logged $name ($cal kcal)!") }
      }
    )
  }

  // Log Activity Dialog
  if (showLogActivityDialog) {
    LogActivityDialog(
      userWeightKg = userProfile.currentWeightKg,
      onDismiss = { showLogActivityDialog = false },
      onLogActivity = { type, duration, calories, intensity, dist, notes ->
        viewModel.logActivity(type, duration, calories, intensity, dist, notes)
        scope.launch { snackbarHostState.showSnackbar("Logged $type ($calories kcal burned)!") }
      }
    )
  }
}
