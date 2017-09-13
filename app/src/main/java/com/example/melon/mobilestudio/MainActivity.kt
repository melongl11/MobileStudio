package com.example.melon.mobilestudio

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.view.Menu
import android.view.View
import android.widget.ActionMenuView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        iv_order.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)

            startActivity(intent)
        }
        iv_history.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)

            startActivity(intent)
        }
    }
}
