package com.example.melon.mobilestudio

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.item_list.view.*

class ItemListAdapter(var datas : ArrayList<Item>, var context: Context) : BaseAdapter()  {
    var laund : Item? = null
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
        val convert = inflater.inflate(R.layout.item_list,null)
        val mTextViewlaund: View = convert.findViewById(R.id.tv_item)
        val mTextViewfared: View = convert.findViewById(R.id.tv_fare)

        laund = datas[position]
        mTextViewlaund.tv_item.text = laund!!.laundry
        mTextViewfared.tv_fare.text = laund!!.fare.toString()

        return convert
    }
}
