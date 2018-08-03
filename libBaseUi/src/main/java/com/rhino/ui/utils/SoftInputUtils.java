package com.rhino.ui.utils;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


/**
 * <p>The utils of soft keyboard</p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public class SoftInputUtils {

    /**
     * hide soft keyboard
     *
     * @param activity the activity
     */
    public static void hideSoftInputFromWindow(Activity activity) {
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    /**
     * open soft keyboard
     *
     * @param activity  the activity
     * @param mEditText the EditText
     */
    public static void openSoftInputFromWindow(final Activity activity, final EditText mEditText) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(mEditText, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        }, 100);
    }
}
