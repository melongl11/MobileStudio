package com.example.mobilestudio_laundry

import android.os.Handler

/**
 * Created by melon on 2017-11-23.
 */
class ServiceThread(var handler: Handler) : Thread() {
    private var isRun = true;

    fun stopForever() {
        synchronized(this) {
            this.isRun = false;
        }
    }

    override fun run() {
        super.run()

        handler.sendEmptyMessage(0)
        try {
            Thread.sleep(10000)
        } catch (e:Exception) {}
    }
}