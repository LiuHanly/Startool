package com.glorystar.startool.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
* @className:FullScreenVideoView
* @author jinyuan
* @desc 全屏播放视图
*/
public class FullScreenVideoViewUtil extends VideoView {
private int videoWidth;
private int videoHeight;

public FullScreenVideoViewUtil(Context context) {
    super(context);
}

public FullScreenVideoViewUtil(Context context, AttributeSet attrs) {
    super(context, attrs);
}

public FullScreenVideoViewUtil(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
}

@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width = getDefaultSize(0, widthMeasureSpec);
    int height = getDefaultSize(0, heightMeasureSpec);
    setMeasuredDimension(width, height);
}

public int getVideoWidth() {
    return videoWidth;
}

public void setVideoWidth(int videoWidth) {
    this.videoWidth = videoWidth;
}

public int getVideoHeight() {
    return videoHeight;
}

public void setVideoHeight(int videoHeight) {
    this.videoHeight = videoHeight;
}

public void setDimensions(int height, int width) {
    videoHeight = height;
    videoWidth = width;
}



}
