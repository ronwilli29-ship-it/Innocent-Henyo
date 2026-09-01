package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ActivityLog
import com.example.data.model.DailyStepAndWater
import com.example.data.model.GroceryItem
import com.example.data.model.MealPlanItem
import com.example.data.model.ReminderItem
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

  // User Profile
  @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
  fun getUserProfileFlow(): Flow<UserProfile?>

  @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
  suspend fun getUserProfileOnce(): UserProfile?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateProfile(profile: UserProfile)

  // Meal Plan Items
  @Query("SELECT * FROM meal_plan_items WHERE date = :date ORDER BY id ASC")
  fun getMealsForDate(date: String): Flow<List<MealPlanItem>>

  @Query("SELECT * FROM meal_plan_items WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC, id ASC")
  fun getMealsForDateRange(startDate: String, endDate: String): Flow<List<MealPlanItem>>

  @Query("SELECT * FROM meal_plan_items ORDER BY date DESC")
  fun getAllMeals(): Flow<List<MealPlanItem>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMeal(meal: MealPlanItem): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMeals(meals: List<MealPlanItem>)

  @Update
  suspend fun updateMeal(meal: MealPlanItem)

  @Delete
  suspend fun deleteMeal(meal: MealPlanItem)

  @Query("DELETE FROM meal_plan_items WHERE date = :date")
  suspend fun deleteMealsForDate(date: String)

  @Query("UPDATE meal_plan_items SET isLogged = :isLogged WHERE id = :id")
  suspend fun updateMealLoggedStatus(id: Long, isLogged: Boolean)

  // Activity Logs
  @Query("SELECT * FROM activity_logs WHERE date = :date ORDER BY timestamp DESC")
  fun getActivitiesForDate(date: String): Flow<List<ActivityLog>>

  @Query("SELECT * FROM activity_logs WHERE date = :date ORDER BY timestamp DESC")
  suspend fun getActivitiesForDateOnce(date: String): List<ActivityLog>

  @Query("SELECT * FROM activity_logs WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
  fun getActivitiesForDateRange(startDate: String, endDate: String): Flow<List<ActivityLog>>

  @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC")
  fun getAllActivities(): Flow<List<ActivityLog>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertActivity(activity: ActivityLog): Long

  @Delete
  suspend fun deleteActivity(activity: ActivityLog)

  // Step and Water
  @Query("SELECT * FROM daily_step_water WHERE date = :date LIMIT 1")
  fun getStepAndWaterForDate(date: String): Flow<DailyStepAndWater?>

  @Query("SELECT * FROM daily_step_water WHERE date = :date LIMIT 1")
  suspend fun getStepAndWaterOnce(date: String): DailyStepAndWater?

  @Query("SELECT * FROM daily_step_water WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
  fun getStepAndWaterForDateRange(startDate: String, endDate: String): Flow<List<DailyStepAndWater>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateStepAndWater(item: DailyStepAndWater)

  // Reminders
  @Query("SELECT * FROM reminders ORDER BY hour ASC, minute ASC")
  fun getAllReminders(): Flow<List<ReminderItem>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertReminder(reminder: ReminderItem): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertReminders(reminders: List<ReminderItem>)

  @Update
  suspend fun updateReminder(reminder: ReminderItem)

  @Delete
  suspend fun deleteReminder(reminder: ReminderItem)

  // Grocery Items
  @Query("SELECT * FROM grocery_items ORDER BY isChecked ASC, category ASC, name ASC")
  fun getAllGroceryItems(): Flow<List<GroceryItem>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGroceryItem(item: GroceryItem): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGroceryItems(items: List<GroceryItem>)

  @Update
  suspend fun updateGroceryItem(item: GroceryItem)

  @Delete
  suspend fun deleteGroceryItem(item: GroceryItem)

  @Query("DELETE FROM grocery_items WHERE isChecked = 1")
  suspend fun clearCheckedGroceryItems()

  @Query("DELETE FROM grocery_items")
  suspend fun clearAllGroceryItems()
}
