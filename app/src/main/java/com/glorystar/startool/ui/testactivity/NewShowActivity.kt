package com.glorystar.startool.ui.testactivity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.setPadding
import androidx.viewpager.widget.PagerAdapter
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.base.MyApplication.Companion.context
import com.glorystar.startool.config.constant.IntenAcitivity
import com.glorystar.startool.ui.view.NoScrollViewPager
import com.glorystar.startool.utils.tool.Rootcommands
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class NewShowActivity : BaseActivity() {

    var screenshow_rl: ImageView? = null
    var seekBar: SeekBar? = null

    lateinit var noscroll_Viewpager_ScreenShow: NoScrollViewPager

    private var viewList = arrayListOf<View>()

    lateinit var failure_bu: Button
    lateinit var redo_bu: TextView
    lateinit var through_bu: Button
    lateinit var nextButton: Button


    lateinit var text_name: TextView

    var Max_Brightness = 100
    var fBrightness = 0.0f
    var lp: WindowManager.LayoutParams? = null

    var time = 0
    var progressIntx: Int = 255
    var isProgess = true

    var timeThread = 1000L
    var isInfox = 0
    var isTher = true

    var MaxBrightness = 0
    var MinBrightness = 0


    @SuppressLint("HandlerLeak")
    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 0) {
                Log.e("handleMessage", "${msg.what}")
                if (numbe < Textcolor.size) {
                    Log.e("handleMessage", "${msg.what} -<if>- $numbe")
                    screenshow_rl?.setBackgroundColor(Color.parseColor(Textcolor[numbe]))
                    if (numbe == 2) {
                        text_name.setTextColor(Color.WHITE)
                    } else {
                        text_name.setTextColor(Color.BLACK)
                    }
                    numbe++
                } else {

                    if (Imagenumbe < imageDtae.size) {
                        // screenshow_rl.setImageDrawable()
                        screenshow_rl?.setBackgroundResource(imageDtae[Imagenumbe])

                        text_name.text = ""
                        Imagenumbe++
                    } else {
                        if (noscroll_Viewpager_ScreenShow?.currentItem != 1) {
                            noscroll_Viewpager_ScreenShow?.currentItem = 1
                        } else {
                            isTher = false
                            /*                          timeThread = 50L
                                                        when (progressIntx) {
                                                            MinBrightness -> {
                                                                isProgess = false
                                                                isInfox++
                                                            }

                                                            MaxBrightness -> {
                                                                isProgess = true
                                                                isInfox++
                                                            }
                                                        }
                                                        if(isInfox != 3){
                                                            Log.e("getResolver", "--progress-->${progressIntx}")
                                                            seekBar?.progress = progressIntx
                                                            setScreenLight(progressIntx)
                                                        }
                                                        if (isProgess) {
                                                            progressIntx--
                                                        } else {
                                                            progressIntx++
                                                        }
                                                        if (isInfox == 3) {
                                                            setIntenAcit()
                                                        }*/

                        }


                    }
                }
            }

        }
    }


    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, NewShowActivity::class.java)
            context.startActivity(intent)
            context.finish()
        }

    }

    //红 , 绿 , 蓝 , 黑 , 白
    private val Textcolor = arrayOf("#00ff00", "#0000ff", "#000000")
    private val imageDtae = arrayOf(
        R.mipmap.imageview1, R.mipmap.imageview3
    )

    var numbe = 0
    var Imagenumbe = 0

    override fun getLayoutId(): Int = R.layout.activity_new_show

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView(savedInstanceState: Bundle?) {
//        if (Settings.System.canWrite(context)) {
//            // 有权限，直接设置亮度
//        } else {
//            // 跳转系统设置页面让用户授权
//            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
//            startActivity(intent)
//        }

        val view1 = layoutInflater.inflate(R.layout.screen_view_noe, null)
        val view2 = layoutInflater.inflate(R.layout.screen_view_tow, null)
        getResolver()

        noscroll_Viewpager_ScreenShow = findViewById(R.id.noscroll_Viewpager_ScreenShow)

        viewList.add(view1)
        viewList.add(view2)


        text_name = view1.findViewById(R.id.text_name)
        nextButton = view2.findViewById(R.id.nextButton)

        text_name.visibility = View.GONE
        nextButton.setOnClickListener {
            IntenAcitivity.getIntenAcitivity(this@NewShowActivity, 1, 2, 1)
        }

        lp = window.attributes

        screenshow_rl = view1.findViewById(R.id.screenshow_rl)

        screenshow_rl?.setBackgroundColor(Color.parseColor("#FF0000"))
        seekBar = view2.findViewById(R.id.seekBar)

        seekBar?.max = MaxBrightness
        seekBar?.min = MinBrightness
        /*        screenshow_rl?.setOnClickListener {
                    if (numbe < Textcolor.size) {
                        screenshow_rl?.setBackgroundColor(Color.parseColor(Textcolor[numbe]))
                        if (numbe == 2) {
                            text_name.setTextColor(Color.WHITE)
                        } else {
                            text_name.setTextColor(Color.BLACK)
                        }
                        numbe++
                    } else {
                        if (Imagenumbe < imageDtae.size) {
                            // screenshow_rl.setImageDrawable()
                            screenshow_rl?.setBackgroundResource(imageDtae[Imagenumbe])

                            text_name.text = ""
                            Imagenumbe++
                        } else {
                            noscroll_Viewpager_ScreenShow?.currentItem = 1
                        }

                        //
                    }
                }*/

        seekBar?.setPadding(50)
        seekBar?.progress = getBrightness()
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                setScreenLight(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        noscroll_Viewpager_ScreenShow?.adapter = object : PagerAdapter() {
            override fun getCount(): Int = viewList.size
            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view === `object`
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                container.addView(viewList[position])
                return viewList[position]
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(viewList[position])
            }
        }
        GlobalScope.launch {
            while (isTher) {
                delay(timeThread)
                time++
                val message: Message = handler.obtainMessage()
                message.what = 0
                message.obj = time
                handler.sendMessage(message)
            }
        }


    }

    @Synchronized
    fun setScreenLight(brightness: Int) {
        val resolver: ContentResolver = getContentResolver()
        val safeBrightness = brightness.coerceIn(0, 255)
        //   Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
        val cmd = "settings put system screen_brightness_mode 0 && settings put system screen_brightness $safeBrightness"
        Rootcommands.runRootCommand(cmd)
//        val uri: Uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS)
//        val contentResolver = contentResolver
//        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
//        contentResolver.notifyChange(uri, null)
    }


    /**
     * 获得当前亮度
     */

    private fun getBrightness(): Int {
        var brightness = 0
        val resolver: ContentResolver = this.contentResolver
        try {
            brightness = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        return brightness
    }

    //获取系统亮度 最大 最小值
    fun getResolver() {
        val resolver = contentResolver
        val brightnessSettingMaximumId =
            resources.getIdentifier("config_screenBrightnessSettingMaximum", "integer", "android")
        val brightnessSettingMaximum = resources.getInteger(brightnessSettingMaximumId)
        val brightnessSettingMinimumId =
            resources.getIdentifier("config_screenBrightnessSettingMinimum", "integer", "android")
        val brightnessSettingMinimum = resources.getInteger(brightnessSettingMinimumId)
        MaxBrightness = brightnessSettingMaximum
        MinBrightness = brightnessSettingMinimum
        Log.e(
            "getResolver",
            "-Maximum-${brightnessSettingMaximum} -Minimum- ${brightnessSettingMinimum}"
        )
    }

    @Synchronized
    fun setIntenAcit() {
        Log.e("getResolver", "--->${isInfox}")
        isTher = false

    }


}