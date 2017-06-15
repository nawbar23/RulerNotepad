package com.nawbar.rulernotepad.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nawbar.rulernotepad.R;

/**
 * Created by Bartosz Nawrot on 2017-06-15.
 */

public class PhotoFragment extends Fragment {

    public PhotoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.photo_fragment, container, false);

        return rootView;
    }
}
