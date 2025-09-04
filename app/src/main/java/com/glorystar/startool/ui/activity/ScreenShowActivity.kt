package com.glorystar.startool.ui.activity

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.core.view.setPadding
import androidx.viewpager.widget.PagerAdapter
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.constant.IntenAcitivity
import com.glorystar.startool.ui.view.NoScrollViewPager
import com.glorystar.startool.utils.tool.StatusNavigationBarUtil

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 屏幕测试
 * */
class ScreenShowActivity : BaseActivity() {

    var ID: Long = 1

    var screenshow_rl: ImageView? = null
    var seekBar: SeekBar? = null
    private var viewList = arrayListOf<View>()
    private lateinit var nextButton: Button

    lateinit var failure_bu: Button
    lateinit var redo_bu: TextView
    lateinit var through_bu: Button

    lateinit var text_name: TextView

    lateinit var noscroll_Viewpager_ScreenShow: NoScrollViewPager


    var Max_Brightness = 100
    var fBrightness = 0.0f
    var lp: WindowManager.LayoutParams? = null

    //红 , 绿 , 蓝 , 黑 , 白
    private val Textcolor = arrayOf("#00ff00", "#0000ff", "#000000", "#ffffff")
    var numbe = 0
    var Imagenumbe = 0
    private val imageDtae = arrayOf(
        R.mipmap.imageview1, R.mipmap.imageview2, R.mipmap.imageview3
    )

    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, ScreenShowActivity::class.java)
            context.startActivity(intent)
            context.finish()
        }
    }

    override fun onStart() {
        super.onStart()
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        StatusNavigationBarUtil().hide(true, this@ScreenShowActivity)
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun getLayoutId(): Int = R.layout.activity_screen_show
    override fun initView(savedInstanceState: Bundle?) {


        val view1 = layoutInflater.inflate(R.layout.screen_view_noe, null)
        val view2 = layoutInflater.inflate(R.layout.screen_view_tow, null)
        val view3 = layoutInflater.inflate(R.layout.screen_view_three, null)

        noscroll_Viewpager_ScreenShow = findViewById(R.id.noscroll_Viewpager_ScreenShow)
        viewList.add(view1)
        viewList.add(view2)
        viewList.add(view3)
        failure_bu = view3.findViewById(R.id.failure_bu)
        redo_bu = view3.findViewById(R.id.redo_bu)
        through_bu = view3.findViewById(R.id.through_bu)
        text_name = view1.findViewById(R.id.text_name)
        nextButton = view2.findViewById(R.id.nextButton)
        lp = getWindow().getAttributes()
        screenshow_rl = view1.findViewById(R.id.screenshow_rl)
        screenshow_rl?.setBackgroundColor(Color.parseColor("#FF0000"))
        seekBar = view2.findViewById(R.id.seekBar)
        seekBar?.max = 255
        screenshow_rl?.setOnClickListener {
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
        }

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
        nextButton.setOnClickListener {
            noscroll_Viewpager_ScreenShow?.currentItem = 2
        }

        failure_bu.setOnClickListener {

            IntenAcitivity.getIntenAcitivity(this@ScreenShowActivity, 1, 1)
        }
        redo_bu.setOnClickListener {
            numbe = 0
            Imagenumbe = 0
            text_name.text = resources.getString(R.string.screen_colors)
            screenshow_rl?.setBackgroundColor(Color.parseColor("#FF0000"))

            noscroll_Viewpager_ScreenShow?.currentItem = 0
        }
        through_bu.setOnClickListener {

            IntenAcitivity.getIntenAcitivity(this@ScreenShowActivity, 1, 2)

        }


    }

    fun setScreenLight(brightness: Int) {
        val resolver: ContentResolver = getContentResolver()
        Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
//        try {
//            val uri: Uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS)
//            val contentResolver = contentResolver
//            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
//            contentResolver.notifyChange(uri, null)
//        } catch (e: Exception) {
//
//        }

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

}