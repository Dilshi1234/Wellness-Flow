package com.wellnessflow.habbittracker.ui.habits

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wellnessflow.habbittracker.data.DataManager
import com.wellnessflow.habbittracker.data.Habit
import kotlinx.coroutines.launch

class HabitsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val dataManager = DataManager(application)
    
    private val _habits = MutableLiveData<List<Habit>>()
    val habits: LiveData<List<Habit>> = _habits
    
    private val _completedCount = MutableLiveData<Int>()
    val completedCount: LiveData<Int> = _completedCount
    
    private val _totalCount = MutableLiveData<Int>()
    val totalCount: LiveData<Int> = _totalCount
    
    init {
        loadHabits()
    }
    
    private fun loadHabits() {
        viewModelScope.launch {
            // Check and reset daily data if needed
            dataManager.checkAndResetDailyData()
            
            val habitsList = dataManager.loadHabits()
            _habits.value = habitsList
            
            val completed = dataManager.getCompletedHabitsCount()
            _completedCount.value = completed
            
            val total = dataManager.getTotalHabitsCount()
            _totalCount.value = total
        }
    }
    
    fun incrementHabitProgress(habitId: String) {
        viewModelScope.launch {
            dataManager.incrementHabitProgress(habitId)
            loadHabits() // Reload data to update UI
        }
    }
    
    fun decrementHabitProgress(habitId: String) {
        viewModelScope.launch {
            dataManager.decrementHabitProgress(habitId)
            loadHabits() // Reload data to update UI
        }
    }
    
    fun updateHabitProgress(habitId: String, newProgress: Int) {
        viewModelScope.launch {
            dataManager.updateHabitProgress(habitId, newProgress)
            loadHabits() // Reload data to update UI
        }
    }
    
    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            dataManager.addHabit(habit)
            loadHabits() // Reload data to update UI
        }
    }
    
    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            dataManager.updateHabit(habit)
            loadHabits() // Reload data to update UI
        }
    }
    
    fun deleteHabit(habitId: String) {
        viewModelScope.launch {
            dataManager.deleteHabit(habitId)
            loadHabits() // Reload data to update UI
        }
    }
    
    fun refreshHabits() {
        loadHabits()
    }
}
