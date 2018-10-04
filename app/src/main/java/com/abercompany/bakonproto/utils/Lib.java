package com.abercompany.bakonproto.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Lib {


    private static Lib sharedLib;

    public static  Lib getInstance() {
        if (sharedLib == null) {
            sharedLib = new Lib();
        }

        return sharedLib;
    }

    public void showSimpleDialog(Context context, String message, String buttonMessage, DialogInterface.OnClickListener btnListener) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setMessage(message);
        alertBuilder.setNegativeButton(buttonMessage,btnListener);
        alertBuilder.setCancelable(false);
        alertBuilder.create().show();
    }

    public void showSimplSelect2Dialog(Context context, String message, String yesBtn, String noBtn, DialogInterface.OnClickListener yesBtnListener, DialogInterface.OnClickListener noBtnListener) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton(yesBtn,yesBtnListener);
        alertBuilder.setNegativeButton(noBtn,noBtnListener);
        alertBuilder.setCancelable(false);
        alertBuilder.create().show();
    }
}
