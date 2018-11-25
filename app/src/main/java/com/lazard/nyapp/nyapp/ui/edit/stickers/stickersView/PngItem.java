package com.lazard.nyapp.nyapp.ui.edit.stickers.stickersView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;


import com.lazard.nyapp.nyapp.util.Utils;

import java.io.IOException;

public class PngItem extends SvgItem {

    private Drawable drawable;

    public PngItem(String name, Context context, ImageViewRotate imageViewRotate) {
        super(name, context, imageViewRotate);

    }

    @Override
    public void draw(Canvas canvas) {
        if (drawable != null) {
            canvas.save();
            canvas.concat(matrix);
            drawable.draw(canvas);
            canvas.restore();
        }
    }

    public void recycleDrawable() {
        if (drawable instanceof BitmapDrawable){
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Utils.INSTANCE.resycle(bitmap);
        }
    }

    @Override
    public void setSvg(String name, Context context) {
        try {
            setName(name);

            // initSvg

            drawable = Drawable.createFromStream(
                    context.getAssets().open(name), null);

            // initImageRect

            imageRect.set(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());

            // initPicture
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());

            // initMatrix
            matrix = new Matrix();
            matrixBorder = new Matrix();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }


    private void updateStyle(com.caverock.androidsvg.StyleUpdater styleUpdater) {
        // do nothing
    }
}
