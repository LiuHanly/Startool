package com.glorystar.startool.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.*
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.constant.Constant.HFTAG_MIFARE
import com.glorystar.startool.config.constant.Constantdtas
import com.glorystar.startool.ui.view.TitleKtView
import com.glorystar.startool.utils.tool.InputFilterUtil
import com.glorystar.startool.utils.tool.RfidModuleUtil

class RfidModulesActivity : BaseActivity(), View.OnClickListener {
    lateinit var titleView: TitleKtView
    lateinit var linearReadCard: LinearLayout
    lateinit var linearWriteCard: LinearLayout
    lateinit var linearSuccess: LinearLayout
    lateinit var scanCard: Button
    lateinit var clearCard: TextView
    lateinit var readCard: Button
    lateinit var writeCard: Button
    lateinit var cardInfo: TextView
    lateinit var scanInfo: TextView
    lateinit var rfidSupport: TextView
    lateinit var rfidSupportDetail: TextView
    lateinit var inputValue: EditText
    lateinit var parentScrollview: ScrollView
    lateinit var childScrollview: ScrollView
    private var rfid: RfidModuleUtil? = null
    private var ret = -1

    override fun getLayoutId(): Int = R.layout.activity_rfid_modules

    override fun initView(savedInstanceState: Bundle?) {
        titleView = findViewById(R.id.tile_view)
        titleView.title.text = getString(R.string.rfid_module)
        titleView.action = this

        linearReadCard = findViewById(R.id.linear_read_card)
        linearWriteCard = findViewById(R.id.linear_write_card)
        linearSuccess = findViewById(R.id.linear_card_success)
        scanCard = findViewById(R.id.bt_scan_card)
        clearCard = findViewById(R.id.tv_clear_card)
        readCard = findViewById(R.id.bt_read_card)
        scanInfo = findViewById(R.id.tv_retrieved_value)
        writeCard = findViewById(R.id.bt_write_card)
        inputValue = findViewById(R.id.edt_input_value)
        cardInfo = findViewById(R.id.tv_card_type)
        rfidSupport = findViewById(R.id.rfid_support)
        rfidSupportDetail = findViewById(R.id.rfid_support_detail)
        parentScrollview = findViewById(R.id.scrollview_parent)
        childScrollview = findViewById(R.id.scrollview_child)

        InputFilterUtil.setEditTextInhibitInput(inputValue)

        rfid = RfidModuleUtil(application)

        ret = rfid!!.init()

        scanCard.setOnClickListener(this)
        clearCard.setOnClickListener(this)
        readCard.setOnClickListener(this)
        writeCard.setOnClickListener(this)
        rfidSupport.setOnClickListener(this)

        rfid!!.start()

        rfid!!.getData(object : RfidModuleUtil.OnGetDataListener {
            override fun onDataReceive(cardType: String, var1: String) {
                runOnUiThread {
                    println("$cardType--->$var1")
                    when (cardType) {
                        HFTAG_MIFARE -> {
                            linearReadCard.visibility = View.VISIBLE
                            linearWriteCard.visibility = View.VISIBLE
                        }
                        else -> {
                            if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                                linearReadCard.visibility = View.GONE
                                linearWriteCard.visibility = View.GONE
                            }else{
                                linearReadCard.visibility = View.INVISIBLE
                                linearWriteCard.visibility = View.INVISIBLE
                            }
                            scanCard.isEnabled = true
                        }
                    }
                    if (var1 != "unknown") {
                        if (var1.contains("Data:")) {
                            scanInfo.text = var1
                        } else if (!var1.equals("No tag")){
                            cardInfo.text = var1
                            scanCard.visibility = View.GONE
                            linearSuccess.visibility = View.VISIBLE
                        }
                        if (var1 == "Unsupported card type") {
                            if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                                linearReadCard.visibility = View.GONE
                                linearWriteCard.visibility = View.GONE
                            }else{
                                linearReadCard.visibility = View.INVISIBLE
                                linearWriteCard.visibility = View.INVISIBLE
                            }
                            scanCard.isEnabled = true
                            readCard.isEnabled = true
                            writeCard.isEnabled = true
                        }
                    } else {
                        scanCard.isEnabled = true
                        readCard.isEnabled = true
                        writeCard.isEnabled = true
                    }
                    if (var1 == "No tag"){
                        showDialog(this@RfidModulesActivity, getString(R.string.scan_rfid_card), getString(R.string.no_card))
                    }
                    if (var1 == "Success"){
                        showDialog(this@RfidModulesActivity, getString(R.string.write_to_card), getString(R.string.card_update))
                    }
                }
            }
        })

        parentScrollview.setOnTouchListener(OnTouchListener { v, event ->
            childScrollview.parent.requestDisallowInterceptTouchEvent(false)
            false
        })

        childScrollview.setOnTouchListener(OnTouchListener {v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        })
    }

    private fun showDialog(context: Context?, title: String, info: String) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_rfid_module, null)
        var tvTitle = view.findViewById<TextView>(R.id.tv_info_title)
        var tvInfo = view.findViewById<TextView>(R.id.tv_info)
        var goBack = view.findViewById<TextView>(R.id.tv_go_back)

        tvInfo.text = info
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

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.bt_scan_card -> {
                if (ret == 1) {
                    rfid!!.searchTag()
                    scanInfo.setText("")
                    if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        linearReadCard.visibility = View.GONE
                        linearWriteCard.visibility = View.GONE
                    }else{
                        linearReadCard.visibility = View.INVISIBLE
                        linearWriteCard.visibility = View.INVISIBLE
                    }
                    scanCard.isEnabled = false
                }
            }
            R.id.tv_clear_card -> {
                cardInfo.text = ""
                linearSuccess.visibility = View.GONE
                scanCard.visibility = View.VISIBLE
                if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    linearReadCard.visibility = View.GONE
                    linearWriteCard.visibility = View.GONE
                }else{
                    linearReadCard.visibility = View.INVISIBLE
                    linearWriteCard.visibility = View.INVISIBLE
                }
            }
            R.id.bt_read_card -> {
                if (ret == 1) {
                    rfid!!.readTag()
                    scanCard.setEnabled(false)
                    readCard.isEnabled = false
                    writeCard.isEnabled = false
                }
            }
            R.id.bt_write_card -> {
                if (ret == 1) {
                    val data: String = inputValue.getText().toString()
                    if (!TextUtils.isEmpty(data)) {
                        if (data.length == 32) {
                            rfid!!.writeTag(data)
                            scanCard.setEnabled(false)
                            readCard.isEnabled = false
                            writeCard.isEnabled = false
                        } else {
                            Toast.makeText(this, "Please enter a 16 byte array", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Please enter hexadecimal data first",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            R.id.rfid_support -> {
                var mListPop = ListPopupWindow(this)
                mListPop.setAdapter(
                    ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        Constantdtas.RFIDSUPPORT
                    )
                )
                mListPop.setWidth(rfidSupport.width)
                mListPop.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT)
                mListPop.setAnchorView(rfidSupport)
                mListPop.setBackgroundDrawable(getDrawable(R.drawable.drop_down_bg))
                mListPop.verticalOffset = 10
                mListPop.setModal(true)

                mListPop.setOnItemClickListener(object : AdapterView.OnItemClickListener {
                    override fun onItemClick(
                        parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                        rfidSupport.text = Constantdtas.RFIDSUPPORT[position]
                        if (position == 0){
                            rfidSupportDetail.text = getString(R.string.standard_content)
                        }else if (position == 1){
                            rfidSupportDetail.text = getString(R.string.version_p_content)
                        }

                        mListPop.dismiss()
                    }
                })

                mListPop.show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        rfid!!.stop()
    }
}