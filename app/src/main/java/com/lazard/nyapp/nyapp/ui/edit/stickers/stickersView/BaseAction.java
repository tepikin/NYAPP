package com.lazard.nyapp.nyapp.ui.edit.stickers.stickersView;

import android.graphics.Bitmap;
import com.lazard.nyapp.nyapp.util.Utils;


import java.io.IOException;

/**
 * Created by Egor on 21.09.2015.
 */
public abstract class BaseAction implements Action {



    protected abstract Bitmap applyWithOOM(Bitmap bitmap) throws OutOfMemoryError, IOException;

    public Bitmap apply(Bitmap bitmap) throws IOException {
        if (bitmap == null || bitmap.isRecycled()) {
            throw new OutOfMemoryError("action");
        }
        try {
            Bitmap mutableBitmap = Utils.INSTANCE.convertToMutable(bitmap);
            if (mutableBitmap == null || mutableBitmap.isRecycled())
                throw new OutOfMemoryError("Can't copy bitmap.");
            Bitmap result = applyWithOOM(mutableBitmap);
            recycleIfNew(bitmap, result);
            recycleIfNew(mutableBitmap, result);
            return result;
        } catch (OutOfMemoryError e) {
            clearMem();
            if (bitmap == null || bitmap.isRecycled()) {
                throw new OutOfMemoryError("action");
            }
            int initialWidth = bitmap.getWidth();
            int initialHeight = bitmap.getHeight();
            for (float scale = 0.9f; scale >= 0.1; scale -= 0.1) {
                try {
                    if (bitmap == null || bitmap.isRecycled()) {
                        throw new OutOfMemoryError("action");
                    }
                    int width = (int) (scale * initialWidth);
                    int height = (int) (scale * initialHeight);
                    Bitmap temp = Bitmap.createScaledBitmap(bitmap, width, height, true);
                    if (temp == null || temp.isRecycled())
                        throw new OutOfMemoryError("Can't copy bitmap.");
                    recycleIfNew(bitmap, temp);
                    bitmap = temp;

                    Bitmap mutableBitmap = Utils.INSTANCE.convertToMutable(bitmap);
                    if (mutableBitmap == null || mutableBitmap.isRecycled())
                        throw new OutOfMemoryError("Can't copy bitmap.");
                    Bitmap result = applyWithOOM(mutableBitmap);
                    recycleIfNew(bitmap, result);
                    recycleIfNew(mutableBitmap, result);
                    return result;
                } catch (OutOfMemoryError e1) {
                    clearMem();
                }
            }
        } catch (Throwable e) {
            throw e;
        }
        throw new OutOfMemoryError("action");
    }

    private void clearMem() {
        System.gc();
    }




    protected void recycle(Bitmap oldBitmap) {
        if (oldBitmap == null) return;
        if (oldBitmap.isRecycled()) return;
        oldBitmap.recycle();
    }

    protected void recycleIfNew(Bitmap oldBitmap, Bitmap newBitmap) {
        if (oldBitmap == newBitmap) return;
        if (newBitmap == null) return;
        if (newBitmap.isRecycled()) return;
        if (oldBitmap == null) return;
        if (oldBitmap.isRecycled()) return;
        oldBitmap.recycle();
    }

    public int getProgressWeight() {
        return 1;
    }


}
