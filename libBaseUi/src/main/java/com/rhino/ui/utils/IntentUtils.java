package com.rhino.ui.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;

/**
 * @author LuoLin
 * @since Create on 2019/6/14.
 **/
public class IntentUtils {


    /**
     * Play video use system player.
     *
     * @param context       context
     * @param videoFilePath the file path of video
     */
    public static void playVideo(Context context, @NonNull String videoFilePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(videoFilePath);
        Uri uri = UriUtils.fromFile(context, file);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "video/*");
        context.startActivity(intent);
    }

}
