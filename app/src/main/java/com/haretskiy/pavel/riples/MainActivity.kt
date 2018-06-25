package com.haretskiy.pavel.riples

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bt_ripples.setOnClickListener{
            startActivity(Intent(this, RipplesActivity::class.java))
        }

        bt_clocks.setOnClickListener{
            startActivity(Intent(this, ClocksActivity::class.java))
        }
    }
}

//Jenkins hooks test2
