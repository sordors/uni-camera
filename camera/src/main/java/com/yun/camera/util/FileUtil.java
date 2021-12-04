package com.yun.camera.util;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by gxj on 2018/2/8.
 */
public class FileUtil {
    //private static final File parentPath = Environment.getExternalStorageDirectory();
    private static String storagePath = "";
    private static final String DST_FOLDER_NAME = "Yun";
    private static String imgPath;

    /**
     * 初始化保存路径
     *
     * @return
     */
    private static String initPath(Context context) {
        if (storagePath.equals("")) {
            storagePath = context.getExternalFilesDir(null) + File.separator + DST_FOLDER_NAME;
            File f = new File(storagePath);
            if (!f.exists()) {
                f.mkdir();
            }
        }
        return storagePath;
    }

    /**
     * 保存Bitmap到sdcard
     *
     * @param b 得到的图片
     */
    public static void saveBitmap(Context context, Bitmap b) {
        String path = initPath(context);
        long dataTake = System.currentTimeMillis();
        imgPath = path + "/" + dataTake + ".jpg";
        try {
            FileOutputStream fout = new FileOutputStream(imgPath);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @return 保存到sd卡的图片路径
     */
    public static String getImgPath() {
        return imgPath;
    }

}
