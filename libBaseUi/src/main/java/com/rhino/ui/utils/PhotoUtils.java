package com.rhino.ui.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

/**
 * <p>The utils of photo</p>
 *
 * @author LuoLin
 * @since Create on 2018/9/27.
 */
public class PhotoUtils {

    public static String LAST_PHOTO_PATH_FROM_CAMERA = null;
    public static String LAST_PHOTO_PATH_FROM_CROP = null;
    public static int MAX_PIC_COUNT = 5;

    public static void showCrop(Activity activity, String filePath, int requestCode) {
        LAST_PHOTO_PATH_FROM_CROP = FileConfig.PICTURE_FOLDER_PATH + "/" + PrimaryUtils.createPrimary() + ".jpg";
        File outFile = new File(LAST_PHOTO_PATH_FROM_CROP);
        Uri outUri = UriUtils.fromFile(activity, outFile);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(UriUtils.fromFile(activity, new File(filePath)), "image/*");  // 要裁剪的图片URI
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);  // aspectX：aspectY 裁剪比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 480);  // 输出图片大小
        intent.putExtra("outputY", 480);
        intent.putExtra("return-data", false);  // 是否以bitmap方式返回，缩略图可设为true，大图一定要设为false，返回URI
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);  // 输出的图片的URI
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// 返回格式
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边

        List<ResolveInfo> resInfoList = activity.getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            activity.grantUriPermission(packageName, outUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        activity.startActivityForResult(intent, requestCode);
    }

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
