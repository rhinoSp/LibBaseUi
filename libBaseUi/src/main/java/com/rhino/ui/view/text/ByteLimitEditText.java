package com.rhino.ui.view.text;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.ColorInt;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.util.AttributeSet;

import com.rhino.log.LogUtils;
import com.rhino.ui.R;
import com.rhino.ui.view.text.watcher.ByteLimitWatcher;


/**
 * @author LuoLin
 * @since Create on 2018/10/19.
 */
public class ByteLimitEditText extends AppCompatEditText {

    private static final int LIMIT_COUNT_GRAVITY_TOP = 1;
    private static final int LIMIT_COUNT_GRAVITY_CENTER = 2;
    private static final int LIMIT_COUNT_GRAVITY_BOTTOM = 3;

    private ByteLimitWatcher mByteLimitWatcher;
    private int mMaxByteLength;
    private boolean mShowLimitCount;
    private int mLimitCountTextColor;
    private int mLimitCountTextSize;
    private int mLimitCountGravity;

    private Paint mLimitCountPaint;


    public ByteLimitEditText(Context context) {
        super(context);
        init(context, null);
    }

    public ByteLimitEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ByteLimitEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ByteLimitEditText);
            mMaxByteLength = typedArray.getInt(R.styleable.ByteLimitEditText_blet_max_byte_length, -1);
            mShowLimitCount = typedArray.getBoolean(R.styleable.ByteLimitEditText_blet_show_limit_count, true);
            mLimitCountTextColor = typedArray.getColor(R.styleable.ByteLimitEditText_blet_limit_count_text_color, 0xff333333);
            mLimitCountTextSize = typedArray.getDimensionPixelSize(R.styleable.ByteLimitEditText_blet_limit_count_text_size, 40);
            mLimitCountGravity = typedArray.getInt(R.styleable.ByteLimitEditText_blet_limit_count_gravity, LIMIT_COUNT_GRAVITY_TOP);
            typedArray.recycle();
        }
        mByteLimitWatcher = createTextWatcher();
        addTextChangedListener(mByteLimitWatcher);

        mLimitCountPaint = new Paint();
        mLimitCountPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mShowLimitCount) {
            drawLimitCount(canvas);
        }
    }

    private void drawLimitCount(Canvas canvas) {
        canvas.save();

        int count = 0;
        try {
            byte[] bytes = getText().toString().getBytes("utf-8");
            count = bytes.length;
        } catch (Exception e) {
            LogUtils.e(e);
        }

        String text = count + "/" + mMaxByteLength;
        mLimitCountPaint.setTextSize(mLimitCountTextSize);
        mLimitCountPaint.setColor(mLimitCountTextColor);
        float limitCountPadding = getTextSize() / 10 * 2;
        float x = getWidth() - mLimitCountPaint.measureText(text) - limitCountPadding;
        float y = getTextSize();
        if (mLimitCountGravity == LIMIT_COUNT_GRAVITY_TOP) {
            y = getTextSize();
        } else if (mLimitCountGravity == LIMIT_COUNT_GRAVITY_CENTER) {
            y = getHeight() / 2;
        } else if (mLimitCountGravity == LIMIT_COUNT_GRAVITY_BOTTOM) {
            y = getHeight() - limitCountPadding;
        }
        canvas.drawText(text, x, y, mLimitCountPaint);
        canvas.restore();
    }

    public void setShowLimitCount(boolean showLimitCount) {
        this.mShowLimitCount = showLimitCount;
        postInvalidate();
    }

    public void setLimitCountTextColor(@ColorInt int color) {
        this.mLimitCountTextColor = color;
        postInvalidate();
    }

    public void setLimitCountTextSize(int textSize) {
        this.mLimitCountTextSize = textSize;
        postInvalidate();
    }

    public void setLimitCountGravity(int gravity) {
        this.mLimitCountGravity = gravity;
        postInvalidate();
    }

    public void setMaxByteLength(int mMaxByteLength) {
        this.mMaxByteLength = mMaxByteLength;
        if (mByteLimitWatcher != null) {
            removeTextChangedListener(mByteLimitWatcher);
        }
        mByteLimitWatcher = createTextWatcher();
        addTextChangedListener(mByteLimitWatcher);
    }

    private ByteLimitWatcher createTextWatcher() {
        return new ByteLimitWatcher(this, mMaxByteLength) {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if (mShowLimitCount) {
                    invalidate();
                }
            }
        };
    }

}
