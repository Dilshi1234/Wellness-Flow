package com.wellnessflow.habbittracker.ui.timer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wellnessflow.habbittracker.data.DataManager
import kotlinx.coroutines.launch
import java.util.*

class TimerViewModel(application: Application) : AndroidViewModel(application) {
    
    private val dataManager = DataManager(application)
    
    private val _currentMode = MutableLiveData(TimerMode.FOCUS)
    val currentMode: LiveData<TimerMode> = _currentMode
    
    private val _timerSessions = MutableLiveData<List<TimerSession>>()
    val timerSessions: LiveData<List<TimerSession>> = _timerSessions
    
    init {
        // Load timer sessions asynchronously to prevent ANR
        viewModelScope.launch {
            try {
                loadTimerSessions()
            } catch (e: Exception) {
                android.util.Log.e("TimerViewModel", "Error initializing TimerViewModel: ${e.message}")
            }
        }
    }
    
    fun setCurrentMode(mode: TimerMode) {
        _currentMode.value = mode
    }
    
    fun saveTimerSession() {
        viewModelScope.launch {
            try {
                val session = TimerSession(
                    id = UUID.randomUUID().toString(),
                    mode = _currentMode.value ?: TimerMode.FOCUS,
                    duration = 25 * 60 * 1000L, // This would be the actual duration
                    completedAt = Date(),
                    notes = ""
                )
                
                dataManager.saveTimerSession(session)
                loadTimerSessions()
            } catch (e: Exception) {
                android.util.Log.e("TimerViewModel", "Error saving timer session: ${e.message}")
            }
        }
    }
    
    private fun loadTimerSessions() {
        viewModelScope.launch {
            try {
                val sessions = dataManager.getTimerSessions()
                _timerSessions.value = sessions
            } catch (e: Exception) {
                android.util.Log.e("TimerViewModel", "Error loading timer sessions: ${e.message}")
                _timerSessions.value = emptyList()
            }
        }
    }
    
    fun getTimerStats(): TimerStats {
        val sessions = _timerSessions.value ?: emptyList()
        val totalSessions = sessions.size
        val totalTime = sessions.sumOf { it.duration }
        val focusSessions = sessions.count { it.mode == TimerMode.FOCUS }
        val exerciseSessions = sessions.count { it.mode == TimerMode.EXERCISE }
        val meditationSessions = sessions.count { it.mode == TimerMode.MEDITATION }
        
        return TimerStats(
            totalSessions = totalSessions,
            totalTime = totalTime,
            focusSessions = focusSessions,
            exerciseSessions = exerciseSessions,
            meditationSessions = meditationSessions
        )
    }
}

data class TimerSession(
    val id: String,
    val mode: TimerMode,
    val duration: Long, // in milliseconds
    val completedAt: Date,
    val notes: String = ""
)

data class TimerStats(
    val totalSessions: Int,
    val totalTime: Long, // in milliseconds
    val focusSessions: Int,
    val exerciseSessions: Int,
    val meditationSessions: Int
)

enum class TimerMode {
    FOCUS, EXERCISE, MEDITATION
}
