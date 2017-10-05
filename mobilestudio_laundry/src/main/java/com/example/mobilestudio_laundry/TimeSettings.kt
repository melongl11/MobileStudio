package com.example.mobilestudio_laundry

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast

/**
 * Created by kim on 2017-10-05.
 */
class TimeSettings : TimePickerDialog.OnTimeSetListener {
    var con : Context

    constructor(con : Context){
        this.con = con
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute : Int) {
        Toast.makeText(con,""+hourOfDay+" : "+minute,Toast.LENGTH_LONG).show()
    }
}