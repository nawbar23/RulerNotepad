package com.nawbar.rulernotepad.editor;

import com.j256.ormlite.dao.Dao;
import com.nawbar.rulernotepad.database.DatabaseHelper;
import com.nawbar.rulernotepad.fragments.GalleryFragment;
import com.nawbar.rulernotepad.fragments.MeasurementsFragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bartosz Nawrot on 2017-06-15.
 */

public class Editor implements
        MeasurementsFragment.MeasurementsCommandsListener,
        GalleryFragment.GalleryFragmentCommandsListener {

    private static String TAG = Editor.class.getSimpleName();

    private Dao<Measurement, Integer> measurementsDao;
    private Dao<Photo, Integer> photosDao;
    private Dao<Arrow, Integer> arrowsDao;

    public Editor(DatabaseHelper helper) {
        try {
            measurementsDao = helper.getMeasurementDao();
            photosDao = helper.getPhotoDao();
            arrowsDao = helper.getArrowDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Measurement> getMeasurements() {
        try {
            return measurementsDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Measurement onMeasurementAdd(String name) {
        Measurement m = new Measurement(name);
        try {
            measurementsDao.create(m);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return m;
    }

    @Override
    public void onMeasurementRemove(Measurement measurement) {

    }

    @Override
    public void onMeasurementSend(Measurement measurement) {

    }

    @Override
    public void onPhotoAdd(Photo photo) {
        if (photo.getMeasurement().getPhotos() != null) {
            photo.getMeasurement().getPhotos().add(photo);
        } else {
            try {
                photosDao.create(photo);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPhotoRemove(Photo photo) {

    }

    @Override
    public List<Photo> getPhotos(Measurement measurement) {
        if (measurement.getPhotos() != null) {
            return new ArrayList<>(measurement.getPhotos());
        } else {
            return new ArrayList<>();
        }
    }
}
