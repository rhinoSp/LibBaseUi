package com.rhino.ui.utils;

import android.os.AsyncTask;

import com.rhino.log.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


/**
 * <p>The utils of zip<p>
 *
 * @author LuoLin
 * @since Create on 2017/9/30
 **/
public class ZipUtils {

    /**
     * Start zip to xxx.zip in asynchronous task
     *
     * @param srcPath  the src path(directory or file)
     * @param destFile the zip file xxx/xx/xxx.zip
     * @param callback the callback
     */
    public static void zip(String srcPath, String destFile, Callback callback) {
        ZipParams zipParams = new ZipParams(ZipParams.ACTION_ZIP, srcPath,
                destFile, callback);
        new ZipTask(zipParams).execute();
    }

    /**
     * Start unzip xxx.zip in asynchronous task
     *
     * @param zipFile         the xxx.zip file path
     * @param outputDirectory the output directory
     * @param callback        the callback
     */
    public static void unzip(String zipFile, String outputDirectory, Callback callback) {
        ZipParams zipParams = new ZipParams(ZipParams.ACTION_UNZIP,
                outputDirectory, zipFile, callback);
        new ZipTask(zipParams).execute();
    }

    /**
     * The asynchronous task
     **/
    private static class ZipTask extends AsyncTask<Void, Integer, ZipParams> {

        private ZipParams zipParams = null;

        private ZipTask(ZipParams zipParams) {
            this.zipParams = zipParams;
        }

        @Override
        protected void onPreExecute() {
            LogUtils.d("onPreExecute");
            if (zipParams != null && zipParams.callback != null) {
                zipParams.callback.onStart();
            }
        }

        @Override
        protected ZipParams doInBackground(Void... params) {
            LogUtils.d("doInBackground");
            if (ZipParams.ACTION_ZIP == zipParams.action) {
                return doZip(zipParams);
            } else if (ZipParams.ACTION_UNZIP == zipParams.action) {
                return doUnZip(zipParams);
            }
            return null;
        }

        @Override
        protected void onPostExecute(ZipParams result) {
            LogUtils.d("onPostExecute");
            if (result != null && 0 == result.error) {
                zipParams.callback.onSuccess(result);
                zipParams.callback.onProgress(100);
            } else if (result != null) {
                zipParams.callback.onError(result.error, result.errDesc);
            } else {
                zipParams.callback.onError(ZipParams.ERROR_DATA, "error data");
            }
        }
    }

    /**
     * Start compression to xxx.zip
     *
     * @param zipParams the zip param
     * @return the zip result
     */
    public static ZipParams doZip(ZipParams zipParams) {
        ZipOutputStream zipOutputStream = null;
        try {
            File outFile = new File(zipParams.destFile);
            File fileOrDirectory = new File(zipParams.srcPath);
            if (fileOrDirectory.isFile()) {
                int startProgress = 0;
                int endProgress = 100;
                zipOutputStream = new ZipOutputStream(new FileOutputStream(outFile));
                zipFileOrDirectory(zipOutputStream, fileOrDirectory, "",
                        zipParams, startProgress, endProgress);
            } else {
                File[] files = fileOrDirectory.listFiles();
                if (null != files) {
                    if (0 >= files.length) {
                        LogUtils.e("Not have file");
                        zipParams.buildError(ZipParams.ERROR_NOT_HAVE_FILE, "Not have file");
                    } else {
                        int startProgress = 0;
                        int endProgress = 0;
                        int per = (int) (100 / (float) files.length);
                        zipOutputStream = new ZipOutputStream(new FileOutputStream(outFile));
                        for (int i = 0; i < files.length; i++) {
                            startProgress = endProgress;
                            endProgress += per;
                            zipFileOrDirectory(zipOutputStream, files[i], "",
                                    zipParams, startProgress, endProgress);
                        }
                    }
                } else {
                    LogUtils.e("Not have file");
                    zipParams.buildError(ZipParams.ERROR_SRC_PATH_NOT_EXIT, "Not have file");
                }
            }
        } catch (IOException ex) {
            LogUtils.e(ex.toString());
            zipParams.buildError(ZipParams.ERROR_DATA_EXCEPTION, ex.toString());
        } finally {
            if (zipOutputStream != null) {
                try {
                    zipOutputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return zipParams;
    }

    /**
     * Zip files, support recursive compression
     *
     * @param zipOutputStream the output stream
     * @param fileOrDirectory the file or directory need zip
     * @param curPath         the current path
     * @param zipParams       the zip param
     * @param startProgress   the start progress
     * @param endProgress     the end progress
     * @throws IOException the exception
     */
    private static void zipFileOrDirectory(ZipOutputStream zipOutputStream, File fileOrDirectory,
                                           String curPath, ZipParams zipParams, int startProgress, int endProgress) throws IOException {
        FileInputStream fileInputStream = null;
        try {
            if (!fileOrDirectory.isDirectory()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                fileInputStream = new FileInputStream(fileOrDirectory);
                ZipEntry entry = new ZipEntry(curPath + fileOrDirectory.getName());
                zipOutputStream.putNextEntry(entry);
                int bytesAvailable = fileInputStream.available();
                int totalRead = 0;
                int progress = 0;
                int offset = endProgress - startProgress;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    zipOutputStream.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                    progress = Math.round(totalRead / (float) bytesAvailable * offset)
                            + startProgress;
                    zipParams.callback.onProgress(progress);
                }
                zipOutputStream.closeEntry();
            } else {
                File[] files = fileOrDirectory.listFiles();
                if (null != files) {
                    if (0 >= files.length) {
                        zipParams.callback.onProgress(endProgress);
                    } else {
                        int start = startProgress;
                        int end = startProgress;
                        int sum = endProgress - startProgress;
                        int per = (int) (sum / (float) files.length);
                        for (int i = 0; i < files.length; i++) {
                            end += per;
                            zipFileOrDirectory(zipOutputStream, files[i],
                                    curPath + fileOrDirectory.getName() + "/",
                                    zipParams, start, end);
                            start = end;
                        }
                    }
                } else {
                    LogUtils.e("Not have file");
                    zipParams.buildError(ZipParams.ERROR_SRC_PATH_NOT_EXIT, "Not have file");
                }
            }
        } catch (IOException ex) {
            LogUtils.e(ex.toString());
            zipParams.buildError(ZipParams.ERROR_DATA_EXCEPTION, ex.toString());
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * get file count in xxx.zip
     *
     * @param filePath the path of xxx.zip
     * @return the file count in xxx.zip
     */
    @SuppressWarnings("rawtypes")
    private static int getZipFileCount(String filePath) {
        int fileCount = 0;
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(filePath);
            Enumeration enumeration = zipFile.entries();
            while (enumeration.hasMoreElements()) {
                enumeration.nextElement();
                fileCount++;
            }
        } catch (IOException e) {
            LogUtils.e(e.toString());
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return fileCount;
    }

    /**
     * Start unzip file
     *
     * @param zipParams the unzip param
     * @return the unzip result
     */
    @SuppressWarnings("rawtypes")
    public static ZipParams doUnZip(ZipParams zipParams) {
        int fileCount = getZipFileCount(zipParams.destFile);
        int completeCount = 0;
        int progress = 0;
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(zipParams.destFile);
            Enumeration enumeration = zipFile.entries();
            ZipEntry zipEntry = null;
            File destPath = new File(zipParams.srcPath);
            if (!destPath.exists()) {
                if (!destPath.mkdirs()) {
                    LogUtils.e("make dir failed.");
                    zipParams.buildError(ZipParams.ERROR_DATA_EXCEPTION, "make dir failed");
                    return zipParams;
                }
            }
            while (enumeration.hasMoreElements()) {
                zipEntry = (ZipEntry) enumeration.nextElement();
                String entryName = zipEntry.getName();
                InputStream inputStream = null;
                FileOutputStream fileOutputStream = null;
                try {
                    if (zipEntry.isDirectory()) {
                        String name = zipEntry.getName();
                        name = name.substring(0, name.length() - 1);
                        File file = new File(zipParams.srcPath + File.separator
                                + name);
                        if (!file.mkdirs()) {
                            LogUtils.e("make dir failed.");
                        }
                    } else {
                        int index = entryName.lastIndexOf("\\");
                        if (index != -1) {
                            File file = new File(zipParams.srcPath
                                    + File.separator
                                    + entryName.substring(0, index));
                            if (!file.mkdirs()) {
                                LogUtils.e("make dir failed.");
                            }
                        }
                        index = entryName.lastIndexOf("/");
                        if (index != -1) {
                            File file = new File(zipParams.srcPath
                                    + File.separator
                                    + entryName.substring(0, index));
                            if (!file.mkdirs()) {
                                LogUtils.e("make dir failed.");
                            }
                        }
                        File file = new File(zipParams.srcPath + File.separator
                                + zipEntry.getName());
                        inputStream = zipFile.getInputStream(zipEntry);
                        fileOutputStream = new FileOutputStream(file);
                        byte[] buf = new byte[4096];
                        float per = 100 / (float) fileCount;
                        int bytesRead = 0;
                        int totalRead = 0;
                        int bytesAvailable = inputStream.available();
                        while ((bytesRead = inputStream.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, bytesRead);
                            totalRead += bytesRead;
                            progress = Math.round(totalRead / (float) bytesAvailable
                                    * per + completeCount * per);
                            zipParams.callback.onProgress(progress);
                        }
                        fileOutputStream.flush();
                        completeCount++;
                    }
                } catch (IOException ex) {
                    LogUtils.e(ex.toString());
                    zipParams.buildError(ZipParams.ERROR_DATA_EXCEPTION, ex.toString());
                } finally {
                    try {
                        inputStream.close();
                        fileOutputStream.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (IOException ex) {
            LogUtils.e(ex.toString());
            zipParams.buildError(ZipParams.ERROR_DATA_EXCEPTION, ex.toString());
        } finally {
            if (null != zipFile) {
                try {
                    zipFile.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return zipParams;
    }

    public static class ZipParams {

        public static final int ACTION_ZIP = 0xFF;
        public static final int ACTION_UNZIP = 0xEE;

        public static final int ERROR_SRC_PATH_NOT_EXIT = 0x1;
        public static final int ERROR_NOT_HAVE_FILE = 0x2;
        public static final int ERROR_DATA = 0x3;
        public static final int ERROR_DATA_EXCEPTION = 0x4;

        public int action;
        public String srcPath;
        public String destFile;
        public Callback callback;
        public int error;
        public String errDesc;

        public ZipParams(int action, String srcPath, String destFile, Callback callback) {
            this.action = action;
            this.srcPath = srcPath;
            this.destFile = destFile;
            this.callback = callback;
        }

        public void buildError(int error, String errDesc) {
            this.error = error;
            this.errDesc = errDesc;
        }

        @Override
        public String toString() {
            return "ZipParams{" +
                    "action=" + action +
                    ", srcPath='" + srcPath + '\'' +
                    ", destFile='" + destFile + '\'' +
                    ", callback=" + callback +
                    ", error=" + error +
                    ", errDesc='" + errDesc + '\'' +
                    '}';
        }
    }

    public interface Callback {

        void onStart();

        void onSuccess(ZipParams zipParams);

        void onError(int errNo, String desc);

        void onProgress(int progress);

    }

}
