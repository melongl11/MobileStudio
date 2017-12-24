package com.example.melon.mobilestudio

import java.util.HashMap

/**
 * Created by melon on 2017-11-12.
 */
class OrderToLaundry() {
    var date:String = ""
    var name:String = ""
    var address:String = ""
    var require:String = ""
    var state:Int = 0
    var saveTime:String = ""
    var userID:String =""
    var hour:Int = 0
    var minute:Int = 0
    var phoneNumber:String = ""
    var payment: String = ""

    constructor(date:String, name:String, address:String, require:String, state:Int, saveTime:String, userID:String, visitHour:Int, visitMinute:Int, phoneNumber:String, payment:String):this() {
        this.date = date
        this.name = name
        this.address = address
        this.require = require
        this.state = state
        this.saveTime = saveTime
        this.userID = userID
        this.hour = visitHour
        this.minute = visitMinute
        this.phoneNumber = phoneNumber
        this.payment = payment
    }

    fun toMap() : Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("date",date)
        result.put("name",name)
        result.put("address",address)
        result.put("require", require)
        result.put("state", state)
        result.put("key",saveTime)
        result.put("userID", userID)
        result.put("hour", hour)
        result.put("minute", minute)
        result.put("phoneNumber", phoneNumber)
        result.put("payment",payment)
        return result
    }
}