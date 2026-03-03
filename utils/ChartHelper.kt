package com.wellnessflow.habbittracker.utils

import android.content.Context
import android.graphics.Color
import android.widget.TextView
import com.wellnessflow.habbittracker.R

/**
 * Simple chart helper for displaying statistics without external chart library
 * This provides basic chart functionality using native Android components
 */
class ChartHelper {
    
    companion object {
        
        /**
         * Display mood trend data as text-based chart
         */
        fun displayMoodTrendData(context: Context, data: List<Pair<String, Float>>, textView: TextView) {
            if (data.isEmpty()) {
                textView.text = "No mood data available"
                return
            }
            
            val chartText = buildString {
                appendLine("📊 Mood Trend (Last 7 Days)")
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                
                data.forEach { (date, moodValue) ->
                    val moodEmoji = when {
                        moodValue >= 4.5f -> "😊"
                        moodValue >= 3.5f -> "😐"
                        moodValue >= 2.5f -> "😔"
                        moodValue >= 1.5f -> "😴"
                        else -> "😤"
                    }
                    
                    val progressBar = "█".repeat((moodValue * 2).toInt())
                    appendLine("$date: $moodEmoji $progressBar (${String.format("%.1f", moodValue)})")
                }
                
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                appendLine("Scale: 😤(0) → 😔(1) → 😴(2) → 😐(3) → 😊(5)")
            }
            
            textView.text = chartText
        }
        
        /**
         * Display habit completion data as text-based chart
         */
        fun displayHabitCompletionData(context: Context, data: List<Pair<String, Float>>, textView: TextView) {
            if (data.isEmpty()) {
                textView.text = "No habit data available"
                return
            }
            
            val chartText = buildString {
                appendLine("📈 Habit Completion (Last 7 Days)")
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                
                data.forEach { (date, completion) ->
                    val percentage = completion.toInt()
                    val progressBar = "█".repeat(percentage / 5) + "░".repeat((100 - percentage) / 5)
                    appendLine("$date: $progressBar $percentage%")
                }
                
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                appendLine("Scale: ░(0%) → █(100%)")
            }
            
            textView.text = chartText
        }
        
        /**
         * Display habit performance data as text-based chart
         */
        fun displayHabitPerformanceData(context: Context, data: List<Triple<String, Float, Int>>, textView: TextView) {
            if (data.isEmpty()) {
                textView.text = "No habit performance data available"
                return
            }
            
            val chartText = buildString {
                appendLine("🏆 Habit Performance Overview")
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                
                data.forEach { (habitName, completionRate, currentValue) ->
                    val emoji = when (habitName) {
                        "Water Intake" -> "💧"
                        "Meditation" -> "🧘"
                        "Steps" -> "🚶"
                        "Exercise" -> "🏃"
                        "Sleep" -> "😴"
                        else -> "💪"
                    }
                    
                    val percentage = completionRate.toInt()
                    val progressBar = "█".repeat(percentage / 10) + "░".repeat((100 - percentage) / 10)
                    appendLine("$emoji $habitName: $progressBar $percentage%")
                }
                
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                appendLine("Scale: ░(0%) → █(100%)")
            }
            
            textView.text = chartText
        }
        
        /**
         * Create a simple progress bar using text
         */
        fun createTextProgressBar(percentage: Int, maxLength: Int = 20): String {
            val filledLength = (percentage * maxLength) / 100
            val emptyLength = maxLength - filledLength
            return "█".repeat(filledLength) + "░".repeat(emptyLength)
        }
        
        /**
         * Get mood emoji based on value
         */
        fun getMoodEmoji(moodValue: Float): String {
            return when {
                moodValue >= 4.5f -> "😊"
                moodValue >= 3.5f -> "😐"
                moodValue >= 2.5f -> "😔"
                moodValue >= 1.5f -> "😴"
                else -> "😤"
            }
        }
    }
}
