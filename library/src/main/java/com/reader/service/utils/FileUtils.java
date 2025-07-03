package com.reader.service.utils;

import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by unengchan on 2017/12/26.
 * 文件操作帮助类
 */

public class FileUtils {

    private FileUtils() {
    }


    /**
     * 通过路径获取文件，路径由目录和文件名组成
     *
     * @param dir
     * @param fileName
     * @return
     */
    public static File getFileFromPath(String dir, String fileName) {
        if (EmptyUtils.isEmpty(dir) || EmptyUtils.isEmpty(fileName)) {
            return null;
        }
        File file = new File(dir + File.separator + fileName);
        if (file.exists()) {
            return file;
        }
        return null;
    }

    /**
     * 删除某一路径下的所有文件和文件夹
     *
     */
    public static void deleteFile(File file) {
        try {
            if (file.exists()) {
                // 如果不是文件夹，直接删除
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFile(files[i]);
                    }
                }
                file.delete();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            deleteFile(new File(filePath));
        }
    }

    public static boolean createDirectory(String path) {
        File file = new File(path);
        return file.mkdirs();
    }


    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String formatFileSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
//            return size + "Byte";
            return "0K";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    public static void close(Closeable os){
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                
            }
        }
    }

    public static boolean isFileCanCreate(String filePath) {
        try {
            File file = new File(filePath);
            if (file.createNewFile()) {
                file.delete();
                return true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取文件列表中创建时间最晚的文件
     * @param folderPath
     * @return
     */
    public static ArrayList<String> getAllDataFileName(String folderPath){
        ArrayList<String> fileList = new ArrayList<>();
        File file = new File(folderPath);
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            System.out.println("文     件：" + tempList[i].getName());
            String fileName = tempList[i].getName();
            if (fileName.endsWith(".log")){    //  根据自己的需要进行类型筛选
                fileList.add(fileName.split("\\.")[0]);
            }
        }
        return fileList;
    }


    /**
     *
     * <p>
     * 〈获取最大日期和最小日期〉
     * </p>
     *
     * @param dates
     * @return
     */
    public static String getMinAndMaxDate(List<String> dates){
        if(null == dates || dates.size()<=0){
            return null;
        }
        if(dates.size()<2){
            return dates.get(0)+".log";
        }
        //自定义list排序，集合数据(月份)按升序排序;
        final SimpleDateFormat sdft = new SimpleDateFormat("yyyyMMdd");//yyyy-MM-dd HHmmss
        Collections.sort(dates,new Comparator<String>() {
            public int compare(String o1, String o2) {
                try {
                    Date date1 = sdft.parse(o1);
                    Date date2 = sdft.parse(o2);
                    if(date1.getTime() < date2.getTime()){
                        return -1;//调整顺序,-1为不需要调整顺序;
                    }
                    if(o1.equals(o2)){
                        return 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 1;
            }
        });
        return dates.get(dates.size()-1)+".log";
    }

    /***
     * 保存文件内容
     * @param data
     * @param filePath
     */
    public static void saveBytesToSD(byte[] data, String filePath)  {
        if (filePath != null && !filePath.isEmpty()) {
            File file = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
            if (!file.exists()) {
                file.mkdirs();
            }

            try (BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(filePath))) {
                bos.write(data);
                bos.flush();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
