package com.example.manoj.hyveg_observation.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manoj.hyveg_observation.Interfaces.IAlertListener;
import com.example.manoj.hyveg_observation.R;
import com.example.manoj.hyveg_observation.activity.Sessionsave;
import com.example.manoj.hyveg_observation.adapter.olist_adapter;
import com.example.manoj.hyveg_observation.list.ob_list;
import com.example.manoj.hyveg_observation.services.alertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ob_traits extends Fragment implements IAlertListener {

    TextView text_ob_traits;
    RecyclerView recyclerView;
    List<ob_list> observation_list;
    String crop_code = "", myJSON, TAG = "ob_traits", from, crop_id ,crop_name = "",
            grower_code,pd_number,pd_status;
    alertDialog alert;
    Sessionsave sessionsave;
    private File Oroot = Environment.getExternalStorageDirectory() ,GetMasterPath=null;
    private SQLiteDatabase dbGetMaster;
    private Cursor GetWareHouseDetails;

    public ob_traits() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_ob_traits, container, false);

        recyclerView = rootview.findViewById(R.id.cp_observe);
        text_ob_traits = rootview.findViewById(R.id.text_ob_traits);
        alert = new alertDialog(this.getActivity());
        sessionsave = new Sessionsave(this.getActivity());
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
//        if (crop_select.equals("Tomato")) {
//            Prepare();
//        } else if (crop_select.equals("Hot Pepper(Fresh)")) {
//            Prepare1();
//        }
//
//        if (crop_select.equals("Hot Pepper(Dry)")) {
//            RecyclerView.Adapter adapter = new olist_adapter(getActivity(), mlists, "observe", "nodisease");
//            recyclerView.setAdapter(adapter);
//        } else {
//            RecyclerView.Adapter adapter = new olist_adapter(getActivity(), mlists, "observe", "disease");
//            recyclerView.setAdapter(adapter);
//        }

        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();


//        get_observation_data();
        get_observation_data_db();

    }

    private void get_observation_data_db() {
        String ret_s = null;
        GetWareHouseDetails = dbGetMaster.rawQuery("select * from ObservationMaster where CropCode = '"+crop_code+"'", null);

        if (GetWareHouseDetails.moveToFirst()) {
            ret_s = GetWareHouseDetails.getString(2);
        }

        if (ret_s == null){
            Toast.makeText(getActivity(), "No data available for this crop", Toast.LENGTH_SHORT).show();
        }else {
            spilt_data(ret_s);
        }
    }

    private void spilt_data(String s) {
        observation_list = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(s);

            JSONArray dr = jsonObj.getJSONArray("dr");
            JSONObject c = null;
            for (int i = 0; i < dr.length(); i++) {
                ob_list ob = new ob_list();
                try {
                    c = dr.getJSONObject(i);
                    ob.getKey(c.getString("fkey"));
                    ob.getName(c.getString("fname"));
                    ob.getReq(c.getString("req"));
                    try {
                        if (c.getString("valbased") != null){

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
                                    Log.e(TAG ,"val based error "+e.toString());
                                }
                            }
                        }
                    }catch (Exception e){
                        Log.e(TAG ,"Error no val based ");
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
                    }else if (f_type.equals("number")){
                        ob.get_decimal(c.getString("decimal"));
                        try {
                            ob.get_min(c.getString("minVal"));
                            ob.get_max(c.getString("maxVal"));
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.e(TAG ,"no min & max "+e.toString());
                        }
                    }else if (f_type.equals("instruction")) {
                        sessionsave.save_ob_traits_instruction(c.getString("fname"));
                    }
                    if (from.equals("edit")) {
                        String f_value = c.getString("value");
                        ob.getValue(f_value);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                assert c != null;
                if (!c.getString("ftype").equals("instruction")) {
                    observation_list.add(ob);
                }
            }
            if (sessionsave.get_ob_traits_instruction().equals("null")){
                text_ob_traits.setVisibility(View.GONE);
            }else {
                text_ob_traits.setVisibility(View.VISIBLE);
                text_ob_traits.setText(sessionsave.get_ob_traits_instruction());
            }
            RecyclerView.Adapter adapter = new olist_adapter(getActivity(), observation_list, from,grower_code,pd_number,pd_status,crop_code);
            recyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "No Data Available", Toast.LENGTH_LONG).show();

        }
    }

//    private void get_observation_data() {
//        observation_list = new ArrayList<>();
//
//        if (alert.isNetworkAvailable()) {
//            class GetDataJSON extends AsyncTask<String, Void, String> {
//
//                @Override
//                protected String doInBackground(String... params) {
//                    HttpPost httppost = null;
//                    DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
//                    if (from.equals("crop_select")) {
//                        try {
//                            httppost = new HttpPost(Api.get_observation(URLEncoder.encode(crop_code, "UTF-8"), sessionsave.get_company()));
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        try {
//                            httppost = new HttpPost(Api.ob_data(URLEncoder.encode(crop_id, "UTF-8"), sessionsave.get_company()));
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
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
//                    myJSON = result;
//                    try {
//                        spilt_data();
//                    } catch (Exception e) {
//                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//            GetDataJSON g = new GetDataJSON();
//            g.execute();
//        }
//    }
//
//    private void spilt_data() {
//        try {
//            JSONObject jsonObj = new JSONObject(myJSON);
//
//            JSONArray dr = jsonObj.getJSONArray("dr");
//            JSONObject c = null;
//            for (int i = 0; i < dr.length(); i++) {
//                ob_list ob = new ob_list();
//                try {
//                    c = dr.getJSONObject(i);
//                    ob.getKey(c.getString("fkey"));
//                    ob.getName(c.getString("fname"));
//                    ob.getReq(c.getString("req"));
//                    String f_type = c.getString("ftype");
//                    ob.getType(f_type);
//                    if (f_type.equals("select") || f_type.equals("checkbox")) {
//                        ob.get_multi(c.getString("msel"));
//                        JSONArray choice = c.getJSONArray("choice");
//                        String[] choice_desc = new String[choice.length()];
//                        String[] choice_val = new String[choice.length()];
//                        for (int j = 0; j < choice.length(); j++) {
//                            choice_desc[j] = choice.getJSONObject(j).getString("cdesc");
//                            choice_val[j] = choice.getJSONObject(j).getString("cval");
//                        }
//                        ob.getChoice_desc(choice_desc);
//                        ob.getChoice_val(choice_val);
//                    }else if (f_type.equals("number")){
//                        ob.get_decimal(c.getString("decimal"));
//                    }
//                    if (from.equals("edit")) {
//                        String f_value = c.getString("value");
//                        ob.getValue(f_value);
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                observation_list.add(ob);
//            }
//            RecyclerView.Adapter adapter = new olist_adapter(getActivity(), observation_list, from);
//            recyclerView.setAdapter(adapter);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(getActivity(), "No Data Available", Toast.LENGTH_LONG).show();
//
//        }
//    }

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


//    private void Prepare() {
//
//        mlist crop = new mlist("Heat set:", "", "yes", new String[]{"1=Poor", " 9=Very good"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Tolcv:", "", "spin", new String[]{"select", "I(Immune)=100%", "HR(high resistant)=91-99%", "IR(Intermediate resistance)=51-90%", "S(susceptible)=0-50%"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Blight:", "", "spin", new String[]{"select", "I(Immune)=100%", "HR(high resistant)=91-99%", "IR(Intermediate resistance)=51-90%", "S(susceptible)=0-50%"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("BW:", "", "spin", new String[]{"select", "I(Immune)=100%", "HR(high resistant)=91-99%", "IR(Intermediate resistance)=51-90%", "S(susceptible)=0-50%"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Physiological Disorders (%) :", "", "spin", new String[]{"select", "CR=Cracking", " UR=Uneven ripening", " BER=Blossom end rot)"}, "no");
//        mlists.add(crop);
//
//    }
//
//    private void Prepare1() {
//
//        mlist crop = new mlist("Virus-LCV:", "", "spin", new String[]{"select", "Score I(Immune)=100%", " HR(high resistant)=91-99%", "IR(Intermediate resistance)=51-90%", "S(susceptible)=0-50%"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Virus-CMV:", "", "spin", new String[]{"select", "Score I(Immune)=100%", " HR(high resistant)=91-99%", "IR(Intermediate resistance)=51-90%", "S(susceptible)=0-50%"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Virus-CVMV:", "", "spin", new String[]{"select", "Score I(Immune)=100%", " HR(high resistant)=91-99%", "IR(Intermediate resistance)=51-90%", "S(susceptible)=0-50%"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fungus-PM/Anth/Wilt):", "", "spin", new String[]{"select", "Score I(Immune)=100%", " HR(high resistant)=91-99%", "IR(Intermediate resistance)=51-90%", "S(susceptible)=0-50%"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fungus-Anthracnose:", "", "spin", new String[]{"select", "Score I(Immune)=100%", " HR(high resistant)=91-99%", "IR(Intermediate resistance)=51-90%", "S(susceptible)=0-50%"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fungus-Wilt:", "", "spin", new String[]{"select", "Score I(Immune)=100%", " HR(high resistant)=91-99%", "IR(Intermediate resistance)=51-90%", "S(susceptible)=0-50%"}, "no");
//        mlists.add(crop);
//
//
//    }


}
