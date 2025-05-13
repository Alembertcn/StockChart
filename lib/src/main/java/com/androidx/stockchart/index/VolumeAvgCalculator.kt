package com.androidx.stockchart.index

import com.androidx.stockchart.entities.FLAG_EMPTY
import com.androidx.stockchart.entities.IKEntity
import com.androidx.stockchart.entities.containFlag

/**
 * 成交量均线指标
 */
object VolumeAvgCalculator: ICalculator {
    override fun calculate(
        param: String,
        input: List<IKEntity>
    ): List<List<Float?>> {
        val day = param.toIntOrNull()?:5
        val data = mutableListOf<Float?>()
        for (i in input.indices){
            if(input[i].containFlag(FLAG_EMPTY) || i<day-1){
                data.add(null)
            }else{
                var sum =0f
                for (j in 0 until day){
                    sum+=input[i-j].getVolume()
                }
                data.add(sum/day)
            }
        }
        return listOf(data)
    }
}