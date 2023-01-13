package com.glorystar.startool.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView

class Jar101Activity : BaseActivity() {
    lateinit var titleView: TitleKtView
    lateinit var relativeLight: RelativeLayout

    override fun getLayoutId(): Int = R.layout.activity_jar101

    override fun initView(savedInstanceState: Bundle?) {
        titleView = findViewById(R.id.tile_view)
        titleView.title.text = getString(R.string.model_jar101)
        titleView.action = this

        relativeLight = findViewById(R.id.relative_light)

        relativeLight.setOnClickListener {
            startActivity(Intent(this, LightStripsActivity::class.java))
        }
    }
}