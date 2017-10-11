package com.example.melon.mobilestudio

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import com.google.firebase.database.*

class MyService : Service() {
    var Notifi_M:NotificationManager? = null


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Notifi_M = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val dbRef = FirebaseDatabase.getInstance().getReference("users")
        dbRef.addChildEventListener(postListener)
        return START_STICKY
    }

    private val postListener = object : ChildEventListener {
        override fun onChildChanged(datasnapshot: DataSnapshot?, p1: String?) {
            val intent = Intent(this@MyService, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this@MyService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            var Notifi = Notification.Builder(applicationContext)
                    .setContentTitle("Title!")
                    .setContentText("Text")
                    .setSmallIcon(R.drawable.main_icon)
                    .setTicker("알림")
                    .setContentIntent(pendingIntent)
                    .build()
            Notifi.defaults = Notification.DEFAULT_SOUND
            Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE
            Notifi.flags = Notification.FLAG_AUTO_CANCEL

            Notifi_M!!.notify(777, Notifi)

        }

        override fun onCancelled(p0: DatabaseError?) {
        }
        override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
        }
        override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
        }
        override fun onChildRemoved(p0: DataSnapshot?) {
        }

    }
}
