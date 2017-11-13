package com.example.mobilestudio_laundry

import android.app.Dialog
import android.support.v4.app.DialogFragment
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.content.Intent.getIntentOld
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.widget.TimePicker
import kotlinx.android.synthetic.main.activity_management.*
import kotlinx.android.synthetic.main.activity_management.view.*
import java.util.*

/**
 * Created by kim on 2017-10-05.
 */
class DialogHandler : DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val calender : Calendar = Calendar.getInstance()
        val hour : Int = calender.get(Calendar.HOUR_OF_DAY)
        val min : Int = calender.get(Calendar.MINUTE)

        return TimePickerDialog(activity,timePickerListener,hour,min,false)

    }

    private val timePickerListener = object: TimePickerDialog.OnTimeSetListener {
        override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
            activity.tv_visithour.text = hour.toString()
            activity.tv_visitminute.text = minute.toString()
            activity.tv_visittime2.text = ((hour+1).toString() + ":" + minute.toString())
        }
    }

}