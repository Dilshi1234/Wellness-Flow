package com.wellnessflow.habbittracker.ui.calendar

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wellnessflow.habbittracker.databinding.FragmentCalendarBinding
import java.util.*

class CalendarFragment : Fragment() {
    
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    
    // Removed ViewModel to prevent ANR - using simple local state instead
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var reminderAdapter: ReminderAdapter
    
    private val calendar = Calendar.getInstance()
    private val reminders = mutableListOf<Reminder>()
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private var selectedDate: Calendar? = null
    
    companion object {
        private const val REMINDERS_KEY = "calendar_reminders"
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("calendar_prefs", android.content.Context.MODE_PRIVATE)
        
        // Load existing reminders
        loadReminders()
        
        // Simple initialization without ViewModel to prevent ANR
        setupRecyclerViews()
        setupUI()
        updateCalendarDisplay()
        
        // Force display all reminders initially for testing
        android.util.Log.d("CalendarFragment", "Initial setup - showing all reminders")
        reminderAdapter.updateReminders(reminders)
        binding.tvReminderCount.text = reminders.size.toString()
        
        updateRemindersDisplay()
    }
    
    private fun setupRecyclerViews() {
        // Calendar grid
        calendarAdapter = CalendarAdapter { day ->
            // Handle day click - filter reminders by selected date
            when (day) {
                is CalendarDay.Day -> {
                    selectedDate = Calendar.getInstance().apply {
                        set(Calendar.YEAR, calendar.get(Calendar.YEAR))
                        set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                        set(Calendar.DAY_OF_MONTH, day.dayNumber)
                    }
                    android.util.Log.d("CalendarFragment", "Day clicked: ${day.dayNumber}")
                    android.util.Log.d("CalendarFragment", "Selected date: ${getFormattedDate(selectedDate!!)}")
                    updateRemindersDisplay()
                    android.widget.Toast.makeText(requireContext(), "Selected: ${getFormattedDate(selectedDate!!)} - Showing reminders", android.widget.Toast.LENGTH_LONG).show()
                }
                is CalendarDay.Empty -> {
                    // Do nothing for empty days
                }
            }
        }
        
        binding.rvCalendar.apply {
            layoutManager = GridLayoutManager(context, 7)
            adapter = calendarAdapter
        }
        
        // Reminders list
        reminderAdapter = ReminderAdapter(
            onReminderToggle = { reminder ->
                // Handle reminder toggle - update enabled status
                val updatedReminder = reminder.copy(enabled = !reminder.enabled)
                val index = reminders.indexOfFirst { it.id == reminder.id }
                if (index != -1) {
                    reminders[index] = updatedReminder
                    saveReminders()
                    updateRemindersDisplay()
                }
            },
            onReminderDelete = { reminder ->
                // Handle reminder deletion - remove from list
                reminders.removeAll { it.id == reminder.id }
                saveReminders()
                updateRemindersDisplay()
            }
        )
        
        binding.rvReminders.apply {
            adapter = reminderAdapter
        }
    }
    
    private fun setupUI() {
        binding.apply {
            // Month navigation
            btnPrevMonth.setOnClickListener {
                calendar.add(Calendar.MONTH, -1)
                updateCalendarDisplay()
            }
            
            btnNextMonth.setOnClickListener {
                calendar.add(Calendar.MONTH, 1)
                updateCalendarDisplay()
            }
            
            // Add reminder button
            btnAddReminder.setOnClickListener {
                // Navigate to add reminder dialog/fragment
                showAddReminderDialog()
            }
        }
    }
    
    // Removed observeViewModel - not using ViewModel anymore
    
    private fun generateSimpleCalendarDays() {
        val days = mutableListOf<CalendarDay>()
        
        // Get the first day of the month and adjust to start of week
        val firstDayOfMonth = Calendar.getInstance().apply {
            set(Calendar.YEAR, calendar.get(Calendar.YEAR))
            set(Calendar.MONTH, calendar.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, 1)
        }
        
        val firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK)
        val daysInMonth = firstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        // Add empty days for the beginning of the week
        for (i in 1 until firstDayOfWeek) {
            days.add(CalendarDay.Empty)
        }
        
        // Add days of the month with simple status
        for (day in 1..daysInMonth) {
            days.add(CalendarDay.Day(day, HabitStatus.NONE))
        }
        
        calendarAdapter.updateDays(days)
    }
    
    private fun updateCalendarDisplay() {
        val monthYear = java.text.SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            .format(calendar.time)
        
        binding.tvMonthYear.text = monthYear
        binding.tvCurrentMonth.text = monthYear
        
        // Generate simple calendar days for the current month
        generateSimpleCalendarDays()
    }
    
    private fun showAddReminderDialog() {
        val builder = android.app.AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(com.wellnessflow.habbittracker.R.layout.dialog_add_reminder, null)
        
        val etTitle = dialogView.findViewById<android.widget.EditText>(com.wellnessflow.habbittracker.R.id.etReminderTitle)
        val etDescription = dialogView.findViewById<android.widget.EditText>(com.wellnessflow.habbittracker.R.id.etReminderDescription)
        val etReminderDate = dialogView.findViewById<android.widget.EditText>(com.wellnessflow.habbittracker.R.id.etReminderDate)
        val etReminderTime = dialogView.findViewById<android.widget.EditText>(com.wellnessflow.habbittracker.R.id.etReminderTime)
        val rgRepeatOptions = dialogView.findViewById<android.widget.RadioGroup>(com.wellnessflow.habbittracker.R.id.rgRepeatOptions)
        val rbNoRepeat = dialogView.findViewById<com.google.android.material.radiobutton.MaterialRadioButton>(com.wellnessflow.habbittracker.R.id.rbNoRepeat)
        val rbDaily = dialogView.findViewById<com.google.android.material.radiobutton.MaterialRadioButton>(com.wellnessflow.habbittracker.R.id.rbDaily)
        val rbWeekly = dialogView.findViewById<com.google.android.material.radiobutton.MaterialRadioButton>(com.wellnessflow.habbittracker.R.id.rbWeekly)
        val rbCustom = dialogView.findViewById<com.google.android.material.radiobutton.MaterialRadioButton>(com.wellnessflow.habbittracker.R.id.rbCustom)
        val tilCustomInterval = dialogView.findViewById<com.google.android.material.textfield.TextInputLayout>(com.wellnessflow.habbittracker.R.id.tilCustomInterval)
        val etCustomInterval = dialogView.findViewById<android.widget.EditText>(com.wellnessflow.habbittracker.R.id.etCustomInterval)
        
        // Set default date to today
        val today = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date())
        etReminderDate.setText(today)
        
        // Set default time to current time + 1 hour
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.HOUR_OF_DAY, 1)
        val defaultTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(calendar.time)
        etReminderTime.setText(defaultTime)
        
        // Date picker
        etReminderDate.setOnClickListener {
            val calendar = java.util.Calendar.getInstance()
            val datePickerDialog = android.app.DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = java.util.Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }
                    val dateString = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(selectedDate.time)
                    etReminderDate.setText(dateString)
                },
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
        
        // Time picker
        etReminderTime.setOnClickListener {
            val calendar = java.util.Calendar.getInstance()
            val timePickerDialog = android.app.TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    val timeString = String.format("%02d:%02d", hourOfDay, minute)
                    etReminderTime.setText(timeString)
                },
                calendar.get(java.util.Calendar.HOUR_OF_DAY),
                calendar.get(java.util.Calendar.MINUTE),
                true // 24 hour format
            )
            timePickerDialog.show()
        }
        
        // Handle repeat options with RadioGroup
        rgRepeatOptions.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                com.wellnessflow.habbittracker.R.id.rbCustom -> {
                    tilCustomInterval.visibility = android.view.View.VISIBLE
                }
                else -> {
                    tilCustomInterval.visibility = android.view.View.GONE
                }
            }
        }
        
        builder.setView(dialogView)
            .setTitle("Add New Reminder")
            .setPositiveButton("Add") { _, _ ->
                val title = etTitle.text.toString().trim()
                val description = etDescription.text.toString().trim()
                val date = etReminderDate.text.toString().trim()
                val time = etReminderTime.text.toString().trim()
                
                if (title.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()) {
                    // Determine repeat option using RadioGroup checked ID
                    val repeatOption = when (rgRepeatOptions.checkedRadioButtonId) {
                        com.wellnessflow.habbittracker.R.id.rbNoRepeat -> "No Repeat"
                        com.wellnessflow.habbittracker.R.id.rbDaily -> "Daily"
                        com.wellnessflow.habbittracker.R.id.rbWeekly -> "Weekly"
                        com.wellnessflow.habbittracker.R.id.rbCustom -> etCustomInterval.text.toString().trim().ifEmpty { "Custom" }
                        else -> "No Repeat"
                    }
                    
                    val reminder = Reminder(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        description = description,
                        time = "$date at $time ($repeatOption)",
                        enabled = true
                    )
                    
                    // Add reminder to list and save
                    reminders.add(reminder)
                    saveReminders()
                    updateRemindersDisplay()
                    
                    android.widget.Toast.makeText(requireContext(), "Reminder added: $title", android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    android.widget.Toast.makeText(requireContext(), "Please fill in title, date and time", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun loadReminders() {
        try {
            val remindersJson = sharedPreferences.getString(REMINDERS_KEY, null)
            if (remindersJson != null) {
                val type = object : TypeToken<List<Reminder>>() {}.type
                val loadedReminders: List<Reminder> = gson.fromJson(remindersJson, type)
                reminders.clear()
                reminders.addAll(loadedReminders)
            } else {
                // Add some sample reminders for testing if none exist
                addSampleReminders()
            }
        } catch (e: Exception) {
            // Handle error - clear corrupted data
            reminders.clear()
            sharedPreferences.edit().remove(REMINDERS_KEY).apply()
            addSampleReminders()
        }
    }
    
    private fun addSampleReminders() {
        val sampleReminders = listOf(
            Reminder(
                id = UUID.randomUUID().toString(),
                title = "Drink Water",
                description = "Stay hydrated throughout the day",
                time = "Oct 01, 2025 at 09:00 (Daily)",
                enabled = true
            ),
            Reminder(
                id = UUID.randomUUID().toString(),
                title = "Exercise",
                description = "Morning workout session",
                time = "Oct 03, 2025 at 07:00 (Daily)",
                enabled = true
            ),
            Reminder(
                id = UUID.randomUUID().toString(),
                title = "Read Book",
                description = "Evening reading time",
                time = "Oct 05, 2025 at 20:00 (Daily)",
                enabled = true
            ),
            Reminder(
                id = UUID.randomUUID().toString(),
                title = "Meditation",
                description = "Morning meditation session",
                time = "Oct 03, 2025 at 06:00 (Daily)",
                enabled = true
            )
        )
        reminders.addAll(sampleReminders)
        saveReminders()
    }
    
    private fun saveReminders() {
        try {
            val remindersJson = gson.toJson(reminders)
            sharedPreferences.edit()
                .putString(REMINDERS_KEY, remindersJson)
                .apply()
        } catch (e: Exception) {
            // Handle error silently
        }
    }
    
    private fun updateRemindersDisplay() {
        // Filter reminders by selected date
        val filteredReminders = if (selectedDate != null) {
            val filtered = reminders.filter { reminder ->
                isReminderForDate(reminder, selectedDate!!)
            }
            android.util.Log.d("CalendarFragment", "Filtering reminders for ${getFormattedDate(selectedDate)}")
            android.util.Log.d("CalendarFragment", "Total reminders: ${reminders.size}")
            android.util.Log.d("CalendarFragment", "Filtered reminders: ${filtered.size}")
            filtered.forEach { reminder ->
                android.util.Log.d("CalendarFragment", "Checking reminder: ${reminder.title} - ${reminder.time}")
            }
            filtered
        } else {
            // If no date selected, show all reminders
            android.util.Log.d("CalendarFragment", "No date selected, showing all ${reminders.size} reminders")
            reminders
        }
        
        reminderAdapter.updateReminders(filteredReminders)
        
        // Update the reminder count
        binding.tvReminderCount.text = filteredReminders.size.toString()
        
        // Debug: Log reminder count
        android.util.Log.d("CalendarFragment", "Final display: ${filteredReminders.size} reminders")
    }
    
    private fun getFormattedDate(date: Calendar?): String {
        return if (date != null) {
            java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(date.time)
        } else {
            "No date selected"
        }
    }
    
    private fun isReminderForDate(reminder: Reminder, selectedDate: Calendar): Boolean {
        try {
            // Parse the reminder time string to extract the date
            // Format: "Oct 01, 2025 at 09:00 (Daily)"
            val timeString = reminder.time
            android.util.Log.d("CalendarFragment", "Checking reminder: $timeString")
            
            // Extract date part before "at"
            val datePart = timeString.substringBefore(" at").trim()
            android.util.Log.d("CalendarFragment", "Extracted date part: $datePart")
            
            // Parse the date
            val reminderDate = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).parse(datePart)
            
            if (reminderDate != null) {
                val reminderCalendar = Calendar.getInstance().apply {
                    time = reminderDate
                }
                
                val selectedDateStr = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(selectedDate.time)
                val reminderDateStr = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(reminderCalendar.time)
                
                android.util.Log.d("CalendarFragment", "Selected date: $selectedDateStr")
                android.util.Log.d("CalendarFragment", "Reminder date: $reminderDateStr")
                
                // Check if the reminder date matches the selected date
                val matches = reminderCalendar.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
                       reminderCalendar.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) &&
                       reminderCalendar.get(Calendar.DAY_OF_MONTH) == selectedDate.get(Calendar.DAY_OF_MONTH)
                
                android.util.Log.d("CalendarFragment", "Date matches: $matches")
                return matches
            }
        } catch (e: Exception) {
            android.util.Log.e("CalendarFragment", "Error parsing reminder date: ${reminder.time}", e)
        }
        
        return false
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
