package com.example.mobilestudio_laundry

/**
 * Created by kim on 2017-10-18.
 */
class Laundry() {
    var laundry : String = " "
    var fare : Int = 0
    constructor(fare:Int, laundry:String) : this() {
        this.laundry = laundry
        this.fare = fare
    }
}