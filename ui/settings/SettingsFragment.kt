package com.wellnessflow.habbittracker.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.wellnessflow.habbittracker.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: SettingsViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        
        setupUI()
        observeViewModel()
    }
    
    private fun setupUI() {
        binding.apply {
            // Hydration reminder switch
            switchHydrationEnabled.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateHydrationReminderEnabled(isChecked)
            }
            
            // Reminder interval button
            btnReminderInterval.setOnClickListener {
                showReminderIntervalDialog()
            }
            
            // Reminder hours button
            btnReminderHours.setOnClickListener {
                showReminderHoursDialog()
            }
            
            // Sound switch
            switchSoundEnabled.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateSoundEnabled(isChecked)
            }
            
            // Vibration switch
            switchVibrationEnabled.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateVibrationEnabled(isChecked)
            }
            
            // Data management buttons
            btnResetToday.setOnClickListener {
                viewModel.resetTodayProgress()
            }
            
            btnExportData.setOnClickListener {
                exportData()
            }
            
            btnClearAllData.setOnClickListener {
                showClearDataConfirmation()
            }
        }
    }
    
    private fun observeViewModel() {
        viewModel.userSettings.observe(viewLifecycleOwner) { settings ->
            binding.apply {
                switchHydrationEnabled.isChecked = settings.hydrationReminderEnabled
                btnReminderInterval.text = formatReminderInterval(settings.reminderIntervalMinutes)
                btnReminderHours.text = "${settings.reminderStartTime} - ${settings.reminderEndTime}"
                switchSoundEnabled.isChecked = settings.soundEnabled
                switchVibrationEnabled.isChecked = settings.vibrationEnabled
            }
        }
    }
    
    private fun formatReminderInterval(minutes: Int): String {
        return when {
            minutes < 60 -> "${minutes}m"
            minutes == 60 -> "1h"
            minutes % 60 == 0 -> "${minutes / 60}h"
            else -> "${minutes / 60}h ${minutes % 60}m"
        }
    }
    
    private fun showReminderIntervalDialog() {
        // This will be implemented when we create the dialog
        // For now, cycle through common intervals
        val currentInterval = viewModel.userSettings.value?.reminderIntervalMinutes ?: 120
        val intervals = listOf(15, 30, 60, 90, 120, 180, 240)
        val currentIndex = intervals.indexOf(currentInterval)
        val nextIndex = (currentIndex + 1) % intervals.size
        viewModel.updateReminderInterval(intervals[nextIndex])
    }
    
    private fun showReminderHoursDialog() {
        // This will be implemented when we create the dialog
        // For now, just a placeholder
    }
    
    private fun exportData() {
        val data = viewModel.exportData()
        // This will be implemented when we add sharing functionality
        // For now, just a placeholder
    }
    
    private fun showClearDataConfirmation() {
        // This will be implemented when we create the confirmation dialog
        // For now, just clear directly
        viewModel.clearAllData()
    }
    
    override fun onResume() {
        super.onResume()
        viewModel.refreshSettings()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
