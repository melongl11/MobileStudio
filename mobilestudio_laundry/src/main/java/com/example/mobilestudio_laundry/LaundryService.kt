package com.example.mobilestudio_laundry

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class LaundryService : Service() {
    var Notifi_M: NotificationManager? = null
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var userID:String = ""

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    override fun onCreate() {
        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                userID = user.uid
            } else {
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Notifi_M = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mAuth!!.addAuthStateListener(mAuthListener!!)
        Handler().postDelayed({
            val dbRef = FirebaseDatabase.getInstance().getReference("laundry/$userID/orders")
            dbRef.addChildEventListener(postListener)
        },1000)
        return START_STICKY
    }
    private val postListener = object : ChildEventListener {
        override fun onChildChanged(datasnapshot: DataSnapshot?, p1: String?) {

        }
        override fun onCancelled(p0: DatabaseError?) {
        }
        override fun onChildAdded(datasnapshot: DataSnapshot?, p1: String?) {
            val intent = Intent(this@LaundryService, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this@LaundryService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val ordered = datasnapshot!!.getValue(Ordered::class.java)
            if (ordered!!.state == 0) {
                val notifiText = "새로운 주문이 들어왔습니다."
                val Notifi = Notification.Builder(applicationContext)
                        .setContentTitle("세탁왕")
                        .setContentText(notifiText)
                        .setSmallIcon(R.drawable.user_icon)
                        .setTicker("알림")
                        .setContentIntent(pendingIntent)
                        .build()
                Notifi.defaults = Notification.DEFAULT_SOUND
                Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE
                Notifi.flags = Notification.FLAG_AUTO_CANCEL

                Notifi_M!!.notify(777, Notifi)
            }
        }
        override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
        }
        override fun onChildRemoved(p0: DataSnapshot?) {
        }

    }
}
