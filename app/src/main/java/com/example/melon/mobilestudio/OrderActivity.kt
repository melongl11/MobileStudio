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
import android.widget.ArrayAdapter




class OrderActivity : AppCompatActivity() {

    var address:String = " "
    var require:String = " "
    var saveFormat = SimpleDateFormat("yyMMddhhmmss")
    var visitHour:Int = 0
    var visitMinute:Int = 0
    var i:Intent? = null
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var userID:String = ""
    private var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    private var spinnerList = ArrayList<String>()

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)

        Handler().postDelayed({
            val dbRef = FirebaseDatabase.getInstance().getReference("/laundry/${i!!.getStringExtra("laundryID")}/info/time/")
            dbRef.addListenerForSingleValueEvent(postListener)
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

        tv_userAddressCheck.setText(address)
        val dateFormat = SimpleDateFormat("yy-MM-dd")
        val today = dateFormat.format(Date())
        finalorder.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("주문하시겠습니까?")
            builder.setPositiveButton("예"){dialog, whichButton ->
                val intent = Intent(this,OrderFinishActivity::class.java)
                require = et_require.text.toString()
                newOrder(today, require, 0)
                intent.putExtra("require", require)
                intent.putExtra("laundryInfo", i!!.getStringExtra("laundryInfo"))
                intent.putExtra("date",today)
                startActivity(intent)
                finish()
            }
            builder.setNegativeButton("아니오"){dialog,whichButton ->
                dialog.cancel()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

    }
    private fun newOrder(date:String, laundry:String, state:Int) {
        val saveTime = saveFormat.format(Date())
        val order = Order(date, laundry, state, saveTime, i!!.getStringExtra("laundryID"))
        val orderValue = order.toMap()

        val childUpdate = HashMap<String, Any>()

        childUpdate.put("/users/$userID/orders/$saveTime", orderValue)
        mDatabase.updateChildren(childUpdate)

        val result = HashMap<String, Any>()
        result.put("date",date)
        result.put("name",laundry)
        result.put("address",address)
        result.put("require", require)
        result.put("state", state)
        result.put("key",saveTime)
        result.put("userID", userID)
        result.put("hour", visitHour)
        result.put("minute", visitMinute)
        childUpdate.put("/laundry/${i!!.getStringExtra("laundryID")}/orders/$saveTime", result)
        mDatabase.updateChildren(childUpdate)

    }

    private val postListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {
        }
        override fun onDataChange(datasnapshot: DataSnapshot) {
            for(snapshot in datasnapshot.children) {
                val visitTime = snapshot.getValue(VisitTime::class.java)
                visitHour = visitTime!!.hourOfDay
                visitMinute = visitTime.minute
                val timeFormat = SimpleDateFormat("HH : mm")
                val fromTime = timeFormat.format(Date(2000,1,1,visitHour, visitMinute,0))
                val toTime = timeFormat.format(Date(2000,1,1,visitHour+1, visitMinute,0))
                val time =
                spinnerList.add("${fromTime} ~  ${toTime}")
            }
            val spinnerAdapter = ArrayAdapter(this@OrderActivity, android.R.layout.simple_spinner_item, spinnerList)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spin_visit_time.adapter = spinnerAdapter

        }
    }
}
