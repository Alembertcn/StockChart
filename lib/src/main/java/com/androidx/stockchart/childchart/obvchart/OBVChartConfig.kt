package com.androidx.stockchart.childchart.obvchart

import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import com.androidx.stock_chart.R
import com.androidx.stockchart.DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_BOTTOM
import com.androidx.stockchart.DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_TOP
import com.androidx.stockchart.DEFAULT_CHILD_CHART_HEIGHT
import com.androidx.stockchart.DEFAULT_CHILD_CHART_MARGIN_BOTTOM
import com.androidx.stockchart.DEFAULT_CHILD_CHART_MARGIN_TOP
import com.androidx.stockchart.childchart.base.BaseChildChartConfig
import com.androidx.stockchart.childchart.base.HighlightLabelConfig
import com.androidx.stockchart.index.Index
import com.androidx.stockchart.listener.OnHighlightListener
import com.androidx.stockchart.util.ResourceUtil

/**
 * @author hai
 * @version 创建时间: 2023/3/9
 */
class OBVChartConfig(
    height: Int = DEFAULT_CHILD_CHART_HEIGHT,
    marginTop: Int = DEFAULT_CHILD_CHART_MARGIN_TOP,
    marginBottom: Int = DEFAULT_CHILD_CHART_MARGIN_BOTTOM,
    onHighlightListener: OnHighlightListener? = null,
    chartMainDisplayAreaPaddingTop: Float = DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_TOP,
    chartMainDisplayAreaPaddingBottom: Float = DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_BOTTOM,
    // 长按时高亮线左侧标签配置
    var highlightLabelLeft: HighlightLabelConfig? = null,
    // 长按时高亮线右侧标签配置
    var highlightLabelRight: HighlightLabelConfig? = null,
    // 指标线的颜色
    var indexColors: List<Int> = listOf(ResourceUtil.getColor(R.color.stock_chart_index_line1), ResourceUtil.getColor(R.color.stock_chart_index_line2), ResourceUtil.getColor(R.color.stock_chart_index_line3)),
    // 指标线宽度
    var lineStrokeWidth: Float = 3f,
    // 虚线颜色
    var dashLineColor: Int = Color.TRANSPARENT,
    // 需要展示的指标配置
    var index: Index? = Index.OBV(),
    // 指标头文字背景色
    var indexStarterBgColor: Int = Color.TRANSPARENT,
    // 指标头文字背景水平内间距
    var indexStarterBgPaddingHorizontal: Float = 0f,
    // 指标头文字右侧图标
    var indexStarterRightIcon: Bitmap? = null,
    // 指标头文字点击事件
    var indexStarterClickListener: ((View) -> Unit)? = null
) : BaseChildChartConfig(
    height,
    marginTop,
    marginBottom,
    onHighlightListener,
    chartMainDisplayAreaPaddingTop,
    chartMainDisplayAreaPaddingBottom
)