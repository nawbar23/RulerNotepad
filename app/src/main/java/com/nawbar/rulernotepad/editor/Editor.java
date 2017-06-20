package com.nawbar.rulernotepad.editor;

import android.graphics.Bitmap;
import android.support.v4.util.Pair;
import android.util.Log;

import com.nawbar.rulernotepad.fragments.GalleryFragment;
import com.nawbar.rulernotepad.fragments.MeasurementsFragment;
import com.nawbar.rulernotepad.fragments.PhotoFragment;

import java.util.ArrayList;
import java.util.Collections;
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
    private Map<String, Map<String, Bitmap>> parsed;

    public Editor() {
        parsed = new HashMap<>();
    }

    @Override
    public void onMeasurementAdd(String name) {
        parsed.put(name, new HashMap<String, Bitmap>());
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
    public void onPhotoAdd(Pair<String, String> name, Bitmap photo) {
        parsed.get(name.first).put(name.second, photo);
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
    public Map<String, Bitmap> getPhotos(String measurement) {
        Map<String, Bitmap> result = parsed.get(measurement);
        if (result == null) {
            Log.e(TAG, "Cashing measurements: " + measurement);
            result = Collections.emptyMap();
        }
        return result;
    }

    @Override
    public Bitmap getPhoto(Pair<String, String> name)
    {
        Map<String, Bitmap> gallery = parsed.get(name.first);
        if (gallery == null) {
            Log.e(TAG, "Cashing measurements: " + name.first);
            gallery = Collections.emptyMap();
        }
        return gallery.get(name.second);
    }

    @Override
    public void onAddPhotoMeasurement() {

    }
}
