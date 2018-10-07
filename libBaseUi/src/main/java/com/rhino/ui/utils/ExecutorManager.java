package com.rhino.ui.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;


/**
 * <p>The manager of Executor</p>
 *
 * @author LuoLin
 * @since Create on 2018/9/28.
 */
public class ExecutorManager {
    private static Executor mParallelExecutor = THREAD_POOL_EXECUTOR;
    private static Executor mSerialExecutor = AsyncTask.SERIAL_EXECUTOR;
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    private ExecutorManager() {
        mParallelExecutor = THREAD_POOL_EXECUTOR;
        mSerialExecutor = AsyncTask.SERIAL_EXECUTOR;
    }

    public static void runOnUIThread(Runnable runnable) {
        sHandler.post(runnable);
    }

    public static void executeTask(Runnable task) {
        executeTask(task, true);
    }

    public static void executeTaskSerially(Runnable task) {
        executeTask(task, false);
    }

    public static void executeTask(Runnable task, boolean parallel) {
        if (parallel) {
            mParallelExecutor.execute(task);
            return;
        }
        mSerialExecutor.execute(task);
    }
}