package com.nawbar.rulernotepad;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

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
        linePaint.setTextAlign(Paint.Align.CENTER);

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
            drawArrow(a, canvas, true);
        }
        if (currentDrawing != null) {
            drawArrow(currentDrawing, canvas, false);
        }
    }

    private void fillArrow(Canvas canvas, float x0, float y0, float x1, float y1) {
        linePaint.setStyle(Paint.Style.FILL);

        float deltaX = x1 - x0;
        float deltaY = y1 - y0;
        float frac = (float) 0.03;

        float point_x_1 = x0 + ((1 - frac) * deltaX + frac * deltaY);
        float point_y_1 = y0 + ((1 - frac) * deltaY - frac * deltaX);
        float point_x_3 = x0 + ((1 - frac) * deltaX - frac * deltaY);
        float point_y_3 = y0 + ((1 - frac) * deltaY + frac * deltaX);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(point_x_1, point_y_1);
        path.lineTo(x1, y1);
        path.lineTo(point_x_3, point_y_3);
        path.lineTo(point_x_1, point_y_1);
        path.lineTo(point_x_1, point_y_1);
        path.close();

        canvas.drawPath(path, linePaint);
    }

    private void drawMeasurement(Canvas canvas, Arrow arrow) {
        linePaint.setTextSize(arrow.getLength()*0.08f);
        Path path = new Path();
        path.moveTo(arrow.getStart().x, arrow.getStart().y);
        path.lineTo(arrow.getEnd().x, arrow.getEnd().y);
        canvas.drawTextOnPath(String.valueOf(arrow.getMeasurement()) + "cm", path, 0, -arrow.getLength()*0.01f, linePaint);
    }

    private void drawArrow(Arrow arrow, Canvas canvas, boolean set) {
        canvas.drawLine(arrow.getStart().x, arrow.getStart().y,
                arrow.getEnd().x, arrow.getEnd().y, linePaint);
        fillArrow(canvas, arrow.getStart().x, arrow.getStart().y,
                arrow.getEnd().x, arrow.getEnd().y);
        fillArrow(canvas, arrow.getEnd().x, arrow.getEnd().y,
                arrow.getStart().x, arrow.getStart().y);
        if (set) {
            drawMeasurement(canvas, arrow);
        }
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
                addArrow(currentDrawing);
                currentDrawing = null;
                invalidate();
                break;
        }
        return true;
    }

    private void addArrow(final Arrow arrow) {
        if (arrow.isValid()) {
            Log.e(TAG, "fab_revert");
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final EditText measurementInput = new EditText(getContext());
            measurementInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            measurementInput.setHint("Jak długość w centymetrach ma ten wymiar?");
            builder.setTitle("Zmierz dlugość")
                    .setCancelable(false)
                    .setView(measurementInput)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.e(TAG, "measurement added");
                            Arrow a = new Arrow(arrow);
                            if (!measurementInput.getText().toString().isEmpty()) {
                                a.setMeasurement(Integer.valueOf(measurementInput.getText().toString()));
                                photo.addArrow(a);
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Zmierz dlugość")
                    .setCancelable(true)
                    .setMessage("Ten wymiar to chyba jakaś nieuwaga...")
                    .setNegativeButton(android.R.string.cancel, null)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }
    }

    boolean isRedrawTimeout() {
        Log.e(TAG, "isRedrawTimeout: " + (System.currentTimeMillis() - lastRedraw));
        return (System.currentTimeMillis() - lastRedraw) > MIN_REDRAW_TIMEOUT;
    }
}
