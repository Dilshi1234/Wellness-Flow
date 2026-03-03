package com.wellnessflow.habbittracker.ui.habits

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wellnessflow.habbittracker.R
import com.wellnessflow.habbittracker.data.Habit
import com.wellnessflow.habbittracker.databinding.ItemHabitBinding
import com.wellnessflow.habbittracker.utils.PerformanceUtils

class HabitsAdapter(
    private val onIncrementClick: (String) -> Unit,
    private val onDecrementClick: (String) -> Unit,
    private val onEditClick: (Habit) -> Unit,
    private val onDeleteClick: (String) -> Unit
) : ListAdapter<Habit, HabitsAdapter.HabitViewHolder>(HabitDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HabitViewHolder(private val binding: ItemHabitBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(habit: Habit) {
            binding.apply {
                tvHabitName.text = habit.name
                tvHabitProgress.text = habit.getProgressText()
                
                // Update progress bar
                progressHabit.progress = habit.getCompletionPercentage().toInt()
                
                // Check if this is a water-related habit
                val isWaterHabit = habit.name.lowercase().contains("water") || 
                                 habit.name.lowercase().contains("hydration") ||
                                 habit.name.lowercase().contains("water intake")
                
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
                
                // Check if this is a sleep-related habit
                val isSleepHabit = habit.name.lowercase().contains("sleep") || 
                                 habit.name.lowercase().contains("rest")
                
                // Debug log
                android.util.Log.d("HabitsAdapter", "Habit: ${habit.name}, isWaterHabit: $isWaterHabit, isMeditationHabit: $isMeditationHabit, isStepsHabit: $isStepsHabit, isFitnessHabit: $isFitnessHabit, isSleepHabit: $isSleepHabit")
                
                if (isWaterHabit) {
                    // Show water background image and overlay
                    try {
                        ivBackgroundImage.setImageResource(R.drawable.newwater1)
                        ivBackgroundImage.visibility = android.view.View.VISIBLE
                        viewOverlay.visibility = android.view.View.VISIBLE
                        android.util.Log.d("HabitsAdapter", "Setting water background for: ${habit.name}")
                    } catch (e: Exception) {
                        android.util.Log.e("HabitsAdapter", "Error setting water background: ${e.message}")
                    }
                    
                    // Change text colors for better visibility
                    tvHabitName.setTextColor(android.graphics.Color.WHITE)
                    tvHabitProgress.setTextColor(android.graphics.Color.WHITE)
                    tvProgressPercentage.setTextColor(android.graphics.Color.WHITE)
                    
                    // Add text shadow for better readability
                    tvHabitName.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                    tvHabitProgress.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                    tvProgressPercentage.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                } else if (isMeditationHabit) {
                    // Show meditation background image and overlay
                    try {
                        ivBackgroundImage.setImageResource(R.drawable.medit)
                        ivBackgroundImage.visibility = android.view.View.VISIBLE
                        viewOverlay.visibility = android.view.View.VISIBLE
                        android.util.Log.d("HabitsAdapter", "Setting meditation background for: ${habit.name}")
                    } catch (e: Exception) {
                        android.util.Log.e("HabitsAdapter", "Error setting meditation background: ${e.message}")
                    }
                    
                    // Change text colors for better visibility
                    tvHabitName.setTextColor(android.graphics.Color.WHITE)
                    tvHabitProgress.setTextColor(android.graphics.Color.WHITE)
                    tvProgressPercentage.setTextColor(android.graphics.Color.WHITE)
                    
                    // Add text shadow for better readability
                    tvHabitName.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                    tvHabitProgress.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                    tvProgressPercentage.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                } else if (isStepsHabit) {
                    // Show steps background image and overlay
                    try {
                        ivBackgroundImage.setImageResource(R.drawable.print2)
                        ivBackgroundImage.visibility = android.view.View.VISIBLE
                        viewOverlay.visibility = android.view.View.VISIBLE
                        android.util.Log.d("HabitsAdapter", "Setting steps background for: ${habit.name}")
                    } catch (e: Exception) {
                        android.util.Log.e("HabitsAdapter", "Error setting steps background: ${e.message}")
                    }
                    
                    // Change text colors for better visibility
                    tvHabitName.setTextColor(android.graphics.Color.WHITE)
                    tvHabitProgress.setTextColor(android.graphics.Color.WHITE)
                    tvProgressPercentage.setTextColor(android.graphics.Color.WHITE)
                    
                    // Add text shadow for better readability
                    tvHabitName.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                    tvHabitProgress.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                    tvProgressPercentage.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                } else if (isFitnessHabit) {
                    // Show fitness background image and overlay
                    try {
                        ivBackgroundImage.setImageResource(R.drawable.fitness2)
                        ivBackgroundImage.visibility = android.view.View.VISIBLE
                        viewOverlay.visibility = android.view.View.VISIBLE
                        android.util.Log.d("HabitsAdapter", "Setting fitness background for: ${habit.name}")
                    } catch (e: Exception) {
                        android.util.Log.e("HabitsAdapter", "Error setting fitness background: ${e.message}")
                    }
                    
                    // Change text colors for better visibility
                    tvHabitName.setTextColor(android.graphics.Color.WHITE)
                    tvHabitProgress.setTextColor(android.graphics.Color.WHITE)
                    tvProgressPercentage.setTextColor(android.graphics.Color.WHITE)
                    
                    // Add text shadow for better readability
                    tvHabitName.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                    tvHabitProgress.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                    tvProgressPercentage.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                } else if (isSleepHabit) {
                    // Show sleep background image and overlay
                    try {
                        ivBackgroundImage.setImageResource(R.drawable.sleep2)
                        ivBackgroundImage.visibility = android.view.View.VISIBLE
                        viewOverlay.visibility = android.view.View.VISIBLE
                        android.util.Log.d("HabitsAdapter", "Setting sleep background for: ${habit.name}")
                    } catch (e: Exception) {
                        android.util.Log.e("HabitsAdapter", "Error setting sleep background: ${e.message}")
                    }
                    
                    // Change text colors for better visibility
                    tvHabitName.setTextColor(android.graphics.Color.WHITE)
                    tvHabitProgress.setTextColor(android.graphics.Color.WHITE)
                    tvProgressPercentage.setTextColor(android.graphics.Color.WHITE)
                    
                    // Add text shadow for better readability
                    tvHabitName.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                    tvHabitProgress.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                    tvProgressPercentage.setShadowLayer(2f, 1f, 1f, android.graphics.Color.BLACK)
                } else {
                    // Hide background image and overlay for other habits
                    ivBackgroundImage.visibility = android.view.View.GONE
                    viewOverlay.visibility = android.view.View.GONE
                    
                    // Use normal text colors
                    tvHabitName.setTextColor(itemView.context.getColor(R.color.text_primary))
                    tvHabitProgress.setTextColor(itemView.context.getColor(R.color.text_secondary))
                    tvProgressPercentage.setTextColor(itemView.context.getColor(R.color.text_secondary))
                    
                    // Remove text shadow for normal habits
                    tvHabitName.setShadowLayer(0f, 0f, 0f, android.graphics.Color.TRANSPARENT)
                    tvHabitProgress.setShadowLayer(0f, 0f, 0f, android.graphics.Color.TRANSPARENT)
                    tvProgressPercentage.setShadowLayer(0f, 0f, 0f, android.graphics.Color.TRANSPARENT)
                }
                
                // Use debounced click handlers for better performance
                btnHabitComplete.setOnClickListener { 
                    PerformanceUtils.debounce { onIncrementClick(habit.id) }()
                }
                
                btnHabitDecrement.setOnClickListener { 
                    PerformanceUtils.debounce { onDecrementClick(habit.id) }()
                }
                
                btnHabitEdit.setOnClickListener {
                    onEditClick(habit)
                }
                
                btnHabitDelete.setOnClickListener {
                    onDeleteClick(habit.id)
                }
            }
        }
    }

    /**
     * DiffUtil callback for efficient list updates
     */
    class HabitDiffCallback : DiffUtil.ItemCallback<Habit>() {
        override fun areItemsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem == newItem
        }
    }
}
