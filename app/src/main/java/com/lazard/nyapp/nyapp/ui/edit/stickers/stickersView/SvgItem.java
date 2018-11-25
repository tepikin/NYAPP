package com.lazard.nyapp.nyapp.ui.edit.stickers.stickersView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.caverock.androidsvg.ColorUpdaterInstanceFill;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.lazard.nyapp.nyapp.R;
import com.lazard.nyapp.nyapp.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SvgItem extends ControllerItem {

    private Picture picture;

    private SVG svg;

    private String name;

    private Drawable drawableColor;

    private Drawable drawableFlipVertical;

    private Drawable drawableFlipHorizontal;

    public SvgItem(String name, Context context, ImageViewRotate imageViewRotate) {
        super(context, imageViewRotate);
        setSvg(name, context);

        drawableColor = context.getResources().getDrawable(
                R.drawable.edit_tool_masks_palet);

        drawableFlipVertical = context.getResources().getDrawable(
                R.drawable.edit_tool_masks_flip_vertical);
        drawableFlipHorizontal = context.getResources().getDrawable(
                R.drawable.edit_tool_masks_flip_horizontal);
    }

    @Override
    public void draw(Canvas canvas) {
        try {
            canvas.save();
            canvas.concat(matrix);
            picture.draw(canvas);
            canvas.restore();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void drawButtons(Canvas canvas, float[] points, int alpha) {
        if (getColor() != -1) {
            drawDrawable(drawableColor, points[0], points[1], alpha, canvas);
        }
        drawDrawable(drawableFlipHorizontal, points[6], points[7], alpha,
                canvas);
        drawDrawable(drawableFlipVertical, points[8], points[9], alpha, canvas);
    }

    @Override
    public List<Drawable> getAllDrawables() {
        List<Drawable> list = new ArrayList<Drawable>();
        list.add(getRotateDrawable());
        list.add(drawableColor);
        list.add(drawableFlipHorizontal);
        list.add(drawableFlipVertical);
        return list;
    }

    public String getName() {
        return name;
    }

    public Picture getPicture() {
        return picture;
    }

    @Override
    public boolean onDoubleTap(PointF point) {
        return true;
    }

    @Override
    public boolean onSingleTapUp(PointF point) {
        float padding = Utils.INSTANCE.dpToPx(8, imageView.getContext());
        if (getColor() != -1) {
            if (inDrawable(drawableColor, point)) {
                imageView.onColorClick();
                return true;
            }
        }

        if (inDrawable(drawableFlipHorizontal, point)) {
            flipHofizontal();
            return true;
        }

        if (inDrawable(drawableFlipVertical, point)) {
            flipVertical();
            return true;
        }

        return false;
    }

    @Override
    public void setColor(int color) {
        this.color = color;
        if (color != -1) {
            updateStyle(new ColorUpdaterInstanceFill(color));
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSvg(String name, Context context) {
        try {
            this.name = name;

            // initSvg
            svg = SVG.getFromAsset(context.getAssets(), name);
            svg.setDocumentWidth("100%");
            svg.setDocumentHeight("100%");

            for (Iterator<String> it = svg.getViewList().iterator(); it
                    .hasNext();) {
                String f = it.next();
                Log.e("TAG", f);
            }

            // initImageRect
            RectF documentViewBox = svg.getDocumentViewBox();
            float documentWidth = svg.getDocumentWidth();
            float documentHeight = svg.getDocumentHeight();
            if (documentViewBox != null) {
                imageRect.set(documentViewBox);
            } else {
                imageRect.set(0, 0, documentWidth, documentHeight);
            }

            // initPicture
            picture = svg.renderToPicture((int) imageRect.width(),
                    (int) imageRect.height());

            // initMatrix
            matrix = new Matrix();
            matrixBorder = new Matrix();
        } catch (SVGParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void updateStyle(com.caverock.androidsvg.StyleUpdater styleUpdater) {
        if (svg == null || styleUpdater == null || imageRect == null) {
            return;
        }
        svg.updateStyle(styleUpdater);
        picture = svg.renderToPicture((int) imageRect.width(),
                (int) imageRect.height());
    }
}
