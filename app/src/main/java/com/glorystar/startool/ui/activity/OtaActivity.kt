package com.glorystar.startool.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.glorystar.otasdk.OtaSdk
import com.glorystar.otasdk.interfaces.DownloadProgress
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView
import com.glorystar.startool.utils.tool.NetworkUtil
import com.glorystar.startool.utils.tool.StatusNaviBarUitl
import java.io.File

class OtaActivity : BaseActivity() {
    lateinit var titleView: TitleKtView
    lateinit var edtInput: EditText
    lateinit var downloadPackage: Button
    lateinit var verifyPackage: Button
    lateinit var clearInput: TextView
    lateinit var otaSdk: OtaSdk
    lateinit var progressBar: ProgressBar
    lateinit var dialog: AlertDialog
    lateinit var linearSuccess: LinearLayout

    override fun getLayoutId(): Int {
        return R.layout.activity_ota
    }

    override fun initView(savedInstanceState: Bundle?) {
        titleView = findViewById(R.id.tile_view)
        titleView.title.text = getString(R.string.ota_update)
        titleView.action = this

        otaSdk = OtaSdk(this)

        edtInput = findViewById(R.id.edt_input)
        downloadPackage = findViewById(R.id.bt_download)
        clearInput = findViewById(R.id.tv_clear)
        linearSuccess = findViewById(R.id.linear_success)
        verifyPackage = findViewById(R.id.bt_verify)

        clearInput.setOnClickListener {
            edtInput.setText("")
        }

        downloadPackage.setOnClickListener {
            if (downloadPackage.text == getString(R.string.ota_download)) {
                if (NetworkUtil.isConnected(this)) {
                    var url = edtInput.text.toString()
                    if (!TextUtils.isEmpty(url)) {
                        edtInput.clearFocus()
                        otaSdk.setDownloadUrl(url)
                        otaSdk.downloadFirmware()
                        showDialog(
                            this@OtaActivity,
                            R.drawable.ic_downloading,
                            getString(R.string.downloading_package),
                            "",
                            1
                        )
                    }
                } else {
                    showDialog(
                        this@OtaActivity,
                        R.drawable.ic_error,
                        getString(R.string.download_fail),
                        getString(R.string.download_fail_tip),
                        0
                    )
                }
            }else if (downloadPackage.text == getString(R.string.install_package)){
                otaSdk.upgrade()
            }
        }

        verifyPackage.setOnClickListener {
            val file = File(Environment.getExternalStorageDirectory().absolutePath, "glorystar.zip")
            if (file.exists()) {
                showDialog(this@OtaActivity, R.drawable.ic_checked,getString(R.string.package_verified),getString(R.string.verify_success),0)
            } else {
                showDialog(this@OtaActivity, R.drawable.ic_error,getString(R.string.verify_fail), getString(R.string.verify_fail_tip),0)
            }
        }

        otaSdk.setDownloadListener(object : DownloadProgress {
            override fun onPrepare() {

            }

            override fun onProgress(i: Int) {
                Log.i("ota percent---->", "$i%")
                runOnUiThread {
                    progressBar.progress = i
                }
            }

            override fun onDownloadSuccess(p0: String?) {
                runOnUiThread {
                    dialog.dismiss()
                    linearSuccess.visibility = View.VISIBLE
                    downloadPackage.setText(getString(R.string.install_package))
                    clearInput.visibility = View.GONE
                    verifyPackage.visibility = View.VISIBLE
                }
            }

            override fun onPreUpgrade() {}
            override fun onUpgrade() {}
            override fun onError(i: Int, s: String) {
                runOnUiThread {
                    if (i == 1000) {
                        Toast.makeText(
                            this@OtaActivity,
                            "code:$i, message:$s",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }


    private fun showDialog(context: Context?, iconId: Int, title: String, info: String, type:Int) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_ota, null)
        var ivIcon = view.findViewById<ImageView>(R.id.iv_info)
        var tvTitle = view.findViewById<TextView>(R.id.tv_info_title)
        var tvInfo = view.findViewById<TextView>(R.id.tv_info)
        var goBack = view.findViewById<TextView>(R.id.tv_go_back)
        progressBar = view.findViewById<ProgressBar>(R.id.pb_update)

        if (type == 0){
            progressBar.visibility = View.GONE
            tvInfo.visibility = View.VISIBLE
            goBack.text = getString(R.string.go_back)
        }else{
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

        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }
}