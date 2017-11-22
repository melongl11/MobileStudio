package com.example.mobilestudio_laundry

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

val arrayListforActivity = ArrayList<Activity>()
class MainActivity : AppCompatActivity() {

    private var backPressedHandler = BackPressHandler(this)
    private var mAuth : FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var userID:String = ""
    private var user:FirebaseUser? = null
    private lateinit var drawerToggle:ActionBarDrawerToggle

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, LaundryService::class.java)
        serviceIntent.putExtra("userID",userID)
        startService(serviceIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            user = firebaseAuth.currentUser
            if (user != null) {
                userID = user!!.uid
            } else {
            }
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.Drawerlayout)
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.app_name,R.string.app_name)
        drawerLayout.addDrawerListener(drawerToggle)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        bt_orderedlist.setOnClickListener{
            val intent = Intent(this,OrderedActivity::class.java)
            arrayListforActivity.add(this)
            startActivity(intent)
        }

        bt_acceptedlist.setOnClickListener{
            val intent = Intent(this,AcceptedList::class.java)
            arrayListforActivity.add(this)
            startActivity(intent)
        }
        bt_management.setOnClickListener{
            val intent = Intent(this,ManagementActivity::class.java)
            arrayListforActivity.add(this)
            startActivity(intent)
        }
        bt_signout.setOnClickListener{
            arrayListforActivity.add(this)
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

    override fun onBackPressed() {
        backPressedHandler.onBackPressedEnd()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (drawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

}
