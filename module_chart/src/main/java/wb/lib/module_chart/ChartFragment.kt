package wb.lib.module_chart

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wb.stockchart.DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_BOTTOM
import com.wb.stockchart.DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_TOP
import com.wb.stockchart.DEFAULT_K_CHART_COST_PRICE_LINE_COLOR
import com.wb.stockchart.StockChartConfig
import com.wb.stockchart.childchart.base.HighlightLabelConfig
import com.wb.stockchart.childchart.kchart.KChartConfig
import com.wb.stockchart.childchart.kchart.KChartConfig.KChartType
import com.wb.stockchart.childchart.kchart.KChartFactory
import com.wb.stockchart.childchart.kdjchart.KdjChartConfig
import com.wb.stockchart.childchart.kdjchart.KdjChartFactory
import com.wb.stockchart.childchart.macdchart.MacdChartConfig
import com.wb.stockchart.childchart.macdchart.MacdChartFactory
import com.wb.stockchart.childchart.rskchart.RsiChartConfig
import com.wb.stockchart.childchart.rskchart.RsiChartFactory
import com.wb.stockchart.childchart.timebar.TimeBarConfig
import com.wb.stockchart.childchart.timebar.TimeBarFactory
import com.wb.stockchart.childchart.volumechart.VolumeChartConfig
import com.wb.stockchart.childchart.volumechart.VolumeChartFactory
import com.wb.stockchart.entities.FLAG_EMPTY
import com.wb.stockchart.entities.Highlight
import com.wb.stockchart.entities.IKEntity
import com.wb.stockchart.entities.containFlag
import com.wb.stockchart.index.Index
import com.wb.stockchart.listener.OnHighlightListener
import com.wb.stockchart.listener.OnLoadMoreListener
import com.wb.stockchart.util.DimensionUtil
import com.wb.stockchart.util.NumberFormatUtil
import kotlinx.android.synthetic.main.fragment_chart.llConfig
import kotlinx.android.synthetic.main.fragment_chart.startAnim
import kotlinx.android.synthetic.main.fragment_chart.stock_chart
import kotlinx.android.synthetic.main.fragment_chart.tv_highlight_info
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.custom
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import wb.lib.module_chart.custom.CustomChartConfig
import wb.lib.module_chart.custom.CustomChartFactory
import kotlin.reflect.KClass

/**
 * @author Hai
 * @date   2025/4/10 10:02
 * @desc
 */
class ChartFragment:Fragment() {

    private var periodOptionButtons = mutableMapOf<View, Period>()
    private var kChartTypeOptionButtons = mutableMapOf<View, KChartConfig.KChartType>()
    private var indexOptionButton = mutableMapOf<View, Index>()

    var period = MutableStateFlow(Period.DAY_TIME)
    private var kChartType: KChartConfig.KChartType = KChartConfig.KChartType.CANDLE()
    private var kChartIndex: Index?=null

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
    private var isDev = true
    var onLoadMoreListener: OnLoadMoreListener? = null
    var onHighlightListener: OnHighlightListener?=null
    private var scope:CoroutineScope?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kChartIndex =Index.MA(preFixText = " ${getString(R.string.quo_chart_no_restoration)} ")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_chart,container,false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getBoolean("dev")?.let {
            isDev = it
        }
        scope=MainScope()
        // StockChart初始化
        initStockChart()

        // 各选项按钮初始化
        initPeriodButtons()
        initKChartTypeButtons()
        initChartTypeButtons()
        initIndexButtons()
        initCustomChartButtons()
        startAnim.setOnClickListener {
            kChartConfig.preClosePrice?.let {
                lastPrice = it * (0.99f + Math.random().toFloat()*0.02f)
            }
        }

        setIsDev(isDev)
        changePeriod(Period.DAY_TIME)

        scope?.launch {
            period.asStateFlow()
                .collectLatest {
                Log.d("testChart", "period collectLatest: $it")
                changePeriod(it)
            }
        }
    }

    fun setIsDev(dev: Boolean){
        isDev = dev
        if(isDev){
            llConfig?.visibility = View.VISIBLE
        }else{
            llConfig?.visibility = View.GONE
        }
    }
    private fun initChartTypeButtons() {
        kchart_type_simple.setOnClickListener {
            changeChartType(0)
        }
        kchart_type_profession.setOnClickListener {
            changeChartType(1)
        }
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
            chartMainDisplayAreaPaddingLeft = DimensionUtil.dp2px(this@ChartFragment.requireContext(),2.5f).toFloat()
            chartMainDisplayAreaPaddingRight = DimensionUtil.dp2px(this@ChartFragment.requireContext(),2.5f).toFloat()
            // 将需要显示的子图的工厂添加进StockChart配置
            if(!isDev){
                addChildCharts(
                    kChartFactory!!,
                    timeBarFactory!!,
                    volumeChartFactory!!,
                    macdChartFactory!!,
                    kdjChartFactory!!,
                    rsiChartFactory!!,
                    customChartFactory!!
                )
            }else{
                addChildCharts(
                    kChartFactory!!,
                    timeBarFactory!!,
                    volumeChartFactory!!,
                )
            }


            // 最大缩放比例
            scaleFactorMax = 2f

            // 最小缩放比例
            scaleFactorMin = 0.5f

            // 网格线设置
            gridVerticalLineCount = 3
            gridHorizontalLineCount = 4

            onLoadMoreListener = this@ChartFragment.onLoadMoreListener
        }


        // 绑定配置
        stock_chart.setConfig(stockChartConfig)
    }

    /**
     * K线图初始化
     */
    private fun initKChart() {
        kChartFactory = KChartFactory(stock_chart, kChartConfig)

        kChartConfig.apply {

            // 指标线宽度
            indexStrokeWidth = DimensionUtil.dp2px(this@ChartFragment.requireContext(), 0.5f).toFloat()

            // 监听长按信息
            onHighlightListener = object : OnHighlightListener {
                override fun onHighlightBegin() {
                    this@ChartFragment.onHighlightListener?.onHighlightBegin()
                }

                override fun onHighlightEnd() {
                    tv_highlight_info.text = ""
                    this@ChartFragment.onHighlightListener?.onHighlightEnd()
                }

                override fun onHighlight(highlight: Highlight) {

                    val idx = highlight.getIdx()
                    val kEntities = stockChartConfig.kEntities
                    var showContent = ""

                    if (idx in kEntities.indices) {
                        val kEntity = kEntities[idx]
                        if (kEntity.containFlag(FLAG_EMPTY)) {
                            showContent = ""
                        }else if(period.value == Period.DAY_TIME){
                            val price =
                                "最新价:${NumberFormatUtil.formatPrice(kEntity.getClosePrice())}"
                            var changeRatio = "涨跌幅:——"
                            kChartConfig.preClosePrice?.let{ it ->
                                changeRatio = "涨跌幅:${Util.formatChangeRatio(kEntity.getClosePrice(),it)}"
                            }
                            val volume = "成交量:${Util.formatVolume(kEntity.getVolume())}"
                            val amount = "成交额:${kEntity.getAmount()?.let { Util.formatAmount(it) } ?: "- -"}"

                            showContent = "$price，$changeRatio，$volume $amount"
                        } else if (kChartType is KChartConfig.KChartType.LINE || kChartType is KChartConfig.KChartType.MOUNTAIN) {
                            val firstIdx = stock_chart.findFirstNotEmptyKEntityIdxInDisplayArea()
                            val price =
                                "最新价:${NumberFormatUtil.formatPrice(kEntity.getClosePrice())}"
                            var changeRatio = "涨跌幅:——"
                            firstIdx?.let{ it ->
                                changeRatio = "涨跌幅:${Util.formatChangeRatio(kEntity.getClosePrice(),kEntities[it].getClosePrice())}"
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
                    tv_highlight_info.text = showContent
                    this@ChartFragment.onHighlightListener?.onHighlight(highlight)
                }
            }

            // 图高度
            height = DimensionUtil.dp2px(this@ChartFragment.requireContext(), 170f)

            // 左侧标签设置
            leftLabelConfig = KChartConfig.LabelConfig(
                3,
                { "${NumberFormatUtil.formatPrice(it)}" },
                DimensionUtil.sp2px(this@ChartFragment.requireContext(), 8f).toFloat(),
                Color.parseColor("#E4E4E4"),
                DimensionUtil.dp2px(this@ChartFragment.requireContext(), 5f).toFloat(),0f,0f,
                {
                    when (NumberFormatUtil.formatPrice(preClosePrice?:it)) {
                        NumberFormatUtil.formatPrice(it)-> leftLabelConfig!!.textColor
                        else -> if(it>preClosePrice!!) Color.RED else Color.GREEN
                    }
                }
            )

            // 长按左侧标签配置
            highlightLabelLeft =
                HighlightLabelConfig(
                    textSize = DimensionUtil.sp2px(this@ChartFragment.requireContext(), 10f).toFloat(),
                    bgColor = Color.parseColor("#A3A3A3"),
                    padding = DimensionUtil.dp2px(this@ChartFragment.requireContext(), 5f).toFloat()
                )

            // 空心蜡烛边框宽度
            hollowChartLineStrokeWidth = DimensionUtil.dp2px(this@ChartFragment.requireContext(), 1f).toFloat()
        }
    }

    /**
     * 成交量图初始化
     */
    private fun initVolumeChart() {
        volumeChartFactory = VolumeChartFactory(stock_chart, volumeChartConfig)

        volumeChartConfig.apply {
            // 图高度
            height = DimensionUtil.dp2px(this@ChartFragment.requireContext(), 40f)


            // 长按左侧标签配置
            highlightLabelLeft = HighlightLabelConfig(
                textSize = DimensionUtil.sp2px(this@ChartFragment.requireContext(), 10f).toFloat(),
                bgColor = Color.parseColor("#A3A3A3"),
                padding = DimensionUtil.dp2px(this@ChartFragment.requireContext(), 5f).toFloat(),
                textFormat = { volume ->
                    Util.formatVolume(volume = volume.toLong())
                }
            )

            // 柱子空心时的线条宽度
            hollowChartLineStrokeWidth = DimensionUtil.dp2px(this@ChartFragment.requireContext(), 1f).toFloat()

        }
    }

    /**
     * 时间条图初始化
     */
    private fun initTimeBar() {
        timeBarFactory = TimeBarFactory(stock_chart, timeBarConfig)

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
        macdChartFactory = MacdChartFactory(stock_chart, macdChartConfig)

        macdChartConfig.apply {
            // 图高度
            height = DimensionUtil.dp2px(this@ChartFragment.requireContext(), 80f)

            // 长按左侧标签配置
            highlightLabelLeft = HighlightLabelConfig(
                textSize = DimensionUtil.sp2px(this@ChartFragment.requireContext(), 10f).toFloat(),
                bgColor = Color.parseColor("#A3A3A3"),
                padding = DimensionUtil.dp2px(this@ChartFragment.requireContext(), 5f).toFloat()
            )
        }
    }

    /**
     * kdj指标图初始化
     */
    private fun initKdjChart() {
        kdjChartFactory = KdjChartFactory(stock_chart, kdjChartConfig)

        kdjChartConfig.apply {
            // 图高度
            height = DimensionUtil.dp2px(this@ChartFragment.requireContext(), 80f)

            // 长按左侧标签配置
            highlightLabelLeft = HighlightLabelConfig(
                textSize = DimensionUtil.sp2px(this@ChartFragment.requireContext(), 10f).toFloat(),
                bgColor = Color.parseColor("#A3A3A3"),
                padding = DimensionUtil.dp2px(this@ChartFragment.requireContext(), 5f).toFloat()
            )
        }
    }

    /**
     * rsi指标图初始化
     */
    private fun initRsiChart() {
        rsiChartFactory = RsiChartFactory(stock_chart, rsiChartConfig)

        rsiChartConfig.apply {
            // 图高度
            height = DimensionUtil.dp2px(this@ChartFragment.requireContext(), 80f)

            // 长按左侧标签配置
            highlightLabelLeft = HighlightLabelConfig(
                textSize = DimensionUtil.sp2px(this@ChartFragment.requireContext(), 10f).toFloat(),
                bgColor = Color.parseColor("#A3A3A3"),
                padding = DimensionUtil.dp2px(this@ChartFragment.requireContext(), 5f).toFloat()
            )
        }
    }

    /**
     * 自定义示例图初始化
     */
    private fun initCustomChart() {
        customChartFactory = CustomChartFactory(stock_chart, customChartConfig)
        customChartConfig.apply {
            height = DimensionUtil.dp2px(this@ChartFragment.requireContext(), 50f)
            bigLabel = "这是自定义子图示例"
        }
    }

    fun doAfterLoad(
        kEntities: List<IKEntity>,
        preClosePrice: Float?=null,
        timeBarType: TimeBarConfig.Type,
        isAppend: Boolean=false,
        initialPageSize:Int? = 60
    ) {

        // 设置时间条样式
        timeBarConfig.type = timeBarType
        if(timeBarType is  TimeBarConfig.Type.DayTime){
            stockChartConfig.xValueMin = 0.0f
            stockChartConfig.xValueMax = timeBarType.totalPoint!!.toFloat()
            kChartConfig.preClosePrice = preClosePrice
            kChartConfig.chartMainDisplayAreaPaddingTop = 0f
            kChartConfig.chartMainDisplayAreaPaddingBottom = 0f
            // 分时图只有两种类型自动校验
            if (period.value != Period.DAY_TIME && period.value != Period.FIVE_DAYS) {
                changePeriod(Period.DAY_TIME)
            }
        }else{
            if(period.value == Period.DAY_TIME){
                changePeriod(Period.DAY)
            }
            //非分时不绘制昨收线
            kChartConfig.preClosePrice = null
            stockChartConfig.xValueMin = null
            stockChartConfig.xValueMax = null
            stockChartConfig.minShowCount = initialPageSize?:60
            kChartConfig.chartMainDisplayAreaPaddingTop = DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_TOP
            kChartConfig.chartMainDisplayAreaPaddingBottom = DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_BOTTOM
        }


        // 设置数据
        if(isAppend){
            stockChartConfig.appendLeftKEntities(kEntities)
        }else{
            if (initialPageSize != null) {
                stockChartConfig.setKEntities(
                    kEntities,
                    (kEntities.size - initialPageSize).coerceIn(0,kEntities.size),
                    (kEntities.size - 1).coerceIn(0,kEntities.size)
                )
            } else {
                stockChartConfig.setKEntities(kEntities)
            }
        }
        stock_chart.notifyChanged()
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
                    kChartType = if(volumeChartConfig.volumeChartType is VolumeChartConfig.VolumeChartType.CANDLE) KChartConfig.KChartType.CANDLE() else KChartConfig.KChartType.HOLLOW()
                }
            }
        }
        this.period.value = period
        refreshOptionButtonsState()
    }

    private fun changeKChartType(kChartType: KChartConfig.KChartType) {

        if (period.value == Period.DAY_TIME || period.value == Period.FIVE_DAYS) {
            return
        }

        this.kChartType = kChartType
        kChartConfig.kChartType = this.kChartType
        // 成交量图根据K线图类型决定是空心还是实心
        volumeChartConfig.volumeChartType = if (this.kChartType is KChartConfig.KChartType.HOLLOW) VolumeChartConfig.VolumeChartType.HOLLOW() else VolumeChartConfig.VolumeChartType.CANDLE()
//        volumeChartConfig.volumeChartType =  VolumeChartConfig.VolumeChartType.CANDLE()
        stock_chart.notifyChanged()
        refreshOptionButtonsState()
    }

    private fun initPeriodButtons() {
        periodOptionButtons.putAll(
            arrayOf(
                Pair(period_day, Period.DAY),
                Pair(period_five_days, Period.FIVE_DAYS),
                Pair(period_week, Period.WEEK),
                Pair(period_month, Period.MONTH),
                Pair(period_quarter, Period.QUARTER),
                Pair(period_year, Period.YEAR),
                Pair(period_five_years, Period.FIVE_YEARS),
                Pair(period_ytd, Period.YTD),
                Pair(period_one_minute, Period.ONE_MINUTE),
                Pair(period_five_minutes, Period.FIVE_MINUTES),
                Pair(period_thirty_minutes, Period.THIRTY_MINUTES),
                Pair(period_sixty_minutes, Period.SIXTY_MINUTES),
                Pair(period_day_time, Period.DAY_TIME),
            )
        )

        periodOptionButtons.forEach { (button, period) ->
            button.setOnClickListener { changePeriod(period) }
        }
    }

    private fun initKChartTypeButtons() {
        kChartTypeOptionButtons.putAll(
            listOf(
                Pair(kchart_type_candle, KChartConfig.KChartType.CANDLE()),
                Pair(kchart_type_hollow, KChartConfig.KChartType.HOLLOW()),
                Pair(kchart_type_line, KChartConfig.KChartType.LINE()),
                Pair(kchart_type_mountain, KChartConfig.KChartType.MOUNTAIN()),
                Pair(kchart_type_bar, KChartConfig.KChartType.BAR())
            )
        )
        kChartTypeOptionButtons.forEach { (button, kChatType) ->
            button.setOnClickListener { changeKChartType(kChatType) }
        }
    }

    private fun initIndexButtons() {
        indexOptionButton.putAll(
            listOf(
                Pair(index_ma, Index.MA(preFixText = " ${getString(R.string.quo_chart_no_restoration)} ")),
                Pair(index_ema, Index.EMA(preFixText = " ${getString(R.string.quo_chart_no_restoration)} ")),
                Pair(index_boll, Index.BOLL(preFixText = " ${getString(R.string.quo_chart_no_restoration)} ")),
                Pair(index_macd, Index.MACD()),
                Pair(index_kdj, Index.KDJ()),
                Pair(index_rsi, Index.RSI()),
                Pair(index_vol, Index.VOL()),
            )
        )

        indexOptionButton.forEach { (button, index) ->
            button.setOnClickListener {
                changeIndexType(index)
            }
        }
    }
    private fun initCustomChartButtons() {
        custom.setOnClickListener {
            if (stockChartConfig.childChartFactories.contains(customChartFactory!!)) {
                stockChartConfig.removeChildCharts(customChartFactory!!)
            } else {
                stockChartConfig.addChildCharts(customChartFactory!!)
            }
            stock_chart.notifyChanged()
            refreshOptionButtonsState()
        }
    }

    /**
     * 选项按钮状态刷新
     */
    private fun refreshOptionButtonsState() {
        periodOptionButtons.forEach { (button, period) ->
            button.isSelected = period == this.period.value
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

        custom.isSelected = stockChartConfig.childChartFactories.contains(customChartFactory!!)

        kchart_type_simple.isSelected = currentType == 0
        kchart_type_profession.isSelected = currentType != 0
    }


    //    简洁版 | 专业版
    var currentType  = 1
    var lastHLConfig = mutableMapOf<KChartConfig.KChartType, KChartConfig.HighestAndLowestLabelConfig?>()
    private fun changeChartType(i: Int) {
        if (i == 0) {
            // 简洁版
            stockChartConfig.apply {
                showHighlightHorizontalLine = false
                gridLineColor = Color.TRANSPARENT
                removeChildCharts(timeBarFactory!!)
            }
            kChartConfig.apply {
                avgLineColor = Color.TRANSPARENT
                preCloseLineColor = Color.TRANSPARENT
                leftLabelConfig = null
                index = null
                kChartType.highestAndLowestLabelConfig = null
            }
            volumeChartConfig.index = null

        }else{
            // 专业版
            stockChartConfig.apply {
                showHighlightHorizontalLine = true
                gridLineColor = com.wb.stockchart.DEFAULT_GRID_LINE_COLOR
                addChildChart(timeBarFactory!!,1)
            }
            kChartConfig.apply {
                avgLineColor = com.wb.stockchart.DEFAULT_AVG_LINE_COLOR
                preCloseLineColor = DEFAULT_K_CHART_COST_PRICE_LINE_COLOR

                // 左侧标签设置
                leftLabelConfig = KChartConfig.LabelConfig(
                    3,
                    { "${NumberFormatUtil.formatPrice(it)}" },
                    DimensionUtil.sp2px(this@ChartFragment.requireContext(), 8f).toFloat(),
                    Color.parseColor("#E4E4E4"),
                    DimensionUtil.dp2px(this@ChartFragment.requireContext(), 5f).toFloat(),0f,0f,
                    {
                        when (preClosePrice) {
                            null,it-> leftLabelConfig!!.textColor
                            else -> if(it>preClosePrice!!) Color.RED else Color.GREEN
                        }
                    }
                )
                lastHLConfig[kChartType]?.let {
                    kChartType.highestAndLowestLabelConfig = it
                }
            }
            volumeChartConfig.index = Index.VOL()
        }
        stock_chart.notifyChanged()
        currentType = i
        refreshOptionButtonsState()
    }

    var lastPrice:Float?=null
        set(value) {
            field = value?.also {
                if(period.value == Period.DAY_TIME && it>0.0){
                    kChartConfig.updateTimeDayLast(it)
                    stock_chart.notifyChanged()
                }else{
                    kChartConfig.showCircle = false
                }
            }
            if(value == null){
                kChartConfig.showCircle = false
            }
        }

    fun hasIndexType(type: KClass<out Index>): Boolean = when(type){
        Index.MA::class-> kChartIndex is Index.MA
        Index.EMA::class->kChartIndex is Index.EMA
        Index.BOLL::class->kChartIndex is Index.BOLL
        Index.MACD::class -> stockChartConfig.childChartFactories.contains(macdChartFactory!!)
        Index.KDJ::class -> stockChartConfig.childChartFactories.contains(kdjChartFactory!!)
        Index.RSI::class -> stockChartConfig.childChartFactories.contains(rsiChartFactory!!)
        Index.VOL::class -> stockChartConfig.childChartFactories.contains(volumeChartFactory!!)
        else -> false
    }


    fun changeIndexType(index: Index){
        when (index::class) {
            Index.MA::class, Index.EMA::class, Index.BOLL::class -> { // 这三个是K线图中的指标

                if (period.value == Period.DAY_TIME || period.value == Period.FIVE_DAYS) return

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
        stock_chart.notifyChanged()
        refreshOptionButtonsState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scope?.cancel()
        scope = null
    }
}
