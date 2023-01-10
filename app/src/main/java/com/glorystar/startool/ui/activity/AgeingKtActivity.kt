package com.glorystar.startool.ui.activity

import android.annotation.SuppressLint
import android.content.ClipData.Item
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.constant.AgeingDataBase
import com.glorystar.startool.config.constant.Constantdtas
import com.glorystar.startool.utils.db.Ageing
import com.glorystar.startool.utils.tool.DateUtil
import com.glorystar.startool.utils.tool.LiveDataBus
import com.glorystar.startool.utils.tool.StatusNaviBarUitl

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

//老化测试
class AgeingKtActivity : BaseActivity() {
    var adl: MutableList<Ageing> = arrayListOf()
    val addFragment: MutableList<String> = arrayListOf(
        "CPUKtTestFragment",
        "MemoryTestKtFragment",
        "DDRTestKtFragment",
        "VideoTestKtFragment",

    )
    val addTimber: MutableList<String> = arrayListOf(
        Constantdtas.CPUKTTESTFRAGMENT,
        Constantdtas.MEMORYTESTKTFRAGMENT,
        Constantdtas.DDRTESTKTFRAGMENT,
        Constantdtas.VIDEOTESTKTFRAGMENT,

    )

    var stTime = ""

    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Context) {
            val intent = Intent(context, AgeingKtActivity::class.java)
            context.startActivity(intent)
        }
    }
    override fun getLayoutId(): Int = R.layout.ageingkt_activity
    override fun onResume() {
        super.onResume()
        stTime = DateUtil.stampToTime(DateUtil.timeStamp.toString())!!
        adl = AgeingDataBase.getInstance(this).ageingDao?.getAllAgeing()!!
        if (adl.isEmpty()) {
            var ageingID = 0
            addFragment.forEach {
                Log.e("AgeingKtActivity", "addTimber--->" + addTimber[ageingID])
                adl.add(Ageing(ageingID, it, stTime, stTime, "2", addTimber[ageingID]))
                ageingID++
            }
            AgeingDataBase.getInstance(this).ageingDao?.insertAll(adl)
            appData()
        } else {
            appData()
        }
    }






    @SuppressLint("LogNotTimber")
    override fun initView(savedInstanceState: Bundle?) {
        StatusNaviBarUitl.show(this@AgeingKtActivity)
        LiveDataBus.get().with("AgeingKtActivity", String::class.java).observe(this, Observer {
            val name = it.split(":")
            when (name[0]) {
                "CPUKtTestFragment" -> {
                    if (name[1] == "0") {
                        AgeingDataBase.getInstance(this).ageingDao?.update(
                            Ageing(
                                0,
                                name[0],
                                AgeingDataBase.getInstance(this).ageingDao.getAgeingID(0).AgeingStarTime,
                                DateUtil.stampToTime(DateUtil.timeStamp.toString()),
                                "1",
                                name[1]
                            )
                        )
                        val AgeingRemaining =
                            AgeingDataBase.getInstance(this).ageingDao?.getAgeingID(1)?.AgeingRemaining
                        LiveDataBus.get().with("MemoryTestKtFragment")
                            .postValue("start:$AgeingRemaining")

                    }
                }
                "MemoryTestKtFragment" -> {
                    if (name[1] == "0") {

                        AgeingDataBase.getInstance(this).ageingDao?.update(
                            Ageing(
                                1,
                                name[0],
                                AgeingDataBase.getInstance(this).ageingDao.getAgeingID(0).AgeungStopTime,
                                DateUtil.stampToTime(DateUtil.timeStamp.toString()),
                                "1",
                                name[1]
                            )
                        )
                        val AgeingRemaining =
                            AgeingDataBase.getInstance(this).ageingDao?.getAgeingID(2)?.AgeingRemaining
                        LiveDataBus.get().with("DDRTestKtFragment")
                            .postValue("start:$AgeingRemaining")


                    }

                }
                "DDRTestKtFragment" -> {
                    if (name[1] == "0") {


                        AgeingDataBase.getInstance(this).ageingDao?.update(
                            Ageing(
                                2,
                                name[0],
                                AgeingDataBase.getInstance(this).ageingDao.getAgeingID(1).AgeungStopTime,
                                DateUtil.stampToTime(DateUtil.timeStamp.toString()),
                                "1",
                                name[1]
                            )
                        )
                        val AgeingRemaining =
                            AgeingDataBase.getInstance(this).ageingDao?.getAgeingID(3)?.AgeingRemaining
                        LiveDataBus.get().with("VideoTestKtFragment")
                            .postValue("start:$AgeingRemaining")


                    }

                }
                "VideoTestKtFragment" -> {
                    if (name[1] == "0") {

                        AgeingDataBase.getInstance(this).ageingDao?.update(
                            Ageing(
                                3,
                                name[0],
                                AgeingDataBase.getInstance(this).ageingDao.getAgeingID(2).AgeungStopTime,
                                DateUtil.stampToTime(DateUtil.timeStamp.toString()),
                                "1",
                                name[1]
                            )
                        )
                    //   ShutdownActivity.toStartActivity(this@AgeingKtActivity)

                    }
                }
            }
        })
    }

    @SuppressLint("LogNotTimber")
    fun appData() {
        AgeingDataBase.getInstance(this).ageingDao?.getAllAgeing()?.forEach {
            Log.e(
                "AgeingKtActivity",
                "AgeingID--->" + it.AgeingID +
                        "AgeingName--->" + it.AgeingName +
                        "it.AgeingType---->" + it.AgeingType +
                        "-->" + it.AgeingRemaining
            )
            if (it.AgeingType.equals("2")) {
                val AgeingRemaining = it.AgeingRemaining
                if (it.AgeingName == "ShutdownActivity") {
                //    ShutdownActivity.toStartActivity(this@AgeingKtActivity)
                } else {
                    LiveDataBus.get().with(it.AgeingName).postValue("start:$AgeingRemaining")
                }

                return
            } else if (it.AgeingName == "ShutdownActivity" && it.AgeingType == "1") {
                AgeingTestReportActivity.toStartActivity(this@AgeingKtActivity)
            } else {
                LiveDataBus.get().with(it.AgeingName).postValue("stop")
            }


        }
    }


}