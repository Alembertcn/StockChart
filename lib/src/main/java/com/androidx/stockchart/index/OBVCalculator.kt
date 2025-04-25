package com.androidx.stockchart.index

import com.androidx.stockchart.entities.IKEntity
import kotlin.math.abs

object OBVCalculator : ICalculator {
    override fun calculate(
        param: String,
        input: List<IKEntity>
    ): List<List<Float?>> {
        // 按时间排序确保数据顺序
        val sortedInput = input.sortedBy { it.getTime() }

        // 初始化OBV列表（首日OBV=0）
        val obvList = mutableListOf<Float?>().apply {
            add(0f)  // 首日基准值
        }

        // 从第二根K线开始计算
        for (i in 1 until sortedInput.size) {
            val current = sortedInput[i]
            val prev = sortedInput[i - 1]

            val currentClose = current.getClosePrice()
            val prevClose = prev.getClosePrice()

            // 计算规则
            obvList.add(when {
                currentClose > prevClose -> obvList.last()!! + current.getVolume().toFloat()
                currentClose < prevClose -> obvList.last()!! - current.getVolume().toFloat()
                else -> obvList.last()!!  // 平盘时OBV不变
            })
        }

        // 处理首日特殊逻辑：当输入只有1个数据点时保持0值
        if (sortedInput.size == 1) {
            return listOf(listOf(0f))
        }

        // 返回格式要求：List<List<Float?>>
        return listOf(obvList)
    }
}