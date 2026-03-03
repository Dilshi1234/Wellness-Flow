package com.wellnessflow.habbittracker.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wellnessflow.habbittracker.utils.NotificationHelper

/**
 * Broadcast receiver for hydration reminder alarms
 */
class HydrationReminderReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_HYDRATION_REMINDER -> {
                val notificationHelper = NotificationHelper(context)
                notificationHelper.showHydrationReminder()
            }
        }
    }
    
    companion object {
        const val ACTION_HYDRATION_REMINDER = "com.wellnessflow.habbittracker.HYDRATION_REMINDER"
    }
}
