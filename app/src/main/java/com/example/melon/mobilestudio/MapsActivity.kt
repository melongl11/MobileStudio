package com.example.melon.mobilestudio

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.FragmentActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import android.widget.ToggleButton

import kotlinx.android.synthetic.main.activity_order.*
import android.Manifest.permission.WRITE_CALENDAR
import android.location.*
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.android.gms.maps.MapFragment
import kotlinx.android.synthetic.main.activity_order2.*
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveListener{
    private var mMap: GoogleMap? = null
    private var uiSettings:UiSettings? = null
    private var name: String = ""
    private var info: String = ""
    private var laundryID: String = ""
    private var enableCurrentLocation = true
    private var initLocation:Location? = null
    private var lm:LocationManager? = null

    override fun onMarkerClick(marker: Marker?): Boolean {
        tv_laundryInfo.setText(marker!!.snippet)
        laundryID = marker.title
        info = marker.snippet

        return true
    }

    override fun onCameraMove() {
        if(enableCurrentLocation) {
            enableCurrentLocation = false
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)


        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        bt_popup.setOnClickListener {
            val intent = Intent(this, LaundryInfo::class.java)
            intent.putExtra("laundryInfo", info)
            intent.putExtra("laundryName", name)
            intent.putExtra("laundryID", laundryID)
            intent.putExtra("userAddress","${tv_userAddress.text.toString()} ${et_detailAddress.text.toString()}")

            startActivity(intent)
        }
        bt_checkAddress.setOnClickListener {
            val geocoder:Geocoder = Geocoder(this)
            val center = mMap!!.projection.visibleRegion.latLngBounds.center
            val addressList = geocoder.getFromLocation(center.latitude, center.longitude, 1)
            tv_userAddress.setText(addressList.get(0).getAddressLine(0).toString())

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

} // end of onCreate
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
        mMap!!.setOnMarkerClickListener(this)
        mMap!!.setOnCameraMoveListener(this)
        try {
            lm!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0.00001.toFloat(), mLocationListener)
            lm!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0.00001.toFloat(), mLocationListener)
        } catch (e: SecurityException) {

        }
        val dbListenTimer = DBListenTimer(mMap!!)

        val timer = Timer()
        timer.schedule(dbListenTimer,0,1000)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    class DBListenTimer(val mMap:GoogleMap) : TimerTask() {
        private var datas = ArrayList<LaundryLocation>()
        override fun run() {
            val dbRef = FirebaseDatabase.getInstance().getReference("laundry_list")
            dbRef.addListenerForSingleValueEvent(postListener)
        }
        private val postListener = object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {
                datas.clear()

                mMap.clear()
                for(snapshot in datasnapshot.getChildren()) {
                    val laundryLocation = snapshot.getValue(LaundryLocation::class.java)
                    datas.add(laundryLocation!!)
                }
                for (data in datas) {
                    val markerOption = MarkerOptions()
                    val location = LatLng(data.latitude, data.longitude)

                    Log.d("test",data.latitude.toString() + data.longitude.toString())
                    markerOption.position(location)
                    markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    markerOption.title(data.laundryID)
                    markerOption.snippet(data.name + "\n" + data.address + "\n")
                    mMap.addMarker(markerOption)

                }
            }

            override fun onCancelled(p0: DatabaseError?) {

            }
        }
    }

}


