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

package com.androidx.stockchart.childchart.kdjchart

import android.graphics.Canvas
import android.graphics.Paint
import com.androidx.stockchart.IStockChart
import com.androidx.stockchart.childchart.base.BaseChildChart
import kotlin.math.abs

/**
 * @author hai
 * @version 创建时间: 2021/2/18
 */
class KdjChart(stockChart: IStockChart, chartConfig: KdjChartConfig) :
    BaseChildChart<KdjChartConfig>(stockChart, chartConfig) {

    private val linePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply { strokeCap = Paint.Cap.ROUND }
    }
    private val highlightHorizontalLinePaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val highlightVerticalLinePaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val highlightLabelPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val highlightLabelBgPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val indexTextPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    private var indexList: List<List<Float?>>? = null

    private var drawnIndexTextHeight = 0f

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        indexList = chartConfig.index?.calculate(getKEntities())
    }

    override fun onKEntitiesChanged() {
        indexList = chartConfig.index?.calculate(getKEntities())
    }

    override fun getYValueRange(startIndex: Int, endIndex: Int, result: FloatArray) {
        var yMax = 0f
        var yMin = 0f
        indexList?.forEach { valueList ->
            valueList.filterIndexed { idx, _ -> idx in startIndex..endIndex }.filterNotNull()
                .apply {
                    if (size > 0) {
                        yMax = kotlin.math.max(yMax, max()!!)
                        yMin = kotlin.math.min(yMin, min()!!)
                    }
                }
        }

        if (abs(yMin - yMax) > stockChart.getConfig().valueTendToZero) {
            result[0] = yMin
            result[1] = yMax
        } else { // 约等于0
            var delta = 2
            result[0] = yMin - delta
            result[1] = yMax + delta
        }
    }

    override fun preDrawBackground(canvas: Canvas) {
    }

    override fun drawBackground(canvas: Canvas) {
    }

    override fun preDrawData(canvas: Canvas) {
    }

    override fun drawData(canvas: Canvas) {
        val kIdx = 0
        val dIdx = 1
        val jIdx = 2

        // draw k line
        linePaint.strokeWidth = chartConfig.kLineStrokeWidth
        linePaint.color = chartConfig.kLineColor
        doDrawLine(canvas, indexList?.get(kIdx))

        // draw d line
        linePaint.strokeWidth = chartConfig.dLineStrokeWidth
        linePaint.color = chartConfig.dLineColor
        doDrawLine(canvas, indexList?.get(dIdx))

        // draw j line
        linePaint.strokeWidth = chartConfig.jLineStrokeWidth
        linePaint.color = chartConfig.jLineColor
        doDrawLine(canvas, indexList?.get(jIdx))

        // draw index text
        drawnIndexTextHeight = 0f
        chartConfig.index?.let { index ->
            indexList?.let { indexList ->
                val highlight = getHighlight()
                var indexIdx =
                    highlight?.getIdx() ?: stockChart.findLastNotEmptyKEntityIdxInDisplayArea()
                indexTextPaint.textSize = index.textSize
                var left = index.textMarginLeft
                val top = index.textMarginTop
                if (!index.startText.isNullOrEmpty()) {
                    indexTextPaint.color = index.startTextColor
                    indexTextPaint.getFontMetrics(tmpFontMetrics)
                    canvas.drawText(
                        index.startText,
                        left,
                        -tmpFontMetrics.top + top,
                        indexTextPaint
                    )
                    left += indexTextPaint.measureText(index.startText) + index.textSpace
                    drawnIndexTextHeight =
                        tmpFontMetrics.bottom - tmpFontMetrics.top
                }
                indexList.forEachIndexed { lineIdx, pointList ->
                    indexTextPaint.color = when (lineIdx) {
                        kIdx -> chartConfig.kLineColor
                        dIdx -> chartConfig.dLineColor
                        else -> chartConfig.jLineColor
                    }
                    val value =
                        if (indexIdx != null && indexIdx in pointList.indices && pointList[indexIdx] != null) pointList[indexIdx] else null
                    val text = index.textFormatter.invoke(lineIdx, value)
                    indexTextPaint.getFontMetrics(tmpFontMetrics)
                    canvas.drawText(
                        text,
                        left,
                        -tmpFontMetrics.top + top,
                        indexTextPaint
                    )
                    left += indexTextPaint.measureText(text) + index.textSpace
                    drawnIndexTextHeight =
                        tmpFontMetrics.bottom - tmpFontMetrics.top
                }
            }
        }
    }

    private fun doDrawLine(canvas: Canvas, valueList: List<Float?>?) {
        if(valueList==null)return

        val saveCount = canvas.saveLayer(
            getChartMainDisplayArea().left,
            getChartDisplayArea().top,
            getChartMainDisplayArea().right,
            getChartDisplayArea().bottom,
            null
        )

        val firstIndex = (stockChart.findFirstIdxInDisplayArea()-1).coerceAtLeast(0)
        val lastIndex = (stockChart.findLastIdxInDisplayArea()+1).coerceAtMost(valueList.size-1)
        for (valueIdx in firstIndex..lastIndex){
            val value = valueList[valueIdx]
            if(valueIdx!=0 && value!=null){
                valueList[valueIdx - 1]?.let { preValue ->
                    tmp4FloatArray[0] = valueIdx - 1 + 0.5f
                    tmp4FloatArray[1] = preValue
                    tmp4FloatArray[2] = valueIdx + 0.5f
                    tmp4FloatArray[3] = value

                    mapPointsValue2Real(tmp4FloatArray)

                    canvas.drawLines(tmp4FloatArray, linePaint)
                }
            }
        }
        canvas.restoreToCount(saveCount)
    }

    override fun preDrawHighlight(canvas: Canvas) {
    }

    override fun drawHighlight(canvas: Canvas) {
        getHighlight()?.let { highlight ->
            val highlightAreaTop = getChartDisplayArea().top + drawnIndexTextHeight
            if (stockChart.getConfig().showHighlightHorizontalLine) {
                if (highlight.y >= highlightAreaTop && highlight.y <= getChartDisplayArea().bottom) {

                    highlightHorizontalLinePaint.color =
                        stockChart.getConfig().highlightHorizontalLineColor
                    highlightHorizontalLinePaint.strokeWidth =
                        stockChart.getConfig().highlightHorizontalLineWidth
                    highlightHorizontalLinePaint.pathEffect =
                        stockChart.getConfig().highlightHorizontalLinePathEffect

                    var highlightHorizontalLineLeft = getChartDisplayArea().left
                    var highlightHorizontalLineRight = getChartDisplayArea().right

                    // left highlight label
                    chartConfig.highlightLabelLeft?.let { highlightLabel ->
                        highlightLabelPaint.textSize = highlightLabel.textSize
                        highlightLabelPaint.color = highlightLabel.textColor
                        highlightLabelBgPaint.color = highlightLabel.bgColor
                        val text = highlightLabel.textFormat(highlight.valueY)
                        highlightLabelPaint.getTextBounds(text, 0, text.length, tmpRect)
                        val textWidth = tmpRect.width()
                        val textHeight = tmpRect.height()
                        val bgWidth = textWidth + highlightLabel.padding * 2
                        val bgHeight = textHeight + highlightLabel.padding * 2
                        tmpRectF.left = getChartDisplayArea().left
                        tmpRectF.top = highlight.y - bgHeight / 2
                        tmpRectF.right = bgWidth
                        tmpRectF.bottom = highlight.y + bgHeight / 2
                        if (tmpRectF.top < highlightAreaTop) {
                            tmpRectF.offset(0f, highlightAreaTop - tmpRectF.top)
                        } else if (tmpRectF.bottom > getChartDisplayArea().bottom) {
                            tmpRectF.offset(0f, getChartDisplayArea().bottom - tmpRectF.bottom)
                        }
                        highlightLabelPaint.getFontMetrics(tmpFontMetrics)
                        val textBaseLine =
                            tmpRectF.top + bgHeight / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom

                        canvas.drawRoundRect(
                            tmpRectF,
                            highlightLabel.bgCorner,
                            highlightLabel.bgCorner,
                            highlightLabelBgPaint
                        )

                        canvas.drawText(
                            text,
                            tmpRectF.left + highlightLabel.padding,
                            textBaseLine,
                            highlightLabelPaint
                        )

                        highlightHorizontalLineLeft += bgWidth
                    }

                    // right highlight label
                    chartConfig.highlightLabelRight?.let { highlightLabel ->
                        highlightLabelPaint.textSize = highlightLabel.textSize
                        highlightLabelPaint.color = highlightLabel.textColor
                        highlightLabelBgPaint.color = highlightLabel.bgColor
                        val text = highlightLabel.textFormat(highlight.valueY)
                        highlightLabelPaint.getTextBounds(text, 0, text.length, tmpRect)
                        val textWidth = tmpRect.width()
                        val textHeight = tmpRect.height()
                        val bgWidth = textWidth + highlightLabel.padding * 2
                        val bgHeight = textHeight + highlightLabel.padding * 2
                        tmpRectF.left = getChartDisplayArea().right - bgWidth
                        tmpRectF.top = highlight.y - bgHeight / 2
                        tmpRectF.right = getChartDisplayArea().right
                        tmpRectF.bottom = highlight.y + bgHeight / 2
                        if (tmpRectF.top < highlightAreaTop) {
                            tmpRectF.offset(0f, highlightAreaTop - tmpRectF.top)
                        } else if (tmpRectF.bottom > getChartDisplayArea().bottom) {
                            tmpRectF.offset(0f, getChartDisplayArea().bottom - tmpRectF.bottom)
                        }
                        highlightLabelPaint.getFontMetrics(tmpFontMetrics)
                        val textBaseLine =
                            tmpRectF.top + bgHeight / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom

                        canvas.drawRoundRect(
                            tmpRectF,
                            highlightLabel.bgCorner,
                            highlightLabel.bgCorner,
                            highlightLabelBgPaint
                        )

                        canvas.drawText(
                            text,
                            tmpRectF.left + highlightLabel.padding,
                            textBaseLine,
                            highlightLabelPaint
                        )

                        highlightHorizontalLineRight -= bgWidth
                    }

                    val saveCount = canvas.saveLayer(
                        getChartMainDisplayArea().left,
                        getChartDisplayArea().top,
                        getChartMainDisplayArea().right,
                        getChartDisplayArea().bottom,
                        null
                    )

                    // highlight horizontal line
                    canvas.drawLine(
                        highlightHorizontalLineLeft,
                        highlight.y,
                        highlightHorizontalLineRight,
                        highlight.y,
                        highlightHorizontalLinePaint
                    )

                    canvas.restoreToCount(saveCount)

                }
            }

            if (stockChart.getConfig().showHighlightVerticalLine) {
                if (highlight.x >= getChartDisplayArea().left && highlight.x <= getChartDisplayArea().right) {

                    highlightVerticalLinePaint.color =
                        stockChart.getConfig().highlightVerticalLineColor
                    highlightVerticalLinePaint.strokeWidth =
                        stockChart.getConfig().highlightVerticalLineWidth
                    highlightVerticalLinePaint.pathEffect =
                        stockChart.getConfig().highlightVerticalLinePathEffect

                    tmp2FloatArray[0] = highlight.getIdx() + 0.5f
                    tmp2FloatArray[1] = 0f
                    mapPointsValue2Real(tmp2FloatArray)
                    val x = tmp2FloatArray[0]

                    val saveCount = canvas.saveLayer(
                        getChartMainDisplayArea().left,
                        getChartDisplayArea().top,
                        getChartMainDisplayArea().right,
                        getChartDisplayArea().bottom,
                        null
                    )

                    // highlight vertical line
                    canvas.drawLine(
                        x,
                        highlightAreaTop,
                        x,
                        getChartDisplayArea().bottom,
                        highlightVerticalLinePaint
                    )

                    canvas.restoreToCount(saveCount)

                }
            }
        }
    }

    override fun drawAddition(canvas: Canvas) {
    }
}