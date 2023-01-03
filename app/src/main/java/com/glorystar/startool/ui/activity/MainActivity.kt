package com.glorystar.startool.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.base.BasePoup
import com.glorystar.startool.config.constant.Constantdtas
import com.glorystar.startool.utils.tool.IntentJumpUtils
import com.glorystar.startool.utils.tool.LiveDataBus
import com.glorystar.startool.utils.tool.StatusNaviBarUitl
import com.lxj.xpopup.enums.PopupPosition

class MainActivity : BaseActivity() {

    private lateinit var linear_general: LinearLayout

    private lateinit var linear_model: LinearLayout

    private lateinit var linear_addon: LinearLayout

    lateinit var linear_aging: LinearLayout

    private lateinit var language_text: TextView

    /**
     * 设置布局
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    /**
     * 初始化视图
     */
    override fun initView(savedInstanceState: Bundle?) {
        linear_general = findViewById(R.id.linear_general)
        linear_model = findViewById(R.id.linear_model)
        linear_addon = findViewById(R.id.linear_addon)
        linear_aging = findViewById(R.id.linear_aging)
        language_text = findViewById(R.id.textlanguage)

        StatusNaviBarUitl.hide(application)

        linear_general.setOnClickListener {
            this.startActivity(Intent(application, GeneralActivity::class.java))
        }

        linear_model.setOnClickListener {
            startActivity(Intent(application, ModelActivity::class.java))
        }

        linear_aging.setOnClickListener {

            this.startActivity(Intent(application, AgingActivity::class.java))
        }
        language_text.setOnClickListener {
            BasePoup.Builder(this)
                .hasShadowBg(false)
                .atView(it)
                .popupPosition(PopupPosition.Bottom)
                .asAttaServedView(Constantdtas.AttaServedName){ position, text ->
                    language_text.text = text
                    IntentJumpUtils.setLanguage(position)
                    LiveDataBus.get().with("Language").postValue("refresh_language")
                    this.run {
                        this?.run {
                            ActivityCompat.recreate(this)
                        }
                    }
                }.show()

        }
    }
}