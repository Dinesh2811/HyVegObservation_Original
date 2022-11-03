package com.example.manoj.hyveg_observation.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manoj.hyveg_observation.Api;
import com.example.manoj.hyveg_observation.R;
import com.example.manoj.hyveg_observation.adapter.ob_get_list_adapter;
import com.example.manoj.hyveg_observation.list.ob_get_list;
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
import java.util.ArrayList;
import java.util.List;

import static com.example.manoj.hyveg_observation.Api.TAG_RESULTS;

public class Observation_view extends AppCompatActivity implements View.OnClickListener{

//    FloatingActionButton fab_add;
    String myJSON ,TAG = "observation_view" ,emp_code = "";
    alertDialog alert;
    Sessionsave sessionsave;
    List<ob_get_list> ob_get;
    RecyclerView recycle_ob_get;
    private File Oroot = Environment.getExternalStorageDirectory() ,GetMasterPath=null;
    private SQLiteDatabase dbGetMaster;
    private Cursor GetWareHouseDetails;
    TextView text_sl_no ,text_ob_no ,text_crop_name ,text_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        alert = new alertDialog(this);
        sessionsave = new Sessionsave(this);

        try {
            String[] emp_data = getIntent().getStringExtra("Emp_code").split("-");
            emp_code = emp_data[0].trim();
        }catch (Exception e){
            emp_code = sessionsave.get_emp_code();
        }

        text_sl_no = findViewById(R.id.text_sl_no);
        text_ob_no = findViewById(R.id.text_ob_no);
        text_crop_name = findViewById(R.id.text_crop_name);
        text_date = findViewById(R.id.text_date);

        if (Oroot.canWrite()) {
            File dir = new File(Oroot.getAbsolutePath() + "/Android/Observation");
            dir.mkdirs();

            GetMasterPath = new File(dir, "GetMasterDB.db");

        }

        dbGetMaster = openOrCreateDatabase(GetMasterPath.getPath(), Context.MODE_PRIVATE, null);

        ob_get = new ArrayList<>();
//        fab_add = findViewById(R.id.fab_add_ob_data);
        recycle_ob_get = findViewById(R.id.recycle_ob_data);
        recycle_ob_get.setHasFixedSize(true);
        recycle_ob_get.setLayoutManager(new LinearLayoutManager(this));

//        if (sessionsave.get_d_code().equals("PDM")&&sessionsave.get_d_code().equals("PDADMIN")){
//            fab_add.setVisibility(View.GONE);
//        }else {
//            fab_add.setVisibility(View.VISIBLE);
//        }
//        fab_add.setOnClickListener(this);
//        fab_add.setVisibility(View.GONE);
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
        get_ob_data();
    }

    private void get_ob_data() {
        if (alert.isNetworkAvailable()) {
            class GetDataJSON extends AsyncTask<String, Void, String> {

                @Override
                protected String doInBackground(String... params) {
                    DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                    HttpPost httppost = null;
                    if (emp_code.equals("All")) {
                        httppost = new HttpPost(Api.ob_list("all", sessionsave.get_company(),
                                sessionsave.get_d_code()));
                    }else {
                        httppost = new HttpPost(Api.ob_list(emp_code, sessionsave.get_company(),
                                sessionsave.get_d_code()));
                    }
                    Log.e("url",Api.ob_list(emp_code, sessionsave.get_company(),
                            sessionsave.get_d_code()));

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
                    Log.e(TAG ,result);
                    myJSON = result;
                    try {
                        showList3();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            GetDataJSON g = new GetDataJSON();
            g.execute();
        }else {
            get_ob_data_db();
        }
    }

    private void get_ob_data_db() {
        Toast.makeText(this, "Displaying only offline records", Toast.LENGTH_SHORT).show();
        text_ob_no.setVisibility(View.GONE);
        text_date.setVisibility(View.VISIBLE);
        ob_get = new ArrayList<>();
        GetWareHouseDetails = dbGetMaster.rawQuery("select * from ObservationData ", null);

        if (GetWareHouseDetails.moveToFirst()) {
            ob_get_list ob = new ob_get_list();
            do {
                String id = new String(GetWareHouseDetails.getString(0));
                String CropName = new String(GetWareHouseDetails.getString(2));
                String CreatedDateTime = new String(GetWareHouseDetails.getString(15));
                String pd_number = new String(GetWareHouseDetails.getString(18));
                String grower_code = new String(GetWareHouseDetails.getString(17));

                ob.get_id(id);
                ob.get_ob_id("OB-" + id);
                ob.get_pd_number(pd_number);
                ob.get_growercode(grower_code);
                ob.get_name(CropName);
                ob.get_date(CreatedDateTime);
            }while (GetWareHouseDetails.moveToNext());
            ob_get.add(ob);

            RecyclerView.Adapter adapter = new ob_get_list_adapter(Observation_view.this, ob_get ,"offline");
            recycle_ob_get.setAdapter(adapter);
        }
    }

    private void showList3() {
        ob_get = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            JSONArray peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < peoples.length(); i++) {
                ob_get_list ob = new ob_get_list();
                try {
                    JSONObject c = peoples.getJSONObject(i);

//                String Id = c.getString(Api.T1);
                    String A = c.getString(Api.T2);
                    String B = c.getString(Api.T3);
                    String C = c.getString(Api.T4);
                     String D = c.getString(Api.T5);
                    String E = c.getString(Api.T6);
                    String F = c.getString(Api.T7);
                /* String E = c.getString(Api.T6);
                String F = c.getString(Api.T7);
                String G = c.getString(Api.T8);
                String H = c.getString(Api.T9);
                String I = c.getString(Api.T10);
                String J = c.getString(Api.T11);
                String K = c.getString(Api.T12);
                String L = c.getString(Api.T13);
                String M = c.getString(Api.T14);
                String N = c.getString(Api.T15);
                String O = c.getString(Api.T16);
                String P = c.getString(Api.T17);
                String Q = c.getString(Api.T18);
                String R = c.getString(Api.T19);
                String S = c.getString(T20);
                String T = c.getString(T21);
                String U = c.getString(T22);
                String V = c.getString(T23);
                String W = c.getString(T24);
                String X = c.getString(T25);
                String Y = c.getString(T26);
                String Z = c.getString(T27);*/

                    ob.get_id(A);
                    ob.get_ob_id(B);
                    ob.get_name(C);
                    ob.get_pd_number(D);
                    ob.get_growercode(E);
                    ob.get_date(F);
                }catch (Exception e){
                    e.printStackTrace();
                }
                ob_get.add(ob);
            }
            RecyclerView.Adapter adapter = new ob_get_list_adapter(Observation_view.this, ob_get ,"Online");
            recycle_ob_get.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "No Data Available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_add_ob_data){
            sessionsave.session_clear();
            Intent myIntent = new Intent(Observation_view.this, cropselect.class);
            startActivity(myIntent);
        }
    }
}
