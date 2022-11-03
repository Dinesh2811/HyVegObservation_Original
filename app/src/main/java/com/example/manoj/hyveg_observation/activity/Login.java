package com.example.manoj.hyveg_observation.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manoj.hyveg_observation.Api;
import com.example.manoj.hyveg_observation.BuildConfig;
import com.example.manoj.hyveg_observation.Interfaces.IAlertListener;
import com.example.manoj.hyveg_observation.R;
import com.example.manoj.hyveg_observation.services.alertDialog;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.manoj.hyveg_observation.Api.TAG_RESULTS;
import static com.example.manoj.hyveg_observation.Api.ipconfig;

public class Login extends AppCompatActivity implements View.OnClickListener ,IAlertListener{

    Button login;
    EditText user,pass;
    TextView text_show_pass;
    //Service
    alertDialog alert;
    String PoCodes,myJSON,PoCodes_p ,folder_name = "null",company = "" ,sp = "0" ,android_version;
    Sessionsave sessionsave;
    CheckBox check_hyb ,check_opfc;
    private static final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 1;
    File Iroot = Environment.getExternalStorageDirectory();
    ProgressDialog prgDialogUpdate;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionsave = new Sessionsave(this);
        alert = new alertDialog(this);
        login = findViewById(R.id.login);
        user=findViewById(R.id.username);
        user.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        pass=findViewById(R.id.pass);
        pass.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        check_hyb = findViewById(R.id.COT);
        check_opfc = findViewById(R.id.FC);
        text_show_pass = findViewById(R.id.text_pass_show);

        login.setOnClickListener(this);
        check_hyb.setOnClickListener(this);
        check_opfc.setOnClickListener(this);
        text_show_pass.setOnClickListener(this);
        check_permissions();

        PackageInfo pInfo = null;

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        android_version = pInfo.versionName;

        check_login();
    }

    private void check_login() {
        if (alert.isNetworkAvailable()) {
            check_version(1);
//            if (!sessionsave.get_emp_code().equals("")) {
//                startActivity(new Intent(Login.this, ScreenActivity.class));
//                finish();
//            }
        }else {
            Toast.makeText(context, "Offline Mode", Toast.LENGTH_LONG).show();
            if (!sessionsave.get_emp_code().equals("")) {
                startActivity(new Intent(Login.this, ScreenActivity.class));
                finish();
            }
        }
    }

    private void check_permissions() {
        if (Build.VERSION.SDK_INT >= 23) {

            int internet = checkSelfPermission(Manifest.permission.INTERNET);
            int network_state = checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE);
            int read_external = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int write_external = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int fine_location = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int coarse_location = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            int camera = checkSelfPermission(Manifest.permission.CAMERA);

            List<String> permission = new ArrayList<>();
            if (internet != PackageManager.PERMISSION_GRANTED) {
                permission.add(Manifest.permission.INTERNET);
            }
            if (network_state != PackageManager.PERMISSION_GRANTED) {
                permission.add(Manifest.permission.ACCESS_NETWORK_STATE);
            }
            if (read_external != PackageManager.PERMISSION_GRANTED) {
                permission.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (write_external != PackageManager.PERMISSION_GRANTED) {
                permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (fine_location != PackageManager.PERMISSION_GRANTED) {
                permission.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (coarse_location != PackageManager.PERMISSION_GRANTED) {
                permission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (camera != PackageManager.PERMISSION_GRANTED) {
                permission.add(Manifest.permission.CAMERA);
            }
            if (!permission.isEmpty()) {
                requestPermissions(permission.toArray(new String[permission.size()]),
                        REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login) {
            if (alert.isNetworkAvailable()) {
                PoCodes = user.getText().toString();
                PoCodes_p = pass.getText().toString();

                if (!folder_name.equals("null")) {

                    if (PoCodes.trim().length() > 0 && PoCodes_p.trim().length() > 0) {

                        check_version(0);

                    } else {
                        Toast.makeText(this, "Please Enter UserName or Password", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "Please tick the division", Toast.LENGTH_SHORT).show();
                }
            }else {
                PoCodes = user.getText().toString();
                PoCodes_p = pass.getText().toString();

                if (PoCodes.trim().length() > 0 && PoCodes_p.trim().length() > 0) {
                    if (!sessionsave.get_emp_code().equals("")) {
                        if (PoCodes.equals(sessionsave.get_emp_code())) {
                            if (PoCodes_p.equals(sessionsave.get_pass())) {
                                if (!folder_name.equals("null")) {
                                    Toast.makeText(this, "No network connection available \nLaunching Offline mode", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Login.this, ScreenActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(this, "Please tick the division", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "Password incorrect", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "UserName incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(this, "There is no network connection to login", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "Please Enter UserName or Password", Toast.LENGTH_SHORT).show();
                }
            }
//        startActivity(new Intent(Login.this, cropselect.class));
        }else if (view.getId() == R.id.COT){
            if (check_hyb.isChecked()) {
                check_opfc.setChecked(false);
                folder_name = "SalesIndentCotton";
                company = "hvg";
            }else {
                folder_name = "null";
                company = "";
            }
        }else if (view.getId() == R.id.FC){
            if (check_opfc.isChecked()) {
                check_hyb.setChecked(false);
                folder_name = "SalesIndentFieldCrop";
                company = "hof";
            }else {
                folder_name = "null";
                company = "";
            }
        }else if (view.getId() == R.id.text_pass_show){
            if (sp.equals("0")) {
                text_show_pass.setText("Hide");
                pass.setInputType(InputType.TYPE_CLASS_TEXT);
                sp = "1";
            } else {
                text_show_pass.setText("Show");
                pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                sp = "0";
            }
        }
    }

    private void check_version(final int type) {

        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
//                HttpPost httppost = new HttpPost("http://192.168.35.24/hyveg/pd_trail/Version.php");
                HttpPost httppost = new HttpPost(Api.version());

                // Depends on your web service
                httppost.setHeader("Content-type", "application/json");
                Log.e("values",Api.version());
                InputStream inputStream = null;
                String result = null;
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    result = sb.toString();

                } catch (Exception e) {

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
//                Log.e(TAG_RESULTS ,myJSON);
                try {
                    JSONObject object = new JSONObject(myJSON);
                    String ver_server = object.getString("Id");
                    String file_name = object.getString("app");
                    Log.e("values",ver_server);
                    Log.e("values",android_version);
                    if (ver_server.equals(android_version)){
                        Log.e("values","inserting-----");
                        if (type == 0) {
                            check_online();
                            Log.e("values","inserting---1--");
                        }else {
                            Log.e("values","inserting----2-");
                            if (!sessionsave.get_emp_code().equals("")) {
                                Log.e("values","inserting----3-");
                                startActivity(new Intent(Login.this, ScreenActivity.class));
                                finish();
                            }
                        }
                    }else {
                        folderDelete();
                        get_file(file_name);
                    }
                }catch (Exception e){
                    Toast.makeText(Login.this, "Error1", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }

        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    private void get_file(String file_name) {
        Toast.makeText(getApplicationContext(), "Update Available", Toast.LENGTH_SHORT).show();

        prgDialogUpdate = new ProgressDialog(this, R.style.StyledDialog);
        prgDialogUpdate.setMessage("Please Wait New Update Downloading ...");
        prgDialogUpdate.setCancelable(false);
//        Update("http://192.168.35.24/hyveg/pd_trail/Android/"+file_name+".apk" ,file_name);
        Update(ipconfig+"/Android/"+file_name+".apk" ,file_name);
    }

    private void Update(final String apk_url , final String file_name) {
        prgDialogUpdate.show();
        new AsyncTask<Void, String, String>() {
            String result = "";

            @Override
            protected String doInBackground(Void... params) {
                try {
                    URL url = new URL(apk_url);
                    HttpURLConnection c = (HttpURLConnection) url
                            .openConnection();
                    c.setRequestMethod("GET");

                    c.connect();

                    String PATH = Environment.getExternalStorageDirectory()
                            + "/download/";
                    File file = new File(PATH);
                    file.mkdirs();
                    File outputFile = new File(file, file_name+".apk");
                    FileOutputStream fos = new FileOutputStream(outputFile);

                    InputStream is = c.getInputStream();

                    byte[] buffer = new byte[1024];
                    int len1 = 0;
                    while ((len1 = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len1);
                    }
                    fos.close();
                    is.close();
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
////                    intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + file_name+".apk")), "application/vnd.android.package-archive");
//                    Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(Environment.getExternalStorageDirectory() + "/download/" + file_name+".apk"));
//                    intent.setDataAndType(uri,"application/vnd.android.package-archive");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);

                    File toInstall = new File(Environment.getExternalStorageDirectory()+ "/download/" , file_name + ".apk");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", toInstall);
                        Intent intent1 = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                        intent1.setData(apkUri);
                        intent1.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent1);
                    } else {
                        Uri apkUri = Uri.fromFile(toInstall);
                        Intent intent2 = new Intent(Intent.ACTION_VIEW);
                        intent2.setDataAndType(apkUri, "application/vnd.android.package-archive");
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent2);
                    }

                } catch (IOException e) {
                    result = "Update error! " + e.getMessage();
                    e.printStackTrace();

                }
                return result;
            }

            protected void onPostExecute(String result) {

                Toast.makeText(getApplicationContext(), "Downloading Successful Please Install",
                        Toast.LENGTH_LONG).show();

                prgDialogUpdate.cancel();
            }
        }.execute();
    }

    private void folderDelete() {
        File dir = new File(Iroot.getAbsolutePath() + "/Android/Observation");
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                new File(dir, aChildren).delete();
            }
        }
    }

    private void check_online() {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());

//                                    HttpPost httppost = new HttpPost("http://103.44.97.110:8080/kanagaraj/" + folder_name + "/ISLogin.php?EmpCode=" + PoCodes);
                HttpPost httppost = new HttpPost(Api.login(PoCodes, PoCodes_p, company));

                Log.e("url",Api.login(PoCodes, PoCodes_p, company));

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
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {

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
                Log.e(TAG_RESULTS, myJSON);
                try {
                    showListonline();
                } catch (Exception e) {
                    Toast.makeText(Login.this, "Error2", Toast.LENGTH_SHORT).show();
                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    protected void showListonline() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);

            if (jsonObj.getString("status").equals("ok")){
                sessionsave.save_emp_code(PoCodes);
                sessionsave.save_pass(PoCodes_p);
                sessionsave.save_folder(folder_name);
                sessionsave.save_company(company);
                sessionsave.save_d_code(jsonObj.getString("DCode"));

//                startActivity(new Intent(Login.this, Observation_view.class));
                startActivity(new Intent(Login.this, ScreenActivity.class));
                finish();
            }else {
                Toast.makeText(this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
            }
//            JSONArray peoples = jsonObj.getJSONArray(Api.TAG_RESULTS);
//
//            for (int i = 0; i < peoples.length(); i++) {
//                JSONObject c = peoples.getJSONObject(i);
//                String Pocodess = c.getString(T2k);
//                String UserStatus = c.getString(T28);
//
//                if (Pocodess.trim().equals(PoCodes)) {
//                    if (UserStatus.equals("ACTIVE")) {
//                        sessionsave.save_emp_code(PoCodes);
//                        sessionsave.save_folder(folder_name);
//                        sessionsave.save_company(company);
//                        startActivity(new Intent(Login.this, Observation_view.class));
//                    }
//                }else {
//                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
//                }
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_SOME_FEATURES_PERMISSIONS: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.v("Permissions", "Permission Granted: " + permissions[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Log.v("Permissions", "Permission Denied: " + permissions[i]);
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        user.setText("");
        pass.setText("");
        check_hyb.setChecked(false);
        check_opfc.setChecked(false);
        folder_name = "null";
        text_show_pass.setText("Show");
        sp = "0";
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
