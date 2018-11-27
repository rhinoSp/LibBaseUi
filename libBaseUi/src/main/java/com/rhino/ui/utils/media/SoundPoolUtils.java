package com.rhino.ui.utils.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.annotation.NonNull;
import android.util.SparseIntArray;


/**
 * <p>The utils of SoundPool</p>
 *
 * @author LuoLin
 * @since Create on 2018/11/27.
 */
public class SoundPoolUtils {

    private AudioManager mAudioManager;
    private SoundPool mSoundPool;
    private SparseIntArray mSoundPoolArray;
    private int currentSoundId = 0;
    private boolean mSoundEnable = true;

    public SoundPoolUtils(Context context) {
        this.mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 1);
        this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public SoundPoolUtils(Context context, @NonNull int[] soundResIdArray) {
        this.mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 1);
        this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        initSound(context, soundResIdArray);
    }

    public void initSound(Context context, @NonNull int[] soundResIdArray) {
        this.mSoundPoolArray = new SparseIntArray();
        for (int i = 0; i < soundResIdArray.length; i++) {
            mSoundPoolArray.put(i, mSoundPool.load(context, soundResIdArray[i], 1));
        }
    }

    public boolean isSoundEnable() {
        return mSoundEnable;
    }

    public void setSoundEnable(boolean enable) {
        mSoundEnable = enable;
    }

    public void playSound(int soundIndex) {
        if (null == mSoundPool) {
            return;
        }
        if (null == mSoundPoolArray) {
            throw new RuntimeException("Must init mSoundPoolArray.");
        }
        Integer soundId = mSoundPoolArray.get(soundIndex);
        if (!mSoundEnable) {
            return;
        }
        mSoundPool.stop(currentSoundId);

        float audioMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float audioCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volumeRatio = audioCurrentVolume / audioMaxVolume;
        currentSoundId = mSoundPool.play(soundId, volumeRatio, volumeRatio, 1, 0, 1);
    }

    public void play() {
        playSound(0);
    }

    public void release() {
        if (null != mSoundPool) {
            mSoundPool.release();
            mSoundPool = null;
        }
        if (null != mSoundPoolArray) {
            mSoundPoolArray.clear();
            mSoundPoolArray = null;
        }
        mAudioManager = null;
    }

}
