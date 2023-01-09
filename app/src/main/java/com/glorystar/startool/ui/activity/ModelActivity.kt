package com.glorystar.startool.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView

class ModelActivity :BaseActivity(), View.OnClickListener {
    lateinit var titleView: TitleKtView
    lateinit var neb101: FrameLayout
    lateinit var neb156: FrameLayout
    lateinit var vega: FrameLayout

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

        neb101 = findViewById(R.id.frame_101)
        neb156 = findViewById(R.id.frame_neb)
        vega = findViewById(R.id.frame_vega)

        neb101.setOnClickListener(this)
        neb156.setOnClickListener(this)
        vega.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.frame_101 ->{
                startActivity(Intent(application, Neb101Activity::class.java))
            }
            R.id.frame_neb ->{
                startActivity(Intent(application, Neb156Activity::class.java))
            }
            R.id.frame_vega ->{
                startActivity(Intent(application, VegaActivity::class.java))
            }
        }
    }
}