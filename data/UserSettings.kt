package com.wellnessflow.habbittracker.data

/**
 * Data class representing user settings and preferences
 * @param hydrationReminderEnabled Whether hydration reminders are enabled
 * @param reminderIntervalMinutes Interval between hydration reminders in minutes
 * @param reminderStartTime Start time for reminders (HH:mm format)
 * @param reminderEndTime End time for reminders (HH:mm format)
 * @param theme App theme preference ("light" or "dark")
 * @param notificationsEnabled Whether notifications are enabled
 * @param vibrationEnabled Whether vibration is enabled for notifications
 * @param soundEnabled Whether sound is enabled for notifications
 * @param firstLaunch Whether this is the first app launch
 * @param lastDataResetDate Last date when daily data was reset (YYYY-MM-DD format)
 */
data class UserSettings(
    val hydrationReminderEnabled: Boolean = true,
    val reminderIntervalMinutes: Int = 120, // 2 hours default
    val reminderStartTime: String = "08:00",
    val reminderEndTime: String = "22:00",
    val theme: String = "light",
    val notificationsEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val firstLaunch: Boolean = true,
    val lastDataResetDate: String = "",
    val dailyResetEnabled: Boolean = true,
    val weekStartDay: Int = 1, // Monday = 1, Sunday = 7
    val language: String = "en"
) {
    /**
     * Get reminder interval in hours
     */
    fun getReminderIntervalHours(): Float {
        return reminderIntervalMinutes / 60f
    }
    
    /**
     * Check if reminders should be active at current time
     */
    fun shouldShowReminderAtTime(currentTime: String): Boolean {
        if (!hydrationReminderEnabled) return false
        
        val current = timeToMinutes(currentTime)
        val start = timeToMinutes(reminderStartTime)
        val end = timeToMinutes(reminderEndTime)
        
        return current in start..end
    }
    
    /**
     * Convert time string (HH:mm) to minutes since midnight
     */
    private fun timeToMinutes(timeString: String): Int {
        val parts = timeString.split(":")
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()
        return hours * 60 + minutes
    }
    
    /**
     * Get next reminder time based on current time
     */
    fun getNextReminderTime(currentTime: String): String {
        val currentMinutes = timeToMinutes(currentTime)
        val nextMinutes = currentMinutes + reminderIntervalMinutes
        
        val hours = (nextMinutes / 60) % 24
        val minutes = nextMinutes % 60
        
        return String.format("%02d:%02d", hours, minutes)
    }
    
    /**
     * Validate settings
     */
    fun isValid(): Boolean {
        return reminderIntervalMinutes > 0 &&
                reminderIntervalMinutes <= 1440 && // Max 24 hours
                isValidTimeFormat(reminderStartTime) &&
                isValidTimeFormat(reminderEndTime) &&
                theme in listOf("light", "dark", "system")
    }
    
    /**
     * Check if time format is valid (HH:mm)
     */
    private fun isValidTimeFormat(timeString: String): Boolean {
        return try {
            val parts = timeString.split(":")
            if (parts.size != 2) return false
            
            val hours = parts[0].toInt()
            val minutes = parts[1].toInt()
            
            hours in 0..23 && minutes in 0..59
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Default user settings for WellnessFlow app
 */
object DefaultUserSettings {
    fun getDefaultSettings(): UserSettings {
        return UserSettings()
    }
    
    /**
     * Get minimum reminder interval (15 minutes)
     */
    const val MIN_REMINDER_INTERVAL = 15
    
    /**
     * Get maximum reminder interval (4 hours)
     */
    const val MAX_REMINDER_INTERVAL = 240
    
    /**
     * Get default reminder intervals in minutes
     */
    fun getReminderIntervalOptions(): List<Int> {
        return listOf(15, 30, 60, 90, 120, 180, 240)
    }
    
    /**
     * Get reminder interval display strings
     */
    fun getReminderIntervalDisplayStrings(): List<String> {
        return getReminderIntervalOptions().map { minutes ->
            when {
                minutes < 60 -> "${minutes}m"
                minutes == 60 -> "1h"
                minutes % 60 == 0 -> "${minutes / 60}h"
                else -> "${minutes / 60}h ${minutes % 60}m"
            }
        }
    }
}
