package com.rhino.ui.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.rhino.log.LogUtils;

import java.io.IOException;

/**
 * @author rhino
 * @since Create on 2019/12/9.
 **/
public class AudioPlayerUtils implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener {

    private static final String TAG = AudioPlayerUtils.class.getSimpleName();

    public MediaPlayer mediaPlayer;
    private String voiceUrl;
    private boolean pause;
    private boolean complete;
    private int playPosition;

    private MediaPlayer.OnCompletionListener onCompletionListener;

    public AudioPlayerUtils() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
        } catch (Exception e) {
            LogUtils.e("error", e);
        }
    }

    @Override
    public void onPrepared(MediaPlayer arg0) {
        arg0.start();
        LogUtils.e("onPrepared");
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        LogUtils.e("onCompletion");
        complete = true;
        if (onCompletionListener != null) {
            onCompletionListener.onCompletion(arg0);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
        int currentPosition = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();
        LogUtils.e("currentPosition = " + currentPosition + "duration = " + duration + ", bufferingProgress = " + bufferingProgress);
        if (complete && bufferingProgress >= 100) {

        }
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener) {
        this.onCompletionListener = onCompletionListener;
    }

    /**
     * 来电话了
     */
    public void callIsComing() {
        if (mediaPlayer.isPlaying()) {
            playPosition = mediaPlayer.getCurrentPosition();// 获得当前播放位置
            mediaPlayer.stop();
        }
    }

    /**
     * 通话结束
     */
    public void callIsDown() {
        if (playPosition > 0) {
            playNet(playPosition);
            playPosition = 0;
        }
    }

    /**
     * 播放
     */
    public void play() {
        playNet(0);
    }

    /**
     * 重播
     */
    public void replay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(0);// 从开始位置开始播放音乐
        } else {
            playNet(0);
        }
    }

    /**
     * 暂停
     */
    public boolean pause() {
        if (mediaPlayer.isPlaying()) {// 如果正在播放
            mediaPlayer.pause();// 暂停
            pause = true;
        } else {
            if (pause) {// 如果处于暂停状态
                mediaPlayer.start();// 继续播放
                pause = false;
            }
        }
        return pause;
    }

    /**
     * 停止
     */
    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }


    /**
     * 播放音乐
     */
    private void playNet(int playPosition) {
        try {
            mediaPlayer.reset();// 把各项参数恢复到初始状态
            mediaPlayer.setDataSource(voiceUrl);
            mediaPlayer.prepare();// 进行缓冲
            mediaPlayer.setOnPreparedListener(new MyPreparedListener(
                    playPosition));
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    private final class MyPreparedListener implements MediaPlayer.OnPreparedListener {
        private int playPosition;

        public MyPreparedListener(int playPosition) {
            this.playPosition = playPosition;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mediaPlayer.start();// 开始播放
            if (playPosition > 0) {
                mediaPlayer.seekTo(playPosition);
            }
        }
    }

    public static long getDuration(String audioUrl) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepare();
            int duration = mediaPlayer.getDuration();
            mediaPlayer.release();
            return duration;
        } catch (IOException e) {
            LogUtils.e(e);
        }
        return 0L;
    }
}