package com.wellnessflow.habbittracker.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.wellnessflow.habbittracker.R

class ReminderAdapter(
    private val onReminderToggle: (Reminder) -> Unit,
    private val onReminderDelete: (Reminder) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {
    
    private var reminders = listOf<Reminder>()
    
    fun updateReminders(newReminders: List<Reminder>) {
        reminders = newReminders
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reminder, parent, false)
        return ReminderViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(reminders[position])
    }
    
    override fun getItemCount(): Int = reminders.size
    
    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvReminderTitle: TextView = itemView.findViewById(R.id.tvReminderTitle)
        private val tvReminderTime: TextView = itemView.findViewById(R.id.tvReminderTime)
        private val tvReminderDate: TextView = itemView.findViewById(R.id.tvReminderDate)
        private val tvReminderStatus: TextView = itemView.findViewById(R.id.tvReminderStatus)
        private val ivEditReminder: android.widget.ImageView = itemView.findViewById(R.id.ivEditReminder)
        private val ivDeleteReminder: android.widget.ImageView = itemView.findViewById(R.id.ivDeleteReminder)
        
        fun bind(reminder: Reminder) {
            tvReminderTitle.text = reminder.title
            
            // Parse the reminder time to extract time and date
            try {
                val timeString = reminder.time
                // Format: "Oct 01, 2025 at 09:00 (Daily)"
                val datePart = timeString.substringBefore(" at").trim()
                val timePart = timeString.substringAfter(" at ").substringBefore(" (").trim()
                
                tvReminderDate.text = datePart
                tvReminderTime.text = timePart
            } catch (e: Exception) {
                tvReminderTime.text = reminder.time
                tvReminderDate.text = ""
            }
            
            // Set status text and color based on enabled state
            if (reminder.enabled) {
                tvReminderStatus.text = "Status: Active"
                tvReminderStatus.setTextColor(itemView.context.getColor(com.wellnessflow.habbittracker.R.color.success_green))
            } else {
                tvReminderStatus.text = "Status: Inactive"
                tvReminderStatus.setTextColor(itemView.context.getColor(com.wellnessflow.habbittracker.R.color.error_red))
            }
            
            // Edit button click
            ivEditReminder.setOnClickListener {
                android.widget.Toast.makeText(itemView.context, "Edit reminder: ${reminder.title}", android.widget.Toast.LENGTH_SHORT).show()
            }
            
            // Delete button click
            ivDeleteReminder.setOnClickListener {
                android.app.AlertDialog.Builder(itemView.context)
                    .setTitle("Delete Reminder")
                    .setMessage("Are you sure you want to delete this reminder?")
                    .setPositiveButton("Delete") { _, _ ->
                        onReminderDelete(reminder)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }
}
