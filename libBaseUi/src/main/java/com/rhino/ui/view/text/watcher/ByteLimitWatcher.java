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
    private int mMaxByteLength;

    public ByteLimitWatcher(EditText mEditText, int mMaxByteLength) {
        this.mEditText = mEditText;
        this.mMaxByteLength = mMaxByteLength > 0 ? mMaxByteLength : 0;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s == null) {
            return;
        }
        if (count <= 0 || mMaxByteLength <= 0) {
            return;
        }
        String realText;
        int realCount = count;
        do {
            realText = s.toString().substring(0, start + realCount) + s.toString().substring(start + count);
            int length = getByteLength(realText);
            if (length <= mMaxByteLength) {
                break;
            }
        } while (realCount-- > 0);
        if (!realText.equals(s.toString())) {
            mEditText.setText(realText);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        s = mEditText.getEditableText();
        mEditText.setSelection(s.length());
        afterTextChanged(getByteLength(s.toString()), mMaxByteLength);
    }

    public void afterTextChanged(int byteCount, int maxByteCount) {
    }

    public static int getByteLength(String text) {
        try {
            return text.getBytes("utf-8").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return 0;
    }
}