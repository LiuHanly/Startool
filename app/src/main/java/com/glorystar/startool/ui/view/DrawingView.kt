package com.glorystar.startool.ui.view

import android.app.Activity
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.glorystar.startool.utils.tool.LiveDataBus

import java.util.*

class DrawingView : View {
    private var mPaint: Paint? = null
    private var mCanvas: Canvas? = null
    private var mPath: Path? = null
    private var mBitmapPaint: Paint? = null
    private var circlePaint: Paint? = null
    private var circlePath: Path? = null
    private var mcontext: Activity? = null
    private var bitmap //位图
            : Bitmap? = null

    //    var width = 0
//    var height = 0

    var pathButton1 = mutableSetOf<Int>()  //左右
    var pathButton2 = mutableSetOf<Int>()  //右左
    var pathButton3 = mutableSetOf<Int>()  //上
    var pathButton4 = mutableSetOf<Int>()  //下
    var pathButton5 = mutableSetOf<Int>()  //左
    var pathButton6 = mutableSetOf<Int>()  //右

    private var mX = 0f
    private var mY = 0f

    constructor(context: Activity) : super(context) {
        init(context)
    }

    constructor(context: Activity, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Activity?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    private fun init(context: Activity) {
        mPath = Path()
        mcontext = context
        mBitmapPaint = Paint(Paint.DITHER_FLAG)
        mBitmapPaint!!.color = Color.RED
        circlePaint = Paint()
        circlePath = Path()
        circlePaint!!.isAntiAlias = true
        circlePaint!!.color = Color.RED
        circlePaint!!.style = Paint.Style.STROKE
        circlePaint!!.strokeJoin = Paint.Join.MITER
        circlePaint!!.strokeWidth = 4f
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.isDither = true
        mPaint!!.color = Color.GREEN
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.strokeJoin = Paint.Join.ROUND
        mPaint!!.strokeCap = Paint.Cap.ROUND
        mPaint!!.strokeWidth = 12f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val outMetrics = DisplayMetrics()
        mcontext!!.windowManager.defaultDisplay.getMetrics(outMetrics)
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888) //设置位图的宽高
//        width = w
//        height = h
        mCanvas = Canvas()
        // TrigonPain(mCanvas);
        mCanvas!!.setBitmap(bitmap)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(bitmap!!, 0f, 0f, mBitmapPaint)
        TrigonPain(mCanvas)
        canvas.drawPath(mPath!!, mPaint!!)
    }

    private fun touch_start(x: Float, y: Float) {
        mPath!!.reset()
        mPath!!.moveTo(x, y)
        mX = x
        mY = y
    }

    private fun touch_move(x: Float, y: Float) {
        val dx = Math.abs(x - mX)
        val dy = Math.abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath!!.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            //mPath.quadTo(mX, mY, x, y);
            mX = x
            mY = y
            circlePath!!.reset()
            circlePath!!.addCircle(mX, mY, 30f, Path.Direction.CW)
            listTouchPoints(x, y)
            RightTouchPoints(x, y)
            LeftpathTouchPoints(x, y)
            RightpathTouchPoints(x, y)
            ToppathTouchPoints(x, y)
            BoompathTouchPoints(x, y)
        }
    }

    private fun touch_up() {
        mPath!!.lineTo(mX, mY)
        circlePath!!.reset()
        // commit the path to our offscreen
        mCanvas!!.drawPath(mPath!!, mPaint!!)
        // kill this so we don't double draw
        if (pathButton1.size == 5 && pathButton2.size == 5 && pathButton3.size == 5 && pathButton4.size == 5 && pathButton5.size == 5 && pathButton6.size == 5) {

            LiveDataBus.get().with("DrawingView").postValue(true)

        }
        mPath!!.reset()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touch_start(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touch_move(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touch_up()
                invalidate()
            }
        }
        return true
    }

    //绘制背景
    fun TrigonPain(canvas: Canvas?) {
        val p = Paint()
        p.color = Color.BLUE
        p.isAntiAlias = true
        val p2 = Paint()
        p2.color = Color.BLACK
        p2.isAntiAlias = true

        //实例化路径
        val Leftpath = Path() //左边的三角形
        Leftpath.moveTo(100f, (height * 0.15).toFloat()) // 此点为多边形的起点
        Leftpath.lineTo(100f, (height * 0.85).toFloat())
        Leftpath.lineTo((width * 0.5).toFloat() - 100, (height * 0.5).toFloat())
        Leftpath.close() // 使这些点构成封闭的多边形

        //右边的三角形
        val Rightpath = Path()
        Rightpath.moveTo((width - 100).toFloat(), (height * 0.15).toFloat())
        Rightpath.lineTo((width - 100).toFloat(), (height * 0.85).toFloat())
        Rightpath.lineTo((width * 0.5).toFloat() + 100, (height * 0.5).toFloat())

        //上发的三角形
        val Toppath = Path()
        Toppath.moveTo((width - 200).toFloat(), (height * 0.1).toFloat())
        Toppath.lineTo(200f, (height * 0.1).toFloat()) // 此点为多边形的起点
        Toppath.lineTo((width * 0.5).toFloat(), (height * 0.5).toFloat() - 50)

        //下方三角形
        val Boompath = Path()
        Boompath.moveTo(200f, (height * 0.9).toFloat())
        Boompath.lineTo((width - 200).toFloat(), (height * 0.9).toFloat())
        Boompath.lineTo((width * 0.5).toFloat(), (height * 0.5).toFloat() + 50)

        canvas!!.drawPath(Leftpath, p)
        canvas.drawPath(Rightpath, p)
        canvas.drawPath(Toppath, p2)
        canvas.drawPath(Boompath, p2)
    }

    //判断左上角至右下角 斜线
    fun listTouchPoints(px: Float, py: Float) {
        if (px in 1.0..150.0 && py in 0.0..150.0) {
            pathButton1.add(1)
        }
        if (px in (width * 0.25) - 150..(width * 0.25) + 150 && py in (height * 0.25) - 150..(height * 0.25) + 150) {
            pathButton1.add(2)
        }
        if (px in (width * 0.5) - 150..(width * 0.5) + 150 && py in (height * 0.5) - 150..(height * 0.5) + 150) {
            pathButton1.add(3)
        }
        if (px in (width * 0.75) - 150..(width * 0.75) + 200 && py in (height * 0.75) - 150..(height * 0.75) + 150) {
            pathButton1.add(4)
        }
        if (px in (width) - 150.0..(width) + 150.0 && py in (height - 150.0)..(height + 150.0)) {
            pathButton1.add(5)
        }
    }

    //判断右上角至左下角 斜线
    fun RightTouchPoints(px: Float, py: Float) {
        if (px in (width) - 150.0..(width) + 150.0 && py in 0.0..150.0) {
            pathButton2.add(6)
        }
        if (px in (width * 0.75) - 150..(width * 0.75) + 150 && py in (height * 0.25) - 150..(height * 0.25) +150) {
            pathButton2.add(7)
        }
        if (px in (width * 0.5) - 150..(width * 0.5) + 150 && py in (height * 0.5) - 150..(height * 0.5) + 150) {
            pathButton2.add(8)
        }
        if (px in (width * 0.25) - 150..(width * 0.25) + 150 && py in (height * 0.75) - 150..(height * 0.75) + 150) {
            pathButton2.add(9)
        }
        if (px in 1.0..150.0 && py in (height - 150.0)..(height + 150.0)) {
            pathButton2.add(10)
        }
    }

    // 左边框
    fun LeftpathTouchPoints(px: Float, py: Float) {
        if (px in 1.0..150.0 && py in 0.0..150.0) {
            pathButton3.add(11)
        }
        if (px in 1.0..150.0 && py in (height * 0.25) - 150..(height * 0.25) + 150) {
            pathButton3.add(12)
        }
        if (px in 1.0..150.0 && py in (height * 0.5) - 150..(height * 0.5) + 150) {
            pathButton3.add(13)
        }
        if (px in 1.0..150.0 && py in (height * 0.75) - 150..(height * 0.75) + 150) {
            pathButton3.add(14)
        }
        if (px in 1.0..150.0 && py in (height - 150.0)..(height + 150.0)) {
            pathButton3.add(15)
        }
    }

    //右边框
    fun RightpathTouchPoints(px: Float, py: Float) {
        if (px in (width) - 150.0..(width) + 150.0 && py in 0.0..150.0) {
            pathButton4.add(16)
        }
        if (px in (width) - 150.0..(width) + 150.0 && py in (height * 0.25) - 150..(height * 0.25) + 150) {
            pathButton4.add(17)
        }
        if (px in (width) - 150.0..(width) + 150.0 && py in (height * 0.5) - 150..(height * 0.5) + 150) {
            pathButton4.add(18)
        }
        if (px in (width) - 150.0..(width) + 150.0 && py in (height * 0.75) - 150..(height * 0.75) + 150) {
            pathButton4.add(19)
        }
        if (px in (width) - 150.0..(width) + 150.0 && py in (height - 150.0)..(height + 150.0)) {
            pathButton4.add(20)
        }
    }

    //上边框
    fun ToppathTouchPoints(px: Float, py: Float) {
        if (px in 1.0..150.0 && py in 0.0..150.0) {
            pathButton5.add(21)

        }
        if (px in (width * 0.25) - 150..(width * 0.25) + 150 && py in 0.0..150.0) {
            pathButton5.add(22)
        }
        if (px in (width * 0.5) - 150..(width * 0.5) + 150 && py in 0.0..150.0) {
            pathButton5.add(23)
        }
        if (px in (width * 0.75) - 150..(width * 0.75) + 150 && py in 0.0..150.0) {
            pathButton5.add(24)
        }

        if (px in (width) - 100.0..(width) + 150.0 && py in 0.0..150.0) {
            pathButton5.add(25)
        }
    }

    // 下边框
    fun BoompathTouchPoints(px: Float, py: Float) {
        if (px in (width) - 150.0..(width) + 150.0 && py in (height - 150.0)..(height + 150.0)) {
            pathButton6.add(26)
        }
        if (px in (width * 0.25) - 150..(width * 0.25) + 150 && py in (height - 150.0)..(height + 150.0)) {
            pathButton6.add(27)
        }
        if (px in (width * 0.5) - 150..(width * 0.5) + 150 && py in (height - 150.0)..(height + 150.0)) {
            pathButton6.add(28)
        }
        if (px in (width * 0.75) - 150..(width * 0.75) + 150 && py in (height - 150.0)..(height + 150.0)) {
            pathButton6.add(29)
        }

        if (px in (width) - 100.0..(width) + 150.0 && py in (height - 150.0)..(height + 150.0)) {
            pathButton6.add(30)
        }
    }

    companion object {
        private const val TOUCH_TOLERANCE = 4f
    }

}