package com.example.mobilestudio_laundry

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.ordered_list.view.*

/**
 * Created by melon on 2017-09-20.
 */
class OrderedListAdt(var datas:ArrayList<Ordered>, var context: Context, var uID: String) : BaseAdapter() {
    private var inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var key:String = " "
    var userID = " "
    var ordered:Ordered? = null
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val convert = inflater.inflate(R.layout.ordered_list, parent, false)
        val mTextViewName: View = convert.findViewById(R.id.tv_costumer_name)
        val mTextViewAddress: View = convert.findViewById(R.id.tv_address)
        val mTextViewVisitDay: View = convert.findViewById(R.id.tv_visitday)
        val mTextViewVisitTime: View = convert.findViewById(R.id.tv_visittime)
        val mTextViewPhone: View = convert.findViewById(R.id.tv_costumer_phone)
        val mTextViewAccept: View = convert.findViewById(R.id.tv_accept)

        if (datas.isEmpty()) {
            mTextViewAddress.tv_address.text = "주문이 없습니다."
        }
        ordered = datas[position]
        mTextViewPhone.tv_costumer_phone.text = ordered!!.phoneNumber
        mTextViewName.tv_costumer_name.text = ordered!!.name
        mTextViewAddress.tv_address.text = ordered!!.address
        mTextViewVisitTime.tv_visittime.text = ("${ordered!!.hour} : ${ordered!!.minute} ~ ${ordered!!.hour + 1} : ${ordered!!.minute} 방문 요망")
        mTextViewVisitDay.tv_visitday.text = ordered!!.date

        mTextViewAccept.tv_accept.setOnClickListener {
            key = datas[position].key
            userID = datas[position].userID
            FirebaseDatabase.getInstance().getReference("/users/$userID/orders/${key}/state").setValue(1)
            FirebaseDatabase.getInstance().getReference("/laundry/$uID/orders/${key}/state").setValue(1)
        }
        return convert
    }

    override fun getItem(p0: Int): Any {
        return datas[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return datas.size
    }

}