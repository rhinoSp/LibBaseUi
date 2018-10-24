package com.rhino.ui.view.progress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

/**
 * @author rhino
 * @version 1.0
 * @Description 圆形进度条  带左右加减按钮
 * @date 2016年1月27日 下午1:28:08 
 */
public class CircleProgress extends View {

    private final float PI = 3.14f;
    private final float PI_ANGLE = 180f;
    private final float DEFAULT_START_ANGLE = 215.0f;
    private final float DEFAULT_SWEEP_ANGLE = 110.0f;
    private final float MAX_SWEEP_ANGLE = 310.0f;
    private final int DEFAULT_MIN_VALUE = 1;
    private final int DEFAULT_MAX_VALUE = 30;

    private final float DEFAULT_SEEKBAR_RADIUS = 250.0f;
    private final float DEFAULT_SEEKBAR_WIDTH = 10.0f;
    private final float DEFAULT_SLIDEPOINT_SIZE = 20.0f;
    private final float DEFAULT_SLIDEPOINTTEXT_SIZE = 8.0f;

    private final int DEFAULT_CIRCLESEEKBAR_BG_COLOR = Color.GRAY;
    private final int DEFAULT_SLIDEPOINT_INNER_COLOR = Color.parseColor("#bee8e5");
    private final int DEFAULT_SLIDEPOINT_OUTER_COLOR = Color.parseColor("#aaaaaa");
    private final int DEFAULT_SLIDEPOINT_TEXT_COLOR = Color.BLACK;

    private Context mContext;
    private OnChangeActionListener mOnChangeActionListener = null;
    private boolean isOnclick = true;

    private int currProgress = 0;
    private String UNIT = "°C";
    private float currSweepAngle = 0.0f;
    private float startAngle = DEFAULT_START_ANGLE;
    private float sweepAngle = DEFAULT_SWEEP_ANGLE;
    private int minProgress = DEFAULT_MIN_VALUE; // 最小值
    private int maxProgress = DEFAULT_MAX_VALUE; // 最大值

    private float mCircularSeekBarRadius = DEFAULT_SEEKBAR_RADIUS; // 进度条的大小
    private float mCircularSeekBarRingWidth = DEFAULT_SEEKBAR_WIDTH; // 进度条宽度
    private float slidePointSize = DEFAULT_SLIDEPOINT_SIZE; // 滑动块图片大小
    private float slidePointTextSize = DEFAULT_SLIDEPOINTTEXT_SIZE; // 滑动块文字大小
    private float adjustOffsetAngle; // 加、减按钮后调整用

    private int mCircleRingBgColor = DEFAULT_CIRCLESEEKBAR_BG_COLOR;
    private int mSlidePointInnerColor = DEFAULT_SLIDEPOINT_INNER_COLOR;
    private int mSlidePointOuterColor = DEFAULT_SLIDEPOINT_OUTER_COLOR;
    private int mSlidePointTextColor = DEFAULT_SLIDEPOINT_TEXT_COLOR;

    /**
     * 进度条圆环，背景
     */
    private Paint mCircleSeekBarRing;
    private Paint mCircleSeekBarBg;
    /**
     * 进度条滑动块 内圆，外圆，文字
     */
    private Paint slideInnerCircle;
    private Paint slideOuterCircle;
    private Paint slideCircleText;

    /**
     * 进度条滑动块 横坐标
     */
    private float slidePointX;
    private float slidePointY;

    private RectF mCircularSeekBarRect; // 进度条
    private RectF addBtnRect; // 加按钮
    private RectF subBtnRect; // 减按钮

    private Bitmap mAddBitmap; // 加按钮
    private Bitmap mSubBitmap; // 减按钮
    private boolean isSupportBtns = false;
    private boolean isClickOnSlidePoint = false;

    private static final int[] SECTION_COLORS = {Color.parseColor("#ff4800"),
            Color.parseColor("#ff0042"), Color.parseColor("#ff00f0"),
            Color.parseColor("#29b4ff"), Color.parseColor("#28edff"),
            Color.parseColor("#1ad966"), Color.parseColor("#00c212"),
            Color.parseColor("#1ad92c"), Color.parseColor("#77ff08"),
            Color.parseColor("#f0ff00"), Color.parseColor("#ff9600")};
    private static final float[] SECTION_COLORS_POSITIONS = new float[]{
            0.05f, 0.1f, 0.3f, 0.35f, 0.56f, 0.62f, 0.65f, 0.775f, 0.78f,
            0.85f, 1f};

    public CircleProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {

        mCircleSeekBarBg = new Paint();
        mCircleSeekBarBg.setColor(mCircleRingBgColor);
        mCircleSeekBarBg.setAntiAlias(true);
        mCircleSeekBarBg.setStyle(Paint.Style.STROKE);

        mCircleSeekBarRing = new Paint();
        mCircleSeekBarRing.setAntiAlias(true);
        mCircleSeekBarRing.setStyle(Paint.Style.STROKE);

        slideInnerCircle = new Paint();
        slideInnerCircle.setColor(mSlidePointInnerColor);
        slideInnerCircle.setAntiAlias(true);
        slideInnerCircle.setStyle(Paint.Style.FILL);

        slideOuterCircle = new Paint();
        slideOuterCircle.setColor(mSlidePointOuterColor);
        slideOuterCircle.setAntiAlias(true);
        slideOuterCircle.setStyle(Paint.Style.FILL);

        slideCircleText = new Paint();
        slideCircleText.setTypeface(Typeface.SANS_SERIF); // 设置字体
        slideCircleText.setColor(mSlidePointTextColor);

        mCircularSeekBarRect = new RectF();
        subBtnRect = new RectF();
        addBtnRect = new RectF();
    }

    /**
     * 计算出合适的高度和宽度值
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = getWidth();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = getHeight();
        }

        initRect(width, height);

        setMeasuredDimension(width, height);
    }

    private void initRect(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        float mWidth = width * 0.85f; // 得到view的宽度
        float mHeight = height * 0.85f;// 得到view的高度
        float viewSize = (mWidth > mHeight) ? mHeight : mWidth; // 取较小值

        mCircularSeekBarRadius = viewSize / 2;
        mCircularSeekBarRingWidth = mCircularSeekBarRadius * 0.012f;
        slidePointSize = (int) (mCircularSeekBarRadius * 0.12);
        slidePointTextSize = (int) (mCircularSeekBarRadius * 0.07);
        if (isSupportBtns) {
            adjustOffsetAngle = (float) (2 * Math.toDegrees(Math
                    .asin(slidePointSize / mCircularSeekBarRadius)));
        }

        mCircleSeekBarBg.setStrokeWidth(mCircularSeekBarRingWidth);
        mCircleSeekBarRing.setStrokeWidth(mCircularSeekBarRingWidth);
        slideCircleText.setTextSize(slidePointTextSize);

        mCircularSeekBarRect.left = -mCircularSeekBarRadius;
        mCircularSeekBarRect.right = mCircularSeekBarRadius;
        mCircularSeekBarRect.top = -mCircularSeekBarRadius;
        mCircularSeekBarRect.bottom = mCircularSeekBarRadius;

        subBtnRect.left = (float) (mCircularSeekBarRadius
                * Math.cos(Math.toRadians(startAngle - adjustOffsetAngle)) - slidePointSize);
        subBtnRect.top = (float) (mCircularSeekBarRadius
                * Math.sin(Math.toRadians(startAngle - adjustOffsetAngle)) - slidePointSize);
        subBtnRect.right = subBtnRect.left + slidePointSize * 2;
        subBtnRect.bottom = subBtnRect.top + slidePointSize * 2;

        addBtnRect.left = (float) (mCircularSeekBarRadius
                * Math.cos(Math.toRadians(startAngle + sweepAngle
                + adjustOffsetAngle)) - slidePointSize);
        addBtnRect.top = (float) (mCircularSeekBarRadius
                * Math.sin(Math.toRadians(startAngle + sweepAngle
                + adjustOffsetAngle)) - slidePointSize);
        addBtnRect.right = addBtnRect.left + slidePointSize * 2;
        addBtnRect.bottom = addBtnRect.top + slidePointSize * 2;

        setProgress(currProgress);
    }

    private void getSlidePointCoord(float currSweepAngle) {
        slidePointX = (float) (mCircularSeekBarRadius
                * Math.cos(Math.toRadians(startAngle + currSweepAngle)));
        slidePointY = (float) (mCircularSeekBarRadius
                * Math.sin(Math.toRadians(startAngle + currSweepAngle)));
    }

    /**
     * 当触摸屏幕时调用
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX() - getWidth() / 2;
        float y = event.getY() - getHeight() / 2;

        ViewParent parent = getParent();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isClickBtns((int) x, (int) y)) {
                } else if (clickOnSlidePoint((int) x, (int) y)) {
                    isClickOnSlidePoint = true;
                } else if (isClickOnProgress((int) x, (int) y)) {
                    currSweepAngle = calCurrSweepAngle(x, y);
                    if (!isOutMove()) {
                        moveToPoint();
                        callListener(true);
                    }
                }

                if (null != parent) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isClickOnSlidePoint) {
                    currSweepAngle = calCurrSweepAngle(x, y);
                    if (!isOutMove()) {
                        moveToPoint();
                        callListener(false);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isClickOnSlidePoint) {
                    isClickOnSlidePoint = false;
                    isOutMove();
                    moveToPoint();
                    callListener(true);
                }

                if (null != parent) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void callListener(boolean isFinish) {
        if (null != mOnChangeActionListener && isFinish) {
            mOnChangeActionListener.OnChange(currProgress);
        }
    }

    private boolean isClickOnProgress(float x, float y) {
        float clickRadius;
        float currSweepAngle = calCurrSweepAngle(x, y);

        clickRadius = (float) (x / Math.cos(Math.toRadians(startAngle + currSweepAngle)));

        if (mCircularSeekBarRadius - slidePointSize < clickRadius
                && mCircularSeekBarRadius + slidePointSize > clickRadius
                && 0 < currSweepAngle
                && sweepAngle > currSweepAngle) {
            return true;
        }
        return false;
    }

    /**
     * 用于处理加减按钮
     */
    private boolean isClickBtns(int x, int y) {
        if (isSupportBtns) {
            if (clickOnSub((int) x, (int) y)) { // 减
                setProgress(--currProgress);
                if (null != mOnChangeActionListener) {
                    mOnChangeActionListener.OnChange(currProgress);
                }
                return true;
            }

            if (clickOnAdd((int) x, (int) y)) { // 加
                setProgress(++currProgress);
                if (null != mOnChangeActionListener) {
                    mOnChangeActionListener.OnChange(currProgress);
                }
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 是否点击滑动按钮
     *
     * @param x 横坐标
     * @param y 纵坐标
     * @return ture点击 false未点击
     */
    private boolean clickOnSlidePoint(int x, int y) {
        return x > slidePointX - slidePointSize
                && x < slidePointX + slidePointSize
                && y > slidePointY - slidePointSize
                && y < slidePointY + slidePointSize;
    }

    /**
     * 是否点击加按钮
     *
     * @param x 横坐标
     * @param y 纵坐标
     * @return ture点击 false未点击
     */
    private boolean clickOnAdd(int x, int y) {
        return addBtnRect.contains(x, y);
    }

    /**
     * 是否点击减按钮
     *
     * @param x 横坐标
     * @param y 纵坐标
     * @return ture点击 false未点击
     */
    private boolean clickOnSub(int x, int y) {
        return subBtnRect.contains(x, y);
    }

    /**
     * 该方法在invalidate()调用结束后调用
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        canvas.translate(getWidth() / 2, getHeight() / 2);

        drawCircularSeekBar(canvas);

        if (isSupportBtns) {
            drawButton(canvas);
        }

        canvas.restore();
    }

    /**
     * 画进度条
     */
    private void drawCircularSeekBar(Canvas canvas) {

        canvas.drawArc(mCircularSeekBarRect, startAngle - adjustOffsetAngle,
                sweepAngle + 2 * adjustOffsetAngle, false, mCircleSeekBarBg); // 背景

        if (isOnclick) {

            Shader mSweepGradient = new SweepGradient(0, 0, SECTION_COLORS,
                    SECTION_COLORS_POSITIONS);
            mCircleSeekBarRing.setShader(mSweepGradient);
            canvas.drawArc(mCircularSeekBarRect,
                    startAngle - adjustOffsetAngle, currSweepAngle
                            + adjustOffsetAngle, false, mCircleSeekBarRing); // 进度条

            canvas.drawCircle(slidePointX, slidePointY, slidePointSize, slideOuterCircle);
            canvas.drawCircle(slidePointX, slidePointY, slidePointSize
                    - mCircularSeekBarRingWidth, slideInnerCircle);

            float width = slideCircleText.measureText(currProgress + UNIT);
            canvas.drawText(currProgress + UNIT, slidePointX - width / 2, slidePointY
                    + slidePointTextSize / 2, slideCircleText);
        }
    }

    /**
     * 画加减按钮
     */
    private void drawButton(Canvas canvas) {
        canvas.drawBitmap(mSubBitmap, null, subBtnRect, null);
        canvas.drawBitmap(mAddBitmap, null, addBtnRect, null);
    }

    /**
     * 滑动
     */
    private void moveToPoint() {
        currProgress = angle2Progress(currSweepAngle);
        getSlidePointCoord(currSweepAngle);
        invalidate();
    }

    private boolean isOutMove() {
        if (currSweepAngle < 0) {
            currSweepAngle = 0;
            return true;
        } else if (currSweepAngle > sweepAngle) {
            currSweepAngle = sweepAngle;
            return true;
        }
        return false;
    }

    private float calCurrSweepAngle(float x, float y) {
        float currSweepAngle;
        double angrad = Math.atan2(-y, -x);
        float angle = (float) Math.toDegrees(angrad);


        if (startAngle > PI_ANGLE) {
            currSweepAngle = angle - startAngle + PI_ANGLE;
        } else {
            if (-PI / 2 < angrad) {
                currSweepAngle = angle - startAngle + PI_ANGLE;
            } else {
                currSweepAngle = PI_ANGLE * 2 + angle - startAngle + PI_ANGLE;
            }
        }
        return currSweepAngle;
    }

    /**
     * 设置进度值
     *
     * @param progress 进度
     */
    public void setProgress(int progress) {

        if (progress < minProgress) {
            currProgress = minProgress;
        } else if (progress > maxProgress) {
            currProgress = maxProgress;
        } else {
            currProgress = progress;
        }

        currSweepAngle = progress2Angle(currProgress);
        getSlidePointCoord(currSweepAngle);

        invalidate();
    }

    private int angle2Progress(float currSweepAngle) {
        return Math.round((maxProgress - minProgress) * currSweepAngle / sweepAngle)
                + minProgress;
    }

    private float progress2Angle(int progress) {
        return (progress - minProgress) * sweepAngle / (maxProgress - minProgress);
    }

    /**
     * 设置单位
     *
     * @param unit
     */
    public void setValueUnit(String unit) {
        this.UNIT = unit;
    }

    /***
     * 设置进度条扫过的角度值
     */
    public void setSweepAngle(float sweepAngle) {
        this.sweepAngle = sweepAngle > MAX_SWEEP_ANGLE ? MAX_SWEEP_ANGLE : sweepAngle;
        this.startAngle = 270 - this.sweepAngle / 2;
    }

    /***
     * 设置进度条最小值
     */
    public void setMinProgress(int minProgress) {
        this.minProgress = minProgress;
    }

    /***
     * 设置进度条最大值
     */
    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    /**
     * 设置加减按钮， 默认为不支持
     */
    public void setBtnBitmap(int subDrawId, int addDrawId) {
        mSubBitmap = BitmapFactory.decodeResource(getResources(), subDrawId);
        mAddBitmap = BitmapFactory.decodeResource(getResources(), addDrawId);
        this.isSupportBtns = true;
    }

    public void setOnChangeActionListener(OnChangeActionListener change) {
        this.mOnChangeActionListener = change;
    }

    public interface OnChangeActionListener {
        void OnChange(int progress);
    }

    public void setOnClick(boolean isOnclick) {
        this.isOnclick = isOnclick;
    }
}
