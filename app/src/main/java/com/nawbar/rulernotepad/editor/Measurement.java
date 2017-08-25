package com.nawbar.rulernotepad.editor;

import android.support.annotation.NonNull;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Bartosz Nawrot on 2017-06-21.
 */

@DatabaseTable(tableName = "measurements")
public class Measurement {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false)
    private Date date;

    @DatabaseField(canBeNull = false)
    private long form;

    @DatabaseField
    private double latitude;

    @DatabaseField
    private double longitude;

    @ForeignCollectionField
    private ForeignCollection<Photo> photos;

    public Measurement() {
        // ORMLite needs a no-arg constructor
    }

    public Measurement(String name) {
        this.name = name;
        this.date = new Date();
        this.form = Long.MAX_VALUE;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getForm() {
        return form;
    }

    public void setForm(long form) {
        this.form = form;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public ForeignCollection<Photo> getPhotos() {
        return photos;
    }

    public String getDateString() {
        return dateFormat.format(date);
    }

    public boolean isFormFilled() {
        return form != Long.MAX_VALUE;
    }

    void setFormValue(int id, boolean state) {
        if (id <= Long.SIZE) {
            if (state) {
                form |= 1 << id;
            } else {
                form &= ~(1 << id);
            }
        }
    }

    public Boolean getFormValue(int i) {
        return i <= Long.SIZE && (form & (1 << i)) != 0;
    }
}
