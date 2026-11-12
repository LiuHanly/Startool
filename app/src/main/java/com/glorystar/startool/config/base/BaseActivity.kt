package com.glorystar.startool.config.base

import android.app.Application.getProcessName
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.glorystar.startool.utils.tool.IntentJumpUtils
import com.glorystar.startool.utils.tool.LanguageUtil
import com.glorystar.startool.utils.tool.LiveDataBus

abstract class BaseActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Android 9 (API 28) 及以上系统，必须为非主进程的 WebView 指定独立的数据目录后缀
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = getProcessName()
            if (packageName != processName) {
                // 如果不是主进程，为 WebView 设置目录后缀（比如叫 webview）
                WebView.setDataDirectorySuffix("webview")
            }
        }
        init()
        // 设置布局
        setContentView(getLayoutId())
        // 初始化视图
        initView(savedInstanceState)



    }

    open fun init(){
        var item = IntentJumpUtils.getLanguage()
        LanguageUtil.changLang(this, item)
        LiveDataBus.get().with("Language", String::class.java)
            .observe(this, androidx.lifecycle.Observer {
                when (it) {
                    "refresh_language" -> {
                        var item = IntentJumpUtils.getLanguage()
                        LanguageUtil.changLang(this, item)
                        this?.run {
                            ActivityCompat.recreate(this@BaseActivity)
                        }
                    }
                }
            })
    }

    /**
     * 设置布局
     */
    abstract fun getLayoutId(): Int

    /**
     * 初始化视图
     */
    abstract fun initView(savedInstanceState: Bundle?)


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        return super.onKeyDown(keyCode, event)

    }



}