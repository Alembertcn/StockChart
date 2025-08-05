package com.androidx.stockchart.sample
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import android.text.TextUtils
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * @author Hai
 * @date   2025/7/14 11:55
 * @desc
 */
object SigningUtils {

    val share256Maps= mapOf(
        "com.infast.stock" to "C5:7A:4A:21:C2:5C:7D:87:E6:90:E1:C3:9B:7F:06:A2:BC:E7:03:E0:E2:08:7A:F9:26:3B:31:AC:31:F4:15:CF",
        "com.wealthbroker.stock" to "C5:7A:4A:21:C2:5C:7D:87:E6:90:E1:C3:9B:7F:06:A2:BC:E7:03:E0:E2:08:7A:F9:26:3B:31:AC:31:F4:15:CF",
        )

    /**
     * 获取应用的签名信息的SHA256哈希值（冒号分隔的十六进制字符串）
     *
     * @param context Context对象
     * @param packageName 包名
     * @return SHA256哈希字符串，如 "C5:7A:4A:21:..." 或 null（如果获取失败）
     */
    fun getAppSignatureSha256(context: Context, packageName: String?): String? {
        if (TextUtils.isEmpty(packageName)) {
            return null
        }
        return try {
            // 获取PackageInfo，注意兼容不同API版本
            val packageInfo: PackageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(
                    packageName!!,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(packageName!!, PackageManager.GET_SIGNATURES)
            }

            // 提取签名数组
            val signatures: Array<Signature> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val signingInfo = packageInfo.signingInfo
                if (signingInfo.hasMultipleSigners()) {
                    signingInfo.apkContentsSigners
                } else {
                    signingInfo.signingCertificateHistory
                }?.map { Signature(it.toByteArray()) }?.toTypedArray() ?: return null
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }

            // 如果有多个签名，取第一个（通常情况只有一个）
            if (signatures.isEmpty()) return null
            val signature = signatures[0]

            // 计算SHA256哈希
            val digest = MessageDigest.getInstance("SHA-256").digest(signature.toByteArray())
            // 转换为十六进制并添加冒号分隔
            toHexWithColon(digest)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 将字节数组转换为冒号分隔的十六进制字符串
     *
     * @param bytes 字节数组
     * @return 例如 "C5:7A:4A:21:..."
     */
    private fun toHexWithColon(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (i in bytes.indices) {
            sb.append(String.format("%02X", bytes[i]))
            if (i < bytes.size - 1) {
                sb.append(':')
            }
        }
        return sb.toString()
    }


    fun isAppInstalled(context: Context, packageName: String): Boolean {
        try {
            // 获取PackageManager
            val pm = context.packageManager
            // 尝试获取应用信息，如果应用不存在会抛出NameNotFoundException
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
    }
}