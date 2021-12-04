package com.yun.camera.activity;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.FocusMeteringResult;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceOrientedMeteringPointFactory;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.ListenableFuture;
import com.yun.camera.CameraModule;
import com.yun.camera.ParamBean;
import com.yun.camera.R;
import com.yun.camera.util.FileUtil;
import com.yun.camera.util.Tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by gxj on 2018/2/18 11:46.
 * 拍照界面
 */
public final class CameraxActivity extends FragmentActivity implements View.OnClickListener {


    public final static int REQUEST_CODE = 0X13;

    private ImageCapture imageCapture;
    /**
     * camera的相关信息，例如缩放级别等
     */
    private CameraInfo mCameraInfo;
    /**
     * 摄像头控制，例如缩放，对焦等
     */
    private CameraControl mCameraControl;
    /**
     * 摄像头选择实例，可以指定前或者后置摄像头
     */
    private CameraSelector cameraSelector;

    private PreviewView cameraView;
    private LinearLayout cameraContainer;
    private LinearLayout containerView;
    private ImageView cropView;
    private ImageView closeView;
    private ImageView cameraTake;
    private ImageView enableTorch;
    private ImageView takeSuccess;
    private ImageView takeCancel;
    private LinearLayout previewPicture;
    private ImageView imgPicture;
    private LinearLayout cameraOption;
    private LinearLayout cameraTakeOption;
    private TextView touchText;

    private Bitmap photoBitmap = null;

    private boolean enableTorchStatus = false;
    private ParamBean paramBean;

    /**
     * 跳转到拍照页面
     */
    public static void navToCamera(Context context, JSONObject options) {
        Intent intent = new Intent(context, CameraxActivity.class);
        intent.putExtra("options", options.toJSONString());
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        String planString = getIntent().getStringExtra("options");
        Log.d("planString", planString);
        paramBean = JSONObject.parseObject(planString, ParamBean.class);

        super.onCreate(savedInstanceState);
        if (paramBean.getLandscape()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }


        setContentView(R.layout.camerax);
        cameraContainer = findViewById(R.id.camera_container);
        containerView = findViewById(R.id.camera_crop_container);
        cropView = findViewById(R.id.camera_crop);
        cameraView = findViewById(R.id.cameraView);
        closeView = findViewById(R.id.camera_close);
        cameraTake = findViewById(R.id.camera_take);
        enableTorch = findViewById(R.id.enable_torch);
        cameraOption = findViewById(R.id.camera_option);
        cameraTakeOption = findViewById(R.id.camera_take_option);
        takeSuccess = findViewById(R.id.take_success);
        takeCancel = findViewById(R.id.take_cancel);
        previewPicture = findViewById(R.id.preview_picture);
        imgPicture = findViewById(R.id.img_picture);
        touchText = findViewById(R.id.touch_text);


        //获取屏幕最小边，设置为cameraPreview较窄的一边
        float screenMinSize = Math.min(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);

        //横屏配置
        float height = (int) (screenMinSize * 0.75);
        float width = (int) (height * 75.0f / 47.0f);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams((int) width, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams cropParams = new LinearLayout.LayoutParams((int) width, (int) height);

        //竖屏配置
        if (!paramBean.getLandscape()) {
            width = (int) (screenMinSize * 0.9);
            height = (int) (width * 47.0f / 75.0f);
            containerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) height);
            cropParams = new LinearLayout.LayoutParams((int) width, (int) height);
        }

        containerView.setLayoutParams(containerParams);
        cropView.setLayoutParams(cropParams);

        //设置身份证图片
        if (paramBean.getType() == ParamBean.TYPE_ID_CARD_FRONT) {
            if (paramBean.getImageIndex().equals(0)) {
                cropView.setImageResource(R.mipmap.camera_front);
            } else if (paramBean.getImageIndex().equals(1)) {
                cropView.setImageResource(R.mipmap.idcard_icon);
            } else {
                cropView.setImageResource(R.mipmap.camera_idcard_front);
            }
        } else if (paramBean.getType() == ParamBean.TYPE_ID_CARD_BACK) {
            if (paramBean.getImageIndex().equals(0)) {
                cropView.setImageResource(R.mipmap.camera_back);
            } else if (paramBean.getImageIndex().equals(1)) {
                cropView.setImageResource(R.mipmap.idcard_back_icon);
            } else {
                cropView.setImageResource(R.mipmap.camera_idcard_back);
            }
        }


        //手电筒配置
        if (paramBean.getEnableTorch()) {
            enableTorch.setVisibility(View.VISIBLE);
        } else {
            enableTorch.setVisibility(View.GONE);
        }

        touchText.setText(paramBean.getText());

        //权限设置
        String[] permissions = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        List<String> mPermissionList = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);//添加还未授予的权限
            }
        }

        if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
            startCamera();
            initListener();
        }
    }

    /**
     * 接收权限通知结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false;//有权限没有通过
        if (1 == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                }
            }
            //如果权限都被允许
            if (!hasPermissionDismiss) {
                startCamera();
                initListener();
            }
        }

    }

    /**
     * 初始化监听
     */
    private void initListener() {
        cameraView.setOnClickListener(this);
        closeView.setOnClickListener(this);
        cameraTake.setOnClickListener(this);
        enableTorch.setOnClickListener(this);
        takeSuccess.setOnClickListener(this);
        takeCancel.setOnClickListener(this);
        takeSuccess.setOnClickListener(this);
    }

    /**
     * 启动相机
     */
    private void startCamera() {

        //通过摄像头提供者获取摄像头代理对象
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        //注册一个相机准备完成的回调，参数为一个Runnable和Executor，此方式用于解耦操作，类似于相机准备好了，回调这边的主线程进行相关的
        //相机配置和初始化逻辑。
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    /**
     * 绑定相机
     *
     * @param cameraProvider
     */
    public void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Size screenAspectRatio = new Size(cameraView.getWidth(), cameraView.getHeight());

        imageCapture = new ImageCapture.Builder()
                .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
                .setTargetResolution(screenAspectRatio)
                .build();

        // .setTargetAspectRatio(AspectRatio.RATIO_16_9)预览配置
        Preview preview = new Preview.Builder()
                .setTargetResolution(screenAspectRatio)
                .build();

        // 选择摄像头配置，现在选择的是后置摄像头
        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalyzer = new ImageAnalysis.Builder()
                .setTargetResolution(screenAspectRatio)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        try {
            cameraProvider.unbindAll();
            preview.setSurfaceProvider(cameraView.getSurfaceProvider());
            Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalyzer);
            //配置预览界面
            mCameraInfo = camera.getCameraInfo();
            mCameraControl = camera.getCameraControl();
        } catch (Exception exc) {
            System.out.println(exc.getMessage());
            exc.printStackTrace();
        }
    }

    /**
     * 自动聚焦
     *
     * @param x
     * @param y
     */
    private void autoFocus(int x, int y) {
        MeteringPointFactory factory = new SurfaceOrientedMeteringPointFactory(x, y);
        MeteringPoint point = factory.createPoint(x, y);
        System.out.println("聚焦x点位：" + x + "聚焦y点位：" + y);
        FocusMeteringAction action = new FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                .setAutoCancelDuration(3, TimeUnit.SECONDS)
                .build();
        ListenableFuture<FocusMeteringResult> future = mCameraControl.startFocusAndMetering(action);
        future.addListener(() -> {
            try {
                FocusMeteringResult result = future.get();
                if (result.isFocusSuccessful()) {
                    System.out.println("聚焦成功");
                } else {
                    System.out.println("聚焦失败");
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }, ContextCompat.getMainExecutor(this));
    }


    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cameraView) {
            int[] outLocation = Tools.getViewLocal(cropView);
            autoFocus(outLocation[0] + (cropView.getMeasuredWidth()) / 2, outLocation[1] + (cropView.getMeasuredHeight()) / 2);
        } else if (id == R.id.camera_close) {
            finish();
        } else if (id == R.id.camera_take) {
            takePhoto();
        } else if (id == R.id.enable_torch) {
            if (enableTorchStatus) {
                setEnableTorch(false);
            } else {
                setEnableTorch(true);
            }
        } else if (id == R.id.take_cancel) {
            previewPicture.setVisibility(View.GONE);
            cameraOption.setVisibility(View.VISIBLE);
            cameraTakeOption.setVisibility(View.GONE);

            if (photoBitmap != null) {
                if (!photoBitmap.isRecycled()) {
                    photoBitmap.recycle();
                    photoBitmap = null;
                }
            }
        } else if (id == R.id.take_success) {
            cutPhoto();
        }
    }

    /**
     * 裁剪图片
     */
    public void cutPhoto() {
        if (photoBitmap == null) {
            return;
        }
        float left = ((float) containerView.getLeft() - (float) cameraView.getLeft()) / (float) cameraView.getWidth();
        float top = (float) cropView.getTop() / (float) cameraView.getHeight();
        float right = (float) containerView.getRight() / (float) cameraView.getWidth();
        float bottom = (float) cropView.getBottom() / (float) cameraView.getHeight();

        if (!paramBean.getLandscape()) {
            left = ((float) cropView.getLeft() - (float) cameraView.getLeft()) / (float) cameraView.getWidth();
            top = (float) containerView.getTop() / (float) cameraView.getHeight();
            right = (float) cropView.getRight() / (float) cameraView.getWidth();
            bottom = (float) containerView.getBottom() / (float) cameraView.getHeight();
        }

        //裁剪及保存到文件
        Bitmap resBitmap = Bitmap.createBitmap(photoBitmap,
                (int) (left * (float) photoBitmap.getWidth()),
                (int) (top * (float) photoBitmap.getHeight()),
                (int) ((right - left) * (float) photoBitmap.getWidth()),
                (int) ((bottom - top) * (float) photoBitmap.getHeight()));

        FileUtil.saveBitmap(this, resBitmap);

        if (!photoBitmap.isRecycled()) {
            photoBitmap.recycle();
            photoBitmap = null;
        }

        if (!resBitmap.isRecycled()) {
            resBitmap.recycle();
        }

        //拍照完成，返回对应图片路径
        Intent intent = new Intent();
        intent.putExtra("result", FileUtil.getImgPath());

        setResult(CameraModule.RESULT_CODE, intent);
        finish();
    }

    /**
     * 手电筒
     *
     * @param state
     */
    public void setEnableTorch(Boolean state) {
        try {
            mCameraControl.enableTorch(state);
            enableTorchStatus = state;
            if (state) {
                enableTorch.setImageResource(R.mipmap.enable_torch_open);
            } else {
                enableTorch.setImageResource(R.mipmap.enable_torch);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 进行拍照
     */
    private void takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        if (imageCapture == null) {
            return;
        }
        String photoFile = System.currentTimeMillis() + "_cameraX.jpg";
        // 配置保存的文件夹
        File file = new File(this.getExternalCacheDir(), photoFile);
        // 拍照保存文件的相关配置
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(file).build();

        // 进行拍照以及监听拍照完成的回调和异常回调
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                previewPicture.setVisibility(View.VISIBLE);
                cameraOption.setVisibility(View.GONE);
                cameraTakeOption.setVisibility(View.VISIBLE);
                if (!paramBean.getLandscape()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    Matrix matrix = Tools.pictureDegree(file.getAbsolutePath());
                    photoBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                } else {
                    photoBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                }

                imgPicture.setImageBitmap(photoBitmap);
                file.delete();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                exception.printStackTrace();
            }
        });
    }
}
