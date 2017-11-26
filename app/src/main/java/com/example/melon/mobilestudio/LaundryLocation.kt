package com.example.melon.mobilestudio

/**
 * Created by melon on 2017-09-22.
 */
class LaundryLocation() {
    var latitude:Double = 0.0
    var longitude:Double= 0.0
    var name:String = ""
    var address: String = ""
    var laundryID: String = ""
    var laundryNum:String = ""
    constructor(latitude:Double, longitude:Double, name:String, address:String, laundryID:String, laundryNum:String) : this() {
        this.latitude = latitude
        this.longitude = longitude
        this.name = name
        this.address = address
        this.laundryID = laundryID
        this.laundryNum = laundryNum
    }
}