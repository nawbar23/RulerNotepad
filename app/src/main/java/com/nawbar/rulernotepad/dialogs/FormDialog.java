package com.nawbar.rulernotepad.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.nawbar.rulernotepad.R;
import com.nawbar.rulernotepad.adapters.FormAdapter;
import com.nawbar.rulernotepad.editor.Measurement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Bartosz Nawrot on 2017-08-24.
 */

public class FormDialog extends Dialog implements
        DialogInterface.OnDismissListener,
        View.OnClickListener {

    private static final String TAG = FormDialog.class.getSimpleName();

    private Listener listener;
    private Measurement measurement;

    public FormDialog(Context context, Listener listener, Measurement measurement) {
        super(context);
        this.listener = listener;
        this.measurement = measurement;

        setCancelable(false);
        setTitle("Zakres prac");
        setOnDismissListener(this);
        setContentView(R.layout.form_dialog);

        List<String> resArrayList = Arrays.asList(getContext().getResources().getStringArray(R.array.questions));
        List<Pair<String, Boolean>> list = new ArrayList<>();
        int i = 0;
        for (String s : resArrayList) {
            list.add(new Pair<>(s, measurement.getFormValue(i)));
            i++;
        }

        FormAdapter adapter = new FormAdapter(getContext(), list, measurement);
        ListView optList = (ListView) findViewById(R.id.list_view);
        optList.setAdapter(adapter);

        Button safe = (Button) findViewById(R.id.btn_safe);
        safe.setOnClickListener(this);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.e(TAG, "onDismiss");
        listener.onFormClose(measurement);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_safe) {
            measurement.setFormFilled();
            dismiss();
        } else {
            Log.e(TAG, "Ooops, unexpected click event");
        }
    }

    public interface Listener {
        void onFormClose(Measurement measurement);
    }
}
