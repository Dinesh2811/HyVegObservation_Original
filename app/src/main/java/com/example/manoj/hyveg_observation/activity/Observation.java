package com.example.manoj.hyveg_observation.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import androidx.annotation.NonNull;

import com.example.manoj.hyveg_observation.fragments.disease;
import com.example.manoj.hyveg_observation.fragments.observation_new;
import com.example.manoj.hyveg_observation.pass_postion_onclick;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manoj.hyveg_observation.Others.CustomViewPager;
import com.example.manoj.hyveg_observation.R;
import com.example.manoj.hyveg_observation.fragments.General;
import com.example.manoj.hyveg_observation.fragments.master;
import com.example.manoj.hyveg_observation.fragments.ob_traits;
import com.example.manoj.hyveg_observation.fragments.usp;
import com.example.manoj.hyveg_observation.services.alertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.manoj.hyveg_observation.fragments.observation_new.mHeadingList;

public class Observation extends AppCompatActivity implements pass_postion_onclick {

    private CustomViewPager viewPager;
    String item, crop_code = "", from, crop_id, no_desc = "0", TAG = "observation", crop_name = "",grower_code = "",
                 pd_number = "",pd_status = "",check_hybrids = "null",observation_type;
    IntentFilter intentFilter;
    alertDialog alert;
    TabLayout.Tab tab;
    TabLayout tabLayout;
    Sessionsave sessionsave;

    private SQLiteDatabase dbGetMaster;
    private Cursor GetWareHouseDetails;
    File Oroot = Environment.getExternalStorageDirectory() ,GetMasterPath=null;

    public static List check_hyb = new ArrayList<>();

    String status;

    int  pass_pos;
    String pass_string;

    TextView text_hvcode;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        text_hvcode = findViewById(R.id.text_hvcode);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        if (Oroot.canWrite()) {
            File dir = new File(Oroot.getAbsolutePath() + "/Android/Observation");
            dir.mkdirs();

            GetMasterPath = new File(dir, "GetMasterDB.db");

        }


        alert = new alertDialog(this);
        sessionsave = new Sessionsave(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("CHAT_RECEIVED");

        toolbar.setNavigationIcon(R.drawable.back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        dbGetMaster = openOrCreateDatabase(GetMasterPath.getPath(), Context.MODE_PRIVATE, null);

        crop_code = getIntent().getStringExtra("crop_code");
        crop_name = getIntent().getStringExtra("crop_name");
        grower_code = getIntent().getStringExtra("grower_code");
        pd_number = getIntent().getStringExtra("pd_number");
        pd_status = getIntent().getStringExtra("pd_status");
        check_hybrids = getIntent().getStringExtra(("check_hybrid"));
        observation_type = getIntent().getStringExtra("observation_type");
        Log.e("values_hycode----",check_hybrids);
        check_hyb = new ArrayList<>();
        check_hyb.clear();
        check_hyb = Arrays.asList(check_hybrids.split(","));
        Log.e("values----size", String.valueOf(check_hyb.size()));
        sessionsave.save_hybrid(0);
        sessionsave.save_hybrid(check_hyb.size());
        from = getIntent().getStringExtra("from");
        if (from.equals("edit")) {
            crop_id = getIntent().getStringExtra("crop_id");
        }
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);



        tabLayout = findViewById(R.id.tabslayout);
        tabLayout.setupWithViewPager(viewPager);


//        to disable the tab click
        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
        viewPager.disableScroll(true);


//        get_observation_data();
        get_observation_data_db();

        //to disable the swipe on view pager
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        if (alert.isNetworkAvailable()) {
              if(observation_type.contains("Observation")){
                  Log.e("values", observation_type);
                  viewPager.setCurrentItem(1);
              }else if(observation_type.contains("Disease")){
                  viewPager.setCurrentItem(2);
              }else if(observation_type.equals("Conclusion")){
                  viewPager.setCurrentItem(3);
              }else{

              }

        }else{
            GetWareHouseDetails = dbGetMaster.rawQuery("select * from ObservationData WHERE pd_register_num ='" + pd_number + "'", null);
            if(GetWareHouseDetails.getCount()==0){
                if(observation_type.contains("Observation")){
                    Log.e("values", observation_type);
                    viewPager.setCurrentItem(1);
                }else if(observation_type.contains("Disease")){
                    viewPager.setCurrentItem(2);
                }else if(observation_type.equals("Conclusion")){
                    viewPager.setCurrentItem(3);
                }else{

                }
            }else {
                if (GetWareHouseDetails.moveToFirst()) {
                    status = GetWareHouseDetails.getString(7);
                    if (status.equals("general")) {
                        viewPager.setCurrentItem(1);
                    }else if(status.equals("observe")) {
                        viewPager.setCurrentItem(2);
                    }else if(status.equals("disease")){
                        viewPager.setCurrentItem(3);
                    }
                    Log.e("values", status);
                }
            }
        }



//        tabLayout.setOnTabSelectedListener(
//                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
//                    @Override
//                    public void onTabSelected(TabLayout.Tab tab) {
//                        super.onTabSelected(tab);
//                        Toast.makeText(Observation.this, "" + tab.getPosition(), Toast.LENGTH_SHORT).show();
//                        switch(tab.getPosition()) {
//                            case 0:
//                                cropselect.general_click = 1;
//                                viewPager.setCurrentItem(0);
//                                Toast.makeText(Observation.this, "Values entered for general condiitons", Toast.LENGTH_SHORT).show();
//                                break;
//                            case 1:
//                                viewPager.setCurrentItem(1);
//                                break;
//                            case 2:
//                                viewPager.setCurrentItem(2);
//                                break;
//                            case 3:
//                                viewPager.setCurrentItem(3);
//                                break;
//
//
//                        }
//                    }
//                });

    }

//    @Override
//    protected void onResumeFragments() {
//        super.onResumeFragments();
//        try {
//            Log.d("intefaces---try", String.valueOf(sessionsave.gethvcodes()));
//            text_hvcode.setText(sessionsave.gethvcodes());
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.d("intefaces---catch", String.valueOf(pass_pos));
//        }
//
//    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new master(), "General Conditions");
//        adapter.addFragment(new General(), "Observation traits");
        adapter.addFragment(new observation_new(), "Observation traits");
//        adapter.addFragment(new ob_traits(), "Disease reaction");
        adapter.addFragment(new disease(), "Disease reaction");
        adapter.addFragment(new usp(), "Conclusion");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void pass_position(int position, String view_type) {

        Log.d("intefaces-----sctivity", String.valueOf(position));

        pass_pos = position;
        pass_string = view_type;

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("crop_code", crop_code);
                    bundle1.putString("crop_name", crop_name);
                    bundle1.putString("grower_code", grower_code);
                    bundle1.putString("pd_number", pd_number);
                    bundle1.putString("pd_status", pd_status);
                    bundle1.putString("from", from);
                    if (from.equals("edit")) {
                        bundle1.putString("crop_id", crop_id);
                    }
                    master masters = new master();
                    masters.setArguments(bundle1);
                    return masters;
                case 1:

                    Bundle bundle2 = new Bundle();
                    bundle2.putString("crop_code", crop_code);
                    bundle2.putString("crop_name", crop_name);
                    bundle2.putString("grower_code", grower_code);
                    bundle2.putString("pd_number", pd_number);
                    bundle2.putString("pd_status", pd_status);
                    bundle2.putString("check_hybrid", check_hybrids);
                    bundle2.putString("from", from);
                    if (from.equals("edit")) {
                        bundle2.putString("crop_id", crop_id);
                    }
                    observation_new general = new observation_new();
                    general.setArguments(bundle2);
                    return general;
                case 2:
                    if (no_desc.equals("1")) {
                        Bundle bundle3 = new Bundle();
                        bundle3.putString("crop_code", crop_code);
                        bundle3.putString("crop_name", crop_name);
                        bundle3.putString("grower_code", grower_code);
                        bundle3.putString("pd_number", pd_number);
                        bundle3.putString("pd_status", pd_status);
                        bundle3.putString("from", from);
                        if (from.equals("edit")) {
                            bundle3.putString("crop_id", crop_id);
                        }
                        usp usp = new usp();
                        usp.setArguments(bundle3);
                        return usp;
                    }
                    else {
                        Bundle bundle3 = new Bundle();
                        bundle3.putString("crop_code", crop_code);
                        bundle3.putString("crop_name", crop_name);
                        bundle3.putString("grower_code", grower_code);
                        bundle3.putString("pd_number", pd_number);
                        bundle3.putString("pd_status", pd_status);
                        bundle3.putString("check_hybrid", check_hybrids);
                        bundle3.putString("from", from);
                        if (from.equals("edit")) {
                            bundle3.putString("crop_id", crop_id);
                        }
                        disease ob_traits = new disease();
                        ob_traits.setArguments(bundle3);
                        return ob_traits;
                    }
                case 3:
                    Bundle bundle4 = new Bundle();
                    bundle4.putString("crop_code", crop_code);
                    bundle4.putString("crop_name", crop_name);
                    bundle4.putString("grower_code", grower_code);
                    bundle4.putString("pd_number", pd_number);
                    bundle4.putString("pd_status", pd_status);
                    bundle4.putString("from", from);
                    if (from.equals("edit")) {
                        bundle4.putString("crop_id", crop_id);
                    }
                    usp usp = new usp();
                    usp.setArguments(bundle4);
                    return usp;
            }
            return null;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return mFragmentTitleList.get(position);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return super.getItemPosition(object);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver(intentReceiver, intentFilter);
    }

    private void get_observation_data_db(){
        String ret_s = null;
        GetWareHouseDetails = dbGetMaster.rawQuery("select * from ObservationMaster where CropCode = '"+crop_code+"'", null);

        if (GetWareHouseDetails.moveToFirst()) {
            ret_s = GetWareHouseDetails.getString(2);
        }

        if (ret_s == null){
            Toast.makeText(this, "No data available for this crop", Toast.LENGTH_SHORT).show();
        }else {
            spilt_data(ret_s);
        }
    }

    private void spilt_data(String s) {
        try {
            JSONObject jsonObj = new JSONObject(s);

//            JSONArray gc = jsonObj.getJSONArray("gc");
//            JSONArray obt = jsonObj.getJSONArray("obt");
            JSONArray dr = jsonObj.getJSONArray("dr");
//            JSONArray cn = jsonObj.getJSONArray("cn");
            if (dr.length() == 0) {
                tab = tabLayout.getTabAt(2);
                tabLayout.removeTab(tab);
                no_desc = "1";
                Log.e(TAG, "present");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            tab = tabLayout.getTabAt(2);
            tabLayout.removeTab(tab);
            no_desc = "1";
        }
    }

//    private void spilt_data() {
//        try {
//            JSONObject jsonObj = new JSONObject(myJSON);
//
//            //            JSONArray gc = jsonObj.getJSONArray("gc");
//            //            JSONArray obt = jsonObj.getJSONArray("obt");
//            JSONArray dr = jsonObj.getJSONArray("dr");
//            //            JSONArray cn = jsonObj.getJSONArray("cn");
//            if (dr.length() < 0) {
//                Log.e(TAG, "present");
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            tab = tabLayout.getTabAt(2);
//            tabLayout.removeTab(tab);
//            no_desc = "1";
//        }
//    }

    //    private void Prepare() {
//
//        mlist crop = new mlist("Person conducting trial: ", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
////        crop=new mlist("Grower name :","","yes",new String[]{"others"},"no");
////        mlists.add(crop);
////
////        crop=new mlist("HV Code:","","yes",new String[]{"others"},"no");
////        mlists.add(crop);
////
////        crop=new mlist("Segment:","","yes",new String[]{"others"},"no");
////        mlists.add(crop);
////
////        crop=new mlist("Trial Stage:","","spin",new String[]{"Select","RST1","RST2","PD1","PD2","MKT","COM"},"no");
////        mlists.add(crop);
////
////        crop=new mlist("Plot Size:","","yes",new String[]{"others"},"no");
////        mlists.add(crop);
//        crop = new mlist("Grower Name:", "", "yes", new String[]{"others"}, "yes");
//        mlists.add(crop);
//
//        crop = new mlist("Location:", "", "yes", new String[]{"others"}, "yes");
//        mlists.add(crop);
//
//        crop = new mlist("State:", "", "spin", new String[]{"Select", "Andra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "MadyaPradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Orissa", "Punjab", "Rajasthan", "Sikkim", "TamilNadu", "Telagana", "Tripura", "Uttaranchal", "Uttar Pradesh", "West Bengal"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Sowing Date:", "", "date", new String[]{"others"}, "yes");
//        mlists.add(crop);
//
//        crop = new mlist("Planting Date:", "", "date", new String[]{"others"}, "yes");
//        mlists.add(crop);
//
//        crop = new mlist("Soil Type:", "", "spin", new String[]{"select", "SA-Sand(Light)", "Sl-Slit(Medium)", "CL-Clay(Heavy)"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Irrigation Practices:", "", "spin", new String[]{"select", "DR-Drip", "FR-Furrow", "FL-Flood", "RF=Rainfed"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Previous Crop:", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Crop Management:", "", "spin", new String[]{"select", "Poor", "Medium", "Good"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Cultivation System:", "", "spin", new String[]{"select", "BD-Bedsystem", "GD=Ground(flat)", "ST=Staking"}, "no");
//        mlists.add(crop);
//
////        crop=new mlist("Days to First Harvest:","","yes",new String[]{"others"},"no");
////        mlists.add(crop);
////
////        crop=new mlist("Expected Harvest Date:","","yes",new String[]{"others"},"no");
////        mlists.add(crop);
////
////        crop=new mlist("Trial Status:","","spin",new String[]{"Select","Sowing","Vegetative","Fruiting start","Fruiting peak","Fruiting end","Finished"},"no");
////        mlists.add(crop);
////
////        crop=new mlist("Comments:","","yes",new String[]{"others"},"no");
////        mlists.add(crop);
//
//    }
//
//    private void Prepare1() {
//        mlist crop = new mlist("Person conducting trial: ", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Grower Name:", "", "yes", new String[]{"others"}, "yes");
//        mlists.add(crop);
//
//        crop = new mlist("Location:", "", "yes", new String[]{"others"}, "yes");
//        mlists.add(crop);
//
//        crop = new mlist("State:", "", "spin", new String[]{"Select", "Andra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "MadyaPradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Orissa", "Punjab", "Rajasthan", "Sikkim", "TamilNadu", "Telagana", "Tripura", "Uttaranchal", "Uttar Pradesh", "West Bengal"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Sowing Date:", "", "date", new String[]{"others"}, "yes");
//        mlists.add(crop);
//
//        crop = new mlist("Planting Date:", "", "date", new String[]{"others"}, "yes");
//        mlists.add(crop);
//
//        crop = new mlist("Soil Type:", "", "spin", new String[]{"select", "SA-Sand(Light)", "Sl-Slit(Medium)", "CL-Clay(Heavy)"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Irrigation Practices:", "", "spin", new String[]{"select", "DR-Drip", "FR-Furrow", "FL-Flood", "RF=Rainfed"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Previous Crop:", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Crop Management:", "", "spin", new String[]{"select", "Poor", "Medium", "Good"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Cultivation System:", "", "spin", new String[]{"select", "BD-Bedsystem", "GD=Ground(flat)", "ST=Staking"}, "no");
//        mlists.add(crop);
//
//    }
//
//    private void Prepare2() {
//
//        mlist crop = new mlist("Person conducting trial: ", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Grower Name:", "", "yes", new String[]{"others"}, "yes");
//        mlists.add(crop);
//
//        crop = new mlist("Location:", "", "yes", new String[]{"others"}, "yes");
//        mlists.add(crop);
//
//        crop = new mlist("State:", "", "spin", new String[]{"Select", "Andra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "MadyaPradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Orissa", "Punjab", "Rajasthan", "Sikkim", "TamilNadu", "Telagana", "Tripura", "Uttaranchal", "Uttar Pradesh", "West Bengal"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Sowing Date:", "", "date", new String[]{"others"}, "yes");
//        mlists.add(crop);
//
//        crop = new mlist("Planting Date:", "", "date", new String[]{"others"}, "yes");
//        mlists.add(crop);
//
//        crop = new mlist("Soil Type:", "", "spin", new String[]{"select", "SA-Sand(Light)", "Sl-Slit(Medium)", "CL-Clay(Heavy)"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Irrigation Practices:", "", "spin", new String[]{"select", "DR-Drip", "FR-Furrow", "FL-Flood", "RF=Rainfed"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Previous Crop:", "", "yes", new String[]{"others"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Crop Management:", "", "spin", new String[]{"select", "Poor", "Medium", "Good"}, "no");
//        mlists.add(crop);
//
//        crop = new mlist("Cultivation System:", "", "spin", new String[]{"select", "BD-Bedsystem", "GD=Ground(flat)", "ST=Staking"}, "no");
//        mlists.add(crop);
//    }

//    private void get_observation_data() {
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
//                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//            GetDataJSON g = new GetDataJSON();
//            g.execute();
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(intentReceiver);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Exit Observation");
        alertDialogBuilder.setMessage("Do you want to exit this entry session?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                        finish();
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
    }
}
