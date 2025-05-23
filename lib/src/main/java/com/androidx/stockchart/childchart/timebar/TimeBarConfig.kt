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

import android.graphics.Color
import androidx.annotation.ColorInt
import com.androidx.stockchart.childchart.base.BaseChildChartConfig
import com.androidx.stockchart.listener.OnHighlightListener
import com.androidx.stockchart.util.ResourceUtil
import java.text.DateFormat
import java.text.SimpleDateFormat
import com.androidx.stock_chart.R


/**
 * @author hai
 * @version 创建时间: 2021/2/22
 */
class TimeBarConfig(
    height: Int = com.androidx.stockchart.DEFAULT_TIME_BAR_HEIGHT,
    marginTop: Int = com.androidx.stockchart.DEFAULT_CHILD_CHART_MARGIN_TOP,
    marginBottom: Int = com.androidx.stockchart.DEFAULT_CHILD_CHART_MARGIN_BOTTOM,
    onHighlightListener: OnHighlightListener? = null,
    chartMainDisplayAreaPaddingTop: Float = 0f,
    chartMainDisplayAreaPaddingBottom: Float = 0f,
    // 背景色
    @ColorInt var backGroundColor: Int = Color.TRANSPARENT,
    // 标签文本大小
    var labelTextSize: Float = com.androidx.stockchart.DEFAULT_TIME_BAR_LABEL_TEXT_SIZE,
    // 标签文本色
    @ColorInt var labelTextColor: Int = ResourceUtil.getColor(R.color.stock_chart_axis_x_label),
    // 长按显示的标签文本大小
    var highlightLabelTextSize: Float = com.androidx.stockchart.DEFAULT_TIME_BAR_HIGHLIGHT_LABEL_TEXT_SIZE,
    // 长按显示的标签文本色
    @ColorInt var highlightLabelTextColor: Int = ResourceUtil.getColor(R.color.stock_chart_highlight_label),
    // 长按显示的标签背景色
    @ColorInt var highlightLabelBgColor: Int =  ResourceUtil.getColor(R.color.stock_chart_highlight_label_bg),
    // 时间条样式
    var type: Type = Type.Day()
) : BaseChildChartConfig(
    height,
    marginTop,
    marginBottom,
    onHighlightListener,
    chartMainDisplayAreaPaddingTop,
    chartMainDisplayAreaPaddingBottom
) {

    sealed class Type(val labelDateFormat: DateFormat, val highlightLabelDateFormat: DateFormat,val diffLabelDateFormat:DateFormat=SimpleDateFormat("yyyy/MM/dd")) {

        class Day(
            labelDateFormat: DateFormat = SimpleDateFormat("MM/dd"),
            highlightLabelDateFormat: DateFormat = SimpleDateFormat("MM/dd"),
        ) : Type(labelDateFormat, highlightLabelDateFormat)

        class FiveDays(
            labelDateFormat: DateFormat = SimpleDateFormat("MM/dd"),
            highlightLabelDateFormat: DateFormat = SimpleDateFormat("HH:mm")
        ) : Type(labelDateFormat, highlightLabelDateFormat)

        class Week(
            labelDateFormat: DateFormat = SimpleDateFormat("MM/dd"),
            highlightLabelDateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd")
        ) : Type(labelDateFormat, highlightLabelDateFormat)

        class Month(
            labelDateFormat: DateFormat = SimpleDateFormat("MM/dd"),
            highlightLabelDateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd"),
            diffLabelDateFormat: DateFormat = SimpleDateFormat("yyyy/MM")
        ) : Type(labelDateFormat, highlightLabelDateFormat, diffLabelDateFormat)

        class Quarter(
            labelDateFormat: DateFormat = SimpleDateFormat("yyyy/MM"),
            highlightLabelDateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd")
        ) : Type(labelDateFormat, highlightLabelDateFormat)

        class Year(
            labelDateFormat: DateFormat = SimpleDateFormat("yyyy/MM"),
            highlightLabelDateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd")
        ) : Type(labelDateFormat, highlightLabelDateFormat)

        class FiveYears(
            labelDateFormat: DateFormat = SimpleDateFormat("yyyy/MM"),
            highlightLabelDateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd")
        ) : Type(labelDateFormat, highlightLabelDateFormat)

        class YTD(
            labelDateFormat: DateFormat = SimpleDateFormat("yyyy/MM"),
            highlightLabelDateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd")
        ) : Type(labelDateFormat, highlightLabelDateFormat)

        class OneMinute(
            labelDateFormat: DateFormat = SimpleDateFormat("HH:mm"),
            highlightLabelDateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm"),
            diffLabelDateFormat: DateFormat = SimpleDateFormat("MM/dd HH:mm")
        ) : Type(labelDateFormat, highlightLabelDateFormat,diffLabelDateFormat)

        class FiveMinutes(
            labelDateFormat: DateFormat = SimpleDateFormat("HH:mm"),
            highlightLabelDateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm"),
            diffLabelDateFormat: DateFormat = SimpleDateFormat("MM/dd HH:mm")
        ) : Type(labelDateFormat, highlightLabelDateFormat,diffLabelDateFormat)

        class ThirtyMinutes(
            labelDateFormat: DateFormat = SimpleDateFormat("HH:mm"),
            highlightLabelDateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm"),
            diffLabelDateFormat: DateFormat = SimpleDateFormat("MM/dd HH:mm")
        ) : Type(labelDateFormat, highlightLabelDateFormat,diffLabelDateFormat)

        class SixtyMinutes(
            labelDateFormat: DateFormat = SimpleDateFormat("HH:mm"),
            highlightLabelDateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm"),
            diffLabelDateFormat: DateFormat = SimpleDateFormat("MM/dd HH:mm")
        ) : Type(labelDateFormat, highlightLabelDateFormat,diffLabelDateFormat)

        class DayTime(
            labelDateFormat: DateFormat = SimpleDateFormat("HH:mm"),
            highlightLabelDateFormat: DateFormat = SimpleDateFormat("HH:mm"),
            var labelParis:Map<Int,String>? = mutableMapOf(0 to "9:00",50 to "11:00",100 to "15:00"),
            var totalPoint:Int?=null
        ) : Type(labelDateFormat, highlightLabelDateFormat)
    }

}