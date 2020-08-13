package com.rhino.ui.utils.ui;

import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * @author LuoLin
 * @since Create on 2019/9/17.
 **/
public class ViewUtils {

    public static void removeOnGlobalLayoutListener(View view, ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener) {
        if (Build.VERSION.SDK_INT < 16) {
            view.getViewTreeObserver().removeGlobalOnLayoutListener(onGlobalLayoutListener);
        } else {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        }
    }

}