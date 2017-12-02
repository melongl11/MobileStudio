package com.example.mobilestudio_laundry

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_accepted.*

class AcceptedList : AppCompatActivity() {

    private var datas = ArrayList<Accepted>()
    lateinit var adapter : AcceptedListAdt

    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var userID:String = ""

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)
        Handler().postDelayed({
            adapter = AcceptedListAdt(datas, this, userID)
            lv_accepted.setAdapter(adapter)
            val dbRef = FirebaseDatabase.getInstance().getReference("laundry/$userID/orders")
            dbRef.addValueEventListener(postListener)
        },1000)
    }
    override fun onStop() {
        super.onStop()
        if(mAuthListener != null) {
            mAuth!!.removeAuthStateListener(mAuthListener!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accepted)

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
        override fun onDataChange(datasnapshot: DataSnapshot?) {
            datas.clear()
            for(child in datasnapshot!!.children) {
                val ordered = child.getValue(Accepted::class.java)
                if (ordered!!.state != 0)
                    datas.add(ordered)
            }
            adapter.notifyDataSetChanged()
        }
        override fun onCancelled(p0: DatabaseError?) {

        }
    }
}
