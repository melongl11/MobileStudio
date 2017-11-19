package com.example.mobilestudio_laundry

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_visittime_list.view.*

class VisittimeListAdt(var datas : ArrayList<Visittime>, var userID:String, var context: Context) : BaseAdapter() {
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
        val convert = inflater.inflate(R.layout.activity_visittime_list, parent, false)
        val mTextViewfirst: TextView = convert.findViewById(R.id.tv_firsttime)
        val mTextViewlast: TextView = convert.findViewById(R.id.tv_lasttime)
        val mTextViewDelete:TextView = convert.findViewById(R.id.tv_visitTimeDelete)


        visit = datas[position]
        mTextViewfirst.text = ("${visit!!.hourOfDay.toString()} : ${visit!!.minute.toString()}")
        mTextViewlast.text = ("${(visit!!.hourOfDay + 1).toString()} : ${visit!!.minute.toString()}")
        mTextViewDelete.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setMessage("방문 시간을 삭제 하시겠습니까?")
            builder.setPositiveButton("예"){dialog, whichButton ->
                FirebaseDatabase.getInstance().getReference("/laundry/$userID/info/time/${datas[position].hourOfDay.toString()+datas[position].minute.toString()}").removeValue()
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
