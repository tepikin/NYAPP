package com.lazard.nyapp.nyapp.ui.edit.stickers.stickersView;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class Controller extends RotateController {

    private ImageViewRotate view;

    private ControllerItem item;

    public Controller(Context context) {
        super(context);
    }

    public ControllerItem getItem() {
        return item;
    }

    public View getView() {
        return view;
    }

    @Override
    public void onRotation(double radians, Point p1, Point p2) {
        float digres = (float) Math.toDegrees(radians);
        item.rotateCenter(digres);
        view.invalidate();
        super.onRotation(radians, p1, p2);
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        float scaleFactor = detector.getScaleFactor();
        item.scaleCenter(scaleFactor);
        view.invalidate();
        return super.onScale(detector);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY) {

        float point[] = new float[] { -distanceX, -distanceY };
        Matrix imageMatrix = new Matrix(view.getImageMatrix());
        Matrix matrixInvert = new Matrix();
        imageMatrix.invert(matrixInvert);
        matrixInvert.mapVectors(point);

        item.translate(point[0], point[1]);
        view.invalidate();
        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    public void setItem(ControllerItem item) {
        this.item = item;
    }

    public void setView(ImageViewRotate view) {
        this.view = view;
    }

}
