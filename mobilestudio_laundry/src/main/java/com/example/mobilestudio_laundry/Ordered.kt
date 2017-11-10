package com.example.mobilestudio_laundry

/**
 * Created by melon on 2017-09-20.
 */
class Ordered() {
    var date:String = " "
    var name:String = " "
    var address:String = " "
    var key:String= " "
    var require:String = " "
    var state:Int = 0;
    var userID:String = ""
    var hour:Int = 0
    var minute:Int = 0
    constructor(date:String, laundry:String, address:String, require:String,state:Int, key:String, userID:String, hour:Int, minute:Int) : this() {
        this.date = date
        this.name = laundry
        this.address = address
        this.require = require
        this.state = state
        this.key = key
        this.userID = userID
        this.hour = hour
        this.minute = minute
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
        return result
    }
}