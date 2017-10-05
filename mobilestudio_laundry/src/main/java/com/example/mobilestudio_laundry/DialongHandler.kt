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
import android.widget.TimePicker
import kotlinx.android.synthetic.main.activity_management.*
import java.util.*

/**
 * Created by kim on 2017-10-05.
 */
class DialogHandler : DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        var calender : Calendar = Calendar.getInstance()
        var hour : Int = calender.get(Calendar.HOUR_OF_DAY)
        var min : Int = calender.get(Calendar.MINUTE)
        var dialog : TimePickerDialog
        var ts = TimeSettings(activity)
        dialog = TimePickerDialog(activity,ts,hour,min,DateFormat.is24HourFormat(activity))
        return dialog
    }

}