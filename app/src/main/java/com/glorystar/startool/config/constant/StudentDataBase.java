package com.glorystar.startool.config.constant;

import android.content.Context;

import com.glorystar.startool.utils.db.AppDataBase;
import com.glorystar.startool.utils.db.StudentDao;


public class StudentDataBase {
    private static StudentDataBase instance;
    private StudentDao studentDao;

    public StudentDataBase(Context context) {
        studentDao =  AppDataBase.Companion.getDatabase(context).getStudentDao();
    }

    public static StudentDataBase getInstance(Context context){
        if (instance == null) {
            synchronized (StudentDataBase.class) {
                if (instance == null) {
                    instance = new StudentDataBase(context);
                }
            }
        }
        return instance;
    }

    public StudentDao getStudentDao() {
        return studentDao;
    }


}
