package com.nawbar.rulernotepad.email;

import android.os.AsyncTask;
import android.util.Log;

import com.nawbar.rulernotepad.editor.Measurement;

import javax.mail.AuthenticationFailedException;

/**
 * Created by Bartosz Nawrot on 2017-08-24.
 */

public class MeasurementSender {

    private static String TAG = MeasurementSender.class.getSimpleName();

    private Listener listener;

    public MeasurementSender(Listener listener) {
        this.listener = listener;
    }

    public void send(Measurement measurement) {
        new AsyncTask<Measurement, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Measurement... params) {
                try {
                    GMailSender sender = new GMailSender("baza.okna.kosim@gmail.com", "");
                    sender.sendMail("This is Subject",
                            "This is Body",
                            "baza.okna.kosim@gmail.com",
                            "nawbar23@gmail.com");
                    return true;
                } catch (AuthenticationFailedException e) {
                    e.printStackTrace();
                    listener.onError("AuthenticationFailedException");
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onError(e.getMessage());
                    return false;
                }
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

    public interface Listener {
        void onSuccess();
        void onError(final String message);
    }
}
