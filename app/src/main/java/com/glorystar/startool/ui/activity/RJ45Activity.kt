package com.glorystar.startool.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application.getProcessName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler

import android.os.Looper
import android.os.Message
import android.util.Log
import android.webkit.*
import android.widget.Button
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.constant.Constantdtas
import com.glorystar.startool.config.constant.IntenAcitivity
import com.glorystar.startool.ui.view.AutoWebView
import com.glorystar.startool.utils.tool.StatusNaviBarUitl


class RJ45Activity : BaseActivity() {

    lateinit var full_webView:WebView
    lateinit var failure_bu: Button
    lateinit var through_bu: Button

    private var retryCount = 0
    private val MAX_RETRY = 10

    companion object {
        private const val TAG = "RJ45Activity"
        private const val MSG_CHECK_LAN = 0
        private const val REFRESH_TIME = 2000L

        fun toStartActivity(context: Activity) {
            val intent = Intent(context, RJ45Activity::class.java)
            context.startActivity(intent)
            context.finish()
        }
    }

    private val mAutoRefreshRunnable = Runnable {
        if (retryCount < MAX_RETRY) {
            retryCount++
            Log.w(TAG, "自动重试第 $retryCount 次")
            full_webView.reload()
        }
    }

    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
        when (msg.what) {
                MSG_CHECK_LAN -> {
                    Log.d(TAG, "checkLan = ${Constantdtas.checkLan()}")
                    if (Constantdtas.checkLan()) {
                        val url = "http://www.apple.com.cn"
                        initWebSetting(url)
                    } else {
                    }
                }
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_rj45
    }

    override fun initView(savedInstanceState: Bundle?) {
        try {
            StatusNaviBarUitl.hide(this@RJ45Activity)
        } catch (_: Exception) {}

        // Android 9 (API 28) 及以上系统，必须为非主进程的 WebView 指定独立的数据目录后缀
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = getProcessName()
            if (packageName != processName) {
                // 如果不是主进程，为 WebView 设置目录后缀（比如叫 webview）
                WebView.setDataDirectorySuffix("webview")
            }
        }

        full_webView = findViewById(R.id.full_webView)
        failure_bu = findViewById(R.id.failure_bu)
        through_bu = findViewById(R.id.through_bu)

        // ===== 先设置 Client，再发消息加载 =====
        setupWebViewClient()

        // 发送检查消息
        mHandler.sendEmptyMessage(MSG_CHECK_LAN)

        failure_bu.setOnClickListener {
            try { StatusNaviBarUitl.hide(this@RJ45Activity) } catch (_: Exception) {}
            IntenAcitivity.getIntenAcitivity(this@RJ45Activity, 6, 1)
        }

        through_bu.setOnClickListener {
            try { StatusNaviBarUitl.hide(this@RJ45Activity) } catch (_: Exception) {}
            IntenAcitivity.getIntenAcitivity(this@RJ45Activity, 6, 2)
        }
    }

    /**
     * 设置 WebViewClient 和 WebChromeClient
     */
    private fun setupWebViewClient() {
        full_webView.webChromeClient = WebChromeClient()

        full_webView.webViewClient = object : WebViewClient() {

            // ✅ 修复：不再手动 loadUrl，让 WebView 自行处理
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(TAG, "页面加载完成: $url")
                retryCount = 0 // 加载成功后重置计数
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                // 只处理主页面的错误，忽略子资源（图片、广告等）
                if (request?.isForMainFrame == true) {
                    Log.e(TAG, "加载失败: ${error?.description}, url=${request.url}")
                    // ✅ 修复：加入重试上限，防止死循环
                    mHandler.postDelayed(mAutoRefreshRunnable, REFRESH_TIME)
                }
            }

            // ✅ 新增：处理 SSL 错误（自签证书设备）
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: android.net.http.SslError?
            ) {
                Log.w(TAG, "SSL错误: ${error?.primaryError}")
                handler?.proceed() // 内网测试设备可放行；生产环境应校验证书
            }
        }
    }

    /**
     * ✅ 修复：所有 Settings 在 loadUrl 之前设置
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebSetting(url: String) {
        val webSettings: WebSettings = full_webView.settings

        // 基础配置
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.mediaPlaybackRequiresUserGesture = false
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT

        // ✅ 允许混合内容（http页面中加载https资源）
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        // ✅ 最后才加载
        full_webView.loadUrl(url)
        Log.d(TAG, "开始加载: $url")
    }

    override fun onResume() {
        super.onResume()
        full_webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        full_webView.onPause()
    }

    override fun onStop() {
        super.onStop()
        try {
            StatusNaviBarUitl.hide(this@RJ45Activity)
        } catch (_: Exception) {}
    }

    override fun onDestroy() {
        mHandler.removeCallbacksAndMessages(null) // ✅ 防止内存泄漏
        full_webView.destroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (full_webView.canGoBack()) {
            full_webView.goBack()
        } else {
            super.onBackPressed()
        }
    }


}