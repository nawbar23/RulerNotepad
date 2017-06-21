package com.nawbar.rulernotepad;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.nawbar.rulernotepad.editor.Editor;
import com.nawbar.rulernotepad.fragments.GalleryFragment;
import com.nawbar.rulernotepad.fragments.MeasurementsFragment;
import com.nawbar.rulernotepad.fragments.PhotoFragment;

public class MainActivity extends AppCompatActivity implements
        MeasurementsFragment.MeasurementsListener,
        GalleryFragment.GalleryFragmentListener,
        PhotoFragment.PhotoFragmentListener {

    private static String TAG = MainActivity.class.getSimpleName();

    private Fragment[] fragments;
    private int currentPosition;

    private Editor editor;

    private String currentMeasurement;
    private String currentPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editor = new Editor();

        fragments = new Fragment[]{new MeasurementsFragment(), new GalleryFragment(), new PhotoFragment()};
        currentPosition = 0;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, fragments[currentPosition]);
        ft.commit();
    }

    @Override
    public void onMeasurementSelect(String name) {
        Log.e(TAG, "onGallerySelect: " + name);
        currentMeasurement = name;
        currentPosition = 1;
        moveToFragment();
    }

    @Override
    public MeasurementsFragment.MeasurementsCommandsListener getMeasurementsCommandsListener() {
        return editor;
    }

    @Override
    public void onPhotoSelect(String name) {
        Log.e(TAG, "onPhotoSelect: " + name);
        currentPhoto = name;
        currentPosition = 2;
        moveToFragment();
    }

    @Override
    public String getCurrentMeasurement() {
        return currentMeasurement;
    }

    @Override
    public GalleryFragment.GalleryFragmentCommandsListener getGalleryCommandsListener() {
        return editor;
    }

    @Override
    public Pair<String, String> getCurrentPhoto() {
        return new Pair<>(currentMeasurement, currentPhoto);
    }

    @Override
    public PhotoFragment.PhotoFragmentCommandsListener getPhotoCommandsListener() {
        return editor;
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
}
