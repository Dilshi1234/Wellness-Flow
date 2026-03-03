package com.wellnessflow.habbittracker.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wellnessflow.habbittracker.data.DataManager
import com.wellnessflow.habbittracker.data.UserSettings
import com.wellnessflow.habbittracker.utils.AlarmManagerHelper
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val dataManager = DataManager(application)
    private val alarmManagerHelper = AlarmManagerHelper(application)
    
    private val _userSettings = MutableLiveData<UserSettings>()
    val userSettings: LiveData<UserSettings> = _userSettings
    
    init {
        loadUserSettings()
    }
    
    private fun loadUserSettings() {
        viewModelScope.launch {
            val settings = dataManager.loadUserSettings()
            _userSettings.value = settings
        }
    }
    
    fun updateHydrationReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val currentSettings = _userSettings.value ?: return@launch
            val updatedSettings = currentSettings.copy(hydrationReminderEnabled = enabled)
            dataManager.saveUserSettings(updatedSettings)
            _userSettings.value = updatedSettings
            
            // Update alarm schedule
            if (enabled) {
                alarmManagerHelper.scheduleHydrationReminders(updatedSettings)
            } else {
                alarmManagerHelper.cancelHydrationReminders()
            }
        }
    }
    
    fun updateReminderInterval(intervalMinutes: Int) {
        viewModelScope.launch {
            val currentSettings = _userSettings.value ?: return@launch
            val updatedSettings = currentSettings.copy(reminderIntervalMinutes = intervalMinutes)
            dataManager.saveUserSettings(updatedSettings)
            _userSettings.value = updatedSettings
            
            // Reschedule alarms with new interval
            if (updatedSettings.hydrationReminderEnabled) {
                alarmManagerHelper.scheduleHydrationReminders(updatedSettings)
            }
        }
    }
    
    fun updateReminderHours(startTime: String, endTime: String) {
        viewModelScope.launch {
            val currentSettings = _userSettings.value ?: return@launch
            val updatedSettings = currentSettings.copy(
                reminderStartTime = startTime,
                reminderEndTime = endTime
            )
            dataManager.saveUserSettings(updatedSettings)
            _userSettings.value = updatedSettings
            
            // Reschedule alarms with new hours
            if (updatedSettings.hydrationReminderEnabled) {
                alarmManagerHelper.scheduleHydrationReminders(updatedSettings)
            }
        }
    }
    
    fun updateSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val currentSettings = _userSettings.value ?: return@launch
            val updatedSettings = currentSettings.copy(soundEnabled = enabled)
            dataManager.saveUserSettings(updatedSettings)
            _userSettings.value = updatedSettings
        }
    }
    
    fun updateVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val currentSettings = _userSettings.value ?: return@launch
            val updatedSettings = currentSettings.copy(vibrationEnabled = enabled)
            dataManager.saveUserSettings(updatedSettings)
            _userSettings.value = updatedSettings
        }
    }
    
    fun resetTodayProgress() {
        viewModelScope.launch {
            dataManager.resetTodayProgress()
        }
    }
    
    fun exportData(): String {
        return dataManager.exportAllData()
    }
    
    fun clearAllData() {
        viewModelScope.launch {
            dataManager.clearAllData()
            alarmManagerHelper.cancelHydrationReminders()
            loadUserSettings() // Reload default settings
        }
    }
    
    fun refreshSettings() {
        loadUserSettings()
    }
}
