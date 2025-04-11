import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.github.wangyiqian.stockchart.childchart.kchart.KChartConfig
import com.github.wangyiqian.stockchart.util.DimensionUtil
import java.lang.Float.max

class CirclePaint (val view:View){
    var maxRadius: Float =2.5f// 初始半径为 2.5dp
    private var circleRadius: Float =0f// 初始半径为 2.5dp
    private var circleColor: Int = Color.RED // 初始颜色为全红
    private val paint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
        }
    }
    val animation by lazy {
        ValueAnimator.ofFloat(1f,maxRadius).apply {
            duration = 500 // 2秒
            repeatCount = 2
            repeatMode = ValueAnimator.REVERSE
            interpolator = android.view.animation.LinearInterpolator()
            addUpdateListener { animation ->
                circleRadius = animation.animatedValue as Float
                // 计算颜色透明度
                val fraction = animation.animatedFraction
                val alpha = (255 * (max(fraction,0.3f))).toInt()
                paint.color = ColorUtils.setAlphaComponent(circleColor,alpha)
                view.invalidate() // 重绘视图
            }
        }
    }

    var lastCenterX = 0.0f
    var lastCenterY = 0.0f
    fun onDraw(canvas: Canvas,startX:Float,startY:Float,centerX:Float,centerY:Float,linePaint:Paint,config: KChartConfig){
        if(!config.showCircle || config.kChartType !is KChartConfig.KChartType.LINE)return
        circleColor = if(config.preClosePrice!!>config.lastPrice!!) Color.GREEN else Color.RED
        if(lastCenterX!=centerX || lastCenterY!=centerY){
            animation.start()
        }
        canvas.drawLine(startX,startY,centerX,centerY,linePaint)
        // 绘制圆圈
        canvas.drawCircle(centerX, centerY, DimensionUtil.dp2px(view.context, circleRadius).toFloat() , paint)
        lastCenterX = centerX
        lastCenterY = centerY
    }
}