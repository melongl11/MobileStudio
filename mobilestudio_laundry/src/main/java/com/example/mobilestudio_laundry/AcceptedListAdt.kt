package com.example.mobilestudio_laundry

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
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
import kotlinx.android.synthetic.main.activity_accepted_list.view.*

/**
 * Created by melon on 2017-09-20.
 */
class AcceptedListAdt (var datas:ArrayList<Accepted>, var context: Context, var uID:String) : BaseAdapter() {
    private var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var holder = ViewHolder()
        var convert = convertView
        if(convert == null) {
            convert = inflater.inflate(R.layout.activity_accepted_list, parent, false)
            holder.tv_name_aa = convert!!.findViewById(R.id.tv_name_aa)
            holder.tv_address_aa= convert.findViewById(R.id.tv_address_aa)
            holder.tv_visittime_aa= convert.findViewById(R.id.tv_visittime_aa)
            holder.tv_state_a = convert.findViewById(R.id.tv_state_a)
            holder.tv_date_aa = convert.findViewById(R.id.tv_date_aa)
            holder.tv_phone_aa = convert.findViewById(R.id.tv_phone_aa)
            holder.tv_require_aa = convert.findViewById(R.id.tv_require_aa)
            convert.setTag(holder)
        } else {
            holder = convert.getTag() as ViewHolder
        }

        val accepted = datas[position]
        holder.tv_name_aa!!.text = accepted.name
        holder.tv_address_aa!!.text = accepted.address
        holder.tv_visittime_aa!!.text = ("${accepted.hour} : ${accepted.minute} ~ ${accepted.hour + 1} : ${accepted.minute}")
        holder.tv_date_aa!!.text = accepted.date
        holder.tv_phone_aa!!.text = accepted.phoneNumber
        holder.tv_require_aa!!.text = accepted.require

        when(accepted.state) {
            1 -> {
                holder.tv_state_a!!.text = "세탁완료"
                holder.tv_state_a!!.setOnClickListener {
                    FirebaseDatabase.getInstance().getReference("/users/${accepted.userID}/orders/${accepted.key}/state").setValue(2)
                    FirebaseDatabase.getInstance().getReference("laundry/${uID}/orders/${accepted.key}/state").setValue(2)
                    Log.d("checkPosition", datas[position].key + " position : " + position)
                }
            }
            2 -> {
                holder.tv_state_a!!.text = "배송대기"
            }
            3 -> {
                holder.tv_state_a!!.text = "배송출발"
                holder.tv_state_a!!.setOnClickListener {
                    FirebaseDatabase.getInstance().getReference("users/${accepted.userID}/orders/${accepted.key}/state").setValue(4)
                    FirebaseDatabase.getInstance().getReference("laundry/${uID}/orders/${accepted.key}/state").setValue(4)
                }
            }
            4-> {
                holder.tv_state_a!!.text = "배송중"
            }
        }
            //if(!datas.isEmpty()) Toast.makeText(context, position.toString(), Toast.LENGTH_SHORT).show()
        return convert

    }

    override fun getItem(p0: Int): Any {
        return datas.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return datas.size
    }
    private class ViewHolder {
        var tv_name_aa : TextView? = null
        var tv_address_aa :TextView? = null
        var tv_visittime_aa : TextView? = null
        var tv_state_a: TextView? = null
        var tv_date_aa: TextView? = null
        var tv_phone_aa: TextView? = null
        var tv_require_aa: TextView? = null

    }

}
