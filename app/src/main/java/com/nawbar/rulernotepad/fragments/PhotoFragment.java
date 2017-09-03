package com.nawbar.rulernotepad.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nawbar.rulernotepad.dialogs.AskDialog;
import com.nawbar.rulernotepad.PhotoNotepadView;
import com.nawbar.rulernotepad.R;
import com.nawbar.rulernotepad.editor.Arrow;
import com.nawbar.rulernotepad.editor.Photo;

import java.util.List;

/**
 * Created by Bartosz Nawrot on 2017-06-15.
 */

public class PhotoFragment extends Fragment {

    private static final String TAG = PhotoFragment.class.getSimpleName();

    private PhotoFragmentListener listener;

    private PhotoNotepadView photoView;

    @Override
    public void onAttach(Context context) {
        Log.e(TAG, "onAttach");
        super.onAttach(context);
        if (context instanceof PhotoFragmentListener) {
            listener = (PhotoFragmentListener) context;
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
        photoView.initialize(listener.getCurrentPhoto(), listener.getPhotoCommandsListener());
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    private void setupButtons(View view) {
        FloatingActionButton fab_comment = (FloatingActionButton) view.findViewById(R.id.fab_comment);
        fab_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "fab_comment");
                listener.onComment(listener.getCurrentPhoto());
            }
        });

        FloatingActionButton fab_revert = (FloatingActionButton) view.findViewById(R.id.fab_revert);
        fab_revert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "fab_revert");
                AskDialog.show(getActivity(), "Cofnij!", "Napewno chcesz usunąc ostatni wymiar?", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(TAG, "fab_revert accepted");
                        photoView.onRevert();
                    }
                });
            }
        });
    }

    public interface PhotoFragmentListener {
        void onMessage(String message);
        void onComment(Photo currentPhoto);
        Photo getCurrentPhoto();
        PhotoFragmentCommandsListener getPhotoCommandsListener();
    }

    public interface PhotoFragmentCommandsListener {
        void onArrowAdd(Arrow arrow);
        void onArrowRemove(Arrow arrow);
        List<Arrow> getArrows(Photo photo);
    }
}
