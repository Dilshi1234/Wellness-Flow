package com.wellnessflow.habbittracker.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wellnessflow.habbittracker.R
import com.wellnessflow.habbittracker.data.Habit
import com.wellnessflow.habbittracker.databinding.ItemHabitOverviewBinding

class HabitOverviewAdapter : RecyclerView.Adapter<HabitOverviewAdapter.HabitOverviewViewHolder>() {
    
    private var habits: List<Habit> = emptyList()
    
    private fun getHabitIconResource(habitName: String): Int {
        return when (habitName.lowercase()) {
            "meditation" -> R.drawable.ic_meditation_professional
            "mindfulness" -> R.drawable.ic_meditation_professional
            "water" -> R.drawable.water2
            "hydration" -> R.drawable.water2
            "water intake" -> R.drawable.water2
            "steps" -> R.drawable.ic_steps_professional
            "walking" -> R.drawable.ic_steps_professional
            "exercise" -> R.drawable.ic_exercise_professional
            "fitness" -> R.drawable.ic_exercise_professional
            "workout" -> R.drawable.ic_exercise_professional
            else -> R.drawable.ic_meditation_professional // Default professional icon
        }
    }
    
    fun updateHabits(newHabits: List<Habit>) {
        habits = newHabits
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitOverviewViewHolder {
        val binding = ItemHabitOverviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HabitOverviewViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: HabitOverviewViewHolder, position: Int) {
        holder.bind(habits[position])
    }
    
    override fun getItemCount(): Int = habits.size
    
    inner class HabitOverviewViewHolder(private val binding: ItemHabitOverviewBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(habit: Habit) {
            binding.apply {
                ivHabitIcon.setImageResource(getHabitIconResource(habit.name))
                tvHabitName.text = habit.name
                tvHabitProgress.text = habit.getProgressText()
                tvHabitPercentage.text = "${habit.getCompletionPercentage().toInt()}%"
                
                // Check if this is a water-related habit
                val isWaterHabit = habit.name.lowercase().contains("water") || 
                                 habit.name.lowercase().contains("hydration")
                
                // Check if this is a meditation-related habit
                val isMeditationHabit = habit.name.lowercase().contains("meditation") || 
                                       habit.name.lowercase().contains("mindfulness")
                
                // Check if this is a steps-related habit
                val isStepsHabit = habit.name.lowercase().contains("steps") || 
                                 habit.name.lowercase().contains("walking")
                
                // Check if this is an exercise/fitness-related habit
                val isFitnessHabit = habit.name.lowercase().contains("exercise") || 
                                    habit.name.lowercase().contains("fitness") || 
                                    habit.name.lowercase().contains("workout")
                
                if (isWaterHabit) {
                    // Show water background image and overlay
                    ivBackgroundImage.setImageResource(R.drawable.water2)
                    ivBackgroundImage.visibility = android.view.View.VISIBLE
                    viewOverlay.visibility = android.view.View.VISIBLE
                    
                    // Hide the small icon and change text colors for better visibility
                    ivHabitIcon.visibility = android.view.View.GONE
                    tvHabitName.setTextColor(android.graphics.Color.WHITE)
                    tvHabitProgress.setTextColor(android.graphics.Color.WHITE)
                    tvHabitPercentage.setTextColor(android.graphics.Color.WHITE)
                    
                    // Add text shadow for better readability
                    tvHabitName.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                    tvHabitProgress.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                    tvHabitPercentage.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                } else if (isMeditationHabit) {
                    // Show meditation background image and overlay
                    ivBackgroundImage.setImageResource(R.drawable.meditation2)
                    ivBackgroundImage.visibility = android.view.View.VISIBLE
                    viewOverlay.visibility = android.view.View.VISIBLE
                    
                    // Hide the small icon and change text colors for better visibility
                    ivHabitIcon.visibility = android.view.View.GONE
                    tvHabitName.setTextColor(android.graphics.Color.WHITE)
                    tvHabitProgress.setTextColor(android.graphics.Color.WHITE)
                    tvHabitPercentage.setTextColor(android.graphics.Color.WHITE)
                    
                    // Add text shadow for better readability
                    tvHabitName.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                    tvHabitProgress.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                    tvHabitPercentage.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                } else if (isStepsHabit) {
                    // Show steps background image and overlay
                    ivBackgroundImage.setImageResource(R.drawable.steps)
                    ivBackgroundImage.visibility = android.view.View.VISIBLE
                    viewOverlay.visibility = android.view.View.VISIBLE
                    
                    // Hide the small icon and change text colors for better visibility
                    ivHabitIcon.visibility = android.view.View.GONE
                    tvHabitName.setTextColor(android.graphics.Color.WHITE)
                    tvHabitProgress.setTextColor(android.graphics.Color.WHITE)
                    tvHabitPercentage.setTextColor(android.graphics.Color.WHITE)
                    
                    // Add text shadow for better readability
                    tvHabitName.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                    tvHabitProgress.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                    tvHabitPercentage.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                } else if (isFitnessHabit) {
                    // Show fitness background image and overlay
                    ivBackgroundImage.setImageResource(R.drawable.fitness)
                    ivBackgroundImage.visibility = android.view.View.VISIBLE
                    viewOverlay.visibility = android.view.View.VISIBLE
                    
                    // Hide the small icon and change text colors for better visibility
                    ivHabitIcon.visibility = android.view.View.GONE
                    tvHabitName.setTextColor(android.graphics.Color.WHITE)
                    tvHabitProgress.setTextColor(android.graphics.Color.WHITE)
                    tvHabitPercentage.setTextColor(android.graphics.Color.WHITE)
                    
                    // Add text shadow for better readability
                    tvHabitName.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                    tvHabitProgress.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                    tvHabitPercentage.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                } else {
                    // Hide background image and overlay for other habits
                    ivBackgroundImage.visibility = android.view.View.GONE
                    viewOverlay.visibility = android.view.View.GONE
                    
                    // Show the small icon and use normal text colors
                    ivHabitIcon.visibility = android.view.View.VISIBLE
                    tvHabitName.setTextColor(itemView.context.getColor(R.color.text_primary_dark_purple))
                    tvHabitProgress.setTextColor(itemView.context.getColor(R.color.text_secondary))
                    tvHabitPercentage.setTextColor(itemView.context.getColor(R.color.purple_primary))
                    
                    // Remove text shadow for normal habits
                    tvHabitName.setShadowLayer(0f, 0f, 0f, android.graphics.Color.TRANSPARENT)
                    tvHabitProgress.setShadowLayer(0f, 0f, 0f, android.graphics.Color.TRANSPARENT)
                    tvHabitPercentage.setShadowLayer(0f, 0f, 0f, android.graphics.Color.TRANSPARENT)
                }
            }
        }
    }
}
