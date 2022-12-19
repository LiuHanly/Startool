package com.glorystar.startool.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class DincondFontTextView : AppCompatTextView {
    constructor(context: Context?) : super(context!!) {
        setTypeface()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs) {
        setTypeface()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle) {
        setTypeface()
    }

    private fun setTypeface() {
        // 如果自定义typeface初始化失败，就用原生的typeface
        typeface = if (StressTestApplication.sMIUIBoldTextType == null) {
            typeface
        } else {
            StressTestApplication.sMIUIBoldTextType
        }
    }
}