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

package com.androidx.stockchart.sample

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import java.security.MessageDigest
import java.text.DecimalFormat

/**
 * @author hai
 * @version 创建时间: 2021/4/4
 */
object Util {

    private val numberDecimalFormat = DecimalFormat().apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }

    /**
     * 格式化成交量
     */
    fun formatVolume(volume: Long): String {
        var magnitude: Float
        var unit = ""
        when {
            volume > 1_0000_0000_0000f -> {
                magnitude = 1_0000_0000_0000f
                unit = "万亿"
            }
            volume > 1_0000_0000f -> {
                magnitude = 1_0000_0000f
                unit = "亿"
            }
            volume > 1_0000f -> {
                magnitude = 1_0000f
                unit = "万"
            }
            else -> {
                magnitude = 1f
                unit = ""
            }
        }

        return "${numberDecimalFormat.format(volume / magnitude)}${unit}股"
    }

    /**
     * 格式化涨跌幅
     */
    fun formatChangeRatio(new: Float, old: Float): String {
        if (old == 0f) return "——"
        val ratio = (new - old) / old * 100
        return "${if (new > old) "+" else ""}${numberDecimalFormat.format(ratio)}"
    }


    fun getCallerSignatureHash(packageName: String,packageManager: PackageManager): String? {
        return try {
            val packageInfo: PackageInfo = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            )
            val signatures = packageInfo.signatures
            if (signatures.isEmpty()) return null

            // 取第一个签名计算哈希值
            calcSha256(signatures[0].toByteArray())
        } catch (e: Exception) {
            null
        }
    }

    private fun calcSha256(bytes: ByteArray): String {
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            digest.joinToString(":") { "%02X".format(it) }
        } catch (e: Exception) {
            ""
        }
    }

}