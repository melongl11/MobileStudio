package com.example.melon.mobilestudio

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.view.Menu
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import android.support.annotation.NonNull
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.widget.*
import android.widget.ArrayAdapter
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_history.*


val arrayListforActivity = ArrayList<Activity>()
var userLatitude = 0.0
var userLongitude = 0.0
class MainActivity : AppCompatActivity() {
    private var backPressedHandler = BackPressHandler(this)
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var userID:String = ""
    private lateinit var drawerToggle:ActionBarDrawerToggle
    private var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    private var user:FirebaseUser? = null
    private var address:String = ""

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)
        val serviceIntent = Intent(this, MyService::class.java)
        serviceIntent.putExtra("userID",userID)
        startService(serviceIntent)

        Handler().postDelayed({
            val dbRef = FirebaseDatabase.getInstance().getReference("/users/$userID/info/name")
            dbRef.addValueEventListener(infoListener)
            val dbRefForAddress  = FirebaseDatabase.getInstance().getReference("/users/$userID/info/address")
            dbRefForAddress.addValueEventListener(addressListener)
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
        setContentView(R.layout.activity_main)

        val drawerLayout:DrawerLayout = findViewById(R.id.drawerlayout)
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.app_name,R.string.app_name)
        drawerLayout.addDrawerListener(drawerToggle)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            user = firebaseAuth.currentUser
            if (user != null) {
                userID = user!!.uid
            } else {
            }
        }


        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),100)
        } else {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),100)
        }
        iv_order.setOnClickListener {
            if (address == "") {
                Toast.makeText(this@MainActivity, "주소를 등록해 주세요", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, MapsActivity::class.java)
                arrayListforActivity.add(this)
                startActivity(intent)
            }
        }
        iv_history.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            arrayListforActivity.add(this)
            startActivity(intent)
        }
        btn_logout.setOnClickListener{
            mAuth!!.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            arrayListforActivity.add(this)
            startActivity(intent)
        }
        btn_modify_address.setOnClickListener {
            val intent = Intent(this, UserSaveAddressActivity::class.java)
            arrayListforActivity.add(this)
            startActivity(intent)
        }
        btn_modify_info.setOnClickListener {
            val intent = Intent(this, UserModifyInformationActivity::class.java)
            arrayListforActivity.add(this)
            startActivity(intent)
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

    private val infoListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {
        }
        override fun onDataChange(datasnapshot: DataSnapshot) {
            if (!datasnapshot.hasChildren()) {
                for (userInfo in user!!.providerData) {
                    val name = userInfo.displayName.toString()
                    val phoneNumber = userInfo.phoneNumber.toString()
                    val userInformation = HashMap<String, Any>()
                    userInformation.put("name", name)
                    userInformation.put("phoneNumber", phoneNumber)
                    val childUpdate = HashMap<String, Any>()
                    childUpdate.put("/users/${user!!.uid}/info/name/", userInformation)
                    mDatabase.updateChildren(childUpdate)
                }
            }
            val information = datasnapshot.getValue(Information::class.java)
            if(information!!.phoneNumber == "" || information.phoneNumber =="null") {
                Toast.makeText(this@MainActivity, "휴대전화를 등록해 주세요.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@MainActivity, UserModifyInformationActivity::class.java)
                arrayListforActivity.add(this@MainActivity)
                startActivity(intent)
            }
        }
    }
    private val addressListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {
        }
        override fun onDataChange(datasnapshot: DataSnapshot) {
            if (!datasnapshot.hasChildren()) {
                address = ""
            } else {
                val userAddress = datasnapshot.getValue(UserAddress::class.java)
                address = userAddress!!.address
            }
        }
    }
}
