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

package com.androidx.stockchart.sample.sample3.activechart

import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.PathEffect
import com.androidx.stock_chart.R
import com.androidx.stockchart.childchart.base.BaseChildChartConfig
import com.androidx.stockchart.listener.OnHighlightListener
import com.androidx.stockchart.util.ResourceUtil
import kotlin.IntArray

/**
 * @author hai
 * @version 创建时间: 2021/5/14
 */
class ActiveChartConfig(
    // 图高度
    height: Int = com.androidx.stockchart.DEFAULT_CHILD_CHART_HEIGHT,
    // 顶部外间距
    marginTop: Int = com.androidx.stockchart.DEFAULT_CHILD_CHART_MARGIN_TOP,
    // 底部外间距
    marginBottom: Int = com.androidx.stockchart.DEFAULT_CHILD_CHART_MARGIN_BOTTOM,
    onHighlightListener: OnHighlightListener? = null,
    // K线绘制区域顶部内间距
    chartMainDisplayAreaPaddingTop: Float = com.androidx.stockchart.DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_TOP,
    // K线绘制区域底部内间距
    chartMainDisplayAreaPaddingBottom: Float = com.androidx.stockchart.DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_BOTTOM,
    // 山峰图线条颜色
    var mountainChartColor: Int = ResourceUtil.getColor(R.color.stock_chart_mountain_line),
    // 山峰图的线条宽度
    var mountainChartStrokeWidth: Float = com.androidx.stockchart.DEFAULT_K_CHART_MOUNTAIN_CHART_STROKE_WIDTH,
    // 山峰图的封闭渐变色
    var mountainChartLinearGradientColors: IntArray = intArrayOf(ResourceUtil.getColor(R.color.stock_chart_mountain_line_gradient_start), ResourceUtil.getColor(R.color.stock_chart_mountain_line_gradient_end)),
    // 昨收价
    var preClosePrice: Float? = null,
    // 昨收线颜色
    var preClosePriceLineColor: Int = ResourceUtil.getColor(R.color.stock_chart_pre_close_price_line),
    // 昨收线宽度
    var preCloseLineStrokeWidth: Float = 2f,
    // 昨收线虚线配置
    var preCloseLinePathEffect: PathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0f),
    // 时间文字颜色
    var timeTextColor: Int = Color.parseColor("#7c7b80"),
    // 时间文字大小
    var timeTextSize: Float = 30f,
    // 时间文字水平外间距
    var timeTextMarginH: Float = 10f,
    // 时间文字垂直外间距
    var timeTextMarginV: Float = 10f,
    // 异动绿色值
    var activeColorGreen: Int = Color.parseColor("#01c39e"),
    // 异动红色值
    var activeColorRed: Int = Color.parseColor("#e53248"),
    // 异动点圆圈半径
    var activeCircleRadius: Float = 6f,
    // 异动点圆圈线条宽度
    var activeCircleStrokeWidth: Float = 3f,
    // 异动点指示线宽度
    var activeLineStrokeWidth: Float = 2f,
    // 异动行业名文字大小
    var activeIndustryTextSize: Float = 30f,
    // 异动行业名文字颜色
    var activeIndustryTextColor: Int = Color.parseColor("#ffffff"),
    // 异动行业名字水平内边距
    var activeIndustryTextPaddingH: Float = 10f,
    // 异动行业名字垂直内边距
    var activeIndustryTextPaddingV: Float = 2f,
    // 移动行业被点击的事件监听器
    var onActiveIndustryClickListener: OnActiveIndustryClickListener? = null,
    // 左边时间固定，如果是null，那么左边取第一个数据点的时间
    var fixTimeLeft: String? = null,
    // 右边时间固定，如果是null，那么左边取最后一个数据点的时间
    var fixTimeRight: String? = null
) : BaseChildChartConfig(
    height,
    marginTop,
    marginBottom,
    onHighlightListener,
    chartMainDisplayAreaPaddingTop,
    chartMainDisplayAreaPaddingBottom
)