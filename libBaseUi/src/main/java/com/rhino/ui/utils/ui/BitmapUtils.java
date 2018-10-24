package com.rhino.ui.utils.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.InputStream;

/**
 * <p>The utils of bitmap<p>
 *
 * @author LuoLin
 * @since Create on 2017/5/19
 **/
public class BitmapUtils {

    /**
     * 根据资源id获取bitmap
     *
     * @param context 上下文
     * @param resId   资源id
     * @return bitmap
     */
    public static Bitmap getBitmapByResId(Context context, int resId) {
        if (0 == resId) {
            return null;
        }
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // opt.inSampleSize = 2;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 根据高度缩放Bitmap
     *
     * @param srcBitmap 源Bitmap
     * @param newHeight 新的height
     * @return 新的Bitmap
     */
    public static Bitmap zoomImageByHeight(Bitmap srcBitmap, int newHeight) {
        if (null == srcBitmap || 0 >= srcBitmap.getWidth()
                || 0 >= srcBitmap.getHeight()
                || 0 >= newHeight) {
            return null;
        }
        int width = srcBitmap.getWidth();
        int height = srcBitmap.getHeight();
        Matrix matrix = new Matrix();
        float scale = (float) newHeight / height;
        matrix.setScale(scale, scale);
        srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, width, height,
                matrix, true);
        return srcBitmap;
    }

    /**
     * 根据宽度缩放Bitmap
     *
     * @param srcBitmap 源Bitmap
     * @param newWidth  新的width
     * @return 新的Bitmap
     */
    public static Bitmap zoomImageByWidth(Bitmap srcBitmap, int newWidth) {
        if (null == srcBitmap || 0 >= srcBitmap.getWidth()
                || 0 >= srcBitmap.getHeight()
                || 0 >= newWidth) {
            return null;
        }
        int width = srcBitmap.getWidth();
        int height = srcBitmap.getHeight();
        Matrix matrix = new Matrix();
        float scale = (float) newWidth / width;
        matrix.setScale(scale, scale);
        srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, width, height,
                matrix, true);
        return srcBitmap;
    }

    /**
     * 根据宽高缩放Bitmap
     *
     * @param srcBitmap 源Bitmap
     * @param newWidth  新的width
     * @param newHeight 新的height
     * @return 新的Bitmap
     */
    public static Bitmap zoomImage(Bitmap srcBitmap, int newWidth, int newHeight) {
        if (null == srcBitmap || 0 >= srcBitmap.getWidth()
                || 0 >= srcBitmap.getHeight()
                || 0 >= newWidth
                || 0 >= newHeight) {
            return null;
        }
        int width = srcBitmap.getWidth();
        int height = srcBitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = (float) newWidth / width;
        float scaleHeight = (float) newHeight / height;
        matrix.setScale(scaleWidth, scaleHeight);
        srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, width, height,
                matrix, true);
        return srcBitmap;
    }

}
