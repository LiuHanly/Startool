package com.glorystar.startool.ui.dialog

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseDialogFramene

class TouchDialog : BaseDialogFramene() {
    lateinit var start_button: Button


    companion object {
        /**
         * @param datase 文件类型
         * @param stun  是否为单选
         * @param identifier 文件路径  sp key
         * @param TYPE 文件类型 针对 普通网页和全屏网页sp key
         * */
        fun newInstance(): TouchDialog? {
            return TouchDialog()
        }
    }

    /**
     * 设置布局
     */
    override val layoutId: Int
        get() = R.layout.dialog_touch

    /**
     * 初始化视图
     */
    override fun initView(view: View?, savedInstanceState: Bundle?) {

        start_button = view?.findViewById(R.id.start_button)!!


        start_button.setOnClickListener {
            dismiss()
        }


    }

}