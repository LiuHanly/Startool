package com.glorystar.startool.ui.view

import android.graphics.*
import com.glorystar.startool.R
import com.glorystar.startool.config.base.MyApplication.Companion.context

import java.util.*

class Circle(var x: Float, var y: Float, var pointId: Int) {
    var r = 100
    var red: Int
    var green: Int
    var blue: Int
    var random = Random()
    fun drawSelf(canvas: Canvas, paint: Paint) {
        val bitmap = BitmapFactory.decodeResource(
            context?.resources,
            R.mipmap.touchtest_logoxxxhdpi
        )
        canvas.drawBitmap(imageScale(bitmap,150,150)!!, x-75, y-75, paint)

    }

    init {
        red = random.nextInt(255)
        green = random.nextInt(255)
        blue = random.nextInt(255)
    }

    fun imageScale(bitmap: Bitmap, dst_w: Int, dst_h: Int): Bitmap? {
        val src_w = bitmap.width
        val src_h = bitmap.height
        val scale_w = dst_w.toFloat() / src_w
        val scale_h = dst_h.toFloat() / src_h
        val matrix = Matrix()
        matrix.postScale(scale_w, scale_h)
        return Bitmap.createBitmap(
            bitmap, 0, 0, src_w, src_h, matrix,
            true
        )
    }

}