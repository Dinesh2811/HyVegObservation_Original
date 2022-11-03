package com.example.manoj.hyveg_observation.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Environment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.androidbuts.multispinnerfilter.SpinnerListener;
import com.example.manoj.hyveg_observation.Others.AppController;
import com.example.manoj.hyveg_observation.Others.GPSTracker;
import com.example.manoj.hyveg_observation.R;
import com.example.manoj.hyveg_observation.services.alertDialog;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.example.manoj.hyveg_observation.Api.Pdtrial_formregistration;
import static com.example.manoj.hyveg_observation.Api.save_growermaster;
import static com.example.manoj.hyveg_observation.activity.create_master.MAX_TIME;

public class Pdtrial_fieldregister extends AppCompatActivity implements View.OnClickListener {


    //Service
    alertDialog alert;
    //    JsonServiceHelper serviceHelper;
    Sessionsave sessionsave;
    private SQLiteDatabase dbGetMaster;
    private Cursor GetWareHouseDetails;
    File Oroot = Environment.getExternalStorageDirectory() ,GetMasterPath=null;
    String tag_string_req = "req_login", strAdd;

    MultiAutoCompleteTextView multiAutoCompleteTextView;
    MultiSpinnerSearch searchMultiSpinnerLimit;

    // GPSTracker class
    GPSTracker gps;
    double latitude, longitude;

    List<String> grower_names = new ArrayList<>();
    List<String> grower_codes = new ArrayList<>();
    List<String> grower_codes_names = new ArrayList<>();

    TextView text_hvcode;

    Spinner spin_grower,spin_year, spin_season,spin_crop,spin_hybrid,spin_chkhybrids;
    String grower_code = "", year = "",pdyear = "",season="",CropName,CropCode = "",hybrids="",check_hybrids="",grower = "";
    Button Pdregister_form;
    String Hybrid_names,hybrid_chk;


    List<String> codes_hyb = new ArrayList<String>();
    List<KeyPairBoolData> codes3;



    List<KeyPairBoolData> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdtrial_fieldregister);

        //Path for the database
        if (Oroot.canWrite()) {
            File dir = new File(Oroot.getAbsolutePath() + "/Android/Observation");
            dir.mkdirs();

            GetMasterPath = new File(dir, "GetMasterDB.db");

        }

        dbGetMaster = openOrCreateDatabase(GetMasterPath.getPath(), Context.MODE_PRIVATE, null);
        alert = new alertDialog(this);
        sessionsave = new Sessionsave(this);

        spin_grower = findViewById(R.id.spin_grower);
        spin_year = findViewById(R.id.spin_year);
        spin_season = findViewById(R.id.spin_season);
        spin_crop = findViewById(R.id.spin_crop);
        spin_hybrid = findViewById(R.id.spin_hybrid);
        multiAutoCompleteTextView = (MultiAutoCompleteTextView)findViewById(R.id.multiAutoCompleteTextViewEmail);
        searchMultiSpinnerLimit =findViewById(R.id.searchMultiSpinnerLimit);
        Pdregister_form = findViewById(R.id.Pdregister_form);
        text_hvcode = findViewById(R.id.text_hvcode);

        Pdregister_form.setOnClickListener(this);

        // Set multiAutoCompleteTextView related attribute value in java code.
        multiAutoCompleteTextView.setPadding(15,15,15,15);
        multiAutoCompleteTextView.setTextColor(getResources().getColor(R.color.colorWhite));




        get_growerdata();
        get_crop_data();
        getseason_data();
        getyear_data();


        spin_grower.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                grower = spin_grower.getSelectedItem().toString();
                Log.e("values---grower",grower);
                String[] parts = grower.split("-");
                String part1 = parts[0];
//                String part2 = parts[1];
//                grower_code = grower_codes.get(arg2);
                grower_code = part1;

                Log.e("values------",grower_code);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        spin_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                year = spin_year.getSelectedItem().toString();
                pdyear = get_pd_year(spin_year.getSelectedItem().toString());
                Log.e("values-----cname",year);


            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        spin_season.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                season = spin_season.getSelectedItem().toString();
                Log.e("values-----cname",season);


            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spin_crop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                CropName = spin_crop.getSelectedItem().toString();
                CropCode = get_crop_code(spin_crop.getSelectedItem().toString());
                Log.e("values-----cname",CropName);
                text_hvcode.setText("Please Select Min(1) or Max(10) in checkhybrids");
//                codes3.clear();
                try{
                    codes_hyb.clear();
                }catch (Exception e){
                    e.printStackTrace();
                }



            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        spin_hybrid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                hybrids = spin_hybrid.getSelectedItem().toString();
                Log.e("values-----cname",hybrids);




            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
//        spin_chkhybrids.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            public void onItemSelected(AdapterView<?> arg0, View arg1,
//                                       int arg2, long arg3) {
//
//                check_hybrids = spin_chkhybrids.getSelectedItem().toString();
//                Log.e("values-----cname",check_hybrids);
//
//
//            }
//
//            public void onNothingSelected(AdapterView<?> arg0) {
//            }
//        });




    }

    private String get_pd_year(String toString) {

        String ret_s = null;
        try {
            GetWareHouseDetails = dbGetMaster.rawQuery("select PdYear from YearMaster where YearCode = '" + toString + "'", null);

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

    private void get_hybrid_data(String cropCode) {

        final List<String> codes = new ArrayList<>();
        List<String> codes_chk1 = new ArrayList<>();
        List<String> codes_chk = new ArrayList<>();

//        final List<String> list = Arrays.asList(getResources().getStringArray(R.array.sports_array));


        if (isTableExists(dbGetMaster, "HybridMaster")) {


            GetWareHouseDetails = dbGetMaster.rawQuery("select codes_chk from HybridMaster where CropCode = '" + cropCode + "'", null);

            if (GetWareHouseDetails.moveToFirst()) {
                do {

                    codes_chk.add(new String(GetWareHouseDetails.getString(0)));
                    Log.d("vales----898else",(codes_chk.toString()));

                } while (GetWareHouseDetails.moveToNext());

            }
        }

        if (isTableExists(dbGetMaster, "HybridMaster")) {

            GetWareHouseDetails = dbGetMaster.rawQuery("select codes_chk from HybridMaster where CropCode = '" + cropCode + "'", null);

            if (GetWareHouseDetails.moveToFirst()) {

                do {
                    codes.add(new String(GetWareHouseDetails.getString(0)));
                    Log.d("vales----898else",(codes_chk.toString()));

                } while (GetWareHouseDetails.moveToNext());

            }
//            Log.d("vales----898else",(codes.toString()));
            HashSet<String> listToSet = new HashSet<String>(codes);

            List<String> listWithoutDuplicates_codes = new ArrayList<String>(listToSet);
            Log.d("vales----898else",(listWithoutDuplicates_codes.toString()));

        }

        HashSet<String> set = new HashSet<>(codes);
        ArrayList<String> result = new ArrayList<>(set);
        Collections.sort(result);
//        List<String> codes3 = new ArrayList<String>();
        codes3 = new ArrayList<>();

//        for (int j = 0; j < result.size(); j++) {
//
//            String  codes2= result.get(j);
//            codes3.add(codes2);
//
//        }
        Log.e("result", String.valueOf(result));

        for (int i = 0; i < result.size(); i++) {

            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(result.get(i));
            h.setSelected(false);
            codes3.add(h);


        }

//        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, codes3);
//        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spin_hybrid.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
//        spin_hybridbrid.setAdapter(adapter1);


        try {
            searchMultiSpinnerLimit.setItems(codes3, -1, new SpinnerListener() {

                @Override
                public void onItemsSelected(List<KeyPairBoolData> items) {


                    for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).isSelected()) {
                            Log.i("TAG", i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                            Hybrid_names = items.get(i).getName();




//                        for(int j =0;j<codes.size();j++){
//                            String name =codes.get(i);
//                            if(codes.size()==1) {
//                                text_hvcode.setText(codes.get(i));
//                            }else{
//                                text_hvcode.setText(name,codes.get(i));
//                            }
//                        }
//                            Log.e("hybrid_names", Hybrid_names);


                        }



                    }


                    Log.e("values----spine",searchMultiSpinnerLimit.getSelectedItem().toString() );

//                    codes_hyb.add(searchMultiSpinnerLimit.getSelectedItem().toString());

                   String hv_values = searchMultiSpinnerLimit.getSelectedItem().toString();
                    String[] parts = hv_values.split(",");
                    Log.e("values----spine-str-", parts.toString());
                    codes_hyb = new ArrayList<String>(Arrays.asList(parts));

                    Log.e("values----spine--", String.valueOf(codes_hyb.size()));

                    if(codes_hyb.size()>10){
                        Toast.makeText(getApplicationContext(),
                                "Limit exceed ", Toast.LENGTH_LONG).show();
                        text_hvcode.setText("HV code selection is exceed by 10");
                    }else{
                        text_hvcode.setText(searchMultiSpinnerLimit.getSelectedItem().toString());
                    }
                }






            });


//            searchMultiSpinnerLimit.setLimit(10, new MultiSpinnerSearch.LimitExceedListener() {
//                @Override
//                public void onLimitListener(KeyPairBoolData data) {
//                    Log.e("values------key", String.valueOf(data.isSelected()));
//                    Toast.makeText(getApplicationContext(),
//                            "Limit exceed ", Toast.LENGTH_LONG).show();
//                }
//            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getyear_data() {
        List<String> codes = new ArrayList<>();

        if (isTableExists(dbGetMaster, "YearMaster")) {

            GetWareHouseDetails = dbGetMaster.rawQuery("select * from YearMaster", null);

            if (GetWareHouseDetails.moveToFirst()) {
                do {
                    codes.add(new String(GetWareHouseDetails.getString(1)));
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
        spin_year.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        spin_year.setAdapter(adapter1);
    }

    private void getseason_data() {

        List<String> codes = new ArrayList<>();

        if (isTableExists(dbGetMaster, "SeasonMaster")) {

            GetWareHouseDetails = dbGetMaster.rawQuery("select * from SeasonMaster", null);

            if (GetWareHouseDetails.moveToFirst()) {
                do {
                    codes.add(new String(GetWareHouseDetails.getString(1)));
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
        spin_season.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        spin_season.setAdapter(adapter1);
    }

    private String get_crop_code(String s) {
        String ret_s = null;
        try {
            GetWareHouseDetails = dbGetMaster.rawQuery("select CropCode from CropMaster where Name = '" + s + "'", null);

            if (GetWareHouseDetails.moveToFirst()) {
                ret_s = GetWareHouseDetails.getString(0);
                Log.e("values-----code",ret_s);

                get_hybrid_data(ret_s);
                get_checkhybrid(ret_s);
            }
            return ret_s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret_s;
    }

    private void get_checkhybrid(String ret_s) {

        List<String> codes = new ArrayList<>();

        if (isTableExists(dbGetMaster, "HybridMaster")) {

            GetWareHouseDetails = dbGetMaster.rawQuery("select hvcode from HybridMaster where CropCode = '" + ret_s + "'AND  check_hyb = '" + 1 + "'" , null);
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


        // Create a new data adapter object.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, codes3);

        // Connect the data source with AutoCompleteTextView through adapter.
        multiAutoCompleteTextView.setAdapter(arrayAdapter);

        // Must set tokenizer for MultiAutoCompleteTextView object, otherwise it will not take effect.
        multiAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

    }

    private void get_crop_data() {
        List<String> codes = new ArrayList<>();

        if (isTableExists(dbGetMaster, "CropMaster")) {

            GetWareHouseDetails = dbGetMaster.rawQuery("select * from CropMaster", null);

            if (GetWareHouseDetails.moveToFirst()) {
                do {
                    if ("VEG".equals(GetWareHouseDetails.getString(3))) {
                        codes.add(new String(GetWareHouseDetails.getString(2)));
                    }
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
        spin_crop.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        spin_crop.setAdapter(adapter1);
    }

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

    private void get_growerdata() {

        StringRequest strReq = new StringRequest(Request.Method.POST,save_growermaster , new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("create", "Response: " + response);

                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");
                    String msg = jObj.getString("msg");
                    JSONArray jsonArray = jObj.getJSONArray("grower");
                    for (int i =0;i<jsonArray.length();i++){
                        JSONObject fixObj = jsonArray.getJSONObject(i);
                        String  code = fixObj.getString("code");
                        String  name = fixObj.getString("name");
                        Log.e("code",code);

                        grower_names.add(name);
                        grower_codes.add(code);
                        String grower_code_na = code+" "+"-"+" "+name;
                        grower_codes_names.add(grower_code_na);

                        getgrowername();


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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
                params.put("Action","getGrower");

                System.out.println("WWWWWWWWWWWWWW" + params);
                Log.d("Request", String.valueOf(params));
                return params;
            }

        };
        Log.d("result", String.valueOf(strReq));
        strReq.setRetryPolicy(new DefaultRetryPolicy(MAX_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void getgrowername() {

        HashSet<String> set = new HashSet<>(grower_codes_names);
        ArrayList<String> result = new ArrayList<>(set);
        Collections.sort(result);
        List<String> codes3 = new ArrayList<String>();
        for (int j = 0; j < result.size(); j++) {

            String  codes2= result.get(j);
            codes3.add(codes2);

        }
        Collections.reverse(codes3);
        codes3.add(0, "Please Select");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, codes3);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_grower.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        spin_grower.setAdapter(adapter1);
    }

    @Override
    public void onClick(View view) {

        check_hybrids = text_hvcode.getText().toString();
        String[] items = check_hybrids.split(",");
        System.out.println("No of items::"+items.length);
        Log.e("values",check_hybrids);

        try {
            if (grower.contains("Select")) {
                Toast.makeText(this, "Please Select the grower and try again", Toast.LENGTH_SHORT).show();
            }else if (year.contains("Select")){
                Toast.makeText(this, "Please Select the year code and try again", Toast.LENGTH_SHORT).show();
            }else if (season.contains("Select")){
                Toast.makeText(this, "Please Select the season stages and try again", Toast.LENGTH_SHORT).show();
            }else if (CropName.contains("Select")){
                Toast.makeText(this, "Please Select the crop and try again", Toast.LENGTH_SHORT).show();
            }else if (check_hybrids.contains("Please Select")){
                Toast.makeText(this, "Please enter the hybrid and try again", Toast.LENGTH_SHORT).show();
            }
//            else if (check_hybrids.contains("Select")){
//                Toast.makeText(this, "Please enter the check-hybrid and try again", Toast.LENGTH_SHORT).show();
//            }
            else if(!check_hybrids.contains("Chk-Hyb(YES)")){
                Toast.makeText(this, "Please enter the Chk-Hyb(YES) with minimum 1 and maximum 10", Toast.LENGTH_SHORT).show();
            } else if(check_hybrids.equals("HV code selection is exceed by 10")){
                Toast.makeText(this, "Please enter the check-hybrid with minimum 1 and maximum 10", Toast.LENGTH_SHORT).show();
            } else {
                sessionsave.save_crop_data(CropName ,CropCode ,hybrids ,check_hybrids, season,year ,grower_code ,grower ,"City");
                save_pdtrial_registerform();

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void save_pdtrial_registerform() {

        gps_function();
        if(check_hybrids.contains("Select")){
            check_hybrids = "";
        }else {
        }
        if(check_hybrids.contains("[")){
            check_hybrids = check_hybrids.replace("[","");
        }if(check_hybrids.contains("]")){
            check_hybrids = check_hybrids.replace("]","");
        }
        if(check_hybrids.contains("Chk-Hyb(YES)")) {
            hybrid_chk = check_hybrids.replace("  -  Chk-Hyb(YES)", "").trim();
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, Pdtrial_formregistration , new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("create", "Response: " + response);
                if(response.contains("Success")||response.contains("ok")){
                    Toast.makeText(Pdtrial_fieldregister.this, "Pd trial registered successfully", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(Pdtrial_fieldregister.this, ScreenActivity.class);
                    startActivity(myIntent);
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

                params.put("Action", "newRegistration");
                params.put("growerCode", grower_code);
                params.put("year", year);
                params.put("pdyear", pdyear);
                params.put("season", season);
                params.put("cropCode", CropCode);
                params.put("cropName", CropName);
                params.put("hybrid", hybrid_chk);
//                params.put("checkHybrid", hybrid_chk);
                params.put("Company",sessionsave.get_company());
                params.put("Gpslocation",strAdd.trim());

                System.out.println("WWWWWWWWWWWWWW" + params);
                Log.d("Request", String.valueOf(params));
                return params;
            }

        };
        Log.d("result", String.valueOf(strReq));
        strReq.setRetryPolicy(new DefaultRetryPolicy(MAX_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void gps_function() {
        final LocationManager manager = (LocationManager) Objects.requireNonNull(this).getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
//            displayLocationSettingsRequest(this);
        }
        else {
            gps = new GPSTracker(this);
            // check if GPS enabled
            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                //  strAdd = getAddressFromLocation(latitude, longitude);
//                    statusCheck();
                strAdd = getCompleteAddressString(latitude, longitude);


            }
        }
    }

    @SuppressLint("LongLogTag")
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
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
//    private void displayLocationSettingsRequest(Context context) {
//        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
//                .addApi(LocationServices.API).build();
//        googleApiClient.connect();
//
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(10000);
//        locationRequest.setFastestInterval(10000 / 2);
//
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
//        builder.setAlwaysShow(true);
//
//        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
//        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//            @Override
//            public void onResult(LocationSettingsResult result)
//            {
//                final Status status = result.getStatus();
//                switch (status.getStatusCode()) {
//                    case LocationSettingsStatusCodes.SUCCESS:
//                        Log.i(TAG, "All location settings are satisfied.");
//                        break;
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
//
//                        try {
//                            // Show the dialog by calling startResolutionForResult(), and check the result
//                            // in onActivityResult().
//                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGSs);
//                        } catch (IntentSender.SendIntentException e) {
//                            Log.i(TAG, "PendingIntent unable to execute request.");
//                        }
//                        break;
//                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
//                        break;
//                }
//            }
//        });
//    }


}
