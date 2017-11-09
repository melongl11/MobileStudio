package com.example.melon.mobilestudio

/**
 * Created by melon on 2017-11-09.
 */
class Information() {
    var name:String = ""
    var phoneNumber : String = ""
    constructor(name : String, phoneNumber : String) : this(){
        this.name = name
        this.phoneNumber = phoneNumber
    }
    fun toMap() : Map<String, Any> {
        val result : HashMap<String, Any> = HashMap<String, Any>()
        result.put("name", name)
        result.put("phoneNumber", phoneNumber)
        return result
    }
}