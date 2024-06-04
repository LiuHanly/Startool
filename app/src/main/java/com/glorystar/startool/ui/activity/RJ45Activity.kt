package com.glorystar.startool.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.constant.Constantdtas
import com.glorystar.startool.config.constant.IntenAcitivity
import com.glorystar.startool.utils.tool.NetworkUtil.isConnected
import com.glorystar.startool.utils.tool.NetworkUtils
import com.glorystar.startool.utils.tool.StatusNaviBarUitl

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.jessyan.autosize.AutoSizeCompat
import me.jessyan.autosize.AutoSizeConfig


/**
 * RJ45测试
 * */
class RJ45Activity : BaseActivity() {

    lateinit var full_webView: WebView
    lateinit var failure_bu: Button
    lateinit var through_bu: Button

    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, RJ45Activity::class.java)
            context.startActivity(intent)
            context.finish()

        }
    }

    // 自动刷新
    private val mAutoRefreshRunnable = Runnable { full_webView.reload() }
    protected val REFRESH_TIME = (2 * 1000).toLong()

    @SuppressLint("HandlerLeak")
    var mHandler: Handler = object : Handler() {
        @SuppressLint("NewApi")
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {

                0 -> {
                    Log.e("WIFI_STATE_ENABLED", "Constant.checkLan()--->" + Constantdtas.checkLan())
                    if (Constantdtas.checkLan()) {
                        val url = "http://www.apple.com.cn"
                        initWebSetting(url)

                    } else {
                        Srnus()
                    }
                }
            }
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_rj45
    }

    override fun initView(savedInstanceState: Bundle?) {
        StatusNaviBarUitl.hide(this@RJ45Activity)
        //AutoSizeCompat.autoConvertDensityOfGlobal(resources)

        full_webView = findViewById(R.id.full_webView)
        failure_bu = findViewById(R.id.failure_bu)
        through_bu = findViewById(R.id.through_bu)

        mHandler.sendEmptyMessage(0)
        // full_webView.loadUrl("http://www.apple.com.cn")

        full_webView.webChromeClient = WebChromeClient()
        full_webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onReceivedError(
                view: WebView, errorCode: Int,
                description: String, failingUrl: String
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                if (NetworkUtils.isConnected(applicationContext)) {
                    mHandler.post(mAutoRefreshRunnable)
                } else {
                    mHandler.postDelayed(mAutoRefreshRunnable, REFRESH_TIME)
                }
            }
        }
        failure_bu.setOnClickListener {
            StatusNaviBarUitl.hide(this@RJ45Activity)
            IntenAcitivity.getIntenAcitivity(this@RJ45Activity, 6, 1)

        }
        through_bu.setOnClickListener {
            StatusNaviBarUitl.hide(this@RJ45Activity)
            IntenAcitivity.getIntenAcitivity(this@RJ45Activity, 6, 2)

        }
    }

    override fun onStop() {
        super.onStop()
        StatusNaviBarUitl.hide(this@RJ45Activity)
    }

    /**
     * @title:initWebSetting
     * @desc:初始化网页设置
     * @return void
     * @param mBundle
     * @param url
     */
    @SuppressLint("NewApi")
    fun initWebSetting(url: String) {

        val webSettings: WebSettings = full_webView.settings
        webSettings.mediaPlaybackRequiresUserGesture = false
        webSettings.useWideViewPort = true
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webSettings.loadWithOverviewMode = true
        webSettings.domStorageEnabled = true
        // 启动缓存
        //    webSettings.setAppCacheEnabled(true)
        // 优先使用缓存
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        //初始方案：去掉长按后出现菜单中的搜索功能
        //mWebView.getSettings().setDisabledActionModeMenuItems(0);
        // 不使用缓存
        // webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        full_webView.loadUrl(url)
        // 对网页脚本进行判断
        webSettings.javaScriptEnabled = true
        // 手动放大Zoom
        webSettings.builtInZoomControls = true
    }

    fun Srnus() {

        mHandler.sendEmptyMessageDelayed(0, 1000)
    }

}