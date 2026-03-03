package com.wellnessflow.habbittracker.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.wellnessflow.habbittracker.data.UserSettings
import com.wellnessflow.habbittracker.receivers.HydrationReminderReceiver
import java.util.Calendar

/**
 * Helper class for managing hydration reminder alarms
 */
class AlarmManagerHelper(private val context: Context) {
    
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    /**
     * Schedule hydration reminders based on user settings
     */
    fun scheduleHydrationReminders(settings: UserSettings) {
        if (!settings.hydrationReminderEnabled) {
            cancelHydrationReminders()
            return
        }
        
        val intervalMillis = settings.reminderIntervalMinutes * 60 * 1000L
        val startTime = parseTimeString(settings.reminderStartTime)
        val endTime = parseTimeString(settings.reminderEndTime)
        
        // Calculate first reminder time
        val firstReminderTime = calculateFirstReminderTime(startTime, intervalMillis)
        
        // Schedule repeating alarm
        val intent = Intent(context, HydrationReminderReceiver::class.java).apply {
            action = HydrationReminderReceiver.ACTION_HYDRATION_REMINDER
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Schedule the alarm
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            firstReminderTime,
            intervalMillis,
            pendingIntent
        )
    }
    
    /**
     * Cancel all hydration reminder alarms
     */
    fun cancelHydrationReminders() {
        val intent = Intent(context, HydrationReminderReceiver::class.java).apply {
            action = HydrationReminderReceiver.ACTION_HYDRATION_REMINDER
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
    }
    
    /**
     * Parse time string (HH:mm) to Calendar time
     */
    private fun parseTimeString(timeString: String): Calendar {
        val parts = timeString.split(":")
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()
        
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hours)
        calendar.set(Calendar.MINUTE, minutes)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        return calendar
    }
    
    /**
     * Calculate the first reminder time
     */
    private fun calculateFirstReminderTime(startTime: Calendar, intervalMillis: Long): Long {
        val now = Calendar.getInstance()
        val todayStartTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, startTime.get(Calendar.MINUTE))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        // If start time has passed today, schedule for tomorrow
        if (todayStartTime.timeInMillis <= now.timeInMillis) {
            todayStartTime.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        return todayStartTime.timeInMillis
    }
    
    /**
     * Check if reminders are currently scheduled
     */
    fun areRemindersScheduled(): Boolean {
        val intent = Intent(context, HydrationReminderReceiver::class.java).apply {
            action = HydrationReminderReceiver.ACTION_HYDRATION_REMINDER
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        
        return pendingIntent != null
    }
}
