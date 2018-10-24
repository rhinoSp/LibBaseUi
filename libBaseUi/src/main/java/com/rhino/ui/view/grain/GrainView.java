package com.rhino.ui.view.grain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;


/**
 * <p>The grain custom view.</p>
 *
 * @since Created by LuoLin on 2018/1/26.
 **/
public class GrainView extends View {

    private static final int DEFAULT_FLOW_COLOR = 0xFF56DB3C;
    /**
     * The ratio of height for width.
     */
    private static final float HEIGHT_RATIO_FOR_WIDTH = 0.8333F;
    /**
     * The default delay time of anim.(ms)
     */
    private static final int DEFAULT_REFRESH_DELAY_TIME = 10;
    /**
     * The default x offset of anim.(px)
     */
    private static final int DEFAULT_OFFSET_X_PER_PERIOD = 15;
    /**
     * The default color of grain.
     */
    private static final int DEFAULT_GRAIN_COLOR = 0x66FFFFFF;
    /**
     * The default ratio of flow light width for height.
     */
    private static final float DEFAULT_FLOW_LIGHT_WIDTH_RATIO_FOR_HEIGHT = 0.3f;

    /**
     * The width of view.
     */
    private int mViewWidth;
    /**
     * The height of view.
     */
    private int mViewHeight;
    /**
     * The GrainHelper for this view.
     */
    private GrainHelper mGrainHelper;
    /**
     * The Paint for drawing.
     */
    private Paint mPaint;

    /**
     * The Path first top.
     */
    private Path mGrainTop1Path;
    /**
     * The Path second top.
     */
    private Path mGrainTop2Path;
    /**
     * The Path third top.
     */
    private Path mGrainTop3Path;
    /**
     * The Path center gap.
     */
    private Path mCenterGapPath;
    /**
     * The Path third bottom.
     */
    private Path mGrainBottom3Path;
    /**
     * The Path second bottom.
     */
    private Path mGrainBottom2Path;
    /**
     * The Path first bottom.
     */
    private Path mGrainBottom1Path;
    /**
     * The Path for clip.
     */
    private Path mClipPath;

    /**
     * The Drawable for flow anim.
     */
    private GradientDrawable mFlowLightShadowDrawable;
    /**
     * The Rect of flow anim Drawable.
     */
    private Rect mFlowLightShadowDestRect;
    /**
     * The runnable for flow anim.
     */
    private FlowAnimRunnable mFlowAnimRunnable;
    /**
     * Whether anim started.
     */
    private boolean mIsAnimStarted;
    /**
     * Whether anim showed.
     */
    private boolean mIsAnimShow = true;

    public GrainView(Context context) {
        this(context, null);
    }

    public GrainView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GrainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = getWidth();
        }
        /*if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = getHeight();
        }*/
        height = (int) (width * HEIGHT_RATIO_FOR_WIDTH);
        if (mViewWidth != width || mViewHeight != height) {
            mViewWidth = width;
            mViewHeight = height;
            initView(width, height);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(0, mViewHeight / 2);
        drawGrainPath(canvas);
        if (mIsAnimShow) {
            drawFlowLightShadow(canvas);
        }
        canvas.restore();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnim();
    }

    /**
     * Do something init.
     */
    private void init() {
        this.mGrainHelper = new GrainHelper();
        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mFlowLightShadowDestRect = new Rect();
        this.mFlowLightShadowDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{0x01FFFFFF & DEFAULT_FLOW_COLOR, 0x88FFFFFF & DEFAULT_FLOW_COLOR, 0x01FFFFFF & DEFAULT_FLOW_COLOR});
    }

    /**
     * Init view size
     **/
    private void initView(int width, int height) {
        if (0 >= width || 0 >= height) {
            return;
        }
        mGrainHelper.measure(getContext(), width, height);
        Path mGrainTop1Path1 = mGrainHelper.getGrainPath1FromLeft(false);
        Path mGrainTop1Path2 = mGrainHelper.getGrainPath1FromRight(false);
        mGrainTop1Path = new Path();
        mGrainTop1Path.addPath(mGrainTop1Path1);
        mGrainTop1Path.addPath(mGrainTop1Path2);

        Path mGrainTop2Path1 = mGrainHelper.getGrainPath2FromLeft(false);
        Path mGrainTop2Path2 = mGrainHelper.getGrainPath2FromRight(false);
        mGrainTop2Path = new Path();
        mGrainTop2Path.addPath(mGrainTop2Path1);
        mGrainTop2Path.addPath(mGrainTop2Path2);

        Path mGrainTop3Path1 = mGrainHelper.getGrainPath3FromLeft(false);
        Path mGrainTop3Path2 = mGrainHelper.getGrainPath3FromRight(false);
        mGrainTop3Path = new Path();
        mGrainTop3Path.addPath(mGrainTop3Path1);
        mGrainTop3Path.addPath(mGrainTop3Path2);

        mCenterGapPath = mGrainHelper.getGrainTop4Path();
        mCenterGapPath.addPath(mGrainHelper.getGrainBottom4Path());

        Path mGrainBottom3Path1 = mGrainHelper.getGrainPath3FromLeft(true);
        Path mGrainBottom3Path2 = mGrainHelper.getGrainPath3FromRight(true);
        mGrainBottom3Path = new Path();
        mGrainBottom3Path.addPath(mGrainBottom3Path1);
        mGrainBottom3Path.addPath(mGrainBottom3Path2);

        Path mGrainBottom2Path1 = mGrainHelper.getGrainPath2FromLeft(true);
        Path mGrainBottom2Path2 = mGrainHelper.getGrainPath2FromRight(true);
        mGrainBottom2Path = new Path();
        mGrainBottom2Path.addPath(mGrainBottom2Path1);
        mGrainBottom2Path.addPath(mGrainBottom2Path2);

        Path mGrainBottom1Path1 = mGrainHelper.getGrainPath1FromLeft(true);
        Path mGrainBottom1Path2 = mGrainHelper.getGrainPath1FromRight(true);
        mGrainBottom1Path = new Path();
        mGrainBottom1Path.addPath(mGrainBottom1Path1);
        mGrainBottom1Path.addPath(mGrainBottom1Path2);

        mClipPath = new Path();
        mClipPath.addPath(mGrainTop1Path);
        mClipPath.addPath(mGrainTop2Path);
        mClipPath.addPath(mGrainTop3Path);
        mClipPath.addPath(mCenterGapPath);
        mClipPath.addPath(mGrainBottom3Path);
        mClipPath.addPath(mGrainBottom2Path);
        mClipPath.addPath(mGrainBottom1Path);

        mFlowLightShadowDestRect.top = -mViewHeight / 2;
        mFlowLightShadowDestRect.bottom = mViewHeight / 2;
        mFlowLightShadowDestRect.left = (int) (mViewWidth / 2 - DEFAULT_FLOW_LIGHT_WIDTH_RATIO_FOR_HEIGHT * mFlowLightShadowDestRect.height());
        mFlowLightShadowDestRect.right = (int) (mViewWidth / 2 + DEFAULT_FLOW_LIGHT_WIDTH_RATIO_FOR_HEIGHT * mFlowLightShadowDestRect.height());
    }

    /**
     * Draw grain path.
     *
     * @param canvas Canvas
     */
    private void drawGrainPath(Canvas canvas) {
        canvas.save();

        this.mPaint.setColor(DEFAULT_GRAIN_COLOR);

        canvas.drawPath(mGrainTop1Path, mPaint);
        canvas.drawPath(mGrainTop2Path, mPaint);
        canvas.drawPath(mGrainTop3Path, mPaint);
        canvas.drawPath(mCenterGapPath, mPaint);
        canvas.drawPath(mGrainBottom3Path, mPaint);
        canvas.drawPath(mGrainBottom2Path, mPaint);
        canvas.drawPath(mGrainBottom1Path, mPaint);

        canvas.restore();
    }

    /**
     * Draw the shadow flow light.
     *
     * @param canvas Canvas
     */
    private void drawFlowLightShadow(Canvas canvas) {
        canvas.save();
        canvas.clipPath(mClipPath);
        if (null != mFlowLightShadowDrawable) {
            mFlowLightShadowDrawable.setBounds(mFlowLightShadowDestRect);
            mFlowLightShadowDrawable.draw(canvas);
        }
        canvas.restore();
    }

    /**
     * The runnable for flow anim.
     */
    private class FlowAnimRunnable implements Runnable {
        @Override
        public void run() {
            mFlowLightShadowDestRect.offset(DEFAULT_OFFSET_X_PER_PERIOD, 0);
            if (mFlowLightShadowDestRect.left >= mViewWidth) {
                mFlowLightShadowDestRect.offset(-mViewWidth - mFlowLightShadowDestRect.width(), 0);
            }
            invalidate();
            postDelayed(this, DEFAULT_REFRESH_DELAY_TIME);
        }
    }

    /**
     * Start float anim.
     */
    public void startAnim() {
        if (mIsAnimStarted) {
            return;
        }
        if (null == mFlowAnimRunnable) {
            mFlowAnimRunnable = new FlowAnimRunnable();
        } else {
            removeCallbacks(mFlowAnimRunnable);
        }
        post(mFlowAnimRunnable);
        mIsAnimStarted = true;
    }

    /**
     * Stop float anim.
     */
    public void stopAnim() {
        if (null != mFlowAnimRunnable) {
            mIsAnimStarted = false;
            removeCallbacks(mFlowAnimRunnable);
        }
    }

    /**
     * Return whether anim started.
     *
     * @return true tarted, false not started
     */
    public boolean isAnimStarted() {
        return mIsAnimStarted;
    }

    /**
     * Set the color of grain.
     *
     * @param color color
     */
    public void setGrainCenterColor(@ColorInt int color) {
        this.mPaint.setColor(color);
        invalidate();
    }

    /**
     * Set the drawable of flow anim.
     *
     * @param drawable GradientDrawable
     */
    private void setAnimDrawable(GradientDrawable drawable) {
        this.mFlowLightShadowDrawable = drawable;
        invalidate();
    }

    /**
     * Return whether anim showed.
     *
     * @return true showed, false not showed
     */
    public boolean isAnimShow() {
        return mIsAnimShow;
    }

    /**
     * Set anim showed.
     *
     * @param show true showed, false not showed
     */
    public void setAnimShow(boolean show) {
        this.mIsAnimShow = show;
        invalidate();
    }
}
