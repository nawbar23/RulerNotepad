package com.nawbar.rulernotepad;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by Bartosz Nawrot on 2017-08-24.
 */

public class AskDialog {
    private static String TAG = AskDialog.class.getSimpleName();

    public static AlertDialog show(Context context,
                                   String title,
                                   String message,
                                   DialogInterface.OnClickListener pos) {
        return show(context,title, message, pos, null);
    }

    public static AlertDialog show(Context context,
                                   String title,
                                   String message,
                                   DialogInterface.OnClickListener pos,
                                   DialogInterface.OnClickListener neg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        return builder.setTitle(title)
                .setCancelable(true)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, pos)
                .setNegativeButton(android.R.string.cancel, neg)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
}
