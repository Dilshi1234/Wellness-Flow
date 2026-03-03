package com.wellnessflow.habbittracker.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.wellnessflow.habbittracker.R

class CalendarAdapter(
    private val onDayClick: (CalendarDay) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {
    
    private var days = listOf<CalendarDay>()
    
    fun updateDays(newDays: List<CalendarDay>) {
        days = newDays
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(days[position])
    }
    
    override fun getItemCount(): Int = days.size
    
    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDayNumber: TextView = itemView.findViewById(R.id.tvDayNumber)
        private val vHabitStatus: View = itemView.findViewById(R.id.vHabitStatus)
        
        fun bind(day: CalendarDay) {
            when (day) {
                is CalendarDay.Empty -> {
                    tvDayNumber.text = ""
                    tvDayNumber.visibility = View.GONE
                    vHabitStatus.visibility = View.GONE
                    itemView.isClickable = false
                }
                is CalendarDay.Day -> {
                    tvDayNumber.text = day.dayNumber.toString()
                    tvDayNumber.visibility = View.VISIBLE
                    itemView.isClickable = true
                    
                    // Set habit status indicator
                    when (day.habitStatus) {
                        HabitStatus.COMPLETED -> {
                            vHabitStatus.visibility = View.VISIBLE
                            vHabitStatus.setBackgroundColor(
                                ContextCompat.getColor(itemView.context, R.color.success_green)
                            )
                        }
                        HabitStatus.PARTIAL -> {
                            vHabitStatus.visibility = View.VISIBLE
                            vHabitStatus.setBackgroundColor(
                                ContextCompat.getColor(itemView.context, R.color.warning_yellow)
                            )
                        }
                        HabitStatus.MISSED -> {
                            vHabitStatus.visibility = View.VISIBLE
                            vHabitStatus.setBackgroundColor(
                                ContextCompat.getColor(itemView.context, R.color.error_red)
                            )
                        }
                        HabitStatus.NONE -> {
                            vHabitStatus.visibility = View.GONE
                        }
                    }
                    
                    // Set click listener
                    itemView.setOnClickListener {
                        onDayClick(day)
                    }
                }
            }
        }
    }
}
