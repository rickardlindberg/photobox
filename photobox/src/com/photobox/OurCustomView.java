package com.photobox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.WindowManager;

public class OurCustomView extends View {

    private ScaleGestureDetector scaleDetector;
    private float scaleFactor = 1.f;

    private PhotoCollection collection;

    private float px = 0;
    private float py = 0;
    private float previousPx = 0;
    private float previousPy = 0;
    private Float previousFingerAngle = null;

    public OurCustomView(Context context) {
        this(context, null, 0);
    }

    public OurCustomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OurCustomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        SimpleOnScaleGestureListener scaleListener = new ScaleListener();
        scaleDetector = new ScaleGestureDetector(context, scaleListener);
        initializePxPy(context);
        collection = new PhotoCollection();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.testimage_x);
        collection.addPhoto(new Photo().withBitmap(bitmap));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("panic", "someone is touching me... help!!");
        scaleDetector.onTouchEvent(event);
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            Log.d("iiiih!", "don't push it...");
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
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        renderBackground(canvas);
        for (Photo photo : collection.getPhotos()) {
            renderPhoto(canvas, photo);
        }
    }

    private void movePhoto(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            double dy = event.getY(1) - event.getY(0);
            double dx = event.getX(1) - event.getX(0);
            double currentFingerAngle = Math.toDegrees(Math.atan2(dy, dx));
            if (previousFingerAngle != null) {
                double diffAngle = currentFingerAngle - previousFingerAngle;
                currentPhoto().angle += (float) diffAngle;
            }
            previousFingerAngle = new Float(currentFingerAngle);
        } else {
            previousFingerAngle = null;
        }
        px = event.getX();
        py = event.getY();
        currentPhoto().centerX = px;
        currentPhoto().centerY = py;
        Log.d("coords", "px=" + px + ", py=" + py);
        invalidate();
    }

    private void renderBackground(Canvas canvas) {
        Paint bgPaint = new Paint();
        bgPaint.setARGB(255, 255, 255, 0);
        canvas.drawPaint(bgPaint);
    }

    private void renderPhoto(Canvas canvas, Photo photo) {
        Bitmap image = BitmapFactory.decodeResource(getResources(),
                R.drawable.testimage_x);

        float w = currentPhoto().width;
        float h = photo.height;

        moveOnlyWhenFingerOnImage(w, h);

        canvas.translate(photo.centerX, photo.centerY);
        canvas.rotate(photo.angle);
        canvas.scale(scaleFactor, scaleFactor);

        Paint paint = new Paint();
        paint.setARGB(255, 255, 255, 255);
        canvas.drawRect(-w / 2, -h / 2, w / 2, h / 2, paint);

        canvas.drawBitmap(image, -w / 2 + photo.BORDER, -h / 2 + photo.BORDER,
                null);

        Paint p = new Paint();
        p.setColor(Color.BLUE);
        canvas.drawCircle(0, 0, 10, p);
    }

    private void moveOnlyWhenFingerOnImage(float w, float h) {
        float scaledWidth = w * scaleFactor;
        float scaledHeight = h * scaleFactor;
        if (previousPx - scaledWidth / 2 > px
                || previousPx + scaledWidth / 2 < px
                || previousPy - scaledHeight / 2 > py
                || previousPy + scaledHeight / 2 < py) {
            px = previousPx;
            py = previousPy;
        }
        previousPx = px;
        previousPy = py;
    }

    private void initializePxPy(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        px = metrics.widthPixels / 2;
        py = metrics.heightPixels / 2;
        previousPx = metrics.widthPixels / 2;
        previousPy = metrics.heightPixels / 2;
    }

    private class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleDetector) {
            scaleFactor *= scaleDetector.getScaleFactor();
            return true;
        }
    }

    private Photo currentPhoto() {
        return collection.getPhotos().get(0);
    }

}
