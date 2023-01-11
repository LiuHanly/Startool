package com.glorystar.startool.ui.activity

import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView
import com.glorystar.startool.utils.tool.SystemOperate
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.math.BigDecimal
import android.widget.AdapterView

import android.widget.ArrayAdapter
import com.glorystar.startool.config.constant.Constantdtas.ORIENTATION


class GeneralActivity : BaseActivity(), OnClickListener {
    private val PIC_PATH = Environment.getExternalStorageDirectory().path + "/screenshot.png"
    lateinit var titleView: TitleKtView
    lateinit var relativeDeviceInfo: RelativeLayout
    lateinit var relativeOta: RelativeLayout
    lateinit var btReboot:Button
    lateinit var btScreenshot:Button
    lateinit var btOrientation:Button
    lateinit var btVolume:Button
    lateinit var btBrightness:Button
    lateinit var btBrightnessSet: Button
    lateinit var btVolumeSet: Button
    lateinit var btLandscape: Button
    lateinit var btPortrait: Button
    var currentVolume:Int = 0
    var media_max:Int = 0
    private val MAX_BRIGHTNESS = 255
    private var mAudioManager: AudioManager? = null
    private var imm: InputMethodManager? = null

    /**
     * 设置布局
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_genera
    }

    /**
     * 初始化视图
     */
    override fun initView(savedInstanceState: Bundle?) {
        titleView = findViewById(R.id.tile_view)
        titleView.title.text = getString(R.string.General)
        titleView.action = this

        imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?

        relativeDeviceInfo = findViewById(R.id.relative_device_info)
        relativeOta = findViewById(R.id.relative_ota)
        btBrightness = findViewById(R.id.bt_brightness_get)
        btOrientation = findViewById(R.id.bt_orientation_get)
        btVolume = findViewById(R.id.bt_volume_get)
        btScreenshot = findViewById(R.id.bt_screenshot)
        btReboot = findViewById(R.id.bt_reboot)
        btBrightnessSet = findViewById(R.id.bt_1)
        btVolumeSet = findViewById(R.id.bt_2)
        btLandscape = findViewById(R.id.bt_landscape)
        btPortrait = findViewById(R.id.bt_portrait)

        relativeDeviceInfo.setOnClickListener(this)
        relativeOta.setOnClickListener(this)
        btBrightness.setOnClickListener(this)
        btVolume.setOnClickListener(this)
        btOrientation.setOnClickListener(this)
        btReboot.setOnClickListener(this)
        btScreenshot.setOnClickListener(this)

        btBrightnessSet.setOnClickListener(this)
        btVolumeSet.setOnClickListener(this)
        btLandscape.setOnClickListener(this)
        btPortrait.setOnClickListener(this)
    }
    override fun onStart() {
        super.onStart()
        mAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager?

        media_max = mAudioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)!!

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
            btPortrait.setBackgroundResource(R.drawable.gray_button_bg)
        }else {
            btLandscape.setBackgroundResource(R.drawable.gray_button_bg)
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.relative_device_info ->{
                startActivity(Intent(this@GeneralActivity, DeviceInfoActivity::class.java))
            }
            R.id.relative_ota ->{
                startActivity(Intent(this@GeneralActivity, OtaActivity::class.java))
            }
            R.id.bt_brightness_get ->{
                showDialog(this, R.drawable.ic_brightness, getString(R.string.screen_brightness), getBrightness().toString())
            }
            R.id.bt_volume_get ->{
                showDialog(this, R.drawable.ic_volume, getString(R.string.volume), getVolume().toString())
            }
            R.id.bt_orientation_get ->{
                showDialog(this, R.drawable.ic_orientation, getString(R.string.orientation), "")
            }
            R.id.bt_screenshot ->{
                val picFile: File = File(PIC_PATH)
                if (picFile.exists()) {
                    picFile.delete()
                }
                SystemOperate().screenShot(PIC_PATH)
                if (picFile.exists()) {
                    picDialog(this)
                }
            }
            R.id.bt_reboot ->{
                rebootDialog(this)
            }
            R.id.bt_1 ->{
                setDialog(this, R.drawable.ic_brightness, getString(R.string.screen_brightness))
            }
            R.id.bt_2 ->{
                setDialog(this, R.drawable.ic_volume, getString(R.string.volume))
            }
            R.id.bt_landscape ->{
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

            R.id.bt_portrait -> {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }

    private fun showDialog(context: Context?, iconId: Int, title: String, info: String) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_get_info, null)
        var ivIcon = view.findViewById<ImageView>(R.id.iv_info)
        val ivOrientation = view.findViewById<ImageView>(R.id.iv_orientation)
        var tvTitle = view.findViewById<TextView>(R.id.tv_info_title)
        var tvInfo = view.findViewById<TextView>(R.id.tv_info)
        var goBack = view.findViewById<TextView>(R.id.tv_go_back)

        if (title.equals(getString(R.string.orientation))){
            ivOrientation.visibility = View.VISIBLE
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                ivOrientation.setImageResource(R.drawable.ic_portrait)
                tvInfo.text = context?.getString(R.string.portrait)
            } else {
                ivOrientation.setImageResource(R.drawable.ic_landscape)
                tvInfo.text = context?.getString(R.string.landscape)
            }
        }else{
            ivOrientation.visibility = View.GONE
            tvInfo.text = info
        }

        ivIcon.setImageResource(iconId)
        tvTitle.text = title

        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setCancelable(false)
        val dialog = builder.create()
        goBack.setOnClickListener { dialog.dismiss() }

        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun setDialog(context: Context?, iconId: Int, title: String) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_set_info, null)
        var ivIcon = view.findViewById<ImageView>(R.id.iv_info)
        var tvTitle = view.findViewById<TextView>(R.id.tv_info_title)
        val goBack = view.findViewById<TextView>(R.id.tv_go_back)
        val confirm = view.findViewById<Button>(R.id.bt_confirm)
        var edtInput = view.findViewById<EditText>(R.id.edt_input)

        ivIcon.setImageResource(iconId)
        tvTitle.text = title

        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setCancelable(false)
        val dialog = builder.create()
        goBack.setOnClickListener { dialog.dismiss() }

        confirm.setOnClickListener {
            var value = edtInput.text.toString()
            if (title.equals(getString(R.string.volume))){
                if (getCurrentFocus() != null) {
                    imm!!.hideSoftInputFromWindow(
                        getCurrentFocus()?.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                }
                if (!TextUtils.isEmpty(value)) {
                    setVolume(value)
                    edtInput.clearFocus()
                }
            }else if (title.equals(getString(R.string.screen_brightness))){
                if (getCurrentFocus() != null) {
                    imm!!.hideSoftInputFromWindow(
                        getCurrentFocus()?.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                }
                if (!TextUtils.isEmpty(value)) {
                    setBrightness(
                        Math.round(
                            java.lang.Float.valueOf(value) / 100 * MAX_BRIGHTNESS
                        ).toInt()
                    )
                    edtInput.clearFocus()
                }
            }
            dialog.dismiss()
        }

        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun picDialog(context: Context) {
        val view = layoutInflater.inflate(R.layout.dialog_pic, null)
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        builder.setView(view)
        builder.setCancelable(false)
        var iv = view.findViewById<ImageView>(R.id.iv_screen)
        val goBack = view.findViewById<TextView>(R.id.tv_go_back)
        iv.setImageBitmap(getLocalBitmap(PIC_PATH))
        val dialog = builder.create()
        goBack.setOnClickListener { dialog.dismiss() }

        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun rebootDialog(context: Context?) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_reboot, null)
        val goBack = view.findViewById<TextView>(R.id.tv_go_back)
        val confirm = view.findViewById<Button>(R.id.bt_confirm)

        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setCancelable(false)
        val dialog = builder.create()
        goBack.setOnClickListener { dialog.dismiss() }
        confirm.setOnClickListener {
            SystemOperate().rebootSystem()
        }

        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
    }

    //获得当前亮度
    private fun getBrightness(): String? {
        var brightness = 0
        val resolver: ContentResolver = getContentResolver()
        try {
            brightness = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
        }
        return Math.round(brightness.toDouble() / 255 * 100).toString()
    }

    //设置亮度
    private fun setBrightness(brightness: Int) {
        val resolver: ContentResolver = getContentResolver()
        Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
    }

    //获取当前音量
    private fun getVolume(): String? {
        currentVolume = mAudioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)!!
        currentVolume = Math.round(
            BigDecimal(java.lang.Double.valueOf(currentVolume.toDouble()) / media_max).setScale(
                2,
                BigDecimal.ROUND_HALF_UP
            ).toDouble() * 100
        )
            .toInt()
        return currentVolume.toString()
    }

    //设置音量
    private fun setVolume(volume: String) {
        val vol = Math.ceil(java.lang.Double.valueOf(volume) / 100 * media_max).toInt()
        mAudioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, vol, AudioManager.FLAG_PLAY_SOUND)
    }

    fun getLocalBitmap(url: String?): Bitmap? {
        return try {
            val fis = FileInputStream(url)
            BitmapFactory.decodeStream(fis)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}