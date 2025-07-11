package wb.lib.module_chart.index.qm

import android.util.Log
import com.androidx.stockchart.DefaultIndexParams
import com.androidx.stockchart.entities.FLAG_EMPTY
import com.androidx.stockchart.entities.IKEntity
import com.androidx.stockchart.entities.containFlag
import com.androidx.stockchart.index.BollCalculator
import com.androidx.stockchart.index.ICalculator
import com.androidx.stockchart.index.MACDCalculator
import com.androidx.stockchart.index.MACalculator
import com.androidx.stockchart.index.RSICalculator
import com.androidx.stockchart.index.VolumeAvgCalculator

object QMCalculator: ICalculator {

    override fun calculate(param: String, input: List<IKEntity>): List<List<Float?>> {
        val macdDatas = MACDCalculator.calculate(DefaultIndexParams.MACD, input)
        val rsi = RSICalculator.calculate(DefaultIndexParams.RSI, input)
        val boll = BollCalculator.calculate(DefaultIndexParams.BOLL,input)

        val valumeDatas = VolumeAvgCalculator.calculate("5", input)
        val maDatas = MACalculator.calculate("5,10", input)

        val result = mutableListOf<Float?>()

        input.forEachIndexed { kEntityIdx, kEntity ->
            if (kEntity.containFlag(FLAG_EMPTY) || kEntityIdx<4) {
                result.add(null)
                return@forEachIndexed
            }
            val ma5 = maDatas[0][kEntityIdx]
            val ma10 = maDatas[1][kEntityIdx]
            val ma5Pre = maDatas[0][kEntityIdx-1]
            val ma10Pre = maDatas[1][kEntityIdx-1]
            val volume5 = valumeDatas[0][kEntityIdx]
            val volume = kEntity.getVolume()
            val dif = macdDatas[0][kEntityIdx]
            val dea = macdDatas[1][kEntityIdx]
            val difPre = macdDatas[0][kEntityIdx-1]
            val deaPre = macdDatas[1][kEntityIdx-1]
            val rsi1 = rsi[0][kEntityIdx]
            val rsi2 = rsi[1][kEntityIdx]
            val rsi3 = rsi[2][kEntityIdx]
            val upper = boll[1][kEntityIdx]
            val lower = boll[2][kEntityIdx]
            val upperPre = boll[1][kEntityIdx-1]
            val lowerPre = boll[2][kEntityIdx-1]
            val closePrice = kEntity.getClosePrice()
            val closePricePre = input[kEntityIdx-1].getClosePrice()

            //1.MA5 > MA10 且 前MA5 <= MA10 且 当前成交量>=5日平均成交量*1.5；
            val buy1_1= compare(ma5,ma10)
            val buy1_2= compare(ma5Pre,ma10,false, includeSame = true)
            val buy1_3= compare(volume.toFloat(),volume5?.let { it * 1.5f }, includeSame = true)

            //2.DIF > DEA 且 前一DIF <= 前一DEA 且 RSI<30；
            val buy2_1= compare(dif,dea)
            val buy2_2=  compare(difPre,deaPre, biger = false, includeSame = true)
            val buy2_3= (listOf<Float?>(rsi2).any { it!=null && it<30f })

            //3.收盘价 > UPPER 且 前一收盘价 <=  前一UPPER 且 当前成交量 > 5日平均成交量*1.2
            val buy3_1= compare(closePrice,upper)
            val buy3_2=  compare(closePricePre,upperPre,false,includeSame = true)
            val buy3_3= compare(volume.toFloat(),volume5?.let { it*1.2f })

            Log.d("testQM","$kEntityIdx buy1 compare $buy1_1 $buy1_2 $buy1_3")
            Log.d("testQM","$kEntityIdx buy2 compare $buy2_1 $buy2_2 $buy2_3")
            Log.d("testQM","$kEntityIdx buy3 compare $buy3_1 $buy3_2 $buy3_3")



            //1.MA5 < MA10 且 前MA5 >= MA10 且 当前成交量>=5日平均成交量*0.8；
            val sell1_1= compare(ma5,ma10,false)
            val sell1_2= compare(ma5Pre,ma10,true, includeSame = true)
            val sell1_3= compare(volume.toFloat(),volume5?.let { it * .8f }, includeSame = true)

            //2.DIF < DEA 且 前一DIF >= 前一DEA 且 RSI>70；
            val sell2_1= compare(dif,dea,false)
            val sell2_2=  compare(difPre,deaPre, biger = true, includeSame = true)
            val sell2_3= (listOf<Float?>(rsi2).any { it!=null && it>70f })

            //3.收盘价 < LOWER 且 前一收盘价 >=  前一LOWER 且 当前成交量 > 5日平均成交量*1.2
            val sell3_1= compare(closePrice,lower,false)
            val sell3_2=  compare(closePricePre,lowerPre,true,includeSame = true)
            val sell3_3= compare(volume.toFloat(),volume5?.let { it*1.2f })

            Log.d("testQM","$kEntityIdx buy1 compare $sell1_1 $sell1_2 $sell1_3")
            Log.d("testQM","$kEntityIdx buy2 compare $sell2_1 $sell2_2 $sell2_3")
            Log.d("testQM","$kEntityIdx buy3 compare $sell3_1 $sell3_2 $sell3_3")

            if(
                //1.MA5 > MA10 且 前MA5 <= MA10 且 当前成交量>=5日平均成交量*1.5；
                (buy1_1
                && buy1_2
                && buy1_3)

                //2.DIF > DEA 且 前一DIF <= 前一DEA 且 RSI<30；
                || (buy2_1
                && buy2_2
//                && (listOf<Float?>(rsi1,rsi2,rsi3).any { it!=null && it<30f }))
                && buy2_3)

                //3.收盘价 > UPPER 且 前一收盘价 <=  前一UPPER 且 当前成交量 > 5日平均成交量*1.2
                || (buy3_1
                && buy3_2
                && buy3_3)){
                result.add(kEntityIdx,1f)
            }else if(
                //1.MA5 < MA10 且 前MA5 >= MA10 且 当前成交量>=5日平均成交量*0.8；
                (sell1_1
                && sell1_2
                && sell1_3)

                //2.DIF < DEA 且 前一DIF >= 前一DEA 且 RSI>70；
                || (sell2_1
                && sell2_2
//                && (listOf<Float?>(rsi1,rsi2,rsi3).any { it!=null && it>70f }))
                && sell2_3)

                //3.收盘价 < LOWER 且 前一收盘价 >=  前一LOWER 且 当前成交量 > 5日平均成交量*1.2
                || (sell3_1
                && sell3_2
                && sell3_3)
            ){
                result.add(kEntityIdx,-1f)
            }else{
                result.add(kEntityIdx,null)
//                result.add(kEntityIdx,-1f)

            }
        }
        return mutableListOf<List<Float?>>().apply {
            addAll(macdDatas)
            add(result)
        }
    }


    fun compare(o1: Float?,o2: Float?,biger: Boolean=true,includeSame: Boolean=false): Boolean{
        if(o1!=null && o2!=null){
            if(includeSame && o1 == o2){
                return true
            }
            return if(biger)  o1>o2  else o1<o2
        }
        return false
    }
}