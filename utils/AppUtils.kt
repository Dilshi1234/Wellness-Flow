package com.wellnessflow.habbittracker.utils

import android.content.Context
import android.widget.Toast

/**
 * Utility class for common app operations
 */
object AppUtils {
    
    /**
     * Show a toast message
     */
    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
    
    /**
     * Show success toast
     */
    fun showSuccessToast(context: Context, message: String) {
        showToast(context, "✅ $message")
    }
    
    /**
     * Show error toast
     */
    fun showErrorToast(context: Context, message: String) {
        showToast(context, "❌ $message")
    }
    
    /**
     * Show info toast
     */
    fun showInfoToast(context: Context, message: String) {
        showToast(context, "ℹ️ $message")
    }
    
    /**
     * Format percentage for display
     */
    fun formatPercentage(value: Float): String {
        return "${value.toInt()}%"
    }
    
    /**
     * Format time for display
     */
    fun formatTime(hours: Int, minutes: Int): String {
        return String.format("%02d:%02d", hours, minutes)
    }
    
    /**
     * Get habit emoji by name
     */
    fun getHabitEmoji(habitName: String): String {
        return when (habitName) {
            "Water Intake" -> "💧"
            "Meditation" -> "🧘"
            "Steps" -> "🚶"
            "Exercise" -> "🏃"
            "Sleep" -> "😴"
            else -> "💪"
        }
    }
    
    /**
     * Get mood emoji by description
     */
    fun getMoodEmoji(description: String): String {
        return when (description.lowercase()) {
            "happy" -> "😊"
            "neutral" -> "😐"
            "sad" -> "😔"
            "tired" -> "😴"
            "angry" -> "😤"
            else -> "😐"
        }
    }
    
    /**
     * Validate habit name
     */
    fun isValidHabitName(name: String): Boolean {
        return name.trim().isNotBlank() && name.length <= 50
    }
    
    /**
     * Validate habit target value
     */
    fun isValidTargetValue(value: Int): Boolean {
        return value > 0 && value <= 1000
    }
    
    /**
     * Validate mood notes
     */
    fun isValidMoodNotes(notes: String): Boolean {
        return notes.length <= 200
    }
    
    /**
     * Get completion status text
     */
    fun getCompletionStatusText(completed: Int, total: Int): String {
        return "$completed of $total completed"
    }
    
    /**
     * Get completion status color
     */
    fun getCompletionStatusColor(completed: Int, total: Int): String {
        val percentage = if (total > 0) (completed.toFloat() / total * 100) else 0f
        return when {
            percentage >= 80 -> "#7ED321" // Green
            percentage >= 60 -> "#F5A623" // Orange
            else -> "#DC143C" // Red
        }
    }
}
