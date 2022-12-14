package com.glorystar.startool.config.constant;

import android.content.Context;

import com.glorystar.startool.utils.db.AppDataBase;
import com.glorystar.startool.utils.db.TeacherDao;

public class TeacherDataBase {
    private static TeacherDataBase instance;
    private TeacherDao teacherDao;

    public TeacherDataBase(Context context) {
        teacherDao = AppDataBase.Companion.getDatabase(context).getTeacherDao();
    }

    public static TeacherDataBase getInstance(Context context) {
        if (instance == null) {
            synchronized (TeacherDataBase.class) {
                if (instance == null) {
                    instance = new TeacherDataBase(context);
                }
            }
        }
        return instance;
    }

    public TeacherDao getTeacherDao() {
        return teacherDao;
    }

}

