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
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.nawbar.rulernotepad.database.DatabaseHelper;
import com.nawbar.rulernotepad.dialogs.AskDialog;
import com.nawbar.rulernotepad.dialogs.CommentDialog;
import com.nawbar.rulernotepad.editor.Editor;
import com.nawbar.rulernotepad.editor.Measurement;
import com.nawbar.rulernotepad.editor.Photo;
import com.nawbar.rulernotepad.email.MeasurementSender;
import com.nawbar.rulernotepad.dialogs.FormDialog;
import com.nawbar.rulernotepad.fragments.GalleryFragment;
import com.nawbar.rulernotepad.fragments.MeasurementsFragment;
import com.nawbar.rulernotepad.fragments.PhotoFragment;

import java.security.KeyStore;
import java.security.KeyStoreException;

/**
 * Created by Bartosz Nawrot on 2017-06-21.
 */

public class MainActivity extends AppCompatActivity implements
        MeasurementsFragment.MeasurementsListener,
        GalleryFragment.GalleryFragmentListener,
        PhotoFragment.PhotoFragmentListener,
        MeasurementSender.Listener,
        FormDialog.Listener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private volatile DatabaseHelper helper = null;
    private volatile boolean created = false;
    private volatile boolean destroyed = false;

    private Fragment[] fragments;
    private int currentPosition;

    private Editor editor;

    private MeasurementSender sender;
    private ProgressDialog emailProgress;

    private FormDialog formDialog;

    private Measurement currentMeasurement;
    private Photo currentPhoto;

    private boolean formWhileSending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");

        helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        created = true;

        editor = new Editor(getDatabaseHelper());
        sender = new MeasurementSender(this, this);

        fragments = new Fragment[]{new MeasurementsFragment(), new GalleryFragment(), new PhotoFragment()};

        currentPosition = 0;
        currentMeasurement = null;
        currentPhoto = null;

        // TODO also these dialogs should be restored if not null
        emailProgress = null;
        formDialog = null;

        restoreState(savedInstanceState);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(currentPosition != 0);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.container, fragments[currentPosition]);
            ft.commit();
        }
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("currentPosition")) {
                currentPosition = savedInstanceState.getInt("currentPosition");
                Log.e(TAG, "Loaded currentPosition: " + currentPosition);
            }
            if (savedInstanceState.containsKey("currentMeasurementId")) {
                currentMeasurement = editor.getMeasurement(savedInstanceState.getInt("currentMeasurementId"));
                Log.e(TAG, "Loaded currentMeasurement: " + currentMeasurement.getName());
            }
            if (savedInstanceState.containsKey("currentPhotoId")) {
                currentPhoto = editor.getPhoto(savedInstanceState.getInt("currentPhotoId"));
                Log.e(TAG, "Loaded currentPhoto: " + currentPhoto.getName());
            }
        }
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, "onSaveInstanceState");
        outState.putInt("currentPosition", currentPosition);
        if (currentMeasurement != null) {
            outState.putInt("currentMeasurementId", currentMeasurement.getId());
        }
        if (currentPhoto != null) {
            outState.putInt("currentPhotoId", currentPhoto.getId());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        destroyed = true;
        super.onDestroy();
    }

    @Override
    public void onMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT);
                ViewGroup group = (ViewGroup) toast.getView();
                TextView messageTextView = (TextView) group.getChildAt(0);
                messageTextView.setTextSize(25);
                toast.show();
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
        if (measurement.getPhotos().isEmpty()) {
            onMessage("Ten pomiar nie ma zadnych zdjęć...");
            return;
        }
        if (measurement.isFormFilled()) {
            sendMeasurement(measurement);
        } else {
            formWhileSending = true;
            onFormFill(measurement);
        }

    }

    @Override
    public void onFormFill(Measurement measurement) {
        Log.e(TAG, "onFormFill: " + measurement.getName());
        formDialog = new FormDialog(this, this, measurement);
        formDialog.show();
    }

    @Override
    public MeasurementsFragment.MeasurementsCommandsListener getMeasurementsCommandsListener() {
        Log.e(TAG, "getMeasurementsCommandsListener");
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
        Log.e(TAG, "getCurrentMeasurement");
        return currentMeasurement;
    }

    @Override
    public GalleryFragment.GalleryFragmentCommandsListener getGalleryCommandsListener() {
        Log.e(TAG, "getGalleryCommandsListener");
        return editor;
    }

    @Override
    public void onComment(Photo photo) {
        Log.e(TAG, "onComment: " + photo.getName());
        CommentDialog.show(this, editor, photo);
    }

    @Override
    public Photo getCurrentPhoto() {
        Log.e(TAG, "getCurrentPhoto");
        return currentPhoto;
    }

    @Override
    public PhotoFragment.PhotoFragmentCommandsListener getPhotoCommandsListener() {
        Log.e(TAG, "getPhotoCommandsListener");
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
        if (formWhileSending) {
            Log.e(TAG, "Sending measurement after form fill");
            formWhileSending = false;
            sendMeasurement(measurement);
        }
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
                                currentMeasurement = null;
                                currentPhoto = null;
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

    private void sendMeasurement(Measurement measurement) {
        Log.e(TAG, "sendMeasurement: " + measurement.getName());
        emailProgress = ProgressDialog.show(this,
                "Chwilka...",
                "Tworzę wiadomość i wysyłam pomiar :)\n" +
                "To może potrwać do kilku minut, w zależności od jakości połączenia internetowego.", true);
        sender.send(measurement);
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
