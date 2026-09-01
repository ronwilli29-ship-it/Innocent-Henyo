package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_step_water")
data class DailyStepAndWater(
  @PrimaryKey val date: String, // "YYYY-MM-DD"
  val stepCount: Int = 0,
  val waterIntakeMl: Int = 0
)
