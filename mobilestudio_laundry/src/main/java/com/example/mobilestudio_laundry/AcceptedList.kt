package com.example.mobilestudio_laundry

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_accepted.*

class AcceptedList : AppCompatActivity() {

    private var datas = ArrayList<Accepted>()
    lateinit var adapter : AcceptedListAdt
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accepted)

        var dbRef = FirebaseDatabase.getInstance().getReference("laundry/orders")
        dbRef.addListenerForSingleValueEvent(postListener)

        adapter = AcceptedListAdt(datas, this)
        lv_accepted.setAdapter(adapter)
    }
    private val postListener = object : ValueEventListener {
        override fun onDataChange(datasnapshot: DataSnapshot) {
            datas.clear()
            for(snapshot in datasnapshot.getChildren()) {
                var ordered = snapshot.getValue(Accepted::class.java)
                datas.add(ordered!!)
            }
            adapter.notifyDataSetChanged()
        }

        override fun onCancelled(p0: DatabaseError?) {

        }
    }
}
