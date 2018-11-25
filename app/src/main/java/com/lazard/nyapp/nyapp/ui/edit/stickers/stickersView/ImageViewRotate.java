package com.lazard.nyapp.nyapp.ui.edit.stickers.stickersView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;


import com.google.gson.Gson;
import com.lazard.nyapp.nyapp.ui.edit.EditActivity;
import com.lazard.nyapp.nyapp.ui.edit.stickers.stickersView.text.TextModel;
import com.lazard.nyapp.nyapp.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * com.camlyapp.Camly.ui.edit.view.scale.ImageViewRotate
 * 
 * @author Egor
 */
public class ImageViewRotate extends AppCompatImageView {

    private static final int BORDER_DELAY = 2000;

    private static final int ANIMATION_DELAY = 250;

    private List<Controller> controllers = new ArrayList<Controller>();

    private int currentItem = 0;

    private boolean isRotateScroll = false;

    float padding = Utils.INSTANCE.dpToPx(15, getContext());

    private GestureDetector gestureDetector = new GestureDetector(getContext(),
            new GestureDetector.SimpleOnGestureListener() {

                private PointF lastScroll = null;;

                private int touchSlopScale = 5;

                private float getAngleDiff(MotionEvent e2, PointF center) {
                    float angle1 = (float) Math.toDegrees(Math.atan2(
                            lastScroll.x - center.x, lastScroll.y - center.y));
                    float angle2 = (float) Math.toDegrees(Math.atan2(e2.getX()
                            - center.x, e2.getY() - center.y));

                    if (angle1 < 0) {
                        angle1 += 360;
                    }
                    if (angle2 < 0) {
                        angle2 += 360;
                    }
                    float digres = angle1 - angle2;
                    return digres;
                }

                private float getScaleDiff(MotionEvent e2, PointF center) {
                    float distance1 = (float) Math.sqrt(Math.pow(lastScroll.x
                            - center.x, 2)
                            + Math.pow(lastScroll.y - center.y, 2));
                    float distance2 = (float) Math.sqrt(Math.pow(e2.getX()
                            - center.x, 2)
                            + Math.pow(e2.getY() - center.y, 2));

                    float scale = 1;
                    if (distance1 > touchSlopScale) {
                        scale = distance2 / distance1;
                    }
                    return scale;
                };

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    PointF point = new PointF(e.getX(), e.getY());
                    Controller conteroller = getCurentController();
                    if (conteroller != null) {
                        boolean isApplay = conteroller.getItem().onDoubleTap(
                                point);
                        if (isApplay) {
                            return true;
                        }
                    }
                    return true;
                };

                @Override
                public boolean onDown(MotionEvent e) {
                    if (e.getAction() != MotionEvent.ACTION_DOWN) {
                        return false;
                    }
                    lastScroll = null;
                    Controller controller = getCurentController();

                    PointF point = new PointF(e.getX(), e.getY());
                    controller = getContollerByPointAndDots(point);

                    if (controller != null && controller.getItem() != null
                            && controller.getItem().getRotateDrawable() != null) {
                        lastTouchUpdate();
                        setControllerCurrent(controller);
                        RectF rect = new RectF();
                        rect.set(controller.getItem().getRotateDrawable()
                                .getBounds());
                        increaseRect(padding, rect);
                        if (rect.contains(point.x, point.y)) {
                            isRotateScroll = true;
                            return true;
                        }
                        isRotateScroll = false;
                        return false;
                    }
                    lastTouchOff();
                    isRotateScroll = false;
                    invalidate();
                    return false;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    // do nothing
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2,
                        float distanceX, float distanceY) {
                    if (isRotateScroll) {
                        if (lastScroll != null) {
                            Controller conteroller = getCurentController();
                            if (conteroller != null) {

                                if (lastTouch <= 0) {
                                    return false;
                                }

                                PointF center = conteroller.getItem()
                                        .getCenter();
                                float point[] = new float[] { center.x,
                                        center.y };
                                getImageMatrix().mapPoints(point);
                                center.set(point[0], point[1]);

                                float digres = getAngleDiff(e2, center);
                                float scale = getScaleDiff(e2, center);

                                conteroller.getItem().rotateCenter(digres);
                                conteroller.getItem().scaleCenter(scale);

                                ImageViewRotate.this.invalidate();
                            }
                        }
                        lastScroll = new PointF(e2.getX(), e2.getY());
                        return true;
                    }
                    return false;
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {

                    RectF rect = new RectF();
                    PointF point = new PointF(e.getX(), e.getY());

                    Controller conteroller = getCurentController();
                    if (conteroller != null) {
                        boolean isApplay = conteroller.getItem().onSingleTapUp(
                                point);
                        if (isApplay) {
                            lastTouchUpdate();
                            return true;
                        }
                    }

                    RotateController controller = getContollerByPoint(point);
                    if (controller == null) {
                        lastTouchOff();
                        invalidate();
                    } else {
                        lastTouchUpdate();
                        setControllerCurrent(controller);
                    }

                    return true;
                }

            });

    private Paint paint = new Paint();

    private float gridStrokeWidthDark = Utils.INSTANCE.dpToPx(2.5f,getContext());

    private float gridStrokeWidthLight = Utils.INSTANCE.dpToPx(1.5f, getContext());

    private int gridColorDark = Color.argb(255 * 20 / 100, 0, 0, 0);

    private int gridColorLight = Color.argb(255 * 40 / 100, 255, 255, 255);

    private boolean gridVisible;

    private long lastTouch = System.currentTimeMillis();

    private Runnable runnableLastUpdate = new Runnable() {

        @Override
        public void run() {
            Animation animation = new Animation() {

                @Override
                protected void applyTransformation(float interpolatedTime,
                        Transformation t) {
                    ImageViewRotate.this.invalidate();
                    super.applyTransformation(interpolatedTime, t);
                }
            };
            animation.setDuration(ANIMATION_DELAY);
            startAnimation(animation);
        }

    };

    private Handler handler;



    public ImageViewRotate(Context context) {
        super(context);
        init();
    }

    public ImageViewRotate(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageViewRotate(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void addController(Controller controller) {
        lastTouchUpdate();
        controllers.add(controller);
        currentItem = controllers.size() - 1;
    }

    public void addControllerAsynch(final String svgName, final int color,
            final ControllerItem baseItem) {

        AsyncTask<Void, Void, Controller> asyncTask = new AsyncTask<Void, Void, Controller>() {

            Controller controller = null;

            @Override
            protected Controller doInBackground(Void... params) {
                // Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                if (svgName.endsWith("png")) {
                    controller.setItem(new PngItem(svgName, getContext(),
                            ImageViewRotate.this));
                } else {
                    controller.setItem(new SvgItem(svgName, getContext(),
                            ImageViewRotate.this));
                }
                controller.setView(ImageViewRotate.this);
                controller.getItem().setColor(color);
                return controller;
            }

            @Override
            protected void onPostExecute(Controller controller) {
                lastTouchUpdate();
                if (getContext() instanceof EditActivity) {
                    ((EditActivity) getContext()).hideWater();
                }
                ImageViewRotate.this.addController(controller);
                if (baseItem == null) {
                    moveControllerToCenter(controller);
                } else {
                    ControllerItem item = controller.getItem();
                    item.matrix = new Matrix(baseItem.getMatrix());
                    item.matrixBorder = new Matrix(baseItem.getMatrixBorder());
                    translateAnimation(item);
                }
                ImageViewRotate.this.invalidate();
            }

            @Override
            protected void onPreExecute() {
                if (getContext() instanceof EditActivity) {
                    ((EditActivity) getContext()).showWater();
                }
                controller = new Controller(getContext());
            }
        };
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void alignSelectedSvg() {
        if (lastTouch <= 0) {
            return;
        }

        Controller controoler = getCurentController();
        if (controoler != null && controoler.getItem() != null
                && controoler.getItem().getMatrixBorder() != null) {
            if (getAnimation() != null) {
                getAnimation().cancel();
            }
            lastTouchUpdate();
            Matrix m = controoler.getItem().getMatrixBorder();
            double angle = Utils.INSTANCE.getRotate(m);
            float digres = (float) (angle * 180 / Math.PI);
            if (Math.abs(digres) < 1) {
                controoler.getItem().rotateCenter(digres);
                controoler.getItem().rotateCenterAnimationShake();
            } else {
                controoler.getItem().rotateCenterAnimation(digres);
                invalidate();
            }
        }

    }

    private void drawGrid(Canvas canvas, RectF rect) {
        canvas.drawLine(rect.left + rect.width() / 3, rect.top, rect.left
                + rect.width() / 3, rect.bottom, paint);
        canvas.drawLine(rect.left + rect.width() * 2 / 3, rect.top, rect.left
                + rect.width() * 2 / 3, rect.bottom, paint);
        canvas.drawLine(rect.left, rect.top + rect.height() / 3, rect.right,
                rect.top + rect.height() / 3, paint);
        canvas.drawLine(rect.left, rect.top + rect.height() * 2 / 3,
                rect.right, rect.top + rect.height() * 2 / 3, paint);
    }

    public void duplicateSelectedSvg() {
        if (lastTouch <= 0) {
            return;
        }
        lastTouchUpdate();
        if (currentItem >= 0 && currentItem < controllers.size()) {
            ControllerItem item = controllers.get(currentItem).getItem();
            if (item instanceof SvgItem) {
                String name = ((SvgItem) item).getName();
                int color = item.getColor();
                addControllerAsynch(name, color, item);
                invalidate();
            }
            if (item instanceof TextControllerItem) {
                TextControllerItem itemText = (TextControllerItem) item;

                TextModel model = new Gson().fromJson(
                        new Gson().toJson(itemText.getTextModel()),
                        TextModel.class);

                Controller controller = new Controller(getContext());
                controller.setView(this);
                TextControllerItem newItem = new TextControllerItem(model,
                        getContext(), this);
                controller.setItem(newItem);

                newItem.matrix = new Matrix(itemText.getMatrix());
                newItem.matrixBorder = new Matrix(itemText.getMatrixBorder());
                newItem.updateImageRect();
                translateAnimation(newItem);

                addController(controller);

                // moveControllerToCenter(controller);
                invalidate();

            }
        }

    }

    public void editTextSticker(TextModel model) {
        Controller conteroller = getCurentController();
        if (conteroller != null) {
            if (conteroller.getItem() instanceof TextControllerItem) {
                TextControllerItem textControllerItem = (TextControllerItem) conteroller
                        .getItem();
                textControllerItem.isVisible = true;
                textControllerItem.setTextModel(model);
            }
        }
        lastTouchUpdate();
        invalidate();

    }

    private Controller getContollerByPoint(PointF point) {

        float[] pointF = new float[] { point.x, point.y };
        Matrix inverse = new Matrix(getImageMatrix());
        Matrix matrixInvert = new Matrix();
        inverse.invert(matrixInvert);
        matrixInvert.mapPoints(pointF);
        point.set(pointF[0], pointF[1]);

        for (int i = controllers.size() - 1; i >= 0; i--) {
            Controller rotateSvgController = controllers.get(i);
            if (rotateSvgController.getItem().isPointIn(point,
                    currentItem != i || lastTouch <= 0)) {
                return rotateSvgController;
            }
        }
        return null;
    }

    private Controller getContollerByPointAndDots(PointF pointIn) {

        PointF pointInitial = new PointF(pointIn.x, pointIn.y);

        float[] pointF = new float[] { pointIn.x, pointIn.y };
        Matrix inverse = new Matrix(getImageMatrix());
        Matrix matrixInvert = new Matrix();
        inverse.invert(matrixInvert);
        matrixInvert.mapPoints(pointF);
        PointF point = new PointF(pointF[0], pointF[1]);

        for (int i = controllers.size() - 1; i >= 0; i--) {
            Controller rotateSvgController = controllers.get(i);
            if (rotateSvgController.getItem().isPointIn(point,
                    currentItem != i || lastTouch <= 0)) {
                return rotateSvgController;
            }

            if (rotateSvgController != null) {
                RectF rect = new RectF();
                rect.set(rotateSvgController.getItem().getRotateDrawable()
                        .getBounds());
                increaseRect(padding, rect);
                if (rect.contains(pointInitial.x, pointInitial.y)) {
                    return rotateSvgController;
                }
            }

            if (rotateSvgController != null) {
                for (Drawable drawable : rotateSvgController.getItem()
                        .getAllDrawables()) {
                    if (drawable != null) {
                        RectF rect = new RectF();
                        rect.set(drawable.getBounds());
                        increaseRect(padding, rect);
                        if (rect.contains(pointInitial.x, pointInitial.y)) {
                            return rotateSvgController;
                        }
                    }
                }

            }

        }
        return null;
    }

    public List<Controller> getControllers() {
        return controllers;
    }

    private Controller getCurentController() {
        if (currentItem >= 0 && currentItem < controllers.size()) {
            return controllers.get(currentItem);
        } else {
            currentItem = 0;
            if (controllers.size() > 0) {
                return controllers.get(0);
            }
        }
        return null;
    }

    public boolean getGridVisible() {
        return gridVisible;
    }

    private void increaseRect(float padding, RectF rect) {
        rect.top -= padding;
        rect.left -= padding;
        rect.right += padding;
        rect.bottom += padding;
    }

    private void init() {
        gestureDetector.setIsLongpressEnabled(false);

        handler = new Handler();
        try {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.argb(255 * 70 / 100, 255, 255, 255));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
    }

    private void lastTouchOff() {
        lastTouch = Integer.MIN_VALUE;
    }

    private void lastTouchUpdate() {
        handler.removeCallbacks(runnableLastUpdate);
        handler.postDelayed(runnableLastUpdate, BORDER_DELAY);
        lastTouch = Long.MAX_VALUE;// System.currentTimeMillis();

    }

    protected void moveControllerToCenter(Controller controller) {
        try {
            ControllerItem item = controller.getItem();

            PointF centerView = item.getCenter();
            // PointF center = new PointF(getWidth() / 2, getHeight() / 2);
            float[] center = new float[] { getWidth() / 2, getHeight() / 2 };
            Matrix matrix = new Matrix(getImageMatrix());
            Matrix matrixInvert = new Matrix();
            matrix.invert(matrixInvert);
            matrixInvert.mapPoints(center);
            item.translate(center[0] - centerView.x, center[1] - centerView.y);

            if (getDrawable() instanceof BitmapDrawable) {

                RectF rect = new RectF(item.getImageRect());

                Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
                float scaleX = bitmap.getWidth() / 2 / rect.width();
                float scaleY = bitmap.getHeight() / 2 / rect.height();
                float scale = Math.min(scaleX, scaleY);

                item.scaleCenter(scale);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void moveCurrentControllerToTop() {
        // temporary off
        if (true) {
            return;
        }

        Controller controller = getCurentController();
        if (controller != null) {
            controllers.remove(controller);
            controllers.add(controller);
            currentItem = controllers.size() - 1;
        }

    }

    public void onColorClick() {
        final Controller controller = getCurentController();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
if (getDrawable()==null)return;
            RectF rect = new RectF(getDrawable().getBounds());

            getImageMatrix().mapRect(rect);

            if (gridVisible) {
                paint.setStrokeWidth(gridStrokeWidthDark);
                paint.setColor(gridColorDark);
                drawGrid(canvas, rect);

                paint.setStrokeWidth(gridStrokeWidthLight);
                paint.setColor(gridColorLight);
                drawGrid(canvas, rect);
            }

            canvas.save();
            canvas.clipRect(rect);
            canvas.concat(getImageMatrix());

            for (int i = 0; i < controllers.size(); i++) {
                Controller rotateSvgController = controllers.get(i);
                rotateSvgController.getItem().draw(canvas);
            }
            canvas.restore();

            Controller controller = getCurentController();

            if (controller != null && lastTouch > 0) {
                int alpha = 255;
                controller.getItem()
                        .drawBorder(canvas, alpha, getImageMatrix());
            }

            // long touchDelay = System.currentTimeMillis() - lastTouch;
            // if (controller != null && touchDelay < BORDER_DELAY +
            // ANIMATION_DELAY) {
            //
            // int alpha = 255;
            // if (touchDelay > BORDER_DELAY) {
            // alpha = 255 - (int) (255 * (touchDelay - BORDER_DELAY) /
            // ANIMATION_DELAY);
            // }
            // controller.getItem().drawBorder(canvas, alpha, getImageMatrix());
            // }
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    protected void onFlipHorizontalClick() {
        Controller conteroller = getCurentController();
        if (conteroller != null) {
            final ControllerItem item = conteroller.getItem();
            item.flipHofizontal();
        }
    }

    protected void onFlipVerticalClick() {
        Controller conteroller = getCurentController();
        if (conteroller != null) {
            final ControllerItem item = conteroller.getItem();
            item.flipVertical();
        }
    }

    public void onResume() {
        Controller controller = getCurentController();
        if (controller != null) {
            if (controller.getItem() instanceof TextControllerItem) {
                TextControllerItem textControllerItem = (TextControllerItem) controller
                        .getItem();
                textControllerItem.isVisible = true;
            }
        }

        invalidate();
    }

    public void onTextEditClick() {
        Controller controller = getCurentController();
        if (controller != null
                && controller.getItem() instanceof TextControllerItem) {
            TextControllerItem item = (TextControllerItem) controller.getItem();
            item.isVisible = false;
            invalidate();
            Toast.makeText(getContext(),"not implemented yeat",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // if (System.currentTimeMillis() - lastTouch > BORDER_DELAY
        // + ANIMATION_DELAY) {
        // RotateSvgController controller = getContollerByPoint(new PointF(
        // event.getX(), event.getY()));
        // setControllerCurrent(controller);
        // }

        // lastTouchUpdate();

        gestureDetector.onTouchEvent(event);
        if (isRotateScroll) {
            return true;
        }

        Controller controller = getCurentController();
        if (controller != null) {
            if (lastTouch <= 0) {
                return true;
            }
            controller.onTouchEvent(event);
        }
        return true;
    }

    public void removeSelectedSvg() {
        if (lastTouch <= 0) {
            return;
        }

        lastTouchUpdate();
        if (currentItem >= 0 && currentItem < controllers.size()) {
            controllers.remove(currentItem);
            currentItem--;
            if (currentItem < 0) {
                currentItem = 0;
            }
            moveCurrentControllerToTop();
            invalidate();
        }

    }

    private void setControllerCurrent(RotateController controller) {
        if (controller != null) {
            currentItem = controllers.indexOf(controller);
            if (currentItem < 0) {
                currentItem = 0;
            }
            moveCurrentControllerToTop();
            ImageViewRotate.this.invalidate();
        }
    }

    public void setControllers(List<Controller> controllers) {
        this.controllers.addAll(controllers);
    }

    public void setGridVisible(boolean gridVisible) {
        this.gridVisible = gridVisible;
        invalidate();
    }

    private void translateAnimation(ControllerItem item) {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            Rect bounds = drawable.getBounds();
            int width = bounds.width();
            int height = bounds.height();
            int distance = Math.min(width, height) / 8;
            item.translateAnimation(distance);
        }
    }
}
