package com.nawbar.rulernotepad.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nawbar.rulernotepad.R;

/**
 * Created by Bartosz Nawrot on 2017-06-15.
 */

public class MeasurementsFragment extends ListFragment {

    public MeasurementsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.measurments_fragment, container, false);

        return rootView;
    }
}
