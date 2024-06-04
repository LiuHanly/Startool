package com.glorystar.startool.config.constant

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.glorystar.startool.config.bean.AgingBean
import com.glorystar.startool.config.bean.liuchenData
import com.glorystar.startool.config.constant.Constantdtas.geiActivity
import com.glorystar.startool.config.constant.Constantdtas.getInString
import com.glorystar.startool.ui.activity.TestReportActivity.Companion.toStartActivity
import com.glorystar.startool.ui.testactivity.NewTestReportActivity
import com.glorystar.startool.utils.db.Student
import com.glorystar.startool.utils.db.Teacher
import com.glorystar.startool.utils.tool.DateUtil.stampToTime
import com.glorystar.startool.utils.tool.DateUtil.timeStamp_L
import com.glorystar.startool.utils.tool.JsonHelper


object IntenAcitivity {

    val minItemBean: MutableList<AgingBean> = ArrayList<AgingBean>()

    @Synchronized
    fun getIntenAcitivity(context: Activity?, testID: Int, transit: Int) {
        var ID = testID
        Log.e("PeriodicSync", "--->$testID")
        val lisn = getInString(context!!)
        val teacherDao = TeacherDataBase(context).teacherDao
        val ListStudent: List<Student> = StudentDataBase(context).studentDao.getAllStudents()
        //  val itJson = JsonHelper.newfromJson(StrngIt, liuchenData::class.java)
        val statrDate = stampToTime(timeStamp_L.toString() + "")

        Log.e("PeriodicSync", "--lisn.get(testID)-->" + lisn[testID])

        if (teacherDao.getTeacherID(testID.toLong()) != null) {
            teacherDao.update(
                Teacher(
                    java.lang.Long.valueOf(testID.toLong()),
                    JsonHelper.newfromJson(lisn[testID], liuchenData::class.java).name,
                    statrDate,
                    statrDate,
                    transit
                )
            )
        } else {
            teacherDao.insert(
                Teacher(
                    java.lang.Long.valueOf(testID.toLong()),
                    JsonHelper.newfromJson(lisn[testID], liuchenData::class.java).name,
                    statrDate,
                    statrDate,
                    transit
                )
            )
        }
        if (testID == 10) {
            return
        }
        while (ID < ListStudent.size) {
            Log.e(
                "PeriodicSync",
                "--PeriodicSync--->" + (ListStudent[ID].StudentID.toString() + ListStudent[ID].StudentName + "----" + ListStudent[ID].StudentType)
            )
            if (ListStudent[ID].StudentType) {
                geiActivity(context, ListStudent[ID].appinfoID!!)
                return
            } else if (ListStudent[ID].StudentID == 13 && !ListStudent[ID].StudentType) {
                toStartActivity(context)
            }
            ID++
        }
    }

    @SuppressLint("LogNotTimber")
    fun PeriodicSync(lines1: List<String>, context: Activity?) {
        context?.let { Constantdtas.getGreAgingDate(it) }?.let { minItemBean.addAll(it) }

        val alils: MutableList<Student> = arrayListOf()
        var id = 0
        StudentDataBase(context).studentDao.deleteAll()
        Thread(Runnable {
            lines1.forEach { StrngIt ->
                Log.e("PeriodicSync->", StrngIt)
                val itJson = JsonHelper.newfromJson(StrngIt, liuchenData::class.java)
                minItemBean.forEach { appinfoIt ->
                    Log.e(
                        "PeriodicSync",
                        "StrngIt-->" + itJson.id + "appinfoIt.appName--->" + appinfoIt.id + "---" + appinfoIt.AdingName + "---" + appinfoIt.selected

                    )
                    if (itJson.id == appinfoIt.id) {
                        alils.add(
                            Student(
                                id, appinfoIt.id, appinfoIt.AdingName, true
                            )
                        )
                    } else if (itJson.id === 11) {
                        alils.add(Student(id, appinfoIt.id, appinfoIt.AdingName, false))
                    }

                }
                id++
            }
            StudentDataBase(context).studentDao?.insertAll(alils)

        }).start()
    }

    fun getIntenAcitivity(context: Activity?, testID: Int, transit: Int, code: Int) {
        var ID = testID
        val lisn = getInString(context!!, 0)
        val teacherDao = TeacherDataBase(context).teacherDao
        val ListStudent: List<Student> = StudentDataBase(context).studentDao.getAllStudents()
        //  val itJson = JsonHelper.newfromJson(StrngIt, liuchenData::class.java)
        val statrDate = stampToTime(timeStamp_L.toString() + "")

        if (teacherDao.getTeacherID(testID.toLong()) != null) {
            teacherDao.update(
                Teacher(
                    java.lang.Long.valueOf(testID.toLong()),
                    JsonHelper.newfromJson(lisn[testID], liuchenData::class.java).name,
                    statrDate,
                    statrDate,
                    transit
                )
            )
        } else {
            teacherDao.insert(
                Teacher(
                    java.lang.Long.valueOf(testID.toLong()),
                    JsonHelper.newfromJson(lisn[testID], liuchenData::class.java).name,
                    statrDate,
                    statrDate,
                    transit
                )
            )
        }
        if (testID == 10) {
            return
        }


        while (ID < ListStudent.size) {
            if (ListStudent[ID].StudentType) {
                Log.e("IntenAcitivity", "-appinfoID-->${ListStudent[ID].appinfoID!!}")
                geiActivity(context, ListStudent[ID].appinfoID!!, code)
                return
            } else if (ListStudent[ID].StudentID == 13 && !ListStudent[ID].StudentType) {
                toStartActivity(context)
            }
            ID++
        }
    }

    @Synchronized
    fun getIntenAcitivity(context: Activity?, transit: Int) {
        val lisn = getInString(context!!)
        val teacherDao = TeacherDataBase(context).teacherDao
        val ListStudent: List<Student> = StudentDataBase(context).studentDao.getAllStudents()
        val statrDate = stampToTime(timeStamp_L.toString() + "")

        if (teacherDao.getTeacherID(14) != null) {

            teacherDao.update(
                Teacher(
                    14L,
                    JsonHelper.newfromJson(lisn[lisn.size - 1], liuchenData::class.java).name,
                    statrDate,
                    statrDate,
                    transit
                )
            );
            teacherDao.getAllTeachers().forEach {
            }
            //  JsonHelper.newfromJson(lisn[testID], liuchenData::class.java).name,
        } else {
            teacherDao.getAllTeachers()
            teacherDao.insert(
                Teacher(
                    14L,
                    JsonHelper.newfromJson(lisn[lisn.size - 1], liuchenData::class.java).name,
                    statrDate,
                    statrDate,
                    transit
                )
            );
            teacherDao.getAllTeachers().forEach {
            }

        }
        toStartActivity(context)
    }

    @Synchronized
    fun getIntenAcitivity(context: Activity?, transit: Int, code: String) {
        val lisn = getInString(context!!)
        val teacherDao = TeacherDataBase(context).teacherDao
        val ListStudent: List<Student> = StudentDataBase(context).studentDao.getAllStudents()
        val statrDate = stampToTime(timeStamp_L.toString() + "")

        if (teacherDao.getTeacherID(14) != null) {

            teacherDao.update(
                Teacher(
                    14L,
                    JsonHelper.newfromJson(lisn[lisn.size - 1], liuchenData::class.java).name,
                    statrDate,
                    statrDate,
                    transit
                )
            );
            teacherDao.getAllTeachers().forEach {
            }
            //  JsonHelper.newfromJson(lisn[testID], liuchenData::class.java).name,
        } else {
            teacherDao.getAllTeachers()
            teacherDao.insert(
                Teacher(
                    14L,
                    JsonHelper.newfromJson(lisn[lisn.size - 1], liuchenData::class.java).name,
                    statrDate,
                    statrDate,
                    transit
                )
            );
            teacherDao.getAllTeachers().forEach {
            }

        }
        NewTestReportActivity.toStartActivity(context)
    }


    fun performFactoryReset(context: Context) {
        // 设置一个特定的标志位来触发出厂设置
        Settings.Global.putInt(context.contentResolver, Settings.Global.DEVICE_PROVISIONED, 0)

        // 这里可以添加更多的清除数据的逻辑，例如删除应用的数据库或SharedPreferences
    }

}