package com.itedu.paramersetting.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by wangx on 2016/7/6.
 * 帮助管理 目录
 */
public class FileUtils {
    public static final String ROOT = "nihao";
    public static final String CACHE = "cache";


    public static File getDir(String dir) {
        /**
         * append
         */
        StringBuilder stringBuilder = new StringBuilder();

        //   /mnt/sdcard/GooglePlayz14/cache      --->  /data/data/包名/cache/cache

            //    获取  sd卡的根目录
            String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath(); // /mnt/sdcard
            stringBuilder.append(sdcardPath);  //  /mnt/sdcard
            //    /
            stringBuilder.append(File.separator); //   /mnt/sdcard/
            stringBuilder.append(ROOT);// /mnt/sdcard/GooglePlayz14
            stringBuilder.append(File.separator);  ///mnt/sdcard/GooglePlayz14/
            stringBuilder.append(dir);//  /mnt/sdcard/GooglePlayz14/cache
//            //   /data/data/包名/cache/cache
//            String cachePath = UIUtils.getContext().getCacheDir().getAbsolutePath();//  /data/data/包名/cache
//            stringBuilder.append(cachePath);
//            ///data/data/包名/cache/cache
//            stringBuilder.append(File.separator);
//            stringBuilder.append(dir);
        File file = new File(stringBuilder.toString());

        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * sd卡是否可用
     * @return
     */
    private static boolean isSdCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取缓存路径
     * @return
     */
    public static File getCache(){
        return getDir(CACHE);
    }
}
