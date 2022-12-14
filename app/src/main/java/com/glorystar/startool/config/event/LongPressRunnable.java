package com.glorystar.startool.config.event;

import android.util.Log;

import com.glorystar.startool.utils.tool.LiveDataBus;


public class LongPressRunnable implements Runnable {
    private int x, y;

    public void setPressLocation(float x, float y) {
        this.x = (int) x;
        this.y = (int) y;
    }

    @Override
    public void run() {
        Log.i("LongPressRunnable","发生长按事件，位置在：" + x + "、" + y);
        LiveDataBus.get().with("DrawingView").postValue(true);
    }
}
