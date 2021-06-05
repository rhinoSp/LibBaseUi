package com.rhino.ui.view.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import androidx.annotation.ColorInt;
import androidx.appcompat.widget.AppCompatImageView;

import android.util.AttributeSet;

import com.rhino.ui.R;


/**
 * <p>The custom to set corner freely of ImageView.</p>
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
 *      &lt;com.rhino.view.image.FreeCornerImageView
 *          android:id="@+id/FreeCornerImageView"
 *          android:layout_width="80dp"
 *          android:layout_height="80dp"
 *          android:layout_marginTop="10dp"
 *          android:src="@mipmap/ic_launcher"
 *          android:scaleType="centerCrop"
 *          app:fcim_stroke_width="2dp"
 *          app:fcim_stroke_color="#4B000000"
 *          app:fcim_corner_left_top="40dp"
 *          app:fcim_corner_left_bottom="40dp"
 *          app:fcim_corner_right_top="40dp"
 *          app:fcim_corner_right_bottom="40dp"
 *          app:fcim_center_background_color="#00000000"/&gt
 *
 *&lt;/RelativeLayout&gt
 *</pre>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public class FreeCornerImageView extends AppCompatImageView {

    private static final int DEFAULT_STROKE_WIDTH = 2;
    private static final int DEFAULT_STROKE_COLOR = 0x88000000;
    private static final int DEFAULT_CENTER_BACKGROUND_COLOR = 0x1A000000;
    private int mStrokeWidth = DEFAULT_STROKE_WIDTH;
    private int mStrokeColor = DEFAULT_STROKE_COLOR;
    private int mCenterBackgroundColor = DEFAULT_CENTER_BACKGROUND_COLOR;

    private int mViewHeight;
    private int mViewWidth;
    private RectF mCornerRectF;
    private Path mCornerPath;
    private float[] mCornerArray;
    private Paint mPaint;

    public FreeCornerImageView(Context context) {
        this(context, null);
    }

    public FreeCornerImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FreeCornerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
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
    protected void onDraw(Canvas canvas) {
        clipCorner(canvas);
        drawCenterBackground(canvas);
        super.onDraw(canvas);
        drawStroke(canvas);
    }

    private void init(Context context, AttributeSet attrs) {
        if (null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FreeCornerImageView);
            mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.FreeCornerImageView_fcim_stroke_width,
                    DEFAULT_STROKE_WIDTH);
            mStrokeColor = typedArray.getColor(R.styleable.FreeCornerImageView_fcim_stroke_color,
                    DEFAULT_STROKE_COLOR);
            mCenterBackgroundColor = typedArray.getColor(R.styleable.FreeCornerImageView_fcim_center_background_color,
                    DEFAULT_CENTER_BACKGROUND_COLOR);
            float leftTop = typedArray.getDimension(R.styleable.FreeCornerImageView_fcim_corner_left_top, 0);
            float rightTop = typedArray.getDimension(R.styleable.FreeCornerImageView_fcim_corner_right_top, 0);
            float rightBottom = typedArray.getDimension(R.styleable.FreeCornerImageView_fcim_corner_right_bottom, 0);
            float leftBottom = typedArray.getDimension(R.styleable.FreeCornerImageView_fcim_corner_left_bottom, 0);
            mCornerArray = new float[]{leftTop, leftTop, rightTop, rightTop, rightBottom,
                    rightBottom, leftBottom, leftBottom};
            typedArray.recycle();
        }
        mCornerPath = new Path();
        mCornerRectF = new RectF();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    /**
     * Draw center background.
     *
     * @param canvas Canvas
     */
    private void drawCenterBackground(Canvas canvas) {
        canvas.save();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mCenterBackgroundColor);
        canvas.drawPath(mCornerPath, mPaint);
        canvas.restore();
    }

    /**
     * Draw stroke.
     *
     * @param canvas Canvas
     */
    private void drawStroke(Canvas canvas) {
        canvas.save();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(mStrokeColor);
        canvas.drawPath(mCornerPath, mPaint);
        canvas.restore();
    }

    /**
     * Clip corner.
     *
     * @param canvas Canvas
     */
    private void clipCorner(Canvas canvas) {
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
     * Set width of stroke.
     * @param width width
     */
    public void setStrokeWidth(int width) {
        this.mStrokeWidth = width;
        invalidate();
    }

    /**
     * Set color of stroke.
     * @param color color
     */
    public void setStrokeColor(@ColorInt int color) {
        this.mStrokeColor = mStrokeColor;
        invalidate();
    }

    /**
     * Set color of center background.
     * @param color color
     */
    public void setCenterBackgroundColor(@ColorInt int color) {
        this.mCenterBackgroundColor = color;
        invalidate();
    }
}
