package com.androidx.stockchart.index

import com.androidx.stockchart.entities.IKEntity
import kotlin.math.abs

object ATRCalculator : ICalculator {
    override fun calculate(
        param: String,
        input: List<IKEntity>
    ): List<List<Float?>> {
        val period = param.toIntOrNull()?: 14  // 默认周期为14日

        // 按时间排序确保数据顺序正确
        val sortedInput = input.sortedBy { it.getTime() }

        // 计算TR（真实波幅）列表
        val trList = sortedInput.mapIndexed { index, entity ->
            if (index == 0) {
                // 首日TR = 当日最高价 - 当日最低价
                entity.getHighPrice() - entity.getLowPrice()
            } else {
                val prevClose = sortedInput[index - 1].getClosePrice()
                maxOf(
                    entity.getHighPrice() - entity.getLowPrice(),
                    abs(entity.getHighPrice() - prevClose),
                    abs(entity.getLowPrice() - prevClose)
                )
            }
        }

        // 初始化ATR列表（填充null）
        val atrList = MutableList<Float?>(sortedInput.size) { null }

        if (sortedInput.size >= period) {
            // 计算初始ATR（前period日TR的简单平均）
            val initialATR = trList.subList(0, period).average().toFloat()
            atrList[period - 1] = initialATR  // 第14日写入首个ATR值

            // 计算后续ATR（平滑移动平均）
            for (i in period until sortedInput.size) {
                val previousATR = atrList[i - 1] ?: 0f
                val currentTR = trList[i]
                atrList[i] = (previousATR * (period - 1) + currentTR) / period
            }
        }

        // 返回格式要求：List<List<Float?>>
        return listOf(trList,atrList)
    }
}