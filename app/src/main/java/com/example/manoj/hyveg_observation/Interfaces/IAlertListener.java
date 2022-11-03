package com.example.manoj.hyveg_observation.Interfaces;

import android.content.DialogInterface;

/**
 * Created by Agna on 01-03-2018.
 */

public interface IAlertListener {
    void onOKclick(DialogInterface dialogInterface, String type);
    void onCancelclick(DialogInterface dialogInterface, String type);
}
