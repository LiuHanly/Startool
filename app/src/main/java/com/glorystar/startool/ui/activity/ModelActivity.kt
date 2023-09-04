package com.glorystar.startool.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView

class ModelActivity : BaseActivity(), View.OnClickListener {
    lateinit var titleView: TitleKtView
    lateinit var jar101led: RelativeLayout
    lateinit var neb101rk3399: RelativeLayout
    lateinit var neb101rk3568: RelativeLayout
    lateinit var neb156rk3288: RelativeLayout
    lateinit var neb156rk3399: RelativeLayout
    lateinit var neb215rk3288: RelativeLayout
    lateinit var neb215rk3399: RelativeLayout
    lateinit var vod073rk3568: RelativeLayout
    lateinit var vod073rk3399: RelativeLayout
    lateinit var vod073px30: RelativeLayout
    lateinit var emt431rk3399: RelativeLayout
    lateinit var tad321rk3399: RelativeLayout
    lateinit var vod101rk3568: RelativeLayout
    lateinit var vid101a133: RelativeLayout

    /**
     * 设置布局
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_model
    }

    /**
     * 初始化视图
     */
    override fun initView(savedInstanceState: Bundle?) {
        titleView = findViewById(R.id.tile_view)
        titleView.title.text = getString(R.string.model_test)
        titleView.action = this

        jar101led = findViewById(R.id.relative_jar101)
        neb101rk3399 = findViewById(R.id.relative_neb101_rk3399)
        neb101rk3568 = findViewById(R.id.relative_neb101_rk3568)
        neb156rk3288 = findViewById(R.id.relative_neb156_rk3288)
        neb156rk3399 = findViewById(R.id.relative_neb156_rk3399)
        neb215rk3288 = findViewById(R.id.relative_neb215_rk3288)
        neb215rk3399 = findViewById(R.id.relative_neb215_rk3399)
        vod073px30 = findViewById(R.id.relative_vod073_px30)
        vod073rk3399 = findViewById(R.id.relative_vod073_rk3399)
        vod073rk3568 = findViewById(R.id.relative_vod073_rk3568)
        emt431rk3399 = findViewById(R.id.relative_emt431_rk3399)
        tad321rk3399 = findViewById(R.id.relative_tad321_rk3399)
        vod101rk3568 = findViewById(R.id.relative_vod101_rk3568)
        vid101a133 = findViewById(R.id.relative_vid101_a133)

        jar101led.setOnClickListener(this)
        neb101rk3568.setOnClickListener(this)
        neb101rk3399.setOnClickListener(this)
        neb156rk3399.setOnClickListener(this)
        neb156rk3288.setOnClickListener(this)
        neb215rk3399.setOnClickListener(this)
        neb215rk3288.setOnClickListener(this)
        vod073px30.setOnClickListener(this)
        vod073rk3399.setOnClickListener(this)
        vod073rk3568.setOnClickListener(this)
        emt431rk3399.setOnClickListener(this)
        tad321rk3399.setOnClickListener(this)
        vod101rk3568.setOnClickListener(this)
        vid101a133.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.relative_jar101 ->{
                startActivity(Intent(this, Jar101Activity::class.java))
            }
            R.id.relative_neb101_rk3399 ->{
                startActivity(Intent(this, Neb101Activity::class.java))
            }
            R.id.relative_neb101_rk3568 ->{
                startActivity(Intent(this, Neb101Activity::class.java))
            }
            R.id.relative_neb156_rk3288 ->{
                var intent = Intent(this, Neb156Activity::class.java)
                intent.putExtra("title", getString(R.string.model_neb156))
                startActivity(intent)
            }
            R.id.relative_neb156_rk3399 ->{
                var intent = Intent(this, Neb156Activity::class.java)
                intent.putExtra("title", getString(R.string.model_neb156))
                startActivity(intent)
            }
            R.id.relative_neb215_rk3288 ->{
                var intent = Intent(this, Neb156Activity::class.java)
                intent.putExtra("title", getString(R.string.model_neb156))
                startActivity(intent)
            }
            R.id.relative_neb215_rk3399 ->{
                var intent = Intent(this, Neb156Activity::class.java)
                intent.putExtra("title", getString(R.string.model_neb156))
                startActivity(intent)
            }
            R.id.relative_vod073_px30 ->{
                startActivity(Intent(this, VegaActivity::class.java))
            }
            R.id.relative_vod073_rk3399 ->{
                startActivity(Intent(this, VegaActivity::class.java))
            }
            R.id.relative_vod073_rk3568 ->{
                startActivity(Intent(this, VegaActivity::class.java))
            }
            R.id.relative_emt431_rk3399 ->{
                var intent = Intent(this, Neb156Activity::class.java)
                intent.putExtra("title", getString(R.string.model_emt431))
                startActivity(intent)
            }
            R.id.relative_tad321_rk3399 ->{
                var intent = Intent(this, Neb156Activity::class.java)
                intent.putExtra("title", getString(R.string.model_tad321))
                startActivity(intent)
            }
            R.id.relative_vod101_rk3568 ->{
                var intent = Intent(this, Vod101Activity::class.java)
                intent.putExtra("title", getString(R.string.model_vega))
                startActivity(intent)
            }
            R.id.relative_vid101_a133 ->{
                var intent = Intent(this, Vid101Activity::class.java)
                intent.putExtra("title", getString(R.string.model_vega))
                startActivity(intent)
            }
        }
    }
}