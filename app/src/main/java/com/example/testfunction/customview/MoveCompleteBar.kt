package com.example.testfunction.customview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v4.content.ContextCompat.getSystemService
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.testfunction.R

/**
 * TODO: document your custom view class.
 */
class MoveCompleteBar : View {

    private var _exampleString: String? = null // TODO: use a default from R.string...
    private var _exampleColor: Int = Color.RED // TODO: use a default from R.color...
    private var _exampleDimension: Float = 0f // TODO: use a default from R.dimen...

    private var textPaint: TextPaint? = null
    private var textWidth: Float = 0f
    private var textHeight: Float = 0f

    private val paint = Paint()
    private val backgroundRectF = RectF()
    private val buttonRectF = RectF()
    private var strokeWidth = 1f
    /**
     * 触摸点的x
     */
    private var touchX = 0
    /**
     * button绘制的x中心点
     */
    private var drawX = 0
    /**
     * button的默认位置,应该是长度的1/8
     */
    private var buttonDefX = 0

    /**
     * 圆角设置
     */
    private var rx = 5f
    private var ry = 5f

    /**
     * 触摸圆形的半径
     */
    private var circleRadius = 20f

    /**
     * The text to draw
     */
    var exampleString: String?
        get() = _exampleString
        set(value) {
            _exampleString = value
            invalidateTextPaintAndMeasurements()
        }

    /**
     * The font color
     */
    var exampleColor: Int
        get() = _exampleColor
        set(value) {
            _exampleColor = value
            invalidateTextPaintAndMeasurements()
        }

    /**
     * In the example view, this dimension is the font size.
     */
    var exampleDimension: Float
        get() = _exampleDimension
        set(value) {
            _exampleDimension = value
            invalidateTextPaintAndMeasurements()
        }

    /**
     * In the example view, this drawable is drawn above the text.
     */
    var exampleDrawable: Drawable? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(attrs, R.styleable.MoveCompleteBar, defStyle, 0)

        _exampleString = a.getString(R.styleable.MoveCompleteBar_exampleString)
        _exampleColor = a.getColor(R.styleable.MoveCompleteBar_exampleColor, exampleColor)
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        _exampleDimension = a.getDimension(R.styleable.MoveCompleteBar_exampleDimension, exampleDimension)

        if (a.hasValue(R.styleable.MoveCompleteBar_drawable_move)) {
            exampleDrawable = a.getDrawable(R.styleable.MoveCompleteBar_drawable_move)
            exampleDrawable?.callback = this
            circleRadius = exampleDrawable?.bounds?.width()?.toFloat() ?: circleRadius
        }

        a.recycle()

        // Set up a default TextPaint object
        textPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
        }

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements()

        paint.isAntiAlias = true

        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    private fun invalidateTextPaintAndMeasurements() {
        textPaint?.let {
            it.textSize = exampleDimension
            it.color = exampleColor
            textWidth = it.measureText(exampleString)
            textHeight = it.fontMetrics.bottom
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width == 0 || height == 0) {
            return
        }
        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom
        val width = width
        val height = height
        buttonDefX = width / 8
        val buttonWidth = width / 3
        val buttonHeight = height


        updateBackground(backgroundRectF)
        canvas.drawRoundRect(backgroundRectF, rx, ry, paint)

        // draw button
        updateButtonLocation(buttonRectF, buttonWidth.toFloat(), buttonHeight.toFloat(), touchX)
        canvas.drawRoundRect(buttonRectF, rx, ry, paint)

        // todo
        val circleY = height / 2

        exampleString?.let {
            // Draw the text.
            canvas.drawText(
                it,
                paddingLeft + (contentWidth - textWidth) / 2,
                paddingTop + (contentHeight + textHeight) / 2,
                textPaint
            )
        }

        // Draw the example drawable on top of the text.
        exampleDrawable?.let {
            it.setBounds(
                paddingLeft, paddingTop,
                paddingLeft + contentWidth, paddingTop + contentHeight
            )
            it.draw(canvas)
        }
    }

    private fun updateButtonLocation(buttonRectF: RectF, w: Float, h: Float, touchX: Int) {
        // 确定button在stroke内
        with(buttonRectF) {
            left = touchX - w * 0.5f
            top = strokeWidth
            right = touchX + w * 0.5f
            bottom = h - strokeWidth
        }
    }

    private fun updateBackground(backgroundRectF: RectF) {
        with(backgroundRectF) {
            left = 0f
            top = 0f
            right = width.toFloat()
            bottom = height.toFloat()
        }
    }

    /**
     * 限制button的绘制范围总在背景里
     */
    private fun outOfBounds(): Boolean {
        if (drawX < buttonDefX) {
            drawX = buttonDefX
            return true
        } else if (drawX > width - buttonDefX) {
            drawX = width - buttonDefX
            return true
        }
        return false
    }

    private var xButton = 0
    private var yButton = 0
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        val bounds = exampleDrawable?.bounds ?: return false
        when (ev?.action ?: return false) {
            MotionEvent.ACTION_DOWN -> ev.takeIf { bounds.contains(it.x.toInt(), it.y.toInt()) }?.let { return true }
            MotionEvent.ACTION_MOVE -> onActionMove(ev)
            MotionEvent.ACTION_UP -> onActionUp(ev)

            else -> {

            }
        }

        return false
    }

    private fun onActionUp(ev: MotionEvent) {
        touchX

    }

    private fun onActionMove(ev: MotionEvent) {
        touchX = ev.x.toInt()
        drawX = touchX
        if (!outOfBounds()) {

            invalidate()
        }
    }

    private fun vabrate() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(200, 100))
        } else {
            vibrator?.vibrate(200)
        }
    }
}
