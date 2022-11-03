package com.example.manoj.hyveg_observation.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Environment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.example.manoj.hyveg_observation.R;
import com.example.manoj.hyveg_observation.services.alertDialog;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.manoj.hyveg_observation.Api.Pd_RegistrationNumber;
import static com.example.manoj.hyveg_observation.activity.create_master.MAX_TIME;

public class cropselect extends AppCompatActivity implements View.OnClickListener ,IAlertListener {

    Button cropSelect;
    Spinner cropName ,spin_breeder ,spin_trial ,spin_states;
    TextView text_variety,text_address,text_city,text_taluk,text_village;
    EditText edit_city;
    String CropCode = "" ,myJSON ,TAG = "crop_select" ,TAG_RESULTS = "result" ,CropName = "",breed_code = "" ,Trial_stages = "" ,
            State = "",State_code = "" ,HV_code = "" ,Variety = "" ,City = "";

    String pd_status_offline;
    LinearLayout linear_address;

    //new spinner

    Spinner spin_growercode,spin_pdregisternumber, spin_observationtype;
    String GrowerCode, PdRegisterNumber, pd_status = "",status,check_hybrids,observation_type,address = "",city = "",state = "";

    List<String> Status_list;

    private ProgressDialog dialog;

    public static int general_click = 0;
    //Service
    alertDialog alert;
//    JsonServiceHelper serviceHelper;
    Sessionsave sessionsave;
    private SQLiteDatabase dbGetMaster;
    private Cursor GetWareHouseDetails;
    File Oroot = Environment.getExternalStorageDirectory() ,GetMasterPath=null;
    ArrayList<String> list_breed ,list_trial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropselect);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        dialog = new ProgressDialog(this);

        //Path for the database
        if (Oroot.canWrite()) {
            File dir = new File(Oroot.getAbsolutePath() + "/Android/Observation");
            dir.mkdirs();

            GetMasterPath = new File(dir, "GetMasterDB.db");

        }

        alert = new alertDialog(this);
        sessionsave = new Sessionsave(this);
        cropSelect = findViewById(R.id.cropselect);
        cropName = findViewById(R.id.spin_crop);
        spin_breeder = findViewById(R.id.spin_breed);
//        text_hv_code = findViewById(R.id.text_hv_code);
        text_variety = findViewById(R.id.text_variety);
        spin_trial = findViewById(R.id.spin_stages);
        spin_states = findViewById(R.id.spin_states);
        edit_city = findViewById(R.id.edit_city);

        linear_address = findViewById(R.id.linear_address);
        text_address = findViewById(R.id.text_address);
        text_city = findViewById(R.id.text_city);
        text_taluk = findViewById(R.id.text_taluk);
        text_village = findViewById(R.id.text_village);

        spin_growercode = findViewById(R.id.spin_growercode);
        spin_pdregisternumber = findViewById(R.id.spin_pdregisternumber);

        spin_observationtype = findViewById(R.id.spin_observationtype);

        dbGetMaster = openOrCreateDatabase(GetMasterPath.getPath(), Context.MODE_PRIVATE, null);


        list_trial = new ArrayList<>();

        //checking the database is empty
//        if (isTableExists(dbGetMaster, "CropMaster")) {
//            get_crop_name_db();
////            get_crop_code();
//        }else
//        {
//            Toast.makeText(this, "Please get data from server and try again", Toast.LENGTH_SHORT).show();
//            finish();
//        }

        //newly added 10th april 2020

        get_grower_code();
        get_observation_type();

//

        ///////

//        try {
//            get_crop_name_db();
//        }catch (SQLException e){
//            Toast.makeText(this, "Please get data from server and try again", Toast.LENGTH_SHORT).show();
//        }catch (Exception e){
//            Log.e(TAG,"Error "+e.toString());
//        }

//        cropName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            public void onItemSelected(AdapterView<?> arg0, View arg1,
//                                       int arg2, long arg3) {
//                CropName = cropName.getSelectedItem().toString();
//                CropCode = get_crop_code(cropName.getSelectedItem().toString());
////                text_hv_code.setText("");
//                text_variety.setText("");
//                spin_trial.setSelection(0);
//                spin_states.setSelection(0);
//                edit_city.setText("");
////                get_breeder(CropCode);
//                //getting breeder code when a crop is selected
//                get_breeder_db(CropCode);
//            }
//
//            public void onNothingSelected(AdapterView<?> arg0) {
//            }
//        });

        spin_breeder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
//                breed_code = spin_breeder.getSelectedItem().toString();
                HV_code = spin_breeder.getSelectedItem().toString();
//                get_hv_code(CropCode,breed_code);
                //getting hv code when a breeder is selected
                get_hv_code_db(CropCode ,HV_code);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spin_trial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                Trial_stages = spin_trial.getSelectedItem().toString();
            }

            public void onNothingSelected(AdapterView<?> arg0) {


            }
        });

        spin_states.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                State = spin_states.getSelectedItem().toString();
                State_code = get_state_code(spin_states.getSelectedItem().toString());
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spin_growercode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                String GroCode = spin_growercode.getSelectedItem().toString();
                String[] parts = GroCode.split("-");
                String part1 = parts[0];
                GrowerCode = part1.trim();
                Log.e("values-----cname",part1);
                get_pd_registration_number(part1.trim());
                Log.e("values-----cname",part1);

                address =  get_address(GrowerCode);
                city = get_city(GrowerCode);
                state = get_grower_state(GrowerCode);

                try {
                    String grower_city = city.replace("|",",");
                    String[] gr_add = grower_city.split(",");
                    String village = gr_add[0];
                    String taluk = gr_add[1];
                    String city_gr = gr_add[2];

                    linear_address.setVisibility(View.VISIBLE);
                    text_address.setText(address);
                    text_village.setText(village);
                    text_taluk.setText(taluk);
                    text_city.setText(city_gr + " , " + state);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spin_pdregisternumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                PdRegisterNumber = spin_pdregisternumber.getSelectedItem().toString();
                CropCode = get_crop_code(spin_pdregisternumber.getSelectedItem().toString());
                CropName = get_crop_name_db(CropCode);
                pd_status = get_pd_status(spin_pdregisternumber.getSelectedItem().toString());
                check_hybrids = get_check_hybrids(spin_pdregisternumber.getSelectedItem().toString());

                Log.e("values-----cname",PdRegisterNumber);




            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        spin_observationtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                observation_type = spin_observationtype.getSelectedItem().toString();


                Log.e("values-----cname",observation_type);




            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        list_trial.add("Please Select");
        list_trial.add("RST1");
        list_trial.add("RST2");
        list_trial.add("PD1");
        list_trial.add("PD2");
        list_trial.add("MKT");
        list_trial.add("COM");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_trial);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_trial.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        spin_trial.setAdapter(adapter1);

        cropSelect.setOnClickListener(this);
//        Delete_create_GetMasterDatabase();

        //get states
//        get_states();

    }

    private String get_address(String address) {

        String ret_s = null;
        try {
            GetWareHouseDetails = dbGetMaster.rawQuery("select address from PdregistrationMaster where growerCode = '" + address + "'", null);

            if (GetWareHouseDetails.moveToFirst()) {
                ret_s = GetWareHouseDetails.getString(0);
                Log.e("values-----code",ret_s);

            }
            return ret_s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret_s;
    }

    private String get_city(String address) {

        String ret_s = null;
        try {
            GetWareHouseDetails = dbGetMaster.rawQuery("select city from PdregistrationMaster where growerCode = '" + address + "'", null);

            if (GetWareHouseDetails.moveToFirst()) {
                ret_s = GetWareHouseDetails.getString(0);
                Log.e("values-----city",ret_s);

            }
            return ret_s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret_s;
    }

    private String get_grower_state(String address) {

        String ret_s = null;
        try {
            GetWareHouseDetails = dbGetMaster.rawQuery("select state from PdregistrationMaster where growerCode = '" + address + "'", null);

            if (GetWareHouseDetails.moveToFirst()) {
                ret_s = GetWareHouseDetails.getString(0);
                Log.e("values-----state",ret_s);

            }
            return ret_s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret_s;
    }

    private String get_check_hybrids(String toString) {
        String ret_s = null;
        try {
            GetWareHouseDetails = dbGetMaster.rawQuery("select hybrid from PdregistrationMaster where pdRegNum = '" + toString + "'", null);

            if (GetWareHouseDetails.moveToFirst()) {
                ret_s = GetWareHouseDetails.getString(0);
                Log.e("Values_hycode",ret_s);
            }
            return ret_s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret_s;
    }

    private String get_pd_status(String toString) {

        String ret_s = null;
        try {
            GetWareHouseDetails = dbGetMaster.rawQuery("select status from PdregistrationMaster where pdRegNum = '" + toString + "'", null);

            if (GetWareHouseDetails.moveToFirst()) {
                ret_s = GetWareHouseDetails.getString(0);
                Log.e("Values_cropcode",ret_s);
            }
            return ret_s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret_s;
    }

    private void get_pd_registration_number(String growerCode) {

        List<String> codes = new ArrayList<>();

        if (isTableExists(dbGetMaster, "PdregistrationMaster")) {

            GetWareHouseDetails = dbGetMaster.rawQuery("select pdRegNum from PdregistrationMaster where growerCode = '" + growerCode + "'", null);

            if (GetWareHouseDetails.moveToFirst()) {
                do {
                    codes.add(new String(GetWareHouseDetails.getString(0)));
                } while (GetWareHouseDetails.moveToNext());

            }
        }

        HashSet<String> set = new HashSet<>(codes);
        ArrayList<String> result = new ArrayList<>(set);
        Collections.sort(result);
        List<String> codes3 = new ArrayList<String>();
        codes3.add("Please Select");
        for (int j = 0; j < result.size(); j++) {

            String  codes2= result.get(j);
            codes3.add(codes2);

        }

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, codes3);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_pdregisternumber.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        spin_pdregisternumber.setAdapter(adapter1);
    }

    private void get_grower_code() {

        List<String> codes = new ArrayList<>();



        if (isTableExists(dbGetMaster, "PdregistrationMaster")) {

            GetWareHouseDetails = dbGetMaster.rawQuery("select * from PdregistrationMaster", null);

            if (GetWareHouseDetails.moveToFirst()) {
                do {
                    codes.add(new String(GetWareHouseDetails.getString(10)));
                } while (GetWareHouseDetails.moveToNext());

            }
        }

        HashSet<String> set = new HashSet<>(codes);
        ArrayList<String> result = new ArrayList<>(set);
        Collections.sort(result);
        List<String> codes3 = new ArrayList<String>();
//        codes3.add("Please Select");
        for (int j = 0; j < result.size(); j++) {

            String  codes2= result.get(j);
            codes3.add(codes2);

        }
        Collections.reverse(codes3);
        codes3.add(0, "Please Select");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, codes3);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_growercode.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        spin_growercode.setAdapter(adapter1);
    }

    private void get_observation_type() {

        List<String> codes3 = new ArrayList<String>();
        codes3.add("Please Select");
        codes3.add("General Condition");
        codes3.add("Observation traits");
        codes3.add("Disease reaction");
        codes3.add("Conclusion");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, codes3);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_observationtype.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        spin_observationtype.setAdapter(adapter1);



    }


    private void get_hv_code_db(String cropCode, String breed_code) {
        if (isTableExists(dbGetMaster, "BreederMaster")) {

            GetWareHouseDetails = dbGetMaster.rawQuery("select * from BreederMaster WHERE CropCode='"+cropCode+"'and HVCode='"+breed_code+"'", null);

            if (GetWareHouseDetails.moveToFirst()) {
                do {
                    this.breed_code = new String(GetWareHouseDetails.getString(2));
//                    text_hv_code.setText(new String(GetWareHouseDetails.getString(3)));
                    Variety = new String(GetWareHouseDetails.getString(4));
                    text_variety.setText(new String(GetWareHouseDetails.getString(4)));
                } while (GetWareHouseDetails.moveToNext());
            }
        }
    }

    private void get_breeder_db(String cropCode) {

        List<String> codes = new ArrayList<>();

        if (isTableExists(dbGetMaster, "BreederMaster")) {

            GetWareHouseDetails = dbGetMaster.rawQuery("select * from BreederMaster WHERE CropCode='"+cropCode+"'", null);

            if (GetWareHouseDetails.moveToFirst()) {
                do {
                    codes.add(new String(GetWareHouseDetails.getString(3)));
                } while (GetWareHouseDetails.moveToNext());
            }
        }

        HashSet<String> set = new HashSet<>(codes);
        ArrayList<String> result = new ArrayList<>(set);
        Collections.sort(result);
        List<String> codes3 = new ArrayList<String>();
        codes3.add("Please Select");
        for (int j = 0; j < result.size(); j++) {

            String  codes2= result.get(j);
            codes3.add(codes2);

        }

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, codes3);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_breeder.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        spin_breeder.setAdapter(adapter1);
    }

    private String get_state_code(String s) {
        String ret_s = null;
        try {
            GetWareHouseDetails = dbGetMaster.rawQuery("select StateCode from StateMaster where StateName = '" + s + "'", null);
            if (GetWareHouseDetails.moveToFirst()) {
                ret_s = GetWareHouseDetails.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret_s;
    }

    private String get_crop_code(String s) {
        String ret_s = null;
        try {
            GetWareHouseDetails = dbGetMaster.rawQuery("select cropCode from PdregistrationMaster where pdRegNum = '" + s + "'", null);

            if (GetWareHouseDetails.moveToFirst()) {
                ret_s = GetWareHouseDetails.getString(0);
                Log.e("Values_cropcode",ret_s);
            }
            return ret_s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret_s;
    }

    public void onClick(View view) {

             general_click = 0;

            if (GrowerCode.contains("Select")) {
                Toast.makeText(this, "Please select the GrowerCode and try again", Toast.LENGTH_SHORT).show();
            }else if (PdRegisterNumber.contains("Select")){
                Toast.makeText(this, "Please select Pd registeration number and try again", Toast.LENGTH_SHORT).show();
            } else if(observation_type.contains("Select")) {

                Toast.makeText(this, "Please select observation type and try again", Toast.LENGTH_SHORT).show();
//                sessionsave.save_crop_data(CropName ,CropCode ,breed_code ,HV_code , Variety,Trial_stages ,State ,State_code ,City);
            }else{

                get_observation_data();
                dialog.setMessage("Loading\n Please wait .....");
                dialog.setCancelable(false);
                dialog.show();


            }
        }

    private void get_Pd_registrationnumber() {

        StringRequest strReq = new StringRequest(Request.Method.POST,Pd_RegistrationNumber , new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("create", "Response: " + response);
                if(response.contains("Success")||response.contains("ok")){
                    JSONObject jObj = null;
                    try {
                        jObj = new JSONObject(response);
                        String status = jObj.getString("status");
                        String msg = jObj.getString("msg");
                        JSONArray jsonArray = jObj.getJSONArray("RegNum");
                        for (int i =0;i<jsonArray.length();i++){
                            JSONObject fixObj = jsonArray.getJSONObject(i);
                            String  pdRegNum = fixObj.getString("pdRegNum");
                            String  growerCode = fixObj.getString("growerCode");
                            String  crop_status = fixObj.getString("status");
                            String  Season = fixObj.getString("Season");
                            String  cropCode = fixObj.getString("cropCode");
                            String  year = fixObj.getString("year");
                            String  hybrid = fixObj.getString("hybrid");
                            String  checkHybrid = fixObj.getString("checkHybrid");
                            String  pd_status = fixObj.getString("status");
                            Log.e("code---1--",pdRegNum);
                            Log.e("code---2--",growerCode);
                            Log.e("code---3--",crop_status);

                            String query = "INSERT INTO PdregistrationMaster (pdRegNum,growerCode,Season,cropCode,year,hybrid,status,check_hybrid) VALUES('" + pdRegNum + "','" + growerCode + "','" + Season + "','" + cropCode + "','" + year + "','" + hybrid + "','" + pd_status + "','" + checkHybrid + "');";
                            dbGetMaster.execSQL(query);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("create", "network Error: " + error.getMessage());


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("Company",sessionsave.get_company());
                params.put("Action","getCode");

                System.out.println("WWWWWWWWWWWWWW" + params);
                Log.d("Request", String.valueOf(params));
                return params;
            }

        };
        Log.d("result", String.valueOf(strReq));
        strReq.setRetryPolicy(new DefaultRetryPolicy(MAX_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq, "ScreenListAdapter");
    }

    private void get_observation_data_db(String cropCode) {
        if (dialog != null) {
            dialog.cancel();
        }
        String ret_s = null;
        GetWareHouseDetails = dbGetMaster.rawQuery("select * from ObservationMaster where CropCode = '"+cropCode+"'", null);

        if (GetWareHouseDetails.moveToFirst()) {
            ret_s = GetWareHouseDetails.getString(2);
        }

        if (ret_s == null){
            Toast.makeText(this, "No data available for this crop", Toast.LENGTH_SHORT).show();
        }else {


//            GetWareHouseDetails = dbGetMaster.rawQuery("select * from ObservationData WHERE pd_register_num ='"+ PdRegisterNumber +"'", null);
//            if(GetWareHouseDetails.getCount()==0)
//            {
//                Log.e("No values","in pd nu");
//                Intent myIntent = new Intent(cropselect.this, Observation.class);
//                myIntent.putExtra("crop_code", CropCode);
//                myIntent.putExtra("crop_name", CropName);
//                myIntent.putExtra("pd_status", pd_status);
//                myIntent.putExtra("grower_code", GrowerCode);
//                myIntent.putExtra("pd_number", PdRegisterNumber);
//                myIntent.putExtra("check_hybrid", check_hybrids);
//                myIntent.putExtra("observation_type", observation_type);
//                myIntent.putExtra("from", "crop_select");
//                startActivity(myIntent);
//                finish();
//            }else{
//                Status_list = new ArrayList<>();
//
//                if (GetWareHouseDetails.moveToFirst())
//                {
//
//
//                    while (!GetWareHouseDetails.isAfterLast()){
//                        status = GetWareHouseDetails.getString(7);
//                        Status_list.add(status);
//                        Log.e("values-status", Status_list.toString());
//                        GetWareHouseDetails.moveToNext();
//
//                    }
//
//                }
//            GetWareHouseDetails = dbGetMaster.rawQuery("select * from BreederMaster WHERE CropCode='"+cropCode+"'and HVCode='"+breed_code+"'", null);
            GetWareHouseDetails = dbGetMaster.rawQuery("select * from ObservationData WHERE pd_register_num ='"+ PdRegisterNumber +"'", null);
            if (GetWareHouseDetails.moveToFirst()) {
                pd_status_offline = GetWareHouseDetails.getString(19);
            }
            if(GetWareHouseDetails.getCount()==0){
                Log.e("values","jdfsdf");
                if(pd_status.equals("0")){
                    if(observation_type.contains("Observation")||observation_type.contains("Disease")||observation_type.contains("Conclusion")){
                        Toast.makeText(this, "Please add general condition first", Toast.LENGTH_SHORT).show();
                        if (dialog != null) {
                            dialog.cancel();
                        }
                    }else{
                        Intent myIntent = new Intent(cropselect.this, Observation.class);
                        myIntent.putExtra("crop_code", CropCode);
                        myIntent.putExtra("crop_name", CropName);
                        myIntent.putExtra("grower_code", GrowerCode);
                        myIntent.putExtra("pd_number", PdRegisterNumber);
                        myIntent.putExtra("pd_status", pd_status);
                        myIntent.putExtra("check_hybrid", check_hybrids);
                        myIntent.putExtra("observation_type", observation_type);
                        myIntent.putExtra("from", "crop_select");
                        startActivity(myIntent);
                        finish();
                    }
                } else if(pd_status.equals("1")||pd_status.equals("2")||pd_status.equals("3")){
                    if(observation_type.contains("General")){
                        Toast.makeText(this, "General condition already added", Toast.LENGTH_SHORT).show();
                        if (dialog != null) {
                            dialog.cancel();
                        }
                    }else{
                        Intent myIntent = new Intent(cropselect.this, Observation.class);
                        myIntent.putExtra("crop_code", CropCode);
                        myIntent.putExtra("crop_name", CropName);
                        myIntent.putExtra("grower_code", GrowerCode);
                        myIntent.putExtra("pd_number", PdRegisterNumber);
                        myIntent.putExtra("pd_status", pd_status);
                        myIntent.putExtra("check_hybrid", check_hybrids);
                        myIntent.putExtra("observation_type", observation_type);
                        myIntent.putExtra("from", "crop_select");
                        startActivity(myIntent);
                        finish();

                    }

                }
            }else{

                if(pd_status_offline != null && pd_status_offline.equals("0")){
                    if(observation_type.contains("Observation")||observation_type.contains("Disease")||observation_type.contains("Conclusion")){
                        Toast.makeText(this, "Please add general condition first", Toast.LENGTH_SHORT).show();
                        if (dialog != null) {
                            dialog.cancel();
                        }
                    }else{
                        Intent myIntent = new Intent(cropselect.this, Observation.class);
                        myIntent.putExtra("crop_code", CropCode);
                        myIntent.putExtra("crop_name", CropName);
                        myIntent.putExtra("grower_code", GrowerCode);
                        myIntent.putExtra("pd_number", PdRegisterNumber);
                        myIntent.putExtra("pd_status", pd_status_offline);
                        myIntent.putExtra("check_hybrid", check_hybrids);
                        myIntent.putExtra("observation_type", observation_type);
                        myIntent.putExtra("from", "crop_select");
                        startActivity(myIntent);
                        finish();
                    }
                } else if(pd_status_offline != null && pd_status_offline.equals("1")||pd_status_offline.equals("2")||pd_status_offline.equals("3")){
                    if(observation_type.contains("General")){
                        Toast.makeText(this, "General condition added", Toast.LENGTH_SHORT).show();
                        if (dialog != null) {
                            dialog.cancel();
                        }
                    }else{
                        Intent myIntent = new Intent(cropselect.this, Observation.class);
                        myIntent.putExtra("crop_code", CropCode);
                        myIntent.putExtra("crop_name", CropName);
                        myIntent.putExtra("grower_code", GrowerCode);
                        myIntent.putExtra("pd_number", PdRegisterNumber);
                        myIntent.putExtra("pd_status", pd_status_offline);
                        myIntent.putExtra("check_hybrid", check_hybrids);
                        myIntent.putExtra("observation_type", observation_type);
                        myIntent.putExtra("from", "crop_select");
                        startActivity(myIntent);
                        finish();

                    }

                }else {
                    Intent myIntent = new Intent(cropselect.this, Observation.class);
                    myIntent.putExtra("crop_code", CropCode);
                    myIntent.putExtra("crop_name", CropName);
                    myIntent.putExtra("grower_code", GrowerCode);
                    myIntent.putExtra("pd_number", PdRegisterNumber);
                    myIntent.putExtra("pd_status", pd_status_offline);
                    myIntent.putExtra("check_hybrid", check_hybrids);
                    myIntent.putExtra("observation_type", observation_type);
                    myIntent.putExtra("from", "crop_select");
                    startActivity(myIntent);
                    finish();

                }

            }

        }
    }

    private void get_observation_data() {
        if (alert.isNetworkAvailable()) {
            class GetDataJSON extends AsyncTask<String, Void, String> {

                @Override
                protected String doInBackground(String... params) {
                    HttpPost httppost = null;
                    DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());

                        try {
                            httppost = new HttpPost(Api.get_observation(URLEncoder.encode(CropCode, "UTF-8"),sessionsave.get_company()));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

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
                    Log.e(TAG ,"result "+result);
                    try {
                        spilt_data();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG ,"error "+e.toString());
                    }
                }
            }
            GetDataJSON g = new GetDataJSON();
            g.execute();
        }else {
            get_observation_data_db(CropCode);
        }
    }

    private void spilt_data() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);

            JSONArray gc = jsonObj.getJSONArray("gc");
            JSONArray obt = jsonObj.getJSONArray("obt");
//            JSONArray dr = jsonObj.getJSONArray("dr");
            JSONArray cn = jsonObj.getJSONArray("cn");

            if (gc.length() != 0 || obt.length() != 0 || cn.length() != 0){

                String ret_s = null;
                GetWareHouseDetails = dbGetMaster.rawQuery("select * from ObservationMaster where CropCode = '"+CropCode+"'", null);

                if (GetWareHouseDetails.moveToFirst()) {
                    ret_s = GetWareHouseDetails.getString(2);
                }

                if (ret_s == null){
                    Delete_create_GetMasterDatabase();
                    get_data(0);
                }

//                GetWareHouseDetails = dbGetMaster.rawQuery("select * from ObservationData WHERE pd_register_num ='"+ PdRegisterNumber +"'", null);
//                if(GetWareHouseDetails.getCount()==0){
//
//                    Intent myIntent = new Intent(cropselect.this, Observation.class);
//                    myIntent.putExtra("crop_code", CropCode);
//                    myIntent.putExtra("crop_name", CropName);
//                    myIntent.putExtra("pd_status", pd_status);
//                    myIntent.putExtra("grower_code", GrowerCode);
//                    myIntent.putExtra("pd_number", PdRegisterNumber);
//                    myIntent.putExtra("check_hybrid", check_hybrids);
//                    myIntent.putExtra("observation_type", observation_type);
//                    myIntent.putExtra("from", "crop_select");
//                    startActivity(myIntent);
//                    finish();
//
//                }else{
//                    Status_list = new ArrayList<>();
//                    if (GetWareHouseDetails.moveToFirst())
//                    {
//                        while (!GetWareHouseDetails.isAfterLast()){
//                            status = GetWareHouseDetails.getString(7);
//                            Status_list.add(status);
//                            Log.e("values-status", Status_list.toString());
//                            GetWareHouseDetails.moveToNext();
//
//                        }
//
//                    }
//
//                    if (Status_list.contains("conclusion")) {
//                        Toast.makeText(this, "Values already added", Toast.LENGTH_SHORT).show();
//                    } else

                if(pd_status.equals("0")){
                    if(observation_type.contains("Observation")||observation_type.contains("Disease")||observation_type.contains("Conclusion")){
                        Toast.makeText(this, "Please add general condition first", Toast.LENGTH_SHORT).show();
                        if (dialog != null) {
                            dialog.cancel();
                        }
                    }else{
                        Intent myIntent = new Intent(cropselect.this, Observation.class);
                        myIntent.putExtra("crop_code", CropCode);
                        myIntent.putExtra("crop_name", CropName);
                        myIntent.putExtra("grower_code", GrowerCode);
                        myIntent.putExtra("pd_number", PdRegisterNumber);
                        myIntent.putExtra("pd_status", pd_status);
                        myIntent.putExtra("check_hybrid", check_hybrids);
                        myIntent.putExtra("observation_type", observation_type);
                        myIntent.putExtra("from", "crop_select");
                        startActivity(myIntent);
                        finish();
                    }
                } else if(pd_status.equals("1")||pd_status.equals("2")||pd_status.equals("3")){
                        if(observation_type.contains("General")){
                            Toast.makeText(this, "General condition already added", Toast.LENGTH_SHORT).show();
                            if (dialog != null) {
                                dialog.cancel();
                            }
                        }else{
                            Intent myIntent = new Intent(cropselect.this, Observation.class);
                            myIntent.putExtra("crop_code", CropCode);
                            myIntent.putExtra("crop_name", CropName);
                            myIntent.putExtra("grower_code", GrowerCode);
                            myIntent.putExtra("pd_number", PdRegisterNumber);
                            myIntent.putExtra("pd_status", pd_status);
                            myIntent.putExtra("check_hybrid", check_hybrids);
                            myIntent.putExtra("observation_type", observation_type);
                            myIntent.putExtra("from", "crop_select");
                            startActivity(myIntent);
                            finish();

                        }

                    }else {
                        Intent myIntent = new Intent(cropselect.this, Observation.class);
                        myIntent.putExtra("crop_code", CropCode);
                        myIntent.putExtra("crop_name", CropName);
                        myIntent.putExtra("grower_code", GrowerCode);
                        myIntent.putExtra("pd_number", PdRegisterNumber);
                        myIntent.putExtra("pd_status", pd_status);
                        myIntent.putExtra("check_hybrid", check_hybrids);
                        myIntent.putExtra("observation_type", observation_type);
                        myIntent.putExtra("from", "crop_select");
                        startActivity(myIntent);
                        finish();

                    }

//                }

            }else {
                Toast.makeText(this, "No data available for this crop", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "No data available for this crop", Toast.LENGTH_SHORT).show();
        }
    }

    private void get_data(final int i) {
        if (alert.isNetworkAvailable()) {
            @SuppressLint("StaticFieldLeak")
            class GetDataJSON extends AsyncTask<String, Void, String> {

                @Override
                protected String doInBackground(String... params) {
                    HttpPost httppost = null;
                    DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());

                    if (i == 0) {
                        Log.e(TAG ,Api.get_observation(sessionsave.get_company()));
                        httppost = new HttpPost(Api.get_observation(sessionsave.get_company()));
                    }else if (i == 1) {
                        Log.e(TAG ,Api.get_breed(sessionsave.get_company()));
                        httppost = new HttpPost(Api.get_breed(sessionsave.get_company()));
//                        Log.e(TAG ,Api.get_hv_codes(sessionsave.get_company()));
//                        httppost = new HttpPost(Api.get_hv_codes(sessionsave.get_company()));
                    }

                    // Depends on your web service
//                    httppost.setHeader("Content-type", "application/json");

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
                    try {
                        Log.e(TAG ,i+":"+result);
                        save_database(result ,i);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            GetDataJSON g = new GetDataJSON();
            g.execute();
        }
    }

    private void save_database(String result ,int i) {
        if (i == 0) {
            save_observation_master(result);
            get_data(1);
        }else if (i == 1){
            save_breed(result);
//            get_data(2);
        }
    }

    private void save_observation_master(String ob_data) {
        try {
            JSONObject jsonObj = new JSONObject(ob_data);

            Iterator<String> iter = jsonObj.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    Object value = jsonObj.get(key);
                    Log.e(TAG , "key: "+key+" Value "+String.valueOf(value));
                    String query = "INSERT INTO ObservationMaster(CropCode,CropData) VALUES('" + key + "','" + value + "');";
                    dbGetMaster.execSQL(query);
                } catch (JSONException e) {
                    // Something went wrong!
                    Log.e(TAG ,"ob error "+e.toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG ,"error "+e.toString());
        }
    }

    private void save_breed(String result) {
        try {
            JSONArray jsonArray = new JSONArray(result);


            for (int i = 0 ; i < jsonArray.length() ; i++){
                JSONObject object = jsonArray.getJSONObject(i);

                String A = object.getString(Api.T2);
                String B = object.getString(Api.T3);
                String C = object.getString(Api.T4);
                String D = object.getString(Api.T5);

                String query = "INSERT INTO BreederMaster(CropCode,BreedCode,HVCode,Variety) VALUES('" + A + "','" + B + "','" + C + "','" + D + "');";
                dbGetMaster.execSQL(query);
            }
            get_data(2);
        }catch (Exception e){
            Log.e(TAG ,"error "+e.toString());
        }
    }

    private void Delete_create_GetMasterDatabase() {
        String DATABASE_TABLE_ObservationMaster="ObservationMaster";
        dbGetMaster.execSQL("DROP TABLE IF EXISTS '" + DATABASE_TABLE_ObservationMaster + "'");
        String DATABASE_TABLE_BreederMaster="BreederMaster";
        dbGetMaster.execSQL("DROP TABLE IF EXISTS '" + DATABASE_TABLE_BreederMaster + "'");

        dbGetMaster.execSQL("CREATE TABLE IF NOT EXISTS ObservationMaster(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, CropCode VARCHAR ,CropData VARCHAR);");
        dbGetMaster.execSQL("CREATE TABLE IF NOT EXISTS BreederMaster(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, CropCode VARCHAR ,BreedCode VARCHAR ,HVCode VARCHAR ,Variety VARCHAR);");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        get_crop_data();

//        text_hv_code.setText("");
        text_variety.setText("");
        spin_trial.setSelection(0);
        spin_states.setSelection(0);
        edit_city.setText("");

//        get_crop_name_db();
        get_states_db();

    }

    private void get_states_db() {
        List<String> codes = new ArrayList<>();

        if (isTableExists(dbGetMaster, "StateMaster")) {

            GetWareHouseDetails = dbGetMaster.rawQuery("select * from StateMaster", null);

            if (GetWareHouseDetails.moveToFirst()) {
                do {
                    codes.add(new String(GetWareHouseDetails.getString(2)));
                } while (GetWareHouseDetails.moveToNext());
            }
        }

        HashSet<String> set = new HashSet<>(codes);
        ArrayList<String> result = new ArrayList<>(set);
        Collections.sort(result);
        List<String> codes3 = new ArrayList<String>();
        codes3.add("Please Select");
        for (int j = 0; j < result.size(); j++) {

            String  codes2= result.get(j);
            codes3.add(codes2);

        }

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, codes3);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_states.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        spin_states.setAdapter(adapter1);
    }

    private String get_crop_name_db(String s) {
        String ret_s = null;
        try {
            GetWareHouseDetails = dbGetMaster.rawQuery("select Name from CropMaster where CropCode = '" + s + "'", null);

            if (GetWareHouseDetails.moveToFirst()) {
                ret_s = GetWareHouseDetails.getString(0);
                Log.e("Values_cropcode",ret_s);
            }
            return ret_s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret_s;
    }

//    private void get_crop_data() {
//        if (alert.isNetworkAvailable()) {
//            class GetDataJSON extends AsyncTask<String, Void, String> {
//
//                @Override
//                protected String doInBackground(String... params) {
//                    DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
////                    HttpPost httppost = new HttpPost(Api.crop_master("SalesIndentCotton"));
//                    HttpPost httppost = new HttpPost("http://103.44.97.110:8080/kanagaraj/"+sessionsave.get_folder()+"/ISCropMaster.php");
//
//                    // Depends on your web service
//                    httppost.setHeader("Content-type", "application/json");
//
//                    InputStream inputStream = null;
//                    String result = null;
//                    try {
//                        HttpResponse response = httpclient.execute(httppost);
//                        HttpEntity entity = response.getEntity();
//
//                        inputStream = entity.getContent();
//                        // json is UTF-8 by default
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
//                        StringBuilder sb = new StringBuilder();
//
//                        String line = null;
//                        while ((line = reader.readLine()) != null) {
//                            sb.append(line + "\n");
//                        }
//                        result = sb.toString();
//                    } catch (Exception e) {
//                        // Oops
//                    } finally {
//                        try {
//                            if (inputStream != null) inputStream.close();
//                        } catch (Exception squish) {
//                        }
//                    }
//                    return result;
//                }
//
//                @Override
//                protected void onPostExecute(String result) {
//                    myJSON = result;
//                    try {
//                        showList3();
//                    } catch (Exception e) {
//                        Log.e(TAG ,"error "+e.toString());
//                    }
//                }
//            }
//            GetDataJSON g = new GetDataJSON();
//            g.execute();
//        }
//    }

//    private void showList3() {
//        try {
//            JSONObject jsonObj = new JSONObject(myJSON);
//            peoples = jsonObj.getJSONArray(TAG_RESULTS);
//
//            for (int i = 0; i < peoples.length(); i++) {
//                JSONObject c = peoples.getJSONObject(i);
//
//                String Id = c.getString(Api.T1);
//                String A = c.getString(Api.T2);
//                String B = c.getString(Api.T3);
//                 /* String C = c.getString(Api.T4);
//                String D = c.getString(Api.T5);
//                String E = c.getString(Api.T6);
//                String F = c.getString(Api.T7);
//                String G = c.getString(Api.T8);
//                String H = c.getString(Api.T9);
//                String I = c.getString(Api.T10);
//                String J = c.getString(Api.T11);
//                String K = c.getString(Api.T12);
//                String L = c.getString(Api.T13);
//                String M = c.getString(Api.T14);
//                String N = c.getString(Api.T15);
//                String O = c.getString(Api.T16);
//                String P = c.getString(Api.T17);
//                String Q = c.getString(Api.T18);
//                String R = c.getString(Api.T19);
//                String S = c.getString(T20);
//                String T = c.getString(T21);
//                String U = c.getString(T22);
//                String V = c.getString(T23);
//                String W = c.getString(T24);
//                String X = c.getString(T25);
//                String Y = c.getString(T26);
//                String Z = c.getString(T27);*/
//
//                String query = "INSERT INTO CropMaster (CropCode,Name,CropType ) VALUES('" + Id + "','" + A + "','" + B + "');";
//                dbGetMaster.execSQL(query);
//            }
//            set_crop_name();
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "No Data Available", Toast.LENGTH_LONG).show();
//
//        }
//    }

//    public void set_crop_name() {
//
//        List<String> codes = new ArrayList<>();
//
//        if (isTableExists(dbGetMaster, "CropMaster")) {
//
//            GetWareHouseDetails = dbGetMaster.rawQuery("select * from CropMaster", null);
//
//            if (GetWareHouseDetails.moveToFirst()) {
//                do {
//                    if ("VEG".equals(GetWareHouseDetails.getString(3))) {
//                        codes.add(new String(GetWareHouseDetails.getString(2)));
//                    }
//                } while (GetWareHouseDetails.moveToNext());
//            }
//        }
//
//        HashSet<String> set = new HashSet<>(codes);
//        ArrayList<String> result = new ArrayList<>(set);
//        Collections.sort(result);
//        List<String> codes3 = new ArrayList<String>();
//        codes3.add("Please Select");
//        for (int j = 0; j < result.size(); j++) {
//
//            String  codes2= result.get(j);
//            codes3.add(codes2);
//
//        }
//
//        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, codes3);
//        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        cropName.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
//        cropName.setAdapter(adapter1);
//    }

    boolean isTableExists(SQLiteDatabase db, String tableName) {
        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        try {
            Cursor cursor = db.rawQuery("select * from  "+tableName+" ", null);
            if (!cursor.moveToFirst())
            {
                cursor.close();
                return false;
            }
            int count = cursor.getInt(0);
            cursor.close();
            return count > 0;
        }catch (Exception e){
            return false;
        }
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

//    private  void Delete_create_GetMasterDatabase() {
//
//        String DATABASE_TABLE_CropMaster="CropMaster";
//        dbGetMaster.execSQL("DROP TABLE IF EXISTS '" + DATABASE_TABLE_CropMaster + "'");
//        String DATABASE_TABLE_StateMaster="StateMaster";
//        dbGetMaster.execSQL("DROP TABLE IF EXISTS '" + DATABASE_TABLE_StateMaster + "'");
//
//        dbGetMaster.execSQL("CREATE TABLE IF NOT EXISTS CropMaster(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, CropCode VARCHAR,Name VARCHAR,CropType VARCHAR);");
//        dbGetMaster.execSQL("CREATE TABLE IF NOT EXISTS StateMaster(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, StateCode VARCHAR,StateName VARCHAR);");
//
//    }

//    private void get_breeder(final String cropCode) {
//        if (alert.isNetworkAvailable()) {
//            class GetDataJSON extends AsyncTask<String, Void, String> {
//
//                @Override
//                protected String doInBackground(String... params) {
//                    DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
//                    HttpPost httppost = new HttpPost(Api.get_breed(cropCode ,sessionsave.get_company()));
//
//                    // Depends on your web service
//                    httppost.setHeader("Content-type", "application/json");
//
//                    InputStream inputStream = null;
//                    String result = null;
//                    try {
//                        HttpResponse response = httpclient.execute(httppost);
//                        HttpEntity entity = response.getEntity();
//
//                        inputStream = entity.getContent();
//                        // json is UTF-8 by default
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
//                        StringBuilder sb = new StringBuilder();
//
//                        String line = null;
//                        while ((line = reader.readLine()) != null) {
//                            sb.append(line + "\n");
//                        }
//                        result = sb.toString();
//                    } catch (Exception e) {
//                        // Oops
//                    } finally {
//                        try {
//                            if (inputStream != null) inputStream.close();
//                        } catch (Exception squish) {
//                        }
//                    }
//                    return result;
//                }
//
//                @Override
//                protected void onPostExecute(String result) {
//                    myJSON = result;
//                    try {
//                        show_breed();
//                    } catch (Exception e) {
//                        Log.e(TAG ,"error "+e.toString());
//                    }
//                }
//            }
//            GetDataJSON g = new GetDataJSON();
//            g.execute();
//        }
//    }

//    private void show_breed() {
//        list_breed = new ArrayList<>();
//        try {
//            JSONArray jsonObj = new JSONArray(myJSON);
//            list_breed.add("Please Select");
//            for (int i = 0 ; i < jsonObj.length() ; i++){
//                list_breed.add(String.valueOf(jsonObj.get(i)));
//            }
//            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_breed);
//            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spin_breeder.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
//            spin_breeder.setAdapter(adapter1);
//        } catch (JSONException e) {
//            Toast.makeText(this, "No breeder data available for this crop", Toast.LENGTH_SHORT).show();
//        }
//    }

//    private void get_hv_code(final String cropCode,final String breed_code) {
//        if (alert.isNetworkAvailable()) {
//            class GetDataJSON extends AsyncTask<String, Void, String> {
//
//                @Override
//                protected String doInBackground(String... params) {
//                    DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
//                    HttpPost httppost = new HttpPost(Api.get_hv_code(cropCode ,sessionsave.get_company(),URLEncoder.encode(breed_code)));
//
//                    // Depends on your web service
//                    httppost.setHeader("Content-type", "application/json");
//
//                    InputStream inputStream = null;
//                    String result = null;
//                    try {
//                        HttpResponse response = httpclient.execute(httppost);
//                        HttpEntity entity = response.getEntity();
//
//                        inputStream = entity.getContent();
//                        // json is UTF-8 by default
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
//                        StringBuilder sb = new StringBuilder();
//
//                        String line = null;
//                        while ((line = reader.readLine()) != null) {
//                            sb.append(line + "\n");
//                        }
//                        result = sb.toString();
//                    } catch (Exception e) {
//                        // Oops
//                    } finally {
//                        try {
//                            if (inputStream != null) inputStream.close();
//                        } catch (Exception squish) {
//                        }
//                    }
//                    return result;
//                }
//
//                @Override
//                protected void onPostExecute(String result) {
//                    myJSON = result;
//                    try {
//                        show_hv_code(myJSON);
//                    } catch (Exception e) {
//                        Log.e(TAG ,"error "+e.toString());
//                    }
//                }
//            }
//            GetDataJSON g = new GetDataJSON();
//            g.execute();
//        }
//    }

//    private void show_hv_code(String myJSON) {
//        if (myJSON != null){
//            String[] data = myJSON.split("\\|");
//            HV_code = data[0];
//            Variety = data[1];
//            text_hv_code.setText(data[0]);
//            text_variety.setText(data[1]);
//        }
//    }


//    private void get_states() {
//        if (alert.isNetworkAvailable()) {
//            class GetDataJSON extends AsyncTask<String, Void, String> {
//
//                @Override
//                protected String doInBackground(String... params) {
//                    DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
////                    HttpPost httppost = new HttpPost(Api.get_state(sessionsave.get_company()));
//                    HttpPost httppost = new HttpPost("http://103.44.97.110:8080/kanagaraj/pd_trail/StateMaster.php?Company="+sessionsave.get_company());
//
//                    // Depends on your web service
//                    httppost.setHeader("Content-type", "application/json");
//
//                    InputStream inputStream = null;
//                    String result = null;
//                    try {
//                        HttpResponse response = httpclient.execute(httppost);
//                        HttpEntity entity = response.getEntity();
//
//                        inputStream = entity.getContent();
//                        // json is UTF-8 by default
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
//                        StringBuilder sb = new StringBuilder();
//
//                        String line = null;
//                        while ((line = reader.readLine()) != null) {
//                            sb.append(line + "\n");
//                        }
//                        result = sb.toString();
//                    } catch (Exception e) {
//                        // Oops
//                    } finally {
//                        try {
//                            if (inputStream != null) inputStream.close();
//                        } catch (Exception squish) {
//                        }
//                    }
//                    return result;
//                }
//
//                @Override
//                protected void onPostExecute(String result) {
//                    myJSON = result;
//                    try {
//                        set_state();
//                    } catch (Exception e) {
//                        Log.e(TAG ,"error "+e.toString());
//                    }
//                }
//            }
//            GetDataJSON g = new GetDataJSON();
//            g.execute();
//        }
//    }

//    private void set_state() {
//        try {
//            JSONArray jsonObj = new JSONArray(myJSON);
//
//            for (int i = 0; i < jsonObj.length(); i++) {
//                JSONObject c = jsonObj.getJSONObject(i);
//
//                String c_val = c.getString("cval");
//                String c_desc = c.getString("cdesc");
////                 /* String B = c.getString(Api.T3);
////                String C = c.getString(Api.T4);
////                String D = c.getString(Api.T5);
////                String E = c.getString(Api.T6);
////                String F = c.getString(Api.T7);
////                String G = c.getString(Api.T8);
////                String H = c.getString(Api.T9);
////                String I = c.getString(Api.T10);
////                String J = c.getString(Api.T11);
////                String K = c.getString(Api.T12);
////                String L = c.getString(Api.T13);
////                String M = c.getString(Api.T14);
////                String N = c.getString(Api.T15);
////                String O = c.getString(Api.T16);
////                String P = c.getString(Api.T17);
////                String Q = c.getString(Api.T18);
////                String R = c.getString(Api.T19);
////                String S = c.getString(T20);
////                String T = c.getString(T21);
////                String U = c.getString(T22);
////                String V = c.getString(T23);
////                String W = c.getString(T24);
////                String X = c.getString(T25);
////                String Y = c.getString(T26);
////                String Z = c.getString(T27);*/
//
//                String query = "INSERT INTO StateMaster (StateCode,StateName) VALUES('" + c_val + "','" + c_desc + "');";
//                dbGetMaster.execSQL(query);
//            }
//            set_state_name();
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "No Data Available", Toast.LENGTH_LONG).show();
//
//        }
//    }

//    private void set_state_name() {
//        List<String> codes = new ArrayList<>();
//
//        if (isTableExists(dbGetMaster, "StateMaster")) {
//
//            GetWareHouseDetails = dbGetMaster.rawQuery("select * from StateMaster", null);
//
//            if (GetWareHouseDetails.moveToFirst()) {
//                do {
//                    codes.add(new String(GetWareHouseDetails.getString(2)));
//                } while (GetWareHouseDetails.moveToNext());
//            }
//        }
//
//        HashSet<String> set = new HashSet<>(codes);
//        ArrayList<String> result = new ArrayList<>(set);
//        Collections.sort(result);
//        List<String> codes3 = new ArrayList<String>();
//        codes3.add("Please Select");
//        for (int j = 0; j < result.size(); j++) {
//
//            String  codes2= result.get(j);
//            codes3.add(codes2);
//
//        }
//
//        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, codes3);
//        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spin_states.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
//        spin_states.setAdapter(adapter1);
//    }

}

