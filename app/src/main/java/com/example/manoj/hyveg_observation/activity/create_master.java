package com.example.manoj.hyveg_observation.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Environment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.manoj.hyveg_observation.Others.AppController;
import com.example.manoj.hyveg_observation.R;
import com.example.manoj.hyveg_observation.services.alertDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.example.manoj.hyveg_observation.Api.save_growermaster;

public class create_master extends AppCompatActivity implements View.OnClickListener {

    EditText edit_growername,edit_groweradress, edit_growervillage, edit_growertaluk,edit_city;

    Spinner spin_states;
    Button createmaster;

   public static int MAX_TIME = 12 * 10000;

    String GrowerName  ="",Address = "",Village = "",Taluk = "",City="",State = "",State_code ="",myJSON;

    String grower_url = "";

    private ProgressDialog dialog;

    //Service
    alertDialog alert;
    //    JsonServiceHelper serviceHelper;
    Sessionsave sessionsave;
    private SQLiteDatabase dbGetMaster;
    private Cursor GetWareHouseDetails;
    File Oroot = Environment.getExternalStorageDirectory() ,GetMasterPath=null;
    String tag_string_req = "req_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_master);

        //Path for the database
        if (Oroot.canWrite()) {
            File dir = new File(Oroot.getAbsolutePath() + "/Android/Observation");
            dir.mkdirs();

            GetMasterPath = new File(dir, "GetMasterDB.db");

        }

        dialog = new ProgressDialog(this);

        alert = new alertDialog(this);
        sessionsave = new Sessionsave(this);

        edit_growername = findViewById(R.id.edit_growername);
        edit_groweradress = findViewById(R.id.edit_groweradress);
        edit_growervillage = findViewById(R.id.edit_growervillage);
        edit_growertaluk = findViewById(R.id.edit_growertaluk);
        edit_city = findViewById(R.id.edit_city);
        spin_states = findViewById(R.id.spin_states);
        createmaster = findViewById(R.id.createmaster);

        createmaster.setOnClickListener(this);

        dbGetMaster = openOrCreateDatabase(GetMasterPath.getPath(), Context.MODE_PRIVATE, null);


        get_states_db();

        spin_states.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                State = spin_states.getSelectedItem().toString();
                State_code = get_state_code(spin_states.getSelectedItem().toString());
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


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


    private void get_states_db() {
        List<String> codes = new ArrayList<>();

        if (isTableExists(dbGetMaster, "StateMaster")) {

            GetWareHouseDetails = dbGetMaster.rawQuery("select * from StateMaster", null);

            if (GetWareHouseDetails.moveToFirst()) {
                do {
                    codes.add(new String(GetWareHouseDetails.getString(2)));
                } while (GetWareHouseDetails.moveToNext());
            }
        }else {
            Toast.makeText(this, "Please get data from server and try again", Toast.LENGTH_SHORT).show();
            finish();
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
    public void onClick(View view) {


            GrowerName = edit_growername.getText().toString();
            Address = edit_groweradress.getText().toString();
            Village = edit_growervillage.getText().toString();
            Taluk = edit_growertaluk.getText().toString();
            City = edit_city.getText().toString();

            if ((GrowerName.equals(""))) {
                Toast.makeText(this, "Please enter the grower name", Toast.LENGTH_SHORT).show();
            }else if (Address.equals("")){
                Toast.makeText(this, "Please enter the address", Toast.LENGTH_SHORT).show();
            }else if (Village.equals("")){
                Toast.makeText(this, "Please enter the village", Toast.LENGTH_SHORT).show();
            }else if (Taluk.equals("")){
                Toast.makeText(this, "Please enter the taluk", Toast.LENGTH_SHORT).show();
            }else if (City.equals("")){
                Toast.makeText(this, "Please enter the city ", Toast.LENGTH_SHORT).show();
            }else if (State.contains("Select")){
                Toast.makeText(this, "Please select the state and try again", Toast.LENGTH_SHORT).show();
            } else {

                createmaster_data();
                dialog.setMessage("Loading\n Please wait .....");
                dialog.setCancelable(false);
                dialog.show();
            }


    }



        private void createmaster_data() {


            // String path = stringPath.J_username+"="+user+"&"+stringPath.J_password+"="+password+"&"+stringPath.PROJECT_KEY+"="+AppConfig.SEC_KEY;

            StringRequest strReq = new StringRequest(Request.Method.POST,save_growermaster , new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d("create", "Response: " + response);
                    if(response.contains("Success")||response.contains("ok")){
                        if (dialog != null) {
                            dialog.cancel();
                        }
                        Toast.makeText(create_master.this, "Grower registered successfully", Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(create_master.this, ScreenActivity.class);
                        startActivity(myIntent);
                    }else{
                        if (dialog != null) {
                            dialog.cancel();
                        }
                        Toast.makeText(create_master.this, "Error in grower registration", Toast.LENGTH_SHORT).show();

                    }


//                hideDialog();
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

                    params.put("state", State_code);
                    params.put("growerName", GrowerName);
                    params.put("address", Address);
                    params.put("village", Village);
                    params.put("taluk", Taluk);
                    params.put("city", City);
                    params.put("Company",sessionsave.get_company());
                    params.put("Action","newGrower");

                    System.out.println("WWWWWWWWWWWWWW" + params);
                    Log.d("Request", String.valueOf(params));
                    return params;
                }

            };
            Log.d("result", String.valueOf(strReq));
            strReq.setRetryPolicy(new DefaultRetryPolicy(MAX_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }
    }



