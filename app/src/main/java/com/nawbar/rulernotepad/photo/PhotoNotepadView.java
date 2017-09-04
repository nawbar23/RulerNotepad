package com.nawbar.rulernotepad.photo;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
    private ArrowDrawer drawer;

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
        drawer = new ArrowDrawer();
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
        drawer.setShift(drawLeft, drawTop);
        drawer.setSize(drawHeight, drawWidth);

        lastRedraw = System.currentTimeMillis();
        for (Arrow a : arrows) {
            drawer.draw(canvas, a, true);
        }
        if (currentDrawing != null) {
            drawer.draw(canvas, currentDrawing, false);
        }
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
        if (arrow.isValid()) {
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
