package com.glorystar.startool.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.viewpager.widget.PagerAdapter
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.constant.IntenAcitivity
import com.glorystar.startool.ui.dialog.TouchDialog
import com.glorystar.startool.ui.view.*
import com.glorystar.startool.utils.tool.LiveDataBus
import com.glorystar.startool.utils.tool.OnDoubleClickListener
import com.glorystar.startool.utils.tool.StatusNaviBarUitl
import com.glorystar.startool.utils.tool.StatusNavigationBarUtil


/**
 *触摸测试
 * */
class ScreenActivity : BaseActivity() {

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
    var textView3: TextView? = null

    var swzoomdragimageview: SwZoomDragImageView? = null
    private var lastClickTime: Long = 0
    var ZoomDragImage: SwZoomDragImageView? = null
    val MIN_CLICK_DELAY_TIME = 1000 //这里设置不能超过多长时间

    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, ScreenActivity::class.java)
            context.startActivity(intent)
            context.finish()
        }
    }

    override fun onStart() {
        super.onStart()
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        try {
            StatusNavigationBarUtil().hide(true, this@ScreenActivity)
            Settings.System.putInt(
                contentResolver, "show_touches", 1
            );
            Settings.System.putInt(
                contentResolver, "pointer_location", 1
            )
        } catch (e: Exception) {

        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_screen
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        val view1 = layoutInflater.inflate(R.layout.activity_show_view_noe, null)
        val view2 = layoutInflater.inflate(R.layout.activity_show_view_two, null)
        val view3 = layoutInflater.inflate(R.layout.activity_show_view_three, null)

        dawsme = view1.findViewById(R.id.dawsme)
        // ZoomDragImage = view2.findViewById(R.id.ZoomDragImage)
        myontouchFrameview = view3.findViewById(R.id.myontouchview)
        FrameLayoutDragImage = view2.findViewById(R.id.FrameLayoutDragImage)

        failure_bu = view3?.findViewById(R.id.failure_bu)
        redo_bu = view3?.findViewById(R.id.redo_bu)
        through_bu = view3?.findViewById(R.id.through_bu)

        textView3 = view2.findViewById(R.id.textView3)
        myontouchview = MyOnTouchView(this@ScreenActivity)
        swzoomdragimageview = SwZoomDragImageView(this)
        swzoomdragimageview?.setImageResource(R.mipmap.imagexxxhdpi)
        swzoomdragimageview?.scaleType = ImageView.ScaleType.CENTER

        mytouchview = MyTouchView(this@ScreenActivity)

        viewList.add(view1)
        viewList.add(view2)
        viewList.add(view3)
        dawsme?.addView(DrawingView(this@ScreenActivity))
        TouchDialog.newInstance()?.show(this.supportFragmentManager, "")
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

        failure_bu?.setOnClickListener {

            IntenAcitivity.getIntenAcitivity(this@ScreenActivity, 2, 1)

        }


        redo_bu?.setOnClickListener {
            dawsme?.removeAllViewsInLayout()
            FrameLayoutDragImage?.removeAllViewsInLayout()

            dawsme?.addView(DrawingView(this@ScreenActivity))

            mytouchview = MyTouchView(this@ScreenActivity)

            FrameLayoutDragImage?.addView(mytouchview)
            noscrollviewpager?.currentItem = 0

        }
        through_bu?.setOnClickListener {

            IntenAcitivity.getIntenAcitivity(this@ScreenActivity, 2, 2)
        }
        //
        textView3?.setOnTouchListener(
            OnDoubleClickListener(
                @SuppressLint("ClickableViewAccessibility") object :
                    OnDoubleClickListener.DoubleClickCallback {
                    override fun onDoubleClick() {
                        LiveDataBus.get().with("DrawingView").postValue(true)
                    }
                })
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            StatusNaviBarUitl.show(this@ScreenActivity)
            Settings.System.putInt(
                contentResolver, "show_touches", 0
            );
            Settings.System.putInt(
                contentResolver, "pointer_location", 0
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}