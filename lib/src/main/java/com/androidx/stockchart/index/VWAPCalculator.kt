package com.androidx.stockchart.index

import com.androidx.stockchart.entities.IKEntity

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
object VWAPCalculator : ICalculator {
    override fun calculate(
        param: String,
        input: List<IKEntity>
    ): List<List<Float?>> {
        val sortedInput = input.sortedBy { it.getTime() }

        // 修复点1：使用可排序的日期字符串作为键
        val dailyGroups = LinkedHashMap<String, MutableList<IKEntity>>().apply {
            sortedInput.forEach { entity ->
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = entity.getTime()
                }
                // 生成自然日级别的键（格式：yyyyMMdd）
                val dayKey = "${calendar.get(Calendar.YEAR)}" +
                        "${calendar.get(Calendar.MONTH) + 1}".padStart(2, '0') +
                        "${calendar.get(Calendar.DAY_OF_MONTH)}".padStart(2, '0')

                getOrPut(dayKey) { mutableListOf() }.add(entity)
            }
        }

        // 修复点2：使用LinkedHashMap保持插入顺序
        val resultMap = mutableMapOf<IKEntity, Float?>()

        dailyGroups.values.forEach { dailyBars ->
            var cumulativeAmount = BigDecimal.ZERO
            var cumulativeVolume = 0L

            dailyBars.sortedBy { it.getTime() }.forEachIndexed { index, bar ->
                bar.getAmount()?.let { amount ->
                    bar.getVolume().takeIf { it > 0 }?.let { volume ->
                        cumulativeAmount += amount
                        cumulativeVolume += volume

                        // 修复点3：增加零值保护
                        if (cumulativeVolume == 0L) {
                            resultMap[bar] = null
                        } else {
                            val vwap = cumulativeAmount.divide(
                                BigDecimal(cumulativeVolume),
                                4,
                                RoundingMode.HALF_UP
                            )
                            // 当日首个有效值向前填充
                            resultMap[bar] = vwap.toFloat().apply {
                                if (index > 0 && resultMap[dailyBars[index - 1]] == null) {
                                    dailyBars.subList(0, index).forEach { prevBar ->
                                        resultMap[prevBar] = this
                                    }
                                }
                            }
                        }
                        return@forEachIndexed
                    }
                }
                resultMap[bar] = null
            }
        }

        // 保持原始输入顺序
        return listOf(sortedInput.map { resultMap[it] })
    }
}