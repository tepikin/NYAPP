package com.lazard.nyapp.nyapp.ui.edit.stickers.stickersView;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class AnimationRotateShake extends Animation {

    private static final long DURATION = 250;

    private static final float REPEAT_COUNT = 2;

    private double angleOld = 0;

    private double digress;

    public AnimationRotateShake() {
        this(2);
    }

    public AnimationRotateShake(double digress) {
        super();
        this.digress = digress;
        setDuration(DURATION);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        try {
            double position = (float) Math.sin(interpolatedTime * 2 * Math.PI
                    * REPEAT_COUNT);
            double angle = position * digress;
            double angleDiff = angle - angleOld;
            angleOld = angle;
            onRotate(angleDiff);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        applyTransformation(1, null);
    }

    protected void onRotate(double angleDiff) {
        // do nothing

    };
}
