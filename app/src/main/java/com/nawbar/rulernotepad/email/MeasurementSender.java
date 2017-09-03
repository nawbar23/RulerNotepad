package com.nawbar.rulernotepad.email;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.nawbar.rulernotepad.R;
import com.nawbar.rulernotepad.editor.Measurement;
import com.nawbar.rulernotepad.editor.Photo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.AuthenticationFailedException;

/**
 * Created by Bartosz Nawrot on 2017-08-24.
 */

public class MeasurementSender {

    private static final String TAG = MeasurementSender.class.getSimpleName();

    private static final String stagingPath = "staging_path";

    private ContextWrapper contextWrapper;
    private Listener listener;

    private final int maxFormQuestionSize;

    public MeasurementSender(Context context, Listener listener) {
        this.contextWrapper = new ContextWrapper(context);
        this.listener = listener;

        // check for the longest question for mail body
        List<String> resArrayList = Arrays.asList(contextWrapper.getResources().getStringArray(R.array.questions));
        int max = 0;
        for (String q : resArrayList) {
            if (q.length() > max) {
                 max = q.length();
            }
        }
        maxFormQuestionSize = max;
    }

    public void send(Measurement measurement) {
        new AsyncTask<Measurement, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Measurement... params) {

                Measurement m = params[0];
                String subject = m.getName() + " - " + m.getDateString();
                String body = buildBody(m);
                ArrayList<Pair<File, String>> attachments = buildAttachments(m);
                boolean result = false;
                try {
                    GMailSender sender = new GMailSender("baza.okna.kosim@gmail.com", "");
                    sender.sendMail("baza.okna.kosim@gmail.com",
                            "nawbar23@gmail.com",//"milena.kosim@yahoo.com",
                            subject,
                            body,
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

    private String buildBody(Measurement m) {
        StringBuilder sb = new StringBuilder();

        // basic information
        sb.append("Nazwisko klienta: ").append(m.getName()).append('\n');
        sb.append("Numer telefonu: ").append(m.getPhone()).append('\n');
        sb.append("Data pomiaru: ").append(m.getDateString()).append('\n');
        sb.append('\n');

        // form questions
        sb.append("    ----- Formularz -----    ").append('\n');
        List<String> resArrayList = Arrays.asList(contextWrapper.getResources().getStringArray(R.array.questions));
        int i = 0;
        for (String q : resArrayList) {
            sb.append(q).append(" - ").append(m.getFormValue(i) ? "TAK" : "NIE").append('\n');
            i++;
        }
        sb.append('\n');

        // comments
        sb.append("    ----- Komentarze -----    ").append('\n');
        boolean commented = false;
        for (Photo p : m.getPhotos()) {
            if (p.getComment() != null) {
                sb.append("Pomiar: ").append(p.getName()).append('\n');
                sb.append(p.getComment()).append("\n\n");
                commented = true;
            }
        }
        if (!commented) sb.append("Brak komentarzy\n");
        sb.append("\n");
        sb.append("Wygenerowano automatycznie w aplikacji do pomiarów Okna Kosim.\n");
        sb.append("W razie problemów skontaktuj się z Bartkiem :)\n");

        return sb.toString();
    }

    private ArrayList<Pair<File,String>> buildAttachments(Measurement m) {
        ArrayList<Pair<File, String>> attachments = new ArrayList<>();
        for (Photo p : m.getPhotos()) {
            attachments.add(new Pair<>(saveToInternalStorage(p.getName(), p.getFull()), p.getName()));
            Log.e(TAG, "Created: " + attachments.get(attachments.size() - 1).first.getAbsolutePath());
        }
        return attachments;
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
