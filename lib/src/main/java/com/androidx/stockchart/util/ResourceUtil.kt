package com.androidx.stockchart.util

import android.content.Context
import androidx.annotation.ColorRes
import java.lang.ref.WeakReference

object ResourceUtil {
    var mContext = WeakReference<Context>(null)

    fun init(context: Context){
        mContext = WeakReference(context)
    }
    fun getColor(@ColorRes color: Int)=mContext.get()!!.getColor(color)
}