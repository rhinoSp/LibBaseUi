package com.rhino.ui.utils.media;

import android.media.MediaRecorder;

import com.rhino.log.LogUtils;


/**
 * <p>The utils of MediaRecorder</p>
 * 
 * @author rhino
 * @since Create on 2018/11/24.
 **/
public class MediaRecorderUtils {

    public static String LAST_AUDIO_PATH_WHEN_RECORDED = null;

    private MediaRecorder mMediaRecorder;
    private boolean isRecording = false;

    private static MediaRecorderUtils mInstance;

    private MediaRecorderUtils() {
    }

    public static MediaRecorderUtils getInstance() {
        if (mInstance == null) {
            mInstance = new MediaRecorderUtils();
        }
        return mInstance;
    }

    private OnStartListener mOnStartListener;

    public interface OnStartListener {
        void onStart();
    }

    public void startRecordAudio(String fileName, OnStartListener listener) {
        LAST_AUDIO_PATH_WHEN_RECORDED = fileName;
        this.mOnStartListener = listener;
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setOutputFile(fileName);
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isRecording = true;
            if (mOnStartListener != null) {
                mOnStartListener.onStart();
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    public void stopRecordAudio() {
        try {
            if (mMediaRecorder != null) {
                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        } finally {
            isRecording = false;
        }
    }

    public boolean isRecordering() {
        return isRecording;
    }


}
