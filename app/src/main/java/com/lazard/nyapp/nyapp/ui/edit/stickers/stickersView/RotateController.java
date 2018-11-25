package com.lazard.nyapp.nyapp.ui.edit.stickers.stickersView;

import android.content.Context;
import android.graphics.Point;
import androidx.core.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;


public class RotateController implements OnGestureListener,
        OnScaleGestureListener, RotationGestureDetector.OnRotationGestureListener {

    private static final String TAG = "RotateController";

    private GestureDetectorCompat gestureDetector;
    private boolean isFlingLog = false;
    private boolean isRotationLog = false;
    private boolean isScaleLog = false;
    private boolean isScrollLog = false;
    private RotationGestureDetector rotationGestureDetector;
    private double rotationOld;
    private ScaleGestureDetector scaleGestureDetector;

    public RotateController(Context context) {
        super();
        gestureDetector = new GestureDetectorCompat(context, this);
        scaleGestureDetector = new ScaleGestureDetector(context, this);
        rotationGestureDetector = new RotationGestureDetector();
        rotationGestureDetector.setOnRotationGestureListener(this);
        gestureDetector.setIsLongpressEnabled(false);
    }

    public GestureDetectorCompat getGestureDetector() {
        return gestureDetector;
    }

    public RotationGestureDetector getRotationGestureDetector() {
        return rotationGestureDetector;
    }

    public ScaleGestureDetector getScaleGestureDetector() {
        return scaleGestureDetector;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        if (isFlingLog) {
            Log.e(TAG, "onFling velocityX=" + velocityX + " velocityY="
                    + velocityY);
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    public void onRotation(double radians, Point p1, Point p2) {
        if (isRotationLog) {
            Log.e(TAG, "onRotation  rotation=" + radians);
        }
    }

    @Override
    public void onRotationGestureEnded(double finalAngle, double distance,
                                       Point p1, Point p2) {
        rotationOld = 0;
        if (isRotationLog) {
            Log.e(TAG, "onRotationGestureEnded  finalAngle=" + finalAngle);
        }
    }

    @Override
    public void onRotationGestureRotated(double totalRotationAngle,
                                         double distance, Point p1, Point p2) {

        double rotation = totalRotationAngle - rotationOld;
        onRotation(rotation, p1, p2);

        rotationOld = totalRotationAngle;
        if (isRotationLog) {
            Log.e(TAG, "onRotationGestureRotated  finalAngle="
                    + totalRotationAngle);
        }
    }

    @Override
    public void onRotationGestureStarted(double distance, Point p1, Point p2) {
        rotationOld = 0;
        if (isRotationLog) {
            Log.e(TAG, "onRotationGestureStarted");
        }
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if (isScaleLog) {
            Log.e(TAG, "onScale = " + detector.getScaleFactor());
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        if (isScaleLog) {
            Log.e(TAG, "onScaleBegin = " + detector.getScaleFactor());
        }
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        if (isScaleLog) {
            Log.e(TAG, "onScaleEnd = " + detector.getScaleFactor());
        }
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        if (isScrollLog) {
            Log.e(TAG, "onScroll distanceX=" + distanceX + " distanceY="
                    + distanceY);
        }
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    public void onTouchEvent(MotionEvent event) {
        try {
            scaleGestureDetector.onTouchEvent(event);
            gestureDetector.onTouchEvent(event);
            rotationGestureDetector.onTouchEvent(event);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
