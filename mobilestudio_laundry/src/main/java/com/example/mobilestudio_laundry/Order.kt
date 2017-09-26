package com.example.mobilestudio_laundry

/**
 * Created by melon on 2017-09-12.
 */
class Order() {
    var date:String = " "
    var laundry:String = " "
    var state:Int = 0
    var key:String = " "
    constructor(date:String, laundry:String, state:Int, key:String) : this() {
        this.date = date
        this.laundry = laundry
        this.state = state
        this.key = key
    }

    fun toMap(): Map<String, Any>  {
        var result : HashMap<String, Any> = HashMap<String, Any>()

        result.put("date",date)
        result.put("laundry",laundry)
        result.put("state",state)
        result.put("key",key)
        return result
    }
}