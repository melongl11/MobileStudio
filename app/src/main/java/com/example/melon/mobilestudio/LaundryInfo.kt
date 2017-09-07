package com.example.melon.mobilestudio

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_laundry_info.*

class LaundryInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laundry_info)

        bt_select.setOnClickListener {
            val intent = Intent(this, OrderActivity::class.java)

            startActivity(intent);
        }
        bt_close.setOnClickListener {
            this.finish()
        }
    }
}
