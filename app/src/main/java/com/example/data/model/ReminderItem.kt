package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderItem(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val type: String, // "BREAKFAST", "LUNCH", "DINNER", "WATER", "WORKOUT", "EVENING_REVIEW"
  val title: String,
  val message: String,
  val hour: Int, // 0-23
  val minute: Int, // 0-59
  val isEnabled: Boolean = true,
  val repeatDays: String = "Everyday" // "Everyday", "Weekdays", "Weekends"
)
