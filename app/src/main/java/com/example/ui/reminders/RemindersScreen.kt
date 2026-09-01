package com.example.ui.reminders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReminderItem
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
fun RemindersScreen(
  reminders: List<ReminderItem>,
  onToggleReminder: (ReminderItem, Boolean) -> Unit,
  onUpdateReminderTime: (ReminderItem, Int, Int) -> Unit,
  onAddReminder: (title: String, message: String, hour: Int, minute: Int, category: String) -> Unit,
  onDeleteReminder: (ReminderItem) -> Unit,
  onTestNotification: (String, String) -> Unit
) {
  var showAddDialog by remember { mutableStateOf(false) }
  var editingReminderForTime by remember { mutableStateOf<ReminderItem?>(null) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .testTag("screen_reminders")
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
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
            text = "Smart Reminders",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
          )
          Text(
            text = "Stay on track with personalized notifications and hydration alerts",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Quick Test Banner
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(
            containerColor = VibrantLavenderContainer
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(RoundedCornerShape(14.dp))
                  .background(Color.White),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = VibrantPurplePrimary, modifier = Modifier.size(22.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text("Test Alerts", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = VibrantLavenderOnContainer)
                Text("Send an instant reminder notification", style = MaterialTheme.typography.bodySmall, color = VibrantLavenderOnContainer.copy(alpha = 0.75f))
              }
            }

            Button(
              onClick = {
                onTestNotification("💧 Hydration Check!", "Remember to drink a glass of fresh water to hit your goal.")
              },
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(containerColor = VibrantPurplePrimary),
              contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
              Text("Send Test", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
        Spacer(modifier = Modifier.height(18.dp))
      }

      // Reminders List
      items(reminders) { reminder ->
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
          ReminderRowCard(
            reminder = reminder,
            onToggle = { isChecked -> onToggleReminder(reminder, isChecked) },
            onEditTime = { editingReminderForTime = reminder },
            onDelete = { onDeleteReminder(reminder) }
          )
        }
      }
    }

    // FAB to Add Reminder
    FloatingActionButton(
      onClick = { showAddDialog = true },
      containerColor = VibrantPurplePrimary,
      contentColor = Color.White,
      shape = CircleShape,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 20.dp, bottom = 100.dp)
        .testTag("fab_add_reminder")
    ) {
      Icon(Icons.Default.Add, contentDescription = "Add Reminder")
    }

    // Add Reminder Dialog
    if (showAddDialog) {
      AddReminderDialog(
        onDismiss = { showAddDialog = false },
        onAdd = { title, msg, h, m, cat ->
          onAddReminder(title, msg, h, m, cat)
          showAddDialog = false
        }
      )
    }

    // Edit Time Picker Dialog
    editingReminderForTime?.let { rem ->
      TimePickerDialog(
        initialHour = rem.hour,
        initialMinute = rem.minute,
        onDismiss = { editingReminderForTime = null },
        onConfirm = { h, m ->
          onUpdateReminderTime(rem, h, m)
          editingReminderForTime = null
        }
      )
    }
  }
}

@Composable
private fun ReminderRowCard(
  reminder: ReminderItem,
  onToggle: (Boolean) -> Unit,
  onEditTime: () -> Unit,
  onDelete: () -> Unit
) {
  val icon = when (reminder.type.uppercase()) {
    "WATER" -> Icons.Default.Opacity
    "BREAKFAST", "LUNCH", "DINNER", "MEAL" -> Icons.Default.Restaurant
    "WORKOUT", "ACTIVITY" -> Icons.Default.DirectionsRun
    else -> Icons.Default.Notifications
  }
  val tint = when (reminder.type.uppercase()) {
    "WATER" -> VibrantSkyBlueAccent
    "BREAKFAST", "LUNCH", "DINNER", "MEAL" -> VibrantMintDark
    "WORKOUT", "ACTIVITY" -> VibrantRoseDark
    else -> VibrantSunsetOrange
  }

  val timeFormatted = String.format(java.util.Locale.getDefault(), "%02d:%02d", reminder.hour, reminder.minute)

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(tint.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = reminder.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
          )

          // Time pill (clickable to edit)
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .clickable { onEditTime() },
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                Icons.Default.AccessTime,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = timeFormatted,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = reminder.message,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 2
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Switch(
        checked = reminder.isEnabled,
        onCheckedChange = onToggle,
        colors = SwitchDefaults.colors(
          checkedThumbColor = Color.White,
          checkedTrackColor = VibrantPurplePrimary
        ),
        modifier = Modifier.testTag("switch_reminder_${reminder.id}")
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddReminderDialog(
  onDismiss: () -> Unit,
  onAdd: (title: String, message: String, hour: Int, minute: Int, category: String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var message by remember { mutableStateOf("") }
  var hour by remember { mutableStateOf("08") }
  var minute by remember { mutableStateOf("30") }
  var category by remember { mutableStateOf("Meal") }
  val categories = listOf("Meal", "Water", "Activity", "Habit")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Add Scheduled Reminder", fontWeight = FontWeight.Bold) },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Reminder Title") },
          placeholder = { Text("e.g. Afternoon Snack") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp)
        )

        OutlinedTextField(
          value = message,
          onValueChange = { message = it },
          label = { Text("Alert Message") },
          placeholder = { Text("e.g. Grab a handful of almonds and a drink!") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp)
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = hour,
            onValueChange = { hour = it.take(2).filter { c -> c.isDigit() } },
            label = { Text("Hour (0-23)") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp)
          )
          OutlinedTextField(
            value = minute,
            onValueChange = { minute = it.take(2).filter { c -> c.isDigit() } },
            label = { Text("Minute (0-59)") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp)
          )
        }

        Text("Category", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categories.forEach { cat ->
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (category == cat) VibrantPurplePrimary else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .weight(1f)
                .clickable { category = cat }
            ) {
              Text(
                text = cat,
                modifier = Modifier.padding(vertical = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (category == cat) Color.White else MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank()) {
            val h = hour.toIntOrNull()?.coerceIn(0, 23) ?: 8
            val m = minute.toIntOrNull()?.coerceIn(0, 59) ?: 0
            onAdd(title.trim(), message.trim(), h, m, category)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = VibrantPurplePrimary),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text("Save Reminder", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
  initialHour: Int,
  initialMinute: Int,
  onDismiss: () -> Unit,
  onConfirm: (hour: Int, minute: Int) -> Unit
) {
  val timeState = rememberTimePickerState(
    initialHour = initialHour,
    initialMinute = initialMinute,
    is24Hour = false
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Set Reminder Time", fontWeight = FontWeight.Bold) },
    text = {
      Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
      ) {
        TimePicker(state = timeState)
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onConfirm(timeState.hour, timeState.minute)
        },
        colors = ButtonDefaults.buttonColors(containerColor = VibrantPurplePrimary),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text("Set Time", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}
