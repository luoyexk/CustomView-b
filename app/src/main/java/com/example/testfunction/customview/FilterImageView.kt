package com.example.testfunction.customview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView

/**
 * 可以控制图片颜色
 * 从原色到黑白的控件
 * @author zouweilin 2019-06-14
 */
class FilterImageView : ImageView {

    /**
     * 颜色渐变时间
     */
    private val colorChangeTime = 800L
    /**
     * 颜色矩阵
     */
    private val colorMatrix = ColorMatrix()
    /**
     * 色调，改变颜色
     */
    private val hueMatrix = ColorMatrix()
    /**
     * 饱和度，改变颜色的纯度
     */
    private val saturationMatrix = ColorMatrix()
    /**
     * 亮度，控制明暗
     */
    private val brightnessMatrix = ColorMatrix()
    /**
     * 色调
     */
    private val hueValue = 0f
    /**
     * 亮度
     */
    private val brightnessValue = 1f
    /**
     * 饱和度
     * 黑白是0，原色是1
     */
    private var saturationValue = 1f

    private val naturalColor = 1f
    private val grayColor = 0f


    private val animate: ValueAnimator = ValueAnimator()

    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    /**
     * @param select true:变成原色图片，false:变成灰色图片
     */
    fun selected(select: Boolean) {
        val from = if (select) grayColor else naturalColor
        val to = if (select) naturalColor else grayColor
        animate.cancel()
        animate.setFloatValues(from, to)
        animate.addUpdateListener { animation ->
            saturationValue = ((animation?.animatedValue as? Float) ?: return@addUpdateListener)
            Log.d("zzzz","value: $saturationValue")
            displayImageColorMatrixHSB(this@FilterImageView, hueValue, saturationValue, brightnessValue)
        }
        animate.duration = colorChangeTime
        animate.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animate.cancel()
    }

    /**
     * 通过色调、饱和度、亮度来操作ImageView的ColorMatrixColorFilter
     *
     * @param hueValue        色调值
     * @param saturationValue 饱和度值
     * @param brightnessValue 亮度值
     */
    private fun displayImageColorMatrixHSB(
        imageView: ImageView,
        hueValue: Float,
        saturationValue: Float,
        brightnessValue: Float
    ) {
        //设置色相，为0°和360的时候相当于原图
        hueMatrix.reset()
        hueMatrix.setRotate(0, hueValue)
        hueMatrix.setRotate(1, hueValue)
        hueMatrix.setRotate(2, hueValue)

        //设置饱和度，为1的时候相当于原图
        saturationMatrix.reset()
        saturationMatrix.setSaturation(saturationValue)

        //亮度，为1的时候相当于原图
        brightnessMatrix.reset()
        brightnessMatrix.setScale(brightnessValue, brightnessValue, brightnessValue, 1f)

        //将上面三种效果和选中的模式混合在一起
        colorMatrix.reset()
        colorMatrix.postConcat(hueMatrix)
        colorMatrix.postConcat(saturationMatrix)
        colorMatrix.postConcat(brightnessMatrix)

        imageView.colorFilter = ColorMatrixColorFilter(colorMatrix)
    }

}