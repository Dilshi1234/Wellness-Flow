package com.wellnessflow.habbittracker.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.wellnessflow.habbittracker.R
import com.wellnessflow.habbittracker.databinding.FragmentSignupBinding

class SignupFragment : Fragment() {
    
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
    }
    
    private fun setupUI() {
        // Sign up button click
        binding.btnSignUp.setOnClickListener {
            if (validateSignup()) {
                // Navigate to dashboard
                findNavController().navigate(R.id.action_signupFragment_to_dashboardFragment)
                Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun validateSignup(): Boolean {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        
        if (name.isEmpty()) {
            binding.etName.error = "Name is required"
            return false
        }
        
        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            return false
        }
        
        if (!email.endsWith("@gmail.com")) {
            binding.etEmail.error = "Email must be @gmail.com"
            return false
        }
        
        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            return false
        }
        
        if (password.length < 8) {
            binding.etPassword.error = "Password must be at least 8 characters"
            return false
        }
        
        if (confirmPassword.isEmpty()) {
            binding.etConfirmPassword.error = "Please confirm your password"
            return false
        }
        
        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Passwords do not match"
            return false
        }
        
        return true
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
