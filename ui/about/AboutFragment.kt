package com.wellnessflow.habbittracker.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wellnessflow.habbittracker.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            tvAppName.text = "WellnessFlow"
            tvAppVersion.text = "Version 1.0.0"
            tvAppDescription.text = "A comprehensive wellness tracking app designed to help you build healthy habits and track your mood journey."
            
            tvFeatures.text = """
                ✨ Key Features:
                • Daily habit tracking
                • Mood journaling
                • Progress analytics
                • Focus timer
                • Wellness calendar
                • Smart reminders
            """.trimIndent()
            
            tvDeveloper.text = "Developed with ❤️ for your wellness journey"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
