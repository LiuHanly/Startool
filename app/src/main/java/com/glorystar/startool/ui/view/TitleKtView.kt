package com.glorystar.startool.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout

import android.widget.RelativeLayout
import android.widget.TextView
import com.glorystar.startool.R

class TitleKtView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    lateinit var back_linear: LinearLayout
    lateinit var title: TextView

    var onLetterTouchedChangeListener: OnLetterTouchedChangeListener? = null


    fun setonLetterTouchedChangeListener(onLetterTouchedChangeListener: OnLetterTouchedChangeListener?) {
        this.onLetterTouchedChangeListener = onLetterTouchedChangeListener
    }

    init {
        View.inflate(context, R.layout.titlekt_view, this)
        initView()
    }

    fun initView() {
        back_linear = findViewById(R.id.back_linear)
        title = findViewById(R.id.title)
        back_linear.setOnClickListener {
            onLetterTouchedChangeListener?.onTouchedLetterChange(it.id)
        }

    }


    interface OnLetterTouchedChangeListener {
        fun onTouchedLetterChange(id: Int)
    }
}