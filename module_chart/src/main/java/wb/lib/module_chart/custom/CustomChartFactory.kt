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

package wb.lib.module_chart.custom

import com.github.wangyiqian.stockchart.IStockChart
import com.github.wangyiqian.stockchart.childchart.base.AbsChildChartFactory

/**
 * @author hai
 * @version 创建时间: 2021/2/9
 */
class CustomChartFactory(stockChart: IStockChart, childChartConfig: CustomChartConfig) :
    AbsChildChartFactory<CustomChartConfig>(stockChart, childChartConfig) {
    override fun createChart() = CustomChart(stockChart, childChartConfig)
}