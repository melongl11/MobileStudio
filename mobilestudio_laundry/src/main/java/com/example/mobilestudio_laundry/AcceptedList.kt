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
        adapter = AcceptedListAdt(datas, this)
        lv_accepted.setAdapter(adapter)
        Handler().postDelayed({
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
        /*override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
            val ordered = p0!!.getValue(Accepted::class.java)
            if (ordered!!.state != 0) datas.add(ordered)
            adapter.notifyDataSetChanged()
        }

        override fun onChildRemoved(p0: DataSnapshot?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }*/

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
