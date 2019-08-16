package com.example.testfunction.customview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.TextView
import com.example.testfunction.R

/**
 * 进度控件,可显示文字
 * 功能：动画进场，单进度前进动画
 * @author zouweilin 2019-06-17
 */
class TriangleStepView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    View(context, attrs, defStyle) {

    data class Item(var title: String, var colorResId: Int)

    /**
     * 关键点：路径对象，每次使用完毕后请调用path.reset()
     */
    private var path = Path()
    /**
     * 三角形的高（从左到右的高）
     * |╲
     * | ╲
     * |--〉h
     * | ╱
     * |╱
     */
    private var triangleHeight = context.resources.getDimension(R.dimen.dp_16)
    /**
     * 动画效果时通过triangleHeight的值动态给该变量赋值
     */
    private var triangleHeightPercent = 0f
    /**
     * 使用集合中的第一个元素的文字长度作为基础长度，之后的step将在该基础长度上延展
     */
    private var firstTextWidth = 0f
    /**
     * 动画效果时通过firstTextWidth的值动态给该变量赋值
     */
    private var firstTextWidthPercent = 0f
    /**
     * 文字缩进
     */
    private var textPaddingStart = 0f
    /**
     * 动画效果时通过textPaddingStart的值动态给该变量赋值
     */
    private var textPaddingStartPercent = 0f

    private var data: MutableList<Item>? = null

    /**
     * 当前的步骤标记
     */
    private var step = 0
    private var lastStep = 0

    private val paint: Paint = Paint()
    private var textPaint = Paint()
    private var animator: ValueAnimator = ValueAnimator()

    private var rectF: RectF? = null
    /**
     * 定义动画效果是否为初始动画
     */
    private var initAnimation = false
    /**
     * 定义动画效果是否为步骤动画
     */
    private var stepAnimation = false
    /**
     * 当动画快结束时，不要再画准备被盖着的文字了，否则三角形无法完全遮住这个文字
     */
    private var stopDrawHideText = false

    init {
        initAttrs(attrs)
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL

        val defaultSize = context.resources.getDimension(R.dimen.sp_12)
        textPaint.color = Color.parseColor("#212121")
        textPaint.isAntiAlias = true
        textPaint.textSize = defaultSize
        textPaint.style = Paint.Style.FILL
        textPaint.textAlign = Paint.Align.CENTER

        setLayerType(TextView.LAYER_TYPE_SOFTWARE, null)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        attrs ?: return
        val array = context.obtainStyledAttributes(attrs, R.styleable.TriangleStepView)
        textPaddingStart = array.getDimension(R.styleable.TriangleStepView_text_padding_start, 0f)
        array.recycle()
    }

    /**
     * 开始或重置
     */
    fun reset(data: MutableList<Item>?) {
        this.data = data
        if (data == null || data.size == 0) {
            return
        }
        initAnimation = true
        stepAnimation = false
        firstTextWidth = textPaint.measureText(data[0].title)
        initStep()

    }

    /**
     * 跳转到指定步骤
     */
    fun stepTo(step: Int) {
        if (-1 == getDataSize()|| step < 0) {
            return
        }
        this.lastStep = this.step
        this.step = step
        initAnimation = false
        stepAnimation = true
        val hidePreStep = hidePreStep() ?: return
        nextStep(!hidePreStep)
    }

    private fun initStep() {
        animator.cancel()
        val percent = 1f
        animator = ValueAnimator.ofFloat(0f, percent)
        animator.addUpdateListener {
            val curPercent = (it.animatedValue as? Float) ?: return@addUpdateListener
            firstTextWidthPercent = firstTextWidth * curPercent
            triangleHeightPercent = triangleHeight * curPercent
            textPaddingStartPercent = textPaddingStart * curPercent
            invalidate()
        }
        startAnim(animator)
    }

    /**
     * 进入下一个步骤
     */
    private fun nextStep(next: Boolean) {
        animator.cancel()
        val percent = 1f
        animator = if (next) {
            ValueAnimator.ofFloat(0f, percent)
        } else {
            ValueAnimator.ofFloat(percent, 0f)
        }
        animator.addUpdateListener {
            val curPercent = (it.animatedValue as? Float) ?: return@addUpdateListener
            firstTextWidthPercent = firstTextWidth * curPercent
            triangleHeightPercent = triangleHeight
            textPaddingStartPercent = textPaddingStart * curPercent
            stopDrawHideText = if (next) {
                curPercent > 0.9f
            } else {
                curPercent < 0.1f
            }
            invalidate()
        }
        startAnim(animator)
    }

    /**
     * 动画通用属性设置
     */
    private fun startAnim(animator: ValueAnimator) {
        animator.interpolator = LinearInterpolator()
        animator.duration = 500
        animator.start()
    }

    /**
     * @return true：隐藏上一步，false：显示上一步，null：不支持
     */
    private fun hidePreStep(): Boolean? {
        return when {
            lastStep < step -> true
            lastStep > step -> false
            else -> null
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val c = canvas ?: return
        val data = this.data
        val size = data?.size ?: return
        if (data.size == 0) {
            return
        }
        // 从最底层开始绘制
        for (i in size - 1 downTo 0) {
            paint.color = ContextCompat.getColor(context, data[i].colorResId)
            if (initAnimation) {
                // 开局自动升级
                showStep(i, true, c, paint)
                drawTitle(0, data[0].title, canvas, true)
            } else if (stepAnimation) {
                val hideLastStep = hidePreStep() ?: return
                // 点next
                if (hideLastStep) {
                    when {
                        i > lastStep -> showStep(i, false, canvas, paint)
                        // 小于上一步的item全都藏起来，区别在上一步有动画，其他无
                        i == lastStep -> {
                            getTitle(step)?.also { drawTitle(step, it, canvas, false) }
                            hideStep(i, true, canvas, paint)
                            if (!stopDrawHideText) {
                                getTitle(lastStep)?.also { drawTitle(lastStep, it ,canvas, true) }
                            }
                        }
                        else -> hideStep(i, false, canvas, paint)
                    }
                // 点pre
                } else {
                    when {
                        i > step -> showStep(i, false, canvas, paint)
                        i == step -> {
                            if (!stopDrawHideText) {
                                getTitle(lastStep)?.also { drawTitle(lastStep, it, canvas, false) }
                            }
                            showStep(i, true, canvas, paint)
                            getTitle(step)?.also { drawTitle(step, it, canvas, true) }
                        }
                        else -> hideStep(i, false, canvas, paint)
                    }
                }
            }
        }
    }

    /**
     * 绘制标题
     */
    private fun drawTitle(step: Int, title: String, canvas: Canvas, anim: Boolean) {
        val stepWidth = getStepWidth(step, anim)
        // 计算文字长度
        val measureText = textPaint.measureText(title)
        // 2.开始绘制文字，绘制在rectF中心
        val drawEnd = stepWidth - triangleHeight
        val drawStart = drawEnd - measureText
        rectF = RectF(drawStart, 0f, drawEnd, height.toFloat())
        val centerX = rectF!!.centerX()
        val centerY = rectF!!.centerY()
        val fontMetrics = textPaint.fontMetrics
        //为基线到字体上边框的距离,即上图中的top
        val top = fontMetrics.top
        //为基线到字体下边框的距离,即上图中的bottom
        val bottom = fontMetrics.bottom
        //基线中
        val baseLineY = (centerY - top / 2 - bottom / 2)
        canvas.drawText(title, centerX, baseLineY, textPaint)
    }

    /**
     * 获取一条主题所在占用的长度
     */
    private fun getStepWidth(step: Int, anim: Boolean): Float {
        return if (anim) {
            paddingLeft + textPaddingStartPercent + firstTextWidthPercent + triangleHeightPercent * (step + 1)
        } else {
            paddingLeft + textPaddingStart + firstTextWidth + triangleHeight * (step + 1)
        }
    }

    /**
     * 获取一条被隐藏的主题占用的长度，只剩下主题的三角形被显示出来
     */
    private fun getHideStepWidth(step: Int, anim: Boolean): Float {
        return if (anim) {
            paddingLeft + textPaddingStartPercent + firstTextWidthPercent + triangleHeightPercent * (step + 1)
        } else {
            paddingLeft + triangleHeight * (step + 1)
        }
    }

    /**
     * 测试根据title长度动态修正背景长度，使title填充满background，而不是留有很多空白
     * 但是效果不理想，因为每个title长度不一样，显示出来时会一会变长一会变短
     */
//    private fun updateTextWidth(step: Int) {
//        if (getDataSize() == -1) {
//            return
//        }
//        val title = getTitle(step)
//        firstTextWidth = textPaint.measureText(title)
//    }

    /**
     * 绘制显示主题动作，边框和三角形区域
     */
    private fun showStep(step: Int, anim: Boolean, canvas: Canvas, paint: Paint) {
        if (step < 0 || -1 == getDataSize() || step >= getDataSize()) {
            return
        }
        val viewHeight = height.toFloat()
        val stepWidth = getStepWidth(step, anim)
        // 1.开始绘制背景图层
        path.reset()
        // 长方形背景长度
        val rectangleX = stepWidth - triangleHeight
        // top line
        path.lineTo(rectangleX, 0f)
        // 三角形等边斜边1
        path.lineTo(stepWidth, viewHeight * 0.5f)
        // 三角形等边斜边2
        path.lineTo(rectangleX, viewHeight)
        // bottom line
        path.lineTo(0f, viewHeight)
        path.close()
        canvas.drawPath(path, paint)
    }

    /**
     * 绘制隐藏主题动作，最后只剩一个三角形
     */
    private fun hideStep(step: Int, anim: Boolean, canvas: Canvas, paint: Paint) {
        if (step < 0 || -1 == getDataSize() || step >= getDataSize()) {
            return
        }
        val viewHeight = height.toFloat()
        val hideStepWidth = getHideStepWidth(step, anim)
        path.reset()
        val topX = hideStepWidth - triangleHeight
        path.lineTo(topX, 0f)
        path.lineTo(hideStepWidth, viewHeight * 0.5f)
        path.lineTo(topX, viewHeight)
        path.lineTo(0f, viewHeight)
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun getDataSize(): Int {
        return data?.size ?: -1
    }

    /**
     * 拿title时防止指针越界
     */
    private fun getTitle(step: Int): String? {
        val dataSize = getDataSize()
        if (dataSize == -1 || step >= dataSize) {
            return null
        }
        return data?.get(step)?.title
    }

}