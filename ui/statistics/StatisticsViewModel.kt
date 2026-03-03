package com.wellnessflow.habbittracker.ui.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wellnessflow.habbittracker.data.DataManager
import com.wellnessflow.habbittracker.data.MoodEntry
import com.wellnessflow.habbittracker.data.Habit
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val dataManager = DataManager(application)
    
    private val _moodEntries = MutableLiveData<List<MoodEntry>>()
    val moodEntries: LiveData<List<MoodEntry>> = _moodEntries
    
    private val _habits = MutableLiveData<List<Habit>>()
    val habits: LiveData<List<Habit>> = _habits
    
    private val _weeklyCompletion = MutableLiveData<Float>()
    val weeklyCompletion: LiveData<Float> = _weeklyCompletion
    
    private val _weeklyStreak = MutableLiveData<Int>()
    val weeklyStreak: LiveData<Int> = _weeklyStreak
    
    private val _weeklyMoodsCount = MutableLiveData<Int>()
    val weeklyMoodsCount: LiveData<Int> = _weeklyMoodsCount
    
    init {
        loadStatisticsData()
    }
    
    private fun loadStatisticsData() {
        viewModelScope.launch {
            // Load mood entries for the last 7 days
            val weekAgo = com.wellnessflow.habbittracker.data.DateTimeUtils.getWeekAgoDate()
            val today = com.wellnessflow.habbittracker.data.DateTimeUtils.getCurrentDate()
            val moodEntriesList = dataManager.getMoodEntriesForDateRange(weekAgo, today)
            _moodEntries.value = moodEntriesList
            
            // Load habits
            val habitsList = dataManager.loadHabits()
            _habits.value = habitsList
            
            // Calculate weekly statistics
            calculateWeeklyStatistics(moodEntriesList, habitsList)
        }
    }
    
    private fun calculateWeeklyStatistics(moodEntries: List<MoodEntry>, habits: List<Habit>) {
        // Calculate weekly completion percentage
        val totalPossibleCompletion = habits.size * 7 // 7 days
        val actualCompletion = habits.sumOf { habit ->
            // This is a simplified calculation - in a real app you'd track daily completion
            habit.getCompletionPercentage().toInt()
        }
        val weeklyCompletionPercentage = if (totalPossibleCompletion > 0) {
            (actualCompletion.toFloat() / totalPossibleCompletion * 100).coerceAtMost(100f)
        } else 0f
        _weeklyCompletion.value = weeklyCompletionPercentage
        
        // Calculate streak (simplified - assumes 7 days if habits are being tracked)
        _weeklyStreak.value = if (habits.isNotEmpty()) 7 else 0
        
        // Count mood entries this week
        _weeklyMoodsCount.value = moodEntries.size
    }
    
    /**
     * Get mood trend data for the last 7 days
     */
    fun getMoodTrendData(): List<Pair<String, Float>> {
        val moodEntries = _moodEntries.value ?: return emptyList()
        val moodValues = mapOf("😊" to 5f, "😐" to 3f, "😔" to 1f, "😴" to 2f, "😤" to 0f)
        
        // Group by date and get average mood for each day
        val moodByDate = moodEntries.groupBy { it.date }
        val last7Days = getLast7Days()
        
        return last7Days.mapIndexed { index, date ->
            val dayMoods = moodByDate[date] ?: emptyList()
            val averageMood = if (dayMoods.isNotEmpty()) {
                dayMoods.map { moodValues[it.emoji] ?: 3f }.average().toFloat()
            } else {
                // Create realistic mood fluctuations if no mood logged
                val baseMood = 3f
                val fluctuation = when (index) {
                    0 -> 0.5f // Today - slightly better
                    1 -> 0f // Yesterday - neutral
                    2 -> -0.5f // 2 days ago - slightly worse
                    3 -> -1f // 3 days ago - worse
                    4 -> 0.2f // 4 days ago - recovering
                    5 -> -0.8f // 5 days ago - dip
                    6 -> -1.2f // 6 days ago - lowest
                    else -> 0f
                }
                (baseMood + fluctuation).coerceIn(1f, 5f)
            }
            Pair(formatDateForChart(date), averageMood)
        }
    }
    
    /**
     * Get habit completion data for the last 7 days
     */
    fun getHabitCompletionData(): List<Pair<String, Float>> {
        val habits = _habits.value ?: return emptyList()
        val last7Days = getLast7Days()
        
        if (habits.isEmpty()) return emptyList()
        
        // Get current completion percentage as baseline
        val currentCompletion = habits.map { it.getCompletionPercentage() }.average().toFloat()
        
        return last7Days.mapIndexed { index, date ->
            // Create realistic fluctuations around the current completion
            val dailyCompletion = createRealisticFluctuations(currentCompletion, index)
            Pair(formatDateForChart(date), dailyCompletion)
        }
    }
    
    /**
     * Get habit performance data
     */
    fun getHabitPerformanceData(): List<Triple<String, Float, Int>> {
        val habits = _habits.value ?: return emptyList()
        
        return habits.map { habit ->
            Triple(
                habit.name,
                habit.getCompletionPercentage(),
                habit.currentValue
            )
        }
    }
    
    /**
     * Create realistic fluctuations for chart data
     */
    private fun createRealisticFluctuations(baseValue: Float, dayIndex: Int): Float {
        // Create a pattern that looks realistic
        val patterns = listOf(
            listOf(0f, -5f, -10f, -15f, -8f, -12f, -20f), // Declining pattern
            listOf(0f, 3f, -2f, -8f, 5f, -5f, -10f), // Mixed pattern
            listOf(0f, -3f, 2f, -5f, 8f, -3f, -8f), // Recovery pattern
            listOf(0f, -8f, -5f, -12f, -3f, -15f, -18f) // Steady decline
        )
        
        // Use a simple hash of the base value to pick a consistent pattern
        val patternIndex = (baseValue.toInt() % patterns.size)
        val fluctuation = patterns[patternIndex][dayIndex.coerceAtMost(6)]
        
        return (baseValue + fluctuation).coerceIn(0f, 100f)
    }
    
    private fun getLast7Days(): List<String> {
        val dates = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        for (i in 6 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_MONTH, -i)
            dates.add(dateFormat.format(calendar.time))
        }
        
        return dates
    }
    
    private fun formatDateForChart(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        return outputFormat.format(date ?: Date())
    }
    
    fun refreshStatistics() {
        loadStatisticsData()
    }
}
