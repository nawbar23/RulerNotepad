package com.nawbar.rulernotepad;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.nawbar.rulernotepad.editor.DataEditor;
import com.nawbar.rulernotepad.fragments.DetailsFragment;
import com.nawbar.rulernotepad.fragments.ProjectsFragment;
import com.nawbar.rulernotepad.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity implements
        ProjectsFragment.ProjectsFragmentListener,
        DetailsFragment.DetailsFragmentListener {

    private DataEditor dataEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO rebuild editor from saved state
        dataEditor = new DataEditor();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new ProjectsFragment())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_about) {
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetailsSelect(String name) {
        Log.e(MainActivity.class.getSimpleName(), "onDetailsSelect " + name);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new DetailsFragment())
                .setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    @Override
    public ProjectsFragment.ProjectsFragmentCommandsListener getProjectsCommandsListener() {
        return dataEditor;
    }

    @Override
    public void onPhotoSelect(String name) {
        Log.e(MainActivity.class.getSimpleName(), "onPhotoSelect " + name);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new ProjectsFragment())
                .setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    @Override
    public DetailsFragment.DetailsFragmentCommandsListener getDetailsCommandsListener() {
        return dataEditor;
    }
}
