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

package com.androidx.stockchart.sample.sample1

import android.os.Bundle
import com.androidx.stockchart.StockChartConfig
import com.androidx.stockchart.childchart.kchart.KChartConfig
import com.androidx.stockchart.childchart.kchart.KChartFactory
import com.androidx.stockchart.childchart.timebar.TimeBarConfig
import com.androidx.stockchart.childchart.timebar.TimeBarFactory
import com.androidx.stockchart.sample.DataMock
import com.androidx.stockchart.sample.databinding.ActivitySample1Binding

/**
 * @author hai
 * @version 创建时间: 2021/2/26
 */
class Sample1Activity : androidx.appcompat.app.AppCompatActivity() {
    lateinit var binding: ActivitySample1Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySample1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 总配置
        val stockChartConfig = StockChartConfig()
        binding.stockChart.setConfig(stockChartConfig)

        // K线图的配置与工厂
        val kChartConfig = KChartConfig()
        val kChartFactory = KChartFactory(stockChart = binding.stockChart, childChartConfig = kChartConfig)

        // 时间条图的配置与工厂
        val timeBarConfig = TimeBarConfig()
        val timeBarFactory =
            TimeBarFactory(stockChart = binding.stockChart, childChartConfig = timeBarConfig)

        // 将需要显示的子图的工厂加入全局配置
        stockChartConfig.addChildCharts(kChartFactory, timeBarFactory)

        // 加载模拟数据
        DataMock.loadDayData(this, 0) { kEntities: List<com.androidx.stockchart.entities.IKEntity> ->

            // 初始显示最后50条数据
            val pageSize = 50

            // 设置加载到的数据
            stockChartConfig.setKEntities(
                kEntities,
                showStartIndex = (kEntities.size - pageSize).coerceIn(0,kEntities.size-1),
                showEndIndex = kEntities.size - 1
            )

            // 通知更新K线图
            binding.stockChart.notifyChanged()
        }

    }
}