package com.example.melon.mobilestudio

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

/**
 * Created by melon on 2017-11-09.
 */
class DBListenTimer(val mMap: GoogleMap) : TimerTask() {
    private var datas = ArrayList<LaundryLocation>()
    override fun run() {
        val dbRef = FirebaseDatabase.getInstance().getReference("laundry_list")
        dbRef.addListenerForSingleValueEvent(postListener)
    }
    private val postListener = object : ValueEventListener {
        override fun onDataChange(datasnapshot: DataSnapshot) {
            datas.clear()
            mMap.clear()
            val userMarkerOption = MarkerOptions()
            userMarkerOption.position(LatLng(userLatitude, userLongitude))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker))
            mMap.addMarker(userMarkerOption)
            for(snapshot in datasnapshot.getChildren()) {
                val laundryLocation = snapshot.getValue(LaundryLocation::class.java)
                datas.add(laundryLocation!!)
            }
            for (data in datas) {
                val markerOption = MarkerOptions()
                val location = LatLng(data.latitude, data.longitude)
                if(mMap.projection.visibleRegion.latLngBounds.contains(location)) {
                    markerOption.position(location)
                    markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    markerOption.title(data.laundryID)
                    markerOption.snippet(data.name + "\n" + data.address + "\n")
                    mMap.addMarker(markerOption)
                }
            }
        }
        override fun onCancelled(p0: DatabaseError?) {

        }
    }
}