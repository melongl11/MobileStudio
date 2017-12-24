package com.example.melon.mobilestudio

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.activity_user_save_address.*

class UserSaveAddressActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraMoveListener {
    private var mMap: GoogleMap? = null
    private var uiSettings: UiSettings? = null
    private var enableCurrentLocation = true
    private var initLocation:Location? = null
    private var lm:LocationManager? = null
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var userID:String = ""
    private var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference()

    private var userLatitude = 0.0
    private var userLongitude = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_save_address)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map6) as SupportMapFragment
        mapFragment.getMapAsync(this)


        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                userID = user.uid
            } else {
            }
        }

        modifyuser.setOnClickListener {
            val newName = nameee.text.toString()
            val newPhoneNumber = phoneee.text.toString()
            val newInformation = Information(newName, newPhoneNumber).toMap()
            val childUpdate = HashMap<String, Any>()
            childUpdate.put("/users/${userID}/info/name/", newInformation)
            mDatabase.updateChildren(childUpdate)
            Toast.makeText(this, "이름과 번호를 저장했습니다.", Toast.LENGTH_SHORT).show()
        }

        iv_saveAddress2.setOnClickListener {
            if(tv_userAddress2.text == "") {
                Toast.makeText(this, "주소를 확인해 주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val userAddress = "${tv_userAddress2.text.toString()} ${et_detailAddress2.text.toString()}"
                val data = HashMap<String, Any>()
                data.put("address", userAddress)
                data.put("latitude", userLatitude)
                data.put("longitude", userLongitude)

                val childUpdate = HashMap<String, Any>()

                childUpdate.put("/users/$userID/info/address", data)
                mDatabase.updateChildren(childUpdate)
                Toast.makeText(this, "주소를 저장했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        iv_checkAddress2.setOnClickListener {
            val geocoder: Geocoder = Geocoder(this)
            val center = mMap!!.projection.visibleRegion.latLngBounds.center
            val addressList = geocoder.getFromLocation(center.latitude, center.longitude, 1)
            userLatitude = center.latitude
            userLongitude = center.longitude
            tv_userAddress2.setText(addressList.get(0).getAddressLine(0).toString())
        }
        iv_location_button2.setOnClickListener {
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

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)
        Handler().postDelayed({
            val dbRef = FirebaseDatabase.getInstance().getReference("/users/${userID}/info/name")
            dbRef.addValueEventListener(postListener)
        },500)
    }

    override fun onStop() {
        super.onStop()
        if(mAuthListener != null) {
            mAuth!!.removeAuthStateListener(mAuthListener!!)
        }
    }
    override fun onCameraMove() {
        if(enableCurrentLocation) {
            enableCurrentLocation = false
        }

    }

    override fun onBackPressed() {
        finish()
    }

    private val mLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if(enableCurrentLocation) {
                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(LatLng(location.getLatitude(), location.getLongitude())))
            }
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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        uiSettings = mMap!!.getUiSettings()

        /*uiSettings!!.setMyLocationButtonEnabled(true)
        try {
            mMap!!.setMyLocationEnabled(true)
        } catch(e:SecurityException) {

        }*/
        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var init = LatLng(37.59788, 126.86443)

        try {
            initLocation = lm!!.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        } catch (e:SecurityException) {

        }
        if (initLocation != null) {
            init = LatLng(initLocation!!.latitude, initLocation!!.longitude)
        }
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(init, 15.toFloat()))
        mMap!!.setOnCameraMoveListener(this)
        try {
            lm!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0.00001.toFloat(), mLocationListener)
            lm!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0.00001.toFloat(), mLocationListener)
        } catch (e: SecurityException) {

        }

    }

    private val postListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {

        }

        override fun onDataChange(datasnapshot: DataSnapshot) {
            val information = datasnapshot.getValue(Information::class.java)
            nameee.setText(information!!.name)
            phoneee.setText(information.phoneNumber)
        }
    }

}
