package com.example.melon.mobilestudio

import android.content.Context
import android.database.DataSetObservable
import android.database.DataSetObserver
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.history_list.*
import kotlinx.android.synthetic.main.history_list.view.*

/**
 * Created by melon on 2017-09-12.
 */
class ListAdapter(var datas:ArrayList<Order>, var context:Context) : BaseAdapter() {
    var inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

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
                2 -> mImageView.iv_state.setImageResource(R.drawable.user_history_2)
            }
            return convert
    }

}