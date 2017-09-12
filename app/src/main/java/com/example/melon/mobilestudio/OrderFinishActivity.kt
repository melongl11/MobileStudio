package com.example.melon.mobilestudio

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_order_finish.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class OrderFinishActivity : AppCompatActivity() {

    private var mDatabase:DatabaseReference = FirebaseDatabase.getInstance().getReference()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_finish)
        var dateFormat = SimpleDateFormat("yy-MM-dd")
        var today = dateFormat.format(Date())
        tv_date.setText(today)
        val i = intent
        var address = i.getStringExtra("address")
        confirm.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            newOrder(today, address, 0)
            startActivity(intent)
        }
    }

    fun newOrder(date:String, laundry:String, state:Int) {
        var key = mDatabase.child("users").child("ld_list").push().getKey()
        var order = Order(date, laundry, state)
        var orderValue = order.toMap()

        var childUpdate = HashMap<String, Any>()

        childUpdate.put("/users/" + date, orderValue)

        mDatabase.updateChildren(childUpdate)
    }
}
