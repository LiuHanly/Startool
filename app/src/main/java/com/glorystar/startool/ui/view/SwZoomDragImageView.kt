package com.glorystar.startool.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.widget.AppCompatImageView
import com.glorystar.startool.utils.tool.LiveDataBus


class SwZoomDragImageView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(
    context!!, attrs, defStyleAttr
), OnTouchListener {
    private val support_touch = true //支持触摸事件
    private var mode = 0 // 初始状态
    var total_scale = MIN_SCALE
    var current_scale //total_scale缩放范围2-1，当小于1回弹到1；当大于2回弹到2
            = 0f
    private val matrixNow = Matrix()
    private val matrixBefore = Matrix()
    private val mInitializationMatrix = Matrix() //初始缩放值
    private val actionDownPoint = PointF() //点击点
    private val dragPoint = PointF() //平移点
    private val startPoint = PointF() //滑动点
    private val mInitializationScalePoint = PointF() //初始缩放点
    private val mCurrentScalePoint = PointF(0F, 0F) //当前缩放点
    private var startDis //滑动开始距离
            = 0f

    /** 两个手指的中间点  */
    private var midPoint = PointF(0f, 0f)
    private fun initData() {
        if (support_touch) {
            setOnTouchListener(this)
        }
        this.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                this@SwZoomDragImageView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val viewLocation = IntArray(2)
                getLocationInWindow(viewLocation)
                val viewX = viewLocation[0] // x 坐标；有bug，在viewpage中。
                val viewY = viewLocation[1] // y 坐标
                mInitializationScalePoint[(this@SwZoomDragImageView.width / 2).toFloat()] =
                    (viewY + this@SwZoomDragImageView.height / 2).toFloat() //初始化缩放位置
                Log.i(
                    "yangxun",
                    "控件 宽：" + mInitializationScalePoint.x + "高：" + mInitializationScalePoint.y
                )
            }
        })
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK) //清空画布
        if (matrixNow != null) {
            canvas.concat(matrixNow)
            //            canvas.setMatrix(matrixNow);//显示有问题
        }
        super.onDraw(canvas)
    }

    override fun setImageMatrix(matrix: Matrix) {
        Log.e("SwZoomDragImageView", "setImageMatrix")
        matrixNow.set(matrix)
        invalidate()
    }

    fun resetImageMatrix() {
        matrixNow.set(mInitializationMatrix)
        invalidate()
    }

    //最小重置数值
    private fun resetToMinStatus() {
        mCurrentScalePoint[0f] = 0f
        total_scale = MIN_SCALE
        Log.e("SwZoomDragImageView", "resetToMinStatus")
    }

    //最大重置数值
    private fun resetToMaxStatus() {
        Log.e("SwZoomDragImageView", "resetToMaxStatus")
        total_scale = MAX_SCALE
    }

    val initializationBitmapHeight: Float
        get() = height * total_scale
    val initializationBitmapWidth: Float
        get() = width * total_scale

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (total_scale != 1f) {
            parent.requestDisallowInterceptTouchEvent(true) //触摸事件请求拦截
        }
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mode = MODE_DRAG
                matrixBefore.set(imageMatrix)
                matrixNow.set(imageMatrix)
                dragPoint[event.x] = event.y
                actionDownPoint[event.x] = event.y
                Log.e("SwZoomDragImageView", "MotionEvent.ACTION_DOWN")
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true) //触摸事件请求拦截
                mode = MODE_ZOOM
                startPoint[event.x] = event.y
                startDis = distance(event)
                /** 计算两个手指间的中间点  */
                if (startDis > 10f) {
                    //记录当前ImageView的缩放倍数
                    matrixBefore.set(imageMatrix)
                    matrixNow.set(imageMatrix)
                }
                Log.e("SwZoomDragImageView", "MotionEvent.ACTION_POINTER_DOWN")
            }
            MotionEvent.ACTION_MOVE -> {
                if (mode == MODE_DRAG && total_scale > 1) {
                    val dx = event.x - dragPoint.x
                    val dy = event.y - dragPoint.y
                    dragPoint[event.x] = event.y
                    imgTransport(dx, dy)
                } else if (mode == MODE_ZOOM) { //缩放
                    val endDis = distance(event)
                    midPoint = mid(event)
                    if (endDis > 10f) {
                        current_scale = endDis / startDis //缩放倍数
                        total_scale *= current_scale
                        matrixNow.postScale(current_scale, current_scale, midPoint.x, midPoint.y)
                        invalidate()
                    }
                    startDis = endDis
                }
                Log.e("SwZoomDragImageView", "MotionEvent.ACTION_MOVE")
            }
            MotionEvent.ACTION_UP -> {
                parent.requestDisallowInterceptTouchEvent(false) //触摸事件请求取消拦截
                mode = 0
                if (mode == MODE_DRAG) checkClick(
                    event.x,
                    event.y,
                    actionDownPoint.x,
                    actionDownPoint.y
                )
                Log.e("SwZoomDragImageView", "MotionEvent.ACTION_UP---" + MODE_DRAG)
            }
            MotionEvent.ACTION_POINTER_UP -> {
                checkZoomValid()
                mode = 0
                Log.e("SwZoomDragImageView", "MotionEvent.ACTION_POINTER_UP")
            }
        }
        return true
    }

    /**
     * 平移图片
     * @param x
     * @param y
     */
    fun imgTransport(x: Float, y: Float) {
        var x = x
        var y = y
        mCurrentScalePoint[mCurrentScalePoint.x + x] = mCurrentScalePoint.y + y
        if (mCurrentScalePoint.x >= (total_scale - 1) * width / 2) {
            mCurrentScalePoint.x = (total_scale - 1) * width / 2
            x = 0f
        } else {
            if (mCurrentScalePoint.x <= -((total_scale - 1) * width) / 2) {
                mCurrentScalePoint.x = -((total_scale - 1) * width) / 2
                x = 0f
            }
        }
        if (mCurrentScalePoint.y >= (total_scale - 1) * height / 2) {
            mCurrentScalePoint.y = (total_scale - 1) * height / 2
            y = 0f
        } else {
            if (mCurrentScalePoint.y <= -((total_scale - 1) * height) / 2) {
                mCurrentScalePoint.y = -((total_scale - 1) * height) / 2
                y = 0f
            }
        }
        Log.i(TAG, "mCurrentScalePoint.x:" + mCurrentScalePoint.x + "   x:" + x)
        matrixNow.postTranslate(x, y)
        invalidate()
    }

    private fun checkZoomValid(): Boolean {
        if (mode == MODE_ZOOM) {
            if (total_scale > MAX_SCALE) {
                Log.e(
                    "SwZoomDragImageView",
                    "checkZoomValid" + "total_scale--" + total_scale + ">>>MAX_SCALE" + MAX_SCALE
                )
                resetToMaxStatus()
                matrixNow.set(mInitializationMatrix)
                matrixNow.postScale(
                    MAX_SCALE,
                    MAX_SCALE,
                    mInitializationScalePoint.x,
                    mInitializationScalePoint.y
                )
                matrixNow.postTranslate(mCurrentScalePoint.x, mCurrentScalePoint.y)
                invalidate()
                LiveDataBus.get().with("DrawingView").postValue(true)
                return false
            } else if (total_scale < MIN_SCALE) {
                Log.e(
                    "SwZoomDragImageView",
                    "checkZoomValid" + "total_scale--" + total_scale + "<<<MAX_SCALE" + MAX_SCALE
                )
                resetToMinStatus()
                matrixNow.set(mInitializationMatrix)
                invalidate()
                return false
            }
            invalidate()
        }
        return true
    }

    private fun distance(event: MotionEvent): Float {
        Log.e("SwZoomDragImageView", "distance")
        val dx = event.getX(1) - event.getX(0)
        val dy = event.getY(1) - event.getY(0)
        return Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }

    private fun mid(event: MotionEvent): PointF {
        Log.e("SwZoomDragImageView", "mid")
        val midX = (event.getX(1) + event.getX(0)) / 2
        val midY = (event.getY(1) + event.getY(0)) / 2
        return PointF(midX, midY)
    }

    fun checkClick(last_x: Float, last_y: Float, now_x: Float, now_y: Float): Boolean {
        Log.e("SwZoomDragImageView", "checkClick")
        val x_d = Math.abs(last_x - now_x)
        val y_d = Math.abs(last_y - now_y)
        if (x_d < 10 && y_d < 10) { //点击事件
            //处理单击事件
        }
        if (total_scale == 1f) {
            matrixNow.set(mInitializationMatrix)
            invalidate()
        }
        return false
    }

    override fun getImageMatrix(): Matrix {
        Log.e("SwZoomDragImageView", "getImageMatrix")
        return matrixNow
    }

    val beforeImageMatrix: Matrix
        get() {
            Log.e("SwZoomDragImageView", "getBeforeImageMatrix")
            return matrixBefore
        }

    companion object {
        private val TAG = SwZoomDragImageView::class.java.simpleName
        private const val MODE_DRAG = 1 //平移
        private const val MODE_ZOOM = 2 //缩放
        private const val MAX_SCALE = 15f
        private const val MIN_SCALE = 1f //最大放大倍数，最小放大倍数
    }

    init {
        initData()
    }
}