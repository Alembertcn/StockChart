package com.androidx.stockchart.childchart.rskchart

import com.androidx.stockchart.IStockChart
import com.androidx.stockchart.childchart.base.AbsChildChartFactory

/**
 * @author hai
 * @version 创建时间: 2023/3/9
 */
class RsiChartFactory(stockChart: IStockChart, childChartConfig: RsiChartConfig) :
    AbsChildChartFactory<RsiChartConfig>(stockChart, childChartConfig) {
    override fun createChart() = RsiChart(stockChart, childChartConfig)
}