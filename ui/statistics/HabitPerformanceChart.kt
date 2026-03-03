package com.wellnessflow.habbittracker.ui.statistics

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.wellnessflow.habbittracker.R
import kotlin.math.*

class HabitPerformanceChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    
    private var chartData = listOf<HabitDataPoint>()
    private var maxValue = 100f
    private var minValue = 0f
    private var isLineChart = true // true for line chart, false for bar chart
    
    // Chart dimensions
    private val padding = 60f
    private val textSize = 20f
    private val lineWidth = 6f
    private val barWidth = 40f
    private val barSpacing = 20f
    
    // Colors
    private val lineColor = ContextCompat.getColor(context, R.color.primary_blue)
    private val barColor = ContextCompat.getColor(context, R.color.primary_green)
    private val textColor = ContextCompat.getColor(context, R.color.text_primary)
    private val gridColor = ContextCompat.getColor(context, R.color.divider)
    
    data class HabitDataPoint(
        val label: String,
        val value: Float,
        val isOverall: Boolean = false // true for overall trend, false for individual habit
    )
    
    fun updateData(data: List<HabitDataPoint>, isLineChart: Boolean = true) {
        chartData = data
        this.isLineChart = isLineChart
        if (data.isNotEmpty()) {
            maxValue = data.maxOf { it.value }.coerceAtLeast(100f)
            minValue = data.minOf { it.value }.coerceAtMost(0f)
        }
        invalidate()
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        if (chartData.isEmpty()) {
            drawEmptyState(canvas)
            return
        }
        
        val width = width.toFloat()
        val height = height.toFloat()
        val chartWidth = width - 2 * padding
        val chartHeight = height - 2 * padding
        
        // Draw grid lines
        drawGridLines(canvas, padding, chartWidth, chartHeight)
        
        // Draw Y-axis labels
        drawYAxisLabels(canvas, padding, chartHeight)
        
        // Draw X-axis labels
        drawXAxisLabels(canvas, padding, chartWidth, chartHeight)
        
        // Draw chart based on type
        if (isLineChart) {
            drawLineChart(canvas, padding, chartWidth, chartHeight)
        } else {
            drawBarChart(canvas, padding, chartWidth, chartHeight)
        }
    }
    
    private fun drawEmptyState(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = height / 2f
        
        textPaint.textSize = 32f
        textPaint.color = textColor
        textPaint.textAlign = Paint.Align.CENTER
        
        canvas.drawText("No habit data available", centerX, centerY, textPaint)
    }
    
    private fun drawGridLines(canvas: Canvas, padding: Float, chartWidth: Float, chartHeight: Float) {
        paint.color = gridColor
        paint.strokeWidth = 2f
        paint.style = Paint.Style.STROKE
        
        // Horizontal grid lines
        for (i in 0..10) {
            val y = padding + (chartHeight / 10) * i
            canvas.drawLine(padding, y, padding + chartWidth, y, paint)
        }
        
        // Vertical grid lines
        if (chartData.isNotEmpty()) {
            val stepX = chartWidth / (chartData.size - 1)
            for (i in chartData.indices) {
                val x = padding + stepX * i
                canvas.drawLine(x, padding, x, padding + chartHeight, paint)
            }
        }
    }
    
    private fun drawYAxisLabels(canvas: Canvas, padding: Float, chartHeight: Float) {
        textPaint.textSize = 18f
        textPaint.color = textColor
        textPaint.textAlign = Paint.Align.RIGHT
        
        for (i in 0..10) {
            val value = maxValue - (maxValue - minValue) * i / 10
            val y = padding + (chartHeight / 10) * i + 7f
            canvas.drawText("${value.toInt()}%", padding - 10, y, textPaint)
        }
    }
    
    private fun drawXAxisLabels(canvas: Canvas, padding: Float, chartWidth: Float, chartHeight: Float) {
        if (chartData.isEmpty()) return
        
        textPaint.textSize = 14f
        textPaint.color = textColor
        textPaint.textAlign = Paint.Align.CENTER
        
        val stepX = chartWidth / (chartData.size - 1)
        for (i in chartData.indices) {
            val x = padding + stepX * i
            val y = padding + chartHeight + 30f
            
            // Truncate long labels
            val label = if (chartData[i].label.length > 8) {
                chartData[i].label.substring(0, 8) + "..."
            } else {
                chartData[i].label
            }
            
            canvas.drawText(label, x, y, textPaint)
        }
    }
    
    private fun drawLineChart(canvas: Canvas, padding: Float, chartWidth: Float, chartHeight: Float) {
        if (chartData.size < 2) return
        
        paint.color = lineColor
        paint.strokeWidth = lineWidth
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
        
        val stepX = chartWidth / (chartData.size - 1)
        val path = Path()
        
        for (i in chartData.indices) {
            val x = padding + stepX * i
            val y = padding + chartHeight - (chartData[i].value - minValue) / (maxValue - minValue) * chartHeight
            
            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        
        canvas.drawPath(path, paint)
        
        // Draw data points with percentages
        drawDataPoints(canvas, padding, chartWidth, chartHeight, stepX)
    }
    
    private fun drawBarChart(canvas: Canvas, padding: Float, chartWidth: Float, chartHeight: Float) {
        if (chartData.isEmpty()) return
        
        val totalBarWidth = (barWidth + barSpacing) * chartData.size
        val startX = padding + (chartWidth - totalBarWidth) / 2
        
        for (i in chartData.indices) {
            val x = startX + i * (barWidth + barSpacing)
            val barHeight = (chartData[i].value - minValue) / (maxValue - minValue) * chartHeight
            val y = padding + chartHeight - barHeight
            
            // Draw bar
            barPaint.color = if (chartData[i].isOverall) {
                ContextCompat.getColor(context, R.color.primary_blue)
            } else {
                ContextCompat.getColor(context, R.color.primary_green)
            }
            barPaint.style = Paint.Style.FILL
            
            canvas.drawRect(x, y, x + barWidth, padding + chartHeight, barPaint)
            
            // Draw percentage text above bar
            textPaint.textSize = 16f
            textPaint.color = textColor
            textPaint.textAlign = Paint.Align.CENTER
            canvas.drawText("${chartData[i].value.toInt()}%", x + barWidth/2, y - 10, textPaint)
        }
    }
    
    private fun drawDataPoints(canvas: Canvas, padding: Float, chartWidth: Float, chartHeight: Float, stepX: Float) {
        if (chartData.isEmpty()) return
        
        for (i in chartData.indices) {
            val x = padding + stepX * i
            val y = padding + chartHeight - (chartData[i].value - minValue) / (maxValue - minValue) * chartHeight
            
            // Draw circle at data point
            paint.color = lineColor
            paint.style = Paint.Style.FILL
            canvas.drawCircle(x, y, 8f, paint)
            
            // Draw percentage text
            textPaint.textSize = 16f
            textPaint.color = textColor
            textPaint.textAlign = Paint.Align.CENTER
            canvas.drawText("${chartData[i].value.toInt()}%", x, y - 15, textPaint)
        }
    }
}
