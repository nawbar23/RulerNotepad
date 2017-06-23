package com.nawbar.rulernotepad;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.nawbar.rulernotepad.editor.Arrow;
import com.nawbar.rulernotepad.editor.Photo;

/**
 * Created by nawba on 23.06.2017.
 */

public class PhotoNotepadView extends android.support.v7.widget.AppCompatImageView
        implements View.OnTouchListener {

    private static String TAG = PhotoNotepadView.class.getSimpleName();
    private static long MIN_REDRAW_TIMEOUT = 50; // [ms], 20Hz

    private Photo photo;

    private Arrow currentDrawing;

    private Paint linePaint;

    private long lastRedraw;

    public PhotoNotepadView(Context context) {
        super(context);
        initialize();
    }

    public PhotoNotepadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public PhotoNotepadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
        setImageBitmap(photo.getFull());
    }

    private void initialize() {
        linePaint = new Paint();
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(2);

        currentDrawing = null;

        setOnTouchListener(this);
    }

    public void onRevert() {
        photo.popBackArrow();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e(TAG, "onDraw");
        super.onDraw(canvas);
        lastRedraw = System.currentTimeMillis();
        for (Arrow a : photo.getArrows()) {
            drawArrow(a, canvas);
        }
        if (currentDrawing != null) {
            drawArrow(currentDrawing, canvas);
        }
    }

    private void drawArrow(Arrow arrow, Canvas canvas) {
        canvas.drawLine(arrow.getStart().x, arrow.getStart().y,
                arrow.getEnd().x, arrow.getEnd().y, linePaint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.e(TAG, event.toString());
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentDrawing = new Arrow(new PointF(x, y), new PointF(x, y));
                break;
            case MotionEvent.ACTION_MOVE:
                currentDrawing.getEnd().x = x;
                currentDrawing.getEnd().y = y;
                if (isRedrawTimeout()) invalidate();
                break;
            case MotionEvent.ACTION_UP:
                currentDrawing.getEnd().x = x;
                currentDrawing.getEnd().y = y;
                photo.addArrow(currentDrawing);
                currentDrawing = null;
                invalidate();
                break;
        }
        return true;
    }

    boolean isRedrawTimeout() {
        Log.e(TAG, "isRedrawTimeout: " + (System.currentTimeMillis() - lastRedraw));
        return (System.currentTimeMillis() - lastRedraw) > MIN_REDRAW_TIMEOUT;
    }
}
