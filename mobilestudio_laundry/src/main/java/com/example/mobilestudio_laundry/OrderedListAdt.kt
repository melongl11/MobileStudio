package com.example.mobilestudio_laundry

import android.app.AlertDialog
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
    var inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        if (convertView == null) {
            var convert = inflater.inflate(R.layout.ordered_list,null)
            var mTextViewName : View = convert.findViewById(R.id.tv_name)
            var mTextViewAddress : View = convert.findViewById(R.id.tv_address)
            var mTextViewVisittime : View = convert.findViewById(R.id.tv_visittime)
            var mImageViewAccept : View = convert.findViewById(R.id.iv_accept)


            var ordered : Ordered = datas.get(position)
            mTextViewName.tv_name.setText(ordered.name)
            mTextViewAddress.tv_address.setText(ordered.address)
            mTextViewVisittime.tv_visittime.setText(ordered.date)

            mImageViewAccept.iv_accept.setOnClickListener{
                var dbRef = FirebaseDatabase.getInstance().getReference("users")
                dbRef.addListenerForSingleValueEvent(postListener)
            }

            return convert
        }
        else {

            return convertView
        }
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

        override fun onDataChange(p0: DataSnapshot?) {

        }

    }
}