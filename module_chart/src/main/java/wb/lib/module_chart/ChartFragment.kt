package wb.lib.module_chart

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.stockchart.StockChartConfig
import com.github.stockchart.childchart.base.HighlightLabelConfig
import com.github.stockchart.childchart.kchart.KChartConfig
import com.github.stockchart.childchart.kchart.KChartFactory
import com.github.stockchart.childchart.kdjchart.KdjChartConfig
import com.github.stockchart.childchart.kdjchart.KdjChartFactory
import com.github.stockchart.childchart.macdchart.MacdChartConfig
import com.github.stockchart.childchart.macdchart.MacdChartFactory
import com.github.stockchart.childchart.rskchart.RsiChartConfig
import com.github.stockchart.childchart.rskchart.RsiChartFactory
import com.github.stockchart.childchart.timebar.TimeBarConfig
import com.github.stockchart.childchart.timebar.TimeBarFactory
import com.github.stockchart.childchart.volumechart.VolumeChartConfig
import com.github.stockchart.childchart.volumechart.VolumeChartFactory
import com.github.stockchart.entities.FLAG_EMPTY
import com.github.stockchart.entities.Highlight
import com.github.stockchart.entities.IKEntity
import com.github.stockchart.entities.KEntity
import com.github.stockchart.entities.containFlag
import com.github.stockchart.index.Index
import com.github.stockchart.listener.OnHighlightListener
import com.github.stockchart.listener.OnLoadMoreListener
import com.github.stockchart.util.DimensionUtil
import com.github.stockchart.util.NumberFormatUtil
import kotlinx.android.synthetic.main.fragment_chart.llConfig
import kotlinx.android.synthetic.main.fragment_chart.startAnim
import kotlinx.android.synthetic.main.fragment_chart.stock_chart
import kotlinx.android.synthetic.main.fragment_chart.tv_highlight_info
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.custom
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.index_boll
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.index_ema
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.index_kdj
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.index_ma
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.index_macd
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.index_rsi
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.index_vol
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.kchart_type_bar
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.kchart_type_candle
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.kchart_type_hollow
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.kchart_type_line
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.kchart_type_mountain
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.kchart_type_profession
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.kchart_type_simple
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.period_day
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.period_day_time
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.period_five_days
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.period_five_minutes
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.period_five_years
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.period_month
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.period_one_minute
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.period_quarter
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.period_sixty_minutes
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.period_thirty_minutes
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.period_week
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.period_year
import kotlinx.android.synthetic.main.layout_sample2_option_buttons.period_ytd
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import wb.lib.module_chart.custom.CustomChartConfig
import wb.lib.module_chart.custom.CustomChartFactory
import java.math.BigDecimal

/**
 * @author Hai
 * @date   2025/4/10 10:02
 * @desc
 */
class ChartFragment:Fragment(), IChartCompose {

    private var periodOptionButtons = mutableMapOf<View, Period>()
    private var kChartTypeOptionButtons = mutableMapOf<View, KChartConfig.KChartType>()
    private var indexOptionButton = mutableMapOf<View, Index>()

    private var period = Period.DAY
    private var kChartType: KChartConfig.KChartType = KChartConfig.KChartType.CANDLE()
    private var kChartIndex: Index?=null
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
    private var assetId = ""
    private var isDev = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kChartIndex =Index.MA(preFixText = " ${getString(R.string.quo_chart_no_restoration)} ")
        arguments?.getBoolean("dev")?.let {
            isDev = it
        }
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

        // StockChart初始化
        initStockChart()

        // 各选项按钮初始化
        initPeriodButtons()
        initKChartTypeButtons()
        initChartTypeButtons()
        initIndexButtons()
        initCustomChartButtons()
        startAnim.setOnClickListener {
            if (kChartConfig.preClosePrice != null) {
                kChartConfig.updateTimeDayLast( kChartConfig.preClosePrice!! * (0.99f + Math.random().toFloat()*0.02f))
                stock_chart.notifyChanged()
            }
        }

        if(isDev){
            llConfig.visibility = View.VISIBLE
            // 切换到到日K，首次加载数据
            changePeriod(Period.DAY)
        }else{
            llConfig.visibility = View.GONE

            MainScope().launch {
                subChartType.asStateFlow().collectLatest {
                    if(isDateTimeType()){
                        stockChartConfig.childChartFactories.clear()
                        when (it) {
                            KSubChartType.MACD -> {
                                stockChartConfig.addChildCharts(macdChartFactory!!)
                            }
                            KSubChartType.KDJ -> stockChartConfig.addChildCharts(kdjChartFactory!!)
                            KSubChartType.RSI -> stockChartConfig.addChildCharts(rsiChartFactory!!)
                            KSubChartType.MA ->  kChartConfig.index = Index.MA()
                            KSubChartType.BOLL ->  kChartConfig.index = Index.BOLL()
                            else-> stockChartConfig.addChildCharts(volumeChartFactory!!)
                        }
                    }
                }
            }
        }
    }

    fun isDateTimeType() = when(mainChartType.value){
        KChartType.ONE_DAY, KChartType.ONE_DAY_DARK, KChartType.ONE_DAY_PRE, KChartType.ONE_DAY_AFTER ->true
        else->false
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
            if(isDev){
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

            if(isDev){
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
                }

                override fun onHighlightEnd() {
                    onCrossLineMoveListener?.onCrossLineDismiss()
                    tv_highlight_info.text = ""
                }

                override fun onHighlight(highlight: Highlight) {

                    val idx = highlight.getIdx()
                    val kEntities = stockChartConfig.kEntities
                    var showContent = ""

                    if (idx in kEntities.indices) {
                        val kEntity = kEntities[idx]
                        onCrossLineMoveListener?.onCrossLineMove(highlight,kEntity)

                        if (kEntity.containFlag(FLAG_EMPTY)) {
                            showContent = ""
                        } else if (kChartType is KChartConfig.KChartType.LINE || kChartType is KChartConfig.KChartType.MOUNTAIN) {
                            val firstIdx = stock_chart.findFirstNotEmptyKEntityIdxInDisplayArea()
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
                    tv_highlight_info.text = showContent
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
                    when (preClosePrice) {
                        null,it-> leftLabelConfig!!.textColor
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
        initialPageSize: Int?,
        timeBarType: TimeBarConfig.Type,
        page: Int=0
    ) {
        if (kEntities.isNotEmpty()) {
            // 设置数据
            if (page == 0) {
                if (initialPageSize != null) {
                    stockChartConfig.setKEntities(
                        kEntities,
                        kEntities.size - initialPageSize,
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
            if(timeBarType is  TimeBarConfig.Type.DayTime){
                stockChartConfig.xValueMin = 0.0f
                stockChartConfig.xValueMax = ChartType.ONE_DAY.pointNum.toFloat()
                kChartConfig.preClosePrice = 622.0f
                kChartConfig.chartMainDisplayAreaPaddingTop = 0f
                kChartConfig.chartMainDisplayAreaPaddingBottom = 0f
                timeBarType.labelParis = getOneDayXLabels()
            }else{
                stockChartConfig.xValueMin = null
                stockChartConfig.xValueMax = null
                kChartConfig.preClosePrice = null
                kChartConfig.chartMainDisplayAreaPaddingTop =
                    com.github.stockchart.DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_TOP
                kChartConfig.chartMainDisplayAreaPaddingBottom =
                    com.github.stockchart.DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_BOTTOM
            }
            // 通知更新
            stock_chart.notifyChanged()
            currentPage = page
        } else {
            Toast.makeText(requireContext(), "没有更多数据了！", Toast.LENGTH_SHORT).show()
        }
        isLoading = false
    }

    var dataLoader:((page: Int, period: Period,cb:(list:List<IKEntity>)->Unit)->Unit)?=null
    // 加载模拟数据
    private fun loadData(page: Int = 0, period: Period) {
        isLoading = true
        dataLoader?.invoke(page,period){list->
            when (period) {
                Period.DAY -> {
                    doAfterLoad(list, 60, TimeBarConfig.Type.Day(),page)
                }
                Period.FIVE_DAYS -> {
                    doAfterLoad(list, null, TimeBarConfig.Type.FiveDays(),page)
                }
                Period.WEEK -> {
                    doAfterLoad(list, 60, TimeBarConfig.Type.Week(),page)
                }
                Period.MONTH -> {
                    doAfterLoad(list, 60, TimeBarConfig.Type.Month(),page)

                }
                Period.QUARTER -> {
                    doAfterLoad(list, null, TimeBarConfig.Type.Quarter(),page)

                }
                Period.YEAR -> {
                    doAfterLoad(list, null, TimeBarConfig.Type.Year(),page)
                }
                Period.FIVE_YEARS -> {
                        doAfterLoad(list, null, TimeBarConfig.Type.FiveYears(),page)
                }
                Period.YTD -> {
                    doAfterLoad(list, null, TimeBarConfig.Type.YTD(),page)
                }
                Period.ONE_MINUTE -> {
                    doAfterLoad(list, 60, TimeBarConfig.Type.OneMinute(),page)
                }
                Period.FIVE_MINUTES -> {
                    doAfterLoad(list, 60, TimeBarConfig.Type.FiveMinutes(),page)
                }
                Period.THIRTY_MINUTES -> {
                    doAfterLoad(list, 60, TimeBarConfig.Type.ThirtyMinutes(),page)

                }
                Period.SIXTY_MINUTES -> {
                    doAfterLoad(list, 60, TimeBarConfig.Type.SixtyMinutes(),page)

                }
                Period.DAY_TIME -> {
                    doAfterLoad(list, null, TimeBarConfig.Type.DayTime(),page)
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
                stock_chart.notifyChanged()
                refreshOptionButtonsState()
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
                leftLabelConfig = null
                index = null
                if(kChartType.highestAndLowestLabelConfig!=null){
                    lastHLConfig[kChartType] = kChartType.highestAndLowestLabelConfig
                }
                kChartType.highestAndLowestLabelConfig = null
            }
            volumeChartConfig.index = null

        }else{
            // 专业版
            stockChartConfig.apply {
                showHighlightHorizontalLine = true
                gridLineColor = com.github.stockchart.DEFAULT_GRID_LINE_COLOR
                addChildChart(timeBarFactory!!,1)
            }
            kChartConfig.apply {
                avgLineColor = com.github.stockchart.DEFAULT_AVG_LINE_COLOR
                // 左侧标签设置
                leftLabelConfig = KChartConfig.LabelConfig(
                    5,
                    { "${NumberFormatUtil.formatPrice(it)}" },
                    DimensionUtil.sp2px(this@ChartFragment.requireContext(), 8f).toFloat(),
                    Color.parseColor("#E4E4E4"),
                    DimensionUtil.dp2px(this@ChartFragment.requireContext(), 10f).toFloat(),
                    DimensionUtil.dp2px(this@ChartFragment.requireContext(), 30f).toFloat(),
                    DimensionUtil.dp2px(this@ChartFragment.requireContext(), 30f).toFloat()
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

    private fun getOneDayXLabels()=mutableMapOf<Int,String>().apply {
        if (assetType == AssetType.US_OPT) {
            this[0] = "09:30"
            this[150] = "12:00"
            this[ChartType.US_ONE_DAY.pointNum] = "16:00"
        }else{
            when{
                assetId?.endsWith(".HK") == true ->{
                    if(mainChartType.value != KChartType.ONE_DAY_DARK){
                        this[0] = "09:30"
                        this[150] = "12:00/13:00"
                        this[ChartType.HK_ONE_DAY.pointNum] = "16:00"
                    }else{
                        this[0] = "16:16"
                        this[ChartType.DARK_DAY.pointNum] = "18:30"
                    }
                }
                assetId?.endsWith(".US") == true -> {
                    //TODO: if(isOTC(assetId))
                    when (mainChartType.value) {
                        KChartType.ONE_DAY_PRE -> {
                            this[0] = "04:00"
                            this[ChartType.US_ONE_DAY_PRE_MARKET.pointNum/2] = "07:00"
                            this[ChartType.US_ONE_DAY_PRE_MARKET.pointNum] = "09:30"
                        }
                        KChartType.ONE_DAY -> {
                            this[0] = "09:30"
                            this[150] = "12:00"
                            this[ChartType.US_ONE_DAY.pointNum] = "16:00"
                        }
                        else -> {
                            this[0] = "16:00"
                            this[ChartType.US_ONE_DAY_AFTER_MARKET.pointNum/2] = "18:00"
                            this[ChartType.US_ONE_DAY_AFTER_MARKET.pointNum] = "20:00"
                        }
                    }
                }
                else -> {
                    this[0] = "09:30"
                    this[ChartType.ONE_DAY.pointNum/2] = "11:30/13:00"
                    this[ChartType.ONE_DAY.pointNum] = "15:00"
                }
            }
        }

    }


    override val adjustType = AdjustType.NO_ADJUST
    override var onCrossLineMoveListener: OnCrossLineMoveListener?=null
    override var assetType: Int = AssetType.SECURITY

    override val subChartType: MutableStateFlow<Int> = MutableStateFlow(-1)
    override var mainChartType: MutableStateFlow<Int> = MutableStateFlow(-1)

    override fun setLastPointData(price: Float, avgPrice: Float?, values: BigDecimal) {
        // 目前只更新
        kChartConfig.updateTimeDayLast(price)
        stock_chart.notifyChanged()
    }

    var preClosePrice:Float?=null
        set(value) {
            field = value?.also {
                if(it>0.0){
                    kChartConfig.preClosePrice = it
                }
            }
        }
    override fun setKData(
        srcDate: JSONObject,
        assetId: String,
        preClosePrice: Double,
        mainChartType: Int
    ) {
        this.assetId = assetId
        this.mainChartType.value = mainChartType
        if(isDateTimeType()){
            timeBarConfig.type = TimeBarConfig.Type.DayTime(labelParis = getOneDayXLabels())
            if (preClosePrice > 0) {
                this.preClosePrice = preClosePrice.toFloat()
            }
            parseTimeData(srcDate)
        }else{
            parseKData(srcDate)
        }
    }

    private fun parseKData(srcDate: JSONObject) {
        val list = mutableListOf<KEntity>()
        srcDate.optJSONArray("data")?.let {
            for (i in 0 until it.length()) {
                it.optJSONArray(i).apply {
                    list.add(KEntity(optDouble(2).toFloat(),optDouble(3).toFloat(),
                        optDouble(1).toFloat(),optDouble(4).toFloat(),
                        optString(6).toLong(),optLong(0, 0L),
                        0f,optDouble(5).toBigDecimal()))
                }
            }
        }
        stockChartConfig.setKEntities(list,0,100)
        stock_chart.notifyChanged()
    }

    private fun parseTimeData(data: JSONObject) {
        this.preClosePrice = data.optDouble("preClose", this.preClosePrice?.toDouble() ?:-1.0).toFloat()
        val list = mutableListOf<KEntity>()

        data.optJSONArray("data")?.let {
            for (i in 0 until it.length()) {
                it.optJSONArray(i).apply {
                    list.add(KEntity(0f,0f,optDouble(4).toFloat(),optDouble(1).toFloat(),optString(3).toLong(),optLong(0, 0L),
                        optDouble(2).toFloat(),optDouble(5).toBigDecimal()))
                }
            }
        }
        stockChartConfig.setKEntities(list,0,100)
        stock_chart.notifyChanged()
    }
}

object AdjustType {
    //不复权
    const val NO_ADJUST: String = "N"

    //前复权
    const val FRONT_ADJUST: String = "F"

    //后复权
    const val BACK_ADJUST: String = "B"
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