package com.nawbar.rulernotepad.editor;

import android.graphics.PointF;

/**
 * Created by Bartosz Nawrot on 2017-06-21.
 */

public class Arrow {

    private PointF start;
    private PointF end;
    private int measurement = 100; // in set scale (default cm)

    public Arrow() {
    }

    public Arrow(Arrow a) {
        this.start = a.start;
        this.end = a.end;
        this.measurement = a.measurement;
    }
    public Arrow(PointF start, PointF end) {
        this.start = start;
        this.end = end;
    }

    public PointF getStart() {
        return start;
    }

    public void setStart(PointF start) {
        this.start = start;
    }

    public PointF getEnd() {
        return end;
    }

    public void setEnd(PointF end) {
        this.end = end;
    }

    public int getMeasurement() {
        return measurement;
    }

    public void setMeasurement(int measurement) {
        this.measurement = measurement;
    }

    public float getLength() {
        return (float)(Math.sqrt(Math.pow(end.x - start.x, 2.0) + Math.pow(end.y - start.y, 2)));
    }

    public boolean isValid() {
        return getLength() > 100.0f;
    }
}
