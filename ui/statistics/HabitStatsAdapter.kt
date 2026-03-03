package com.wellnessflow.habbittracker.ui.statistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wellnessflow.habbittracker.R

class HabitStatsAdapter : RecyclerView.Adapter<HabitStatsAdapter.HabitStatViewHolder>() {
    
    private val habitStats = mutableListOf<HabitStat>()
    
    fun updateHabitStats(newHabitStats: List<HabitStat>) {
        habitStats.clear()
        habitStats.addAll(newHabitStats)
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitStatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit_stat, parent, false)
        return HabitStatViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: HabitStatViewHolder, position: Int) {
        val habit = habitStats[position]
        holder.bind(habit)
    }
    
    override fun getItemCount(): Int = habitStats.size
    
    class HabitStatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val habitName: TextView = itemView.findViewById(R.id.tv_habit_name)
        private val habitStreak: TextView = itemView.findViewById(R.id.tv_habit_streak)
        private val habitCompletion: TextView = itemView.findViewById(R.id.tv_habit_completion)
        
        fun bind(habit: HabitStat) {
            habitName.text = habit.name
            habitStreak.text = habit.streak
            habitCompletion.text = habit.completion
        }
    }
    
    data class HabitStat(
        val name: String,
        val streak: String,
        val completion: String
    )
}