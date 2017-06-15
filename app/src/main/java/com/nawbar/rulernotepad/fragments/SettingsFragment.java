package com.nawbar.rulernotepad.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.nawbar.rulernotepad.R;

/**
 * Created by Bartosz Nawrot on 2017-06-15.
 */

public class SettingsFragment extends PreferenceFragment {

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
