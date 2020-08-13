package com.rhino.ui.utils;

import com.rhino.ui.base.BaseApplication;

import java.io.File;

/**
 * <p>文件目录信息</p>
 *
 * @author LuoLin
 * @since Create on 2018/9/27.
 */
public class FileConfig {

    public static final String PNG_POSTFIX = ".png";
    public static final String JPG_POSTFIX = ".jpg";
    public static final String MP4_POSTFIX = ".mp4";
    public static final String AAC_POSTFIX = ".aac";
    public static final String TXT_POSTFIX = ".txt";

    /**
     * 基础配置目录
     */
    public static final String CONFIG_FOLDER_NAME = ".config";
    /**
     * 图片目录
     */
    public static final String PICTURE_FOLDER_NAME = "picture";
    /**
     * 视频目录
     */
    public static final String VIDEO_FOLDER_NAME = "video";
    /**
     * 音频目录
     */
    public static final String AUDIO_FOLDER_NAME = "audio";
    /**
     * 日志目录
     */
    public static final String LOG_FOLDER_NAME = "log";


    /**
     * 存储根目录
     */
    public static String ROOT_PATH = BaseApplication.getInstance().getExternalFilesDir(null).getParent();
    /**
     * 基础配置文件夹路径
     */
    public static String CONFIG_FOLDER_PATH;
    /**
     * 图片文件夹路径
     */
    public static String PICTURE_FOLDER_PATH;
    /**
     * 视频文件夹路径
     */
    public static String VIDEO_FOLDER_PATH;
    /**
     * 音频文件夹路径
     */
    public static String AUDIO_FOLDER_PATH;
    /**
     * 日志文件夹路径
     */
    public static String LOG_FOLDER_PATH;

    public static void initFolderPath() {
        CONFIG_FOLDER_PATH = ROOT_PATH + File.separator + CONFIG_FOLDER_NAME;
        PICTURE_FOLDER_PATH = ROOT_PATH + File.separator + PICTURE_FOLDER_NAME;
        VIDEO_FOLDER_PATH = ROOT_PATH + File.separator + VIDEO_FOLDER_NAME;
        AUDIO_FOLDER_PATH = ROOT_PATH + File.separator + AUDIO_FOLDER_NAME;
        LOG_FOLDER_PATH = ROOT_PATH + File.separator + LOG_FOLDER_NAME;
    }


}
