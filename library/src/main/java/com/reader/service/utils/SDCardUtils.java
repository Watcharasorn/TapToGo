package com.reader.service.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
/**
 * Created by goldze on 2017/5/14.
 * SD卡相关工具类
 */
public final class SDCardUtils {

    private SDCardUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 判断SD卡是否可用
     *
     * @return true : 可用<br>false : 不可用
     */
    public static boolean isSDCardEnable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取SD卡路径
     * <p>先用shell，shell失败再普通方法获取，一般是/storage/emulated/0/</p>
     *
     * @return SD卡路径
     */
    public static String getSDCardPath() {
        if (!isSDCardEnable()) return null;
        String cmd = "cat /proc/mounts";
        Runtime run = Runtime.getRuntime();
        BufferedReader bufferedReader = null;
        try {
            Process p = run.exec(cmd);
            bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream())));
            String lineStr;
            while ((lineStr = bufferedReader.readLine()) != null) {
                if (lineStr.contains("sdcard") && lineStr.contains(".android_secure")) {
                    String[] strArray = lineStr.split(" ");
                    if (strArray.length >= 5) {
                        return strArray[1].replace("/.android_secure", "") + File.separator;
                    }
                }
                if (p.waitFor() != 0 && p.exitValue() == 1) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeIO(bufferedReader);
        }
        return Environment.getExternalStorageDirectory().getPath() + File.separator;
    }

    /**
     * 获取SD卡data路径
     *
     * @return SD卡data路径
     */
    public static String getDataPath() {
        if (!isSDCardEnable()) return null;
        return Environment.getExternalStorageDirectory().getPath() + File.separator + "data" + File.separator;
    }

//    /**
//     * 获取SD卡剩余空间
//     *
//     * @return SD卡剩余空间
//     */
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
//    public static String getFreeSpace() {
//        if (!isSDCardEnable()) return null;
//        StatFs stat = new StatFs(getSDCardPath());
//        long blockSize, availableBlocks;
//        availableBlocks = stat.getAvailableBlocksLong();
//        blockSize = stat.getBlockSizeLong();
//        return ConvertUtils.byte2FitMemorySize(availableBlocks * blockSize);
//    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getFreeSpaceNum() {
        if (!isSDCardEnable()) return 0L;
        StatFs stat = new StatFs(getSDCardPath());
        long blockSize, availableBlocks;
        availableBlocks = stat.getAvailableBlocksLong();
        blockSize = stat.getBlockSizeLong();
        return availableBlocks * blockSize;
    }



    /**
     * 获取SD卡信息
     *
     * @return SDCardInfo
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getSDCardInfo() {
        if (!isSDCardEnable()) return null;
        SDCardInfo sd = new SDCardInfo();
        sd.isExist = true;
        StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
        sd.totalBlocks = sf.getBlockCountLong();
        sd.blockByteSize = sf.getBlockSizeLong();
        sd.availableBlocks = sf.getAvailableBlocksLong();
        sd.availableBytes = sf.getAvailableBytes();
        sd.freeBlocks = sf.getFreeBlocksLong();
        sd.freeBytes = sf.getFreeBytes();
        sd.totalBytes = sf.getTotalBytes();
        return sd.toString();
    }

    public static class SDCardInfo {
        boolean isExist;
        long    totalBlocks;
        long    freeBlocks;
        long    availableBlocks;
        long    blockByteSize;
        long    totalBytes;
        long    freeBytes;
        long    availableBytes;

        @Override
        public String toString() {
            return "isExist=" + isExist +
                    "\ntotalBlocks=" + totalBlocks +
                    "\nfreeBlocks=" + freeBlocks +
                    "\navailableBlocks=" + availableBlocks +
                    "\nblockByteSize=" + blockByteSize +
                    "\ntotalBytes=" + totalBytes +
                    "\nfreeBytes=" + freeBytes +
                    "\navailableBytes=" + availableBytes;
        }
    }


    /**
//     * 获取内置sd卡的外部文件路径   例如：/storage/emulated/0/Android/data/com.seahung.toolapp/files/image
//     *
//     * @param type 文件夹名字，例如：image
//     * @return 外部文件路径
//     */
//    public static String getExternalFilesDir(String type) {
//        if (!isMounted()) {
//            return null;
//        }
//        File cacheDir = Utils.getContext().getExternalFilesDir(type);
//        if (cacheDir != null) {
//            if (cacheDir.exists()) {
//                return cacheDir.getAbsolutePath();
//            }
//            if (cacheDir.mkdirs()) {
//                return cacheDir.getAbsolutePath();
//            }
//        }
//        // 防止在应用管理清除数据时，没有清掉包名文件夹，从而导致获取不到缓存路径的问题。
//        String dirPath = getSdCardPath() + File.separator + "Android" + File.separator + "data"
//                + File.separator + AppUtils.getPackageName() + File.separator + "files" + File.separator + type;
//        File dir = new File(dirPath);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        return dirPath;
//    }

    public static String getExternalCacheDir(String type, Context context) {
        File dirFile = getExternalCacheDirFile(type,context);
        if (dirFile != null) {
            return dirFile.getAbsolutePath();
        }
        return null;
    }

    public static File getExternalCacheDirFile(String type, Context context) {
        if (!isMounted()) {
            return null;
        }
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir != null) {
            File dir = new File(cacheDir.getAbsolutePath() + File.separator + type);
            if (dir.exists()) {
                return dir;
            }
            if (dir.mkdirs()) {
                return dir;
            }
        }

        // 防止在应用管理清除数据时，没有清掉包名文件夹，从而导致获取不到缓存路径的问题。
        String dirPath = getSdCardPath() + File.separator + "Android" + File.separator + "data"
                + File.separator + context.getPackageName() + File.separator + "cache" + File.separator + type;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    @SuppressLint("SdCardPath")
    public static String getSdCardPath() {
        File file = Environment.getExternalStorageDirectory();
        if (file.exists()) {
            return file.getPath();
        }
        return "/sdcard";
    }

    /**
     * 判断内置sd卡是否挂载
     *cd /
     * @return
     */
    private static boolean isMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }


}
