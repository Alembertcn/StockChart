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

package com.androidx.stockchart

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.core.graphics.drawable.toDrawable
import com.androidx.stockchart.childchart.base.BaseChildChart
import com.androidx.stockchart.childchart.base.IChildChart
import com.androidx.stockchart.entities.*
import com.androidx.stockchart.listener.OnKEntitiesChangedListener
import com.androidx.stockchart.util.checkMainThread
import kotlin.math.max
import kotlin.math.min

/**
 * 股票图，可包含K线图、成交量图、MACD图...
 * 子图目前只提供垂直线性布局
 *
 * @author hai
 * @version 创建时间: 2021/1/28
 */
class StockChart @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ViewGroup(context, attrs),
    IStockChart {

    private val childCharts = mutableListOf<IChildChart>()
    private val touchHelper by lazy { TouchHelper(this, TouchHelperCallBack()) }
    private val onKEntitiesChangedListeners by lazy { mutableSetOf<OnKEntitiesChangedListener>() }
    private val matrixHelper by lazy { MatrixHelper(this) }
    private val highlightMap by lazy { mutableMapOf<IChildChart, Highlight>() }
    private var config: StockChartConfig =
        StockChartConfig()
    private val tmp2FloatArray by lazy { FloatArray(2) }
    private val tmp4FloatArray by lazy { FloatArray(4) }
    private val backgroundGridPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val touchSlop by lazy{ ViewConfiguration.get(context).scaledTouchSlop }
    init {
        setWillNotDraw(false)
        setOnTouchListener(touchHelper)
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }

    override fun getTouchArea() =
        Rect(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom)

    override fun addOnKEntitiesChangedListener(listener: OnKEntitiesChangedListener) {
        onKEntitiesChangedListeners.add(listener)
    }

    override fun removeOnKEntitiesChangedListener(listener: OnKEntitiesChangedListener) {
        onKEntitiesChangedListeners.remove(listener)
    }

    override fun getXScaleMatrix() = matrixHelper.xScaleMatrix

    override fun getFixXScaleMatrix() = matrixHelper.fixXScaleMatrix

    override fun getScrollMatrix() = matrixHelper.scrollMatrix

    override fun getHighlight(childChart: IChildChart) = highlightMap[childChart]

    override fun setConfig(config: StockChartConfig) {
        this.config = config
        notifyChanged()
    }

    override fun getConfig() = config

    override fun getChildCharts() = childCharts

    @UiThread
    override fun notifyChanged() {
        checkMainThread()
        if (config.setKEntitiesFlag) {
            config.setKEntitiesFlag = false
            matrixHelper.resetMatrix()
            onKEntitiesChangedListeners.forEach {
                it.onSetKEntities()
            }
        }

        if (config.modifyKEntitiesFlag) {
            config.modifyKEntitiesFlag = false
            onKEntitiesChangedListeners.forEach {
                it.onModifyKEntities()
            }
        }

        checkChildViews()

        invalidate()
        childCharts.forEach {
            it.invalidate()
        }
    }

    override fun dispatchOnLeftLoadMore() {
        config.getOnLoadMoreListeners().forEach {
            it.onLeftLoadMore()
        }
    }

    override fun dispatchOnRightLoadMore() {
        config.getOnLoadMoreListeners().forEach {
            it.onRightLoadMore()
        }
    }

    override fun findLastIdxInDisplayArea()=(getChildCharts()[0] as? BaseChildChart<*>)?.let {
        tmp2FloatArray[0]=getChildCharts()[0].getChartMainDisplayArea().right
        tmp2FloatArray[1] = 0f
        childCharts[0].mapPointsReal2Value(tmp2FloatArray)
        (tmp2FloatArray[0] +.5f).toInt()-1
    }?:0

    override fun findFirstIdxInDisplayArea()=(getChildCharts()[0] as? BaseChildChart<*>)?.let {
        tmp2FloatArray[0]=getChildCharts()[0].getChartMainDisplayArea().left
        tmp2FloatArray[1] = 0f
        childCharts[0].mapPointsReal2Value(tmp2FloatArray)
        (tmp2FloatArray[0] +.5f).toInt()
    }?:0
    override fun findLastNotEmptyKEntityIdxInDisplayArea(): Int? {
        if (childCharts.isEmpty()) return null
        val leftIdx = findFirstIdxInDisplayArea()
        val rightIdx = findLastIdxInDisplayArea()
        var result: Int? = null
        for (i in rightIdx downTo leftIdx) {
            if (i in config.kEntities.indices && !config.kEntities[i].containFlag(FLAG_EMPTY)) {
                result = i
                break
            }
        }
        return result
    }

    override fun findFirstNotEmptyKEntityIdxInDisplayArea(): Int? {
        if (childCharts.isEmpty()) return null
        val chartDisplayArea = childCharts[0].getChartDisplayArea()
        tmp4FloatArray[0] = chartDisplayArea.left
        tmp4FloatArray[1] = 0f
        tmp4FloatArray[2] = chartDisplayArea.right
        tmp4FloatArray[3] = 0f
        childCharts[0].mapPointsReal2Value(tmp4FloatArray)
        val leftIdx = (tmp4FloatArray[0] + 0.5f).toInt()
        val rightIdx = (tmp4FloatArray[2] + 0.5f).toInt() - 1
        var result: Int? = null
        for (i in leftIdx..rightIdx) {
            if (i in config.kEntities.indices && !config.kEntities[i].containFlag(FLAG_EMPTY)) {
                result = i
                break
            }
        }
        return result
    }

    override fun getTotalScaleX() = matrixHelper.getTotalScaleX()

    private fun checkChildViews() {
        var needReAddViews = false      // 是否需要重新添加view
        var needRequestLayout = false   // 是否需要重新requestLayout
        if (config.childChartFactories.size != childCharts.size) {
            needReAddViews = true
            needRequestLayout = true
        } else {
            run outSide@{
                config.childChartFactories.forEachIndexed { index, childChartFactory ->
                    val childChartConfig = childChartFactory.childChartConfig
                    if (childChartConfig != childCharts[index].getConfig()) {
                        needReAddViews = true
                        needRequestLayout = true
                        return@outSide
                    }
                    if (childChartConfig.setSizeFlag) {
                        childChartConfig.setSizeFlag = false
                        needRequestLayout = true
                        (childCharts[index].view().layoutParams as LayoutParams).apply {
                            width = ViewGroup.LayoutParams.MATCH_PARENT
                            height = childChartConfig.height
                        }
                    }

                    if (childChartConfig.setMarginFlag) {
                        childChartConfig.setMarginFlag = false
                        needRequestLayout = true
                        (childCharts[index].view().layoutParams as LayoutParams).apply {
                            leftMargin = 0
                            topMargin = childChartConfig.marginTop
                            rightMargin = 0
                            bottomMargin = childChartConfig.marginBottom
                        }
                    }
                }
            }
        }

        if (needReAddViews) {
            childCharts.clear()
            removeAllViews()
            config.childChartFactories.forEach {
                val childChart = it.createChart()
                childCharts += childChart
                addView(childChart.view())
            }
        }

        if (needRequestLayout) {
            requestLayout()
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = 0
        var width = 0
        childCharts.map { it.view() }.forEach { childView ->
            val childLayoutParams = childView.layoutParams as LayoutParams
            measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, height)
            height += childView.measuredHeight + childLayoutParams.topMargin + childLayoutParams.bottomMargin
            width = max(
                width,
                childView.measuredWidth + childLayoutParams.leftMargin + childLayoutParams.rightMargin
            )
        }

        width += paddingLeft + paddingRight
        height += paddingTop + paddingBottom

        setMeasuredDimension(
            View.resolveSize(width, widthMeasureSpec),
            View.resolveSize(height, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var childTop = paddingTop
        childCharts.map { it.view() }.forEach { childView ->
            val childMeasuredWidth = childView.measuredWidth
            val childMeasuredHeight = childView.measuredHeight
            val childLayoutParams = childView.layoutParams as LayoutParams
            val childLeft = paddingLeft + childLayoutParams.leftMargin
            childTop += childLayoutParams.topMargin

            val childRight = min(childLeft + childMeasuredWidth, measuredWidth - paddingRight)
            val childBottom = min(
                childTop + childMeasuredHeight,
                measuredHeight - paddingBottom
            )
            if (childRight > childLeft && childBottom > childTop) {
                childView.layout(childLeft, childTop, childRight, childBottom)
            }
            childTop = childBottom + childLayoutParams.bottomMargin
        }
    }
    // 预渲染静态内容到 Bitmap
    private var cachedBitmap: Bitmap? = null
    override fun onDraw(canvas: Canvas) {
        Log.d("shh","testDraw onDraw ${cachedBitmap!=null}")
        canvas.clipRect(0,0,width,height)
        if(cachedBitmap!=null){
            canvas.drawBitmap(cachedBitmap!!,canvas.matrix,null)
        }else{
//            cachedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//            canvas.setBitmap(cachedBitmap)
            drawBackgroundColor(canvas)
            drawBackgroundGrid(canvas)
            super.onDraw(canvas)
        }
    }

    override fun invalidate() {
        Log.d("shh","testDraw invalidate")

        cachedBitmap = null
        super.invalidate()
    }

    private fun drawBackgroundColor(canvas: Canvas) {
        canvas.drawColor(config.backgroundColor)
    }

    private fun drawBackgroundGrid(canvas: Canvas) {
        backgroundGridPaint.color = config.gridLineColor
        backgroundGridPaint.strokeWidth = config.gridLineStrokeWidth
        backgroundGridPaint.pathEffect = config.gridLinePathEffect

        if (config.gridHorizontalLineCount > 0) {
            if(config.horizontalGridLineYCalculator!=null){

            }
            var space = config.horizontalGridLineSpaceCalculator?.invoke(this,0)
                ?: (height.toFloat() / (config.gridHorizontalLineCount + 1))
            var top = config.horizontalGridLineYCalculator?.invoke (this,0)
                ?:config.horizontalGridLineTopOffsetCalculator?.invoke(this)
                ?: space

            for (i in 1..config.gridHorizontalLineCount) {
                canvas.drawLine(
                    config.horizontalGridLineLeftOffsetCalculator?.invoke(this) ?: 0f,
                    top.coerceIn(config.gridLineStrokeWidth/2,height-config.gridLineStrokeWidth/2),
                    width.toFloat(),
                    top.coerceIn(config.gridLineStrokeWidth/2,height-config.gridLineStrokeWidth/2),
                    backgroundGridPaint
                )
                config.horizontalGridLineSpaceCalculator?.invoke(this,i-1)?.let {
                    space = it
                }
                top = config.horizontalGridLineYCalculator?.invoke(this,i)?:(top + space)
            }
        }

        if (config.gridVerticalLineCount > 0) {
            var space = config.verticalGridLineSpaceCalculator?.invoke(this,0)
                ?: (width.toFloat() / (config.gridVerticalLineCount + 1))
            var left = config.verticalGridLineLeftOffsetCalculator?.invoke(this) ?: space

            for (i in 1..config.gridVerticalLineCount) {
                canvas.drawLine(left.coerceIn(config.gridLineStrokeWidth/2,width-config.gridLineStrokeWidth/2), config.verticalGridLineTopOffsetCalculator?.invoke(this) ?: 0f, left.coerceIn(config.gridLineStrokeWidth/2,width-config.gridLineStrokeWidth/2), height.toFloat(), backgroundGridPaint)
                config.verticalGridLineSpaceCalculator?.invoke(this,i-1)?.let {
                    space = it
                }
                left += space
            }
        }
    }

    class LayoutParams(width: Int, height: Int) : ViewGroup.MarginLayoutParams(width, height)

    inner class TouchHelperCallBack :
        TouchHelper.CallBack {

        override fun onTouchDown() {
            handlerScroll = false
            matrixHelper.handleTouchDown()
        }

        override fun onTouchScaleBegin(focusX: Float) {
            if (getConfig().scaleAble) {
                requestDisallowInterceptTouchEvent(true)
                matrixHelper.handleTouchScaleBegin(focusX)
                getConfig().getOnGestureListeners().forEach { it.onScaleBegin(x) }
            }
        }

        override fun onTouchScaling(scaleFactor: Float) {
            if (getConfig().scaleAble) {
                requestDisallowInterceptTouchEvent(true)
                matrixHelper.handleTouchScale(scaleFactor)
                getConfig().getOnGestureListeners().forEach { it.onScaling(getTotalScaleX()) }
            }
        }


        var handlerScroll = false

        override fun onHScroll(distanceX: Float):Boolean {
            if (getConfig().scrollAble) {
                if(matrixHelper.handleTouchScroll(distanceX)){
                    handlerScroll = true
                }
                requestDisallowInterceptTouchEvent(handlerScroll)

                getConfig().getOnGestureListeners().forEach { it.onHScrolling() }
                return handlerScroll
            }else{
                requestDisallowInterceptTouchEvent(false)
            }
            return false
        }

        override fun onVScroll(distanceY: Float): Boolean {
            requestDisallowInterceptTouchEvent(handlerScroll)
            return false
        }

        override fun onTriggerFling(velocityX: Float, velocityY: Float) {
            matrixHelper.handleFlingStart(velocityX, velocityY)
            getConfig().getOnGestureListeners().forEach { it.onFlingBegin() }
        }

        override fun onLongPressMove(x: Float, y: Float) {
            if (getConfig().showHighlightHorizontalLine
                || getConfig().showHighlightVerticalLine
                || childCharts.find { it.getConfig().onHighlightListener != null } != null
            ) {
                requestDisallowInterceptTouchEvent(true)
                childCharts.forEach { childChart ->
                    val childChartX = x - childChart.view().left
                    val childChartY = y - childChart.view().top
                    childChart.getHighlightValue(childChartX, childChartY, tmp2FloatArray)
                    var valueX = tmp2FloatArray[0]
                    val valueY = tmp2FloatArray[1]

                    // 这里防止手机边缘限制这里根据中心点优化偏移量
                    val centerX = (childChart.view().left+childChart.view().right)/2
                    if(x<centerX){
                        valueX-=.5f
                    }else{
                        valueX+=.5f
                    }

                    var highlight = highlightMap[childChart]
                    if (highlight == null) {
                        highlight = Highlight(childChartX, childChartY, valueX, valueY)
                        highlightMap[childChart] = highlight
                        childChart.getConfig().onHighlightListener?.onHighlightBegin()
                    } else {
                        highlight.x = childChartX
                        highlight.y = childChartY
                        highlight.valueX = valueX
                        highlight.valueY = valueY
                    }

                    // 校验最大最小
                    var idx = highlight.getIdx()
                    if(idx !=idx.coerceIn(findFirstNotEmptyKEntityIdxInDisplayArea(),findLastNotEmptyKEntityIdxInDisplayArea())){
                        idx = idx.coerceIn(findFirstNotEmptyKEntityIdxInDisplayArea(),findLastNotEmptyKEntityIdxInDisplayArea())
                        highlight.valueX = idx.toFloat()
                        tmp2FloatArray[0]=highlight.valueX
                        tmp2FloatArray[1]=0f
                        childChart.mapPointsValue2Real(tmp2FloatArray)
                        highlight.x = tmp2FloatArray[0]
                    }


                    highlight.entry = config.getKEntity(highlight.getIdx())

                    highlight.apply { childChart.getConfig().onHighlightListener?.onHighlight(this) }
                }
                notifyChanged()
            }
        }

        override fun onTouchLeave() {
            handlerScroll = false
            getConfig().getOnGestureListeners().forEach { it.onTouchLeave() }
            notifyChanged()
            matrixHelper.checkScrollBack()
        }

        override fun onTap(x: Float, y: Float) {
            childCharts.forEach { childChart ->
                val childChartX = x - childChart.view().left
                val childChartY = y - childChart.view().top
                tmp2FloatArray[0] = childChartX
                tmp2FloatArray[1] = childChartY
                childChart.mapPointsReal2Value(tmp2FloatArray)
                val valueX = tmp2FloatArray[0]
                val valueY = tmp2FloatArray[1]
                val gestureEvent = GestureEvent(childChartX, childChartY, valueX, valueY)
                childChart.onTap(gestureEvent)
            }
            getConfig().getOnGestureListeners().forEach { it.onTap(x, y) }
        }

        override fun onLongPressBegin(x: Float, y: Float) {
            getConfig().getOnGestureListeners().forEach { it.onLongPressBegin(x, y) }
        }

        override fun onLongPressing(x: Float, y: Float) {
            getConfig().getOnGestureListeners().forEach { it.onLongPressing(x, y) }
        }

        override fun onLongPressEnd(x: Float, y: Float) {
            highlightMap.keys.forEach {
                it.getConfig().onHighlightListener?.onHighlightEnd()
            }
            highlightMap.clear()

            getConfig().getOnGestureListeners().forEach { it.onLongPressEnd(x, y) }

            notifyChanged()
        }

    }

    override fun computeScroll() {
        matrixHelper.handleComputeScroll()
    }

}
