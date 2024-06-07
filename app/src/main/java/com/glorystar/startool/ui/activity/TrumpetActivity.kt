package com.glorystar.startool.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.viewpager.widget.PagerAdapter
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.constant.IntenAcitivity
import com.glorystar.startool.ui.view.NoScrollViewPager
import com.glorystar.startool.ui.view.RTextView
import com.glorystar.startool.utils.tool.PlayThread

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 喇叭测试
 * **/

class TrumpetActivity : BaseActivity() {
    var mChannelLeftPlayer: PlayThread? = null
    var mChannelRightPlayer: PlayThread? = null
    var noscroll_Viewpager_ScreenShow: NoScrollViewPager? = null
    private var viewList = arrayListOf<View>()
    lateinit var badge_view: ImageView
    lateinit var through_bu: Button
    lateinit var failure_bu: Button
    lateinit var redo_bu: TextView


    lateinit var through_bu2: Button
    lateinit var failure_bu2: Button
    lateinit var redo_bu2: TextView

    var countdowntimer: CountDownTimer? = null


    var pull = 1


    @SuppressLint("HandlerLeak")
    var mHandler: Handler = object : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

//            if (msg.what/2 == 0){
//                setRunalie(2)
//            }else{
//                setRunalie(1)
//            }


            when (msg.what) {
                0 -> {
                    if (inetse % 2 == 0) {

                        Log.e("TrumpetActivity", "2-->" + inetse)
                        setRunalie(2)
                    } else {

                        Log.e("TrumpetActivity", "1->" + inetse)
                        setRunalie(1)
                    }

                }
            }
        }
    }


    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, TrumpetActivity::class.java)
            context.startActivity(intent)
            context.finish()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_screen_show
    }

    override fun initView(savedInstanceState: Bundle?) {

        noscroll_Viewpager_ScreenShow = findViewById(R.id.noscroll_Viewpager_ScreenShow)
        val view1 = layoutInflater.inflate(R.layout.activity_trumpet, null)
        val view2 = layoutInflater.inflate(R.layout.headset_plug, null)
        viewList.add(view1)
        viewList.add(view2)
        DownTimer()
        badge_view = view1.findViewById(R.id.badge_view)
        through_bu = view1.findViewById(R.id.through_bu)
        failure_bu = view1.findViewById(R.id.failure_bu)
        redo_bu = view1.findViewById(R.id.redo_bu)

        through_bu2 = view2.findViewById(R.id.through_bu2)
        failure_bu2 = view2.findViewById(R.id.failure_bu2)
        redo_bu2 = view2.findViewById(R.id.redo_bu2)

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

        failure_bu2.setOnClickListener {

            IntenAcitivity.getIntenAcitivity(this@TrumpetActivity, 11, 1)

        }


        through_bu2.setOnClickListener {

            IntenAcitivity.getIntenAcitivity(this@TrumpetActivity, 11, 2)

        }


        through_bu.setOnClickListener {
            countdowntimer?.cancel()
            noscroll_Viewpager_ScreenShow?.currentItem = 1
            // DownTimer()

            handler.post(updateThread)
            IntenAcitivity.getIntenAcitivity(this@TrumpetActivity, 10, 2)


        }

        failure_bu.setOnClickListener {
            countdowntimer?.cancel()
            noscroll_Viewpager_ScreenShow?.currentItem = 1
            DownTimer()
            IntenAcitivity.getIntenAcitivity(this@TrumpetActivity, 10, 1)
        }


        //   DownTimer()
        badge_view.setOnClickListener {
            DownTimer()
        }

        redo_bu2.setOnClickListener {
            DownTimer()

        }
        redo_bu.setOnClickListener {
            DownTimer()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mChannelLeftPlayer?.stopp()
        mChannelRightPlayer?.stopp()
        mChannelLeftPlayer == null
        mChannelRightPlayer == null
        countdowntimer?.cancel()
        handler.removeCallbacks(updateThread)
    }

    fun DownTimer() {
        mChannelLeftPlayer?.stopp()
        mChannelRightPlayer?.stopp()
        mChannelLeftPlayer == null
        mChannelRightPlayer == null

        mChannelLeftPlayer = PlayThread(this, "mChannelLeft.wav")
        mChannelRightPlayer = PlayThread(this, "mChannelRight.wav")
        mChannelLeftPlayer?.setChannel(true, false)
        mChannelRightPlayer?.setChannel(false, true)
        mChannelLeftPlayer?.start()
        countdowntimer = object : CountDownTimer(7 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                //  badge_view.text = "播放中"
                badge_view?.isClickable = false
                Log.e("CountDownTimer", "" + millisUntilFinished.div(1000).toInt())
                var Timer = millisUntilFinished.div(1000).toInt()
                when (Timer) {
                    3 -> {
                        mChannelLeftPlayer?.stopp()
                        mChannelRightPlayer?.start()
                    }

                }
            }

            override fun onFinish() {
                // badge_view.text = "播放"
                badge_view?.isClickable = true

                mChannelLeftPlayer?.stopp()
                mChannelRightPlayer?.stopp()

            }

        }
        countdowntimer?.start()
    }

    var inetse = 0
    val handler = Handler()
    val updateThread: Runnable = object : Runnable {
        override fun run() {

            val message = Message()
            message.arg1 = inetse
            mHandler.dispatchMessage(message)
            //在run方法内部，执行postDelayed或者是post方法
            inetse++
            handler.postDelayed(this, 2000)
            //do something
            //每隔1s循环执行run方法
        }
    }

    fun setRunalie(Player: Int) {
        mChannelLeftPlayer?.stopp()
        mChannelRightPlayer?.stopp()
        mChannelLeftPlayer == null
        mChannelRightPlayer == null

        mChannelLeftPlayer = PlayThread(this, "mChannelLeft.wav")
        mChannelRightPlayer = PlayThread(this, "mChannelRight.wav")

        mChannelLeftPlayer?.setChannel(true, false)
        mChannelRightPlayer?.setChannel(false, true)
        if (Player == 1) {
            mChannelLeftPlayer?.start()

        } else {

            mChannelRightPlayer?.start()
        }

    }


}