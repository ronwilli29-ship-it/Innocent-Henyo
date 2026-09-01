package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grocery_items")
data class GroceryItem(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val amount: String = "1 portion",
  val category: String = "Fresh Produce", // "Fresh Produce", "Proteins & Dairy", "Pantry & Grains", "Seasoning & Oils"
  val isChecked: Boolean = false
)
