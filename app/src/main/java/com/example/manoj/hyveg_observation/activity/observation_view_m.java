package com.example.manoj.hyveg_observation.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.manoj.hyveg_observation.Api;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class observation_view_m extends AppCompatActivity {

    Button btn_submit;
    Spinner spin_employee;
    alertDialog alert;
    String TAG = "observation_view_m" ,emp_code = "";
    List<String> emp_list;
    Sessionsave sessionsave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_view_m);

        btn_submit = findViewById(R.id.btn_select);
        spin_employee = findViewById(R.id.spin_employee);
        alert = new alertDialog(this);
        emp_list = new ArrayList<>();
        sessionsave = new Sessionsave(this);

        get_employee_list();

        spin_employee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                emp_code = spin_employee.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emp_code.equals("")){
                    Toast.makeText(observation_view_m.this, "Please Select any employee", Toast.LENGTH_SHORT).show();
                }else {
                    Intent i = new Intent(observation_view_m.this ,Observation_view.class);
                    i.putExtra("Emp_code",emp_code);
                    startActivity(i);
                }
            }
        });
    }

    private void get_employee_list() {
        if (alert.isNetworkAvailable()) {
            class GetDataJSON extends AsyncTask<String, Void, String> {

                @Override
                protected String doInBackground(String... params) {
                    HttpPost httppost = null;
                    DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());

                    httppost = new HttpPost(Api.get_employee_list(sessionsave.get_company()));
//                    httppost = new HttpPost("http://192.168.35.24/hyveg/pd_trail/GetOBPDE.php?Company=hvg");

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
                    Log.e(TAG ,"result "+result);
                    try {
                        spilt_data(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG ,"error "+e.toString());
                    }
                }
            }
            GetDataJSON g = new GetDataJSON();
            g.execute();
        }
    }

    private void spilt_data(String result) {
        try {
            JSONArray jsonObj = new JSONArray(result);

            for (int i = 0; i < jsonObj.length(); i++) {
                JSONObject c = jsonObj.getJSONObject(i);

                String e_id = c.getString("EMPId");
                String e_name = c.getString("EMPName");

                emp_list.add(e_id+" - "+e_name);
            }

            HashSet<String> set = new HashSet<>(emp_list);
            ArrayList<String> result1 = new ArrayList<>(set);
            Collections.sort(result1);
            List<String> codes3 = new ArrayList<String>();
            codes3.add("All");
            for (int j = 0; j < result1.size(); j++) {

                String  codes2= result1.get(j);
                codes3.add(codes2);

            }

            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, codes3);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin_employee.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
            spin_employee.setAdapter(adapter1);

        } catch (JSONException e) {
            Log.e(TAG ,"error "+e.toString());
        }
    }
}
