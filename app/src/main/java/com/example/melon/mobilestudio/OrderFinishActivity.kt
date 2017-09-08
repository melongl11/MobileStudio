package com.example.melon.mobilestudio

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_order_finish.*

class OrderFinishActivity : AppCompatActivity() {

    private var mDatabase:DatabaseReference = FirebaseDatabase.getInstance().getReference()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_finish)
        val i = intent
        var address = i.getStringExtra("address")
        confirm.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            mDatabase.child("users").child("ld_list").setValue(address)
            startActivity(intent)
        }
    }
}
