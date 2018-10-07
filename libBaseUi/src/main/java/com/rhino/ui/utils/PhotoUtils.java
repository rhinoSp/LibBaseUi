package com.rhino.ui.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import java.io.File;

/**
 * <p>The utils of photo</p>
 *
 * @author LuoLin
 * @since Create on 2018/9/27.
 */
public class PhotoUtils {

    public static String LAST_PHOTO_PATH_FROM_CAMERA = null;
    public static int MAX_PIC_COUNT = 5;

    /**
     * 启动系统照相功能
     *
     * @param activity      activity
     * @param imageFilePath 照片文件全路径
     * @param requestCode   requestCode
     */
    public static void takePhotoFromCamera(@NonNull Activity activity, @NonNull String imageFilePath, int requestCode) {
        LAST_PHOTO_PATH_FROM_CAMERA = imageFilePath;
        File imageFile = new File(imageFilePath);
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, imageFile.getAbsolutePath());
        contentValues.put("_data", imageFile.getAbsolutePath());
        contentValues.put(MediaStore.Images.Media.PICASA_ID, imageFile.getAbsolutePath());
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.getAbsolutePath());
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, imageFile.getAbsolutePath());
        contentValues.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, imageFile.getAbsolutePath());
        Uri photoUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 启动相册选择
     *
     * @param activity    activity
     * @param requestCode requestCode
     */
    public static void takePhotoFromAlbum(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 检查图片数量是否限制
     *
     * @param currentCount 当前数量
     * @param maxCount     最多数据
     * @return true 已达上限
     */
    public static boolean checkLimitPicCount(int currentCount, int maxCount) {
        if (currentCount >= maxCount) {
            return true;
        }
        return false;
    }

    /**
     * 检查图片数量是否限制
     *
     * @param currentCount 当前数量
     * @return true 已达上限
     */
    public static boolean checkLimitPicCount(int currentCount) {
        return checkLimitPicCount(currentCount, MAX_PIC_COUNT);
    }

}
