package com.example.melon.mobilestudio

import android.content.Context
import android.content.Intent
import android.database.DataSetObservable
import android.database.DataSetObserver
import android.graphics.drawable.Drawable
import android.renderscript.Sampler
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Spinner
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_order2.*
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

        val order : Order = datas.get(position)
        mTextViewDate.tv_date.setText(order.date)
        mTextViewLaundry.tv_laundry.setText(order.laundry)
        spinner = convert.findViewById(R.id.sp_deliver_time)
        when(order.state) {
            0 -> mImageView.iv_state.setImageResource(R.drawable.user_history_0)
            1 -> {
                mImageView.iv_state.setImageResource(R.drawable.user_history_1)
                val dbref = FirebaseDatabase.getInstance().getReference("laundry/${datas.get(position).laundryID}/info/time/")
                dbref.addValueEventListener(postListener)
            }
            2 -> {
                mImageView.iv_state.setImageResource(R.drawable.user_history_2)
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
}