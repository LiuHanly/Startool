package com.glorystar.startool.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.*

class MyOnTouchView : View {
    var circles: MutableList<Circle?> = ArrayList()

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paint = Paint()
        for (circle in circles) {

            circle!!.drawSelf(canvas, paint)


        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //获取手指的行为
        val action = event.action
        var action_code = action and 0xff
        //手指的下标Index
        val pointIndex = action shr 8
        //获取手指的坐标
        val x = event.getX(pointIndex)
        val y = event.getY(pointIndex)

        //获取手指的名字ID
        val pointId = event.getPointerId(pointIndex)
        if (action_code >= 5) {
            action_code -= 5
        }

        when (action_code) {
            MotionEvent.ACTION_DOWN ->{
                val circle = pointId?.let { x?.let { it1 -> Circle(it1, y!!, it) } }
                circles.add(circle)
            }
            MotionEvent.ACTION_UP -> circles.remove(get(pointId))
            MotionEvent.ACTION_MOVE -> {
                var i = 0
                while (i < event.pointerCount) {
                    val id = event.getPointerId(i)
                    get(id)!!.x = event.getX(i)
                    get(id)!!.y = event.getY(i)
                    i++
                }
            }
        }
        //重新调用onDraw  重绘
        invalidate()
        return true
    }

    operator fun get(pointId: Int): Circle? {
        for (circle in circles) {
            if (circle!!.pointId == pointId) {
                return circle
            }
        }
        return null
    }
}