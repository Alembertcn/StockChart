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

package wb.lib.module_chart.index.qm

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.androidx.stockchart.IStockChart
import com.androidx.stockchart.childchart.macdchart.MacdChart
import kotlin.math.max
import kotlin.math.min

/**
 * MACD & QM指标图
 * @author hai
 * @version 创建时间: 2021/2/18
 */
class QMChart(
    stockChart: IStockChart,
    chartConfig: QMChartConfig
) : MacdChart(stockChart, chartConfig) {

    var riskBitmap: Bitmap?=null
    var downBitmap :Bitmap?=null
    init {
        var srcBitmap = ContextCompat.getDrawable(context,chartConfig.drawableRes)?.toBitmap()
        if(srcBitmap!=null){
            riskBitmap =srcBitmap
            downBitmap = Bitmap.createBitmap(srcBitmap,0,0,srcBitmap.width,srcBitmap.height, Matrix().apply { postRotate(180f) },true)
        }
    }
    val riskPaint by lazy {
        Paint().apply {
        setColorFilter(PorterDuffColorFilter(stockChart.getConfig().riseColor, PorterDuff.Mode.SRC_ATOP))
    } }
    val downPaint by lazy {
        Paint().apply {
            setColorFilter(PorterDuffColorFilter(stockChart.getConfig().downColor, PorterDuff.Mode.SRC_ATOP))
        }
    }

    override fun drawData(canvas: Canvas) {
        super.drawData(canvas)
        val qmIdx = 3
        // draw qm symble
        doDrawQM(canvas, indexList?.get(qmIdx),indexList?.get(macdIdx))
    }

    private fun doDrawQM(canvas: Canvas, valueList: List<Float?>?, macdList: List<Float?>?) {
        if(riskBitmap==null || downBitmap==null) return

        val saveCount = canvas.saveLayer(
            getChartMainDisplayArea().left,
            getChartDisplayArea().top,
            getChartMainDisplayArea().right,
            getChartDisplayArea().bottom,
            null
        )
        val displayTop = 0f
        val displayBottom = height.toFloat()


        val firstIdxInDisplayArea = (stockChart.findFirstIdxInDisplayArea()-1).coerceAtLeast(0)
        val lastIdxInDisplayArea = (stockChart.findLastIdxInDisplayArea()+1).coerceAtMost(stockChart.getConfig().getKEntitiesSize()-1)

        val measureWidth = riskBitmap!!.width/2f
        val symbelHeight = riskBitmap!!.height
        val gap = 10f
        for (valueIdx in firstIdxInDisplayArea..lastIdxInDisplayArea){
            if (valueIdx == 0) continue
            valueList?.get(valueIdx)?.let { value ->
                    tmpRectF.left = valueIdx + 0.5f
                    tmpRectF.top = macdList?.get(valueIdx)?:0f
                    tmpRectF.right = valueIdx + 0.5f
                    tmpRectF.bottom = 0f
                    mapRectValue2Real(tmpRectF)

                    val bitmap = if(value>0f) riskBitmap else downBitmap
                    val top = (if(value>0f) (max(tmpRectF.top,tmpRectF.bottom +gap)) else (min(tmpRectF.top,tmpRectF.bottom)-symbelHeight-gap)).coerceIn(displayTop,displayBottom-symbelHeight)
                    val paint = if(value>0f) riskPaint else downPaint
                    canvas.drawBitmap(bitmap!!,tmpRectF.left-measureWidth,top,paint)
            }
        }
        canvas.restoreToCount(saveCount)
    }
}