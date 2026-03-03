package com.wellnessflow.habbittracker.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wellnessflow.habbittracker.data.DataManager

/**
 * Broadcast receiver for widget action buttons
 */
class WidgetActionReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        val dataManager = DataManager(context)
        
        when (intent.action) {
            ACTION_INCREMENT_WATER -> {
                // Find water habit and increment
                val habits = dataManager.loadHabits()
                val waterHabit = habits.find { it.name == "Water Intake" }
                waterHabit?.let { habit ->
                    dataManager.incrementHabitProgress(habit.id)
                }
            }
            
            ACTION_INCREMENT_STEPS -> {
                // Find steps habit and increment
                val habits = dataManager.loadHabits()
                val stepsHabit = habits.find { it.name == "Steps" }
                stepsHabit?.let { habit ->
                    dataManager.incrementHabitProgress(habit.id)
                }
            }
        }
        
        // Update all widgets
        val widgetManager = android.appwidget.AppWidgetManager.getInstance(context)
        val widgetIds = widgetManager.getAppWidgetIds(
            android.content.ComponentName(context, WellnessWidgetProvider::class.java)
        )
        WellnessWidgetProvider.updateAppWidget(context, widgetManager, widgetIds[0])
    }
    
    companion object {
        const val ACTION_INCREMENT_WATER = "com.wellnessflow.habbittracker.INCREMENT_WATER"
        const val ACTION_INCREMENT_STEPS = "com.wellnessflow.habbittracker.INCREMENT_STEPS"
    }
}
