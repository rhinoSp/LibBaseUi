package com.rhino.ui.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.rhino.log.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * <p>Convert for Base64 and Bitmap</p>
 *
 * @author LuoLin
 * @since Create on 2018/11/27.
 */
public class Base64BitmapUtils {

    /**
     * Image file to base64
     */
    public static String imageFileToBase64(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        return bitmapToBase64(bitmap);
    }

    /**
     * Bitmap to Base64.
     *
     * @param bitmap Bitmap
     * @return String
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            LogUtils.e(e);
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                LogUtils.e(e);
            }
        }
        return result;
    }

    /**
     * Base64 to Bitmap.
     *
     * @param base64Data String
     * @return Bitmap
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}