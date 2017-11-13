package com.example.mobilestudio_laundry

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class Accepted(){
    var date:String = " "
    var name:String = " "
    var address:String = " "
    var key:String= " "
    var require:String = " "
    var state:Int = 0;
    var userID:String = ""
    var hour:Int = 0
    var minute:Int = 0
    var phoneNumber:String = ""
    constructor(date:String, laundry:String, address:String, require:String,state:Int, key:String, userID:String, hour:Int, minute:Int, phoneNumber:String) : this() {
        this.date = date
        this.name = laundry
        this.address = address
        this.require = require
        this.state = state
        this.key = key
        this.userID = userID
        this.hour = hour
        this.minute = minute
        this.phoneNumber = phoneNumber
    }

    fun toMap(): Map<String, Any>  {
        val result : HashMap<String, Any> = HashMap<String, Any>()
        result.put("date",date)
        result.put("name",name)
        result.put("address",address)
        result.put("state",state)
        result.put("key",key)
        result.put("userID",userID)
        result.put("require", require)
        result.put("hour", hour)
        result.put("minute",minute)
        result.put("phoneNumber", phoneNumber)
        return result
    }
}
