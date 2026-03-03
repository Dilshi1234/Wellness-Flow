package com.wellnessflow.habbittracker.ui.timer

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wellnessflow.habbittracker.databinding.FragmentTimerBinding
import com.wellnessflow.habbittracker.ui.timer.TimerMode

class TimerFragment : Fragment() {
    
    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    
    // Removed ViewModel to prevent ANR - using simple local state instead
    private var countDownTimer: CountDownTimer? = null
    private var isRunning = false
    private var isPaused = false
    private var timeLeftInMillis = 25 * 60 * 1000L // 25 minutes default
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Simple initialization without ViewModel to prevent ANR
        setupUI()
        updateTimerDisplay()
        updateButtonStates()
    }
    
    private fun setupUI() {
        try {
            binding.apply {
                // Timer controls
                btnStartTimer.setOnClickListener {
                    if (!isRunning) {
                        startTimer()
                    }
                }
                
                btnPauseTimer.setOnClickListener {
                    if (isRunning) {
                        pauseTimer()
                    }
                }
                
                btnResetTimer.setOnClickListener {
                    resetTimer()
                }
                
                // Timer modes
                btnFocusMode.setOnClickListener {
                    setTimerMode(TimerMode.FOCUS)
                }
                
                btnExerciseMode.setOnClickListener {
                    setTimerMode(TimerMode.EXERCISE)
                }
                
                btnMeditationMode.setOnClickListener {
                    setTimerMode(TimerMode.MEDITATION)
                }
                
                // Preset times
                btn5Min.setOnClickListener { setPresetTime(5) }
                btn15Min.setOnClickListener { setPresetTime(15) }
                btn25Min.setOnClickListener { setPresetTime(25) }
                btn45Min.setOnClickListener { setPresetTime(45) }
                
                // Custom time
                btnSetCustomTime.setOnClickListener {
                    setCustomTime()
                }
                
                // Timer history
                btnTimerHistory.setOnClickListener {
                    // Navigate to timer history
                    showTimerHistory()
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("TimerFragment", "Error setting up UI: ${e.message}")
            android.widget.Toast.makeText(requireContext(), "Error setting up timer UI", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
    
    // Removed observeViewModel - not using ViewModel anymore
    
    private fun startTimer() {
        if (isPaused) {
            // Resume from paused state
            countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeLeftInMillis = millisUntilFinished
                    updateTimerDisplay()
                }
                
                override fun onFinish() {
                    onTimerFinished()
                }
            }.start()
        } else {
            // Start new timer
            countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeLeftInMillis = millisUntilFinished
                    updateTimerDisplay()
                }
                
                override fun onFinish() {
                    onTimerFinished()
                }
            }.start()
        }
        
        isRunning = true
        isPaused = false
        updateButtonStates()
        binding.tvTimerStatus.text = "Timer running..."
    }
    
    private fun pauseTimer() {
        countDownTimer?.cancel()
        isRunning = false
        isPaused = true
        updateButtonStates()
        binding.tvTimerStatus.text = "Timer paused"
    }
    
    private fun resetTimer() {
        countDownTimer?.cancel()
        isRunning = false
        isPaused = false
        timeLeftInMillis = 25 * 60 * 1000L // Reset to default
        updateTimerDisplay()
        updateButtonStates()
        binding.tvTimerStatus.text = "Ready to start"
    }
    
    private fun onTimerFinished() {
        isRunning = false
        isPaused = false
        timeLeftInMillis = 0
        updateTimerDisplay()
        updateButtonStates()
        binding.tvTimerStatus.text = "Timer completed!"
        
        // Timer completed - could save to history later if needed
        
        // Show completion notification or sound
        showTimerCompletionNotification()
    }
    
    private fun updateTimerDisplay() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        
        val timeString = String.format("%02d:%02d", minutes, seconds)
        binding.tvTimerDisplay.text = timeString
    }
    
    private fun updateButtonStates() {
        binding.apply {
            btnStartTimer.isEnabled = !isRunning
            btnPauseTimer.isEnabled = isRunning
            btnResetTimer.isEnabled = isRunning || isPaused
        }
    }
    
    private fun setTimerMode(mode: TimerMode) {
        // Update UI based on mode
        when (mode) {
            TimerMode.FOCUS -> {
                binding.tvTimerMode.text = "Focus Session"
                timeLeftInMillis = 25 * 60 * 1000L // 25 minutes
            }
            TimerMode.EXERCISE -> {
                binding.tvTimerMode.text = "Exercise Session"
                timeLeftInMillis = 45 * 60 * 1000L // 45 minutes
            }
            TimerMode.MEDITATION -> {
                binding.tvTimerMode.text = "Meditation Session"
                timeLeftInMillis = 15 * 60 * 1000L // 15 minutes
            }
        }
        
        updateTimerDisplay()
        updateModeButtons(mode)
    }
    
    private fun updateModeButtons(selectedMode: TimerMode) {
        binding.apply {
            btnFocusMode.isSelected = selectedMode == TimerMode.FOCUS
            btnExerciseMode.isSelected = selectedMode == TimerMode.EXERCISE
            btnMeditationMode.isSelected = selectedMode == TimerMode.MEDITATION
        }
    }
    
    private fun updateModeDisplay(mode: TimerMode) {
        setTimerMode(mode)
    }
    
    private fun setPresetTime(minutes: Int) {
        timeLeftInMillis = minutes * 60 * 1000L
        updateTimerDisplay()
        binding.tvTimerStatus.text = "Timer set to ${minutes} minutes"
    }
    
    private fun setCustomTime() {
        val minutes = binding.etMinutes.text.toString().toIntOrNull() ?: 0
        val seconds = binding.etSeconds.text.toString().toIntOrNull() ?: 0
        
        timeLeftInMillis = (minutes * 60 + seconds) * 1000L
        updateTimerDisplay()
        binding.tvTimerStatus.text = "Custom time set"
    }
    
    private fun showTimerHistory() {
        // Navigate to timer history fragment
    }
    
    private fun showTimerCompletionNotification() {
        // Show notification or play sound when timer completes
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }
}

