package com.glorystar.startool.ui.activity

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.glorystar.startool.BuildConfig
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.base.BasePoup
import com.glorystar.startool.config.base.MyApplication
import com.glorystar.startool.config.bean.OnlineData
import com.glorystar.startool.config.bean.UpdateBean
import com.glorystar.startool.config.bean.UpdateData
import com.glorystar.startool.config.constant.Constantdtas
import com.glorystar.startool.ui.service.DownloadService
import com.glorystar.startool.ui.testactivity.TestMainActivity
import com.glorystar.startool.utils.tool.IntentJumpUtils
import com.glorystar.startool.utils.tool.LiveDataBus
import com.glorystar.startool.utils.tool.NetworkUtils
import com.glorystar.startool.utils.tool.Rootcommands
import com.glorystar.startool.utils.tool.ToastUtil
import com.glorystar.startool.utils.tool.VersionInfoUtil
import com.lxj.xpopup.enums.PopupPosition
import com.permissionx.guolindev.PermissionX
import java.io.File
import java.lang.reflect.Method


class MainActivity : BaseActivity(), UpdateData {
    private lateinit var linear_general: LinearLayout
    private lateinit var linear_model: LinearLayout
    private lateinit var linear_addon: LinearLayout
    private lateinit var linear_learning: LinearLayout
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

    lateinit var logo_startoo: ImageView

    private var clickCount = 1
    private var lastClickTime = 0L
    private val requiredClicks = 4
    private val maxInterval = 800L

    var mstoragemanager: StorageManager? = null

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
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun initView(savedInstanceState: Bundle?) {
        linear_general = findViewById(R.id.linear_general)
        linear_model = findViewById(R.id.linear_model)
        linear_addon = findViewById(R.id.linear_addon)
        linear_learning = findViewById(R.id.linear_learning)
        linear_aging = findViewById(R.id.linear_aging)
        language_text = findViewById(R.id.textlanguage)
        update = findViewById(R.id.iv_update)
        update1 = findViewById(R.id.iv_update1)
        logo_startoo = findViewById(R.id.logo_startoo)
        mstoragemanager = getSystemService(STORAGE_SERVICE) as StorageManager
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

        linear_learning.setOnClickListener {
            startActivity(Intent(this, LivePreviewActivity::class.java))
        }

        update.setOnClickListener {
            updateDialog(this@MainActivity)
        }
        update1.setOnClickListener {
            ButtonClickZoomInAnimation(it, 0.8f)
            val now = System.currentTimeMillis()
            if (now - lastClickTime > 1000) {
                // 超过时间，重新计数
                clickCount = 1
            } else {
                clickCount++
            }
            val remaining = requiredClicks - clickCount
            if (remaining > 0) {

            }
            lastClickTime = now
            if (clickCount == 4) {
                clickCount = 0
                TestMainActivity.toStartActivity(this@MainActivity)
            }
        }
        val Serial = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Build.getSerial()
        } else {
            Build.SERIAL
        }

        val path = MyApplication.context?.getExternalFilesDir("log_${Serial}").toString()
        logo_startoo.setOnClickListener {
            ButtonClickZoomInAnimation(it, 0.8f)
            val now = System.currentTimeMillis()
            if (now - lastClickTime > 1000) {
                // 超过时间，重新计数
                clickCount = 1
            } else {
                clickCount++
            }
            val remaining = requiredClicks - clickCount
            if (remaining > 0) {

            }
            lastClickTime = now
            if (clickCount == 4) {
                clickCount = 0
                try {
                    val directory = File(path)
                    val hasFiles = if (directory.exists() && directory.isDirectory) {
                        // listFiles() 会返回目录下所有文件和文件夹的数组，如果数组不为空且长度大于 0，说明有文件
                        val files = directory.listFiles()
                        files != null && files.isNotEmpty()
                    } else {
                        false // 文件夹不存在或者不是一个目录
                    }
                    val volumeList: List<StorageVolume> =
                        mstoragemanager?.getStorageVolumes() as List<StorageVolume>
                    if (volumeList.isNotEmpty()) {
                        var mPath = ""
                        volumeList.forEach {
                            val label: String = it.getDescription(this) //这个其实就是U盘的名称
                            val status: String = it.getState()
                            val isEmulated = it.isEmulated()            //是否是内部存储设备
                            val isRemovable = it.isRemovable()          //是否是可移除的外部存储设备
                            val myclass = Class.forName("android.os.storage.StorageVolume")
                            val getPath: Method = myclass.getMethod("getPath")
                            getPath.isAccessible = true
                            if (!isEmulated && isRemovable) {
                                mPath = getPath.invoke(it).toString()
                                Log.e("USBInterfaceActivity", "--->${mPath} -->${label}")
                            }

                        }
                        if (mPath.isNotEmpty()) {
                            Log.e("USBInterfaceActivity", "--->${mPath}")
                            if (hasFiles){
                                val command = "cp -rf \"$path\" \"$mPath/\""
                               val runRoot =  Rootcommands.runRootCommand(command)
                                if (runRoot){
                                    ToastUtil.showMsg("Copy complete")
                                }
                                Log.d("FileCheck", "--->$runRoot")
                            }else {
                                ToastUtil.showMsg("USB device not found")
                                // 路径下没有文件，或者文件夹根本不存在
                                Log.d("FileCheck", "No files found in this path, or the folder does not exist")
                            }


                        } else {
                            ToastUtil.showMsg("USB device not found")
                        }

                    }
                } catch (e: Exception) {
                    Log.e("USBInterfaceActivity", "--->${e.message}")
                }

                ///  TestMainActivity.toStartActivity(this@MainActivity)
            }
        }

        language_text.setOnClickListener {
            BasePoup.Builder(this).hasShadowBg(false).atView(it).popupPosition(PopupPosition.Bottom)
                .asAttaServedView(Constantdtas.AttaServedName) { position, text ->
                    language_text.text = text
                    IntentJumpUtils.setLanguage(position)
                    LiveDataBus.get().with("Language").postValue("refresh_language")
                }.show()
        }
        setPermission()


    }


    fun ButtonClickZoomInAnimation(view_lay: View?, numerical: Float) {
        if (view_lay != null) {
            val set: AnimatorSet = AnimatorSet()
            set.playTogether(
                ObjectAnimator.ofFloat(view_lay, "scaleX", numerical, 1f),
                ObjectAnimator.ofFloat(view_lay, "scaleY", numerical, 1f)
            )
            set.setDuration(1 * 200).start()
        }
    }

    override fun onStart() {
        super.onStart()
        super.init()
    }

    private fun updateDialog(context: Context?) {
        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_update, null)
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
                        this@MainActivity, "The network is not connected", Toast.LENGTH_SHORT
                    ).show()
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
        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_ota, null)
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
                                contentUri, "application/vnd.android.package-archive"
                            )
                        } else {
                            val uri = Uri.parse("file://" + File(filePath))
                            installIntent.setDataAndType(
                                uri, "application/vnd.android.package-archive"
                            )
                        }
                        startActivity(installIntent)
                    } else {
                        Toast.makeText(
                            this@MainActivity, "Please download the app first", Toast.LENGTH_SHORT
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


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    //请求需要的权限  .WRITE_SETTINGS. permission.WRITE_SETTINGS  ACTION_MANAGE_WRITE_SETTINGS
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun setPermission() {
        PermissionX.init(this).permissions(
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.WRITE_SYNC_SETTINGS,
                Manifest.permission.WRITE_APN_SETTINGS,
                Manifest.permission.WRITE_SECURE_SETTINGS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CAMERA,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO,
            ).request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    Toast.makeText(this, "All permissions are granted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(
                        this, "These permissions are denied: $deniedList", Toast.LENGTH_LONG
                    ).show()
                }
            }


    }

}