package com.nawbar.rulernotepad;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.provider.Settings;
import android.support.annotation.Size;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.nawbar.rulernotepad.editor.Arrow;
import com.nawbar.rulernotepad.editor.Photo;
import com.nawbar.rulernotepad.fragments.PhotoFragment;

import java.util.List;

/**
 * Created by Bartosz Nawrot on 2017-06-15.
 */

public class PhotoNotepadView extends android.support.v7.widget.AppCompatImageView
        implements View.OnTouchListener {

    private static final String TAG  = PhotoNotepadView.class.getSimpleName();
    private static long MIN_REDRAW_TIMEOUT = 50; // [ms], 20Hz

    PhotoFragment.PhotoFragmentCommandsListener listener;

    private Photo photo;
    private List<Arrow> arrows;
    private Arrow currentDrawing;

    private Bitmap bitmap;
    private float drawLeft, drawTop, drawHeight, drawWidth;

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

    public void initialize(Photo photo, PhotoFragment.PhotoFragmentCommandsListener listener) {
        this.listener = listener;
        this.photo = photo;
        this.arrows = listener.getArrows(photo);
        this.bitmap = photo.getFull();
        setImageBitmap(bitmap);
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
        if (arrows.size() > 0) {
            listener.onArrowRemove(arrows.get(arrows.size() - 1));
            arrows.remove(arrows.size() - 1);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e(TAG, "onDraw");
        super.onDraw(canvas);

        // compute drawing coefficients
        float bitmapRatio  = ((float)bitmap.getWidth())/bitmap.getHeight();
        float imageViewRatio  = ((float)getWidth())/getHeight();
        if(bitmapRatio > imageViewRatio) {
            drawLeft = 0;
            drawHeight = (imageViewRatio/bitmapRatio) * getHeight();
            drawWidth = getWidth();
            drawTop = (getHeight() - drawHeight)/2;
        } else {
            drawTop = 0;
            drawHeight = getHeight();
            drawWidth = (bitmapRatio/imageViewRatio) * getWidth();
            drawLeft = (getWidth() - drawWidth)/2;
        }

        lastRedraw = System.currentTimeMillis();
        for (Arrow a : arrows) {
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
        linePaint.setTextSize(25);
        Path path = new Path();

        float s[] = new float[2];
        ratiosToCoordinates(arrow.getStartX(), arrow.getStartY(), s);
        float e[] = new float[2];
        ratiosToCoordinates(arrow.getEndX(), arrow.getEndY(), e);

        path.moveTo(s[0], s[1]);
        path.lineTo(e[0], e[1]);
        canvas.drawTextOnPath(String.valueOf(arrow.getValue()) + "mm", path, 0, -5, linePaint);
    }

    private void drawArrow(Arrow arrow, Canvas canvas, boolean set) {
        float s[] = new float[2];
        ratiosToCoordinates(arrow.getStartX(), arrow.getStartY(), s);
        float e[] = new float[2];
        ratiosToCoordinates(arrow.getEndX(), arrow.getEndY(), e);

        canvas.drawLine(s[0], s[1], e[0], e[1], linePaint);
        fillArrow(canvas, s[0], s[1], e[0], e[1]);
        fillArrow(canvas, e[0], e[1], s[0], s[1]);

        if (set) {
            drawMeasurement(canvas, arrow);
        }
    }

    private void ratiosToCoordinates(float rX, float rY, @Size(2) float[] coord) {
        coord[0] = drawWidth * rX + drawLeft;
        coord[1] = drawHeight * rY + drawTop;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float rX = (x - drawLeft) / drawWidth;
        float rY = (y - drawTop) / drawHeight;
        Log.e(TAG, "onTouch ratios X: " + rX + " Y: " + rY);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (areValidRatioCoordinates(rX, rY)) {
                    currentDrawing = new Arrow(photo, rX, rY, rX, rY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (areValidRatioCoordinates(rX, rY)) {
                    if (currentDrawing == null) {
                        currentDrawing = new Arrow(photo, rX, rY, rX, rY);
                    } else {
                        currentDrawing.setEndX(rX);
                        currentDrawing.setEndY(rY);
                    }
                }
                if (isRedrawTimeout()) invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (currentDrawing != null) {
                    if (areValidRatioCoordinates(rX, rY)) {
                        currentDrawing.setEndX(rX);
                        currentDrawing.setEndY(rY);
                    }
                    addArrow(currentDrawing);
                }
                currentDrawing = null;
                invalidate();
                break;
        }
        return true;
    }

    private boolean areValidRatioCoordinates(float x, float y) {
        return x >= 0.f && x <= 1.f && y >= 0.f && y <= 1.f;
    }

    private void addArrow(final Arrow arrow) {
        Log.e(TAG, "addArrow");
        if (true) {
            Log.e(TAG, "addArrow accepted");
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final EditText measurementInput = new EditText(getContext());
            measurementInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            measurementInput.setHint("Jaką długość w milimetrach ma ten wymiar?");
            builder.setTitle("Zmierz dlugość")
                    .setCancelable(false)
                    .setView(measurementInput)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.e(TAG, "measurement added");
                            Arrow a = new Arrow(arrow);
                            if (!measurementInput.getText().toString().isEmpty()) {
                                try {
                                    a.setValue(Integer.valueOf(measurementInput.getText().toString()));
                                } catch (NumberFormatException e) {
                                    Log.e(TAG, "not a number");
                                }
                                arrows.add(a);
                                listener.onArrowAdd(a);
                            } else {
                                Log.e(TAG, "empty text");
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
