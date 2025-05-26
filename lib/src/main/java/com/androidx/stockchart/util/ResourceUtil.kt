package com.androidx.stockchart.util

import android.content.Context
import android.util.TypedValue
import androidx.annotation.ColorRes
import androidx.annotation.StringDef
import androidx.annotation.StringRes
import java.lang.ref.WeakReference

object ResourceUtil {
    var mContext = WeakReference<Context>(null)

    fun init(context: Context){
        mContext = WeakReference(context)
    }
    fun getColor(@ColorRes color: Int)=mContext.get()!!.getColor(color)
    fun getString(@StringRes string: Int,vararg args:String)=mContext.get()!!.getString(string,*args)

    fun dp2pix(dpVal: Float): Float{
       return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            dpVal, mContext.get()!!.resources.displayMetrics
        );
    }
}