package com.example.melon.mobilestudio

/**
 * Created by melon on 2017-09-12.
 */
class Order() {
    var date:String = " "
    var laundry:String = " "
    var state:Int = 0
    constructor(date:String, laundry:String, state:Int) : this() {
        this.date = date
        this.laundry = laundry
        this.state = state
    }

    fun toMap(): Map<String, Any>  {
        var result : HashMap<String, Any> = HashMap<String, Any>()

        result.put("date",date)
        result.put("laundry",laundry)
        result.put("state",state)

        return result
    }
}