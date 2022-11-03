package com.example.manoj.hyveg_observation.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manoj.hyveg_observation.R;
import com.example.manoj.hyveg_observation.activity.Sessionsave;
import com.example.manoj.hyveg_observation.fragments.observation_new;
import com.example.manoj.hyveg_observation.list.ListModel;
import com.example.manoj.hyveg_observation.list.rowCheckBoxModel;
import com.example.manoj.hyveg_observation.pass_postion_onclick;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.manoj.hyveg_observation.activity.Observation.check_hyb;

public class FormAdapter extends RecyclerView.Adapter<FormAdapter.MyViewHolder> {

    private Sessionsave sessionsave;

    Context mCtx;


    public static  int TOTAL_SPINNER = check_hyb.size() ;
    private Activity mActivity;
    private observation_new observe;

    private ArrayList<ListModel> mList = new ArrayList();

    private OnItemClickedListener onItemClickedListener;

    private DatePickerDialog datePickerDialog;
    private DecimalFormat dcf = new DecimalFormat("00");



    int prePos = -1;
//    int spPos = 0;

    public FormAdapter(ArrayList<ListModel> mList, Activity mActivity, Context ctx) {
        this.mList = mList;
        this.mActivity = mActivity;
        this.mCtx = ctx;





    }

    @Override
    public int getItemCount() {
//        return 10;
        return this.mList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {

        sessionsave = new Sessionsave(mActivity);

//        Log.e("values--form---session", String.valueOf(sessionsave.getCheck_hybrid()));
//
//        if(sessionsave.getCheck_hybrid()==0){
//            Log.e("values----session","no");
//        }else{
//            TOTAL_SPINNER = sessionsave.getCheck_hybrid();
//        }

        myViewHolder.tvName.setText(mList.get(i).getFname());
        if (mList.get(i).getIntType() == 1){
            myViewHolder.llEditText.setVisibility(View.GONE);
            myViewHolder.llSpinner.setVisibility(View.VISIBLE);
            myViewHolder.llCheckBox.setVisibility(View.GONE);
            myViewHolder.llImageText.setVisibility(View.GONE);
            myViewHolder.llDateText.setVisibility(View.GONE);

            for (int pos = 0; pos < TOTAL_SPINNER; pos++){
                setSpinner(getSpinner(pos, myViewHolder), mList.get(i).getChoice_desc(), i, pos);
            }
          /*  setSpinner(myViewHolder.sp1, mList.get(i).getChoice_desc(), i, spPos);
            setSpinner(myViewHolder.sp2, mList.get(i).getChoice_desc(), i, spPos +1);
            setSpinner(myViewHolder.sp3, mList.get(i).getChoice_desc(), i, 3);
            setSpinner(myViewHolder.sp4, mList.get(i).getChoice_desc(), i, 4);
            setSpinner(myViewHolder.sp5, mList.get(i).getChoice_desc(), i, 5);
            setSpinner(myViewHolder.sp6, mList.get(i).getChoice_desc(), i, 6);
            setSpinner(myViewHolder.sp7, mList.get(i).getChoice_desc(), i, 7);
            setSpinner(myViewHolder.sp8, mList.get(i).getChoice_desc(), i, 8);
            setSpinner(myViewHolder.sp9, mList.get(i).getChoice_desc(), i, 9);
            setSpinner(myViewHolder.sp10, mList.get(i).getChoice_desc(), i, 10);*/

            setSpinnerClick(myViewHolder);
        } else if (mList.get(i).getIntType() == 2){
            myViewHolder.llEditText.setVisibility(View.VISIBLE);
            myViewHolder.llSpinner.setVisibility(View.GONE);
            myViewHolder.llCheckBox.setVisibility(View.GONE);
            myViewHolder.llImageText.setVisibility(View.GONE);
            myViewHolder.llDateText.setVisibility(View.GONE);

            myViewHolder.et1.setInputType(InputType.TYPE_CLASS_NUMBER);
            myViewHolder.et2.setInputType(InputType.TYPE_CLASS_NUMBER);
            myViewHolder.et3.setInputType(InputType.TYPE_CLASS_NUMBER);
            myViewHolder.et4.setInputType(InputType.TYPE_CLASS_NUMBER);
            myViewHolder.et5.setInputType(InputType.TYPE_CLASS_NUMBER);
            myViewHolder.et6.setInputType(InputType.TYPE_CLASS_NUMBER);
            myViewHolder.et7.setInputType(InputType.TYPE_CLASS_NUMBER);
            myViewHolder.et8.setInputType(InputType.TYPE_CLASS_NUMBER);
            myViewHolder.et9.setInputType(InputType.TYPE_CLASS_NUMBER);
            myViewHolder.et10.setInputType(InputType.TYPE_CLASS_NUMBER);
            setEditText(myViewHolder);
        } else if (mList.get(i).getIntType() == 3){
            myViewHolder.llEditText.setVisibility(View.GONE);
            myViewHolder.llSpinner.setVisibility(View.GONE);
            myViewHolder.llCheckBox.setVisibility(View.VISIBLE);
            myViewHolder.llImageText.setVisibility(View.GONE);
            myViewHolder.llDateText.setVisibility(View.GONE);
            setUpRecyclerView(myViewHolder);
        }else if(mList.get(i).getIntType() == 5){
            myViewHolder.llEditText.setVisibility(View.GONE);
            myViewHolder.llSpinner.setVisibility(View.GONE);
            myViewHolder.llCheckBox.setVisibility(View.GONE);
            myViewHolder.llImageText.setVisibility(View.GONE);
            myViewHolder.llDateText.setVisibility(View.VISIBLE);
            setDateRecyclerView(myViewHolder);
        }
    }

    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup paramViewGroup, int paramInt) {
        return new MyViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.raw_form, paramViewGroup, false));
    }

    private void setUpRecyclerView(final MyViewHolder myViewHolder) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false);
        myViewHolder.rvRowCheckBox.setLayoutManager(mLayoutManager);
        myViewHolder.mAdapter = new RowCheckBoxAdapter (mList.get(myViewHolder.getAdapterPosition()).getCheckBoxList(), mActivity,(pass_postion_onclick) mCtx);
        myViewHolder.rvRowCheckBox.setAdapter(myViewHolder.mAdapter);
        myViewHolder.mAdapter.setOnItemClickedListener(new RowCheckBoxAdapter.OnItemClickedListener() {
            @Override
            public void OnItemClicked(int param1Int, rowCheckBoxModel model) {
                try {
                    mList.get(myViewHolder.getAdapterPosition()).getmCheckBoxList().set(param1Int, model);
//                    notifyItemChanged(myViewHolder.getAdapterPosition());
                }catch (Exception e){

                }
            }

            @Override
            public void OnItemClicked(int pos, String decimal) {

            }

            @Override
            public void OnItemClicked(int pos, int spinnerPos) {

            }

            @Override
            public void OnItemClicked(int pos, int ParentPos, int type) {

            }
        });
    }

    public Spinner getSpinner(int pos, MyViewHolder myViewHolder){
        switch (pos){
            case 0:
                return myViewHolder.sp1;
            case 1:
                return myViewHolder.sp2;
            case 2:
                return myViewHolder.sp3;
            case 3:
                return myViewHolder.sp4;
            case 4:
                return myViewHolder.sp5;
            case 5:
                return myViewHolder.sp6;
            case 6:
                return myViewHolder.sp7;
            case 7:
                return myViewHolder.sp8;
            case 8:
                return myViewHolder.sp9;
            case 9:
                return myViewHolder.sp10;
            default:
                return null;
        }
    }

    public void setSpinner(Spinner spinner, String[] list, int i, int pos){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setPrompt(mActivity.getString(R.string.please_select));
        if (mList.get(i).getSelectedChoice(pos) >= 0) {
            spinner.setSelection(mList.get(i).getSelectedChoice(pos));
        }
    }

    public void setSpinnerClick(final MyViewHolder myViewHolder){
       myViewHolder.sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               mList.get(myViewHolder.getAdapterPosition()).setSelectedChoice(
                       myViewHolder.sp1.getSelectedItemPosition(), 0);
           }

           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {

           }
       });
        myViewHolder.sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mList.get(myViewHolder.getAdapterPosition()).setSelectedChoice(myViewHolder.sp2.getSelectedItemPosition(), 1 );
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        myViewHolder.sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mList.get(myViewHolder.getAdapterPosition()).setSelectedChoice(myViewHolder.sp3.getSelectedItemPosition(), 2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        myViewHolder.sp4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mList.get(myViewHolder.getAdapterPosition()).setSelectedChoice(myViewHolder.sp4.getSelectedItemPosition(), 3);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        myViewHolder.sp5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mList.get(myViewHolder.getAdapterPosition()).setSelectedChoice(myViewHolder.sp5.getSelectedItemPosition(), 4);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        myViewHolder.sp6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mList.get(myViewHolder.getAdapterPosition()).setSelectedChoice(myViewHolder.sp6.getSelectedItemPosition(), 5);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        myViewHolder.sp7.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mList.get(myViewHolder.getAdapterPosition()).setSelectedChoice(myViewHolder.sp7.getSelectedItemPosition(),6);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        myViewHolder.sp8.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mList.get(myViewHolder.getAdapterPosition()).setSelectedChoice(myViewHolder.sp8.getSelectedItemPosition(), 7);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        myViewHolder.sp9.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mList.get(myViewHolder.getAdapterPosition()).setSelectedChoice(myViewHolder.sp9.getSelectedItemPosition(), 8);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        myViewHolder.sp10.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mList.get(myViewHolder.getAdapterPosition()).setSelectedChoice(myViewHolder.sp10.getSelectedItemPosition(), 9);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void setEditText(MyViewHolder myViewHolder){
        for (int i = 0; i < TOTAL_SPINNER; i++){
//            myViewHolder.myCustomEditTextListener.updatePosition(i, myViewHolder);
            try {
                if ((mList.get(myViewHolder.getAdapterPosition()).getSelectedDecimal(i) != null)) {
                    getEditText(i, myViewHolder).setText(String.valueOf(mList.get(myViewHolder.getAdapterPosition()).getSelectedDecimal(i)));
                } else {
                    getEditText(i, myViewHolder).setText("");
                }
            } catch (Exception e){
                getEditText(i, myViewHolder).setText("");
            }
        }
    }

    public void setDateRecyclerView(MyViewHolder myViewHolder){
        for (int i = 0; i < TOTAL_SPINNER; i++){
//            myViewHolder.myCustomEditTextListener.updatePosition(i, myViewHolder);
            try {
                if ((mList.get(myViewHolder.getAdapterPosition()).getSelectedDecimal(i) != null)) {
                    getDateText(i, myViewHolder).setText(String.valueOf(mList.get(myViewHolder.getAdapterPosition()).getSelectedDecimal(i)));
                } else {
                    getDateText(i, myViewHolder).setText("");
                }
            } catch (Exception e){
                getDateText(i, myViewHolder).setText("");
            }
        }
    }

    public void setImageRecyclerView(MyViewHolder myViewHolder){
        for (int i = 0; i < TOTAL_SPINNER; i++){
//            myViewHolder.myCustomEditTextListener.updatePosition(i, myViewHolder);
            try {
                if ((mList.get(myViewHolder.getAdapterPosition()).getSelectedImage(i) != null)) {
                    getImageText(i, myViewHolder).setText(String.valueOf(mList.get(myViewHolder.getAdapterPosition()).getSelectedImage(i)));
                } else {
                    getImageText(i, myViewHolder).setText("");
                }
            } catch (Exception e){
                getImageText(i, myViewHolder).setText("");
            }
        }
    }

    private TextView getImageText(int pos, MyViewHolder myViewHolder) {
        switch (pos){
            case 0:
                return myViewHolder.et1;
            case 1:
                return myViewHolder.et2;
            case 2:
                return myViewHolder.et3;
            case 3:
                return myViewHolder.et4;
            case 4:
                return myViewHolder.et5;
            case 5:
                return myViewHolder.et6;
            case 6:
                return myViewHolder.et7;
            case 7:
                return myViewHolder.et8;
            case 8:
                return myViewHolder.et9;
            case 9:
                return myViewHolder.et10;
            default:
                return null;
        }
    }

    public void setEditTextListener(final MyViewHolder myViewHolder){
        myViewHolder.et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    mList.get(myViewHolder.getAdapterPosition()).setSelectedDecimal(charSequence.toString(), 0);
                } catch (Exception e){
//                mList.get(mViewHolder.getAdapterPosition()).setSelectedDecimal(charSequence.toString(), position);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        myViewHolder.et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    mList.get(myViewHolder.getAdapterPosition()).setSelectedDecimal(charSequence.toString(), 1);
                } catch (Exception e){
//                mList.get(mViewHolder.getAdapterPosition()).setSelectedDecimal(charSequence.toString(), position);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        myViewHolder.et3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    mList.get(myViewHolder.getAdapterPosition()).setSelectedDecimal(charSequence.toString(), 2);
                } catch (Exception e){
//                mList.get(mViewHolder.getAdapterPosition()).setSelectedDecimal(charSequence.toString(), position);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        myViewHolder.et4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    mList.get(myViewHolder.getAdapterPosition()).setSelectedDecimal(charSequence.toString(), 3);
                } catch (Exception e){
//                mList.get(mViewHolder.getAdapterPosition()).setSelectedDecimal(charSequence.toString(), position);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        myViewHolder.et5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    mList.get(myViewHolder.getAdapterPosition()).setSelectedDecimal(charSequence.toString(), 4);
                } catch (Exception e){
//                mList.get(mViewHolder.getAdapterPosition()).setSelectedDecimal(charSequence.toString(), position);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        myViewHolder.et6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    mList.get(myViewHolder.getAdapterPosition()).setSelectedDecimal(charSequence.toString(), 5);
                } catch (Exception e){
//                mList.get(mViewHolder.getAdapterPosition()).setSelectedDecimal(charSequence.toString(), position);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        myViewHolder.et7.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    mList.get(myViewHolder.getAdapterPosition()).setSelectedDecimal(charSequence.toString(), 6);
                } catch (Exception e){
//                mList.get(mViewHolder.getAdapterPosition()).setSelectedDecimal(charSequence.toString(), position);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        myViewHolder.et8.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    mList.get(myViewHolder.getAdapterPosition()).setSelectedDecimal(charSequence.toString(), 7);
                } catch (Exception e){
//                mList.get(mViewHolder.getAdapterPosition()).setSelectedDecimal(charSequence.toString(), position);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        myViewHolder.et9.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    mList.get(myViewHolder.getAdapterPosition()).setSelectedDecimal(charSequence.toString(), 8);
                } catch (Exception e){
//                mList.get(mViewHolder.getAdapterPosition()).setSelectedDecimal(charSequence.toString(), position);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        myViewHolder.et10.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    mList.get(myViewHolder.getAdapterPosition()).setSelectedDecimal(charSequence.toString(), 9);
                } catch (Exception e){
//                mList.get(mViewHolder.getAdapterPosition()).setSelectedDecimal(charSequence.toString(), position);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    public void setDateTextListener(final MyViewHolder myViewHolder){

        myViewHolder.date1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current mont
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(mActivity,
                        new DatePickerDialog.OnDateSetListener() {

                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                myViewHolder.date1.setText(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });



    }

    public AppCompatEditText getEditText(int pos, MyViewHolder myViewHolder){
        switch (pos){
            case 0:
                return myViewHolder.et1;
            case 1:
                return myViewHolder.et2;
            case 2:
                return myViewHolder.et3;
            case 3:
                return myViewHolder.et4;
            case 4:
                return myViewHolder.et5;
            case 5:
                return myViewHolder.et6;
            case 6:
                return myViewHolder.et7;
            case 7:
                return myViewHolder.et8;
            case 8:
                return myViewHolder.et9;
            case 9:
                return myViewHolder.et10;
            default:
                return null;
        }
    }
     public TextView getDateText(int pos, MyViewHolder myViewHolder){
        switch (pos){
            case 0:
                return myViewHolder.date1;
            case 1:
                return myViewHolder.date2;
            case 2:
                return myViewHolder.date3;
            case 3:
                return myViewHolder.date4;
            case 4:
                return myViewHolder.date5;
            case 5:
                return myViewHolder.date6;
            case 6:
                return myViewHolder.date7;
            case 7:
                return myViewHolder.date8;
            case 8:
                return myViewHolder.date9;
            case 9:
                return myViewHolder.date10;
            default:
                return null;
        }
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
//        private LinearLayout llMain;

        private AppCompatTextView tvName;
//        public MyCustomEditTextListener myCustomEditTextListener;

        private AppCompatEditText et1;
        private AppCompatEditText et2;
        private AppCompatEditText et3;
        private AppCompatEditText et4;
        private AppCompatEditText et5;
        private AppCompatEditText et6;
        private AppCompatEditText et7;
        private AppCompatEditText et8;
        private AppCompatEditText et9;
        private AppCompatEditText et10;

        private Spinner sp1;
        private Spinner sp2;
        private Spinner sp3;
        private Spinner sp4;
        private Spinner sp5;
        private Spinner sp6;
        private Spinner sp7;
        private Spinner sp8;
        private Spinner sp9;
        private Spinner sp10;

        private TextView date1;
        private TextView date2;
        private TextView date3;
        private TextView date4;
        private TextView date5;
        private TextView date6;
        private TextView date7;
        private TextView date8;
        private TextView date9;
        private TextView date10;


        private RecyclerView rvRowCheckBox;
        private RowCheckBoxAdapter mAdapter;

        private LinearLayout llEditText;
        private LinearLayout llSpinner;
        private LinearLayout llCheckBox;
        private LinearLayout llImageText;
        private LinearLayout llDateText;

        public MyViewHolder(View param1View) {
            super(param1View);
            this.tvName = param1View.findViewById(R.id.tvName);
//            this.myCustomEditTextListener = myCustomEditTextListener;

            this.et1 = param1View.findViewById(R.id.et1);
            this.et2 = param1View.findViewById(R.id.et2);
            this.et3 = param1View.findViewById(R.id.et3);
            this.et4 = param1View.findViewById(R.id.et4);
            this.et5 = param1View.findViewById(R.id.et5);
            this.et6 = param1View.findViewById(R.id.et6);
            this.et7 = param1View.findViewById(R.id.et7);
            this.et8 = param1View.findViewById(R.id.et8);
            this.et9 = param1View.findViewById(R.id.et9);
            this.et10 = param1View.findViewById(R.id.et10);
//
//            this.date1 = param1View.findViewById(R.id.date1);
//            this.date2 = param1View.findViewById(R.id.date2);
//            this.date3 = param1View.findViewById(R.id.date3);
//            this.date4 = param1View.findViewById(R.id.date4);
//            this.date5 = param1View.findViewById(R.id.date5);
//            this.date6 = param1View.findViewById(R.id.date6);
//            this.date7 = param1View.findViewById(R.id.date7);
//            this.date8 = param1View.findViewById(R.id.date8);
//            this.date9 = param1View.findViewById(R.id.date9);
//            this.date10 = param1View.findViewById(R.id.date10);

            setEditTextListener(this);

            setDateTextListener(this);

//            this.et2.addTextChangedListener(myCustomEditTextListener);
//            this.et3.addTextChangedListener(myCustomEditTextListener);
//            this.et4.addTextChangedListener(myCustomEditTextListener);
//            this.et5.addTextChangedListener(myCustomEditTextListener);
//            this.et6.addTextChangedListener(myCustomEditTextListener);
//            this.et7.addTextChangedListener(myCustomEditTextListener);
//            this.et8.addTextChangedListener(myCustomEditTextListener);
//            this.et9.addTextChangedListener(myCustomEditTextListener);
//            this.et10.addTextChangedListener(myCustomEditTextListener);

            this.sp1 = param1View.findViewById(R.id.sp1);
            this.sp2 = param1View.findViewById(R.id.sp2);
            this.sp3 = param1View.findViewById(R.id.sp3);
            this.sp4 = param1View.findViewById(R.id.sp4);
            this.sp5 = param1View.findViewById(R.id.sp5);
            this.sp6 = param1View.findViewById(R.id.sp6);
            this.sp7 = param1View.findViewById(R.id.sp7);
            this.sp8 = param1View.findViewById(R.id.sp8);
            this.sp9 = param1View.findViewById(R.id.sp9);
            this.sp10 = param1View.findViewById(R.id.sp10);

            this.rvRowCheckBox = param1View.findViewById(R.id.rvRowCheckBox);

            this.llEditText = param1View.findViewById(R.id.llEditText);
            this.llSpinner = param1View.findViewById(R.id.llSpinner);
            this.llCheckBox = param1View.findViewById(R.id.llCheckBox);
            this.llImageText = param1View.findViewById(R.id.llImageText);
//            this.llDateText = param1View.findViewById(R.id.llDateText);
        }
    }

    public void setOnItemClickedListener(OnItemClickedListener paramOnItemClickedListener) {
        this.onItemClickedListener = paramOnItemClickedListener;
    }

    public static interface OnItemClickedListener {
        void OnItemClicked(int param1Int);
    }
}