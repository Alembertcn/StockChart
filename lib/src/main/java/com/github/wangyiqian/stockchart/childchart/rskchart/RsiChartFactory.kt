package com.github.wangyiqian.stockchart.childchart.rskchart

import com.github.wangyiqian.stockchart.IStockChart
import com.github.wangyiqian.stockchart.childchart.base.AbsChildChartFactory

/**
 * @author hai
 * @version 创建时间: 2023/3/9
 */
class RsiChartFactory(stockChart: IStockChart, childChartConfig: RsiChartConfig) :
    AbsChildChartFactory<RsiChartConfig>(stockChart, childChartConfig) {
    override fun createChart() = RsiChart(stockChart, childChartConfig)
}