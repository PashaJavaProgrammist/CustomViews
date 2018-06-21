package com.haretskiy.pavel.riples

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.support.v4.content.ContextCompat.getDrawable
import android.util.AttributeSet
import android.view.View

class Ripples
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) {

    init {
        attrs?.let {
            context.obtainStyledAttributes(attrs, R.styleable.Ripples).also { typedArray ->
                try {
                    lowColor = typedArray.getInt(R.styleable.Ripples_lowColor, R.color.colorPrimary)
                    highColor = typedArray.getInt(R.styleable.Ripples_highColor, R.color.colorPrimaryDark)
                } finally {
                    typedArray.recycle()
                }
            }
        }
    }

    private var isRunning = false

    private val invalidateHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private var lowColor = 0
    private var highColor = 0

    private val delay = 100L

    private var xCenter = 0f
    private var yCenter = 0f
    private var radius = 0f

    private val generalSizeConst = 2f

    private val thinStrokeConst = 0.02f

    private val radiusConst = 0.95f

    private val paint = Paint()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        xCenter = width / generalSizeConst
        yCenter = height / generalSizeConst
        radius = minOf(height, width) / generalSizeConst * radiusConst
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isRunning = true
        update()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isRunning = false
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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let { it -> drawCircle(it) }
    }

    private fun drawCircle(canvas: Canvas) {
        paint.apply {
            color = highColor
            strokeWidth = thinStrokeConst * radius
            textAlign = Paint.Align.CENTER
        }
        val drawable = getDrawable(context, R.drawable.circle)?.let {
            if (canvas.width < canvas.height) {
                it.setBounds(10, 10, canvas.width - 10, canvas.width - 10)
            } else {
                it.setBounds(10, 10, canvas.height - 10, canvas.height - 10)
            }
            it.draw(canvas)
        }
    }

    private fun update() {
        if (isRunning) {
            invalidateHandler.postDelayed({
                invalidate()
                update()
            }, delay)
        }
    }

}