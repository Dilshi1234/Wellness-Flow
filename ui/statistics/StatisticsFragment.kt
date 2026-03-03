package com.wellnessflow.habbittracker.ui.statistics

// Import statements for Android framework components
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wellnessflow.habbittracker.R
import com.wellnessflow.habbittracker.databinding.FragmentStatisticsBinding

/**
 * StatisticsFragment - Fragment for displaying analytics and statistics
 * This fragment shows habit performance charts, mood analysis, and detailed
 * statistics for tracking user's wellness journey over time
 */
class StatisticsFragment : Fragment() {
    
    // View binding for safe access to UI elements
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    
    // ViewModel for managing statistics data and business logic
    private lateinit var viewModel: StatisticsViewModel
    // Adapter for displaying habit statistics in a list
    private lateinit var habitStatsAdapter: HabitStatsAdapter
    
    /**
     * onCreateView - Creates and returns the view hierarchy for this fragment
     * This method inflates the statistics layout
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
        // Inflate the statistics layout using data binding
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
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
        viewModel = ViewModelProvider(this)[StatisticsViewModel::class.java]
        
        // Setup RecyclerView for habit statistics
        setupRecyclerView()
        // Setup charts and interactive elements
        setupCharts()
        // Setup observers for ViewModel data changes
        observeViewModel()
    }
    
    /**
     * setupRecyclerView - Configures the RecyclerView for habit statistics display
     * Sets up the adapter and layout manager for the statistics list
     */
    private fun setupRecyclerView() {
        // Initialize the habit statistics adapter
        habitStatsAdapter = HabitStatsAdapter()
        // Configure RecyclerView with linear layout and adapter
        binding.rvHabitStats.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = habitStatsAdapter
        }
    }
    
    /**
     * setupCharts - Configures charts and interactive elements
     * Sets up time period buttons, chart type buttons, and initializes charts
     */
    private fun setupCharts() {
        // Setup mood time period buttons
        // 7-day mood chart button
        binding.btnMood7days.setOnClickListener {
            // Update mood chart to show 7-day data
            updateMoodChart(7)
            // Update button visual states
            updateButtonStates(getString(R.string.button_type_mood), 7)
        }
        
        // 30-day mood chart button
        binding.btnMood30days.setOnClickListener {
            // Update mood chart to show 30-day data
            updateMoodChart(30)
            // Update button visual states
            updateButtonStates(getString(R.string.button_type_mood), 30)
        }
        
        // Setup habit time period buttons
        // 7-day habit chart button
        binding.btnHabit7days.setOnClickListener {
            // Update habit chart to show 7-day data
            updateHabitChart(7)
            // Update button visual states
            updateButtonStates(getString(R.string.button_type_habit), 7)
        }
        
        // 30-day habit chart button
        binding.btnHabit30days.setOnClickListener {
            // Update habit chart to show 30-day data
            updateHabitChart(30)
            // Update button visual states
            updateButtonStates(getString(R.string.button_type_habit), 30)
        }
        
        // Setup habit chart type buttons
        // Trend chart button (shows overall trend)
        binding.btnChartTrend.setOnClickListener {
            // Update chart to show trend view
            updateHabitChartType(true)
            // Update chart type button states
            updateChartTypeButtons(true)
        }
        
        // Individual chart button (shows individual habit data)
        binding.btnChartIndividual.setOnClickListener {
            // Update chart to show individual view
            updateHabitChartType(false)
            // Update chart type button states
            updateChartTypeButtons(false)
        }
        
        // Setup habit stats recycler view
        binding.rvHabitStats.layoutManager = LinearLayoutManager(requireContext())
        habitStatsAdapter = HabitStatsAdapter()
        binding.rvHabitStats.adapter = habitStatsAdapter
        
        // Initialize with default views
        updateMoodChart(7)        // Start with 7-day mood chart
        updateHabitChart(7)       // Start with 7-day habit chart
        updateButtonStates(getString(R.string.button_type_mood), 7)     // Set mood buttons to 7-day state
        updateButtonStates(getString(R.string.button_type_habit), 7)    // Set habit buttons to 7-day state
        updateChartTypeButtons(true)      // Start with trend view
    }
    
    /**
     * updateButtonStates - Updates visual states of time period buttons
     * Changes button colors and styles based on selected time period
     * @param type - Type of buttons (getString(R.string.button_type_mood) or getString(R.string.button_type_habit))
     * @param selectedDays - Selected number of days (7 or 30)
     */
    private fun updateButtonStates(type: String, selectedDays: Int) {
        when (type) {
            getString(R.string.button_type_mood) -> {
                // Update mood time period buttons
                if (selectedDays == 7) {
                    // Highlight 7-day button
                    binding.btnMood7days.apply {
                        setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                        setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_blue))
                    }
                    binding.btnMood30days.apply {
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                        setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
                    }
                } else {
                    binding.btnMood30days.apply {
                        setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                        setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_blue))
                    }
                    binding.btnMood7days.apply {
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                        setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
                    }
                }
            }
            getString(R.string.button_type_habit) -> {
                if (selectedDays == 7) {
                    binding.btnHabit7days.apply {
                        setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                        setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_blue))
                    }
                    binding.btnHabit30days.apply {
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                        setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
                    }
                } else {
                    binding.btnHabit30days.apply {
                        setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                        setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_blue))
                    }
                    binding.btnHabit7days.apply {
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                        setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
                    }
                }
            }
        }
    }
    
    private fun observeViewModel() {
        viewModel.apply {
            weeklyCompletion.observe(viewLifecycleOwner) { completion ->
                binding.tvWeeklyCompletion.text = "${completion.toInt()}%"
            }
            
            weeklyStreak.observe(viewLifecycleOwner) { streak ->
                binding.tvWeeklyStreak.text = streak.toString()
            }
            
            habits.observe(viewLifecycleOwner) { habits ->
                // Update habit stats adapter
                val habitPerformanceData = viewModel.getHabitPerformanceData()
                val habitStats = habitPerformanceData.map { (name, completion, streak) ->
                    HabitStatsAdapter.HabitStat(
                        name = name,
                        streak = "$streak day streak",
                        completion = "${completion.toInt()}%"
                    )
                }
                habitStatsAdapter.updateHabitStats(habitStats)
                
                // Update charts when habits change
                updateCharts()
            }
            
            moodEntries.observe(viewLifecycleOwner) { moodEntries ->
                // Update charts when mood data changes
                updateCharts()
            }
        }
    }
    
    private fun updateMoodChart(days: Int) {
        // Update mood trend data
        val moodTrendData = viewModel.getMoodTrendData()
        if (moodTrendData.isNotEmpty()) {
            // Extract mood values from the pairs
            val moodValues = moodTrendData.map { it.second }
            
            // Update mood statistics
            val avgMood = moodValues.average()
            binding.tvAvgMood.text = String.format("%.1f", avgMood)
            
            // Update trend indicator with better calculation
            val trend = if (moodValues.size >= 2) {
                val recent = moodValues.takeLast(3).average()
                val previous = if (moodValues.size >= 6) {
                    moodValues.dropLast(3).takeLast(3).average()
                } else {
                    moodValues.first().toDouble()
                }
                val change = if (previous != 0.0) {
                    ((recent - previous) / previous * 100).toInt()
                } else {
                    0
                }
                if (change > 0) "+$change%" else "$change%"
            } else {
                getString(R.string.new_item)
            }
            binding.tvMoodTrend.text = trend
            
            // Update best day
            val bestDay = moodValues.maxOrNull() ?: 0.0
            binding.tvBestMood.text = String.format("%.1f", bestDay)
            
            // Update Mood Progress Chart
            updateMoodProgressChart(moodTrendData, days)
            
            // Show user feedback
            val periodText = if (days == 7) getString(R.string.last_seven_days_text) else getString(R.string.last_thirty_days_text)
            // You could add a toast or snackbar here: "Showing mood data for $periodText"
        } else {
            // No data available
            binding.tvAvgMood.text = getString(R.string.na)
            binding.tvMoodTrend.text = getString(R.string.no_data)
            binding.tvBestMood.text = getString(R.string.na)
            
            // Clear chart
            binding.moodProgressChart.updateData(emptyList())
        }
    }
    
    private fun updateMoodProgressChart(moodTrendData: List<Pair<String, Float>>, days: Int) {
        // Convert mood trend data to chart data points
        val chartData = moodTrendData.mapIndexed { index, (day, moodValue) ->
            // Get habit completion percentage for this day
            val habitCompletionData = viewModel.getHabitCompletionData()
            val habitProgress = if (habitCompletionData.isNotEmpty() && index < habitCompletionData.size) {
                habitCompletionData[index].second
            } else {
                0f
            }
            
            // Convert progress percentage to emoji based on dashboard progress ranges
            val progressPercentage = habitProgress.toInt()
            val emoji = getProgressEmoji(progressPercentage)
            
            // Use habit progress as the main progress indicator
            MoodProgressChart.MoodDataPoint(
                day = day,
                progress = habitProgress,
                emoji = emoji,
                moodValue = moodValue
            )
        }
        
        // Update the chart
        binding.moodProgressChart.updateData(chartData)
    }
    
    /**
     * Get emoji based on progress percentage (same as dashboard)
     * Maps different progress ranges to appropriate mood emojis
     */
    private fun getProgressEmoji(percentage: Int): String {
        return when {
            percentage >= 90 -> "🤳" // Excited/Amazing
            percentage >= 80 -> "😊" // Happy/Great
            percentage >= 70 -> "😎" // Cool/Good
            percentage >= 60 -> "🙂" // Satisfied/Okay
            percentage >= 50 -> "😐" // Neutral/Meh
            percentage >= 40 -> "😕" // Concerned/Not great
            percentage >= 30 -> "😞" // Disappointed/Poor
            percentage >= 20 -> "😔" // Sad/Bad
            percentage >= 10 -> "😭" // Very sad/Terrible
            else -> "😢" // Crying/Awful
        }
    }
    
    private fun updateHabitChart(days: Int) {
        // Update habit completion data
        val habitCompletionData = viewModel.getHabitCompletionData()
        if (habitCompletionData.isNotEmpty()) {
            // Extract completion values from the pairs
            val completionValues = habitCompletionData.map { it.second }
            
            // Update completion rate
            val completionRate = completionValues.average()
            binding.tvCompletionRate.text = "${completionRate.toInt()}%"
            
            // Update current streak (simplified - use weekly streak for now)
            val currentStreak = viewModel.weeklyStreak.value ?: 0
            binding.tvCurrentStreak.text = currentStreak.toString()
            
            // Update improvement indicator with better calculation
            val improvement = if (completionValues.size >= 2) {
                val recent = completionValues.takeLast(3).average()
                val previous = if (completionValues.size >= 6) {
                    completionValues.dropLast(3).takeLast(3).average()
                } else {
                    completionValues.first().toDouble()
                }
                val change = if (previous > 0.0) {
                    ((recent - previous) / previous * 100).toInt()
                } else {
                    0
                }
                if (change > 0) "+$change%" else if (change < 0) "$change%" else getString(R.string.same)
            } else {
                getString(R.string.new_item)
            }
            binding.tvHabitImprovement.text = improvement
            
            // Update habit performance chart
            updateHabitChartType(true) // Default to trend view
            
            // Show user feedback
            val periodText = if (days == 7) getString(R.string.last_seven_days_text) else getString(R.string.last_thirty_days_text)
            // You could add a toast or snackbar here: "Showing habit data for $periodText"
        } else {
            // No data available
            binding.tvCompletionRate.text = "0%"
            binding.tvCurrentStreak.text = "0"
            binding.tvHabitImprovement.text = getString(R.string.no_data)
            
            // Clear chart
            binding.habitPerformanceChart.updateData(emptyList(), true)
        }
    }
    
    private fun updateCharts() {
        // Update both charts with current data
        updateMoodChart(7)
        updateHabitChart(7)
    }
    
    private fun updateHabitChartType(isTrend: Boolean) {
        if (isTrend) {
            // Show overall trend data
            val habitCompletionData = viewModel.getHabitCompletionData()
            val chartData = habitCompletionData.map { (day, completion) ->
                HabitPerformanceChart.HabitDataPoint(
                    label = day,
                    value = completion,
                    isOverall = true
                )
            }
            binding.habitPerformanceChart.updateData(chartData, true) // true = line chart
        } else {
            // Show individual habit data
            val habitPerformanceData = viewModel.getHabitPerformanceData()
            val chartData = habitPerformanceData.map { (name, completion, _) ->
                HabitPerformanceChart.HabitDataPoint(
                    label = name,
                    value = completion,
                    isOverall = false
                )
            }
            binding.habitPerformanceChart.updateData(chartData, false) // false = bar chart
        }
    }
    
    private fun updateChartTypeButtons(isTrend: Boolean) {
        if (isTrend) {
            binding.btnChartTrend.apply {
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_blue))
            }
            binding.btnChartIndividual.apply {
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
            }
        } else {
            binding.btnChartIndividual.apply {
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_blue))
            }
            binding.btnChartTrend.apply {
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        viewModel.refreshStatistics()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}