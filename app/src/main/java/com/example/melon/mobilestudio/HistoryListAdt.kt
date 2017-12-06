package com.example.melon.mobilestudio

import android.content.Context
import android.content.Intent
import android.database.DataSetObservable
import android.database.DataSetObserver
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.renderscript.Sampler
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Spinner
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.activity_order2.*
import kotlinx.android.synthetic.main.activity_user_modify_information.*
import kotlinx.android.synthetic.main.history_list.*
import kotlinx.android.synthetic.main.history_list.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by melon on 2017-09-12.
 */
class HistoryListAdt(var datas:ArrayList<Order>, var context:Context, var userID:String) : BaseAdapter() {
    var inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var key:String = ""
    var visitHour:Int = 0
    var visitMinute:Int = 0
    var spinner: Spinner? = null
    var phoneNum : String = ""
    var require : String = ""
    var visitHourR :Int = 0
    var visitMinuteR:Int = 0


    private var spinnerList = java.util.ArrayList<String>()
    override fun getCount(): Int {
        return datas.size
    }

    override fun getItem(position: Int): Any {
        return datas.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val convert = inflater.inflate(R.layout.history_list,null)
        val mTextViewDate : View = convert.findViewById(R.id.tv_date)
        val mTextViewLaundry : View = convert.findViewById(R.id.tv_laundry)
        val mImageView :View = convert.findViewById(R.id.iv_state)
        val mCall : View = convert.findViewById(R.id.tv_call)
        val mReq :View = convert.findViewById(R.id.tv_require_list2)
        val mVisit : View = convert.findViewById(R.id.tv_visittime2)

        val order : Order = datas.get(position)
        val dbrefR = FirebaseDatabase.getInstance().getReference("laundry/${datas.get(position).laundryID}/orders/${datas.get(position).key}")
        dbrefR.addValueEventListener(postListener3)

        mTextViewDate.tv_date.setText(order.date)
        mTextViewLaundry.tv_laundry.setText(order.laundry)
        mReq.tv_require_list2.setText(require)
        mVisit.tv_visittime2.setText("$visitHourR : $visitMinuteR")

        val dbrefphone = FirebaseDatabase.getInstance().getReference("laundry_list/${datas.get(position).laundryID}")
        dbrefphone.addValueEventListener(postListener2)

        mCall.tv_call.setOnClickListener {

            if(phoneNum != "") {
                var intnet = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNum))
                startActivity(context, intnet, Bundle.EMPTY)
            } else {
                var alert = AlertDialog.Builder(context)
                alert.setMessage("저장된 전화번호가 없습니다")
                alert.setPositiveButton("확인"){dialog, which ->
                    dialog.cancel()
                }
                val dial = alert.create()
                dial.show()
            }
        }
        spinner = convert.findViewById(R.id.sp_deliver_time)
        when(order.state) {
            0 -> mImageView.iv_state.setImageResource(R.drawable.user_history_0)
            1 -> {
                mImageView.iv_state.setImageResource(R.drawable.user_history_4)
                val dbref = FirebaseDatabase.getInstance().getReference("laundry/${datas.get(position).laundryID}/info/time/")
                dbref.addValueEventListener(postListener)
            }
            2 -> {
                mImageView.iv_state.setImageResource(R.drawable.user_history_1)
                    if(spinner != null) {
                        var spinn = spinner?.selectedItem.toString()
                        FirebaseDatabase.getInstance().getReference("users/${userID}/orders/${datas[position].key}/state").setValue(3)
                        FirebaseDatabase.getInstance().getReference("laundry/${datas[position].laundryID}/orders/${datas[position].key}/state").setValue(3)
                    }
            }
            3 -> {
                mImageView.iv_state.setImageResource(R.drawable.user_history_2)
            }
            4 -> {
                mImageView.iv_state.setImageResource(R.drawable.user_history_3)
                mImageView.iv_state.setOnClickListener {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                    builder.setMessage("세탁물을 수령 하셨습니까?")
                    builder.setPositiveButton("예"){dialog, whichButton ->

                        FirebaseDatabase.getInstance().getReference("/users/$userID/orders/${order.key}").removeValue()
                        FirebaseDatabase.getInstance().getReference("laundry/${order.laundryID}/orders/${order.key}").removeValue()
                    }
                    builder.setNegativeButton("아니오"){dialog,whichButton ->
                        dialog.cancel()
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
            }
        }
        return convert
    }


    private val postListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {
        }
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for(snapshot in dataSnapshot.children) {
                val visitTime = snapshot.getValue(VisitTime::class.java)
                visitHour = visitTime!!.hourOfDay
                visitMinute = visitTime.minute
                val timeFormat = SimpleDateFormat("HH : mm")
                val fromTime = timeFormat.format(Date(2000,1,1,visitHour, visitMinute,0))
                val toTime = timeFormat.format(Date(2000,1,1,visitHour+1, visitMinute,0))
                spinnerList.add("${fromTime} ~  ${toTime}")
            }
            val spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, spinnerList)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner!!.adapter = spinnerAdapter
        }
    }

    private val postListener2 = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {

        }

        override fun onDataChange(datasnapshot: DataSnapshot) {
            for(snapshot in datasnapshot.children) {
                val Location = datasnapshot.getValue(LaundryLocation::class.java)
                if (Location!!.laundryNum != null) {
                    phoneNum = Location.laundryNum
                }
            }
        }
    }

    private val postListener3 = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {

        }

        override fun onDataChange(datasnapshot: DataSnapshot) {
            for(snapshot in datasnapshot.children) {
                val ReV = datasnapshot.getValue(OrderToLaundry::class.java)
                if (ReV != null) {
                    require = ReV.require
                    visitHourR = ReV.visitHour
                    visitMinuteR = ReV.visitMinute
                }
            }
        }
    }
}