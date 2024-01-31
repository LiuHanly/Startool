package com.glorystar.startool.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView

class BarcodeModulesActivity : BaseActivity(), View.OnClickListener {
    lateinit var titleView: TitleKtView
    lateinit var relativeHoneywell: RelativeLayout
    lateinit var relativeNewland: RelativeLayout
    lateinit var relativeZebra: RelativeLayout

    override fun getLayoutId(): Int = R.layout.activity_barcode_modules

    override fun initView(savedInstanceState: Bundle?) {
        titleView = findViewById(R.id.tile_view)
        titleView.title.text = getString(R.string.barcode_modules)
        titleView.action = this

        relativeHoneywell = findViewById(R.id.relative_barcode_honeywell)
        relativeNewland = findViewById(R.id.relative_barcode_newland)
        relativeZebra = findViewById(R.id.relative_barcode_zebra)

        relativeHoneywell.setOnClickListener(this)
        relativeNewland.setOnClickListener(this)
        relativeZebra.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.relative_barcode_honeywell -> {
                var intent = Intent(this@BarcodeModulesActivity, BarcodeActivity::class.java)
                intent.putExtra("title", getString(R.string.honeywell_n4680))
                startActivity(intent)
            }
            R.id.relative_barcode_newland ->{
                var intent = Intent(this@BarcodeModulesActivity, BarcodeActivity::class.java)
                intent.putExtra("title", getString(R.string.newland_em3396))
                startActivity(intent)
            }
            R.id.relative_barcode_zebra ->{
                var intent = Intent(this@BarcodeModulesActivity, BarcodeActivity::class.java)
                intent.putExtra("title", getString(R.string.zebra_se2707))
                startActivity(intent)
            }
        }
    }
}