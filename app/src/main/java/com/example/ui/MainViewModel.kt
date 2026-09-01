package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.gemini.AiMealPlanResult
import com.example.data.local.AppDatabase
import com.example.data.model.ActivityLog
import com.example.data.model.BadgeAchievement
import com.example.data.model.DailyStepAndWater
import com.example.data.model.DailySummary
import com.example.data.model.DayTrend
import com.example.data.model.GroceryItem
import com.example.data.model.HealthInsight
import com.example.data.model.MealPlanItem
import com.example.data.model.RecipePreset
import com.example.data.model.ReminderItem
import com.example.data.model.UserProfile
import com.example.data.repository.HealthRepository
import com.example.reminder.ReminderScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

sealed interface AiMealPlanUiState {
  object Idle : AiMealPlanUiState
  object Generating : AiMealPlanUiState
  data class Success(val planResult: AiMealPlanResult) : AiMealPlanUiState
  data class Error(val message: String) : AiMealPlanUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: HealthRepository

  private val _selectedDate = MutableStateFlow(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
  val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

  private val _analyticsDays = MutableStateFlow(7)
  val analyticsDays: StateFlow<Int> = _analyticsDays.asStateFlow()

  private val _aiMealPlanState = MutableStateFlow<AiMealPlanUiState>(AiMealPlanUiState.Idle)
  val aiMealPlanState: StateFlow<AiMealPlanUiState> = _aiMealPlanState.asStateFlow()

  val userProfile: StateFlow<UserProfile>
  val dayMeals: StateFlow<List<MealPlanItem>>
  val dayActivities: StateFlow<List<ActivityLog>>
  val stepAndWaterForSelectedDate: StateFlow<DailyStepAndWater?>
  val dailySummary: StateFlow<DailySummary>
  val dayTrends: StateFlow<List<DayTrend>>
  val reminders: StateFlow<List<ReminderItem>>
  val groceries: StateFlow<List<GroceryItem>>
  val healthInsights: StateFlow<List<HealthInsight>>
  val badgeAchievements: StateFlow<List<BadgeAchievement>>

  init {
    val database = AppDatabase.getDatabase(application)
    repository = HealthRepository(database.appDao(), application)

    viewModelScope.launch {
      repository.initializeSeedDataIfNeeded()
    }

    userProfile = repository.userProfileFlow
      .filterNotNull()
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserProfile()
      )

    dayMeals = _selectedDate.flatMapLatest { date ->
      repository.getMealsForDate(date)
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

    dayActivities = _selectedDate.flatMapLatest { date ->
      repository.getActivitiesForDate(date)
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

    stepAndWaterForSelectedDate = _selectedDate.flatMapLatest { date ->
      repository.getStepAndWaterForDate(date)
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = null
    )

    dailySummary = _selectedDate.flatMapLatest { date ->
      repository.getDailySummaryFlow(date)
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = DailySummary(
        date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
        caloriesConsumed = 0,
        caloriesBurned = 0,
        netCalories = 0,
        targetCalories = 2000,
        carbsGrams = 0,
        proteinGrams = 0,
        fatGrams = 0,
        targetCarbs = 225,
        targetProtein = 125,
        targetFat = 67,
        steps = 0,
        targetSteps = 8500,
        waterMl = 0,
        targetWaterMl = 2500,
        activeMinutes = 0,
        targetActiveMinutes = 45
      )
    )

    dayTrends = _analyticsDays.flatMapLatest { days ->
      repository.getTrendDataFlow(days)
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

    reminders = repository.allRemindersFlow
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
      )

    groceries = repository.groceryItemsFlow
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
      )

    healthInsights = combine(dailySummary, userProfile, dayTrends) { sum, prof, trends ->
      repository.generateInsights(sum, prof, trends)
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

    badgeAchievements = combine(dailySummary, userProfile, dayTrends) { sum, prof, trends ->
      repository.getAchievements(prof, sum, trends)
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )
  }

  fun setSelectedDate(date: String) {
    _selectedDate.value = date
  }

  fun setAnalyticsRangeDays(days: Int) {
    _analyticsDays.value = days
  }

  // Meal Operations
  fun toggleMealLogged(meal: MealPlanItem) {
    viewModelScope.launch {
      repository.toggleMealLogged(meal.id, !meal.isLogged)
    }
  }

  fun swapMealWithPreset(meal: MealPlanItem, preset: RecipePreset) {
    viewModelScope.launch {
      repository.swapMealWithPreset(meal, preset)
    }
  }

  fun regenerateDayMealPlan(date: String) {
    viewModelScope.launch {
      val profile = userProfile.value
      repository.regenerateDayMealPlan(date, profile.dietPreference, profile.dailyCalorieTarget)
    }
  }

  fun generateAiMealPlan(date: String = _selectedDate.value) {
    viewModelScope.launch {
      _aiMealPlanState.value = AiMealPlanUiState.Generating
      val result = repository.generateGeminiMealPlan(date)
      result.fold(
        onSuccess = { plan ->
          _aiMealPlanState.value = AiMealPlanUiState.Success(plan)
        },
        onFailure = { error ->
          _aiMealPlanState.value = AiMealPlanUiState.Error(error.localizedMessage ?: "Failed to generate meal plan")
        }
      )
    }
  }

  fun applyAiMealPlan(plan: AiMealPlanResult) {
    viewModelScope.launch {
      repository.applyMealPlan(
        date = plan.date,
        meals = plan.meals,
        autoUpdateGroceries = true
      )
      _aiMealPlanState.value = AiMealPlanUiState.Idle
    }
  }

  fun clearAiMealPlanState() {
    _aiMealPlanState.value = AiMealPlanUiState.Idle
  }

  fun addCustomMeal(
    mealType: String,
    name: String,
    calories: Int,
    carbs: Int,
    protein: Int,
    fat: Int,
    prepTimeMinutes: Int,
    ingredients: String,
    instructions: String,
    dietCategory: String
  ) {
    viewModelScope.launch {
      val item = MealPlanItem(
        date = _selectedDate.value,
        mealType = mealType,
        name = name,
        calories = calories,
        carbs = carbs,
        protein = protein,
        fat = fat,
        prepTimeMinutes = prepTimeMinutes,
        ingredients = ingredients,
        instructions = instructions,
        dietCategory = dietCategory,
        isLogged = true
      )
      repository.addMeal(item)
    }
  }

  fun deleteMeal(meal: MealPlanItem) {
    viewModelScope.launch {
      repository.deleteMeal(meal)
    }
  }

  // Activity Operations
  fun logActivity(
    type: String,
    durationMinutes: Int,
    caloriesBurned: Int,
    intensity: String,
    distanceKm: Float?,
    notes: String
  ) {
    viewModelScope.launch {
      val log = ActivityLog(
        date = _selectedDate.value,
        activityType = type,
        durationMinutes = durationMinutes,
        caloriesBurned = caloriesBurned,
        intensity = intensity,
        distanceKm = distanceKm,
        notes = notes
      )
      repository.logActivity(log)
    }
  }

  fun deleteActivity(activity: ActivityLog) {
    viewModelScope.launch {
      repository.deleteActivity(activity)
    }
  }

  // Water & Steps
  fun addWater(amountMl: Int) {
    viewModelScope.launch {
      repository.addWater(_selectedDate.value, amountMl)
    }
  }

  fun addSteps(count: Int) {
    viewModelScope.launch {
      repository.addSteps(_selectedDate.value, count)
    }
  }

  // Profile operations
  fun updateProfile(profile: UserProfile) {
    viewModelScope.launch {
      repository.updateProfile(profile)
    }
  }

  // Reminders
  fun toggleReminder(reminder: ReminderItem, enabled: Boolean) {
    viewModelScope.launch {
      val updated = reminder.copy(isEnabled = enabled)
      repository.updateReminder(updated)
      if (updated.isEnabled) {
        ReminderScheduler.scheduleReminder(
          getApplication(),
          updated.id,
          updated.title,
          updated.message,
          updated.hour,
          updated.minute
        )
      } else {
        ReminderScheduler.cancelReminder(getApplication(), updated.id)
      }
    }
  }

  fun updateReminderTime(reminder: ReminderItem, hour: Int, minute: Int) {
    viewModelScope.launch {
      val updated = reminder.copy(hour = hour, minute = minute)
      repository.updateReminder(updated)
      if (updated.isEnabled) {
        ReminderScheduler.scheduleReminder(
          getApplication(),
          updated.id,
          updated.title,
          updated.message,
          hour,
          minute
        )
      }
    }
  }

  fun addReminder(
    title: String,
    message: String,
    hour: Int,
    minute: Int,
    type: String
  ) {
    viewModelScope.launch {
      val item = ReminderItem(
        type = type,
        title = title,
        message = message,
        hour = hour,
        minute = minute,
        isEnabled = true
      )
      val id = repository.addReminder(item)
      ReminderScheduler.scheduleReminder(
        getApplication(),
        id.toInt(),
        title,
        message,
        hour,
        minute
      )
    }
  }

  fun deleteReminder(reminder: ReminderItem) {
    viewModelScope.launch {
      ReminderScheduler.cancelReminder(getApplication(), reminder.id)
      repository.deleteReminder(reminder)
    }
  }

  // Groceries
  fun toggleGroceryItem(item: GroceryItem) {
    viewModelScope.launch {
      repository.toggleGroceryItem(item)
    }
  }

  fun addGroceryItem(name: String, category: String) {
    viewModelScope.launch {
      repository.addGroceryItem(name, category)
    }
  }

  fun deleteGroceryItem(item: GroceryItem) {
    viewModelScope.launch {
      repository.deleteGroceryItem(item)
    }
  }

  fun clearCheckedGroceries() {
    viewModelScope.launch {
      repository.clearCheckedGroceries()
    }
  }

  fun generateWeeklyGroceries() {
    viewModelScope.launch {
      val today = LocalDate.now()
      val start = today.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
      val end = today.plusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE)
      repository.generateGroceriesFromDateRange(start, end)
    }
  }
}
