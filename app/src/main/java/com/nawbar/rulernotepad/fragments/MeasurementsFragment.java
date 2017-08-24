package com.nawbar.rulernotepad.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.nawbar.rulernotepad.AskDialog;
import com.nawbar.rulernotepad.R;
import com.nawbar.rulernotepad.editor.Measurement;
import com.nawbar.rulernotepad.adapters.MeasurementsAdapter;

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

    private MeasurementsAdapter adapter;

    private int selectedPosition = -1;
    private TextView selectedTextView = null;

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

        selectedPosition = -1;
        selectedTextView = null;

        return rootView;
    }

    @Override
    public void onStart() {
        Log.e(TAG, "onStart");
        super.onStart();

        reload();
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);
    }

    public void reload() {
        adapter = new MeasurementsAdapter(getActivity(), commandsListener.getMeasurements());
        setListAdapter(adapter);
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e(TAG, "onItemClick, position: " + position);
        if (selectedPosition != position) {
            Log.e(TAG, "setting selection mark");
            TextView tv = (TextView) view.findViewById(R.id.name);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
            if (selectedTextView != null) {
                selectedTextView.setTypeface(null, Typeface.NORMAL);
                selectedTextView.setTextColor(Color.DKGRAY);
            }
            selectedPosition = position;
            selectedTextView = tv;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e(TAG, "onItemLongClick, position: " + position);
        listener.onMeasurementSelect(adapter.getItem(position));
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
                                    Measurement m = commandsListener.onMeasurementAdd(newName);
                                    listener.onMeasurementSelect(m);
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
                if (selectedPosition != -1) {
                    String msg = "Napewno chcesz usunąc pomiar \"" + adapter.getItem(selectedPosition).getName() + "\"?";
                    AskDialog.show(getActivity(), "Usuń!", msg, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.e(TAG, "fab_remove accepted");
                            commandsListener.onMeasurementRemove(adapter.getItem(selectedPosition));
                            selectedPosition = -1;
                            reload();
                        }
                    });
                } else {
                    listener.onMessage("Zaznacz pomiar do usunięcia");
                }
            }
        });
    }

    public interface MeasurementsListener {
        void onMessage(String message);
        void onMeasurementSelect(Measurement measurement);
        MeasurementsCommandsListener getMeasurementsCommandsListener();
    }

    public interface MeasurementsCommandsListener {
        Measurement onMeasurementAdd(String measurementName);
        void onMeasurementRemove(Measurement measurement);
        void onMeasurementSend(Measurement measurement);
        List<Measurement> getMeasurements();
    }
}
