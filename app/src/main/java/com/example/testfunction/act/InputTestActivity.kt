package com.example.testfunction.act

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import com.example.testfunction.R
import kotlinx.android.synthetic.main.activity_input_test.*

class InputTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_test)
        et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.e("zz", "before s:$s start:$start count:$count affter:$after")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.e("zzz", "onChange s:$s start:$start before:$before count:$count ")
            }

            override fun afterTextChanged(s: Editable?) {
                Log.e("zzz", "after s:${s.toString()}")
            }

        })
    }

    var show = false
    fun onClick(view: View) {
        val height = ll.height
        Log.e("zzz", "height$height")
        showInOutAlpha(ll, show)
        showInOutAnim(ll2, show, ll.height)
        show = !show
    }

    fun showInOutAnim(target: View, expandable: Boolean, height: Int) {

        if (expandable) {
            val hideAnim = TranslateAnimation(
                Animation.ABSOLUTE, 0.0f,
                Animation.ABSOLUTE, 0.0f,
                Animation.ABSOLUTE, 0.0f,
                Animation.ABSOLUTE, height.toFloat()
            )
            hideAnim.duration = 500
            target.startAnimation(hideAnim)
        } else {
            // -1.0f表示父布局的-100%p 即父布局的宽度左偏移100%距离
            val showAnim = TranslateAnimation(
                Animation.ABSOLUTE, 0.0f,
                Animation.ABSOLUTE, 0.0f,
                Animation.ABSOLUTE, height.toFloat(),
                Animation.ABSOLUTE, 0.0f
            )
            showAnim.duration = 500
            target.startAnimation(showAnim)
        }
    }

    /**
     * 显示隐藏动画, 渐隐效果
     */
    fun showInOutAlpha(target: View, gone: Boolean) {
        val animation = getAlphaAnim(gone)
        animation.duration = 500
        target.startAnimation(animation)
        target.visibility = if (gone) View.GONE else View.VISIBLE
    }

    private fun getAlphaAnim(gone: Boolean): Animation {
        return if (gone) {
            AlphaAnimation(1f, 0f)
        } else {
            AlphaAnimation(0f, 1f)
        }
    }
}
