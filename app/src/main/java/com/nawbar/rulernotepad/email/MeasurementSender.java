package com.nawbar.rulernotepad.email;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.nawbar.rulernotepad.R;
import com.nawbar.rulernotepad.editor.Arrow;
import com.nawbar.rulernotepad.editor.Measurement;
import com.nawbar.rulernotepad.editor.Photo;
import com.nawbar.rulernotepad.photo.ArrowDrawer;

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

    private ArrowDrawer drawer;

    public MeasurementSender(Context context, Listener listener) {
        this.contextWrapper = new ContextWrapper(context);
        this.listener = listener;
        this.drawer = new ArrowDrawer();
    }

    public void send(Measurement measurement) {
        new AsyncTask<Measurement, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Measurement... params) {

                Measurement m = params[0];
                String subject = m.getName() + " - " + m.getDateString();
                String body = buildHtmlBody(m);//buildBody(m);
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

    private ArrayList<Pair<File,String>> buildAttachments(Measurement m) {
        ArrayList<Pair<File, String>> attachments = new ArrayList<>();
        for (Photo p : m.getPhotos()) {
            Bitmap source = p.getFull();
            Bitmap target = source.copy(source.getConfig(), true);
            drawer.setSize(target.getHeight(), target.getWidth());
            Canvas canvas = new Canvas(target);
            for (Arrow a : p.getArrows()) {
                drawer.draw(canvas, a, true);
            }
            attachments.add(new Pair<>(saveToInternalStorage(p.getName(), target), p.getName() + ".jpg"));
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

    private String buildHtmlBody(Measurement m) {
        return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
                "<head>" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>" +
                "<title>Untitled Document</title>" +
                "<style type=\"text/css\">" +
                "body{-webkit-text-size-adjust:none;}" +
                ".ReadMsgBody{width:100%;}" +
                ".ExternalClass{width:100%;}" +
                ".ExternalClass, .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td, .ExternalClass div {line-height: 100%;}" +
                "</style>" +
                "</head>" +
                "<body style=\"padding:0px; margin:0PX;\" bgcolor=\"\">" +
                "<table width=\"100%\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"\"  style=\"table-layout:fixed; margin:0 auto;\">" +
                "<tr>" +
                "<td width=\"640\" align=\"center\" valign=\"top\">" +
                generateMainTable(m) +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</body>" +
                "</html>";
    }

    private String generateMainTable(Measurement m) {
        return "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">" +
                " <tr>" +
                "<td" +
                generateTop(m.getName(), m.getFormattedPhone(), m.getDateString()) +
                "</td>" +
                "</tr>" +
                "<tr>" +
                "<td style=\"padding: 5px 30px 5px 30px;\">" +
                generateBody(m) +
                "</td>" +
                "</tr>" +
                "<tr>" +
                "<td style=\"padding: 30px 30px 5px 30px;\">" +
                generateFooter() +
                "</td>" +
                "</tr>" +
                "</table>";
    }

    private String generateTop(String name, String phone, String date) {
        return "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
                "<tr>" +
                "<td halign=\"center\" valgin=\"left\" style=\"padding: 10px 10px 10px 10px;\">" +
                "<h2>" +
                "Nazwisko: <b>" + name + "</b><br>" +
                "Telefon: <b>" + phone + "</b><br>" +
                "Data: <b>" + date + "</b>" +
                "</h2>" +
                "</td>" +
                "<td align=\"center\">" +

                "<img src=\"http://okna-kosim.pl/wp-content/themes/meritdesign/images/logo.png\"/>" +

                "</td>" +
                "</tr>" +
                "</table>";
    }

    private String generateBody(Measurement m) {
        return "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">" +
                "<tr>" +
                "<td style=\"padding: 30px 30px 30px 30px;\">" +
                generateForm(m) +
                "</td>" +
                "</tr>" +
                "<tr>" +
                "<td style=\"padding: 0px 30px 0px 30px;\">" +
                "</td>" +
                "</tr>" +
                "<tr>" +
                "<td>" +
                generateComments(m) +
                "</td>" +
                "</tr>" +
                "</table>";
    }

    private String generateForm(Measurement m) {
        String result = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">";
        List<String> resArrayList = Arrays.asList(contextWrapper.getResources().getStringArray(R.array.questions));
        int i = 0;
        for (String q : resArrayList) {
            String a = m.getFormValue(i) ? "TAK" : "NIE";
            String color = m.getFormValue(i) ? "#bef7a8" : "#f7a8a8";
            String h = "<tr bgcolor=\"" + color + "\">" +
                    "<td valign=\"center\" style=\"padding: 8px 8px 8px 8px;\">" +
                    q +
                    "</td><td valign=\"center\" align=\"center\"><b>" +
                    a +
                    "</b></td>" +
                    "</tr>";
            result += h;
            i++;
        }
        result += "</table>";
        return result;
    }

    private String generateComments(Measurement m) {
        String result = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">";
        boolean shadow = true;
        for (Photo p : m.getPhotos()) {
            if (p.getComment() != null) {
                String name = p.getName();
                String comment = p.getComment();
                String color = shadow ? "#e3f9f8" : "#f7ffff";
                shadow = !shadow;
                String a = "<tr bgcolor=\"" + color + "\">" +
                        "<td valign=\"top\" style=\"padding: 12px 12px 12px 12px;\"><b>" + name + "</b><br>" + comment + "</td>" +
                        "</tr>";
                result += a;
            }
        }
        result += "</table>";
        return result;
    }

    private String generateFooter() {
        return "Wygenerowano automatycznie w aplikacji do pomiarów Okna Kosim.<br>" +
                "W razie problemów skontaktuj się z Bartkiem :)<br>";
    }

    private String buildBody(Measurement m) {
        StringBuilder sb = new StringBuilder();

        // basic information
        sb.append("Nazwisko klienta: ").append(m.getName()).append('\n');
        sb.append("Numer telefonu: ").append(m.getFormattedPhone()).append('\n');
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

    public interface Listener {
        void onSuccess();
        void onError(final String message);
    }
}
