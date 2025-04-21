package wb.lib.module_chart

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.fragment.app.Fragment
import com.androidx.stockchart.DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_BOTTOM
import com.androidx.stockchart.DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_TOP
import com.androidx.stockchart.DEFAULT_K_CHART_LINE_CHART_STROKE_WIDTH
import com.androidx.stockchart.DEFAULT_TIME_BAR_LABEL_TEXT_SIZE
import com.androidx.stockchart.StockChartConfig
import com.androidx.stockchart.childchart.base.AbsChildChartFactory
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
import com.androidx.stockchart.childchart.timebar.TimeBarFactory
import com.androidx.stockchart.childchart.volumechart.VolumeChartConfig
import com.androidx.stockchart.childchart.volumechart.VolumeChartFactory
import com.androidx.stockchart.entities.FLAG_EMPTY
import com.androidx.stockchart.entities.Highlight
import com.androidx.stockchart.entities.IKEntity
import com.androidx.stockchart.entities.KEntity
import com.androidx.stockchart.entities.containFlag
import com.androidx.stockchart.index.Index
import com.androidx.stockchart.listener.OnHighlightListener
import com.androidx.stockchart.listener.OnLoadMoreListener
import com.androidx.stockchart.util.DimensionUtil
import com.androidx.stockchart.util.NumberFormatUtil
import com.androidx.stockchart.util.ResourceUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import wb.lib.module_chart.custom.CustomChartConfig
import wb.lib.module_chart.custom.CustomChartFactory
import wb.lib.module_chart.databinding.FragmentChartBinding
import kotlin.reflect.KClass

/**
 * @author Hai
 * @date   2025/4/10 10:02
 * @desc
 */
class ChartFragment:Fragment() {
    private lateinit var binding: FragmentChartBinding
    private var periodOptionButtons = mutableMapOf<View, Period>()
    private var kChartTypeOptionButtons = mutableMapOf<View, KChartConfig.KChartType>()
    private var indexOptionButton = mutableMapOf<View, Index>()

    var period = MutableStateFlow(Period.DAY_TIME)
    private var kChartType: KChartConfig.KChartType = KChartConfig.KChartType.CANDLE()
    private var kChartIndex: Index?=null

    // 总配置
    val stockChartConfig = StockChartConfig()

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
    var isMatchHeight = false
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
        binding = FragmentChartBinding.inflate(inflater)
        return binding.root
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
        binding.startAnim.setOnClickListener {
            kChartConfig.preClosePrice?.let {
                lastPrice = it * (0.99f + Math.random().toFloat()*0.02f)
            }
        }

        setIsDev(isDev)
        if(isDev){
            changePeriod(Period.DAY)
        }else{
            changePeriod(Period.DAY_TIME)
        }

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
            binding.llConfig?.visibility = View.VISIBLE
        }else{
            binding.llConfig?.visibility = View.GONE
        }
    }
    private fun initChartTypeButtons() {
        binding.llOptions.kchartTypeSimple.setOnClickListener {
            changeChartType(0)
        }
        binding.llOptions.kchartTypeProfession.setOnClickListener {
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
//            chartMainDisplayAreaPaddingLeft = DimensionUtil.dp2px(this@ChartFragment.requireContext(),25f).toFloat()
//            chartMainDisplayAreaPaddingRight = DimensionUtil.dp2px(this@ChartFragment.requireContext(),25f).toFloat()
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
            scaleFactorMax = 5f

            // 最小缩放比例
            scaleFactorMin = 0.5f

            // 网格线设置
            gridVerticalLineCount = 0
            gridHorizontalLineCount = 5

            horizontalGridLineTopOffsetCalculator = {
                kChartConfig.chartMainDisplayAreaPaddingTop+kChartConfig.marginTop
            }
            horizontalGridLineSpaceCalculator={
                (kChartConfig.height - kChartConfig.chartMainDisplayAreaPaddingTop-kChartConfig.chartMainDisplayAreaPaddingBottom-kChartConfig.marginTop-kChartConfig.marginBottom)/(gridHorizontalLineCount-1f)
            }

            onLoadMoreListener = this@ChartFragment.onLoadMoreListener
        }


        // 绑定配置
        binding.stockChart.setConfig(stockChartConfig)
    }

    fun getPreNotEmptyEntry(index: Int): IKEntity?{
        for (idx in  index-1 downTo 0){
            val kEntity = stockChartConfig.getKEntity(idx)
            if (kEntity?.containFlag(FLAG_EMPTY) != true) {
                return kEntity
            }
        }
        return null
    }

   var performLayout=false
    override fun onResume() {
        super.onResume()
        if (isMatchHeight && !performLayout){
            performLayout = true
            binding.flRoot.apply {
                viewTreeObserver.addOnGlobalLayoutListener(object :OnGlobalLayoutListener{
                    override fun onGlobalLayout() {
                        if(binding.flRoot.height>0){
                            var radio = 2.8f
                            var totalHeight = view?.height?:0
                            val unit = (totalHeight -60f)/(radio+1)
                            kChartConfig.height =(unit*radio).toInt()
                            volumeChartConfig.height =unit.toInt()
                            rsiChartConfig.height =unit.toInt()
                            kdjChartConfig.height =unit.toInt()
                            volumeChartConfig.height =unit.toInt()
                            macdChartConfig.height =unit.toInt()
                            binding.stockChart.notifyChanged()
                            viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                    }
                })
                if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT){
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                }
                requestLayout()
            }
        }
    }
    /**
     * K线图初始化
     */
    private fun initKChart() {
        kChartFactory = KChartFactory(binding.stockChart, kChartConfig)

        kChartConfig.apply {
            // 指标线宽度
            indexStrokeWidth = DimensionUtil.dp2px(this@ChartFragment.requireContext(), 0.5f).toFloat()
            drawBorder = true
            // 监听长按信息
            onHighlightListener = object : OnHighlightListener {
                override fun onHighlightBegin() {
                    this@ChartFragment.onHighlightListener?.onHighlightBegin()
                }

                override fun onHighlightEnd() {
                    binding.tvHighlightInfo.text = ""
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
                            val firstIdx = binding.stockChart.findFirstNotEmptyKEntityIdxInDisplayArea()
                            val price =
                                "最新价:${NumberFormatUtil.formatPrice(kEntity.getClosePrice())}"
                            var changeRatio = "涨跌幅:——"
                            firstIdx?.let{ it ->
                                changeRatio = "涨跌幅:${Util.formatChangeRatio(kEntity.getClosePrice(),kEntities[it].getClosePrice())}"
                            }
                            val volume = "成交量:${Util.formatVolume(kEntity.getVolume())}"

                            showContent = "$price，$changeRatio，$volume"
                        } else {
                            Log.d("shh", "--->${highlight.entry == kEntity}")
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
                    this@ChartFragment.onHighlightListener?.onHighlight(highlight)
                }
            }

            // 图高度
            height = DimensionUtil.dp2px(this@ChartFragment.requireContext(), 170f)

            // 左侧标签设置
            leftLabelConfig = KChartConfig.LabelConfig(
                3,
                { "${NumberFormatUtil.formatPrice(it)}" },
                DEFAULT_TIME_BAR_LABEL_TEXT_SIZE,
                resources.getColor(R.color.stock_chart_axis_y_label),
                DimensionUtil.dp2px(this@ChartFragment.requireContext(), 5f).toFloat(),0f,0f,
                {
                    when (NumberFormatUtil.formatPrice(preClosePrice?:it)) {
                        NumberFormatUtil.formatPrice(it)-> stockChartConfig.equalColor
                        else -> if(it>preClosePrice!!) stockChartConfig.riseColor else stockChartConfig.downColor
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
        volumeChartFactory = VolumeChartFactory(binding.stockChart, volumeChartConfig)

        volumeChartConfig.apply {
            // 图高度
            height = DimensionUtil.dp2px(this@ChartFragment.requireContext(), 40f)
            drawBorder = true

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
        kdjChartFactory = KdjChartFactory(binding.stockChart, kdjChartConfig)

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
        rsiChartFactory = RsiChartFactory(binding.stockChart, rsiChartConfig)

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
        customChartFactory = CustomChartFactory(binding.stockChart, customChartConfig)
        customChartConfig.apply {
            height = DimensionUtil.dp2px(this@ChartFragment.requireContext(), 50f)
            bigLabel = "这是自定义子图示例"
        }
    }

    fun doAfterLoad(
        kEntities: List<IKEntity>,
        preClosePrice: Float?=null,
        timeBarType: TimeBarConfig.Type,
        appendDirect: Int=0,
        initialPageSize:Int? = 48
    ) {

        // 设置时间条样式
        timeBarConfig.type = timeBarType
        if(timeBarType is  TimeBarConfig.Type.DayTime){
            stockChartConfig.xValueMin = 0.0f
            stockChartConfig.xValueMax = timeBarType.totalPoint!!.toFloat()//一个点就需要0-1坐标
            kChartConfig.preClosePrice = preClosePrice
            kChartConfig.chartMainDisplayAreaPaddingTop = DEFAULT_K_CHART_LINE_CHART_STROKE_WIDTH/2
            kChartConfig.chartMainDisplayAreaPaddingBottom = DEFAULT_K_CHART_LINE_CHART_STROKE_WIDTH/2
            kChartConfig.marginTop = 0
            kChartConfig.leftLabelConfig?.count = 3
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
            kChartConfig.chartMainDisplayAreaPaddingTop = DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_TOP/2
            kChartConfig.marginTop = (DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_TOP/2).toInt()
            kChartConfig.chartMainDisplayAreaPaddingBottom = 0f
            kChartConfig.leftLabelConfig?.count = 5
        }


        // 设置数据
        if(appendDirect>0){
            stockChartConfig.appendRightKEntities(kEntities)
        }else if(appendDirect<0){
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
        binding.stockChart.notifyChanged()
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
                    overScrollAble = false
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
                changeIndexType(index)
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

        binding.llOptions.custom.isSelected = stockChartConfig.childChartFactories.contains(customChartFactory!!)

        binding.llOptions.kchartTypeSimple.isSelected = currentType == 0
        binding.llOptions.kchartTypeProfession.isSelected = currentType != 0
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
                gridLineColor = ResourceUtil.getColor(R.color.stock_chart_grid)
                addChildChart(timeBarFactory!!,1)
            }
            kChartConfig.apply {
                avgLineColor = ResourceUtil.getColor(com.androidx.stock_chart.R.color.stock_chart_avg_price_line)
                preCloseLineColor = ResourceUtil.getColor(com.androidx.stock_chart.R.color.stock_chart_pre_close_price_line)

                // 左侧标签设置
                leftLabelConfig = KChartConfig.LabelConfig(
                    3,
                    { "${NumberFormatUtil.formatPrice(it)}" },
                    DEFAULT_TIME_BAR_LABEL_TEXT_SIZE,
                    ResourceUtil.getColor(com.androidx.stock_chart.R.color.stock_chart_axis_y_label),
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
        binding.stockChart.notifyChanged()
        currentType = i
        refreshOptionButtonsState()
    }

    var lastPrice:Float?=null
        set(value) {
            field = value?.also {
                if(period.value == Period.DAY_TIME && it>0.0){
                    kChartConfig.updateTimeDayLast(it,stockChartConfig.getKEntitiesSize()< (stockChartConfig.xValueMax?.toInt()?:0))
                    binding.stockChart.notifyChanged()
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
        binding.stockChart.notifyChanged()
        refreshOptionButtonsState()
    }

    var lastFactory: AbsChildChartFactory<*>? = null
    fun changeIndexTypeOnlyOne(index: Index){
        if (lastFactory == null) {
            lastFactory = volumeChartFactory
        }
        if (index::class in arrayOf(Index.MA::class, Index.EMA::class, Index.BOLL::class)){
                if (period.value == Period.DAY_TIME || period.value == Period.FIVE_DAYS) return

                kChartIndex =
                    if (kChartIndex != null && kChartIndex!!::class == index::class) {
                        null
                    } else {
                        index
                    }
                kChartConfig.index = kChartIndex
        }else{
           var currentFactory =  when (index::class) {
                Index.MACD::class -> {
                    macdChartFactory
                }
                Index.KDJ::class -> {
                    kdjChartFactory
                }
                Index.RSI::class -> {
                    rsiChartFactory
                }
                else -> {
                    volumeChartFactory
                }
            }
            if(lastFactory!=currentFactory){
                lastFactory?.let {
                    stockChartConfig.removeChildCharts(it)
                }
                stockChartConfig.addChildCharts(currentFactory!!)
                lastFactory = currentFactory
            }
        }

        binding.stockChart.notifyChanged()
        refreshOptionButtonsState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scope?.cancel()
        scope = null
    }
}
