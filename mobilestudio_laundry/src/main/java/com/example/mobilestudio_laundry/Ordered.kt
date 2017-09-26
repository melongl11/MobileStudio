package com.example.mobilestudio_laundry

/**
 * Created by melon on 2017-09-20.
 */
class Ordered() {
    var date:String = " "
    var name:String = " "
    var address:String = " "
    var key:String= " "
    var state:Int = 0;
    constructor(date:String, laundry:String, address:String, state:Int, key:String) : this() {
        this.date = date
        this.name = laundry
        this.address = address
        this.state = state
        this.key = key
    }

    fun toMap(): Map<String, Any>  {
        val result : HashMap<String, Any> = HashMap<String, Any>()
        result.put("date",date)
        result.put("name",name)
        result.put("address",address)
        result.put("state",state)
        result.put("key",key)
        return result
    }
}