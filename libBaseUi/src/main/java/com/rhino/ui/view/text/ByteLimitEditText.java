package com.rhino.ui.view.text;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.rhino.ui.R;
import com.rhino.ui.view.text.watcher.ByteLimitWatcher;


/**
 * @author LuoLin
 * @since Create on 2018/10/19.
 */
public class ByteLimitEditText extends AppCompatEditText {

    private ByteLimitWatcher mByteLimitWatcher;
    private int mMaxByteLength;

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
            typedArray.recycle();
        }
        mByteLimitWatcher = new ByteLimitWatcher(this, null, mMaxByteLength);
        addTextChangedListener(mByteLimitWatcher);
    }

    public void setMaxByteLength(int mMaxByteLength) {
        this.mMaxByteLength = mMaxByteLength;
        if (mByteLimitWatcher != null) {
            removeTextChangedListener(mByteLimitWatcher);
        }
        mByteLimitWatcher = new ByteLimitWatcher(this, null, mMaxByteLength);
        addTextChangedListener(mByteLimitWatcher);
    }


}
