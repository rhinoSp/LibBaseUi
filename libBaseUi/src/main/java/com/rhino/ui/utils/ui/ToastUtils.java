package com.rhino.ui.utils.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;


/**
 * <p>The utils of toast support normal and center<p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
@SuppressLint("ShowToast")
public class ToastUtils {

    /**
     * The context.
     */
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    /**
     * The toast for normal.
     */
    private static Toast mToast;
    /**
     * The toast for center.
     */
    private static Toast mToastCenter;

    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * Show normal toast
     *
     * @param msg     the toast message
     */
    public static void show(CharSequence msg) {
        try {
            if (mToast == null) {
                mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
            } else {
                mToast.setDuration(Toast.LENGTH_SHORT);
                mToast.setText(msg);
            }
            mToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show center toast
     *
     * @param msg     the toast message
     */
    public static void showCenter(CharSequence msg) {
        try {
            if (mToastCenter == null) {
                mToastCenter = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
                mToastCenter.setGravity(Gravity.CENTER, 0, 0);
            } else {
                mToastCenter.setDuration(Toast.LENGTH_SHORT);
                mToastCenter.setText(msg);
            }
            mToastCenter.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Show normal toast
     *
     * @param view    the toast view
     */
    public static void show(View view) {
        try {
            if (null == mToast) {
                mToast = new Toast(mContext);
                mToast.setDuration(Toast.LENGTH_SHORT);
            }
            mToast.setView(view);
            mToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Show center toast
     *
     * @param view    the toast view
     */
    public static void showCenter(View view) {
        try {
            if (null == mToastCenter) {
                mToastCenter = new Toast(mContext);
                mToastCenter.setDuration(Toast.LENGTH_SHORT);
                mToastCenter.setGravity(Gravity.CENTER, 0, 0);
            }
            mToastCenter.setView(view);
            mToastCenter.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
