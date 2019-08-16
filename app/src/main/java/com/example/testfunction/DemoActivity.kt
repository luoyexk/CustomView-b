package com.example.testfunction

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.ArrayAdapter
import com.example.testfunction.act.*
import kotlinx.android.synthetic.main.activity_demo.*

class DemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        permissionCheck()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        val title =
            mutableListOf(
                "Card view",
                "Step ViewPager",
                "WaveProgressView",
                "VerticalBar",
                "Edittext text watcher",
                "takephoto"
            )
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, title)

        listView.setOnItemClickListener { _, _, position, _ ->
            when (title[position]) {
                "Card view" -> {
                    startActivity(Intent(this, CardItemActivity::class.java))
                }
                "Step ViewPager" -> startActivity(Intent(this, StepViewPagerActivity::class.java))
                "WaveProgressView" -> startActivity(Intent(this, WaveProgressActivity::class.java))
                "VerticalBar" -> startActivity(Intent(this, VerticalProgressBarActivity::class.java))
                "Edittext text watcher" -> startActivity(Intent(this, InputTestActivity::class.java))
            }

        }
    }

    private fun permissionCheck() {
        val permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission1 != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED) {
            val permissions =
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, 1)
            }
        }

    }
}
