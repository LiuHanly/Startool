package com.glorystar.startool.utils.tool;

import android.view.MotionEvent;
import android.view.View;

/**
 * @author android_wei
 */
public class OnDoubleClickListener implements View.OnTouchListener{
    private int count = 0;
    private long firstClick = 0;
    private long secondClick = 0;
    private final int totalTime = 1000;
    private DoubleClickCallback mCallback;


    public interface DoubleClickCallback {
        void onDoubleClick();
    }
    public OnDoubleClickListener(DoubleClickCallback callback) {
        super();
        this.mCallback = callback;
    }

    /**
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
                firstClick = System.currentTimeMillis();
            } else if (4 == count) {
                secondClick = System.currentTimeMillis();
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
