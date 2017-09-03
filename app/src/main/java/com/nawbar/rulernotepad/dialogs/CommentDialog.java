package com.nawbar.rulernotepad.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;

import com.nawbar.rulernotepad.editor.Editor;
import com.nawbar.rulernotepad.editor.Photo;

/**
 * Created by Bartosz Nawrot on 2017-09-03.
 */

public class CommentDialog {
    private static final String TAG = AskDialog.class.getSimpleName();

    public static AlertDialog show(Context context, final Editor editor, final Photo photo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Comment");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setSingleLine(false);  //add this
        input.setLines(4);
        input.setMaxLines(5);
        input.setGravity(Gravity.START | Gravity.TOP);
        input.setHorizontalScrollBarEnabled(false);
        if (photo.getComment() != null) {
            input.setText(photo.getComment());
        }
        builder.setView(input);
        builder.setPositiveButton("ZAPISZ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Log.e(TAG, "Comment accepted");
                String comment = input.getText().toString();
                if (!comment.isEmpty()) {
                    Log.e(TAG, "Saving comment to database: " + comment);
                    photo.setComment(comment);
                    editor.update(photo);
                }
            }
        });
        builder.setNegativeButton("ODRZUC",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.show();
    }
}
