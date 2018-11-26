package com.lazard.nyapp.nyapp.ui.edit.stickers.stickersView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import com.lazard.nyapp.nyapp.util.UtilsRect;


/**
 * Created by Egor on 21.09.2015.
 */
public class StickerItemDrawer {

    private transient  Context context;

    public StickerItemDrawer(Context context) {
        this.context = context;
    }

    public StickerItemDrawer() {
    }
    public void setContext(Context context) {
        this.context = context;
    }
    public void draw(Canvas canvas,Bitmap bitmap, StickersAction.StickerItem stickerItem) {

        Matrix matrix = UtilsRect.getMatrixPolyToPoly(UtilsRect.createStartRect10(), stickerItem.getPoints());
        matrix.postScale(bitmap.getWidth(), bitmap.getHeight());

        RectF initRect = stickerItem.getInitRect();
        matrix.preScale(1f / initRect.width(), 1f / initRect.height());
        matrix.preTranslate(-initRect.left,-initRect.top);


        canvas.save();
        canvas.concat(matrix);
        //canvas.drawRect(initRect, new Paint());

        ControllerItem controllerItem=null;
        if (stickerItem.getType() == StickersAction.StickerItem.Type.png) {
            controllerItem= new PngItem(stickerItem.getName(),context,null);
        }if (stickerItem.getType() == StickersAction.StickerItem.Type.svg) {
            SvgItem svg= new SvgItem(stickerItem.getName(),context,null);
            svg.setColor(stickerItem.getColor());
            controllerItem=svg;
        }        if (stickerItem.getType() == StickersAction.StickerItem.Type.text) {
            controllerItem= new TextControllerItem(stickerItem.getTextModel(),context,null);
        }
        controllerItem.draw(canvas);

if (            controllerItem instanceof PngItem){
    ((PngItem)controllerItem).recycleDrawable();
}

        canvas.restore();



    }
}
