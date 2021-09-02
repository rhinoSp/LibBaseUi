package com.rhino.ui.utils.ui;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.rhino.log.LogUtils;
import com.rhino.ui.utils.PrimaryUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author LuoLin
 * @since Create on 2019/7/18.
 */
public class BitmapUtils {

    /**
     * 根据资源id获取bitmap
     */
    @Nullable
    public static Bitmap decodeBitmapFromResource(Context context, int resId) {
        if (0 == resId) {
            return null;
        }
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 读取图片文件
     */
    @Nullable
    public static Bitmap decodeBitmapFromFile(String filePath) {
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            File file = new File(filePath);
            inputStream = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap != null) {
                bitmap = bitmap.copy(bitmap.getConfig(), true);
            }
        } catch (Exception e) {
            LogUtils.e(e.toString());
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                LogUtils.e(e.toString());
            }
        }
        return bitmap;
    }

    /**
     * 读取Assets文件夹下图片文件
     */
    @Nullable
    public static Bitmap decodeBitmapFromAssets(Context context, String filename) {
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            AssetManager asm = context.getAssets();
            inputStream = asm.open(filename);
            bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap != null) {
                bitmap = bitmap.copy(bitmap.getConfig(), true);
            }
        } catch (IOException e) {
            LogUtils.e(e.toString());
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                LogUtils.e(e.toString());
            }
        }
        return bitmap;
    }

    /**
     * 读取Resources bitmap
     */
    @Nullable
    public static Bitmap decodeBitmapFromResource(Resources res, int resId, int destWidth, int destHeight) {
        // 获取 BitmapFactory.Options，这里面保存了很多有关 Bitmap 的设置
        final BitmapFactory.Options options = new BitmapFactory.Options();
        // 设置 true 轻量加载图片信息
        options.inJustDecodeBounds = true;
        // 由于上方设置false，这里轻量加载图片
        BitmapFactory.decodeResource(res, resId, options);
        // 计算采样率
        options.inSampleSize = calculateInSampleSize(options, destWidth, destHeight);
        // 设置 false 正常加载图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * 复制图片，并设置isMutable=true
     */
    @Nullable
    public static Bitmap copyBitmap(Bitmap bitmap) {
        return bitmap.copy(bitmap.getConfig(), true);
    }

    /**
     * 用ByteArrayOutputStream方式把Bitmap转Byte
     */
    @NonNull
    public static byte[] bitmap2BytesByStream(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 用Buffer方式把Bitmap转Byte
     */
    @NonNull
    public static byte[] bitmap2BytesByBuffer(Bitmap bitmap) {
        int byteCount = bitmap.getByteCount();
        ByteBuffer byteBuffer = ByteBuffer.allocate(byteCount);
        bitmap.copyPixelsToBuffer(byteBuffer);
        return byteBuffer.array();
    }

    /**
     * byte转bitmap
     */
    public static Bitmap bytes2Bitmap(byte[] bytes, int width, int height) {
        return bytes2Bitmap(bytes, width, height, Bitmap.Config.ARGB_8888);
    }

    /**
     * byte转bitmap
     */
    public static Bitmap bytes2Bitmap(byte[] bytes, int width, int height, Bitmap.Config config) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(bytes));
        return bitmap;
    }

    /**
     * 根据高度缩放Bitmap
     *
     * @param srcBitmap 源Bitmap
     * @param newHeight 新的height
     * @return 新的Bitmap
     */
    @Nullable
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
    @Nullable
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
    @Nullable
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

    /**
     * 保存bytes
     *
     * @param bytes bytes
     * @return 返回保存图片路径，null 保存失败
     */
    @Nullable
    public static String saveBytes(@NonNull byte[] bytes) {
        File dir = new File(Environment.getExternalStorageDirectory(), "Album");
        if (!dir.exists() && !dir.mkdirs()) {
            return null;
        }
        String fileName = PrimaryUtils.createPrimary()  + ".png";
        File file = new File(dir, fileName);
        if (saveBytes(bytes, file.getAbsolutePath())) {
            return file.getPath();
        }
        return null;
    }
    /**
     * 保存bytes
     *
     * @param bytes bytes
     * @return true 保存成功， false 保存失败
     */
    public static boolean saveBytes(byte[] bytes, String filePath) {
        boolean saveSuccess = false;
        File file = new File(filePath);
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            return false;
        }
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedOutputStream.write(bytes);
            bufferedOutputStream.flush();
            saveSuccess = true;
        } catch (Exception e) {
            LogUtils.e(e.toString());
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    LogUtils.e(e);
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        return saveSuccess;
    }

    /**
     * 保存图片的方法
     *
     * @param bitmap Bitmap
     * @return 返回保存图片路径，null 保存失败
     */
    @Nullable
    public static String saveBitmap(@NonNull Bitmap bitmap) {
        return saveBitmap(bitmap, 100);
    }

    /**
     * 保存图片的方法
     *
     * @param bitmap Bitmap
     * @return 返回保存图片路径，null 保存失败
     */
    @Nullable
    public static String saveBitmap(@NonNull Bitmap bitmap, int quality) {
        File dir = new File(Environment.getExternalStorageDirectory(), "Album");
        if (!dir.exists() && !dir.mkdirs()) {
            return null;
        }
        String fileName = PrimaryUtils.createPrimary() + ".png";
        File file = new File(dir, fileName);
        if (saveBitmap(bitmap, file.getAbsolutePath(), quality)) {
            return file.getPath();
        }
        return null;
    }

    /**
     * 保存图片的方法
     *
     * @param bitmap   Bitmap
     * @param filePath 保存文件路径
     * @param quality 保存质量
     * @return true 保存成功， false 保存失败
     */
    public static boolean saveBitmap(@NonNull Bitmap bitmap, String filePath, int quality) {
        boolean saveSuccess = false;
        File file = new File(filePath);
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            return false;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.setHasAlpha(true);
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, fos);
            fos.flush();
            saveSuccess = true;
        } catch (IOException e) {
            LogUtils.e(e.toString());
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                LogUtils.e(e);
            }
        }
        return saveSuccess;
    }

    /**
     * 回收bitmap
     */
    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    /**
     * 裁剪
     */
    @Nullable
    public static Bitmap crop(@NonNull Bitmap bitmap, Rect previewRect, Rect cropRect) {
        LogUtils.d("准备裁剪, previewRect = " + previewRect.toString() + ", cropRect = " + cropRect);
        long timestamp = System.currentTimeMillis();

        int cropLeft = cropRect.left;
        int cropTop = cropRect.top;
        int cropWidth = cropRect.width();
        int cropHeight = cropRect.height();

        cropLeft = (int) (1.0f * cropLeft / previewRect.width() * bitmap.getWidth());
        cropTop = (int) (1.0f * cropTop / previewRect.height() * bitmap.getHeight());
        cropWidth = (int) (1.0f * cropWidth / previewRect.width() * bitmap.getWidth());
        cropHeight = (int) (1.0f * cropHeight / previewRect.height() * bitmap.getHeight());
        LogUtils.d("开始裁剪, cropLeft = " + cropLeft + ", cropTop = " + cropTop + ", cropWidth = " + cropWidth + ", cropHeight = " + cropHeight + ", time = " + (System.currentTimeMillis() - timestamp));
        Bitmap cropBitmap = Bitmap.createBitmap(bitmap, cropLeft, cropTop, cropWidth, cropHeight);
        LogUtils.d("裁剪完成, time = " + (System.currentTimeMillis() - timestamp));
        return cropBitmap;
    }

    /**
     * 计算采样率
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1; // 宽或高大于预期就将采样率 *=2 进行缩放
        if (width > reqWidth || height > reqHeight) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

}
