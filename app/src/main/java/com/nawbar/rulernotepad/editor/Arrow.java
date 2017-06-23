package com.nawbar.rulernotepad.editor;

import android.graphics.PointF;

/**
 * Created by Bartosz Nawrot on 2017-06-21.
 */

public class Arrow {

    private PointF start;
    private PointF end;

    public Arrow() {
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
}
