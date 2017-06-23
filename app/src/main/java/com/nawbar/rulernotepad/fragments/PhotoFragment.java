package com.nawbar.rulernotepad.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nawbar.rulernotepad.PhotoNotepadView;
import com.nawbar.rulernotepad.R;
import com.nawbar.rulernotepad.editor.Photo;

/**
 * Created by Bartosz Nawrot on 2017-06-15.
 */

public class PhotoFragment extends Fragment {

    private static String TAG = PhotoFragment.class.getSimpleName();

    private PhotoFragmentListener listener;
    private PhotoFragmentCommandsListener commandsListener;

    private PhotoNotepadView photoView;

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
        Log.e(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.photo_fragment, container, false);
        photoView = (PhotoNotepadView) rootView.findViewById(R.id.photo_view);
        setupButtons(rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        Log.e(TAG, "onStart");
        super.onStart();
        photoView.setPhoto(commandsListener.getPhoto(listener.getCurrentPhoto().first,
                listener.getCurrentPhoto().second));
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    private void setupButtons(View view) {
        FloatingActionButton fab_revert = (FloatingActionButton) view.findViewById(R.id.fab_revert);
        fab_revert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "fab_revert");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Cofnij!")
                        .setCancelable(true)
                        .setMessage("Napewno chcesz usunąc ostatni wymiar?")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e(TAG, "fab_revert accepted");
                                photoView.onRevert();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        });
    }

    public interface PhotoFragmentListener {
        Pair<String, String> getCurrentPhoto();
        PhotoFragmentCommandsListener getPhotoCommandsListener();
    }

    public interface PhotoFragmentCommandsListener {
        Photo getPhoto(String measurementName, String photoName);
        void onAddPhotoMeasurement();
    }
}
