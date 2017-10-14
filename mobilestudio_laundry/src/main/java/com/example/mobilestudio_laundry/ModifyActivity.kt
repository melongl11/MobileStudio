package com.example.mobilestudio_laundry

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.Image
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.annotation.NonNull
import android.support.v4.view.LayoutInflaterFactory
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_modify.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_management.*
import kotlinx.android.synthetic.main.activity_modify.view.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URI
import javax.microedition.khronos.opengles.GL


class ModifyActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraMoveListener {
    var marker: Marker?= null
    var center:LatLng? = null
    var bitmap : Bitmap? = null
    var imageUri : Uri? = null

    private var mStorageRef : StorageReference = FirebaseStorage.getInstance().getReference()
    val REQ_CODE_SELECT_IMAGE = 0
    val REQ_CODE_IMAGE_CROP = 1

    override fun onCameraMove() {
        if (mMap!=null) {
            center = mMap!!.projection.visibleRegion.latLngBounds.center
        }
    }

    private var init = 0
    private var mMap: GoogleMap? = null
    private var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    var sydney :LatLng = LatLng(37.59788, 126.86443)

    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var userID:String = ""
    open var url : Uri? = null

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
        setContentView(R.layout.activity_modify)
        ////UserID불러오기///
        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                userID = user.uid
            } else {
            }
        }

        // 사진  ////////////////////////

        downimg()


        bt_setPicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.setType("image/*")
            startActivityForResult(intent, REQ_CODE_SELECT_IMAGE)
        }

        ///////////////////////////////



        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            Handler().postDelayed({
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0.001.toFloat(), mLocationListener)
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0.001.toFloat(), mLocationListener)
                val mapFragment = supportFragmentManager
                        .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this) }, 3000)

        } catch (ex: SecurityException) {

        }
        bt_setLocation.setOnClickListener {
            val saveFormat = SimpleDateFormat("yy-MM-dd-hh-mm-ss")
            val saveTime = saveFormat.format(Date())
            val childUpdate = HashMap<String, Any>()
            val currentLocation = HashMap<String, Any>()

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

    }

    // 사진 업로드  /////////////////////////////////////////////////////////////////

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQ_CODE_SELECT_IMAGE) {
                imageUri= data!!.data
                cropImage(data!!.data)
            }
            else if (requestCode == REQ_CODE_IMAGE_CROP) {
                val extras : Bundle = data!!.extras
                if(extras != null){
                    bitmap = extras.getParcelable("data")
                    uploadimg()
                    var f = File(imageUri!!.path)
                    if(f.exists()){
                        f.delete()
                    }
                }
            }
        }
    }
    fun cropImage(uri : Uri) {
        var intent: Intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(uri,"image/*")
        intent.putExtra("crop","true")
        intent.putExtra("aspectX",4)
        intent.putExtra("aspectY",1)
        intent.putExtra("outputX",256)
        intent.putExtra("outputY",64)
        intent.putExtra("scale",true)
        intent.putExtra("return-data",true)
        startActivityForResult(intent,REQ_CODE_IMAGE_CROP)
    }

    fun uploadimg(){
        var riversRef : StorageReference = mStorageRef.child("/laundry/").child(userID+".jpg")
        var baos : ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG,100,baos)
        var data = baos.toByteArray()

        var uploadTask : UploadTask = riversRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            Toast.makeText(applicationContext,"사진을 업로드했습니다.", Toast.LENGTH_LONG).show()
            downimg()
        }.addOnSuccessListener { taskSnapshot ->
            var DownloadURL = taskSnapshot.downloadUrl!!
        }
    }
    fun downimg(){
        var downRef : StorageReference = mStorageRef.child("/laundry/").child(userID+".jpg")
        downRef.downloadUrl.addOnSuccessListener{
            Glide.with(this).using(FirebaseImageLoader()).load(downRef).placeholder(R.drawable.ex).into(iv_Picture)
            Toast.makeText(applicationContext,"다운성공.", Toast.LENGTH_LONG).show()
        }.addOnFailureListener{
            Toast.makeText(applicationContext,"다운실패.", Toast.LENGTH_LONG).show()
            iv_Picture.setImageResource(R.drawable.ex)
        }
        if (url != null) {
            iv_Picture.setImageURI(url)
        }
    }


    ////////////////////////////////////////////////////////////////////////////////


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val init = LatLng(37.59788, 126.86443)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(init, 10.toFloat()))
        mMap!!.setOnCameraMoveListener(this)
    }

    private val mLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if (init == 0) {
                Handler().postDelayed({
                    sydney = LatLng(location.getLatitude(), location.getLongitude())
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15.toFloat()))
                    init = 1
                }, 1000)

                Log.d("test", "onLocationChanged, location:" + location)
            }
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


}
