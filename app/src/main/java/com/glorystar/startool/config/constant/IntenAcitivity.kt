package com.glorystar.startool.config.constant

import com.glorystar.startool.config.constant.Constantdtas.getInString

import com.glorystar.startool.utils.tool.DateUtil.stampToTime
import com.glorystar.startool.utils.tool.DateUtil.timeStamp_L

import com.glorystar.startool.config.constant.Constantdtas.geiActivity

import com.glorystar.startool.ui.activity.TestReportActivity.Companion.toStartActivity

import kotlin.jvm.Synchronized
import android.app.Activity
import android.util.Log
import com.glorystar.startool.config.constant.Constantdtas
import com.glorystar.startool.utils.db.TeacherDao
import com.glorystar.startool.config.constant.TeacherDataBase
import com.glorystar.startool.utils.db.Student
import com.glorystar.startool.config.constant.StudentDataBase
import com.glorystar.startool.utils.tool.DateUtil
import com.glorystar.startool.utils.db.Teacher
import com.glorystar.startool.ui.activity.TestReportActivity

object IntenAcitivity {
    @Synchronized
    fun getIntenAcitivity(context: Activity?, testID: Int, transit: Int) {
        var ID = testID
        Log.e("PeriodicSync", "--->$testID")
        val lisn = getInString(context!!)
        val teacherDao = TeacherDataBase(context).teacherDao
        val ListStudent: List<Student> = StudentDataBase(context).studentDao.getAllStudents()
        val statrDate = stampToTime(timeStamp_L.toString() + "")
        Log.e("PeriodicSync", "--lisn.get(testID)-->" + lisn[testID])
        if (teacherDao.getTeacherID(testID.toLong()) != null) {
            teacherDao.update(
                Teacher(
                    java.lang.Long.valueOf(testID.toLong()),
                    lisn[testID],
                    statrDate,
                    statrDate,
                    transit
                )
            )
        } else {
            teacherDao.insert(
                Teacher(
                    java.lang.Long.valueOf(testID.toLong()),
                    lisn[testID],
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
                Log.e("PeriodicSync", ListStudent[ID].toString())
                geiActivity(context, ListStudent[ID].appinfoID!!)
                return
            } else if (ListStudent[ID].StudentName!!.contains("Buttons") && !ListStudent[ID].StudentType) {
                Log.e("PeriodicSync", "ID-->" + ID + "-StudentName-->" + ListStudent[ID])
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
        Log.e("getIntenAcitivity", lisn[lisn.size - 1])
        Log.e("getIntenAcitivity", "" + teacherDao.getTeacherID(13))
        if (teacherDao.getTeacherID(14) != null) {

            teacherDao.update(Teacher(14L, lisn[lisn.size - 1], statrDate, statrDate, transit));
            teacherDao.getAllTeachers().forEach {
                Log.e("getIntenAcitivity", "--->" + it.teacherName + "--->" + it.TeacherID+"----"+it.TeacherType)
            }

        } else {
            teacherDao.getAllTeachers()
            Log.e("getIntenAcitivity", "--->" + teacherDao.getAllTeachers().size)

            //  new Teacher(14L, lisn.get(lisn.size() - 1), statrDate, statrDate, transit).
            teacherDao.insert(Teacher(14L, lisn[lisn.size - 1], statrDate, statrDate, transit));

            teacherDao.getAllTeachers().forEach {
                Log.e("getIntenAcitivity", "--->" + it.teacherName + "--->" + it.TeacherID+"----"+it.TeacherType)
            }

        }
        toStartActivity(context)
    }
}