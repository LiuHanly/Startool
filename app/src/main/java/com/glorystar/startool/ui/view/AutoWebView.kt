package com.glorystar.startool.ui.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import me.jessyan.autosize.AutoSize

// 💡 优化点 1：使用 @JvmOverloads 自动生成所有必要的构造函数，保证 XML 反射不漏参数
class AutoWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {


}

