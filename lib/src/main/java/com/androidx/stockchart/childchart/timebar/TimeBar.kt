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

package com.androidx.stockchart.childchart.timebar

import android.graphics.Canvas
import android.graphics.Paint
import com.androidx.stockchart.IStockChart
import com.androidx.stockchart.childchart.base.BaseChildChart
import com.androidx.stockchart.entities.FLAG_EMPTY
import com.androidx.stockchart.entities.containFlag
import com.androidx.stockchart.util.DataTimeUtil
import com.androidx.stockchart.util.DimensionUtil
import java.text.DateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * @author hai
 * @version 创建时间: 2021/2/22
 */
class TimeBar(stockChart: IStockChart, chartConfig: TimeBarConfig) :
    BaseChildChart<TimeBarConfig>(stockChart, chartConfig) {

    private val tmpDate by lazy { Date() }

    private val labelPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val highlightLabelPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val highlightLabelBgPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    override fun onKEntitiesChanged() {
    }

    override fun getYValueRange(startIndex: Int, endIndex: Int, result: FloatArray) {
    }

    override fun preDrawBackground(canvas: Canvas) {
    }

    override fun drawBackground(canvas: Canvas) {
        canvas.drawColor(chartConfig.backGroundColor)
    }

    override fun preDrawData(canvas: Canvas) {
    }

    override fun drawData(canvas: Canvas) {

        labelPaint.textSize = chartConfig.labelTextSize
        labelPaint.color = chartConfig.labelTextColor
        labelPaint.getFontMetrics(tmpFontMetrics)

//        when (chartConfig.type) {
//            is TimeBarConfig.Type.Day -> drawLabelOfDayType(canvas)
//            is TimeBarConfig.Type.FiveDays -> drawLabelOfFiveDaysType(canvas)
//            is TimeBarConfig.Type.Week -> drawLabelOfWeekType(canvas)
//            is TimeBarConfig.Type.Month -> drawLabelOfMonthType(canvas)
//            is TimeBarConfig.Type.Quarter -> drawLabelOfQuarterType(canvas)
//            is TimeBarConfig.Type.Year -> drawLabelOfYearType(canvas)
//            is TimeBarConfig.Type.FiveYears -> drawLabelOfFiveYearsType(canvas)
//            is TimeBarConfig.Type.YTD -> drawLabelOfYTDType(canvas)
//            is TimeBarConfig.Type.OneMinute -> drawLabelOfOneMinuteType(canvas)
//            is TimeBarConfig.Type.FiveMinutes -> drawLabelOfFiveMinutesType(canvas)
//            is TimeBarConfig.Type.SixtyMinutes -> drawLabelOfSixtyMinutesType(canvas)
//            is TimeBarConfig.Type.DayTime -> drawLabelOfDayTimeType(canvas)
//        }

        chartConfig.type.let {
            when (it) {
                is TimeBarConfig.Type.OneMinute,is TimeBarConfig.Type.FiveMinutes,is TimeBarConfig.Type.ThirtyMinutes,is TimeBarConfig.Type.SixtyMinutes -> drawLabelOfDynamic(canvas,it.labelDateFormat,it.diffLabelDateFormat,{ o1,o2->
                     !DataTimeUtil.isHalfHourTimePoint(o2)
                },{o1,o2->!DataTimeUtil.isSameDay(o1,o2)})
                is TimeBarConfig.Type.DayTime -> drawLabelOfDayTimeType(canvas)
                else -> drawLabelOfDynamic(canvas,chartConfig.type.labelDateFormat,chartConfig.type.diffLabelDateFormat)
            }
        }

    }

    override fun preDrawHighlight(canvas: Canvas) {
    }

    override fun drawHighlight(canvas: Canvas) {

        highlightLabelPaint.textSize = chartConfig.highlightLabelTextSize
        highlightLabelPaint.color = chartConfig.highlightLabelTextColor
        highlightLabelPaint.getFontMetrics(tmpFontMetrics)
        highlightLabelBgPaint.color = chartConfig.highlightLabelBgColor
        drawHighlightLabel(canvas)
    }

    override fun drawAddition(canvas: Canvas) {
    }

    /**
     * 动态绘制坐标
     *
     * @param canvas
     * @param labelDateFormat
     * @param diffLabelDateFormat
     */
    private fun drawLabelOfDynamic(canvas: Canvas, labelDateFormat: DateFormat, diffLabelDateFormat: DateFormat, sameCheck:(date1:Long, date2:Long)->Boolean = { o1, o2->
        DataTimeUtil.isSameMoth(o1, o2) && DataTimeUtil.isSameYear(o1, o2)
    }, useDiffCheck:(date1:Long,date2:Long)->Boolean = { o1, o2->
        !DataTimeUtil.isSameYear(o1, o2)
    }) {
        var lastDrawRight = getChartMainDisplayArea().left
        val labelMinSpace = DimensionUtil.dp2px(context,20f)
        var lastDrawLabel = ""
        var lastDrawTime = 0L
        val y =  getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
        getKEntities().forEachIndexed { idx, kEntity ->
            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val firstIndex = stockChart.findFirstNotEmptyKEntityIdxInDisplayArea()?:0
            val lastIndex = stockChart.findLastNotEmptyKEntityIdxInDisplayArea()?:Int.MAX_VALUE
            val time = kEntity.getTime()
            if (idx == firstIndex - 1) {
                tmpDate.time=time
                lastDrawTime=time
            }
            if (idx !in firstIndex..lastIndex) {
                return@forEachIndexed
            }

            // 上一个时间
            val preTime = tmpDate.time
            tmpDate.time=time
            // k不展示相同的坐标
            if (sameCheck.invoke(preTime,time)) {
                return@forEachIndexed
            }
            val label =if(!useDiffCheck(lastDrawTime,time) && lastDrawLabel.isNotEmpty()) labelDateFormat.format(tmpDate) else diffLabelDateFormat.format(tmpDate)

            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            tmp2FloatArray[0] = idx + 0.5f
            tmp2FloatArray[1] = 0f
            mapPointsValue2Real(tmp2FloatArray)
            var centerRealX = tmp2FloatArray[0]

            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                val max = max(lastDrawRight +labelHalfWidth,getChartMainDisplayArea().right-labelHalfWidth)
                val min = min(lastDrawRight +labelHalfWidth,getChartMainDisplayArea().right-labelHalfWidth)
                centerRealX = centerRealX.coerceIn(min,max)
            }
            if(centerRealX-labelHalfWidth<lastDrawRight+labelMinSpace){
                return@forEachIndexed
            }
            val x = centerRealX - labelHalfWidth
            canvas.drawText(label, x, y, labelPaint)
            lastDrawRight = x + labelWidth
            lastDrawLabel = label
            lastDrawTime = time
        }
    }
    private fun drawLabelOfDayType(canvas: Canvas) {
        var lastDrawRight = getChartMainDisplayArea().left
        val labelMinSpace = DimensionUtil.dp2px(context,10f)
        var lastDrawLabel = ""
        val y =  getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
        getKEntities().forEachIndexed { idx, kEntity ->
            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val firstIndex = stockChart.findFirstNotEmptyKEntityIdxInDisplayArea()?:0
            val lastIndex = stockChart.findLastNotEmptyKEntityIdxInDisplayArea()?:Int.MAX_VALUE
            val time = kEntity.getTime()
            if (idx == firstIndex - 1) {
                tmpDate.time=time
            }
            if (idx !in firstIndex..lastIndex) {
                return@forEachIndexed
            }
            // 日k不展示月份相同的坐标
            val sameMoth = DataTimeUtil.isSameMoth(time, tmpDate.time)
            val sameYear = DataTimeUtil.isSameYear(time, tmpDate.time)
            if (sameMoth && sameYear) {
                return@forEachIndexed
            }
            tmpDate.time=time
            val label =if(sameYear && lastDrawLabel.isNotEmpty()) chartConfig.type.labelDateFormat.format(tmpDate) else chartConfig.type.diffLabelDateFormat.format(tmpDate)

            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            tmp2FloatArray[0] = idx + 0.5f
            tmp2FloatArray[1] = 0f
            mapPointsValue2Real(tmp2FloatArray)
            var centerRealX = tmp2FloatArray[0]

            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                centerRealX = centerRealX.coerceIn(lastDrawRight+ labelMinSpace +labelHalfWidth,getChartMainDisplayArea().right-labelHalfWidth)
            }
            val x = centerRealX - labelHalfWidth
            canvas.drawText(label, x, y, labelPaint)
            lastDrawRight = x + labelWidth
            lastDrawLabel = label
        }
    }

    private fun drawLabelOfFiveDaysType(canvas: Canvas) {

        var dayBeginKEntityIdx: Int? = null
        var dayEndKEntityIdx: Int? = null
        var tmpLabel = ""

        getKEntities().forEachIndexed { idx, kEntity ->
            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)

            if (tmpLabel != label) {
                if (dayBeginKEntityIdx != null && dayEndKEntityIdx != null) {
                    doDrawLabelOfFiveDaysType(canvas, dayBeginKEntityIdx!!, dayEndKEntityIdx!!)
                    dayBeginKEntityIdx = null
                    dayEndKEntityIdx = null
                }
            }

            tmpLabel = label

            if (dayBeginKEntityIdx == null) {
                dayBeginKEntityIdx = idx
            }

            dayEndKEntityIdx = idx
        }

        if (dayBeginKEntityIdx != null && dayEndKEntityIdx != null) {
            doDrawLabelOfFiveDaysType(canvas, dayBeginKEntityIdx!!, dayEndKEntityIdx!!)
        }
    }

    private fun doDrawLabelOfFiveDaysType(
        canvas: Canvas,
        dayBeginKEntityIdx: Int,
        dayEndKEntityIdx: Int
    ) {
        val time = getKEntities()[dayBeginKEntityIdx].getTime()
        tmpDate.time = time
        val label = chartConfig.type.labelDateFormat.format(tmpDate)

        val labelWidth = labelPaint.measureText(label)
        val labelHalfWidth = labelWidth / 2

        tmp2FloatArray[0] = (dayBeginKEntityIdx + dayEndKEntityIdx) / 2 + 0.5f
        tmp2FloatArray[1] = 0f
        mapPointsValue2Real(tmp2FloatArray)
        val centerRealX = tmp2FloatArray[0]

        if (centerRealX - labelHalfWidth < getChartMainDisplayArea().left || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
            return
        }

        val x = centerRealX - labelHalfWidth
        val y =
            getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
        canvas.drawText(label, x, y, labelPaint)
    }

    private fun drawLabelOfWeekType(canvas: Canvas) {

        var lastDrawRight = getChartMainDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 70f)

        getKEntities().forEachIndexed { idx, kEntity ->
            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)
            if (label == lastDrawLabel) {
                return@forEachIndexed
            }

            lastDrawLabel = label

            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            tmp2FloatArray[0] = idx + 0.5f
            tmp2FloatArray[1] = 0f
            mapPointsValue2Real(tmp2FloatArray)
            val centerRealX = tmp2FloatArray[0]
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                return@forEachIndexed
            }

            val x = centerRealX - labelHalfWidth
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
            canvas.drawText(label, x, y, labelPaint)

            lastDrawRight = x + labelWidth
        }

    }

    private fun drawLabelOfMonthType(canvas: Canvas) {
        var lastDrawRight = getChartMainDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 100f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)
            if (label == lastDrawLabel) {
                return@forEachIndexed
            }

            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            tmp2FloatArray[0] = idx + 0.5f
            tmp2FloatArray[1] = 0f
            mapPointsValue2Real(tmp2FloatArray)
            val centerRealX = tmp2FloatArray[0]
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                return@forEachIndexed
            }

            val x = centerRealX - labelHalfWidth
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
            canvas.drawText(label, x, y, labelPaint)

            lastDrawLabel = label
            lastDrawRight = x + labelWidth
        }
    }

    private fun drawLabelOfQuarterType(canvas: Canvas) {
        var lastDrawRight = getChartMainDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 50f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)
            if (label == lastDrawLabel) {
                return@forEachIndexed
            }

            lastDrawLabel = label

            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            tmp2FloatArray[0] = idx + 0.5f
            tmp2FloatArray[1] = 0f
            mapPointsValue2Real(tmp2FloatArray)
            val centerRealX = tmp2FloatArray[0]
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                return@forEachIndexed
            }

            val x = centerRealX - labelHalfWidth
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
            canvas.drawText(label, x, y, labelPaint)

            lastDrawRight = x + labelWidth
        }
    }

    private fun drawLabelOfYearType(canvas: Canvas) {
        var lastDrawRight = getChartMainDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 30f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)
            if (label == lastDrawLabel) {
                return@forEachIndexed
            }

            lastDrawLabel = label

            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            tmp2FloatArray[0] = idx + 0.5f
            tmp2FloatArray[1] = 0f
            mapPointsValue2Real(tmp2FloatArray)
            val centerRealX = tmp2FloatArray[0]
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                return@forEachIndexed
            }

            val x = centerRealX - labelHalfWidth
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
            canvas.drawText(label, x, y, labelPaint)

            lastDrawRight = x + labelWidth
        }
    }

    private fun drawLabelOfFiveYearsType(canvas: Canvas) {
        var lastDrawRight = getChartMainDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 50f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)
            if (label == lastDrawLabel) {
                return@forEachIndexed
            }

            lastDrawLabel = label

            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            tmp2FloatArray[0] = idx + 0.5f
            tmp2FloatArray[1] = 0f
            mapPointsValue2Real(tmp2FloatArray)
            val centerRealX = tmp2FloatArray[0]
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                return@forEachIndexed
            }

            val x = centerRealX - labelHalfWidth
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
            canvas.drawText(label, x, y, labelPaint)

            lastDrawRight = x + labelWidth
        }
    }

    private fun drawLabelOfAllType(canvas: Canvas) {
        var lastDrawRight = getChartMainDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 40f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)
            if (label == lastDrawLabel) {
                return@forEachIndexed
            }


            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            var x = getChartMainDisplayArea().left
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom

            if (lastDrawLabel != "") {
                tmp2FloatArray[0] = idx + 0.5f
                tmp2FloatArray[1] = 0f
                mapPointsValue2Real(tmp2FloatArray)
                val centerRealX = tmp2FloatArray[0]
                if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                    return@forEachIndexed
                }

                x = centerRealX - labelHalfWidth
            }

            canvas.drawText(label, x, y, labelPaint)

            lastDrawLabel = label
            lastDrawRight = x + labelWidth
        }
    }

    private fun drawLabelOfYTDType(canvas: Canvas) {
        var lastDrawRight = getChartMainDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 30f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)
            if (label == lastDrawLabel) {
                return@forEachIndexed
            }

            lastDrawLabel = label

            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            tmp2FloatArray[0] = idx + 0.5f
            tmp2FloatArray[1] = 0f
            mapPointsValue2Real(tmp2FloatArray)
            val centerRealX = tmp2FloatArray[0]
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                return@forEachIndexed
            }

            val x = centerRealX - labelHalfWidth
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
            canvas.drawText(label, x, y, labelPaint)

            lastDrawRight = x + labelWidth
        }
    }

    private fun drawLabelOfOneMinuteType(canvas: Canvas) {
        var lastDrawRight = getChartMainDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 50f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)
            if (label == lastDrawLabel) {
                return@forEachIndexed
            }

            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            tmp2FloatArray[0] = idx + 0.5f
            tmp2FloatArray[1] = 0f
            mapPointsValue2Real(tmp2FloatArray)
            val centerRealX = tmp2FloatArray[0]
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                return@forEachIndexed
            }

            val x = centerRealX - labelHalfWidth
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
            canvas.drawText(label, x, y, labelPaint)

            lastDrawLabel = label
            lastDrawRight = x + labelWidth
        }
    }

    private fun drawLabelOfFiveMinutesType(canvas: Canvas) {
        var lastDrawRight = getChartMainDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 50f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)
            if (label == lastDrawLabel) {
                return@forEachIndexed
            }

            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            tmp2FloatArray[0] = idx + 0.5f
            tmp2FloatArray[1] = 0f
            mapPointsValue2Real(tmp2FloatArray)
            val centerRealX = tmp2FloatArray[0]
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                return@forEachIndexed
            }

            val x = centerRealX - labelHalfWidth
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
            canvas.drawText(label, x, y, labelPaint)

            lastDrawLabel = label
            lastDrawRight = x + labelWidth
        }
    }

    private fun drawLabelOfSixtyMinutesType(canvas: Canvas) {
        var lastDrawRight = getChartMainDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 50f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)
            if (label == lastDrawLabel) {
                return@forEachIndexed
            }

            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            tmp2FloatArray[0] = idx + 0.5f
            tmp2FloatArray[1] = 0f
            mapPointsValue2Real(tmp2FloatArray)
            val centerRealX = tmp2FloatArray[0]
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                return@forEachIndexed
            }

            val x = centerRealX - labelHalfWidth
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
            canvas.drawText(label, x, y, labelPaint)

            lastDrawLabel = label
            lastDrawRight = x + labelWidth
        }
    }

    private fun drawLabelOfDayTimeType(canvas: Canvas) {
        when (val labelParis = (chartConfig.type as? TimeBarConfig.Type.DayTime)?.labelParis) {
            null -> {
                stockChart.findFirstNotEmptyKEntityIdxInDisplayArea()?.let { idx ->
                    doDrawLabelOfDayTimeType(canvas, idx)
                }

                stockChart.findLastNotEmptyKEntityIdxInDisplayArea()?.let { idx ->
                    doDrawLabelOfDayTimeType(canvas, idx)
                }
            }
            else -> {
                labelParis.entries.forEach {
                    val label = it.value
                    val labelWidth = labelPaint.measureText(label)
                    val labelHalfWidth = labelWidth / 2

                    tmp2FloatArray[0] = it.key + 0.5f
                    tmp2FloatArray[1] = 0f
                    mapPointsValue2Real(tmp2FloatArray)
                    val centerRealX = tmp2FloatArray[0]

                    var x = centerRealX - labelHalfWidth
                    if (x + labelWidth > getChartMainDisplayArea().right ) x = getChartMainDisplayArea().right  - labelWidth
                    if (x < getChartMainDisplayArea().left ) x = getChartMainDisplayArea().left
                    val y =
                        getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
                    canvas.drawText(label, x, y, labelPaint)
                }
            }
        }
    }

    private fun doDrawLabelOfDayTimeType(canvas: Canvas, idx: Int) {
        val labelMinSpace = DimensionUtil.dp2px(context, 5f)

        val kEntity = getKEntities()[idx]
        val time = kEntity.getTime()
        tmpDate.time = time
        val label = chartConfig.type.labelDateFormat.format(tmpDate)

        val labelWidth = labelPaint.measureText(label)
        val labelHalfWidth = labelWidth / 2

        tmp2FloatArray[0] = idx + 0.5f
        tmp2FloatArray[1] = 0f
        mapPointsValue2Real(tmp2FloatArray)
        val centerRealX = tmp2FloatArray[0]

        var x = centerRealX - labelHalfWidth
        if (x + labelWidth > getChartMainDisplayArea().right - labelMinSpace) x =
            getChartMainDisplayArea().right - labelMinSpace - labelWidth
        if (x < getChartMainDisplayArea().left + labelMinSpace) x =
            getChartMainDisplayArea().left + labelMinSpace
        val y =
            getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
        canvas.drawText(label, x, y, labelPaint)
    }

    private fun drawHighlightLabel(canvas: Canvas) {
        getHighlight()?.let { highlight ->

            if (!stockChart.getConfig().showHighlightVerticalLine) return

            val idx = highlight.getIdx()

            if (idx in getKEntities().indices) {

                val kEntity = getKEntities()[idx]

                if (kEntity.containFlag(FLAG_EMPTY)) return

                val time = kEntity.getTime()
                tmpDate.time = time
                val label = chartConfig.type.highlightLabelDateFormat.format(tmpDate)

                val labelWidth = highlightLabelPaint.measureText(label)
                val labelHalfWidth = labelWidth / 2

                tmp2FloatArray[0] = idx + 0.5f
                tmp2FloatArray[1] = 0f
                mapPointsValue2Real(tmp2FloatArray)
                val centerRealX = tmp2FloatArray[0]

                val bgPadding = 10f
                val bgPaddingV = 10f
                var x = centerRealX - labelHalfWidth
                if (x - bgPadding < getChartMainDisplayArea().left) {
                    x = getChartMainDisplayArea().left + bgPadding
                }
                if (x + labelWidth + bgPadding > getChartMainDisplayArea().right) {
                    x = getChartMainDisplayArea().right - labelWidth - bgPadding
                }
                val fontHeight = tmpFontMetrics.bottom - tmpFontMetrics.top
                val y = getChartDisplayArea().top + getChartDisplayArea().height() / 2 + fontHeight / 2 - tmpFontMetrics.bottom
                canvas.drawRoundRect(
                    x - bgPadding,
                    getChartMainDisplayArea().top,
                    x + labelWidth + bgPadding,
                    getChartMainDisplayArea().bottom,
                    10f,10f,
                    highlightLabelBgPaint
                )
                canvas.drawText(label, x, y, highlightLabelPaint)
            }
        }

    }
}