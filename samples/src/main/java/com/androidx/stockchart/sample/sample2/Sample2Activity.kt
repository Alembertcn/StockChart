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

package com.androidx.stockchart.sample.sample2

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidx.stockchart.StockChartConfig
import com.androidx.stockchart.childchart.base.HighlightLabelConfig
import com.androidx.stockchart.childchart.kchart.KChartConfig
import com.androidx.stockchart.childchart.kchart.KChartFactory
import com.androidx.stockchart.childchart.kdjchart.KdjChartConfig
import com.androidx.stockchart.childchart.kdjchart.KdjChartFactory
import com.androidx.stockchart.childchart.macdchart.MacdChartConfig
import com.androidx.stockchart.childchart.macdchart.MacdChartFactory
import com.androidx.stockchart.childchart.rskchart.RsiChartConfig
import com.androidx.stockchart.childchart.rskchart.RsiChartFactory
import com.androidx.stockchart.childchart.timebar.TimeBarConfig
import com.androidx.stockchart.childchart.timebar.TimeBarConfig.Type.*
import com.androidx.stockchart.childchart.timebar.TimeBarFactory
import com.androidx.stockchart.childchart.volumechart.VolumeChartConfig
import com.androidx.stockchart.childchart.volumechart.VolumeChartFactory
import com.androidx.stockchart.index.Index
import com.androidx.stockchart.listener.OnHighlightListener
import com.androidx.stockchart.listener.OnLoadMoreListener
import com.androidx.stockchart.sample.DataMock
import com.androidx.stockchart.sample.Util
import com.androidx.stockchart.sample.sample2.custom.CustomChartConfig
import com.androidx.stockchart.sample.sample2.custom.CustomChartFactory
import com.androidx.stockchart.util.DimensionUtil
import com.androidx.stockchart.util.NumberFormatUtil
import com.androidx.stockchart.entities.FLAG_EMPTY
import com.androidx.stockchart.entities.Highlight
import com.androidx.stockchart.entities.IKEntity
import com.androidx.stockchart.entities.containFlag
import com.androidx.stockchart.sample.databinding.ActivitySample2Binding
import wb.lib.module_chart.Period
import wb.lib.module_chart.R
import kotlin.math.max

/**
 * @author hai
 * @version 创建时间: 2021/3/6
 */
class Sample2Activity : AppCompatActivity() {

    private lateinit var binding: ActivitySample2Binding
    private var periodOptionButtons = mutableMapOf<View, Period>()
    private var kChartTypeOptionButtons = mutableMapOf<View, KChartConfig.KChartType>()
    private var indexOptionButton = mutableMapOf<View, Index>()

    private var period = Period.DAY
    private var kChartType: KChartConfig.KChartType = KChartConfig.KChartType.CANDLE()
    private var kChartIndex: Index? = Index.MA()
    private var currentPage = 0

    // 总配置
    private val stockChartConfig = StockChartConfig()

    // K线图工厂与配置
    private var kChartFactory: KChartFactory? = null
    private val kChartConfig = KChartConfig(kChartType = kChartType, index = kChartIndex)

    // 成交量图工厂与配置
    private var volumeChartFactory: VolumeChartFactory? = null
    private val volumeChartConfig = VolumeChartConfig()

    // 时间条图工厂与配置
    private var timeBarFactory: TimeBarFactory? = null
    private val timeBarConfig = TimeBarConfig()

    // macd指标图工厂与配置
    private var macdChartFactory: MacdChartFactory? = null
    private val macdChartConfig = MacdChartConfig()

    // kdj指标图工厂与配置
    private var kdjChartFactory: KdjChartFactory? = null
    private val kdjChartConfig = KdjChartConfig()

    // rsi指标图工厂与配置
    private var rsiChartFactory: RsiChartFactory? = null
    private val rsiChartConfig = RsiChartConfig()

    // 自定义示例图与配置
    private var customChartFactory: CustomChartFactory? = null
    private var customChartConfig = CustomChartConfig()

    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySample2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // StockChart初始化
        initStockChart()

        // 各选项按钮初始化
        initPeriodButtons()
        initKChartTypeButtons()
        initIndexButtons()
        initCustomChartButtons()

        // 切换到到日K，首次加载数据
        changePeriod(Period.DAY)
    }

    /**
     * StockChart初始化
     */
    private fun initStockChart() {

        // 初始化设置各种子图
        initKChart()
        initVolumeChart()
        initTimeBar()
        initMacdChart()
        initKdjChart()
        initRsiChart()
        initCustomChart()

        stockChartConfig.apply {
            // 将需要显示的子图的工厂添加进StockChart配置
            addChildCharts(
                kChartFactory!!,
                volumeChartFactory!!,
                timeBarFactory!!,
                macdChartFactory!!,
                kdjChartFactory!!,
                rsiChartFactory!!,
                customChartFactory!!
            )

            // 最大缩放比例
            scaleFactorMax = 2f

            // 最小缩放比例
            scaleFactorMin = 0.5f

            // 网格线设置
            gridVerticalLineCount = 3
            gridHorizontalLineCount = 4

            // 设置滑动到左边界加载更多
            onLoadMoreListener = object : OnLoadMoreListener {
                override fun onLeftLoadMore() {
                    if (!isLoading) {
                        if (period != Period.FIVE_DAYS
                            && period != Period.QUARTER
                            && period != Period.YEAR
                            && period != Period.FIVE_YEARS
                            && period != Period.YTD
                        ) {
                            loadData(page = currentPage + 1, period = period)
                        }
                    }
                }

                override fun onRightLoadMore() {}
            }
        }

        // 绑定配置
        binding.stockChart.setConfig(stockChartConfig)
    }

    /**
     * K线图初始化
     */
    private fun initKChart() {
        kChartFactory = KChartFactory(binding.stockChart, kChartConfig)

        kChartConfig.apply {

            // 指标线宽度
            indexStrokeWidth = DimensionUtil.dp2px(this@Sample2Activity, 0.5f).toFloat()

            // 监听长按信息
            onHighlightListener = object : OnHighlightListener {
                override fun onHighlightBegin() {}

                override fun onHighlightEnd() {
                    binding.tvHighlightInfo.text = ""
                }

                override fun onHighlight(highlight: Highlight) {
                    val idx = highlight.getIdx()
                    val kEntities = stockChartConfig.kEntities
                    var showContent = ""

                    if (idx in kEntities.indices) {
                        val kEntity = kEntities[idx]
                        if (kEntity.containFlag(FLAG_EMPTY)) {
                            showContent = ""
                        } else if (kChartType is KChartConfig.KChartType.LINE || kChartType is KChartConfig.KChartType.MOUNTAIN) {
                            val firstIdx = binding.stockChart.findFirstNotEmptyKEntityIdxInDisplayArea()
                            val price =
                                "最新价:${NumberFormatUtil.formatPrice(kEntity.getClosePrice())}"
                            var changeRatio = "涨跌幅:——"
                            firstIdx?.let {
                                changeRatio = "涨跌幅:${
                                    Util.formatChangeRatio(
                                        kEntity.getClosePrice(),
                                        kEntities[it].getClosePrice()
                                    )
                                }"
                            }
                            val volume = "成交量:${Util.formatVolume(kEntity.getVolume())}"

                            showContent = "$price，$changeRatio，$volume"
                        } else {
                            val open = "开盘价:${NumberFormatUtil.formatPrice(kEntity.getOpenPrice())}"
                            val close =
                                "收盘价:${NumberFormatUtil.formatPrice(kEntity.getClosePrice())}"
                            val high = "最高价:${NumberFormatUtil.formatPrice(kEntity.getHighPrice())}"
                            val low = "最低价${NumberFormatUtil.formatPrice(kEntity.getLowPrice())}"
                            val changeRatio =
                                "涨跌幅:${
                                    Util.formatChangeRatio(
                                        kEntity.getClosePrice(),
                                        kEntity.getOpenPrice()
                                    )
                                }"
                            val volume = "成交量:${Util.formatVolume(kEntity.getVolume())}"

                            showContent = "$open，$close，$high，$low，$changeRatio，$volume"
                        }

                    }

                    // 长按信息显示到界面
                    binding.tvHighlightInfo.text = showContent
                }
            }

            // 图高度
            height = DimensionUtil.dp2px(this@Sample2Activity, 170f)

            // 左侧标签设置
            leftLabelConfig = KChartConfig.LabelConfig(
                5,
                { "${NumberFormatUtil.formatPrice(it)}" },
                DimensionUtil.sp2px(this@Sample2Activity, 8f).toFloat(),
                Color.parseColor("#E4E4E4"),
                DimensionUtil.dp2px(this@Sample2Activity, 10f).toFloat(),
                DimensionUtil.dp2px(this@Sample2Activity, 30f).toFloat(),
                DimensionUtil.dp2px(this@Sample2Activity, 30f).toFloat()
            )

            // 长按左侧标签配置
            highlightLabelLeft =
                HighlightLabelConfig(
                    textSize = DimensionUtil.sp2px(this@Sample2Activity, 10f).toFloat(),
                    bgColor = Color.parseColor("#A3A3A3"),
                    padding = DimensionUtil.dp2px(this@Sample2Activity, 5f).toFloat()
                )

            // 空心蜡烛边框宽度
            hollowChartLineStrokeWidth = DimensionUtil.dp2px(this@Sample2Activity, 1f).toFloat()
        }
    }

    /**
     * 成交量图初始化
     */
    private fun initVolumeChart() {
        volumeChartFactory = VolumeChartFactory(binding.stockChart, volumeChartConfig)

        volumeChartConfig.apply {
            // 图高度
            height = DimensionUtil.dp2px(this@Sample2Activity, 40f)


            // 长按左侧标签配置
            highlightLabelLeft = HighlightLabelConfig(
                textSize = DimensionUtil.sp2px(this@Sample2Activity, 10f).toFloat(),
                bgColor = Color.parseColor("#A3A3A3"),
                padding = DimensionUtil.dp2px(this@Sample2Activity, 5f).toFloat(),
                textFormat = { volume ->
                    Util.formatVolume(volume = volume.toLong())
                }
            )

            // 柱子空心时的线条宽度
            hollowChartLineStrokeWidth = DimensionUtil.dp2px(this@Sample2Activity, 1f).toFloat()

        }

    }

    /**
     * 时间条图初始化
     */
    private fun initTimeBar() {
        timeBarFactory = TimeBarFactory(binding.stockChart, timeBarConfig)

        timeBarConfig.apply {
            // 背景色（时间条这里不像显示网格线，加个背景色覆盖掉）
            backGroundColor = stockChartConfig.backgroundColor

            // 长按标签背景色
            highlightLabelBgColor = Color.parseColor("#A3A3A3")
        }

    }

    /**
     * macd指标图初始化
     */
    private fun initMacdChart() {
        macdChartFactory = MacdChartFactory(binding.stockChart, macdChartConfig)

        macdChartConfig.apply {
            // 图高度
            height = DimensionUtil.dp2px(this@Sample2Activity, 80f)

            // 长按左侧标签配置
            highlightLabelLeft = HighlightLabelConfig(
                textSize = DimensionUtil.sp2px(this@Sample2Activity, 10f).toFloat(),
                bgColor = Color.parseColor("#A3A3A3"),
                padding = DimensionUtil.dp2px(this@Sample2Activity, 5f).toFloat()
            )
        }
    }

    /**
     * kdj指标图初始化
     */
    private fun initKdjChart() {
        kdjChartFactory = KdjChartFactory(binding.stockChart, kdjChartConfig)

        kdjChartConfig.apply {
            // 图高度
            height = DimensionUtil.dp2px(this@Sample2Activity, 80f)

            // 长按左侧标签配置
            highlightLabelLeft = HighlightLabelConfig(
                textSize = DimensionUtil.sp2px(this@Sample2Activity, 10f).toFloat(),
                bgColor = Color.parseColor("#A3A3A3"),
                padding = DimensionUtil.dp2px(this@Sample2Activity, 5f).toFloat()
            )
        }
    }

    /**
     * rsi指标图初始化
     */
    private fun initRsiChart() {
        rsiChartFactory = RsiChartFactory(binding.stockChart, rsiChartConfig)

        rsiChartConfig.apply {
            // 图高度
            height = DimensionUtil.dp2px(this@Sample2Activity, 80f)

            // 长按左侧标签配置
            highlightLabelLeft = HighlightLabelConfig(
                textSize = DimensionUtil.sp2px(this@Sample2Activity, 10f).toFloat(),
                bgColor = Color.parseColor("#A3A3A3"),
                padding = DimensionUtil.dp2px(this@Sample2Activity, 5f).toFloat()
            )
        }
    }

    /**
     * 自定义示例图初始化
     */
    private fun initCustomChart() {
        customChartFactory = CustomChartFactory(binding.stockChart, customChartConfig)
        customChartConfig.apply {
            height = DimensionUtil.dp2px(this@Sample2Activity, 50f)
            bigLabel = "这是自定义子图示例"
        }
    }

    // 加载模拟数据
    private fun loadData(page: Int = 0, period: Period) {
        isLoading = true

        fun doAfterLoad(
            kEntities: List<IKEntity>,
            initialPageSize: Int?,
            timeBarType: TimeBarConfig.Type
        ) {
            if (kEntities.isNotEmpty()) {
                // 设置数据
                if (page == 0) {
                    if (initialPageSize != null) {
                        stockChartConfig.setKEntities(
                            kEntities,
                            max(kEntities.size - initialPageSize,0),
                            kEntities.size - 1
                        )
                    } else {
                        stockChartConfig.setKEntities(kEntities)
                    }

                } else {
                    stockChartConfig.appendLeftKEntities(kEntities)
                }

                // 设置时间条样式
                timeBarConfig.type = timeBarType

                // 通知更新
                binding.stockChart.notifyChanged()
                currentPage = page
            } else {
                Toast.makeText(this, "没有更多数据了！", Toast.LENGTH_SHORT).show()
            }
            isLoading = false
        }

        when (period) {
            Period.DAY -> {
                DataMock.loadDayData(this, page) { list ->
                    doAfterLoad(list, 60, Day())
                }
            }
            Period.FIVE_DAYS -> {
                DataMock.loadFiveDayData(this) { list ->
                    doAfterLoad(list, null, FiveDays())
                }
            }
            Period.WEEK -> {
                DataMock.loadWeekData(this, page) { list ->
                    doAfterLoad(list, 60, Week())
                }
            }
            Period.MONTH -> {
                DataMock.loadMonthData(this, page) { list ->
                    doAfterLoad(list, 60, Month())
                }
            }
            Period.QUARTER -> {
                DataMock.loadQuarterData(this) { list ->
                    doAfterLoad(list, null, Quarter())
                }
            }
            Period.YEAR -> {
                DataMock.loadYearData(this) { list ->
                    doAfterLoad(list, null, Year())
                }
            }
            Period.FIVE_YEARS -> {
                DataMock.loadFiveYearData(this) { list ->
                    doAfterLoad(list, null, FiveYears())
                }
            }
            Period.YTD -> {
                DataMock.loadYTDData(this) { list ->
                    doAfterLoad(list, null, YTD())
                }
            }
            Period.ONE_MINUTE -> {
                DataMock.loadOneMinuteData(this, page) { list ->
                    doAfterLoad(list, 60, OneMinute())
                }
            }
            Period.THIRTY_MINUTES,Period.FIVE_MINUTES -> {
                DataMock.loadFiveMinutesData(this, page) { list ->
                    doAfterLoad(list, 60, FiveMinutes())
                }
            }
            Period.SIXTY_MINUTES -> {
                DataMock.loadSixtyMinutesData(this, page) { list ->
                    doAfterLoad(list, 60, SixtyMinutes())
                }
            }
            Period.DAY_TIME -> {
                DataMock.loadDayTimeData(this) { list ->
                    doAfterLoad(list, null, DayTime())
                }
            }
        }
    }

    private fun changePeriod(period: Period) {
        when (period) {
            Period.DAY_TIME, Period.FIVE_DAYS -> {
                stockChartConfig.apply {
                    scaleAble = false
                    scrollAble = false
                    overScrollAble = false
                }
                kChartConfig.apply {
                    showAvgLine = true // 显示分时均线
                    index = null
                    kChartType = KChartConfig.KChartType.LINE()
                }
            }
            Period.YEAR, Period.QUARTER, Period.FIVE_YEARS -> {
                stockChartConfig.apply {
                    scaleAble = true
                    scrollAble = true
                    overScrollAble = false
                }
                kChartConfig.apply {
                    showAvgLine = false
                    index = kChartIndex
                    kChartType = kChartType
                }
            }
            Period.YTD -> {
                stockChartConfig.apply {
                    scaleAble = false
                    scrollAble = false
                    overScrollAble = false
                }
                kChartConfig.apply {
                    showAvgLine = false
                    index = kChartIndex
                    kChartType = kChartType
                }
            }
            else -> {
                stockChartConfig.apply {
                    scaleAble = true
                    scrollAble = true
                    overScrollAble = true
                }
                kChartConfig.apply {
                    showAvgLine = false
                    index = kChartIndex
                    kChartType = kChartType
                }
            }
        }
        this.period = period
        loadData(period = this.period)
        refreshOptionButtonsState()
    }

    private fun changeKChartType(kChartType: KChartConfig.KChartType) {

        if (period == Period.DAY_TIME || period == Period.FIVE_DAYS) {
            return
        }

        this.kChartType = kChartType
        kChartConfig.kChartType = this.kChartType
        // 成交量图根据K线图类型决定是空心还是实心
        volumeChartConfig.volumeChartType =
            if (this.kChartType is KChartConfig.KChartType.HOLLOW) VolumeChartConfig.VolumeChartType.HOLLOW() else VolumeChartConfig.VolumeChartType.CANDLE()
        binding.stockChart.notifyChanged()
        refreshOptionButtonsState()
    }

    private fun initPeriodButtons() {
        periodOptionButtons.putAll(
            arrayOf(
                Pair(binding.llOptions.periodDay, Period.DAY),
                Pair(binding.llOptions.periodFiveDays, Period.FIVE_DAYS),
                Pair(binding.llOptions.periodWeek, Period.WEEK),
                Pair(binding.llOptions.periodMonth, Period.MONTH),
                Pair(binding.llOptions.periodQuarter, Period.QUARTER),
                Pair(binding.llOptions.periodYear, Period.YEAR),
                Pair(binding.llOptions.periodFiveYears, Period.FIVE_YEARS),
                Pair(binding.llOptions.periodYtd, Period.YTD),
                Pair(binding.llOptions.periodOneMinute, Period.ONE_MINUTE),
                Pair(binding.llOptions.periodFiveMinutes, Period.FIVE_MINUTES),
                Pair(binding.llOptions.periodThirtyMinutes, Period.THIRTY_MINUTES),
                Pair(binding.llOptions.periodSixtyMinutes, Period.SIXTY_MINUTES),
                Pair(binding.llOptions.periodDayTime, Period.DAY_TIME),
            )
        )

        periodOptionButtons.forEach { (button, period) ->
            button.setOnClickListener { changePeriod(period) }
        }
    }

    private fun initKChartTypeButtons() {
        kChartTypeOptionButtons.putAll(
            listOf(
                Pair(binding.llOptions.kchartTypeCandle, KChartConfig.KChartType.CANDLE()),
                Pair(binding.llOptions.kchartTypeHollow, KChartConfig.KChartType.HOLLOW()),
                Pair(binding.llOptions.kchartTypeLine, KChartConfig.KChartType.LINE()),
                Pair(binding.llOptions.kchartTypeMountain, KChartConfig.KChartType.MOUNTAIN()),
                Pair(binding.llOptions.kchartTypeBar, KChartConfig.KChartType.BAR())
            )
        )
        kChartTypeOptionButtons.forEach { (button, kChatType) ->
            button.setOnClickListener { changeKChartType(kChatType) }
        }
    }

    private fun initIndexButtons() {
        indexOptionButton.putAll(
            listOf(
                Pair(binding.llOptions.indexMa, Index.MA(preFixText = " ${getString(R.string.quo_chart_no_restoration)} ")),
                Pair(binding.llOptions.indexEma, Index.EMA(preFixText = " ${getString(R.string.quo_chart_no_restoration)} ")),
                Pair(binding.llOptions.indexBoll, Index.BOLL(preFixText = " ${getString(R.string.quo_chart_no_restoration)} ")),
                Pair(binding.llOptions.indexMacd, Index.MACD()),
                Pair(binding.llOptions.indexKdj, Index.KDJ()),
                Pair(binding.llOptions.indexRsi, Index.RSI()),
                Pair(binding.llOptions.indexVol, Index.VOL()),
            )
        )

        indexOptionButton.forEach { (button, index) ->
            button.setOnClickListener {
                when (index::class) {
                    Index.MA::class, Index.EMA::class, Index.BOLL::class -> { // 这三个是K线图中的指标

                        if (period == Period.DAY_TIME || period == Period.FIVE_DAYS) return@setOnClickListener

                        kChartIndex =
                            if (kChartIndex != null && kChartIndex!!::class == index::class) {
                                null
                            } else {
                                index
                            }
                        kChartConfig.index = kChartIndex
                    }
                    Index.MACD::class -> {
                        if (stockChartConfig.childChartFactories.contains(macdChartFactory!!)) {
                            stockChartConfig.removeChildCharts(macdChartFactory!!)
                        } else {
                            stockChartConfig.addChildCharts(macdChartFactory!!)
                        }
                    }
                    Index.KDJ::class -> {
                        if (stockChartConfig.childChartFactories.contains(kdjChartFactory!!)) {
                            stockChartConfig.removeChildCharts(kdjChartFactory!!)
                        } else {
                            stockChartConfig.addChildCharts(kdjChartFactory!!)
                        }
                    }
                    Index.RSI::class -> {
                        if (stockChartConfig.childChartFactories.contains(rsiChartFactory!!)) {
                            stockChartConfig.removeChildCharts(rsiChartFactory!!)
                        } else {
                            stockChartConfig.addChildCharts(rsiChartFactory!!)
                        }
                    }
                    Index.VOL::class -> {
                        if (stockChartConfig.childChartFactories.contains(volumeChartFactory!!)) {
                            stockChartConfig.removeChildCharts(volumeChartFactory!!)
                        } else {
                            stockChartConfig.addChildCharts(volumeChartFactory!!)
                        }
                    }
                }
                binding.stockChart.notifyChanged()
                refreshOptionButtonsState()
            }
        }
    }

    private fun initCustomChartButtons() {
        binding.llOptions.custom.setOnClickListener {
            if (stockChartConfig.childChartFactories.contains(customChartFactory!!)) {
                stockChartConfig.removeChildCharts(customChartFactory!!)
            } else {
                stockChartConfig.addChildCharts(customChartFactory!!)
            }
            binding.stockChart.notifyChanged()
            refreshOptionButtonsState()
        }
    }

    /**
     * 选项按钮状态刷新
     */
    private fun refreshOptionButtonsState() {
        periodOptionButtons.forEach { (button, period) ->
            button.isSelected = period == this.period
        }
        kChartTypeOptionButtons.forEach { (button, kChartType) ->
            button.isSelected = kChartType::class == kChartConfig.kChartType::class
        }
        indexOptionButton.forEach { (button, index) ->
            when (index::class) {
                Index.MA::class, Index.EMA::class, Index.BOLL::class -> { // 这三个是K线图中的指标
                    button.isSelected =
                        kChartConfig.index != null && kChartConfig.index!!::class == index::class
                }
                Index.MACD::class -> {
                    button.isSelected =
                        stockChartConfig.childChartFactories.contains(macdChartFactory!!)
                }
                Index.KDJ::class -> {
                    button.isSelected =
                        stockChartConfig.childChartFactories.contains(kdjChartFactory!!)
                }
                Index.RSI::class -> {
                    button.isSelected =
                        stockChartConfig.childChartFactories.contains(rsiChartFactory!!)
                }
                Index.VOL::class -> {
                    button.isSelected =
                        stockChartConfig.childChartFactories.contains(volumeChartFactory!!)
                }
            }
        }

        binding.llOptions.custom.isSelected = stockChartConfig.childChartFactories.contains(customChartFactory!!)
    }
}