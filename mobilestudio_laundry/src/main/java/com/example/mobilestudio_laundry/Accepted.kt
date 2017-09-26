package com.example.mobilestudio_laundry

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class Accepted(){
    var date:String = " "
    var name:String = " "
    var address:String = " "
    var state : Int = 0
    var key: String = " "
    constructor(date:String, laundry:String, address:String, state:Int, key:String) : this() {
        this.date = date
        this.name = laundry
        this.address = address
        this.state = state
        this.key = key
    }
}
