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
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import java.io.InputStream;

/**
 * <p>自定义移动图片动画</p>
 *
 * @author LuoLin
 * @since Created on 2017/3/30.
 **/
public class CustomFlowView extends AppCompatImageView {

    /**
     * 默认动画刷新延时时间（毫秒）
     **/
    private static final int DF_REFRESH_DELAY_TIME = 10;
    /**
     * 默认动画速度
     **/
    private static final int DF_FLOW_SPEED = 3;

    /**
     * 动画方向从左往右
     **/
    public static final int ANIM_LEFT_TO_RIGHT = 0x1;
    /**
     * 动画方向从右往左
     **/
    public static final int ANIM_RIGHT_TO_LEFT = 0x2;
    /**
     * 动画方向从上往下
     **/
    public static final int ANIM_TOP_TO_BOTTOM = 0x3;
    /**
     * 动画方向从下往上
     **/
    public static final int ANIM_BOTTOM_TO_TOP = 0x4;
    /**
     * 动画方向
     **/
    private int mAnimStatus = ANIM_LEFT_TO_RIGHT;

    /**
     * 上下文
     **/
    private Context mContext;
    /**
     * 视图高度
     **/
    private int mViewHeight;
    /**
     * 视图宽度
     **/
    private int mViewWidth;

    /**
     * 图片资源id
     **/
    @DrawableRes
    private int mImageResId;
    /**
     * 图片bitmap
     **/
    private Bitmap mBitmap;
    /**
     * paint
     **/
    private Paint mPaint;
    /**
     * 延迟刷新线程
     **/
    private RefreshFlowRunnable mRefreshFlowRunnable;
    /**
     * 动画图片left位置
     **/
    private int mAnimLeft;
    /**
     * 动画图片top位置
     **/
    private int mAnimTop;
    /**
     * 动画图片是否从视图外开始进入
     **/
    private boolean mFlowFromOuter;
    /**
     * 是否显示动画图片, 默认显示
     **/
    private boolean mFlowShow = true;
    /**
     * 是否已开始动画
     **/
    private boolean mFlowAnimShow;
    /**
     * 图片动画速度
     **/
    private float mFlowSpeed = DF_FLOW_SPEED;
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
     * 是否由图片两端一致，保证不跳变的场景
     **/
    private boolean mIsHopProtect;

    public CustomFlowView(Context context) {
        super(context);
        init(context);
    }

    public CustomFlowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomFlowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
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
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mViewWidth / 2, mViewHeight / 2);

        if (null != mCornerPath) {
            clipCorner(canvas);
        }

        if (mFlowShow) {
            drawFlower(canvas);
        }
        canvas.restore();
    }

    /**
     * 资源初始化
     **/
    private void init(Context context) {
        mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
    }

    /**
     * 切圆角
     *
     * @param canvas canvas
     */
    private void clipCorner(Canvas canvas) {
        if (null == mCornerRectF || mCornerRectF.isEmpty()) {
            mCornerRectF = new RectF(-mViewWidth / 2, -mViewHeight / 2, mViewWidth / 2, mViewHeight / 2);
        }
        if (mCornerPath.isEmpty()) {
            mCornerPath.addRoundRect(mCornerRectF, mCornerArray, Path.Direction.CW);
        }
        canvas.clipPath(mCornerPath);
    }

    /**
     * 绘制图片
     *
     * @param canvas canvas
     */
    private void drawFlower(Canvas canvas) {
        canvas.save();
        if (null == mBitmap || 0 == mBitmap.getHeight()) {
            initBitmap();
            initStartCoordinate();
        }

        if (null != mBitmap) {
            calculateFlowCoordinate();
            canvas.drawBitmap(mBitmap, mAnimLeft, mAnimTop, mPaint);
        }
        canvas.restore();
    }

    /**
     * 获取图片左边缘坐标
     **/
    private void calculateFlowCoordinate() {
        switch (mAnimStatus) {
            case ANIM_LEFT_TO_RIGHT:
                int tmp = mIsHopProtect ? mViewHeight - mViewWidth : 0;
                int startLeft = mFlowFromOuter ? mViewWidth / 2 : -mViewWidth / 2 - tmp;
                if (mAnimLeft >= -mViewHeight / 2) {
                    mAnimLeft = -startLeft - mBitmap.getWidth();
                } else {
                    mAnimLeft += mFlowSpeed;
                    if (mAnimLeft >= startLeft) {
                        mAnimLeft = startLeft;
                    }
                }
                mAnimTop = -mViewHeight / 2;
                break;
            case ANIM_RIGHT_TO_LEFT:
                int tmp1 = mIsHopProtect ? mViewHeight - mViewWidth : 0;
                int startLeft1 = mFlowFromOuter ? mViewWidth / 2 : -mViewWidth / 2 - tmp1;
                if (mAnimLeft <= mViewWidth / 2 - mBitmap.getWidth() || mAnimLeft > startLeft1) {
                    mAnimLeft = startLeft1;
                } else {
                    mAnimLeft -= mFlowSpeed;
                    if (mAnimLeft <= mViewWidth / 2 - mBitmap.getWidth()) {
                        mAnimLeft = mViewWidth / 2 - mBitmap.getWidth();
                    }
                }
                mAnimTop = -mViewHeight / 2;
                break;
            case ANIM_TOP_TO_BOTTOM:
                int startTop = mFlowFromOuter ? mViewHeight / 2 : -mViewHeight / 2;
                if (mAnimTop >= -mViewHeight / 2) {
                    mAnimTop = -startTop - mBitmap.getHeight();
                } else {
                    mAnimTop += mFlowSpeed;
                    if (mAnimTop >= startTop) {
                        mAnimTop = startTop;
                    }
                }
                mAnimLeft = -mViewWidth / 2;
                break;
            case ANIM_BOTTOM_TO_TOP:
                int startTop1 = mFlowFromOuter ? mViewHeight / 2 : -mViewHeight / 2;
                if (mAnimTop <= mViewHeight / 2 - mBitmap.getHeight() || mAnimTop > startTop1) {
                    mAnimTop = startTop1;
                } else {
                    mAnimTop -= mFlowSpeed;
                    if (mAnimTop <= mViewHeight / 2 - mBitmap.getHeight()) {
                        mAnimTop = mViewHeight / 2 - mBitmap.getHeight();
                    }
                }
                mAnimLeft = -mViewWidth / 2;
                break;
            default:
                break;
        }
    }

    /**
     * 设置图片
     *
     * @param resId 图片资源id
     */
    public void setFlowImage(@DrawableRes int resId) {
        mImageResId = resId;
        initBitmap();
    }

    /**
     * 设置图片渲染颜色
     *
     * @param color 颜色
     */
    public void setFlowImageColorFilter(int color) {
        mPaint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
    }

    /**
     * 设置动画方向
     *
     * @param status ANIM_xxx
     */
    public void setAnimStatus(int status) {
        mAnimStatus = status;
    }

    /**
     * 设置动画速度
     *
     * @param speed
     */
    public void setFlowSpeed(int speed) {
        mFlowSpeed = speed;
    }

    /**
     * 设置动画图片是否从视图外进入
     *
     * @param fromOuter true 从视图外进入
     */
    public void setFlowFromOuter(boolean fromOuter) {
        mFlowFromOuter = fromOuter;
    }

    /**
     * 设置动画图片是否显示
     *
     * @param show true 显示
     */
    public void setFlowShow(boolean show) {
        mFlowShow = show;
    }

    /**
     * 设置跳变保护，由图片两端一致，保证不跳变的场景
     *
     * @param hopProtect boolean
     */
    public void setHopProtect(boolean hopProtect) {
        mIsHopProtect = hopProtect;
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
     * 动画效果是否打开
     *
     * @return true 正在显示动画效果
     */
    public boolean isFlowAnimShow() {
        return mFlowAnimShow;
    }

    /**
     * 开始动画
     */
    public void startFlow() {
        if (mFlowAnimShow) {
            return;
        }
        if (null == mRefreshFlowRunnable) {
            mRefreshFlowRunnable = new RefreshFlowRunnable();
        } else {
            removeCallbacks(mRefreshFlowRunnable);
        }
        post(mRefreshFlowRunnable);
        mFlowAnimShow = true;
    }

    /**
     * 停止动画
     */
    public void stopFlow() {
        if (null != mRefreshFlowRunnable) {
            mFlowAnimShow = false;
            removeCallbacks(mRefreshFlowRunnable);
        }
    }

    /**
     * 刷新线程
     **/
    private class RefreshFlowRunnable implements Runnable {
        @Override
        public void run() {
            invalidate();
            postDelayed(this, DF_REFRESH_DELAY_TIME);
        }
    }

    /**
     * 初始化bitmap
     **/
    private void initBitmap() {
        if (ANIM_LEFT_TO_RIGHT == mAnimStatus
                || ANIM_RIGHT_TO_LEFT == mAnimStatus) {
            mBitmap = zoomImageByHeight(readBitmap(mContext, mImageResId), mViewHeight);
        } else if (ANIM_TOP_TO_BOTTOM == mAnimStatus
                || ANIM_BOTTOM_TO_TOP == mAnimStatus) {
            mBitmap = zoomImageByWidth(readBitmap(mContext, mImageResId), mViewWidth);
        }
    }

    /**
     * 初始化开始坐标
     **/
    private void initStartCoordinate() {
        switch (mAnimStatus) {
            case ANIM_LEFT_TO_RIGHT:
                int tmp = mIsHopProtect ? mViewHeight - mViewWidth : 0;
                mAnimLeft = mFlowFromOuter ? mViewHeight / 2 : -mViewHeight / 2 - tmp;
                mAnimTop = -mViewHeight / 2;
                break;
            case ANIM_RIGHT_TO_LEFT:
                int tmp1 = mIsHopProtect ? mViewHeight - mViewWidth : 0;
                mAnimLeft = mFlowFromOuter ? mViewWidth / 2 : -mViewWidth / 2 - tmp1;
                mAnimTop = -mViewHeight / 2;
                break;
            case ANIM_TOP_TO_BOTTOM:
                mAnimTop = mFlowFromOuter ? mViewHeight / 2 : -mViewHeight / 2;
                mAnimLeft = -mViewWidth / 2;
                break;
            case ANIM_BOTTOM_TO_TOP:
                mAnimTop = mFlowFromOuter ? mViewHeight / 2 : -mViewHeight / 2;
                mAnimLeft = -mViewWidth / 2;
                break;
            default:
                break;
        }
    }

    /**
     * 获取bitmap
     *
     * @param context 上下文
     * @param resId   资源id
     * @return bitmap
     */
    private Bitmap readBitmap(Context context, int resId) {
        if (0 == resId) {
            return null;
        }
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // opt.inSampleSize = 2;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 根据高度缩放Bitmap
     *
     * @param srcBitmap 源Bitmap
     * @param newHeight 新的height
     * @return 新的Bitmap
     */
    private Bitmap zoomImageByHeight(Bitmap srcBitmap, int newHeight) {
        if (null == srcBitmap || 0 >= srcBitmap.getWidth()
                || 0 >= srcBitmap.getHeight()
                || 0 >= newHeight) {
            return null;
        }
        int width = srcBitmap.getWidth();
        int height = srcBitmap.getHeight();
        Matrix matrix = new Matrix();
        float scale = (float) newHeight / height;
        matrix.setScale(scale, scale);
        srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, width, height,
                matrix, true);
        return srcBitmap;
    }

    /**
     * 根据宽度缩放Bitmap
     *
     * @param srcBitmap 源Bitmap
     * @param newWidth  新的width
     * @return 新的Bitmap
     */
    private Bitmap zoomImageByWidth(Bitmap srcBitmap, int newWidth) {
        if (null == srcBitmap || 0 >= srcBitmap.getWidth()
                || 0 >= srcBitmap.getHeight()
                || 0 >= newWidth) {
            return null;
        }
        int width = srcBitmap.getWidth();
        int height = srcBitmap.getHeight();
        Matrix matrix = new Matrix();
        float scale = (float) newWidth / width;
        matrix.setScale(scale, scale);
        srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, width, height,
                matrix, true);
        return srcBitmap;
    }

}
