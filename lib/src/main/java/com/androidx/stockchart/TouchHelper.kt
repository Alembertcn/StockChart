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

import android.graphics.Matrix
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import kotlin.math.abs

/**
 * 统一处理触摸事件
 *
 * @author hai
 * @version 创建时间: 2021/1/29
 */
internal class TouchHelper(private val stockChart: IStockChart, private val callBack: CallBack) :
    GestureDetector.SimpleOnGestureListener(),
    ScaleGestureDetector.OnScaleGestureListener,
    View.OnTouchListener {

    private val gestureDetector by lazy { GestureDetector(stockChart.getContext(), this) }

    private val scaleGestureDetector by lazy { ScaleGestureDetector(stockChart.getContext(), this)}

    private val tmpMatrix by lazy { Matrix() }

    // 是否正在缩放
    private var isTouchScaling = false

    // 开始缩放后手指是否离开屏幕
    private var isTouchScalePointersLeave = true

    // 是否正在长按
    private var isLongPressing = false

    // 触发长按的第一根手指
    private var inLongPressingPointerId = 0

    private var flingAble = false

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (!stockChart.getTouchArea().contains(event.x.toInt(), event.y.toInt())
                    || stockChart.getChildCharts().isEmpty()
                ) {
                    // 不在允许的触摸范围
                    return false
                }
                //防止快速滑动导致的cancel
                (stockChart as ViewGroup?)?.requestDisallowInterceptTouchEvent(true)
                callBack.onTouchDown()
            }
            MotionEvent.ACTION_MOVE -> {
                if (isLongPressing && event.getPointerId(event.actionIndex) == inLongPressingPointerId) {
                    callBack.onLongPressMove(event.x, event.y)
                    callBack.onLongPressing(event.x, event.y)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (isLongPressing) {
                    isLongPressing = false
                    callBack.onLongPressEnd(event.x, event.y)
                }
                isTouchScalePointersLeave = true
                callBack.onTouchLeave()
            }
            MotionEvent.ACTION_CANCEL -> {
                if (isLongPressing) {
                    isLongPressing = false
                    callBack.onLongPressEnd(event.x, event.y)
                }
                isTouchScalePointersLeave = true
                callBack.onTouchLeave()
            }
            MotionEvent.ACTION_POINTER_UP -> {
                if (isLongPressing && event.getPointerId(event.actionIndex) == inLongPressingPointerId) {
                    isLongPressing = false
                    callBack.onLongPressEnd(event.x, event.y)
                }
            }
        }

        if (!isLongPressing) {
            if (stockChart.getConfig().scaleAble) {
                if(event.pointerCount == 2){
                    // 解决系统缩放最小间距校验导致的缩放限制
                    // https://stackoverflow.com/questions/14524493/infinite-scaling-using-scalegesturedetector
                    val centerX = (event.getX(0) + event.getX(1)) / 2
                    val centerY = (event.getY(0) + event.getY(1)) / 2

                    tmpMatrix.apply {
                        reset()
                        postScale(5f,1f, centerX,centerY)
                    }
                    event.transform(tmpMatrix)
                    scaleGestureDetector.onTouchEvent(event)
                    tmpMatrix.apply {
                       if(invert(this)){
                           event.transform(tmpMatrix)
                       }
                    }

                }
            }
            if (!isTouchScaling && isTouchScalePointersLeave) {
                val onTouchEvent = gestureDetector.onTouchEvent(event)
                Log.d("testScroll","onTouchEvent gestureDetector $onTouchEvent")

                return onTouchEvent
            }
        }

        Log.d("testScroll","onTouchEvent action:${event.action} x:${event.x}")
        return true
    }

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onLongPress(e: MotionEvent) {
        if (!isLongPressing) {
            isLongPressing = true
            callBack.onLongPressBegin(e.x, e.y)
        } else {
            callBack.onLongPressing(e.x, e.y)
        }
        inLongPressingPointerId = e.getPointerId(0)
        callBack.onLongPressMove(e.x, e.y)
        super.onLongPress(e)
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (abs(distanceX) > abs(distanceY)) {
            flingAble = true
           return callBack.onHScroll(distanceX)
        } else {
            flingAble = false
            return callBack.onVScroll(distanceY)
        }
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (flingAble) {
            callBack.onTriggerFling(velocityX, velocityY)
        }
        return super.onFling(e1, e2, velocityX, velocityY)
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        if (!isTouchScaling) {
            isTouchScaling = true
            isTouchScalePointersLeave = false
            callBack.onTouchScaleBegin(detector.focusX)
        }
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        isTouchScaling = false
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        callBack.onTouchScaling(detector.scaleFactor)
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        callBack.onTap(e.x, e.y)
        return super.onSingleTapConfirmed(e)
    }

    interface CallBack {

        /**
         * 触摸开始
         */
        fun onTouchDown()

        /**
         * 开始双指缩放
         */
        fun onTouchScaleBegin(focusX: Float)

        /**
         * 双指缩放中
         */
        fun onTouchScaling(scaleFactor: Float)

        /**
         * 手指左右滑动
         */
        fun onHScroll(distanceX: Float):Boolean
        /**
         * 手指上下滑动
         */
        fun onVScroll(distanceY: Float):Boolean

        /**
         * 触发惯性滑动
         */
        fun onTriggerFling(velocityX: Float, velocityY: Float)

        /**
         * 手指长按滑动
         */
        fun onLongPressMove(x: Float, y: Float)

        /**
         * 手指离开屏幕
         */
        fun onTouchLeave()

        /**
         * 点击
         */
        fun onTap(x: Float, y: Float)

        /**
         * 开始长按
         */
        fun onLongPressBegin(x: Float, y: Float) {}

        /**
         * 长按中
         */
        fun onLongPressing(x: Float, y: Float) {}

        /**
         * 结束长按
         */
        fun onLongPressEnd(x: Float, y: Float) {}
    }
}