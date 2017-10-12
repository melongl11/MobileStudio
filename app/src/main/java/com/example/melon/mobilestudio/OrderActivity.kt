package com.example.melon.mobilestudio

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.ArrayAdapter
import android.widget.CursorAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.activity_order2.*
import kotlinx.android.synthetic.main.activity_order_finish.*
import java.text.SimpleDateFormat
import java.util.*

class OrderActivity : AppCompatActivity() {

    var address:String = " "
    var require:String = " "
    var saveFormat = SimpleDateFormat("yyMMddhhmmss")
    var i:Intent? = null
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var userID:String = ""
    private var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference()

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)
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
        val dateFormat = SimpleDateFormat("yy-MM-dd")
        val today = dateFormat.format(Date())
        finalorder.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("주문하시겠습니까?")
            builder.setPositiveButton("예"){dialog, whichButton ->
                val intent = Intent(this,OrderFinishActivity::class.java)
                address= et_useraddress.text.toString()
                require = et_require.text.toString()
                newOrder(today, require, 0)
                intent.putExtra("require", require)
                intent.putExtra("laundryInfo", i!!.getStringExtra("laundryInfo"))
                intent.putExtra("date",today)
                startActivity(intent)
            }
            builder.setNegativeButton("아니오"){dialog,whichButton ->
                dialog.cancel()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

    }
    fun newOrder(date:String, laundry:String, state:Int) {
        val saveTime = saveFormat.format(Date())
        val order = Order(date, laundry, state, saveTime, i!!.getStringExtra("laundryID"))
        val orderValue = order.toMap()

        val childUpdate = HashMap<String, Any>()

        childUpdate.put("/users/" + userID + "/"+saveTime, orderValue)
        mDatabase.updateChildren(childUpdate)

        val result : HashMap<String, Any> = HashMap<String, Any>()
        result.put("date",date)
        result.put("name",laundry)
        result.put("address",address)
        result.put("require", require)
        result.put("state", state)
        result.put("key",saveTime)
        result.put("userID", userID)
        childUpdate.put("/laundry/"+ i!!.getStringExtra("laundryID") + "/orders/" + saveTime, result)
        mDatabase.updateChildren(childUpdate)

    }
}
