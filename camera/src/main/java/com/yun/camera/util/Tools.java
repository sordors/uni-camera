package com.yun.camera.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.io.IOException;

public final class Tools {

    private Tools() {
        throw new AssertionError();
    }

    private static DisplayMetrics getDisplayMetrics(Context mContext) {
        return mContext.getResources().getDisplayMetrics();
    }

    public static int getScreenwidth(Context mContext) {
        return getDisplayMetrics(mContext).widthPixels;
    }

    public static int getScreenHeight(Context mContext) {
        return getDisplayMetrics(mContext).heightPixels;
    }

    public static int dp2px(Context mContext, float dipValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, getDisplayMetrics(mContext));
    }


    public static int[] getViewLocal(View view) {
        int[] outLocation = new int[2];
        view.getLocationInWindow(outLocation);
        return outLocation;
    }


    public static Matrix pictureDegree(String imgPath) {
        Matrix matrix = new Matrix();
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imgPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (exif == null)
            return matrix;
        int degree = 0;
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
            default:
                break;
        }

        System.out.println("旋转角度：" + degree);
        matrix.postRotate(degree);

        return matrix;
    }


    public static Bitmap bitmapClip(String imgPath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        Log.d("wld__________bitmap", "width:" + bitmap.getWidth() + "--->height:" + bitmap.getHeight());
        Matrix matrix = pictureDegree(imgPath);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap;
    }


    public static Bitmap landscapeBitmapClip(Context mContext, String imgPath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        Log.d("wld__________bitmap", "width:" + bitmap.getWidth() + "--->height:" + bitmap.getHeight());
        Matrix matrix = pictureDegree(imgPath);
        double bitmapRatio = bitmap.getHeight() * 1. / bitmap.getWidth();//基本上都是16/9
        int width = getScreenwidth(mContext);
        int height = getScreenHeight(mContext);
        double screenRatio = height * 1. / width;//屏幕的宽高比
        if (bitmapRatio > screenRatio) {//胖的手机
            int clipHeight = (int) (bitmap.getWidth() * screenRatio);
            bitmap = Bitmap.createBitmap(bitmap, 0, (bitmap.getHeight() - clipHeight) >> 1, bitmap.getWidth(), clipHeight, matrix, true);
        } else {//瘦长的手机
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }


}
