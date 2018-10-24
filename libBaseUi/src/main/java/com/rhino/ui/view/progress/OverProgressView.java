package com.rhino.ui.view.progress;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * <p>矩形滑动进度控件，如：播放视频时，音量、亮度、进度的控制</p>
 * Created by LuoLin on 2017/4/27.
 */
public class OverProgressView extends View {

    /**
     * 点击
     **/
    public static final int EVENT_CLICK = 0;
    /**
     * 视图上半部分水平滑动
     **/
    public static final int EVENT_SCROLL_HORIZONTAL_TOP = 1;
    /**
     * 视图下半部分水平滑动
     **/
    public static final int EVENT_SCROLL_HORIZONTAL_BOTTOM = 2;
    /**
     * 视图左半部分竖直滑动
     **/
    public static final int EVENT_SCROLL_VERTICAL_LEFT = 3;
    /**
     * 视图右半部分竖直滑动
     **/
    public static final int EVENT_SCROLL_VERTICAL_RIGHT = 4;

    /**
     * 视图高度
     **/
    private int mViewHeight;
    /**
     * 视图宽度
     **/
    private int mViewWidth;
    /**
     * 记录按下时x坐标
     **/
    private float mLastDownX = 0;
    /**
     * 记录按下时y坐标
     **/
    private float mLastDownY = 0;
    /**
     * 手指上次移动x距离
     **/
    private float mLastXProgress = 0;
    /**
     * 手指上次移动y距离
     **/
    private float mLastYProgress = 0;
    /**
     * 手指移动x距离
     **/
    private float mMoveXDistance = 0;
    /**
     * 手指移动y距离
     **/
    private float mMoveYDistance = 0;
    /**
     * 监听事件
     **/
    private OnOverTouchListener mOnOverTouchListener = null;

    public OverProgressView(Context context) {
        super(context);
    }

    public OverProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OverProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mViewWidth = widthSize;
        } else {
            mViewWidth = getWidth();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mViewHeight = heightSize;
        } else {
            mViewHeight = getHeight();
        }

        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastDownX = event.getX();
                mLastDownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onMoveClickListener(true);
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveXDistance = event.getX() - mLastDownX;
                mMoveYDistance = mLastDownY - event.getY();
                onMoveClickListener(false);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 处理手指抬起事件
     *
     * @param isFinished whether finished
     */
    private void onMoveClickListener(boolean isFinished) {
        if (0 == mMoveXDistance && 0 == mMoveYDistance) {
            onScrollListener(EVENT_CLICK, 0, isFinished);
        } else if (isHorizontalScroll()) {
            int progress = (int) (mMoveXDistance / mViewWidth * 100);
            if (mLastXProgress == progress && !isFinished) {
                return;
            }
            mLastXProgress = progress;
            if (isScrollInTop()) {
                onScrollListener(EVENT_SCROLL_HORIZONTAL_TOP, progress, isFinished);
            } else {
                onScrollListener(EVENT_SCROLL_HORIZONTAL_BOTTOM, progress, isFinished);
            }
        } else {
            int progress = (int) (mMoveYDistance / mViewHeight * 100);
            if (mLastYProgress == progress && !isFinished) {
                return;
            }
            mLastYProgress = progress;
            if (isScrollInLeft()) {
                onScrollListener(EVENT_SCROLL_VERTICAL_LEFT, (int) (mMoveYDistance / mViewHeight * 100), isFinished);
            } else {
                onScrollListener(EVENT_SCROLL_VERTICAL_RIGHT, (int) (mMoveYDistance / mViewHeight * 100), isFinished);
            }
        }
    }

    /**
     * 是否为水平滑动
     *
     * @return true 水平滑动
     */
    private boolean isHorizontalScroll() {
        return Math.abs(mMoveXDistance) >= Math.abs(mMoveYDistance);
    }

    /**
     * 是否在控件左半部分滑动
     *
     * @return true 左半部分滑动
     */
    private boolean isScrollInLeft() {
        return 0 < mLastDownX && mLastDownX < mViewWidth / 2;
    }

    /**
     * 是否在控件上半部分滑动
     *
     * @return true 上半部分滑动
     */
    private boolean isScrollInTop() {
        return 0 < mLastDownY && mLastDownY < mViewHeight / 2;
    }

    /**
     * 处理回调事件
     *
     * @param event      事件
     * @param progress   滑动进度值
     * @param isFinished 滑动完成是否完成
     */
    private void onScrollListener(int event, int progress, boolean isFinished) {
        if (null != mOnOverTouchListener) {
            mOnOverTouchListener.onTouch(event, progress, isFinished);
        }
    }

    /**
     * 设置滑动监听事件
     *
     * @param listener 滑动事件
     */
    public void setOnOverTouchListener(OnOverTouchListener listener) {
        mOnOverTouchListener = listener;
    }

    public interface OnOverTouchListener {
        /**
         * 滑动回调事件
         *
         * @param event      事件，EVENT_XXX
         * @param progress   滑动进度值，即滑动距离占滑动方向总长度的比例
         * @param isFinished 滑动是否完成
         */
        void onTouch(int event, int progress, boolean isFinished);
    }

}
