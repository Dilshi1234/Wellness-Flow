package com.wellnessflow.habbittracker.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wellnessflow.habbittracker.data.DataManager
import kotlinx.coroutines.launch
import java.util.*

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    
    private val dataManager = DataManager(application)
    
    private val _calendarDays = MutableLiveData<List<CalendarDay>>()
    val calendarDays: LiveData<List<CalendarDay>> = _calendarDays
    
    private val _reminders = MutableLiveData<List<Reminder>>()
    val reminders: LiveData<List<Reminder>> = _reminders
    
    private val _selectedDay = MutableLiveData<CalendarDay?>()
    val selectedDay: LiveData<CalendarDay?> = _selectedDay
    
    init {
        // Load reminders asynchronously to prevent ANR
        viewModelScope.launch {
            try {
                loadReminders()
            } catch (e: Exception) {
                android.util.Log.e("CalendarViewModel", "Error initializing CalendarViewModel: ${e.message}")
            }
        }
    }
    
    fun generateCalendarDays(calendar: Calendar) {
        viewModelScope.launch {
            val days = mutableListOf<CalendarDay>()
            
            // Get the first day of the month and adjust to start of week
            val firstDayOfMonth = Calendar.getInstance().apply {
                set(Calendar.YEAR, calendar.get(Calendar.YEAR))
                set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, 1)
            }
            
            val firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK)
            val daysInMonth = firstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
            
            // Add empty days for the beginning of the week
            for (i in 1 until firstDayOfWeek) {
                days.add(CalendarDay.Empty)
            }
            
            // Add days of the month
            for (day in 1..daysInMonth) {
                val calendarDay = Calendar.getInstance().apply {
                    set(Calendar.YEAR, calendar.get(Calendar.YEAR))
                    set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, day)
                }
                
                val habitStatus = getHabitStatusForDay(calendarDay)
                days.add(CalendarDay.Day(day, habitStatus))
            }
            
            _calendarDays.value = days
        }
    }
    
    private suspend fun getHabitStatusForDay(day: Calendar): HabitStatus {
        // This would check the actual habit completion data
        // For now, return random status for demonstration
        return when ((0..2).random()) {
            0 -> HabitStatus.COMPLETED
            1 -> HabitStatus.PARTIAL
            else -> HabitStatus.MISSED
        }
    }
    
    fun selectDay(day: CalendarDay) {
        _selectedDay.value = day
    }
    
    fun toggleReminder(reminder: Reminder) {
        viewModelScope.launch {
            val updatedReminder = reminder.copy(enabled = !reminder.enabled)
            dataManager.updateReminder(updatedReminder)
            loadReminders()
        }
    }
    
    private fun loadReminders() {
        viewModelScope.launch {
            val loadedReminders = dataManager.getReminders()
            _reminders.value = loadedReminders
        }
    }
    
    fun addReminder(reminder: Reminder) {
        viewModelScope.launch {
            dataManager.addReminder(reminder)
            loadReminders()
        }
    }
    
    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            dataManager.deleteReminder(reminder.id)
            loadReminders()
        }
    }
}

sealed class CalendarDay {
    object Empty : CalendarDay()
    data class Day(val dayNumber: Int, val habitStatus: HabitStatus) : CalendarDay()
}

enum class HabitStatus {
    COMPLETED, PARTIAL, MISSED, NONE
}

data class Reminder(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val time: String,
    val enabled: Boolean = true,
    val habitId: String? = null
)
