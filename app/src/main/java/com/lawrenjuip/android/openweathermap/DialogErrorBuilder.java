package com.lawrenjuip.android.openweathermap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class DialogErrorBuilder{
    //let the user know location data was unable to be accessed due to permissions and close application
    public void showDialogError(final Activity activity){
        DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch(i){
                    case DialogInterface.BUTTON_POSITIVE:
                        activity.finish();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
           builder.setMessage(R.string.location_error)
           .setPositiveButton(R.string.exit_application, mOnClickListener)
           .show();
    }
}
