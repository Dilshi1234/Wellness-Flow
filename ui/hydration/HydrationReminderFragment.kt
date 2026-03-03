package com.wellnessflow.habbittracker.ui.hydration

// Import statements for Android framework components
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.wellnessflow.habbittracker.R
import com.wellnessflow.habbittracker.databinding.FragmentHydrationReminderBinding
import com.wellnessflow.habbittracker.data.DataManager
import java.util.*

/**
 * HydrationReminderFragment - Fragment for managing hydration reminders
 * This fragment allows users to set up water intake reminders with customizable
 * intervals, time ranges, and tracking of daily water consumption
 */
class HydrationReminderFragment : Fragment() {

    // View binding for safe access to UI elements
    private var _binding: FragmentHydrationReminderBinding? = null
    private val binding get() = _binding!!
    
    // ViewModel for managing hydration reminder data and business logic
    private lateinit var viewModel: HydrationReminderViewModel
    // DataManager for persistent storage operations
    private lateinit var dataManager: DataManager
    
    // Reminder configuration variables
    private var intervalUnit = getString(R.string.interval_unit_hours) // "hours" or "minutes" - default to hours
    private var startTime = Calendar.getInstance() // Use current time as default start time
    private var endTime = Calendar.getInstance().apply {
        add(Calendar.HOUR_OF_DAY, 12) // Default end time: 12 hours from now
    }

    /**
     * onCreateView - Creates and returns the view hierarchy for this fragment
     * This method inflates the hydration reminder layout
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
        // Inflate the hydration reminder layout using data binding
        _binding = FragmentHydrationReminderBinding.inflate(inflater, container, false)
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
        viewModel = ViewModelProvider(this)[HydrationReminderViewModel::class.java]
        // Initialize DataManager for persistent storage
        dataManager = DataManager(requireContext())
        
        // Setup UI components and click listeners
        setupUI()
        // Setup observers for ViewModel data changes
        setupObservers()
        // Load saved settings from storage
        loadSettings()
    }

    /**
     * setupUI - Configures UI components and click listeners
     * Sets up all interactive elements including toggles, buttons, and input fields
     */
    private fun setupUI() {
        // Enable/Disable reminder toggle switch
        binding.switchEnableReminders.setOnCheckedChangeListener { _, isChecked ->
            // Update ViewModel with new reminder state
            viewModel.setRemindersEnabled(isChecked)
            // Update UI state based on toggle
            updateUIState(isChecked)
        }

        // Interval unit selection button (hours/minutes)
        binding.btnIntervalUnit.setOnClickListener {
            // Show dialog to select interval unit
            showIntervalUnitDialog()
        }

        // Start time selection button
        binding.btnStartTime.setOnClickListener {
            // Show time picker for start time selection
            showTimePicker(startTime) { selectedTime ->
                // Update start time with selected value
                startTime = selectedTime
                // Update button text with formatted time
                binding.btnStartTime.text = formatTime(selectedTime)
                // Update ViewModel with new start time
                viewModel.setStartTime(selectedTime)
            }
        }

        // End time selection button
        binding.btnEndTime.setOnClickListener {
            // Show time picker for end time selection
            showTimePicker(endTime) { selectedTime ->
                // Update end time with selected value
                endTime = selectedTime
                // Update button text with formatted time
                binding.btnEndTime.text = formatTime(selectedTime)
                // Update ViewModel with new end time
                viewModel.setEndTime(selectedTime)
            }
        }

        // Save settings button
        binding.btnSaveSettings.setOnClickListener {
            // Save all settings to persistent storage
            saveSettings()
        }

        // Add glass button for water intake tracking
        binding.btnAddGlass.setOnClickListener {
            // Increment water intake count
            viewModel.addGlass()
        }

        // Subtract glass button for water intake tracking
        binding.btnSubtractGlass.setOnClickListener {
            // Decrement water intake count
            viewModel.subtractGlass()
        }
    }

    /**
     * setupObservers - Sets up observers for ViewModel data changes
     * Observes water intake, next reminder time, and reminder enabled state
     */
    private fun setupObservers() {
        // Observe water intake changes
        viewModel.waterIntake.observe(viewLifecycleOwner) { intake ->
            // Update water intake display with current count and goal
            binding.tvWaterIntake.text = "$intake / 8 glasses"
            // Calculate and display hydration percentage
            val percentage = (intake * 100 / 8).toInt()
            binding.tvHydrationPercentage.text = "$percentage%"
        }

        // Observe next reminder time changes
        viewModel.nextReminderTime.observe(viewLifecycleOwner) { nextTime ->
            // Update next reminder display with formatted time
            binding.tvNextReminder.text = formatTime(nextTime)
            // Calculate and display time until next reminder
            val timeUntil = getTimeUntil(nextTime)
            binding.tvReminderStatus.text = timeUntil
        }

        // Observe reminder enabled state changes
        viewModel.remindersEnabled.observe(viewLifecycleOwner) { enabled ->
            // Update switch state to match ViewModel
            binding.switchEnableReminders.isChecked = enabled
            // Update UI state based on enabled status
            updateUIState(enabled)
        }
    }

    /**
     * loadSettings - Loads saved reminder settings from storage
     * Restores user's previous configuration including intervals, times, and enabled state
     */
    private fun loadSettings() {
        // Load saved settings from ViewModel
        val settings = viewModel.getReminderSettings()
        // Set interval value in input field
        binding.etIntervalValue.setText(settings.intervalValue.toString())
        // Set interval unit and update button text
        intervalUnit = settings.intervalUnit
        binding.btnIntervalUnit.text = intervalUnit
        
        // Use current time as default if no saved settings exist
        if (settings.startTime.timeInMillis == 0L) {
            // Set start time to current time
            startTime = Calendar.getInstance()
            // Set end time to 12 hours from now
            endTime = Calendar.getInstance().apply {
                add(Calendar.HOUR_OF_DAY, 12) // 12 hours from now
            }
        } else {
            // Use saved start and end times
            startTime = settings.startTime
            endTime = settings.endTime
        }
        
        // Update button texts with loaded times
        binding.btnStartTime.text = formatTime(startTime)
        binding.btnEndTime.text = formatTime(endTime)
        
        // Set switch state and update UI accordingly
        binding.switchEnableReminders.isChecked = settings.enabled
        updateUIState(settings.enabled)
    }

    /**
     * updateUIState - Updates UI elements based on reminder enabled state
     * Dims or enables UI elements when reminders are disabled/enabled
     * @param enabled - Whether reminders are enabled
     */
    private fun updateUIState(enabled: Boolean) {
        // Set alpha value based on enabled state (1.0 = fully visible, 0.5 = dimmed)
        val alpha = if (enabled) 1.0f else 0.5f
        // Apply alpha to interval value input field
        // Apply alpha to all UI elements
        binding.etIntervalValue.alpha = alpha
        binding.btnIntervalUnit.alpha = alpha
        binding.btnStartTime.alpha = alpha
        binding.btnEndTime.alpha = alpha
        binding.btnSaveSettings.alpha = alpha
        
        // Enable/disable all UI elements based on state
        binding.etIntervalValue.isEnabled = enabled
        binding.btnIntervalUnit.isEnabled = enabled
        binding.btnStartTime.isEnabled = enabled
        binding.btnEndTime.isEnabled = enabled
        binding.btnSaveSettings.isEnabled = enabled
    }

    /**
     * showIntervalUnitDialog - Shows dialog to select interval unit (minutes/hours)
     * Allows user to choose between minute and hour intervals for reminders
     */
    private fun showIntervalUnitDialog() {
        // Define available interval unit options
        val options = arrayOf(getString(R.string.interval_unit_minutes), getString(R.string.interval_unit_hours))
        // Determine current selection index
        val currentSelection = if (intervalUnit == getString(R.string.interval_unit_minutes)) 0 else 1
        
        // Create and show Material Design dialog
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.select_interval_unit))
            .setSingleChoiceItems(options, currentSelection) { _, which ->
                // Update interval unit when user selects option
                intervalUnit = options[which]
                // Update button text to show selected unit
                binding.btnIntervalUnit.text = intervalUnit
            }
            .setPositiveButton(getString(R.string.ok), null)
            .show()
    }

    /**
     * showTimePicker - Shows time picker dialog for selecting time
     * @param currentTime - Current time to display in picker
     * @param onTimeSelected - Callback function called when time is selected
     */
    private fun showTimePicker(currentTime: Calendar, onTimeSelected: (Calendar) -> Unit) {
        // Create Material Design time picker
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)  // Use 12-hour format
            .setHour(currentTime.get(Calendar.HOUR_OF_DAY))  // Set current hour
            .setMinute(currentTime.get(Calendar.MINUTE))     // Set current minute
            .setTitleText(getString(R.string.select_time))
            .build()

        // Handle positive button click (time selection)
        timePicker.addOnPositiveButtonClickListener {
            // Create Calendar object with selected time
            val selectedTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, timePicker.hour)
                set(Calendar.MINUTE, timePicker.minute)
                set(Calendar.SECOND, 0)  // Reset seconds to 0
            }
            // Call callback with selected time
            onTimeSelected(selectedTime)
        }

        // Show the time picker dialog
        timePicker.show(parentFragmentManager, "time_picker")
    }

    /**
     * formatTime - Formats Calendar object to readable time string
     * Converts 24-hour format to 12-hour format with AM/PM
     * @param calendar - Calendar object to format
     * @return String - Formatted time string (e.g., "2:30 PM")
     */
    private fun formatTime(calendar: Calendar): String {
        // Get hour and minute from calendar
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        // Determine AM/PM based on hour
        val amPm = if (hour < 12) getString(R.string.am) else getString(R.string.pm)
        // Convert 24-hour to 12-hour format
        val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        // Return formatted time string
        return String.format("%d:%02d %s", displayHour, minute, amPm)
    }

    /**
     * getTimeUntil - Calculates time remaining until target time
     * Returns human-readable string showing time until next reminder
     * @param targetTime - Target time to calculate difference from
     * @return String - Time remaining string (e.g., "in 2 hours 30 minutes")
     */
    private fun getTimeUntil(targetTime: Calendar): String {
        // Get current time
        val now = Calendar.getInstance()
        // Calculate difference in milliseconds
        val diff = targetTime.timeInMillis - now.timeInMillis
        
        // If time has passed, return "Now"
        if (diff <= 0) {
            return getString(R.string.now)
        }
        
        // Calculate days, hours, and minutes from milliseconds
        val days = diff / (1000 * 60 * 60 * 24)
        val hours = (diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
        val minutes = (diff % (1000 * 60 * 60)) / (1000 * 60)
        
        // Return appropriate time string based on duration
        return when {
            days > 0L -> getString(R.string.in_days_hours, days, if (days > 1L) "s" else "", hours, if (hours != 1L) "s" else "")
            hours > 0L -> getString(R.string.in_hours_minutes, hours, if (hours != 1L) "s" else "", minutes, if (minutes != 1L) "s" else "")
            minutes > 0L -> getString(R.string.in_minutes, minutes, if (minutes != 1L) "s" else "")
            else -> getString(R.string.now)
        }
    }

    /**
     * saveSettings - Saves reminder settings and schedules/cancels alarms
     * Validates input, creates settings object, and manages alarm scheduling
     */
    private fun saveSettings() {
        // Get interval value from input field, default to 1 if invalid
        val intervalValue = binding.etIntervalValue.text.toString().toIntOrNull() ?: 1
        
        // Validate interval value range
        if (intervalValue < 1 || intervalValue > 24) {
            Toast.makeText(requireContext(), getString(R.string.interval_must_be_between), Toast.LENGTH_SHORT).show()
            return
        }
        
        // Create settings object with current configuration
        val settings = HydrationReminderSettings(
            enabled = binding.switchEnableReminders.isChecked,
            intervalValue = intervalValue,
            intervalUnit = intervalUnit,
            startTime = startTime,
            endTime = endTime
        )
        
        // Save settings to ViewModel and persistent storage
        viewModel.saveReminderSettings(settings)
        
        // Handle alarm scheduling based on enabled state
        if (settings.enabled) {
            // Schedule reminders and show success message
            scheduleReminders(settings)
            Toast.makeText(requireContext(), getString(R.string.hydration_reminders_enabled), Toast.LENGTH_SHORT).show()
        } else {
            // Cancel reminders and show disabled message
            cancelReminders()
            Toast.makeText(requireContext(), getString(R.string.hydration_reminders_disabled), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * scheduleReminders - Schedules repeating alarms for hydration reminders
     * Creates notification channel and sets up AlarmManager for recurring notifications
     * @param settings - HydrationReminderSettings containing reminder configuration
     */
    private fun scheduleReminders(settings: HydrationReminderSettings) {
        // Create notification channel for Android 8.0+
        createNotificationChannel()
        
        // Get AlarmManager system service
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Create intent for HydrationReminderReceiver
        val intent = Intent(requireContext(), HydrationReminderReceiver::class.java)
        // Create pending intent for broadcast
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Cancel any existing alarms first
        alarmManager.cancel(pendingIntent)
        
        // Return early if reminders are disabled
        if (!settings.enabled) return
        
        // Calculate interval in milliseconds based on unit
        val intervalMs = when (settings.intervalUnit) {
            getString(R.string.interval_unit_minutes) -> settings.intervalValue * 60 * 1000L
            getString(R.string.interval_unit_hours) -> settings.intervalValue * 60 * 60 * 1000L
            else -> 1 * 60 * 60 * 1000L // Default 1 hour
        }
        
        // Schedule repeating alarm starting at specified time
        val startTimeMs = settings.startTime.timeInMillis
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,  // Wake up device for alarm
            startTimeMs,              // Start time in milliseconds
            intervalMs,               // Repeat interval in milliseconds
            pendingIntent             // Pending intent to execute
        )
    }

    /**
     * cancelReminders - Cancels all scheduled hydration reminder alarms
     * Removes existing alarms from AlarmManager
     */
    private fun cancelReminders() {
        // Get AlarmManager system service
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Create intent for HydrationReminderReceiver
        val intent = Intent(requireContext(), HydrationReminderReceiver::class.java)
        // Create pending intent for broadcast
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Cancel the alarm
        alarmManager.cancel(pendingIntent)
    }

    /**
     * createNotificationChannel - Creates notification channel for Android 8.0+
     * Required for displaying notifications on modern Android versions
     */
    private fun createNotificationChannel() {
        // Check if Android version supports notification channels (API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create notification channel with ID, name, and importance
            val channel = NotificationChannel(
                getString(R.string.hydration_reminders_channel_id),        // Channel ID
                getString(R.string.hydration_reminders_channel),        // Channel name
                NotificationManager.IMPORTANCE_DEFAULT  // Importance level
            ).apply {
                description = getString(R.string.hydration_reminders_description)  // Channel description
            }
            
            // Get notification manager and create the channel
            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
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
