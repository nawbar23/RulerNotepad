package com.nawbar.rulernotepad.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.nawbar.rulernotepad.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bartosz Nawrot on 2017-06-15.
 */

public class GalleryFragment extends ListFragment implements
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private static String TAG = MeasurementsFragment.class.getSimpleName();

    private GalleryFragmentListener listener;
    private GalleryFragmentCommandsListener commandsListener;

    @Override
    public void onAttach(Context context) {
        Log.e(TAG, "onAttach");
        super.onAttach(context);
        if (context instanceof GalleryFragmentListener) {
            listener = (GalleryFragmentListener) context;
            commandsListener = listener.getGalleryCommandsListener();
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement GalleryFragment.GalleryFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.gallery_fragment, container, false);

        setupButtons(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        ArrayList<String> list = new ArrayList<>();
        List<Pair<String, Drawable>> photos = commandsListener.getPhotos(listener.getCurrentMeasurement());
        for (Pair<String, Drawable> p : photos) {
            list.add(p.first);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
        setListAdapter(adapter);

        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e(TAG, "onItemClick, position: " + position);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e(TAG, "onItemLongClick, position: " + position);
        return false;
    }

    private void setupButtons(View view) {
        FloatingActionButton fab_email = (FloatingActionButton) view.findViewById(R.id.fab_email);
        fab_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "fab_email");
            }
        });

        FloatingActionButton fab_photo = (FloatingActionButton) view.findViewById(R.id.fab_photo);
        fab_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "fab_photo");
                listener.onPhotoSelect("bbb");
            }
        });

        FloatingActionButton fab_remove = (FloatingActionButton) view.findViewById(R.id.fab_remove);
        fab_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "fab_remove");
            }
        });
    }

    public interface GalleryFragmentListener {
        void onPhotoSelect(String name);
        String getCurrentMeasurement();
        GalleryFragmentCommandsListener getGalleryCommandsListener();
    }

    public interface GalleryFragmentCommandsListener {
        void onPhotoAdd(String item);
        void onPhotoRemove(String item);
        void onPhotoEdit(String item);
        void onPhotoRename(String item);
        List<Pair<String, Drawable>> getPhotos(String measurement);
    }
}
