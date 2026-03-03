package com.wellnessflow.habbittracker.ui.userprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wellnessflow.habbittracker.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserProfileViewModel : ViewModel() {
    
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _saveMessage = MutableStateFlow<String?>(null)
    val saveMessage: StateFlow<String?> = _saveMessage.asStateFlow()
    
    init {
        loadUserProfile()
    }
    
    private fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: Load from SharedPreferences or database
                // For now, use default empty profile
                _userProfile.value = UserProfile()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateName(name: String) {
        _userProfile.value = _userProfile.value.copy(name = name)
    }
    
    fun updateAge(age: Int) {
        _userProfile.value = _userProfile.value.copy(age = age)
    }
    
    fun updateEmail(email: String) {
        _userProfile.value = _userProfile.value.copy(email = email)
    }
    
    fun updatePhoneNumber(phoneNumber: String) {
        _userProfile.value = _userProfile.value.copy(phoneNumber = phoneNumber)
    }
    
    fun updateHeightFeet(heightFeet: Int) {
        _userProfile.value = _userProfile.value.copy(heightFeet = heightFeet)
    }
    
    fun updateHeightInches(heightInches: Int) {
        _userProfile.value = _userProfile.value.copy(heightInches = heightInches)
    }
    
    fun updateWeight(weightKg: Double) {
        _userProfile.value = _userProfile.value.copy(weightKg = weightKg)
    }
    
    fun updateAvatar(avatarPath: String) {
        _userProfile.value = _userProfile.value.copy(avatarPath = avatarPath)
    }
    
    fun saveProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: Save to SharedPreferences or database
                val currentProfile = _userProfile.value
                _userProfile.value = currentProfile.copy(
                    updatedAt = System.currentTimeMillis()
                )
                _saveMessage.value = "Profile saved successfully!"
            } catch (e: Exception) {
                _saveMessage.value = "Error saving profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun resetProfile() {
        _userProfile.value = UserProfile()
        _saveMessage.value = "Profile reset to default"
    }
    
    fun clearSaveMessage() {
        _saveMessage.value = null
    }
    
    fun validateProfile(): Boolean {
        val profile = _userProfile.value
        return profile.name.isNotEmpty() && 
               profile.age > 0 && 
               profile.email.isNotEmpty() && 
               profile.phoneNumber.isNotEmpty()
    }
}
