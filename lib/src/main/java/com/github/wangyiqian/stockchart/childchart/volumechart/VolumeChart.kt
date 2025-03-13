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

package com.github.wangyiqian.stockchart.childchart.volumechart

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.github.wangyiqian.stockchart.IStockChart
import com.github.wangyiqian.stockchart.childchart.base.BaseChildChart
import com.github.wangyiqian.stockchart.entities.FLAG_EMPTY
import com.github.wangyiqian.stockchart.entities.containFlag
import kotlin.math.max

/**
 * 成交量图
 *
 * @author hai
 * @version 创建时间: 2021/1/28
 */
class VolumeChart(
    stockChart: IStockChart,
    chartConfig: VolumeChartConfig
) : BaseChildChart<VolumeChartConfig>(stockChart, chartConfig) {

    private val volumePaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    private val highlightHorizontalLinePaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val highlightVerticalLinePaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val highlightLabelPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val highlightLabelBgPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    private val indexTextPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val indexStarterTextBgPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val indexStarterRightIconPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private var indexStarterRectF = RectF()
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
        var yMax = getKEntities()[startIndex].getVolume().toFloat()
        for (i in startIndex + 1..endIndex) {
            yMax = max(yMax, getKEntities()[i].getVolume().toFloat())
        }
        result[0] = 0f
        result[1] = yMax
    }

    override fun preDrawBackground(canvas: Canvas) {}

    override fun drawBackground(canvas: Canvas) {
    }

    override fun preDrawData(canvas: Canvas) {
    }

    override fun drawData(canvas: Canvas) {
        when (chartConfig.volumeChartType) {
            is VolumeChartConfig.VolumeChartType.CANDLE -> {
                drawVolumeChart(canvas, false)
            }
            is VolumeChartConfig.VolumeChartType.HOLLOW -> {
                drawVolumeChart(canvas, true)
            }
        }


        // draw index text
        chartConfig.index?.let { index ->
            indexList?.let { indexList ->
                val highlight = getHighlight()
                var indexIdx =
                    highlight?.getIdx() ?: stockChart.findLastNotEmptyKEntityIdxInDisplayArea()
                indexTextPaint.textSize = index.textSize
                var left = index.textMarginLeft
                var top = index.textMarginTop
                indexTextPaint.getFontMetrics(tmpFontMetrics)
                val textHeight = tmpFontMetrics.bottom - tmpFontMetrics.top
                if (!index.startText.isNullOrEmpty()) {
                    indexTextPaint.color = index.startTextColor
                    indexStarterTextBgPaint.color = chartConfig.indexStarterBgColor
                    val textWidth = indexTextPaint.measureText(index.startText)
                    val textLeft = left + chartConfig.indexStarterBgPaddingHorizontal
                    var width = textWidth + chartConfig.indexStarterBgPaddingHorizontal * 2
                    val height = textHeight + top
                    val indexStarterRightIconMarginLeft = 10
                    chartConfig.indexStarterRightIcon?.also {
                        width += it.width + indexStarterRightIconMarginLeft
                    }
                    canvas.drawRoundRect(
                        left,
                        top,
                        left + width,
                        top + height,
                        5f,
                        5f,
                        indexStarterTextBgPaint
                    )
                    indexStarterRectF.set(left, top, left + width, top + height)
                    chartConfig.indexStarterRightIcon?.also {
                        canvas.drawBitmap(
                            it,
                            textLeft + textWidth + indexStarterRightIconMarginLeft,
                            top + (height - it.height) / 2,
                            indexStarterRightIconPaint
                        )
                    }
                    canvas.drawText(
                        index.startText,
                        textLeft,
                        -tmpFontMetrics.top + top,
                        indexTextPaint
                    )
                    left += width + index.textSpace
                    drawnIndexTextHeight = height
                } else {
                    indexStarterRectF.set(0f, 0f, 0f, 0f)
                }
                indexList.forEachIndexed { lineIdx, pointList ->
                    indexTextPaint.color = chartConfig.indexTextColor
                    val value = if (indexIdx != null && indexIdx in pointList.indices && pointList[indexIdx] != null) pointList[indexIdx] else null
                    val text = index.textFormatter.invoke(lineIdx, value)
                    drawnIndexTextHeight = textHeight + index.textMarginTop
                    canvas.drawText(
                        text,
                        left,
                        -tmpFontMetrics.top + top,
                        indexTextPaint
                    )
                    left += indexTextPaint.measureText(text) + index.textSpace
                }
            }
        }
    }

    private fun drawVolumeChart(canvas: Canvas, isHollow: Boolean) {
        val saveCount = canvas.saveLayer(
            getChartMainDisplayArea().left,
            getChartDisplayArea().top,
            getChartMainDisplayArea().right,
            getChartDisplayArea().bottom,
            null
        )
        volumePaint.strokeWidth = chartConfig.hollowChartLineStrokeWidth
        val barWidth = 1 * (1 - chartConfig.barSpaceRatio)
        val spaceWidth = 1 * chartConfig.barSpaceRatio
        var left = spaceWidth / 2f

        getKEntities().forEachIndexed { idx, kEntity ->

            if (!kEntity.containFlag(FLAG_EMPTY)) {
                val isRise = isRise(idx)
                volumePaint.color =
                    if (isRise) stockChart.getConfig().riseColor else stockChart.getConfig().downColor
                if (isRise && isHollow) { // 空心
                    volumePaint.style = Paint.Style.STROKE
                } else {
                    volumePaint.style = Paint.Style.FILL
                }

                tmpRectF.left = left
                tmpRectF.top = kEntity.getVolume().toFloat()
                tmpRectF.right = left + barWidth
                tmpRectF.bottom = 0f

                mapRectValue2Real(tmpRectF)

                canvas.drawRect(tmpRectF, volumePaint)
            }

            left += barWidth + spaceWidth
        }
        canvas.restoreToCount(saveCount)
    }

    override fun preDrawHighlight(canvas: Canvas) {}

    override fun drawHighlight(canvas: Canvas) {
        getHighlight()?.let { highlight ->
            if (stockChart.getConfig().showHighlightHorizontalLine) {
                if (highlight.y >= getChartDisplayArea().top && highlight.y <= getChartDisplayArea().bottom) {

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
                        if (tmpRectF.top < getChartDisplayArea().top) {
                            tmpRectF.offset(0f, getChartDisplayArea().top - tmpRectF.top)
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
                        if (tmpRectF.top < getChartDisplayArea().top) {
                            tmpRectF.offset(0f, getChartDisplayArea().top - tmpRectF.top)
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
                        getChartDisplayArea().top,
                        x,
                        getChartDisplayArea().bottom,
                        highlightVerticalLinePaint
                    )

                    canvas.restoreToCount(saveCount)
                }
            }
        }
    }

    override fun drawAddition(canvas: Canvas) {}
}