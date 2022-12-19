package com.glorystar.startool.config.constant;

import android.content.Context;

import com.glorystar.startool.utils.db.AgeingDao;
import com.glorystar.startool.utils.db.AppDataBase;


public class AgeingDataBase {
    private static AgeingDataBase instance;
    private AgeingDao studentDao;

    public AgeingDataBase(Context context) {
        studentDao =  AppDataBase.Companion.getDatabase(context).getAgeingDao();
    }

    public static AgeingDataBase getInstance(Context context){
        if (instance == null) {
            synchronized (AgeingDataBase.class) {
                if (instance == null) {
                    instance = new AgeingDataBase(context);
                }
            }
        }
        return instance;
    }

    public AgeingDao getAgeingDao() {
        return studentDao;
    }
}
