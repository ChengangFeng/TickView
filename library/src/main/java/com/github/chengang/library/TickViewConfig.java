package com.github.chengang.library;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by 陈岗不姓陈 on 2017/10/30.
 * <p>
 * 控件配置
 */

public class TickViewConfig implements Serializable {

    private volatile boolean isNeedToReApply;

    private boolean clickable = true;

    private int unCheckBaseColor;
    private int checkBaseColor;
    private int checkTickColor;
    private int radius;

    //勾的半径
    private float tickRadius;
    //勾的偏移
    private float tickRadiusOffset;

    private OnCheckedChangeListener mOnCheckedChangeListener;
    private TickAnimatorListener mTickAnimatorListener;

    public TickViewConfig(Context context) {
        this(context, null);
    }

    public TickViewConfig(Context context, TickViewConfig config) {
        if (config != null) {
            setConfig(config);
        } else {
            setNeedToReApply(true);
            setupDefaultValue(context);
        }
    }

    private void setupDefaultValue(Context context) {
        this.setUnCheckBaseColor(Color.parseColor("#ffeaeaea"))
                .setCheckBaseColor(Color.parseColor("#fff5d747"))
                .setCheckTickColor(Color.WHITE)
                .setRadius(DisplayUtil.dp2px(context, 30))
                .setClickable(true)
                .setTickRadius(DisplayUtil.dp2px(context, 12))
                .setTickRadiusOffset(DisplayUtil.dp2px(context, 4));
    }

    boolean isNeedToReApply() {
        return isNeedToReApply;
    }

    TickViewConfig setNeedToReApply(boolean needToReApply) {
        this.isNeedToReApply = needToReApply;
        return this;
    }


    public boolean isClickable() {
        return clickable;
    }

    public TickViewConfig setClickable(boolean clickable) {
        this.clickable = clickable;
        return this;
    }

    public int getUnCheckBaseColor() {
        return unCheckBaseColor;
    }

    public TickViewConfig setUnCheckBaseColor(int unCheckBaseColor) {
        this.unCheckBaseColor = unCheckBaseColor;
        return setNeedToReApply(true);
    }

    public int getCheckBaseColor() {
        return checkBaseColor;
    }

    public TickViewConfig setCheckBaseColor(int checkBaseColor) {
        this.checkBaseColor = checkBaseColor;
        return setNeedToReApply(true);
    }

    public int getCheckTickColor() {
        return checkTickColor;
    }

    public TickViewConfig setCheckTickColor(int checkTickColor) {
        this.checkTickColor = checkTickColor;
        return setNeedToReApply(true);
    }

    public int getRadius() {
        return radius;
    }

    public TickViewConfig setRadius(int radius) {
        this.radius = radius;
        return setNeedToReApply(true);
    }

    public float getTickRadius() {
        return tickRadius;
    }

    public TickViewConfig setTickRadius(float tickRadius) {
        this.tickRadius = tickRadius;
        return setNeedToReApply(true);
    }

    public float getTickRadiusOffset() {
        return tickRadiusOffset;
    }

    public TickViewConfig setTickRadiusOffset(float tickRadiusOffset) {
        this.tickRadiusOffset = tickRadiusOffset;
        return setNeedToReApply(true);
    }

    public OnCheckedChangeListener getOnCheckedChangeListener() {
        return mOnCheckedChangeListener;
    }

    public TickViewConfig setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener;
        return this;
    }

    public TickAnimatorListener getTickAnimatorListener() {
        return mTickAnimatorListener;
    }

    public TickViewConfig setTickAnimatorListener(TickAnimatorListener tickAnimatorListener) {
        mTickAnimatorListener = tickAnimatorListener;
        return this;
    }


    public TickViewConfig setConfig(@NonNull TickViewConfig config) {
        if (config == null) return this;
        return setClickable(config.isClickable())
                .setUnCheckBaseColor(config.getUnCheckBaseColor())
                .setCheckBaseColor(config.getCheckBaseColor())
                .setCheckTickColor(config.getCheckTickColor())
                .setOnCheckedChangeListener(config.getOnCheckedChangeListener())
                .setTickAnimatorListener(config.getTickAnimatorListener())
                .setTickRadius(config.getTickRadius())
                .setTickRadiusOffset(config.getTickRadiusOffset());

    }
}
