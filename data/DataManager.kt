package com.wellnessflow.habbittracker.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Data manager class for handling SharedPreferences operations
 * Manages habits, mood entries, and user settings persistence
 */
class DataManager(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val PREFS_NAME = "wellness_flow_prefs"
        
        // Keys for habits
        private const val KEY_HABITS = "habits"
        private const val KEY_LAST_RESET_DATE = "last_reset_date"
        
        // Keys for mood entries
        private const val KEY_MOOD_ENTRIES = "mood_entries"
        
        // Keys for user settings
        private const val KEY_USER_SETTINGS = "user_settings"
        
        // Keys for daily data
        private const val KEY_DAILY_HABIT_PROGRESS = "daily_habit_progress"
        private const val KEY_TODAY_DATE = "today_date"
    }
    
    // ========== HABITS MANAGEMENT ==========
    
    /**
     * Save habits list to SharedPreferences
     */
    fun saveHabits(habits: List<Habit>) {
        try {
            val habitsJson = gson.toJson(habits)
            prefs.edit().putString(KEY_HABITS, habitsJson).apply()
        } catch (e: Exception) {
            // Log error in production
            android.util.Log.e("DataManager", "Error saving habits: ${e.message}")
        }
    }
    
    /**
     * Load habits list from SharedPreferences
     */
    fun loadHabits(): List<Habit> {
        return try {
            val habitsJson = prefs.getString(KEY_HABITS, null)
            if (habitsJson != null && habitsJson.isNotEmpty()) {
                val type = object : TypeToken<List<Habit>>() {}.type
                val habits = gson.fromJson<List<Habit>>(habitsJson, type)
                if (habits.isEmpty()) {
                    val defaultHabits = getDefaultHabits()
                    saveHabits(defaultHabits)
                    defaultHabits
                } else {
                    habits
                }
            } else {
                val defaultHabits = getDefaultHabits()
                saveHabits(defaultHabits)
                defaultHabits
            }
        } catch (e: Exception) {
            android.util.Log.e("DataManager", "Error loading habits: ${e.message}")
            val defaultHabits = getDefaultHabits()
            saveHabits(defaultHabits)
            defaultHabits
        }
    }
    
    /**
     * Get default habits if none exist
     */
    private fun getDefaultHabits(): List<Habit> {
        return DefaultHabits.getAllDefaultHabits()
    }
    
    /**
     * Add a new habit
     */
    fun addHabit(habit: Habit) {
        val habits = loadHabits().toMutableList()
        habits.add(habit)
        saveHabits(habits)
    }
    
    /**
     * Update an existing habit
     */
    fun updateHabit(updatedHabit: Habit) {
        val habits = loadHabits().toMutableList()
        val index = habits.indexOfFirst { it.id == updatedHabit.id }
        if (index != -1) {
            habits[index] = updatedHabit
            saveHabits(habits)
        }
    }
    
    /**
     * Delete a habit
     */
    fun deleteHabit(habitId: String) {
        val habits = loadHabits().toMutableList()
        habits.removeAll { it.id == habitId }
        saveHabits(habits)
    }
    
    // ========== MOOD ENTRIES MANAGEMENT ==========
    
    /**
     * Save mood entries list to SharedPreferences
     */
    fun saveMoodEntries(moodEntries: List<MoodEntry>) {
        val moodEntriesJson = gson.toJson(moodEntries)
        prefs.edit().putString(KEY_MOOD_ENTRIES, moodEntriesJson).apply()
    }
    
    /**
     * Load mood entries list from SharedPreferences
     */
    fun loadMoodEntries(): List<MoodEntry> {
        val moodEntriesJson = prefs.getString(KEY_MOOD_ENTRIES, null)
        return if (moodEntriesJson != null) {
            try {
                val type = object : TypeToken<List<MoodEntry>>() {}.type
                gson.fromJson(moodEntriesJson, type)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }
    
    /**
     * Add a new mood entry
     */
    fun addMoodEntry(moodEntry: MoodEntry) {
        val moodEntries = loadMoodEntries().toMutableList()
        moodEntries.add(moodEntry)
        saveMoodEntries(moodEntries)
    }
    
    /**
     * Update an existing mood entry
     */
    fun updateMoodEntry(updatedMoodEntry: MoodEntry) {
        val moodEntries = loadMoodEntries().toMutableList()
        val index = moodEntries.indexOfFirst { it.id == updatedMoodEntry.id }
        if (index != -1) {
            moodEntries[index] = updatedMoodEntry
            saveMoodEntries(moodEntries)
        }
    }
    
    /**
     * Delete a mood entry
     */
    fun deleteMoodEntry(moodEntryId: String) {
        val moodEntries = loadMoodEntries().toMutableList()
        moodEntries.removeAll { it.id == moodEntryId }
        saveMoodEntries(moodEntries)
    }
    
    /**
     * Get today's mood entry
     */
    fun getTodayMoodEntry(): MoodEntry? {
        val today = DateTimeUtils.getCurrentDate()
        return loadMoodEntries().find { it.date == today }
    }
    
    /**
     * Get mood entries for a specific date range
     */
    fun getMoodEntriesForDateRange(startDate: String, endDate: String): List<MoodEntry> {
        return loadMoodEntries().filter { 
            it.date >= startDate && it.date <= endDate 
        }.sortedByDescending { it.date }
    }
    
    // ========== USER SETTINGS MANAGEMENT ==========
    
    /**
     * Save user settings to SharedPreferences
     */
    fun saveUserSettings(settings: UserSettings) {
        val settingsJson = gson.toJson(settings)
        prefs.edit().putString(KEY_USER_SETTINGS, settingsJson).apply()
    }
    
    /**
     * Load user settings from SharedPreferences
     */
    fun loadUserSettings(): UserSettings {
        val settingsJson = prefs.getString(KEY_USER_SETTINGS, null)
        return if (settingsJson != null) {
            try {
                gson.fromJson(settingsJson, UserSettings::class.java)
            } catch (e: Exception) {
                DefaultUserSettings.getDefaultSettings()
            }
        } else {
            DefaultUserSettings.getDefaultSettings()
        }
    }
    
    // ========== DAILY DATA MANAGEMENT ==========
    
    /**
     * Check if daily data needs to be reset
     */
    fun checkAndResetDailyData() {
        val today = DateTimeUtils.getCurrentDate()
        val lastResetDate = prefs.getString(KEY_LAST_RESET_DATE, "")
        
        if (lastResetDate != today) {
            resetDailyProgress()
            prefs.edit().putString(KEY_LAST_RESET_DATE, today).apply()
        }
    }
    
    /**
     * Reset daily progress for all habits
     */
    private fun resetDailyProgress() {
        val habits = loadHabits()
        val resetHabits = habits.map { it.resetDailyProgress() }
        saveHabits(resetHabits)
    }
    
    /**
     * Update habit progress for today
     */
    fun updateHabitProgress(habitId: String, newProgress: Int) {
        val habits = loadHabits()
        val updatedHabits = habits.map { habit ->
            if (habit.id == habitId) {
                habit.updateProgress(newProgress)
            } else {
                habit
            }
        }
        saveHabits(updatedHabits)
    }
    
    /**
     * Increment habit progress
     */
    fun incrementHabitProgress(habitId: String) {
        val habits = loadHabits()
        val updatedHabits = habits.map { habit ->
            if (habit.id == habitId) {
                habit.incrementProgress()
            } else {
                habit
            }
        }
        saveHabits(updatedHabits)
    }
    
    /**
     * Decrement habit progress
     */
    fun decrementHabitProgress(habitId: String) {
        val habits = loadHabits()
        val updatedHabits = habits.map { habit ->
            if (habit.id == habitId) {
                habit.decrementProgress()
            } else {
                habit
            }
        }
        saveHabits(updatedHabits)
    }
    
    // ========== STATISTICS HELPERS ==========
    
    /**
     * Get completion percentage for today
     */
    fun getTodayCompletionPercentage(): Float {
        val habits = loadHabits()
        if (habits.isEmpty()) return 0f
        
        val totalCompletion = habits.sumOf { it.getCompletionPercentage().toInt() }
        return totalCompletion.toFloat() / habits.size
    }
    
    /**
     * Get number of completed habits today
     */
    fun getCompletedHabitsCount(): Int {
        val habits = loadHabits()
        return habits.count { it.isCompleted() }
    }
    
    /**
     * Get total habits count
     */
    fun getTotalHabitsCount(): Int {
        return loadHabits().size
    }
    
    /**
     * Get habits completion data for statistics
     */
    fun getHabitsCompletionData(): Map<String, Float> {
        val habits = loadHabits()
        return habits.associate { habit ->
            habit.name to habit.getCompletionPercentage()
        }
    }
    
    // ========== DATA MANAGEMENT ==========
    
    /**
     * Clear all data
     */
    fun clearAllData() {
        prefs.edit().clear().apply()
    }
    
    /**
     * Reset only today's progress
     */
    fun resetTodayProgress() {
        resetDailyProgress()
    }
    
    /**
     * Export all data as JSON
     */
    fun exportAllData(): String {
        val exportData = mapOf(
            "habits" to loadHabits(),
            "moodEntries" to loadMoodEntries(),
            "userSettings" to loadUserSettings(),
            "exportDate" to DateTimeUtils.getCurrentDate(),
            "exportTime" to DateTimeUtils.getCurrentTime()
        )
        return gson.toJson(exportData)
    }
    
    /**
     * Check if this is the first app launch
     */
    fun isFirstLaunch(): Boolean {
        val settings = loadUserSettings()
        return settings.firstLaunch
    }
    
    /**
     * Mark first launch as completed
     */
    fun markFirstLaunchCompleted() {
        val settings = loadUserSettings()
        val updatedSettings = settings.copy(firstLaunch = false)
        saveUserSettings(updatedSettings)
    }
    
    // ========== REMINDERS MANAGEMENT ==========
    
    /**
     * Get reminders list from SharedPreferences
     */
    fun getReminders(): List<com.wellnessflow.habbittracker.ui.calendar.Reminder> {
        return try {
            val remindersJson = prefs.getString("reminders", null)
            if (remindersJson != null && remindersJson.isNotEmpty()) {
                val type = object : TypeToken<List<com.wellnessflow.habbittracker.ui.calendar.Reminder>>() {}.type
                val reminders = gson.fromJson<List<com.wellnessflow.habbittracker.ui.calendar.Reminder>>(remindersJson, type)
                reminders ?: getDefaultReminders()
            } else {
                getDefaultReminders()
            }
        } catch (e: Exception) {
            android.util.Log.e("DataManager", "Error loading reminders: ${e.message}")
            // Clear corrupted data and return defaults
            prefs.edit().remove("reminders").apply()
            getDefaultReminders()
        }
    }
    
    /**
     * Add a new reminder
     */
    fun addReminder(reminder: com.wellnessflow.habbittracker.ui.calendar.Reminder) {
        val reminders = getReminders().toMutableList()
        reminders.add(reminder)
        saveReminders(reminders)
    }
    
    /**
     * Update an existing reminder
     */
    fun updateReminder(updatedReminder: com.wellnessflow.habbittracker.ui.calendar.Reminder) {
        val reminders = getReminders().toMutableList()
        val index = reminders.indexOfFirst { it.id == updatedReminder.id }
        if (index != -1) {
            reminders[index] = updatedReminder
            saveReminders(reminders)
        }
    }
    
    /**
     * Delete a reminder
     */
    fun deleteReminder(reminderId: String) {
        val reminders = getReminders().toMutableList()
        reminders.removeAll { it.id == reminderId }
        saveReminders(reminders)
    }
    
    /**
     * Save reminders list to SharedPreferences
     */
    private fun saveReminders(reminders: List<com.wellnessflow.habbittracker.ui.calendar.Reminder>) {
        try {
            val remindersJson = gson.toJson(reminders)
            prefs.edit().putString("reminders", remindersJson).apply()
        } catch (e: Exception) {
            android.util.Log.e("DataManager", "Error saving reminders: ${e.message}")
        }
    }
    
    /**
     * Get default reminders - return empty list so users can add their own
     */
    private fun getDefaultReminders(): List<com.wellnessflow.habbittracker.ui.calendar.Reminder> {
        return emptyList()
    }
    
    // ========== TIMER SESSIONS MANAGEMENT ==========
    
    /**
     * Save timer session
     */
    fun saveTimerSession(session: com.wellnessflow.habbittracker.ui.timer.TimerSession) {
        val sessions = getTimerSessions().toMutableList()
        sessions.add(session)
        saveTimerSessions(sessions)
    }
    
    /**
     * Get timer sessions list from SharedPreferences
     */
    fun getTimerSessions(): List<com.wellnessflow.habbittracker.ui.timer.TimerSession> {
        return try {
            val sessionsJson = prefs.getString("timer_sessions", null)
            if (sessionsJson != null && sessionsJson.isNotEmpty()) {
                val type = object : TypeToken<List<com.wellnessflow.habbittracker.ui.timer.TimerSession>>() {}.type
                val sessions = gson.fromJson<List<com.wellnessflow.habbittracker.ui.timer.TimerSession>>(sessionsJson, type)
                sessions ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("DataManager", "Error loading timer sessions: ${e.message}")
            // Clear corrupted data
            prefs.edit().remove("timer_sessions").apply()
            emptyList()
        }
    }
    
    /**
     * Save timer sessions list to SharedPreferences
     */
    private fun saveTimerSessions(sessions: List<com.wellnessflow.habbittracker.ui.timer.TimerSession>) {
        try {
            val sessionsJson = gson.toJson(sessions)
            prefs.edit().putString("timer_sessions", sessionsJson).apply()
        } catch (e: Exception) {
            android.util.Log.e("DataManager", "Error saving timer sessions: ${e.message}")
        }
    }
}
