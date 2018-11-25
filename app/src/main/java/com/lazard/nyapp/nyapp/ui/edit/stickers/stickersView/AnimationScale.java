package com.lazard.nyapp.nyapp.ui.edit.stickers.stickersView;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class AnimationScale extends Animation {

    private static final long DURATION = 250;

    private float scaleOld = 1;

    public AnimationScale() {
        super();
        setDuration(DURATION);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        interpolatedTime = -interpolatedTime * 2 + 1;

        float scale = 1;
        if (scaleOld != 0) {
            scale = interpolatedTime / scaleOld;
        }
        if (scaleOld * scale == 0) {
            scale = 1;
        }
        scaleOld *= scale;
        onScale(scale);
    }

    @Override
    public void cancel() {
        super.cancel();
        applyTransformation(1, null);
    };

    public void onScale(float scale) {
    }

}
