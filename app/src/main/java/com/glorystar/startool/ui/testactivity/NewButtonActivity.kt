package com.glorystar.startool.ui.testactivity


import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.widget.Button
import android.widget.ImageView
import androidx.lifecycle.Observer
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.constant.IntenAcitivity
import com.glorystar.startool.utils.db.AppDataBase
import com.glorystar.startool.utils.db.TeacherDao
import com.glorystar.startool.utils.tool.LiveDataBus
import com.glorystar.startool.utils.tool.Rootcommands


class NewButtonActivity : BaseActivity() {

    private var mScreenStatusReceiver: ScreenStatusReceiver? = null
    lateinit var KEYCODE_POWER: ImageView
    lateinit var KEYCODE_VOLUME_DOWN: ImageView
    lateinit var KEYCODE_VOLUME_UP: ImageView
    lateinit var failure_bu: Button

    lateinit var through_bu: Button

    var TerDao: TeacherDao? = null
    var VOLUME_DOWN = 1
    var VOLUME_UP = 1
    var VODE_POWER = 1


    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, NewButtonActivity::class.java)
            context.startActivity(intent)
            context.finish()
        }
    }


    override fun getLayoutId(): Int = R.layout.activity_new_button

    override fun initView(savedInstanceState: Bundle?) {

        TerDao = AppDataBase.getDatabase(this).getTeacherDao()
        KEYCODE_POWER = findViewById(R.id.KEYCODE_POWER)
        KEYCODE_VOLUME_DOWN = findViewById(R.id.KEYCODE_VOLUME_DOWN)
        KEYCODE_VOLUME_UP = findViewById(R.id.KEYCODE_VOLUME_UP)
        failure_bu = findViewById(R.id.failure_bu)
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
            // 格式化内存  "format data -f"   am broadcast -a android.intent.action.FACTORY_RESET -p android --es android.intent.extra.REASON MasterClearConfirm
           Rootcommands.runRootCommand("input keyevent 26")
            //   Rootcommands.runRootCommand("recovery --wipe_data")
            //  factoryReset(this)
       //  Settings.Secure.r   contentResolver


        }
        through_bu?.setOnClickListener {
            if (VOLUME_DOWN == 2 && VOLUME_UP == 2 && VODE_POWER == 2) {
                IntenAcitivity.getIntenAcitivity(this@NewButtonActivity, 2,"")
            } else {
                IntenAcitivity.getIntenAcitivity(this@NewButtonActivity, 1,"")
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
        unregisterReceiver(mScreenStatusReceiver)


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