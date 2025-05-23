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

package com.androidx.stockchart.childchart.volumechart

import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import com.androidx.stock_chart.R
import com.androidx.stockchart.childchart.base.*
import com.androidx.stockchart.index.Index
import com.androidx.stockchart.listener.OnHighlightListener
import com.androidx.stockchart.util.ResourceUtil

/**
 * 成交量图配置
 * @author hai
 * @version 创建时间: 2021/2/7
 */
class VolumeChartConfig(
    height: Int = com.androidx.stockchart.DEFAULT_CHILD_CHART_HEIGHT,
    marginTop: Int = com.androidx.stockchart.DEFAULT_CHILD_CHART_MARGIN_TOP,
    marginBottom: Int = com.androidx.stockchart.DEFAULT_CHILD_CHART_MARGIN_BOTTOM,
    onHighlightListener: OnHighlightListener? = null,
    chartMainDisplayAreaPaddingTop: Float = com.androidx.stockchart.DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_TOP /2,
    chartMainDisplayAreaPaddingBottom: Float = 0f,
    // 柱子样式
    var volumeChartType: VolumeChartType = VolumeChartType.CANDLE(),
    // 长按时高亮线左侧标签配置
    var highlightLabelLeft: HighlightLabelConfig? = null,
    // 长按时高亮线右侧标签配置
    var highlightLabelRight: HighlightLabelConfig? = null,
    // 柱子之间的空间占比柱子宽度
    var barSpaceRatio: Float = com.androidx.stockchart.DEFAULT_VOLUME_BAR_SPACE_RATIO,
    // 柱子空心时的线条宽度
    var hollowChartLineStrokeWidth: Float = com.androidx.stockchart.DEFAULT_VOLUME_CHART_HOLLOW_CHART_LINE_STROKE_WIDTH,

    // 需要展示的指标配置
    var index: Index? = Index.VOL(),
    // 指标头文字背景色
    var indexStarterBgColor: Int = Color.TRANSPARENT,
    // 指标头文字背景水平内间距
    var indexStarterBgPaddingHorizontal: Float = 0f,
    // 指标头文字右侧图标
    var indexStarterRightIcon: Bitmap? = null,
    // 指标文字颜色
    var indexTextColor: Int = ResourceUtil.getColor(R.color.stock_chart_index_start_text),
    // 指标头文字点击事件
    var indexStarterClickListener: ((View) -> Unit)? = null
) : BaseChildChartConfig(
    height,
    marginTop,
    marginBottom,
    onHighlightListener,
    chartMainDisplayAreaPaddingTop,
    chartMainDisplayAreaPaddingBottom
) {

    sealed class VolumeChartType {
        // 实心
        class CANDLE : VolumeChartType()
        // 空心
        class HOLLOW : VolumeChartType()
    }

}