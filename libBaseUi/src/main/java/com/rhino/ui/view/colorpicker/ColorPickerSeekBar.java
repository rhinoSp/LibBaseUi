package com.rhino.ui.view.colorpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;


import com.rhino.ui.R;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>自定义颜色选取SeekBar</p>
 * <p>attr ref android.R.styleable#ColorPickerSeekBar_progress_height</p>
 * <p>attr ref android.R.styleable#ColorPickerSeekBar_progress_corner</p>
 * <p>attr ref android.R.styleable#ColorPickerSeekBar_thumb_radius</p>
 * <p>attr ref android.R.styleable#ColorPickerSeekBar_thumb_shadow_radius</p>
 * <p>attr ref android.R.styleable#ColorPickerSeekBar_max_progress</p>
 * <p>attr ref android.R.styleable#ColorPickerSeekBar_color_seeds</p>
 * Create by LuoLin on 2017/3/7
 */
public class ColorPickerSeekBar extends View {

    /**
     * 默认进度条滑块半径
     **/
    private static final int DEFAULT_THUMB_RADIUS = 18;
    /**
     * 默认进度条滑块投影半径
     **/
    private static final int DEFAULT_THUMB_SHADOW_RADIUS = 20;
    /**
     * 默认进度条最小值
     **/
    private static final int DEFAULT_MIN_PROGRESS = 0;
    /**
     * 默认进度条最大值
     **/
    private static final int DEFAULT_MAX_PROGRESS = 100;
    /**
     * 默认圆弧边角
     **/
    private static final int DEFAULT_PROGRESS_CORNER = 2;
    /**
     * 默认进度条高度
     **/
    private static final int DEFAULT_PROGRESS_HEIGHT = 2;

    /**
     * 进度条滑块的半径
     **/
    private int mThumbRadius = DEFAULT_THUMB_RADIUS;
    /**
     * 进度条滑块投影的半径
     **/
    private int mThumbShadowRadius = DEFAULT_THUMB_SHADOW_RADIUS;
    /**
     * 进度条最小值
     **/
    private int mMinProgress = DEFAULT_MIN_PROGRESS;
    /**
     * 进度条最大值
     **/
    private int mMaxProgress = DEFAULT_MAX_PROGRESS;
    /**
     * 圆弧边角弧度
     **/
    private int mProgressCorner = DEFAULT_PROGRESS_CORNER;
    /**
     * 进度条宽度
     **/
    private int mProgressHeight = DEFAULT_PROGRESS_HEIGHT;
    /**
     * 进度条宽度
     **/
    private int mProgressWidth;
    /**
     * 增加滑块的点击范围
     **/
    private int mThumbTouchOffset;
    /**
     * 渐变颜色值
     **/
    private int[] mColorSeeds;

    /**
     * 视图高度
     **/
    private int mViewHeight;
    /**
     * 视图宽度
     **/
    private int mViewWidth;

    /**
     * 进度条背景
     **/
    private GradientDrawable mProgressBgDrawable;
    /**
     * 进度条背景
     **/
    private Rect mProgressBackgroundRect;

    /**
     * 滑块
     **/
    private Paint mThumbPaint;

    /**
     * 滑块投影Drawable
     **/
    private Drawable mThumbShadowDrawable;

    /**
     * 进度条滑动块目标位置
     **/
    private Rect mThumbDestRect;

    /**
     * 进度条滑动块投影目标位置
     **/
    private Rect mThumbShadowDestRect;

    /**
     * 进度条当前值
     **/
    private int mCurrProgress;
    /**
     * 进度条上一个值
     **/
    private int mLastProgress;

    /**
     * 根据各个进度值换算出颜色值进行缓存，便于使用
     */
    private List<Integer> mCacheColor;

    /**
     * 是否点击滑块
     **/
    private boolean mIsClickOnThumb = false;
    /**
     * 是否点击在进度条上
     **/
    private boolean mIsClickOnProgress = false;
    /**
     * 是否滑出控件之外
     **/
    private boolean mIsMovedOut = false;
    /**
     * 是否进度改变来自用户
     **/
    private boolean mIsFromUser = false;

    /**
     * 事件监听
     **/
    private OnColorChangedListener mOnColorChangedListener;

    public interface OnColorChangedListener {
        void onChanged(ColorPickerSeekBar seekBar, boolean fromUser, boolean isFinished);
    }


    public ColorPickerSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs, 0);
        init();
    }

    public ColorPickerSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(context, attrs, defStyle);
        init();
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

        initViewSize(mViewWidth, mViewHeight);
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    /**
     * 资源初始化
     **/
    private void initAttrs(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ColorPickerSeekBar, defStyle, 0);
        mMaxProgress = a.getInteger(
                R.styleable.ColorPickerSeekBar_cpsb_max_value,
                dip2px(context, DEFAULT_MAX_PROGRESS));
        mProgressHeight = a.getDimensionPixelSize(
                R.styleable.ColorPickerSeekBar_cpsb_progress_height,
                dip2px(context, DEFAULT_PROGRESS_HEIGHT));
        mProgressCorner = a.getDimensionPixelSize(
                R.styleable.ColorPickerSeekBar_cpsb_progress_corner,
                dip2px(context, DEFAULT_PROGRESS_CORNER));
        mThumbRadius = a.getDimensionPixelSize(
                R.styleable.ColorPickerSeekBar_cpsb_thumb_radius,
                dip2px(context, DEFAULT_THUMB_RADIUS));
        mThumbShadowRadius = a.getDimensionPixelSize(
                R.styleable.ColorPickerSeekBar_cpsb_thumb_shadow_radius,
                dip2px(context, DEFAULT_THUMB_SHADOW_RADIUS));
        int colorsId = a.getResourceId(R.styleable.ColorPickerSeekBar_cpsb_color_seeds, 0);
        if (0 != colorsId) {
            mColorSeeds = getColorsById(context, colorsId);
        } else {
            mColorSeeds = new int[]{Color.BLACK, Color.RED, Color.WHITE};
        }
        a.recycle();
    }

    /**
     * 资源初始化
     **/
    private void init() {
        mCacheColor = new ArrayList<>();
        mThumbPaint = new Paint();
        mThumbPaint.setStyle(Paint.Style.FILL);
        mThumbPaint.setAntiAlias(true);

        mProgressBgDrawable = new GradientDrawable(Orientation.LEFT_RIGHT,
                new int[]{Color.RED, Color.YELLOW, Color.BLACK, Color.GREEN});
        mProgressBgDrawable.setShape(GradientDrawable.RECTANGLE);
        mProgressBgDrawable.setColors(mColorSeeds);

        mThumbShadowDrawable = getResources().getDrawable(R.mipmap.ic_circle_shadow);

        mProgressBackgroundRect = new Rect();
        mThumbDestRect = new Rect();
        mThumbShadowDestRect = new Rect();

        mThumbDestRect.top = -mThumbRadius;
        mThumbDestRect.bottom = mThumbRadius;
        mThumbDestRect.left = -mThumbRadius;
        mThumbDestRect.right = mThumbRadius;

        mThumbShadowDestRect.top = -mThumbShadowRadius;
        mThumbShadowDestRect.bottom = mThumbShadowRadius;
        mThumbShadowDestRect.left = -mThumbShadowRadius;
        mThumbShadowDestRect.right = mThumbShadowRadius;
    }

    /**
     * 初始化视图
     *
     * @param width  控件宽度
     * @param height 控件高度
     */
    private void initViewSize(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        mProgressWidth = width - 2 * Math.max(mThumbShadowRadius, mThumbRadius);
        mThumbTouchOffset = mThumbRadius;

        mProgressBackgroundRect.top = -mProgressHeight / 2;
        mProgressBackgroundRect.bottom = mProgressHeight / 2;
        mProgressBackgroundRect.left = -mProgressWidth / 2;
        mProgressBackgroundRect.right = mProgressWidth / 2;

        setProgress(mCurrProgress);
        cacheColors();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() - mViewWidth / 2;
        float y = event.getY() - mViewHeight / 2;
        ViewParent parent = getParent();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (clickOnThumb(x, y)) {
                    mIsClickOnThumb = true;
                    mIsClickOnProgress = true;
                    if (null != parent) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                } else if (clickOnProgress(x, y)) {
                    mIsClickOnProgress = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsClickOnThumb) {
                    moveToPoint(x);
                    onProgressChanged(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                mIsClickOnThumb = false;
                if (mIsClickOnProgress) {
                    mIsClickOnProgress = false;
                    moveToPoint(x);
                    onProgressChanged(true);
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mViewWidth / 2, mViewHeight / 2);
        drawProgressBackground(canvas);
        drawThumb(canvas);
        canvas.restore();
    }

    /**
     * 进度条背景
     **/
    private void drawProgressBackground(Canvas canvas) {
        canvas.save();
        mProgressBgDrawable.setBounds(mProgressBackgroundRect);
        mProgressBgDrawable.setCornerRadius(mProgressCorner);
        mProgressBgDrawable.draw(canvas);
        canvas.restore();
    }

    /**
     * 进度条滑块
     **/
    private void drawThumb(Canvas canvas) {
        canvas.save();
        // 投影
        if (null != mThumbShadowDrawable) {
            mThumbShadowDrawable.setBounds(mThumbShadowDestRect);
            mThumbShadowDrawable.draw(canvas);
        }

        // 滑块
        mThumbPaint.setColor(getColor());
        canvas.drawCircle(mThumbDestRect.centerX(), mThumbDestRect.centerY(),
                mThumbDestRect.width() / 2, mThumbPaint);
        canvas.restore();
    }

    /**
     * 是否点击滑动按钮
     *
     * @param x 横坐标
     * @param y 纵坐标
     * @return true点击 false未点击
     */
    private boolean clickOnThumb(float x, float y) {
        return mThumbShadowDestRect.left < mThumbDestRect.right
                && mThumbShadowDestRect.top < mThumbDestRect.bottom
                && x >= mThumbShadowDestRect.left - mThumbTouchOffset
                && x <= mThumbShadowDestRect.right + mThumbTouchOffset
                && y >= mThumbShadowDestRect.top - mThumbTouchOffset
                && y <= mThumbShadowDestRect.bottom + mThumbTouchOffset;
    }

    /**
     * 是否点击滑动按钮
     *
     * @param x 横坐标
     * @param y 纵坐标
     * @return true点击 false未点击
     */
    private boolean clickOnProgress(float x, float y) {
        return mProgressBackgroundRect.left < mProgressBackgroundRect.right
                && mProgressBackgroundRect.top < mProgressBackgroundRect.bottom
                && x >= mProgressBackgroundRect.left - mThumbTouchOffset
                && x <= mProgressBackgroundRect.right + mThumbTouchOffset
                && y >= mProgressBackgroundRect.top - mThumbTouchOffset
                && y <= mProgressBackgroundRect.bottom + mThumbTouchOffset;
    }

    /**
     * 事件监听
     *
     * @param isFinished 是否完成滑动
     */
    private void onProgressChanged(boolean isFinished) {
        if (!mIsMovedOut) {
            mCurrProgress = xCoord2Progress(mThumbDestRect.centerX());
        }
        if (null != mOnColorChangedListener) {
            if (mLastProgress != mCurrProgress || isFinished) {
                mIsFromUser = true;
                mOnColorChangedListener.onChanged(this, true, isFinished);
                mIsFromUser = false;
                if (isFinished) { // 滑动结束后修正滑块的位置
                    float x = progress2XCoord(mCurrProgress);
                    moveToPoint(x);
                }
            }
            mLastProgress = mCurrProgress;
        }
        mIsMovedOut = false;
    }

    /**
     * 开始滑动
     *
     * @param x 横坐标
     */
    private void moveToPoint(float x) {
        float halfWidth = mProgressWidth / 2;
        if (x > halfWidth) { // 滑至最右边
            x = halfWidth;
            mIsMovedOut = true;
            mCurrProgress = mMaxProgress;
        } else if (x < -halfWidth) { // 滑至最左边
            x = -halfWidth;
            mIsMovedOut = true;
            mCurrProgress = mMinProgress;
        }
        mThumbDestRect.left = (int) (x - mThumbRadius);
        mThumbDestRect.right = (int) (x + mThumbRadius);
        mThumbShadowDestRect.left = (int) (x - mThumbShadowRadius);
        mThumbShadowDestRect.right = (int) (x + mThumbShadowRadius);
        invalidate();
    }

    /**
     * 缓存每一个刻度的颜色值
     **/
    private void cacheColors() {
        mCacheColor = getCacheColorList(mColorSeeds, mMaxProgress);
    }

    /**
     * x坐标转换为进度值
     *
     * @param x 横坐标
     * @return 进度值
     */
    private int xCoord2Progress(int x) {
        return Math.round((x + mProgressWidth / 2f)
                * (mMaxProgress - mMinProgress) / mProgressWidth)
                + mMinProgress;
    }

    /**
     * 进度值转换为x坐标
     *
     * @param progress 进度值
     * @return x坐标
     */
    private float progress2XCoord(int progress) {
        return (float) mProgressWidth * (progress - mMinProgress)
                / (mMaxProgress - mMinProgress) - mProgressWidth / 2f;
    }

    /**
     * 通过进度值获取颜色
     *
     * @param progress 进度
     * @return 颜色值
     */
    private int progress2Color(int progress) {
        return getCacheColor(mColorSeeds, progress, mMaxProgress);
    }

    /**
     * dip转px
     *
     * @param ctx     上下文
     * @param dpValue dip
     * @return the px
     */
    private int dip2px(Context ctx, float dpValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据array id获取颜色
     *
     * @param id array id
     * @return 颜色数组
     */
    private int[] getColorsById(Context context, int id) {
        if (isInEditMode()) {
            String[] s = context.getResources().getStringArray(id);
            int[] colors = new int[s.length];
            for (int i = 0; i < s.length; i++) {
                colors[i] = Color.parseColor(s[i]);
            }
            return colors;
        } else {
            TypedArray typedArray = context.getResources()
                    .obtainTypedArray(id);
            int[] colors = new int[typedArray.length()];
            for (int i = 0; i < typedArray.length(); i++) {
                colors[i] = typedArray.getColor(i, Color.BLACK);
            }
            typedArray.recycle();
            return colors;
        }
    }

    /**
     * 设置滑块投影的颜色
     *
     * @param color color
     */
    public void setThumbShadowColorFilter(@ColorInt int color, @NonNull PorterDuff.Mode mode) {
        if (null != mThumbShadowDrawable) {
            mThumbShadowDrawable.setColorFilter(color, mode);
        }
    }

    /**
     * 是否完成控制
     *
     * @return true 是
     */
    public boolean isFinished() {
        return !mIsClickOnProgress && !mIsClickOnThumb;
    }

    /**
     * 是否来自用户控制
     *
     * @return true 是
     */
    public boolean isFromUser() {
        return mIsFromUser;
    }

    /**
     * 获取所有颜色值
     *
     * @return list
     */
    public List<Integer> getCacheColor() {
        return mCacheColor;
    }

    /**
     * 获取当前颜色
     *
     * @return 可能返回0
     */
    public int getColor() {
        if (0 <= mCurrProgress && mCacheColor.size() > mCurrProgress) {
            return mCacheColor.get(mCurrProgress);
        }
        return 0;
    }

    /**
     * 获取上一次进度值
     *
     * @return 上一次进度值
     */
    public int getLastProgress() {
        return mLastProgress;
    }

    /**
     * 获取当前进度值
     *
     * @return 当前进度值
     */
    public int getProgress() {
        return mCurrProgress;
    }

    /**
     * 获取最大值
     *
     * @return 最大值
     */
    public int getMaxProgress() {
        return mMaxProgress;
    }

    /**
     * 设置最大值
     *
     * @param maxProgress 最大值
     */
    public void setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
    }

    /**
     * 设置进度值
     *
     * @param progress 进度
     */
    public void setProgress(int progress) {
        if (!isFinished()) {
            return; // 手动调节时不支持外部控制
        }
        if (progress >= mMinProgress && progress <= mMaxProgress) {
            mCurrProgress = progress;
            float x = progress2XCoord(mCurrProgress);
            moveToPoint(x);
            if (null != mOnColorChangedListener) {
                if (mLastProgress != mCurrProgress) {
                    mIsFromUser = false;
                    mOnColorChangedListener.onChanged(this, false, true);
                }
                mLastProgress = mCurrProgress;
            }
        }
    }

    /**
     * 设置颜色
     *
     * @param color 颜色值
     */
    public void setColor(int color) {
        if (null == mCacheColor) {
            return;
        }

        for (int i = 0; i < mCacheColor.size(); i++) {
            if (color == mCacheColor.get(i)) {
                setProgress(i);
                break;
            }
        }
    }

    /**
     * 设置渐变颜色数组
     *
     * @param colorSeeds 颜色数组
     */
    public void setColorSeeds(int[] colorSeeds) {
        if (null == colorSeeds) {
            return;
        }
        mColorSeeds = colorSeeds;
        mProgressBgDrawable.setColors(mColorSeeds);
        cacheColors();
        setProgress(mMinProgress);
    }

    /**
     * 设置监听
     *
     * @param listener 事件监听
     */
    public void setOnColorChangedListener(OnColorChangedListener listener) {
        mOnColorChangedListener = listener;
    }

    /**
     * 获取缓存颜色值
     *
     * @param colorSeeds  渐变颜色数组
     * @param maxProgress 进度最大值
     * @return list 长度为maxProgress+1
     */
    public static List<Integer> getCacheColorList(int[] colorSeeds, int maxProgress) {
        List<Integer> cacheColor = new ArrayList<>();
        for (int i = 0; i <= maxProgress; i++) {
            cacheColor.add(getCacheColor(colorSeeds, i, maxProgress));
        }
        return cacheColor;
    }

    /**
     * 获取某进度的颜色值
     *
     * @param colorSeeds  渐变颜色数组
     * @param progress    进度值
     * @param maxProgress 进度最大值
     * @return color
     */
    public static int getCacheColor(int[] colorSeeds, int progress, int maxProgress) {
        float unit = (float) progress / maxProgress;
        if (unit <= 0.0) {
            return colorSeeds[0];
        }
        if (unit >= 1) {
            return colorSeeds[colorSeeds.length - 1];
        }
        float colorPosition = unit * (colorSeeds.length - 1);
        int i = (int) colorPosition;
        colorPosition -= i;
        int c0 = colorSeeds[i];
        int c1 = colorSeeds[i + 1];
        int mRed = mix(Color.red(c0), Color.red(c1), colorPosition);
        int mGreen = mix(Color.green(c0), Color.green(c1), colorPosition);
        int mBlue = mix(Color.blue(c0), Color.blue(c1), colorPosition);
        return Color.rgb(mRed, mGreen, mBlue);
    }

    /**
     * 取混合颜色值
     **/
    public static int mix(int start, int end, float position) {
        return start + Math.round(position * (end - start));
    }
}
