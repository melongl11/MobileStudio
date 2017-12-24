package com.example.melon.mobilestudio

/**
 * Created by melon on 2017-09-12.
 */
    class Order() {
        var date:String = " "
        var laundry:String = " "
        var state:Int = 0
        var key:String = " "
        var laundryID:String = ""
        var require:String = ""
        var hour:Int = 0
        var time:Int = 0
        constructor(date:String, laundry:String, state:Int, key:String, laundryID:String, require:String, visithour: Int, visitTime: Int) : this() {
            this.date = date
            this.laundry = laundry
            this.state = state
            this.key = key
            this.laundryID = laundryID
            this.require = require
            this.hour = visithour
            this.time = visitTime
        }

    fun toMap(): Map<String, Any>  {
        val result : HashMap<String, Any> = HashMap<String, Any>()
        result.put("date",date)
        result.put("laundry",laundry)
        result.put("state",state)
        result.put("key",key)
        result.put("laundryID", laundryID)
        result.put("require", require)
        result.put("hour",hour)
        result.put("time",time)
        return result
    }
}