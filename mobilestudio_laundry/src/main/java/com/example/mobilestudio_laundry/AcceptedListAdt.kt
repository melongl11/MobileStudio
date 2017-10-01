package com.example.mobilestudio_laundry

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_accepted_list.view.*

/**
 * Created by melon on 2017-09-20.
 */
class AcceptedListAdt (var datas:ArrayList<Accepted>, var context: Context) : BaseAdapter() {
    var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        if (convertView == null) {
            val convert = inflater.inflate(R.layout.activity_accepted_list, null)
            val mTextViewName: View = convert.findViewById(R.id.tv_name_aa)

            val mTextViewAddress: View = convert.findViewById(R.id.tv_address_aa)

            val mTextViewVisittime: View = convert.findViewById(R.id.tv_visittime_aa)

            val mImageViewAccept : View = convert.findViewById(R.id.iv_state_a)


            if(datas.isEmpty()) {
                mTextViewAddress.tv_address_aa.setText("접수받은 주문이 없습니다.")
            }
            val accepted: Accepted = datas.get(position)
            when(accepted.state) {
                1 -> {
                    mTextViewName.tv_name_aa.setText(accepted.name)
                    mTextViewAddress.tv_address_aa.setText(accepted.address)
                    mTextViewVisittime.tv_visittime_aa.setText(accepted.date)
                    mImageViewAccept.iv_state_a.setImageResource(R.drawable.user_history_1)
                }
                2 -> {
                    mTextViewName.tv_name_aa.setText(accepted.name)
                    mTextViewAddress.tv_address_aa.setText(accepted.address)
                    mTextViewVisittime.tv_visittime_aa.setText(accepted.date)
                    mImageViewAccept.iv_state_a.setImageResource(R.drawable.user_history_2)
                }
            }
            return convert
        } else {

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
}
