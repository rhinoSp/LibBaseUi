package com.rhino.ui.utils.thread;


/**
 * @author LuoLin
 * @since Create on 2018/11/27.
 */
public class ThreadPoolProxyFactory {

    private static ThreadPoolProxy mNormalThreadPoolProxy;
    private static ThreadPoolProxy mDownLoadThreadPoolProxy;

    public static ThreadPoolProxy normal() {
        if (mNormalThreadPoolProxy == null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mNormalThreadPoolProxy == null) {
                    mNormalThreadPoolProxy = new ThreadPoolProxy(5, 5);
                }
            }
        }
        return mNormalThreadPoolProxy;
    }

    public static ThreadPoolProxy download() {
        if (mDownLoadThreadPoolProxy == null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mDownLoadThreadPoolProxy == null) {
                    mDownLoadThreadPoolProxy = new ThreadPoolProxy(3, 3);
                }
            }
        }
        return mDownLoadThreadPoolProxy;
    }

}
