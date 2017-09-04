package com.nawbar.rulernotepad.photo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Size;

import com.nawbar.rulernotepad.editor.Arrow;

/**
 * Created by Bartosz Nawrot on 04.09.2017.
 */

public class ArrowDrawer {
    private  Paint linePaint = null;

    private float drawLeft, drawTop, drawHeight, drawWidth;

    public ArrowDrawer() {
        linePaint = new Paint();
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(2);
        linePaint.setTextAlign(Paint.Align.CENTER);

        drawLeft = 0.f;
        drawTop = 0.f;
        drawHeight = 0.f;
        drawWidth = 0.f;
    }

    public void setShift(float drawLeft, float drawTop) {
        this.drawLeft = drawLeft;
        this.drawTop = drawTop;
    }

    public void setSize(float drawHeight, float drawWidth) {
        this.drawHeight = drawHeight;
        this.drawWidth = drawWidth;
    }

    public void draw(Canvas canvas, Arrow arrow, boolean withMeasure) {
        float s[] = new float[2];
        ratiosToCoordinates(arrow.getStartX(), arrow.getStartY(), s);
        float e[] = new float[2];
        ratiosToCoordinates(arrow.getEndX(), arrow.getEndY(), e);
        draw(canvas, s, e, withMeasure, arrow.getValue());
    }

    public void draw(Canvas canvas, @Size(2) float[] s, @Size(2) float[] e, boolean withMeasure, int value) {
        draw(canvas, s[0], s[1], e[0], e[1], withMeasure, value);
    }

    public void draw(Canvas canvas, float sX, float sY, float eX, float eY, boolean withMeasure, int value) {
        canvas.drawLine(sX, sY, eX, eY, linePaint);
        fillArrow(canvas, sX, sY, eX, eY);
        fillArrow(canvas, eX, eY, sX, sY);
        if (withMeasure) {
            drawMeasurement(canvas, sX, sY, eX, eY, value);
        }
    }

    private void drawMeasurement(Canvas canvas, float sX, float sY, float eX, float eY, int value) {
        linePaint.setTextSize(25);
        Path path = new Path();
        path.moveTo(sX, sY);
        path.lineTo(eX, eY);
        canvas.drawTextOnPath(String.valueOf(value) + "mm", path, 0, -5, linePaint);
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

    private void ratiosToCoordinates(float rX, float rY, @Size(2) float[] coord) {
        coord[0] = drawWidth * rX + drawLeft;
        coord[1] = drawHeight * rY + drawTop;
    }
}
