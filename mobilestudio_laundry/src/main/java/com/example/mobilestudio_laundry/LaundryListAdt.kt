package com.example.mobilestudio_laundry

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_laundry_list_adt.view.*

class LaundryListAdt(var datas : ArrayList<Laundry>, var userID:String, var context: Context): BaseAdapter() {
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
        val mTextViewDelete: TextView = convert.findViewById(R.id.tv_laundry_list)

        laund = datas[position]
        mTextViewlaun.tv_laund_name.text = laund!!.laundry
        mTextViewfare.tv_laund_fare.text = laund!!.fare.toString()
        mTextViewDelete.setOnClickListener{
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setMessage("세탁물을 삭제 하시겠습니까?")
            builder.setPositiveButton("예"){dialog, whichButton ->
                FirebaseDatabase.getInstance().getReference("/laundry/$userID/info/list/${datas[position].laundry}").removeValue()
            }
            builder.setNegativeButton("아니오"){dialog,whichButton ->
                dialog.cancel()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }


        return convert
    }
}
