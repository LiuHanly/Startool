package com.glorystar.startool.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.base.MyApplication
import com.glorystar.startool.config.bean.AgingBean
import com.glorystar.startool.config.bean.liuchenData
import com.glorystar.startool.config.constant.AgeingDataBase
import com.glorystar.startool.config.constant.Constantdtas
import com.glorystar.startool.config.constant.StudentDataBase
import com.glorystar.startool.ui.adapter.BaseRecyclerAdapter
import com.glorystar.startool.ui.adapter.BaseRecyclerHolder
import com.glorystar.startool.ui.view.TitleKtView
import com.glorystar.startool.utils.db.Student
import com.glorystar.startool.utils.tool.JsonHelper
import okhttp3.internal.notify
import java.io.BufferedReader
import java.io.InputStreamReader

class PreAgingTests : BaseActivity() {
    lateinit var tile_view: TitleKtView
    var baserecycleradapter: BaseRecyclerAdapter<AgingBean>? = null
    lateinit var ageing_fro_recy: RecyclerView
    val minItemBean: MutableList<AgingBean> = ArrayList<AgingBean>()
    lateinit var all_select: TextView
    lateinit var start_button: Button
    var position = -1


    companion object {
        // 跳转到当前页面。
        lateinit var tileName: String
        fun toStartActivity(context: Context, tileNmae: String) {
            val intent = Intent(context, PreAgingTests::class.java)
            tileName = tileNmae
            context.startActivity(intent)
        }
    }

    /**
     * 设置布局
     */
    override fun getLayoutId(): Int {
        return R.layout.pre_aging_tests_activity

    }

    /**
     * 初始化视图
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun initView(savedInstanceState: Bundle?) {
        tile_view = findViewById(R.id.tile_view)
        tile_view.title.text = tileName
        tile_view.action = this
        ageing_fro_recy = findViewById(R.id.ageing_fro_recy)
        all_select = findViewById(R.id.all_select)
        start_button = findViewById(R.id.start_button)

        val mAssetManger: AssetManager = getAssets()
        val bufferedReader1 =
            BufferedReader(InputStreamReader(mAssetManger.open("liucheng.txt"), "utf-8"))
        val lines1 = bufferedReader1.readLines()

        minItemBean.addAll(Constantdtas.getGreAgingDate(this@PreAgingTests))

        if (MyApplication.mConfiguration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
            ageing_fro_recy.layoutManager = LinearLayoutManager(this)
        } else {
            ageing_fro_recy.layoutManager = GridLayoutManager(this, 3)


        }

        baserecycleradapter =
            object : BaseRecyclerAdapter<AgingBean>(this, minItemBean, R.layout.ageing_fro_imet) {
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
                    data: AgingBean?,
                    position: Int,
                    isScrolling: Boolean,
                    selectedPosition: Int
                ) {

                    val image = holder?.getView<ImageView>(R.id.iv_image)
                    val textView = holder?.getView<TextView>(R.id.tv_title_name)
                    val checkBox = holder?.getView<CheckBox>(R.id.checkBox)
                    checkBox?.isChecked = data?.selected == true
                    data?.imageView?.let { image?.setImageResource(it) }
                    data?.AdingName?.let { textView?.text = it }
                }

            }

        ageing_fro_recy.adapter = baserecycleradapter
        baserecycleradapter?.setOnItemClickListener { viewHolder, data, position ->
            minItemBean[position].selected = !data.selected
            baserecycleradapter?.notifyDataSetChanged()
        }

        all_select.setOnClickListener {
            minItemBean.forEach {
                it.selected = true
            }
            baserecycleradapter?.notifyDataSetChanged()

        }

        start_button.setOnClickListener {
            MessageActivity.toStartActivity(this@PreAgingTests)
            PeriodicSync(lines1)

        }


    }

    @SuppressLint("LogNotTimber")
    fun PeriodicSync(lines1: List<String>) {
        val alils: MutableList<Student> = arrayListOf()
        var id = 0
        StudentDataBase(this).studentDao.deleteAll()
        Thread(Runnable {
            lines1.forEach { StrngIt ->
                Log.e("PeriodicSync->", StrngIt)
                val itJson = JsonHelper.newfromJson(StrngIt, liuchenData::class.java)
                minItemBean.forEach  { appinfoIt ->

                    Log.e(
                        "PeriodicSync",
                        "StrngIt-->" + itJson.id + "appinfoIt.appName--->" + appinfoIt.id + "---" + appinfoIt.AdingName + "---" + appinfoIt.selected

                    )
                    if (itJson.id == appinfoIt.id) {
                        alils.add(
                            Student(
                                id, appinfoIt.id, appinfoIt.AdingName, appinfoIt.selected
                            )
                        )
                    } else if (itJson.id === 11) {
                        alils.add(Student(id, appinfoIt.id, appinfoIt.AdingName, false))
                    }


                }
                /**    minItemBean.forEach { appinfoIt ->
                if (StrngIt.equals(appinfoIt.appName.toString())) {
                Log.e("PeriodicSync","StrngIt-->"+StrngIt +"appinfoIt.appName--->"+appinfoIt.appName+"appinfoIt.icselect--->"+appinfoIt.icselect)
                alils.add(Student(id, appinfoIt.appName.toString(), appinfoIt.icselect))
                }else if(StrngIt.equals("耳机")){
                alils.add(Student(id, "耳机", false))
                }
                }*/
                id++
            }
            StudentDataBase(this).studentDao?.insertAll(alils)

        }).start()
    }
}