package com.glorystar.startool.ui.testactivity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.viewpager.widget.PagerAdapter
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.constant.IntenAcitivity
import com.glorystar.startool.ui.view.DrawingView
import com.glorystar.startool.ui.view.MyOnTouchView
import com.glorystar.startool.ui.view.MyTouchView
import com.glorystar.startool.ui.view.NoScrollViewPager
import com.glorystar.startool.ui.view.SwZoomDragImageView
import com.glorystar.startool.utils.tool.LiveDataBus
import com.glorystar.startool.utils.tool.StatusNaviBarUitl
import com.glorystar.startool.utils.tool.StatusNavigationBarUtil
import androidx.core.net.toUri


/**
 *触摸测试
 * */
class NewScreenActivity : BaseActivity() {

    private var viewList = arrayListOf<View>()
    var noscrollviewpager: NoScrollViewPager? = null
    var dv: DrawingView? = null
    var dawsme: FrameLayout? = null
    var myontouchFrameview: FrameLayout? = null

    var FrameLayoutDragImage: FrameLayout? = null
    var myontouchview: MyOnTouchView? = null

    var mytouchview: MyTouchView? = null

    var failure_bu: Button? = null
    var redo_bu: TextView? = null
    var through_bu: Button? = null
    lateinit var through_buse: Button
    var textView3: TextView? = null

    var swzoomdragimageview: SwZoomDragImageView? = null
    private var lastClickTime: Long = 0
    var ZoomDragImage: SwZoomDragImageView? = null
    val MIN_CLICK_DELAY_TIME = 1000 //这里设置不能超过多长时间


    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, NewScreenActivity::class.java)
            context.startActivity(intent)
            context.finish()
        }
    }

    override fun onStart() {
        super.onStart()
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        StatusNavigationBarUtil().hide(true, this@NewScreenActivity)

        Settings.System.putInt(
            contentResolver,
            "show_touches", 1
        );
        Settings.System.putInt(
            contentResolver,
            "pointer_location", 1
        )
    }

    override fun getLayoutId(): Int = R.layout.activity_new_screen

    override fun initView(savedInstanceState: Bundle?) {
        val view1 = layoutInflater.inflate(R.layout.activity_show_view_noe, null)
        val view2 = layoutInflater.inflate(R.layout.activity_show_view_two_new, null)
        through_bu = view2.findViewById(R.id.through_bu)
        through_buse = view2.findViewById(R.id.through_buse)
        FrameLayoutDragImage = view2.findViewById(R.id.FrameLayoutDragImage)

        mytouchview = MyTouchView(this@NewScreenActivity)

        dawsme = view1.findViewById(R.id.dawsme)
        viewList.add(view1)
        viewList.add(view2)
        dawsme?.addView(DrawingView(this@NewScreenActivity))


        swzoomdragimageview?.setImageResource(R.mipmap.imagexxxhdpi)
        swzoomdragimageview?.scaleType = ImageView.ScaleType.CENTER


        FrameLayoutDragImage?.addView(mytouchview)

        myontouchFrameview?.addView(myontouchview)

        noscrollviewpager = findViewById(R.id.NoScrollView_screenactivity)
        noscrollviewpager?.adapter = object : PagerAdapter() {
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


        LiveDataBus.get().with("DrawingView", Boolean::class.java).observe(this, Observer {
            val currentTime = SystemClock.uptimeMillis()
            var Item = noscrollviewpager?.currentItem
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                if (it) {
                    noscrollviewpager?.currentItem = Item?.plus(1)!!
//                    PassFramen.newInstance(false, "", this@ScreenActivity)
//                        ?.show(supportFragmentManager, "ScreenActivity")
                }
            }

            lastClickTime = currentTime
        })


        through_bu?.setOnClickListener {
            IntenAcitivity.getIntenAcitivity(this@NewScreenActivity, 2, 2, 1)
        }
        through_buse.setOnClickListener {
            dawsme?.removeAllViewsInLayout()
            FrameLayoutDragImage?.removeAllViewsInLayout()

            dawsme?.addView(DrawingView(this@NewScreenActivity))

            mytouchview = MyTouchView(this@NewScreenActivity)

            FrameLayoutDragImage?.addView(mytouchview)
            noscrollviewpager?.currentItem = 0

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        StatusNaviBarUitl.show(this@NewScreenActivity)
        Settings.System.putInt(
            contentResolver,
            "show_touches", 0
        );
        Settings.System.putInt(
            contentResolver,
            "pointer_location", 0
        )
    }



}