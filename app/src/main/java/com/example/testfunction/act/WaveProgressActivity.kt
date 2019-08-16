package com.example.testfunction.act

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.SeekBar
import com.example.testfunction.R
import com.example.testfunction.customview.WaveBallProgress
import kotlinx.android.synthetic.main.activity_wave_progress.*


class WaveProgressActivity : AppCompatActivity() {

    var waveCircleView: WaveBallProgress? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wave_progress)
        initView()

    }

    private fun initView() {
        waveCircleView = progressBar


        seekBar.max = 100
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                waveCircleView?.startProgress(i,2000,0)
                tvProgress.text = "$i %"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
    }

}
