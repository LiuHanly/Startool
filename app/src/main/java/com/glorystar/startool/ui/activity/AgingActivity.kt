package com.glorystar.startool.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.ui.view.TitleKtView

class AgingActivity : BaseActivity() {
    lateinit var tile_view: TitleKtView

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
        tile_view.title.text = getString(R.string.Aging)
        tile_view.setonLetterTouchedChangeListener(object :
            TitleKtView.OnLetterTouchedChangeListener {
            @SuppressLint("LogNotTimber")
            override fun onTouchedLetterChange(id: Int) {
                Log.e("AgingActivity","---->"+id)
                when (id) {
                    R.id.back_linear -> {
                        finish()
                    }
                }


            }

        })

    }
}