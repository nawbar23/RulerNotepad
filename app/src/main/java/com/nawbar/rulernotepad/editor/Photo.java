package com.nawbar.rulernotepad.editor;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bartosz Nawrot on 2017-06-21.
 */

public class Photo {
    private Bitmap mini;
    private Bitmap full;
    private List<Arrow> arrows;

    public Photo() {
        arrows = new ArrayList<>();
    }

    public Bitmap getMini() {
        return mini;
    }

    public void setMini(Bitmap mini) {
        this.mini = mini;
    }

    public Bitmap getFull() {
        return full;
    }

    public void setFull(Bitmap full) {
        this.full = full;
    }

    public List<Arrow> getArrows() {
        return arrows;
    }

    public void setArrows(List<Arrow> arrows) {
        this.arrows = arrows;
    }
}
