package wb.lib.module_chart


import com.github.stockchart.entities.Highlight
import com.github.stockchart.entities.IKEntity
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONObject
import java.math.BigDecimal

/**
 * @author Hai
 * @date   2025/4/11 14:42
 * @desc
 */
interface IChartCompose {
    val adjustType:String //
    var onCrossLineMoveListener:OnCrossLineMoveListener?
    var assetType: Int // 资产类型
    val subChartType: MutableStateFlow<Int>
    var mainChartType: MutableStateFlow<Int>
    fun setLastPointData(price:Float, avgPrice:Float?, values:BigDecimal)
    fun setKData(srcDate: JSONObject, assetId:String, preClosePrice:Double=0.0, mainChartType:Int)
}
object AssetType {
    const val SECURITY: Int = 0 //证券  在用

    const val INDEX: Int = 1 //指数

    const val INDUSTRY: Int = 2 //行业

    const val CONCEPT: Int = 3 //概念

    const val US_OPT: Int = 4 //美股期权  在用
}

interface OnCrossLineMoveListener {
    fun onCrossLineMove(index: Highlight, kEntity: IKEntity)

    fun onCrossLineDismiss()
}