package com.nawbar.rulernotepad.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.nawbar.rulernotepad.MainActivity;
import com.nawbar.rulernotepad.R;
import com.nawbar.rulernotepad.editor.Photo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Bartosz Nawrot on 2017-06-15.
 */

public class GalleryFragment extends ListFragment implements
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private static final String TAG = GalleryFragment.class.getSimpleName();

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private GalleryFragmentListener listener;
    private GalleryFragmentCommandsListener commandsListener;

    private ArrayAdapter<String> adapter;

    private String currentPhoto;

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
    public void onStart() {
        Log.e(TAG, "onStart");
        super.onStart();

        Map<String, Photo> photos = commandsListener.getPhotos(listener.getCurrentMeasurement());
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<>(photos.keySet()));
        setListAdapter(adapter);

        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e(TAG, "onItemClick, position: " + position);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e(TAG, "onItemLongClick, position: " + position);
        listener.onPhotoSelect((String)getListAdapter().getItem(position));
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final EditText nameInput = new EditText(getActivity());
                nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
                nameInput.setHint("Co będzie na zdjęciu?");
                builder.setTitle("Nowe zdjęcie")
                        .setCancelable(true)
                        .setView(nameInput)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                currentPhoto = nameInput.getText().toString();
                                if (!currentPhoto.isEmpty()) {
                                    Log.e(TAG, "Starting camera for name: " + currentPhoto);
                                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                    }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.e(TAG, "onActivityResult with photo: " + currentPhoto);
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Photo toAdd = new Photo();
            toAdd.setMini(imageBitmap);
            toAdd.setFull(imageBitmap);
            commandsListener.onPhotoAdd(new Pair<>(listener.getCurrentMeasurement(), currentPhoto), toAdd);
            adapter.add(currentPhoto);
            listener.onPhotoSelect(currentPhoto);
        }
    }

    public interface GalleryFragmentListener {
        void onPhotoSelect(String name);
        String getCurrentMeasurement();
        GalleryFragmentCommandsListener getGalleryCommandsListener();
    }

    public interface GalleryFragmentCommandsListener {
        void onPhotoAdd(Pair<String, String> name, Photo photo);
        void onPhotoRemove(String item);
        void onPhotoEdit(String item);
        void onPhotoRename(String item);
        Map<String, Photo> getPhotos(String measurement);
    }
}
