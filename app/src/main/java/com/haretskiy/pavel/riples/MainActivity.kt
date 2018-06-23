package com.haretskiy.pavel.riples

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toggle_reverse.setOnCheckedChangeListener { _, checked ->
            ripples.reverseAnimation = checked
        }
    }
}
