package com.example.mobilestudio_laundry

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.annotation.IntegerRes
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.ListView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_management.*
import kotlinx.android.synthetic.main.activity_management.view.*
import kotlinx.android.synthetic.main.activity_modify.*
import java.util.ArrayList
import java.util.HashMap

class ManagementActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var userID:String = ""

    private var laundryList = ArrayList<Laundry>()
    lateinit private var laundryListAdapter : LaundryListAdt

    private var visitTimeList = ArrayList<Visittime>()
    lateinit private var visitTimeAdapter : VisittimeListAdt
/*
    private var mStorageRef : StorageReference = FirebaseStorage.getInstance().getReference()

    fun downimg(){
        var downRef : StorageReference = mStorageRef.child("laundry").child("image").child("real.jpg")
        downRef.downloadUrl.addOnSuccessListener{
            Glide.with(this).using(FirebaseImageLoader()).load(downRef).into(imageView3)
            Toast.makeText(applicationContext,"다운성공.", Toast.LENGTH_LONG).show()
        }.addOnFailureListener{
            Toast.makeText(applicationContext,"다운실패.", Toast.LENGTH_LONG).show()
        }
    }*/

    private var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    var laundry : String = ""
    var fare : Int = 0
    var hour : Int = 0
    var minute : Int = 0

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)

/*        val dbR = FirebaseDatabase.getInstance().getReference("/laundry/$userID/info/time")
        dbR.addValueEventListener(postListener)

        adapter = VisittimeListAdt(datas, this)
        lv_visittime.setAdapter(adapter)
           },1000)*/
/*        var adapter = LaundryListAdt(datas,this)
        var ivvv : ListView = findViewById(R.id.lv_laund)
        ivvv.lv_laund.setAdapter(adapter)*/
        laundryListAdapter = LaundryListAdt(laundryList,this)
        lv_laund.adapter = laundryListAdapter
        visitTimeAdapter = VisittimeListAdt(visitTimeList, this)
        lv_visittime.adapter = visitTimeAdapter
        Handler().postDelayed({
            val dbRefForLaund = FirebaseDatabase.getInstance().getReference("laundry/$userID/info/list")
            dbRefForLaund.addValueEventListener(laundryListener)
            val dbRefForVisitTime = FirebaseDatabase.getInstance().getReference("laundry/$userID/info/time")
            dbRefForVisitTime.addValueEventListener(visitTimeListener)


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
        setContentView(R.layout.activity_management)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }
        
        supportActionBar?.run {
            setTitle("가게관리")
        }



        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                userID = user.uid
            } else {
            }
        }

        tv_visithour.setOnClickListener{
            val dh  = DialogHandler()
            dh.show(supportFragmentManager,"time_picker")
        }

        bt_plusvisit.setOnClickListener{
            hour = tv_visithour.text.toString().toInt()
            minute = tv_visitminute.text.toString().toInt()
            newtime(hour,minute)
            tv_visithour.setText("")
            tv_visitminute.setText("")
            tv_visittime2.setText("")
        }

        bt_modify.setOnClickListener {
            val intent = Intent(this, ModifyActivity::class.java)
            startActivity(intent)
        }

        bt_pluslaund.setOnClickListener{
            laundry= et_pluslaund.text.toString()
            fare = Integer.parseInt(et_fare.text.toString())
            newlaundlist(laundry,fare)
        }

    }

   /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1111) {
            if (resultCode == Activity.RESULT_OK) {
                var intent = Intent(this, TimeSettings::class.java)
                hour = intent.getStringExtra("hour")
                minute = intent.getStringExtra("minute")
                tv_visittime1.setText("1111")
                tv_visittime2.setText("!!!!")
                Toast.makeText(this,"이거 되는거냐",Toast.LENGTH_LONG).show()
            }
        }
    }*/

    fun newlaundlist(laundry: String, fare: Int) {

        val childUpdate = HashMap<String, Any>()
        val result: HashMap<String, Any> = HashMap<String, Any>()
        result.put("laundry", laundry)
        result.put("fare", fare)
        childUpdate.put("/laundry/$userID/info/list/$laundry", result)
        mDatabase.updateChildren(childUpdate)
    }

    fun newtime(time: Int,time2 : Int) {
        val childUpdate = HashMap<String, Any>()
        val result: HashMap<String, Any> = HashMap<String, Any>()
        result.put("hourOfDay", time)
        result.put("minute",time2)
        val dbKey = time.toString() + time2.toString()
        childUpdate.put("/laundry/$userID/info/time/$dbKey", result)
        mDatabase.updateChildren(childUpdate)
    }

    private val laundryListener = object : ValueEventListener {
        override fun onDataChange(datasnapshot: DataSnapshot) {
            laundryList.clear()
            for(snapshot in datasnapshot.getChildren()) {
                val fare = snapshot.getValue(Laundry::class.java)
                laundryList.add(fare!!)
            }
            laundryListAdapter.notifyDataSetChanged()
        }
        override fun onCancelled(p0: DatabaseError?) {
        }
    }

    private val visitTimeListener = object : ValueEventListener {
        override fun onDataChange(datasnapshot: DataSnapshot) {
            visitTimeList.clear()
            for (snapshot in datasnapshot.getChildren()) {
                val visitTime = snapshot.getValue(Visittime::class.java)
                visitTimeList.add(visitTime!!)
            }
            visitTimeAdapter.notifyDataSetChanged()
        }

        override fun onCancelled(p0: DatabaseError?) {
        }
    }

}
