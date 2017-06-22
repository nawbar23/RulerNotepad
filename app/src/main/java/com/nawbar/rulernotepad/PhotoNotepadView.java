package com.nawbar.rulernotepad;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by nawba on 23.06.2017.
 */

public class PhotoNotepadView extends android.support.v7.widget.AppCompatImageView
        implements View.OnTouchListener {

    private static String TAG = PhotoNotepadView.class.getSimpleName();

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

    private void initialize() {
        setOnTouchListener(this);
    }

    float x1 = 0;
    float y1 = 0;
    float x2 = 0;
    float y2 = 0;
    boolean draw = false;

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e(TAG, "onDraw, draw: " + draw);
        super.onDraw(canvas);
        if (draw) {
            Paint p = new Paint();
            p.setColor(Color.RED);
            p.setStrokeWidth(2);
            canvas.drawLine(x1, y1, x2, y2, p);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.e(TAG, event.toString());
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = x;
                y1 = y;
                break;
            case MotionEvent.ACTION_MOVE:
                x2 = x;
                y2 = y;
                draw = true;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                x2 = x;
                y2 = y;
                draw = true;
                invalidate();
                break;
        }
        return true;
    }
}
