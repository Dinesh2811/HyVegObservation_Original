package com.example.manoj.hyveg_observation.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.manoj.hyveg_observation.Api;
import com.example.manoj.hyveg_observation.Others.GPSTracker;
import com.example.manoj.hyveg_observation.Others.InputFilterMinMax;
import com.example.manoj.hyveg_observation.R;
import com.example.manoj.hyveg_observation.activity.ScreenActivity;
import com.example.manoj.hyveg_observation.activity.Sessionsave;
import com.example.manoj.hyveg_observation.activity.pick_image_1;
import com.example.manoj.hyveg_observation.list.ob_list;
import com.example.manoj.hyveg_observation.services.alertDialog;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;


public class glist_adapter extends RecyclerView.Adapter<glist_adapter.ViewHolder> {

    private Context mCtx;
    private List<ob_list> ob_lists;
    private String mActivity ,date ,TAG = "g_list_adapter",grower_code,pd_number,pd_status,myJSON = "",strAdd,crop_code;
    private DatePickerDialog datePickerDialog;
    private Sessionsave sessionsave;
    private DecimalFormat dcf = new DecimalFormat("00");
    private String[] crop_data;

    private ProgressDialog dialog;
    alertDialog alert;
    private File Oroot = Environment.getExternalStorageDirectory(), GetMasterPath = null;
    private SQLiteDatabase dbGetMaster;
    private Cursor GetWareHouseDetails;
    StringBuilder sb_general;
    // GPSTracker class
    GPSTracker gps;
    double latitude, longitude;
    List<String> check_hyb = new ArrayList<>();
    int posit ;
    ob_list ob_list;
    String[] choice_desc_2;

    public glist_adapter(Context mCtx, List<ob_list> ob_lists, String mActivity, String grower_code, String pd_number,String pd_status, String crop_code,List<String> check_hybs) {
        super();
        this.ob_lists = ob_lists;
        this.mCtx = mCtx;
        this.mActivity = mActivity;
        this.grower_code =grower_code;
        this.pd_number = pd_number;
        this.pd_status = pd_status;
        this.crop_code = crop_code;
        this.check_hyb = check_hybs;


        sessionsave = new Sessionsave(mCtx);
    }

    @NonNull
    @Override
    public glist_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view, parent, false);
        glist_adapter.ViewHolder viewHolder = new glist_adapter.ViewHolder(v);


        crop_data = new String[ob_lists.size()];

        dialog = new ProgressDialog(mCtx);
        alert = new alertDialog(mCtx);
        if (Oroot.canWrite()) {
            File dir = new File(Oroot.getAbsolutePath() + "/Android/Observation");
            dir.mkdirs();

            GetMasterPath = new File(dir, "GetMasterDB.db");

        }
        dbGetMaster = mCtx.openOrCreateDatabase(GetMasterPath.getPath(), Context.MODE_PRIVATE, null);
        return viewHolder;

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final glist_adapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
          ob_list = ob_lists.get(position);

        holder.setIsRecyclable(false);
        holder.pos = position;
        if (position == (ob_lists.size()) - 1) {
            holder.next.setVisibility(View.VISIBLE);
            holder.btn_previous.setVisibility(View.GONE);
        } else {
            holder.next.setVisibility(View.GONE);
            holder.btn_previous.setVisibility(View.GONE);
        }

        if (position == 0) {
            holder.text_one.setVisibility(View.VISIBLE);
            holder.text_two.setVisibility(View.VISIBLE);
            holder.text2.setVisibility(View.VISIBLE);
            holder.text_three.setVisibility(View.VISIBLE);
            holder.text_four.setVisibility(View.VISIBLE);
            holder.text_five.setVisibility(View.VISIBLE);
            holder.text_six.setVisibility(View.VISIBLE);
            holder.text_seven.setVisibility(View.VISIBLE);
            holder.text_eight.setVisibility(View.VISIBLE);
            holder.text_nine.setVisibility(View.VISIBLE);
            holder.text_ten.setVisibility(View.VISIBLE);
        }else {
            holder.text_one.setVisibility(View.GONE);
            holder.text_two.setVisibility(View.GONE);
            holder.text2.setVisibility(View.GONE);
            holder.text_three.setVisibility(View.GONE);
            holder.text_four.setVisibility(View.GONE);
            holder.text_five.setVisibility(View.GONE);
            holder.text_six.setVisibility(View.GONE);
            holder.text_seven.setVisibility(View.GONE);
            holder.text_eight.setVisibility(View.GONE);
            holder.text_nine.setVisibility(View.GONE);
            holder.text_ten.setVisibility(View.GONE);
        }

        for(int i = 0;i<check_hyb.size();i++){
            Log.e("check---1",check_hyb.toString());
            Log.e("check---2", String.valueOf(check_hyb.size()));
            Log.e("check----3",check_hyb.get(i).toString());
            if(i == 0){
                Log.e("check----3",check_hyb.get(i).toString());
                holder.linear1.setVisibility(View.VISIBLE);
                holder.text_one.setText(check_hyb.get(0).toString());
            }else if(i == 1){
                holder.linear2.setVisibility(View.VISIBLE);
                holder.text_two.setText(check_hyb.get(1));
            }else if(i == 2){
                holder.linear3.setVisibility(View.VISIBLE);
                holder.text_three.setText(check_hyb.get(2));
            }else if(i == 3){
                holder.linear4.setVisibility(View.VISIBLE);
                holder.text_four.setText(check_hyb.get(3));
            }else if(i == 4){
                holder.linear5.setVisibility(View.VISIBLE);
                holder.text_five.setText(check_hyb.get(4));
            }else if(i == 5){
                holder.linear6.setVisibility(View.VISIBLE);
                holder.text_six.setText(check_hyb.get(5));
            }else if(i == 6){
                holder.linear7.setVisibility(View.VISIBLE);
                holder.text_seven.setText(check_hyb.get(6));
            }else if(i == 7){
                holder.linear8.setVisibility(View.VISIBLE);
                holder.text_eight.setText(check_hyb.get(7));
            }else if(i == 8){
                holder.linear9.setVisibility(View.VISIBLE);
                holder.text_nine.setText(check_hyb.get(8));
            }else if(i == 9){
                holder.linear10.setVisibility(View.VISIBLE);
                holder.text_ten.setText(check_hyb.get(9));
            }

        }

        gps_function();

        if (mActivity.equals("edit")) {
            Log.e(TAG, "on edit");
        } else {
            get_data();
        }





        try {
            if (ob_list.setReq().equals("1")){
                holder.text_mtext.setText(ob_list.setName()+" *");
            }else {
                holder.text_mtext.setText(ob_list.setName());
            }
//            if (ob_list.setType().equals("number")){
//                if (ob_list.set_decimal().equals("1")) {
//                    holder.text_medit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                    if (!ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.text_medit.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax(ob_list.set_min(), ob_list.set_max(), "float")});
//                    } else if (!ob_list.set_min().equals("null") && ob_list.set_max().equals("null")) {
//                        holder.text_medit.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax(ob_list.set_min(), "9999", "float")});
//                    } else if (ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.text_medit.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax("0", ob_list.set_max(), "float")});
//                    } else {
//                        holder.text_medit.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6)});
//                    }
//                    holder.edit_two.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                    if (!ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_two.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax(ob_list.set_min(), ob_list.set_max(), "float")});
//                    } else if (!ob_list.set_min().equals("null") && ob_list.set_max().equals("null")) {
//                        holder.edit_two.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax(ob_list.set_min(), "9999", "float")});
//                    } else if (ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_two.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax("0", ob_list.set_max(), "float")});
//                    } else {
//                        holder.edit_two.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6)});
//                    }
//                    holder.edit_three.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                    if (!ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_three.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax(ob_list.set_min(), ob_list.set_max(), "float")});
//                    } else if (!ob_list.set_min().equals("null") && ob_list.set_max().equals("null")) {
//                        holder.edit_three.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax(ob_list.set_min(), "9999", "float")});
//                    } else if (ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_three.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax("0", ob_list.set_max(), "float")});
//                    } else {
//                        holder.edit_three.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6)});
//                    }
//                    holder.edit_four.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                    if (!ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_four.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax(ob_list.set_min(), ob_list.set_max(), "float")});
//                    } else if (!ob_list.set_min().equals("null") && ob_list.set_max().equals("null")) {
//                        holder.edit_four.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax(ob_list.set_min(), "9999", "float")});
//                    } else if (ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_four.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax("0", ob_list.set_max(), "float")});
//                    } else {
//                        holder.edit_four.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6)});
//                    }
//
//                    holder.edit_five.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                    if (!ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_five.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax(ob_list.set_min(), ob_list.set_max(), "float")});
//                    } else if (!ob_list.set_min().equals("null") && ob_list.set_max().equals("null")) {
//                        holder.edit_five.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax(ob_list.set_min(), "9999", "float")});
//                    } else if (ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_five.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax("0", ob_list.set_max(), "float")});
//                    } else {
//                        holder.edit_five.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6)});
//                    }
//
//                    holder.edit_six.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                    if (!ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_six.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax(ob_list.set_min(), ob_list.set_max(), "float")});
//                    } else if (!ob_list.set_min().equals("null") && ob_list.set_max().equals("null")) {
//                        holder.edit_six.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax(ob_list.set_min(), "9999", "float")});
//                    } else if (ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_six.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax("0", ob_list.set_max(), "float")});
//                    } else {
//                        holder.edit_six.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6)});
//                    }
//                    holder.edit_seven.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                    if (!ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_seven.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax(ob_list.set_min(), ob_list.set_max(), "float")});
//                    } else if (!ob_list.set_min().equals("null") && ob_list.set_max().equals("null")) {
//                        holder.edit_seven.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax(ob_list.set_min(), "9999", "float")});
//                    } else if (ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_seven.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax("0", ob_list.set_max(), "float")});
//                    } else {
//                        holder.edit_seven.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6)});
//                    }
//
//                    holder.edit_eight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                    if (!ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_eight.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax(ob_list.set_min(), ob_list.set_max(), "float")});
//                    } else if (!ob_list.set_min().equals("null") && ob_list.set_max().equals("null")) {
//                        holder.edit_eight.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax(ob_list.set_min(), "9999", "float")});
//                    } else if (ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_eight.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax("0", ob_list.set_max(), "float")});
//                    } else {
//                        holder.edit_eight.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6)});
//                    }
//
//                    holder.edit_nine.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                    if (!ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_nine.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax(ob_list.set_min(), ob_list.set_max(), "float")});
//                    } else if (!ob_list.set_min().equals("null") && ob_list.set_max().equals("null")) {
//                        holder.edit_nine.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax(ob_list.set_min(), "9999", "float")});
//                    } else if (ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_nine.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax("0", ob_list.set_max(), "float")});
//                    } else {
//                        holder.edit_nine.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6)});
//                    }
//                    holder.edit_ten.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                    if (!ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_ten.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax(ob_list.set_min(), ob_list.set_max(), "float")});
//                    } else if (!ob_list.set_min().equals("null") && ob_list.set_max().equals("null")) {
//                        holder.edit_ten.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax(ob_list.set_min(), "9999", "float")});
//                    } else if (ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_ten.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax("0", ob_list.set_max(), "float")});
//                    } else {
//                        holder.edit_ten.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6)});
//                    }
//                } else {
//                    holder.text_medit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                    if (!ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.text_medit.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax(ob_list.set_min(), ob_list.set_max(), "int")});
//                    } else if (!ob_list.set_min().equals("null") && ob_list.set_max().equals("null")) {
//                        holder.text_medit.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax(ob_list.set_min(), "9999", "int")});
//                    } else if (ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.text_medit.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax("0", ob_list.set_max(), "int")});
//                    } else {
//                        holder.text_medit.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4)});
//                    }
//
//                    holder.edit_two.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                    if (!ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_two.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax(ob_list.set_min(), ob_list.set_max(), "int")});
//                    } else if (!ob_list.set_min().equals("null") && ob_list.set_max().equals("null")) {
//                        holder.edit_two.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax(ob_list.set_min(), "9999", "int")});
//                    } else if (ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_two.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax("0", ob_list.set_max(), "int")});
//                    } else {
//                        holder.edit_two.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4)});
//                    }
//
//                    holder.edit_three.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                    if (!ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_three.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax(ob_list.set_min(), ob_list.set_max(), "int")});
//                    } else if (!ob_list.set_min().equals("null") && ob_list.set_max().equals("null")) {
//                        holder.edit_three.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax(ob_list.set_min(), "9999", "int")});
//                    } else if (ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_three.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax("0", ob_list.set_max(), "int")});
//                    } else {
//                        holder.edit_three.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4)});
//                    }
//
//                    holder.edit_four.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                    if (!ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_four.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax(ob_list.set_min(), ob_list.set_max(), "int")});
//                    } else if (!ob_list.set_min().equals("null") && ob_list.set_max().equals("null")) {
//                        holder.edit_four.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax(ob_list.set_min(), "9999", "int")});
//                    } else if (ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_four.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax("0", ob_list.set_max(), "int")});
//                    } else {
//                        holder.edit_four.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4)});
//                    }
//                    holder.edit_five.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                    if (!ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_five.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax(ob_list.set_min(), ob_list.set_max(), "int")});
//                    } else if (!ob_list.set_min().equals("null") && ob_list.set_max().equals("null")) {
//                        holder.edit_five.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax(ob_list.set_min(), "9999", "int")});
//                    } else if (ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_five.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax("0", ob_list.set_max(), "int")});
//                    } else {
//                        holder.edit_five.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4)});
//                    }
//
//                    holder.edit_six.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                    if (!ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_six.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax(ob_list.set_min(), ob_list.set_max(), "int")});
//                    } else if (!ob_list.set_min().equals("null") && ob_list.set_max().equals("null")) {
//                        holder.edit_six.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax(ob_list.set_min(), "9999", "int")});
//                    } else if (ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_six.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax("0", ob_list.set_max(), "int")});
//                    } else {
//                        holder.edit_six.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4)});
//                    }
//                    holder.edit_seven.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                    if (!ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_seven.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax(ob_list.set_min(), ob_list.set_max(), "int")});
//                    } else if (!ob_list.set_min().equals("null") && ob_list.set_max().equals("null")) {
//                        holder.edit_seven.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax(ob_list.set_min(), "9999", "int")});
//                    } else if (ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_seven.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax("0", ob_list.set_max(), "int")});
//                    } else {
//                        holder.edit_seven.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4)});
//                    }
//                    holder.edit_eight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                    if (!ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_eight.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax(ob_list.set_min(), ob_list.set_max(), "int")});
//                    } else if (!ob_list.set_min().equals("null") && ob_list.set_max().equals("null")) {
//                        holder.edit_eight.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax(ob_list.set_min(), "9999", "int")});
//                    } else if (ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_eight.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax("0", ob_list.set_max(), "int")});
//                    } else {
//                        holder.edit_eight.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4)});
//                    }
//                    holder.edit_nine.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                    if (!ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_nine.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax(ob_list.set_min(), ob_list.set_max(), "int")});
//                    } else if (!ob_list.set_min().equals("null") && ob_list.set_max().equals("null")) {
//                        holder.edit_nine.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax(ob_list.set_min(), "9999", "int")});
//                    } else if (ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_nine.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax("0", ob_list.set_max(), "int")});
//                    } else {
//                        holder.edit_nine.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4)});
//                    }
//                    holder.edit_ten.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                    if (!ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_ten.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax(ob_list.set_min(), ob_list.set_max(), "int")});
//                    } else if (!ob_list.set_min().equals("null") && ob_list.set_max().equals("null")) {
//                        holder.edit_ten.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax(ob_list.set_min(), "9999", "int")});
//                    } else if (ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
//                        holder.edit_ten.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax("0", ob_list.set_max(), "int")});
//                    } else {
//                        holder.edit_ten.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4)});
//                    }
//                }
//            }else {
//                holder.text_medit.setInputType(InputType.TYPE_CLASS_TEXT);
//                holder.edit_two.setInputType(InputType.TYPE_CLASS_TEXT);
//                holder.edit_three.setInputType(InputType.TYPE_CLASS_TEXT);
//                holder.edit_four.setInputType(InputType.TYPE_CLASS_TEXT);
//                holder.edit_five.setInputType(InputType.TYPE_CLASS_TEXT);
//                holder.edit_six.setInputType(InputType.TYPE_CLASS_TEXT);
//                holder.edit_seven.setInputType(InputType.TYPE_CLASS_TEXT);
//                holder.edit_eight.setInputType(InputType.TYPE_CLASS_TEXT);
//                holder.edit_nine.setInputType(InputType.TYPE_CLASS_TEXT);
//                holder.edit_ten.setInputType(InputType.TYPE_CLASS_TEXT);
//            }

//            holder.text_medit.setText(ob_list.setValue1());
//            holder.edit_two.setText(ob_list.setValue1());
//            holder.edit_three.setText(ob_list.setValue2());
//            holder.edit_four.setText(ob_list.setValue());
//            holder.edit_five.setText(ob_list.setValue());
//            holder.edit_six.setText(ob_list.setValue());
//            holder.edit_seven.setText(ob_list.setValue());
//            holder.edit_eight.setText(ob_list.setValue());
//            holder.edit_nine.setText(ob_list.setValue());
//            holder.edit_ten.setText(ob_list.setValue());

            try {
                if (isTableExists(dbGetMaster, "TriatsMaster")) {

                    GetWareHouseDetails = dbGetMaster.rawQuery("select ob_value from TriatsMaster WHERE position ='" + position + "' AND hv_code = '" + check_hyb.get(0) + "'", null);
                    if (GetWareHouseDetails.moveToFirst()) {
                        do {
                            try {
                                String value = (new String(GetWareHouseDetails.getString(0)));
                                Log.e("values----345" + position,value);
                                int spin_value = Integer.parseInt(value);
                                Log.e("values----spin"+position, String.valueOf(spin_value));
                                holder.text_medit.setText(value);
                            } catch (Exception e) {
                                Log.e(TAG, "Int id error " + e.toString());
                            }
                        } while (GetWareHouseDetails.moveToNext());
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (isTableExists(dbGetMaster, "TriatsMaster")) {

                    GetWareHouseDetails = dbGetMaster.rawQuery("select ob_value from TriatsMaster WHERE position ='" + position + "' AND hv_code = '" + check_hyb.get(1) + "'", null);
                    if (GetWareHouseDetails.moveToFirst()) {
                        do {
                            try {
                                String value = (new String(GetWareHouseDetails.getString(0)));
                                Log.e("values----345",value);
                                holder.edit_two.setText(value);
                            } catch (Exception e) {
                                Log.e(TAG, "Int id error " + e.toString());
                            }
                        } while (GetWareHouseDetails.moveToNext());
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (isTableExists(dbGetMaster, "TriatsMaster")) {

                    GetWareHouseDetails = dbGetMaster.rawQuery("select ob_value from TriatsMaster WHERE position ='" + position + "' AND hv_code = '" + check_hyb.get(2) + "'", null);
                    if (GetWareHouseDetails.moveToFirst()) {
                        do {
                            try {
                                String value = (new String(GetWareHouseDetails.getString(0)));
                                Log.e("values----345",value);
                                holder.edit_three.setText(value);
                            } catch (Exception e) {
                                Log.e(TAG, "Int id error " + e.toString());
                            }
                        } while (GetWareHouseDetails.moveToNext());
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (isTableExists(dbGetMaster, "TriatsMaster")) {

                    GetWareHouseDetails = dbGetMaster.rawQuery("select ob_value from TriatsMaster WHERE position ='" + position + "' AND hv_code = '" + check_hyb.get(3) + "'", null);
                    if (GetWareHouseDetails.moveToFirst()) {
                        do {
                            try {
                                String value = (new String(GetWareHouseDetails.getString(0)));
                                Log.e("values----345",value);
                                holder.edit_four.setText(value);
                            } catch (Exception e) {
                                Log.e(TAG, "Int id error " + e.toString());
                            }
                        } while (GetWareHouseDetails.moveToNext());
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (isTableExists(dbGetMaster, "TriatsMaster")) {

                    GetWareHouseDetails = dbGetMaster.rawQuery("select ob_value from TriatsMaster WHERE position ='" + position + "' AND hv_code = '" + check_hyb.get(4) + "'", null);
                    if (GetWareHouseDetails.moveToFirst()) {
                        do {
                            try {
                                String value = (new String(GetWareHouseDetails.getString(0)));
                                Log.e("values----345",value);
                                holder.edit_five.setText(value);
                            } catch (Exception e) {
                                Log.e(TAG, "Int id error " + e.toString());
                            }
                        } while (GetWareHouseDetails.moveToNext());
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (isTableExists(dbGetMaster, "TriatsMaster")) {

                    GetWareHouseDetails = dbGetMaster.rawQuery("select ob_value from TriatsMaster WHERE position ='" + position + "' AND hv_code = '" + check_hyb.get(5) + "'", null);
                    if (GetWareHouseDetails.moveToFirst()) {
                        do {
                            try {
                                String value = (new String(GetWareHouseDetails.getString(0)));
                                Log.e("values----345",value);
                                holder.edit_six.setText(value);
                            } catch (Exception e) {
                                Log.e(TAG, "Int id error " + e.toString());
                            }
                        } while (GetWareHouseDetails.moveToNext());
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (isTableExists(dbGetMaster, "TriatsMaster")) {

                    GetWareHouseDetails = dbGetMaster.rawQuery("select ob_value from TriatsMaster WHERE position ='" + position + "' AND hv_code = '" + check_hyb.get(6) + "'", null);
                    if (GetWareHouseDetails.moveToFirst()) {
                        do {
                            try {
                                String value = (new String(GetWareHouseDetails.getString(0)));
                                Log.e("values----345",value);
                                holder.edit_seven.setText(value);
                            } catch (Exception e) {
                                Log.e(TAG, "Int id error " + e.toString());
                            }
                        } while (GetWareHouseDetails.moveToNext());
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (isTableExists(dbGetMaster, "TriatsMaster")) {

                    GetWareHouseDetails = dbGetMaster.rawQuery("select ob_value from TriatsMaster WHERE position ='" + position + "' AND hv_code = '" + check_hyb.get(7) + "'", null);
                    if (GetWareHouseDetails.moveToFirst()) {
                        do {
                            try {
                                String value = (new String(GetWareHouseDetails.getString(0)));
                                Log.e("values----345",value);
                                holder.edit_eight.setText(value);
                            } catch (Exception e) {
                                Log.e(TAG, "Int id error " + e.toString());
                            }
                        } while (GetWareHouseDetails.moveToNext());
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (isTableExists(dbGetMaster, "TriatsMaster")) {

                    GetWareHouseDetails = dbGetMaster.rawQuery("select ob_value from TriatsMaster WHERE position ='" + position + "' AND hv_code = '" + check_hyb.get(8) + "'", null);
                    if (GetWareHouseDetails.moveToFirst()) {
                        do {
                            try {
                                String value = (new String(GetWareHouseDetails.getString(0)));
                                Log.e("values----345",value);
                                holder.edit_nine.setText(value);
                            } catch (Exception e) {
                                Log.e(TAG, "Int id error " + e.toString());
                            }
                        } while (GetWareHouseDetails.moveToNext());
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (isTableExists(dbGetMaster, "TriatsMaster")) {

                    GetWareHouseDetails = dbGetMaster.rawQuery("select ob_value from TriatsMaster WHERE position ='" + position + "' AND hv_code = '" + check_hyb.get(9) + "'", null);
                    if (GetWareHouseDetails.moveToFirst()) {
                        do {
                            try {
                                String value = (new String(GetWareHouseDetails.getString(0)));
                                Log.e("values----345",value);
                                holder.edit_ten.setText(value);
                            } catch (Exception e) {
                                Log.e(TAG, "Int id error " + e.toString());
                            }
                        } while (GetWareHouseDetails.moveToNext());
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.edit_multi.setText(ob_list.setValue());
            holder.edit_multi_two.setText(ob_list.setValue());
            holder.edit_multi_three.setText(ob_list.setValue());
            holder.edit_multi_four.setText(ob_list.setValue());
            holder.edit_multi_five.setText(ob_list.setValue());
            holder.edit_multi_six.setText( ob_list.setValue());
            holder.edit_multi_seven.setText(ob_list.setValue());
            holder.edit_multi_eight.setText(ob_list.setValue());
            holder.edit_multi_nine.setText(ob_list.setValue());
            holder.edit_multi_ten.setText(ob_list.setValue());

            holder.text_mdate.setText(ob_list.setValue());
            holder.date_two.setText(ob_list.setValue());
            holder.date_three.setText(ob_list.setValue());
            holder.date_four.setText(ob_list.setValue());
            holder.date_five.setText(ob_list.setValue());
            holder.date_six.setText(ob_list.setValue());
            holder.date_seven.setText(ob_list.setValue());
            holder.date_eight.setText(ob_list.setValue());
            holder.date_nine.setText(ob_list.setValue());
            holder.date_ten.setText(ob_list.setValue());

            if (!ob_list.setValue().equals("")){
                holder.img_view.setVisibility(View.VISIBLE);
                holder.img_photo_two.setVisibility(View.VISIBLE);
                holder.img_photo_three.setVisibility(View.VISIBLE);
                holder.img_photo_four.setVisibility(View.VISIBLE);
                holder.img_photo_five.setVisibility(View.VISIBLE);
                holder.img_photo_six.setVisibility(View.VISIBLE);
                holder.img_photo_seven.setVisibility(View.VISIBLE);
                holder.img_photo_eight.setVisibility(View.VISIBLE);
                holder.img_photo_nine.setVisibility(View.VISIBLE);
                holder.img_photo_ten.setVisibility(View.VISIBLE);
//                holder.img_view.setImageBitmap(StringToBitMap(ob_list.setValue()));
                holder.text_img.setVisibility(View.VISIBLE);
                holder.text_img.setText(sessionsave.get_image("general_len")+" images added");
                holder.text_img.setEnabled(false);

                holder.text_click_img_two.setVisibility(View.VISIBLE);
                holder.text_click_img_two.setText(sessionsave.get_image("general_len")+" images added");
                holder.text_click_img_two.setEnabled(false);

                holder.text_click_img_three.setVisibility(View.VISIBLE);
                holder.text_click_img_three.setText(sessionsave.get_image("general_len")+" images added");
                holder.text_click_img_three.setEnabled(false);

                holder.text_click_img_four.setVisibility(View.VISIBLE);
                holder.text_click_img_four.setText(sessionsave.get_image("general_len")+" images added");
                holder.text_click_img_four.setEnabled(false);

                holder.text_click_img_five.setVisibility(View.VISIBLE);
                holder.text_click_img_five.setText(sessionsave.get_image("general_len")+" images added");
                holder.text_click_img_five.setEnabled(false);

                holder.text_click_img_six.setVisibility(View.VISIBLE);
                holder.text_click_img_six.setText(sessionsave.get_image("general_len")+" images added");
                holder.text_click_img_six.setEnabled(false);

                holder.text_click_img_seven.setVisibility(View.VISIBLE);
                holder.text_click_img_seven.setText(sessionsave.get_image("general_len")+" images added");
                holder.text_click_img_seven.setEnabled(false);

                holder.text_click_img_eight.setVisibility(View.VISIBLE);
                holder.text_click_img_eight.setText(sessionsave.get_image("general_len")+" images added");
                holder.text_click_img_eight.setEnabled(false);

                holder.text_click_img_nine.setVisibility(View.VISIBLE);
                holder.text_click_img_nine.setText(sessionsave.get_image("general_len")+" images added");
                holder.text_click_img_nine.setEnabled(false);

                holder.text_click_img_ten.setVisibility(View.VISIBLE);
                holder.text_click_img_ten.setText(sessionsave.get_image("general_len")+" images added");
                holder.text_click_img_ten.setEnabled(false);


                holder.text_change_img.setVisibility(View.VISIBLE);
                holder.text_change_img_two.setVisibility(View.VISIBLE);
                holder.text_change_img_three.setVisibility(View.VISIBLE);
                holder.text_change_img_four.setVisibility(View.VISIBLE);
                holder.text_change_img_five.setVisibility(View.VISIBLE);
                holder.text_change_img_six.setVisibility(View.VISIBLE);
                holder.text_change_img_seven.setVisibility(View.VISIBLE);
                holder.text_change_img_eight.setVisibility(View.VISIBLE);
                holder.text_change_img_nine.setVisibility(View.VISIBLE);
                holder.text_change_img_ten.setVisibility(View.VISIBLE);

            }else {
                holder.text_change_img.setVisibility(View.GONE);
                holder.text_change_img_two.setVisibility(View.GONE);
                holder.text_change_img_three.setVisibility(View.GONE);
                holder.text_change_img_four.setVisibility(View.GONE);
                holder.text_change_img_five.setVisibility(View.GONE);
                holder.text_change_img_six.setVisibility(View.GONE);
                holder.text_change_img_seven.setVisibility(View.GONE);
                holder.text_change_img_eight.setVisibility(View.GONE);
                holder.text_change_img_nine.setVisibility(View.GONE);
                holder.text_change_img_ten.setVisibility(View.GONE);
                holder.text_img.setEnabled(true);
                holder.text_click_img_two.setEnabled(true);
                holder.text_click_img_three.setEnabled(true);
                holder.text_click_img_four.setEnabled(true);
                holder.text_click_img_five.setEnabled(true);
                holder.text_click_img_six.setEnabled(true);
                holder.text_click_img_seven.setEnabled(true);
                holder.text_click_img_eight.setEnabled(true);
                holder.text_click_img_nine.setEnabled(true);
                holder.text_click_img_ten.setEnabled(true);
            }

            holder.next.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View view) {
                    if (check_save()) {
                        Intent broadCastReceiver = new Intent();
                    broadCastReceiver.setAction("CHAT_RECEIVED");
                        final LocationManager manager = (LocationManager) Objects.requireNonNull(mCtx).getSystemService( Context.LOCATION_SERVICE );
                        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
//            displayLocationSettingsRequest(this);
                        }
                        else {
                            gps = new GPSTracker(mCtx);
                            // check if GPS enabled
                            if (gps.canGetLocation()) {
                                latitude = gps.getLatitude();
                                longitude = gps.getLongitude();
                                //  strAdd = getAddressFromLocation(latitude, longitude);
//                    statusCheck();
                                strAdd = getCompleteAddressString(latitude, longitude);
                                save_general();


                            }
                        }

//                        broadCastReceiver.putExtra("id", "2");
//                    mCtx.sendBroadcast(broadCastReceiver);
                    }else {
                        Toast.makeText(mCtx, "Please enter the required(*) fields and try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.btn_previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        Intent broadCastReceiver = new Intent();
                        broadCastReceiver.setAction("CHAT_RECEIVED");
                            save_general();
                            broadCastReceiver.putExtra("id", "0");
                        mCtx.sendBroadcast(broadCastReceiver);
                }
            });

            switch (ob_list.setType()) {
                case "select":
                    holder.text_medit.setVisibility(View.GONE);
                    holder.edit_two.setVisibility(View.GONE);
                    holder.edit_three.setVisibility(View.GONE);
                    holder.edit_four.setVisibility(View.GONE);
                    holder.edit_five.setVisibility(View.GONE);
                    holder.edit_six.setVisibility(View.GONE);
                    holder.edit_seven.setVisibility(View.GONE);
                    holder.edit_eight.setVisibility(View.GONE);
                    holder.edit_nine.setVisibility(View.GONE);
                    holder.edit_ten.setVisibility(View.GONE);
                    holder.r_spin.setVisibility(View.VISIBLE);
                    holder.rspin_two.setVisibility(View.VISIBLE);
                    holder.rspin_three.setVisibility(View.VISIBLE);
                    holder.rspin_four.setVisibility(View.VISIBLE);
                    holder.rspin_five.setVisibility(View.VISIBLE);
                    holder.rspin_six.setVisibility(View.VISIBLE);
                    holder.rspin_seven.setVisibility(View.VISIBLE);
                    holder.rspin_eight.setVisibility(View.VISIBLE);
                    holder.rspin_nine.setVisibility(View.VISIBLE);
                    holder.rspin_ten.setVisibility(View.VISIBLE);
                    String[] choice_desc_val = ob_list.setChoice_desc();
                    String[] choice_desc = new String[ob_list.setChoice_desc().length + 1];
                    choice_desc[0] = "Select";
                    System.arraycopy(choice_desc_val, 0, choice_desc, 1, ob_list.setChoice_desc().length);
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this.mCtx, android.R.layout.simple_spinner_item, choice_desc);

                    String[] choice_desc_val_2 = ob_list.setChoice_val();
                    choice_desc_2 = new String[ob_list.setChoice_val().length + 1];
                    choice_desc_2[0] = "Select";
                    System.arraycopy(choice_desc_val_2, 0, choice_desc_2, 1, ob_list.setChoice_val().length);

                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    holder.mspin.setAdapter(dataAdapter);

                    String[] choice_desc_val1 = ob_list.setChoice_desc();
                    String[] choice_desc1= new String[ob_list.setChoice_desc().length + 1];
                    choice_desc[0] = "Select";
                    System.arraycopy(choice_desc_val1, 0, choice_desc1, 1, ob_list.setChoice_desc().length);
                    ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this.mCtx, android.R.layout.simple_spinner_item, choice_desc);

                    String[] choice_desc_val_two = ob_list.setChoice_val();
                    choice_desc_2 = new String[ob_list.setChoice_val().length + 1];
                    choice_desc_2[0] = "Select";
                    System.arraycopy(choice_desc_val_two, 0, choice_desc_2, 1, ob_list.setChoice_val().length);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    holder.spin_two.setAdapter(dataAdapter);

                    holder.spin_three.setAdapter(dataAdapter);
                    holder.spin_four.setAdapter(dataAdapter);
                    holder.spin_five.setAdapter(dataAdapter);
                    holder.spin_six.setAdapter(dataAdapter);
                    holder.spin_seven.setAdapter(dataAdapter);
                    holder.spin_eight.setAdapter(dataAdapter);
                    holder.spin_nine.setAdapter(dataAdapter);
                    holder.spin_ten.setAdapter(dataAdapter);

                    try {
                        if (isTableExists(dbGetMaster, "TriatsMaster")) {

              GetWareHouseDetails = dbGetMaster.rawQuery("select ob_value from TriatsMaster WHERE position ='" + position + "' AND hv_code = '" + check_hyb.get(0) + "'", null);
              if (GetWareHouseDetails.moveToFirst()) {
              do {
              try {
              String value = (new String(GetWareHouseDetails.getString(0)));
              Log.e("values----"+position,value);
              int spin_value = Integer.parseInt(value);
              Log.e("values----spin"+position, String.valueOf(spin_value));
              holder.mspin.setSelection(spin_value);
              } catch (Exception e) {
              Log.e(TAG, "Int id error " + e.toString());
              }
              } while (GetWareHouseDetails.moveToNext());
              }

              }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (isTableExists(dbGetMaster, "TriatsMaster")) {

             GetWareHouseDetails = dbGetMaster.rawQuery("select ob_value from TriatsMaster WHERE position ='" + position + "' AND hv_code = '" + check_hyb.get(1) + "'", null);
             if (GetWareHouseDetails.moveToFirst()) {
             do {
             try {
             String value = (new String(GetWareHouseDetails.getString(0)));
             Log.e("values----"+position,value);
             int spin_value = Integer.parseInt(value);
             Log.e("values----spin"+position, String.valueOf(spin_value));
             holder.spin_two.setSelection(spin_value);
             } catch (Exception e) {
             Log.e(TAG, "Int id error " + e.toString());
             }
             } while (GetWareHouseDetails.moveToNext());
             }

             }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (isTableExists(dbGetMaster, "TriatsMaster")) {

             GetWareHouseDetails = dbGetMaster.rawQuery("select ob_value from TriatsMaster WHERE position ='" + position + "' AND hv_code = '" + check_hyb.get(2) + "'", null);
             if (GetWareHouseDetails.moveToFirst()) {
             do {
             try {
             String value = (new String(GetWareHouseDetails.getString(0)));
             Log.e("values----"+position,value);
             int spin_value = Integer.parseInt(value);
             Log.e("values----spin"+position, String.valueOf(spin_value));
             holder.spin_three.setSelection(spin_value);
             } catch (Exception e) {
             Log.e(TAG, "Int id error " + e.toString());
             }
             } while (GetWareHouseDetails.moveToNext());
             }

             }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (isTableExists(dbGetMaster, "TriatsMaster")) {

             GetWareHouseDetails = dbGetMaster.rawQuery("select ob_value from TriatsMaster WHERE position ='" + position + "' AND hv_code = '" + check_hyb.get(3) + "'", null);
             if (GetWareHouseDetails.moveToFirst()) {
             do {
             try {
             String value = (new String(GetWareHouseDetails.getString(0)));
             Log.e("values----"+position,value);
             int spin_value = Integer.parseInt(value);
             Log.e("values----spin"+position, String.valueOf(spin_value));
             holder.spin_four.setSelection(spin_value);
             } catch (Exception e) {
             Log.e(TAG, "Int id error " + e.toString());
             }
             } while (GetWareHouseDetails.moveToNext());
             }

             }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (isTableExists(dbGetMaster, "TriatsMaster")) {

             GetWareHouseDetails = dbGetMaster.rawQuery("select ob_value from TriatsMaster WHERE position ='" + position + "' AND hv_code = '" + check_hyb.get(4) + "'", null);
             if (GetWareHouseDetails.moveToFirst()) {
             do {
             try {
             String value = (new String(GetWareHouseDetails.getString(0)));
             Log.e("values----"+position,value);
             int spin_value = Integer.parseInt(value);
             Log.e("values----spin"+position, String.valueOf(spin_value));
             holder.spin_five.setSelection(spin_value);
             } catch (Exception e) {
             Log.e(TAG, "Int id error " + e.toString());
             }
             } while (GetWareHouseDetails.moveToNext());
             }

             }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (isTableExists(dbGetMaster, "TriatsMaster")) {

             GetWareHouseDetails = dbGetMaster.rawQuery("select ob_value from TriatsMaster WHERE position ='" + position + "' AND hv_code = '" + check_hyb.get(5) + "'", null);
             if (GetWareHouseDetails.moveToFirst()) {
             do {
             try {
             String value = (new String(GetWareHouseDetails.getString(0)));
             Log.e("values----"+position,value);
             int spin_value = Integer.parseInt(value);
             Log.e("values----spin"+position, String.valueOf(spin_value));
             holder.spin_six.setSelection(spin_value);
             } catch (Exception e) {
             Log.e(TAG, "Int id error " + e.toString());
             }
             } while (GetWareHouseDetails.moveToNext());
             }

             }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (isTableExists(dbGetMaster, "TriatsMaster")) {

             GetWareHouseDetails = dbGetMaster.rawQuery("select ob_value from TriatsMaster WHERE position ='" + position + "' AND hv_code = '" + check_hyb.get(6) + "'", null);
             if (GetWareHouseDetails.moveToFirst()) {
             do {
             try {
             String value = (new String(GetWareHouseDetails.getString(0)));
             Log.e("values----"+position,value);
             int spin_value = Integer.parseInt(value);
             Log.e("values----spin"+position, String.valueOf(spin_value));
             holder.spin_seven.setSelection(spin_value);
             } catch (Exception e) {
             Log.e(TAG, "Int id error " + e.toString());
             }
             } while (GetWareHouseDetails.moveToNext());
             }

             }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (isTableExists(dbGetMaster, "TriatsMaster")) {

             GetWareHouseDetails = dbGetMaster.rawQuery("select ob_value from TriatsMaster WHERE position ='" + position + "' AND hv_code = '" + check_hyb.get(7) + "'", null);
             if (GetWareHouseDetails.moveToFirst()) {
             do {
             try {
             String value = (new String(GetWareHouseDetails.getString(0)));
             Log.e("values----"+position,value);
             int spin_value = Integer.parseInt(value);
             Log.e("values----spin"+position, String.valueOf(spin_value));
             holder.spin_eight.setSelection(spin_value);
             } catch (Exception e) {
             Log.e(TAG, "Int id error " + e.toString());
             }
             } while (GetWareHouseDetails.moveToNext());
             }

             }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (isTableExists(dbGetMaster, "TriatsMaster")) {

         GetWareHouseDetails = dbGetMaster.rawQuery("select ob_value from TriatsMaster WHERE position ='" + position + "' AND hv_code = '" + check_hyb.get(8) + "'", null);
         if (GetWareHouseDetails.moveToFirst()) {
         do {
         try {
         String value = (new String(GetWareHouseDetails.getString(0)));
         Log.e("values----"+position,value);
         int spin_value = Integer.parseInt(value);
         Log.e("values----spin"+position, String.valueOf(spin_value));
         holder.spin_nine.setSelection(spin_value);
         } catch (Exception e) {
         Log.e(TAG, "Int id error " + e.toString());
         }
         } while (GetWareHouseDetails.moveToNext());
         }

         }
                    try {
                        if (isTableExists(dbGetMaster, "TriatsMaster")) {

                        GetWareHouseDetails = dbGetMaster.rawQuery("select ob_value from TriatsMaster WHERE position ='" + position + "' AND hv_code = '" + check_hyb.get(9) + "'", null);
                        if (GetWareHouseDetails.moveToFirst()) {
                        do {
                        try {
                        String value = (new String(GetWareHouseDetails.getString(0)));
                        Log.e("values----"+position,value);
                        int spin_value = Integer.parseInt(value);
                        Log.e("values----spin"+position, String.valueOf(spin_value));
                        holder.spin_ten.setSelection(spin_value);
                        } catch (Exception e) {
                        Log.e(TAG, "Int id error " + e.toString());
                        }
                        } while (GetWareHouseDetails.moveToNext());
                        }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//



//                    try {
//                        for (int  i = 0 ; i < choice_desc_2.length ; i++){
//                            if (choice_desc_2[i].equals(ob_list.setValue())){
//                                holder.mspin.setSelection(i);
////                                holder.spin_two.setSelection(i);
////                                holder.spin_three.setSelection(i);
////                                holder.spin_four.setSelection(i);
////                                holder.spin_five.setSelection(i);
////                                holder.spin_six.setSelection(i);
////                                holder.spin_seven.setSelection(i);
////                                holder.spin_eight.setSelection(i);
////                                holder.spin_nine.setSelection(i);
////                                holder.spin_ten.setSelection(i);
//                                sessionsave.save_two("general_" + position, choice_desc_2[i]);
//                            }
//                        }
//                        sessionsave.save_two("general_key_" + position, ob_list.setKey() + "");
////                        try {
////                            check_Val(ob_list);
////                        }catch (Exception e){
////                            Log.e(TAG ,"Check val based error "+e.toString());
////                        }
//                    } catch (Exception e) {
//                        holder.mspin.setSelection(0);
////                        holder.spin_two.setSelection(0);
////                        holder.spin_three.setSelection(0);
////                        holder.spin_four.setSelection(0);
////                        holder.spin_five.setSelection(0);
////                        holder.spin_six.setSelection(0);
////                        holder.spin_seven.setSelection(0);
////                        holder.spin_eight.setSelection(0);
////                        holder.spin_nine.setSelection(0);
////                        holder.spin_ten.setSelection(0);
//                    }
                    break;

                case "checkbox":
                    holder.text_medit.setVisibility(View.GONE);
                    holder.edit_two.setVisibility(View.GONE);
                    holder.edit_three.setVisibility(View.GONE);
                    holder.edit_four.setVisibility(View.GONE);
                    holder.edit_five.setVisibility(View.GONE);
                    holder.edit_six.setVisibility(View.GONE);
                    holder.edit_seven.setVisibility(View.GONE);
                    holder.edit_eight.setVisibility(View.GONE);
                    holder.edit_nine.setVisibility(View.GONE);
                    holder.edit_ten.setVisibility(View.GONE);
                    holder.r_check.setVisibility(View.VISIBLE);
                    holder.r_check_two.setVisibility(View.VISIBLE);
                    holder.r_check_three.setVisibility(View.VISIBLE);
                    holder.r_check_four.setVisibility(View.VISIBLE);
                    holder.r_check_five.setVisibility(View.VISIBLE);
                    holder.r_check_six.setVisibility(View.VISIBLE);
                    holder.r_check_seven.setVisibility(View.VISIBLE);
                    holder.r_check_eight.setVisibility(View.VISIBLE);
                    holder.r_check_nine.setVisibility(View.VISIBLE);
                    holder.r_check_ten.setVisibility(View.VISIBLE);
                    RecyclerView.Adapter adapter = new check_adapter(mCtx,ob_list ,position ,"g_list");
                    holder.list_check.setAdapter(adapter);
                    break;

                case "date":
                    holder.r_date.setVisibility((View.VISIBLE));
                    holder.rdate_two.setVisibility((View.VISIBLE));
                    holder.rdate_three.setVisibility((View.VISIBLE));
                    holder.rdate_four.setVisibility((View.VISIBLE));
                    holder.rdate_five.setVisibility((View.VISIBLE));
                    holder.rdate_six.setVisibility((View.VISIBLE));
                    holder.rdate_seven.setVisibility((View.VISIBLE));
                    holder.rdate_eight.setVisibility((View.VISIBLE));
                    holder.rdate_nine.setVisibility((View.VISIBLE));
                    holder.rdate_ten.setVisibility((View.VISIBLE));
                    holder.text_medit.setVisibility(View.GONE);
                    holder.edit_two.setVisibility(View.GONE);
                    holder.edit_three.setVisibility(View.GONE);
                    holder.edit_four.setVisibility(View.GONE);
                    holder.edit_five.setVisibility(View.GONE);
                    holder.edit_six.setVisibility(View.GONE);
                    holder.edit_seven.setVisibility(View.GONE);
                    holder.edit_eight.setVisibility(View.GONE);
                    holder.edit_nine.setVisibility(View.GONE);
                    holder.edit_ten.setVisibility(View.GONE);
//                    holder.r_spin.setVisibility(View.GONE);

                    holder.text_mdate.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // calender class's instance and get current date , month and year from calender
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR); // current year
                            int mMonth = c.get(Calendar.MONTH); // current mont
                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                            // date picker dialog
                            datePickerDialog = new DatePickerDialog(mCtx,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            // set day of month , month and year value in the edit text
                                            holder.text_mdate.setText(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            crop_data[position] = dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year);
                                            ob_list.getValue(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            sessionsave.save_two("general_" + position, dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            sessionsave.save_two("general_key_" + position, ob_list.setKey());
                                            try {
                                                check_Val(ob_list);
                                            }catch (Exception e){
                                                Log.e(TAG ,"Check val based error "+e.toString());
                                            }
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        }
                    });
                    holder.date_two.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // calender class's instance and get current date , month and year from calender
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR); // current year
                            int mMonth = c.get(Calendar.MONTH); // current mont
                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                            // date picker dialog
                            datePickerDialog = new DatePickerDialog(mCtx,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            // set day of month , month and year value in the edit text
                                            holder.date_two.setText(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            crop_data[position] = dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year);
                                            ob_list.getValue(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            sessionsave.save_two("general_" + position, dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            sessionsave.save_two("general_key_" + position, ob_list.setKey());
                                            try {
                                                check_Val(ob_list);
                                            }catch (Exception e){
                                                Log.e(TAG ,"Check val based error "+e.toString());
                                            }
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        }
                    });
                    holder.date_three.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // calender class's instance and get current date , month and year from calender
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR); // current year
                            int mMonth = c.get(Calendar.MONTH); // current mont
                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                            // date picker dialog
                            datePickerDialog = new DatePickerDialog(mCtx,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            // set day of month , month and year value in the edit text
                                            holder.date_three.setText(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            crop_data[position] = dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year);
                                            ob_list.getValue(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            sessionsave.save_two("general_" + position, dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            sessionsave.save_two("general_key_" + position, ob_list.setKey());
                                            try {
                                                check_Val(ob_list);
                                            }catch (Exception e){
                                                Log.e(TAG ,"Check val based error "+e.toString());
                                            }
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        }
                    });
                    holder.date_four.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // calender class's instance and get current date , month and year from calender
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR); // current year
                            int mMonth = c.get(Calendar.MONTH); // current mont
                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                            // date picker dialog
                            datePickerDialog = new DatePickerDialog(mCtx,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            // set day of month , month and year value in the edit text
                                            holder.date_four.setText(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            crop_data[position] = dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year);
                                            ob_list.getValue(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            sessionsave.save_two("general_" + position, dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            sessionsave.save_two("general_key_" + position, ob_list.setKey());
                                            try {
                                                check_Val(ob_list);
                                            }catch (Exception e){
                                                Log.e(TAG ,"Check val based error "+e.toString());
                                            }
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        }
                    });
                    holder.date_five.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // calender class's instance and get current date , month and year from calender
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR); // current year
                            int mMonth = c.get(Calendar.MONTH); // current mont
                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                            // date picker dialog
                            datePickerDialog = new DatePickerDialog(mCtx,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            // set day of month , month and year value in the edit text
                                            holder.date_five.setText(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            crop_data[position] = dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year);
                                            ob_list.getValue(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            sessionsave.save_two("general_" + position, dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            sessionsave.save_two("general_key_" + position, ob_list.setKey());
                                            try {
                                                check_Val(ob_list);
                                            }catch (Exception e){
                                                Log.e(TAG ,"Check val based error "+e.toString());
                                            }
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        }
                    });
                    holder.date_six.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // calender class's instance and get current date , month and year from calender
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR); // current year
                            int mMonth = c.get(Calendar.MONTH); // current mont
                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                            // date picker dialog
                            datePickerDialog = new DatePickerDialog(mCtx,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            // set day of month , month and year value in the edit text
                                            holder.date_six.setText(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            crop_data[position] = dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year);
                                            ob_list.getValue(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            sessionsave.save_two("general_" + position, dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            sessionsave.save_two("general_key_" + position, ob_list.setKey());
                                            try {
                                                check_Val(ob_list);
                                            }catch (Exception e){
                                                Log.e(TAG ,"Check val based error "+e.toString());
                                            }
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        }
                    });
                    holder.date_seven.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // calender class's instance and get current date , month and year from calender
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR); // current year
                            int mMonth = c.get(Calendar.MONTH); // current mont
                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                            // date picker dialog
                            datePickerDialog = new DatePickerDialog(mCtx,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            // set day of month , month and year value in the edit text
                                            holder.date_seven.setText(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            crop_data[position] = dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year);
                                            ob_list.getValue(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            sessionsave.save_two("general_" + position, dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            sessionsave.save_two("general_key_" + position, ob_list.setKey());
                                            try {
                                                check_Val(ob_list);
                                            }catch (Exception e){
                                                Log.e(TAG ,"Check val based error "+e.toString());
                                            }
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        }
                    });
                    holder.date_eight.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // calender class's instance and get current date , month and year from calender
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR); // current year
                            int mMonth = c.get(Calendar.MONTH); // current mont
                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                            // date picker dialog
                            datePickerDialog = new DatePickerDialog(mCtx,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            // set day of month , month and year value in the edit text
                                            holder.date_eight.setText(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            crop_data[position] = dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year);
                                            ob_list.getValue(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            sessionsave.save_two("general_" + position, dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            sessionsave.save_two("general_key_" + position, ob_list.setKey());
                                            try {
                                                check_Val(ob_list);
                                            }catch (Exception e){
                                                Log.e(TAG ,"Check val based error "+e.toString());
                                            }
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        }
                    });
                    holder.date_nine.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // calender class's instance and get current date , month and year from calender
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR); // current year
                            int mMonth = c.get(Calendar.MONTH); // current mont
                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                            // date picker dialog
                            datePickerDialog = new DatePickerDialog(mCtx,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            // set day of month , month and year value in the edit text
                                            holder.date_nine.setText(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            crop_data[position] = dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year);
                                            ob_list.getValue(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            sessionsave.save_two("general_" + position, dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            sessionsave.save_two("general_key_" + position, ob_list.setKey());
                                            try {
                                                check_Val(ob_list);
                                            }catch (Exception e){
                                                Log.e(TAG ,"Check val based error "+e.toString());
                                            }
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        }
                    });
                    holder.date_ten.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // calender class's instance and get current date , month and year from calender
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR); // current year
                            int mMonth = c.get(Calendar.MONTH); // current mont
                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                            // date picker dialog
                            datePickerDialog = new DatePickerDialog(mCtx,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            // set day of month , month and year value in the edit text
                                            holder.date_ten.setText(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            crop_data[position] = dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year);
                                            ob_list.getValue(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            sessionsave.save_two("general_" + position, dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            sessionsave.save_two("general_key_" + position, ob_list.setKey());
                                            try {
                                                check_Val(ob_list);
                                            }catch (Exception e){
                                                Log.e(TAG ,"Check val based error "+e.toString());
                                            }
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        }
                    });
                    break;

                case "datetime":
                    holder.r_date.setVisibility((View.VISIBLE));
                    holder.rdate_two.setVisibility((View.VISIBLE));
                    holder.rdate_three.setVisibility((View.VISIBLE));
                    holder.rdate_four.setVisibility((View.VISIBLE));
                    holder.rdate_five.setVisibility((View.VISIBLE));
                    holder.rdate_six.setVisibility((View.VISIBLE));
                    holder.rdate_seven.setVisibility((View.VISIBLE));
                    holder.rdate_eight.setVisibility((View.VISIBLE));
                    holder.rdate_nine.setVisibility((View.VISIBLE));
                    holder.rdate_ten.setVisibility((View.VISIBLE));

                    holder.text_medit.setVisibility(View.GONE);
                    holder.edit_two.setVisibility(View.GONE);
                    holder.edit_three.setVisibility(View.GONE);
                    holder.edit_four.setVisibility(View.GONE);
                    holder.edit_five.setVisibility(View.GONE);
                    holder.edit_six.setVisibility(View.GONE);
                    holder.edit_seven.setVisibility(View.GONE);
                    holder.edit_eight.setVisibility(View.GONE);
                    holder.edit_nine.setVisibility(View.GONE);
                    holder.edit_ten.setVisibility(View.GONE);
//                    holder.r_spin.setVisibility(View.GONE);

                    holder.text_mdate.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // calender class's instance and get current date , month and year from calender
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR); // current year
                            int mMonth = c.get(Calendar.MONTH); // current mont
                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                            // date picker dialog
                            datePickerDialog = new DatePickerDialog(mCtx,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            // set day of month , month and year value in the edit text
//                                            holder.text_mdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                                            crop_data[position] = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                                            ob_list.getValue(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                            date = dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year);
                                            int mHour = c.get(Calendar.HOUR_OF_DAY);
                                            int mMinute = c.get(Calendar.MINUTE);

                                            // Launch Time Picker Dialog
                                            TimePickerDialog timePickerDialog = new TimePickerDialog(mCtx,
                                                    new TimePickerDialog.OnTimeSetListener() {

                                                        @Override
                                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                                            holder.text_mdate.setText(date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            crop_data[position] = date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute);
                                                            ob_list.getValue(date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            sessionsave.save_two("general_" + position, date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            sessionsave.save_two("general_key_" + position, ob_list.setKey());
                                                            try {
                                                                check_Val(ob_list);
                                                            }catch (Exception e){
                                                                Log.e(TAG ,"Check val based error "+e.toString());
                                                            }
                                                        }
                                                    }, mHour, mMinute, false);
                                            timePickerDialog.show();
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        }
                    });
                    holder.date_two.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // calender class's instance and get current date , month and year from calender
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR); // current year
                            int mMonth = c.get(Calendar.MONTH); // current mont
                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                            // date picker dialog
                            datePickerDialog = new DatePickerDialog(mCtx,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            // set day of month , month and year value in the edit text
//                                            holder.text_mdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                                            crop_data[position] = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                                            ob_list.getValue(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                            date = dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year);
                                            int mHour = c.get(Calendar.HOUR_OF_DAY);
                                            int mMinute = c.get(Calendar.MINUTE);

                                            // Launch Time Picker Dialog
                                            TimePickerDialog timePickerDialog = new TimePickerDialog(mCtx,
                                                    new TimePickerDialog.OnTimeSetListener() {

                                                        @Override
                                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                                            holder.date_two.setText(date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            crop_data[position] = date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute);
                                                            ob_list.getValue(date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            sessionsave.save_two("general_" + position, date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            sessionsave.save_two("general_key_" + position, ob_list.setKey());
                                                            try {
                                                                check_Val(ob_list);
                                                            }catch (Exception e){
                                                                Log.e(TAG ,"Check val based error "+e.toString());
                                                            }
                                                        }
                                                    }, mHour, mMinute, false);
                                            timePickerDialog.show();
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        }
                    });
                    holder.date_three.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // calender class's instance and get current date , month and year from calender
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR); // current year
                            int mMonth = c.get(Calendar.MONTH); // current mont
                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                            // date picker dialog
                            datePickerDialog = new DatePickerDialog(mCtx,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            // set day of month , month and year value in the edit text
//                                            holder.text_mdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                                            crop_data[position] = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                                            ob_list.getValue(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                            date = dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year);
                                            int mHour = c.get(Calendar.HOUR_OF_DAY);
                                            int mMinute = c.get(Calendar.MINUTE);

                                            // Launch Time Picker Dialog
                                            TimePickerDialog timePickerDialog = new TimePickerDialog(mCtx,
                                                    new TimePickerDialog.OnTimeSetListener() {

                                                        @Override
                                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                                            holder.date_three.setText(date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            crop_data[position] = date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute);
                                                            ob_list.getValue(date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            sessionsave.save_two("general_" + position, date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            sessionsave.save_two("general_key_" + position, ob_list.setKey());
                                                            try {
                                                                check_Val(ob_list);
                                                            }catch (Exception e){
                                                                Log.e(TAG ,"Check val based error "+e.toString());
                                                            }
                                                        }
                                                    }, mHour, mMinute, false);
                                            timePickerDialog.show();
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        }
                    });
                    holder.date_four.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // calender class's instance and get current date , month and year from calender
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR); // current year
                            int mMonth = c.get(Calendar.MONTH); // current mont
                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                            // date picker dialog
                            datePickerDialog = new DatePickerDialog(mCtx,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            // set day of month , month and year value in the edit text
//                                            holder.text_mdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                                            crop_data[position] = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                                            ob_list.getValue(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                            date = dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year);
                                            int mHour = c.get(Calendar.HOUR_OF_DAY);
                                            int mMinute = c.get(Calendar.MINUTE);

                                            // Launch Time Picker Dialog
                                            TimePickerDialog timePickerDialog = new TimePickerDialog(mCtx,
                                                    new TimePickerDialog.OnTimeSetListener() {

                                                        @Override
                                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                                            holder.date_four.setText(date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            crop_data[position] = date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute);
                                                            ob_list.getValue(date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            sessionsave.save_two("general_" + position, date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            sessionsave.save_two("general_key_" + position, ob_list.setKey());
                                                            try {
                                                                check_Val(ob_list);
                                                            }catch (Exception e){
                                                                Log.e(TAG ,"Check val based error "+e.toString());
                                                            }
                                                        }
                                                    }, mHour, mMinute, false);
                                            timePickerDialog.show();
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        }
                    });
                    holder.date_five.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // calender class's instance and get current date , month and year from calender
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR); // current year
                            int mMonth = c.get(Calendar.MONTH); // current mont
                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                            // date picker dialog
                            datePickerDialog = new DatePickerDialog(mCtx,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            // set day of month , month and year value in the edit text
//                                            holder.text_mdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                                            crop_data[position] = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                                            ob_list.getValue(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                            date = dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year);
                                            int mHour = c.get(Calendar.HOUR_OF_DAY);
                                            int mMinute = c.get(Calendar.MINUTE);

                                            // Launch Time Picker Dialog
                                            TimePickerDialog timePickerDialog = new TimePickerDialog(mCtx,
                                                    new TimePickerDialog.OnTimeSetListener() {

                                                        @Override
                                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                                            holder.date_five.setText(date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            crop_data[position] = date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute);
                                                            ob_list.getValue(date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            sessionsave.save_two("general_" + position, date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            sessionsave.save_two("general_key_" + position, ob_list.setKey());
                                                            try {
                                                                check_Val(ob_list);
                                                            }catch (Exception e){
                                                                Log.e(TAG ,"Check val based error "+e.toString());
                                                            }
                                                        }
                                                    }, mHour, mMinute, false);
                                            timePickerDialog.show();
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        }
                    });
                    holder.date_six.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // calender class's instance and get current date , month and year from calender
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR); // current year
                            int mMonth = c.get(Calendar.MONTH); // current mont
                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                            // date picker dialog
                            datePickerDialog = new DatePickerDialog(mCtx,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            // set day of month , month and year value in the edit text
//                                            holder.text_mdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                                            crop_data[position] = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                                            ob_list.getValue(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                            date = dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year);
                                            int mHour = c.get(Calendar.HOUR_OF_DAY);
                                            int mMinute = c.get(Calendar.MINUTE);

                                            // Launch Time Picker Dialog
                                            TimePickerDialog timePickerDialog = new TimePickerDialog(mCtx,
                                                    new TimePickerDialog.OnTimeSetListener() {

                                                        @Override
                                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                                            holder.date_six.setText(date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            crop_data[position] = date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute);
                                                            ob_list.getValue(date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            sessionsave.save_two("general_" + position, date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            sessionsave.save_two("general_key_" + position, ob_list.setKey());
                                                            try {
                                                                check_Val(ob_list);
                                                            }catch (Exception e){
                                                                Log.e(TAG ,"Check val based error "+e.toString());
                                                            }
                                                        }
                                                    }, mHour, mMinute, false);
                                            timePickerDialog.show();
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        }
                    });
                    holder.date_seven.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // calender class's instance and get current date , month and year from calender
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR); // current year
                            int mMonth = c.get(Calendar.MONTH); // current mont
                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                            // date picker dialog
                            datePickerDialog = new DatePickerDialog(mCtx,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            // set day of month , month and year value in the edit text
//                                            holder.text_mdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                                            crop_data[position] = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                                            ob_list.getValue(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                            date = dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year);
                                            int mHour = c.get(Calendar.HOUR_OF_DAY);
                                            int mMinute = c.get(Calendar.MINUTE);

                                            // Launch Time Picker Dialog
                                            TimePickerDialog timePickerDialog = new TimePickerDialog(mCtx,
                                                    new TimePickerDialog.OnTimeSetListener() {

                                                        @Override
                                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                                            holder.date_seven.setText(date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            crop_data[position] = date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute);
                                                            ob_list.getValue(date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            sessionsave.save_two("general_" + position, date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            sessionsave.save_two("general_key_" + position, ob_list.setKey());
                                                            try {
                                                                check_Val(ob_list);
                                                            }catch (Exception e){
                                                                Log.e(TAG ,"Check val based error "+e.toString());
                                                            }
                                                        }
                                                    }, mHour, mMinute, false);
                                            timePickerDialog.show();
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        }
                    });
                    holder.date_eight.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // calender class's instance and get current date , month and year from calender
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR); // current year
                            int mMonth = c.get(Calendar.MONTH); // current mont
                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                            // date picker dialog
                            datePickerDialog = new DatePickerDialog(mCtx,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            // set day of month , month and year value in the edit text
//                                            holder.text_mdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                                            crop_data[position] = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                                            ob_list.getValue(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                            date = dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year);
                                            int mHour = c.get(Calendar.HOUR_OF_DAY);
                                            int mMinute = c.get(Calendar.MINUTE);

                                            // Launch Time Picker Dialog
                                            TimePickerDialog timePickerDialog = new TimePickerDialog(mCtx,
                                                    new TimePickerDialog.OnTimeSetListener() {

                                                        @Override
                                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                                            holder.date_eight.setText(date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            crop_data[position] = date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute);
                                                            ob_list.getValue(date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            sessionsave.save_two("general_" + position, date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            sessionsave.save_two("general_key_" + position, ob_list.setKey());
                                                            try {
                                                                check_Val(ob_list);
                                                            }catch (Exception e){
                                                                Log.e(TAG ,"Check val based error "+e.toString());
                                                            }
                                                        }
                                                    }, mHour, mMinute, false);
                                            timePickerDialog.show();
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        }
                    });
                    holder.date_nine.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // calender class's instance and get current date , month and year from calender
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR); // current year
                            int mMonth = c.get(Calendar.MONTH); // current mont
                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                            // date picker dialog
                            datePickerDialog = new DatePickerDialog(mCtx,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            // set day of month , month and year value in the edit text
//                                            holder.text_mdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                                            crop_data[position] = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                                            ob_list.getValue(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                            date = dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year);
                                            int mHour = c.get(Calendar.HOUR_OF_DAY);
                                            int mMinute = c.get(Calendar.MINUTE);

                                            // Launch Time Picker Dialog
                                            TimePickerDialog timePickerDialog = new TimePickerDialog(mCtx,
                                                    new TimePickerDialog.OnTimeSetListener() {

                                                        @Override
                                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                                            holder.date_nine.setText(date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            crop_data[position] = date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute);
                                                            ob_list.getValue(date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            sessionsave.save_two("general_" + position, date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            sessionsave.save_two("general_key_" + position, ob_list.setKey());
                                                            try {
                                                                check_Val(ob_list);
                                                            }catch (Exception e){
                                                                Log.e(TAG ,"Check val based error "+e.toString());
                                                            }
                                                        }
                                                    }, mHour, mMinute, false);
                                            timePickerDialog.show();
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        }
                    });
                    holder.date_ten.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // calender class's instance and get current date , month and year from calender
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR); // current year
                            int mMonth = c.get(Calendar.MONTH); // current mont
                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                            // date picker dialog
                            datePickerDialog = new DatePickerDialog(mCtx,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            // set day of month , month and year value in the edit text
//                                            holder.text_mdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                                            crop_data[position] = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                                            ob_list.getValue(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                            date = dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year);
                                            int mHour = c.get(Calendar.HOUR_OF_DAY);
                                            int mMinute = c.get(Calendar.MINUTE);

                                            // Launch Time Picker Dialog
                                            TimePickerDialog timePickerDialog = new TimePickerDialog(mCtx,
                                                    new TimePickerDialog.OnTimeSetListener() {

                                                        @Override
                                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                                            holder.date_ten.setText(date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            crop_data[position] = date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute);
                                                            ob_list.getValue(date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            sessionsave.save_two("general_" + position, date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            sessionsave.save_two("general_key_" + position, ob_list.setKey());
                                                            try {
                                                                check_Val(ob_list);
                                                            }catch (Exception e){
                                                                Log.e(TAG ,"Check val based error "+e.toString());
                                                            }
                                                        }
                                                    }, mHour, mMinute, false);
                                            timePickerDialog.show();
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        }
                    });
                    break;

                case "text":
                    holder.text_medit.setVisibility(View.VISIBLE);
                    holder.edit_two.setVisibility(View.VISIBLE);
                    holder.edit_three.setVisibility(View.VISIBLE);
                    holder.edit_four.setVisibility(View.VISIBLE);
                    holder.edit_five.setVisibility(View.VISIBLE);
                    holder.edit_six.setVisibility(View.VISIBLE);
                    holder.edit_seven.setVisibility(View.VISIBLE);
                    holder.edit_eight.setVisibility(View.VISIBLE);
                    holder.edit_nine.setVisibility(View.VISIBLE);
                    holder.edit_ten.setVisibility(View.VISIBLE);

                    break;

                case "image":
                    holder.text_medit.setVisibility(View.GONE);
                    holder.edit_two.setVisibility(View.GONE);
                    holder.edit_three.setVisibility(View.GONE);
                    holder.edit_four.setVisibility(View.GONE);
                    holder.edit_five.setVisibility(View.GONE);
                    holder.edit_six.setVisibility(View.GONE);
                    holder.edit_seven.setVisibility(View.GONE);
                    holder.edit_eight.setVisibility(View.GONE);
                    holder.edit_nine.setVisibility(View.GONE);
                    holder.edit_ten.setVisibility(View.GONE);
                    holder.r_img.setVisibility(View.VISIBLE);
                    holder.r_image_two.setVisibility(View.VISIBLE);
                    holder.r_image_three.setVisibility(View.VISIBLE);
                    holder.r_image_four.setVisibility(View.VISIBLE);
                    holder.r_image_five.setVisibility(View.VISIBLE);
                    holder.r_image_six.setVisibility(View.VISIBLE);
                    holder.r_image_seven.setVisibility(View.VISIBLE);
                    holder.r_image_eight.setVisibility(View.VISIBLE);
                    holder.r_image_nine.setVisibility(View.VISIBLE);
                    holder.r_image_ten.setVisibility(View.VISIBLE);
                    break;

                case "video":
                    holder.text_medit.setVisibility(View.GONE);
                    holder.edit_two.setVisibility(View.GONE);
                    holder.edit_three.setVisibility(View.GONE);
                    holder.edit_four.setVisibility(View.GONE);
                    holder.edit_five.setVisibility(View.GONE);
                    holder.edit_six.setVisibility(View.GONE);
                    holder.edit_seven.setVisibility(View.GONE);
                    holder.edit_eight.setVisibility(View.GONE);
                    holder.edit_nine.setVisibility(View.GONE);
                    holder.edit_ten.setVisibility(View.GONE);
                    holder.r_video.setVisibility(View.VISIBLE);
                    holder.r_video_two.setVisibility(View.VISIBLE);
                    holder.r_video_three.setVisibility(View.VISIBLE);
                    holder.r_video_four.setVisibility(View.VISIBLE);
                    holder.r_video_five.setVisibility(View.VISIBLE);
                    holder.r_video_seven.setVisibility(View.VISIBLE);
                    holder.r_video_six.setVisibility(View.VISIBLE);
                    holder.r_video_eight.setVisibility(View.VISIBLE);
                    holder.r_video_nine.setVisibility(View.VISIBLE);
                    holder.r_video_ten.setVisibility(View.VISIBLE);
                    break;

                case "textarea":
                    holder.text_medit.setVisibility(View.GONE);
                    holder.edit_two.setVisibility(View.GONE);
                    holder.edit_three.setVisibility(View.GONE);
                    holder.edit_four.setVisibility(View.GONE);
                    holder.edit_five.setVisibility(View.GONE);
                    holder.edit_six.setVisibility(View.GONE);
                    holder.edit_seven.setVisibility(View.GONE);
                    holder.edit_eight.setVisibility(View.GONE);
                    holder.edit_nine.setVisibility(View.GONE);
                    holder.edit_ten.setVisibility(View.GONE);
                    holder.edit_multi.setVisibility(View.VISIBLE);
                    holder.edit_multi_two.setVisibility(View.VISIBLE);
                    holder.edit_multi_three.setVisibility(View.VISIBLE);
                    holder.edit_multi_four.setVisibility(View.VISIBLE);
                    holder.edit_multi_five.setVisibility(View.VISIBLE);
                    holder.edit_multi_six.setVisibility(View.VISIBLE);
                    holder.edit_multi_seven.setVisibility(View.VISIBLE);
                    holder.edit_multi_eight.setVisibility(View.VISIBLE);
                    holder.edit_multi_nine.setVisibility(View.VISIBLE);
                    holder.edit_multi_ten.setVisibility(View.VISIBLE);

                    break;

                default:
                    holder.text_medit.setVisibility(View.VISIBLE);
                    holder.text_two.setVisibility(View.VISIBLE);
                    holder.text_three.setVisibility(View.VISIBLE);
                    holder.text_four.setVisibility(View.VISIBLE);
                    holder.text_five.setVisibility(View.VISIBLE);
                    holder.text_six.setVisibility(View.VISIBLE);
                    holder.text_seven.setVisibility(View.VISIBLE);
                    holder.text_eight.setVisibility(View.VISIBLE);
                    holder.text_nine.setVisibility(View.VISIBLE);
                    holder.text_ten.setVisibility(View.VISIBLE);

                    holder.r_date.setVisibility((View.GONE));
                    holder.rdate_two.setVisibility(View.GONE);
                    holder.rdate_three.setVisibility(View.GONE);
                    holder.rdate_four.setVisibility(View.GONE);
                    holder.rdate_five.setVisibility(View.GONE);
                    holder.rdate_six.setVisibility(View.GONE);
                    holder.rdate_seven.setVisibility(View.GONE);
                    holder.rdate_eight.setVisibility(View.GONE);
                    holder.rdate_nine.setVisibility(View.GONE);
                    holder.rdate_ten.setVisibility(View.GONE);

                    holder.r_spin.setVisibility(View.GONE);
                    holder.rspin_two.setVisibility(View.GONE);
                    holder.rspin_three.setVisibility(View.GONE);
                    holder.rspin_four.setVisibility(View.GONE);
                    holder.rspin_five.setVisibility(View.GONE);
                    holder.rspin_six.setVisibility(View.GONE);
                    holder.rspin_seven.setVisibility(View.GONE);
                    holder.rspin_eight.setVisibility(View.GONE);
                    holder.rspin_nine.setVisibility(View.GONE);
                    holder.rspin_ten.setVisibility(View.GONE);

                    break;
            }

            if (position == (ob_lists.size()) - 1) {
                holder.next.setVisibility(View.VISIBLE);
                holder.btn_previous.setVisibility(View.GONE);
            } else {
                holder.next.setVisibility(View.GONE);
                holder.btn_previous.setVisibility(View.GONE);
            }

            if (position == 0) {
                holder.text_one.setVisibility(View.VISIBLE);
                holder.text_two.setVisibility(View.VISIBLE);
                holder.text2.setVisibility(View.VISIBLE);
                holder.text_three.setVisibility(View.VISIBLE);
                holder.text_four.setVisibility(View.VISIBLE);
                holder.text_five.setVisibility(View.VISIBLE);
                holder.text_six.setVisibility(View.VISIBLE);
                holder.text_seven.setVisibility(View.VISIBLE);
                holder.text_eight.setVisibility(View.VISIBLE);
                holder.text_nine.setVisibility(View.VISIBLE);
                holder.text_ten.setVisibility(View.VISIBLE);
            }else {
                holder.text_one.setVisibility(View.GONE);
                holder.text_two.setVisibility(View.GONE);
                holder.text2.setVisibility(View.GONE);
                holder.text_three.setVisibility(View.GONE);
                holder.text_four.setVisibility(View.GONE);
                holder.text_five.setVisibility(View.GONE);
                holder.text_six.setVisibility(View.GONE);
                holder.text_seven.setVisibility(View.GONE);
                holder.text_eight.setVisibility(View.GONE);
                holder.text_nine.setVisibility(View.GONE);
                holder.text_ten.setVisibility(View.GONE);
            }

            holder.text_medit.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }


                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    crop_data[position] = arg0.toString();
                    ob_list.getValue1(arg0.toString());
                    sessionsave.save_two("general_" + position, arg0.toString());
                    sessionsave.save_two("general_key_" + position, ob_list.setKey());

                    String query = "INSERT INTO TriatsMaster (hv_code,position,ob_value) VALUES('" + check_hyb.get(0).toString() + "','" + position + "','" + arg0.toString() + "');";
                    dbGetMaster.execSQL(query);
                }

            });
            holder.edit_two.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    crop_data[position] = arg0.toString();
                    ob_list.getValue1(arg0.toString());
                    sessionsave.save_two("general_" + position + "two", arg0.toString());
                    sessionsave.save_two("general_key_" + position +"two", ob_list.setKey());

                    String query = "INSERT INTO TriatsMaster (hv_code,position,ob_value) VALUES('" + check_hyb.get(1).toString() + "','" + position + "','" + arg0.toString() + "');";
                    dbGetMaster.execSQL(query);
//                    try {
//                        check_Val2(ob_list);
//                    }catch (Exception e){
//                        Log.e(TAG ,"Check val based error "+e.toString());
//                    }
                }
            });
            holder.edit_three.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    crop_data[position] = arg0.toString();
                    ob_list.getValue2(arg0.toString());
                    sessionsave.save_two("general_" + position, arg0.toString());
                    sessionsave.save_two("general_key_" + position, ob_list.setKey());
                    String query = "INSERT INTO TriatsMaster (hv_code,position,ob_value) VALUES('" + check_hyb.get(2).toString() + "','" + position + "','" + arg0.toString() + "');";
                    dbGetMaster.execSQL(query);
//                    try {
//                        check_Val(ob_list);
//                    }catch (Exception e){
//                        Log.e(TAG ,"Check val based error "+e.toString());
//                    }
                }
            });
            holder.edit_four.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    crop_data[position] = arg0.toString();
                    ob_list.getValue2(arg0.toString());
                    sessionsave.save_two("general_" + position, arg0.toString());
                    sessionsave.save_two("general_key_" + position, ob_list.setKey());
                    String query = "INSERT INTO TriatsMaster (hv_code,position,ob_value) VALUES('" + check_hyb.get(3).toString() + "','" + position + "','" + arg0.toString() + "');";
                    dbGetMaster.execSQL(query);
                    try {
                        check_Val(ob_list);
                    }catch (Exception e){
                        Log.e(TAG ,"Check val based error "+e.toString());
                    }
                }
            });
            holder.edit_five.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    crop_data[position] = arg0.toString();
                    ob_list.getValue(arg0.toString());
                    sessionsave.save_two("general_" + position, arg0.toString());
                    sessionsave.save_two("general_key_" + position, ob_list.setKey());
                    String query = "INSERT INTO TriatsMaster (hv_code,position,ob_value) VALUES('" + check_hyb.get(4).toString() + "','" + position + "','" + arg0.toString() + "');";
                    dbGetMaster.execSQL(query);
                    try {
                        check_Val(ob_list);
                    }catch (Exception e){
                        Log.e(TAG ,"Check val based error "+e.toString());
                    }
                }
            });
            holder.edit_six.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    crop_data[position] = arg0.toString();
                    ob_list.getValue(arg0.toString());
                    sessionsave.save_two("general_" + position, arg0.toString());
                    sessionsave.save_two("general_key_" + position, ob_list.setKey());
                    String query = "INSERT INTO TriatsMaster (hv_code,position,ob_value) VALUES('" + check_hyb.get(5).toString() + "','" + position + "','" + arg0.toString() + "');";
                    dbGetMaster.execSQL(query);
                    try {
                        check_Val(ob_list);
                    }catch (Exception e){
                        Log.e(TAG ,"Check val based error "+e.toString());
                    }
                }
            });
            holder.edit_seven.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    crop_data[position] = arg0.toString();
                    ob_list.getValue(arg0.toString());
                    sessionsave.save_two("general_" + position, arg0.toString());
                    sessionsave.save_two("general_key_" + position, ob_list.setKey());
                    String query = "INSERT INTO TriatsMaster (hv_code,position,ob_value) VALUES('" + check_hyb.get(6).toString() + "','" + position + "','" + arg0.toString() + "');";
                    dbGetMaster.execSQL(query);
                    try {
                        check_Val(ob_list);
                    }catch (Exception e){
                        Log.e(TAG ,"Check val based error "+e.toString());
                    }
                }
            });
            holder.edit_eight.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    crop_data[position] = arg0.toString();
                    ob_list.getValue(arg0.toString());
                    sessionsave.save_two("general_" + position, arg0.toString());
                    sessionsave.save_two("general_key_" + position, ob_list.setKey());
                    String query = "INSERT INTO TriatsMaster (hv_code,position,ob_value) VALUES('" + check_hyb.get(7).toString() + "','" + position + "','" + arg0.toString() + "');";
                    dbGetMaster.execSQL(query);
                    try {
                        check_Val(ob_list);
                    }catch (Exception e){
                        Log.e(TAG ,"Check val based error "+e.toString());
                    }
                }
            });
            holder.edit_nine.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    crop_data[position] = arg0.toString();
                    ob_list.getValue(arg0.toString());
                    sessionsave.save_two("general_" + position, arg0.toString());
                    sessionsave.save_two("general_key_" + position, ob_list.setKey());
                    String query = "INSERT INTO TriatsMaster (hv_code,position,ob_value) VALUES('" + check_hyb.get(8).toString() + "','" + position + "','" + arg0.toString() + "');";
                    dbGetMaster.execSQL(query);
                    try {
                        check_Val(ob_list);
                    }catch (Exception e){
                        Log.e(TAG ,"Check val based error "+e.toString());
                    }
                }
            });
            holder.edit_ten.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    crop_data[position] = arg0.toString();
                    ob_list.getValue(arg0.toString());
                    sessionsave.save_two("general_" + position, arg0.toString());
                    sessionsave.save_two("general_key_" + position, ob_list.setKey());
                    String query = "INSERT INTO TriatsMaster (hv_code,position,ob_value) VALUES('" + check_hyb.get(9).toString() + "','" + position + "','" + arg0.toString() + "');";
                    dbGetMaster.execSQL(query);
                    try {
                        check_Val(ob_list);
                    }catch (Exception e){
                        Log.e(TAG ,"Check val based error "+e.toString());
                    }
                }
            });






            holder.edit_multi.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    crop_data[position] = arg0.toString();
                    ob_list.getValue(arg0.toString());
                    sessionsave.save_two("general_" + position, arg0.toString());
                    sessionsave.save_two("general_key_" + position, ob_list.setKey());
                    try {
                        check_Val(ob_list);
                    }catch (Exception e){
                        Log.e(TAG ,"Check val based error "+e.toString());
                    }
                }
            });
            holder.edit_multi_two.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    crop_data[position] = arg0.toString();
                    ob_list.getValue(arg0.toString());
                    sessionsave.save_two("general_" + position, arg0.toString());
                    sessionsave.save_two("general_key_" + position, ob_list.setKey());
                    try {
                        check_Val(ob_list);
                    }catch (Exception e){
                        Log.e(TAG ,"Check val based error "+e.toString());
                    }
                }
            });
            holder.edit_multi_three.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    crop_data[position] = arg0.toString();
                    ob_list.getValue(arg0.toString());
                    sessionsave.save_two("general_" + position, arg0.toString());
                    sessionsave.save_two("general_key_" + position, ob_list.setKey());
                    try {
                        check_Val(ob_list);
                    }catch (Exception e){
                        Log.e(TAG ,"Check val based error "+e.toString());
                    }
                }
            });
            holder.edit_multi_four.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    crop_data[position] = arg0.toString();
                    ob_list.getValue(arg0.toString());
                    sessionsave.save_two("general_" + position, arg0.toString());
                    sessionsave.save_two("general_key_" + position, ob_list.setKey());
                    try {
                        check_Val(ob_list);
                    }catch (Exception e){
                        Log.e(TAG ,"Check val based error "+e.toString());
                    }
                }
            });
            holder.edit_multi_five.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    crop_data[position] = arg0.toString();
                    ob_list.getValue(arg0.toString());
                    sessionsave.save_two("general_" + position, arg0.toString());
                    sessionsave.save_two("general_key_" + position, ob_list.setKey());
                    try {
                        check_Val(ob_list);
                    }catch (Exception e){
                        Log.e(TAG ,"Check val based error "+e.toString());
                    }
                }
            });
            holder.edit_multi_six.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    crop_data[position] = arg0.toString();
                    ob_list.getValue(arg0.toString());
                    sessionsave.save_two("general_" + position, arg0.toString());
                    sessionsave.save_two("general_key_" + position, ob_list.setKey());
                    try {
                        check_Val(ob_list);
                    }catch (Exception e){
                        Log.e(TAG ,"Check val based error "+e.toString());
                    }
                }
            });
            holder.edit_multi_seven.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    crop_data[position] = arg0.toString();
                    ob_list.getValue(arg0.toString());
                    sessionsave.save_two("general_" + position, arg0.toString());
                    sessionsave.save_two("general_key_" + position, ob_list.setKey());
                    try {
                        check_Val(ob_list);
                    }catch (Exception e){
                        Log.e(TAG ,"Check val based error "+e.toString());
                    }
                }
            });
            holder.edit_multi_eight.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    crop_data[position] = arg0.toString();
                    ob_list.getValue(arg0.toString());
                    sessionsave.save_two("general_" + position, arg0.toString());
                    sessionsave.save_two("general_key_" + position, ob_list.setKey());
                    try {
                        check_Val(ob_list);
                    }catch (Exception e){
                        Log.e(TAG ,"Check val based error "+e.toString());
                    }
                }
            });
            holder.edit_multi_nine.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    crop_data[position] = arg0.toString();
                    ob_list.getValue(arg0.toString());
                    sessionsave.save_two("general_" + position, arg0.toString());
                    sessionsave.save_two("general_key_" + position, ob_list.setKey());
                    try {
                        check_Val(ob_list);
                    }catch (Exception e){
                        Log.e(TAG ,"Check val based error "+e.toString());
                    }
                }
            });
            holder.edit_multi_ten.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    crop_data[position] = arg0.toString();
                    ob_list.getValue(arg0.toString());
                    sessionsave.save_two("general_" + position, arg0.toString());
                    sessionsave.save_two("general_key_" + position, ob_list.setKey());
                    try {
                        check_Val(ob_list);
                    }catch (Exception e){
                        Log.e(TAG ,"Check val based error "+e.toString());
                    }
                }
            });

            holder.mspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    crop_data[position] = holder.mspin.getSelectedItemPosition() + "";
                    String item_position = String.valueOf(holder.mspin.getSelectedItemPosition());
                    if(!item_position.equals("0")){
                        String query = "INSERT INTO TriatsMaster (hv_code,position,ob_value) VALUES('" + check_hyb.get(0).toString() + "','" + position + "','" + item_position + "');";
                        dbGetMaster.execSQL(query);
                    }

                    Log.e("spin", item_position);
                    try {
                        String[] choice_desc_val_1 = ob_list.setChoice_val();
                        String[] choice_desc_1 = new String[ob_list.setChoice_val().length + 1];
                        choice_desc_1[0] = "Select";
                        System.arraycopy(choice_desc_val_1, 0, choice_desc_1, 1, ob_list.setChoice_val().length);

                        if (!ob_list.setValue().equals(choice_desc_1[holder.mspin.getSelectedItemPosition()])) {
                            ob_list.getValue(choice_desc_1[holder.mspin.getSelectedItemPosition()]);
                            try {
                                check_Val(ob_list);
                            } catch (Exception e) {
                                Log.e(TAG, "Check val based error " + e.toString());
                            }
                        }

                        ob_list.getValue(choice_desc_1[holder.mspin.getSelectedItemPosition()]);
                        sessionsave.save_two("general_" + position, choice_desc_1[holder.mspin.getSelectedItemPosition()]);
                        sessionsave.save_two("general_key_" + position, ob_list.setKey() + "");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            holder.spin_two.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    crop_data[position] = holder.spin_two.getSelectedItemPosition() + "";
                    String item_position = String.valueOf(holder.spin_two.getSelectedItemPosition());
                    String query = "INSERT INTO TriatsMaster (hv_code,position,ob_value) VALUES('" + check_hyb.get(1).toString() + "','" + position + "','" + item_position + "');";
                    dbGetMaster.execSQL(query);
                    Log.e("spin1", item_position);
                    try {
                        String[] choice_desc_val_1 = ob_list.setChoice_val();
                        String[] choice_desc_1 = new String[ob_list.setChoice_val().length + 1];
                        choice_desc_1[0] = "Select";
                        System.arraycopy(choice_desc_val_1, 0, choice_desc_1, 1, ob_list.setChoice_val().length);

                        if (!ob_list.setValue().equals(choice_desc_1[holder.spin_two.getSelectedItemPosition()])) {
                            ob_list.getValue(choice_desc_1[holder.spin_two.getSelectedItemPosition()]);
                            try {
                                check_Val(ob_list);
                            } catch (Exception e) {
                                Log.e(TAG, "Check val based error " + e.toString());
                            }
                        }

                        ob_list.getValue(choice_desc_1[holder.spin_two.getSelectedItemPosition()]);
                        sessionsave.save_two("general_" + position, choice_desc_1[holder.spin_two.getSelectedItemPosition()]);
                        sessionsave.save_two("general_key_" + position, ob_list.setKey() + "");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            holder.spin_three.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    crop_data[position] = holder.spin_three.getSelectedItemPosition() + "";
                    String item_position = String.valueOf(holder.spin_three.getSelectedItemPosition());
                    Log.e("spin3", item_position);
                    String query = "INSERT INTO TriatsMaster (hv_code,position,ob_value) VALUES('" + check_hyb.get(2).toString() + "','" + position + "','" + item_position + "');";
                    dbGetMaster.execSQL(query);
                    try {
                        String[] choice_desc_val_1 = ob_list.setChoice_val();
                        String[] choice_desc_1 = new String[ob_list.setChoice_val().length + 1];
                        choice_desc_1[0] = "Select";
                        System.arraycopy(choice_desc_val_1, 0, choice_desc_1, 1, ob_list.setChoice_val().length);

                        if (!ob_list.setValue().equals(choice_desc_1[holder.spin_three.getSelectedItemPosition()])) {
                            ob_list.getValue(choice_desc_1[holder.spin_three.getSelectedItemPosition()]);
                            try {
                                check_Val(ob_list);
                            } catch (Exception e) {
                                Log.e(TAG, "Check val based error " + e.toString());
                            }
                        }

                        ob_list.getValue(choice_desc_1[holder.spin_three.getSelectedItemPosition()]);
                        sessionsave.save_two("general_" + position, choice_desc_1[holder.spin_three.getSelectedItemPosition()]);
                        sessionsave.save_two("general_key_" + position, ob_list.setKey() + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            holder.spin_four.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    crop_data[position] = holder.spin_four.getSelectedItemPosition() + "";
                    String item_position = String.valueOf(holder.spin_four.getSelectedItemPosition());
                    Log.e("spin1", item_position);
                    String query = "INSERT INTO TriatsMaster (hv_code,position,ob_value) VALUES('" + check_hyb.get(3).toString() + "','" + position + "','" + item_position + "');";
                    dbGetMaster.execSQL(query);
                    try {
                        String[] choice_desc_val_1 = ob_list.setChoice_val();
                        String[] choice_desc_1 = new String[ob_list.setChoice_val().length + 1];
                        choice_desc_1[0] = "Select";
                        System.arraycopy(choice_desc_val_1, 0, choice_desc_1, 1, ob_list.setChoice_val().length);

                        if (!ob_list.setValue().equals(choice_desc_1[holder.spin_four.getSelectedItemPosition()])) {
                            ob_list.getValue(choice_desc_1[holder.spin_four.getSelectedItemPosition()]);
                            try {
                                check_Val(ob_list);
                            } catch (Exception e) {
                                Log.e(TAG, "Check val based error " + e.toString());
                            }
                        }

                        ob_list.getValue(choice_desc_1[holder.spin_four.getSelectedItemPosition()]);
                        sessionsave.save_two("general_" + position, choice_desc_1[holder.spin_four.getSelectedItemPosition()]);
                        sessionsave.save_two("general_key_" + position, ob_list.setKey() + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            holder.spin_five.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    crop_data[position] = holder.spin_five.getSelectedItemPosition() + "";
                    String item_position = String.valueOf(holder.spin_five.getSelectedItemPosition());
                    Log.e("spin1", item_position);
                    String query = "INSERT INTO TriatsMaster (hv_code,position,ob_value) VALUES('" + check_hyb.get(4).toString() + "','" + position + "','" + item_position + "');";
                    dbGetMaster.execSQL(query);
                    try {
                        String[] choice_desc_val_1 = ob_list.setChoice_val();
                        String[] choice_desc_1 = new String[ob_list.setChoice_val().length + 1];
                        choice_desc_1[0] = "Select";
                        System.arraycopy(choice_desc_val_1, 0, choice_desc_1, 1, ob_list.setChoice_val().length);

                        if (!ob_list.setValue().equals(choice_desc_1[holder.spin_five.getSelectedItemPosition()])) {
                            ob_list.getValue(choice_desc_1[holder.spin_five.getSelectedItemPosition()]);
                            try {
                                check_Val(ob_list);
                            } catch (Exception e) {
                                Log.e(TAG, "Check val based error " + e.toString());
                            }
                        }

                        ob_list.getValue(choice_desc_1[holder.spin_five.getSelectedItemPosition()]);
                        sessionsave.save_two("general_" + position, choice_desc_1[holder.spin_five.getSelectedItemPosition()]);
                        sessionsave.save_two("general_key_" + position, ob_list.setKey() + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            holder.spin_six.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    crop_data[position] = holder.spin_six.getSelectedItemPosition() + "";
                    String item_position = String.valueOf(holder.spin_six.getSelectedItemPosition());
                    Log.e("spin1", item_position);
                    String query = "INSERT INTO TriatsMaster (hv_code,position,ob_value) VALUES('" + check_hyb.get(5).toString() + "','" + position + "','" + item_position + "');";
                    dbGetMaster.execSQL(query);
                    try {
                        String[] choice_desc_val_1 = ob_list.setChoice_val();
                        String[] choice_desc_1 = new String[ob_list.setChoice_val().length + 1];
                        choice_desc_1[0] = "Select";
                        System.arraycopy(choice_desc_val_1, 0, choice_desc_1, 1, ob_list.setChoice_val().length);

                        if (!ob_list.setValue().equals(choice_desc_1[holder.spin_six.getSelectedItemPosition()])) {
                            ob_list.getValue(choice_desc_1[holder.spin_six.getSelectedItemPosition()]);
                            try {
                                check_Val(ob_list);
                            } catch (Exception e) {
                                Log.e(TAG, "Check val based error " + e.toString());
                            }
                        }

                        ob_list.getValue(choice_desc_1[holder.spin_six.getSelectedItemPosition()]);
                        sessionsave.save_two("general_" + position, choice_desc_1[holder.spin_six.getSelectedItemPosition()]);
                        sessionsave.save_two("general_key_" + position, ob_list.setKey() + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            holder.spin_seven.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    crop_data[position] = holder.spin_seven.getSelectedItemPosition() + "";
                    String item_position = String.valueOf(holder.spin_seven.getSelectedItemPosition());
                    Log.e("spin1", item_position);
                    String query = "INSERT INTO TriatsMaster (hv_code,position,ob_value) VALUES('" + check_hyb.get(6).toString() + "','" + position + "','" + item_position + "');";
                    dbGetMaster.execSQL(query);
                    try {
                        String[] choice_desc_val_1 = ob_list.setChoice_val();
                        String[] choice_desc_1 = new String[ob_list.setChoice_val().length + 1];
                        choice_desc_1[0] = "Select";
                        System.arraycopy(choice_desc_val_1, 0, choice_desc_1, 1, ob_list.setChoice_val().length);

                        if (!ob_list.setValue().equals(choice_desc_1[holder.spin_seven.getSelectedItemPosition()])) {
                            ob_list.getValue(choice_desc_1[holder.spin_seven.getSelectedItemPosition()]);
                            try {
                                check_Val(ob_list);
                            } catch (Exception e) {
                                Log.e(TAG, "Check val based error " + e.toString());
                            }
                        }

                        ob_list.getValue(choice_desc_1[holder.spin_seven.getSelectedItemPosition()]);
                        sessionsave.save_two("general_" + position, choice_desc_1[holder.spin_seven.getSelectedItemPosition()]);
                        sessionsave.save_two("general_key_" + position, ob_list.setKey() + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            holder.spin_eight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    crop_data[position] = holder.spin_eight.getSelectedItemPosition() + "";
                    String item_position = String.valueOf(holder.spin_eight.getSelectedItemPosition());
                    Log.e("spin1", item_position);
                    String query = "INSERT INTO TriatsMaster (hv_code,position,ob_value) VALUES('" + check_hyb.get(7).toString() + "','" + position + "','" + item_position + "');";
                    dbGetMaster.execSQL(query);
                    try {
                        String[] choice_desc_val_1 = ob_list.setChoice_val();
                        String[] choice_desc_1 = new String[ob_list.setChoice_val().length + 1];
                        choice_desc_1[0] = "Select";
                        System.arraycopy(choice_desc_val_1, 0, choice_desc_1, 1, ob_list.setChoice_val().length);

                        if (!ob_list.setValue().equals(choice_desc_1[holder.spin_eight.getSelectedItemPosition()])) {
                            ob_list.getValue(choice_desc_1[holder.spin_eight.getSelectedItemPosition()]);
                            try {
                                check_Val(ob_list);
                            } catch (Exception e) {
                                Log.e(TAG, "Check val based error " + e.toString());
                            }
                        }

                        ob_list.getValue(choice_desc_1[holder.spin_eight.getSelectedItemPosition()]);
                        sessionsave.save_two("general_" + position, choice_desc_1[holder.spin_eight.getSelectedItemPosition()]);
                        sessionsave.save_two("general_key_" + position, ob_list.setKey() + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            holder.spin_nine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    crop_data[position] = holder.spin_nine.getSelectedItemPosition() + "";
                    String item_position = String.valueOf(holder.spin_nine.getSelectedItemPosition());
                    String query = "INSERT INTO TriatsMaster (hv_code,position,ob_value) VALUES('" + check_hyb.get(8).toString() + "','" + position + "','" + item_position + "');";
                    dbGetMaster.execSQL(query);
                    try {
                        String[] choice_desc_val_1 = ob_list.setChoice_val();
                        String[] choice_desc_1 = new String[ob_list.setChoice_val().length + 1];
                        choice_desc_1[0] = "Select";
                        System.arraycopy(choice_desc_val_1, 0, choice_desc_1, 1, ob_list.setChoice_val().length);

                        if (!ob_list.setValue().equals(choice_desc_1[holder.spin_nine.getSelectedItemPosition()])) {
                            ob_list.getValue(choice_desc_1[holder.spin_nine.getSelectedItemPosition()]);
                            try {
                                check_Val(ob_list);
                            } catch (Exception e) {
                                Log.e(TAG, "Check val based error " + e.toString());
                            }
                        }

                        ob_list.getValue(choice_desc_1[holder.spin_nine.getSelectedItemPosition()]);
                        sessionsave.save_two("general_" + position, choice_desc_1[holder.spin_nine.getSelectedItemPosition()]);
                        sessionsave.save_two("general_key_" + position, ob_list.setKey() + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            holder.spin_ten.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    crop_data[position] = holder.spin_ten.getSelectedItemPosition() + "";
                    String item_position = String.valueOf(holder.spin_ten.getSelectedItemPosition());
                    String query = "INSERT INTO TriatsMaster (hv_code,position,ob_value) VALUES('" + check_hyb.get(9).toString() + "','" + position + "','" + item_position + "');";
                    dbGetMaster.execSQL(query);
                    try {
                        String[] choice_desc_val_1 = ob_list.setChoice_val();
                        String[] choice_desc_1 = new String[ob_list.setChoice_val().length + 1];
                        choice_desc_1[0] = "Select";
                        System.arraycopy(choice_desc_val_1, 0, choice_desc_1, 1, ob_list.setChoice_val().length);

                        if (!ob_list.setValue().equals(choice_desc_1[holder.spin_ten.getSelectedItemPosition()])) {
                            ob_list.getValue(choice_desc_1[holder.spin_ten.getSelectedItemPosition()]);
                            try {
                                check_Val(ob_list);
                            } catch (Exception e) {
                                Log.e(TAG, "Check val based error " + e.toString());
                            }
                        }

                        ob_list.getValue(choice_desc_1[holder.spin_ten.getSelectedItemPosition()]);
                        sessionsave.save_two("general_" + position, choice_desc_1[holder.spin_ten.getSelectedItemPosition()]);
                        sessionsave.save_two("general_key_" + position, ob_list.setKey() + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            holder.text_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(mCtx, pick_image_1.class);
                    myIntent.putExtra("image_name", "obt_img_"+position);
                    myIntent.putExtra("position", position+"");
                    myIntent.putExtra("from", "g_list");
                    myIntent.putExtra("key", ob_list.setKey());
                    mCtx.startActivity(myIntent);
                }
            });

            holder.text_change_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(mCtx, pick_image_1.class);
                    myIntent.putExtra("image_name", "gc_img_" + position);
                    myIntent.putExtra("position", position + "");
                    myIntent.putExtra("from", "g_list_edit");
//                    myIntent.putExtra("from", "g_list");
                    myIntent.putExtra("key", ob_list.setKey());
                    mCtx.startActivity(myIntent);
                }
            });
            holder.text_change_img_two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(mCtx, pick_image_1.class);
                    myIntent.putExtra("image_name", "gc_img_" + position);
                    myIntent.putExtra("position", position + "");
                    myIntent.putExtra("from", "g_list_edit");
//                    myIntent.putExtra("from", "g_list");
                    myIntent.putExtra("key", ob_list.setKey());
                    mCtx.startActivity(myIntent);
                }
            });
            holder.text_change_img_three.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(mCtx, pick_image_1.class);
                    myIntent.putExtra("image_name", "gc_img_" + position);
                    myIntent.putExtra("position", position + "");
                    myIntent.putExtra("from", "g_list_edit");
//                    myIntent.putExtra("from", "g_list");
                    myIntent.putExtra("key", ob_list.setKey());
                    mCtx.startActivity(myIntent);
                }
            });
            holder.text_change_img_four.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(mCtx, pick_image_1.class);
                    myIntent.putExtra("image_name", "gc_img_" + position);
                    myIntent.putExtra("position", position + "");
                    myIntent.putExtra("from", "g_list_edit");
//                    myIntent.putExtra("from", "g_list");
                    myIntent.putExtra("key", ob_list.setKey());
                    mCtx.startActivity(myIntent);
                }
            });
            holder.text_change_img_five.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(mCtx, pick_image_1.class);
                    myIntent.putExtra("image_name", "gc_img_" + position);
                    myIntent.putExtra("position", position + "");
                    myIntent.putExtra("from", "g_list_edit");
//                    myIntent.putExtra("from", "g_list");
                    myIntent.putExtra("key", ob_list.setKey());
                    mCtx.startActivity(myIntent);
                }
            });
            holder.text_change_img_six.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(mCtx, pick_image_1.class);
                    myIntent.putExtra("image_name", "gc_img_" + position);
                    myIntent.putExtra("position", position + "");
                    myIntent.putExtra("from", "g_list_edit");
//                    myIntent.putExtra("from", "g_list");
                    myIntent.putExtra("key", ob_list.setKey());
                    mCtx.startActivity(myIntent);
                }
            });
            holder.text_change_img_seven.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(mCtx, pick_image_1.class);
                    myIntent.putExtra("image_name", "gc_img_" + position);
                    myIntent.putExtra("position", position + "");
                    myIntent.putExtra("from", "g_list_edit");
//                    myIntent.putExtra("from", "g_list");
                    myIntent.putExtra("key", ob_list.setKey());
                    mCtx.startActivity(myIntent);
                }
            });
            holder.text_change_img_eight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(mCtx, pick_image_1.class);
                    myIntent.putExtra("image_name", "gc_img_" + position);
                    myIntent.putExtra("position", position + "");
                    myIntent.putExtra("from", "g_list_edit");
//                    myIntent.putExtra("from", "g_list");
                    myIntent.putExtra("key", ob_list.setKey());
                    mCtx.startActivity(myIntent);
                }
            });
            holder.text_change_img_nine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(mCtx, pick_image_1.class);
                    myIntent.putExtra("image_name", "gc_img_" + position);
                    myIntent.putExtra("position", position + "");
                    myIntent.putExtra("from", "g_list_edit");
//                    myIntent.putExtra("from", "g_list");
                    myIntent.putExtra("key", ob_list.setKey());
                    mCtx.startActivity(myIntent);
                }
            });
            holder.text_change_img_ten.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(mCtx, pick_image_1.class);
                    myIntent.putExtra("image_name", "gc_img_" + position);
                    myIntent.putExtra("position", position + "");
                    myIntent.putExtra("from", "g_list_edit");
//                    myIntent.putExtra("from", "g_list");
                    myIntent.putExtra("key", ob_list.setKey());
                    mCtx.startActivity(myIntent);
                }
            });
            try {
                if (ob_list.setValKey() != null) {
                    if (ob_list.setValCheck() == null) {
                        holder.text_mtext.setVisibility(View.GONE);
                        holder.text_medit.setVisibility(View.GONE);
                        holder.edit_multi.setVisibility(View.GONE);
                        holder.r_spin.setVisibility(View.GONE);
                        holder.r_date.setVisibility(View.GONE);
                        holder.r_check.setVisibility(View.GONE);
                        holder.r_img.setVisibility(View.GONE);

                        holder.r_video.setVisibility(View.GONE);
                        holder.r_video_two.setVisibility(View.GONE);
                        holder.r_video_three.setVisibility(View.GONE);
                        holder.r_video_four.setVisibility(View.GONE);
                        holder.r_video_five.setVisibility(View.GONE);
                        holder.r_video_seven.setVisibility(View.GONE);
                        holder.r_video_six.setVisibility(View.GONE);
                        holder.r_video_eight.setVisibility(View.GONE);
                        holder.r_video_nine.setVisibility(View.GONE);
                        holder.r_video_ten.setVisibility(View.GONE);

                        //two
                        holder.rspin_two.setVisibility(View.GONE);
                    }else {
                        holder.text_mtext.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.text_mtext.setVisibility(View.VISIBLE);
                }
//                    check_Val(ob_lists.get(position) ,holder ,position);
            }catch (Exception e){
                Log.e(TAG ,"Error for val "+e.toString());
            }
        } catch (Exception e) {
            Log.e(TAG, "error " + e.toString());
        }

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

    private void gps_function() {
        final LocationManager manager = (LocationManager) Objects.requireNonNull(mCtx).getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
//            displayLocationSettingsRequest(this);
        }
        else {
            gps = new GPSTracker(mCtx);
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
        Geocoder geocoder = new Geocoder(mCtx, Locale.getDefault());
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

    private void check_Val(final ob_list ob_list) {
        for (int m = 0 ; m < ob_lists.size() ; m++){
            if (ob_list.setValue().equals(ob_lists.get(m).setValValue())){
                if (ob_list.setName().equals(ob_lists.get(m).setValLabel())){
                    ob_lists.get(m).getValCheck("1");
                    notifyDataSetChanged();
                }
            }
        }
    }
    private void check_Val2(final ob_list ob_list) {
        for (int m = 0 ; m < ob_lists.size() ; m++){
            if (ob_list.setValue1().equals(ob_lists.get(m).setValValue())){
                if (ob_list.setName().equals(ob_lists.get(m).setValLabel())){
                    ob_lists.get(m).getValCheck("2");
                    notifyDataSetChanged();
                }
            }
        }
    }

    private boolean check_save() {
        boolean res = true;
        ArrayList<Boolean> res_arr = new ArrayList<>();
        for (int i = 0 ; i< crop_data.length ;i++){
            if (ob_lists.get(i).setReq().equals("1")){
                if (!ob_lists.get(i).setValue().trim().equals("") && !ob_lists.get(i).setValue().trim().equals("Select")){
                    if (ob_lists.get(i).setValKey() == null) {
                        res_arr.add(true);
                    }
                    try {
                        if (ob_lists.get(i).setValCheck().equals("1")){
                            res_arr.add(true);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    if (ob_lists.get(i).setValKey() == null) {
                        res_arr.add(false);
                    }
                    try {
                        if (ob_lists.get(i).setValCheck().equals("1")) {
                            res_arr.add(false);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        for (int j = 0 ; j < res_arr.size() ; j++){
            if (!res_arr.get(j)){
                res = false;
            }
        }
        return res;
    }

    private void get_data() {
        for (int i = 0 ; i < crop_data.length ; i++){
            ob_lists.get(i).getValue(sessionsave.get_two("general_"+i));
        }
    }

    private void save_general() {
        for (int i = 0 ; i< crop_data.length ;i++){
            sessionsave.save_two("general_"+i ,ob_lists.get(i).setValue());
            if (ob_lists.get(i).setType().equals("image")){
                Log.e(TAG ,"skip saving");
            }else {
                sessionsave.save_two("general_key_" + i, ob_lists.get(i).setKey());
            }
            Log.e("values__generala",sessionsave.get_two("general_key_" + i));
        }
        sessionsave.save_two("general_len" ,crop_data.length+"");

        saving_observation_data();
        dialog.setMessage("Saving this observation data \nPlease wait .....");
        dialog.show();
    }


    private void saving_observation_data() {

        if (alert.isNetworkAvailable()) {

            Map<String, String> params = new HashMap<>();
            ArrayList<String> img_names = new ArrayList<>();
            ArrayList<String> images = new ArrayList<>();

            params.put("Company", sessionsave.get_company());
            params.put("pdRegNum", pd_number);
            params.put("CropCode",crop_code);
            params.put("CreatedBy", sessionsave.get_emp_code());
            params.put("pdStatus", "2");
            params.put("GPSLocation", strAdd.trim());
            params.put("menuId", "2");
            if (pd_status.equals("0")) {
                params.put("obid", "");
            } else {
                params.put("obid", sessionsave.get_obid());
            }
            final File Oroot = Environment.getExternalStorageDirectory();
            final String f_path = Oroot.getPath() + "/Android/RnDObservation/Images/";
            try {
                int general_len = Integer.parseInt(sessionsave.get_two("general_len"));
//                params.put("menuId", 2 + "");
                for (int i = 0; i < general_len; i++) {
                    if (sessionsave.get_two("general_key_" + i).contains("img")) {
                        int len = Integer.parseInt(sessionsave.get_image("general_len"));
                        for (int mas = 1; mas < (len + 1); mas++) {
                            String[] q = sessionsave.get_image("general_" + mas).split("/");
                            int idx = q.length - 1;
                            images.add(f_path + q[idx]);
                            img_names.add(sessionsave.get_one("general_key_" + i) + "_" + mas);
//                                nameValuePairs.add(new BasicNameValuePair(sessionsave.get_one("general_key_" + i) + "_" + mas, sessionsave.get_image("general_" + mas)));
                        }
                    } else {
                        params.put(sessionsave.get_two("general_key_" + i), sessionsave.get_two("general_" + i));
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error_usp" + e.toString());
            }

            String result = multipartRequest(Api.obd_insert, params, images, img_names, "image/*");
            Log.e(TAG, "res 0 " + result);
            Log.e("usp", String.valueOf(params));
        }else{
            save_data_db();
        }
    }

    private void save_data_db() {

        try {
            sb_general = new StringBuilder();
            int general_len = Integer.parseInt(sessionsave.get_two("general_len"));
            for (int i = 0; i < general_len; i++) {
                if (sessionsave.get_two("general_key_" + i).contains("img")) {
                    int len = Integer.parseInt(sessionsave.get_image("general_len"));
                    for (int mas = 1; mas < (len + 1); mas++) {
                        sb_general.append(sessionsave.get_two("general_key_" + i) + "_" + mas + "=>" + sessionsave.get_image("general_" + mas));
                        sb_general.append("|");
                    }
                } else {
                    sb_general.append(sessionsave.get_two("general_key_" + i) + "=>" + sessionsave.get_two("general_" + i));
                    sb_general.append("|");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error " + e.toString());
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date)); //2016-11-16 12:08:43
        String createdDateTime = dateFormat.format(date);
        String query = "INSERT INTO ObservationData(CropCode,CropName,Company,BreederCode,HVCode,Variety,Stage,State,City,CreatedBy,Master_data,General_data,Observe_data,usp_data,CreatedDateTime,Location,grower_code,pd_register_num,pd_status) VALUES('" + crop_code + "','" + sessionsave.get_crop_name() + "','" + sessionsave.get_company() + "','" + sessionsave.get_breeder_code() + "','" + sessionsave.get_hv_code() + "','" + sessionsave.get_variety() + "','" + "observe" + "','" + sessionsave.get_states_code() + "','" + sessionsave.get_city() + "','" + sessionsave.get_emp_code() + "','" + "sb_master" + "','" + sb_general + "','" + "sb_observe" + "','" + "sb_conclusion" + "','" + createdDateTime + "','" + strAdd +  "','" + grower_code + "','" + pd_number + "','" + "2" + "');";
        dbGetMaster.execSQL(query);
        if (dialog != null) {
            dialog.cancel();
        }
        Toast.makeText(mCtx, "Observation Added Successfully", Toast.LENGTH_SHORT).show();
        sessionsave.session_clear();
        Intent myIntent = new Intent(mCtx, ScreenActivity.class);
        mCtx.startActivity(myIntent);
        ((Activity)mCtx).finish();

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
                            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + file_names.get(i) + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
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
                        Toast.makeText(mCtx, "Observation Added Successfully", Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(mCtx, ScreenActivity.class);
                        mCtx.startActivity(myIntent);
                        ((Activity)mCtx).finish();
                    }else{
                        Toast.makeText(mCtx, "Error in uploading", Toast.LENGTH_SHORT).show();
                    }
//                    if (!result.equals("failed")) {
//                        if (save_no == 1) {
//                            sessionsave.save_obid(myJSON.trim());
//                        }
//                        save_no++;
//                        if (no_desc.equals("1")) {
//                            if (save_no < 4) {
//                                save_data();
//                            } else {
//                                if (dialog != null) {
//                                    dialog.cancel();
//                                }
//                                Toast.makeText(getActivity(), "Observation Added Successfully", Toast.LENGTH_SHORT).show();
//                                sessionsave.session_clear();
//                                Objects.requireNonNull(getActivity()).finish();
//                            }
//                        } else {
//                            if (save_no < 5) {
//                                save_data();
//                            } else {
//                                if (dialog != null) {
//                                    dialog.cancel();
//                                }
//                                Toast.makeText(getActivity(), "Observation Added Successfully", Toast.LENGTH_SHORT).show();
//                                sessionsave.session_clear();
//                                Objects.requireNonNull(getActivity()).finish();
//                            }
//                        }
//                    } else {
//                        Toast.makeText(gps, "Error on record inserting", Toast.LENGTH_SHORT).show();
//                    }
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
    public int getItemCount() {
        return ob_lists.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView text_mtext  ,text_vid ,
                text_one,text_two,text_three,text_four,text_five,text_six, text_seven, text_eight,text_nine,text_ten,
                text_click_vid_two,text2,
                text_mdate,date_two,date_three,date_four,date_five,date_six,date_seven,date_eight,date_nine,date_ten,
                text_img, text_click_img_two,text_click_img_three,text_click_img_four,text_click_img_five,text_click_img_six,
                text_click_img_seven, text_click_img_eight,text_click_img_nine,text_click_img_ten,
                text_change_img,text_change_img_two,text_change_img_three,text_change_img_four,text_change_img_five,text_change_img_six,
                text_change_img_seven,text_change_img_eight,text_change_img_nine,text_change_img_ten;

        RelativeLayout  r_date ,r_check  ,
                r_spin,rspin_two,rspin_three, rspin_four, rspin_five,rspin_six,rspin_seven,rspin_eight,rspin_nine,rspin_ten,
                r_check_two,r_check_three,r_check_four,r_check_five,r_check_six,r_check_seven,r_check_eight,r_check_nine,r_check_ten,
                rdate_two,rdate_three,rdate_four,rdate_five,rdate_six,rdate_seven,rdate_eight,rdate_nine,rdate_ten,
                r_img,r_image_two,r_image_three,r_image_four,r_image_five,r_image_six,r_image_seven,r_image_eight,r_image_nine,r_image_ten,
                r_video,r_video_two,r_video_three,r_video_four,r_video_five,r_video_six,r_video_seven,r_video_eight,r_video_nine,r_video_ten;
        EditText text_medit ,edit_two,edit_three,edit_four,edit_five,edit_six,edit_seven,edit_eight,edit_nine,edit_ten,
                 edit_multi,edit_multi_two,edit_multi_three,edit_multi_four,edit_multi_five,edit_multi_six,edit_multi_seven,edit_multi_eight,
                edit_multi_nine,edit_multi_ten;
        Spinner mspin,spin_two,spin_three,spin_four,spin_five,spin_six,spin_seven,spin_eight,spin_nine,spin_ten;
        Button next ,btn_previous;
        RecyclerView list_check,list_check_two,list_check_three,list_check_four,list_check_five,list_check_six,list_check_seven,
                      list_check_eight,list_check_nine,list_check_ten;
        int pos;
        ImageView img_view,img_photo_two,img_photo_three,img_photo_four,img_photo_five,img_photo_six,img_photo_seven,img_photo_eight,
                img_photo_nine,img_photo_ten;;
        VideoView video_view,videoPreview_two,videoPreview_three,videoPreview_four,videoPreview_five,videoPreview_six,videoPreview_seven,
                videoPreview_eight,videoPreview_nine,videoPreview_ten;
        LinearLayout linear1,linear2, linear3, linear4, linear5,linear6,linear7,linear8,linear9,linear10;

        ViewHolder(final View itemView) {
            super(itemView);

            //one
            text_mtext = itemView.findViewById(R.id.text1);
            next = itemView.findViewById(R.id.next);
            btn_previous = itemView.findViewById(R.id.btn_previous);
            btn_previous.setVisibility(View.GONE);



            text_vid = itemView.findViewById(R.id.text_click_vid);
            //linearlayout

            linear1 = itemView.findViewById(R.id.linear1);
            linear2 = itemView.findViewById(R.id.linear2);
            linear3 = itemView.findViewById(R.id.linear3);
            linear4 = itemView.findViewById(R.id.linear4);
            linear5 = itemView.findViewById(R.id.linear5);
            linear6 = itemView.findViewById(R.id.linear6);
            linear7 = itemView.findViewById(R.id.linear7);
            linear8 = itemView.findViewById(R.id.linear8);
            linear9 = itemView.findViewById(R.id.linear9);
            linear10 = itemView.findViewById(R.id.linear10);

            //video
            video_view = itemView.findViewById(R.id.videoPreview);
            videoPreview_two = itemView.findViewById(R.id.videoPreview_two);
            videoPreview_three = itemView.findViewById(R.id.videoPreview_three);
            videoPreview_four = itemView.findViewById(R.id.videoPreview_four);
            videoPreview_five = itemView.findViewById(R.id.videoPreview_five);
            videoPreview_six = itemView.findViewById(R.id.videoPreview_six);
            videoPreview_seven = itemView.findViewById(R.id.videoPreview_seven);
            videoPreview_eight = itemView.findViewById(R.id.videoPreview_eight);
            videoPreview_nine = itemView.findViewById(R.id.videoPreview_nine);
            videoPreview_ten = itemView.findViewById(R.id.videoPreview_ten);

            //text change textview
            text_change_img = itemView.findViewById(R.id.text_change_img);
            text_change_img_two = itemView.findViewById(R.id.text_change_img_two);
            text_change_img_three = itemView.findViewById(R.id.text_change_img_three);
            text_change_img_four = itemView.findViewById(R.id.text_change_img_four);
            text_change_img_five = itemView.findViewById(R.id.text_change_img_five);
            text_change_img_six = itemView.findViewById(R.id.text_change_img_six);
            text_change_img_seven = itemView.findViewById(R.id.text_change_img_seven);
            text_change_img_eight = itemView.findViewById(R.id.text_change_img_eight);
            text_change_img_nine = itemView.findViewById(R.id.text_change_img_nine);
            text_change_img_ten = itemView.findViewById(R.id.text_change_img_ten);

            //text image view

            text_img = itemView.findViewById(R.id.text_click_img);
            text_click_img_two = itemView.findViewById(R.id.text_click_img_two);
            text_click_img_three = itemView.findViewById(R.id.text_click_img_three);
            text_click_img_four = itemView.findViewById(R.id.text_click_img_four);
            text_click_img_five = itemView.findViewById(R.id.text_click_img_five);
            text_click_img_six = itemView.findViewById(R.id.text_click_img_six);
            text_click_img_seven = itemView.findViewById(R.id.text_click_img_seven);
            text_click_img_eight = itemView.findViewById(R.id.text_click_img_eight);
            text_click_img_nine = itemView.findViewById(R.id.text_click_img_nine);
            text_click_img_ten = itemView.findViewById(R.id.text_click_img_ten);

            //imave view
            img_view = itemView.findViewById(R.id.img_photo);
            img_photo_two = itemView.findViewById(R.id.img_photo_two);
            img_photo_three = itemView.findViewById(R.id.img_photo_three);
            img_photo_four = itemView.findViewById(R.id.img_photo_four);
            img_photo_five = itemView.findViewById(R.id.img_photo_five);
            img_photo_six = itemView.findViewById(R.id.img_photo_six);
            img_photo_seven = itemView.findViewById(R.id.img_photo_seven);
            img_photo_eight = itemView.findViewById(R.id.img_photo_eight);
            img_photo_nine = itemView.findViewById(R.id.img_photo_nine);
            img_photo_ten = itemView.findViewById(R.id.img_photo_ten);

            //recycler view
            list_check = itemView.findViewById(R.id.list_check);
            list_check.setHasFixedSize(true);
            list_check.setLayoutManager(new LinearLayoutManager(mCtx));

            list_check_two = itemView.findViewById(R.id.list_check_two);
            list_check_two.setHasFixedSize(true);
            list_check_two.setLayoutManager(new LinearLayoutManager(mCtx));

            list_check_three = itemView.findViewById(R.id.list_check_three);
            list_check_three.setHasFixedSize(true);
            list_check_three.setLayoutManager(new LinearLayoutManager(mCtx));

            list_check_four = itemView.findViewById(R.id.list_check_four);
            list_check_four.setHasFixedSize(true);
            list_check_four.setLayoutManager(new LinearLayoutManager(mCtx));

            list_check_five = itemView.findViewById(R.id.list_check_five);
            list_check_five.setHasFixedSize(true);
            list_check_five.setLayoutManager(new LinearLayoutManager(mCtx));

            list_check_six = itemView.findViewById(R.id.list_check_six);
            list_check_six.setHasFixedSize(true);
            list_check_six.setLayoutManager(new LinearLayoutManager(mCtx));

            list_check_seven = itemView.findViewById(R.id.list_check_seven);
            list_check_seven.setHasFixedSize(true);
            list_check_seven.setLayoutManager(new LinearLayoutManager(mCtx));

            list_check_eight = itemView.findViewById(R.id.list_check_eight);
            list_check_eight.setHasFixedSize(true);
            list_check_eight.setLayoutManager(new LinearLayoutManager(mCtx));

            list_check_nine = itemView.findViewById(R.id.list_check_nine);
            list_check_nine.setHasFixedSize(true);
            list_check_nine.setLayoutManager(new LinearLayoutManager(mCtx));

            list_check_ten = itemView.findViewById(R.id.list_check_ten);
            list_check_ten.setHasFixedSize(true);
            list_check_ten.setLayoutManager(new LinearLayoutManager(mCtx));


            //textview
            text_one = itemView.findViewById(R.id.text_one);
            text_two = itemView.findViewById(R.id.text_two);
            text2 = itemView.findViewById(R.id.text2);
            text_three = itemView.findViewById(R.id.text_three);
            text_four = itemView.findViewById(R.id.text_four);
            text_five  = itemView.findViewById(R.id.text_five);
            text_six = itemView.findViewById(R.id.text_six);
            text_seven = itemView.findViewById(R.id.text_seven);
            text_eight = itemView.findViewById(R.id.text_eight);
            text_nine  = itemView.findViewById(R.id.text_nine);
            text_ten = itemView.findViewById(R.id.text_ten);

            //edit_text
            text_medit = itemView.findViewById(R.id.edit);
            edit_two = itemView.findViewById(R.id.edit_two);
            edit_three = itemView.findViewById(R.id.edit_three);
            edit_four = itemView.findViewById(R.id.edit_four);
            edit_five = itemView.findViewById(R.id.edit_five);
            edit_six = itemView.findViewById(R.id.edit_six);
            edit_seven = itemView.findViewById(R.id.edit_seven);
            edit_eight = itemView.findViewById(R.id.edit_eight);
            edit_nine = itemView.findViewById(R.id.edit_nine);
            edit_ten = itemView.findViewById(R.id.edit_ten);

            //Relative spinner
            r_spin = itemView.findViewById((R.id.rspin));
            rspin_two = itemView.findViewById(R.id.rspin_two);
            rspin_three = itemView.findViewById(R.id.rspin_three);
            rspin_four = itemView.findViewById(R.id.rspin_four);
            rspin_five = itemView.findViewById(R.id.rspin_five);
            rspin_six = itemView.findViewById(R.id.rspin_six);
            rspin_seven = itemView.findViewById(R.id.rspin_seven);
            rspin_eight = itemView.findViewById(R.id.rspin_eight);
            rspin_nine = itemView.findViewById(R.id.rspin_nine);
            rspin_ten = itemView.findViewById(R.id.rspin_ten);

            //spinner
            mspin = itemView.findViewById(R.id.spin);
            spin_two = itemView.findViewById(R.id.spin_two);
            spin_three = itemView.findViewById(R.id.spin_three);
            spin_four = itemView.findViewById(R.id.spin_four);
            spin_five = itemView.findViewById(R.id.spin_five);
            spin_six = itemView.findViewById(R.id.spin_six);
            spin_seven= itemView.findViewById(R.id.spin_seven);
            spin_eight = itemView.findViewById(R.id.spin_eight);
            spin_nine = itemView.findViewById(R.id.spin_nine);
            spin_ten = itemView.findViewById(R.id.spin_ten);

            //relatve check-------------
            r_check = itemView.findViewById((R.id.r_check));
            r_check_two = itemView.findViewById(R.id.r_check_two);
            r_check_three = itemView.findViewById(R.id.r_check_three);
            r_check_four = itemView.findViewById(R.id.r_check_four);
            r_check_five = itemView.findViewById(R.id.r_check_five);
            r_check_six = itemView.findViewById(R.id.r_check_six);
            r_check_seven = itemView.findViewById(R.id.r_check_seven);
            r_check_eight = itemView.findViewById(R.id.r_check_eight);
            r_check_nine = itemView.findViewById(R.id.r_check_nine);
            r_check_ten = itemView.findViewById(R.id.r_check_ten);

            //relate date-------------
            r_date = itemView.findViewById((R.id.rdate));
            rdate_two = itemView.findViewById(R.id.rdate_two);
            rdate_three = itemView.findViewById(R.id.rdate_three);
            rdate_four = itemView.findViewById(R.id.rdate_four);
            rdate_five = itemView.findViewById(R.id.rdate_five);
            rdate_six = itemView.findViewById(R.id.rdate_six);
            rdate_seven = itemView.findViewById(R.id.rdate_seven);
            rdate_eight = itemView.findViewById(R.id.rdate_eight);
            rdate_nine = itemView.findViewById(R.id.rdate_nine);
            rdate_ten = itemView.findViewById(R.id.rdate_ten);

            //date-----
            text_mdate = itemView.findViewById(R.id.date);
            date_two = itemView.findViewById(R.id.date_two);
            date_three = itemView.findViewById(R.id.date_three);
            date_four = itemView.findViewById(R.id.date_four);
            date_five = itemView.findViewById(R.id.date_five);
            date_six = itemView.findViewById(R.id.date_six);
            date_seven = itemView.findViewById(R.id.date_seven);
            date_eight = itemView.findViewById(R.id.date_eight);
            date_nine = itemView.findViewById(R.id.date_nine);
            date_ten = itemView.findViewById(R.id.date_ten);

            //relative image

            r_img = itemView.findViewById((R.id.r_image));
            r_image_two = itemView.findViewById((R.id.r_image_two));
            r_image_three = itemView.findViewById((R.id.r_image_three));
            r_image_four = itemView.findViewById((R.id.r_image_four));
            r_image_five = itemView.findViewById((R.id.r_image_five));
            r_image_six = itemView.findViewById((R.id.r_image_six));
            r_image_seven = itemView.findViewById((R.id.r_image_seven));
            r_image_eight = itemView.findViewById((R.id.r_image_eight));
            r_image_nine = itemView.findViewById((R.id.r_image_nine));
            r_image_ten = itemView.findViewById((R.id.r_image_ten));

            //relative video

            r_video = itemView.findViewById((R.id.r_video));
            r_video_two = itemView.findViewById((R.id.r_video_two));
            r_video_three = itemView.findViewById((R.id.r_video_three));
            r_video_four = itemView.findViewById((R.id.r_video_four));
            r_video_five = itemView.findViewById((R.id.r_video_five));
            r_video_six = itemView.findViewById((R.id.r_video_six));
            r_video_seven = itemView.findViewById((R.id.r_video_seven));
            r_video_eight = itemView.findViewById((R.id.r_video_eight));
            r_video_nine = itemView.findViewById((R.id.r_video_nine));
            r_video_ten = itemView.findViewById((R.id.r_video_ten));

            //edit text multi
            edit_multi = itemView.findViewById(R.id.edit_multi);
            edit_multi_two = itemView.findViewById(R.id.edit_multi_two);
            edit_multi_three = itemView.findViewById(R.id.edit_multi_three);
            edit_multi_four = itemView.findViewById(R.id.edit_multi_four);
            edit_multi_five = itemView.findViewById(R.id.edit_multi_five);
            edit_multi_six = itemView.findViewById(R.id.edit_multi_six);
            edit_multi_seven = itemView.findViewById(R.id.edit_multi_seven);
            edit_multi_eight = itemView.findViewById(R.id.edit_multi_eight);
            edit_multi_nine = itemView.findViewById(R.id.edit_multi_nine);
            edit_multi_ten = itemView.findViewById(R.id.edit_multi_ten);





            rspin_two = itemView.findViewById(R.id.rspin_two);


            text_click_vid_two = itemView.findViewById(R.id.text_click_vid_two);



            img_photo_two = itemView.findViewById(R.id.img_photo_two);

            videoPreview_two = itemView.findViewById(R.id.videoPreview_two);



        }
    }

    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; ++i)
            {
                if (!Pattern.compile("[1234567890.]").matcher(String.valueOf(source.charAt(i))).matches())
                {
                    return "";
                }
            }

            return null;
        }
    };

}
