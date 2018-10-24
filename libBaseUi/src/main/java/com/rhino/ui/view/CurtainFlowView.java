package com.rhino.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;

import com.rhino.ui.R;


/**
 * <p>窗帘动画</p>
 *
 * @author LuoLin
 * @since Created on 2017/4/8.
 **/
public class CurtainFlowView extends View {

    /**
     * 默认动画刷新延时时间（毫秒）
     **/
    private static final int DF_REFRESH_DELAY_TIME = 10;
    /**
     * 延迟刷新线程
     **/
    private RefreshFlowRunnable mRefreshFlowRunnable;

    /**
     * 左右开合
     **/
    public static final int TYPE_LEFT_RIGHT = 1;
    /**
     * 上下开合
     **/
    public static final int TYPE_UP_DOWN = 2;
    /**
     * 窗帘类型
     **/
    private int mType = TYPE_LEFT_RIGHT;

    /**
     * 视图高度
     **/
    private int mViewHeight;
    /**
     * 视图宽度
     **/
    private int mViewWidth;

    /**
     * 圆角矩形框
     **/
    private RectF mCornerRectF;
    /**
     * 圆角路径
     **/
    private Path mCornerPath;
    /**
     * 圆角
     **/
    private float[] mCornerArray;

    /**
     * 绘笔
     **/
    private Paint mPaint;
    /**
     * Bitmap
     **/
    private Bitmap mLeftBitmap, mRightBitmap, mUpDownBitmap;
    /**
     * Matrix
     **/
    private Matrix mLeftMatrix, mRightMatrix, mUpDownMatrix;

    /**
     * 当前设置百分比
     **/
    private int mSettingPercent = 0;
    /**
     * 当前打开百分比
     **/
    private int mCurrentPercent = 0;
    /**
     * 一步移动的距离
     **/
    private float mOneSpace = 0;

    public CurtainFlowView(Context context) {
        super(context);
        init();
    }

    public CurtainFlowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CurtainFlowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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

        setMeasuredDimension(mViewWidth, mViewHeight);
        initView(mViewWidth, mViewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        if (null != mCornerPath) {
            clipCorner(canvas);
        }
        drawFlow(canvas);
        canvas.restore();
    }

    /**
     * 绘制图片
     *
     * @param canvas canvas
     */
    private void drawFlow(Canvas canvas) {
        if (TYPE_LEFT_RIGHT == mType) {
            if (null != mLeftBitmap && !mLeftBitmap.isRecycled()) {
                canvas.drawBitmap(mLeftBitmap, mLeftMatrix, mPaint);
            }

            if (null != mRightBitmap && !mRightBitmap.isRecycled()) {
                canvas.drawBitmap(mRightBitmap, mRightMatrix, mPaint);
            }
        } else if (TYPE_UP_DOWN == mType) {
            if (null != mUpDownBitmap && !mUpDownBitmap.isRecycled()) {
                canvas.drawBitmap(mUpDownBitmap, mUpDownMatrix, mPaint);
            }
        }
    }

    /**
     * 资源初始化
     **/
    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);

        mLeftBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.img_curtain_left);
        mRightBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.img_curtain_right);
        mUpDownBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.img_curtain_up_down);

        mLeftMatrix = new Matrix();
        mRightMatrix = new Matrix();
        mUpDownMatrix = new Matrix();
    }

    /**
     * 初始化视图
     *
     * @param width  宽
     * @param height 高
     */
    private void initView(int width, int height) {
        if (0 >= width || 0 >= height) {
            return;
        }

        mLeftBitmap = zoomImage(mLeftBitmap, width, height);
        mLeftMatrix.postTranslate(0, 0);

        mRightBitmap = zoomImage(mRightBitmap, width, height);
        mRightMatrix.postTranslate(0, 0);

        mUpDownBitmap = zoomImage(mUpDownBitmap, width, height);
        mUpDownMatrix.postTranslate(0, 0);

        if (TYPE_LEFT_RIGHT == mType) {
            mOneSpace = (float) 1 / 100 * mLeftBitmap.getWidth();
        } else if (TYPE_UP_DOWN == mType) {
            mOneSpace = (float) 1 / 100 * mUpDownBitmap.getHeight();
        }

        if (mCurrentPercent != mSettingPercent) {
            float distance = (mSettingPercent - mCurrentPercent) * mOneSpace;
            if (0 != distance) {
                mCurrentPercent = mSettingPercent;
                doPostTranslate(distance);
                postInvalidate();
            }
        }
    }

    /**
     * 切圆角
     *
     * @param canvas canvas
     */
    private void clipCorner(Canvas canvas) {
        if (null == mCornerRectF || mCornerRectF.isEmpty()) {
            mCornerRectF = new RectF(0, 0, mViewWidth, mViewHeight);
        }
        if (mCornerPath.isEmpty()) {
            mCornerPath.addRoundRect(mCornerRectF, mCornerArray, Path.Direction.CW);
        }
        canvas.clipPath(mCornerPath);
    }

    /**
     * 开始动画
     */
    private void startFlow() {
        if (null == mRefreshFlowRunnable) {
            mRefreshFlowRunnable = new RefreshFlowRunnable();
        } else {
            removeCallbacks(mRefreshFlowRunnable);
        }
        post(mRefreshFlowRunnable);
    }

    /**
     * 停止动画
     */
    public void stopFlow() {
        if (null != mRefreshFlowRunnable) {
            removeCallbacks(mRefreshFlowRunnable);
        }
    }

    /**
     * 处理动画
     *
     * @param oneSpace 步进
     */
    private void doPostTranslate(float oneSpace) {
        if (TYPE_LEFT_RIGHT == mType) {
            mLeftMatrix.postTranslate(-oneSpace, 0);
            mRightMatrix.postTranslate(oneSpace, 0);
        } else if (TYPE_UP_DOWN == mType) {
            mUpDownMatrix.postTranslate(0, -oneSpace);
        }
    }

    /**
     * 刷新线程
     **/
    private class RefreshFlowRunnable implements Runnable {
        @Override
        public void run() {
            if (mCurrentPercent != mSettingPercent) {
                if (mCurrentPercent < mSettingPercent) {
                    mCurrentPercent++;
                    doPostTranslate(mOneSpace);
                } else {
                    mCurrentPercent--;
                    doPostTranslate(-mOneSpace);
                }
            } else {
                stopFlow();
                return;
            }
            invalidate();
            postDelayed(this, DF_REFRESH_DELAY_TIME);
        }
    }

    /**
     * 根据高度缩放Bitmap
     *
     * @param srcBitmap 源Bitmap
     * @param newHeight 新的height
     * @return 新的Bitmap
     */
    private Bitmap zoomImage(Bitmap srcBitmap, int newWidth, int newHeight) {
        if (null == srcBitmap || 0 >= srcBitmap.getWidth()
                || 0 >= srcBitmap.getHeight()
                || 0 >= newHeight
                || 0 >= newWidth) {
            return null;
        }
        int width = srcBitmap.getWidth();
        int height = srcBitmap.getHeight();
        Matrix matrix = new Matrix();
        float sw = (float) newWidth / width;
        float sh = (float) newHeight / height;
        matrix.setScale(sw, sh);
        srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, width, height,
                matrix, true);
        return srcBitmap;
    }

    /**
     * 设置窗帘开合类型
     *
     * @param type 开合类型 TYPE_XXX
     */
    public void setType(int type) {
        mType = type;
    }

    /**
     * 设置四个角的圆角
     *
     * @param leftTop     左上
     * @param rightTop    右上
     * @param rightBottom 右下
     * @param leftBottom  左下
     */
    public void setCorner(float leftTop, float rightTop, float rightBottom, float leftBottom) {
        mCornerArray = new float[]{leftTop, leftTop, rightTop, rightTop, rightBottom,
                rightBottom, leftBottom, leftBottom};
        mCornerPath = new Path();
    }

    /**
     * 设置窗帘颜色
     *
     * @param color 颜色值
     */
    public void setColorFilter(@ColorInt int color) {
        mPaint.setColorFilter(new PorterDuffColorFilter(color,
                PorterDuff.Mode.SRC_ATOP));
        invalidate();
    }

    /**
     * 设备当前打开开百分比
     *
     * @param percent 百分比0-100
     */
    public void setPercent(int percent, boolean isShowFlow) {
        if (0 > percent || 100 < percent) {
            return;
        }
        mSettingPercent = percent;
        if (isShowFlow) {//设备是否显示动画
            startFlow();
        } else {
            float distance = (mSettingPercent - mCurrentPercent) * mOneSpace;
            if (0 != distance) {
                mCurrentPercent = mSettingPercent;
                doPostTranslate(distance);
                invalidate();
            }
        }
    }

    /**
     * 释放内存
     */
    public void releaseAll() {
        if (null != mLeftBitmap && !mLeftBitmap.isRecycled()) {
            mLeftBitmap.recycle();
            mLeftBitmap = null;
        }

        if (null != mRightBitmap && !mRightBitmap.isRecycled()) {
            mRightBitmap.recycle();
            mRightBitmap = null;
        }

        if (null != mUpDownBitmap && !mUpDownBitmap.isRecycled()) {
            mUpDownBitmap.recycle();
            mUpDownBitmap = null;
        }
    }

}
