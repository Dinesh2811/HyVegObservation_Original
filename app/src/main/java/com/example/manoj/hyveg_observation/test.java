//package com.example.manoj.hyveg_observation.adapter;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.LocationManager;
//import android.os.AsyncTask;
//import android.os.Environment;
//import androidx.annotation.NonNull;
//import androidx.cardview.widget.CardView;
//import androidx.recyclerview.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.example.manoj.hyveg_observation.Api;
//import com.example.manoj.hyveg_observation.Others.AppController;
//import com.example.manoj.hyveg_observation.Others.GPSTracker;
//import com.example.manoj.hyveg_observation.R;
//import com.example.manoj.hyveg_observation.activity.Login;
//import com.example.manoj.hyveg_observation.activity.Observation_view;
//import com.example.manoj.hyveg_observation.activity.Pdtrial_fieldregister;
//import com.example.manoj.hyveg_observation.activity.Sessionsave;
//import com.example.manoj.hyveg_observation.activity.create_master;
//import com.example.manoj.hyveg_observation.activity.cropselect;
//import com.example.manoj.hyveg_observation.activity.observation_view_m;
//import com.example.manoj.hyveg_observation.list.FormModel;
//import com.example.manoj.hyveg_observation.list.ListModel;
//import com.example.manoj.hyveg_observation.services.FeedReaderDbHelper;
//import com.example.manoj.hyveg_observation.services.SQLHelper;
//import com.example.manoj.hyveg_observation.services.alertDialog;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.params.BasicHttpParams;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Objects;
//
//import static com.example.manoj.hyveg_observation.Api.Pd_RegistrationNumber;
//import static com.example.manoj.hyveg_observation.Api.TAG_RESULTS;
//import static com.example.manoj.hyveg_observation.activity.create_master.MAX_TIME;
//
//public class screen_list_adapter extends RecyclerView.Adapter<screen_list_adapter.ViewHolder>{
//
//    private Context mCtx;
//    private String[] show_data;
//    private int[] show_img;
//    private alertDialog alert;
//    private Sessionsave sessionsave;
//    private int save_no = 0 ,int_id = 0;
//    private String TAG = "screen_adapter" ,strAdd = "0", result = "",check_hybrids="";
//    private ProgressDialog dialog;
//    private File Oroot = Environment.getExternalStorageDirectory() ,GetMasterPath=null;
//    private SQLiteDatabase dbGetMaster;
//    private Cursor GetWareHouseDetails;
//
//    List check_hyb = new ArrayList<>();
//
//    String image1,image2;
//
//    private ArrayList<ListModel> listModel;
//    ArrayList<FormModel> formModel;
//
//    Activity mActivity;
//
//    //DB
//    public SQLiteDatabase db;
//    public FeedReaderDbHelper dbHelper;
//
//    private ArrayList<String> codesList =  new ArrayList<>();;
//
//    String  offline_id = "0" ;
//
//    // GPSTracker class
//    GPSTracker gps;
//    double latitude, longitude;
//
//    public screen_list_adapter(Context mCtx ,String[] show_data ,int[] show_img, Activity mActivity) {
//        super();
//        this.mCtx = mCtx;
//        this.show_data = show_data;
//        this.show_img = show_img;
//        this.mActivity = mActivity;
//
//        alert = new alertDialog(mCtx);
//        sessionsave = new Sessionsave(mCtx);
//        dialog = new ProgressDialog(mCtx);
//
//        if (Oroot.canWrite()) {
//            File dir = new File(Oroot.getAbsolutePath() + "/Android/Observation");
//            dir.mkdirs();
//
//            GetMasterPath = new File(dir, "GetMasterDB.db");
//
//        }
//
//        initDB();
//
//
//
//        dbGetMaster = mCtx.openOrCreateDatabase(GetMasterPath.getPath(), Context.MODE_PRIVATE, null);
//        if (alert.isNetworkAvailable()) {
//            Create_delete_pddatabase();
//        }
//        get_Pd_registrationnumber();
//    }
//
//    public void initDB() {
//        this.dbHelper = new FeedReaderDbHelper((mCtx));
//        this.db = this.dbHelper.getWritableDatabase();
//    }
//
//    private void get_Pd_registrationnumber() {
//
//        StringRequest strReq = new StringRequest(Request.Method.POST,Pd_RegistrationNumber , new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                Log.d("create", "Response: " + response);
//
//                if(response.contains("Success")||response.contains("ok")){
//                    JSONObject jObj = null;
//                    try {
//                        jObj = new JSONObject(response);
//                        String status = jObj.getString("status");
//                        String msg = jObj.getString("msg");
//                        JSONArray jsonArray = jObj.getJSONArray("RegNum");
//                        for (int i =0;i<jsonArray.length();i++){
//                            JSONObject fixObj = jsonArray.getJSONObject(i);
//                            String  pdRegNum = fixObj.getString("pdRegNum");
//                            String  growerCode = fixObj.getString("growerCode");
//                            String  growerName = fixObj.getString("growerName");
//                            String  crop_status = fixObj.getString("status");
//                            String  Season = fixObj.getString("Season");
//                            String  cropCode = fixObj.getString("cropCode");
//                            String  year = fixObj.getString("year");
//                            String  hybrid = fixObj.getString("hybrid");
//                            String  checkHybrid = fixObj.getString("checkHybrid");
//                            String  pd_status = fixObj.getString("status");
//                            Log.e("code---1--",pdRegNum);
//                            Log.e("code---2--",growerCode);
//                            Log.e("code---3--",crop_status);
//                            String grower_code_name = growerCode+" "+"|"+growerName;
//
//                            String query = "INSERT INTO PdregistrationMaster (pdRegNum,growerCode,Season,cropCode,year,hybrid,status,grower_code_name) VALUES('" + pdRegNum + "','" + growerCode + "','" + Season + "','" + cropCode + "','" + year + "','" + hybrid + "','" + pd_status + "','" + grower_code_name + "');";
//                            dbGetMaster.execSQL(query);
//
//
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                Log.e("create", "network Error: " + error.getMessage());
//
//
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//
//                params.put("Company",sessionsave.get_company());
//                params.put("Action","getCode");
//
//                System.out.println("WWWWWWWWWWWWWW" + params);
//                Log.d("Request", String.valueOf(params));
//                return params;
//            }
//
//        };
//        Log.d("result", String.valueOf(strReq));
//        strReq.setRetryPolicy(new DefaultRetryPolicy(MAX_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        AppController.getInstance().addToRequestQueue(strReq, "ScreenListAdapter");
//    }
//
//    @NonNull
//    @Override
//    public screen_list_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_screen_list, parent, false);
//        screen_list_adapter.ViewHolder viewHolder = new screen_list_adapter.ViewHolder(v);
//
//        return viewHolder;
//
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final screen_list_adapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
//        holder.text_tab_name.setText(show_data[position]);
//        holder.img_tab.setImageResource(show_img[position]);
//
//
//        update_table();
//
//
//        Log.e("values--emp",sessionsave.get_emp_code());
//    }
//
//    @Override
//    public int getItemCount() {
//        return show_data.length;
//    }
//
//    @Override
//    public int getItemViewType(final int position) {
//        return position;
//    }
//
//    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
//
//        TextView text_tab_name;
//        CardView card_tab;
//        ImageView img_tab;
//
//        ViewHolder(View itemView) {
//            super(itemView);
//            card_tab = itemView.findViewById(R.id.card_tab);
//            text_tab_name = itemView.findViewById(R.id.text_tab_name);
//            img_tab = itemView.findViewById(R.id.img_tab);
//
//            card_tab.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View view) {
//            if (view.equals(card_tab)) {
//                String tab_name = text_tab_name.getText().toString();
//                switch (tab_name) {
//                    case "Get Data":
//                        if (alert.isNetworkAvailable()) {
//                            Delete_create_GetMasterDatabase();
//                            get_data(0);
//                            dialog.setMessage("Getting the data from server\nPlease wait .....");
//                            dialog.setCancelable(false);
//                            dialog.show();
//                        }else {
//                            Toast.makeText(mCtx, "Please try in Online mode", Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//
//                    case "Grower Register":
//                        if (alert.isNetworkAvailable()) {
//                            sessionsave.session_clear();
//                            mCtx.startActivity(new Intent(mCtx, create_master.class));
//                            break;
//                        }else {
//                            Toast.makeText(mCtx, "Please try in Online mode", Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//
//                    case "Upload Data":
//                        if (alert.isNetworkAvailable()) {
////                            try {
////                                Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
////                                intent.putExtra("enabled", true);
////                                mCtx.sendBroadcast(intent);
////                            }catch (Exception e){
////                                e.printStackTrace();
////                            }
//                            upload_data(1);
//                        }else {
//                            Toast.makeText(mCtx, "Please try in Online mode", Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//
//                    case "OB Create":
//                        if (isTableExists(dbGetMaster, "PdregistrationMaster")) {
//
//                            GetWareHouseDetails = dbGetMaster.rawQuery("select * from PdregistrationMaster", null);
//
//                            if(GetWareHouseDetails.getCount()==0){
//                                Toast.makeText(mCtx, "Please get data from server", Toast.LENGTH_SHORT).show();
//
//                            }else {
//                                sessionsave.session_clear();
//                                mCtx.startActivity(new Intent(mCtx, cropselect.class));
//                            }
//                            break;
//                        }
//
//
//                    case "OB List":
//                        if (sessionsave.get_d_code().equals("PDE")) {
//                            mCtx.startActivity(new Intent(mCtx, Observation_view.class));
//                        }else {
//                            mCtx.startActivity(new Intent(mCtx, observation_view_m.class));
//                        }
//                        break;
//
//                    case "Offline OB List":
//                        mCtx.startActivity(new Intent(mCtx, Observation_view.class));
//                        break;
//
//                    case "PD Trial Field Registration":
//                        mCtx.startActivity(new Intent(mCtx, Pdtrial_fieldregister.class));
//                        break;
//
//                    case "Logout":
//                        if (isTableExists(dbGetMaster, "ObservationData")) {
//                            GetWareHouseDetails = dbGetMaster.rawQuery("select * from ObservationData", null);
//
//                            if (GetWareHouseDetails.moveToFirst()) {
//                                do {
//                                    try {
//                                        int_id = Integer.parseInt(new String(GetWareHouseDetails.getString(0)));
//                                    } catch (Exception e) {
//                                        Log.e(TAG, "Int id error " + e.toString());
//                                    }
//                                } while (GetWareHouseDetails.moveToNext());
//                            }
//
//                            if (int_id == 0) {
//                                if (!sessionsave.get_emp_code().equals("")) {
//                                    sessionsave.login_session_clear();
//                                    Intent i = new Intent(mCtx, Login.class);
//                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    mCtx.startActivity(i);
//                                } else {
//                                    android.os.Process.killProcess(android.os.Process.myPid());
//                                    System.exit(1);
//                                }
//                            } else {
//                                Toast.makeText(mCtx, "Please upload the data before you logout", Toast.LENGTH_SHORT).show();
//                            }
//                        }else {
//                            if (!sessionsave.get_emp_code().equals("")) {
//                                sessionsave.login_session_clear();
//                                Intent i = new Intent(mCtx, Login.class);
//                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                mCtx.startActivity(i);
//                            } else {
//                                android.os.Process.killProcess(android.os.Process.myPid());
//                                System.exit(1);
//                            }
//                        }
//                        break;
//                }
//            }
//        }
//    }
//
//    boolean isTableExists(SQLiteDatabase db, String tableName) {
//        if (tableName == null || db == null || !db.isOpen())
//        {
//            return false;
//        }
//        try {
//            Cursor cursor = db.rawQuery("select * from  "+tableName+" ", null);
//            if (!cursor.moveToFirst())
//            {
//                cursor.close();
//                return false;
//            }
//            int count = cursor.getInt(0);
//            cursor.close();
//            return count > 0;
//        }catch (Exception e){
//            return false;
//        }
//    }
//
//    private void upload_data(int i_no) {
//
//        GetWareHouseDetails = dbGetMaster.rawQuery("select * from ObservationData", null);
//
//        if (GetWareHouseDetails.moveToFirst()) {
//            do {
//                try {
//                    int_id = Integer.parseInt(new String(GetWareHouseDetails.getString(0)));
//                } catch (Exception e) {
//                    Log.e(TAG, "Int id error " + e.toString());
//                }
//            } while (GetWareHouseDetails.moveToNext());
//        }
//
//        Log.e(TAG ,"int_id "+int_id);
//        if (int_id == 0){
//            Toast.makeText(mCtx, "There is no record to upload", Toast.LENGTH_SHORT).show();
//        }else {
//            dialog.setMessage("Saving this observation data \nPlease wait .....");
//            dialog.show();
//        }
//
//        GetWareHouseDetails = dbGetMaster.rawQuery("select * from ObservationData WHERE id='"+i_no+"'", null);
//
//        if (GetWareHouseDetails.moveToFirst()) {
//
////            while (!GetWareHouseDetails.isAfterLast()){
//
//
//            String id = new String(GetWareHouseDetails.getString(0));
//            String CropCode = new String(GetWareHouseDetails.getString(1));
//            String CropName = new String(GetWareHouseDetails.getString(2));
//            String Company = new String(GetWareHouseDetails.getString(3));
//            String BreederCode = new String(GetWareHouseDetails.getString(4));
//            String HVCode = new String(GetWareHouseDetails.getString(5));
//            String Variety = new String(GetWareHouseDetails.getString(6));
//            String Stage = new String(GetWareHouseDetails.getString(7));
//            String State = new String(GetWareHouseDetails.getString(8));
//            String City = new String(GetWareHouseDetails.getString(9));
//            String CreatedBy = new String(GetWareHouseDetails.getString(10));
//            String Master_data = new String(GetWareHouseDetails.getString(11));
//            String General_data = new String(GetWareHouseDetails.getString(12));
//            String Observe_data = new String(GetWareHouseDetails.getString(13));
//            String usp_data  = new String(GetWareHouseDetails.getString(14));
//            String CreatedDateTime = new String(GetWareHouseDetails.getString(15));
//            String Location = new String(GetWareHouseDetails.getString(16));
//            String grower_code = new String(GetWareHouseDetails.getString(17));
//            String pd_number = new String(GetWareHouseDetails.getString(18));
//            String pd_status = new String(GetWareHouseDetails.getString(19));
//
//            Log.e("Values",CropName);
//            Log.e("Values-----",Stage);
//
//            save_no = 1;
//            save_bulk_data(id ,CropCode ,CropName ,Company ,BreederCode ,HVCode ,Variety ,Stage ,State ,City ,CreatedBy ,Master_data ,General_data ,Observe_data ,usp_data ,CreatedDateTime,Location,pd_status,pd_number);
//            Log.d("inserting",Stage);
////            GetWareHouseDetails.moveToNext();
////
////            }
//        }
//
//    }
//
//    private void save_bulk_data(final String id, final String cropCode, final String cropName, final String company, final String breederCode, final String hvCode, final String variety, final String stage, final String state, final String city, final String createdBy, final String master_data, final String general_data, final String observe_data, final String usp_data , final String CreatedDateTime , final String Location, final String pd_status, final String pd_number) {
//
//
//        check_hybrids = get_check_hybrids(pd_number);
////        Log.e("values___offline",check_hybrids);
//
//        check_hyb = Arrays.asList(check_hybrids.split(","));
//
//        String aa[] =check_hybrids.split(",");
//
//        Log.e("va;ues------kdkd", String.valueOf(check_hyb.size()));
//
//        check_hyb = Arrays.asList(aa);
//
//        for (int i = 0; i < check_hyb.size(); i++)
//        {
//            if(i == 0){
//                Log.e("check----3",check_hyb.get(i).toString());
//                codesList.add(check_hyb.get(0).toString());
//            }else if(i == 1){
//                codesList.add(check_hyb.get(1).toString());
//            }else if(i == 2){
//                codesList.add(check_hyb.get(2).toString());
//            }else if(i == 3){
//                codesList.add(check_hyb.get(3).toString());
//            }else if(i == 4){
//                codesList.add(check_hyb.get(4).toString());
//            }else if(i == 5){
//                codesList.add(check_hyb.get(5).toString());
//            } else if(i == 6){
//                codesList.add(check_hyb.get(6).toString());
//            }else if(i == 7){
//                codesList.add(check_hyb.get(7).toString());
//            }else if(i == 8){
//                codesList.add(check_hyb.get(8).toString());
//            }else if(i == 9){
//                codesList.add(check_hyb.get(9).toString());
//            }
//        }
//
//        try {
//            offline_id = id;
//            Log.e("value---offline", offline_id);
//        }catch (Exception e){
//            e.printStackTrace();
//            offline_id = "";
//        }
//
//
//        if (alert.isNetworkAvailable()) {
//
//            ArrayList mainheading =new ArrayList();
//            ArrayList msgstring =new ArrayList();
//
//            String msg = "";
//            String emailList= "";
//
//            gps_function();
//
//            Log.e("inserting----","inserting-----");
//
//            Map<String, String> params = new HashMap<>();
//            ArrayList<String> img_names = new ArrayList<>();
//            ArrayList<String> images = new ArrayList<>();
//            ArrayList<String> images_file_name = new ArrayList<>();
//
//            params.put("Company", sessionsave.get_company());
//            params.put("pdRegNum",pd_number );
//            params.put("pdStatus", pd_status);
//            params.put("GPSLocation", Location);
//            params.put("CropCode",cropCode);
//            params.put("CreatedBy", sessionsave.get_emp_code());
//            params.put("menuId", pd_status);
//            if (save_no == 1) {
//                params.put("obid", "");
//            } else {
//                params.put("obid", sessionsave.get_obid());
//            }
//            final File Oroot = Environment.getExternalStorageDirectory();
//            final String f_path = Oroot.getPath() + "/Android/RnDObservation/Images/";
//
//            if (stage.equals("general")) {
//                try {
//                    String[] master_split = master_data.split("\\|");
//                    Log.e(TAG, "master length " + master_split.length);
//                    params.put("menuId", 1 + "");
//                    for (String aMaster_split : master_split) {
//                        Log.e(TAG, "spiliting " + aMaster_split);
//                        String[] master_values = aMaster_split.split("=>");
//                        try {
//                            if (master_values[0].contains("img")) {
//                                String[] q = master_values[1].split("/");
//                                int idx = q.length - 1;
//                                images.add(f_path+q[idx]);
//                                img_names.add(master_values[0]);
//                            }else {
//                                params.put(master_values[0], master_values[1]);
//                            }
//                        } catch (Exception e) {
//                            params.put(master_values[0], "");
//                        }
//                    }
//                } catch (Exception e) {
//                    Log.e(TAG, "Error1 " + e.toString());
//                }
//            }else if(stage.equals("observe")){
//                params.put("menuId", "2");
//
//                for (int j = 0; j < codesList.size(); j++) {
//                    mainheading.add(codesList.get(j));
//                    formModel = SQLHelper.getAllUnSyncedFormFromDB(db, codesList.get(j),"observe",mActivity);
//                    for (int l = 0; l < formModel.size(); l++) {
//                        listModel = formModel.get(l).getListModels();
//                        int img_pos = 0;
//
//                        for (int i = 0; i < listModel.size(); i++) {
//                            if (listModel.get(i).getIntType() == 2) {
//                                if(listModel.get(i).getDecimals() != null && listModel.get(i).getDecimals().length > 0) {
//                                    msg = msg.concat(listModel.get(i).getFkey().concat(" => ").concat(listModel.get(i).getDecimals()[0]).concat("\n"));
//                                    emailList = msg.replaceAll("\n", "|");
//                                    Log.e("values_off1", listModel.get(i).getFname().concat(" => ") + "   " + listModel.get(i).getDecimals()[0]);
//                                }
//                            } else if(listModel.get(i).getIntType() == 5){
//                                if(listModel.get(i).getDecimals() != null && listModel.get(i).getDecimals().length > 0) {
//                                    msg = msg.concat(listModel.get(i).getFkey().concat(" => ").concat(listModel.get(i).getDecimals()[0]).concat("\n"));
//                                    emailList = msg.replaceAll("\n", "|");
//                                    Log.e("values--off2", listModel.get(i).getFname().concat(" => ") + "   " + listModel.get(i).getDecimals()[0]);
//                                }
//                            }else if(listModel.get(i).getIntType() == 1) {
//                                if(listModel.get(i).getSelected_choice_desc() != null && listModel.get(i).getSelected_choice_desc().length > 0) {
//                                    Log.e("values---off3", listModel.get(i).getFname().concat(" => ") + "  " + listModel.get(i).getSelected_choice_desc()[0]);
//                                    msg = msg.concat(listModel.get(i).getFkey().concat(" => ").concat(listModel.get(i).getSelected_choice_desc()[0]).concat("\n"));
//                                    emailList = msg.replaceAll("\n", "|");
//                                    Log.e("values----img",emailList);
//                                }
//                            }else if(listModel.get(i).getIntType() == 4) {
//                                if (listModel.get(i).getChoice_desc() != null && listModel.get(i).getChoice_desc().length > 0) {
//                                    if (i != 0) {
//                                        if (i % 2 == 0) {
//                                            img_pos++;
//                                        }
//                                    }
//                                    Log.e("values----1", listModel.get(i).getFname().concat(" => ") + "   " + listModel.get(i).getChoice_desc()[img_pos]);
//                                    Log.e("values----2", listModel.get(i).getFname().concat(" => ") + "   " + listModel.get(i).getChoice_desc()[img_pos + 1]);
//
//                                    image1 =  listModel.get(i).getChoice_desc()[img_pos];
//                                    image2 = listModel.get(i).getChoice_desc()[img_pos + 1];
//                                    if(image1!=null &&image1.contains("jpg") ) {
//                                        if(image2 != null && image2.contains("jpg")) {
//                                            msg = msg.concat("img_" + listModel.get(i).getFkey().concat(" => ").concat("1,2").concat("\n"));
//                                            emailList = msg.replaceAll("\n", "|");
//                                            Log.e("values----image1", emailList);
//                                        }else{
//                                            msg = msg.concat("img_" + listModel.get(i).getFkey().concat(" => ").concat("1").concat("\n"));
//                                            emailList = msg.replaceAll("\n", "|");
//                                            Log.e("values---image2",emailList);
//                                        }
//                                    }else{
//                                        msg = msg.concat("img_" + listModel.get(i).getFkey().concat(" => ").concat("1").concat("\n"));
//                                        emailList = msg.replaceAll("\n", "|");
//                                        Log.e("values---image2",emailList);
//                                    }
//                                    if (image1 == null){
//                                        image1 = "";
//                                    }
//                                    if (image2 == null){
//                                        image2 = "";
//                                    }
//                                    String imgname = listModel.get(i).getFkey();
//                                    images.add(image1);
//                                    images.add(image2);
//                                    img_names.add(imgname);
//
//                                }
//                            }
////
//
//
//                        }
//                    }
//                    if(!emailList.equalsIgnoreCase("") && emailList.length() > 0) {
//                        emailList = emailList.substring(0, emailList.length() - 1);
//                        Log.e("c", emailList);
//                    }
//                    if(emailList.contains("Please Select")){
//                        emailList = emailList.replace("Please Select","");
//                    }
//                    msgstring.add((emailList));
//                    msg = "";
//                    emailList = "";
//                }
//
//                for(int i = 0 ;i<mainheading.size();i++) {
//                    int img_name_pos = 0;
//                    for(int j=0;j<images.size();j++){
//                        if(j % 2 ==0){
//                            if (j != 0){
//                                img_name_pos++;
//                            }
////                        try {
//                            Log.e("values", "allfiles["+ mainheading.get(i) +"]" + "[img_" + img_names.get(img_name_pos) + "_1]");
//                            images_file_name.add("allfiles["+ mainheading.get(i) +"]" + "[img_"+img_names.get(img_name_pos)+"_1]");
//
////                        }catch (Exception e){
////                            e.printStackTrace();
////                            Log.e("error1","error");
////                        }
//                        }else{
////                        try {
//                            //                        Log.e("values","allflies[" + mainheading.get(i) + "]" + "[img_"+img_names.get(j)+"_2]");
//                            images_file_name.add("allfiles["+ mainheading.get(i) +"]"+"[img_"+img_names.get(img_name_pos)+"_2]");
////                        }catch (Exception e){
////                            e.printStackTrace();
////                            Log.e("error2","error2");
////                        }
//
//                        }
//                    }
//                    if(images.size()==2){
//                        params.put("data[" + mainheading.get(i) + "]", "["+msgstring.get(i)+"]");
//                    }else{
//                        params.put("data[" + mainheading.get(i) + "]", "["+msgstring.get(i)+"]");
//                    }
//
//                }
//
//
//
//                Log.e("params", String.valueOf(params));
//                Log.e("images", String.valueOf(images.size()));
//
//                Log.e("values----filename",images_file_name.toString());
//
//            }else if(stage.equals("disease")){
//                params.put("menuId", "3");
//                for (int j = 0; j < codesList.size(); j++) {
//                    mainheading.add(codesList.get(j));
//                    formModel = SQLHelper.getAllUnSyncedFormFromDB(db, codesList.get(j),"disease",mActivity);
//                    for (int l = 0; l < formModel.size(); l++) {
//                        listModel = formModel.get(l).getListModels();
//                        int img_pos = 0;
//
//                        for (int i = 0; i < listModel.size(); i++) {
//                            if (listModel.get(i).getIntType() == 2) {
//                                if(listModel.get(i).getDecimals() != null && listModel.get(i).getDecimals().length > 0) {
//                                    msg = msg.concat(listModel.get(i).getFkey().concat(" => ").concat(listModel.get(i).getDecimals()[0]).concat("\n"));
//                                    emailList = msg.replaceAll("\n", "|");
//                                    Log.e("values_off1", listModel.get(i).getFname().concat(" => ") + "   " + listModel.get(i).getDecimals()[0]);
//                                }
//                            } else if(listModel.get(i).getIntType() == 5){
//                                if(listModel.get(i).getDecimals() != null && listModel.get(i).getDecimals().length > 0) {
//                                    msg = msg.concat(listModel.get(i).getFkey().concat(" => ").concat(listModel.get(i).getDecimals()[0]).concat("\n"));
//                                    emailList = msg.replaceAll("\n", "|");
//                                    Log.e("values--off2", listModel.get(i).getFname().concat(" => ") + "   " + listModel.get(i).getDecimals()[0]);
//                                }
//                            }else if(listModel.get(i).getIntType() == 1) {
//                                if(listModel.get(i).getSelected_choice_desc() != null && listModel.get(i).getSelected_choice_desc().length > 0) {
//                                    Log.e("values---off3", listModel.get(i).getFname().concat(" => ") + "  " + listModel.get(i).getSelected_choice_desc()[0]);
//                                    msg = msg.concat(listModel.get(i).getFkey().concat(" => ").concat(listModel.get(i).getSelected_choice_desc()[0]).concat("\n"));
//                                    emailList = msg.replaceAll("\n", "|");
//                                    Log.e("values----img",emailList);
//                                }
//                            }else if(listModel.get(i).getIntType() == 4) {
//                                if (listModel.get(i).getChoice_desc() != null && listModel.get(i).getChoice_desc().length > 0) {
//                                    if (i != 0) {
//                                        if (i % 2 == 0) {
//                                            img_pos++;
//                                        }
//                                    }
//                                    Log.e("values----1", listModel.get(i).getFname().concat(" => ") + "   " + listModel.get(i).getChoice_desc()[img_pos]);
//                                    Log.e("values----2", listModel.get(i).getFname().concat(" => ") + "   " + listModel.get(i).getChoice_desc()[img_pos + 1]);
//
//                                    image1 =  listModel.get(i).getChoice_desc()[img_pos];
//                                    image2 = listModel.get(i).getChoice_desc()[img_pos + 1];
//                                    if(image1!=null &&image1.contains("jpg") ) {
//                                        if(image2 != null && image2.contains("jpg")) {
//                                            msg = msg.concat("img_" + listModel.get(i).getFkey().concat(" => ").concat("1,2").concat("\n"));
//                                            emailList = msg.replaceAll("\n", "|");
//                                            Log.e("values----image1", emailList);
//                                        }else{
//                                            msg = msg.concat("img_" + listModel.get(i).getFkey().concat(" => ").concat("1").concat("\n"));
//                                            emailList = msg.replaceAll("\n", "|");
//                                            Log.e("values---image2",emailList);
//                                        }
//                                    }else{
//                                        msg = msg.concat("img_" + listModel.get(i).getFkey().concat(" => ").concat("1").concat("\n"));
//                                        emailList = msg.replaceAll("\n", "|");
//                                        Log.e("values---image2",emailList);
//                                    }
//                                    if (image1 == null){
//                                        image1 = "";
//                                    }
//                                    if (image2 == null){
//                                        image2 = "";
//                                    }
//                                    String imgname = listModel.get(i).getFkey();
//                                    images.add(image1);
//                                    images.add(image2);
//                                    img_names.add(imgname);
//
//                                }
//                            }
////
//
//
//                        }
//                    }
//                    if(!emailList.equalsIgnoreCase("") && emailList.length() > 0) {
//                        emailList = emailList.substring(0, emailList.length() - 1);
//                        Log.e("c", emailList);
//                    }
//                    if(emailList.contains("Please Select")){
//                        emailList = emailList.replace("Please Select","");
//                    }
//                    msgstring.add((emailList));
//                    msg = "";
//                    emailList = "";
//                }
//
//                for(int i = 0 ;i<mainheading.size();i++) {
//                    int img_name_pos = 0;
//                    for(int j=0;j<images.size();j++){
//                        if(j % 2 ==0){
//                            if (j != 0){
//                                img_name_pos++;
//                            }
////                        try {
//                            Log.e("values", "allfiles["+ mainheading.get(i) +"]" + "[img_" + img_names.get(img_name_pos) + "_1]");
//                            images_file_name.add("allfiles["+ mainheading.get(i) +"]" + "[img_"+img_names.get(img_name_pos)+"_1]");
//
////                        }catch (Exception e){
////                            e.printStackTrace();
////                            Log.e("error1","error");
////                        }
//                        }else{
////                        try {
//                            //                        Log.e("values","allflies[" + mainheading.get(i) + "]" + "[img_"+img_names.get(j)+"_2]");
//                            images_file_name.add("allfiles["+ mainheading.get(i) +"]"+"[img_"+img_names.get(img_name_pos)+"_2]");
////                        }catch (Exception e){
////                            e.printStackTrace();
////                            Log.e("error2","error2");
////                        }
//
//                        }
//                    }
//                    if(images.size()==2){
//                        params.put("data[" + mainheading.get(i) + "]", "["+msgstring.get(i)+"]");
//                    }else{
//                        params.put("data[" + mainheading.get(i) + "]", "["+msgstring.get(i)+"]");
//                    }
//
//                }
//
//
//
//                Log.e("params", String.valueOf(params));
//                Log.e("images", String.valueOf(images.size()));
//
//                Log.e("values----filename",images_file_name.toString());
//
//            }else if (stage.equals("conclusion")) {
//                try {
//                    String[] usp_split = usp_data.split("\\|");
//                    params.put("menuId", 4 + "");
//                    for (String anUsp_split : usp_split) {
//                        String[] usp_values = anUsp_split.split("=>");
//                        try {
//                            if (usp_values[0].contains("img")) {
//                                String[] q = usp_values[1].split("/");
//                                int idx = q.length - 1;
//                                images.add(f_path+q[idx]);
//                                img_names.add(usp_values[0]);
//                            }else {
//                                params.put(usp_values[0], usp_values[1]);
//                            }
//                        } catch (Exception e) {
//                            params.put(usp_values[0], "");
//                        }
//                    }
//                } catch (Exception e) {
//                    Log.e(TAG, "Error4 " + e.toString());
//                }
//            }
//
//
//            Log.e("params---test", String.valueOf(params));
//
//            String result = multipartRequest(Api.obd_insert, params, images, images_file_name, "image/*");
//            Log.e(TAG ,"res 0 "+result);
//            Log.e("USP",params.toString());
//
//        }
//    }
//
//    private String get_check_hybrids(String pd_number) {
//        String ret_s = null;
//        try {
//            GetWareHouseDetails = dbGetMaster.rawQuery("select hybrid from PdregistrationMaster where pdRegNum = '" + pd_number + "'", null);
//
//            if (GetWareHouseDetails.moveToFirst()) {
//                ret_s = GetWareHouseDetails.getString(0);
//                Log.e("Values_hycode",ret_s);
//            }
//            return ret_s;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return ret_s;
//    }
//
//
//    private void gps_function() {
//        final LocationManager manager = (LocationManager) Objects.requireNonNull(mCtx).getSystemService( Context.LOCATION_SERVICE );
//        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
////            displayLocationSettingsRequest(this);
//        }
//        else {
//            gps = new GPSTracker(mCtx);
//            // check if GPS enabled
//            if (gps.canGetLocation()) {
//                latitude = gps.getLatitude();
//                longitude = gps.getLongitude();
//                //  strAdd = getAddressFromLocation(latitude, longitude);
////                    statusCheck();
//                strAdd = getCompleteAddressString(latitude, longitude);
//
//
//            }
//        }
//    }
//
//    @SuppressLint("LongLogTag")
//    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
//        String strAdd = "";
//        Geocoder geocoder = new Geocoder(mCtx, Locale.getDefault());
//        try {
//            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
//            if (addresses != null) {
//                Address returnedAddress = addresses.get(0);
//                StringBuilder strReturnedAddress = new StringBuilder("");
//
//                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
//                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
//                }
//                strAdd = strReturnedAddress.toString();
//                Log.e("myaddress----",strAdd);
//                Log.w("My Current loction address", strReturnedAddress.toString());
//            } else {
//                Log.w("My Current loction address", "No Address returned!");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.w("My Current loction address", "Canont get Address!");
//        }
//        return strAdd;
//    }
//
//    private String multipartRequest(final String urlTo, final Map<String, String> parmas, final ArrayList<String> filepaths, final ArrayList<String> file_names, final String fileMimeType) {
//
//        class UploadFileAsync extends AsyncTask<String, Void, String> {
//
//            @Override
//            protected String doInBackground(String... strings) {
//                HttpURLConnection connection = null;
//                DataOutputStream outputStream = null;
//                InputStream inputStream = null;
//
//                String twoHyphens = "--";
//                String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
//                String lineEnd = "\r\n";
//
//                try {
//
//                    URL url = new URL(urlTo);
//                    connection = (HttpURLConnection) url.openConnection();
//
//                    connection.setDoInput(true);
//                    connection.setDoOutput(true);
//                    connection.setUseCaches(false);
//
//                    connection.setRequestMethod("POST");
//                    connection.setRequestProperty("Connection", "Keep-Alive");
//                    connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
//                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
//
//                    outputStream = new DataOutputStream(connection.getOutputStream());
//                    for (int i = 0 ; i < filepaths.size() ;i++) {
//                        int bytesRead, bytesAvailable, bufferSize;
//                        byte[] buffer;
//                        int maxBufferSize = 1 * 1024 * 1024;
//                        String[] q = filepaths.get(i).split("/");
//                        int idx = q.length - 1;
//                        File file = new File(filepaths.get(i));
//                        Log.e(TAG ,"sending image "+file_names.get(i));
//                        if (file.exists()) {
//                            FileInputStream fileInputStream = new FileInputStream(file);
//
//                            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
//                            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + file_names.get(i) + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
//                            outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
//                            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
//                            outputStream.writeBytes(lineEnd);
//
//                            bytesAvailable = fileInputStream.available();
//                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                            buffer = new byte[bufferSize];
//
//                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//                            while (bytesRead > 0) {
//                                outputStream.write(buffer, 0, bufferSize);
//                                bytesAvailable = fileInputStream.available();
//                                bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//                            }
//
//                            outputStream.writeBytes(lineEnd);
//                            fileInputStream.close();
//                        }else {
//                            Log.e(TAG ,"error no file");
//                        }
//                    }
//
//                    // Upload POST Data
//                    for (String key : parmas.keySet()) {
//                        String value = parmas.get(key);
//                        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
//                        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
//                        outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
//                        outputStream.writeBytes(lineEnd);
//                        outputStream.writeBytes(value);
//                        outputStream.writeBytes(lineEnd);
//                    }
//
//                    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//
//                    if (200 != connection.getResponseCode()) {
//                        Log.e(TAG,"Failed to upload code:" + connection.getResponseCode() + " " + connection.getResponseMessage());
//                    }
//
//                    inputStream = connection.getInputStream();
//
//                    result = convertStreamToString(inputStream);
//
//                    inputStream.close();
//                    outputStream.flush();
//                    outputStream.close();
//
//                    return result;
//                } catch (Exception e) {
//                    Log.e(TAG ,"error "+e.toString());
//                }
//                return result;
//
//            }
//
//            @Override
//            protected void onPostExecute(String result) {
//                try {
//                    Log.e(TAG, "upload result " + result);
//
//                    if (result .equals("success")) {
//                        try {
//                            if ((Integer.parseInt(offline_id) == int_id)) {
//                                Log.e("offline_data",offline_id + int_id);
//                                int_id = 0;
//                                if (dialog != null) {
//                                    dialog.cancel();
//                                }
//
//                                Delete_Observation_data();
//                                Toast.makeText(mCtx, "Observation Added Successfully", Toast.LENGTH_SHORT).show();
//                                sessionsave.session_clear();
//                            } else if ((Integer.parseInt(offline_id)  != int_id)) {
//                                Log.e("offline_data---",offline_id + int_id);
//                                upload_data((Integer.parseInt(offline_id) + 1));
////                                dbGetMaster.execSQL("DELETE FROM ObservationData WHERE id ='" + offline_id + "'");
//
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }catch (Exception e){
//                    dialog.cancel();
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            protected void onPreExecute() {
//            }
//
//            @Override
//            protected void onProgressUpdate(Void... values) {
//            }
//        }
//        new UploadFileAsync().execute("");
//        return result;
//    }
//
//    private String convertStreamToString(InputStream is) {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//        StringBuilder sb = new StringBuilder();
//
//        String line;
//        try {
//            while ((line = reader.readLine()) != null) {
//                sb.append(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                is.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return sb.toString();
//    }
//
//    private void update_table() {
//
//        try {
//            dbGetMaster.execSQL("ALTER TABLE ObservationData ADD COLUMN grower_code VARCHAR");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }     try {
//            dbGetMaster.execSQL("ALTER TABLE ObservationData ADD COLUMN pd_register_num VARCHAR");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }try {
//            dbGetMaster.execSQL("ALTER TABLE ObservationData ADD COLUMN pd_status VARCHAR");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void Delete_Observation_data() {
//        String DATABASE_TABLE_ObservationData="ObservationData";
//        dbGetMaster.execSQL("DROP TABLE IF EXISTS '" + DATABASE_TABLE_ObservationData + "'");
//
//        dbGetMaster.execSQL("CREATE TABLE IF NOT EXISTS ObservationData(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, CropCode VARCHAR,CropName VARCHAR,Company VARCHAR,BreederCode VARCHAR,HVCode VARCHAR,Variety VARCHAR,Stage VARCHAR,State VARCHAR,City VARCHAR,CreatedBy VARCHAR,Master_data VARCHAR,General_data VARCHAR,Observe_data VARCHAR,usp_data VARCHAR ,CreatedDateTime VARCHAR,Location VARCHAR);");
//    }
//
//    private void get_data(final int i) {
//        if (alert.isNetworkAvailable()) {
//            @SuppressLint("StaticFieldLeak")
//            class GetDataJSON extends AsyncTask<String, Void, String> {
//
//                @Override
//                protected String doInBackground(String... params) {
//                    HttpPost httppost = null;
//                    DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
//
//                    if (i == 0) {
//                        Log.e(TAG ,Api.get_observation(sessionsave.get_company()));
//                        httppost = new HttpPost(Api.get_observation(sessionsave.get_company()));
//                        Log.e("url",Api.get_observation(sessionsave.get_company()));
//                    }else if (i == 1) {
//                        Log.e(TAG ,Api.get_breed(sessionsave.get_company()));
//                        httppost = new HttpPost(Api.get_breed(sessionsave.get_company()));
//                        Log.e("url",Api.get_breed(sessionsave.get_company()));
////                        Log.e(TAG ,Api.get_hv_codes(sessionsave.get_company()));
////                        httppost = new HttpPost(Api.get_hv_codes(sessionsave.get_company()));
//                    }else if (i == 2) {
//                        Log.e(TAG ,"http://103.44.97.110:8080/kanagaraj/"+sessionsave.get_folder()+"/ISCropMaster.php");
////                        httppost = new HttpPost(Api.crop_master(sessionsave.get_folder()));
//                        httppost = new HttpPost("http://103.44.97.110:8080/kanagaraj/"+sessionsave.get_folder()+"/ISCropMaster.php");
//                    }else if (i == 3) {
//                        Log.e(TAG ,"http://103.44.97.110:8080/kanagaraj/pd_trail/StateMaster.php?Company="+sessionsave.get_company());
////                        httppost = new HttpPost(Api.get_state(sessionsave.get_company()));
//                        httppost = new HttpPost("http://103.44.97.110:8080/kanagaraj/pd_trail/StateMaster.php?Company="+sessionsave.get_company());
//                    }else if (i == 4) {
//                        Log.e(TAG ,"http://103.44.97.110:8080/kanagaraj/Pd_trail_Dev/SeasonMaster.php?Company="+sessionsave.get_company());
////                        httppost = new HttpPost(Api.get_state(sessionsave.get_company()));
//                        httppost = new HttpPost("http://103.44.97.110:8080/kanagaraj/Pd_trail_Dev/SeasonMaster.php?Company="+sessionsave.get_company());
//                    }else if (i == 5) {
//                        Log.e(TAG ,"http://103.44.97.110:8080/kanagaraj/Pd_trail_Dev/YearMaster.php?Company="+sessionsave.get_company());
////                        httppost = new HttpPost(Api.get_state(sessionsave.get_company()));
//                        httppost = new HttpPost("http://103.44.97.110:8080/kanagaraj/Pd_trail_Dev/YearMaster.php?Company="+sessionsave.get_company());
//                    }else if (i == 6) {
//                        Log.e(TAG ,"http://103.44.97.110:8080/kanagaraj/Pd_trail_Dev/HybridsMaster.php?Company="+sessionsave.get_company());
////                        httppost = new HttpPost(Api.get_state(sessionsave.get_company()));
//                        httppost = new HttpPost("http://103.44.97.110:8080/kanagaraj/Pd_trail_Dev/HybridsMaster.php?Company="+sessionsave.get_company());
//                    }
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
//                    try {
//                        Log.e(TAG ,i+":"+result);
//                        save_database(result ,i);
//                    } catch (Exception e) {
//                        Toast.makeText(mCtx, "Error", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//            GetDataJSON g = new GetDataJSON();
//            g.execute();
//        }
//    }
//
//    private void save_database(String result ,int i) {
//        if (i == 0) {
//            save_observation_master(result);
//            get_data(1);
//        }else if (i == 1){
//            save_breed(result);
////            get_data(2);
//        }else if (i == 2){
//            save_crop(result);
////            get_data(3);
//        }else if (i == 3){
//            save_states(result);
//        }else if (i == 4){
//            save_season(result);
//        } else if (i == 5){
//            save_year(result);
//        }else if (i == 6){
//            save_hybrid(result);
//        }
//    }
//
//    private void save_hybrid(String result) {
//        try {
//            JSONArray jsonObj = new JSONArray(result);
//
//            for (int i = 0; i < jsonObj.length(); i++) {
//                JSONObject c = jsonObj.getJSONObject(i);
//                String codes_chhy = "";
//                String crop_code = c.getString("cropcode");
//                String hv_code = c.getString("hvcode");
//                String check_hyb = c.getString("check");
//                String company = c.getString("company");
//
//                if(check_hyb.contains("1")){
//                    codes_chhy = hv_code+ "  -  "+"Chk-Hyb(YES)";
//                }else if(check_hyb.contains("0")){
//                    codes_chhy = hv_code;
//                }
//
//                String query = "INSERT INTO HybridMaster (CropCode,hvcode,check_hyb,company,codes_chk) VALUES('" + crop_code + "','" + hv_code + "','" + check_hyb + "','" + company + "','" + codes_chhy + "');";
//                dbGetMaster.execSQL(query);
//            }
//        }catch(JSONException e){
//            Log.e(TAG, "error " + e.toString());
//        }
//    }
//
//    private void save_year(String result) {
//
//        try {
//            JSONArray jsonObj = new JSONArray(result);
//
//            for (int i = 0; i < jsonObj.length(); i++) {
//                JSONObject c = jsonObj.getJSONObject(i);
//
//                String year_code = c.getString("code");
//                String year_desc = c.getString("desc");
//                String pd_year = c.getString("pdyear");
//
//                String query = "INSERT INTO YearMaster (YearCode,YearDesc,PdYear) VALUES('" + year_code + "','" + year_desc + "','" + pd_year + "');";
//                dbGetMaster.execSQL(query);
//            }
//            get_data(6);
//        }catch(JSONException e){
//            Log.e(TAG, "error " + e.toString());
//        }
//    }
//
//    private void save_season(String result) {
//
//        try{
//            JSONArray jsonObj = new JSONArray(result);
//
//            for (int i = 0; i < jsonObj.length(); i++) {
//
//                String season = jsonObj.getString(i);
//                Log.e("values",season);
//                String query = "INSERT INTO SeasonMaster(SeasonName) VALUES('" + season + "');";
//                dbGetMaster.execSQL(query);
//            }
//            get_data(5);
//
//        } catch (JSONException e) {
//            Log.e(TAG ,"error "+e.toString());
//        }
//    }
//
//    private void save_states(String result) {
//        try {
//            JSONArray jsonObj = new JSONArray(result);
//
//            for (int i = 0; i < jsonObj.length(); i++) {
//                JSONObject c = jsonObj.getJSONObject(i);
//
//                String c_val = c.getString("cval");
//                String c_desc = c.getString("cdesc");
//
//                String query = "INSERT INTO StateMaster (StateCode,StateName) VALUES('" + c_val + "','" + c_desc + "');";
//                dbGetMaster.execSQL(query);
//            }
//            get_data(4);
//            if (dialog!=null){
//                dialog.cancel();
//                Toast.makeText(mCtx, "Data Imported Successfully", Toast.LENGTH_SHORT).show();
//            }
//        } catch (JSONException e) {
//            Log.e(TAG ,"error "+e.toString());
//        }
//    }
//
//    private void save_crop(String result) {
//        try {
//            JSONObject jsonObj = new JSONObject(result);
//            JSONArray peoples = jsonObj.getJSONArray(TAG_RESULTS);
//            for (int i = 0 ; i < peoples.length() ; i++){
//                JSONObject object = peoples.getJSONObject(i);
//
//                String Id = object.getString(Api.T1);
//                String A = object.getString(Api.T2);
//                String B = object.getString(Api.T3);
//
//                String query = "INSERT INTO CropMaster (CropCode,Name,CropType ) VALUES('" + Id + "','" + A + "','" + B + "');";
//                dbGetMaster.execSQL(query);
//            }
//            get_data(3);
//        }catch (Exception e){
//            Log.e(TAG ,"error "+e.toString());
//        }
//    }
//
//    private void save_breed(String result) {
//        try {
//            JSONArray jsonArray = new JSONArray(result);
//
//
//            for (int i = 0 ; i < jsonArray.length() ; i++){
//                JSONObject object = jsonArray.getJSONObject(i);
//
//                String A = object.getString(Api.T2);
//                String B = object.getString(Api.T3);
//                String C = object.getString(Api.T4);
//                String D = object.getString(Api.T5);
//
//                String query = "INSERT INTO BreederMaster(CropCode,BreedCode,HVCode,Variety) VALUES('" + A + "','" + B + "','" + C + "','" + D + "');";
//                dbGetMaster.execSQL(query);
//            }
//            get_data(2);
//        }catch (Exception e){
//            Log.e(TAG ,"error "+e.toString());
//        }
//    }
//
//    private void save_observation_master(String ob_data) {
//        try {
//            JSONObject jsonObj = new JSONObject(ob_data);
//
//            Iterator<String> iter = jsonObj.keys();
//            while (iter.hasNext()) {
//                String key = iter.next();
//                try {
//                    Object value = jsonObj.get(key);
//                    Log.e(TAG , "key: "+key+" Value "+String.valueOf(value));
//                    String query = "INSERT INTO ObservationMaster(CropCode,CropData) VALUES('" + key + "','" + value + "');";
//                    dbGetMaster.execSQL(query);
//                } catch (JSONException e) {
//                    // Something went wrong!
//                    Log.e(TAG ,"ob error "+e.toString());
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e(TAG ,"error "+e.toString());
//        }
//    }
//
//    private void Delete_create_GetMasterDatabase() {
//
//        String DATABASE_TABLE_ObservationMaster="ObservationMaster";
//        dbGetMaster.execSQL("DROP TABLE IF EXISTS '" + DATABASE_TABLE_ObservationMaster + "'");
//        String DATABASE_TABLE_BreederMaster="BreederMaster";
//        dbGetMaster.execSQL("DROP TABLE IF EXISTS '" + DATABASE_TABLE_BreederMaster + "'");
//        String DATABASE_TABLE_CropMaster="CropMaster";
//        dbGetMaster.execSQL("DROP TABLE IF EXISTS '" + DATABASE_TABLE_CropMaster + "'");
//        String DATABASE_TABLE_StateMaster="StateMaster";
//        dbGetMaster.execSQL("DROP TABLE IF EXISTS '" + DATABASE_TABLE_StateMaster + "'");
//        String DATABASE_TABLE_ObservationData="ObservationData";
//        dbGetMaster.execSQL("DROP TABLE IF EXISTS '" + DATABASE_TABLE_ObservationData + "'");
//        String DATABASE_TABLE_ObservationImage="ObservationImage";
//        dbGetMaster.execSQL("DROP TABLE IF EXISTS '" + DATABASE_TABLE_ObservationImage + "'");
//        String DATABASE_TABLE_SeasonMaster ="SeasonMaster";
//        dbGetMaster.execSQL("DROP TABLE IF EXISTS '" + DATABASE_TABLE_SeasonMaster + "'");
//        String DATABASE_TABLE_YearMaster ="YearMaster";
//        dbGetMaster.execSQL("DROP TABLE IF EXISTS '" + DATABASE_TABLE_YearMaster + "'");
//        String DATABASE_TABLE_HybridMaster ="HybridMaster";
//        dbGetMaster.execSQL("DROP TABLE IF EXISTS '" + DATABASE_TABLE_HybridMaster + "'");
//
//
//        dbGetMaster.execSQL("CREATE TABLE IF NOT EXISTS ObservationMaster(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, CropCode VARCHAR ,CropData VARCHAR);");
//        dbGetMaster.execSQL("CREATE TABLE IF NOT EXISTS BreederMaster(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, CropCode VARCHAR ,BreedCode VARCHAR ,HVCode VARCHAR ,Variety VARCHAR);");
//        dbGetMaster.execSQL("CREATE TABLE IF NOT EXISTS CropMaster(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, CropCode VARCHAR,Name VARCHAR,CropType VARCHAR);");
//        dbGetMaster.execSQL("CREATE TABLE IF NOT EXISTS StateMaster(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, StateCode VARCHAR,StateName VARCHAR);");
//        dbGetMaster.execSQL("CREATE TABLE IF NOT EXISTS ObservationData(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, CropCode VARCHAR,CropName VARCHAR,Company VARCHAR,BreederCode VARCHAR,HVCode VARCHAR,Variety VARCHAR,Stage VARCHAR,State VARCHAR,City VARCHAR,CreatedBy VARCHAR,Master_data VARCHAR,General_data VARCHAR,Observe_data VARCHAR,usp_data VARCHAR ,CreatedDateTime VARCHAR,Location VARCHAR);");
//        dbGetMaster.execSQL("CREATE TABLE IF NOT EXISTS ObservationImage(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, Master VARCHAR,General VARCHAR,Observation VARCHAR,Usp VARCHAR,CropCode VARCHAR);");
//        dbGetMaster.execSQL("CREATE TABLE IF NOT EXISTS SeasonMaster(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,SeasonName VARCHAR);");
//        dbGetMaster.execSQL("CREATE TABLE IF NOT EXISTS YearMaster(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,YearCode VARCHAR, YearDesc VARCHAR, PdYear VARCHAR);");
//        dbGetMaster.execSQL("CREATE TABLE IF NOT EXISTS HybridMaster(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,CropCode VARCHAR, hvcode VARCHAR, check_hyb VARCHAR, company VARCHAR,codes_chk VARCHAR);");
//
//    }
//
//    private void Create_delete_pddatabase() {
//
//        String DATABASE_TABLE_PdregistrationnumberMaster ="PdregistrationMaster";
//        String DATABASE_TABLE_TriatsMaster ="TriatsMaster";
//        dbGetMaster.execSQL("DROP TABLE IF EXISTS '" + DATABASE_TABLE_PdregistrationnumberMaster + "'");
//        dbGetMaster.execSQL("DROP TABLE IF EXISTS '" + DATABASE_TABLE_TriatsMaster + "'");
//        dbGetMaster.execSQL("CREATE TABLE IF NOT EXISTS PdregistrationMaster(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,pdRegNum VARCHAR, growerCode VARCHAR, Season VARCHAR, cropCode VARCHAR, year VARCHAR,hybrid VARCHAR,check_hybrid VARCHAR,status VARCHAR,grower_code_name VARCHAR);");
//        dbGetMaster.execSQL("CREATE TABLE IF NOT EXISTS TriatsMaster(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,hv_code VARCHAR, position VARCHAR, ob_value VARCHAR);");
//    }
//
//    private String getAddressFromLocation(final double latitude, final double longitude) {
//        Log.e(TAG ,"https://maps.google.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&sensor=true&key=AIzaSyCDUWjHo0zN88TnRyWc0FfEbnKrmGnsDHY");
//        StringRequest strreq = new StringRequest(Request.Method.GET, "https://maps.google.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&sensor=true&key=AIzaSyCqj0weR1XyaVH9unsCRhiGo7qs6NFHcJw", new Response.Listener<String>() {
//
//            // String location_string = null;
//            @Override
//            public void onResponse(String response) {
//                try {
//                    Log.e(TAG, "onResponse: " + response);
//                    JSONObject location = new JSONObject(response);
//                    JSONArray testjson = location.getJSONArray("results");
//
//                    if (testjson.length() == 0) {
//                        Log.e(TAG ,"null location");
//                    } else {
//                        location = testjson.getJSONObject(0);
//                        Log.e(TAG, "onResponse: " + location);
//                        strAdd = location.getString("formatted_address");
//                        Log.e("test", "formatted address:" + strAdd);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("Error listener", "onErrorResponse: " + error.getMessage());
//            }
//        });
//        strreq.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        AppController.getInstance().addToRequestQueue(strreq);
//        return strAdd;
//    }
//}
//
////