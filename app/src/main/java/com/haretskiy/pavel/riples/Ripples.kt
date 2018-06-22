package com.haretskiy.pavel.riples

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat.getDrawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator


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

    private var counter = 0

    private var isRunning = false
    private var isAnimStarted = false

    private var circleRadius = 0
    private var newCircleRadius = 0

    private val WIDTH_RIPLE = 120
    private val ANIM_DUR = 1000L

    private var lowColor = 0
    private var highColor = 0

    private var xCenter = 0f
    private var yCenter = 0f
    private var radius = 0f

    private val generalSizeConst = 2f

    private val thinStrokeConst = 0.02f

    private val radiusConst = 0.95f

    private val paint = Paint()

    private val drawable = getDrawable(context, R.drawable.circle)
    private val icon = getDrawable(context, R.mipmap.ic_launcher_round)


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        xCenter = width / generalSizeConst
        yCenter = height / generalSizeConst
        radius = minOf(height, width) / generalSizeConst * radiusConst
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isRunning = true
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
        canvas?.let { it ->
            if (circleRadius == 0) {
                circleRadius = if (it.width < it.height) {
                    it.width - 10
                } else {
                    it.height - 10
                }
            }
        }
        canvas?.let { canvas1 ->
            drawCircle(canvas1, newCircleRadius)
            drawIcon(canvas1)
//            (1..10).forEach {
//                drawCircle(canvas1, getRadius(it * WIDTH_RIPLE))
//            }
        }
        if (!isAnimStarted) {
            animator().start()
        }
    }

    private fun getRadius(value: Int): Int {
        return if (newCircleRadius - value > 0) newCircleRadius - value else 0
    }

    private fun drawIcon(canvas: Canvas) {
        val radius = 100
        paint.apply {
            color = highColor
            strokeWidth = thinStrokeConst * radius
            textAlign = Paint.Align.CENTER
        }
        icon?.let {
            it.setBounds(circleRadius / 2 - radius / 2, circleRadius / 2 - radius / 2, circleRadius / 2 + radius / 2, circleRadius / 2 + radius / 2)
            it.draw(canvas)
        }
    }

    private fun drawCircle(canvas: Canvas, radius: Int) {
        if (radius <= circleRadius) {
            paint.apply {
                color = highColor
                strokeWidth = thinStrokeConst * radius
                textAlign = Paint.Align.CENTER
            }
            drawable?.let {
                it.setBounds(circleRadius / 2 - radius / 2, circleRadius / 2 - radius / 2, circleRadius / 2 + radius / 2, circleRadius / 2 + radius / 2)
                //todo: fix alpha
                it.alpha = convertInAlpha(radius)
                it.draw(canvas)
            }
        }
        if (radius > WIDTH_RIPLE) {
            drawCircle(canvas, radius - WIDTH_RIPLE)
        }
    }

    private fun convertInAlpha(r: Int) = ((circleRadius - r) * 100 / circleRadius) * 2

    private fun animator(): ValueAnimator {
        val animator = if (counter % 2 == 0) ValueAnimator.ofInt(0, circleRadius * 1000) else ValueAnimator.ofInt(circleRadius, 100)
        animator.duration = ANIM_DUR * 12000
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animation ->
            newCircleRadius = animation.animatedValue as Int
            invalidate()
        }
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(p0: Animator?) {
                isAnimStarted = false
            }

            override fun onAnimationStart(p0: Animator?) {
                isAnimStarted = true
            }

            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationCancel(p0: Animator?) {}

        })
        counter++
        return animator
    }

}