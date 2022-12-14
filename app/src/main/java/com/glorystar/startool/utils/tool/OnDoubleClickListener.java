package com.glorystar.startool.utils.tool;

import android.view.MotionEvent;
import android.view.View;

/**
 * @author android_wei
 */
public class OnDoubleClickListener implements View.OnTouchListener{
    //点击次数
    private int count = 0;
    //第一次点击时间
    private long firstClick = 0;
    //第二次点击时间
    private long secondClick = 0;
    /**
     * 点击时间间隔，单位毫秒
     */
    private final int totalTime = 1000;
    /**
     * 自定义回调接口
     */
    private DoubleClickCallback mCallback;


    public interface DoubleClickCallback {
        void onDoubleClick();
    }
    public OnDoubleClickListener(DoubleClickCallback callback) {
        super();
        this.mCallback = callback;
    }

    /**
     * 触摸事件处理
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //按下
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            count++;
            if (1 == count) {
                //记录第一次点击时间
                firstClick = System.currentTimeMillis();
            } else if (4 == count) {
                //记录第二次点击时间
                secondClick = System.currentTimeMillis();
                //判断二次点击时间间隔是否在设定的间隔时间之内
                if (secondClick - firstClick < totalTime) {
                    if (mCallback != null) {
                        mCallback.onDoubleClick();
                    }
                    count = 0;
                    firstClick = 0;
                } else {
                    firstClick = secondClick;
                    count = 1;
                }
                secondClick = 0;
            }
        }

        return true;
    }
}
