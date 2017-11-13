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
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_order2.*
import kotlinx.android.synthetic.main.history_list.*
import kotlinx.android.synthetic.main.history_list.view.*

/**
 * Created by melon on 2017-09-12.
 */
class HistoryListAdt(var datas:ArrayList<Order>, var context:Context, var userID:String) : BaseAdapter() {
    var inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var key:String = ""
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
            when(order.state) {
                0 -> mImageView.iv_state.setImageResource(R.drawable.user_history_0)
                1 -> mImageView.iv_state.setImageResource(R.drawable.user_history_1)
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
}