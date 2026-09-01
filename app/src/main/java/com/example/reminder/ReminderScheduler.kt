package com.example.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.Calendar

class ReminderBroadcastReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    val id = intent.getIntExtra("REMINDER_ID", 100)
    val title = intent.getStringExtra("REMINDER_TITLE") ?: "NutriPulse Health Reminder"
    val message = intent.getStringExtra("REMINDER_MESSAGE") ?: "Time for your daily health check-in!"

    NotificationHelper.showNotification(context, id, title, message)
  }
}

object ReminderScheduler {

  fun scheduleReminder(
    context: Context,
    reminderId: Int,
    title: String,
    message: String,
    hour: Int,
    minute: Int
  ) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

    val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
      putExtra("REMINDER_ID", reminderId)
      putExtra("REMINDER_TITLE", title)
      putExtra("REMINDER_MESSAGE", message)
    }

    val pendingIntent = PendingIntent.getBroadcast(
      context,
      reminderId,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val calendar = Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, hour)
      set(Calendar.MINUTE, minute)
      set(Calendar.SECOND, 0)
      if (before(Calendar.getInstance())) {
        add(Calendar.DAY_OF_YEAR, 1)
      }
    }

    try {
      alarmManager.setInexactRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        pendingIntent
      )
    } catch (e: Exception) {
      // Fallback
    }
  }

  fun cancelReminder(context: Context, reminderId: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
    val intent = Intent(context, ReminderBroadcastReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
      context,
      reminderId,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
  }
}
