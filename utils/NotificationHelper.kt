package com.wellnessflow.habbittracker.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.wellnessflow.habbittracker.MainActivity
import com.wellnessflow.habbittracker.R

/**
 * Helper class for managing notifications
 */
class NotificationHelper(private val context: Context) {
    
    companion object {
        const val CHANNEL_ID = "hydration_reminders"
        const val CHANNEL_NAME = "Hydration Reminders"
        const val NOTIFICATION_ID = 1001
    }
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to drink water throughout the day"
                enableVibration(true)
                enableLights(true)
            }
            
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun showHydrationReminder() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water_drop)
            .setContentTitle("Stay Hydrated! 💧")
            .setContentText("Time to drink some water and stay healthy!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 300, 100, 300))
            .build()
        
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    fun cancelHydrationReminders() {
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
