package com.photobox;

import android.graphics.Matrix;

public class Rotator {

    public static Point rotatePoint(Point p, float angle) {
        Matrix m = new Matrix();
        m.setRotate(-angle, 0, 0);

        float[] dst = new float[] { 0, 0 };
        float[] src = new float[] { p.x, p.y };
        m.mapPoints(dst, src);
        float newX = dst[0];
        float newY = dst[1];

        return new Point(newX, newY);
    }

}
