package com.example.mobilestudio_laundry

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_visittime_list.view.*

class VisittimeListActivity(var datas : ArrayList<Visittime>,var context: Context) : BaseAdapter() {
    var visit : Visittime? = null
    var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return datas.size
    }

    override fun getItem(p0: Int): Any {
        return datas.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        if (convertView == null) {
            val convert = inflater.inflate(R.layout.activity_visittime_list, null)
            val mTextViewfirst: View = convert.findViewById(R.id.tv_firsttime)
            val mTextViewlast: View = convert.findViewById(R.id.tv_lasttime)

            visit = datas[position]
            mTextViewfirst.tv_firsttime.setText(visit!!.hourOfDay)
            mTextViewlast.tv_lasttime.setText(visit!!.minute)
            return convert
        } else {
            return convertView
        }
    }
}
