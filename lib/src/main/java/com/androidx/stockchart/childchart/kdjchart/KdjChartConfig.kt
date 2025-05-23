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

package com.androidx.stockchart.childchart.kdjchart

import com.androidx.stock_chart.R
import com.androidx.stockchart.childchart.base.*
import com.androidx.stockchart.index.Index
import com.androidx.stockchart.listener.OnHighlightListener
import com.androidx.stockchart.util.ResourceUtil

/**
 * @author hai
 * @version 创建时间: 2021/2/18
 */
class KdjChartConfig(
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
    // k线颜色
    var kLineColor: Int =  ResourceUtil.getColor(R.color.stock_chart_kdj_k),
    // k线宽度
    var kLineStrokeWidth: Float = com.androidx.stockchart.DEFAULT_KDJ_K_LINE_STROKE_WIDTH,
    // d线颜色
    var dLineColor: Int = ResourceUtil.getColor(R.color.stock_chart_kdj_d),
    // d线宽度
    var dLineStrokeWidth: Float = com.androidx.stockchart.DEFAULT_KDJ_D_LINE_STROKE_WIDTH,
    // j线颜色
    var jLineColor: Int = ResourceUtil.getColor(R.color.stock_chart_kdj_j),
    // j线宽度
    var jLineStrokeWidth: Float = com.androidx.stockchart.DEFAULT_KDJ_J_LINE_STROKE_WIDTH,
    // 需要展示的指标配置
    var index: Index? = Index.KDJ()
) : BaseChildChartConfig(
    height,
    marginTop,
    marginBottom,
    onHighlightListener,
    chartMainDisplayAreaPaddingTop,
    chartMainDisplayAreaPaddingBottom
)