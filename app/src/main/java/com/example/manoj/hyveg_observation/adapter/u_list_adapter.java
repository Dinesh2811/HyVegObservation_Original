package com.example.manoj.hyveg_observation.adapter;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.VideoView;

import com.example.manoj.hyveg_observation.Others.InputFilterMinMax;
import com.example.manoj.hyveg_observation.R;
import com.example.manoj.hyveg_observation.activity.Sessionsave;
import com.example.manoj.hyveg_observation.activity.pick_image_1;
import com.example.manoj.hyveg_observation.list.ob_list;
import com.example.manoj.hyveg_observation.services.alertDialog;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import static com.android.volley.VolleyLog.TAG;

public class u_list_adapter extends RecyclerView.Adapter<u_list_adapter.ViewHolder> {

    private Context mCtx;
    private List<ob_list> ob_lists;
    private String mActivity,date,grower_code,pd_number,pd_status,myJSON = "";
    private DatePickerDialog datePickerDialog;
    private Sessionsave sessionsave;
    private DecimalFormat dcf = new DecimalFormat("00");
    private String[] crop_data;
    private ProgressDialog dialog;
    alertDialog alert;
    private File Oroot = Environment.getExternalStorageDirectory(), GetMasterPath = null;
    private SQLiteDatabase dbGetMaster;
    private Cursor GetWareHouseDetails;
    StringBuilder sb_conclusion;

    public u_list_adapter(Context mCtx, List<ob_list> ob_lists, String mActivity, String grower_code, String pd_number,String pd_status) {
        super();
        this.ob_lists = ob_lists;
        this.mCtx = mCtx;
        this.mActivity = mActivity;
        this.grower_code =grower_code;
        this.pd_number = pd_number;
        this.pd_status = pd_status;

        sessionsave = new Sessionsave(mCtx);
    }

    @NonNull
    @Override
    public u_list_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_gc_cn, parent, false);
        u_list_adapter.ViewHolder viewHolder = new u_list_adapter.ViewHolder(v);

        crop_data = new String[ob_lists.size()];
        dialog = new ProgressDialog(mCtx);
        alert = new alertDialog(mCtx);
        if (Oroot.canWrite()) {
            File dir = new File(Oroot.getAbsolutePath() + "/Android/Observation");
            dir.mkdirs();

            GetMasterPath = new File(dir, "GetMasterDB.db");

        }
        dbGetMaster = mCtx.openOrCreateDatabase(GetMasterPath.getPath(), Context.MODE_PRIVATE, null);

        sessionsave.save_four("usp_len" ,crop_data.length+"");
        return viewHolder;

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final u_list_adapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final ob_list ob_list = ob_lists.get(position);

        holder.setIsRecyclable(false);
        holder.pos = position;

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
            if (ob_list.setType().equals("number")){
                if (ob_list.set_decimal().equals("1")) {
                    holder.text_medit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    if (!ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
                        holder.text_medit.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax(ob_list.set_min(), ob_list.set_max(), "float")});
                    } else if (!ob_list.set_min().equals("null") && ob_list.set_max().equals("null")) {
                        holder.text_medit.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax(ob_list.set_min(), "9999", "float")});
                    } else if (ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
                        holder.text_medit.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax("0", ob_list.set_max(), "float")});
                    } else {
                        holder.text_medit.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6)});
                    }
                } else {
                    holder.text_medit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    if (!ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
                        holder.text_medit.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax(ob_list.set_min(), ob_list.set_max(), "int")});
                    } else if (!ob_list.set_min().equals("null") && ob_list.set_max().equals("null")) {
                        holder.text_medit.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax(ob_list.set_min(), "9999", "int")});
                    } else if (ob_list.set_min().equals("null") && !ob_list.set_max().equals("null")) {
                        holder.text_medit.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax("0", ob_list.set_max(), "int")});
                    } else {
                        holder.text_medit.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4)});
                    }
                }
            }else {
                holder.text_medit.setInputType(InputType.TYPE_CLASS_TEXT);
            }
            holder.text_medit.setText(ob_list.setValue());
            holder.edit_multi.setText(ob_list.setValue());
            holder.text_mdate.setText(ob_list.setValue());
            if (!ob_list.setValue().equals("")){
                holder.img_view.setVisibility(View.VISIBLE);
//                holder.img_view.setImageBitmap(StringToBitMap(ob_list.setValue()));
                holder.text_img.setVisibility(View.VISIBLE);
                holder.text_img.setText(sessionsave.get_image("usp_len")+" images added");
                holder.text_change_img.setVisibility(View.VISIBLE);
                holder.text_img.setEnabled(false);
            }else {
                holder.text_change_img.setVisibility(View.GONE);
                holder.text_img.setEnabled(true);
            }

            switch (ob_list.setType()) {
                case "select":
                    holder.text_medit.setVisibility(View.GONE);
                    holder.r_spin.setVisibility(View.VISIBLE);
                    String[] choice_desc_val = ob_list.setChoice_desc();
                    String[] choice_desc = new String[ob_list.setChoice_desc().length + 1];
                    choice_desc[0] = "Select";
                    System.arraycopy(choice_desc_val, 0, choice_desc, 1, ob_list.setChoice_desc().length);
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this.mCtx, android.R.layout.simple_spinner_item, choice_desc);

                    String[] choice_desc_val_2 = ob_list.setChoice_val();
                    String[] choice_desc_2 = new String[ob_list.setChoice_val().length + 1];
                    choice_desc_2[0] = "Select";
                    System.arraycopy(choice_desc_val_2, 0, choice_desc_2, 1, ob_list.setChoice_val().length);

                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    holder.mspin.setAdapter(dataAdapter);

                    try {
                        for (int  i = 0 ; i < choice_desc_2.length ; i++){
                            if (choice_desc_2[i].equals(ob_list.setValue())){
                                holder.mspin.setSelection(i);
                                sessionsave.save_four("usp_" + position, choice_desc_2[i]);
                            }
                        }
                        sessionsave.save_four("usp_key_" + position, ob_list.setKey() + "");
//                        try {
//                            check_Val(ob_list);
//                        }catch (Exception e){
//                            Log.e(TAG ,"Check val based error "+e.toString());
//                        }
                    } catch (Exception e) {
                        holder.mspin.setSelection(0);
                    }

                    break;

                case "checkbox":
                    holder.text_medit.setVisibility(View.GONE);
                    holder.r_check.setVisibility(View.VISIBLE);
//                    holder.r_date.setVisibility(View.GONE);
//                    holder.r_spin.setVisibility(View.VISIBLE);

//                    String[] choice_desc_val_1 = ob_list.setChoice_desc();
//                    String[] choice_desc_1 = new String[ob_list.setChoice_desc().length + 1];
//                    choice_desc_1[0] = "Select";
//                    System.arraycopy(choice_desc_val_1, 0, choice_desc_1, 1, ob_list.setChoice_desc().length);
//                    ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this.mCtx, android.R.layout.simple_spinner_item, choice_desc_1);

//                    String[] choice_desc_val_3 = ob_list.setChoice_val();
//                    String[] choice_desc_3 = new String[ob_list.setChoice_val().length + 1];
//                    choice_desc_3[0] = "Select";
//                    System.arraycopy(choice_desc_val_3, 0, choice_desc_3, 1, ob_list.setChoice_val().length);

//                    String multi_sel = ob_list.set_multi();
                    RecyclerView.Adapter adapter = new check_adapter(mCtx,ob_list,position,"u_list");
                    holder.list_check.setAdapter(adapter);
//                    try {
//                        for (int  i = 0 ; i < choice_desc_3.length ; i++){
//                            if (choice_desc_3[i].equals(ob_list.setValue())){
//                                holder.mspin.setSelection(i);
//                                sessionsave.save_four("usp_" + position, choice_desc_3[i]);
//                            }
//                        }
//                        sessionsave.save_four("usp_key_" + position, ob_list.setKey() + "");
//                    } catch (Exception e) {
//                        holder.mspin.setSelection(0);
//                    }
                    break;

                case "date":
                    holder.r_date.setVisibility((View.VISIBLE));
                    holder.text_medit.setVisibility(View.GONE);
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
                                            holder.text_mdate.setText(dcf.format(dayOfMonth )+ "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            crop_data[position] = dcf.format(dayOfMonth )+ "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year);
                                            ob_list.getValue(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            sessionsave.save_four("usp_" + position, dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                            sessionsave.save_four("usp_key_" + position, ob_list.setKey());
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
                    holder.text_medit.setVisibility(View.GONE);
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
                                                            sessionsave.save_four("usp_" + position, date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
                                                            sessionsave.save_four("usp_key_" + position, ob_list.setKey());
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
                    break;

                case "image":
                    holder.text_medit.setVisibility(View.GONE);
                    holder.r_img.setVisibility(View.VISIBLE);
                    break;

                case "video":
                    holder.text_medit.setVisibility(View.GONE);
                    holder.r_video.setVisibility(View.VISIBLE);
                    break;

                case "textarea":
                    holder.text_medit.setVisibility(View.GONE);
                    holder.edit_multi.setVisibility(View.VISIBLE);
                    break;

                default:
                    holder.r_date.setVisibility((View.GONE));
                    holder.text_medit.setVisibility(View.VISIBLE);
                    holder.r_spin.setVisibility(View.GONE);
                    break;
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
                    ob_list.getValue(arg0.toString());
                    sessionsave.save_four("usp_" + position, arg0.toString());
                    sessionsave.save_four("usp_key_" + position, ob_list.setKey());
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
                    sessionsave.save_four("usp_" + position, arg0.toString());
                    sessionsave.save_four("usp_key_" + position, ob_list.setKey());
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
                    sessionsave.save_four("usp_" + position, choice_desc_1[holder.mspin.getSelectedItemPosition()]);
                    sessionsave.save_four("usp_key_" + position, ob_list.setKey() + "");
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            holder.text_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(mCtx, pick_image_1.class);
                    myIntent.putExtra("image_name", "cn_img_"+position);
                    myIntent.putExtra("position", position+"");
                    myIntent.putExtra("from", "u_list");
                    myIntent.putExtra("key", ob_list.setKey());
                    mCtx.startActivity(myIntent);
                }
            });

            holder.text_change_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(mCtx, pick_image_1.class);
                    myIntent.putExtra("image_name", "cn_img_"+position);
                    myIntent.putExtra("position", position+"");
                    myIntent.putExtra("from", "u_list_edit");
//                    myIntent.putExtra("from", "u_list");
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

//    private boolean check_save() {
//        boolean res = false;
//        for (int i = 0 ; i< crop_data.length ;i++){
//            if (ob_lists.get(i).setReq().equals("1")){
//                if (!ob_lists.get(i).setValue().trim().equals("")){
//                    res = true;
//                }else {
//                    res = false;
//                }
//            }
//        }
//        return res;
//    }

    private void get_data() {
        for (int i = 0 ; i < crop_data.length ; i++){
            ob_lists.get(i).getValue(sessionsave.get_four("usp_"+i));
            if (ob_lists.get(i).setType().equals("image")){
                Log.e(TAG ,"skip saving");
            }else {
                sessionsave.save_four("usp_key_" + i, ob_lists.get(i).setKey());
            }
        }
    }

//    private void save_observe() {
//        for (int i = 0 ; i< crop_data.length ;i++){
//            sessionsave.save_four("usp_"+i ,ob_lists.get(i).setValue());
//        }
//    }

    @Override
    public int getItemCount() {
        return ob_lists.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView text_mtext,text_mdate ,text_img ,text_vid ,text_change_img;
        RelativeLayout r_spin, r_date ,r_check ,r_img ,r_video;
        EditText text_medit ,edit_multi;
        Spinner mspin;
        Button next, btn_previous;
        RecyclerView list_check;
        int pos;
        ImageView img_view;
        VideoView video_view;

        ViewHolder(final View itemView) {
            super(itemView);

            text_mtext = itemView.findViewById(R.id.text1);
            text_medit = itemView.findViewById(R.id.edit);
            edit_multi = itemView.findViewById(R.id.edit_multi);
            text_mdate = itemView.findViewById(R.id.date);
            mspin = itemView.findViewById(R.id.spin);
            r_spin = itemView.findViewById((R.id.rspin));
            r_date = itemView.findViewById((R.id.rdate));
            r_check = itemView.findViewById((R.id.r_check));
            r_img = itemView.findViewById((R.id.r_image));
            r_video = itemView.findViewById((R.id.r_video));
            list_check = itemView.findViewById(R.id.list_check);
            next = itemView.findViewById(R.id.next);
            img_view = itemView.findViewById(R.id.img_photo);
            video_view = itemView.findViewById(R.id.videoPreview);
            text_img = itemView.findViewById(R.id.text_click_img);
            text_vid = itemView.findViewById(R.id.text_click_vid);
            text_change_img = itemView.findViewById(R.id.text_change_img);
            btn_previous = itemView.findViewById(R.id.btn_previous);

            list_check.setHasFixedSize(true);
            list_check.setLayoutManager(new LinearLayoutManager(mCtx));

            next.setVisibility(View.GONE);
            btn_previous.setVisibility(View.GONE);
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
