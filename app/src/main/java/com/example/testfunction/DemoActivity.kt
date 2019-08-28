package com.example.testfunction

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import com.example.testfunction.act.*
import com.example.testfunction.fragment.MoveCompleteSBFragment
import kotlinx.android.synthetic.main.activity_demo.*

class DemoActivity : AppCompatActivity() {

    private val fragmentContainer = R.id.container
    private var curFragment: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)
        initView()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { curFragment?.let { removeFragment(it) } }

        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, TITLE)
        listView.setOnItemClickListener { _, _, position, _ ->
            when (TITLE.getOrNull(position)) {
                CARD_VIEW -> startActivity(Intent(this, CardItemActivity::class.java))
                Step_ViewPager -> startActivity(Intent(this, StepViewPagerActivity::class.java))
                WaveProgressView -> startActivity(Intent(this, WaveProgressActivity::class.java))
                VerticalBar -> startActivity(Intent(this, VerticalProgressBarActivity::class.java))
                EDITTEXT_TEXT_WATCHER -> startActivity(Intent(this, InputTestActivity::class.java))
                MoveCompleteSeekBar -> addFragment(MoveCompleteSBFragment.newInstance("", "").also { curFragment = it })
            }
        }
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(fragmentContainer, fragment).commit()
    }

    private fun removeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().remove(fragment).commit()
    }

    companion object {
        const val CARD_VIEW = "Card view"
        const val Step_ViewPager = "Step ViewPager"
        const val WaveProgressView = "WaveProgressView"
        const val VerticalBar = "VerticalBar"
        const val EDITTEXT_TEXT_WATCHER = "Edittext text watcher"
        const val MoveCompleteSeekBar = "Move to End Complete View"
        val TITLE = mutableListOf(
            CARD_VIEW,
            Step_ViewPager,
            WaveProgressView,
            VerticalBar,
            EDITTEXT_TEXT_WATCHER
        )
    }
}
