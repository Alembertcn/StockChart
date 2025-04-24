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

package com.androidx.stockchart.childchart.kchart

import android.graphics.DashPathEffect
import android.graphics.PathEffect
import com.androidx.stockchart.childchart.base.*
import com.androidx.stockchart.index.Index
import com.androidx.stockchart.listener.OnHighlightListener
import com.androidx.stockchart.util.NumberFormatUtil
import com.androidx.stockchart.util.ResourceUtil
import com.androidx.stock_chart.R
/**
 * K线图配置
 *
 * @author hai
 * @version 创建时间: 2021/2/7
 */
open class KChartConfig(
    height: Int = com.androidx.stockchart.DEFAULT_CHILD_CHART_HEIGHT,
    marginTop: Int = com.androidx.stockchart.DEFAULT_CHILD_CHART_MARGIN_TOP,
    marginBottom: Int = com.androidx.stockchart.DEFAULT_CHILD_CHART_MARGIN_BOTTOM,
    onHighlightListener: OnHighlightListener? = null,
    chartMainDisplayAreaPaddingTop: Float = com.androidx.stockchart.DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_TOP,
    chartMainDisplayAreaPaddingBottom: Float = com.androidx.stockchart.DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_BOTTOM,
    var kChartType: KChartType = KChartType.CANDLE(),
    // 长按时高亮线左侧标签配置
    var highlightLabelLeft: HighlightLabelConfig? = com.androidx.stockchart.DEFAULT_K_CHART_HIGHLIGHT_LABEL_LEFT,
    // 长按时高亮线顶部标签配置
    var highlightLabelTop: HighlightLabelConfig? = null,
    // 长按时高亮线右侧标签配置
    var highlightLabelRight: HighlightLabelConfig? = null,
    // 长按时高亮线底部标签配置
    var highlightLabelBottom: HighlightLabelConfig? = null,
    // 线形图的线条颜色
    var lineChartColor: Int = ResourceUtil.getColor(R.color.stock_chart_line),
    // 线形图的线条宽度
    var lineChartStrokeWidth: Float = com.androidx.stockchart.DEFAULT_K_CHART_LINE_CHART_STROKE_WIDTH,
    // 山峰图线条颜色
    var mountainChartColor: Int = ResourceUtil.getColor(R.color.stock_chart_mountain_line),
    // 山峰图的线条宽度
    var mountainChartStrokeWidth: Float = com.androidx.stockchart.DEFAULT_K_CHART_MOUNTAIN_CHART_STROKE_WIDTH,
    // 山峰图的封闭渐变色
    var mountainChartLinearGradientColors: IntArray = intArrayOf(ResourceUtil.getColor(R.color.stock_chart_mountain_line_gradient_start), ResourceUtil.getColor(R.color.stock_chart_mountain_line_gradient_end)),
    // 蜡烛图的中间线宽度
    var candleChartLineStrokeWidth: Float = com.androidx.stockchart.DEFAULT_K_CHART_CANDLE_CHART_LINE_STROKE_WIDTH,
    // 空心蜡烛图线条宽度
    var hollowChartLineStrokeWidth: Float = com.androidx.stockchart.DEFAULT_K_CHART_HOLLOW_CHART_LINE_STROKE_WIDTH,
    // 美国线图（竹节图）线条宽度
    var barChartLineStrokeWidth: Float = com.androidx.stockchart.DEFAULT_K_CHART_BAR_CHART_LINE_STROKE_WIDTH,
    // 成本线价格
    var costPrice: Float? = null,
    // 成本线颜色
    var costPriceLineColor: Int = ResourceUtil.getColor(R.color.stock_chart_cost_price_line),
    // 成本线宽度
    var costPriceLineWidth: Float = com.androidx.stockchart.DEFAULT_K_CHART_COST_PRICE_LINE_WIDTH,
    // 昨收线价格
    var preClosePrice: Float? = null,
    // 昨收线颜色
    var preCloseLineColor: Int = ResourceUtil.getColor(R.color.stock_chart_pre_close_price_line),
    // 昨收线宽度
    var preCloseLineWidth: Float = com.androidx.stockchart.DEFAULT_K_CHART_COST_PRICE_LINE_WIDTH,
    var preClosePriceLineEffect: PathEffect? = DashPathEffect(floatArrayOf(20f, 10f), 0f),
    // 指标线条宽度
    var indexStrokeWidth: Float = com.androidx.stockchart.DEFAULT_K_CHART_INDEX_STROKE_WIDTH,
    // 柱子之间的空间占比柱子宽度
    var barSpaceRatio: Float = com.androidx.stockchart.DEFAULT_K_CHART_BAR_SPACE_RATIO,
    // 需要展示的指标类型
    var index: Index? = com.androidx.stockchart.DEFAULT_K_CHART_INDEX,
    // 指标线的颜色
    var indexColors: List<Int> = listOf(ResourceUtil.getColor(R.color.stock_chart_index_line1), ResourceUtil.getColor(R.color.stock_chart_index_line2), ResourceUtil.getColor(R.color.stock_chart_index_line3))
    ,
    // 左侧标签配置
    var leftLabelConfig: LabelConfig? = com.androidx.stockchart.DEFAULT_K_CHART_LEFT_LABEL_CONFIG,
    // 右侧标签配置
    var rightLabelConfig: LabelConfig? = null,
    // 是否显示分时均线。若需要显示，K线数据需带有分时均线价格。
    var showAvgLine: Boolean = false,
    // 分时均线颜色
    var avgLineColor: Int = ResourceUtil.getColor(R.color.stock_chart_avg_price_line),
    // 分时均线宽度
    var avgLineStrokeWidth: Float = com.androidx.stockchart.DEFAULT_AVG_LINE_WIDTH,
    // y轴范围最小值，在增加或修改K线数据之前指定才有效
    var yValueMin: Float? = null,
    // y轴范围最大值，在增加或修改K线数据之前指定才有效
    var yValueMax: Float? = null,
    // y轴依赖昨收价的百分比 null表示不依赖昨收
    var minYRangeP: Float? = 0.03f,
    var showCircle: Boolean = false,
    var lastPrice: Float? = null,
    var lastMaxY: Float? = null,
    var lastMinY: Float? = null,
) : BaseChildChartConfig(
    height,
    marginTop,
    marginBottom,
    onHighlightListener,
    chartMainDisplayAreaPaddingTop,
    chartMainDisplayAreaPaddingBottom
) {
    sealed class KChartType(
        var highestAndLowestLabelConfig: HighestAndLowestLabelConfig?,
    ) {
        // 实心蜡烛图
        class CANDLE(
            highestAndLowestLabelConfig: HighestAndLowestLabelConfig = HighestAndLowestLabelConfig(
                { NumberFormatUtil.formatPrice(it) },
                ResourceUtil.getColor(R.color.stock_chart_highest_lowest_label),
                com.androidx.stockchart.DEFAULT_K_CHART_HIGHEST_AND_LOWEST_LABEL_TEXT_SIZE,
                com.androidx.stockchart.DEFAULT_K_CHART_HIGHEST_AND_LOWEST_LABEL_LINE_STROKE_WIDTH,
                com.androidx.stockchart.DEFAULT_K_CHART_HIGHEST_AND_LOWEST_LABEL_LINE_LENGTH
            ),
        ) : KChartType(highestAndLowestLabelConfig)

        // 空心蜡烛图
        class HOLLOW(
            highestAndLowestLabelConfig: HighestAndLowestLabelConfig = HighestAndLowestLabelConfig(
                { NumberFormatUtil.formatPrice(it) },
                ResourceUtil.getColor(R.color.stock_chart_highest_lowest_label),
                com.androidx.stockchart.DEFAULT_K_CHART_HIGHEST_AND_LOWEST_LABEL_TEXT_SIZE,
                com.androidx.stockchart.DEFAULT_K_CHART_HIGHEST_AND_LOWEST_LABEL_LINE_STROKE_WIDTH,
                com.androidx.stockchart.DEFAULT_K_CHART_HIGHEST_AND_LOWEST_LABEL_LINE_LENGTH
            ),
        ) : KChartType(highestAndLowestLabelConfig)

        // 折线图
        class LINE(highestAndLowestLabelConfig: HighestAndLowestLabelConfig? = null) :
            KChartType(highestAndLowestLabelConfig)

        // 山峰图
        class MOUNTAIN(highestAndLowestLabelConfig: HighestAndLowestLabelConfig? = null) :
            KChartType(highestAndLowestLabelConfig)

        // 竹节（美国线）图
        class BAR(
            highestAndLowestLabelConfig: HighestAndLowestLabelConfig = HighestAndLowestLabelConfig(
                { NumberFormatUtil.formatPrice(it) },
                ResourceUtil.getColor(R.color.stock_chart_highest_lowest_label),
                com.androidx.stockchart.DEFAULT_K_CHART_HIGHEST_AND_LOWEST_LABEL_TEXT_SIZE,
                com.androidx.stockchart.DEFAULT_K_CHART_HIGHEST_AND_LOWEST_LABEL_LINE_STROKE_WIDTH,
                com.androidx.stockchart.DEFAULT_K_CHART_HIGHEST_AND_LOWEST_LABEL_LINE_LENGTH
            ),
        ) : KChartType(highestAndLowestLabelConfig)
    }

    /**
     * 最高最低价的标签配置
     */
    class HighestAndLowestLabelConfig(
        // 显示格式化
        var formatter: (price: Float) -> String,
        // 标签颜色
        var labelColor: Int,
        // 标签文字尺寸
        var labelTextSize: Float,
        // 标签线宽度
        var lineStrokeWidth: Float,
        // 标签线长度
        var lineLength: Float,
    )

    /**
     * 标签配置
     */
    class LabelConfig(
        // 标签数
        var count: Int,
        // 显示内容格式化
        var formatter: (price: Float) -> String,
        // 文字大小
        var textSize: Float,
        // 文字颜色
        var textColor: Int,
        // 水平外间距
        var horizontalMargin: Float,
        // 顶部外间距
        var marginTop: Float,
        // 底部外间距
        var marginBottom: Float,
        var textColorFormatter: ((price: Float) -> Int)? = null,
    )

    /**
     * 更新最新价格
     * @return 返回是否需要更新坐标
     */
    fun updateTimeDayLast(last:Float?,show: Boolean=true):Boolean{
        showCircle = show
        lastPrice = last
        if(showCircle && lastPrice!=null && lastMinY!=null && lastMaxY!=null){
          return  lastPrice!! !in lastMinY!!..lastMaxY!!
        }
        return false
    }
}