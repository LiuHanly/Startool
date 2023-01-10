package com.glorystar.startool.ui.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.constant.Constant.*
import com.glorystar.startool.config.constant.Constantdtas
import com.glorystar.startool.ui.view.TitleKtView
import com.glorystar.startool.utils.tool.BarcodeUtil
import com.glorystar.startool.utils.tool.PreferenceUtils

class BarcodeActivity : BaseActivity() , View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    lateinit var titleView: TitleKtView
    lateinit var barcodeType: TextView
    lateinit var barcodeSupport: TextView
    lateinit var scanInfo: EditText
    lateinit var lightMode: TextView
    lateinit var frameMode: FrameLayout
    lateinit var linearKeyboard: LinearLayout
    lateinit var linearSerial: LinearLayout
    lateinit var ivKeyboard: ImageView
    lateinit var ivSerial: ImageView
    lateinit var swEnter: Switch
    lateinit var swBeep: Switch
    private var imm: InputMethodManager? = null
    private var barcode: BarcodeUtil? = null
    var LIGHTMODE = ArrayList<String>()
    private var ret = -1

    companion object {
        var index = 0
    }

    override fun getLayoutId(): Int = R.layout.activity_barcode

    override fun initView(savedInstanceState: Bundle?) {
        titleView = findViewById(R.id.tile_view)
        imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val title = intent.getStringExtra("title")
        if (title.equals(getString(R.string.honeywell_n4680))){
            index = 0
        }else{
            index = 1
        }
        titleView.title.text = title
        titleView.action = this
        barcodeType = findViewById(R.id.barcode_type)
        barcodeSupport = findViewById(R.id.barcode_support)
        frameMode = findViewById(R.id.frame_mode)
        lightMode = findViewById(R.id.tv_light_mode)
        scanInfo = findViewById(R.id.edt_scan_info)
        swBeep = findViewById(R.id.sw_sound)
        swEnter = findViewById(R.id.sw_auto_return)
        linearKeyboard = findViewById(R.id.linear_keyboard)
        linearSerial = findViewById(R.id.linear_serial)
        ivKeyboard = findViewById(R.id.iv_keyboard)
        ivSerial = findViewById(R.id.iv_serial)

        barcode = BarcodeUtil(application)

        if (index == 0) {
            barcode?.setCOM("/dev/ttyACM2")
            barcodeSupport.text = getString(R.string.honey_barcode_1d_content)
        } else if (index == 1) {
            barcode?.setCOM("/dev/ttyACM0")
            barcodeSupport.text = getString(R.string.newland_barcode_1d_content)
        }

        ret = barcode!!.init()

        barcodeType.setOnClickListener(this)
        frameMode.setOnClickListener(this)
        swBeep.setOnCheckedChangeListener(this)
        swEnter.setOnCheckedChangeListener(this)
        linearKeyboard.setOnClickListener(this)
        linearSerial.setOnClickListener(this)

        LIGHTMODE.add(getString(R.string.auto))
        LIGHTMODE.add(getString(R.string.on))
        LIGHTMODE.add(getString(R.string.off))

        barcode!!.start()
        barcode!!.getData(object : BarcodeUtil.OnGetDataListener {
            override fun onDataReceive(var1: String?) {
                runOnUiThread { scanInfo.setText(var1) }
            }
        })

        restoreData()
        setupUI(window.decorView)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.linear_keyboard ->{
                if (ret == 1) {
                    scanInfo.setText("")
                    scanInfo.isEnabled = true
                    scanInfo.requestFocus()
                    imm!!.hideSoftInputFromWindow(
                        currentFocus!!.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                    linearKeyboard.setBackgroundResource(R.drawable.r_button_bg)
                    linearSerial.setBackgroundResource(R.color.transparent)
                    ivKeyboard.visibility = View.VISIBLE
                    ivSerial.visibility = View.INVISIBLE
                    barcode!!.setScanningMode(true)
                    if (index == 0) {
                        PreferenceUtils.putBoolean(HONEYWELL_SCAN_MODE, true)
                    } else if (index == 1) {
                        PreferenceUtils.putBoolean(NEWLAND_SCAN_MODE, true)
                    }
                }
            }
            R.id.linear_serial ->{
                if (ret == 1) {
                    scanInfo.setText("")
                    scanInfo.clearFocus()
                    scanInfo.isEnabled = false
                    imm!!.hideSoftInputFromWindow(
                        currentFocus!!.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                    linearKeyboard.setBackgroundResource(R.color.transparent)
                    linearSerial.setBackgroundResource(R.drawable.r_button_bg)
                    ivKeyboard.visibility = View.INVISIBLE
                    ivSerial.visibility = View.VISIBLE
                    barcode!!.setScanningMode(false)
                    if (index == 0) {
                        PreferenceUtils.putBoolean(HONEYWELL_SCAN_MODE, false)
                    } else if (index == 1) {
                        PreferenceUtils.putBoolean(NEWLAND_SCAN_MODE, false)
                    }
                }
            }
            R.id.frame_mode -> {
                var mListPop = ListPopupWindow(this)
                mListPop.setAdapter(
                    ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        LIGHTMODE
                    )
                )
                mListPop.setWidth(frameMode.width)
                mListPop.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT)
                mListPop.setAnchorView(frameMode)
                mListPop.setBackgroundDrawable(getDrawable(R.drawable.drop_down_bg))
                mListPop.verticalOffset = 10
                mListPop.setModal(true)

                mListPop.setOnItemClickListener(object : AdapterView.OnItemClickListener {
                    override fun onItemClick(
                        parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                        if (ret == 1) {
                            barcode!!.setLightMode(position)
                            if (index == 0) {
                                PreferenceUtils.putInt(HONEYWELL_LIGHT_MODE, position)
                            } else if (index == 1) {
                                PreferenceUtils.putInt(NEWLAND_LIGHT_MODE, position)
                            }
                        }
                        lightMode.text = LIGHTMODE.get(position)
                        mListPop.dismiss()
                    }
                })

                mListPop.show()
            }
            R.id.barcode_type -> {
                var mListPop = ListPopupWindow(this)
                mListPop.setAdapter(
                    ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        Constantdtas.BARCODE
                    )
                )
                mListPop.setWidth(barcodeType.width)
                mListPop.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT)
                mListPop.setAnchorView(barcodeType)
                mListPop.setBackgroundDrawable(getDrawable(R.drawable.drop_down_bg))
                mListPop.verticalOffset = 10
                mListPop.setModal(true)

                mListPop.setOnItemClickListener(object : AdapterView.OnItemClickListener {
                    override fun onItemClick(
                        parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                        barcodeType.text = Constantdtas.BARCODE[position]
                        if (index == 0) {
                            if (position == 0){
                                barcodeSupport.text = getString(R.string.honey_barcode_1d_content)
                            }else if (position == 1){
                                barcodeSupport.text = getString(R.string.honey_barcode_2d_content)
                            }
                        }else if (index == 1){
                            if (position == 0){
                                barcodeSupport.text = getString(R.string.newland_barcode_1d_content)
                            }else if (position == 1){
                                barcodeSupport.text = getString(R.string.newland_barcode_2d_content)
                            }
                        }
                        mListPop.dismiss()
                    }
                })

                mListPop.show()
            }
        }
    }

    private fun restoreData() {
        if (ret != 1) {
            return
        }
        var scan_mode = false
        var beep = false
        var scan_enter = false
        var mode: Int = 0
        if (index == 0) {
            scan_mode = PreferenceUtils.getBoolean(HONEYWELL_SCAN_MODE, false)
            beep = PreferenceUtils.getBoolean(HONEYWELL_PLAY_BEEP_SOUND, true)
            scan_enter = PreferenceUtils.getBoolean(HONEYWELL_SCAN_ENTER, false)
            mode = PreferenceUtils.getInt(HONEYWELL_LIGHT_MODE, 0)
        } else if (index == 1) {
            scan_mode = PreferenceUtils.getBoolean(NEWLAND_SCAN_MODE, false)
            beep = PreferenceUtils.getBoolean(NEWLAND_PLAY_BEEP_SOUND, true)
            scan_enter = PreferenceUtils.getBoolean(NEWLAND_SCAN_ENTER, false)
            mode = PreferenceUtils.getInt(NEWLAND_LIGHT_MODE, 0)
        }
        if (scan_mode) {
            linearKeyboard.setBackgroundResource(R.drawable.r_button_bg)
            linearSerial.setBackgroundResource(R.color.transparent)
            ivKeyboard.visibility = View.VISIBLE
            ivSerial.visibility = View.INVISIBLE
            barcode!!.setScanningMode(true)
            scanInfo.setEnabled(true)
            scanInfo.requestFocus()
        } else {
            linearKeyboard.setBackgroundResource(R.color.transparent)
            linearSerial.setBackgroundResource(R.drawable.r_button_bg)
            ivKeyboard.visibility = View.INVISIBLE
            ivSerial.visibility = View.VISIBLE
            barcode!!.setScanningMode(false)
            scanInfo.clearFocus()
            scanInfo.setEnabled(false)
        }
        lightMode.text = LIGHTMODE.get(mode)
        swBeep.setChecked(beep)
        swEnter.setChecked(scan_enter)
        barcode!!.setLightMode(mode)
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        when(p0?.id){
            R.id.sw_auto_return ->{
                if (ret == 1) {
                    if (p1) {
                        barcode!!.setAutoEnter(true)
                    } else {
                        barcode!!.setAutoEnter(false)
                    }
                    if (index == 0) {
                        PreferenceUtils.putBoolean(HONEYWELL_SCAN_ENTER, p1)
                    } else if (index == 1) {
                        PreferenceUtils.putBoolean(NEWLAND_SCAN_ENTER, p1)
                    }
                }
            }
            R.id.sw_sound -> {
                if (ret == 1) {
                    if (p1) {
                        barcode!!.setBeep(true)
                    } else {
                        barcode!!.setBeep(false)
                    }
                    if (index == 0) {
                        PreferenceUtils.putBoolean(HONEYWELL_PLAY_BEEP_SOUND, p1)
                    } else if (index == 1) {
                        PreferenceUtils.putBoolean(NEWLAND_PLAY_BEEP_SOUND, p1)
                    }
                }
            }
        }
    }

    fun setupUI(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                imm!!.hideSoftInputFromWindow(
                    currentFocus!!.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
                false
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        barcode?.stop()
    }
}