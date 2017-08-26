package com.nawbar.rulernotepad.adapters;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.nawbar.rulernotepad.R;
import com.nawbar.rulernotepad.editor.Measurement;

import java.util.List;

/**
 * Created by nawba on 24.08.2017.
 */

public class FormAdapter extends ArrayAdapter<Pair<String, Boolean>> {

    private static String TAG = FormAdapter.class.getSimpleName();

    private Measurement measurement;

    public FormAdapter(Context context, List<Pair<String, Boolean>> questions, Measurement measurement) {
        super(context, 0, questions);
        this.measurement = measurement;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Pair<String, Boolean> quest = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.form_dialog_quest_row, parent, false);
        }
        // Lookup view for data population
        Switch sw = (Switch) convertView.findViewById(R.id.quest_switch);
        // Populate the data into the template view using the data object
        sw.setText(quest.first);
        sw.setChecked(quest.second);

        final int positionSafe = position;
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e(TAG, "onCheckedChanged: " + String.valueOf(isChecked) + " at " + String.valueOf(positionSafe));
                measurement.setFormValue(positionSafe, isChecked);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
