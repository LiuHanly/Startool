package com.glorystar.startool.config.base

import android.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.glorystar.startool.utils.tool.DimenUtil


abstract class BaseDialogFramene :  DialogFragment() {



    override fun onStart() {
        super.onStart()
        // 弹框添加。
        val dialog = dialog
        if (dialog != null && activity != null) {
            //设置窗口的背景
            dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
            val lp = dialog.window!!.attributes
            lp.gravity = Gravity.NO_GRAVITY // 显示在底部
            lp.dimAmount = 0.5f //设置窗口后面的暗度
            // lp.alpha = 0.8f;//设置dialog窗口的透明度
            //设置dialog窗口的宽
            lp.width = ((DimenUtil.screenWidth * 0.8).toInt())
            lp.height =(DimenUtil.screenHeight *0.35).toInt() // LinearLayout.LayoutParams.WRAP_CONTENT *0.75 设置dialog窗口的高
            dialog.window!!.attributes = lp
            dialog.setCancelable(true)

        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE); // 不显示标题栏
        val view = inflater.inflate(layoutId, container, false)
        initView(view,savedInstanceState)
        return view
    }

    /**
     * 设置布局
     */
    abstract val layoutId: Int

    /**
     * 初始化视图
     */
    protected abstract fun initView(view: View?, savedInstanceState: Bundle?)
    override fun onDestroyView() {
        super.onDestroyView()
    }

}