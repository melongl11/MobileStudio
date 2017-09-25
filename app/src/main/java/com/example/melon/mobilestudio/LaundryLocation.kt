package com.example.melon.mobilestudio

/**
 * Created by melon on 2017-09-22.
 */
class LaundryLocation() {
    var latitude:Double = 0.0
    var longitude:Double= 0.0
    var name:String = ""
    var address: String = ""
    constructor(latitude:Double, longitude:Double, name:String, address:String) : this() {
        this.latitude = latitude
        this.longitude = longitude
        this.name = name
        this.address = address
    }
}