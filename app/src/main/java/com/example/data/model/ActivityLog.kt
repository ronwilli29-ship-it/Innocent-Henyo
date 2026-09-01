package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_logs")
data class ActivityLog(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val date: String, // "YYYY-MM-DD"
  val activityType: String, // "Running", "Brisk Walking", "Cycling", "Strength Training", "HIIT", "Yoga", "Swimming", "Pilates"
  val durationMinutes: Int,
  val caloriesBurned: Int,
  val intensity: String = "Moderate", // "Low", "Moderate", "High"
  val distanceKm: Float? = null,
  val timestamp: Long = System.currentTimeMillis(),
  val notes: String = ""
)
