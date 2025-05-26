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

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.androidx.stockchart.sample.sample1.Sample1Activity
import com.androidx.stockchart.sample.sample2.Sample2Activity
import com.androidx.stockchart.sample.sample3.Sample3Activity
import com.androidx.stockchart.sample.sample4.Sample4Activity
import com.androidx.stockchart.util.ResourceUtil
import com.dianping.logan.Logan
import com.dianping.logan.LoganConfig
import com.dianping.logan.SendLogCallback
import java.io.File
import java.sql.Date
import java.text.SimpleDateFormat


/**
 * @author hai
 * @version 创建时间: 2021/4/4
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ResourceUtil.init(this)
        setContentView(R.layout.activity_main)
        val config: LoganConfig? = LoganConfig.Builder()
            .setCachePath(filesDir.absolutePath)
            .setPath(
                (getExternalFilesDir(null)!!.absolutePath
                        + File.separator + "logan_v1")
            )
            .setDay(System.currentTimeMillis())
            .setEncryptKey16("0123456789012345".toByteArray())
            .setEncryptIV16("0123456789012345".toByteArray())
            .build()
        Logan.init(config)
    }

    fun sample1(v: View) {
        startActivity(Intent(this, Sample1Activity::class.java))
    }

    fun sample2(view: View) {
        startActivity(Intent(this, Sample2Activity::class.java))
    }

    fun sample3(view: View) {
        startActivity(Intent(this, Sample3Activity::class.java))
    }
    fun sample4(view: View) {
        startActivity(Intent(this, Sample4Activity::class.java))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        ResourceUtil.init(this)
    }

    fun sample5(view: View) {
//        Logan.w("logTest",10)
//        Logan.f()
        val url = "http://18.166.99.210:18233/logan-web/logan/upload.json"
        Logan.s(
            url,
            SimpleDateFormat("yyyy-MM-dd").format(java.util.Date()),
            "com.test.appid",
            "testUnionid",
            "testdDviceId",
            "testBuildVersion",
            "testAppVersion"
        ) { statusCode, data ->
            val resultData = if (data != null) String(data) else ""
            Log.d(
                "TAG",
                "upload result, httpCode: " + statusCode + ", details: " + resultData
            )
        }
    }

    fun sample6(view: View) {
        throw RuntimeException("test Exception")
    }
}