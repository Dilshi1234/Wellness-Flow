package com.wellnessflow.habbittracker.ui.statistics

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.wellnessflow.habbittracker.R

class MoodProgressChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class MoodDataPoint(
        val day: String,
        val progress: Float,
        val emoji: String,
        val moodValue: Float
    )

    private var dataPoints = listOf<MoodDataPoint>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val emojiPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    
    private val chartPadding = 60f
    private val axisStrokeWidth = 4f
    private val gridStrokeWidth = 2f
    private val pointRadius = 8f
    private val lineStrokeWidth = 4f
    
    private val primaryColor = ContextCompat.getColor(context, R.color.primary_purple)
    private val gridColor = ContextCompat.getColor(context, R.color.divider)
    private val textColor = ContextCompat.getColor(context, R.color.text_secondary)
    private val backgroundColor = ContextCompat.getColor(context, R.color.card_background_white)

    init {
        setupPaints()
    }

    private fun setupPaints() {
        // Line paint
        paint.color = primaryColor
        paint.strokeWidth = lineStrokeWidth
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND

        // Point paint
        paint.color = primaryColor
        paint.style = Paint.Style.FILL

        // Text paint
        textPaint.color = textColor
        textPaint.textSize = 32f
        textPaint.textAlign = Paint.Align.CENTER

        // Emoji paint
        emojiPaint.textSize = 40f
        emojiPaint.textAlign = Paint.Align.CENTER
    }

    fun updateData(data: List<MoodDataPoint>) {
        dataPoints = data
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        if (dataPoints.isEmpty()) {
            drawNoDataMessage(canvas)
            return
        }

        val width = width.toFloat()
        val height = height.toFloat()
        val chartWidth = width - 2 * chartPadding
        val chartHeight = height - 2 * chartPadding

        // Draw background
        canvas.drawColor(backgroundColor)

        // Draw grid lines
        drawGridLines(canvas, chartWidth, chartHeight)

        // Draw axes
        drawAxes(canvas, chartWidth, chartHeight)

        // Draw data line and points
        drawDataLine(canvas, chartWidth, chartHeight)

        // Draw labels
        drawLabels(canvas, chartWidth, chartHeight)
    }

    private fun drawNoDataMessage(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()
        
        textPaint.textSize = 48f
        textPaint.color = textColor
        textPaint.textAlign = Paint.Align.CENTER
        
        canvas.drawText("No mood data available", width / 2, height / 2, textPaint)
    }

    private fun drawGridLines(canvas: Canvas, chartWidth: Float, chartHeight: Float) {
        paint.color = gridColor
        paint.strokeWidth = gridStrokeWidth
        paint.style = Paint.Style.STROKE

        // Horizontal grid lines (0%, 25%, 50%, 75%, 100%)
        for (i in 0..4) {
            val y = chartPadding + (chartHeight * i / 4)
            canvas.drawLine(chartPadding, y, chartPadding + chartWidth, y, paint)
        }

        // Vertical grid lines
        if (dataPoints.isNotEmpty()) {
            for (i in dataPoints.indices) {
                val x = chartPadding + (chartWidth * i / (dataPoints.size - 1))
                canvas.drawLine(x, chartPadding, x, chartPadding + chartHeight, paint)
            }
        }
    }

    private fun drawAxes(canvas: Canvas, chartWidth: Float, chartHeight: Float) {
        paint.color = primaryColor
        paint.strokeWidth = axisStrokeWidth
        paint.style = Paint.Style.STROKE

        // X-axis
        canvas.drawLine(chartPadding, chartPadding + chartHeight, chartPadding + chartWidth, chartPadding + chartHeight, paint)
        
        // Y-axis
        canvas.drawLine(chartPadding, chartPadding, chartPadding, chartPadding + chartHeight, paint)
    }

    private fun drawDataLine(canvas: Canvas, chartWidth: Float, chartHeight: Float) {
        if (dataPoints.size < 2) return

        paint.color = primaryColor
        paint.strokeWidth = lineStrokeWidth
        paint.style = Paint.Style.STROKE

        val path = Path()
        val points = mutableListOf<PointF>()

        // Calculate points
        dataPoints.forEachIndexed { index, dataPoint ->
            val x = chartPadding + (chartWidth * index / (dataPoints.size - 1))
            val y = chartPadding + chartHeight - (chartHeight * dataPoint.progress / 100f)
            points.add(PointF(x, y))
        }

        // Draw line
        path.moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size) {
            path.lineTo(points[i].x, points[i].y)
        }
        canvas.drawPath(path, paint)

        // Draw points and emojis
        points.forEachIndexed { index, point ->
            // Draw point circle
            paint.style = Paint.Style.FILL
            canvas.drawCircle(point.x, point.y, pointRadius, paint)
            
            // Draw emoji above point
            val emoji = dataPoints[index].emoji
            val emojiY = point.y - 30f
            canvas.drawText(emoji, point.x, emojiY, emojiPaint)
            
            // Draw percentage below point
            val percentage = dataPoints[index].progress.toInt()
            textPaint.textSize = 24f
            textPaint.color = textColor
            canvas.drawText("$percentage%", point.x, point.y + 40f, textPaint)
        }
    }

    private fun drawLabels(canvas: Canvas, chartWidth: Float, chartHeight: Float) {
        textPaint.textSize = 28f
        textPaint.color = textColor
        textPaint.textAlign = Paint.Align.CENTER

        // Y-axis labels (0%, 25%, 50%, 75%, 100%)
        for (i in 0..4) {
            val percentage = i * 25
            val y = chartPadding + chartHeight - (chartHeight * i / 4)
            canvas.drawText("$percentage%", chartPadding - 20f, y + 10f, textPaint)
        }

        // X-axis labels (day names)
        dataPoints.forEachIndexed { index, dataPoint ->
            val x = chartPadding + (chartWidth * index / (dataPoints.size - 1))
            val y = chartPadding + chartHeight + 40f
            canvas.drawText(dataPoint.day, x, y, textPaint)
        }
    }
}