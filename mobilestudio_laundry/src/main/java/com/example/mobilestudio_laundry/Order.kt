package com.example.mobilestudio_laundry

/**
 * Created by melon on 2017-09-12.
 */
class Order() {
    var date:String = " "
    var laundry:String = " "
    var state:Int = 0
    var key:String = " "
    var laundryID:String = ""
    constructor(date:String, laundry:String, state:Int, key:String,laundryID: String) : this() {
        this.date = date
        this.laundry = laundry
        this.state = state
        this.key = key
        this.laundryID = laundryID
    }

    fun toMap(): Map<String, Any>  {
        val result : HashMap<String, Any> = HashMap<String, Any>()
        result.put("date",date)
        result.put("laundry",laundry)
        result.put("state",state)
        result.put("key",key)
        result.put("userID",laundryID)
        return result
    }
}