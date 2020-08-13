package com.rhino.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.rhino.ui.R;


/**
 * <p>The custom view to select the letter.</p>
 *
 * @author LuoLin
 * @since Created on 2017/8/18.
 **/
public class WaveView extends View {

    private static final int MSG_WHAT_START_WAVE = 9527;

    private static final int DEFAULT_WAVE_MARGIN_TOP = 50;
    private static final int DEFAULT_STROKE_WIDTH = 0;
    private static final int DEFAULT_STROKE_COLOR = 0x88000000;
    private static final int DEFAULT_CENTER_BACKGROUND_COLOR = 0;
    private static final int DEFAULT_WAVE_COLOR = 0x88000000;
    private int mStrokeWidth = DEFAULT_STROKE_WIDTH;
    private int mStrokeColor = DEFAULT_STROKE_COLOR;
    private int waveColor = DEFAULT_WAVE_COLOR;
    private int mCenterBackgroundColor = DEFAULT_CENTER_BACKGROUND_COLOR;

    private int mViewWidth;
    private int mViewHeight;
    private int mViewSize;
    private Paint mPaint;
    private float angle = 0;
    private float mWaveRadian = 20;
    private float mWaveSpeed = 1;
    private float mWaveMarginTop = -1;

    private boolean isStart = false;

    private RectF mCornerRectF;
    private Path mCornerPath;
    private float[] mCornerArray;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_WHAT_START_WAVE) {
                if (!isWaveStart()) {
                    return;
                }
                invalidate();
                angle += mWaveSpeed;
                if (angle == 360) {
                    angle = 0;
                }
                handler.sendEmptyMessageDelayed(MSG_WHAT_START_WAVE, 10);
            }
        }
    };

    public WaveView(Context context) {
        super(context);
        init(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
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
        clipCorner(canvas);
        super.onDraw(canvas);
        drawCenterBackground(canvas);
        drawWave(canvas);
        drawStroke(canvas);
    }

    private void init(Context context, AttributeSet attrs) {
        if (null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
            mWaveMarginTop = typedArray.getDimensionPixelSize(R.styleable.WaveView_vv_wave_margin_top,
                    DEFAULT_WAVE_MARGIN_TOP);
            mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.WaveView_vv_stroke_width,
                    DEFAULT_STROKE_WIDTH);
            mStrokeColor = typedArray.getColor(R.styleable.WaveView_vv_stroke_color,
                    DEFAULT_STROKE_COLOR);
            waveColor = typedArray.getColor(R.styleable.WaveView_vv_wave_color,
                    DEFAULT_WAVE_COLOR);
            mCenterBackgroundColor = typedArray.getColor(R.styleable.WaveView_vv_center_background_color,
                    DEFAULT_CENTER_BACKGROUND_COLOR);
            float leftTop = typedArray.getDimension(R.styleable.WaveView_vv_corner_left_top, 0);
            float rightTop = typedArray.getDimension(R.styleable.WaveView_vv_corner_right_top, 0);
            float rightBottom = typedArray.getDimension(R.styleable.WaveView_vv_corner_right_bottom, 0);
            float leftBottom = typedArray.getDimension(R.styleable.WaveView_vv_corner_left_bottom, 0);
            mCornerArray = new float[]{leftTop, leftTop, rightTop, rightTop, rightBottom,
                    rightBottom, leftBottom, leftBottom};
            typedArray.recycle();
        }
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mCornerPath = new Path();
        mCornerRectF = new RectF();
    }

    /**
     * Init view
     */
    private void initView(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        mViewSize = width > height ? width : height;
    }

    /**
     * Draw wave
     */
    private void drawWave(Canvas canvas) {
        canvas.save();
        int lineX, lineY;
        mPaint.setColor(waveColor);
        for (int i = -mViewSize; i < mViewSize; i++) {
            lineX = i;
            lineY = (int) (mWaveRadian * Math.sin(Math.toRadians(i + angle)));
            canvas.drawLine(lineX, lineY + mWaveMarginTop, lineX, mViewHeight, mPaint);
        }
        canvas.restore();
    }

    /**
     * Draw stroke.
     *
     * @param canvas Canvas
     */
    private void drawStroke(Canvas canvas) {
        if (mStrokeWidth <= 0) {
            return;
        }
        canvas.save();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(mStrokeColor);
        canvas.drawPath(mCornerPath, mPaint);
        canvas.restore();
    }

    /**
     * Draw center background.
     *
     * @param canvas Canvas
     */
    private void drawCenterBackground(Canvas canvas) {
        if (mCenterBackgroundColor == 0) {
            return;
        }
        canvas.save();
        mPaint.setColor(mCenterBackgroundColor);
        canvas.drawPath(mCornerPath, mPaint);
        canvas.restore();
    }

    /**
     * Clip corner.
     *
     * @param canvas Canvas
     */
    private void clipCorner(Canvas canvas) {
        if (mCornerArray == null) {
            return;
        }
        if (mCornerRectF.isEmpty()) {
            mCornerRectF.set(0, 0, mViewWidth, mViewHeight);
        }
        if (mCornerPath.isEmpty()) {
            mCornerPath.addRoundRect(mCornerRectF, mCornerArray, Path.Direction.CW);
        }
        canvas.clipPath(mCornerPath);
    }

    /**
     * Set the corner.
     *
     * @param leftTop     the corner on left top
     * @param rightTop    the corner on right top
     * @param rightBottom the corner on right bottom
     * @param leftBottom  the corner on left bottom
     */
    public void setCorner(float leftTop, float rightTop, float rightBottom, float leftBottom) {
        mCornerArray = new float[]{leftTop, leftTop, rightTop, rightTop, rightBottom,
                rightBottom, leftBottom, leftBottom};
        invalidate();
    }

    /**
     * Return true when wave started
     **/
    public boolean isWaveStart() {
        return isStart;
    }

    /**
     * Start wave
     **/
    public void startWave() {
        if (isStart) {
            return;
        }
        this.isStart = true;
        handler.sendEmptyMessage(MSG_WHAT_START_WAVE);
    }

    /**
     * Stop wave
     **/
    public void stopWave() {
        this.isStart = false;
    }

    /**
     * Set wave speed
     **/
    public void setWaveSpace(float space) {
        this.mWaveSpeed = space;
        invalidate();
    }

    /**
     * Set angle
     **/
    public void setAngle(float angle) {
        this.angle = angle;
        invalidate();
    }

    /**
     * Set wave radian
     **/
    public void setWaveRadian(float radian) {
        this.mWaveRadian = radian;
        invalidate();
    }

    /**
     * Set the top margin of wave
     */
    public void setWaveMarginTop(float mWaveMarginTop) {
        this.mWaveMarginTop = mWaveMarginTop;
        invalidate();
    }

    /**
     * Set wave color
     */
    public void setWaveColor(int color) {
        this.waveColor = color;
        invalidate();
    }

    /**
     * Set center background color
     */
    public void setCenterBackgroundColor(int color) {
        this.mCenterBackgroundColor = color;
        invalidate();
    }


}