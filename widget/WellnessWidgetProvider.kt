package com.wellnessflow.habbittracker.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.wellnessflow.habbittracker.MainActivity
import com.wellnessflow.habbittracker.R
import com.wellnessflow.habbittracker.data.DataManager

/**
 * Widget provider for WellnessFlow app
 * Shows today's habit completion percentage and quick actions
 */
class WellnessWidgetProvider : AppWidgetProvider() {
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    override fun onEnabled(context: Context) {
        // Called when the first widget is created
    }
    
    override fun onDisabled(context: Context) {
        // Called when the last widget is removed
    }
    
    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val dataManager = DataManager(context)
            
            // Get today's data
            val completionPercentage = dataManager.getTodayCompletionPercentage()
            val completedHabits = dataManager.getCompletedHabitsCount()
            val totalHabits = dataManager.getTotalHabitsCount()
            val todayMood = dataManager.getTodayMoodEntry()
            
            // Create RemoteViews
            val views = RemoteViews(context.packageName, R.layout.widget_wellness_flow)
            
            // Update completion percentage
            views.setTextViewText(R.id.widget_completion_percentage, "${completionPercentage.toInt()}%")
            
            // Update habits count
            views.setTextViewText(R.id.widget_habits_count, "$completedHabits/$totalHabits")
            
            // Update mood display
            if (todayMood != null) {
                views.setTextViewText(R.id.widget_mood_emoji, todayMood.emoji)
                views.setTextViewText(R.id.widget_mood_text, todayMood.description)
            } else {
                views.setTextViewText(R.id.widget_mood_emoji, "😐")
                views.setTextViewText(R.id.widget_mood_text, "No mood")
            }
            
            // Set up click intents
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
            
            // Quick action buttons
            setupQuickActionButtons(context, views, dataManager)
            
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
        
        private fun setupQuickActionButtons(
            context: Context,
            views: RemoteViews,
            dataManager: DataManager
        ) {
            // Water increment button
            val waterIntent = Intent(context, WidgetActionReceiver::class.java).apply {
                action = WidgetActionReceiver.ACTION_INCREMENT_WATER
            }
            val waterPendingIntent = PendingIntent.getBroadcast(
                context, 1, waterIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_water_button, waterPendingIntent)
            
            // Steps increment button
            val stepsIntent = Intent(context, WidgetActionReceiver::class.java).apply {
                action = WidgetActionReceiver.ACTION_INCREMENT_STEPS
            }
            val stepsPendingIntent = PendingIntent.getBroadcast(
                context, 2, stepsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_steps_button, stepsPendingIntent)
        }
    }
}
