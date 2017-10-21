package com.example.mobilestudio_laundry

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_laundry_list_adt.view.*

class LaundryListAdt(var datas : ArrayList<Laundry>, var context: Context): BaseAdapter() {
    var laund : Laundry? = null
    var inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return datas.size
    }

    override fun getItem(p0: Int): Any {
        return datas.get(p0)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val convert = inflater.inflate(R.layout.activity_laundry_list_adt,null)
            val mTextViewlaun: View = convert.findViewById(R.id.tv_laund_name)
            val mTextViewfare: View = convert.findViewById(R.id.tv_laund_fare)

            laund = datas[position]
        mTextViewlaun.tv_laund_name.text = laund!!.laundry
        mTextViewfare.tv_laund_fare.text = laund!!.fare.toString()

        return convert
    }
}
