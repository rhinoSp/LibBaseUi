package com.rhino.ui.view.progress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * <p>圆形进度条 梯形刻度</p>
 * Created by LuoLin on 2016/1/29
 **/
public class CircleProgressTick extends View {

    /**
     * 圆弧的进度条
     */
    private Paint mCircleProgressPaint;
    /**
     * 圆弧的进度条
     */
    private Paint mTextPaint;
    /**
     * 圆弧的背景
     */
    private RectF mCircleRect;
    /**
     * 圆弧半径
     */
    private float mCircleRadius;
    /**
     * 圆弧的背景
     */
    private float mStrokeWidth;

    private OnChangeActionListener mChange;
    private boolean isOnclick = true;
    private boolean isMove = false;

    private float cx;
    private float cy;
    private float startAngle = 190;
    private float sweepAngle = 160;
    private float currentAngle;
    private float textSize;
    private int minProgress = 35;
    private int maxProgress = 60;

    public CircleProgressTick(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mWidth = getWidth();
        int mHeight = getHeight();
        float size = mWidth > mHeight ? mHeight * 0.85f : mWidth * 0.85f;

        cx = mWidth / 2;
        cy = mHeight / 2;
        mCircleRadius = size / 2;
        mStrokeWidth = size * 0.08f;
        textSize = mStrokeWidth;
        mCircleProgressPaint.setStrokeWidth(mStrokeWidth);

        mCircleRect.left = cx - mCircleRadius;
        mCircleRect.right = cx + mCircleRadius;
        mCircleRect.top = cy - mCircleRadius;
        mCircleRect.bottom = cy + mCircleRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        drawCircleProgressBg(canvas);
        drawCircleProgress(canvas);
        drawCircleProgressTick(canvas);
        canvas.restore();
    }

    /**
     * 背景
     **/
    private void drawCircleProgressBg(Canvas canvas) {
        mCircleProgressPaint.setColor(Color.LTGRAY);
        canvas.save();
        canvas.drawArc(mCircleRect, startAngle, sweepAngle, false,
                mCircleProgressPaint);
        canvas.restore();
    }

    /**
     * 进度
     **/
    private void drawCircleProgress(Canvas canvas) {
        canvas.save();
        float realAngle = currentAngle;
        float count = sweepAngle / (maxProgress - minProgress);
        for (float tmp = startAngle; tmp < startAngle + sweepAngle; ) {
            tmp += count;
            if (currentAngle < tmp - startAngle) {
                realAngle = tmp - startAngle + 0.5f;
                break;
            }
        }
        if (currentAngle >= sweepAngle - count) {
            realAngle = sweepAngle;
        }
        mCircleProgressPaint.setColor(Color.GREEN);
        canvas.drawArc(mCircleRect, startAngle, realAngle, false,
                mCircleProgressPaint); // 进度
        mTextPaint.setTextSize(textSize);
        canvas.drawText((int) (minProgress + realAngle
                * (maxProgress - minProgress) / sweepAngle)
                + "", cx - textSize / 2, cy - textSize / 2, mTextPaint);
        canvas.restore();
    }

    /**
     * 刻度
     **/
    private void drawCircleProgressTick(Canvas canvas) {
        canvas.save();
        mCircleProgressPaint.setColor(Color.WHITE);
        float count = sweepAngle / (maxProgress - minProgress);
        for (float tmp = startAngle; tmp < startAngle + sweepAngle; ) {
            if (tmp <= startAngle + sweepAngle - count) {
                canvas.drawArc(mCircleRect, tmp, 0.5f, false,
                        mCircleProgressPaint);
            }
            tmp += count;
        }
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isOnclick) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float distance = (float) Math.sqrt(Math.pow((x - cx), 2)
                        + Math.pow((y - cy), 2));
                if (distance > mCircleRadius + mStrokeWidth
                        || distance < mCircleRadius - mStrokeWidth) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mChange != null
                        && y < cy + mCircleRadius
                        * Math.sin(Math.toRadians(startAngle))) {
                    isMove = true;
                }
                moved(x, y);
                break;
            case MotionEvent.ACTION_UP:
                if ((isMove || moved(x, y)) && mChange != null) {
                    mChange.OnChange(true);
                    isMove = false;
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 进度条滑动到指定位置
     */
    private boolean moved(float x, float y) {
        boolean onSeekBar = y <= cy + mCircleRadius * Math.sin(Math.toRadians(startAngle - 5));
        if (onSeekBar) {
            currentAngle = (float) (Math.toDegrees(Math.atan2(cy - y, cx - x))
                    - startAngle + 180);

            if (sweepAngle <= currentAngle) {
                currentAngle = sweepAngle;
            } else if (0 >= currentAngle) {
                currentAngle = 0;
            }
            invalidate();
            return true;
        } else {
            invalidate();
            return false;
        }
    }

    public void setOnChangeActionListener(OnChangeActionListener change) {
        mChange = change;
    }

    public interface OnChangeActionListener {
        void OnChange(boolean isChange);
    }

    public void setOnClick(boolean isOnclick) {
        this.isOnclick = isOnclick;
    }

    private void init() {
        mCircleRect = new RectF();

        mCircleProgressPaint = new Paint();
        mCircleProgressPaint.setColor(Color.CYAN);
        mCircleProgressPaint.setAntiAlias(true);
        mCircleProgressPaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(Color.CYAN);
    }
}
