package com.nawbar.rulernotepad.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nawbar.rulernotepad.R;

import java.util.List;

/**
 * Created by Bartosz Nawrot on 2017-06-15.
 */

public class DetailsFragment extends ListFragment {

    public interface DetailsFragmentListener {
        void onPhotoSelect(String name);
        DetailsFragmentCommandsListener getDetailsCommandsListener();
    }

    public interface DetailsFragmentCommandsListener {
        void onPhotoAdd(String item);
        void onPhotoRemove(String item);
        void onPhotoEdit(String item);
        void onPhotoRename(String item);
        List<Pair<String, Drawable>> getItems();
    }

    private DetailsFragmentListener listener;
    private DetailsFragmentCommandsListener commandsListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(DetailsFragment.class.getSimpleName(), "onCreateView");

        View view = inflater.inflate(R.layout.fragment_details, container, false);

        FloatingActionButton fab_email = (FloatingActionButton) view.findViewById(R.id.fab_email);
        fab_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPhotoSelect("bbb");
                Snackbar.make(view, "Replace with your own action: fab_email", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DetailsFragmentListener) {
            listener = (DetailsFragmentListener) context;
            commandsListener = listener.getDetailsCommandsListener();
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ProjectsFragment.DetailsFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}