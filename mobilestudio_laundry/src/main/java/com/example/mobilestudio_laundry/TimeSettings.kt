package com.example.mobilestudio_laundry

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.BundleCompat
import android.support.v4.app.SupportActivity
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_management.*
import java.util.HashMap

/**
 * Created by kim on 2017-10-05.
 */
class TimeSettings : TimePickerDialog.OnTimeSetListener,AppCompatActivity {

    private var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference()

    var con : Context

    constructor(con : Context){
        this.con = con
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute : Int) {
        var time = hourOfDay.toString()
        var time2 = minute.toString()

        var intent = Intent()
        intent.putExtra("hour",time)
        intent.putExtra("minute",time2)
        Toast.makeText(con,""+hourOfDay+" : "+minute,Toast.LENGTH_LONG).show()
        /*
        */

    /*
        setResult(android.app.Activity.RESULT_OK,intent)
        finish()
        */
    }

}
