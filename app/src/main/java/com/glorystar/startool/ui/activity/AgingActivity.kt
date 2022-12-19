package com.glorystar.startool.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView

class AgingActivity : BaseActivity() {
    lateinit var tile_view: TitleKtView

    lateinit var preaging_relat: RelativeLayout
    lateinit var aging_relat: RelativeLayout
    lateinit var test_post: RelativeLayout

    /**
     * 设置布局
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_aging
    }

    /**
     * 初始化视图
     */
    override fun initView(savedInstanceState: Bundle?) {
        tile_view = findViewById(R.id.tile_view)
        tile_view.action = this
        tile_view.title.text = getString(R.string.Aging)

        preaging_relat = findViewById(R.id.preaging_relat)
        aging_relat = findViewById(R.id.aging_relat)
        test_post = findViewById(R.id.text_post)

        preaging_relat.setOnClickListener {
            startActivity(Intent(this, PreAgingTests::class.java))
        }
        aging_relat.setOnClickListener {
            AgeingKtActivity.toStartActivity(this@AgingActivity)
        }
        test_post.setOnClickListener {
            startActivity(Intent(this, PreAgingTests::class.java))
        }
    }
}