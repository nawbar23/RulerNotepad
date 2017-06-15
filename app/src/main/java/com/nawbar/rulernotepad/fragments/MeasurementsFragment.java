package com.nawbar.rulernotepad.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.nawbar.rulernotepad.R;

import java.util.List;

/**
 * Created by Bartosz Nawrot on 2017-06-15.
 */

public class MeasurementsFragment extends ListFragment implements AdapterView.OnItemClickListener {

    private static String TAG = MeasurementsFragment.class.getSimpleName();

    private MeasurementsListener listener;
    private MeasurementsCommandsListener commandsListener;

    @Override
    public void onAttach(Context context) {
        Log.e(TAG, "onAttach");
        super.onAttach(context);
        if (context instanceof MeasurementsListener) {
            listener = (MeasurementsListener) context;
            commandsListener = listener.getMeasurementsCommandsListener();
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MeasurementsFragment.MeasurementsListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.measurments_fragment, container, false);

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
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.Planets, android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
        Log.e(MeasurementsFragment.class.getSimpleName(), "size: " + adapter.getCount());
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e(TAG, "onItemClick, position: " + position);
    }

    public interface MeasurementsListener {
        void onGallerySelect(String name);
        MeasurementsCommandsListener getMeasurementsCommandsListener();
    }

    public interface MeasurementsCommandsListener {
        void onMeasurementAdd(String name);
        void onMeasurementRemove(String name);
        void onMeasurementSend(String name);
        List<String> getMeasurements();
    }
}
