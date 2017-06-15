package com.nawbar.rulernotepad;

import android.app.Activity;
import android.os.Bundle;

import com.nawbar.rulernotepad.fragments.SettingsFragment;

/**
 * Created by Bartosz Nawrot on 2017-06-15.
 */

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
