package com.nawbar.rulernotepad.form;

import android.app.Dialog;
import android.content.Context;

import com.nawbar.rulernotepad.editor.Measurement;

/**
 * Created by Bartosz Nawrot on 2017-08-24.
 */

public class FormDialog extends Dialog {
    private static String TAG = FormDialog.class.getSimpleName();

    private Listener listener;
    private Measurement measurement;

    public FormDialog(Context context, Listener listener, Measurement measurement) {
        super(context);
        this.listener = listener;
        this.measurement = measurement;

        setCancelable(false);
    }

    public interface Listener {
        void onClose();
    }
}
