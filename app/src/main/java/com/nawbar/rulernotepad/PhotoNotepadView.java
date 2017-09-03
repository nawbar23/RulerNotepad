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

    private Bitmap bitmap;

    private Photo photo;
    private List<Arrow> arrows;
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
        path.moveTo(arrow.getStartX(), arrow.getStartY());
        path.lineTo(arrow.getEndX(), arrow.getEndY());
        canvas.drawTextOnPath(String.valueOf(arrow.getValue()) + "mm", path, 0, -5, linePaint);
    }

    private void drawArrow(Arrow arrow, Canvas canvas, boolean set) {
        canvas.drawLine(arrow.getStartX(), arrow.getStartY(),
                arrow.getEndX(), arrow.getEndY(), linePaint);
        fillArrow(canvas, arrow.getStartX(), arrow.getStartY(),
                arrow.getEndX(), arrow.getEndY());
        fillArrow(canvas, arrow.getEndX(), arrow.getEndY(),
                arrow.getStartX(), arrow.getStartY());
        if (set) {
            drawMeasurement(canvas, arrow);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //Log.e(TAG, event.toString());
        float x = event.getX();
        float y = event.getY();
        // These holds the ratios for the ImageView and the bitmap
        double bitmapRatio  = ((double)bitmap.getWidth())/bitmap.getHeight();
        double imageViewRatio  = ((double)getWidth())/getHeight();
        double drawLeft, drawTop, drawHeight, drawWidth;
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

        double Xr = (x - drawLeft) / drawWidth;
        double Yr = (y - drawTop) / drawHeight;
        StringBuilder sb = new StringBuilder();
        sb.append("X: ").append(x).append(" Y: ").append(y).append('\n');
        sb.append("X: ").append(Xr).append(" Y: ").append(Yr).append('\n');
        Log.e(TAG, sb.toString());

//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                currentDrawing = new Arrow(photo, x, y, x, y);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                currentDrawing.setEndX(x);
//                currentDrawing.setEndY(y);
//                if (isRedrawTimeout()) invalidate();
//                break;
//            case MotionEvent.ACTION_UP:
//                currentDrawing.setEndX(x);
//                currentDrawing.setEndY(y);
//                addArrow(currentDrawing);
//                currentDrawing = null;
//                invalidate();
//                break;
//        }
        return true;
    }

    private void addArrow(final Arrow arrow) {
        if (true) {
            Log.e(TAG, "fab_revert");
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
