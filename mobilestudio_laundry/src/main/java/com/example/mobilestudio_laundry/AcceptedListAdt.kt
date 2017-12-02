package com.example.mobilestudio_laundry

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
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

    var userID:String = ""
    var key : String = ""
    var accepted: Accepted? = null
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val convert = inflater.inflate(R.layout.activity_accepted_list, null)
            val mTextViewName: View = convert!!.findViewById(R.id.tv_name_aa)
            val mTextViewAddress: View = convert.findViewById(R.id.tv_address_aa)
            val mTextViewVisittime: View = convert.findViewById(R.id.tv_visittime_aa)
            val mTextViewAccept : View = convert.findViewById(R.id.tv_state_a)
            val mTextViewDate: View = convert.findViewById(R.id.tv_date_aa)
            val mTextViewPhone:View = convert.findViewById(R.id.tv_phone_aa)

            accepted = datas[position]
            mTextViewName.tv_name_aa.text = accepted!!.name
            mTextViewAddress.tv_address_aa.text = accepted!!.address
            mTextViewVisittime.tv_visittime_aa.text = ("${accepted!!.hour} : ${accepted!!.minute} ~ ${accepted!!.hour} : ${accepted!!.minute}")
            mTextViewDate.tv_date_aa.text = accepted!!.date
            mTextViewPhone.tv_phone_aa.text = accepted!!.phoneNumber

            when(accepted!!.state) {
                1 -> {
                    mTextViewAccept.tv_state_a.text = "세탁완료"
                    mTextViewAccept.tv_state_a.setOnClickListener {
                        userID = datas[position].userID
                        key = datas[position].key
                        val dbRef = FirebaseDatabase.getInstance().getReference("/users/$userID/orders")
                        dbRef.addValueEventListener(postListener)
                    }
                }
                2 -> {
                    mTextViewAccept.tv_state_a.text = "배송대기"
                }
                3 -> {
                    mTextViewAccept.tv_state_a.text = "배송출발"
                    mTextViewAccept.tv_state_a.setOnClickListener {
                        val newState = HashMap<String, Any>()
                        FirebaseDatabase.getInstance().getReference("users/${datas[position].userID}/orders/${datas[position].key}/state").setValue(4)
                        FirebaseDatabase.getInstance().getReference("laundry/${uID}/orders/${datas[position].key}/state").setValue(4)
                    }
                }
                4-> {
                    mTextViewAccept.tv_state_a.text = "배송중"
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

    private  val postListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {

        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val mDatabase = FirebaseDatabase.getInstance().reference
            for(snapshot in dataSnapshot.children) {
                val order = snapshot.getValue(Order::class.java)
                if(order!!.key == key && order.state == 1) {
                    val newOrder = Order(order.date, order.laundry, 2, key, order.laundryID)
                    val childUpdate = HashMap<String, Any>()
                    childUpdate.put("users/$userID/orders/$key", newOrder)

                    mDatabase.updateChildren(childUpdate)
                    val complete = accepted
                    complete!!.state = 2
                    val acceptUpdate = HashMap<String, Any>()
                    acceptUpdate.put("laundry/${order.laundryID}/orders/$key", complete)
                    mDatabase.updateChildren(acceptUpdate)
                }
            }
        }
    }
}
