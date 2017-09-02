package com.nawbar.rulernotepad.email;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.nawbar.rulernotepad.editor.Measurement;
import com.nawbar.rulernotepad.editor.Photo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.mail.AuthenticationFailedException;

/**
 * Created by Bartosz Nawrot on 2017-08-24.
 */

public class MeasurementSender {

    private static final String TAG = MeasurementSender.class.getSimpleName();

    private static final String stagingPath = "staging_path";

    private ContextWrapper contextWrapper;
    private Listener listener;

    public MeasurementSender(Context context, Listener listener) {
        this.contextWrapper = new ContextWrapper(context);
        this.listener = listener;
    }

    public void send(Measurement measurement) {
        new AsyncTask<Measurement, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Measurement... params) {

                Measurement m = params[0];
                String subject = m.getName() + " - " + m.getDateString();
                ArrayList<Pair<File, String>> attachments = new ArrayList<>();
                for (Photo p : m.getPhotos()) {
                    attachments.add(new Pair<>(saveToInternalStorage(p.getName(), p.getFull()), p.getName()));
                    Log.e(TAG, "Created: " + attachments.get(attachments.size() - 1).first.getAbsolutePath());
                }

                boolean result = false;
                try {
                    GMailSender sender = new GMailSender("baza.okna.kosim@gmail.com", "");
                    sender.sendMail("baza.okna.kosim@gmail.com",
                            "nawbar23@gmail.com",
                            subject,
                            "This is Body",
                            attachments);
                    result = true;
                } catch (AuthenticationFailedException e) {
                    e.printStackTrace();
                    listener.onError("AuthenticationFailedException");
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onError(e.getMessage());
                }

                // cleanup
                deleteRecursive(contextWrapper.getDir(stagingPath, Context.MODE_PRIVATE));

                return result;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (result) {
                    listener.onSuccess();
                } else {
                    Log.e(TAG, "Error occurred, listener already notified");
                }
            }
        }.execute(measurement);
    }

    private File saveToInternalStorage(String name, Bitmap bitmapImage){
        File directory = contextWrapper.getDir(stagingPath, Context.MODE_PRIVATE);
        File path = new File(directory, name + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        if (!fileOrDirectory.delete()) {
            Log.e(TAG, "Can not delete: " + fileOrDirectory.toString());
        } else {
            Log.e(TAG, "Deleted: " + fileOrDirectory.toString());
        }
    }

    public interface Listener {
        void onSuccess();
        void onError(final String message);
    }
}
