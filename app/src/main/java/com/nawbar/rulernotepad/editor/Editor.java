package com.nawbar.rulernotepad.editor;

import com.j256.ormlite.dao.Dao;
import com.nawbar.rulernotepad.database.DatabaseHelper;
import com.nawbar.rulernotepad.fragments.GalleryFragment;
import com.nawbar.rulernotepad.fragments.MeasurementsFragment;
import com.nawbar.rulernotepad.fragments.PhotoFragment;

import java.sql.SQLException;
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
        try {
            measurementsDao.delete(measurement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMeasurementSend(Measurement measurement) {

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
        try {
            photosDao.delete(photo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Photo> getPhotos(Measurement measurement) {
        try {
            measurementsDao.refresh(measurement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (measurement.getPhotos() != null) {
            return new ArrayList<>(measurement.getPhotos());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void onArrowAdd(Arrow arrow) {
        if (arrow.getPhoto().getArrows() != null) {
            arrow.getPhoto().getArrows().add(arrow);
        } else {
            try {
                arrowsDao.create(arrow);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onArrowRemove(Arrow arrow) {
        try {
            arrowsDao.delete(arrow);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Arrow> getArrows(Photo photo) {
        try {
            photosDao.refresh(photo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (photo.getArrows() != null) {
            return new ArrayList<>(photo.getArrows());
        } else {
            return new ArrayList<>();
        }
    }

    public void update(Measurement measurement) {
        try {
            measurementsDao.update(measurement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
