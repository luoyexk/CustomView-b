package com.example.testfunction.customview.stepviewpager

import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.testfunction.R

/**
 *
 * @author zouweilin 2019-06-17
 */
class StepViewPagerController(viewPager: ViewPager, list: MutableList<Item>) {

    init {
        val pagerAdapter = StepPagerAdapter(list)
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = list.size
    }

    data class Item(var title: String, var colorResId: Int)

    class StepPagerAdapter(var data: MutableList<Item>): PagerAdapter(){
        override fun isViewFromObject(p0: View, p1: Any): Boolean {
            return p0 == p1
        }

        override fun getCount(): Int {
            return data.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = LayoutInflater.from(container.context).inflate(R.layout.item_arc_step_title, container, false)
//            val imageView = view.findViewById<ImageView>(R.id.ivArc)
            val tvTitle = view.findViewById<TextView>(R.id.title)
            val item = data[position]
            val colorResId = item.colorResId
            val t = item.title
//            imageView.setBackgroundResource(colorResId)
            tvTitle.text = t
            tvTitle.setBackgroundResource(colorResId)
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
            container.removeView(any as View?)
        }
    }

}