package com.yun.camera;

import java.io.Serializable;

public class ParamBean implements Serializable {

    /**
     * 身份证正面
     */
    public final static int TYPE_ID_CARD_FRONT = 0;
    /**
     * 身份证反面
     */
    public final static int TYPE_ID_CARD_BACK = 1;

    public final static int CUSTOM_IMAGE = 2;
    /**
     * 相机类型
     */
    private Integer type = 0;

    /**
     * 图片类型
     */
    private Integer imageIndex = 0;

    /**
     * 是否开启手电筒
     */
    private Boolean enableTorch = true;

    /**
     * 是否启动横屏
     */
    private Boolean landscape = true;

    /**
     * 触摸聚焦
     */
    private String text = "触摸屏幕对焦";

    /**
     * ocr是否启动
     */
    private Boolean ocr = false;

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public Integer getImageIndex() {
        return imageIndex;
    }

    public void setImageIndex(Integer imageIndex) {
        this.imageIndex = imageIndex;
    }

    public Boolean getEnableTorch() {
        return enableTorch;
    }

    public void setEnableTorch(Boolean enableTorch) {
        this.enableTorch = enableTorch;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getLandscape() {
        return landscape;
    }

    public void setLandscape(Boolean landscape) {
        this.landscape = landscape;
    }

    public Boolean getOcr() {
        return ocr;
    }

    public void setOcr(Boolean ocr) {
        this.ocr = ocr;
    }
}
