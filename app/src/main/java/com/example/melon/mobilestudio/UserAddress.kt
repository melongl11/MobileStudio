package com.example.melon.mobilestudio

/**
 * Created by melon on 2017-11-09.
 */
class UserAddress() {
    var address = ""
    var latitude = 0.0
    var longitude = 0.0
    constructor(address : String, latitude : Double, longitude : Double) : this() {
        this.address = address
        this.latitude = latitude
        this.longitude = longitude
    }
}