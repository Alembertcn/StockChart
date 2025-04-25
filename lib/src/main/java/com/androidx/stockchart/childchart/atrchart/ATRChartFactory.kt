package com.androidx.stockchart.childchart.atrchart

import com.androidx.stockchart.IStockChart
import com.androidx.stockchart.childchart.base.AbsChildChartFactory

/**
 * @author hai
 * @version 创建时间: 2023/3/9
 */
class ATRChartFactory(stockChart: IStockChart, childChartConfig: ATRChartConfig) :
    AbsChildChartFactory<ATRChartConfig>(stockChart, childChartConfig) {
    override fun createChart() = ATRChart(stockChart, childChartConfig)
}