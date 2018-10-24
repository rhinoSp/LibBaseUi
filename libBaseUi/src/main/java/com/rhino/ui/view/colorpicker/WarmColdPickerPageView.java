package com.rhino.ui.view.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * <p>冷暖色调选择</p>
 * Created by LuoLin on 2017/7/21
 */
public class WarmColdPickerPageView extends View {

    /**
     * 默认暖光颜色值
     **/
    private static final int DF_COLOR_WARM = 0xFFFFE487;
    /**
     * 默认冷光颜色值
     **/
    private static final int DF_COLOR_CLOD = 0xFF3279FD;
    /**
     * 默认取色器投影颜色
     **/
    private static final int DF_COLOR_SHADOW = 0x33000000;
    /**
     * 默认取色器投影宽度
     **/
    private static final int DF_THUMB_SHADOW_WIDTH = 1;
    /**
     * 默认取色器外圆半径
     **/
    private static final int DF_THUMB_OUTER_RADIUS = 14;
    /**
     * 默认取色器内圆半径
     **/
    private static final int DF_THUMB_INNER_RADIUS = 6;

    /**
     * view的宽度
     **/
    private int mViewWidth;
    /**
     * view的高度
     **/
    private int mViewHeight;

    /**
     * 背景色盘Paint
     **/
    private Paint mColorPanelPaint;
    /**
     * 背景色盘渐变
     **/
    private Shader mColorPanelShader;

    /**
     * 朦板层Paint
     **/
    private Paint mWhiteAlphaPaint;
    /**
     * 朦板层渐变
     **/
    private Shader mWhiteAlphaShader;
    /**
     * 朦板层圆形区域半径
     **/
    private float mWhiteAlphaRadius;

    private float mThumbCenterX, mThumbCenterY;

    /**
     * 取色器Paint
     **/
    private Paint mThumbPaint;
    /**
     * 取色器投影宽度
     **/
    private int mThumbShadowWidth;
    /**
     * 取色器外圆半径
     **/
    private float mThumbOuterRadius;
    /**
     * 取色器内圆半径
     **/
    private float mThumbInnerRadius;

    /**
     * 触摸取色区域
     **/
    private Rect mPickerRect;
    /**
     * 用于边缘位置偏移
     **/
    private float mPickerEdgOffset;
    /**
     * 可选色区域Padding
     **/
    private int mContentPaddingLeft, mContentPaddingTop, mContentPaddingRight, mContentPaddingBottom;

    /**
     * 当前冷暖值
     **/
    private int mCurrColdValue = 50;
    private OnValueChangedListener mOnValueChangedListener;


    public WarmColdPickerPageView(Context context) {
        super(context);
        init();
    }

    public WarmColdPickerPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
        initView(mViewWidth, mViewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawThumbDrawable(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean down = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mPickerRect.contains((int) event.getX(), (int) event.getY())) {
                    down = dealTouchEvent(event.getX(), event.getY(), false);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                down = dealTouchEvent(event.getX(), event.getY(), false);
                break;
            case MotionEvent.ACTION_UP:
                down = dealTouchEvent(event.getX(), event.getY(), true);
                break;
            default:
                break;
        }
        return down || super.onTouchEvent(event);
    }

    /**
     * 初始化
     **/
    private void init() {

        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mColorPanelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWhiteAlphaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThumbPaint.setColor(Color.WHITE);
        mThumbPaint.setStyle(Paint.Style.FILL);

        mPickerRect = new Rect();

        float scale = getContext().getResources().getDisplayMetrics().density;
        mThumbShadowWidth = (int) (DF_THUMB_SHADOW_WIDTH * scale + 0.5f);
        mThumbOuterRadius = (int) (DF_THUMB_OUTER_RADIUS * scale + 0.5f);
        mThumbInnerRadius = (int) (DF_THUMB_INNER_RADIUS * scale + 0.5f);

        //mPickerEdgOffset = mThumbOuterRadius;

        mThumbPaint.setShadowLayer(mThumbShadowWidth, 1, mThumbShadowWidth, DF_COLOR_SHADOW);

    }

    /**
     * 初始化视图
     **/
    private void initView(int width, int height) {
        if (0 >= width || 0 >= height) {
            return;
        }

        mPickerRect.set(0, 0, width, height);

        mPickerRect.set(mContentPaddingLeft, mContentPaddingTop, mViewWidth - mContentPaddingRight, mViewHeight - mContentPaddingBottom);

        mColorPanelShader = new LinearGradient(0, 0, 0, height, DF_COLOR_WARM,
                DF_COLOR_CLOD, Shader.TileMode.CLAMP);
        mColorPanelPaint.setShader(mColorPanelShader);

        mWhiteAlphaRadius = (int) Math.min(width * 0.4f, height * 0.4f);
        mWhiteAlphaShader = new RadialGradient(mPickerRect.centerX(), mPickerRect.centerY(),
                mWhiteAlphaRadius, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
        mWhiteAlphaPaint.setShader(mWhiteAlphaShader);

        float[] coord = coldValue2Coord(mCurrColdValue);
        onColorCoordChanged(coord[0], coord[1]);
    }

    private void drawBackground(Canvas canvas) {
        canvas.save();
        canvas.drawPaint(mColorPanelPaint);
        canvas.drawPaint(mWhiteAlphaPaint);
        canvas.restore();
    }

    private void drawThumbDrawable(Canvas canvas) {
        canvas.save();
        canvas.clipRect(mPickerRect);
        canvas.drawCircle(mThumbCenterX, mThumbCenterY, mThumbOuterRadius, mThumbPaint);
        canvas.drawCircle(mThumbCenterX, mThumbCenterY, mThumbInnerRadius, mThumbPaint);
        canvas.restore();
    }

    /**
     * 处理事件
     **/
    private boolean dealTouchEvent(float x, float y, boolean isFinished) {
        onColorCoordChanged(x, y);

        int value = coord2ColdValue(mThumbCenterX, mThumbCenterY);
        if (mCurrColdValue != value) {
            mCurrColdValue = value;
            onColorChangedListener(true, isFinished);
        }
        postInvalidate();
        return true;
    }

    /**
     * 处理坐标改变，这里计算改变取色器的位置
     **/
    private void onColorCoordChanged(float x, float y) {
        mThumbCenterX = x;
        mThumbCenterY = y;

        if (mThumbCenterX < mPickerRect.left + mPickerEdgOffset) {
            mThumbCenterX = mPickerRect.left + mPickerEdgOffset;
        } else if (mThumbCenterX > mPickerRect.right - mPickerEdgOffset) {
            mThumbCenterX = mPickerRect.right - mPickerEdgOffset;
        }

        if (mThumbCenterY < mPickerRect.top + mPickerEdgOffset) {
            mThumbCenterY = mPickerRect.top + mPickerEdgOffset;
        } else if (mThumbCenterY > mPickerRect.bottom - mPickerEdgOffset) {
            mThumbCenterY = mPickerRect.bottom - mPickerEdgOffset;
        }
    }

    /**
     * 颜色改变
     **/
    private void onColorChangedListener(boolean fromUser, boolean isFinished) {
        if (null != mOnValueChangedListener) {
            mOnValueChangedListener.onValueChanged(this, fromUser, isFinished);
        }
    }

    /**
     * 坐标值转换为冷暖值
     **/
    private int coord2ColdValue(float x, float y) {
        return (int) (100f * (y - mPickerRect.top - mPickerEdgOffset) / (mPickerRect.height() - 2 * mPickerEdgOffset));
    }

    /**
     * 冷暖值转换为坐标值
     **/
    private float[] coldValue2Coord(int cold) {
        float[] coord = new float[2];
        coord[0] = mPickerRect.centerX();
        coord[1] = (mPickerRect.height() - 2 * mPickerEdgOffset) * cold / 100f + mPickerRect.top + mPickerEdgOffset;
        return coord;
    }

    /**
     * 设置padding
     *
     * @param left   left
     * @param top    top
     * @param right  right
     * @param bottom bottom
     */
    public void setContentPadding(int left, int top, int right, int bottom) {
        mContentPaddingLeft = left;
        mContentPaddingTop = top;
        mContentPaddingRight = right;
        mContentPaddingBottom = bottom;
    }

    /**
     * 获取冷暖值
     *
     * @return cold
     */
    public int getColdValue() {
        return mCurrColdValue;
    }

    /**
     * 设置冷暖值
     *
     * @param cold cold
     */
    public void setColdValue(int cold) {
        if (mCurrColdValue == cold) {
            return;
        }
        mCurrColdValue = cold;
        float[] coord = coldValue2Coord(cold);
        onColorCoordChanged(coord[0], coord[1]);
        onColorChangedListener(false, true);
        postInvalidate();
    }

    /**
     * 设置监听
     *
     * @param listener listener
     */
    public void setOnValueChangedListener(OnValueChangedListener listener) {
        mOnValueChangedListener = listener;
    }

    public interface OnValueChangedListener {
        void onValueChanged(WarmColdPickerPageView picker, boolean fromUser, boolean isFinished);
    }
}
