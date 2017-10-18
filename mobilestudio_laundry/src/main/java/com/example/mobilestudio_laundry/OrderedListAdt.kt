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
class OrderedListAdt(var datas:ArrayList<Ordered>, var context: Context) : BaseAdapter() {
    private var inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var key:String = " "
    var userID = " "
    var ordered:Ordered? = null
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        if (convertView == null) {
            val convert = inflater.inflate(R.layout.ordered_list,null)
            val mTextViewName : View = convert.findViewById(R.id.tv_name)
            val mTextViewAddress : View = convert.findViewById(R.id.tv_address)
            val mTextViewVisitTime : View = convert.findViewById(R.id.tv_visittime)
            val mImageViewAccept : View = convert.findViewById(R.id.iv_accept)

            if(datas.isEmpty()){
                mTextViewAddress.tv_address.setText("주문이 없습니다.")
            }
            else {
                ordered = datas[position]
                mTextViewName.tv_name.setText(ordered!!.name)
                mTextViewAddress.tv_address.setText(ordered!!.address)
                mTextViewVisitTime.tv_visittime.setText(ordered!!.date)
                mImageViewAccept.iv_accept.setImageResource(R.drawable.bt_accept)

                key = ordered!!.key
                userID = ordered!!.userID
                mImageViewAccept.iv_accept.setOnClickListener {
                    val dbRef = FirebaseDatabase.getInstance().getReference("/users/$userID/orders")
                    dbRef.addListenerForSingleValueEvent(postListener)
                }
            }
            return convert
        }
        else {

            return convertView
        }
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

    private  val postListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {

        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val mDatabase = FirebaseDatabase.getInstance().reference
            for(snapshot in dataSnapshot.children) {
                val order = snapshot.getValue(Order::class.java)
                if(order!!.key == key) {
                    val newOrder = Order(order.date, order.laundry, 1, key, order.laundryID)
                    val childUpdate = HashMap<String, Any>()
                    childUpdate.put("users/$userID/orders/$key", newOrder)

                    mDatabase.updateChildren(childUpdate)
                    val newOrdered = Ordered(ordered!!.date, ordered!!.name, ordered!!.address, ordered!!.require,1, ordered!!.key, ordered!!.userID)
                    val acceptUpdate = HashMap<String, Any>()
                    acceptUpdate.put("laundry/${order.laundryID}/orders/$key", newOrdered)
                    mDatabase.updateChildren(acceptUpdate)
                }
            }
        }
    }
}