package com.nawbar.rulernotepad.editor;

import android.graphics.Bitmap;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Bartosz Nawrot on 2017-06-21.
 */

@DatabaseTable(tableName = "photos")
public class Photo {

    public static final String MEASUREMENT_ID_FIELD_NAME = "measurement_id";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = MEASUREMENT_ID_FIELD_NAME)
    private Measurement measurement;

    @DatabaseField(canBeNull = false)
    private String name;

    //@DatabaseField(canBeNull = false)
    private Bitmap mini;

    //@DatabaseField(canBeNull = false)
    private Bitmap full;

    @DatabaseField
    private String comment;

    @ForeignCollectionField
    private ForeignCollection<Arrow> arrows;

    public Photo() {
        // ORMLite needs a no-arg constructor
    }

    public Photo(Measurement measurement, String name) {
        this.measurement = measurement;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public Measurement getMeasurement() {
        return measurement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ForeignCollection<Arrow> getArrows() {
        return arrows;
    }
}

