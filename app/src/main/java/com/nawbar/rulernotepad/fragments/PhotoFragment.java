package com.nawbar.rulernotepad.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nawbar.rulernotepad.R;

/**
 * Created by Bartosz Nawrot on 2017-06-15.
 */

public class PhotoFragment extends Fragment {

    private static String TAG = PhotoFragment.class.getSimpleName();

    private PhotoFragmentListener listener;
    private PhotoFragmentCommandsListener commandsListener;

    @Override
    public void onAttach(Context context) {
        Log.e(TAG, "onAttach");
        super.onAttach(context);
        if (context instanceof PhotoFragmentListener) {
            listener = (PhotoFragmentListener) context;
            commandsListener = listener.getPhotoCommandsListener();
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement PhotoFragment.PhotoFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.photo_fragment, container, false);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    public interface PhotoFragmentListener {
        PhotoFragmentCommandsListener getPhotoCommandsListener();
    }

    public interface PhotoFragmentCommandsListener {
        void onAddPhotoMeasurement();
    }
}
