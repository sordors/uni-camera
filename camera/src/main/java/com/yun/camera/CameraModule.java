package com.yun.camera;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSONObject;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.bridge.JSCallback;
import com.yun.camera.activity.CameraxActivity;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;

public class CameraModule extends WXSDKEngine.DestroyableModule {

    String TAG = "CameraModule";
    public static int RESULT_CODE = 1000;
    private Map<String, JSCallback> uniJSCallbackMap = new HashMap<>();
    private Application application = null;

    /**
     * 获取DCloudApplication
     *
     * @return
     */
    private Application getApplication() {
        if (application == null) {
            try {
                Class<?> activityThread = Class.forName("android.app.ActivityThread");
                Method currentApplication = activityThread.getDeclaredMethod("currentApplication");
                Method currentActivityThread = activityThread.getDeclaredMethod("currentActivityThread");
                Object current = currentActivityThread.invoke((Object) null);
                Object app = currentApplication.invoke(current);
                application = (Application) app;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return application;
    }

    /**
     * 初始化
     */
    public void checkPermission() {
        Activity activity = (Activity) mWXSDKInstance.getContext();
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 0x12);
            return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CODE && data.hasExtra("result")) {
            Log.d(TAG, "拍照结束：" + data.getStringExtra("result"));
            Log.d(TAG, data.getStringExtra("result"));
            JSCallback callback = uniJSCallbackMap.get("takePhoto");
            JSONObject respond = new JSONObject();
            respond.put("file", data.getStringExtra("result"));
            respond.put("model", android.os.Build.MODEL);
            mWXSDKInstance.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + data.getStringExtra("result"))));
            callback.invoke(respond);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * type 1正面 2 反面
     *
     * @param options
     * @param callback
     */
    @UniJSMethod(uiThread = true)
    public void takePhoto(JSONObject options, JSCallback callback) {
        Log.d(TAG, "调用拍照");
        if (mWXSDKInstance != null && mWXSDKInstance.getContext() instanceof Activity) {
            try {
                CameraxActivity.navToCamera(mUniSDKInstance.getContext(), options);
                uniJSCallbackMap.put("takePhoto", callback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void destroy() {

    }
}
