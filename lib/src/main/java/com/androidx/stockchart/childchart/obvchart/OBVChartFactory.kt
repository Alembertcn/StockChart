package com.androidx.stockchart.childchart.obvchart

import com.androidx.stockchart.IStockChart
import com.androidx.stockchart.childchart.base.AbsChildChartFactory

/**
 * @author hai
 * @version 创建时间: 2023/3/9
 */
class OBVChartFactory(stockChart: IStockChart, childChartConfig: OBVChartConfig) :
    AbsChildChartFactory<OBVChartConfig>(stockChart, childChartConfig) {
    override fun createChart() = OBVChart(stockChart, childChartConfig)
}