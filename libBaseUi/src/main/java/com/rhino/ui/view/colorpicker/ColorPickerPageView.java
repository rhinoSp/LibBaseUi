package com.rhino.ui.view.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.rhino.ui.R;


/**
 * <p>颜色选取控件</p>
 * Created by LuoLin on 2017/7/21
 */
public class ColorPickerPageView extends View {

    /**
     * 取色器图标默认宽度
     **/
    private static final int DF_THUMB_DRAWABLE_WIDTH = 60;
    /**
     * 取色器图标默认高度
     **/
    private static final int DF_THUMB_DRAWABLE_HEIGHT = 105;

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

    /**
     * 点击的位置
     **/
    private float mPickerX, mPickerY;

    /**
     * 取色器图标
     **/
    private Drawable mThumbDrawable;
    /**
     * 取色器图标位置大小
     **/
    private Rect mThumbDrawableRect;
    /**
     * 取色器图标顶部圆圈，用于放大显示当前颜色区域Paint
     **/
    private Paint mThumbCirclePaint;
    /**
     * 取色器图标顶部圆圈，用于放大显示当前颜色区域坐标位置
     **/
    private float mThumbCircleX, mThumbCircleY;
    /**
     * 取色器图标顶部圆圈，用于放大显示当前颜色区域半径大小
     **/
    private float mThumbCircleRadius;
    /**
     * 取色起图标底部圆圈，用于标识当前选中坐标位置
     **/
    private float mColorPointOffsetX, mColorPointOffsetY;

    /**
     * 触摸取色区域
     **/
    private Rect mPickerRect;
    /**
     * 用于边缘位置偏移
     **/
    private float mPickerEdgOffset = 12;
    /**
     * 可选色区域Padding
     **/
    private int mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom;

    /**
     * 当前选取颜色
     **/
    @ColorInt
    private int mCurrColor = Color.WHITE;
    /**
     * 颜色改变监听事件
     **/
    private OnColorChangedListener mOnColorChangedListener;


    public ColorPickerPageView(Context context) {
        super(context);
        init();
    }

    public ColorPickerPageView(Context context, AttributeSet attrs) {
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
        canvas.save();
        drawBackground(canvas);
        drawThumbDrawable(canvas);
        canvas.restore();
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
        mColorPanelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWhiteAlphaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPickerRect = new Rect();

        mThumbDrawable = getResources().getDrawable(R.mipmap.ic_color_picker_thumb);

        float scale = getContext().getResources().getDisplayMetrics().density;

        mThumbDrawableRect = new Rect();
        mThumbDrawableRect.left = 0;
        mThumbDrawableRect.top = 0;
        mThumbDrawableRect.right = (int) (DF_THUMB_DRAWABLE_WIDTH * scale + 0.5f);
        mThumbDrawableRect.bottom = (int) (DF_THUMB_DRAWABLE_HEIGHT * scale + 0.5f);

        mThumbCircleX = mThumbDrawableRect.left + mThumbDrawableRect.width() / 2;
        mThumbCircleY = mThumbDrawableRect.top + mThumbDrawableRect.width() / 2;
        mThumbCircleRadius = mThumbDrawableRect.width() * 0.36f;

        mColorPointOffsetX = mThumbDrawableRect.left + mThumbDrawableRect.width() / 2;
        mColorPointOffsetY = mThumbDrawableRect.height() * 0.89f;

        mPickerEdgOffset = mThumbDrawableRect.height() * 0.11f;

        mThumbCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThumbCirclePaint.setColor(mCurrColor);
        mThumbCirclePaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 初始化视图
     **/
    private void initView(int width, int height) {
        if (0 >= width || 0 >= height) {
            return;
        }

        mPickerRect.set(0, 0, width, height);

        mPickerRect.set(mPaddingLeft, mPaddingTop, mViewWidth - mPaddingRight, mViewHeight - mPaddingBottom);

        mColorPanelShader = new SweepGradient(mPickerRect.centerX(), mPickerRect.centerY(),
                buildHueColorArray(), null);
        mColorPanelPaint.setShader(mColorPanelShader);

        mWhiteAlphaRadius = (int) Math.min(width * 0.4f, height * 0.4f);
        mWhiteAlphaShader = new RadialGradient(mPickerRect.centerX(), mPickerRect.centerY(),
                mWhiteAlphaRadius, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
        mWhiteAlphaPaint.setShader(mWhiteAlphaShader);

        float[] coord = color2Coord(mCurrColor);
        onColorCoordChanged(coord[0], coord[1]);
    }

    /**
     * 绘制背景色盘
     **/
    private void drawBackground(Canvas canvas) {
        canvas.save();

        canvas.rotate(-90, mPickerRect.centerX(), mPickerRect.centerY());
        mColorPanelPaint.setShader(mColorPanelShader);
        canvas.drawPaint(mColorPanelPaint);
        canvas.drawPaint(mWhiteAlphaPaint);
        canvas.restore();

        canvas.restore();
    }

    /**
     * 绘制取色器
     **/
    private void drawThumbDrawable(Canvas canvas) {
        if (null == mThumbDrawable) {
            return;
        }

        canvas.save();

        if (mThumbDrawableRect.top < 0) {
            canvas.rotate(180, mPickerX, mPickerY);
        }

        mThumbDrawable.setBounds(mThumbDrawableRect);
        mThumbDrawable.draw(canvas);

        mThumbCirclePaint.setStyle(Paint.Style.FILL);
        mThumbCirclePaint.setColor(mCurrColor);
        canvas.drawCircle(mThumbCircleX, mThumbCircleY, mThumbCircleRadius, mThumbCirclePaint);
        canvas.restore();
    }

    /**
     * 处理事件
     **/
    private boolean dealTouchEvent(float x, float y, boolean isFinished) {
        onColorCoordChanged(x, y);

        int color = coord2Color(mPickerX, mPickerY);
        if (mCurrColor != color) {
            onColorChangedListener(true, isFinished);
            mCurrColor = color;
        }
        postInvalidate();
        return true;
    }

    /**
     * 处理坐标改变，这里计算改变取色器的位置
     **/
    private void onColorCoordChanged(float x, float y) {
        mPickerX = x;
        mPickerY = y;

        if (mPickerX < mPickerRect.left + mPickerEdgOffset) {
            mPickerX = mPickerRect.left + mPickerEdgOffset;
        } else if (mPickerX > mPickerRect.right - mPickerEdgOffset) {
            mPickerX = mPickerRect.right - mPickerEdgOffset;
        }

        if (mPickerY < mPickerRect.top + mPickerEdgOffset) {
            mPickerY = mPickerRect.top + mPickerEdgOffset;
        } else if (mPickerY > mPickerRect.bottom - mPickerEdgOffset) {
            mPickerY = mPickerRect.bottom - mPickerEdgOffset;
        }
        mThumbDrawableRect.offsetTo((int) (mPickerX - mColorPointOffsetX), (int) (mPickerY - mColorPointOffsetY));
        mThumbCircleX = mThumbDrawableRect.left + mThumbDrawableRect.width() / 2;
        mThumbCircleY = mThumbDrawableRect.top + mThumbDrawableRect.width() / 2;
    }

    /**
     * 颜色改变
     **/
    private void onColorChangedListener(boolean fromUser, boolean isFinished) {
        if (null != mOnColorChangedListener) {
            mOnColorChangedListener.onColorChanged(this, fromUser, isFinished);
        }
    }

    /**
     * 获取颜色数组
     **/
    private int[] buildHueColorArray() {
        int[] hue = new int[361];
        int count = 0;
        for (int i = hue.length - 1; i >= 0; i--, count++) {
            hue[count] = Color.HSVToColor(new float[]{i, 1f, 1f});
        }
        return hue;
    }

    /**
     * 通过坐标计算得到颜色
     **/
    private int coord2Color(float x, float y) {
        float d_x = x - mPickerRect.centerX();
        float d_y = y - mPickerRect.centerY();

        float degAngle = (float) (Math.toDegrees(getAngle(Math.atan2(d_y, d_x))));
        if (degAngle < 90) {
            degAngle += 270;
        } else {
            degAngle -= 90;
        }

        double ra = Math.sqrt(d_x * d_x + d_y * d_y);
        if (ra > mWhiteAlphaRadius) {
            ra = mWhiteAlphaRadius;
        }
        return Color.HSVToColor(new float[]{degAngle, (float) (ra / mWhiteAlphaRadius), 1f});
    }

    /**
     * 通过颜色值计算为坐标, 0 - x; 1 - y
     **/
    private float[] color2Coord(int color) {
        float[] coord = new float[2];
        float[] hsv = new float[]{0f, 0f, 0f};
        Color.colorToHSV(color, hsv);
        float ra = hsv[1] * mWhiteAlphaRadius;
        float angle = 270 - hsv[0];
        if (angle < 0) {
            angle = 360 + angle;
        }
        double radiansAngle = Math.toRadians(angle);
        coord[0] = (float) Math.cos(radiansAngle) * ra + mPickerRect.centerX();
        coord[1] = (float) Math.sin(radiansAngle) * ra + mPickerRect.centerY();
        return coord;
    }

    /**
     * 得到连续的角度变换
     **/
    private double getAngle(double angle) {
        if (angle < 0) {
            angle = (float) (2 * Math.PI + angle);
        }
        return 2 * Math.PI - angle;
    }


    /**
     * 设置padding
     *
     * @param left   left
     * @param top    top
     * @param right  right
     * @param bottom bottom
     */
    public void setPadding(int left, int top, int right, int bottom) {
        mPaddingLeft = left;
        mPaddingTop = top;
        mPaddingRight = right;
        mPaddingBottom = bottom;
    }

    /**
     * 获取当前选取颜色
     *
     * @return color
     */
    @ColorInt
    public int getColor() {
        return mCurrColor;
    }

    /**
     * 设置当前颜色
     *
     * @param color color
     */
    public void setColor(@ColorInt int color) {
        if (mCurrColor == color) {
            return;
        }
        mCurrColor = color;
        float[] coord = color2Coord(mCurrColor);
        onColorCoordChanged(coord[0], coord[1]);
        onColorChangedListener(false, true);

        postInvalidate();
    }

    /**
     * 设置颜色选取事件监听
     *
     * @param listener listener
     */
    public void setIOnColorChangedListener(OnColorChangedListener listener) {
        mOnColorChangedListener = listener;
    }

    public interface OnColorChangedListener {
        void onColorChanged(ColorPickerPageView picker, boolean fromUser, boolean isFinished);
    }

}
