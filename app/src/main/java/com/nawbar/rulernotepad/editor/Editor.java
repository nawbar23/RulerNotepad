package com.nawbar.rulernotepad.editor;

import com.nawbar.rulernotepad.fragments.GalleryFragment;
import com.nawbar.rulernotepad.fragments.MeasurementsFragment;
import com.nawbar.rulernotepad.fragments.PhotoFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Bartosz Nawrot on 2017-06-15.
 */

public class Editor implements
        MeasurementsFragment.MeasurementsCommandsListener,
        GalleryFragment.GalleryFragmentCommandsListener,
        PhotoFragment.PhotoFragmentCommandsListener {

    private static String TAG = Editor.class.getSimpleName();

    private List<Measurement> parsed;

    public Editor() {
        parsed = new ArrayList<>();
    }

    public Measurement getMeasurement(String name) {
        for (Measurement m : parsed) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return null;
    }

    @Override
    public void onMeasurementAdd(String name) {
        parsed.add(new Measurement(name));
    }

    @Override
    public void onMeasurementRemove(String name) {

    }

    @Override
    public void onMeasurementSend(String name) {

    }

    @Override
    public List<Measurement> getMeasurements() {
        return parsed;
    }

    @Override
    public void onPhotoAdd(String measurementName, Photo photo) {
        getMeasurement(measurementName).addPhoto(photo);
    }

    @Override
    public void onPhotoRemove(String item) {

    }

    @Override
    public void onPhotoEdit(String item) {

    }

    @Override
    public void onPhotoRename(String item) {

    }

    @Override
    public List<Photo> getPhotos(String measurementName) {
        return getMeasurement(measurementName).getPhotos();
    }

    @Override
    public Photo getPhoto(String measurementName, String photoName)
    {
        return getMeasurement(measurementName).getPhoto(photoName);
    }

    @Override
    public void onAddPhotoMeasurement() {

    }
}
