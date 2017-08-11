package com.nawbar.rulernotepad.database;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.nawbar.rulernotepad.editor.Arrow;
import com.nawbar.rulernotepad.editor.Measurement;
import com.nawbar.rulernotepad.editor.Photo;

/**
 * Created by Bartosz Nawrot on 2017-08-08.
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getName();
    private static final String DATABASE_NAME = "measurements.db";
    private static final int DATABASE_VERSION = 1; // 11.08.2017

    // the DAO object we use to access the SimpleData table
    private Dao<Measurement, Integer> measurementsDao = null;
    private Dao<Photo, Integer> photosDao = null;
    private Dao<Arrow, Integer> arrowsDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(TAG, "onCreate");
            setupDatabase();
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    private void setupDatabase() throws SQLException {
        // if you need to create the table
        TableUtils.createTable(connectionSource, Measurement.class);
        TableUtils.createTable(connectionSource, Photo.class);
        TableUtils.createTable(connectionSource, Arrow.class);
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Measurement.class, true);
            TableUtils.dropTable(connectionSource, Photo.class, true);
            TableUtils.dropTable(connectionSource, Arrow.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Measurement, Integer> getMeasurementDao() throws SQLException{
        if (measurementsDao == null) {
            measurementsDao = getDao(Measurement.class);
        }
        return measurementsDao;
    }

    public Dao<Photo, Integer> getPhotoDao() throws SQLException {
        if (photosDao == null) {
            photosDao = getDao(Photo.class);
        }
        return photosDao;
    }

    public Dao<Arrow, Integer> getArrowDao() throws SQLException {
        if (arrowsDao == null) {
            arrowsDao = getDao(Arrow.class);
        }
        return arrowsDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        measurementsDao = null;
        photosDao = null;
        arrowsDao = null;
    }
}