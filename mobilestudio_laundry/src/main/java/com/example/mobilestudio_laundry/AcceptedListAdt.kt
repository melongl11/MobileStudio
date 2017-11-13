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
class AcceptedListAdt (var datas:ArrayList<Accepted>, var context: Context) : BaseAdapter() {
    private var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    var userID:String = ""
    var key : String = ""
    var accepted: Accepted? = null
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val convert = inflater.inflate(R.layout.activity_accepted_list, null)
            val mTextViewName: View = convert!!.findViewById(R.id.tv_name_aa)
            val mTextViewAddress: View = convert.findViewById(R.id.tv_address_aa)
            val mTextViewVisittime: View = convert.findViewById(R.id.tv_visittime_aa)
            val mImageViewAccept : View = convert.findViewById(R.id.iv_state_a)

            accepted = datas[position]
            mTextViewName.tv_name_aa.text = accepted!!.name
            mTextViewAddress.tv_address_aa.text = accepted!!.address
            mTextViewVisittime.tv_visittime_aa.text = accepted!!.date
            userID = accepted!!.userID
            key = accepted!!.key
            when(accepted!!.state) {
                1 -> {
                    mImageViewAccept.iv_state_a.setImageResource(R.drawable.user_history_1)
                    mImageViewAccept.iv_state_a.setOnClickListener {
                        val dbRef = FirebaseDatabase.getInstance().getReference("/users/$userID/orders")
                        dbRef.addValueEventListener(postListener)
                    }
                }
                2 -> {
                    mImageViewAccept.iv_state_a.setImageResource(R.drawable.user_history_2)
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
                    val complete = Accepted(accepted!!.date, accepted!!.name, accepted!!.address, accepted!!.require,2, accepted!!.key, accepted!!.userID,accepted!!.hour, accepted!!.minute, accepted!!.phoneNumber)
                    val acceptUpdate = HashMap<String, Any>()
                    acceptUpdate.put("laundry/${order.laundryID}/orders/$key", complete)
                    mDatabase.updateChildren(acceptUpdate)
                }
            }
        }
    }
}
