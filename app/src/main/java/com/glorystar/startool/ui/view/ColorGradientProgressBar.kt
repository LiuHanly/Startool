package com.glorystar.startool.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


class ColorGradientProgressBar @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    /**
     * 渐变颜色组
     */
    private val GRADIENT_COLORS =
        intArrayOf(Color.parseColor("#85D0E8"), Color.parseColor("#138CCF"))
    /***
     * 设置最大进度
     *
     * @param maxCount
     */
    /**
     * 最大进度
     */
    var max = 100f

    /**
     * 当前进度
     */
    private var progress = 0f

    /**
     * 画笔
     */
    private var mPaint: Paint? = null

    /**
     * 外描边的宽度
     */
    private var BORDER_STROCK = 0f

    /**
     * 进度条进度矩形与控件边界的距离,≥BORDER_STROCK
     */
    private var PROGRESS_STROCK = 0f

    //进度条的宽高
    private var mWidth = 0
    private var mHeight = 0

    /**
     * 画进度条的矩形
     */
    private var mRectF: RectF? = null
    private fun initView() {
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mRectF = RectF()
        BORDER_STROCK = 0f
        PROGRESS_STROCK = 0f
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val round = mHeight/32 //弧度为高度的一半
        mRectF!![0f, 0f, mWidth.toFloat()] = mHeight.toFloat() //第一层矩形(描边层)
        mPaint!!.color = Color.parseColor("#A3D8E8") //第一层矩形颜色(进度条描边的颜色)
        canvas.drawRoundRect(mRectF!!, round.toFloat(), round.toFloat(), mPaint!!) //画第一层圆角矩形
        mPaint!!.color = Color.parseColor("#424e7b") //第二层矩形颜色(背景层颜色)
        mRectF!![BORDER_STROCK, BORDER_STROCK, mWidth - BORDER_STROCK] =
            mHeight - BORDER_STROCK //第二层矩形(背景层)
        canvas.drawRoundRect(mRectF!!,
            round.toFloat(),
            round.toFloat(),
            mPaint!!) //画背景层圆角矩形(盖在描边层之上)
        if (progress == 0f) //进度为 0不画进度
            return
        val section = progress / max

//第三层矩形(进度层)
        mRectF!![PROGRESS_STROCK, PROGRESS_STROCK, (mWidth - PROGRESS_STROCK) * section] =
            mHeight - PROGRESS_STROCK

//创建线性颜色渐变器
        val shader = LinearGradient(PROGRESS_STROCK,
            PROGRESS_STROCK,
            (mWidth - PROGRESS_STROCK) * section,
            mHeight - PROGRESS_STROCK,
            GRADIENT_COLORS,
            null,
            Shader.TileMode.MIRROR)
        mPaint!!.shader = shader //第三层矩形颜色(进度渐变色)
        canvas.drawRoundRect(mRectF!!,
            round.toFloat(),
            round.toFloat(),
            mPaint!!) //画第三层(进度层)圆角矩形(盖在背景层之上)
        mPaint!!.shader = null //清除之前传递的shader
    }

    /***
     * 设置当前进度
     *
     * @param currentCount
     */
    fun setProgress(currentCount: Float) {
        progress = if (currentCount > max) max else currentCount
        invalidate()
    }

    /**
     * 测量得到进度条的宽高
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        mWidth = if (widthSpecMode == MeasureSpec.EXACTLY || widthSpecMode == MeasureSpec.AT_MOST) widthSpecSize else 0
        mHeight = if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) 20 else heightSpecSize
        setMeasuredDimension(mWidth, mHeight)
    }

    init {
        initView()
    }
}