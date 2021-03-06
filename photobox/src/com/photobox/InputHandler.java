package com.photobox;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;

public class InputHandler {

    public ScaleGestureDetector scaleDetector;
    public WorldMapping mapping;
    public Float previousFingerAngle = null;
    public PhotoCollection collection;

    public InputHandler(Context context, WorldMapping mapping, PhotoCollection collection) {
        SimpleOnScaleGestureListener scaleListener = new ScaleListener();
        scaleDetector = new ScaleGestureDetector(context, scaleListener);
        this.mapping = mapping;
        this.collection = collection;
    }

    public void onTouchEvent(MotionEvent event) {
        Log.d("panic", "someone is touching me... help!!");
        scaleDetector.onTouchEvent(event);
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            Log.d("iiiih!", "don't push it...");
            collection.fingerDown(mapping.toWorld(new Point(event.getX(), event.getY())));
            break;
        case MotionEvent.ACTION_UP:
            Log.d("phiew!", "back again!");
            previousFingerAngle = null;
            break;
        case MotionEvent.ACTION_MOVE:
            movePhoto(event);
            break;
        default:
            break;
        }
    }
    
    private void movePhoto(MotionEvent event) {
        if (collection.getActive() == null) {
            return;
        }
        if (event.getPointerCount() > 1) {
            Point p1 = mapping.toWorld(new Point(event.getX(0), event.getY(0)));
            Point p2 = mapping.toWorld(new Point(event.getX(1), event.getY(1)));
            Point pDiff = p2.minus(p1);
            double currentFingerAngle = Math.toDegrees(Math.atan2(pDiff.y, pDiff.x));
            if (previousFingerAngle != null) {
                double diffAngle = currentFingerAngle - previousFingerAngle;
                collection.getActive().angle += (float) diffAngle;
            }
            previousFingerAngle = new Float(currentFingerAngle);
        } else {
            previousFingerAngle = null;
        }
        collection.getActive().setCenterPoint(mapping.toWorld(new Point(event.getX(), event.getY())));
    }

    private class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleDetector) {
            mapping.scaleFactor *= scaleDetector.getScaleFactor();
            return true;
        }
    }

}
