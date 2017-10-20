package com.example.melon.mobilestudio

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_laundry_info.*

class LaundryInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laundry_info)
        val i = intent
        li_tv_laundryName.setText(i.getStringExtra("laundryName"))
        li_tv_laundryInfo.setText(i.getStringExtra("laundryInfo"))
        bt_select.setOnClickListener {
            val intent = Intent(this, OrderActivity::class.java)
            intent.putExtra("laundryName", i.getStringExtra("laundryName"))
            intent.putExtra("laundryID", i.getStringExtra("laundryID"))
            intent.putExtra("userAddress", i.getStringExtra("userAddress"))
            startActivity(intent);
            arrayListforActivity.get(0).finish()
            finish()
        }
        bt_close.setOnClickListener {
            this.finish()
        }
    }
}
