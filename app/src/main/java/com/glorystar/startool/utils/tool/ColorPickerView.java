package com.glorystar.startool.utils.tool;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.glorystar.startool.R;

public class ColorPickerView extends View {
    private static final float BASE_RADIO = 1.0f;
    private Context mContext;
    private int mBigCircle;
    private int mRudeRadius;
    private int whiteWheelRadius;
    private int mCenterColor;
    private int mRudeStrokeWidth = 5;
    private Bitmap mBitmapBack;
    private Paint mPaint; //
    private Paint mCenterPaint;
    private Paint whiteWheelPaint;
    private Point mCenterPoint;
    private Point mRockPosition;
    private OnColorChangedListener mListener;
    private double length;
    private double mHue = 0.0;
    private double mBrightness = 1.0;
    private double mSaturation = 1.0;
    private float[] mHSV = new float[3];

    public ColorPickerView(Context context) {
        super(context);
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs);
    }

    /**
     * @param attrs
     */
    private void init(AttributeSet attrs) {
        TypedArray types = mContext.obtainStyledAttributes(attrs,
                R.styleable.color_picker);
        try {
            mBigCircle = types.getDimensionPixelOffset(
                    R.styleable.color_picker_circle_radius, 100);
            mRudeRadius = types.getDimensionPixelOffset(
                    R.styleable.color_picker_center_radius, 10);
            mCenterColor = types.getColor(R.styleable.color_picker_center_color,
                    Color.WHITE);
            whiteWheelRadius = types.getDimensionPixelOffset(R.styleable.color_picker_wheel_radius,102);
        } finally {
            types.recycle();
        }

        //mBitmapBack = BitmapFactory.decodeResource(getResources(),
        //       R.drawable.hsb_circle_hard);
        //mBitmapBack = Bitmap.createScaledBitmap(mBitmapBack, mBigCircle * 2,
        //       mBigCircle * 2, false);



        mCenterPoint = new Point(whiteWheelRadius, whiteWheelRadius);
        mRockPosition = new Point(mCenterPoint);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mBitmapBack = createColorWheelBitmap(whiteWheelRadius * 2, whiteWheelRadius * 2);
        mCenterPaint = new Paint();
        mCenterPaint.setColor(mCenterColor);
        mCenterPaint.setStrokeWidth(mRudeStrokeWidth);
        mCenterPaint.setStyle(Paint.Style.STROKE);

        whiteWheelPaint = new Paint();
        whiteWheelPaint.setColor(Color.WHITE);
        whiteWheelPaint.setAntiAlias(true);
    }


    private Bitmap createColorWheelBitmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int colorCount = 12;
        int colorAngleStep = 360 / colorCount;
        int colors[] = new int[colorCount + 1];
        float hsv[] = new float[]{0f, 1f, 1f};
        for (int i = 0; i < colors.length; i++) {
            hsv[0] = 360 - (i * colorAngleStep) % 360;
            colors[i] = Color.HSVToColor(hsv);
        }
        colors[colorCount] = colors[0];
        SweepGradient sweepGradient = new SweepGradient(width / 2, height / 2, colors, null);
        RadialGradient radialGradient = new RadialGradient(width / 2, height / 2, mBigCircle, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
        ComposeShader composeShader = new ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.SRC_OVER);
        mPaint.setShader(composeShader);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(width / 2, height / 2, mBigCircle, mPaint);
        return bitmap;
    }

    private void drawWhiteWheel(Canvas canvas) {
        //绘制白色圆圈
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, whiteWheelRadius, whiteWheelPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //外白圆
        drawWhiteWheel(canvas);
        // 画背景图片
        canvas.drawBitmap(mBitmapBack, 0, 0, mPaint);
        // 画中心小球
        canvas.drawCircle(mRockPosition.x, mRockPosition.y, mRudeRadius,
                mCenterPaint);
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this.mListener = listener;
    }

    public interface OnColorChangedListener {
        void onColorChange(float[] hsb);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_MOVE:
                length = getLength(event.getX(), event.getY(), mCenterPoint.x,
                        mCenterPoint.y);
                if (length <= mBigCircle - mRudeRadius) {
                    mRockPosition.set((int) event.getX(), (int) event.getY());
                } else {
                    mRockPosition = getBorderPoint(mCenterPoint, new Point(
                            (int) event.getX(), (int) event.getY()), (mBigCircle - mRudeRadius - 5));
                }

                float cX = mCenterPoint.x;
                float cY = mCenterPoint.y;
                float pX = event.getX();
                float pY = event.getY();

                mHue = getHue(cX, cY, pX, pY);
                if (mHue < 0)
                    mHue += 360.0;

                double deltaX = Math.abs(cX - pX), deltaY = (cY - pY);
                mSaturation = Math.pow(deltaX * deltaX + deltaY * deltaY, 0.5) / mBigCircle * BASE_RADIO;

                if (mSaturation <= 0) mSaturation = 0;
                if (mSaturation >= 1.0) mSaturation = 1.0;
                break;
            default:
                break;
        }

        final double hue = mHue, // 360.0,
                sat = mSaturation,
                brt = mBrightness;
        mHSV[0] = (float) hue;
        mHSV[1] = (float) sat;
        mHSV[2] = (float) brt;

        mListener.onColorChange(mHSV);
        whiteWheelPaint.setColor(Color.HSVToColor(mHSV));
        invalidate();
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(whiteWheelRadius * 2, whiteWheelRadius * 2);
    }

    /**
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private double getLength(float x1, float y1, float x2, float y2) {
        return  Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /**
     * @param a
     * @param b
     * @param cutRadius
     * @return
     */
    private Point getBorderPoint(Point a, Point b, int cutRadius) {
        float radian = getRadian(a, b);
        return new Point(a.x + (int) (cutRadius * Math.cos(radian)), a.x
                + (int) (cutRadius * Math.sin(radian)));
    }

    /**
     * @param a
     * @param b
     * @return
     */
    private float getRadian(Point a, Point b) {
        float lenA = b.x - a.x;
        float lenB = b.y - a.y;
        float lenC = (float) Math.sqrt(lenA * lenA + lenB * lenB);
        float ang = (float) Math.acos(lenA / lenC);
        ang = ang * (b.y < a.y ? -1 : 1);
        return ang;
    }

    private double getHue(float centerX, float centerY, float rockX, float rockY) {
        double hue = 0.0;
        double deltaA = Math.abs(rockX - centerX);
        double deltaB = Math.abs(rockY - centerY);
        double deltaC = getLength(centerX, centerY, rockX, rockY);

        if(centerX == rockX && centerY == rockY){
            return 0;
        }

        if (centerX == rockX) {
            if (centerY > rockY) {
                hue = 90;
            } else {
                hue = 270;
            }
            return hue;
        }

        if (centerY == rockY) {
            if (centerX > rockX) {
                hue = 180;
            } else {
                hue = 0;
            }
            return hue;
        }

        if (rockX > centerX && centerY > rockY) {
            hue = Math.asin(deltaB / deltaC) * 180 / Math.PI;
        } else if (rockX < centerX && rockY < centerY) {
            hue = Math.asin(deltaA / deltaC) * 180 / Math.PI + 90;
        } else if (rockX < centerX && rockY > centerY) {
            hue = Math.asin(deltaB / deltaC) * 180 / Math.PI + 180;
        } else if (rockX > centerX && rockY > centerY) {
            hue = Math.asin(deltaA / deltaC) * 180 / Math.PI + 270;
        }
        return hue;
    }

}
