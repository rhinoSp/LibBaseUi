package com.rhino.ui.utils.media;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.rhino.log.LogUtils;

import java.io.File;

/**
 * <p>The utils of MediaPlayer</p>
 * 
 * @author LuoLin
 * @since Create on 2018/11/27.
 */
public class MediaPlayerUtils {

    private MediaPlayer mMediaPlayer;

    private static MediaPlayerUtils mInstance;
    private MediaPlayerUtils() {
    }

    public static MediaPlayerUtils getInstance() {
        if (mInstance == null) {
            mInstance = new MediaPlayerUtils();
        }
        return mInstance;
    }

    public void stopPlay() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void startPlay(Context context, int resId) {
        stopPlay();
        mMediaPlayer = MediaPlayer.create(context, resId);
        mMediaPlayer.start();
    }

    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    public static long getMediaDuration(Context context, String filePath) {
        int duration = 0;
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, Uri.fromFile(file));
            if (mediaPlayer != null) {
                try {
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    LogUtils.e(e.toString());
                } finally {
                    duration = mediaPlayer.getDuration();
                }
            }
        }
        return duration;
    }

}
