package com.example.testfunction.act

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.SeekBar
import com.example.testfunction.R
import com.example.testfunction.customview.VerticalSeekBar
import kotlinx.android.synthetic.main.activity_vertical_progress_bar.*

class VerticalProgressBarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vertical_progress_bar)
        seekBar.setOnSeekBarChangeListener(object : VerticalSeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(progress: Int, changeByUser: Boolean) {
                Log.e("zzz", "progress:$progress changeByUser$changeByUser")
            }

            override fun onStartTrackingTouch(bar: VerticalSeekBar) {
                Log.e("zzz", "start")
            }

            override fun onStopTrackingTouch(bar: VerticalSeekBar) {
                Log.e("zzz", "stop")
            }

        })
        root.setOnClickListener { seekBar.maxProgress = 70 }
    }
}
