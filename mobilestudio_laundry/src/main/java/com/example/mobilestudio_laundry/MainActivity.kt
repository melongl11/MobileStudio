package com.example.mobilestudio_laundry

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var mAuth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()

        bt_orderedlist.setOnClickListener{
            val intent = Intent(this,OrderedActivity::class.java)
            startActivity(intent)
        }

        bt_acceptedlist.setOnClickListener{
            val intent = Intent(this,AcceptedList::class.java)
            startActivity(intent)
        }
        bt_management.setOnClickListener{
            val intent = Intent(this,ManagementActivity::class.java)
            startActivity(intent)
        }
        bt_signout.setOnClickListener{
            signout()
        }
    }

    private fun signout(){
        mAuth?.signOut()
        updateUI(null)
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user == null) {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
