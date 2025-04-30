/*
 * Copyright 2025 hai
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 *
 */

package com.androidx.stockchart.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.androidx.stock_chart.R
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.ConcurrentHashMap

/**
 * @author hai
 * @version 创建时间: 2021/2/26
 */
object NumberFormatUtil {

    private const val DEFAULT_POINT=3
    private val FORMAT_CACHE = ConcurrentHashMap<String,ThreadLocal<NumberFormat>>()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getFormat(minimumFractionDigits:Int=DEFAULT_POINT,maximumFractionDigits:Int=DEFAULT_POINT)=FORMAT_CACHE.computeIfAbsent("$minimumFractionDigits|$maximumFractionDigits") {
        ThreadLocal.withInitial {
            DecimalFormat.getInstance().also {
                it.isGroupingUsed = true
                it.maximumFractionDigits = maximumFractionDigits
                it.minimumFractionDigits = minimumFractionDigits
            }
        }
    }.get()

    @RequiresApi(Build.VERSION_CODES.O)
    @Synchronized
    fun formatPrice(price: Float,minimumFractionDigits:Int=DEFAULT_POINT,maximumFractionDigits:Int=DEFAULT_POINT): String =getFormat(minimumFractionDigits, maximumFractionDigits)?.format(price)?:"- -"

    /**
     * 格式化成交量
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatVolume(volume: Long?): String {
        if(volume == null) return ""
        var magnitude: Float
        var unit = ""
        when {
            volume > 1_0000_0000_0000f -> {
                magnitude = 1_0000_0000_0000f
                unit = ResourceUtil.getString(R.string.billions_w)
            }
            volume > 1_0000_0000f -> {
                magnitude = 1_0000_0000f
                unit = ResourceUtil.getString(R.string.billions)
            }
            volume > 1_0000f -> {
                magnitude = 1_0000f
                unit = ResourceUtil.getString(R.string.millions)
            }
            else -> {
                magnitude = 1f
                unit = ""
            }
        }

//        return ResourceUtil.getString(R.string.vol_info_format,getFormat(2, 2)?.format(volume / magnitude)?:"- -",unit,ResourceUtil.getString(R.string.gu))
        return ResourceUtil.getString(R.string.vol_info_format,getFormat(DEFAULT_POINT, DEFAULT_POINT)?.format(volume / magnitude)?:"- -",unit,"")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatAmount(amount:BigDecimal?):String{
        if(amount == null) return ""
        var magnitude: BigDecimal
        var unit = ""
        when {
            amount > 1_0000_0000_0000f.toBigDecimal() -> {
                magnitude = 1_0000_0000_0000f.toBigDecimal()
                unit = ResourceUtil.getString(R.string.billions_w)
            }
            amount > 1_0000_0000f.toBigDecimal() -> {
                magnitude = 1_0000_0000f.toBigDecimal()
                unit = ResourceUtil.getString(R.string.billions)
            }
            amount > 1_0000f.toBigDecimal() -> {
                magnitude = 1_0000f.toBigDecimal()
                unit = ResourceUtil.getString(R.string.millions)
            }
            else -> {
                magnitude = BigDecimal.ONE
                unit = ""
            }
        }
        return "${getFormat(DEFAULT_POINT, DEFAULT_POINT)?.format(amount / magnitude)?:"- -"}$unit"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatPresent(present:Float?):String{
        return present?.let { "${formatPrice(it * 100 ,2,2)}%"   } ?: "- -"
    }
    /**
     * 格式化涨跌幅
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatChangeRatio(new: Float, old: Float): String {
        if (old == 0f) return "——"
        val ratio = (new - old) / old * 100
        return "${if (new > old) "+" else ""}${formatPresent(ratio)}"
    }
}