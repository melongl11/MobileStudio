package com.example.mobilestudio_laundry

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.HashMap

/**
 * Created by kim on 2017-10-05.
 */
class TimeSettings : TimePickerDialog.OnTimeSetListener {

    private var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference()

    var con : Context

    constructor(con : Context){
        this.con = con
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute : Int) {

        fun newtime(time: String,time2 : String) {

            val childUpdate = HashMap<String, Any>()

            val result: HashMap<String, Any> = HashMap<String, Any>()
            result.put("hourOfDay", hourOfDay)
            result.put("minute",minute)
            childUpdate.put("/laundry/info/", result)
            mDatabase.updateChildren(childUpdate)
        }

        var time = hourOfDay.toString()
        var time2 = minute.toString()
        newtime(time,time2)
        Toast.makeText(con,""+hourOfDay+" : "+minute,Toast.LENGTH_LONG).show()
    }
}