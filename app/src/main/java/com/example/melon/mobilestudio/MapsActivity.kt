package com.example.melon.mobilestudio

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v4.app.FragmentActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import android.widget.ToggleButton

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_order.*
import android.Manifest.permission.WRITE_CALENDAR
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    override fun onMarkerClick(marker: Marker?): Boolean {
        tv_laundryInfo.setText(marker!!.snippet)
        tv_laundryName.setText(marker!!.title)

        return true
    }

    private var mMap: GoogleMap? = null
    private var datas = ArrayList<LaundryLocation>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

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

        bt_popup.setOnClickListener {
            val intent = Intent(this, LaundryInfo::class.java)
            startActivity(intent)
        }

} // end of onCreate

    private val mLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Handler().postDelayed({
                val sydney = LatLng(location.getLatitude(), location.getLongitude())
                mMap!!.clear()
                mMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15.toFloat()))
                val dbRef = FirebaseDatabase.getInstance().getReference("laundry_list")
                dbRef.addListenerForSingleValueEvent(postListener)
            },1000)

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
        val init = LatLng(37.59788, 126.86443)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(init, 10.toFloat()))
        mMap!!.setOnMarkerClickListener(this)

    }
    private val postListener = object : ValueEventListener {
        override fun onDataChange(datasnapshot: DataSnapshot) {
            datas.clear()
            for(snapshot in datasnapshot.getChildren()) {
                var laundryLocation = snapshot.getValue(LaundryLocation::class.java)
                datas.add(laundryLocation!!)
            }
            for (data in datas) {
                var markerOption = MarkerOptions()
                var location = LatLng(data!!.latitude, data!!.longitude)

                Log.d("test",data!!.latitude.toString() + data!!.longitude.toString())
                markerOption.position(location!!)
                markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                markerOption.title(data.name)
                markerOption.snippet(data.address)
                mMap!!.addMarker(markerOption)

            }
        }

        override fun onCancelled(p0: DatabaseError?) {

        }
    }
}


