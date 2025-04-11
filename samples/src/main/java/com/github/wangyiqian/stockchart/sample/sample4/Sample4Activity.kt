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

package com.github.wangyiqian.stockchart.sample.sample4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.wangyiqian.stockchart.childchart.timebar.TimeBarConfig
import com.github.wangyiqian.stockchart.sample.DataMock
import com.github.wangyiqian.stockchart.sample.R
import com.github.wangyiqian.stockchart.sample.sample2.Sample2Activity
import wb.lib.module_chart.ChartFragment
import wb.lib.module_chart.Period

/**
 * @author hai
 * @version 创建时间: 2021/3/6
 */
class Sample4Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample4)
        supportFragmentManager.beginTransaction().replace(R.id.flContent, ChartFragment().apply { 
            dataLoader = {page,period,cb->
                when (period) {
                    Period.DAY -> {
                        DataMock.loadDayData(this@Sample4Activity, page) { list ->
                            cb.invoke(list)
                        }
                    }
                    Period.FIVE_DAYS -> {
                        DataMock.loadFiveDayData(this@Sample4Activity) { list ->
                            cb.invoke(list)
                        }
                    }
                    Period.WEEK -> {
                        DataMock.loadWeekData(this@Sample4Activity, page) { list ->
                            cb.invoke(list)
                        }
                    }
                    Period.MONTH -> {
                        DataMock.loadMonthData(this@Sample4Activity, page) { list ->
                            cb.invoke(list)
                        }
                    }
                    Period.QUARTER -> {
                        DataMock.loadQuarterData(this@Sample4Activity) { list ->
                            cb.invoke(list)
                        }
                    }
                    Period.YEAR -> {
                        DataMock.loadYearData(this@Sample4Activity) { list ->
                            cb.invoke(list)
                        }
                    }
                    Period.FIVE_YEARS -> {
                        DataMock.loadFiveYearData(this@Sample4Activity) { list ->
                            cb.invoke(list)
                        }
                    }
                    Period.YTD -> {
                        DataMock.loadYTDData(this@Sample4Activity) { list ->
                            cb.invoke(list)
                        }
                    }
                    Period.ONE_MINUTE -> {
                        DataMock.loadOneMinuteData(this@Sample4Activity, page) { list ->
                            cb.invoke(list)
                        }
                    }
                    Period.THIRTY_MINUTES, Period.FIVE_MINUTES -> {
                        DataMock.loadFiveMinutesData(this@Sample4Activity, page) { list ->
                            cb.invoke(list)
                        }
                    }
                    Period.SIXTY_MINUTES -> {
                        DataMock.loadSixtyMinutesData(this@Sample4Activity, page) { list ->
                            cb.invoke(list)
                        }
                    }
                    Period.DAY_TIME -> {
                        DataMock.loadDayTimeData(this@Sample4Activity) { list ->
                            cb.invoke(list)
                        }
                    }
                }
            }
        }).commitAllowingStateLoss()
    }
}