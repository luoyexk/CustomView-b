package com.example.testfunction.act

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.testfunction.R
import kotlinx.android.synthetic.main.layout_check_box_filter.*

class CardItemActivity : AppCompatActivity() {

    companion object{
        val list = mutableListOf<Context>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        list.add(this)
        setContentView(R.layout.activity_card_item)

        iv1.selected(false)

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            iv1.selected(isChecked)
        }

        circleCheckBox.setListener {isChecked ->
            iv1.selected(isChecked)
        }
    }

}
