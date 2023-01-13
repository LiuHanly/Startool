package com.glorystar.startool.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.constant.IntenAcitivity
import com.glorystar.startool.utils.db.AppDataBase
import com.glorystar.startool.utils.db.TeacherDao
import com.glorystar.startool.utils.tool.LiveDataBus

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader


/**
 * 按钮测试
 * **/
class ButtonActivity : BaseActivity() {
    private var mScreenStatusReceiver: ScreenStatusReceiver? = null
    lateinit var KEYCODE_POWER: ImageView
    lateinit var KEYCODE_VOLUME_DOWN: ImageView
    lateinit var KEYCODE_VOLUME_UP: ImageView
    lateinit var failure_bu: Button
    lateinit var redo_bu: TextView
    lateinit var through_bu: Button


    var TerDao: TeacherDao? = null
    var VOLUME_DOWN = 1
    var VOLUME_UP = 1
    var VODE_POWER = 1

    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, ButtonActivity::class.java)
            context.startActivity(intent)
            context.finish()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_button
    }

    override fun initView(savedInstanceState: Bundle?) {
        TerDao = AppDataBase.getDatabase(this).getTeacherDao()
        KEYCODE_POWER = findViewById(R.id.KEYCODE_POWER)
        KEYCODE_VOLUME_DOWN = findViewById(R.id.KEYCODE_VOLUME_DOWN)
        KEYCODE_VOLUME_UP = findViewById(R.id.KEYCODE_VOLUME_UP)
        failure_bu = findViewById(R.id.failure_bu)
        redo_bu = findViewById(R.id.redo_bu)
        through_bu = findViewById(R.id.through_bu)

        registSreenStatusReceiver()
        LiveDataBus.get().with("ButtonActivity", String::class.java).observe(this, Observer {
            when (it) {
                "KeyEvent.KEYCODE_POWER" -> {

                    KEYCODE_POWER.setImageResource(R.drawable.ic_checkcircle__)
                    VODE_POWER = 2
                }
            }
        })
        failure_bu?.setOnClickListener {

            IntenAcitivity.getIntenAcitivity(this@ButtonActivity, 1)


        }
        redo_bu?.setOnClickListener {
            KEYCODE_POWER.setImageResource(R.drawable.ic_gantanhao)
            KEYCODE_VOLUME_DOWN.setImageResource(R.drawable.ic_gantanhao)
            KEYCODE_VOLUME_UP.setImageResource(R.drawable.ic_gantanhao)
            VODE_POWER = 1
            VOLUME_UP = 1
            VOLUME_DOWN = 1

        }
        through_bu?.setOnClickListener {


            if (VOLUME_DOWN == 2 && VOLUME_UP == 2 && VODE_POWER == 2) {
                IntenAcitivity.getIntenAcitivity(this@ButtonActivity, 2)
            } else {
                IntenAcitivity.getIntenAcitivity(this@ButtonActivity, 1)
            }


        }
    }

    private fun registSreenStatusReceiver() {
        mScreenStatusReceiver = ScreenStatusReceiver()
        val screenStatusIF = IntentFilter()
        screenStatusIF.addAction(Intent.ACTION_SCREEN_ON)
        screenStatusIF.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(mScreenStatusReceiver, screenStatusIF)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mScreenStatusReceiver);

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                KEYCODE_VOLUME_UP.setImageResource(R.drawable.ic_checkcircle__)
                VOLUME_DOWN = 2
            }
            KeyEvent.KEYCODE_VOLUME_UP -> {
                KEYCODE_VOLUME_DOWN.setImageResource(R.drawable.ic_checkcircle__)
                VOLUME_UP = 2
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    internal class ScreenStatusReceiver : BroadcastReceiver() {
        var SCREEN_ON = "android.intent.action.SCREEN_ON"
        var SCREEN_OFF = "android.intent.action.SCREEN_OFF"
        override fun onReceive(context: Context?, intent: Intent) {
            if (SCREEN_ON == intent.action) {
                LiveDataBus.get().with("ButtonActivity").postValue("KeyEvent.KEYCODE_POWER")
            } else if (SCREEN_OFF == intent.action) {
                LiveDataBus.get().with("ButtonActivity").postValue("KeyEvent.KEYCODE_POWER")
            }
        }
    }

}