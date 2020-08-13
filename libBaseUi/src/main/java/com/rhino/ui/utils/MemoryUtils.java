package com.rhino.ui.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import com.rhino.log.LogUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author rhino
 * @since Create on 2019/12/9.
 **/
public class MemoryUtils {

    private static final String TAG = MemoryUtils.class.getSimpleName();

    public static void getMemoryInfo_2(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        long availMem = outInfo.availMem;//单位是字节
        StringBuffer sb = new StringBuffer();
        try {
            BufferedReader burf = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/meminfo")));
            String strInfo = burf.readLine();
            char[] chInfo = strInfo.toCharArray();
            int size = chInfo.length;
            for (int i = 0; i < size; i++) {
                if (chInfo[i] <= '9' && chInfo[i] >= '0') {
                    sb.append(chInfo[i]);
                }
            }
            burf.close();
        } catch (Exception e) {
            LogUtils.e(e);
        }
        String totalMem = sb.toString();//单位是KB
        LogUtils.i("availMem = " + Formatter.formatFileSize(context, availMem));
        LogUtils.i("totalMem = " + totalMem);
    }

    public static void readSDCard() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSize();
            long blockCount = sf.getBlockCount();
            long availCount = sf.getAvailableBlocks();

            LogUtils.d("block大小:" + blockSize + ",block数目:" + blockCount + ",总大小:" + (blockSize * blockCount) / 1024 + "KB");
            LogUtils.d("可用的block数目:" + availCount + ",剩余空间:" + (availCount * blockSize / 1024) + "KB");
        }
    }


    /**
     * /proc/meminfo
     * MemTotal:        2902436 kB
     * MemFree:          199240 kB
     * MemAvailable:    1088764 kB
     * Buffers:           40848 kB
     * Cached:           862908 kB
     * SwapCached:        54696 kB
     * Active:          1222848 kB
     * Inactive:         671468 kB
     * Active(anon):     758516 kB
     * Inactive(anon):   242560 kB
     * Active(file):     464332 kB
     * Inactive(file):   428908 kB
     * Unevictable:        5972 kB
     * Mlocked:             256 kB
     * SwapTotal:       1048572 kB
     * SwapFree:         537124 kB
     * Dirty:                12 kB
     * Writeback:             0 kB
     * AnonPages:        988820 kB
     * Mapped:           508996 kB
     * Shmem:              4800 kB
     * Slab:             157204 kB
     * SReclaimable:      57364 kB
     * SUnreclaim:        99840 kB
     * KernelStack:       41376 kB
     * PageTables:        51820 kB
     * NFS_Unstable:          0 kB
     * Bounce:                0 kB
     * WritebackTmp:          0 kB
     * CommitLimit:     2499788 kB
     * Committed_AS:   76292324 kB
     * VmallocTotal:   258867136 kB
     * VmallocUsed:           0 kB
     * VmallocChunk:          0 kB
     * CmaTotal:              0 kB
     * CmaFree:               0 kB
     */
    public static List<String> getMemInfo() {
        List<String> result = new ArrayList<>();
        try {
            String line;
            BufferedReader br = new BufferedReader(new FileReader("/proc/meminfo"));
            while ((line = br.readLine()) != null) {
                result.add(line);
                LogUtils.d(line);
            }
            br.close();
        } catch (IOException e) {
            LogUtils.e(e);
        }
        return result;
    }


    /**
     * MemTotal 所有可用的 RAM 大小，物理内存减去预留位和内核使用
     * MemFree LowFree + HighFree
     * Buffers 用来给块设备做缓存的大小（文件系统的 metadata, tracking in-flight pages）
     * Cached 文件的缓冲区大小
     * SwapCached 已经被交换出来的内存。与 I/O 相关
     * Active 经常（最近）被使用的内存
     * Inactive 最近不常使用的内存。这很容易被系统移做他用
     * Active(anon) ?
     * Inactive(anon) ?
     * Unevictable ?
     * Mlocked ?
     * HighTotal 所有在 860MB（0x35C00000）以上的空间。主要是用户空间程序或缓存页
     * LowTotal 860MB 以下的空间。如果该空间用完了，系统可能会异常
     * HighFree 860MB 以上空间的可用空间
     * LowFree 860MB 以下空间的可用空间
     * SwapTotal 交换空间总和
     * SwapFree RAM 暂存在 Swap 中的大小
     * Dirty 等待写回的数据大小
     * WriteBack 正在写回的数据大小
     * Mapped 映射文件大小
     * AnonPages 映射到用户空间的非文件页表大小
     * Shmem ?
     * Slab 内核数据结构缓存
     * SReclaimable Slab 的一部分。当内存压力大时，可以 reclaim
     * SUnreclaim 不可以 reclaim 的 Slab
     * KernelStack ?
     * PageTables 最底层的页表的内存空间
     * NFS_Unstable 已经发给 NFS 服务器、但是尚未被确认（committed）写入到稳定存储的页表
     * Bonce ?
     * WritebackTmp Memory used by FUSE for temporary writeback buffers
     * CommitLimit CommitLimit = ('vm.overcommit_ratio' * Physical RAM)
     * Committed_AS The amount of memory presently allocated on the system.（系统中目前分配了的内存？）
     * VmallocTotal vmalloc 内存大小
     * VmallocUsed 已被使用的虚拟内存大小
     * VmallocChunk 在 vmalloc 区域中可用的最大的连续内存块的大小
     */
    public static String getFieldFromMemoryInfo(String field) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("/proc/meminfo"));
        Pattern p = Pattern.compile(field + "\\s*:\\s*(.*)");
        try {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher m = p.matcher(line);
                if (m.matches()) {
                    return m.group(1);
                }
            }
        } finally {
            br.close();
        }
        return null;
    }

    public static String getMemTotal() {
        String result = null;

        try {
            result = getFieldFromMemoryInfo("MemTotal");
        } catch (IOException e) {
            LogUtils.e(e);
        }

        return result;
    }

    public static String getMemAvailable() {
        String result = null;

        try {
            result = getFieldFromMemoryInfo("MemAvailable");
        } catch (IOException e) {
            LogUtils.e(e);
        }

        return result;
    }

    public static String formatSize(long size) {
        String suffix = null;
        float fSize;
        if (size >= 1024) {
            suffix = "KB";
            fSize = size / 1024;
            if (fSize >= 1024) {
                suffix = "MB";
                fSize /= 1024;
            }
            if (fSize >= 1024) {
                suffix = "GB";
                fSize /= 1024;
            }
        } else {
            fSize = size;
        }
        java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
        StringBuilder resultBuffer = new StringBuilder(df.format(fSize));
        if (suffix != null)
            resultBuffer.append(suffix);
        return resultBuffer.toString();
    }


    public static long[] getRomMemroy() {
        long[] romInfo = new long[2];
        //Total rom memory
        romInfo[0] = getTotalInternalMemorySize();

        //Available rom memory
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        romInfo[1] = blockSize * availableBlocks;

        LogUtils.d(romInfo[0] + ", " + romInfo[1]);
        return romInfo;
    }

    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }


    /**
     * 获取运行内存，GB
     */
    public static String getTotalRam() {
        String path = "/proc/meminfo";
        String firstLine;
        int totalRam = 0;
        BufferedReader bufferedReader = null;
        try {
            FileReader fileReader = new FileReader(path);
            bufferedReader = new BufferedReader(fileReader, 8192);
            firstLine = bufferedReader.readLine().split("\\s+")[1];
            if (firstLine != null) {
                totalRam = (int) Math.ceil((Float.valueOf(firstLine) / (1024 * 1024)));
            }
        } catch (Exception e) {
            LogUtils.e(e.toString());
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                LogUtils.e(e);
            }
        }
        LogUtils.d("totalRam = " + totalRam);
        return totalRam + "GB";
    }

    /**
     * SD卡
     */
    public static String getSDCardInfo() {
        return getSDCardInfoInt() + "GB";
    }

    /**
     * SD卡
     */
    public static int getSDCardInfoInt() {
        int totalGb = 0;
        try {
            File path = Environment.getExternalStorageDirectory();
            StatFs s = new StatFs(path.getPath());
            long availableBlocks = s.getAvailableBlocksLong();
            long blockCount = s.getBlockCountLong();
            long blockSize = s.getBlockSizeLong();
            long totalsize = blockSize * blockCount;
            long availsize = blockSize * availableBlocks;
            totalGb = (int) Math.ceil((Float.valueOf(totalsize) / (1024 * 1024 * 1024)));
            if (totalGb > 4 && totalGb < 8) {
                totalGb = 8;
            } else if (totalGb > 8 && totalGb < 16) {
                totalGb = 16;
            } else if (totalGb > 16 && totalGb < 32) {
                totalGb = 32;
            } else if (totalGb > 32 && totalGb < 64) {
                totalGb = 64;
            } else if (totalGb > 64 && totalGb < 128) {
                totalGb = 128;
            } else if (totalGb > 128 && totalGb < 256) {
                totalGb = 256;
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return totalGb;
    }

    /**
     * 手机内存
     *
     * @param context
     */
    public static void getDataInfo(Context context) {
        try {
            File path = Environment.getDataDirectory();
            StatFs s = new StatFs(path.getPath());
            long availableBlocks = s.getAvailableBlocksLong();
            long blockCount = s.getBlockCountLong();
            long blockSize = s.getBlockSizeLong();
            long totalsize = blockSize * blockCount;
            long availsize = blockSize * availableBlocks;
            String totalsizeStr = Formatter.formatFileSize(context, totalsize);
            String availsizeStr = Formatter.formatFileSize(context, availsize);
            LogUtils.d("totalsizeStr = " + totalsizeStr);
            LogUtils.d("availsizeStr = " + availsizeStr);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }


    /**
     * 计算Sdcard的剩余大小
     *
     * @return MB
     */
    public static float getAvailableSize() {
        //得到外部储存sdcard的状态
        String sdcard = Environment.getExternalStorageState();
        //外部储存sdcard存在的情况
        String state = Environment.MEDIA_MOUNTED;
        //获得路径
        File file = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(file.getPath());
        if (sdcard.equals(state)) {
            //获得Sdcard上每个block的size
            long blockSize = statFs.getBlockSize();
            //获取可供程序使用的Block数量
            long blockavailable = statFs.getAvailableBlocks();
            //计算标准大小使用
            long blockavailableTotal = blockSize * blockavailable;
            float size = (float) Math.ceil((Float.valueOf(blockavailableTotal) / (1024 * 1024)));
            return size;
        } else {
            return 0f;
        }
    }

}
