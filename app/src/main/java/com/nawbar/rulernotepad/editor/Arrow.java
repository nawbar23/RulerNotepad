package com.nawbar.rulernotepad.editor;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Bartosz Nawrot on 2017-06-21.
 */

@DatabaseTable(tableName = "arrows")
public class Arrow {

    public static final String PHOTO_ID_FIELD_NAME = "photo_id";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = PHOTO_ID_FIELD_NAME)
    private Photo photo;

    @DatabaseField(canBeNull = false)
    private float startX;

    @DatabaseField(canBeNull = false)
    private float startY;

    @DatabaseField(canBeNull = false)
    private float endX;

    @DatabaseField(canBeNull = false)
    private float endY;

    @DatabaseField(canBeNull = false)
    private int value; // [mm]

    public Arrow() {
        // ORMLite needs a no-arg constructor
    }

    public Arrow(Photo photo) {
        this.photo = photo;
    }

    public Arrow(Photo photo, float sx, float sy, float ex, float ey) {
        this.startX = sx;
        this.startY = sy;
        this.endX = ex;
        this.endY = ey;
        this.photo = photo;
    }

    public Arrow(Arrow a) {
        this.photo = a.photo;
        this.startX = a.startX;
        this.startY = a.startY;
        this.endX = a.endX;
        this.endY = a.endY;
        this.value = a.value;
    }

    public int getId() {
        return id;
    }

    public Photo getPhoto() {
        return photo;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getEndX() {
        return endX;
    }

    public void setEndX(float endX) {
        this.endX = endX;
    }

    public float getEndY() {
        return endY;
    }

    public void setEndY(float endY) {
        this.endY = endY;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public float getLenght() {
        return (float)Math.hypot(startX - endX, startY - endY);
    }

    public boolean isValid() {
        return getLenght() > 0.07f;
    }
}
