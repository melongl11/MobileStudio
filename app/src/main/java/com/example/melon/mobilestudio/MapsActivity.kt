package com.example.melon.mobilestudio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log

import kotlinx.android.synthetic.main.activity_order.*
import android.location.*
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import jp.wasabeef.glide.transformations.CropCircleTransformation
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
    private var address = ""
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var userID:String = ""
    private var phoneNumber = ""
    private var ItemList = ArrayList<Item>()
    lateinit private var ItemListAdt : ItemListAdapter

    var storage = FirebaseStorage.getInstance()

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)
        ItemListAdt = ItemListAdapter(ItemList,this)


    }
    override fun onStop() {
        super.onStop()
        if(mAuthListener != null) {
            mAuth!!.removeAuthStateListener(mAuthListener!!)
        }
    }
    override fun onMarkerClick(marker: Marker?): Boolean {
        tv_laundryN.setText(marker!!.snippet)
        laundryID = marker.title
        info = marker.snippet
        down(laundryID)
        val dbRefItem = FirebaseDatabase.getInstance().getReference("laundry/$laundryID/info/list/")
        dbRefItem.addListenerForSingleValueEvent(laundryListener)
        lv_laundry.adapter = ItemListAdt

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

        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                userID = user.uid
            } else {
            }
        }

        iv_selectLaundry.setOnClickListener {
            if(address == "") {
                Toast.makeText(this, "주소를 등록해 주세요.",Toast.LENGTH_SHORT).show()
            }
            else if(laundryID == "") {
                Toast.makeText(this, "세탁소를 선택해 주세요", Toast.LENGTH_SHORT).show()
            }
            else {
                val intent = Intent(this, OrderActivity::class.java)
                intent.putExtra("laundryInfo", info)
                intent.putExtra("laundryID", laundryID)
                intent.putExtra("userAddress",address)
                intent.putExtra("userName", name)
                intent.putExtra("userPhoneNumber", phoneNumber)

                startActivity(intent)
                arrayListforActivity.add(this)
            }
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


        Handler().postDelayed({
            val dbRefForUserAddress = FirebaseDatabase.getInstance().getReference("users/$userID/info/address")
            dbRefForUserAddress.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }
                override fun onDataChange(p0: DataSnapshot) {
                    val userAddress = p0.getValue(UserAddress::class.java)
                    userLatitude = userAddress!!.latitude
                    userLongitude = userAddress.longitude
                    address = userAddress.address
                }
            })
            val dbRefForUserInfo = FirebaseDatabase.getInstance().getReference("users/$userID/info/name")
            dbRefForUserInfo.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }
                override fun onDataChange(p0: DataSnapshot) {
                    val userInfo = p0.getValue(Information::class.java)
                    name = userInfo!!.name
                    phoneNumber = userInfo.phoneNumber
                }
            })
        }, 500)

        val dbListenTimer = DBListenTimer(mMap!!)

        val makerTimer = Timer()
        makerTimer.schedule(dbListenTimer,0,500)
    }

    fun down(laundID : String){
        var filename = "image.jpg"
        var storageRef = storage.getReference().child("image/$laundID").child(filename)

        Glide.with(this)
                .using(FirebaseImageLoader())
                .load(storageRef)
                .placeholder(R.drawable.loading)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .bitmapTransform(CropCircleTransformation(CustomBitmapPool()))
                .into(iv_icon)
    }

    private val laundryListener = object : ValueEventListener {
        override fun onDataChange(datasnapshot: DataSnapshot) {
            ItemList.clear()
            for(snapshot in datasnapshot.getChildren()) {
                val fare = snapshot.getValue(Item::class.java)
                ItemList.add(fare!!)
            }
            ItemListAdt.notifyDataSetChanged()
        }
        override fun onCancelled(p0: DatabaseError?) {
        }
    }

    override fun onBackPressed() {
        finish()
    }
}


