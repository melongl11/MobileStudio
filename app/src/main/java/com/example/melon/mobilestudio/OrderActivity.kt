package com.example.melon.mobilestudio

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.CursorAdapter

class OrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order2)

        var timeList = arrayOf("09:00 ~ 10:00", "17:00 ~ 18:00")

    }
}
