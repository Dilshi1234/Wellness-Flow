package com.wellnessflow.habbittracker.data

/**
 * UserProfile - Data class representing a user's profile information
 * This class stores personal information, physical measurements, and provides
 * utility methods for BMI calculation and data formatting
 * 
 * @property id - Unique identifier for the user (default: "default_user")
 * @property name - User's full name
 * @property age - User's age in years
 * @property email - User's email address
 * @property phoneNumber - User's phone number
 * @property avatarPath - Path to user's profile picture
 * @property heightFeet - User's height in feet
 * @property heightInches - User's height in inches (additional to feet)
 * @property weightKg - User's weight in kilograms
 * @property createdAt - Timestamp when profile was created
 * @property updatedAt - Timestamp when profile was last updated
 */
data class UserProfile(
    val id: String = "default_user",           // Default user ID
    val name: String = "",                     // User's full name
    val age: Int = 0,                          // User's age in years
    val email: String = "",                    // User's email address
    val phoneNumber: String = "",              // User's phone number
    val avatarPath: String = "",               // Path to profile picture
    val heightFeet: Int = 0,                   // Height in feet
    val heightInches: Int = 0,                 // Additional height in inches
    val weightKg: Double = 0.0,               // Weight in kilograms
    val createdAt: Long = System.currentTimeMillis(),    // Creation timestamp
    val updatedAt: Long = System.currentTimeMillis()    // Last update timestamp
) {
    
    /**
     * getDisplayName - Returns formatted name for display
     * Returns the user's name or "User" if name is empty
     * @return String - Formatted name for UI display
     */
    fun getDisplayName(): String {
        return if (name.isNotEmpty()) name else "User"
    }
    
    /**
     * getDisplayAge - Returns formatted age for display
     * Returns age with "years old" suffix or "Not specified" if age is 0
     * @return String - Formatted age for UI display
     */
    fun getDisplayAge(): String {
        return if (age > 0) "$age years old" else "Not specified"
    }
    
    /**
     * getDisplayEmail - Returns formatted email for display
     * Returns email or "Not specified" if email is empty
     * @return String - Formatted email for UI display
     */
    fun getDisplayEmail(): String {
        return if (email.isNotEmpty()) email else "Not specified"
    }
    
    /**
     * getDisplayPhone - Returns formatted phone number for display
     * Returns phone number or "Not specified" if phone is empty
     * @return String - Formatted phone number for UI display
     */
    fun getDisplayPhone(): String {
        return if (phoneNumber.isNotEmpty()) phoneNumber else "Not specified"
    }
    
    /**
     * getDisplayHeight - Returns formatted height for display
     * Returns height in feet'inches" format or "Not specified" if height is 0
     * @return String - Formatted height for UI display
     */
    fun getDisplayHeight(): String {
        return if (heightFeet > 0 || heightInches > 0) {
            "$heightFeet'$heightInches\""  // Format: 5'10"
        } else "Not specified"
    }
    
    /**
     * getDisplayWeight - Returns formatted weight for display
     * Returns weight with "kg" suffix or "Not specified" if weight is 0
     * @return String - Formatted weight for UI display
     */
    fun getDisplayWeight(): String {
        return if (weightKg > 0) "${weightKg}kg" else "Not specified"
    }
    
    /**
     * calculateBMI - Calculates Body Mass Index based on height and weight
     * Uses the standard BMI formula: weight(kg) / height(m)²
     * @return Double - BMI value or 0.0 if height/weight is invalid
     */
    fun calculateBMI(): Double {
        // Get height and weight values directly from properties
        val feet = heightFeet
        val inches = heightInches
        val weight = weightKg
        
        // Convert feet and inches to total inches
        val totalInches = (feet * 12) + inches
        // Convert inches to meters (1 inch = 0.0254 meters)
        val heightMeters = totalInches * 0.0254
        
        // Calculate BMI using standard formula: weight(kg) / height(m)²
        if (heightMeters > 0 && weight > 0) {
            return weight / (heightMeters * heightMeters)
        }
        // Return 0.0 if height or weight is invalid
        return 0.0
    }
    
    /**
     * getBMICategory - Returns BMI category based on calculated BMI value
     * Categorizes BMI into standard health categories
     * @return String - BMI category (Underweight, Normal, Overweight, Obese, Not calculated)
     */
    fun getBMICategory(): String {
        // Calculate BMI first
        val bmi = calculateBMI()
        // Return appropriate category based on BMI ranges
        return when {
            bmi <= 0 -> "Not calculated"        // Invalid BMI
            bmi < 18.5 -> "Underweight"        // BMI < 18.5
            bmi < 25 -> "Normal"               // BMI 18.5-24.9
            bmi < 30 -> "Overweight"           // BMI 25-29.9
            else -> "Obese"                    // BMI >= 30
        }
    }
    
    /**
     * getBMIDisplay - Returns formatted BMI value for display
     * Returns BMI with one decimal place or "Not calculated" if BMI is 0
     * @return String - Formatted BMI value for UI display
     */
    fun getBMIDisplay(): String {
        // Calculate BMI first
        val bmi = calculateBMI()
        // Return formatted BMI or "Not calculated"
        return if (bmi > 0) {
            String.format("%.1f", bmi)  // Format to 1 decimal place
        } else "Not calculated"
    }
    
    /**
     * hasCompleteProfile - Checks if all essential profile fields are filled
     * Validates that name, age, email, phone, height, and weight are provided
     * @return Boolean - true if profile is complete, false otherwise
     */
    fun hasCompleteProfile(): Boolean {
        // Check that all essential fields have valid values
        return name.isNotEmpty() && age > 0 && email.isNotEmpty() && 
               phoneNumber.isNotEmpty() && heightFeet > 0 && weightKg > 0
    }
}
