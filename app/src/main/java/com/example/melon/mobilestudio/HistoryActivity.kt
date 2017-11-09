package com.example.melon.mobilestudio

import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.renderscript.Sampler
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_history.*
import kotlin.collections.ArrayList

class HistoryActivity : AppCompatActivity() {
    private var datas = ArrayList<Order>()
    lateinit var adapter : com.example.melon.mobilestudio.ListAdapter

    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var userID:String = ""

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)
        Handler().postDelayed({
            val dbRef = FirebaseDatabase.getInstance().getReference("/users/$userID/orders")
            dbRef.addValueEventListener(postListener)
            adapter = com.example.melon.mobilestudio.ListAdapter(datas, this,userID)
            lv_history.setAdapter(adapter)
        },500)
    }
    override fun onStop() {
        super.onStop()
        if(mAuthListener != null) {
            mAuth!!.removeAuthStateListener(mAuthListener!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                userID = user.uid
            } else {
            }
        }

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
