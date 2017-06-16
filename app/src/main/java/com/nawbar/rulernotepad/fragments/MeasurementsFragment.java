package com.nawbar.rulernotepad.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.nawbar.rulernotepad.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bartosz Nawrot on 2017-06-15.
 */

public class MeasurementsFragment extends ListFragment implements
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private static String TAG = MeasurementsFragment.class.getSimpleName();

    private MeasurementsListener listener;
    private MeasurementsCommandsListener commandsListener;

    private ArrayAdapter<String> adapter;

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

        setupButtons(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
                commandsListener.getMeasurements());
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
        listener.onMeasurementSelect((String)getListAdapter().getItem(position));
        return true;
    }

    private void setupButtons(View view) {
        FloatingActionButton fab_email = (FloatingActionButton) view.findViewById(R.id.fab_email);
        fab_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "fab_email");
            }
        });

        FloatingActionButton fab_add = (FloatingActionButton) view.findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "fab_add");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final EditText nameInput = new EditText(getActivity());
                nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
                nameInput.setHint("Jak się ma nazywać ten pomiar?");
                builder.setTitle("Nowy pomiar")
                        .setCancelable(true)
                        .setView(nameInput)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String newName = nameInput.getText().toString();
                                if (!newName.isEmpty()) {
                                    Log.e(TAG, "New name for measurement: " + newName);
                                    commandsListener.onMeasurementAdd(newName);
                                    adapter.add(newName);
                                    listener.onMeasurementSelect(newName);
                                }
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
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

    public interface MeasurementsListener {
        void onMeasurementSelect(String name);
        MeasurementsCommandsListener getMeasurementsCommandsListener();
    }

    public interface MeasurementsCommandsListener {
        void onMeasurementAdd(String name);
        void onMeasurementRemove(String name);
        void onMeasurementSend(String name);
        List<String> getMeasurements();
    }
}
