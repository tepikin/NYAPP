package com.lazard.nyapp.nyapp.ui.edit.stickers.stickersView;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class AnimationTranslate extends Animation {

    private static final long DURATION = 250;

    private float translateOld = 0;

    private float distance;

    public AnimationTranslate() {
        this(50);
    }

    public AnimationTranslate(float distance) {
        super();
        this.distance = distance;
        setDuration(DURATION);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        float translate = interpolatedTime * distance;
        float translateDiff = translate - translateOld;
        translateOld = translate;
        onTranslate(translateDiff);
    }

    @Override
    public void cancel() {
        super.cancel();
        applyTransformation(1, null);
    }

    protected void onTranslate(float translateDiff) {
        // do nothing

    };

}
