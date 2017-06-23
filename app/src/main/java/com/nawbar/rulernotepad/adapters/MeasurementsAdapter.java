package com.nawbar.rulernotepad.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nawbar.rulernotepad.R;
import com.nawbar.rulernotepad.editor.Measurement;

import java.util.List;

/**
 * Created by nawba on 22.06.2017.
 */

public class MeasurementsAdapter extends ArrayAdapter<Measurement> {

    public MeasurementsAdapter(Context context, List<Measurement> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Measurement measurement = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.measurements_list_row, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        // Populate the data into the template view using the data object
        name.setText(measurement.getName());
        date.setText(measurement.getDate());
        // Return the completed view to render on screen
        return convertView;
    }
}
