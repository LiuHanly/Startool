package com.glorystar.startool.ui.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import me.jessyan.autosize.AutoSize

class AutoWebView : WebView {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun setOverScrollMode(mode: Int) {
        super.setOverScrollMode(mode)
        AutoSize.autoConvertDensityOfGlobal(context as Activity)
    }
}
