package com.glorystar.startool.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.constant.Constant
import com.glorystar.startool.config.constant.Constantdtas
import com.glorystar.startool.config.constant.Constantdtas.setTime
import com.glorystar.startool.config.constant.StudentDataBase
import com.glorystar.startool.ui.adapter.BaseRecyclerAdapter
import com.glorystar.startool.ui.adapter.BaseRecyclerHolder
import com.glorystar.startool.utils.db.Ageing
import com.glorystar.startool.utils.db.AgeingDao
import com.glorystar.startool.utils.db.AppDataBase
import com.glorystar.startool.utils.tool.DateUtil

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.logging.Logger

//老化测试报告

class AgeingTestReportActivity : BaseActivity() {


    lateinit var recyclerview_vido: RecyclerView
    lateinit var Serial_number6: TextView
    lateinit var Serial_number7: TextView
    lateinit var Serial_number: TextView
    lateinit var finish_buton: Button

    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, AgeingTestReportActivity::class.java)
            context.startActivity(intent)
            context.finish()
        }
    }

    var ado: AgeingDao? = null
    var baserecycleradapter: BaseRecyclerAdapter<Ageing>? = null
    override fun getLayoutId(): Int = R.layout.activity_ageing_test_report

    override fun initView(savedInstanceState: Bundle?) {
        GlobalScope.launch {

            recyclerview_vido = findViewById(R.id.recyclerview_vido)
            Serial_number6 = findViewById(R.id.Serial_number6)
            Serial_number7 = findViewById(R.id.Serial_number7)
            Serial_number = findViewById(R.id.Serial_number)
            finish_buton = findViewById(R.id.finish_buton)

            ado = AppDataBase.getDatabase(this@AgeingTestReportActivity).getAgeingDao()

            baserecycleradapter = object : BaseRecyclerAdapter<Ageing>(
                this@AgeingTestReportActivity,
                ado?.getAllAgeing(),
                R.layout.terdao_imet
            ) {
                override fun convert(
                    holder: BaseRecyclerHolder?,
                    data: Ageing?,
                    position: Int,
                    isScrolling: Boolean,
                    selectedPosition: Int
                ) {
                    val image_name = holder?.getView<ImageView>(R.id.image_name)
                    val name_text = holder?.getView<TextView>(R.id.name_text)

                    name_text?.text = when (data?.AgeingName) {
                        "CPUKtTestFragment" -> "CPU Test"
                        "MemoryTestKtFragment" -> "EMMC Test:"
                        "DDRTestKtFragment" -> "DDR Test:"
                        "VideoTestKtFragment" -> "Video Test:"
                        else -> "Timing Switcher ："
                    }
                    if (data?.AgeingType == "1") {
                        image_name?.setImageResource(R.drawable.ic_checkcircle__)
                    } else {
                        image_name?.setImageResource(R.drawable.ic_gantanhao)

                    }

                }

            }
            recyclerview_vido.adapter = baserecycleradapter

            Serial_number6.text = DateUtil.stampToTime(DateUtil.timeStamp.toString())
            Serial_number7.text = ado?.getAgeingID(0)?.AgeingStarTime
            Serial_number.text = ado?.getAgeingID(3)?.AgeungStopTime

            GlobalScope.launch {
                var stringbuilder: StringBuilder = StringBuilder()
                ado?.getAllAgeing()?.forEach { adoIt ->

                    val TeacherType = when (adoIt.AgeingType) {
                        "1" -> "_通过"
                        "2" -> "_失败"
                        else -> ""
                    }
                    val name = when (adoIt?.AgeingName) {
                        "CPUKtTestFragment" -> "CPU 测试_老化时间_ ${
                            Constantdtas.setTime(
                                Constantdtas.CPUKTTESTFRAGMENT.toLong()
                            )
                        }_"

                        "MemoryTestKtFragment" -> "EMMC 测试_老化时间_${
                            Constantdtas.setTime(
                                Constantdtas.MEMORYTESTKTFRAGMENT.toLong()
                            )
                        }_"

                        "DDRTestKtFragment" -> "DDR 测试_老化时间 ${
                            Constantdtas.setTime(
                                Constantdtas.DDRTESTKTFRAGMENT.toLong()
                            )
                        }_"

                        "VideoTestKtFragment" -> "Video 测试:_老化时间_ ${
                            Constantdtas.setTime(
                                Constantdtas.VIDEOTESTKTFRAGMENT.toLong()
                            )
                        }_"

                        else -> "定时开关机_老化时间_${Constantdtas.setTime(300000)}_"
                    }
                    stringbuilder?.append(adoIt.AgeingStarTime + "_" + adoIt.AgeungStopTime + "_" + name + TeacherType)
                    stringbuilder?.append("\n")

                }
                Constantdtas.writeSDFile(stringbuilder.toString(),"老化测试报告")

                ado?.deleteAll()
            }
            finish_buton.setOnClickListener {
                finish()
            }

        }

    }


}