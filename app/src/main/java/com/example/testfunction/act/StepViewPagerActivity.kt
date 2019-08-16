package com.example.testfunction.act

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.testfunction.R
import com.example.testfunction.customview.TriangleStepView
import kotlinx.android.synthetic.main.activity_step_view_pager.*
import java.lang.IllegalArgumentException

class StepViewPagerActivity : AppCompatActivity() {

    val list = createItem()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_view_pager)

//        val controller = StepViewPagerController(viewPager, list)

        initInput()
        tvReset.setOnClickListener { tvTriangle.reset(list)}
        begin()

    }

    private fun begin() {
        tvTriangle.postDelayed({
            tvTriangle.reset(list)
        }, 1000)
    }

    private fun createItem(): MutableList<TriangleStepView.Item> {
        val titleList = mutableListOf(
            "Property Boundary",
            "Land Boundary",
            "Property Height",
            "Obstacle Height",
            "Review"
        )
        val colorResId = mutableListOf(
            R.color.color_step_1,
            R.color.color_step_2,
            R.color.color_step_3,
            R.color.color_step_4,
            R.color.color_step_5
        )
        val result = mutableListOf<TriangleStepView.Item>()
        val size = titleList.size
        if (colorResId.size != size) {
            throw IllegalArgumentException("two list size must be same")
        }
        for (i in 0 until size) {
            val title = titleList[i]
            val color = colorResId[i]
            val item = TriangleStepView.Item(title, color)
            result.add(item)
        }
        return result
    }

    private var step = 0
    private fun initInput() {
        stepMinus.text = "<"
        stepAdd.text = ">"
        stepAdd.setOnClickListener {
            edit_step.setText("${++step}")
            tvTriangle.stepTo(step)
        }
        stepMinus.setOnClickListener {
            edit_step.setText("${--step}")
            tvTriangle.stepTo(step)
        }
    }
}
