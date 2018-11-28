package com.lazard.nyapp.nyapp.util.views.imagezoom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.ViewConfiguration;


public class ImageViewTouch extends ImageViewTouchBase {
    static final float SCROLL_DELTA_THRESHOLD = 1.0f;
    protected ScaleGestureDetector mScaleDetector;
    protected GestureDetector mGestureDetector;
    protected int mTouchSlop;
    protected float mCurrentScaleFactor;
    protected float mScaleFactor;
    protected int mDoubleTapDirection;
    protected OnGestureListener mGestureListener;
    protected OnScaleGestureListener mScaleListener;
    protected boolean mDoubleTapEnabled = true;
    protected boolean mScaleEnabled = true;
    protected boolean mScrollEnabled = true;
    private OnSigleTapConfirmed onSingleTapConfirmedListener = null;
    private OnImageViewTouchDoubleTapListener mDoubleTapListener;

    public ImageViewTouch(Context context) {
        super(context);
    }

    public ImageViewTouch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewTouch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static float getMatrixScaleX(Matrix matrix) {
        float[] f = new float[9];
        matrix.getValues(f);
        return f[Matrix.MSCALE_X];
    }

    protected void onSingleTapUp(MotionEvent e) {
        // TODO un implemented yaeat

    }

    protected void onDown(MotionEvent e) {
        // TODO un implemented yaeat

    }

    public boolean scaleBy(float scaleFactor, float focusX, float focusY) {

        float targetScale = mCurrentScaleFactor * scaleFactor;
        if (mScaleEnabled) {
            targetScale = Math.min(getMaxZoom(),
                    Math.max(targetScale, getMinZoom() - 0.1f));
            zoomTo(targetScale, focusX, focusY);
            mCurrentScaleFactor = Math.min(getMaxZoom(),
                    Math.max(targetScale, getMinZoom() - 1.0f));
            mDoubleTapDirection = 1;
            invalidate();
            return true;
        }
        return false;
    }

    @Override
    protected void _setImageDrawable(final Drawable drawable,
                                     final boolean reset, final Matrix initial_matrix,
                                     final float maxZoom) {
        super._setImageDrawable(drawable, reset, initial_matrix, maxZoom);
        mScaleFactor = getMaxZoom() / 3;
    }

    /**
     * Determines whether this ImageViewTouch can be scrolled.
     *
     * @param direction - positive direction value means scroll from right to left,
     *                  negative value means scroll from left to right
     * @return true if there is some more place to scroll, false - otherwise.
     */
    public boolean canScroll(int direction) {
        RectF bitmapRect = getBitmapRect();
        updateRect(bitmapRect, mScrollRect);
        Rect imageViewRect = new Rect();
        getGlobalVisibleRect(imageViewRect);

        if (bitmapRect.right >= imageViewRect.right) {
            if (direction < 0) {
                return Math.abs(bitmapRect.right - imageViewRect.right) > SCROLL_DELTA_THRESHOLD;
            }
        }

        double bitmapScrollRectDelta = Math.abs(bitmapRect.left
                - mScrollRect.left);
        return bitmapScrollRectDelta > SCROLL_DELTA_THRESHOLD;
    }

    public boolean getDoubleTapEnabled() {
        return mDoubleTapEnabled;
    }

    public void setDoubleTapEnabled(boolean value) {
        mDoubleTapEnabled = value;
    }

    protected OnGestureListener getGestureListener() {
        return new GestureListener();
    }

    protected OnScaleGestureListener getScaleListener() {
        return new ScaleListener();
    }

    @Override
    protected void init() {
        super.init();
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mGestureListener = getGestureListener();
        mScaleListener = getScaleListener();

        mScaleDetector = new ScaleGestureDetector(getContext(), mScaleListener);
        mGestureDetector = new GestureDetector(getContext(), mGestureListener,
                null, true);

        mCurrentScaleFactor = 1f;
        mDoubleTapDirection = 1;
    }

    @Override
    protected void onBitmapChanged(Drawable drawable) {
        super.onBitmapChanged(drawable);

        float v[] = new float[9];
        mSuppMatrix.getValues(v);
        mCurrentScaleFactor = v[Matrix.MSCALE_X];
    }

    protected float onDoubleTapPost(float scale, float maxZoom) {
        if (mDoubleTapDirection == 1) {
            if (scale + mScaleFactor * 2 <= maxZoom) {
                return scale + mScaleFactor;
            } else {
                mDoubleTapDirection = -1;
                return maxZoom;
            }
        } else {
            mDoubleTapDirection = 1;
            return 1f;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        if (!mScrollEnabled) {
            return false;
        }

        if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1) {
            return false;
        }
        if (mScaleDetector.isInProgress()) {
            return false;
        }

        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();

        if (Math.abs(velocityX) > 800 || Math.abs(velocityY) > 800) {
            scrollBy(diffX / 2, diffY / 2, 300);
            invalidate();
            return true;
        }
        return false;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        if (!mScrollEnabled) {
            return false;
        }

        if (e1 == null || e2 == null) {
            return false;
        }
        if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1) {
            return false;
        }
        if (mScaleDetector.isInProgress()) {
            return false;
        }
        // if (getScale() == 1f) {
        // return false;
        // }

        Log.d(LOG_TAG, "onScroll: " + distanceX + ", " + distanceY);
        scrollBy(-distanceX, -distanceY);
        invalidate();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        if (!mScaleDetector.isInProgress()) {
            mGestureDetector.onTouchEvent(event);
        }
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (getScale() < getMinZoom()) {
                    zoomTo(getMinZoom(), 50);
                }
                break;
        }
        return true;
    }

    @Override
    protected void onZoom(float scale) {
        super.onZoom(scale);
        if (!mScaleDetector.isInProgress()) {
            mCurrentScaleFactor = scale;
        }
    }

    @Override
    protected void onZoomAnimationCompleted(float scale) {
        super.onZoomAnimationCompleted(scale);
        if (!mScaleDetector.isInProgress()) {
            mCurrentScaleFactor = scale;
        }

        if (scale < getMinZoom()) {
            zoomTo(getMinZoom(), 50);
        }
    }

    public void setDoubleTapListener(OnImageViewTouchDoubleTapListener listener) {
        mDoubleTapListener = listener;
    }

    public void setOnSingleTapConfirmedListener(
            OnSigleTapConfirmed onSingleTapConfirmedListener) {
        this.onSingleTapConfirmedListener = onSingleTapConfirmedListener;
    }

    public void setScaleEnabled(boolean value) {
        mScaleEnabled = value;
    }

    public void setScrollEnabled(boolean value) {
        mScrollEnabled = value;
    }

    public void setSuppMatrix(Matrix matrix) {
        mSuppMatrix = new Matrix(matrix);
        setImageMatrix(getImageViewMatrix());
        mCurrentScaleFactor = getMatrixScaleX(mSuppMatrix);
        center(true, true);
    }

    public interface OnImageViewTouchDoubleTapListener {

        void onDoubleTap();
    }

    public interface OnSigleTapConfirmed {

        void onSigleTapConfirmed(MotionEvent e);
    }

    public class GestureListener extends
            GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i(LOG_TAG, "onDoubleTap. double tap enabled? "
                    + mDoubleTapEnabled);
            if (mDoubleTapEnabled) {
                float scale = getScale();
                float targetScale = scale;
                targetScale = onDoubleTapPost(scale, getMaxZoom());
                targetScale = Math.min(getMaxZoom(),
                        Math.max(targetScale, getMinZoom()));
                mCurrentScaleFactor = targetScale;
                zoomTo(targetScale, e.getX(), e.getY(),
                        DEFAULT_ANIMATION_DURATION);
                invalidate();
            }

            if (null != mDoubleTapListener) {
                mDoubleTapListener.onDoubleTap();
            }

            return super.onDoubleTap(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            return ImageViewTouch.this.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (isLongClickable()) {
                if (!mScaleDetector.isInProgress()) {
                    setPressed(true);
                    performLongClick();
                }
            }
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            return ImageViewTouch.this.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            ImageViewTouch.this.onDown(e);
            return super.onDown(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            ImageViewTouch.this.onSingleTapUp(e);
            //return super.onSingleTapUp(e);
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (ImageViewTouch.this.onSingleTapConfirmedListener != null) {
                ImageViewTouch.this.onSingleTapConfirmedListener
                        .onSigleTapConfirmed(e);
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    public class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {

        @SuppressWarnings("unused")
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float span = detector.getCurrentSpan() - detector.getPreviousSpan();
            float scaleFactor = detector.getScaleFactor();
            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();
            if (Float.isNaN(scaleFactor)) {
                return false;
            }
            return scaleBy(scaleFactor, focusX, focusY);
        }
    }
}
