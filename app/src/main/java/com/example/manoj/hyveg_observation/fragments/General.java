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
import com.example.manoj.hyveg_observation.adapter.glist_adapter;
import com.example.manoj.hyveg_observation.list.ob_list;
import com.example.manoj.hyveg_observation.services.alertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class General extends Fragment implements IAlertListener {

    TextView text_general;
    RecyclerView recyclerView;
    List<ob_list> observation_list;
    String crop_code = "", myJSON, TAG = "General", from, crop_id ,crop_name = "",grower_code = "",
            pd_number = "",pd_status="",check_hybrid="";;
    alertDialog alert;
    Sessionsave sessionsave;
    private File Oroot = Environment.getExternalStorageDirectory() ,GetMasterPath=null;
    private SQLiteDatabase dbGetMaster;
    private Cursor GetWareHouseDetails;
    public static List check_hyb = new ArrayList<>();


    public General() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_general, container, false);

        recyclerView = rootview.findViewById(R.id.cp_general);
        text_general = rootview.findViewById(R.id.text_general);
        alert = new alertDialog(this.getActivity());
        sessionsave = new Sessionsave(this.getActivity());
        observation_list = new ArrayList<>();

        assert getArguments() != null;
        crop_code = getArguments().getString("crop_code");
        crop_name = getArguments().getString("crop_name");
        grower_code = getArguments().getString("grower_code");
        pd_number = getArguments().getString("pd_number");
        pd_status = getArguments().getString("pd_status");
        check_hybrid = getArguments().getString("check_hybrid");
        check_hyb = Arrays.asList(check_hybrid.split(","));
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

//        switch (crop_select) {
//            case "Tomato":
//                Prepare();
//                break;
//            case "Hot Pepper(Fresh)":
//                Prepare1();
//                break;
//            case "Hot Pepper(Dry)":
//                Prepare2();
//                break;
//        }
//
//        if (crop_select.equals("Hot Pepper(Dry)")) {
//
//            RecyclerView.Adapter adapter = new glist_adapter(getActivity(), mlists, "general", "nodisease");
//            recyclerView.setAdapter(adapter);
//        } else {
//            RecyclerView.Adapter adapter = new glist_adapter(getActivity(), mlists, "general", "disease");
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

            JSONArray obt = jsonObj.getJSONArray("obt");
            JSONObject c = null;
            for (int i = 0; i < obt.length(); i++) {
                ob_list ob = new ob_list();
                try {
                    c = obt.getJSONObject(i);
                    ob.getKey(c.getString("fkey"));
                    ob.getName(c.getString("fname"));
                    ob.getReq(c.getString("req"));
                    try {
                        if (c.getString("valbased") != null){

                            JSONObject val_based = new JSONObject(c.getString("valbased"));
                            Log.e("values----",val_based.toString());

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
                        sessionsave.save_general_instruction(c.getString("fname"));
                    }
//                    if (from.equals("edit")) {
//                        String f_value = c.getString("value");
//                        ob.getValue(f_value);
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                assert c != null;
                if (!c.getString("ftype").equals("instruction")) {
                    observation_list.add(ob);
                }
            }
            RecyclerView.Adapter adapter = new glist_adapter(getActivity(), observation_list, from,grower_code,pd_number,pd_status,crop_code,check_hyb);
            recyclerView.setAdapter(adapter);
            if (sessionsave.get_general_instruction().equals("null")){
                text_general.setVisibility(View.GONE);
            }else {
                text_general.setVisibility(View.VISIBLE);
                text_general.setText(sessionsave.get_general_instruction());
            }
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
//            JSONArray obt = jsonObj.getJSONArray("obt");
//            JSONObject c = null;
//            for (int i = 0; i < obt.length(); i++) {
//                ob_list ob = new ob_list();
//                try {
//                    c = obt.getJSONObject(i);
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
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                observation_list.add(ob);
//            }
//            RecyclerView.Adapter adapter = new glist_adapter(getActivity(), observation_list, from);
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
//        mlist crop = new mlist("First harvest after Sowing:", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Days to first harvest:", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Plant population:", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fruit Shape to be observed at 4th harvest:", "", "spin", new String[]{"select", "FR=Flat round", "RD= Round", "SR= Square round", "SO=Square oval"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fruit Color Mature at 4th harvest:", "", "spin", new String[]{"select", "RD-Red", "DR-DeepRed"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fruit Firmness at 4th harvest:", "", "spin", new String[]{"select", "P=Poor(Soft)", "M=Medium", "G=Good (Firm)"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Avg Fruit Wt (Gm)(After 4 picking):", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Avg Fruit Wt (Gm)(Last Picking):", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fruit shelf life in days:", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fruit set (no of fruits per plant) at 4th harvest:", "", "spin", new String[]{"select", "P=Poor(0-10 fruits)", " M=Medium(11-25 fruits)", "G=Good(>25 fruits)"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Top to bottom fruit set(After 4th picking:", "", "spin", new String[]{"select", " P=Poor", " M=Medium", "G=Good"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fruit uniformity across all pickings:", "", "spin", new String[]{"select", "P=Poor(<70%)", "M=Medium(71-95%)", "G=Good(>95%)"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Picking longivity:", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Number Of Fruits Per Plant:", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fruit Weight Per Plant(Kg):", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//    }
//
//    private void Prepare1() {
//
//        mlist crop = new mlist("First harvest after Sowing:", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Days to first harvest:", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Plant habit:", "", "spin", new String[]{"select", "PR=Prostrate", "ER=Erect", "CO=Compact"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Branching habit:", "", "spin", new String[]{"select", "D=Dense", "IM=intermediate", "S=sparse"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fruit length (cm) (3-4th harvest stage):", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fruit girth (cm) (3-4th harvest stage):", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Pedecal length (cm) (3-4th harvest stage):", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Pericarp thickness (cm):", "", "spin", new String[]{"select", "G-Good(7-10)", "A-Average(4-6)", "L-Less(1-3)"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fruit curving:", "", "spin", new String[]{"select", "SE=Severe", "MO=Moderate", " FR=Free"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fruit set:", "", "spin", new String[]{"select", "G=Good", "A=Average", " L=less"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fruit surface:", "", "spin", new String[]{"select", "S=Smooth", "SW=semi wrinkle", " W=wrinkle"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fruit colour:", "", "spin", new String[]{"select", "DG=Dark green", "G=Green", "LG=Light green", "YG=Yellowish green"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fruit firmness:", "", "spin", new String[]{"select", "G=Good(7-10)", "A=Avg(4-6)", " L=less(1-3)"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fruit pungency:", "", "spin", new String[]{"select", "L=Low", "M=Medium", "H=High"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("No. of pickings :", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fruit shelf life:", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Yield (Two plots of  representative trials):", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//    }
//
//    private void Prepare2() {
//
//        mlist crop = new mlist("First harvest after Sowing:", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Days to first harvest:", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Plant habit:", "", "spin", new String[]{"select", "PR=Prostrate", "ER=Erect", "CO=Compact"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Branching habit:", "", "spin", new String[]{"select", "D=Dense", "IM=intermediate", "S=sparse"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fruit length (cm) (3-4th harvest stage):", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fruit girth (cm) (3-4th harvest stage):", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fruit colour:", "", "spin", new String[]{"select", "(L=Low", "M=Medium", "H=High"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fruit red Surface:", "", "spin", new String[]{"select", "S=Smooth", "VS-Very Smooth", "SW=semi wrinkle", " W=wrinkle"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fruit curving:", "", "spin", new String[]{"select", "SE=Severe", "MO=Moderate", " FR=Free"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Fruit pungency:", "", "spin", new String[]{"select", "L=Low", "M=Medium", "H=High"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Drying Period:", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Seeds per fruit(Av No. of seeds  in 10 fruits):", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("No. of fruits per kg:", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Yield:", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Dry chilli price:", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Wastage:", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Cold storage studies for Good fruits and wastage fruits:", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Spice board analysis:", "", "spin", new String[]{"select", "Oleoresin", "Capsanthin", " Capsaicin"}, "no");
//        mlists.add(crop);
//
//    }

}
