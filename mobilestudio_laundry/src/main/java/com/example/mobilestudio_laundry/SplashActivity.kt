package com.example.mobilestudio_laundry

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try{
            Thread.sleep(3000)
            var splashIntent = Intent(this,LoginActivity::class.java)
            startActivity(splashIntent)
            finish()
        } catch (e : InterruptedException){
            e.printStackTrace()
        }
    }
}
