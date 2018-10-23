package com.rhino.ui.view.text.watcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;

/**
 * @author LuoLin
 * @since Create on 2018/10/19.
 */
public class ByteLimitWatcher implements TextWatcher {

    private EditText mEditText;
    private TextWatcher mTextWatcher;
    private int mMaxByteLength;

    public ByteLimitWatcher(EditText mEditText, int mMaxByteLength) {
        this.mEditText = mEditText;
        this.mMaxByteLength = mMaxByteLength > 0 ? mMaxByteLength : 0;
    }

    public ByteLimitWatcher(EditText mEditText, TextWatcher mTextWatcher, int mMaxByteLength) {
        this.mEditText = mEditText;
        this.mTextWatcher = mTextWatcher;
        this.mMaxByteLength = mMaxByteLength > 0 ? mMaxByteLength : 0;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (mTextWatcher != null) {
            mTextWatcher.beforeTextChanged(s, start, count, after);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s != null) {
            String tmp;
            try {
                if (count > 0 && mMaxByteLength > 0) {
                    int cnt = count;
                    do {
                        tmp = s.toString().substring(0, start + cnt) + s.toString().substring(start + count);
                        byte[] bytes = tmp.getBytes("utf-8");
                        if (bytes.length <= mMaxByteLength) {
                            break;
                        }
                    } while (cnt-- > 0);
                    if (!tmp.equals(s.toString())) {
                        mEditText.setText(tmp);
                        mEditText.setSelection(start + cnt);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (mTextWatcher != null) {
            mTextWatcher.onTextChanged(s, start, before, count);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mTextWatcher != null) {
            mTextWatcher.afterTextChanged(s);
        }
    }
}