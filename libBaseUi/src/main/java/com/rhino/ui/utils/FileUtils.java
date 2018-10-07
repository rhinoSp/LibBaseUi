package com.rhino.ui.utils;

import java.io.File;

/**
 * <p>The utils of file<p>
 *
 * @author LuoLin
 * @since Create on 2018/9/27.
 */
public class FileUtils {

    /**
     * Make directory.
     *
     * @param directoryArray the array of directory path, like /sdcard/Android/temp/
     * @return true success,  false failed
     */
    public static boolean makeDirectory(String... directoryArray) {
        boolean isSuccess = true;
        for (int i = 0, count = directoryArray.length; i < count; i++) {
            if (!isDirectoryExists(directoryArray[i])) {
                try {
                    File dir = new File(directoryArray[i]);
                    if (dir.isFile()) {
                        isSuccess = dir.createNewFile();
                    } else {
                        isSuccess = dir.mkdirs();
                    }
                } catch (Exception e) {
                    isSuccess = false;
                }
                isSuccess &= isSuccess;
            }
        }
        return isSuccess;
    }

    /**
     * Whether exist this directory.
     *
     * @param directoryPath the path of directory
     * @return true exit,  false not exit
     */
    public static boolean isDirectoryExists(String directoryPath) {
        File file = new File(directoryPath);
        return file.isDirectory() && file.exists();
    }

    /**
     * Whether exist this file.
     *
     * @param filePath the path of file
     * @return true exit,  false not exit
     */
    public static boolean isFileExists(String filePath) {
        return new File(filePath).exists();
    }

}
