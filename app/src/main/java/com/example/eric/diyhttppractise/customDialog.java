package com.example.eric.diyhttppractise;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

/**
 * Created by eric on 16/8/28.
 */
public class customDialog extends AlertDialog implements DialogInterface.OnClickListener{
    protected customDialog(Context context) {
        super(context);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if(i==BUTTON_POSITIVE){

        }
    }
}
