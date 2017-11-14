package com.example.mobilestudio_laundry

import android.graphics.Bitmap
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool

/**
 * Created by kim on 2017-11-14.
 */
class CustomBitmapPool : BitmapPool {
    override fun getMaxSize() :Int {
        return 0;
    }

    override fun setSizeMultiplier(sizeMultiplier : Float) {

    }

    override fun put(bitmap : Bitmap) : Boolean {
        return false;
    }

    override fun get(width : Int,height : Int,  config : Bitmap.Config) : Bitmap?{
        return null;
    }

    override fun getDirty(width : Int, height : Int, config : Bitmap.Config ) : Bitmap? {
        return null;
    }

    override fun clearMemory() {

    }

    override fun trimMemory(level : Int) {

    }

}
