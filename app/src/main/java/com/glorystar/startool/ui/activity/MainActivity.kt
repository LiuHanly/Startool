package com.glorystar.startool.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity

class MainActivity : BaseActivity() {

    private lateinit var General_Relative: RelativeLayout

    private lateinit var Model_Relative: RelativeLayout

    private lateinit var Addon_Relative: RelativeLayout

    lateinit var Aging_Relative: RelativeLayout

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
        General_Relative = findViewById(R.id.Enter_General)
        Model_Relative = findViewById(R.id.Enter_Model)
        Addon_Relative = findViewById(R.id.Enter_Addon)
        Aging_Relative = findViewById(R.id.Enter_Aging)
        language_text = findViewById(R.id.textlanguage)


        General_Relative.setOnClickListener {
            this.startActivity(Intent(application, GeneralActivity::class.java))
        }

        Aging_Relative.setOnClickListener {

            this.startActivity(Intent(application, AgingActivity::class.java))
        }


    }
}