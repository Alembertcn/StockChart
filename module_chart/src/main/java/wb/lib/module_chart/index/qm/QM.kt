package wb.lib.module_chart.index.qm

import com.androidx.stockchart.entities.IKEntity
import com.androidx.stockchart.index.Index
import com.androidx.stockchart.util.ResourceUtil
import wb.lib.module_chart.R

class QM: Index.MACD(startText = ResourceUtil.getString(R.string.qm),
    textFormatter = {idx,value-> when(idx){
     in 0..2->com.androidx.stockchart.DefaultIndexTextFormatter.MACD.invoke(idx,value)
    else -> when {
        value == null || value == 0f -> ""
        value > 0f -> "Buy"
        else -> "Sell"
    } } }) {

    override fun calculate(input: List<IKEntity>): List<List<Float?>> {
        return QMCalculator.calculate("",input)
    }


}