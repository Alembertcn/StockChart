package wb.lib.module_chart.index.qm

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import com.androidx.stock_chart.R
import com.androidx.stockchart.childchart.base.HighlightLabelConfig
import com.androidx.stockchart.childchart.macdchart.MacdChartConfig
import com.androidx.stockchart.index.Index
import com.androidx.stockchart.listener.OnHighlightListener
import com.androidx.stockchart.util.ResourceUtil

class QMChartConfig(height: Int = com.androidx.stockchart.DEFAULT_CHILD_CHART_HEIGHT,
                    marginTop: Int = com.androidx.stockchart.DEFAULT_CHILD_CHART_MARGIN_TOP,
                    marginBottom: Int = com.androidx.stockchart.DEFAULT_CHILD_CHART_MARGIN_BOTTOM,
                    onHighlightListener: OnHighlightListener? = null,
                    chartMainDisplayAreaPaddingTop: Float = com.androidx.stockchart.DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_TOP,
                    chartMainDisplayAreaPaddingBottom: Float = com.androidx.stockchart.DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_BOTTOM,
    // 长按时高亮线左侧标签配置
                    highlightLabelLeft: HighlightLabelConfig? = null,
    // 长按时高亮线右侧标签配置
                    highlightLabelRight: HighlightLabelConfig? = null,
    // dif线颜色
                    difLineColor: Int = ResourceUtil.getColor(R.color.stock_chart_macd_dif),
    // dif线宽度
                    difLineStrokeWidth: Float = com.androidx.stockchart.DEFAULT_MACD_DIF_LINE_STROKE_WIDTH,
    // dea线颜色
                    deaLineColor: Int =  ResourceUtil.getColor(R.color.stock_chart_macd_dea),
    // dea线宽度
                    deaLineStrokeWidth: Float = com.androidx.stockchart.DEFAULT_MACD_DEA_LINE_STROKE_WIDTH,
    // macd文字颜色
                    macdTextColor: Int = ResourceUtil.getColor(R.color.stock_chart_macd_text),
    // 柱子之间的空间占比柱子宽度
                    barSpaceRatio: Float = com.androidx.stockchart.DEFAULT_MACD_BAR_SPACE_RATIO,
    // 需要展示的指标配置
                    index: Index? = QM(),
                    var drawableRes: Int = wb.lib.module_chart.R.mipmap.qm_arrow
): MacdChartConfig(height,marginTop,marginBottom,onHighlightListener,chartMainDisplayAreaPaddingTop,chartMainDisplayAreaPaddingBottom,highlightLabelLeft,highlightLabelRight,difLineColor,difLineStrokeWidth,deaLineColor,deaLineStrokeWidth,macdTextColor,barSpaceRatio,index)