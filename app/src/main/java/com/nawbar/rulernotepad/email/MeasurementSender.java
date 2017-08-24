package com.nawbar.rulernotepad.email;

import android.os.AsyncTask;
import android.text.style.WrapTogetherSpan;
import android.util.Log;

import com.nawbar.rulernotepad.editor.Measurement;

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
                    Thread.sleep(1234);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                listener.onError("tralalalala asd asd asd asda d:D :D :D :D");
                return false;

                //return true;
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
