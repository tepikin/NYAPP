package com.lazard.nyapp.nyapp.util;

import android.graphics.*;

public class UtilsRect {


    public static Matrix getMatrixPolyToPoly(PointF[] pointsFrom, PointF[] pointsTo) {

        float[] srcFrom = pointToArrayFloat(pointsFrom);
        float[] srcTo = pointToArrayFloat(pointsTo);

        Matrix matrix = new Matrix();
        matrix.setPolyToPoly(srcFrom, 0, srcTo, 0, pointsFrom.length);
        return matrix;
    }

    public static RectF getExternalRect(PointF[] points) {

        PointF[] resultPoints = clonePoints(points);

        double top = resultPoints[0].y;
        top = Math.min(top, resultPoints[1].y);
        top = Math.min(top, resultPoints[2].y);
        top = Math.min(top, resultPoints[3].y);

        double bottom = resultPoints[0].y;
        bottom = Math.max(bottom, resultPoints[1].y);
        bottom = Math.max(bottom, resultPoints[2].y);
        bottom = Math.max(bottom, resultPoints[3].y);

        double left = resultPoints[0].x;
        left = Math.min(left, resultPoints[1].x);
        left = Math.min(left, resultPoints[2].x);
        left = Math.min(left, resultPoints[3].x);

        double right = resultPoints[0].x;
        right = Math.max(right, resultPoints[1].x);
        right = Math.max(right, resultPoints[2].x);
        right = Math.max(right, resultPoints[3].x);

        return new RectF((float) left, (float) top, (float) right, (float) bottom);
    }
    public static PointF[] getScaled(PointF[] points,double width, double height) {
        Matrix matrix = new Matrix();
        matrix.postScale((float) width, (float) height);
        PointF[] resultPoints = applayMatrixToPoints(points, matrix);
        return resultPoints;
    }
    public static PointF[] applayMatrixToPoints(PointF[] points, Matrix matrix) {
        if (points == null) return points;
        if (points.length == 0) return new PointF[0];
        if (matrix == null) return new PointF[0];
        PointF[] clonePoints = clonePoints(points);
        if (matrix.isIdentity()) {
            return clonePoints;
        }

        float[] src = pointToArrayFloat(points);
        float[] dst = pointToArrayFloat(points);

        matrix.mapPoints(dst, src);

        PointF[] resultPoints = arrayFloatToPoints(dst);

        return resultPoints;
    }


    public static PointF[] arrayFloatToPoints(float[] dst) {
        PointF[] resultPoints = new PointF[dst.length / 2];
        for (int i = 0; i < resultPoints.length; i++) {
            resultPoints[i] = new PointF(dst[i * 2], dst[i * 2 + 1]);
        }
        return resultPoints;
    }

    public static PointF[] clonePoints(PointF[] points) {
        if (points == null) return points;
        if (points.length == 0) return new PointF[0];
        PointF[] cloned = new PointF[points.length];
        for (int i = 0; i < cloned.length; i++) {
            PointF point = points[i];
            PointF clonedPoint = new PointF(point.x, point.y);
            cloned[i] = clonedPoint;
        }
        return cloned;
    }

    public static PointF[] createStartRect10() {
        PointF[] rectPoints = new PointF[]{new PointF(0, 0), new PointF(1, 0), new PointF(1, 1), new PointF(0, 1)};
        return rectPoints;
    }
    public static Bitmap cropBitmap(PointF[]resultPointsIn01, Bitmap bitmap) {
        if (bitmap==null)return bitmap;
        PointF[] scaled =UtilsRect.getScaled(resultPointsIn01,bitmap.getWidth(),bitmap.getHeight());
        int widthNew =(int) Math.round( UtilsRect.distance(scaled[0], scaled[1]));
        int heightNew =(int)  Math.round(UtilsRect.distance(scaled[0], scaled[3]));
        Bitmap newBitmap =Bitmap.createBitmap(widthNew, heightNew, Bitmap.Config.ARGB_8888);


        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);

        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, UtilsRect.getMatrixForDraw(resultPointsIn01, bitmap.getWidth(), bitmap.getHeight()), paint);

        return newBitmap;
    }
    public static Matrix getMatrixForDraw(PointF[]resultPointsIn01,double width, double height) {
        PointF[] scaled01 = UtilsRect.rectToPoints(new RectF(0, 0, (float) width, (float) height));
        PointF[] scaledLast = UtilsRect.getScaled(resultPointsIn01, width, height);

        scaled01 =  UtilsRect.normalizeRect(scaled01);
        scaledLast =  UtilsRect.normalizeRect(scaledLast);

        Matrix matrix = UtilsRect.getMatrixPolyToPoly(scaled01, scaledLast);
        Matrix matrixInvert = new Matrix();
        matrix.invert(matrixInvert);
        return matrixInvert;
    }
    public static double distance(PointF point1, PointF point2) {
        if (point1 == null) return Double.NaN;
        if (point2 == null) return Double.NaN;
        double distance = Math.sqrt((point1.x - point2.x) * (point1.x - point2.x) + (point1.y - point2.y) * (point1.y - point2.y));
        return distance;
    }

    public static PointF getRectCenter(PointF[] points) {
        if (points == null) return null;
        float x = points[0].x + (points[2].x - points[0].x) * 0.5f;
        float y = points[0].y + (points[2].y - points[0].y) * 0.5f;
        PointF resultPoint = new PointF(x, y);
        return resultPoint;
    }

    public static PointF[] normalizeRect(PointF[] points) {
        if (points == null) return null;
        double[] n1 = Utils.INSTANCE.vectorNormal(new double[]{points[1].x - points[0].x, points[1].y - points[0].y});
        double[] n2 = Utils.INSTANCE.vectorNormal(new double[]{points[2].x - points[1].x, points[2].y - points[1].y});
        double[] n3 = Utils.INSTANCE.vectorNormal(new double[]{points[3].x - points[2].x, points[3].y - points[2].y});

        PointF[] pointsResult = clonePoints(points);

        pointsResult[1].set(pointsResult[0].x + (float) n1[0], pointsResult[0].y + (float) n1[1]);
        pointsResult[2].set(pointsResult[1].x + (float) n2[0], pointsResult[1].y + (float) n2[1]);
        pointsResult[3].set(pointsResult[2].x + (float) n3[0], pointsResult[2].y + (float) n3[1]);

        return pointsResult;
    }

    public static float[] pointToArrayFloat(PointF[] points) {
        float srcFrom[] = new float[points.length * 2];
        for (int i = 0; i < points.length; i++) {
            srcFrom[i * 2] = points[i].x;
            srcFrom[i * 2 + 1] = points[i].y;
        }
        return srcFrom;
    }

    public static PointF[] rectToPoints(RectF rect) {
        return new PointF[]{new PointF(rect.left, rect.top), new PointF(rect.right, rect.top), new PointF(rect.right, rect.bottom), new PointF(rect.left, rect.bottom)};
    }
}
