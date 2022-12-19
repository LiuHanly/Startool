package com.glorystar.startool.config.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragemt : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(layoutId, container, false)
        // 初始化视图
        initView(view, savedInstanceState)
        view?.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                activity?.onTouchEvent(event);
                return false
            }
        })

        return view
    }
    /**
     * 设置布局
     */
    protected abstract val layoutId: Int

    /**
     * 初始化视图
     */
    protected abstract fun initView(view: View?, savedInstanceState: Bundle?)
    override fun onDestroyView() {
        super.onDestroyView()
    }

    @SuppressLint("HandlerLeak")
    public var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {


            }
        }
    }

}