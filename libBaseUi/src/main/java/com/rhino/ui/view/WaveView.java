package com.rhino.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;


/**
 * <p>The custom view to select the letter.</p>
 *
 * @author LuoLin
 * @since Created on 2017/8/18.
 **/
public class WaveView extends View {

    private static final int START_WAVE = 9527;

    /**
     * view的宽度
     **/
    private int mViewWidth;
    /**
     * view的高度
     **/
    private int mViewHeight;
    /**
     * paint
     **/
    private Paint mPaint;
    /**
     * 水流区域大小
     **/
    private int size;
    /**
     * 振幅
     **/
    private int angle = 0;
    /**
     * 波浪弧度
     **/
    private int radian = 20;
    /**
     * 水流速度
     **/
    private int space = 1;
    /**
     * 是否开始
     **/
    private boolean isStart = false;

    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = measureWidth(widthMeasureSpec);
        mViewHeight = measureHeight(heightMeasureSpec);

        setMeasuredDimension(mViewWidth, mViewHeight);

        initView(mViewWidth, mViewHeight);
    }

    private int measureWidth(int measureSpec) {
        int preferred = 0;
        return getMeasurement(measureSpec, preferred);
    }

    private int measureHeight(int measureSpec) {
        int preferred = 0;
        return getMeasurement(measureSpec, preferred);
    }

    private int getMeasurement(int measureSpec, int preferred) {
        int specSize = MeasureSpec.getSize(measureSpec);
        int measurement;
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.EXACTLY:
                measurement = specSize;
                break;
            case MeasureSpec.AT_MOST:
                measurement = Math.min(preferred, specSize);
                break;
            default:
                measurement = preferred;
                break;
        }
        return measurement;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mViewWidth / 2, mViewHeight / 2);
        drawWave(canvas);
        canvas.restore();
    }

    private void drawWave(Canvas canvas) {
        int lineX, lineY;
        // 根据正弦绘制波浪
        for (int i = -size; i < size; i++) {
            lineX = i;
            lineY = (int) (radian * Math.sin(Math.toRadians(i + angle)));
            canvas.drawLine(lineX, lineY, lineX, size / 2, mPaint);
        }
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.GRAY);
        mPaint.setAntiAlias(true);
    }

    private void initView(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        size = width > height ? width : height;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == START_WAVE) {
                if (!isWaveStart()) {
                    return;
                }
                invalidate();
                angle += space;
                if (angle == 360) {
                    angle = 0;
                }
                handler.sendEmptyMessageDelayed(START_WAVE, 10);
            }
        }
    };

    /**
     * 是否已开始动画
     **/
    public boolean isWaveStart() {
        return isStart;
    }

    /**
     * 开始动画
     **/
    public void startWave() {
        if (isStart) {
            return;
        }
        this.isStart = true;
        handler.sendEmptyMessage(START_WAVE);
    }

    /**
     * 停止动画
     **/
    public void stopWave() {
        this.isStart = false;
    }

    /**
     * 设置水流速度
     **/
    public void setSpace(int space) {
        this.space = space;
    }

    /**
     * 设置开始角度
     **/
    public void setAngle(int angle) {
        this.angle = angle;
    }

    /**
     * 设置波浪弧度
     **/
    public void setRadian(int radian) {
        this.radian = radian;
    }
}