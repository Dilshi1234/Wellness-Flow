package com.wellnessflow.habbittracker.data

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    
    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
    }
    
    fun getWeekAgoDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
    }
    
    fun getCurrentTime(): String {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.format(Date())
    }
    
    fun getCurrentDateTime(): String {
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return dateTimeFormat.format(Date())
    }
    
    fun formatDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }
    
    fun formatTime(timestamp: Long): String {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.format(Date(timestamp))
    }
}
