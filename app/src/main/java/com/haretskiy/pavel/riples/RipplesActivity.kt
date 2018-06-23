package com.haretskiy.pavel.riples

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_ripples.*

class RipplesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ripples)

        toggle_reverse.setOnCheckedChangeListener { _, checked ->
            ripples.reverseAnimation = checked
        }

        toggle_turn_on.setOnCheckedChangeListener { _, checked ->
            ripples.isOn = checked
        }
    }
}
