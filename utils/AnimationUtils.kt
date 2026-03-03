package com.wellnessflow.habbittracker.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat

/**
 * UI animation utilities for WellnessFlow
 */
object AnimationUtils {
    
    /**
     * Animate view fade in with scale
     */
    fun fadeInScale(view: View, duration: Long = 300L) {
        view.alpha = 0f
        view.scaleX = 0.8f
        view.scaleY = 0.8f
        view.visibility = View.VISIBLE
        
        view.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }
    
    /**
     * Animate view fade out with scale
     */
    fun fadeOutScale(view: View, duration: Long = 200L, onEnd: (() -> Unit)? = null) {
        view.animate()
            .alpha(0f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                    onEnd?.invoke()
                }
            })
            .start()
    }
    
    /**
     * Animate button press effect
     */
    fun buttonPress(view: View, duration: Long = 150L) {
        val originalScaleX = view.scaleX
        val originalScaleY = view.scaleY
        
        view.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(duration / 2)
            .setInterpolator(OvershootInterpolator())
            .withEndAction {
                view.animate()
                    .scaleX(originalScaleX)
                    .scaleY(originalScaleY)
                    .setDuration(duration / 2)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            }
            .start()
    }
    
    /**
     * Animate progress bar fill
     */
    fun animateProgressBar(view: View, targetProgress: Int, duration: Long = 500L) {
        val animator = ObjectAnimator.ofInt(view, "progress", 0, targetProgress)
        animator.duration = duration
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }
    
    /**
     * Animate slide in from top
     */
    fun slideInFromTop(view: View, duration: Long = 300L) {
        view.translationY = -view.height.toFloat()
        view.alpha = 0f
        view.visibility = View.VISIBLE
        
        view.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(BounceInterpolator())
            .start()
    }
    
    /**
     * Animate bounce effect
     */
    fun bounce(view: View, duration: Long = 300L) {
        view.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(duration / 2)
            .setInterpolator(OvershootInterpolator())
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(duration / 2)
                    .setInterpolator(BounceInterpolator())
                    .start()
            }
            .start()
    }
    
    /**
     * Animate pulse effect
     */
    fun pulse(view: View, duration: Long = 1000L, repeatCount: Int = 3) {
        val animator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.5f, 1f)
        animator.duration = duration
        animator.repeatCount = repeatCount
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }
    
    /**
     * Animate rotation
     */
    fun rotate(view: View, degrees: Float, duration: Long = 300L) {
        view.animate()
            .rotation(degrees)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }
    
    /**
     * Animate shake effect
     */
    fun shake(view: View, duration: Long = 500L) {
        val animator = ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
        animator.duration = duration
        animator.start()
    }
    
    /**
     * Animate success checkmark
     */
    fun successAnimation(view: View, onComplete: (() -> Unit)? = null) {
        view.scaleX = 0f
        view.scaleY = 0f
        view.alpha = 0f
        view.visibility = View.VISIBLE
        
        view.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .alpha(1f)
            .setDuration(200)
            .setInterpolator(OvershootInterpolator())
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            onComplete?.invoke()
                        }
                    })
                    .start()
            }
            .start()
    }
    
    /**
     * Animate progress completion
     */
    fun animateProgressCompletion(view: View, onComplete: (() -> Unit)? = null) {
        // First bounce the progress bar
        bounce(view, 300)
        
        // Then show success animation
        view.postDelayed({
            successAnimation(view, onComplete)
        }, 300)
    }
    
    /**
     * Animate mood selection
     */
    fun animateMoodSelection(view: View, isSelected: Boolean) {
        if (isSelected) {
            view.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(200)
                .setInterpolator(OvershootInterpolator())
                .start()
        } else {
            view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(200)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }
    
    /**
     * Animate card elevation change
     */
    fun animateCardElevation(view: View, elevation: Float, duration: Long = 200L) {
        ViewCompat.animate(view)
            .translationZ(elevation)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }
}
