package com.nawbar.rulernotepad.editor;

import android.graphics.drawable.Drawable;
import android.support.v4.util.Pair;
import android.util.Log;

import com.nawbar.rulernotepad.fragments.GalleryFragment;
import com.nawbar.rulernotepad.fragments.MeasurementsFragment;
import com.nawbar.rulernotepad.fragments.PhotoFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Bartosz Nawrot on 2017-06-15.
 */

public class Editor implements
        MeasurementsFragment.MeasurementsCommandsListener,
        GalleryFragment.GalleryFragmentCommandsListener,
        PhotoFragment.PhotoFragmentCommandsListener {

    private static String TAG = Editor.class.getSimpleName();

    // <Measurement name, Photos<Pair<Photo name, Photo>>>
    private Map<String, List<Pair<String, Drawable>>> parsed;

    public Editor() {
        parsed = new HashMap<>();
    }

    @Override
    public void onMeasurementAdd(String name) {
        parsed.put(name, new ArrayList<Pair<String, Drawable>>());
    }

    @Override
    public void onMeasurementRemove(String name) {

    }

    @Override
    public void onMeasurementSend(String name) {

    }

    @Override
    public List<String> getMeasurements() {
        return new ArrayList<>(parsed.keySet());
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
    public List<Pair<String, Drawable>> getPhotos(String measurement) {
        List<Pair<String, Drawable>> result = parsed.get(measurement);
        if (result == null) {
            Log.e(TAG, "Cashing measurements: " + measurement);
            result = new ArrayList<>();
        }
        return result;
    }

    @Override
    public void onAddPhotoMeasurement() {

    }
}
