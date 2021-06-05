package com.rhino.ui.utils.ui;

import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.core.view.ViewCompat;

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

    public static boolean onViewFocusEnter(View v, KeyEvent keyEvent) {
        return v.isFocused()
                && keyEvent.getAction() == KeyEvent.ACTION_DOWN
                && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER);
    }

    public static void zoomIn(View view) {
        ViewCompat.animate(view)
                .setDuration(200)
                .scaleX(1f)
                .scaleY(1f)
                .start();
    }

    public static void zoomOut(View view, int viewGap) {
        int height = view.getHeight();
        int width = view.getWidth();
        float scale;
        if (height < width) {
            // 以宽计算比例放大
            scale = (width + viewGap) / width;
        } else {
            // 以高计算比例放大
            scale = (height + viewGap) / height;
        }
        ViewCompat.animate(view)
                .setDuration(200)
                .scaleX(scale)
                .scaleY(scale)
                .start();
    }

}