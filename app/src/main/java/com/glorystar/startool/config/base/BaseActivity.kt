package com.glorystar.startool.config.base

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.glorystar.startool.utils.tool.IntentJumpUtils
import com.glorystar.startool.utils.tool.LanguageUtil
import com.glorystar.startool.utils.tool.LiveDataBus

abstract class BaseActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置布局
        setContentView(getLayoutId())
        // 初始化视图
        initView(savedInstanceState)



        LiveDataBus.get().with("Language", String::class.java)
            .observe(this, androidx.lifecycle.Observer {
                when (it) {
                    "refresh_language" -> {
                        var item = IntentJumpUtils.getLanguage()
                        LanguageUtil.changLang(this, item)
                        this?.run {
                            ActivityCompat.recreate(this@BaseActivity)
                        }
                        ///   recreate()
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