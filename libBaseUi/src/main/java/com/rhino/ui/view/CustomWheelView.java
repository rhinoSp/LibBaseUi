package com.rhino.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.NumberPicker;
import android.widget.OverScroller;

import com.rhino.ui.R;


/**
 * 示例
 * <pre>
 * CustomWheelView cwv = new CustomWheelView(this);//注意以下方法的调用顺序
 * cwv.setWrapSelectorWheel(false);//设置是否循环
 * cwv.setNormalTextSize(50);//可以不设置
 * cwv.setMaxValue(dispItems.length - 1);
 * cwv.setValue(x);
 * cwv.setDisplayedValues(dispItems);
 * cwv.setOnValueChangedListener(new OnValueChangeListener());
 * cwv.setLayoutParams(new LayoutParams(-1, 300));//需要设置一个高度
 * cwv.setMiddSignEnable(true);//设置中心标志是否显示
 * </pre>
 *
 * <pre>
 * &lt;com.custom.view.CustomWheelView
 *       xmlns:app="http://schemas.android.com/apk/res-auto"
 *       android:id="@+id/custom_wheel"
 *       android:layout_width="match_parent"
 *       android:layout_height="0dp"
 *       android:layout_weight="4"
 *       android:background="#ffffffff"
 *       app:wheelStyle="vertical"
 *       app:alphaFac="0.5"
 *       app:autoAjustItemHeight="true"
 *       app:verticalSignLength="0.4"
 *       app:itemTextColor="#ff000000"/&gt;
 *
 * </pre>
 *
 *
 * @attr ref R.styleable.CustomWheelView_wheelStyle
 * @attr ref R.styleable.CustomWheelView_alphaFac
 * @attr ref R.styleable.CustomWheelView_autoAjustItemHeight
 * @attr ref R.styleable.CustomWheelView_itemTextColor
 * @attr ref R.styleable.CustomWheelView_itemUnableTextColor
 * @attr ref R.styleable.CustomWheelView_showMiddleSign
 * @attr ref R.styleable.CustomWheelView_verticalSignLength
 * @see NumberPicker
 *
 * @author LuoLin
 * @since Created on 2018/1/27.
 **/
@SuppressWarnings(value = {"all"})
public class CustomWheelView extends View {
    private static final int STYLE_HORIZONTAL = 1;
    private static final int STYLE_VERTICAL = 2;
    /*scroll fling duration*/
    private static final int SNAP_SCROLL_DURATION = 300;
    /*adjust duration*/
    private static final int SELECTOR_ADJUSTMENT_DURATION_MILLIS = 800;
    /*fling 最大速率值 调整系数*/
    private static final int SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT = 2;
    /*item text min alpha value*/
    private static final float SF_TXT_ALPHA_FAC = 0.1f;

    private static final int SF_DEF_ITEM_COUNT = 7;

    private static final int SF_TEXT_NORMAL_COLOR = Color.WHITE;
    private static final int SF_TEXT_UNABLE_COLOR = 0xFF808080;

    //item的文字最大多少， 是item宽度的0.8还是item高度的0.76
    private static final float SF_ITEMTXT_MAX_SCALE_TO_WIDTH = 0.85F;
    private static final float SF_ITEMTXT_MAX_SCALE_TO_HEIGHT = 0.76F;

    private int mVerticalSignLength;
    // 相对与width的比例
    private float mVerticalSignScale;
    private float mAlphaFac = SF_TXT_ALPHA_FAC;
    // 显示出来的项数
    private int mWheelItemCount = SF_DEF_ITEM_COUNT;

    private TextPaint mPaint;

    private OverScroller mFlingScroller;
    private OverScroller mAdjustScroller;

    private VelocityTracker mVelocityTracker;

    // 滑动触发的最小值
    private int mTouchSlop;
    private int mMinimumFlingVelocity;
    private int mMaximumFlingVelocity;

    /*
     *  记录需要绘制的item内容的位置 e.g.
     *  12345、23456等，如果是-1代表无效项。
     */
    private int[] mSelectorIndices = new int[mWheelItemCount];

    // 用于绘制每一个item
    private ItemRect[] mItemPostions = new ItemRect[mWheelItemCount];
    private final SparseArray<String> mSelectorIndexToStringCache = new SparseArray<String>();
    private String[] mItemsDrawContents;

    private int mWidth;
    private int mHeight;
    private int mHalfWidth;
    /*
     * 默认是36dp
     */
    private int mItemHeight;

    //用于Y轴滚轮
    private int mTotalHeight;
    private int mOffsetTotalHeight;
    private boolean mWheelStyleVertical;
    private boolean mWheelEnableScrollOffset = true;

    private boolean mAutoAdjustItemHeight = false;

    private boolean mHandleScrollChange;//设置的值 不调用监听

    // 当前是第几项
    private int mValue;
    // 当前偏移
    private int mCurrentScrollOffset;
    // 默认偏移
    private int mInitialScrollOffset;

    // item宽度或者高度
    private int mSelectorElementSize;
    private int mElementTrigSize;

    private boolean mShowMiddSign = true;
    private int mMiddSignPadding;// 0.16f;
    private int mMiddSignHeight;// 0.12f;
    private int mMiddSignWidth = 1;// dp
    private Rect mMiddSignRect = new Rect();

    private int mWheelMiddIdx = mWheelItemCount / 2;

    private int mMinValue = 0;// 最小下标
    private int mMaxValue = 10;// 最大下标

    private int mTextSize;
    private int mTextNormalColor = SF_TEXT_NORMAL_COLOR;// 0xff0080ff;
    private int mUnableTextColor = SF_TEXT_UNABLE_COLOR;
    private int mMiddSignAlpha = 0x66;

    // 是否循环
    private boolean mWrapSelectorWheel = true;

    private float mLastDownEventX;
    private float mLastDownOrMoveEventX;
    private float mLastDownEventY;
    private float mLastDownOrMoveEventY;

    private int mScrollState;

    private int mPreviousScrollerX;
    private int mPreviousScrollerY;

    private OnValueChangeListener mOnValueChangeListener;
    private OnScrollListener mOnScrollListener;
    private boolean isDefaultSound = false;


    public CustomWheelView(Context context) {
        this(context, null);
    }

    public CustomWheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomWheelView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, 0);
        init(context, attrs);
    }

    /**
     * set wheel value, scroll to destination
     *
     * @param value
     */
    public void scrollChangeValue(int value) {
        if (null != mVelocityTracker) {
            // 如果正在滑动控制  那么不响应设置
            return;
        }
        if (mWidth == 0) {
            setValueInternal(value, false);
        } else if (getScrollState() == OnScrollListener.SCROLL_STATE_IDLE) {
            if (mAdjustScroller.isFinished()) {
                changeValueByValue(value, 0);
            }
        }
    }

    /**
     * set wheel value
     *
     * @param value
     */
    public void setValue(int value) {
        setValueInternal(value, false);
    }

    /**
     * set value change listener
     *
     * @param onValueChangedListener
     */
    public void setOnValueChangedListener(
            OnValueChangeListener onValueChangedListener) {
        mOnValueChangeListener = onValueChangedListener;
    }

    /**
     * scroll listener
     *
     * @param onScrollListener
     */
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    /**
     * @return true or false
     */
    public boolean isVerticalWheel() {
        return mWheelStyleVertical;
    }

    /**
     * 是否支持滚动的时候进行缩放
     *
     * @param itemScale 默认 true 支持
     */
    public void setEnableItemOffset(boolean itemScale) {
        mWheelEnableScrollOffset = itemScale;
    }

    /**
     * 设置wheel类型 必须在onDraw之前调用
     *
     * @param verticalStyle 默认false 水平滚轮
     */
    public void setWheelStyle(boolean verticalStyle) {
        this.mWheelStyleVertical = verticalStyle;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            if (null != mVelocityTracker) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
            return true;
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastDownEventX = event.getX();
                mLastDownEventY = event.getY();
                if (!mFlingScroller.isFinished()) {
                    mFlingScroller.forceFinished(true);
                    mAdjustScroller.forceFinished(true);
                } else if (!mAdjustScroller.isFinished()) {
                    mFlingScroller.forceFinished(true);
                    mAdjustScroller.forceFinished(true);
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
                break;
            case MotionEvent.ACTION_MOVE: {
                float currentMoveX = event.getX();
                float currentMoveY = event.getY();
                if (mScrollState != OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    int deltaDown;
                    if (isVerticalWheel()) {
                        deltaDown = (int) Math.abs(currentMoveY - mLastDownEventY);
                    } else {
                        deltaDown = (int) Math.abs(currentMoveX - mLastDownEventX);
                    }

                    if (deltaDown > mTouchSlop) {
                        onScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
                    }
                } else {
                    int deltaMoveX = (int) (currentMoveX - mLastDownOrMoveEventX);
                    int deltaMoveY = (int) (currentMoveY - mLastDownOrMoveEventY);
                    scrollBy(deltaMoveX, deltaMoveY);
                    invalidate();
                }
                mLastDownOrMoveEventX = currentMoveX;
                mLastDownOrMoveEventY = currentMoveY;
            }
            break;
            case MotionEvent.ACTION_UP:
                VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                int initialVelocity = isVerticalWheel() ? (int) velocityTracker.getYVelocity() : (int) velocityTracker.getXVelocity();
                if (Math.abs(initialVelocity) > mMinimumFlingVelocity) {
                    fling(initialVelocity);
                    onScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
                } else {
                    int eventX = (int) event.getX();
                    int deltaMoveX = (int) Math.abs(eventX - mLastDownEventX);
                    if (deltaMoveX <= mTouchSlop) {
                        // 处理点击 一个一个加减问题
                	/*int selectorIndexOffset = (eventX / mSelectorElementWidth)
                            - SELECTOR_MIDDLE_ITEM_INDEX;
                    int changeValue = mValue;
                	if (selectorIndexOffset > 0) {
                		if (changeValue == mMaxValue) {
                			if (mWrapSelectorWheel) {
                				changeValue = mMinValue;
                			}
                		} else {
                			changeValue++;
                		}
                    } else if (selectorIndexOffset < 0) {
                    	if (changeValue == mMinValue) {
                			if (mWrapSelectorWheel) {
                				changeValue = mMaxValue;
                			}
                		} else {
                			changeValue--;
                		}
                    }
                    changeValueByValue(changeValue, 0);*/
                        // 如果不处理点击  需要调用调整方法
                        ensureScrollWheelAdjusted();
                    } else {
                        ensureScrollWheelAdjusted();
                    }
                    onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
                }
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 初始化 滚轮项的显示个数  需要优先设置
     *
     * @param n 范围>=3
     */
    public void setWheelItemCount(int n) {
        mWheelItemCount = (n <= 2 ? SF_DEF_ITEM_COUNT : n);
        mItemPostions = new ItemRect[mWheelItemCount];
        mSelectorIndices = new int[mWheelItemCount];
        mWheelMiddIdx = mWheelItemCount / 2;
    }

    public int getWheelItemCount() {
        return mWheelItemCount;
    }

    /**
     * 是否显示中间的指示图
     *
     * @param show
     */
    public void setMiddSignEnable(boolean show) {
        this.mShowMiddSign = show;
    }

    /**
     * text size
     *
     * @param textSize
     */
    public void setNormalTextSize(int textSize) {
        this.mTextSize = textSize;
    }

    /**
     * normal text color
     *
     * @param color
     */
    public void setNormalTextColor(int color) {
        this.mTextNormalColor = color;
    }

    /**
     * unable status text color
     *
     * @param color
     */
    public void setUnableTextColor(int color) {
        this.mUnableTextColor = color;
    }

    public int getScrollState() {
        return mScrollState;
    }

    /**
     * current wheel value
     *
     * @return
     */
    public int getValue() {
        return mValue;
    }

    /**
     * 是否循环显示
     *
     * @param wrapSelectorWheel
     */
    public void setWrapSelectorWheel(boolean wrapSelectorWheel) {
        mWrapSelectorWheel = wrapSelectorWheel;
        final boolean wrappingAllowed = (mMaxValue - mMinValue) >= mSelectorIndices.length;
        if ((!wrapSelectorWheel || wrappingAllowed) && wrapSelectorWheel != mWrapSelectorWheel) {
            mWrapSelectorWheel = wrapSelectorWheel;
        }
    }

    public boolean isCyclicWheel() {
        return mWrapSelectorWheel;
    }

    /**
     * item strings
     *
     * @param displayedValues
     */
    public void setDisplayedValues(String[] displayedValues) {
        if (mItemsDrawContents == displayedValues) {
            return;
        }
        mItemsDrawContents = displayedValues;
        initializeSelectorWheelIndices();
    }

    /**
     * 如果纯数字， 设置最小值（默认0）和最大值
     *
     * @param minValue
     */
    public void setMinValue(int minValue) {
        if (mMinValue == minValue) {
            return;
        }
        if (minValue < 0) {
            throw new IllegalArgumentException("minValue must be >= 0");
        }
        mMinValue = minValue;
        if (mMinValue > mValue) {
            mValue = mMinValue;
        }

        initializeSelectorWheelIndices();
        invalidate();
    }

    /**
     * 如果纯数字， 设置最小值（默认0）和最大值
     *
     * @param maxValue
     */
    public void setMaxValue(int maxValue) {
        if (mMaxValue == maxValue) {
            return;
        }
        if (maxValue < 0) {
            throw new IllegalArgumentException("maxValue must be >= 0");
        }
        mMaxValue = maxValue;
        if (mMaxValue < mValue) {
            mValue = mMaxValue;
        }

        initializeSelectorWheelIndices();
        invalidate();
    }

    /**
     * 设置透明度的因子  (0, 1]，越小代表边缘区域越透明
     *
     * @param fac
     */
    public void setAlhpaFac(float fac) {
        this.mAlphaFac = fac;
    }


    @Override
    public void scrollBy(int x, int y) {
        int space = isVerticalWheel() ? y : x;
        int[] selectorIndices = mSelectorIndices;
        if (space > 0 && isDecrementToEnd()) {
            return;
        }
        if (space < 0 && isIncrementToEnd()) {
            return;
        }
        //FIXME 需要解决 边缘效应
        mCurrentScrollOffset += space;
        while (mCurrentScrollOffset - mInitialScrollOffset > mElementTrigSize) {
            mCurrentScrollOffset -= mSelectorElementSize;
            decrementSelectorIndices(selectorIndices);
            setValueInternal(selectorIndices[mWheelMiddIdx], true);
            if (isDecrementToEnd()) {
                mCurrentScrollOffset = mInitialScrollOffset;
            }
        }
        while (mCurrentScrollOffset - mInitialScrollOffset < -mElementTrigSize) {
            mCurrentScrollOffset += mSelectorElementSize;
            incrementSelectorIndices(selectorIndices);
            setValueInternal(selectorIndices[mWheelMiddIdx], true);
            if (isIncrementToEnd()) {
                mCurrentScrollOffset = mInitialScrollOffset;
            }
        }
    }

    public int getItemHeight() {
        return mItemHeight;
    }

    /**
     * 设置每项的高度  vertical 有效
     *
     * @param height
     */
    public void setItemHeight(int height) {
        mItemHeight = height;
    }

    @Override
    public void computeScroll() {
        OverScroller scroller = mFlingScroller;
        if (scroller.isFinished()) {
            scroller = mAdjustScroller;
            if (scroller.isFinished()) {
                return;
            }
        }
        scroller.computeScrollOffset();

        if (isVerticalWheel()) {
            int currentScrollerY = scroller.getCurrY();
            if (mPreviousScrollerY == 0) {
                mPreviousScrollerY = scroller.getStartY();
            }
            scrollBy(0, currentScrollerY - mPreviousScrollerY);
            mPreviousScrollerY = currentScrollerY;
        } else {
            int currentScrollerX = scroller.getCurrX();
            if (mPreviousScrollerX == 0) {
                mPreviousScrollerX = scroller.getStartX();
            }
            scrollBy(currentScrollerX - mPreviousScrollerX, 0);
            mPreviousScrollerX = currentScrollerX;
        }


        if (scroller.isFinished()) {
            onScrollerFinished(scroller);
        } else {
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        initViewSize();
        if (isVerticalWheel()) {
            drawVertical(canvas);
        } else {
            drawHorizontal(canvas);
        }
    }

    //XXX 处理布局中带来的view 尺寸
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void drawHorizontal(Canvas canvas) {
        int color = isEnabled() ? mTextNormalColor : mUnableTextColor;
        int offset = mCurrentScrollOffset;
        for (int i = 0; i < mWheelItemCount; i++) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(color);
            mPaint.setTextAlign(Align.CENTER);

            ItemRect ir = mItemPostions[i];
            float x = ir.getDrawX(offset);
            float y = ir.getRealY();
            float f = ir.getFac(mCurrentScrollOffset);
            String txt = mSelectorIndexToStringCache.get(mSelectorIndices[i]);
            int alpha = (int) (ir.getAlphaFac(mCurrentScrollOffset) * 255);

            mPaint.setTextSize(f * mTextSize);

            FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
            int baseline = (int) (y - (fontMetrics.bottom + fontMetrics.top) / 2);

            if (null != txt) {
                mPaint.setAlpha(alpha);
                canvas.drawText(txt, x, baseline, mPaint);
            }

            if (mShowMiddSign && i == mWheelMiddIdx) {
                mPaint.setColor(mTextNormalColor);
                mPaint.setAlpha(mMiddSignAlpha);
                mMiddSignRect.offsetTo(ir.getRealX() - mMiddSignWidth / 2, mMiddSignPadding);
                canvas.drawRect(mMiddSignRect, mPaint);
                mMiddSignRect.offset(0, mHeight - mMiddSignHeight - mMiddSignPadding * 2);
                canvas.drawRect(mMiddSignRect, mPaint);
            }
        }
    }

    private void drawVertical(Canvas canvas) {
        int color = isEnabled() ? mTextNormalColor : mUnableTextColor;
        int offset = mCurrentScrollOffset;

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(color);
        mPaint.setTextAlign(Align.CENTER);

        for (int i = 0; i < mWheelItemCount; i++) {
            ItemRect ir = mItemPostions[i];
            float x = ir.getRealX();
            float y = mWheelEnableScrollOffset ? ir.getDrawY(offset) : (offset + ir.getRealY());
            float f = ir.getFac(offset);
            String txt = mSelectorIndexToStringCache.get(mSelectorIndices[i]);
            int alpha = (int) (ir.getAlphaFac(offset) * 255);

            mPaint.setTextSize(f * mTextSize);

            FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
            int baseline = (int) (y - (fontMetrics.bottom + fontMetrics.top) / 2);

            if (null != txt) {
                mPaint.setAlpha(alpha);
                canvas.drawText(txt, x, baseline, mPaint);
            }
        }
        if (mShowMiddSign) {
            mPaint.setAlpha(mMiddSignAlpha);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawLine(mMiddSignRect.left, mMiddSignRect.top, mMiddSignRect.right, mMiddSignRect.top, mPaint);
            canvas.drawLine(mMiddSignRect.left, mMiddSignRect.bottom, mMiddSignRect.right, mMiddSignRect.bottom, mPaint);
        }
    }

    private void initViewSize() {
        if (mWidth == 0 || mHeight == 0) {
            mWidth = getMeasuredWidth();
            mHeight = getMeasuredHeight();
            mHalfWidth = mWidth / 2;

            mMiddSignPadding = (int) (0.16f * mHeight);
            mMiddSignHeight = (int) (0.12f * mHeight);
            mMiddSignRect.right = mMiddSignWidth;
            mMiddSignRect.bottom = mMiddSignHeight;
            if (mAutoAdjustItemHeight && isVerticalWheel()) {
                setItemHeight(mHeight / mWheelItemCount);
            }

            mVerticalSignLength = mWidth;
            if (mVerticalSignScale >= 0f && mVerticalSignScale <= 1f) {
                mVerticalSignLength = (int) (mWidth * mVerticalSignScale);
            } else if (mVerticalSignScale > 1f) {
                mVerticalSignLength = (int) mVerticalSignScale;
            }
            initializeSelectorWheel();

            if (isVerticalWheel()) {
                initVerticalItems();
            } else {
                initHorizontalItems();
            }

            if (isVerticalWheel()) {
                mMiddSignRect.set((mWidth - mVerticalSignLength) / 2, (mHeight - mSelectorElementSize) / 2,
                        (mWidth + mVerticalSignLength) / 2, (mHeight + mSelectorElementSize) / 2);
            }
        }
    }

    private void initVerticalItems() {
        ItemRect itemRect = null;
        int halfItemWidth = mWidth / 2;
        int centerYCoor = mHeight / 2;
        final int itemCount = mWheelItemCount;
        mTotalHeight = getItemHeight() * mWheelItemCount;
        mOffsetTotalHeight = (mHeight - mTotalHeight) / 2;

        for (int i = 0; i < mItemPostions.length; i++) {
            itemRect = new VerticalItemRect(i);
            itemRect.updateCenterCoorY(centerYCoor + (i - itemCount / 2) * mSelectorElementSize);
            itemRect.updateCenterCoorX(halfItemWidth);
            mItemPostions[i] = itemRect;
        }

        if (null != mItemsDrawContents) {
            mTextSize = (int) (mSelectorElementSize * SF_ITEMTXT_MAX_SCALE_TO_HEIGHT);
            String txt = getMaxWidthString(mItemsDrawContents);
            int txtMeasureSize = measureTextSize((int) (SF_ITEMTXT_MAX_SCALE_TO_WIDTH * mWidth), txt);
            if (mTextSize > txtMeasureSize) {
                mTextSize = txtMeasureSize;
            }
        }
    }

    private void initHorizontalItems() {
        ItemRect itemRect = null;
        int halfItemWidth = mSelectorElementSize / 2;
        for (int i = 0; i < mItemPostions.length; i++) {
            itemRect = new HorizontalItemRect(mSelectorElementSize);
            itemRect.updateCenterCoorY(mHeight / 2);
            itemRect.updateCenterCoorX(halfItemWidth + mSelectorElementSize * i);
            mItemPostions[i] = itemRect;
        }

        if (null != mItemsDrawContents) {
            String txt = getMaxWidthString(mItemsDrawContents);
            if (!TextUtils.isEmpty(txt)) {
                float fac = mItemPostions[mWheelMiddIdx].getFac(0);
                int txtMeasureSize = measureTextSize((int) (fac * mSelectorElementSize), txt);
                int maxTextSize = (int) ((mHeight - (mMiddSignHeight + mMiddSignPadding) * 2) * 0.68f);
                if (txtMeasureSize < mTextSize) {
                    mTextSize = txtMeasureSize;
                }
                if (mTextSize > maxTextSize) {
                    mTextSize = maxTextSize;
                }
            }
        }
    }

    private String getMaxWidthString(String[] arrs) {
        String txt;
        Paint paint = new Paint();
        paint.setTextSize(10);
        int idx = 0;
        float curw = 0;
        for (int i = 0; i < arrs.length; i++) {
            txt = arrs[i];
            if (null != txt && txt.length() > 0) {
                float w = paint.measureText(txt);
                if (w >= curw) {
                    curw = w;
                    idx = i;
                }
            }
        }
        return arrs[idx];
    }

    private int measureTextSize(final int maxWidth, String text) {
        Paint paint = new Paint();
        int textSize = 1;
        if (maxWidth <= 0 || TextUtils.isEmpty(text)) {
            textSize = 0;
        } else {
            for (; ; ) {
                textSize++;
                paint.setTextSize(textSize);
                int mw = (int) paint.measureText(text);
                if (mw > maxWidth) {
                    textSize--;
                    for (; ; ) {
                        textSize--;
                        paint.setTextSize(textSize);
                        mw = (int) paint.measureText(text);
                        if (mw <= maxWidth) {
                            return textSize;
                        }
                    }
                }
            }
        }
        return textSize;
    }

    private void fling(int velocity) {
        mPreviousScrollerX = 0;
        mPreviousScrollerY = 0;

        if (isVerticalWheel()) {
            if (velocity > 0) {
                mFlingScroller.fling(0, 0, 0, velocity, 0, 0, 0, Integer.MAX_VALUE);
            } else {
                mFlingScroller.fling(0, Integer.MAX_VALUE, 0, velocity, 0, 0, 0, Integer.MAX_VALUE);
            }

        } else {
            if (velocity > 0) {
                mFlingScroller.fling(0, 0, velocity, 0, 0, Integer.MAX_VALUE, 0, 0);
            } else {
                mFlingScroller.fling(Integer.MAX_VALUE, 0, velocity, 0, 0, Integer.MAX_VALUE, 0, 0);
            }
        }

        invalidate();
    }

    private boolean ensureScrollWheelAdjusted() {
        // adjust to the closest value

        if (null != changeFinishListener && !mHandleScrollChange) {
            changeFinishListener.onValueChange(this, mValue);
        }
        mHandleScrollChange = false;

        int delta = mInitialScrollOffset - mCurrentScrollOffset;
        if (delta != 0) {
            mPreviousScrollerX = 0;
            mPreviousScrollerY = 0;
            if (Math.abs(delta) > mSelectorElementSize / 2) {
                delta += (delta > 0) ? -mSelectorElementSize : mSelectorElementSize;
            }
            if (isVerticalWheel()) {
                mAdjustScroller.startScroll(0, 0, 0, delta, SELECTOR_ADJUSTMENT_DURATION_MILLIS);
            } else {
                mAdjustScroller.startScroll(0, 0, delta, 0, SELECTOR_ADJUSTMENT_DURATION_MILLIS);
            }

            postInvalidate();
            return true;
        }
        return false;
    }

    private void onScrollerFinished(OverScroller scroller) {
        if (scroller == mFlingScroller) {
            ensureScrollWheelAdjusted();
            onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
        } else {
            invalidate();
        }
    }

    private void onScrollStateChange(int scrollState) {
        if (mScrollState == scrollState) {
            return;
        }
        mScrollState = scrollState;
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChange(this, scrollState);
        }
    }

    private void notifyChange(int previous, int current) {
        if (mOnValueChangeListener != null && !mHandleScrollChange) {
            mOnValueChangeListener.onValueChange(this, previous, mValue);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        if (null != attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomWheelView);
            int style = a.getInt(R.styleable.CustomWheelView_wheelStyle, STYLE_HORIZONTAL);
            mAlphaFac = a.getFloat(R.styleable.CustomWheelView_alphaFac, SF_TXT_ALPHA_FAC);
            mAutoAdjustItemHeight = a.getBoolean(R.styleable.CustomWheelView_autoAjustItemHeight, false);
            mTextNormalColor = a.getColor(R.styleable.CustomWheelView_itemTextColor, SF_TEXT_NORMAL_COLOR);
            mUnableTextColor = a.getColor(R.styleable.CustomWheelView_itemUnableTextColor, SF_TEXT_UNABLE_COLOR);
            mShowMiddSign = a.getBoolean(R.styleable.CustomWheelView_showMiddleSign, true);
            mVerticalSignScale = a.getFloat(R.styleable.CustomWheelView_verticalSignLength, 0f);
            mWheelStyleVertical = style == STYLE_VERTICAL;

            a.recycle();
        }

        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity()
                / SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT;

        mFlingScroller = new OverScroller(context, new LinearInterpolator());
        mAdjustScroller = new OverScroller(context, new DecelerateInterpolator(2.5f));

        mPaint = new TextPaint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        float desity = getResources().getDisplayMetrics().density;
        mMiddSignWidth = Math.round(1 * desity);
        mPaint.setStrokeWidth(mMiddSignWidth);

        mItemHeight = (int) (context.getResources().getDisplayMetrics().density * 36 + 0.5f);
        mTextSize = mItemHeight / 2;
    }

    private void initializeSelectorWheel() {
        initializeSelectorWheelIndices();
        mInitialScrollOffset = 0;
        mCurrentScrollOffset = mInitialScrollOffset;

        mSelectorElementSize = getSelectorElementSize();
        mElementTrigSize = mSelectorElementSize / 2;
    }

    private int getSelectorElementSize() {
        if (isVerticalWheel()) {
            return getItemHeight();
        } else {
            return getItemWidth();
        }
    }

    /*
     * item宽度
     */
    private int getItemWidth() {
        return mWidth / mWheelItemCount;
    }

    private void setValueInternal(int current, boolean notifyChange) {
        if (mValue == current) {
            return;
        }
        if (mWrapSelectorWheel) {
            current = getWrappedSelectorIndex(current);
        } else {
            current = Math.max(current, mMinValue);
            current = Math.min(current, mMaxValue);
        }
        int previous = mValue;
        mValue = current;
        if (notifyChange) {
            notifyChange(previous, current);
        }
        initializeSelectorWheelIndices();
        invalidate();
    }

    private boolean isDecrementToEnd() {
        return !mWrapSelectorWheel
                && mSelectorIndices[mWheelMiddIdx] <= mMinValue;
    }

    private boolean isIncrementToEnd() {
        return !mWrapSelectorWheel
                && mSelectorIndices[mWheelMiddIdx] >= mMaxValue;
    }

    private void incrementSelectorIndices(int[] selectorIndices) {
        for (int i = 0; i < selectorIndices.length - 1; i++) {
            selectorIndices[i] = selectorIndices[i + 1];
        }
        int nextScrollSelectorIndex = selectorIndices[selectorIndices.length - 2] + 1;
        if (mWrapSelectorWheel && nextScrollSelectorIndex > mMaxValue) {
            nextScrollSelectorIndex = mMinValue;
        }
        selectorIndices[selectorIndices.length - 1] = nextScrollSelectorIndex;
    }

    private void decrementSelectorIndices(int[] selectorIndices) {
        for (int i = selectorIndices.length - 1; i > 0; i--) {
            selectorIndices[i] = selectorIndices[i - 1];
        }
        int nextScrollSelectorIndex = selectorIndices[1] - 1;
        if (mWrapSelectorWheel && nextScrollSelectorIndex < mMinValue) {
            nextScrollSelectorIndex = mMaxValue;
        }
        selectorIndices[0] = nextScrollSelectorIndex;
    }

    private void initializeSelectorWheelIndices() {
        mSelectorIndexToStringCache.clear();
        int[] selectorIndices = mSelectorIndices;
        int current = getValue();
        for (int i = 0; i < mSelectorIndices.length; i++) {
            int selectorIndex = current + (i - mWheelMiddIdx);
            if (mWrapSelectorWheel) {
                selectorIndex = getWrappedSelectorIndex(selectorIndex);
            }
            selectorIndices[i] = selectorIndex;
            ensureCachedScrollSelectorValue(selectorIndices[i]);
        }
    }

    private int getWrappedSelectorIndex(int selectorIndex) {
        if (selectorIndex > mMaxValue) {
            return mMinValue + (selectorIndex - mMaxValue) % (mMaxValue - mMinValue) - 1;
        } else if (selectorIndex < mMinValue) {
            return mMaxValue - (mMinValue - selectorIndex) % (mMaxValue - mMinValue) + 1;
        }
        return selectorIndex;
    }

    private void ensureCachedScrollSelectorValue(int selectorIndex) {
        SparseArray<String> cache = mSelectorIndexToStringCache;
        String scrollSelectorValue = cache.get(selectorIndex);
        if (scrollSelectorValue != null) {
            return;
        }
        if (selectorIndex < mMinValue || selectorIndex > mMaxValue) {
            scrollSelectorValue = "";
        } else {
            if (mItemsDrawContents != null) {
                int displayedValueIndex = selectorIndex - mMinValue;
                scrollSelectorValue = mItemsDrawContents[displayedValueIndex];
            } else {
                scrollSelectorValue = "" + selectorIndex;
            }
        }
        cache.put(selectorIndex, scrollSelectorValue);
    }


    private void changeValueByValue(int changeValue, int litOffset) {
        if (!moveToFinalScrollerPosition(mFlingScroller)) {
            moveToFinalScrollerPosition(mAdjustScroller);
        }
        mPreviousScrollerX = 0;
        mPreviousScrollerY = 0;

        int scrollValue, dv;
        dv = changeValue - mValue;
        if (dv == 0) {
            return;
        } else if (dv < 0) {
            scrollValue = mSelectorElementSize * dv * (-1) + litOffset;
        } else {
            scrollValue = mSelectorElementSize * dv * (-1) - litOffset;
        }

        if (isVerticalWheel()) {
            mFlingScroller.startScroll(0, 0, 0, scrollValue, SNAP_SCROLL_DURATION);
        } else {
            mFlingScroller.startScroll(0, 0, scrollValue, 0, SNAP_SCROLL_DURATION);
        }

        mHandleScrollChange = true;

        invalidate();
    }

    private boolean moveToFinalScrollerPosition(OverScroller scroller) {
        scroller.forceFinished(true);
        int amountToScroll;
        if (isVerticalWheel()) {
            amountToScroll = scroller.getFinalY() - scroller.getCurrY();
        } else {
            amountToScroll = scroller.getFinalX() - scroller.getCurrX();
        }

        int futureScrollOffset = (mCurrentScrollOffset + amountToScroll) % mSelectorElementSize;
        int overshootAdjustment = mInitialScrollOffset - futureScrollOffset;
        if (overshootAdjustment != 0) {
            if (Math.abs(overshootAdjustment) > mSelectorElementSize / 2) {
                if (overshootAdjustment > 0) {
                    overshootAdjustment -= mSelectorElementSize;
                } else {
                    overshootAdjustment += mSelectorElementSize;
                }
            }
            amountToScroll += overshootAdjustment;

            if (isVerticalWheel()) {
                scrollBy(0, amountToScroll);
            } else {
                scrollBy(amountToScroll, 0);
            }

            return true;
        }
        return false;
    }

    public interface ItemRect {
        void updateCenterCoorX(int x);

        void updateCenterCoorY(int y);

        int getRealX();

        int getDrawX(int offset);

        int getRealY();

        int getDrawY(int offset);

        int getDrawSize(int offset);

        float getFac(int offset);

        float getAlphaFac(int offset);
    }

    private class VerticalItemRect implements ItemRect {
        int mPosition;
        int mx;
        int my;

        public VerticalItemRect(int position) {
            this.mPosition = position;
        }

        @Override
        public void updateCenterCoorX(int x) {
            mx = x;
        }

        @Override
        public void updateCenterCoorY(int y) {
            my = y;
        }

        @Override
        public int getRealX() {
            return mx;
        }

        @Override
        public int getDrawX(int offset) {
            return mx;
        }

        @Override
        public int getRealY() {
            return my;
        }

        @Override
        public int getDrawY(int offset) {
            int cy = offset + my;
            if (cy > (mHeight / 2)) {
                return (int) (mHeight - (mHeight - cy) * getFac(offset));
            } else {
                return (int) (getFac(offset) * cy);
            }
        }

        @Override
        public int getDrawSize(int offset) {
            //TODO
            return 0;
        }

        @Override
        public float getFac(int offset) {
            float fac = getFac_(offset);
            fac = 0.6f + 0.4f * fac * fac;
            return fac;
        }

        @Override
        public float getAlphaFac(int offset) {
            float fac = getFac_(offset);
            fac = mAlphaFac + (1 - mAlphaFac) * fac * fac;
            return fac;
        }

        private float getFac_(int offset) {
            float fac;
            int cy = my + offset - mOffsetTotalHeight;
            int halfHeight = mTotalHeight / 2;
            if (cy > halfHeight) {
                fac = (mTotalHeight - cy) / (mTotalHeight * 0.5f);
            } else {
                fac = cy / (mTotalHeight * 0.5f);
            }

            return fac;
        }
    }

    private class HorizontalItemRect implements ItemRect {

        private int mItemCenterCoorX;
        private int mItemCenterCoorY;

        private int mItemSize;

        public HorizontalItemRect(int itemSize) {
            this.mItemSize = itemSize;
        }

        @Override
        public void updateCenterCoorX(int x) {
            this.mItemCenterCoorX = x;
        }

        @Override
        public void updateCenterCoorY(int y) {
            this.mItemCenterCoorY = y;
        }

        @Override
        public int getRealX() {
            return mItemCenterCoorX;
        }

        @Override
        public int getDrawX(int offset) {
            int curCoorX = mItemCenterCoorX + offset;
            if (curCoorX > mHalfWidth) {
                return (int) (mWidth - (mWidth - curCoorX) * getFac(offset));
            } else {
                return (int) (curCoorX * getFac(offset));
            }
        }

        @Override
        public int getRealY() {
            return mItemCenterCoorY;
        }

        @Override
        public int getDrawY(int offset) {
            return (int) (mItemCenterCoorY * getFac(offset));
        }

        @Override
        public int getDrawSize(int offset) {
            return (int) (mItemSize * getFac(offset));
        }

        @Override
        public float getFac(int offset) {
            int curCoorX = mItemCenterCoorX + offset;
            float fac;
            if (curCoorX > mHalfWidth) {
                fac = (mWidth - curCoorX) / (mHalfWidth * 1f);
            } else {
                fac = curCoorX / (mHalfWidth * 1f);
            }
            fac = 0.6f + 0.4f * fac * fac;
            return fac;
        }

        @Override
        public float getAlphaFac(int offset) {
            int curCoorX = mItemCenterCoorX + offset;
            float fac;
            if (curCoorX > mHalfWidth) {
                fac = (mWidth - curCoorX) / (mHalfWidth * 1f);
            } else {
                fac = curCoorX / (mHalfWidth * 1f);
            }
            fac = mAlphaFac + (1 - mAlphaFac) * fac * fac;
            return fac;
        }
    }

    private OnValueChangeFinishListener changeFinishListener;

    /**
     * 当滚轮停止滚动时的value
     *
     * @param listener
     */
    public void setOnValueChangeFinishListener(OnValueChangeFinishListener listener) {
        this.changeFinishListener = listener;
    }

    /**
     * copy from NumberPicker
     */
    public interface OnValueChangeListener {
        void onValueChange(CustomWheelView picker, int oldVal, int newVal);
    }

    public interface OnValueChangeFinishListener {
        void onValueChange(CustomWheelView picker, int value);
    }

    /**
     * copy from NumberPicker
     */
    public interface OnScrollListener {
        public static int SCROLL_STATE_IDLE = 0;
        public static int SCROLL_STATE_TOUCH_SCROLL = 1;
        public static int SCROLL_STATE_FLING = 2;

        public void onScrollStateChange(CustomWheelView view, int scrollState);
    }
}
