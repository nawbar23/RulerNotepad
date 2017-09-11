package com.nawbar.rulernotepad.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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

import com.nawbar.rulernotepad.dialogs.AskDialog;
import com.nawbar.rulernotepad.R;
import com.nawbar.rulernotepad.adapters.GalleryAdapter;
import com.nawbar.rulernotepad.editor.Measurement;
import com.nawbar.rulernotepad.editor.Photo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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

    private String currentPhotoName = null;
    private String currentPhotoPath = null;

    private int selectedPosition;
    private TextView selectedTextView;

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

        selectedPosition = -1;
        selectedTextView = null;

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("selectedPosition")) {
                selectedPosition = savedInstanceState.getInt("selectedPosition");
                Log.e(TAG, "Loaded selectedPosition: " + selectedPosition);
            }
            if (savedInstanceState.containsKey("currentPhotoName")) {
                currentPhotoName = savedInstanceState.getString("currentPhotoName");
                Log.e(TAG, "Loaded currentPhotoName: " + currentPhotoName);
            }
            if (savedInstanceState.containsKey("currentPhotoPath")) {
                currentPhotoPath = savedInstanceState.getString("currentPhotoPath");
                Log.e(TAG, "Loaded currentPhotoPath: " + currentPhotoPath);
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
        adapter = new GalleryAdapter(getActivity(), commandsListener.getPhotos(listener.getCurrentMeasurement()));
        setListAdapter(adapter);

        if (selectedPosition != -1) {
            Log.e(TAG, "selectedPosition to mark: " + selectedPosition);
            // TODO set selection mark
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
        if (currentPhotoName != null) {
            outState.putString("currentPhotoName", currentPhotoName);
        }
        if (currentPhotoPath != null) {
            outState.putString("currentPhotoPath", currentPhotoPath);
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
        listener.onPhotoSelect(adapter.getItem(position));
        return false;
    }

    private void setupButtons(View view) {
        FloatingActionButton fab_form = (FloatingActionButton) view.findViewById(R.id.fab_form);
        fab_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "fab_form");
                listener.onFormFill(listener.getCurrentMeasurement());
            }
        });

        FloatingActionButton fab_email = (FloatingActionButton) view.findViewById(R.id.fab_email);
        fab_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "fab_email");
                listener.onMeasurementSend(listener.getCurrentMeasurement());
            }
        });

        FloatingActionButton fab_comment = (FloatingActionButton) view.findViewById(R.id.fab_comment);
        fab_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "fab_comment");
                if (selectedPosition != -1) {
                    listener.onComment(adapter.getItem(selectedPosition));
                } else {
                    listener.onMessage("Zaznacz zdjęcie do skomentowania");
                }
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
                                currentPhotoName = nameInput.getText().toString();
                                if (!currentPhotoName.isEmpty()) {
                                    List<Photo> photos = commandsListener.getPhotos(listener.getCurrentMeasurement());
                                    boolean exists = false;
                                    for (Photo p : photos) {
                                        if (p.getName().equals(currentPhotoName)) {
                                            exists = true;
                                            break;
                                        }
                                    }
                                    if (!exists) {
                                        Log.e(TAG, "Starting camera for name: " + currentPhotoName);
                                        dispatchTakePictureIntent();
                                    } else {
                                        listener.onMessage("Zdjęcie o takiej nazwie już istnieje...");
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
                if (selectedPosition != -1 && adapter.getItem(selectedPosition) != null) {
                    String msg = "Napewno chcesz usunąc zdjęcie \"" + adapter.getItem(selectedPosition).getName() + "\"?";
                    AskDialog.show(getActivity(), "Usuń!", msg, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.e(TAG, "fab_remove accepted");
                            commandsListener.onPhotoRemove(adapter.getItem(selectedPosition));
                            selectedPosition = -1;
                            reload();
                        }
                    });
                } else {
                    listener.onMessage("Zaznacz zdjęcie do usunięcia");
                }
            }
        });
    }

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
        currentPhotoPath = image.getAbsolutePath();
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
            Log.e(TAG, "onActivityResult with photo: " + currentPhotoName + " path: " + currentPhotoPath);
            final ProgressDialog progress = ProgressDialog.show(getActivity(), "Chwilka...",
                    "Laduje zdjecie :)", true);
            new AsyncTask<String, Void, Void>() {
                @Override
                protected Void doInBackground(String... params) {
                    Photo toAdd = new Photo(listener.getCurrentMeasurement(), currentPhotoName);
                    toAdd.setPhotoBitmap(BitmapFactory.decodeFile(currentPhotoPath));
                    if (new File(currentPhotoPath).delete()) {
                        Log.e(TAG, "Error while deleting temporary file");
                    }
                    commandsListener.onPhotoAdd(toAdd);
                    listener.onPhotoSelect(toAdd);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                        }
                    });
                    currentPhotoName = null;
                    currentPhotoPath = null;
                }
            }.execute(currentPhotoName);
        }
    }

    public interface GalleryFragmentListener {
        void onMessage(String message);
        void onPhotoSelect(Photo photo);
        void onMeasurementSend(Measurement measurement);
        void onFormFill(Measurement measurement);
        void onComment(Photo photo);
        Measurement getCurrentMeasurement();
        GalleryFragmentCommandsListener getGalleryCommandsListener();
    }

    public interface GalleryFragmentCommandsListener {
        void onPhotoAdd(Photo photo);
        void onPhotoRemove(Photo photo);
        List<Photo> getPhotos(Measurement measurement);
    }
}
