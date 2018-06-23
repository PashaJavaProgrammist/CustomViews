package com.haretskiy.pavel.riples

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class CustomClock
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) {

    init {
        attrs?.let {
            context.obtainStyledAttributes(attrs, R.styleable.CustomClock).also { typedArray ->
                try {
                    colorOfClock = typedArray.getInt(R.styleable.CustomClock_color_of_clock, R.color.colorAccent)
                    colorOfFrame = typedArray.getInt(R.styleable.CustomClock_color_of_frame, R.color.colorAccent)
                    colorOfSecArrow = typedArray.getInt(R.styleable.CustomClock_color_of_sec_arrow, R.color.colorAccent)
                    colorOfMinArrow = typedArray.getInt(R.styleable.CustomClock_color_of_min_arrow, R.color.colorAccent)
                    colorOfHourArrow = typedArray.getInt(R.styleable.CustomClock_color_of_hour_arrow, R.color.colorAccent)
                    isShowDigitalClock = typedArray.getBoolean(R.styleable.CustomClock_show_digital_clock, false)
                } finally {
                    typedArray.recycle()
                }
            }
        }
    }

    private var isRunning = false

    private val invalidateHandler: Handler by lazy {
        Handler()
    }

    private val paint = Paint()
    private var colorOfClock = 0
    private var colorOfFrame = 0
    private var colorOfSecArrow = 0
    private var colorOfMinArrow = 0
    private var colorOfHourArrow = 0

    private val formatmS = SimpleDateFormat("SS", Locale.getDefault())
    private val formatS = SimpleDateFormat("ss", Locale.getDefault())
    private val formatM = SimpleDateFormat("mm", Locale.getDefault())
    private val formatH = SimpleDateFormat("hh", Locale.getDefault())
    private val format = SimpleDateFormat("hh:mm:ss", Locale.getDefault())

    private var sec = 0
    private var min = 0
    private var hour = 0

    private var xCenter = 0f
    private var yCenter = 0f
    private var radius = 0f

    private var rect = RectF()

    private val countOfmS = 100
    private val countOfSecorMin = 60

    private val generalSizeConst = 2f
    private val radiusConst = 0.95f
    private val thinStrokeConst = 0.02f
    private val middleStrokeConst = 0.03f
    private val fatStrokeConst = 0.05f

    private val rotateDegreesBig = 30f
    private val rotateDegreesSmall = 6f

    private val halfCircleDegrees = 180

    private val delay = 100L

    private val minDegrees = 0.1
    private val secDegrees = 0.06
    private val hourDegrees = 0.5

    private val secArrConst = 0.1f
    private val secArrConst2 = 0.9f
    private val minArrConst = 0.2f
    private val hArrConst = 0.3f

    private val frameConst = 0.98f

    private val sizeOfNumConst = 0.2f

    private val numConstMidnight = 0.75F
    private val numConstDay = 0.9F
    private val numConstDinner = 1.2f
    private val numConstEvening = 0.08f

    private val divsmallConst = 0.95f
    private val divmidConst = 0.9f
    private val textSizeConst = 8f

    private val horConst = 3f
    private val horConstRect = 2f
    private val verConst = 0.35f
    private val verConstB = 0.55f

    private var isShowDigitalClock = false

    private val date = Date(0)

    override fun invalidate() {
        date.time = System.currentTimeMillis()
        sec = formatS.format(date).toInt() * countOfmS + formatmS.format(date).toInt()
        min = formatM.format(date).toInt() * countOfSecorMin + formatS.format(date).toInt()
        hour = formatH.format(date).toInt() * countOfSecorMin + formatM.format(date).toInt()
        super.invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        xCenter = width / generalSizeConst
        yCenter = height / generalSizeConst
        radius = minOf(height, width) / generalSizeConst * radiusConst
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isRunning = true
        updateClock()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isRunning = false
    }

    private fun updateClock() {
        if (isRunning) {
            invalidateHandler.postDelayed({
                invalidate()
                updateClock()
            }, delay)
        }
    }

    override fun onDraw(canvas: Canvas) {
        setPaintParams()
        drawClockFrame(canvas, xCenter, yCenter, radius)
        drawDividerLines(canvas, xCenter, yCenter, radius)
        drawSmallDividerLines(canvas, xCenter, yCenter, radius)
        if (isShowDigitalClock) {
            drawDigitalClock(canvas, xCenter, yCenter, radius)
            drawRect(canvas, xCenter, yCenter, radius)
        }
        drawNumbers(canvas, radius, xCenter, yCenter)
        drawMinutesHour(canvas, radius, xCenter, yCenter, xCenter, yCenter - radius)
        drawMinutesArrow(canvas, radius, xCenter, yCenter, xCenter, yCenter - radius)
        drawSecondsArrow(canvas, radius, xCenter, yCenter, xCenter, yCenter - radius)
        drawPoint(canvas, xCenter, yCenter)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var width = 0
        var height = 0
        val desiredSize = 3 * minOf(widthSize, heightSize) / 4
        when (widthMode) {
            MeasureSpec.EXACTLY -> width = widthSize
            MeasureSpec.AT_MOST -> width = minOf(desiredSize, widthSize)
            MeasureSpec.UNSPECIFIED -> width = desiredSize
        }
        when (heightMode) {
            MeasureSpec.EXACTLY -> height = heightSize
            MeasureSpec.AT_MOST -> height = minOf(desiredSize, heightSize)
            MeasureSpec.UNSPECIFIED -> height = desiredSize
        }
        val horizontalPaddingsAmount = paddingLeft + paddingRight
        val verticalPaddingsAmount = paddingBottom + paddingTop

        width -= horizontalPaddingsAmount
        height -= verticalPaddingsAmount
        setMeasuredDimension(width, height)
    }

    private fun drawDigitalClock(canvas: Canvas, xCenter: Float, yCenter: Float, radius: Float) {
        paint.apply {
            color = colorOfFrame
            strokeWidth = thinStrokeConst * radius
            textSize = radius / textSizeConst
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(format.format(Date(System.currentTimeMillis())), xCenter, yCenter + radius / 2, paint)

    }

    private fun drawDividerLines(canvas: Canvas, xCenter: Float, yCenter: Float, radius: Float) {
        for (i in 1..12) {
            paint.apply {
                color = colorOfFrame
                strokeWidth = thinStrokeConst * radius
            }
            canvas.apply {
                rotate(rotateDegreesBig, xCenter, yCenter)
                drawLine(xCenter, yCenter + radius * divmidConst, xCenter, yCenter + radius, paint)
            }
        }
    }

    private fun drawSmallDividerLines(canvas: Canvas, xCenter: Float, yCenter: Float, radius: Float) {
        for (i in 1..60) {
            paint.apply {
                color = colorOfFrame
                strokeWidth = thinStrokeConst * radius
            }
            canvas.apply {
                rotate(rotateDegreesSmall, xCenter, yCenter)
                drawLine(xCenter, yCenter + radius * divsmallConst, xCenter, yCenter + radius, paint)
            }
        }
    }

    private fun drawPoint(canvas: Canvas, x: Float, y: Float) {
        paint.apply {
            color = colorOfFrame
            strokeWidth = radius * fatStrokeConst
        }
        canvas.drawPoint(x, y, paint)
    }

    private fun drawRect(canvas: Canvas, x: Float, y: Float, radius: Float) {
        paint.apply {
            color = colorOfFrame
            strokeWidth = radius * thinStrokeConst
            style = Paint.Style.STROKE
        }
        rect.set(x - radius / horConst, y + radius * verConst, x + radius / horConst, y + radius * verConstB)
        canvas.drawRoundRect(rect, x + radius / horConstRect, y, paint)
    }

    private fun drawNumbers(canvas: Canvas, radius: Float, x: Float, y: Float) {
        paint.apply {
            color = colorOfFrame
            textSize = radius * sizeOfNumConst
            textAlign = Paint.Align.CENTER
            style = Paint.Style.FILL
        }
        canvas.apply {
            drawText("12", x, y - radius * numConstMidnight, paint)
            drawText("6", x, y + radius * numConstDay, paint)
            drawText("9", x - radius / numConstDinner, y + radius * numConstEvening, paint)
            drawText("3", x + radius / numConstDinner, y + radius * numConstEvening, paint)
        }
    }

    private fun setPaintParams() {
        paint.apply {
            isAntiAlias = true
            isSubpixelText = true
        }
    }

    private fun drawClockFrame(canvas: Canvas, xCenter: Float, yCenter: Float, radius: Float) {
        paint.apply {
            color = colorOfFrame
            canvas.drawCircle(xCenter, yCenter, radius, this)
            color = colorOfClock
            canvas.drawCircle(xCenter, yCenter, radius * frameConst, this)
        }
    }

    private fun drawSecondsArrow(canvas: Canvas, radius: Float, xFirst: Float, yFirst: Float, xSecond: Float, ySecond: Float) {
        val list = getCoordinatesSec(xFirst, yFirst, xSecond, ySecond + radius * secArrConst)
        val listEnd = getCoordinatesSecEnd(xFirst, yFirst, xSecond, ySecond + radius * secArrConst2)
        paint.apply {
            color = colorOfSecArrow
            strokeWidth = thinStrokeConst / 2 * radius
            canvas.drawLine(xFirst, yFirst, list[0], list[1], this)
            canvas.drawLine(xFirst, yFirst, listEnd[0], listEnd[1], this)
        }
    }

    private fun drawMinutesArrow(canvas: Canvas, radius: Float, xFirst: Float, yFirst: Float, xSecond: Float, ySecond: Float) {
        val list = getCoordinatesMin(xFirst, yFirst, xSecond, ySecond + radius * minArrConst)
        paint.apply {
            color = colorOfMinArrow
            strokeWidth = thinStrokeConst * radius
            canvas.drawLine(xFirst, yFirst, list[0], list[1], this)
        }
    }

    private fun drawMinutesHour(canvas: Canvas, radius: Float, xFirst: Float, yFirst: Float, xSecond: Float, ySecond: Float) {
        val list = getCoordinatesHour(xFirst, yFirst, xSecond, ySecond + radius * hArrConst)
        paint.apply {
            color = colorOfHourArrow
            strokeWidth = middleStrokeConst * radius
            canvas.drawLine(xFirst, yFirst, list[0], list[1], this)
        }
    }

    private fun getCoordinatesMin(x: Float, y: Float, x0: Float, y0: Float): MutableList<Float> {
        val degree = (min * minDegrees) * (PI / halfCircleDegrees)
        return getCoordinates(degree, x, y, x0, y0)
    }

    private fun getCoordinatesSec(x: Float, y: Float, x0: Float, y0: Float): MutableList<Float> {
        val degree = (sec * secDegrees) * (PI / halfCircleDegrees)
        return getCoordinates(degree, x, y, x0, y0)
    }

    private fun getCoordinatesSecEnd(x: Float, y: Float, x0: Float, y0: Float): MutableList<Float> {
        val degree = (sec * secDegrees) * (PI / halfCircleDegrees) - PI
        return getCoordinates(degree, x, y, x0, y0)
    }

    private fun getCoordinatesHour(x: Float, y: Float, x0: Float, y0: Float): MutableList<Float> {
        val degree = (hour * hourDegrees) * (PI / halfCircleDegrees)
        return getCoordinates(degree, x, y, x0, y0)
    }

    private fun getCoordinates(degree: Double, x: Float, y: Float, x0: Float, y0: Float): MutableList<Float> {
        val rx = x0 - x
        val ry = y0 - y
        val c = cos(degree)
        val s = sin(degree)
        val resx = x + rx * c - ry * s
        val resy = y + rx * s + ry * c
        return mutableListOf(resx.toFloat(), resy.toFloat())
    }

}