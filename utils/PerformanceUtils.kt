package com.wellnessflow.habbittracker.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*

/**
 * Performance optimization utilities for WellnessFlow
 */
object PerformanceUtils {
    
    private val mainHandler = Handler(Looper.getMainLooper())
    private val backgroundScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    /**
     * Debounce function to prevent rapid button clicks
     */
    fun debounce(
        delay: Long = 300L,
        action: () -> Unit
    ): () -> Unit {
        var lastCallTime = 0L
        return {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastCallTime >= delay) {
                lastCallTime = currentTime
                action()
            }
        }
    }
    
    /**
     * Throttle function to limit function calls
     */
    fun throttle(
        delay: Long = 1000L,
        action: () -> Unit
    ): () -> Unit {
        var lastCallTime = 0L
        return {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastCallTime >= delay) {
                lastCallTime = currentTime
                action()
            }
        }
    }
    
    /**
     * Execute on main thread
     */
    fun runOnMainThread(action: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action()
        } else {
            mainHandler.post(action)
        }
    }
    
    /**
     * Execute on background thread
     */
    fun runOnBackgroundThread(action: suspend () -> Unit) {
        backgroundScope.launch {
            action()
        }
    }
    
    /**
     * Optimize RecyclerView performance
     */
    fun optimizeRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(20)
        recyclerView.setDrawingCacheEnabled(true)
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH)
    }
    
    /**
     * Memory-efficient image loading
     */
    fun loadImageOptimized(
        context: Context,
        imageView: ImageView,
        drawableRes: Int,
        width: Int = 100,
        height: Int = 100
    ) {
        runOnBackgroundThread {
            try {
                val drawable = ContextCompat.getDrawable(context, drawableRes)
                val bitmap = drawBitmap(drawable, width, height)
                
                runOnMainThread {
                    imageView.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                // Handle error gracefully
                runOnMainThread {
                    imageView.setImageResource(drawableRes)
                }
            }
        }
    }
    
    private fun drawBitmap(drawable: Drawable?, width: Int, height: Int): Bitmap? {
        if (drawable == null) return null
        
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
    
    /**
     * Clear memory cache
     */
    fun clearMemoryCache() {
        System.gc()
    }
    
    /**
     * Check if device has low memory
     */
    fun isLowMemory(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val memoryInfo = android.app.ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.lowMemory
    }
    
    /**
     * Optimize for low memory devices
     */
    fun optimizeForLowMemory(context: Context) {
        if (isLowMemory(context)) {
            // Reduce cache sizes
            clearMemoryCache()
            
            // Disable animations if needed
            // This would be handled in the UI layer
        }
    }
    
    /**
     * Batch operations for better performance
     */
    fun batchOperations(operations: List<() -> Unit>) {
        runOnBackgroundThread {
            operations.forEach { operation ->
                try {
                    operation()
                } catch (e: Exception) {
                    // Log error but continue with other operations
                    android.util.Log.e("PerformanceUtils", "Batch operation failed: ${e.message}")
                }
            }
        }
    }
    
    /**
     * Cancel all background operations
     */
    fun cancelAllOperations() {
        backgroundScope.coroutineContext.cancel()
    }
}
