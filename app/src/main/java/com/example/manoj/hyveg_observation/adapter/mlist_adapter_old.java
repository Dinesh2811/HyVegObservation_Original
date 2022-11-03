package com.example.manoj.hyveg_observation.adapter;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.manoj.hyveg_observation.R;
import com.example.manoj.hyveg_observation.activity.Sessionsave;
import com.example.manoj.hyveg_observation.list.ob_list;

import java.util.Calendar;
import java.util.List;

public class mlist_adapter_old extends RecyclerView.Adapter<mlist_adapter_old.ViewHolder>{

    private Context mCtx;
    private List<ob_list> ob_lists;
    private String mActivity = "";
    private String mActivity1 = "" ,date;
    private final String TAG = "adapter";
    private DatePickerDialog datePickerDialog;
    private Sessionsave sessionsave;

    private String[] crop_data;

    public mlist_adapter_old(Context mCtx, List<ob_list> ob_lists, String mActivity, String mActivity1) {
        super();
        this.ob_lists = ob_lists;
        this.mCtx = mCtx;
        this.mActivity = mActivity;
        this.mActivity1 = mActivity1;

        sessionsave = new Sessionsave(mCtx);
    }

    @NonNull
    @Override
    public mlist_adapter_old.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view, parent, false);
        mlist_adapter_old.ViewHolder viewHolder = new mlist_adapter_old.ViewHolder(v);

        crop_data = new String[ob_lists.size()];

        return viewHolder;

    }


    @Override
    public void onBindViewHolder(@NonNull final mlist_adapter_old.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {


        final ob_list ob_list = ob_lists.get(position);

        holder.setIsRecyclable(false);
        Log.e(TAG, "oblist "+ob_list);
        holder.pos = position;

        if (mActivity.equals("edit")) {
            Log.e(TAG, "on edit");
        } else {
            get_data();
        }

        try {
            holder.text_mtext.setText(ob_list.setName());
            holder.text_medit.setText(ob_list.setValue());
            holder.text_mdate.setText(ob_list.setValue());

            holder.next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent broadCastReceiver = new Intent();
                    broadCastReceiver.setAction("CHAT_RECEIVED");
                    if (mActivity1.equals("nodisease")) {

                        save_master();
                        broadCastReceiver.putExtra("id", "1");

                    } else if (mActivity1.equals("disease")) {
                        save_master();
                        broadCastReceiver.putExtra("id", "1");

                    }
                    mCtx.sendBroadcast(broadCastReceiver);

                }
            });

            switch (ob_list.setType()) {
                case "select":
                    holder.text_medit.setVisibility(View.GONE);
                    holder.r_date.setVisibility(View.GONE);
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

//                    try {
//                        holder.mspin.setSelection(Integer.parseInt(ob_list.setValue()));
//                        sessionsave.save_one("master_" + position, Integer.parseInt(ob_list.setValue()) + "");
//                        sessionsave.save_one("master_key_" + position, ob_list.setKey() + "");
//                    } catch (Exception e) {
//                        holder.mspin.setSelection(0);
//                    }

                    try {
                        for (int  i = 0 ; i < choice_desc_2.length ; i++){
                            if (choice_desc_2[i].equals(ob_list.setValue())){
                                holder.mspin.setSelection(i);
                                sessionsave.save_one("master_" + position, choice_desc_2[i]);
                            }
                        }
                        sessionsave.save_one("master_key_" + position, ob_list.setKey() + "");
                    } catch (Exception e) {
                        holder.mspin.setSelection(0);
                    }

                    break;

                case "checkbox":
                    holder.text_medit.setVisibility(View.GONE);
                    holder.r_date.setVisibility(View.GONE);
                    holder.r_spin.setVisibility(View.VISIBLE);

                    String[] choice_desc_val_1 = ob_list.setChoice_desc();
                    String[] choice_desc_1 = new String[ob_list.setChoice_desc().length + 1];
                    choice_desc_1[0] = "Select";
                    System.arraycopy(choice_desc_val_1, 0, choice_desc_1, 1, ob_list.setChoice_desc().length);
                    ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this.mCtx, android.R.layout.simple_spinner_item, choice_desc_1);

                    String[] choice_desc_val_3 = ob_list.setChoice_val();
                    String[] choice_desc_3 = new String[ob_list.setChoice_val().length + 1];
                    choice_desc_3[0] = "Select";
                    System.arraycopy(choice_desc_val_3, 0, choice_desc_3, 1, ob_list.setChoice_val().length);

//                    String multi_sel = ob_list.set_multi();
//
//                    RecyclerView.Adapter adapter = new check_adapter(mCtx, choice_desc_val_1, choice_desc_val_3 ,multi_sel);
//                    holder.list_check.setAdapter(adapter);


                    dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    holder.mspin.setAdapter(dataAdapter1);
//
////                    try {
////                        holder.mspin.setSelection(Integer.parseInt(ob_list.setValue()));
////                        sessionsave.save_one("master_" + position, Integer.parseInt(ob_list.setValue()) + "");
////                        sessionsave.save_one("master_key_" + position, ob_list.setKey() + "");
////                    } catch (Exception e) {
////                        holder.mspin.setSelection(0);
////                    }
//
                    try {
                        for (int  i = 0 ; i < choice_desc_3.length ; i++){
                            if (choice_desc_3[i].equals(ob_list.setValue())){
                                holder.mspin.setSelection(i);
                                sessionsave.save_one("master_" + position, choice_desc_3[i]);
                            }
                        }
                        sessionsave.save_one("master_key_" + position, ob_list.setKey() + "");
                    } catch (Exception e) {
                        holder.mspin.setSelection(0);
                    }

                    break;

                case "date":
                    holder.r_date.setVisibility((View.VISIBLE));
                    holder.text_medit.setVisibility(View.GONE);
                    holder.r_spin.setVisibility(View.GONE);

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
                                            holder.text_mdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                            crop_data[position] = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                            ob_list.getValue(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        }
                    });
                    break;
    //
//                case "datetime":
//                    holder.r_date.setVisibility((View.VISIBLE));
//                    holder.text_medit.setVisibility(View.GONE);
////                    holder.r_spin.setVisibility(View.GONE);
//
//                    holder.text_mdate.setOnClickListener(new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View view) {
//                            // calender class's instance and get current date , month and year from calender
//                            final Calendar c = Calendar.getInstance();
//                            int mYear = c.get(Calendar.YEAR); // current year
//                            int mMonth = c.get(Calendar.MONTH); // current mont
//                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
//                            // date picker dialog
//                            datePickerDialog = new DatePickerDialog(mCtx,
//                                    new DatePickerDialog.OnDateSetListener() {
//
//                                        @SuppressLint("SetTextI18n")
//                                        @Override
//                                        public void onDateSet(DatePicker view, int year,
//                                                              int monthOfYear, int dayOfMonth) {
//                                            // set day of month , month and year value in the edit text
////                                            holder.text_mdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
////                                            crop_data[position] = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
////                                            ob_list.getValue(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                                            date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                                            int mHour = c.get(Calendar.HOUR_OF_DAY);
//                                            int mMinute = c.get(Calendar.MINUTE);
//                                            final DecimalFormat dcf = new DecimalFormat("00");
//
//                                            // Launch Time Picker Dialog
//                                            TimePickerDialog timePickerDialog = new TimePickerDialog(mCtx,
//                                                    new TimePickerDialog.OnTimeSetListener() {
//
//                                                        @Override
//                                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//
//                                                            holder.text_mdate.setText(date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
//                                                            crop_data[position] = date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute);
//                                                            ob_list.getValue(date+" "+dcf.format(hourOfDay) + ":" + dcf.format(minute));
//                                                        }
//                                                    }, mHour, mMinute, false);
//                                            timePickerDialog.show();
//                                        }
//                                    }, mYear, mMonth, mDay);
//                            datePickerDialog.show();
//                        }
//                    });
//                    break;
//
//                case "text":
//                    holder.text_medit.setVisibility(View.VISIBLE);
//                    break;
//
//                case "image":
//                    holder.text_medit.setVisibility(View.GONE);
//                    holder.r_img.setVisibility(View.VISIBLE);
//                    break;
//
//                case "video":
//                    holder.text_medit.setVisibility(View.GONE);
//                    holder.r_video.setVisibility(View.VISIBLE);
//                    break;
//
//                case "textarea":
//                    holder.text_medit.setVisibility(View.GONE);
//                    holder.edit_multi.setVisibility(View.VISIBLE);
//                    break;

                default:
                    holder.r_date.setVisibility((View.GONE));
                    holder.text_medit.setVisibility(View.VISIBLE);
                    holder.r_spin.setVisibility(View.GONE);
                    break;
            }

            if (position == (ob_lists.size()) - 1) {
                holder.next.setVisibility(View.VISIBLE);
            } else {
                holder.next.setVisibility(View.GONE);
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
                    sessionsave.save_one("master_" + position, arg0.toString());
                    sessionsave.save_one("master_key_" + position, ob_list.setKey());

                }
            });

            holder.mspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    crop_data[position] = holder.mspin.getSelectedItemPosition() + "";
//                    ob_list.getValue(holder.mspin.getSelectedItemPosition() + "");
//                    sessionsave.save_one("master_" + position, holder.mspin.getSelectedItemPosition() + "");
//                    sessionsave.save_one("master_key_" + position, ob_list.setKey() + "");

                    String[] choice_desc_val_1 = ob_list.setChoice_val();
                    String[] choice_desc_1 = new String[ob_list.setChoice_val().length + 1];
                    choice_desc_1[0] = "Select";
                    System.arraycopy(choice_desc_val_1, 0, choice_desc_1, 1, ob_list.setChoice_val().length);
                    ob_list.getValue(choice_desc_1[holder.mspin.getSelectedItemPosition()]);
                    sessionsave.save_one("master_" + position, choice_desc_1[holder.mspin.getSelectedItemPosition()]);
                    sessionsave.save_one("master_key_" + position, ob_list.setKey() + "");
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        } catch (Exception e) {
            Log.e(TAG, "error " + e.toString());
        }
    }

    private void save_master() {
        for (int i = 0 ; i< crop_data.length ;i++){
            sessionsave.save_one("master_"+i ,ob_lists.get(i).setValue());
            sessionsave.save_one("master_key_"+i ,ob_lists.get(i).setKey());
        }
        sessionsave.save_one("master_len" ,crop_data.length+"");
    }

    @Override
    public int getItemCount() {
        return ob_lists.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return position;
    }

    private void get_data() {
        for (int i = 0 ; i < crop_data.length ; i++){
            Log.e("session one get ",sessionsave.get_one("master_"+i));
            ob_lists.get(i).getValue(sessionsave.get_one("master_"+i));
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView text_mtext,text_mdate;
        RelativeLayout r_spin, r_date;
        EditText text_medit;
        Spinner mspin;
        Button next;
        int pos;

        ViewHolder(View itemView) {
            super(itemView);
            text_mtext = itemView.findViewById(R.id.text1);
            text_medit = itemView.findViewById(R.id.edit);
            text_mdate = itemView.findViewById(R.id.date);
            mspin = itemView.findViewById(R.id.spin);
            next = itemView.findViewById(R.id.next);
            r_spin = itemView.findViewById((R.id.rspin));
            r_date = itemView.findViewById(R.id.rdate);
        }
    }
}

