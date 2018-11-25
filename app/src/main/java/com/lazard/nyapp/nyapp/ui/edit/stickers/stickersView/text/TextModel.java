package com.lazard.nyapp.nyapp.ui.edit.stickers.stickersView.text;

import android.graphics.Color;

public class TextModel {

    private String text;

    private int color = Color.WHITE;

    private String font = "fonts/BebasNeue.otf";

    public static final int MAX_BORDER = 10;

    private float scalePercent;

    private float borderPercent;

    private int borderColor = Color.WHITE;

    public int getBorderColor() {
        return this.borderColor;
    }

    public float getBorderPercent() {
        return borderPercent;
    }

    public float getBorderWidth() {
        return MAX_BORDER * borderPercent;
    }

    public int getColor() {
        return color;
    }

    public String getFont() {
        return font;
    }

    public float getScalePercent() {
        return scalePercent;
    }

    public String getText() {
        return text;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public void setBorderPercent(float percent) {
        this.borderPercent = percent;

    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public void setScalePercent(float percent) {
        this.scalePercent = percent;

    }

    public void setText(String text) {
        this.text = text;
    }

}
