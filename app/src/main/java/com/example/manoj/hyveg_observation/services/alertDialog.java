package com.example.manoj.hyveg_observation.services;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;

import com.example.manoj.hyveg_observation.Interfaces.IAlertListener;


/**
 * Created by Agna on 01-03-2018.
 */

public class alertDialog {
    Context context;
    IAlertListener alertListener;
    public alertDialog(Context context) {
        this.context = context;
    }


    public void setServiceListener(IAlertListener alertListener) {
        this.alertListener =  alertListener;
    }

    public void alert_Msg(String title ,String Msg, final String type ) {
        if (type != null) {
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(Msg).setCancelable(false)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertListener.onCancelclick(dialogInterface, type);
                        }
                    })
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertListener.onOKclick(dialog, type);
                        }
                    }).show();
        }else {
            Log.e("alert","error "+ type.toString());
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
