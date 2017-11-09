package com.example.melon.mobilestudio

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_user_modify_information.*

class UserModifyInformationActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var userID:String = ""
    private var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference()

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)
        Handler().postDelayed({
            val dbRef = FirebaseDatabase.getInstance().getReference("/users/${userID}/info/")
            dbRef.addValueEventListener(postListener)
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
        setContentView(R.layout.activity_user_modify_information)

        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                userID = user.uid
            } else {
            }
        }

        bt_modify_user_information.setOnClickListener {
            val newName = et_user_name.text.toString()
            val newPhoneNumber = et_phone_number.text.toString()
            val newInformation = Information(newName, newPhoneNumber).toMap()
            val childUpdate = HashMap<String, Any>()
            childUpdate.put("/users/${userID}/info/name/", newInformation)
            mDatabase.updateChildren(childUpdate)
        }
    }

    private val postListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {

        }

        override fun onDataChange(datasnapshot: DataSnapshot) {
            for (snapshot in datasnapshot.children) {
                val information = snapshot.getValue(Information::class.java)
                et_user_name.setText(information!!.name)
                et_phone_number.setText(information.phoneNumber)
            }
        }
    }
}
class Information() {
    var name:String = ""
    var phoneNumber : String = ""
    constructor(name : String, phoneNumber : String) : this(){
        this.name = name
        this.phoneNumber = phoneNumber
    }
    fun toMap() : Map<String, Any> {
        val result : HashMap<String, Any> = HashMap<String, Any>()
        result.put("name", name)
        result.put("phoneNumber", phoneNumber)
        return result
    }
}
