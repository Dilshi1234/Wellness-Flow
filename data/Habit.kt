package com.wellnessflow.habbittracker.data

import java.util.UUID

/**
 * Data class representing a wellness habit
 * @param id Unique identifier for the habit
 * @param name Display name of the habit
 * @param description Optional description
 * @param unit Unit of measurement (glasses, minutes, steps, hours)
 * @param targetValue Target value to achieve daily
 * @param currentValue Current progress for today
 * @param isActive Whether the habit is currently being tracked
 * @param emoji Visual representation emoji
 * @param createdAt When the habit was created
 * @param lastUpdated When the habit was last modified
 */
data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val unit: String,
    val targetValue: Int,
    val currentValue: Int = 0,
    val isActive: Boolean = true,
    val emoji: String = "💪",
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
) {
    /**
     * Calculate completion percentage
     */
    fun getCompletionPercentage(): Float {
        return if (targetValue > 0) {
            (currentValue.toFloat() / targetValue.toFloat() * 100).coerceAtMost(100f)
        } else 0f
    }
    
    /**
     * Check if habit is completed for today
     */
    fun isCompleted(): Boolean {
        return currentValue >= targetValue
    }
    
    /**
     * Get progress text
     */
    fun getProgressText(): String {
        return "$currentValue / $targetValue $unit"
    }
    
    /**
     * Reset daily progress
     */
    fun resetDailyProgress(): Habit {
        return copy(currentValue = 0, lastUpdated = System.currentTimeMillis())
    }
    
    /**
     * Increment progress by 1
     */
    fun incrementProgress(): Habit {
        return copy(
            currentValue = (currentValue + 1).coerceAtMost(targetValue),
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Decrement progress by 1
     */
    fun decrementProgress(): Habit {
        return copy(
            currentValue = (currentValue - 1).coerceAtLeast(0),
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Update progress to specific value
     */
    fun updateProgress(newValue: Int): Habit {
        return copy(
            currentValue = newValue.coerceIn(0, targetValue),
            lastUpdated = System.currentTimeMillis()
        )
    }
}

/**
 * Default habits for WellnessFlow app
 */
object DefaultHabits {
    val WATER_INTAKE = Habit(
        name = "Water Intake",
        description = "Stay hydrated throughout the day",
        unit = "glasses",
        targetValue = 8,
        emoji = "💧"
    )
    
    val MEDITATION = Habit(
        name = "Meditation",
        description = "Practice mindfulness and relaxation",
        unit = "minutes",
        targetValue = 30,
        emoji = "🧘"
    )
    
    val STEPS = Habit(
        name = "Steps",
        description = "Stay active and walk daily",
        unit = "steps",
        targetValue = 10000,
        emoji = "🚶"
    )
    
    val EXERCISE = Habit(
        name = "Exercise",
        description = "Physical activity and fitness",
        unit = "minutes",
        targetValue = 60,
        emoji = "🏃"
    )
    
    val SLEEP = Habit(
        name = "Sleep",
        description = "Get adequate rest and recovery",
        unit = "hours",
        targetValue = 8,
        emoji = "😴"
    )
    
    fun getAllDefaultHabits(): List<Habit> {
        return listOf(WATER_INTAKE, MEDITATION, STEPS, EXERCISE, SLEEP)
    }
}
