package com.androidx.stockchart.sample.sample_recyclerview

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.stockchart.sample.R
class OptionChainDecoration(
    private val centerWidthPx: Int,
    private val sidePaddingPx: Int,
    private val context: Context
) : RecyclerView.ItemDecoration() {

    private val matrix = Matrix()
    private val tempRect = Rect()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private var lastX = 0f
    var currentTranslateX = 0f
        private set(value) {
            field = value
//            field = value.coerceIn(-maxTranslateX, maxTranslateX)
        }
    private var maxTranslateX = 0f

    // 新增：触摸处理相关
    private val velocityTracker = VelocityTracker.obtain()
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var isDragging = false
    private var totalContentWidth = 0f

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        // 首次计算总内容宽度
        if (totalContentWidth == 0f) {
            parent.forEachVisibleChild { child ->
                child.findViewById<LinearLayout>(R.id.leftContent)?.let {
                    totalContentWidth += it.width.toFloat()
                }
            }
            maxTranslateX = (totalContentWidth - parent.width) * 0.5f
        }

        // 首次计算最大滑动距离
        if (maxTranslateX == 0f) {
            maxTranslateX = parent.width * 0.5f
        }

        drawCenterColumn(c, parent)
        applyMatrixToSides(c, parent)
        drawCenterContent(c, parent)
    }
    private fun applyMatrixToSides(c: Canvas, parent: RecyclerView) {
        val saveCount = c.save()
        matrix.setTranslate(currentTranslateX, 0f)
        c.concat(matrix)
        drawSidesContent(parent, c)
        c.restore()
    }

    private fun drawSidesContent(parent: RecyclerView, canvas: Canvas) {
        parent.forEachVisibleChild { child ->
            val leftView = child.findViewById<LinearLayout>(R.id.leftContent)
            val rightView = child.findViewById<LinearLayout>(R.id.rightContent)

            // 仅绘制左右部分
            if (leftView != null || rightView != null) {
                drawChildWithMatrix(parent, child, canvas)
            }
        }
    }

    private fun drawChildWithMatrix(parent: RecyclerView, child: View, canvas: Canvas) {
        canvas.save()
        parent.getDecoratedBoundsWithMargins(child, tempRect)
        canvas.clipRect(tempRect)
        parent.drawChild(canvas, child, parent.drawingTime)
        canvas.restore()
    }
    private fun drawCenterColumn(c: Canvas, parent: RecyclerView) {
        val centerStart = parent.width / 2f - centerWidthPx / 2f
        c.drawRect(
            centerStart - sidePaddingPx,
            0f,
            centerStart + centerWidthPx + sidePaddingPx,
            parent.height.toFloat(),
            paint
        )
    }

    private fun drawCenterContent(c: Canvas, parent: RecyclerView) {
        parent.forEachVisibleChild { view ->
            val holder = parent.getChildViewHolder(view) as? OptionAdapter.ViewHolder ?: return@forEachVisibleChild
            view.getGlobalVisibleRect(tempRect)
            val centerLeft = tempRect.centerX() - centerWidthPx / 2

            c.save()
            c.translate(centerLeft.toFloat(), tempRect.top.toFloat())
            holder.tvCenter.draw(c)
            c.restore()
        }
    }

    fun attachToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                when (e.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        lastX = e.x
                        velocityTracker.clear()
                        velocityTracker.addMovement(e)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (Math.abs(e.x - lastX) > touchSlop) {
                            rv.parent?.requestDisallowInterceptTouchEvent(true)
                            isDragging = true
                            return true // 关键点：拦截事件
                        }
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                velocityTracker.addMovement(e)
                when (e.actionMasked) {
                    MotionEvent.ACTION_MOVE -> {
                        if (isDragging) {
                            val deltaX = e.x - lastX
                            currentTranslateX += deltaX
                            // 动态计算边界
                            maxTranslateX = (totalContentWidth - rv.width) * 0.5f
                            currentTranslateX = currentTranslateX.coerceIn(-maxTranslateX, maxTranslateX)
                            rv.invalidateItemDecorations()
                            lastX = e.x
                        }
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        isDragging = false
                        velocityTracker.computeCurrentVelocity(1000)
                        startFlingAnimation(rv, velocityTracker.xVelocity)
                    }
                }
            }
        })
    }

    private fun startFlingAnimation(rv: RecyclerView, velocityX: Float) {
        val animator = ValueAnimator.ofFloat(currentTranslateX, currentTranslateX + velocityX * 0.25f).apply {
            addUpdateListener {
                currentTranslateX = it.animatedValue as Float
                rv.invalidateItemDecorations()
            }
            duration = 1000
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        velocityTracker.recycle()
    }

    // 添加扩展函数：遍历可见子View
    private inline fun RecyclerView.forEachVisibleChild(block: (View) -> Unit) {
        for (i in 0 until childCount) {
            block(getChildAt(i))
        }
    }

    // 替换drawChildren的自定义实现
    private fun drawChildrenWithMatrix(recyclerView: RecyclerView, canvas: Canvas) {
        recyclerView.forEachVisibleChild { child ->
            // 跳过中间列的绘制
            if (child.findViewById<TextView>(R.id.tvCenter) == null) {
                canvas.save()
                canvas.concat(matrix)
                recyclerView.getDecoratedBoundsWithMargins(child, tempRect)
                canvas.clipRect(tempRect)
                recyclerView.drawChild(canvas, child, recyclerView.drawingTime)
                canvas.restore()
            }
        }
    }



}