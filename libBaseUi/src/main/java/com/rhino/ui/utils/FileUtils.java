package com.rhino.ui.utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

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
            try {
                File dir = new File(directoryArray[i]);
                if (!dir.exists() && dir.isDirectory()) {
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
     * 复制整个目录的内容 覆盖
     *
     * @param srcDirPath  待复制目录的目录名
     * @param destDirPath 目标目录名
     * @return 如果复制成功返回true，否则返回false
     */
    public static boolean copyDirectory(String srcDirPath, String destDirPath) {
        return copyDirectory(srcDirPath, destDirPath, true);
    }

    /**
     * 复制整个目录的内容
     *
     * @param srcDirPath  待复制目录的绝对路径
     * @param destDirPath 目标目录的绝对路径
     * @param isOverlay   如果目标目录存在，是否覆盖
     * @return 如果复制成功返回true，否则返回false
     */
    public static boolean copyDirectory(String srcDirPath, String destDirPath, boolean isOverlay) {
        File srcDir = new File(srcDirPath);
        if (!srcDir.exists()) {
            return false;
        } else if (!srcDir.isDirectory()) {
            return false;
        }
        // 如果目标目录名不是以文件分隔符结尾，则加上文件分隔符
        if (!destDirPath.endsWith(File.separator)) {
            destDirPath = destDirPath + File.separator;
        }
        File destDir = new File(destDirPath);
        // 如果目标文件夹存在
        if (destDir.exists()) {
            // 如果允许覆盖则删除已存在的目标目录
            if (isOverlay) {
                deleteFile(destDirPath);
            } else {
                return true;
            }
        } else {
            // 创建目的目录
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
     * 复制单个文件 覆盖
     *
     * @param srcFileName  待复制的文件绝对路径
     * @param destFileName 目标文件绝对路径
     * @return 如果复制成功返回true，否则返回false
     */
    public static boolean copyFile(String srcFileName, String destFileName) {
        return copyFile(srcFileName, destFileName, true);
    }

    /**
     * 复制单个文件
     *
     * @param srcFilePath  待复制的文件绝对路径
     * @param destFilePath 目标文件绝对路径
     * @param isOverlay    如果目标文件存在，是否覆盖
     * @return 复制成功返回true，否则返回false
     */
    public static boolean copyFile(String srcFilePath, String destFilePath, boolean isOverlay) {
        File srcFile = new File(srcFilePath);
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            return false;
        } else if (!srcFile.isFile()) {
            return false;
        }
        // 判断目标文件是否存在
        File destFile = new File(destFilePath);
        if (destFile.exists()) {
            // 如果目标文件存在并允许覆盖
            if (isOverlay) {
                // 删除已经存在的目标文件，无论目标文件是目录还是单个文件
                if (!deleteFile(destFilePath)) {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            // 如果目标文件所在目录不存在，则创建目录
            if (!destFile.getParentFile().exists()) {
                // 目标文件所在目录不存在
                if (!destFile.getParentFile().mkdirs()) {
                    // 复制文件失败：创建目标文件所在目录失败
                    return false;
                }
            }
        }
        // 文件通道的方式来进行文件复制
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

}
