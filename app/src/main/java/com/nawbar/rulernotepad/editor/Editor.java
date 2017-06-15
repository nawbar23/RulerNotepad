package com.nawbar.rulernotepad.editor;

import android.graphics.drawable.Drawable;
import android.support.v4.util.Pair;

import com.nawbar.rulernotepad.fragments.GalleryFragment;
import com.nawbar.rulernotepad.fragments.MeasurementsFragment;
import com.nawbar.rulernotepad.fragments.PhotoFragment;

import java.util.List;


/**
 * Created by Bartosz Nawrot on 2017-06-15.
 */

public class Editor implements
        MeasurementsFragment.MeasurementsCommandsListener,
        GalleryFragment.GalleryFragmentCommandsListener,
        PhotoFragment.PhotoFragmentCommandsListener {

    public Editor() {

    }

    @Override
    public void onMeasurementAdd(String name) {

    }

    @Override
    public void onMeasurementRemove(String name) {

    }

    @Override
    public void onMeasurementSend(String name) {

    }

    @Override
    public List<String> getMeasurements() {
        return null;
    }

    @Override
    public void onPhotoAdd(String item) {

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
    public List<Pair<String, Drawable>> getPhotos() {
        return null;
    }

    @Override
    public void onAddPhotoMeasurement() {

    }
}
