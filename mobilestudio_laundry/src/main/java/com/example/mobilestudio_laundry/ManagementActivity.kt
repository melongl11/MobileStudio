package com.example.mobilestudio_laundry

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.annotation.IntegerRes
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ListView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_management.*
import kotlinx.android.synthetic.main.activity_management.view.*
import kotlinx.android.synthetic.main.activity_modify.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ManagementActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraMoveListener {

    var marker: Marker?= null
    var center: LatLng? = null

    private var filePath : Uri? = null
    private var fileExtras : Bundle? = null
    private var bitmap : Bitmap? = null

    private var init = 0
    private var mMap: GoogleMap? = null
    private var enableCurrentLocation = true
    private var lm: LocationManager? = null
    private var initLocation: Location? = null

    var storage = FirebaseStorage.getInstance()

    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var userID:String = ""

    private var laundryList = ArrayList<Laundry>()
    lateinit private var laundryListAdapter : LaundryListAdt

    private var visitTimeList = ArrayList<Visittime>()
    lateinit private var visitTimeAdapter : VisittimeListAdt

    private var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    var laundry : String = ""
    var fare : Int = 0
    var hour : Int = 0
    var minute : Int = 0

    override fun onCameraMove() {
        if(enableCurrentLocation) {
            enableCurrentLocation = false
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)

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
                down()
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

        bt_setPicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.setType("image/*")
            startActivityForResult(Intent.createChooser(intent,"이미지를 선택하세요."),0)
        }

        lv_visittime.setOnTouchListener (object : View.OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                scrollView.requestDisallowInterceptTouchEvent(true)
                return false
            }
        })

        lv_laund.setOnTouchListener (object : View.OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                scrollView.requestDisallowInterceptTouchEvent(true)
                return false
            }
        })


        try {
            val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        } catch (ex: SecurityException) {

        }
        bt_setLocation.setOnClickListener {
            val saveFormat = SimpleDateFormat("yy-MM-dd-hh-mm-ss")
            val saveTime = saveFormat.format(Date())
            val childUpdate = HashMap<String, Any>()
            val currentLocation = HashMap<String, Any>()
            center = mMap!!.projection.visibleRegion.latLngBounds.center

            currentLocation.put("latitude",center!!.latitude)
            currentLocation.put("longitude",center!!.longitude)
            currentLocation.put("address", et_laundryAddress.text.toString())
            currentLocation.put("name",et_laundryName.text.toString())
            currentLocation.put("laundryID", userID)


            childUpdate.put("/laundry_list/"+userID , currentLocation)
            mDatabase.updateChildren(childUpdate)

            AlertDialog.Builder(this)
                    .setMessage("세탁소 정보를 등록하였습니다.")
                    .setNegativeButton("닫기", DialogInterface.OnClickListener{ dialog, whichButton ->})
                    .show()
        }
        iv_location_button.setOnClickListener {
            if (enableCurrentLocation) {
                enableCurrentLocation = false
            }
            else {
                enableCurrentLocation = true
                try {
                    initLocation = lm!!.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                    if (initLocation != null) {
                        val init = LatLng(initLocation!!.latitude, initLocation!!.longitude)
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(init))
                    }

                } catch (e:SecurityException) {

                }
            }
        }

    }

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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val init = LatLng(37.59788, 126.86443)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(init, 10.toFloat()))
        mMap!!.setOnCameraMoveListener(this)
        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            lm!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0.00001.toFloat(), mLocationListener)
            lm!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0.00001.toFloat(), mLocationListener)
        } catch (e:SecurityException) {

        }
    }

    private val mLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            mMap!!.moveCamera(CameraUpdateFactory.newLatLng(LatLng(location.getLatitude(), location.getLongitude())))
            Log.d("test", "onLocationChanged, location:" + location)
        }

        override fun onProviderDisabled(provider: String) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider)
        }

        override fun onProviderEnabled(provider: String) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:$provider, status:$status ,Bundle:$extras")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0){
            cropImage(data!!.data)
        }
        if(requestCode == 1){
            fileExtras = data!!.extras
            if(fileExtras != null){
                bitmap = fileExtras!!.getParcelable("data")
                iv_Picture.setImageBitmap(bitmap)
                uploadFile()
            }
            iv_Picture.setImageBitmap(bitmap)
            uploadFile()
        }
    }

    fun uploadFile(){
        if (fileExtras != null){
            val progressDialog = ProgressDialog(this)
            progressDialog.show()


            var filename = "image.jpg"
            var storageRef = storage.getReference().child("image/$userID").child(filename)
            var baos : ByteArrayOutputStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG,100,baos)
            var data = baos.toByteArray()

            storageRef.putBytes(data)
                    .addOnSuccessListener { task ->
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext,"업로드 완료!",Toast.LENGTH_SHORT).show()
                        down()
                    }
                    .addOnFailureListener { task ->
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext,"업로드 실패!",Toast.LENGTH_LONG).show()
                    }
                    .addOnProgressListener { taskSnapshot: UploadTask.TaskSnapshot? ->
                        var progress = (100 * taskSnapshot!!.bytesTransferred / taskSnapshot.totalByteCount)
                        progressDialog.setMessage("업로드 중 " +(progress.toInt()) + "% ..." )
                    }

        } else {
            Toast.makeText(applicationContext, "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show()
        }
    }

    fun down(){
        var filename = "image.jpg"
        var storageRef = storage.getReference().child("image/$userID").child(filename)

        Glide.with(this)
                .using(FirebaseImageLoader())
                .load(storageRef)
                .placeholder(R.mipmap.ic_launcher_round)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .bitmapTransform(CropCircleTransformation(CustomBitmapPool()))
                .into(iv_Picture)
    }

    fun cropImage(uri : Uri) {
        var intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(uri,"image/*")
        intent.putExtra("crop","true")
        intent.putExtra("aspectX",1)
        intent.putExtra("aspectY",1)
        intent.putExtra("outputX",256)
        intent.putExtra("outputY",256)
        intent.putExtra("scale",true)
        intent.putExtra("return-data",true)
        startActivityForResult(intent,1)
    }

}
