package com.nawbar.rulernotepad.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nawbar.rulernotepad.R;
import com.nawbar.rulernotepad.editor.Measurement;
import com.nawbar.rulernotepad.editor.Photo;

import java.util.List;

/**
 * Created by Bartosz Nawrot on 22.06.2017.
 */

public class GalleryAdapter extends ArrayAdapter<Photo> {

    public GalleryAdapter(Context context, List<Photo> photos) {
        super(context, 0, photos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Photo photo = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.gallery_list_row, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.name);
        ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
        // Populate the data into the template view using the data object
        name.setText(photo.getName());
        icon.setImageBitmap(photo.getMini());
        // Return the completed view to render on screen
        return convertView;
    }
}
