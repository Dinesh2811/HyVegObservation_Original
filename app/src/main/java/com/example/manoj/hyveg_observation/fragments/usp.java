package com.example.manoj.hyveg_observation.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.manoj.hyveg_observation.Api;
import com.example.manoj.hyveg_observation.Interfaces.IAlertListener;
import com.example.manoj.hyveg_observation.Others.AppController;
import com.example.manoj.hyveg_observation.Others.GPSTracker;
import com.example.manoj.hyveg_observation.R;
import com.example.manoj.hyveg_observation.activity.ScreenActivity;
import com.example.manoj.hyveg_observation.activity.Sessionsave;
import com.example.manoj.hyveg_observation.adapter.u_list_adapter;
import com.example.manoj.hyveg_observation.list.ob_list;
import com.example.manoj.hyveg_observation.services.alertDialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class usp extends Fragment implements IAlertListener ,View.OnClickListener {

    TextView text_usp;
    RecyclerView recyclerView;
    List<ob_list> observation_list;
    String crop_code = "", myJSON = "", TAG = "usp", from, crop_id, no_desc = "0", crop_name = "", strAdd,grower_code = "",
            pd_number = "",pd_status="";
    alertDialog alert;
    Button save, btn_previous;
    Sessionsave sessionsave;
    int save_no;
    private ProgressDialog dialog;
    private File Oroot = Environment.getExternalStorageDirectory(), GetMasterPath = null;
    private SQLiteDatabase dbGetMaster;
    private Cursor GetWareHouseDetails;
    StringBuilder sb_master, sb_general, sb_observe, sb_conclusion;
    // GPSTracker class
    GPSTracker gps;
    double latitude, longitude;

    public usp() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionsave = new Sessionsave(Objects.requireNonNull(getActivity()));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final ScrollView rootview = (ScrollView) inflater.inflate(R.layout.fragment_usp, container, false);

//        View rootview= inflater.inflate(R.layout.fragment_usp, container, false);

        save = rootview.findViewById(R.id.save);
        text_usp = rootview.findViewById(R.id.text_usp);
        btn_previous = rootview.findViewById(R.id.btn_previous);
        btn_previous.setVisibility(View.GONE);
//        spin = rootview.findViewById(R.id.spin);
//        spin1.add("Select");
//        spin1.add("Good");
//        spin1.add("Medium");
//        spin1.add("Poor");
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, spin1);
//        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
//        spin.setAdapter(adapter);

        recyclerView = rootview.findViewById(R.id.cp_usp);
        alert = new alertDialog(this.getActivity());
        observation_list = new ArrayList<>();

        assert getArguments() != null;
        crop_code = getArguments().getString("crop_code");
        crop_name = getArguments().getString("crop_name");
        grower_code = getArguments().getString("grower_code");
        pd_number = getArguments().getString("pd_number");
        pd_status = getArguments().getString("pd_status");
        from = getArguments().getString("from");
        if (from.equals("edit")) {
            crop_id = getArguments().getString("crop_id");
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (Oroot.canWrite()) {
            File dir = new File(Oroot.getAbsolutePath() + "/Android/Observation");
            dir.mkdirs();

            GetMasterPath = new File(dir, "GetMasterDB.db");

        }

        dbGetMaster = getActivity().openOrCreateDatabase(GetMasterPath.getPath(), Context.MODE_PRIVATE, null);

        save.setOnClickListener(this);
        btn_previous.setOnClickListener(this);
        save.setVisibility(View.VISIBLE);
        dialog = new ProgressDialog(getActivity());

//        try {
//            Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
//            intent.putExtra("enabled", true);
//            getActivity().sendBroadcast(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        gps_function();

        return rootview;
    }

    private void gps_function() {
        final LocationManager manager = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            displayLocationSettingsRequest(getActivity());
        }
        else {
            gps = new GPSTracker(getActivity());
            // check if GPS enabled
            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                //  strAdd = getAddressFromLocation(latitude, longitude);
//                    statusCheck();
                strAdd = "";
                strAdd = getCompleteAddressString(latitude, longitude);


            }
        }
    }
    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result)
            {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("TAG", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("TAG", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(getActivity(), 1);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("TAG", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("TAG", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }


    @SuppressLint("LongLogTag")
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.e("myaddress----",strAdd);
                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    @Override
    public void onResume() {
        super.onResume();

//        get_observation_data();
        get_observation_data_db();

    }

    private void get_observation_data_db() {
        String ret_s = null;
        GetWareHouseDetails = dbGetMaster.rawQuery("select * from ObservationMaster where CropCode = '" + crop_code + "'", null);

        if (GetWareHouseDetails.moveToFirst()) {
            ret_s = GetWareHouseDetails.getString(2);
        }

        if (ret_s == null) {
            Toast.makeText(getActivity(), "No data available for this crop", Toast.LENGTH_SHORT).show();
        } else {
            spilt_data(ret_s);
        }
    }

    private void spilt_data(String s) {
        observation_list = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(s);

            JSONArray cn = jsonObj.getJSONArray("cn");
            JSONObject c = null;
            for (int i = 0; i < cn.length(); i++) {
                ob_list ob = new ob_list();
                try {
                    c = cn.getJSONObject(i);
                    ob.getKey(c.getString("fkey"));
                    ob.getName(c.getString("fname"));
                    ob.getReq(c.getString("req"));
                    try {
                        if (c.getString("valbased") != null) {

                            JSONObject val_based = new JSONObject(c.getString("valbased"));

                            Iterator<String> iter = val_based.keys();
                            while (iter.hasNext()) {
                                String key = iter.next();
                                try {
                                    switch (key) {
                                        case "parentkey":
                                            ob.getValKey(String.valueOf(val_based.get(key)));
                                            break;
                                        case "parentlable":
                                            ob.getValLabel(String.valueOf(val_based.get(key)));
                                            break;
                                        case "parentval":
                                            ob.getValValue(String.valueOf(val_based.get(key)));
                                            break;
                                    }
                                } catch (JSONException e) {
                                    // Something went wrong!
                                    Log.e(TAG, "val based error " + e.toString());
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error no val based ");
                    }
                    String f_type = c.getString("ftype");
                    ob.getType(f_type);
                    if (f_type.equals("select") || f_type.equals("checkbox")) {
                        ob.get_multi(c.getString("msel"));
                        JSONArray choice = c.getJSONArray("choice");
                        String[] choice_desc = new String[choice.length()];
                        String[] choice_val = new String[choice.length()];
                        for (int j = 0; j < choice.length(); j++) {
                            choice_desc[j] = choice.getJSONObject(j).getString("cdesc");
                            choice_val[j] = choice.getJSONObject(j).getString("cval");
                        }
                        ob.getChoice_desc(choice_desc);
                        ob.getChoice_val(choice_val);
                    } else if (f_type.equals("number")) {
                        ob.get_decimal(c.getString("decimal"));
                        try {
                            ob.get_min(c.getString("minVal"));
                            ob.get_max(c.getString("maxVal"));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "no min & max " + e.toString());
                        }
                    } else if (f_type.equals("instruction")) {
                        sessionsave.save_usp_instruction(c.getString("fname"));
                    }
                    if (from.equals("edit")) {
                        String f_value = c.getString("value");
                        ob.getValue(f_value);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    JSONArray dr = jsonObj.getJSONArray("dr");
                } catch (Exception e) {
                    no_desc = "1";
                }
                assert c != null;
                if (!c.getString("ftype").equals("instruction")) {
                    observation_list.add(ob);
                }
            }
            RecyclerView.Adapter adapter = new u_list_adapter(getActivity(), observation_list, from,grower_code,pd_number,pd_status);
            recyclerView.setAdapter(adapter);
            if (sessionsave.get_usp_instruction().equals("null")) {
                text_usp.setVisibility(View.GONE);
            } else {
                text_usp.setVisibility(View.VISIBLE);
                text_usp.setText(sessionsave.get_usp_instruction());
            }
            save.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "No Data Available", Toast.LENGTH_LONG).show();

        }
    }

    private void get_observation_data() {
        observation_list = new ArrayList<>();
        if (alert.isNetworkAvailable()) {
            class GetDataJSON extends AsyncTask<String, Void, String> {

                @Override
                protected String doInBackground(String... params) {
                    HttpPost httppost = null;
                    DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                    if (from.equals("crop_select")) {
                        try {
                            httppost = new HttpPost(Api.get_observation(URLEncoder.encode(crop_code, "UTF-8"), sessionsave.get_company()));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            httppost = new HttpPost(Api.ob_data(URLEncoder.encode(crop_id, "UTF-8"), sessionsave.get_company()));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    // Depends on your web service
                    httppost.setHeader("Content-type", "application/json");

                    InputStream inputStream = null;
                    String result = null;
                    try {
                        HttpResponse response = httpclient.execute(httppost);
                        HttpEntity entity = response.getEntity();

                        inputStream = entity.getContent();
                        // json is UTF-8 by default
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                        StringBuilder sb = new StringBuilder();

                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        result = sb.toString();
                    } catch (Exception e) {
                        // Oops
                    } finally {
                        try {
                            if (inputStream != null) inputStream.close();
                        } catch (Exception squish) {
                        }
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(String result) {
                    myJSON = result;
                    try {
                        spilt_data();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            GetDataJSON g = new GetDataJSON();
            g.execute();
        }
    }

    private void spilt_data() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);

            JSONArray cn = jsonObj.getJSONArray("cn");
            JSONObject c = null;
            for (int i = 0; i < cn.length(); i++) {
                ob_list ob = new ob_list();
                try {
                    c = cn.getJSONObject(i);
                    ob.getKey(c.getString("fkey"));
                    ob.getName(c.getString("fname"));
                    ob.getReq(c.getString("req"));
                    String f_type = c.getString("ftype");
                    ob.getType(f_type);
                    if (f_type.equals("select") || f_type.equals("checkbox")) {
                        ob.get_multi(c.getString("msel"));
                        JSONArray choice = c.getJSONArray("choice");
                        String[] choice_desc = new String[choice.length()];
                        String[] choice_val = new String[choice.length()];
                        for (int j = 0; j < choice.length(); j++) {
                            choice_desc[j] = choice.getJSONObject(j).getString("cdesc");
                            choice_val[j] = choice.getJSONObject(j).getString("cval");
                        }
                        ob.getChoice_desc(choice_desc);
                        ob.getChoice_val(choice_val);
                    } else if (f_type.equals("number")) {
                        ob.get_decimal(c.getString("decimal"));
                        try {
                            ob.get_min(c.getString("minVal"));
                            ob.get_max(c.getString("maxVal"));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "no min & max " + e.toString());
                        }
                    }
                    if (from.equals("edit")) {
                        String f_value = c.getString("value");
                        ob.getValue(f_value);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    JSONArray dr = jsonObj.getJSONArray("dr");
                } catch (Exception e) {
                    no_desc = "1";
                }
                observation_list.add(ob);
            }
            RecyclerView.Adapter adapter = new u_list_adapter(getActivity(), observation_list, from,grower_code,
                    pd_number,pd_status);
            recyclerView.setAdapter(adapter);
            save.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "No Data Available", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.save) {
            final LocationManager manager = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService( Context.LOCATION_SERVICE );
            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                displayLocationSettingsRequest(getActivity());
            }
            else {
                gps = new GPSTracker(getActivity());
                // check if GPS enabled
                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    //  strAdd = getAddressFromLocation(latitude, longitude);
//                    statusCheck();
                    strAdd = "";
                    strAdd = getCompleteAddressString(latitude, longitude);
                    if (check_save()) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity());
                        alertDialogBuilder.setTitle("Save Observation");
                        alertDialogBuilder.setMessage("Are you sure of saving this data");
                        alertDialogBuilder.setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        arg0.dismiss();
                                        myJSON = "";
                                        save_no = 1;
                                        dialog.setMessage("Saving this observation data \nPlease wait .....");
                                        dialog.show();
                                        save_data();
//                                save_data_db();
                                    }
                                });
                        alertDialogBuilder.setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } else {
                        Toast.makeText(getActivity(), "Please enter the required(*) fields and try again", Toast.LENGTH_SHORT).show();
                    }


                }
            }

        }
        else if (view.getId() == R.id.btn_previous) {
            Intent broadCastReceiver = new Intent();
            broadCastReceiver.setAction("CHAT_RECEIVED");
            if (no_desc.equals("1")) {
                broadCastReceiver.putExtra("id", "1");
            } else {
                broadCastReceiver.putExtra("id", "2");
            }
            getActivity().sendBroadcast(broadCastReceiver);
        }
    }

//    private boolean check_save() {
//        boolean res = true;
//        for (int i = 0 ; i< observation_list.size() ;i++){
//            if (observation_list.get(i).setReq().equals("1")){
//                if (!observation_list.get(i).setValue().trim().equals("") && !observation_list.get(i).setValue().trim().equals("Select")){
//                    res = true;
//                }else {
//                    res = false;
//                }
//            }
//        }
//        return res;
//    }

    private boolean check_save() {
        boolean res = true;
        ArrayList<Boolean> res_arr = new ArrayList<>();
        for (int i = 0; i < observation_list.size(); i++) {
            if (observation_list.get(i).setReq().equals("1")) {
                if (!observation_list.get(i).setValue().trim().equals("") && !observation_list.get(i).setValue().trim().equals("Select")) {
                    if (observation_list.get(i).setValKey() == null) {
                        res_arr.add(true);
                    }
                    try {
                        if (observation_list.get(i).setValCheck().equals("1")) {
                            res_arr.add(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (observation_list.get(i).setValKey() == null) {
                        res_arr.add(false);
                    }
                    try {
                        if (observation_list.get(i).setValCheck().equals("1")) {
                            res_arr.add(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        for (int j = 0; j < res_arr.size(); j++) {
            if (!res_arr.get(j)) {
                res = false;
            }
        }
        return res;
    }

    private void save_data_db() {

//        try {
//            sb_master = new StringBuilder();
//            int master_len = Integer.parseInt(sessionsave.get_one("master_len"));
//            for (int i = 0; i < master_len; i++) {
//
//                if (sessionsave.get_one("master_key_" + i).contains("img")) {
//                    int len = Integer.parseInt(sessionsave.get_image("master_len"));
//                    for (int mas = 1; mas < (len + 1); mas++) {
//                        Log.e("Master_details",sessionsave.get_one("master_key_" + i));
//                        sb_master.append(sessionsave.get_one("master_key_" + i) + "_" + mas + "=>" + sessionsave.get_image("master_" + mas));
//                        sb_master.append("|");
//                    }
////                                    StringBuilder sb = new StringBuilder();
////                                    for (int mas = 1 ; mas < (len+1) ;mas++){
////                                        if (mas == 1) {
////                                            sb.append(sessionsave.get_image("master_" + mas));
////                                        }else {
////                                            sb.append(",");
////                                            sb.append(sessionsave.get_image("master_" + mas));
////                                        }
//////                                        nameValuePairs.add(new BasicNameValuePair(sessionsave.get_one("master_key_" + i)+"_"+mas, sessionsave.get_image("master_" + mas)));
////                                    }
////                                    nameValuePairs.add(new BasicNameValuePair(sessionsave.get_one("master_key_" + i)+"[]", sb.toString()));
//                } else {
//                    sb_master.append(sessionsave.get_one("master_key_" + i) + "=>" + sessionsave.get_one("master_" + i));
//                    sb_master.append("|");
//                }
//
////                sb_master.append(sessionsave.get_one("master_key_" + i) + "=>" + sessionsave.get_one("master_" + i));
////                sb_master.append("|");
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "Error " + e.toString());
//        }
//
//        try {
//            sb_general = new StringBuilder();
//            int general_len = Integer.parseInt(sessionsave.get_two("general_len"));
//            for (int i = 0; i < general_len; i++) {
//                if (sessionsave.get_two("general_key_" + i).contains("img")) {
//                    int len = Integer.parseInt(sessionsave.get_image("general_len"));
//                    for (int mas = 1; mas < (len + 1); mas++) {
//                        sb_general.append(sessionsave.get_two("general_key_" + i) + "_" + mas + "=>" + sessionsave.get_image("general_" + mas));
//                        sb_general.append("|");
//                    }
//                } else {
//                    sb_general.append(sessionsave.get_two("general_key_" + i) + "=>" + sessionsave.get_two("general_" + i));
//                    sb_general.append("|");
//                }
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "Error " + e.toString());
//        }
//
//        try {
//            sb_observe = new StringBuilder();
//            int observe_len = Integer.parseInt(sessionsave.get_three("observe_len"));
//            for (int i = 0; i < observe_len; i++) {
//                if (sessionsave.get_three("observe_key_" + i).contains("img")) {
//                    int len = Integer.parseInt(sessionsave.get_image("observe_len"));
//                    for (int mas = 1; mas < (len + 1); mas++) {
//                        sb_observe.append(sessionsave.get_three("observe_key_" + i) + "_" + mas + "=>" + sessionsave.get_image("observe_" + mas));
//                        sb_observe.append("|");
//                    }
//                } else {
//                    sb_observe.append(sessionsave.get_three("observe_key_" + i) + "=>" + sessionsave.get_three("observe_" + i));
//                    sb_observe.append("|");
//                }
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "Error " + e.toString());
//        }

        try {
            sb_conclusion = new StringBuilder();
            int usp_len = Integer.parseInt(sessionsave.get_four("usp_len"));
            for (int i = 0; i < usp_len; i++) {
                if (sessionsave.get_four("usp_key_" + i).contains("img")) {
                    int len = Integer.parseInt(sessionsave.get_image("usp_len"));
                    for (int mas = 1; mas < (len + 1); mas++) {
                        sb_conclusion.append(sessionsave.get_four("usp_key_" + i) + "_" + mas + "=>" + sessionsave.get_image("usp_" + mas));
                        sb_conclusion.append("|");
                    }
                } else {
                    sb_conclusion.append(sessionsave.get_four("usp_key_" + i) + "=>" + sessionsave.get_four("usp_" + i));
                    sb_conclusion.append("|");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error " + e.toString());
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date)); //2016-11-16 12:08:43
        String createdDateTime = dateFormat.format(date);
        String query = "INSERT INTO ObservationData(CropCode,CropName,Company,BreederCode,HVCode,Variety,Stage,State,City,CreatedBy,Master_data,General_data,Observe_data,usp_data,CreatedDateTime,Location,grower_code,pd_register_num,pd_status) VALUES('" + crop_code + "','" + sessionsave.get_crop_name() + "','" + sessionsave.get_company() + "','" + sessionsave.get_breeder_code() + "','" + sessionsave.get_hv_code() + "','" + sessionsave.get_variety() + "','" + "conclusion" + "','" + sessionsave.get_states_code() + "','" + sessionsave.get_city() + "','" + sessionsave.get_emp_code() + "','" + "sb_master" + "','" + "sb_general" + "','" + "sb_observe" + "','" + sb_conclusion + "','" + createdDateTime + "','" + "" + "','" + grower_code + "','" + pd_number + "','" + "4" + "');";
        dbGetMaster.execSQL(query);
        if (dialog != null) {
            dialog.cancel();
        }
        Toast.makeText(getActivity(), "Observation Added Successfully", Toast.LENGTH_SHORT).show();
        sessionsave.session_clear();
        Intent myIntent = new Intent(getActivity(), ScreenActivity.class);
        startActivity(myIntent);
        Objects.requireNonNull(getActivity()).finish();
    }

    private void save_data() {
        if (alert.isNetworkAvailable()) {

            Map<String, String> params = new HashMap<>();
            ArrayList<String> img_names = new ArrayList<>();
            ArrayList<String> images = new ArrayList<>();

            params.put("Company", sessionsave.get_company());
            params.put("pdRegNum", pd_number);
            params.put("pdStatus", "4");
            params.put("CropCode",crop_code);
            params.put("CropName", crop_name);
            params.put("CreatedBy", sessionsave.get_emp_code());
            params.put("GPSLocation", strAdd.trim());
            params.put("menuId", "4");
            if (save_no == 1) {
                params.put("obid", "");
            } else {
                params.put("obid", sessionsave.get_obid());
            }
            final File Oroot = Environment.getExternalStorageDirectory();
            final String f_path = Oroot.getPath() + "/Android/RnDObservation/Images/";
//            if (save_no == 1) {
//                try {
//                    int master_len = Integer.parseInt(sessionsave.get_one("master_len"));
//                    params.put("menuId", save_no + "");
//                    for (int i = 0; i < master_len; i++) {
//                        if (sessionsave.get_one("master_key_" + i).contains("img")) {
//                            int len = Integer.parseInt(sessionsave.get_image("master_len"));
//                            for (int mas = 1; mas < (len + 1); mas++) {
//                                String[] q = sessionsave.get_image("master_" + mas).split("/");
//                                int idx = q.length - 1;
//                                images.add(f_path + q[idx]);
//                                img_names.add(sessionsave.get_one("master_key_" + i) + "_" + mas);
////                                nameValuePairs.add(new BasicNameValuePair(sessionsave.get_one("master_key_" + i) + "_" + mas, sessionsave.get_image("master_" + mas)));
//                            }
//                        } else {
//                            params.put(sessionsave.get_one("master_key_" + i), sessionsave.get_one("master_" + i));
//                        }
//                    }
//                } catch (Exception e) {
//                    Log.e(TAG, "Error " + e.toString());
//                }
//            } else if (save_no == 2) {
//                try {
//                    int general_len = Integer.parseInt(sessionsave.get_two("general_len"));
//                    params.put("menuId", save_no + "");
//                    for (int i = 0; i < general_len; i++) {
//                        if (sessionsave.get_two("general_key_" + i).contains("img")) {
//                            int len = Integer.parseInt(sessionsave.get_image("general_len"));
//                            for (int mas = 1; mas < (len + 1); mas++) {
//                                String[] q = sessionsave.get_image("general_" + mas).split("/");
//                                int idx = q.length - 1;
//                                images.add(f_path + q[idx]);
//                                img_names.add(sessionsave.get_one("general_key_" + i) + "_" + mas);
////                                nameValuePairs.add(new BasicNameValuePair(sessionsave.get_one("general_key_" + i) + "_" + mas, sessionsave.get_image("general_" + mas)));
//                            }
//                        } else {
//                            params.put(sessionsave.get_two("general_key_" + i), sessionsave.get_two("general_" + i));
//                        }
//                    }
//                } catch (Exception e) {
//                    Log.e(TAG, "Error " + e.toString());
//                }
//            } else if (save_no == 3) {
//                if (no_desc.equals("1")) {
//                    try {
//                        int usp_len = Integer.parseInt(sessionsave.get_four("usp_len"));
//                        params.put("menuId", (save_no + 1) + "");
//                        for (int i = 0; i < usp_len; i++) {
//                            if (sessionsave.get_four("usp_key_" + i).contains("img")) {
//                                int len = Integer.parseInt(sessionsave.get_image("usp_len"));
//                                for (int mas = 1; mas < (len + 1); mas++) {
//                                    String[] q = sessionsave.get_image("usp_" + mas).split("/");
//                                    int idx = q.length - 1;
//                                    images.add(f_path + q[idx]);
//                                    img_names.add(sessionsave.get_one("usp_key_" + i) + "_" + mas);
////                                    nameValuePairs.add(new BasicNameValuePair(sessionsave.get_one("usp_key_" + i) + "_" + mas, sessionsave.get_image("usp_" + mas)));
//                                }
//                            } else {
//                                params.put(sessionsave.get_four("usp_key_" + i), sessionsave.get_four("usp_" + i));
//                            }
//                        }
//                    } catch (Exception e) {
//                        Log.e(TAG, "Error " + e.toString());
//                    }
//                } else {
//                    try {
//                        int observe_len = Integer.parseInt(sessionsave.get_three("observe_len"));
//                        params.put("menuId", save_no + "");
//                        for (int i = 0; i < observe_len; i++) {
//                            if (sessionsave.get_three("observe_key_" + i).contains("img")) {
//                                int len = Integer.parseInt(sessionsave.get_image("observe_len"));
//                                for (int mas = 1; mas < (len + 1); mas++) {
//                                    String[] q = sessionsave.get_image("observe_" + mas).split("/");
//                                    int idx = q.length - 1;
//                                    images.add(f_path + q[idx]);
//                                    img_names.add(sessionsave.get_one("observe_key_" + i) + "_" + mas);
////                                    nameValuePairs.add(new BasicNameValuePair(sessionsave.get_one("observe_key_" + i) + "_" + mas, sessionsave.get_image("observe_" + mas)));
//                                }
//                            } else {
//                                params.put(sessionsave.get_three("observe_key_" + i), sessionsave.get_three("observe_" + i));
//                            }
//                        }
//                    } catch (Exception e) {
//                        Log.e(TAG, "Error " + e.toString());
//                    }
//                }
//            } else if (save_no == 4) {

                try {
                    int usp_len = Integer.parseInt(sessionsave.get_four("usp_len"));
//                    params.put("menuId", save_no + "");
                    for (int i = 0; i < usp_len; i++) {
                        if (sessionsave.get_four("usp_key_" + i).contains("img")) {
                            int len = Integer.parseInt(sessionsave.get_image("usp_len"));
                            for (int mas = 1; mas < (len + 1); mas++) {
                                String[] q = sessionsave.get_image("usp_" + mas).split("/");
                                int idx = q.length - 1;
                                images.add(f_path + q[idx]);
                                img_names.add(sessionsave.get_one("usp_key_" + i) + "_" + mas);
//                                nameValuePairs.add(new BasicNameValuePair(sessionsave.get_one("usp_key_" + i) + "_" + mas, sessionsave.get_image("usp_" + mas)));
                            }
                        } else {
                            params.put(sessionsave.get_four("usp_key_" + i), sessionsave.get_four("usp_" + i));
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error " + e.toString());
                }
//            }
            String result = multipartRequest(Api.obd_insert, params, images, img_names, "image/*");
            Log.e(TAG, "res 0 " + result);
            Log.e("usp", String.valueOf(params));
        } else {
            save_data_db();
        }
    }

    public String multipartRequest(final String urlTo, final Map<String, String> parmas, final ArrayList<String> filepaths, final ArrayList<String> file_names, final String fileMimeType) {
            final String[] result = {""};

        class UploadFileAsync extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... strings) {
                HttpURLConnection connection = null;
                DataOutputStream outputStream = null;
                InputStream inputStream = null;

                String twoHyphens = "--";
                String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
                String lineEnd = "\r\n";

                try {

                    URL url = new URL(urlTo);
                    connection = (HttpURLConnection) url.openConnection();

                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);

                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                    outputStream = new DataOutputStream(connection.getOutputStream());
                    for (int i = 0; i < filepaths.size(); i++) {
                        int bytesRead, bytesAvailable, bufferSize;
                        byte[] buffer;
                        int maxBufferSize = 1 * 1024 * 1024;
                        String[] q = filepaths.get(i).split("/");
                        int idx = q.length - 1;
                        File file = new File(filepaths.get(i));
                        Log.e(TAG, "sending image " + file_names.get(i));
                        if (file.exists()) {
                            FileInputStream fileInputStream = new FileInputStream(file);

                            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + file_names.get(i) + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
                            outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
                            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
                            outputStream.writeBytes(lineEnd);

                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            buffer = new byte[bufferSize];

                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                            while (bytesRead > 0) {
                                outputStream.write(buffer, 0, bufferSize);
                                bytesAvailable = fileInputStream.available();
                                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                            }

                            outputStream.writeBytes(lineEnd);
                            fileInputStream.close();
                        } else {
                            Log.e(TAG, "error no file");
                        }
                    }

                    // Upload POST Data
                    for (String key : parmas.keySet()) {
                        String value = parmas.get(key);
                        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                        outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                        outputStream.writeBytes(lineEnd);
                        outputStream.writeBytes(value);
                        outputStream.writeBytes(lineEnd);
                    }

                    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    if (200 != connection.getResponseCode()) {
                        Log.e(TAG, "Failed to upload code:" + connection.getResponseCode() + " " + connection.getResponseMessage());
                    }

                    inputStream = connection.getInputStream();

                    result[0] = convertStreamToString(inputStream);

                    inputStream.close();
                    outputStream.flush();
                    outputStream.close();

                    return result[0];
                } catch (Exception e) {
                    Log.e(TAG, "error " + e.toString());
                }
                return result[0];

            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    myJSON = result;
                    Log.e(TAG, "res _1 " + result);

                    if(myJSON.contains("success")){
                        Toast.makeText(getActivity(), "Observation Added Successfully", Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(getActivity(), ScreenActivity.class);
                        startActivity(myIntent);
                        Objects.requireNonNull(getActivity()).finish();
                    }else{
                        Toast.makeText(getActivity(), "Error in uploading", Toast.LENGTH_SHORT).show();
                    }
//                    if (!result.equals("failed")) {
//                        if (save_no == 1) {
//                            sessionsave.save_obid(myJSON.trim());
//                        }
//                        save_no++;
//                        if (no_desc.equals("1")) {
//                            if (save_no < 4) {
//                                save_data();
//                            } else {
//                                if (dialog != null) {
//                                    dialog.cancel();
//                                }
//                                Toast.makeText(getActivity(), "Observation Added Successfully", Toast.LENGTH_SHORT).show();
//                                sessionsave.session_clear();
//                                Objects.requireNonNull(getActivity()).finish();
//                            }
//                        } else {
//                            if (save_no < 5) {
//                                save_data();
//                            } else {
//                                if (dialog != null) {
//                                    dialog.cancel();
//                                }
//                                Toast.makeText(getActivity(), "Observation Added Successfully", Toast.LENGTH_SHORT).show();
//                                sessionsave.session_clear();
//                                Objects.requireNonNull(getActivity()).finish();
//                            }
//                        }
//                    } else {
//                        Toast.makeText(gps, "Error on record inserting", Toast.LENGTH_SHORT).show();
//                    }
                } catch (Exception e) {
                    dialog.cancel();
                    e.printStackTrace();
                }
            }

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected void onProgressUpdate(Void... values) {
            }
        }
        new UploadFileAsync().execute("");
        return result[0];
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    @Override
    public void onOKclick(DialogInterface dialogInterface, String type) {
        switch (type) {
            case "Network":
                dialogInterface.dismiss();
                Intent intent1 = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent1);
                break;
        }
    }

    @Override
    public void onCancelclick(DialogInterface dialogInterface, String type) {
        switch (type) {
            case "Network":
                dialogInterface.dismiss();
                break;
        }
    }
}
