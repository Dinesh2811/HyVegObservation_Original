package com.example.manoj.hyveg_observation.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manoj.hyveg_observation.Api;
import com.example.manoj.hyveg_observation.Others.GPSTracker;
import com.example.manoj.hyveg_observation.R;
import com.example.manoj.hyveg_observation.activity.ScreenActivity;
import com.example.manoj.hyveg_observation.activity.Sessionsave;
import com.example.manoj.hyveg_observation.adapter.FormAdapter;
import com.example.manoj.hyveg_observation.adapter.FormAdapterDynamic;
import com.example.manoj.hyveg_observation.adapter.FormAdapterDynamicColumn;
import com.example.manoj.hyveg_observation.adapter.HeadingAdapter;

import com.example.manoj.hyveg_observation.list.FormModel;
import com.example.manoj.hyveg_observation.list.ListModel;
import com.example.manoj.hyveg_observation.pass_postion_onclick;
import com.example.manoj.hyveg_observation.pass_postion_tofrag;
import com.example.manoj.hyveg_observation.services.FeedReaderDbHelper;
import com.example.manoj.hyveg_observation.services.SQLHelper;
import com.example.manoj.hyveg_observation.services.alertDialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.example.manoj.hyveg_observation.activity.Observation.check_hyb;


public class disease extends Fragment implements View.OnClickListener, pass_postion_onclick, pass_postion_tofrag {
    private final static String BUNDLE_CODE = "code";
    private final static String TAG = "DynamicForm";

    private String code;

    private AppCompatButton btnSubmit;
    private RecyclerView rvForm , rvForm1;
    private RecyclerView rvHeading;
    //    private RecyclerView rvTitle;
    private ArrayList<ListModel> mList = new ArrayList<>();
    public static ArrayList<String> mHeadingList = new ArrayList<>();
    private FormAdapterDynamic mAdapter;
    private FormAdapterDynamicColumn mAdapter1;
    private HeadingAdapter mHeadingAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    String crop_code = "", myJSON, from, crop_id ,crop_name = "",grower_code = "",
            pd_number = "",pd_status="",check_hybrid="",strAdd = "";

    //DB
    public SQLiteDatabase db;
    public FeedReaderDbHelper dbHelper;

    //Image
    int pos = -1;
    int parentPos = -1;
    int type = -1;
    int img_pos = -1;
    String mOfflinePathImage = "";


    private File Oroot = Environment.getExternalStorageDirectory() ,GetMasterPath=null;
    private SQLiteDatabase dbGetMaster;
    private Cursor GetWareHouseDetails;

    private ProgressDialog dialog;
    alertDialog alert;

    // GPSTracker class
    GPSTracker gps;
    double latitude, longitude;

    private Sessionsave sessionsave;

    TextView text_usp;

    String[] hvcode;
    int form_id;

    private ArrayList<ListModel> listModel;

    String image1,image2;

    TextView text_hvcode;
    int  pass_pos;
    String pass_string;

    public static void startActivity(Context context, String code) {
        Intent intent = new Intent(context, observation_new.class);
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_CODE, code);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public disease() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gps_function();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_disease, container, false);
        View rootview = inflater.inflate(R.layout.fragment_disease, container, false);

        gps_function();


        dialog = new ProgressDialog(getActivity());
        alert = new alertDialog(getActivity());
        sessionsave = new Sessionsave(getActivity());

        rvForm = rootview.findViewById(R.id.rvForm);
        rvForm1 = rootview.findViewById(R.id.rvForm1);
        rvHeading = rootview.findViewById(R.id.rvHeading);
//        rvTitle = findViewById(R.id.rvTitle);
        btnSubmit = rootview.findViewById(R.id.btnSubmit);

        text_usp = rootview.findViewById(R.id.text_usp);

        text_hvcode = rootview.findViewById(R.id.text_hvcode);

        assert getArguments() != null;
        crop_code = getArguments().getString("crop_code");
        crop_name = getArguments().getString("crop_name");
        grower_code = getArguments().getString("grower_code");
        pd_number = getArguments().getString("pd_number");
        pd_status = getArguments().getString("pd_status");
        check_hybrid = getArguments().getString("check_hybrid");
//        check_hyb = Arrays.asList(check_hybrid.split(","));
        from = getArguments().getString("from");
        if (from.equals("edit")) {
            crop_id = getArguments().getString("crop_id");
        }

        btnSubmit.setOnClickListener(this);


        //image

        mOfflinePathImage = Environment.getExternalStorageDirectory() + "/RnDObservation/Images/";
        boolean successC = (new File(mOfflinePathImage)).mkdirs();
        if (!successC) {
            Log.e("Error", "Error creating directory");
        }


        if (Oroot.canWrite()) {
            File dir = new File(Oroot.getAbsolutePath() + "/Android/Observation");
            dir.mkdirs();

            GetMasterPath = new File(dir, "GetMasterDB.db");

        }

        dbGetMaster = getActivity().openOrCreateDatabase(GetMasterPath.getPath(), Context.MODE_PRIVATE, null);

        getBundle();
        initDB();
        get_observation_data_db();
        setUpRecyclerView();

        return rootview;
    }

    private void gps_function() {
        final LocationManager manager = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            displayLocationSettingsRequest(getActivity());
        }
        else {
            gps = new GPSTracker(getActivity());
            // check if GPS enabled
            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                //  strAdd = getAddressFromLocation(latitude, longitude);
//                    statusCheck();
                strAdd = "";
                strAdd = getCompleteAddressString(latitude, longitude);


            }
        }
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result)
            {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("TAG", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("TAG", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(getActivity(), 1);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("TAG", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("TAG", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }


    @SuppressLint("LongLogTag")
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
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
        mList.clear();
        try {
            ListModel model;
            JSONObject jsonObj = new JSONObject(s);

            JSONArray dr = jsonObj.getJSONArray("dr");
            JSONObject c = null;
            for (int i = 0; i < dr.length(); i++) {
//                ob_list ob = new ob_list();
                model = new ListModel(getActivity());
                try {
                    c = dr.getJSONObject(i);
                    model.setFkey(c.getString("fkey"));
                    model.setFname(c.getString("fname"));
                    model.setReq(c.getString("req"));
                    try {
                        if (c.getString("valbased") != null){

                            JSONObject val_based = new JSONObject(c.getString("valbased"));

                            Iterator<String> iter = val_based.keys();
                            while (iter.hasNext()) {
                                String key = iter.next();
                                try {
                                    switch (key) {
                                        case "parentkey":
                                            model.setValKey(String.valueOf(val_based.get(key)));
                                            break;
                                        case "parentlable":
                                            model.setValLabel(String.valueOf(val_based.get(key)));
                                            break;
                                        case "parentval":
                                            model.setValValue(String.valueOf(val_based.get(key)));
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
                    model.setF_type(f_type);
                    if (f_type.equals("select") ) {
                        model.setIntType(1);
                        model.setMsel(c.getString("msel"));
                        JSONArray choice = c.getJSONArray("choice");
                        String[] choice_desc = new String[choice.length()];
                        String[] choice_val = new String[choice.length()];
                        for (int j = 0; j < choice.length(); j++) {
                            choice_desc[j] = choice.getJSONObject(j).getString("cdesc");
                            choice_val[j] = choice.getJSONObject(j).getString("cval");
                        }
                        model.setChoice_desc(choice_desc);
                        model.setChoice_val(choice_val);
                        model.setUpSpinnerData();
                    } else if (f_type.equals("checkbox")) {
                        model.setIntType(3);
//                        model.sec(c.getString("msel"));
                        JSONArray choice = c.getJSONArray("choice");
                        String[] choice_desc = new String[choice.length()];
                        String[] choice_val = new String[choice.length()];
                        for (int j = 0; j < choice.length(); j++) {
                            choice_desc[j] = choice.getJSONObject(j).getString("cdesc");
                            choice_val[j] = choice.getJSONObject(j).getString("cval");
                        }
                        model.setChoice_desc(choice_desc);
                        model.setChoice_val(choice_val);
                        model.setUpCheckBoxData();
                    } else if (f_type.equals("number")) {
                        model.setIntType(2);
                        model.setDecimal(c.getString("decimal"));
                        model.setUpEditTextData();
                        try {
                            model.setMinVal(Integer.parseInt(c.getString("minVal")));
                            model.setMaxVal(Integer.parseInt(c.getString("maxVal")));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "no min & max " + e.toString());
                        }
                    } else if (f_type.equals("image")) {
                        model.setIntType(4);
                        model.setDecimal("decimal");
                        model.setUpImageData();
//                        model.setReq("0");

                    } else if (f_type.equals("date")) {
                        model.setIntType(5);
                        model.setDecimal("decimal");
                        model.setUpEditTextData();
//                        model.setReq("0");
                    } else if (f_type.equals("instruction")) {
                        sessionsave.save_usp_instruction(c.getString("fname"));

                    }
                    if (from.equals("edit")) {
                        String f_value = c.getString("value");
//                        ob.getValue(f_value);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                assert c != null;
                if (!c.getString("ftype").equals("instruction")) {
                    mList.add(model);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "No Data Available", Toast.LENGTH_LONG).show();

        }

        if (sessionsave.get_usp_instruction().equals("null")) {
            text_usp.setVisibility(View.GONE);
        } else {
            text_usp.setVisibility(View.VISIBLE);
            text_usp.setText(sessionsave.get_usp_instruction());
        }
    }




    public void initDB() {
        this.dbHelper = new FeedReaderDbHelper((getActivity()));
        this.db = this.dbHelper.getWritableDatabase();
    }

    public void getBundle(){
        if (getActivity().getIntent() != null){
            if (getActivity().getIntent().getExtras() != null){
                if (getActivity().getIntent().getExtras().getString(BUNDLE_CODE) != null){
                    code = getActivity().getIntent().getExtras().getString(BUNDLE_CODE);
                }
            }
        }
    }

    public void setHeadingData(){
//        mHeadingList.add("HV Codes");

        mHeadingList.clear();
        for(int i = 0; i < sessionsave.getCheck_hybrid(); i++){

            Log.e("values=-----", String.valueOf(sessionsave.getCheck_hybrid()));

            if(check_hyb.size()==1){

                if(i==0) {
                    Log.e("check----3", check_hyb.get(0).toString());
                    mHeadingList.add(check_hyb.get(0).toString());
                }

            }else {
                if (i == 0) {
                    Log.e("check----3-----", check_hyb.get(0).toString());
                    mHeadingList.add(check_hyb.get(0).toString());
                } else if (i == 1) {

                    mHeadingList.add(check_hyb.get(1).toString());
                } else if (i == 2) {
                    mHeadingList.add(check_hyb.get(2).toString());
                } else if (i == 3) {
                    mHeadingList.add(check_hyb.get(3).toString());
                } else if (i == 4) {
                    mHeadingList.add(check_hyb.get(4).toString());
                } else if (i == 5) {
                    mHeadingList.add(check_hyb.get(5).toString());
                } else if (i == 6) {
                    mHeadingList.add(check_hyb.get(6).toString());
                } else if (i == 7) {
                    mHeadingList.add(check_hyb.get(7).toString());
                } else if (i == 8) {
                    mHeadingList.add(check_hyb.get(8).toString());
                } else if (i == 9) {
                    mHeadingList.add(check_hyb.get(9).toString());
                }

            }

        }
    }

  /*  public void setTitleData(){
        mTitleList.add("HV Codes");
        for (int i = 0; i < mList.size(); i++){
            mTitleList.add(mList.get(i).getFname());
        }
    }*/

    private void setUpRecyclerView() {
        //For headings
//        setHeadingData();
//        mLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
//        rvHeading.setLayoutManager(this.mLayoutManager);
//        mHeadingAdapter = new HeadingAdapter (mHeadingList, getActivity());
//        rvHeading.setAdapter(this.mHeadingAdapter);
//
//     /*   //For titles
//        setTitleData();
//        mLayoutManager = new LinearLayoutManager(this);
//        rvTitle.setLayoutManager(this.mLayoutManager);
//        mTitleAdapter = new TitleAdapter (mTitleList, this);
//        rvTitle.setAdapter(this.mTitleAdapter);*/
//
//        //for form
//        mLayoutManager = new LinearLayoutManager(getActivity());
//        rvForm.setLayoutManager(this.mLayoutManager);
//        mAdapter = new FormAdapterDynamic(mList, getActivity());
////        HeaderItemDecoration stickHeaderDecoration =new HeaderItemDecoration(rvForm, (HeaderItemDecoration.StickyHeaderInterface) mAdapter);
////        rvForm.addItemDecoration(stickHeaderDecoration);
//        mAdapter.setOnImageClickedListener(new FormAdapterDynamic.OnImageClickedListener() {
//            @Override
//            public void OnImageClicked(int pos, int parentPos, int type, int img_pos) {
//                if (type == 3){
//                    mList.get(parentPos).getSpinnerList().get(pos).removeOneChoice_desc(img_pos);
//                    mList.get(parentPos).removeOneChoice_desc((pos * 2) + img_pos);
//                    mAdapter.notifyDataSetChanged();
//                } else {
//                    disease.this.pos = pos;
//                    disease.this.parentPos = parentPos;
//                    disease.this.type = type;
//                    disease.this.img_pos = img_pos;
//                    selectImage(getActivity());
//                }
//            }
//        });
//        rvForm.setAdapter(this.mAdapter);

       setHeadingData();
        mLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        rvHeading.setLayoutManager(this.mLayoutManager);
        mHeadingAdapter = new HeadingAdapter (mHeadingList, getActivity());
        rvHeading.setAdapter(this.mHeadingAdapter);
//
//     /*   //For titles
//        setTitleData();
//        mLayoutManager = new LinearLayoutManager(this);
//        rvTitle.setLayoutManager(this.mLayoutManager);
//        mTitleAdapter = new TitleAdapter (mTitleList, this);
//        rvTitle.setAdapter(this.mTitleAdapter);*/
//
//        for form
        mLayoutManager = new LinearLayoutManager(getActivity());
        rvForm.setLayoutManager(this.mLayoutManager);
        mAdapter = new FormAdapterDynamic(mList,mHeadingList, getActivity(),this,this);
//        HeaderItemDecoration stickHeaderDecoration =new HeaderItemDecoration(rvForm, (HeaderItemDecoration.StickyHeaderInterface) mAdapter);
//        rvForm.addItemDecoration(stickHeaderDecoration);
        mAdapter.setOnImageClickedListener(new FormAdapterDynamic.OnImageClickedListener() {
            @Override
            public void OnImageClicked(int pos, int parentPos, int type, int img_pos) {
                if (type == 3){
                    mList.get(parentPos).getSpinnerList().get(pos).removeOneChoice_desc(img_pos);
                    mList.get(parentPos).removeOneChoice_desc((pos * 2) + img_pos);
                    mAdapter.notifyDataSetChanged();
                } else {
                    disease.this.pos = pos;
                    disease.this.parentPos = parentPos;
                    disease.this.type = type;
                    disease.this.img_pos = img_pos;
                    selectImage(getActivity());
                }
            }
        });
        rvForm.setAdapter(this.mAdapter);


        //for form
        mLayoutManager = new LinearLayoutManager(getActivity());
        rvForm1.setLayoutManager(this.mLayoutManager);
        mAdapter1 = new FormAdapterDynamicColumn(mList, getActivity());
//        HeaderItemDecoration stickHeaderDecoration =new HeaderItemDecoration(rvForm, (HeaderItemDecoration.StickyHeaderInterface) mAdapter);
//        rvForm.addItemDecoration(stickHeaderDecoration);

        rvForm1.setAdapter(this.mAdapter1);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btnSubmit:
                //TODO database

                final LocationManager manager = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService( Context.LOCATION_SERVICE );
                if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    displayLocationSettingsRequest(getActivity());
                }
                else {
                    gps = new GPSTracker(getActivity());
                    // check if GPS enabled
                    if (gps.canGetLocation()) {
                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();
                        //  strAdd = getAddressFromLocation(latitude, longitude);
//                    statusCheck();
                        strAdd = "";
                        strAdd = getCompleteAddressString(latitude, longitude);

                        if (isValidate() && isMaxMinValidate()) {
                            String[] hvcode = new String[mHeadingList.size()];
                            for (int i = 0; i < mHeadingList.size(); i++) {
//                    if (i != 0) {
                                hvcode[i] = mHeadingList.get(i);
//                    }
                            }
                            for (int i = 0; i < mList.size(); i++) {
                                mList.get(i).setHv_codes(hvcode);
                            }

                            int form_id = (int) SQLHelper.addFormToDB(mList, db, "N","disease");
//                Toast.makeText(getActivity(), "Your data has been saved successfully.", Toast.LENGTH_LONG).show();
//                    mHeadingList.remove(0);
                            saving_observation_data(form_id);
                            dialog.setMessage("Saving this observation data \nPlease wait .....");
                            dialog.show();
                        }


                    }
                }
                break;
        }

    }

    public boolean isMaxMinValidate() {
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getIntType() == 2) {
                for (int a = 0; a < sessionsave.getCheck_hybrid(); a++) {
                    if (mList.get(i).getDecimals()[a] == null ||
                            mList.get(i).getDecimals()[a].equalsIgnoreCase("")) {

                    } else {
                        if (mList.get(i).getMinVal() >= 0 && mList.get(i).getMaxVal() > 0) {
                            if (mList.get(i).getMinVal() > Integer.parseInt(mList.get(i).getDecimals()[a])
                                    || Integer.parseInt(mList.get(i).getDecimals()[a]) > mList.get(i).getMaxVal()) {
//                                mAdapter.setEditTextValidation(i, a, "Please enter number between" + mList.get(i).getMinVal()
//                                        + " to " + mList.get(i).getMaxVal());
                                /*Toast.makeText(getActivity(), "Please enter number between " + mList.get(i).getMinVal()
                                        + " to " + mList.get(i).getMaxVal() + " for the key value: " + mList.get(i).getFname() + " in hv code: "
                                        + mHeadingList.get(a), Toast.LENGTH_LONG).show();*/
                                mList.get(i).setSelectedDecimalError("Please enter number between " + mList.get(i).getMinVal()
                                        + " to " + mList.get(i).getMaxVal(), a);
                                mAdapter.notifyDataSetChanged();
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean isValidate() {
        boolean isOk = true;
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getReq().equalsIgnoreCase("1")) {
                if (mList.get(i).getIntType() == 1) {
                    for (int a = 0; a < sessionsave.getCheck_hybrid(); a++) {
                        if (mList.get(i).getSelected_choice_desc()[a] == null ||
                                mList.get(i).getSelected_choice_desc()[a].equalsIgnoreCase("") ||
                                mList.get(i).getSelected_choice_desc()[a].equalsIgnoreCase("Please Select")) {
                            isOk = false;
                            Toast.makeText(getActivity(), "Please select any value for the key value: " + mList.get(i).getFname() + " in hv code: "
                                    + mHeadingList.get(a + 1), Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }
                } else if (mList.get(i).getIntType() == 2) {
                    for (int a = 0; a < sessionsave.getCheck_hybrid(); a++) {
                        if (mList.get(i).getDecimals()[a] == null ||
                                mList.get(i).getDecimals()[a].equalsIgnoreCase("")) {
//                            mAdapter.setEditTextValidation(i, a, "Please enter any number");
                            Toast.makeText(getActivity(), "Please enter any number for the key value: " + mList.get(i).getFname() + " in hv code: "
                                    + mHeadingList.get(a + 1), Toast.LENGTH_LONG).show();
                            return false;
                        } /*else {
                            if (mList.get(i).getMinVal() >= Integer.parseInt(mList.get(i).getDecimals()[a])
                                     && Integer.parseInt(mList.get(i).getDecimals()[a]) >= mList.get(i).getMaxVal()) {
                                mAdapter.setEditTextValidation(i, a, "Please enter number between" + mList.get(i).getMinVal()
                                + " to " + mList.get(i).getMaxVal());
                            }
                        }*/
                    }
                } else if (mList.get(i).getIntType() == 3) {
                    for (int a = 0; a < sessionsave.getCheck_hybrid(); a++) {
                        if (mList.get(i).getSelected_choice_desc()[a] == null ||
                                mList.get(i).getSelected_choice_desc()[a].equalsIgnoreCase("") ||
                                mList.get(i).getSelected_choice_desc()[a].equalsIgnoreCase("Please Select")) {
                            isOk = false;
                            Toast.makeText(getActivity(), "Please select any value for the key value: " + mList.get(i).getFname() + " in hv code: "
                                    + mHeadingList.get(a + 1), Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }
                } else if (mList.get(i).getIntType() == 5) {
                    for (int a = 0; a < sessionsave.getCheck_hybrid(); a++) {
                        if (mList.get(i).getDecimals()[a] == null ||
                                mList.get(i).getDecimals()[a].equalsIgnoreCase("")) {
                            Toast.makeText(getActivity(), "Please select any date for the key value: " + mList.get(i).getFname() + " in hv code: "
                                    + mHeadingList.get(a + 1), Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private void saving_observation_data(int form_id) {
        if (alert.isNetworkAvailable()) {

            ArrayList mainheading =new ArrayList();
            ArrayList msgstring =new ArrayList();
            ArrayList<String> img_names = new ArrayList<>();
            ArrayList<String> images = new ArrayList<>();
            ArrayList<String> images_file_name = new ArrayList<>();

            Map<String, String> params = new HashMap<>();
            Map<String, String> params_new = new HashMap<>();
            params.put("Company", sessionsave.get_company());
            params.put("pdRegNum", pd_number);
            params.put("CropCode",crop_code);
            params.put("CropName", crop_name);
            params.put("CreatedBy", sessionsave.get_emp_code());
            params.put("pdStatus", "3");
            params.put("GPSLocation", strAdd.trim());
            params.put("menuId", "3");
            if (pd_status.equals("0")) {
                params.put("obid", "");
            } else {
                params.put("obid", sessionsave.get_obid());
            }
            String msg = "";
            String emailList= "";

            for (int j = 0; j < mHeadingList.size(); j++) {
                mainheading.add(mHeadingList.get(j));
                listModel = SQLHelper.getAllFormDataByHvCodeFromDB(db, form_id, mHeadingList.get(j),getActivity());
                int img_pos = 0;

                for (int i = 0; i < listModel.size(); i++) {
                    if (listModel.get(i).getIntType() == 2) {
                        if(listModel.get(i).getDecimals() != null && listModel.get(i).getDecimals().length > 0) {
                            msg = msg.concat(listModel.get(i).getFkey().concat(" => ").concat(listModel.get(i).getDecimals()[0]).concat("\n"));
                            emailList = msg.replaceAll("\n", "|");
                            Log.e("values", listModel.get(i).getFname().concat(" => ") + "   " + listModel.get(i).getDecimals()[0]);
                        }
                    } else if(listModel.get(i).getIntType() == 5){
                        if(listModel.get(i).getDecimals() != null && listModel.get(i).getDecimals().length > 0) {
                            msg = msg.concat(listModel.get(i).getFkey().concat(" => ").concat(listModel.get(i).getDecimals()[0]).concat("\n"));
                            emailList = msg.replaceAll("\n", "|");
                            Log.e("values", listModel.get(i).getFname().concat(" => ") + "   " + listModel.get(i).getDecimals()[0]);
                        }
                    }else if(listModel.get(i).getIntType() == 1) {
                        if(listModel.get(i).getSelected_choice_desc() != null && listModel.get(i).getSelected_choice_desc().length > 0) {
                            Log.e("values", listModel.get(i).getFname().concat(" => ") + "  " + listModel.get(i).getSelected_choice_desc()[j]);
                            msg = msg.concat(listModel.get(i).getFkey().concat(" => ").concat(listModel.get(i).getSelected_choice_desc()[j]).concat("\n"));
                            emailList = msg.replaceAll("\n", "|");
                            Log.e("values----img",emailList);
                        }
                    }else if(listModel.get(i).getIntType() == 4) {
                        if (listModel.get(i).getChoice_desc() != null && listModel.get(i).getChoice_desc().length > 0) {
                            if (i != 0) {
                                if (i % 2 == 0) {
                                    img_pos++;
                                }
                            }
                            Log.e("values----1", listModel.get(i).getFname().concat(" => ") + "   " + listModel.get(i).getChoice_desc()[img_pos]);
                            Log.e("values----2", listModel.get(i).getFname().concat(" => ") + "   " + listModel.get(i).getChoice_desc()[img_pos + 1]);

                            image1 =  listModel.get(i).getChoice_desc()[img_pos];
                            image2 = listModel.get(i).getChoice_desc()[img_pos + 1];



                            if(image1!=null &&image1.contains("jpg") ) {
                                if(image2 != null && image2.contains("jpg")) {
                                    msg = msg.concat("img_"+listModel.get(i).getFkey().concat("=>").concat("1,2").concat("\n"));
                                    emailList = msg.replaceAll("\n", "|");
                                    Log.e("values----image1", emailList);
                                }else{
                                    msg = msg.concat("img_"+listModel.get(i).getFkey().concat("=>").concat("1").concat("\n"));
                                    emailList = msg.replaceAll("\n", "|");
                                    Log.e("values---image2",emailList);
                                }
                            }else{
                                msg = msg.concat("img_"+ listModel.get(i).getFkey().concat("=>").concat("1").concat("\n"));
                                emailList = msg.replaceAll("\n", "|");
                                Log.e("values---image2",emailList);
                            }
                            if (image1 == null){
                                image1 = "";
                            }
                            if (image2 == null){
                                image2 = "";
                            }
                            String imgname = listModel.get(i).getFkey();
                            images.add(image1);
                            images.add(image2);
                            img_names.add(imgname);

                        }
                    }
//


                }

                if(!emailList.equalsIgnoreCase("") && emailList.length() > 0) {
                    emailList = emailList.substring(0, emailList.length() - 1);
                    Log.e("values--------msg", emailList);
                }
                if(emailList.contains("Please Select")){
                    emailList = emailList.replace("Please Select","");
                }
                msgstring.add((emailList));
                msg = "";
                emailList = "";
            }

            for(int i = 0 ;i<mainheading.size();i++) {
                int img_name_pos = 0;
                for(int j=0;j<images.size();j++){
                    if(j % 2 ==0){
                        if (j != 0){
                            img_name_pos++;
                        }
//                        try {
                        Log.e("values", "allfiles["+ mainheading.get(i) +"]" + "[img_" + img_names.get(img_name_pos) + "_1]");
                        images_file_name.add("allfiles["+ mainheading.get(i) +"]" + "[img_"+img_names.get(img_name_pos)+"_1]");

//                        }catch (Exception e){
//                            e.printStackTrace();
//                            Log.e("error1","error");
//                        }
                    }else{
//                        try {
                        //                        Log.e("values","allflies[" + mainheading.get(i) + "]" + "[img_"+img_names.get(j)+"_2]");
                        images_file_name.add("allfiles["+ mainheading.get(i) +"]"+"[img_"+img_names.get(img_name_pos)+"_2]");
//                        }catch (Exception e){
//                            e.printStackTrace();
//                            Log.e("error2","error2");
//                        }

                    }
                }
                if(images.size()==2){
                    params.put("data[" + mainheading.get(i) + "]", "["+msgstring.get(i)+"]");
                }else{
                    params.put("data[" + mainheading.get(i) + "]", "["+msgstring.get(i)+"]");
                }

            }



            Log.e("params", String.valueOf(params));
            Log.e("images", String.valueOf(images.size()));

            Log.e("values----filename",images_file_name.toString());



            String result = multipartRequest(Api.obd_insert, params, images, images_file_name, "image/*");
            Log.e(TAG, "res 0 " + result);
            Log.e("usp", String.valueOf(params));
        }else{

            String query = "INSERT INTO ObservationData(CropCode,CropName,Company,BreederCode,HVCode,Variety,Stage,State,City,CreatedBy,Master_data,General_data,Observe_data,usp_data,CreatedDateTime,Location,grower_code,pd_register_num,pd_status) VALUES('" + crop_code + "','" + sessionsave.get_crop_name() + "','" + sessionsave.get_company() + "','" + sessionsave.get_breeder_code() + "','" + sessionsave.get_hv_code() + "','" + sessionsave.get_variety() + "','" + "disease" + "','" + sessionsave.get_states_code() + "','" + sessionsave.get_city() + "','" + sessionsave.get_emp_code() + "','" + "sb_master" + "','" + "sb_general" + "','" + "sb_observe" + "','" + "sb_conclusion" + "','" + "createdDateTime" + "','" + strAdd +  "','" + grower_code + "','" + pd_number + "','" + "3" + "');";
            dbGetMaster.execSQL(query);
            dialog.dismiss();
            dialog.cancel();
            Intent myIntent = new Intent(getActivity(), ScreenActivity.class);
            startActivity(myIntent);
            getActivity().finish();
            Toast.makeText(getActivity(), "Observation Added Successfully", Toast.LENGTH_SHORT).show();

        }
    }

    public String multipartRequest(final String urlTo, final Map<String, String> parmas, final ArrayList<String> filepaths, final ArrayList<String> file_names, final String fileMimeType) {
        final String[] result = {""};

        class UploadFileAsync extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... strings) {
                HttpURLConnection connection = null;
                DataOutputStream outputStream = null;
                InputStream inputStream = null;

                String twoHyphens = "--";
                String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
                String lineEnd = "\r\n";

                try {

                    URL url = new URL(urlTo);
                    connection = (HttpURLConnection) url.openConnection();

                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);

                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                    outputStream = new DataOutputStream(connection.getOutputStream());
                    for (int i = 0; i < filepaths.size(); i++) {
                        int bytesRead, bytesAvailable, bufferSize;
                        byte[] buffer;
                        int maxBufferSize = 1 * 1024 * 1024;
                        String[] q = filepaths.get(i).split("/");
                        int idx = q.length - 1;
                        File file = new File(filepaths.get(i));
                        Log.e(TAG, "sending image " + file_names.get(i));
                        if (file.exists()) {
                            FileInputStream fileInputStream = new FileInputStream(file);

                            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + file_names.get(i) + "\"; filename=\"" + file.getName() + "\"" + lineEnd);
                            outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
                            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
                            outputStream.writeBytes(lineEnd);

                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            buffer = new byte[bufferSize];

                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                            while (bytesRead > 0) {
                                outputStream.write(buffer, 0, bufferSize);
                                bytesAvailable = fileInputStream.available();
                                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                            }

                            outputStream.writeBytes(lineEnd);
                            fileInputStream.close();
                        } else {
                            Log.e(TAG, "error no file");
                        }
                    }

                    // Upload POST Data
                    for (String key : parmas.keySet()) {
                        String value = parmas.get(key);
                        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                        outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                        outputStream.writeBytes(lineEnd);
                        outputStream.writeBytes(value);
                        outputStream.writeBytes(lineEnd);
                    }

                    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    if (200 != connection.getResponseCode()) {
                        Log.e(TAG, "Failed to upload code:" + connection.getResponseCode() + " " + connection.getResponseMessage());
                    }

                    inputStream = connection.getInputStream();

                    result[0] = convertStreamToString(inputStream);

                    inputStream.close();
                    outputStream.flush();
                    outputStream.close();

                    return result[0];
                } catch (Exception e) {
                    Log.e(TAG, "error " + e.toString());
                }
                return result[0];

            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    myJSON = result;
                    Log.e(TAG, "res _1 " + result);

                    if(myJSON.contains("success")){
                        Toast.makeText(getActivity(), "Observation Added Successfully", Toast.LENGTH_SHORT).show();
                        updateOneFrom();
                        Intent myIntent = new Intent(getActivity(), ScreenActivity.class);
                        startActivity(myIntent);
                        getActivity().finish();
                    }else{
                        Toast.makeText(getActivity(), "Error in uploading", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    dialog.cancel();
                    e.printStackTrace();
                }
            }

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected void onProgressUpdate(Void... values) {
            }
        }
        new UploadFileAsync().execute("");
        return result[0];
    }

    private void updateOneFrom() {
        Log.e("values","data remove");
        ArrayList<FormModel> list = new ArrayList<>();
        list.add(new FormModel("", form_id, listModel,"disease"));
        //TODO get all form data and upload to server
        SQLHelper.updateForm(list, db, "Y");
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == 1230) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String img1 = data.getStringExtra("img0");
                    String img2 = data.getStringExtra("img1");
                    int pos = data.getIntExtra("pos", -1);
                    int parentPos = data.getIntExtra("parentPos", -1);
                    if (pos > -1 && parentPos > -1) {
//                    mAdapter.setImage(pos, parentPos, img1, img2);
                        String[] imgs = new String[2];
                        imgs[0] = img1;
                        imgs[1] = img2;
                        mList.get(parentPos).setSpinnerList(pos, imgs);
                        mList.get(parentPos).setChoice_desc(pos, imgs);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        }*/

        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {

                        Log.e("values---222344--","noimages");
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
//                        imageView.setImageBitmap(selectedImage);
                        if (selectedImage != null) {

                            Log.e("values---344--","noimages");
                            String path = saveOfflineImg(selectedImage, img_pos);
//                            mList.get(parentPos).getSpinnerList().get(pos).getChoice_desc()[img_pos] = path;

                            Log.e("values----pic",path);
                            if (type == 1) {
                                addImageToList(path);
                            } else if (type == 2){
                                mList.get(parentPos).getSpinnerList().get(pos).setOneChoice_desc(img_pos, path);
                                mList.get(parentPos).setOneChoice(pos, path);
                                mAdapter.notifyDataSetChanged();
                            } else if (type == 3){
                                mList.get(parentPos).getSpinnerList().get(pos).removeOneChoice_desc(img_pos);
                                mList.get(parentPos).setOneChoice(pos, path);
                                mAdapter.notifyDataSetChanged();
                            }
                        }else{
                            Log.e("values-----","noimages");
                        }
                    }else{
                        Log.e("values---13344--","noimages");
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Log.e("values---image--","noimages");
                        if (selectedImage != null) {

                            Log.e("values---image-ghjkjk-","noimages");
                            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
//                                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                                if (picturePath != null) {
                                    Log.e("values----pic",picturePath);
                                    if (type == 1) {
                                        Log.e("values----pic1",picturePath);
                                        addImageToList(picturePath);
                                    } else if (type == 2){
                                        Log.e("values----pic2",picturePath);
                                        mList.get(parentPos).getSpinnerList().get(pos).setOneChoice_desc(img_pos, picturePath);
                                        mList.get(parentPos).setOneChoice(pos, picturePath);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                   /* String[] imgs = mList.get(parentPos).getSpinnerList().get(pos).getChoice_desc();
                                    imgs[img_pos] = picturePath;
                                    mList.get(parentPos).setSpinnerList(pos, imgs);
                                    mList.get(parentPos).setChoice_desc(pos, imgs);
                                    mAdapter.notifyDataSetChanged();*/
                                }
                            }
                        }else{
                            Log.e("values-----","noimages");
                        }

                    }else{
                        Log.e("values---13344--","noimages");
                    }
                    break;
            }
        }
    }

    public void addImageToList(String path){
        String[] imgs = new String[2];
        String[] s_imgs = mList.get(parentPos).getSpinnerList().get(pos).getChoice_desc();
        for (int i = 0; i < imgs.length; i++) {
            if (i == img_pos) {
                imgs[i] = path;
            } else {
                imgs[i] = s_imgs[i];
            }
        }
        mList.get(parentPos).setSpinnerList(pos, imgs);
        int p = (pos + 1) * 2;
//                            String[] main_ch = mList.get(parentPos).getChoice_desc();
        String[] main_ch = mList.get(parentPos).getChoice_desc();
        String[] ch_imgs;
        int main_ch_len;
        if (main_ch == null || main_ch.length == 0) {
            main_ch_len = 0;

        } else {
            main_ch_len = main_ch.length;
//                                ch_imgs = new String[2];
        }
        if (p <= main_ch_len) {
            ch_imgs = new String[main_ch_len];
            for (int i = 0; i < ch_imgs.length; i++) {
                ch_imgs[i] = main_ch[i];
            }
            ch_imgs[(pos * 2) + img_pos] = imgs[img_pos];
        } else {
            ch_imgs = new String[main_ch_len + 2];
            for (int i = 0; i < ch_imgs.length; i++) {
                if (i < main_ch_len) {
                    ch_imgs[i] = main_ch[i];
                }
            }
            ch_imgs[main_ch_len] = imgs[0];
            ch_imgs[main_ch_len + 1] = imgs[1];
        }
//                            mList.get(parentPos).setChoice_desc(pos, ch_imgs);
        mList.get(parentPos).setChoice_desc(pos, ch_imgs);
        mAdapter.notifyDataSetChanged();
    }

    public Bitmap getBitmap(File image) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
//        bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
        return bitmap;
    }

    public File getFile(int pos, int name) {
        String url = Environment.getExternalStorageDirectory() + "/RnDObservation/Images/"
                + parentPos + pos + name + ".jpg";
        try {
            return new File(url);
        } catch (Exception str) {
            return null;
        }
    }

    public String saveOfflineImg(Bitmap paramBitmap, int name) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        paramBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        String s_path = "".concat(String.valueOf(parentPos)).concat(String.valueOf(pos)).concat(String.valueOf(name)).concat(".jpg");
        File file = new File(mOfflinePathImage, s_path);
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mOfflinePathImage + s_path;
    }

    private void selectImage(Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    @Override
    public void pass_position(int position, String view_type) {
        Log.d("intefaces----fragment", String.valueOf(position));



        Log.d("intefaces----fragment",mHeadingList.get(position));
        Log.d("intefaces--frag--top",view_type);



    }

    @Override
    public void pass_po_tofrg(int position, String view_type) {

        pass_pos = position;
        pass_string = view_type;

        text_hvcode.setVisibility(View.VISIBLE);
        text_hvcode.setText(mHeadingList.get(position));

    }
}
