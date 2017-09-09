package com.nawbar.rulernotepad.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nawbar.rulernotepad.dialogs.AskDialog;
import com.nawbar.rulernotepad.R;
import com.nawbar.rulernotepad.editor.Measurement;
import com.nawbar.rulernotepad.adapters.MeasurementsAdapter;

import java.util.List;

import static android.text.InputType.TYPE_CLASS_PHONE;

/**
 * Created by Bartosz Nawrot on 2017-06-15.
 */

public class MeasurementsFragment extends ListFragment implements
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private static final String TAG = MeasurementsFragment.class.getSimpleName();

    private MeasurementsListener listener;
    private MeasurementsCommandsListener commandsListener;

    private MeasurementsAdapter adapter;

    private int selectedPosition;
    private TextView selectedTextView;

    @Override
    public void onAttach(Context context) {
        Log.e(TAG, "onAttach");
        if (context instanceof MeasurementsListener) {
            listener = (MeasurementsListener) context;
            commandsListener = listener.getMeasurementsCommandsListener();
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MeasurementsFragment.MeasurementsListener");
        }
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.measurments_fragment, container, false);
        setupButtons(rootView);

        selectedPosition = -1;
        selectedTextView = null;

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("selectedPosition")) {
                selectedPosition = savedInstanceState.getInt("selectedPosition");
                Log.e(TAG, "Loaded selectedPosition: " + selectedPosition);
            }
        }

        return rootView;
    }

    @Override
    public void onStart() {
        Log.e(TAG, "onStart");
        super.onStart();

        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);
        reload();
    }

    public void reload() {
        adapter = new MeasurementsAdapter(getActivity(), commandsListener.getMeasurements());
        setListAdapter(adapter);

        if (selectedPosition != -1) {
            Log.e(TAG, "selectedPosition to mark: " + selectedPosition);
        }
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDetach() {
        Log.e(TAG, "onStop");
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, "onSaveInstanceState");
        if (selectedPosition != -1) {
            outState.putInt("selectedPosition", selectedPosition);
        }
        super.onSaveInstanceState(outState);
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
        FloatingActionButton fab_form = (FloatingActionButton) view.findViewById(R.id.fab_form);
        fab_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "fab_form");
                if (selectedPosition != -1) {
                    listener.onFormFill(adapter.getItem(selectedPosition));
                } else {
                    listener.onMessage("Zaznacz pomiar do formularza");
                }
            }
        });

        FloatingActionButton fab_email = (FloatingActionButton) view.findViewById(R.id.fab_email);
        fab_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "fab_email");
                if (selectedPosition != -1) {
                    listener.onMeasurementSend(adapter.getItem(selectedPosition));
                } else {
                    listener.onMessage("Zaznacz pomiar do wysłania");
                }
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
                nameInput.setHint("Nazwisko");
                final EditText phoneInput = new EditText(getActivity());
                phoneInput.setInputType(TYPE_CLASS_PHONE);
                phoneInput.setHint("Numer telefonu");
                LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(nameInput);
                layout.addView(phoneInput);
                layout.setDividerPadding(30);
                builder.setTitle("Nowy pomiar")
                        .setCancelable(true)
                        .setView(layout)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String newName = nameInput.getText().toString();
                                String newPhone = phoneInput.getText().toString();
                                if (!newName.isEmpty() && !newPhone.isEmpty()) {
                                    Log.e(TAG, "New name for measurement: " + newName + " with " + newPhone);
                                    String newNameUpper = newName.substring(0,1).toUpperCase() + newName.substring(1);
                                    Measurement m = commandsListener.onMeasurementAdd(newNameUpper, newPhone);
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
        void onMeasurementSend(Measurement measurement);
        void onFormFill(Measurement measurement);
        MeasurementsCommandsListener getMeasurementsCommandsListener();
    }

    public interface MeasurementsCommandsListener {
        Measurement onMeasurementAdd(String measurementName, String measurementPhone);
        void onMeasurementRemove(Measurement measurement);
        List<Measurement> getMeasurements();
    }
}
