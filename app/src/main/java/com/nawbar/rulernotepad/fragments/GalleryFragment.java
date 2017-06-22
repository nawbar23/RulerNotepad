package com.nawbar.rulernotepad.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v4.content.FileProvider;
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
import com.nawbar.rulernotepad.adapters.GalleryAdapter;
import com.nawbar.rulernotepad.editor.Measurement;
import com.nawbar.rulernotepad.editor.Photo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private ArrayAdapter<Photo> adapter;

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

        adapter = new GalleryAdapter(getActivity(), commandsListener.getPhotos(listener.getCurrentMeasurement()));
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
        listener.onPhotoSelect(adapter.getItem(position).getName());
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
                                    dispatchTakePictureIntent();
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

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                Log.e(TAG, ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider",
                        photoFile);
                List<ResolveInfo> resInfoList = getActivity().getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    getActivity().grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.e(TAG, "onActivityResult with photo: " + currentPhoto + " path: " + mCurrentPhotoPath);
            Photo toAdd = new Photo(currentPhoto);

            toAdd.setFull(BitmapFactory.decodeFile(mCurrentPhotoPath));

            // Get the dimensions of the View
            int targetW = 100;
            int targetH = 100;
            int photoW = toAdd.getFull().getWidth();
            int photoH = toAdd.getFull().getHeight();

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            toAdd.setMini(BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions));
            commandsListener.onPhotoAdd(listener.getCurrentMeasurement(), toAdd);

            // TODO delete file saved in path for memory performance

            listener.onPhotoSelect(currentPhoto);
        }
    }

    public interface GalleryFragmentListener {
        void onPhotoSelect(String name);
        String getCurrentMeasurement();
        GalleryFragmentCommandsListener getGalleryCommandsListener();
    }

    public interface GalleryFragmentCommandsListener {
        void onPhotoAdd(String measurementName, Photo photo);
        void onPhotoRemove(String item);
        void onPhotoEdit(String item);
        void onPhotoRename(String item);
        List<Photo> getPhotos(String measurement);
    }
}
