package com.wellnessflow.habbittracker.ui.userprofile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.wellnessflow.habbittracker.R
import com.wellnessflow.habbittracker.databinding.FragmentUserProfileBinding
import kotlinx.coroutines.launch

class UserProfileFragment : Fragment() {
    
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: UserProfileViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        observeViewModel()
        
        // Force BMI update on start
        calculateBMI()
    }
    
    private fun setupUI() {
        // Save button click
        binding.btnSaveProfile.setOnClickListener {
            viewModel.saveProfile()
        }
        
        // Reset button click
        binding.btnResetProfile.setOnClickListener {
            viewModel.resetProfile()
        }
        
        // Change avatar button click
        binding.btnChangeAvatar.setOnClickListener {
            // TODO: Implement avatar selection
            Toast.makeText(context, getString(R.string.avatar_selection_coming_soon), Toast.LENGTH_SHORT).show()
        }
        
        // Calculate BMI button click
        binding.btnCalculateBmi.setOnClickListener {
            updateBMIFromFields()
        }
        
        // Text change listeners for auto-calculation
        binding.etName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.updateName(binding.etName.text.toString())
            }
        }
        
        binding.etAge.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val ageText = binding.etAge.text.toString()
                val age = if (ageText.isNotEmpty()) ageText.toIntOrNull() ?: 0 else 0
                viewModel.updateAge(age)
            }
        }
        
        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.updateEmail(binding.etEmail.text.toString())
            }
        }
        
        binding.etPhone.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.updatePhoneNumber(binding.etPhone.text.toString())
            }
        }
        
        // Add TextWatchers for BMI fields to enable auto-calculation
        binding.etHeightFeet.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                android.util.Log.d("BMI", "Height Feet TextWatcher triggered: '${s.toString()}'")
                val feetText = s.toString()
                val feet = if (feetText.isNotEmpty()) feetText.toIntOrNull() ?: 0 else 0
                viewModel.updateHeightFeet(feet)
                calculateBMI()
            }
        })
        
        binding.etHeightInches.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                android.util.Log.d("BMI", "Height Inches TextWatcher triggered: '${s.toString()}'")
                val inchesText = s.toString()
                val inches = if (inchesText.isNotEmpty()) inchesText.toIntOrNull() ?: 0 else 0
                viewModel.updateHeightInches(inches)
                calculateBMI()
            }
        })
        
        binding.etWeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                android.util.Log.d("BMI", "Weight TextWatcher triggered: '${s.toString()}'")
                val weightText = s.toString()
                val weight = if (weightText.isNotEmpty()) weightText.toDoubleOrNull() ?: 0.0 else 0.0
                viewModel.updateWeight(weight)
                calculateBMI()
            }
        })
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userProfile.collect { profile ->
                updateUI(profile)
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saveMessage.collect { message ->
                message?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearSaveMessage()
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.btnSaveProfile.isEnabled = !isLoading
                binding.btnResetProfile.isEnabled = !isLoading
            }
        }
    }
    
    private fun updateUI(profile: com.wellnessflow.habbittracker.data.UserProfile) {
        binding.etName.setText(profile.name)
        binding.etAge.setText(if (profile.age > 0) profile.age.toString() else "")
        binding.etEmail.setText(profile.email)
        binding.etPhone.setText(profile.phoneNumber)
        binding.etHeightFeet.setText(if (profile.heightFeet > 0) profile.heightFeet.toString() else "")
        binding.etHeightInches.setText(if (profile.heightInches > 0) profile.heightInches.toString() else "")
        binding.etWeight.setText(if (profile.weightKg > 0) profile.weightKg.toString() else "")
        
        // Update BMI display using direct calculation
        calculateBMI()
        
        // Update avatar if path is available
        if (profile.avatarPath.isNotEmpty()) {
            // TODO: Load custom avatar image
        }
    }
    
    private fun calculateBMI() {
        // Get values directly from fields
        val feetText = binding.etHeightFeet.text.toString()
        val inchesText = binding.etHeightInches.text.toString()
        val weightText = binding.etWeight.text.toString()
        
        android.util.Log.d("BMI", "Raw text values - Feet: '$feetText', Inches: '$inchesText', Weight: '$weightText'")
        
        val feet = if (feetText.isNotEmpty()) feetText.toIntOrNull() ?: 0 else 0
        val inches = if (inchesText.isNotEmpty()) inchesText.toIntOrNull() ?: 0 else 0
        val weight = if (weightText.isNotEmpty()) weightText.toDoubleOrNull() ?: 0.0 else 0.0
        
        android.util.Log.d("BMI", "Parsed values - Feet: $feet, Inches: $inches, Weight: $weight")
        
        // Calculate BMI directly
        val totalInches = (feet * 12) + inches
        val heightMeters = totalInches * 0.0254
        val bmi = if (heightMeters > 0 && weight > 0) {
            weight / (heightMeters * heightMeters)
        } else {
            0.0
        }
        
        android.util.Log.d("BMI", "Calculation - Total inches: $totalInches, Height meters: $heightMeters, BMI: $bmi")
        
        // Update display directly
        val bmiDisplay = if (bmi > 0) String.format("%.1f", bmi) else "0.0"
        binding.tvBmiValue.text = bmiDisplay
        
        val category = when {
            bmi <= 0 -> "Not calculated"
            bmi < 18.5 -> "Underweight"
            bmi < 25 -> "Normal"
            bmi < 30 -> "Overweight"
            else -> "Obese"
        }
        
        binding.tvBmiCategory.text = category
        
        // Set colors
        val color = when (category) {
            "Underweight" -> android.graphics.Color.parseColor("#FF9800")
            "Normal" -> android.graphics.Color.parseColor("#4CAF50")
            "Overweight" -> android.graphics.Color.parseColor("#FF9800")
            "Obese" -> android.graphics.Color.parseColor("#F44336")
            "Not calculated" -> android.graphics.Color.parseColor("#9E9E9E")
            else -> android.graphics.Color.parseColor("#2196F3")
        }
        binding.tvBmiValue.setTextColor(color)
        binding.tvBmiCategory.setTextColor(color)
        
        android.util.Log.d("BMI", "Display updated - BMI: $bmiDisplay, Category: $category")
    }
    
    private fun updateBMIFromFields() {
        // Get values directly from fields
        val feetText = binding.etHeightFeet.text.toString()
        val inchesText = binding.etHeightInches.text.toString()
        val weightText = binding.etWeight.text.toString()
        
        val feet = if (feetText.isNotEmpty()) feetText.toIntOrNull() ?: 0 else 0
        val inches = if (inchesText.isNotEmpty()) inchesText.toIntOrNull() ?: 0 else 0
        val weight = if (weightText.isNotEmpty()) weightText.toDoubleOrNull() ?: 0.0 else 0.0
        
        // Update ViewModel
        viewModel.updateHeightFeet(feet)
        viewModel.updateHeightInches(inches)
        viewModel.updateWeight(weight)
        
        // Calculate BMI
        calculateBMI()
    }
    
    private fun updateBMIDisplay() {
        val profile = viewModel.userProfile.value
        val bmi = profile.calculateBMI()
        val category = profile.getBMICategory()
        
        // Debug logging
        android.util.Log.d("BMI", "Height Feet: ${profile.heightFeet}, Height Inches: ${profile.heightInches}, Weight: ${profile.weightKg}")
        android.util.Log.d("BMI", "Calculated BMI: $bmi, Category: $category")
        
        // Always show BMI calculation, even if 0
        binding.tvBmiValue.text = if (bmi > 0) String.format("%.1f", bmi) else "0.0"
        binding.tvBmiCategory.text = category
        
        // Set color based on BMI category
        val color = when (category) {
            "Underweight" -> android.graphics.Color.parseColor("#FF9800") // Orange
            "Normal" -> android.graphics.Color.parseColor("#4CAF50") // Green
            "Overweight" -> android.graphics.Color.parseColor("#FF9800") // Orange
            "Obese" -> android.graphics.Color.parseColor("#F44336") // Red
            "Not calculated" -> android.graphics.Color.parseColor("#9E9E9E") // Gray
            else -> android.graphics.Color.parseColor("#2196F3") // Blue
        }
        binding.tvBmiValue.setTextColor(color)
        binding.tvBmiCategory.setTextColor(color)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
