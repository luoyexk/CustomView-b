package com.example.testfunction.customview

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.example.testfunction.R
import kotlin.math.ceil
import kotlin.math.max

/**
 * 竖直的seekbar,用于控制地图的明暗
 * @author zouweilin 2019-07-11
 */
class VerticalSeekBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    View(context, attrs, defStyle) {

    interface OnSeekBarChangeListener {
        fun onProgressChanged(progress: Int, changeByUser: Boolean)

        fun onStartTrackingTouch(bar: VerticalSeekBar)

        fun onStopTrackingTouch(bar: VerticalSeekBar)
    }

    private val colorHigh = ContextCompat.getColor(context, R.color.vertical_seek_bar_bg_high)
    private val colorLow = ContextCompat.getColor(context, R.color.vertical_seek_bar_bg_low)
    private val paint: Paint = Paint()
    private val drawableHigh = R.mipmap.dark_mask_high
    private val drawableLow = R.mipmap.dark_mask_low
    private val bitmapHigh: Bitmap
    private val bitmapLow: Bitmap
    private var bitmapPadding: Float
    private var topRect: Rect? = null
    private var bottomRect: Rect? = null
    private var rectBitmapHigh: Rect? = null
    private var rectBitmapLow: Rect? = null
    private val porterDuff: PorterDuffXfermode
    private var rectf: RectF? = null
    private var percent = 0
    private var lastPercent = -1
    private var corners = 30f
    private var touchY = 0f
    private var listener: OnSeekBarChangeListener? = null
    var maxProgress = 100

    init {
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.ROUND

        bitmapHigh = BitmapFactory.decodeResource(context.resources, drawableHigh)
        bitmapLow = BitmapFactory.decodeResource(context.resources, drawableLow)
        bitmapPadding = context.resources.getDimension(R.dimen.dp_8)
        bitmapHigh?.also { rectBitmapHigh = Rect(0, 0, it.width, it.height) }
        bitmapLow?.also { rectBitmapLow = Rect(0, 0, it.width, it.height) }
        porterDuff = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        setLayerType(TextView.LAYER_TYPE_SOFTWARE, null)

    }

    fun setOnSeekBarChangeListener(listener: OnSeekBarChangeListener) {
        this.listener = listener
    }

    fun setProgress(progress: Int) {
        touchY = progress * height / 100f
        listener?.onProgressChanged(progress, true)
        invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (width - 2 * bitmapPadding < 0) {
            bitmapPadding = 0f
        }
        val bitmapHighHeight = rectBitmapHigh?.bottom ?: return
        val bitmapLogHeight = rectBitmapLow?.bottom ?: return
        val bitmapHighWidth = rectBitmapHigh?.width() ?: return
        val bitmapLowWidth = rectBitmapLow?.width() ?: return

        val padStart = ((width - bitmapHighWidth) * 0.5).toInt()
        val padding = bitmapPadding.toInt()
        val padStart2 = ((width - bitmapLowWidth) * 0.5).toInt()
        topRect = Rect(padStart, padding, width - padStart, padding + bitmapHighHeight)
        bottomRect = Rect(padStart2, height - bitmapLogHeight - padding, width - padStart2, height - padding)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val ev = event ?: return false
        val height = height
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                listener?.onStartTrackingTouch(this)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                touchY = ev.y
                percent = (ceil((touchY / height * maxProgress).toDouble())).toInt()
                checkPercent(percent)
                if (lastPercent != percent) {
                    listener?.onProgressChanged(percent, false)
                    lastPercent = percent
                }
            }
            MotionEvent.ACTION_UP -> {
                listener?.onStopTrackingTouch(this)
            }
            else -> {
            }
        }
        invalidate()
        return true
    }

    private fun checkPercent(percent: Int) {
        if (percent < 0) {
            this.percent = 0
        } else if (percent > maxProgress) {
            this.percent = maxProgress
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val paint = this.paint
        canvas ?: return
        val myWidth = width.toFloat()
        val myHeight = height.toFloat()
        if (rectf == null) {
            rectf = RectF(0f, 0f, myWidth, myHeight)
        }
        val sc = canvas.saveLayer(rectf, null)


        // 画浅透明黑和圆角
        paint.color = colorHigh
        canvas.drawRoundRect(rectf!!, corners, corners, paint)
        paint.xfermode = porterDuff

        // 画深透明黑
        paint.color = colorLow
        canvas.drawRect(0f, touchY, myWidth, myHeight, paint)

        paint.xfermode = null
        topRect?.also {
            canvas.drawBitmap(bitmapHigh, rectBitmapHigh, it, paint)
        }
        bottomRect?.also {
            canvas.drawBitmap(bitmapLow, rectBitmapLow, it, paint)
        }

        canvas.restoreToCount(sc)
    }


}