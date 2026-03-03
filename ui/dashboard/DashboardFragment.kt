package com.wellnessflow.habbittracker.ui.dashboard

// Import statements for Android framework components
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.wellnessflow.habbittracker.R
import com.wellnessflow.habbittracker.databinding.FragmentDashboardBinding
import com.wellnessflow.habbittracker.utils.AnimationUtils

/**
 * DashboardFragment - The main dashboard screen of the WellnessFlow app
 * This fragment displays the user's daily progress, habits overview, and quick actions
 * It serves as the central hub where users can see their wellness journey at a glance
 */
class DashboardFragment : Fragment() {
    
    // View binding for safe access to UI elements
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    
    // ViewModel for managing dashboard data and business logic
    private lateinit var viewModel: DashboardViewModel
    // Adapter for displaying habits in a grid layout
    private lateinit var habitOverviewAdapter: HabitOverviewAdapter
    
    /**
     * onCreateView - Creates and returns the view hierarchy for this fragment
     * This method inflates the layout and returns the root view
     * @param inflater - LayoutInflater to inflate views
     * @param container - Parent view group
     * @param savedInstanceState - Saved instance state
     * @return View - The inflated view hierarchy
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the dashboard layout using data binding
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    /**
     * onViewCreated - Called after the view has been created
     * This method initializes the UI components and sets up observers
     * @param view - The created view
     * @param savedInstanceState - Saved instance state
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel using ViewModelProvider
        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]

        // Setup RecyclerView for habits display
        setupRecyclerView()
        // Setup UI components and click listeners
        setupUI()
        // Setup observers for ViewModel data
        observeViewModel()
        // Setup animations for smooth user experience
        setupAnimations()
    }
    
    /**
     * setupRecyclerView - Configures the RecyclerView for habits display
     * Sets up the adapter and layout manager for the habits grid
     */
    private fun setupRecyclerView() {
        // Initialize the habit overview adapter
        habitOverviewAdapter = HabitOverviewAdapter()
        // Configure RecyclerView with grid layout and adapter
        binding.rvHabitsOverview.apply {
            // Use GridLayoutManager with 2 columns for habits display
            layoutManager = GridLayoutManager(context, 2)
            // Set the adapter to display habit data
            adapter = habitOverviewAdapter
        }
    }
    
    /**
     * setupUI - Configures UI components and click listeners
     * Sets up greeting, quick action buttons, and other interactive elements
     */
    private fun setupUI() {
        binding.apply {
            // Set time-based greeting based on current time
            tvGreeting.text = getTimeBasedGreeting()
            
            // Quick action buttons for common habit updates
                // Water intake quick action button
                btnSteps.setOnClickListener { view ->
                    // Add button press animation for user feedback
                    AnimationUtils.buttonPress(view)
                    // Find water habit and increment its progress
                    viewModel.habits.value?.find { habit -> habit.name == getString(R.string.habit_water_intake) }?.let { habit ->
                        // Increment habit progress in ViewModel
                        viewModel.incrementHabitProgress(habit.id)
                        // Add bounce animation to completion percentage
                        AnimationUtils.bounce(binding.tvCompletionPercentage)
                    }
                }

                // Steps quick action button
                btnQuickSteps.setOnClickListener { view ->
                    // Add button press animation for user feedback
                    AnimationUtils.buttonPress(view)
                    // Find steps habit and increment its progress
                    viewModel.habits.value?.find { habit -> habit.name == getString(R.string.habit_steps_name) }?.let { habit ->
                        // Increment habit progress in ViewModel
                        viewModel.incrementHabitProgress(habit.id)
                        // Add bounce animation to completion percentage
                        AnimationUtils.bounce(binding.tvCompletionPercentage)
                    }
                }
            
            // Mood logging button (placeholder for future navigation)
            btnLogMood.setOnClickListener {
                // Navigate to mood journal
                // This will be implemented when we add navigation
            }
        }
    }
    
    /**
     * observeViewModel - Sets up observers for ViewModel data changes
     * Observes habits, completion percentage, and mood data
     */
    private fun observeViewModel() {
        viewModel.apply {
            // Observe habits data changes
            habits.observe(viewLifecycleOwner) { habits ->
                // Update adapter when habits data changes
                habitOverviewAdapter.updateHabits(habits)
            }
            
            // Observe completion percentage changes
            completionPercentage.observe(viewLifecycleOwner) { percentage ->
                // Convert percentage to integer for display
                val percentageInt = percentage.toInt()
                // Update completion percentage text
                binding.tvCompletionPercentage.text = getString(R.string.completion_percentage, percentageInt)
                
                // Update mood display based on progress percentage
                updateProgressBasedMood(percentageInt)
            }
            
            // Observe completed habits count
            completedHabitsCount.observe(viewLifecycleOwner) { completed ->
                // Observe total habits count
                totalHabitsCount.observe(viewLifecycleOwner) { total ->
                    // Update completed/total habits display
                    binding.tvCompletedHabits.text = getString(R.string.habits_completed, completed, total)
                }
            }
            
            // Keep the original mood entry logic as fallback
            // Observe today's mood entry
            todayMood.observe(viewLifecycleOwner) { moodEntry ->
                // Only update if no progress-based mood is set (fallback to default)
                if (moodEntry != null && binding.tvCurrentMood.text == getString(R.string.mood_neutral_emoji_alt)) {
                    // Update mood emoji from logged entry
                    binding.tvCurrentMood.text = moodEntry.emoji
                    // Update mood description from logged entry
                    binding.tvMoodDescription.text = moodEntry.description
                    // Format timestamp for display
                    val timeFormat = java.text.SimpleDateFormat(getString(R.string.time_format_pattern), java.util.Locale.getDefault())
                    val timeString = timeFormat.format(java.util.Date(moodEntry.timestamp))
                    // Update mood time display
                    binding.tvMoodTime.text = getString(R.string.logged_at, timeString)
                }
            }
        }
    }
    
    /**
     * Update mood display based on progress percentage
     * This method dynamically updates the mood based on daily progress
     * @param percentage - The completion percentage (0-100)
     */
    private fun updateProgressBasedMood(percentage: Int) {
        // Get appropriate emoji based on percentage
        val emoji = getProgressEmoji(percentage)
        // Get appropriate description based on percentage
        val description = getProgressDescription(percentage)
        
        // Update mood display elements
        binding.apply {
            tvCurrentMood.text = emoji
            tvMoodDescription.text = description
            tvMoodTime.text = getString(R.string.mood_progress_based)
        }
    }

    /**
     * setupAnimations - Configures entrance animations for UI elements
     * Adds smooth animations when the dashboard loads
     */
    private fun setupAnimations() {
        // Animate cards on load with staggered timing
        binding.apply {
            // Welcome card animation (first)
            AnimationUtils.fadeInScale(cardWelcome, 400)
            // Progress card animation (second)
            AnimationUtils.fadeInScale(cardProgress, 500)
            // Mood card animation (third)
            AnimationUtils.fadeInScale(cardMood, 600)
            // Habits list animation (last)
            AnimationUtils.slideInFromTop(rvHabitsOverview, 700)
        }
    }
    
    /**
     * Get emoji based on progress percentage
     * Maps different progress ranges to appropriate mood emojis
     * @param percentage - The completion percentage (0-100)
     * @return String - The appropriate emoji for the percentage
     */
    private fun getProgressEmoji(percentage: Int): String {
        return when {
            percentage >= 90 -> getString(R.string.mood_excited) // 🤳 (Excited/Amazing)
            percentage >= 80 -> getString(R.string.mood_happy_emoji) // 😊 (Happy/Great)
            percentage >= 70 -> getString(R.string.mood_cool) // 😎 (Cool/Good)
            percentage >= 60 -> getString(R.string.mood_satisfied) // 🙂 (Satisfied/Okay)
            percentage >= 50 -> getString(R.string.mood_neutral_emoji_alt) // 😐 (Neutral/Meh)
            percentage >= 40 -> getString(R.string.mood_concerned) // 😕 (Concerned/Not great)
            percentage >= 30 -> getString(R.string.mood_disappointed) // 😞 (Disappointed/Poor)
            percentage >= 20 -> getString(R.string.mood_sad_emoji) // 😔 (Sad/Bad)
            percentage >= 10 -> getString(R.string.mood_very_sad) // 😭 (Very sad/Terrible)
            else -> getString(R.string.mood_crying) // 😢 (Crying/Awful)
        }
    }
    
    /**
     * Get progress description based on percentage
     * Provides encouraging messages based on completion percentage
     * @param percentage - The completion percentage (0-100)
     * @return String - The appropriate description for the percentage
     */
    private fun getProgressDescription(percentage: Int): String {
        return when {
            percentage >= 90 -> getString(R.string.progress_amazing)
            percentage >= 80 -> getString(R.string.progress_great)
            percentage >= 70 -> getString(R.string.progress_good)
            percentage >= 60 -> getString(R.string.progress_not_bad)
            percentage >= 50 -> getString(R.string.progress_halfway)
            percentage >= 40 -> getString(R.string.progress_keep_trying)
            percentage >= 30 -> getString(R.string.progress_can_do_better)
            percentage >= 20 -> getString(R.string.progress_dont_give_up)
            percentage >= 10 -> getString(R.string.progress_keep_pushing)
            else -> getString(R.string.progress_start_fresh)
        }
    }
    
    /**
     * Get time-based greeting based on current hour
     * Returns appropriate greeting based on time of day
     * @return String - The appropriate greeting for the current time
     */
    private fun getTimeBasedGreeting(): String {
        // Get current calendar instance
        val calendar = java.util.Calendar.getInstance()
        // Get current hour (24-hour format)
        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        
        // Return appropriate greeting based on hour
        return when (hour) {
            in 5..11 -> getString(R.string.greeting_morning)    // 5 AM - 11 AM
            in 12..17 -> getString(R.string.greeting_afternoon) // 12 PM - 5 PM
            in 18..21 -> getString(R.string.greeting_evening)    // 6 PM - 9 PM
            else -> getString(R.string.greeting_night)          // 10 PM - 4 AM
        }
    }

    /**
     * onResume - Called when the fragment becomes visible
     * Refreshes data when user returns to the dashboard
     */
    override fun onResume() {
        super.onResume()
        // Refresh data to ensure it's up to date
        viewModel.refreshData()
    }
    
    /**
     * onDestroyView - Called when the view is being destroyed
     * Cleans up the binding to prevent memory leaks
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Clear binding to prevent memory leaks
        _binding = null
    }
}
