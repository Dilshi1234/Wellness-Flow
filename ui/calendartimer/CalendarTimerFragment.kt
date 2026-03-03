package com.wellnessflow.habbittracker.ui.calendartimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.wellnessflow.habbittracker.R
import com.wellnessflow.habbittracker.databinding.FragmentCalendarTimerBinding

class CalendarTimerFragment : Fragment() {
    
    private var _binding: FragmentCalendarTimerBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarTimerBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup UI with error handling to prevent ANR
        try {
            setupUI()
        } catch (e: Exception) {
            android.util.Log.e("CalendarTimerFragment", "Error setting up UI: ${e.message}")
        }
    }
    
    private fun setupUI() {
        binding.apply {
            // Calendar card click
            cardCalendar.setOnClickListener {
                findNavController().navigate(R.id.action_calendarTimerFragment_to_calendarFragment)
            }
            
            // Timer card click
            cardTimer.setOnClickListener {
                findNavController().navigate(R.id.action_calendarTimerFragment_to_timerFragment)
            }
            
            // Update stats (these would come from ViewModel in real implementation)
            tvStreakDays.text = "7"
            tvTotalSessions.text = "24"
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
