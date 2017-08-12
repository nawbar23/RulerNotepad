package com.nawbar.rulernotepad.editor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.ByteArrayOutputStream;

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

    @DatabaseField(dataType = DataType.BYTE_ARRAY, canBeNull = false)
    private byte[] mini;

    @DatabaseField(dataType = DataType.BYTE_ARRAY, canBeNull = false)
    private byte[] full;

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
        return BitmapFactory.decodeByteArray(mini, 0, mini.length);
    }

    public Bitmap getFull() {
        return BitmapFactory.decodeByteArray(full, 0, full.length);
    }

    public void setPhotoBitmap(Bitmap photoBitmap) {
        ByteArrayOutputStream streamFull = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, streamFull);
        full = streamFull.toByteArray();

        // Get the dimensions of the View
        int targetW = 100;
        int targetH = 100;
        float photoW = photoBitmap.getWidth();
        float photoH = photoBitmap.getHeight();
        float scaleFactor = Math.max(photoW/targetW, photoH/targetH);
        int dstW = (int)(photoW/scaleFactor);
        int dstH = (int)(photoH/scaleFactor);

        Bitmap miniBitmap = Bitmap.createScaledBitmap(photoBitmap, dstW, dstH, true);
        ByteArrayOutputStream streamMini = new ByteArrayOutputStream();
        miniBitmap.compress(Bitmap.CompressFormat.PNG, 100, streamMini);
        mini = streamMini.toByteArray();
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

