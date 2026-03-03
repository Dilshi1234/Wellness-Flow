package com.wellnessflow.habbittracker.ui.hydration

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wellnessflow.habbittracker.data.DataManager
import java.util.*

data class HydrationReminderSettings(
    val enabled: Boolean,
    val intervalValue: Int,
    val intervalUnit: String,
    val startTime: Calendar,
    val endTime: Calendar
)

class HydrationReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val dataManager = DataManager(application)
    
    private val _waterIntake = MutableLiveData<Int>()
    val waterIntake: LiveData<Int> = _waterIntake
    
    private val _nextReminderTime = MutableLiveData<Calendar>()
    val nextReminderTime: LiveData<Calendar> = _nextReminderTime
    
    private val _remindersEnabled = MutableLiveData<Boolean>()
    val remindersEnabled: LiveData<Boolean> = _remindersEnabled
    
    init {
        loadWaterIntake()
        loadReminderSettings()
        calculateNextReminder()
    }
    
    fun addGlass() {
        val current = _waterIntake.value ?: 0
        val newValue = (current + 1).coerceAtMost(20) // Max 20 glasses
        _waterIntake.value = newValue
        saveWaterIntake(newValue)
    }
    
    fun subtractGlass() {
        val current = _waterIntake.value ?: 0
        val newValue = (current - 1).coerceAtLeast(0) // Min 0 glasses
        _waterIntake.value = newValue
        saveWaterIntake(newValue)
    }
    
    fun setRemindersEnabled(enabled: Boolean) {
        _remindersEnabled.value = enabled
    }
    
    fun setStartTime(time: Calendar) {
        calculateNextReminder()
    }
    
    fun setEndTime(time: Calendar) {
        calculateNextReminder()
    }
    
    fun saveReminderSettings(settings: HydrationReminderSettings) {
        // Save to SharedPreferences or database
        val prefs = getApplication<Application>().getSharedPreferences("hydration_reminders", 0)
        prefs.edit().apply {
            putBoolean("enabled", settings.enabled)
            putInt("interval_value", settings.intervalValue)
            putString("interval_unit", settings.intervalUnit)
            putLong("start_time", settings.startTime.timeInMillis)
            putLong("end_time", settings.endTime.timeInMillis)
            apply()
        }
        
        _remindersEnabled.value = settings.enabled
        calculateNextReminder()
    }
    
    fun getReminderSettings(): HydrationReminderSettings {
        val prefs = getApplication<Application>().getSharedPreferences("hydration_reminders", 0)
        
        val enabled = prefs.getBoolean("enabled", false)
        val intervalValue = prefs.getInt("interval_value", 1)
        val intervalUnit = prefs.getString("interval_unit", "hours") ?: "hours"
        
        val startTime = Calendar.getInstance().apply {
            timeInMillis = prefs.getLong("start_time", System.currentTimeMillis())
        }
        
        val endTime = Calendar.getInstance().apply {
            timeInMillis = prefs.getLong("end_time", System.currentTimeMillis() + 12 * 60 * 60 * 1000L) // 12 hours from now
        }
        
        return HydrationReminderSettings(
            enabled = enabled,
            intervalValue = intervalValue,
            intervalUnit = intervalUnit,
            startTime = startTime,
            endTime = endTime
        )
    }
    
    private fun loadWaterIntake() {
        val prefs = getApplication<Application>().getSharedPreferences("hydration_reminders", 0)
        val intake = prefs.getInt("water_intake", 0)
        _waterIntake.value = intake
    }
    
    private fun saveWaterIntake(intake: Int) {
        val prefs = getApplication<Application>().getSharedPreferences("hydration_reminders", 0)
        prefs.edit().putInt("water_intake", intake).apply()
    }
    
    private fun loadReminderSettings() {
        val settings = getReminderSettings()
        _remindersEnabled.value = settings.enabled
    }
    
    private fun calculateNextReminder() {
        val settings = getReminderSettings()
        if (!settings.enabled) {
            _nextReminderTime.value = Calendar.getInstance()
            return
        }
        
        val now = Calendar.getInstance()
        val nextReminder = Calendar.getInstance()
        
        // Calculate interval in milliseconds
        val intervalMs = when (settings.intervalUnit) {
            "minutes" -> settings.intervalValue * 60 * 1000L
            "hours" -> settings.intervalValue * 60 * 60 * 1000L
            else -> 1 * 60 * 60 * 1000L // Default 1 hour
        }
        
        // Find the next reminder time based on current time
        // Start from the most recent reminder time that has passed
        val startOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, settings.startTime.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, settings.startTime.get(Calendar.MINUTE))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val endOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, settings.endTime.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, settings.endTime.get(Calendar.MINUTE))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        // If we're before the start time today, next reminder is at start time
        if (now.timeInMillis < startOfToday.timeInMillis) {
            nextReminder.timeInMillis = startOfToday.timeInMillis
        } else {
            // Find the next reminder time within today's range
            // Start from the most recent reminder time that has passed
            nextReminder.timeInMillis = startOfToday.timeInMillis
            
            // Keep adding intervals until we find the next time after now
            while (nextReminder.timeInMillis <= now.timeInMillis) {
                nextReminder.timeInMillis += intervalMs
            }
            
            // If we've gone past the end time, schedule for tomorrow's start time
            if (nextReminder.timeInMillis > endOfToday.timeInMillis) {
                nextReminder.timeInMillis = startOfToday.timeInMillis
                nextReminder.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        
        _nextReminderTime.value = nextReminder
    }
}
