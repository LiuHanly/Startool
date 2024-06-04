package com.glorystar.startool.ui.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.glorystar.startool.BuildConfig
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.base.BasePoup
import com.glorystar.startool.config.bean.OnlineData
import com.glorystar.startool.config.bean.UpdateBean
import com.glorystar.startool.config.bean.UpdateData
import com.glorystar.startool.config.constant.Constantdtas
import com.glorystar.startool.ui.service.DownloadService
import com.glorystar.startool.ui.testactivity.TestMainActivity
import com.glorystar.startool.utils.tool.*
import com.lxj.xpopup.enums.PopupPosition
import java.io.File

class MainActivity : BaseActivity(), UpdateData {
    private lateinit var linear_general: LinearLayout
    private lateinit var linear_model: LinearLayout
    private lateinit var linear_addon: LinearLayout
    lateinit var linear_aging: LinearLayout
    private lateinit var language_text: TextView
    lateinit var linearUpdate: LinearLayout
    lateinit var update: ImageView
    lateinit var checkUpdate: Button
    lateinit var ivLoading: ImageView
    var filePath = ""
    var downloadUrl = ""
    lateinit var progressBar: ProgressBar
    lateinit var dialog: AlertDialog
    lateinit var rotateAnimation: Animation
    lateinit var update1: ImageView

    @SuppressLint("HandlerLeak")
    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> {
                    ivLoading.clearAnimation()
                    ivLoading.visibility = View.GONE
                    checkUpdate.visibility = View.VISIBLE
                    val ub = msg.obj as UpdateBean
                    downloadUrl = ub.apkUrl
                    checkUpdate.text = getString(R.string.update_now)
                }

                1 -> {
                    ivLoading.clearAnimation()
                    linearUpdate.visibility = View.VISIBLE
                    ivLoading.visibility = View.GONE
                }

                else -> {
                }
            }
        }
    }

    /**
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    /**
     */
    override fun initView(savedInstanceState: Bundle?) {
        linear_general = findViewById(R.id.linear_general)
        linear_model = findViewById(R.id.linear_model)
        linear_addon = findViewById(R.id.linear_addon)
        linear_aging = findViewById(R.id.linear_aging)
        language_text = findViewById(R.id.textlanguage)
        update = findViewById(R.id.iv_update)
        update1 = findViewById(R.id.iv_update1)

        // StatusNaviBarUitl.hide(application)

        val filter = IntentFilter()
        filter.addAction("com.glorystar.intent.APK_DEMO_UPDATE_PERCENT")
        registerReceiver(updateDemoReceiver, filter)

        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.nim_circle_rotate)
        val interpolator = LinearInterpolator()
        rotateAnimation.interpolator = interpolator

        linear_general.setOnClickListener {
            this.startActivity(Intent(this, GeneralActivity::class.java))
        }

        linear_model.setOnClickListener {
            startActivity(Intent(this, ModelActivity::class.java))
        }

        linear_addon.setOnClickListener {
            startActivity(Intent(this, AddonActivity::class.java))
        }

        linear_aging.setOnClickListener {

            this.startActivity(Intent(this, AgingActivity::class.java))
        }

        update.setOnClickListener {
            updateDialog(this@MainActivity)
        }
        update1.setOnClickListener {
          TestMainActivity.toStartActivity(this@MainActivity)


        }

        language_text.setOnClickListener {
            BasePoup.Builder(this)
                .hasShadowBg(false)
                .atView(it)
                .popupPosition(PopupPosition.Bottom)
                .asAttaServedView(Constantdtas.AttaServedName) { position, text ->
                    language_text.text = text
                    IntentJumpUtils.setLanguage(position)
                    LiveDataBus.get().with("Language").postValue("refresh_language")
                }.show()
        }
    }

    override fun onStart() {
        super.onStart()
        super.init()
    }

    private fun updateDialog(context: Context?) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_update, null)
        checkUpdate = view.findViewById<Button>(R.id.bt_check_update)
        var appVersion = view.findViewById<TextView>(R.id.app_version)
        ivLoading = view.findViewById<ImageView>(R.id.iv_loading)
        linearUpdate = view.findViewById<LinearLayout>(R.id.linear_up_date)
        var goBack = view.findViewById<TextView>(R.id.tv_go_back)

        appVersion.text =
            getString(R.string.app_version) + " " + VersionInfoUtil.getVerName(context)

        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setCancelable(false)
        var updateDialog = builder.create()
        goBack.setOnClickListener { updateDialog.dismiss() }
        checkUpdate.setOnClickListener {
            if (checkUpdate.text.equals(getString(R.string.check_for_update))) {
                if (NetworkUtils.isConnected(this@MainActivity)) {
                    OnlineData().getApkData(Constantdtas.UPDATE_APP, this)
                    ivLoading.visibility = View.VISIBLE
                    ivLoading.startAnimation(rotateAnimation)
                    checkUpdate.visibility = View.GONE
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "The network is not connected",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } else if (checkUpdate.text.equals(getString(R.string.update_now))) {
                if (!TextUtils.isEmpty(downloadUrl)) {
                    updateDialog.dismiss()
                    showDialog(
                        this@MainActivity,
                        R.drawable.ic_downloading,
                        getString(R.string.update_startool),
                        "",
                        1
                    )
                    val intent = Intent(this@MainActivity, DownloadService::class.java)
                    intent.action = "glorystar.intent.action.DOWNLOAD_DEMO_APK"
                    intent.putExtra("downloadUrl", downloadUrl)
                    intent.putExtra("name", "starTool")
                    startService(intent)
                }
            }
        }
        updateDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        updateDialog.show()

    }

    private fun showDialog(context: Context?, iconId: Int, title: String, info: String, type: Int) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_ota, null)
        var ivIcon = view.findViewById<ImageView>(R.id.iv_info)
        var tvTitle = view.findViewById<TextView>(R.id.tv_info_title)
        var tvInfo = view.findViewById<TextView>(R.id.tv_info)
        var goBack = view.findViewById<TextView>(R.id.tv_go_back)
        progressBar = view.findViewById<ProgressBar>(R.id.pb_update)

        if (type == 0) {
            progressBar.visibility = View.GONE
            tvInfo.visibility = View.VISIBLE
            goBack.text = getString(R.string.go_back)
        } else {
            progressBar.visibility = View.VISIBLE
            tvInfo.visibility = View.GONE
            goBack.text = getString(R.string.cancel)
        }

        ivIcon.setImageResource(iconId)
        tvTitle.text = title
        tvInfo.text = info
        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setCancelable(false)
        dialog = builder.create()
        goBack.setOnClickListener { dialog.dismiss() }
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

    }

    var updateDemoReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "com.glorystar.intent.APK_DEMO_UPDATE_PERCENT") {
                val percent = intent.getIntExtra("percent", 0)
                filePath = intent.getStringExtra("filePath")!!
                progressBar.setProgress(percent)
                if (percent == 100) {
                    if (!TextUtils.isEmpty(filePath)) {
                        dialog.dismiss()
                        val installIntent = Intent(Intent.ACTION_VIEW)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            installIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            val contentUri = FileProvider.getUriForFile(
                                this@MainActivity,
                                BuildConfig.APPLICATION_ID.toString() + ".fileProvider",
                                File(filePath)
                            )
                            installIntent.setDataAndType(
                                contentUri,
                                "application/vnd.android.package-archive"
                            )
                        } else {
                            val uri = Uri.parse("file://" + File(filePath))
                            installIntent.setDataAndType(
                                uri,
                                "application/vnd.android.package-archive"
                            )
                        }
                        startActivity(installIntent)
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Please download the app first",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun apkInfo(apk_data: UpdateBean?) {
        if (apk_data!!.apkVersion.compareTo(VersionInfoUtil.getVerName(this@MainActivity)) > 0) {
            val message = handler.obtainMessage()
            message.obj = apk_data
            message.what = 0
            handler.sendMessage(message)
        } else {
            handler.sendEmptyMessage(1)
        }
    }

    override fun onFail() {
        handler.sendEmptyMessage(1)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)

        if (updateDemoReceiver != null) {
            unregisterReceiver(updateDemoReceiver)
        }
    }
}