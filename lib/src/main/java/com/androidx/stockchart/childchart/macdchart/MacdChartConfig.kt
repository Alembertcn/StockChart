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

package com.androidx.stockchart.childchart.macdchart

import com.androidx.stockchart.childchart.base.*
import com.androidx.stockchart.index.Index
import com.androidx.stockchart.listener.OnHighlightListener
import com.androidx.stockchart.util.ResourceUtil
import com.androidx.stock_chart.R
/**
 * @author hai
 * @version 创建时间: 2021/2/18
 */
open class MacdChartConfig(
    height: Int = com.androidx.stockchart.DEFAULT_CHILD_CHART_HEIGHT,
    marginTop: Int = com.androidx.stockchart.DEFAULT_CHILD_CHART_MARGIN_TOP,
    marginBottom: Int = com.androidx.stockchart.DEFAULT_CHILD_CHART_MARGIN_BOTTOM,
    onHighlightListener: OnHighlightListener? = null,
    chartMainDisplayAreaPaddingTop: Float = com.androidx.stockchart.DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_TOP,
    chartMainDisplayAreaPaddingBottom: Float = com.androidx.stockchart.DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_BOTTOM,
    // 长按时高亮线左侧标签配置
    var highlightLabelLeft: HighlightLabelConfig? = null,
    // 长按时高亮线右侧标签配置
    var highlightLabelRight: HighlightLabelConfig? = null,
    // dif线颜色
    var difLineColor: Int = ResourceUtil.getColor(R.color.stock_chart_macd_dif),
    // dif线宽度
    var difLineStrokeWidth: Float = com.androidx.stockchart.DEFAULT_MACD_DIF_LINE_STROKE_WIDTH,
    // dea线颜色
    var deaLineColor: Int =  ResourceUtil.getColor(R.color.stock_chart_macd_dea),
    // dea线宽度
    var deaLineStrokeWidth: Float = com.androidx.stockchart.DEFAULT_MACD_DEA_LINE_STROKE_WIDTH,
    // macd文字颜色
    var macdTextColor: Int = ResourceUtil.getColor(R.color.stock_chart_macd_text),
    // 柱子之间的空间占比柱子宽度
    var barSpaceRatio: Float = com.androidx.stockchart.DEFAULT_MACD_BAR_SPACE_RATIO,
    // 需要展示的指标配置
    var index: Index? = Index.MACD()
) : BaseChildChartConfig(
    height,
    marginTop,
    marginBottom,
    onHighlightListener,
    chartMainDisplayAreaPaddingTop,
    chartMainDisplayAreaPaddingBottom
)