package com.rhino.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.rhino.ui.R;

/**
 * <p>The custom CheckBox with anim.</p>
 *Follow this example:
 *
 * <pre class="prettyprint">
 * &lt;?xml version="1.0" encoding="utf-8"?&gt</br>
 * &lt;RelativeLayout
 *      xmlns:android="http://schemas.android.com/apk/res/android"
 *      xmlns:app="http://schemas.android.com/apk/res-auto"
 *      android:layout_width="match_parent"
 *      android:layout_height="match_parent"&gt
 *
 *      &lt;com.rhino.view.AnimCheckBox
 *          android:id="@+id/AnimCheckBox"
 *          android:layout_width="150dp"
 *          android:layout_height="80dp"
 *          android:layout_centerInParent="true"
 *          app:acb_stroke_width="3dp"
 *          app:acb_unchecked_background_color="@color/white"
 *          app:acb_unchecked_stroke_color="@color/black_10"
 *          app:acb_unchecked_thumb_color="@color/white"
 *          app:acb_unchecked_thumb_shadow_color="@color/black_30"
 *          app:acb_checked_background_color="@color/theme_color"
 *          app:acb_checked_stroke_color="@color/theme_color"
 *          app:acb_checked_thumb_color="@color/white"
 *          app:acb_checked_thumb_shadow_color="@color/transparent"
 *          app:acb_thumb_shadow_radius="2dp"
 *          app:acb_thumb_shadow_x="-0.7dp"
 *          app:acb_thumb_shadow_y="0.7dp"
 *          app:acb_anim_enable="true"/&gt
 *
 *&lt;/RelativeLayout&gt
 *</pre>
 * @author LuoLin
 * @since Create on 2018/1/23.
 **/
public class AnimCheckBox extends View {

    private static final float DEFAULT_THUMB_SCALE_MAX_X = 0.1f;
    private static final int DEFAULT_CHECKED_BACKGROUND_COLOR = 0xFF008888;
    private static final int DEFAULT_UNCHECKED_BACKGROUND_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_CHECKED_STROKE_COLOR = 0xFF008888;
    private static final int DEFAULT_UNCHECKED_STROKE_COLOR = 0xFFDADBDA;
    private static final int DEFAULT_STROKE_WIDTH = 8;
    private static final int DEFAULT_CHECKED_THUMB_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_UNCHECKED_THUMB_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_THUMB_SHADOW_X = -2;
    private static final int DEFAULT_THUMB_SHADOW_Y = 2;
    private static final int DEFAULT_THUMB_SHADOW_RADIUS = 5;
    private static final int DEFAULT_CHECKED_THUMB_SHADOW_COLOR = 0xFF888888;
    private static final int DEFAULT_UNCHECKED_THUMB_SHADOW_COLOR = 0xFF888888;
    private int mCheckedBackgroundColor;
    private int mUncheckedBackgroundColor;
    private int mCheckedStrokeColor;
    private int mUncheckedStrokeColor;
    private int mStrokeWidth;
    private int mCheckedThumbColor;
    private int mUncheckedThumbColor;
    private int mThumbShadowX;
    private int mThumbShadowY;
    private int mThumbShadowRadius;
    private int mCheckedThumbShadowColor;
    private int mUncheckedThumbShadowColor;
    private boolean mAnimEnable;

    private int mViewWidth;
    private int mViewHeight;
    private Paint mPaint;
    private float[] mBackgroundCornerArray;
    private RectF mStrokeRect = new RectF();
    private Path mStrokePath = new Path();
    private RectF mBackgroundRect = new RectF();
    private Path mBackgroundPath = new Path();
    private RectF mThumbRect = new RectF();
    private Path mThumbPath = new Path();

    private ValueAnimator mThumbScaleAnimator;
    private float mThumbScaleMaxX;
    private float mThumbScaleX;

    private ValueAnimator mSetAnimator;

    private boolean mIsTouchThumb = false;
    private boolean mIsCancel = false;
    private boolean mChecked = false;
    private boolean mIsFromUser = false;
    private boolean mLastChecked = false;
    private OnCheckedChangeListener mOnCheckedChangeListener;


    public AnimCheckBox(Context context) {
        this(context, null);
    }

    public AnimCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
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
        initView(mViewWidth, mViewHeight);
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    private void init(Context context, AttributeSet attrs) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);

        if (null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AnimCheckBox);
            mCheckedBackgroundColor = typedArray.getColor(R.styleable.AnimCheckBox_acb_checked_background_color,
                    DEFAULT_CHECKED_BACKGROUND_COLOR);
            mUncheckedBackgroundColor = typedArray.getColor(R.styleable.AnimCheckBox_acb_unchecked_background_color,
                    DEFAULT_UNCHECKED_BACKGROUND_COLOR);
            mCheckedStrokeColor = typedArray.getColor(R.styleable.AnimCheckBox_acb_checked_stroke_color,
                    DEFAULT_CHECKED_STROKE_COLOR);
            mUncheckedStrokeColor = typedArray.getColor(R.styleable.AnimCheckBox_acb_unchecked_stroke_color,
                    DEFAULT_UNCHECKED_STROKE_COLOR);
            mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.AnimCheckBox_acb_stroke_width,
                    DEFAULT_STROKE_WIDTH);
            mCheckedThumbColor = typedArray.getColor(R.styleable.AnimCheckBox_acb_checked_thumb_color,
                    DEFAULT_CHECKED_THUMB_COLOR);
            mUncheckedThumbColor = typedArray.getColor(R.styleable.AnimCheckBox_acb_unchecked_thumb_color,
                    DEFAULT_UNCHECKED_THUMB_COLOR);
            mThumbShadowRadius = typedArray.getDimensionPixelSize(R.styleable.AnimCheckBox_acb_thumb_shadow_radius,
                    DEFAULT_THUMB_SHADOW_RADIUS);
            mThumbShadowX = typedArray.getDimensionPixelSize(R.styleable.AnimCheckBox_acb_thumb_shadow_x,
                    DEFAULT_THUMB_SHADOW_X);
            mThumbShadowY = typedArray.getDimensionPixelSize(R.styleable.AnimCheckBox_acb_thumb_shadow_y,
                    DEFAULT_THUMB_SHADOW_Y);
            mCheckedThumbShadowColor = typedArray.getColor(R.styleable.AnimCheckBox_acb_checked_thumb_shadow_color,
                    DEFAULT_CHECKED_THUMB_SHADOW_COLOR);
            mUncheckedThumbShadowColor = typedArray.getColor(R.styleable.AnimCheckBox_acb_unchecked_thumb_shadow_color,
                    DEFAULT_UNCHECKED_THUMB_SHADOW_COLOR);
            mAnimEnable = typedArray.getBoolean(R.styleable.AnimCheckBox_acb_anim_enable,
                    true);
            typedArray.recycle();
        }
    }

    /**
     * Do something init.
     *
     * @param width  width
     * @param height height
     */
    private void initView(int width, int height) {
        if (0 >= width || 0 >= height) {
            return;
        }

        mStrokeRect.top = 0;
        mStrokeRect.left = 0;
        mStrokeRect.bottom = height;
        mStrokeRect.right = width;

        float strokeCorner = mStrokeRect.height() / 2;
        mStrokePath.reset();
        mStrokePath.addRoundRect(mStrokeRect, new float[]{strokeCorner, strokeCorner, strokeCorner, strokeCorner, strokeCorner,
                strokeCorner, strokeCorner, strokeCorner}, Path.Direction.CW);

        mBackgroundRect.top = mStrokeWidth;
        mBackgroundRect.left = mStrokeWidth;
        mBackgroundRect.bottom = height - mStrokeWidth;
        mBackgroundRect.right = width - mStrokeWidth;

        float backgroundCorner = mBackgroundRect.height() / 2;
        mBackgroundCornerArray = new float[]{backgroundCorner, backgroundCorner, backgroundCorner, backgroundCorner, backgroundCorner,
                backgroundCorner, backgroundCorner, backgroundCorner};
        mBackgroundPath.reset();
        mBackgroundPath.addRoundRect(mBackgroundRect, mBackgroundCornerArray, Path.Direction.CW);

        mThumbRect.top = mStrokeWidth;
        mThumbRect.bottom = height - mStrokeWidth;
        calculateThumbRect(mChecked ? mStrokeRect.right : mStrokeRect.left);

        mThumbScaleMaxX = mThumbRect.height() * DEFAULT_THUMB_SCALE_MAX_X;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.save();
        drawStroke(canvas);
        drawBackground(canvas);
        drawThumb(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsTouchThumb = mThumbRect.contains(x, y);
                startTouchAnim(true);
                mLastChecked = mChecked;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsTouchThumb) {
                    calculateThumbRect(x);
                    boolean check = mThumbRect.centerX() >= mStrokeRect.centerX();
                    mIsCancel = mLastChecked == check;
                    mChecked = check;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsTouchThumb = false;
                boolean check = mThumbRect.centerX() >= mStrokeRect.centerX();
                if (!mIsCancel && mLastChecked == check) {
                    setChecked(!check, false, true);
                } else {
                    if (null != mOnCheckedChangeListener && mLastChecked != check) {
                        mOnCheckedChangeListener.onCheckedChanged(this, check, true);
                    }
                }
                mChecked = check;
                startTouchAnim(false);
                mLastChecked = check;
                mIsCancel = false;
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * Draw the stroke.
     *
     * @param canvas canvas
     */
    private void drawStroke(Canvas canvas) {
        canvas.save();
        mPaint.setColor(mChecked ? mCheckedStrokeColor : mUncheckedStrokeColor);
        canvas.drawPath(mStrokePath, mPaint);
        canvas.restore();
    }

    /**
     * Draw the background.
     *
     * @param canvas canvas
     */
    private void drawBackground(Canvas canvas) {
        canvas.save();
        mPaint.setColor(mChecked ? mCheckedBackgroundColor : mUncheckedBackgroundColor);
        canvas.drawPath(mBackgroundPath, mPaint);
        canvas.restore();
    }

    /**
     * Draw the circle of thumb.
     *
     * @param canvas canvas
     */
    private void drawThumb(Canvas canvas) {
        canvas.save();
        mPaint.setColor(mChecked ? mCheckedThumbColor : mUncheckedThumbColor);
        mPaint.setShadowLayer(mThumbShadowRadius, mThumbShadowX, mThumbShadowY,
                mChecked ? mCheckedThumbShadowColor : mUncheckedThumbShadowColor);
        canvas.drawPath(mThumbPath, mPaint);
        mPaint.clearShadowLayer();
        canvas.restore();
    }

    /**
     * Calculate the thumb rect.
     *
     * @param thumbCenterX the center x of thumb rect.
     */
    private void calculateThumbRect(float thumbCenterX) {
        if (thumbCenterX < mStrokeRect.left + mStrokeWidth + mThumbRect.height() / 2 + mThumbScaleX) {
            thumbCenterX = mStrokeRect.left + mStrokeWidth + mThumbRect.height() / 2 + mThumbScaleX;
        }

        if (thumbCenterX > mStrokeRect.right - mStrokeWidth - mThumbRect.height() / 2 - mThumbScaleX) {
            thumbCenterX = mStrokeRect.right - mStrokeWidth - mThumbRect.height() / 2 - mThumbScaleX;
        }

        mThumbRect.left = thumbCenterX - mThumbRect.height() / 2 - mThumbScaleX;
        mThumbRect.right = thumbCenterX + mThumbRect.height() / 2 + mThumbScaleX;
        mThumbPath.reset();
        mThumbPath.addRoundRect(mThumbRect, mBackgroundCornerArray, Path.Direction.CW);
    }

    /**
     * Calculate the background rect.
     *
     * @param scale the anim scale of background.
     */
    private void calculateBackgroundRect(float scale) {
        mBackgroundRect.top = mViewHeight / 2 - (mViewHeight / 2 - mStrokeWidth) * scale;
        mBackgroundRect.left = mViewWidth / 2 - (mViewWidth / 2 - mStrokeWidth) * scale;
        mBackgroundRect.bottom = mViewHeight / 2 + (mViewHeight / 2 - mStrokeWidth) * scale;
        mBackgroundRect.right = mViewWidth / 2 + (mViewWidth / 2 - mStrokeWidth) * scale;

        mBackgroundPath.reset();
        mBackgroundPath.addRoundRect(mBackgroundRect, mBackgroundCornerArray, Path.Direction.CW);
    }

    /**
     * Start touch anim.
     *
     * @param enlarge enlarge
     */
    private void startTouchAnim(boolean enlarge) {
        float start = enlarge ? 0 : 1;
        float stop = enlarge ? 1 : 0;
        if (null == mThumbScaleAnimator) {
            mThumbScaleAnimator = new ValueAnimator();
            mThumbScaleAnimator.setInterpolator(new DecelerateInterpolator());
            mThumbScaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (Float) animation.getAnimatedValue();
                    calculateBackgroundRect(1 - value);

                    mThumbScaleX = mThumbScaleMaxX * value;
                    if (!mIsTouchThumb) {
                        if (1.0f == animation.getAnimatedFraction()) {
                            mChecked = mThumbRect.centerX() >= mStrokeRect.centerX();
                            calculateThumbRect(mThumbRect.centerX() > mStrokeRect.centerX()
                                    ? mStrokeRect.right : mStrokeRect.left);
                        } else {
                            calculateThumbRect(mThumbRect.centerX() <= mStrokeRect.centerX()
                                    ? mThumbRect.centerX() - (mThumbScaleMaxX - mThumbScaleX)
                                    : mThumbRect.centerX() + mThumbScaleX);
                        }
                    } else {
                        calculateThumbRect(mThumbRect.centerX());
                    }
                    invalidate();
                }
            });
        } else {
            mThumbScaleAnimator.cancel();
        }
        mThumbScaleAnimator.setFloatValues(start, stop);
        mThumbScaleAnimator.setDuration(mAnimEnable ? 200 : 0);
        mThumbScaleAnimator.start();
    }

    /**
     * Start set anim.
     *
     * @param checked checked
     */
    private void startSetAnim(boolean checked) {
        float start = checked ? 1 : -1;
        float stop = checked ? -1 : 1;
        if (null == mSetAnimator) {
            mSetAnimator = new ValueAnimator();
            mSetAnimator.setDuration(200);
            mSetAnimator.setInterpolator(new DecelerateInterpolator());
            mSetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (Float) animation.getAnimatedValue();
                    calculateBackgroundRect(Math.abs(value));

                    mThumbScaleX = mThumbScaleMaxX * (1 - Math.abs(value));
                    float thumbXDistance = mStrokeRect.width() - 2 * mStrokeWidth - mThumbRect.height() - 2 * mThumbScaleX;
                    float x = mStrokeRect.centerX() - thumbXDistance / 2 * value;
                    calculateThumbRect(x);

                    invalidate();
                }
            });
        } else {
            mSetAnimator.cancel();
        }
        mSetAnimator.setFloatValues(start, stop);
        mSetAnimator.start();
    }

    public interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param checkBox  The CheckBox view whose state has changed.
         * @param isChecked The new checked state of buttonView.
         * @param fromUser  The new checked state changed by user.
         */
        void onCheckedChanged(AnimCheckBox checkBox, boolean isChecked, boolean fromUser);
    }

    /**
     * Register a callback to be invoked when the checked state of this button
     * changes.
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    /**
     * <p>Changes the checked state of this button.</p>
     *
     * @param checked true to check the button, false to uncheck it
     */
    public void setChecked(boolean checked) {
        setChecked(checked, true, false);
    }

    /**
     * <p>Changes the checked state of this button.</p>
     *
     * @param checked true to check the button, false to uncheck it
     * @param anim    true with anim, false without anim
     */
    public void setChecked(boolean checked, boolean anim) {
        setChecked(checked, anim, false);
    }

    /**
     * <p>Changes the checked state of this button.</p>
     *
     * @param checked  true to check the button, false to uncheck it
     * @param anim     true with anim, false without anim
     * @param fromUser true by user, false not by user
     */
    public void setChecked(boolean checked, boolean anim, boolean fromUser) {
        if (mChecked != checked) {
            if (null != mOnCheckedChangeListener) {
                mIsFromUser = fromUser;
                mOnCheckedChangeListener.onCheckedChanged(this, checked, mIsFromUser);
                mIsFromUser = false;
            }
            mChecked = checked;
            if (anim) {
                startSetAnim(checked);
            } else {
                calculateThumbRect(checked ? mStrokeRect.right : mStrokeRect.left);
                invalidate();
            }
        }
    }

    /**
     * Whether checked.
     *
     * @return true checked, false unchecked
     */
    public boolean isChecked() {
        return mChecked;
    }

    /**
     * Whether changed by user.
     *
     * @return true by user, false not by user
     */
    public boolean isFromUser() {
        return mIsFromUser;
    }

    /**
     * Set the background color when checked.
     *
     * @param color color
     */
    public void setCheckedBackgroundColor(@ColorInt int color) {
        this.mCheckedBackgroundColor = color;
        invalidate();
    }

    /**
     * Set the background color when unchecked.
     *
     * @param color color
     */
    public void setUncheckedBackgroundColor(@ColorInt int color) {
        this.mUncheckedBackgroundColor = color;
        invalidate();
    }

    /**
     * Set the stroke color when checked.
     *
     * @param color color
     */
    public void setCheckedStrokeColor(@ColorInt int color) {
        this.mCheckedStrokeColor = color;
        invalidate();
    }

    /**
     * Set the stroke color when unchecked.
     *
     * @param color color
     */
    public void setUncheckedStrokeColor(@ColorInt int color) {
        this.mUncheckedStrokeColor = color;
        invalidate();
    }

    /**
     * Set the stroke width.
     *
     * @param width width
     */
    public void setStrokeWidth(int width) {
        this.mStrokeWidth = width;
        invalidate();
    }

    /**
     * Set the thumb color when checked.
     *
     * @param color color
     */
    public void setCheckedThumbColor(@ColorInt int color) {
        this.mCheckedThumbColor = color;
        invalidate();
    }

    /**
     * Set the thumb color when unchecked.
     *
     * @param color color
     */
    public void setUncheckedThumbColor(@ColorInt int color) {
        this.mUncheckedThumbColor = color;
        invalidate();
    }

    /**
     * Set the thumb shadow x.
     *
     * @param x x
     */
    public void setThumbShadowX(int x) {
        this.mThumbShadowX = x;
        invalidate();
    }

    /**
     * Set the thumb shadow y.
     *
     * @param y y
     */
    public void setThumbShadowY(int y) {
        this.mThumbShadowY = y;
        invalidate();
    }

    /**
     * Set the thumb shadow radius.
     *
     * @param radius radius
     */
    public void setThumbShadowRadius(int radius) {
        this.mThumbShadowRadius = radius;
        invalidate();
    }

    /**
     * Set the thumb shadow color when checked.
     *
     * @param color color
     */
    public void setCheckedThumbShadowColor(@ColorInt int color) {
        this.mCheckedThumbShadowColor = color;
        invalidate();
    }

    /**
     * Set the thumb shadow color when unchecked.
     *
     * @param color color
     */
    public void setUncheckedThumbShadowColor(@ColorInt int color) {
        this.mUncheckedThumbShadowColor = color;
        invalidate();
    }

    /**
     * Set the enable of anim.
     *
     * @param enable enable
     */
    public void setAnimEnable(boolean enable) {
        this.mAnimEnable = enable;
    }

}