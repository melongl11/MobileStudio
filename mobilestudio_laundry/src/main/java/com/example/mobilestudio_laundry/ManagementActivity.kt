package com.example.mobilestudio_laundry

import android.Manifest
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_management.*
import kotlinx.android.synthetic.main.activity_modify.*

class ManagementActivity : AppCompatActivity() {

    private var mStorageRef : StorageReference = FirebaseStorage.getInstance().getReference()

    fun downimg(){
        var downRef : StorageReference = mStorageRef.child("laundry").child("image").child("food.jpg")
        downRef.downloadUrl.addOnSuccessListener{
            Glide.with(this).using(FirebaseImageLoader()).load(downRef).into(imageView3)
            Toast.makeText(applicationContext,"다운성공.", Toast.LENGTH_LONG).show()
        }.addOnFailureListener{
            Toast.makeText(applicationContext,"다운실패.", Toast.LENGTH_LONG).show()
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
        bt_modify.setOnClickListener {
            val intent = Intent(this, ModifyActivity::class.java)
            startActivity(intent)
        }

        downimg()
    }

}
