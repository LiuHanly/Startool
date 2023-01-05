package com.glorystar.startool.ui.widget

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.glorystar.startool.R
import com.glorystar.startool.ui.adapter.BaseRecyclerAdapter
import com.glorystar.startool.ui.adapter.BaseRecyclerHolder
import com.lxj.xpopup.core.AttachPopupView
import com.lxj.xpopup.interfaces.OnSelectListener
import com.lxj.xpopup.widget.VerticalRecyclerView

class AttachListPoView  constructor(context: Context) : AttachPopupView(context){

    var recyclerView: RecyclerView? = null
    var data: MutableList<String>? = null

    override fun getImplLayoutId(): Int {
        return R.layout.attaserved_view
    }

    override fun initPopupContent() {
        super.initPopupContent()
        attachPopupContainer.setBackgroundResource(R.drawable.slectr_ageing_bg22)
        recyclerView = findViewById(R.id.recyclerView)


        val adapter =
            object : BaseRecyclerAdapter<String>(context, data, R.layout.attaserved_simple_item) {
                /**
                 * 填充RecyclerView适配器的方法，子类需要重写
                 *
                 * @param holder      ViewHolder
                 * @param data        子项
                 * @param position    位置
                 * @param isScrolling 是否在滑动
                 */
                override fun convert(
                    holder: BaseRecyclerHolder?,
                    data: String?,
                    position: Int,
                    isScrolling: Boolean,
                    selectedPosition: Int
                ) {
                    holder?.setText(R.id.text_tv, data)
                }
            }

        recyclerView?.adapter = adapter


        adapter.setOnItemClickListener { viewHolder, data, position ->
            if (selectListener != null) {
                selectListener?.onSelect(position, data);
            }
            if (popupInfo.autoDismiss) {
                dismiss()
            }

        }

    }

    fun setStringData(data: MutableList<String>): AttachListPoView {
        this.data = data
        return this
    }

    fun setOffsetXAndY(offsetX: Int, offsetY: Int): AttachListPoView? {
        defaultOffsetX += offsetX
        defaultOffsetY += offsetY
        return this
    }

    var selectListener: OnSelectListener? = null
    fun setOnSelectListener(selectListener: OnSelectListener?): AttachListPoView? {
        this.selectListener = selectListener
        return this
    }


}