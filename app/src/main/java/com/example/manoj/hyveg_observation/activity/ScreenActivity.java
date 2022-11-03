package com.example.manoj.hyveg_observation.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;

import com.example.manoj.hyveg_observation.Interfaces.IAlertListener;
import com.example.manoj.hyveg_observation.Others.GridSpacingItemDecoration;
import com.example.manoj.hyveg_observation.R;
import com.example.manoj.hyveg_observation.adapter.screen_list_adapter;
import com.example.manoj.hyveg_observation.services.alertDialog;

public class ScreenActivity extends AppCompatActivity implements IAlertListener{


    RecyclerView recycle_screen;
    Sessionsave sessionsave;
    alertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);

        sessionsave = new Sessionsave(this);
        recycle_screen = findViewById(R.id.recycle_screen);
        alert = new alertDialog(this);

        alert.setServiceListener(this);


        Log.e("values--emp--",sessionsave.get_emp_code());

        recycle_screen.setHasFixedSize(true);
        recycle_screen.setLayoutManager(new GridLayoutManager(this ,2));
        recycle_screen.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(), true));
    }

    private int dpToPx() {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics()));
    }

    @Override
    public void onOKclick(DialogInterface dialogInterface, String type) {
        if (type.equals("Network") || type.equals("Network1")){
            dialogInterface.dismiss();
            Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
            startActivity(intent);
        }
    }

    @Override
    public void onCancelclick(DialogInterface dialogInterface, String type) {
        switch (type) {
            case "Network":
                dialogInterface.dismiss();
                break;
            case "Network1":
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        check();
    }

    private void check() {
        if (sessionsave.get_d_code().equals("PDE")){
            if (alert.isNetworkAvailable()) {
                String[] show_data = {"Get Data","Grower Register", "PD Trial Field Registration", "OB Create", "OB List","Upload Data", "Logout"};
                int[] show_img = {R.drawable.get_data,R.drawable.master, R.drawable.upload, R.drawable.ob_create_3, R.drawable.ob_list,R.drawable.upload, R.drawable.logout};
                RecyclerView.Adapter adapter = new screen_list_adapter(ScreenActivity.this, show_data, show_img, this);
                recycle_screen.setAdapter(adapter);
            }else {
                String[] show_data = {"Get Data","Grower Register", "PD Trial Field Registration", "OB Create", "Offline OB List","Upload Data", "Logout"};
                int[] show_img = {R.drawable.get_data, R.drawable.master, R.drawable.upload, R.drawable.ob_create_3, R.drawable.ob_list,R.drawable.upload, R.drawable.logout};
                RecyclerView.Adapter adapter = new screen_list_adapter(ScreenActivity.this, show_data, show_img,this);
                recycle_screen.setAdapter(adapter);
            }
        }else if (sessionsave.get_d_code().equals("PDM")){
            if (alert.isNetworkAvailable()) {
                String[] show_data = {"Get Data","Grower Register", "PD Trial Field Registration", "OB Create", "OB List","Upload Data", "Logout"};
                int[] show_img = {R.drawable.get_data,R.drawable.master, R.drawable.upload, R.drawable.ob_create_3, R.drawable.ob_list,R.drawable.upload, R.drawable.logout};
                RecyclerView.Adapter adapter = new screen_list_adapter(ScreenActivity.this, show_data, show_img,this);
                recycle_screen.setAdapter(adapter);
            }else {
                String[] show_data = {"Get Data","Grower Register", "PD Trial Field Registration","OB Create", "Offline OB List", "Upload Data", "Logout"};
                int[] show_img = {R.drawable.get_data,R.drawable.master, R.drawable.upload, R.drawable.ob_create_3, R.drawable.ob_list,R.drawable.upload, R.drawable.logout};
                RecyclerView.Adapter adapter = new screen_list_adapter(ScreenActivity.this, show_data, show_img,this);
                recycle_screen.setAdapter(adapter);
            }
//            else {
//                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
//                alert.alert_Msg("Network", "No Network Connection Available.", "Network1");
//            }
        }
    }
}
