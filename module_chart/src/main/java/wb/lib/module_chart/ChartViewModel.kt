//package com.github.wangyiqian.stockchart.sample.sample4
//
//import android.widget.Toast
//import androidx.lifecycle.viewModelScope
//import com.github.wangyiqian.stockchart.DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_BOTTOM
//import com.github.wangyiqian.stockchart.DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_TOP
//import com.github.wangyiqian.stockchart.childchart.timebar.TimeBarConfig
//import com.github.wangyiqian.stockchart.entities.IKEntity
//import com.github.wangyiqian.stockchart.sample.DataMock
//import com.wbroker.core.base.BaseAction
//import com.wbroker.core.base.BaseState
//import com.wbroker.core.base.BaseViewModel
//import kotlinx.android.synthetic.main.fragment_chart.stock_chart
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.launch
//
///**
// * @author Hai
// * @date   2025/4/10 10:17
// * @desc
// */
//private class ChartViewModel: BaseViewModel<UIStatus, ChartAction>(UIStatus.Loading) {
//
//    private var isLoading = false
//    private var assetId:String?=null
//    private var job: Job?=null
//
//    // 加载模拟数据
//    fun loadData(page: Int = 0, period: Period) {
//        job?.cancel()
//        job = null
//        job = viewModelScope.launch {
//
//        }
//        isLoading = true
//
//        fun doAfterLoad(
//            kEntities: List<IKEntity>,
//            initialPageSize: Int?,
//            timeBarType: TimeBarConfig.Type
//        ) {
//            if (kEntities.isNotEmpty()) {
//                // 设置数据
//                if (page == 0) {
//                    if (initialPageSize != null) {
//                        stockChartConfig.setKEntities(
//                            kEntities,
//                            kEntities.size - initialPageSize,
//                            kEntities.size - 1
//                        )
//                    } else {
//                        stockChartConfig.setKEntities(kEntities)
//                    }
//
//                } else {
//                    stockChartConfig.appendLeftKEntities(kEntities)
//                }
//
//                // 设置时间条样式
//                timeBarConfig.type = timeBarType
//                if(timeBarType is  TimeBarConfig.Type.DayTime){
//                    stockChartConfig.xValueMin = 0.0f
//                    stockChartConfig.xValueMax = 100.0f
//                    kChartConfig.preClosePrice = 622.0f
//                    kChartConfig.chartMainDisplayAreaPaddingTop = 0f
//                    kChartConfig.chartMainDisplayAreaPaddingBottom = 0f
//                    timeBarType.labelParis = mutableMapOf(0 to "9:31",50 to "11:30" ,100 to "15:30")
//                }else{
//                    stockChartConfig.xValueMin = null
//                    stockChartConfig.xValueMax = null
//                    kChartConfig.preClosePrice = null
//                    kChartConfig.chartMainDisplayAreaPaddingTop = DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_TOP
//                    kChartConfig.chartMainDisplayAreaPaddingBottom = DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_BOTTOM
//                }
//                // 通知更新
//                stock_chart.notifyChanged()
//                currentPage = page
//            } else {
//                Toast.makeText(requireContext(), "没有更多数据了！", Toast.LENGTH_SHORT).show()
//            }
//            isLoading = false
//        }
//
//        when (period) {
//            Period.DAY -> {
//                DataMock.loadDayData(requireContext(), page) { list ->
//                    doAfterLoad(list, 60, TimeBarConfig.Type.Day())
//                }
//            }
//            Period.FIVE_DAYS -> {
//                DataMock.loadFiveDayData(requireContext()) { list ->
//                    doAfterLoad(list, null, TimeBarConfig.Type.FiveDays())
//                }
//            }
//            Period.WEEK -> {
//                DataMock.loadWeekData(requireContext(), page) { list ->
//                    doAfterLoad(list, 60, TimeBarConfig.Type.Week())
//                }
//            }
//            Period.MONTH -> {
//                DataMock.loadMonthData(requireContext(), page) { list ->
//                    doAfterLoad(list, 60, TimeBarConfig.Type.Month())
//                }
//            }
//            Period.QUARTER -> {
//                DataMock.loadQuarterData(requireContext()) { list ->
//                    doAfterLoad(list, null, TimeBarConfig.Type.Quarter())
//                }
//            }
//            Period.YEAR -> {
//                DataMock.loadYearData(requireContext()) { list ->
//                    doAfterLoad(list, null, TimeBarConfig.Type.Year())
//                }
//            }
//            Period.FIVE_YEARS -> {
//                DataMock.loadFiveYearData(requireContext()) { list ->
//                    doAfterLoad(list, null, TimeBarConfig.Type.FiveYears())
//                }
//            }
//            Period.YTD -> {
//                DataMock.loadYTDData(requireContext()) { list ->
//                    doAfterLoad(list, null, TimeBarConfig.Type.YTD())
//                }
//            }
//            Period.ONE_MINUTE -> {
//                DataMock.loadOneMinuteData(requireContext(), page) { list ->
//                    doAfterLoad(list, 60, TimeBarConfig.Type.OneMinute())
//                }
//            }
//            Period.FIVE_MINUTES -> {
//                DataMock.loadFiveMinutesData(requireContext(), page) { list ->
//                    doAfterLoad(list, 60, TimeBarConfig.Type.FiveMinutes())
//                }
//            }
//            Period.THIRTY_MINUTES -> {
//                DataMock.loadSixtyMinutesData(requireContext(), page) { list ->
//                    doAfterLoad(list, 60, TimeBarConfig.Type.ThirtyMinutes())
//                }
//            }
//            Period.SIXTY_MINUTES -> {
//                DataMock.loadSixtyMinutesData(requireContext(), page) { list ->
//                    doAfterLoad(list, 60, TimeBarConfig.Type.SixtyMinutes())
//                }
//            }
//            Period.DAY_TIME -> {
//                DataMock.loadDayTimeData(requireContext()) { list ->
//                    doAfterLoad(list, null, TimeBarConfig.Type.DayTime(labelParis = getOneDayXLabels()))
//                }
//            }
//        }
//    }
//
//    var pointNum:Int=0;
//    fun isDarkStock(assetId:String?)=false
//    fun isOTC(assetId:String?)=false
//    fun isUS_OPTION(assetId:String?)=false
//    fun getOneDayXLabels(sessionId:Int=1)=mutableMapOf<Int,String>().apply {
//        when{
//            assetId?.endsWith(".HK") == true ->{
//                if(isDarkStock(assetId)){
//                    this[0] = "09:30"
//                    this[150] = "12:00/13:00"
//                    this[ChartType.DARK_DAY.pointNum] = "16:00"
//                }else{
//                    this[0] = "16:16"
//                    this[ChartType.HK_ONE_DAY.pointNum] = "18:30"
//                }
//            }
//            assetId?.endsWith(".US") == true -> {
//                if(isOTC(assetId)){
//                    when (sessionId) {
//                        -1 -> {
//                            this[0] = "08:00"
//                            this[ChartType.US_OTC_ONE_DAY_PRE_MARKET.pointNum*2/3] = "09:00"
//                            this[ChartType.US_OTC_ONE_DAY_PRE_MARKET.pointNum] = "09:30"
//                        }
//                        1 -> {
//                            this[0] = "09:30"
//                            this[150] = "12:00"
//                            this[ChartType.US_ONE_DAY.pointNum] = "16:00"
//                        }
//                        else -> {
//                            this[0] = "16:00"
//                            this[ChartType.US_OTC_ONE_DAY_AFTER_MARKET.pointNum/2] = "17:00"
//                            this[ChartType.US_OTC_ONE_DAY_AFTER_MARKET.pointNum] = "18:00"
//                        }
//                    }
//                }else if(isUS_OPTION(assetId)){
//                    this[0] = "09:30"
//                    this[150] = "12:00"
//                    this[ChartType.US_ONE_DAY.pointNum] = "16:00"
//                }else{
//                    when (sessionId) {
//                        -1 -> {
//                            this[0] = "04:00"
//                            this[ChartType.US_ONE_DAY_PRE_MARKET.pointNum/2] = "07:00"
//                            this[ChartType.US_ONE_DAY_PRE_MARKET.pointNum] = "09:30"
//                        }
//                        1 -> {
//                            this[0] = "09:30"
//                            this[150] = "12:00"
//                            this[ChartType.US_ONE_DAY.pointNum] = "16:00"
//                        }
//                        else -> {
//                            this[0] = "16:00"
//                            this[ChartType.US_ONE_DAY_AFTER_MARKET.pointNum/2] = "18:00"
//                            this[ChartType.US_ONE_DAY_AFTER_MARKET.pointNum] = "20:00"
//                        }
//                    }
//                }
//            }
//            else -> {
//                this[0] = "09:30"
//                this[ChartType.ONE_DAY.pointNum/2] = "11:30/13:00"
//                this[ChartType.ONE_DAY.pointNum] = "15:00"
//            }
//        }
//    }
//
//}
//
//internal sealed interface ChartAction: BaseAction<UIStatus>{
//    class ChartDateLoadSuccess(private val datas:List<IKEntity>):ChartAction{
//        override fun reduce(state: UIStatus)=UIStatus.Content(datas)
//    }
//
//    object ChartDateLoadFailure : ChartAction {
//        override fun reduce(state: UIStatus) = UIStatus.Error
//    }
//}
//
//internal sealed interface UIStatus:BaseState{
//    data class Content(val kEntities:List<IKEntity>):UIStatus
//    object Loading:UIStatus
//    object Error:UIStatus
//}