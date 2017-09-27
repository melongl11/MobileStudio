package com.example.mobilestudio_laundry

import android.app.Activity
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
import android.provider.MediaStore
import android.support.v4.view.LayoutInflaterFactory
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
import kotlinx.android.synthetic.main.activity_modify.view.*
import java.net.URI


class ModifyActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraMoveListener {
    var marker: Marker?= null
    var center:LatLng? = null

    val REQ_CODE_SELECT_IMAGE = 1001

    override fun onCameraMove() {
        if (mMap!=null) {
            center = mMap!!.projection.visibleRegion.latLngBounds.center
            var markerOption = MarkerOptions()
            markerOption.position(center!!)
            markerOption.title("Current Location")
            mMap!!.clear()
            marker = mMap!!.addMarker(markerOption)
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent){
        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                var uri: Uri = data.data
                val mImageView : View = findViewById(R.id.iv_Picture)
                mImageView.iv_Picture.setImageURI(uri)
            }
        }
    }


    private var init = 0
    private var mMap: GoogleMap? = null
    private var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    var sydney :LatLng = LatLng(37.59788, 126.86443)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify)

        /* 사진 첨부 */

        bt_setPicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.setType("image/*")
            startActivityForResult(intent, REQ_CODE_SELECT_IMAGE)
        }



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
            childUpdate.put("/laundry_list/" + saveTime , currentLocation)
            mDatabase.updateChildren(childUpdate)

            AlertDialog.Builder(this)
                    .setMessage("세탁소 정보를 등록하였습니다.")
                    .setNegativeButton("닫기", DialogInterface.OnClickListener{ dialog, whichButton ->})
                    .show()

        }

    }

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
                    mMap!!.clear()
                    mMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
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
