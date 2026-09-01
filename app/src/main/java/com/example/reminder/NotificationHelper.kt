package com.example.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {
  const val CHANNEL_ID_REMINDERS = "channel_nutripulse_reminders"
  const val CHANNEL_NAME_REMINDERS = "Healthy Living Reminders"

  fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID_REMINDERS,
        CHANNEL_NAME_REMINDERS,
        NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = "Reminders for meal times, water intake, workouts, and health tracking."
        enableVibration(true)
      }
      val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      manager.createNotificationChannel(channel)
    }
  }

  fun showNotification(
    context: Context,
    notificationId: Int,
    title: String,
    message: String
  ) {
    createNotificationChannel(context)

    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      0,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
      .setSmallIcon(android.R.drawable.ic_dialog_info)
      .setContentTitle(title)
      .setContentText(message)
      .setStyle(NotificationCompat.BigTextStyle().bigText(message))
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    try {
      manager.notify(notificationId, builder.build())
    } catch (e: SecurityException) {
      // Permission not granted on Android 13+
    }
  }
}
