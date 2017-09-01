package com.nawbar.rulernotepad;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.nawbar.rulernotepad.database.DatabaseHelper;
import com.nawbar.rulernotepad.dialogs.AskDialog;
import com.nawbar.rulernotepad.editor.Editor;
import com.nawbar.rulernotepad.editor.Measurement;
import com.nawbar.rulernotepad.editor.Photo;
import com.nawbar.rulernotepad.email.MeasurementSender;
import com.nawbar.rulernotepad.dialogs.FormDialog;
import com.nawbar.rulernotepad.fragments.GalleryFragment;
import com.nawbar.rulernotepad.fragments.MeasurementsFragment;
import com.nawbar.rulernotepad.fragments.PhotoFragment;

/**
 * Created by Bartosz Nawrot on 2017-06-21.
 */

public class MainActivity extends AppCompatActivity implements
        MeasurementsFragment.MeasurementsListener,
        GalleryFragment.GalleryFragmentListener,
        PhotoFragment.PhotoFragmentListener,
        MeasurementSender.Listener,
        FormDialog.Listener {

    private static String TAG = MainActivity.class.getSimpleName();

    private volatile DatabaseHelper helper = null;
    private volatile boolean created = false;
    private volatile boolean destroyed = false;

    private Fragment[] fragments;
    private int currentPosition;

    private Editor editor;

    private MeasurementSender sender;
    private ProgressDialog emailProgress = null;

    private FormDialog formDialog = null;

    private Measurement currentMeasurement;
    private Photo currentPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        created = true;

        editor = new Editor(getDatabaseHelper());
        sender = new MeasurementSender(this);

        fragments = new Fragment[]{new MeasurementsFragment(), new GalleryFragment(), new PhotoFragment()};
        currentPosition = 0;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, fragments[currentPosition]);
        ft.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyed = true;
    }

    @Override
    public void onMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onMeasurementSelect(Measurement measurement) {
        Log.e(TAG, "onGallerySelect: " + measurement.getName());
        currentMeasurement = measurement;
        currentPosition = 1;
        moveToFragment();
    }

    @Override
    public void onMeasurementSend(Measurement measurement) {
        Log.e(TAG, "onMeasurementSend: " + measurement.getName());
        emailProgress = ProgressDialog.show(this, "Chwilka...", "Wysyłam pomiar :)", true);
        sender.send(measurement);
    }

    @Override
    public void onFormFill(Measurement measurement) {
        Log.e(TAG, "onFormFill: " + measurement.getName());
        formDialog = new FormDialog(this, this, measurement);
        formDialog.show();
    }

    @Override
    public MeasurementsFragment.MeasurementsCommandsListener getMeasurementsCommandsListener() {
        return editor;
    }

    @Override
    public void onPhotoSelect(Photo photo) {
        Log.e(TAG, "onPhotoSelect: " + photo.getName());
        currentPhoto = photo;
        currentPosition = 2;
        moveToFragment();
    }

    @Override
    public Measurement getCurrentMeasurement() {
        return currentMeasurement;
    }

    @Override
    public GalleryFragment.GalleryFragmentCommandsListener getGalleryCommandsListener() {
        return editor;
    }

    @Override
    public Photo getCurrentPhoto() {
        return currentPhoto;
    }

    @Override
    public PhotoFragment.PhotoFragmentCommandsListener getPhotoCommandsListener() {
        return editor;
    }

    @Override
    public void onSuccess() {
        Log.e(TAG, "onSuccess");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (emailProgress != null) {
                    emailProgress.dismiss();
                }
                Toast.makeText(MainActivity.this, "Pomiar wsyłąny, sprawdź skrzynkę pocztową :)", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onError(final String message) {
        Log.e(TAG, "onError " + message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (emailProgress != null) {
                    emailProgress.dismiss();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Błąd!")
                        .setCancelable(true)
                        .setMessage("Pomiar nie wysłąny.\n" +
                                "Sprawdź połączenie internetowe, albo skontaktuj się z Bartkiem :)\n"
                                + message)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    @Override
    public void onFormClose(Measurement measurement) {
        Log.e(TAG, "onFormClose");
        editor.update(measurement);
        formDialog = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.action_about:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.action_about)
                        .setCancelable(true)
                        .setMessage(R.string.action_about_description)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
                break;

            case R.id.action_wipe:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle("Wyczyść dane!")
                        .setCancelable(true)
                        .setMessage("Czy napewno chcesz usunąć całą historę pomiarów?\nDane będą bezpowrotnie stracone!")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e(TAG, "database wipe accepted");
                                helper.wipeDatabase();
                                if (currentPosition > 0) {
                                    currentPosition = 0;
                                    moveToFragment();
                                } else {
                                    ((MeasurementsFragment)fragments[0]).reload();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;

            case android.R.id.home:
                onPopFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (currentPosition > 0) {
            onPopFragment();
        } else {
            super.onBackPressed();
        }
    }

    private void moveToFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter, R.anim.exit);
        ft.replace(R.id.container, fragments[currentPosition]);
        ft.commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void onPopFragment() {
        currentPosition--;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit);
        ft.replace(R.id.container, fragments[currentPosition]);
        ft.commit();
        if (currentPosition == 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    public DatabaseHelper getDatabaseHelper() {
        if (helper == null) {
            if (!created) {
                throw new IllegalStateException("A call has not been made to onCreate() yet so the helper is null");
            } else if (destroyed) {
                throw new IllegalStateException(
                        "A call to onDestroy has already been made and the helper cannot be used after that point");
            } else {
                throw new IllegalStateException("Helper is null for some unknown reason");
            }
        } else {
            return helper;
        }
    }
}
