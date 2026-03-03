package com.wellnessflow.habbittracker.data

import java.util.*

data class MoodEntry(
    val id: String = UUID.randomUUID().toString(),
    val emoji: String,
    val name: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val date: String = getCurrentDate()
) {
    companion object {
        fun getCurrentDate(): String {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            return "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
        }
    }
}

data class MoodOption(
    val emoji: String,
    val name: String,
    val description: String,
    val color: String
)