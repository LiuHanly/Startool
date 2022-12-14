package com.glorystar.startool.ui.view

import android.app.Activity
import android.graphics.*
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import com.glorystar.startool.config.event.LongPressRunnable


class MyTouchView : View {
    private var mPaint: Paint? = null
    private var mCanvas: Canvas? = null
    private var mPath: Path? = null
    private var mBitmapPaint: Paint? = null
    private var circlePaint: Paint? = null
    private var circlePath: Path? = null
    private var mcontext: Activity? = null
    private var bitmap //位图
            : Bitmap? = null

    private var mX = 0f
    private var mY = 0f
    var countdowntimer: CountDownTimer? = null
    companion object {
        private const val TOUCH_TOLERANCE = 4f
    }

    /* 当长按事件发生时，要触发的任务
     */
    private val longPressRunnable: LongPressRunnable = LongPressRunnable()


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

        countdowntimer = object : CountDownTimer(7 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {


            }

        }
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
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        removeCallbacks(longPressRunnable)
        if (event.action == MotionEvent.ACTION_DOWN && event.pointerCount == 1) {
            postCheckForLongTouch(event.x, event.y)
        }
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
    fun postDelayed(){

        countdowntimer?.start()
    }


    private fun touch_up() {
        mPath!!.lineTo(mX, mY)
        circlePath!!.reset()
        // commit the path to our offscreen
        mCanvas!!.drawPath(mPath!!, mPaint!!)
        // kill this so we don't double draw
        mPath!!.reset()
    }
    //绘制背景
    fun TrigonPain(canvas: Canvas?) {
        val p = Paint()
        p.color = Color.BLUE
        p.isAntiAlias = true
        val p2 = Paint()
        p2.color = Color.BLACK
        p2.isAntiAlias = true
        p2.textSize = 18F
        canvas?.drawText("",0f,0f,p2)


    }


    private fun postCheckForLongTouch(x: Float, y: Float) {
        longPressRunnable.setPressLocation(x, y)
        postDelayed(longPressRunnable, 3000)
    }

}