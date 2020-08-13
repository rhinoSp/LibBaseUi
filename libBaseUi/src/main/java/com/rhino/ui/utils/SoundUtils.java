package com.rhino.ui.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Vibrator;

import com.rhino.log.LogUtils;

/**
 * @author rhino
 * @since Create on 2019/8/20.
 **/
public class SoundUtils {

    public static final String TAG = SoundUtils.class.getSimpleName();

    /**
     * 获取最大系统音量
     */
    public static int getMaxSystemSound(Context context) {
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audio.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
    }

    /**
     * 获取当前系统音量
     */
    public static int getSystemSound(Context context) {
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audio.getStreamVolume(AudioManager.STREAM_SYSTEM);
    }

    /**
     * 设置系统音量
     */
    public static void setSystemSound(Context context, int sound) {
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audio.setStreamVolume(AudioManager.STREAM_SYSTEM, sound, AudioManager.FLAG_PLAY_SOUND);
    }

    /**
     * 获取声音模式
     * <p>
     * AudioManager.RINGER_MODE_SILENT://静音
     * AudioManager.RINGER_MODE_VIBRATE://振动
     * AudioManager.RINGER_MODE_NORMAL://声音模式
     */
    public static int getRingerMode(Context context) {
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audio.getRingerMode();
    }

    /**
     * 设置声音模式
     */
    public static void setRingerMode(Context context, int ringerMode) {
        try {
            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audio.setRingerMode(ringerMode);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }


    private void systemMode(Context context) {
        //获取声音管理器
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        switch (audio.getRingerMode()) {//获取系统设置的铃声模式
            case AudioManager.RINGER_MODE_SILENT://静音模式   震动
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(1000);
                break;
            case AudioManager.RINGER_MODE_NORMAL://声音模式   系统提示音
                MediaPlayer mp = new MediaPlayer();
                try {
                    mp.setDataSource(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    mp.prepare();
                    mp.start();
                } catch (Exception e) {
                    LogUtils.e(e);
                }
        }
    }

    public static void set(Context context) {
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

//        //通话音量
        int max = audio.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        int current = audio.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        LogUtils.d("VOICE max : " + max + "current: " + current);

        //系统音量
        max = audio.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        current = audio.getStreamVolume(AudioManager.STREAM_SYSTEM);
        LogUtils.d("SYSTEM max : " + max + "current: " + current);

        //铃声音量
        max = audio.getStreamMaxVolume(AudioManager.STREAM_RING);
        current = audio.getStreamVolume(AudioManager.STREAM_RING);
        LogUtils.d("RING max : " + max + "current: " + current);

        //音乐音量
        max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        current = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        LogUtils.d("MUSIC vmax : " + max + "current: " + current);

        //提示声音音量
        max = audio.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        current = audio.getStreamVolume(AudioManager.STREAM_ALARM);
        LogUtils.d("ALARM max : " + max + "current: " + current);


    }

}
