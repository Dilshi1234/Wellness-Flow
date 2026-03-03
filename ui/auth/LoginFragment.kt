package com.wellnessflow.habbittracker.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.wellnessflow.habbittracker.R
import com.wellnessflow.habbittracker.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
    }
    
    private fun setupUI() {
        // Login button click
        binding.btnLogin.setOnClickListener {
            if (validateLogin()) {
                // Navigate to dashboard
                findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Sign up link click
        binding.tvSignUpLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }
        
        // Forgot password click
        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(context, "Forgot password feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun validateLogin(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        
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
        
        // Check for default credentials
        if (email == "admin123@gmail.com" && password == "admin000") {
            return true
        } else {
            Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
            return false
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
