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
import android.widget.*
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
import kotlinx.android.synthetic.main.history_list.*
import kotlinx.android.synthetic.main.history_list.view.*
import org.w3c.dom.Text
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
    var spinnerAdapter : ArrayAdapter<String>? = null
    private var holder = ViewHolder()

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
        var convert = convertView
        if(convert == null){
            convert = inflater.inflate(R.layout.history_list,parent,false)
            holder.spinner_aa = convert!!.findViewById(R.id.sp_deliver_time)
            holder.tv_date = convert!!.findViewById(R.id.tv_date)
            holder.tv_laundry = convert.findViewById(R.id.tv_laundry)
            holder.mImageView  = convert.findViewById(R.id.iv_state)
            holder.mCall = convert.findViewById(R.id.tv_call)
            holder.mReq = convert.findViewById(R.id.tv_require_list2)
            holder.mVisit = convert.findViewById(R.id.tv_visittime2)
            convert.setTag(holder)
        }else{
            holder = convert.getTag() as ViewHolder
        }

        val order : Order = datas.get(position)

        holder.tv_date!!.setText(order.date)
        holder.tv_laundry!!.setText(order.laundry)
        holder.mReq!!.setText(order.require)
        holder.mVisit!!.text = ("${order.hour} : ${order.time} ~ ${order.hour + 1} : ${order.time}")


        holder.mCall!!.setOnClickListener {

            if(order.laundryPhone != "") {
                var intnet = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + order.laundryPhone))
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

        when(order.state) {
            0 -> holder.mImageView!!.setImageResource(R.drawable.user_history_0)
            1 -> {
                holder.mImageView!!.setImageResource(R.drawable.user_history_4)
            }
            2 -> {
                holder.mImageView!!.setImageResource(R.drawable.user_history_1)
                val dbref = FirebaseDatabase.getInstance().getReference("laundry/${datas.get(position).laundryID}/info/time/")
                dbref.addValueEventListener(postListener)
                holder.mImageView!!.setOnClickListener {
                    if (holder.spinner_aa != null) {
                            var spinn = holder.spinner_aa?.selectedItem.toString()
                            var returnHour = spinn.substring(0, 2).trim().toInt()
                            var returnMinute = spinn.substring(4, 7).trim().toInt()
                            FirebaseDatabase.getInstance().getReference("users/${userID}/orders/${datas[position].key}/state").setValue(3)
                            FirebaseDatabase.getInstance().getReference("users/${userID}/orders/${datas[position].key}/hour").setValue(returnHour)
                            FirebaseDatabase.getInstance().getReference("users/${userID}/orders/${datas[position].key}/minute").setValue(returnMinute)
                            FirebaseDatabase.getInstance().getReference("laundry/${datas[position].laundryID}/orders/${datas[position].key}/state").setValue(3)
                            FirebaseDatabase.getInstance().getReference("laundry/${datas[position].laundryID}/orders/${datas[position].key}/hour").setValue(returnHour)
                            FirebaseDatabase.getInstance().getReference("laundry/${datas[position].laundryID}/orders/${datas[position].key}/minute").setValue(returnMinute)
                        }
                 }
            }
            3 -> {
                holder.mImageView!!.setImageResource(R.drawable.user_history_2)
            }
            4 -> {
                holder.mImageView!!.setImageResource(R.drawable.user_history_3)
                holder.mImageView!!.setOnClickListener {
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
            spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, spinnerList)
            spinnerAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            holder.spinner_aa!!.adapter = spinnerAdapter
        }
    }



    private class ViewHolder {
        var spinner_aa : Spinner? = null
        var tv_date : TextView? = null
        var tv_laundry : TextView? = null
        var mImageView :ImageView? = null
        var mCall : View? = null
        var mReq :TextView? = null
        var mVisit : TextView? = null
    }
}