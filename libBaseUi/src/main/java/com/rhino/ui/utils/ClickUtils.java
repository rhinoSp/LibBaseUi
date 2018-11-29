package com.rhino.ui.utils;

import android.app.Activity;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>The utls of click</p>
 *
 * @author LuoLin
 * @since Create on 2018/11/28.
 */
public class ClickUtils {

    public static void viewClick(View view, float x, float y) {
        long downTime = SystemClock.uptimeMillis();
        final MotionEvent downEvent = MotionEvent.obtain(downTime, downTime,MotionEvent.ACTION_DOWN, x, y, 0);
        downTime += 1000;
        final MotionEvent upEvent = MotionEvent.obtain(downTime, downTime,MotionEvent.ACTION_UP, x, y, 0);
        view.onTouchEvent(downEvent);
        view.onTouchEvent(upEvent);
        downEvent.recycle();
        upEvent.recycle();
    }

    public static void mouseClick(Activity activity, int x, int y){
        MotionEvent evenDownt = MotionEvent.obtain(System.currentTimeMillis(),
                System.currentTimeMillis() + 100, MotionEvent.ACTION_DOWN, x, y, 0);
        activity.dispatchTouchEvent(evenDownt);
        MotionEvent eventUp = MotionEvent.obtain(System.currentTimeMillis(),
                System.currentTimeMillis() + 100, MotionEvent.ACTION_UP, x, y, 0);
        activity.dispatchTouchEvent(eventUp);
        evenDownt.recycle();
        eventUp.recycle();
    }

    public static void excuteShellCMDClick(int x, int y) throws IOException {
        String cmd = "input tap " + x + " " + y;
        // 申请获取root权限，这一步很重要，不然会没有作用
        Process process = Runtime.getRuntime().exec("su");
        //获取输入流
        OutputStream outputStream = process.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(
                outputStream);
        dataOutputStream.writeBytes(cmd);
        dataOutputStream.flush();
        dataOutputStream.close();
        outputStream.close();
    }

}
