package com.nawbar.rulernotepad.editor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by nawba on 22.06.2017.
 */

public class Measurement {
    private String name;
    private String date;
    private List<Photo> photos;

    public Measurement(String name) {
        this.name = name;
        this.date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(new Date());
        photos = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public Photo getPhoto(String name) {
        for (Photo p : photos) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
    }
}
