package com.rhino.ui.utils.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Field;


/**
 * <p>The utils of toast support normal and center<p>
 *
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
@SuppressLint("ShowToast")
public class ToastUtils {

    private static Field sField_TN;
    private static Field sField_TN_Handler;

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

    static {
        try {
            sField_TN = Toast.class.getDeclaredField("mTN");
            sField_TN.setAccessible(true);
            sField_TN_Handler = sField_TN.getType().getDeclaredField("mHandler");
            sField_TN_Handler.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * Show normal toast
     *
     * @param msg the toast message
     */
    public static void show(CharSequence msg) {
        try {
            if (mToast == null) {
                mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
            } else {
                mToast.setDuration(Toast.LENGTH_SHORT);
                mToast.setText(msg);
            }
            hook(mToast);
            mToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show center toast
     *
     * @param msg the toast message
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
            hook(mToast);
            mToastCenter.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show normal toast
     *
     * @param view the toast view
     */
    public static void show(View view) {
        try {
            if (null == mToast) {
                mToast = new Toast(mContext);
                mToast.setDuration(Toast.LENGTH_SHORT);
            }
            hook(mToast);
            mToast.setView(view);
            mToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Show center toast
     *
     * @param view the toast view
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

    private static void hook(Toast toast) {
        try {
            Object tn = sField_TN.get(toast);
            Handler preHandler = (Handler) sField_TN_Handler.get(tn);
            sField_TN_Handler.set(tn, new SafelyHandlerWrapper(preHandler));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class SafelyHandlerWrapper extends Handler {
        private Handler handler;
        public SafelyHandlerWrapper(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void dispatchMessage(Message msg) {
            try {
                super.dispatchMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void handleMessage(Message msg) {
            handler.handleMessage(msg);
        }
    }

}
