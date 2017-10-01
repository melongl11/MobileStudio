package com.example.melon.mobilestudio

import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_history.*
import kotlin.collections.ArrayList

class HistoryActivity : AppCompatActivity() {
    private var datas = ArrayList<Order>()
    lateinit var adapter : com.example.melon.mobilestudio.ListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)


        val dbRef = FirebaseDatabase.getInstance().getReference("users")
        dbRef.addListenerForSingleValueEvent(postListener)

        adapter =  com.example.melon.mobilestudio.ListAdapter(datas, this)
        lv_history.setAdapter(adapter)

    }
    private val postListener = object : ValueEventListener {
        override fun onDataChange(datasnapshot: DataSnapshot) {
            datas.clear()
            for(snapshot in datasnapshot.getChildren()) {
                val order = snapshot.getValue(Order::class.java)
                datas.add(order!!)
            }
            adapter.notifyDataSetChanged()
        }

        override fun onCancelled(p0: DatabaseError?) {

        }
    }
}
