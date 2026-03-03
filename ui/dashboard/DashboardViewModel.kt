package com.wellnessflow.habbittracker.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wellnessflow.habbittracker.data.DataManager
import com.wellnessflow.habbittracker.data.Habit
import com.wellnessflow.habbittracker.data.MoodEntry
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    
    private val dataManager = DataManager(application)
    
    private val _habits = MutableLiveData<List<Habit>>()
    val habits: LiveData<List<Habit>> = _habits
    
    private val _todayMood = MutableLiveData<MoodEntry?>()
    val todayMood: LiveData<MoodEntry?> = _todayMood
    
    private val _completionPercentage = MutableLiveData<Float>()
    val completionPercentage: LiveData<Float> = _completionPercentage
    
    private val _completedHabitsCount = MutableLiveData<Int>()
    val completedHabitsCount: LiveData<Int> = _completedHabitsCount
    
    private val _totalHabitsCount = MutableLiveData<Int>()
    val totalHabitsCount: LiveData<Int> = _totalHabitsCount
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            // Check and reset daily data if needed
            dataManager.checkAndResetDailyData()
            
            // Load habits
            val habitsList = dataManager.loadHabits()
            _habits.value = habitsList
            
            // Load today's mood
            val todayMoodEntry = dataManager.getTodayMoodEntry()
            _todayMood.value = todayMoodEntry
            
            // Calculate completion statistics
            val completion = dataManager.getTodayCompletionPercentage()
            _completionPercentage.value = completion
            
            val completedCount = dataManager.getCompletedHabitsCount()
            _completedHabitsCount.value = completedCount
            
            val totalCount = dataManager.getTotalHabitsCount()
            _totalHabitsCount.value = totalCount
        }
    }
    
    fun incrementHabitProgress(habitId: String) {
        viewModelScope.launch {
            dataManager.incrementHabitProgress(habitId)
            loadData() // Reload data to update UI
        }
    }
    
    fun decrementHabitProgress(habitId: String) {
        viewModelScope.launch {
            dataManager.decrementHabitProgress(habitId)
            loadData() // Reload data to update UI
        }
    }
    
    fun refreshData() {
        loadData()
    }
}
