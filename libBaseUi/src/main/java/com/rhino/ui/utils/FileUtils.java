package com.rhino.ui.utils;

import android.content.Context;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

/**
 * <p>The utils of file<p>
 *
 * @author LuoLin
 * @since Create on 2018/9/27.
 */
public class FileUtils {

    /**
     * Whether has SD card.
     *
     * @return true had
     */
    public static boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    /**
     * Make directory.
     *
     * @param directoryArray the array of directory path, like /sdcard/Android/temp/
     * @return true success,  false failed
     */
    public static boolean makeDirectory(String... directoryArray) {
        boolean isSuccess = true;
        for (int i = 0, count = directoryArray.length; i < count; i++) {
            try {
                File dir = new File(directoryArray[i]);
                if (!dir.exists()) {
                    isSuccess &= dir.mkdirs();
                }
            } catch (Exception e) {
                isSuccess = false;
            }
            isSuccess &= isSuccess;
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
        File directoryFile = new File(directoryPath);
        return directoryFile.isDirectory() && directoryFile.exists();
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

    /**
     * Delete directory.
     *
     * @param directoryFile the path of directory
     * @return true success,  false failed
     */
    public static boolean deleteDirectory(File directoryFile) {
        boolean isSuccess = true;
        if (directoryFile == null || !directoryFile.exists() || !directoryFile.isDirectory()) {
            return false;
        }
        for (File file : directoryFile.listFiles()) {
            if (file.isFile()) {
                isSuccess &= file.delete();
            } else if (file.isDirectory()) {
                isSuccess &= deleteDirectory(file);
            }
        }
        isSuccess &= directoryFile.delete();
        return isSuccess;
    }

    /**
     * Delete file.
     *
     * @param filePath the path of file
     * @return true success,  false failed
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * Copy the directory.
     *
     * @param srcDirPath  the path of src directory
     * @param destDirPath the path of dest directory
     * @return true success，false failed
     */
    public static boolean copyDirectory(String srcDirPath, String destDirPath) {
        return copyDirectory(srcDirPath, destDirPath, true);
    }

    /**
     * Copy the directory.
     *
     * @param srcDirPath  the path of src directory
     * @param destDirPath the path of dest directory
     * @param isOverlay   overlay ?
     * @return true success，false failed
     */
    public static boolean copyDirectory(String srcDirPath, String destDirPath, boolean isOverlay) {
        File srcDir = new File(srcDirPath);
        if (!srcDir.exists()) {
            return false;
        } else if (!srcDir.isDirectory()) {
            return false;
        }
        if (!destDirPath.endsWith(File.separator)) {
            destDirPath = destDirPath + File.separator;
        }
        File destDir = new File(destDirPath);
        if (destDir.exists()) {
            if (isOverlay) {
                deleteFile(destDirPath);
            } else {
                return true;
            }
        } else {
            if (!destDir.mkdirs()) {
                return false;
            }
        }
        boolean flag = true;
        File[] files = srcDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    flag = copyFile(file.getAbsolutePath(), destDirPath + file.getName(), isOverlay);
                    if (!flag) {
                        break;
                    }
                } else if (file.isDirectory()) {
                    flag = copyDirectory(file.getAbsolutePath(), destDirPath + file.getName(), isOverlay);
                    if (!flag) {
                        break;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * Copy the file.
     *
     * @param srcFileName  the path of src file
     * @param destFileName the path of dest file
     * @return true success，false failed
     */
    public static boolean copyFile(String srcFileName, String destFileName) {
        return copyFile(srcFileName, destFileName, true);
    }

    /**
     * Copy the file.
     *
     * @param srcFilePath  the path of src file
     * @param destFilePath the path of dest file
     * @param isOverlay    overlay ?
     * @return true success，false failed
     */
    public static boolean copyFile(String srcFilePath, String destFilePath, boolean isOverlay) {
        File srcFile = new File(srcFilePath);
        if (!srcFile.exists()) {
            return false;
        } else if (!srcFile.isFile()) {
            return false;
        }
        File destFile = new File(destFilePath);
        if (destFile.exists()) {
            if (isOverlay) {
                if (!deleteFile(destFilePath)) {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            if (!destFile.getParentFile().exists()) {
                if (!destFile.getParentFile().mkdirs()) {
                    return false;
                }
            }
        }
        try {
            FileInputStream inStream = new FileInputStream(srcFile);
            FileOutputStream outStream = new FileOutputStream(destFile);
            FileDescriptor fd = outStream.getFD();
            FileChannel in = inStream.getChannel();
            FileChannel out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
            fd.sync();
            inStream.close();
            in.close();
            outStream.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Read the file content.
     *
     * @param file the file path
     * @return the content
     */
    public static String readFile(String file) {
        try {
            int length;
            byte[] bytes = new byte[1024];
            FileInputStream mFileInputStream = new FileInputStream(file);
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            while ((length = mFileInputStream.read(bytes)) != -1) {
                arrayOutputStream.write(bytes, 0, length);
            }
            mFileInputStream.close();
            arrayOutputStream.close();
            return new String(arrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Read the file content from assets file.
     *
     * @param context  the context
     * @param fileName the file name
     * @return the content
     */
    public static String readFileFromAssets(Context context, String fileName) {
        try {
            int length;
            byte[] bytes = new byte[1024];
            InputStream mInputStream = context.getResources().getAssets()
                    .open(fileName);
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            while ((length = mInputStream.read(bytes)) != -1) {
                arrayOutputStream.write(bytes, 0, length);
            }
            mInputStream.close();
            arrayOutputStream.close();
            return new String(arrayOutputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Write data to file.
     *
     * @param file the file path
     * @param data the content
     * @return true write success
     */
    public static boolean writeFile(String file, String data) {
        FileOutputStream mFileOutputStream;
        try {
            mFileOutputStream = new FileOutputStream(file);
            mFileOutputStream.write(data.getBytes());
            mFileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
