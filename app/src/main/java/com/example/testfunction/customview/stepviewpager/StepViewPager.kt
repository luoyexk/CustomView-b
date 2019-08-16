package com.example.testfunction.customview.stepviewpager

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View
import com.example.testfunction.R

/**
 *
 * @author zouweilin 2019-06-17
 */
class StepViewPager: ViewPager {

    constructor(context: Context): super(context)

    constructor(context: Context, attr: AttributeSet): super(context, attr)

    private val offsetX: Float = context.resources.getDimension(R.dimen.dp_16)

    init {
        setPageTransformer(false, StepPageTransformer(offsetX))
    }

    class StepPageTransformer(var offsetX: Float): PageTransformer{

        override fun transformPage(page: View, position: Float) {

//            page.alpha = 0.5f
            page.translationX = (-page.width * position) + offsetX * position
        }

    }
}