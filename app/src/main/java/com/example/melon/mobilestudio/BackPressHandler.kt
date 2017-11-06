package com.example.melon.mobilestudio

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast

/**
 * Created by melon on 2017-09-29.
 */
class BackPressHandler {
    private var backKeyPressedTime :Long = 0
    private var activity:Activity? = null

    constructor(context:Activity) {
        this.activity = context
    }

    fun onBackPressedEnd() {
        if(System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            Toast.makeText(activity!!,"한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
            return;
        }
        if(System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            for( i in arrayListforActivity)
                i.finish()
            activity!!.finish()
        }
    }
}