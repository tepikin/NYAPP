package com.lazard.nyapp.nyapp.ui.edit.stickers.stickersView;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class AnimationRotate extends Animation {

    private static final long DURATION = 250;

    private double angleOld = 0;

    private double digress;

    public AnimationRotate() {
        this(50);
    }

    public AnimationRotate(double digress) {
        super();
        this.digress = digress;
        setDuration(DURATION);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        double angle = interpolatedTime * digress;
        double angleDiff = angle - angleOld;
        angleOld = angle;
        onRotate(angleDiff);
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
