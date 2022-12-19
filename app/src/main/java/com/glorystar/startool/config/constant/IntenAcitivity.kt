package com.glorystar.startool.config.constant;

import android.app.Activity;
import android.util.Log;


import com.glorystar.startool.ui.activity.TestReportActivity;
import com.glorystar.startool.utils.db.Student;
import com.glorystar.startool.utils.db.Teacher;
import com.glorystar.startool.utils.db.TeacherDao;
import com.glorystar.startool.utils.tool.DateUtil;

import java.util.List;

public class IntenAcitivity {

    public synchronized static void getIntenAcitivity(Activity context, int testID, int transit) {
        int ID = testID;
        Log.e("PeriodicSync", "--->" + testID);
        List<String> lisn = Constantdtas.INSTANCE.getInString(context);
        TeacherDao teacherDao = new TeacherDataBase(context).getTeacherDao();
        List<Student> ListStudent = new StudentDataBase(context).getStudentDao().getAllStudents();

        String statrDate = DateUtil.INSTANCE.stampToTime(DateUtil.INSTANCE.getTimeStamp_L() + "");

        Log.e("PeriodicSync", lisn.get(testID));

        if (teacherDao.getTeacherID(testID) != null) {
            teacherDao.update(new Teacher(Long.valueOf(testID), lisn.get(testID), statrDate, statrDate, transit));
        } else {

            teacherDao.insert(new Teacher(Long.valueOf(testID), lisn.get(testID), statrDate, statrDate, transit));
        }
        if (testID == 10) {
            return;
        }

        while (ID < ListStudent.size()) {
            if (ListStudent.get(ID).getStudentType()) {
                Log.e("PeriodicSync", ListStudent.get(ID).toString());
                Constantdtas.INSTANCE.geiActivity(context, ListStudent.get(ID).getAppinfoID());
                return;
            } else if (ListStudent.get(ID).getStudentName().contains("Buttons") && !ListStudent.get(ID).getStudentType()) {
                Log.e("PeriodicSync", "ID-->" + ID + "-StudentName-->" + ListStudent.get(ID));
                TestReportActivity.Companion.toStartActivity(context);
            }
            ID++;
        }

    }

    public synchronized static void getIntenAcitivity(Activity context, int transit) {
        List<String> lisn = Constantdtas.INSTANCE.getInString(context);
        TeacherDao teacherDao = new TeacherDataBase(context).getTeacherDao();
        List<Student> ListStudent = new StudentDataBase(context).getStudentDao().getAllStudents();
        String statrDate = DateUtil.INSTANCE.stampToTime(DateUtil.INSTANCE.getTimeStamp_L() + "");
        if (teacherDao.getTeacherID(13) != null) {
            teacherDao.update(new Teacher(14L, lisn.get(lisn.size() - 1), statrDate, statrDate, transit));
        } else {
            teacherDao.insert(new Teacher(14L, lisn.get(lisn.size() - 1), statrDate, statrDate, transit));
        }
        TestReportActivity.Companion.toStartActivity(context);
    }


}
