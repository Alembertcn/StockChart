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

package com.wb.stockchart.sample.sample4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wb.stockchart.childchart.timebar.TimeBarConfig
import com.wb.stockchart.childchart.timebar.TimeBarConfig.Type.DayTime
import com.wb.stockchart.childchart.timebar.TimeBarConfig.Type.FiveDays
import com.wb.stockchart.childchart.timebar.TimeBarConfig.Type.FiveMinutes
import com.wb.stockchart.childchart.timebar.TimeBarConfig.Type.FiveYears
import com.wb.stockchart.childchart.timebar.TimeBarConfig.Type.Month
import com.wb.stockchart.childchart.timebar.TimeBarConfig.Type.OneMinute
import com.wb.stockchart.childchart.timebar.TimeBarConfig.Type.Quarter
import com.wb.stockchart.childchart.timebar.TimeBarConfig.Type.SixtyMinutes
import com.wb.stockchart.childchart.timebar.TimeBarConfig.Type.Week
import com.wb.stockchart.childchart.timebar.TimeBarConfig.Type.YTD
import com.wb.stockchart.childchart.timebar.TimeBarConfig.Type.Year
import com.wb.stockchart.entities.Highlight
import com.wb.stockchart.index.Index
import com.wb.stockchart.listener.OnHighlightListener
import com.wb.stockchart.listener.OnLoadMoreListener
import com.wb.stockchart.sample.DataMock
import com.wb.stockchart.sample.R
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import wb.lib.module_chart.ChartFragment
import wb.lib.module_chart.Period

/**
 * @author hai
 * @version 创建时间: 2021/3/6
 */
class Sample4Activity : AppCompatActivity(), OnLoadMoreListener, OnHighlightListener {
    val fragment by lazy {
        ChartFragment().apply {
            onLoadMoreListener = this@Sample4Activity
            onHighlightListener = this@Sample4Activity
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample4)

        supportFragmentManager.beginTransaction().replace(R.id.flContent, fragment).commitAllowingStateLoss()
        MainScope().launch {
            fragment.period.collectLatest {
                loadData(0,it)
            }
        }
//        fragment.changeIndexType(Index.MA())
//        fragment.hasIndexType(Index.MA::class)
    }

    var isLoading = true
    var currentPage = 0
    // 加载模拟数据
    private fun loadData(page: Int = 0, period: Period) {
        isLoading = true
        when (period) {
            Period.DAY -> {
                DataMock.loadDayData(this, page) { list ->
                    fragment.doAfterLoad(list, timeBarType = TimeBarConfig.Type.Day())
                }
            }
            Period.FIVE_DAYS -> {
                DataMock.loadFiveDayData(this) { list ->
                    fragment.doAfterLoad(list, timeBarType = FiveDays())

                }
            }
            Period.WEEK -> {
                DataMock.loadWeekData(this, page) { list ->
                    fragment.doAfterLoad(list, timeBarType = Week())

                }
            }
            Period.MONTH -> {
                DataMock.loadMonthData(this, page) { list ->
                    fragment.doAfterLoad(list, timeBarType = Month())
                }
            }
            Period.QUARTER -> {
                DataMock.loadQuarterData(this) { list ->
                    fragment.doAfterLoad(list, timeBarType = Quarter())
                }
            }
            Period.YEAR -> {
                DataMock.loadYearData(this) { list ->
                    fragment.doAfterLoad(list, timeBarType = Year())
                }
            }
            Period.FIVE_YEARS -> {
                DataMock.loadFiveYearData(this) { list ->
                    fragment.doAfterLoad(list, timeBarType = FiveYears())
                }
            }
            Period.YTD -> {
                DataMock.loadYTDData(this) { list ->
                    fragment.doAfterLoad(list, timeBarType = YTD())
                }
            }
            Period.ONE_MINUTE -> {
                DataMock.loadOneMinuteData(this, page) { list ->
                    fragment.doAfterLoad(list, timeBarType = OneMinute())
                }
            }
            Period.THIRTY_MINUTES,Period.FIVE_MINUTES -> {
                DataMock.loadFiveMinutesData(this, page) { list ->
                    fragment.doAfterLoad(list, timeBarType = FiveMinutes())
                }
            }
            Period.SIXTY_MINUTES -> {
                DataMock.loadSixtyMinutesData(this, page) { list ->
                    fragment.doAfterLoad(list, timeBarType = SixtyMinutes())
                }
            }
            Period.DAY_TIME -> {
                DataMock.loadDayTimeData(this) { list ->
                    fragment.doAfterLoad(list, preClosePrice = 623.0f, timeBarType = DayTime(totalPoint = 280,  labelParis = mapOf(0 to "9:00",380 to "15:00")))
                }
            }
        }
        currentPage = page
    }

    override fun onLeftLoadMore() {
        loadData(++currentPage,fragment.period.value)
    }

    override fun onRightLoadMore() {
        loadData(--currentPage,fragment.period.value)
    }

    override fun onHighlightBegin() {
    }

    override fun onHighlightEnd() {
    }

    override fun onHighlight(highlight: Highlight) {
    }

}

object KChartType{
    // k线类型
    const val K_1MIN: Int = 5
    const val K_5MIN: Int = 7
    const val K_15MIN: Int = 8
    const val K_30MIN: Int = 9
    const val K_60MIN: Int = 10
    const val K_1DAY: Int = 6
    const val K_1WEEK: Int = 11
    const val K_1MONTH: Int = 4

    const val ONE_DAY_DARK: Int = 12 //暗盘

    const val ONE_DAY: Int = 0
    const val ONE_DAY_PRE: Int = 2 //盘前
    const val ONE_DAY_AFTER: Int = 3 //盘后
    const val FIVE_DAY: Int = 1
}

object KSubChartType{
    const val K_VOLUME:Int=1
    const val MACD :Int=2
    const val KDJ :Int=3
    const val RSI:Int=4
    const val MA:Int=5
    const val BOLL:Int=6
}