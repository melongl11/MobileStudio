package com.example.melon.mobilestudio

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.renderscript.Sampler
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.activity_order2.*
import kotlinx.android.synthetic.main.activity_order_finish.*
import java.text.SimpleDateFormat
import java.util.*
import android.R.array
import android.os.AsyncTask
import android.util.Log
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.FirebaseStorage
import jp.wasabeef.glide.transformations.CropCircleTransformation


class OrderActivity : AppCompatActivity() {

    var address:String = " "
    var require:String = " "
    private var saveFormat = SimpleDateFormat("yyMMddhhmmss")
    var visitHour:Int = 0
    var visitMinute:Int = 0
    var storage = FirebaseStorage.getInstance()
    var laundid = ""
    var payment:String = ""
    var laundryPhone : String = ""
    private var i:Intent? = null
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var userID:String = ""
    private var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var spinnerList = ArrayList<String>()

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)

        Handler().postDelayed({
            val dbRef = FirebaseDatabase.getInstance().getReference("/laundry/${i!!.getStringExtra("laundryID")}/info/time/")
            dbRef.addListenerForSingleValueEvent(postListener)

            val dbRefPhone = FirebaseDatabase.getInstance().getReference("/laundry_list/")
            dbRefPhone.addListenerForSingleValueEvent(postListener1)

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
        setContentView(R.layout.activity_order2)
        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                userID = user.uid
            } else {
            }
        }
        i = intent
        address = i!!.getStringExtra("userAddress")

       down()

        tv_laundryN2.text = i!!.getStringExtra("laundryInfo")

        tv_userAddressCheck.text = address
        val dateFormat = SimpleDateFormat("yy-MM-dd")
        val today = dateFormat.format(Date())
        finalorder.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("주문하시겠습니까?")
            builder.setPositiveButton("예"){dialog, whichButton ->
                val intent = Intent(this,OrderFinishActivity::class.java)
                var id = rg.checkedRadioButtonId
                var rb : RadioButton = findViewById(id)
                payment = rb.text.toString()
                require = et_require.text.toString()
                newOrder(today, i!!.getStringExtra("laundryID"), 0)
                intent.putExtra("require", require)
                intent.putExtra("laundryInfo", i!!.getStringExtra("laundryInfo"))
                intent.putExtra("date",today)
                startActivity(intent)
                finish()
            }
            builder.setNegativeButton("아니오"){dialog, whichButton ->
                dialog.cancel()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

    }
    private fun newOrder(date:String, laundry:String, state:Int) {
        val saveTime = saveFormat.format(Date())
        var spinn = spin_visit_time.selectedItem.toString()
        var hour = spinn.substring(0, 2).trim().toInt()
        var minute = spinn.substring(4, 7).trim().toInt()
        val order = Order(date, i!!.getStringExtra("laundryInfo"), state, saveTime, i!!.getStringExtra("laundryID"),require,hour,minute, laundryPhone)
        val orderValue = order.toMap()

        val childUpdate = HashMap<String, Any>()

        childUpdate.put("/users/$userID/orders/$saveTime", orderValue)
        mDatabase.updateChildren(childUpdate)

        val orderToLaundry = OrderToLaundry(date, i!!.getStringExtra("userName"), address, require, state, saveTime, userID, hour, minute, i!!.getStringExtra("userPhoneNumber"),payment)
        val result = orderToLaundry.toMap()
        childUpdate.put("/laundry/$laundry/orders/$saveTime", result)
        mDatabase.updateChildren(childUpdate)
    }

    private val postListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {
        }
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for(snapshot in dataSnapshot.children) {
                val visitTime = snapshot.getValue(VisitTime::class.java)
                visitHour = visitTime!!.hourOfDay
                visitMinute = visitTime.minute
                val timeFormat = SimpleDateFormat("HH : mm")
                val fromTime = timeFormat.format(Date(2000,1,1,visitHour, visitMinute,0))
                val toTime = timeFormat.format(Date(2000,1,1,visitHour+1, visitMinute,0))
                spinnerList.add("${fromTime} ~  ${toTime}")
            }
            val spinnerAdapter = ArrayAdapter(this@OrderActivity, android.R.layout.simple_spinner_item, spinnerList)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spin_visit_time.adapter = spinnerAdapter
        }
    }

    private val postListener1 = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for(snapshot in dataSnapshot.children) {
                val laundryInfo = snapshot.getValue(LaundryInfo::class.java)
                if (laundryInfo!!.laundryID == i!!.getStringExtra("laundryID")) {
                    laundryPhone = laundryInfo.laundryNum
                }
            }
        }
    }


    fun down(){
        var filename = "image.jpg"
        var laundID = i!!.getStringExtra("laundryID")
        var storageRef = storage.getReference().child("image/$laundID").child(filename)

        Glide.with(this)
                .using(FirebaseImageLoader())
                .load(storageRef)
                .placeholder(R.drawable.loading)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .bitmapTransform(CropCircleTransformation(CustomBitmapPool()))
                .into(iv_laundryPic)
    }
}
