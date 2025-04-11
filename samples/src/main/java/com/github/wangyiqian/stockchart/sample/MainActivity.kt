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

package com.github.stockchart.sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.stockchart.sample.R.*
import com.github.stockchart.sample.sample1.Sample1Activity
import com.github.stockchart.sample.sample2.Sample2Activity
import com.github.stockchart.sample.sample3.Sample3Activity
import com.github.stockchart.sample.sample4.Sample4Activity

/**
 * @author hai
 * @version 创建时间: 2021/4/4
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
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
}