package com.example.melon.mobilestudio

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.CursorAdapter
import kotlinx.android.synthetic.main.activity_order2.*

class OrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order2)

        finalorder.setOnClickListener {
            val intent = Intent(this, OrderFinishActivity::class.java)
            intent.putExtra("address",et_useraddress.text.toString())
            startActivity(intent)
        }

    }
}
