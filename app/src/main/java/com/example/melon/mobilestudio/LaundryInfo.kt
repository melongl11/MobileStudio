package com.example.melon.mobilestudio

/**
 * Created by melon on 2017-12-25.
 */
class LaundryInfo() {
    var address: String = ""
    var latitude: Float = 0.0f
    var laundryID: String = ""
    var laundryNum :String = ""
    var longitude: Float = 0.0f
    var name : String = ""

    constructor(address: String, latitude:Float, laundryID:String, laundryNum:String, longitude:Float, name:String):this() {
        this.address = address
        this.latitude = latitude
        this.laundryID = laundryID
        this.laundryNum = laundryNum
        this.longitude = longitude
        this.name = name
    }
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("address", address)
        result.put("latitude", latitude)
        result.put("laundryID", laundryID)
        result.put("laundryNum", laundryNum)
        result.put("longitude", longitude)
        result.put("name", name)
        return result
    }
}