package com.example.manoj.hyveg_observation.adapter;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manoj.hyveg_observation.Others.InputFilterMinMax;
import com.example.manoj.hyveg_observation.Others.InputFilterMinMax_double;
import com.example.manoj.hyveg_observation.R;
import com.example.manoj.hyveg_observation.activity.Sessionsave;
import com.example.manoj.hyveg_observation.fragments.observation_new;
import com.example.manoj.hyveg_observation.list.ob_list;
import com.example.manoj.hyveg_observation.list.rowCheckBoxModel;

import com.example.manoj.hyveg_observation.pass_postion_onclick;
import com.firebase.ui.auth.data.remote.EmailSignInHandler;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class RowCheckBoxAdapter extends RecyclerView.Adapter<RowCheckBoxAdapter.MyViewHolder>implements pass_postion_onclick {
    private static final int VIEW_TYPE_SPINNER = 1;
    private static final int VIEW_TYPE_NUMBER = 2;
    private static final int VIEW_TYPE_CHECKBOX = 3;
    private static final int VIEW_TYPE_IMAGE = 4;
    private static final int VIEW_TYPE_DATE = 5;


    private Context mActivity;
    private Intent myintent;
//    private MyViewHolder myViewHolder;

    private observation_new observe;

    private Sessionsave sessionsave;

    private ArrayList<rowCheckBoxModel> mList = new ArrayList();

    private OnItemClickedListener onItemClickedListener;
    private OnImageClickedListener onImageClickedListener;

    public static Bitmap Image = null;
    private ArrayList<MyViewHolder> holders = new ArrayList<>();

    private DatePickerDialog datePickerDialog;
    private DecimalFormat dcf = new DecimalFormat("00");

    private pass_postion_onclick Interface_pass_position;

    int position ;

    int prePos = -1;
    int parentPos = -1;
    String imagess;

    String edit_value;

    public RowCheckBoxAdapter(ArrayList<rowCheckBoxModel> mList, Context mActivity, int parentPos) {
        this.mList = mList;
        this.mActivity = mActivity;
        this.parentPos = parentPos;

    }

    public RowCheckBoxAdapter(ArrayList<rowCheckBoxModel> mList, Context mActivity,pass_postion_onclick pass_postion_onclick) {
        this.mList = mList;
        this.mActivity = mActivity;
        this.Interface_pass_position = pass_postion_onclick;
    }




    public int getItemCount() {
//        return 10;
        return this.mList.size();
    }

    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {



        sessionsave = new Sessionsave(mActivity);

        Log.e("Values====my", String.valueOf(myViewHolder.getAdapterPosition()));

//        myViewHolder.setIsRecyclable(false);

//        imagess  = myintent.getStringExtra("image");
        if (mList.get(i).getIntType() == 1) {
            setSpinner(myViewHolder, mList.get(i).getChoice_desc());
        } else if (mList.get(i).getIntType() == 2) {
            myViewHolder.et.setText(mList.get(i).getDecimal());

            setEditTextListener(myViewHolder);


        } else if (mList.get(i).getIntType() == 3) {
            setUpRecyclerView(myViewHolder);
        } else if (mList.get(i).getIntType() == 4) {
//            myViewHolder.text_click_img.setText(mList.get(i).getDecimal());
            setUpImageRecyclerViewUpdated(myViewHolder);
//            setUpImageRecyclerView(myViewHolder);
        } else if (mList.get(i).getIntType() == 5) {
            myViewHolder.date.setText(mList.get(i).getDecimal());
            setUpDateRecyclerView(myViewHolder);
        }

    }

    private void setUpImageRecyclerViewUpdated(final MyViewHolder myViewHolder) {
        String img1 = "";
        String img2 = "";
        //For first img
        img1 = getImage(myViewHolder, 0);
        if (img1 != null && !img1.equalsIgnoreCase("")){
            myViewHolder.ivAdd1.setVisibility(View.GONE);
            myViewHolder.ivEdit1.setVisibility(View.VISIBLE);
            myViewHolder.ivDelete1.setVisibility(View.VISIBLE);

            File file = getFile(img1);
            Bitmap bitmap = getBitmap(file);
            myViewHolder.img_photo1.setImageBitmap(bitmap);
        } else {
            myViewHolder.ivAdd1.setVisibility(View.VISIBLE);
            myViewHolder.ivEdit1.setVisibility(View.GONE);
            myViewHolder.ivDelete1.setVisibility(View.GONE);
        }

        //For first img
        img2 = getImage(myViewHolder, 1);
        if (img2 != null && !img2.equalsIgnoreCase("")){
            myViewHolder.ivAdd2.setVisibility(View.GONE);
            myViewHolder.ivEdit2.setVisibility(View.VISIBLE);
            myViewHolder.ivDelete2.setVisibility(View.VISIBLE);

            File file2 = getFile(img2);
            Bitmap bitmap2 = getBitmap(file2);
            myViewHolder.img_photo2.setImageBitmap(bitmap2);
        } else {
            myViewHolder.ivAdd2.setVisibility(View.VISIBLE);
            myViewHolder.ivEdit2.setVisibility(View.GONE);
            myViewHolder.ivDelete2.setVisibility(View.GONE);
        }

        myViewHolder.ivAdd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onImageClickedListener != null) {
                    onImageClickedListener.OnItemClicked(myViewHolder.getAdapterPosition(), parentPos, 1, 0);

                    Log.e("Values----focus--", String.valueOf(myViewHolder.getAdapterPosition()));
//                    Interface_pass_position.pass_position(myViewHolder.getAdapterPosition(),"image");

                }


            }
        });
        myViewHolder.ivAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onImageClickedListener != null) {
                    onImageClickedListener.OnItemClicked(myViewHolder.getAdapterPosition(), parentPos, 1, 1);
                }
            }
        });

        myViewHolder.ivEdit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onImageClickedListener != null) {
                    onImageClickedListener.OnItemClicked(myViewHolder.getAdapterPosition(), parentPos, 2, 0);
                }
            }
        });
        myViewHolder.ivEdit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onImageClickedListener != null) {
                    onImageClickedListener.OnItemClicked(myViewHolder.getAdapterPosition(), parentPos, 2, 1);
                }
            }
        });

        myViewHolder.ivDelete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onImageClickedListener != null) {
                    onImageClickedListener.OnItemClicked(myViewHolder.getAdapterPosition(), parentPos, 3, 0);
                }
            }
        });
        myViewHolder.ivDelete2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onImageClickedListener != null) {
                    onImageClickedListener.OnItemClicked(myViewHolder.getAdapterPosition(), parentPos, 3, 1);
                }
            }
        });

    }

    public String getImage(MyViewHolder myViewHolder, int i_pos)
    {
        String img = "";
        if (mList.get(myViewHolder.getAdapterPosition()).getChoice_desc().length > i_pos) {
            if (mList.get(myViewHolder.getAdapterPosition()).getChoice_desc() != null && mList.get(myViewHolder.getAdapterPosition()).getChoice_desc()[i_pos] != null &&
                    !mList.get(myViewHolder.getAdapterPosition()).getChoice_desc()[i_pos].equalsIgnoreCase("")) {
                img = mList.get(myViewHolder.getAdapterPosition()).getChoice_desc()[i_pos];
            }
        }
        return img;
    }

    /* private void setUpImageRecyclerView(final MyViewHolder myViewHolder) {

         ArrayList<String> img_names = new ArrayList<>();
         ArrayList<String> images = new ArrayList<>();
        myViewHolder.text_click_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(mActivity, Pick_image.class);
                myIntent.putExtra("image_name", "gc_img_"+myViewHolder.getAdapterPosition());
                myIntent.putExtra("position", myViewHolder.getAdapterPosition()+"");
                myIntent.putExtra("from", "g_list");
                myIntent.putExtra("pos", myViewHolder.getAdapterPosition());
                myIntent.putExtra("parentPos", parentPos);
                mActivity.startActivityForResult(myIntent, 1230);
            }
        });
        myViewHolder.img_photo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(mActivity, Pick_image.class);
                myIntent.putExtra("image_name", "gc_img_"+myViewHolder.getAdapterPosition());
                myIntent.putExtra("position", myViewHolder.getAdapterPosition()+"");
                myIntent.putExtra("from", "g_list_edit");
                mActivity.startActivityForResult(myIntent, 1230);
            }
        });
         myViewHolder.img_photo2.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent myIntent = new Intent(mActivity, Pick_image.class);
                 myIntent.putExtra("image_name", "gc_img_"+myViewHolder.getAdapterPosition());
                 myIntent.putExtra("position", myViewHolder.getAdapterPosition()+"");
                 myIntent.putExtra("from", "g_list_edit");
                 mActivity.startActivityForResult(myIntent, 1230);
             }
         });
         if (mList.get(myViewHolder.getAdapterPosition()).getChoice_desc() != null && mList.get(myViewHolder.getAdapterPosition()).getChoice_desc()[0] != null &&
         !mList.get(myViewHolder.getAdapterPosition()).getChoice_desc()[0].equalsIgnoreCase("")){
 //        if (myViewHolder.text_click_img.getText().toString().contains("images added")){
             myViewHolder.img_photo1.setVisibility(View.VISIBLE);
             File file = getFile(myViewHolder.getAdapterPosition(), 0);
             Bitmap bitmap = getBitmap(file);
             myViewHolder.img_photo1.setImageBitmap(bitmap);
 //                holder.img_view.setImageBitmap(StringToBitMap(ob_list.setValue()));
             myViewHolder.text_click_img.setVisibility(View.VISIBLE);
 //            myViewHolder.text_click_img.setText(sessionsave.get_image("general_len")+" images added");
             myViewHolder.text_change_img.setVisibility(View.VISIBLE);
 //            myViewHolder.text_click_img.setEnabled(false);
         }  else {
             myViewHolder.img_photo1.setVisibility(View.GONE);
             myViewHolder.text_click_img.setVisibility(View.VISIBLE);
             myViewHolder.text_change_img.setVisibility(View.GONE);
         }
         if (mList.get(myViewHolder.getAdapterPosition()).getChoice_desc() != null && mList.get(myViewHolder.getAdapterPosition()).getChoice_desc()[1] != null &&
                 !mList.get(myViewHolder.getAdapterPosition()).getChoice_desc()[1].equalsIgnoreCase("")){
             myViewHolder.img_photo2.setVisibility(View.VISIBLE);
             File file = getFile(myViewHolder.getAdapterPosition(), 1);
             Bitmap bitmap = getBitmap(file);
             myViewHolder.img_photo2.setImageBitmap(bitmap);
             myViewHolder.text_click_img.setVisibility(View.GONE);
             myViewHolder.text_change_img.setVisibility(View.GONE);
 //            myViewHolder.text_click_img.setEnabled(true);
         } else {
             myViewHolder.img_photo2.setVisibility(View.GONE);
             myViewHolder.text_click_img.setVisibility(View.VISIBLE);
             myViewHolder.text_change_img.setVisibility(View.GONE);
         }

         Log.e("values",sessionsave.get_image("general_len")+" images added");
         Log.e("values-----img",sessionsave.get_image("general_" + 0));
 //        mList.get(myViewHolder.getAdapterPosition()).setDecimal(sessionsave.get_image("general_len")+" images added");
 //        mList.get(myViewHolder.getAdapterPosition()).setSelectedChoice(0,sessionsave.get_image("general_" + 0)+" images added");
 //        mList.get(myViewHolder.getAdapterPosition()).setSelectedChoice(1,sessionsave.get_image("general_" + 1)+" images added");
         onItemClickedListener.OnItemClicked(myViewHolder.getAdapterPosition(), sessionsave.get_image("general_len")+" images added");
         onItemClickedListener.OnItemClicked(myViewHolder.getAdapterPosition(), mList.get(myViewHolder.getAdapterPosition()));
         Log.e("values","imagess");
     }
 */


    public File getFile(String url) {
       /* String url = Environment.getExternalStorageDirectory() + "/RnDObservation/Images/"
                + parentPos + pos + name + ".jpg";*/
        try {
            return new File(url);
        } catch (Exception str) {
            return null;
        }
    }

    public Bitmap getBitmap(File image) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
//        bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
        return bitmap;
    }


    private void setEditTextListener(final MyViewHolder myViewHolder) {
        myViewHolder.et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                Log.e("Values---max---", charSequence.toString());

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mList.get(myViewHolder.getAdapterPosition()).setDecimal(charSequence.toString());
                onItemClickedListener.OnItemClicked(myViewHolder.getAdapterPosition(), charSequence.toString());
                try {
                    edit_value = (charSequence.toString());
                }catch (NumberFormatException e){
                    e.getMessage();
                    edit_value = (charSequence.toString());
                }

                Log.e("Values---max--1-", String.valueOf(edit_value));


//

//

//                for(int j = 0;j<observation_new.mList.size();j++){
//                    int setmin = (int) observation_new.mList.get(j).getMinVal();
//                    int setmax = (int) observation_new.mList.get(j).getMaxVal();
//                    Log.e("Values---min---", observation_new.mList.get(j).getFname());
//                    Log.e("Values---max---", String.valueOf(setmax));
//                    try{
//                        Log.e("Values---decimal---", observation_new.mList.get(j).getDecimal());
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                if (observation_new.mList.get(FormAdapterDynamic.position).getIntType().equals("number")){
                    if (observation_new.mList.get(FormAdapterDynamic.position).getDecimal() != null &&observation_new.mList.get(FormAdapterDynamic.position).getDecimal().equals("1")) {
                myViewHolder.et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                if (observation_new.mList.get(FormAdapterDynamic.position).getMinVal()!=0 && observation_new.mList.get(FormAdapterDynamic.position).getMaxVal()!=0) {
                    myViewHolder.et.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax_double(observation_new.mList.get(FormAdapterDynamic.position).getMinVal(), observation_new.mList.get(FormAdapterDynamic.position).getMaxVal(), "float")});
                } else if (observation_new.mList.get(FormAdapterDynamic.position).getMinVal()!=0 && observation_new.mList.get(FormAdapterDynamic.position).getMaxVal()==0) {
                    myViewHolder.et.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax_double(observation_new.mList.get(FormAdapterDynamic.position).getMinVal(), 9999, "float")});
                } else if (observation_new.mList.get(FormAdapterDynamic.position).getMinVal()==0 && observation_new.mList.get(FormAdapterDynamic.position).getMaxVal()!=0) {
                    myViewHolder.et.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6), new InputFilterMinMax_double(0, observation_new.mList.get(FormAdapterDynamic.position).getMaxVal(), "float")});
                } else {
                    myViewHolder.et.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(6)});
                }
            } else {
                myViewHolder.et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                if (observation_new.mList.get(FormAdapterDynamic.position).getMinVal()!=0 && observation_new.mList.get(FormAdapterDynamic.position).getMaxVal()!=0) {
                    myViewHolder.et.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax_double(observation_new.mList.get(FormAdapterDynamic.position).getMinVal(), observation_new.mList.get(FormAdapterDynamic.position).getMaxVal(), "int")});
                } else if (observation_new.mList.get(FormAdapterDynamic.position).getMinVal()!=0 && observation_new.mList.get(FormAdapterDynamic.position).getMaxVal()==0) {
                    myViewHolder.et.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax_double(observation_new.mList.get(FormAdapterDynamic.position).getMinVal(), 9999, "int")});
                } else if (observation_new.mList.get(FormAdapterDynamic.position).getMinVal()==0 && observation_new.mList.get(FormAdapterDynamic.position).getMaxVal()!=0) {
                    myViewHolder.et.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4), new InputFilterMinMax_double(0, observation_new.mList.get(FormAdapterDynamic.position).getMaxVal(), "int")});
                } else {
                    myViewHolder.et.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4)});
                }
            }

//                }



            }

            @Override
            public void afterTextChanged(Editable editable) {
                int edit = 0;
                try {
                    edit = Integer.parseInt(edit_value);
                }catch (Exception e){
                    e.printStackTrace();
//                    edit = (int) Double.parseDouble(edit_value);
                }
                int min_value = (int) observation_new.mList.get(FormAdapterDynamic.position).getMinVal();
                int max_value = (int) observation_new.mList.get(FormAdapterDynamic.position).getMaxVal();
                Log.e("Values---min--2-", String.valueOf(min_value));
                Log.e("Values---max--2-", String.valueOf(max_value));
                if((max_value!=0)||(min_value!=0)){
                    if((edit>max_value)||(edit<min_value)){
                        myViewHolder.et.setError("Please enter number between "+min_value + " to "  + max_value);
                    }
                }


            }
        });
        myViewHolder.et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    Interface_pass_position.pass_position(myViewHolder.getAdapterPosition(),"edit");
                    Log.e("Values----focus--", String.valueOf(myViewHolder.getAdapterPosition()));


                }
            }
        });


    }

    public void setEditTextValidation(int pos, int headingPos, String msg) {
        if (holders.get(headingPos) != null) {
            holders.get(headingPos).et.setError(msg);
        }
    }

    private void setUpDateRecyclerView(final MyViewHolder myViewHolder) {

        myViewHolder.date.setOnClickListener(new View.OnClickListener() {
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
                                // set day of month , month and year value in the edit text
                                myViewHolder.date.setText(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                mList.get(myViewHolder.getAdapterPosition()).setDecimal(dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                                onItemClickedListener.OnItemClicked(myViewHolder.getAdapterPosition(), dcf.format(dayOfMonth) + "/" + dcf.format(monthOfYear + 1) + "/" + dcf.format(year));
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                Log.e("Values------date", String.valueOf(myViewHolder.getAdapterPosition()));
            }
        });

        myViewHolder.date.setFocusableInTouchMode(true);
        myViewHolder.date.requestFocus();
        myViewHolder.date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {

                    Interface_pass_position.pass_position(myViewHolder.getAdapterPosition(),"date");
                    Log.e("Values----focus--", String.valueOf(myViewHolder.getAdapterPosition()));

                }

            }
        });

    }


    public void setSpinner(final MyViewHolder myViewHolder, String[] list) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myViewHolder.sp.setAdapter(dataAdapter);myViewHolder.sp.setAdapter(dataAdapter);
        myViewHolder.sp.setPrompt(mActivity.getString(R.string.please_select));
        if (mList.get(myViewHolder.getAdapterPosition()).getSelectedChoice() >= 0) {
            myViewHolder.sp.setSelection(mList.get(myViewHolder.getAdapterPosition()).getSelectedChoice());
        }

        myViewHolder.sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mList.get(myViewHolder.getAdapterPosition()).setSelectedChoice(myViewHolder.sp.getSelectedItemPosition());
                onItemClickedListener.OnItemClicked(myViewHolder.getAdapterPosition(), myViewHolder.sp.getSelectedItemPosition());
                Log.e("Values------spin", String.valueOf(myViewHolder.getAdapterPosition()));
//                Interface_pass_position.pass_position(myViewHolder.getAdapterPosition(),"Spin");
                @SuppressLint("RestrictedApi") InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(myViewHolder.sp.getWindowToken(), 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        myViewHolder.sp.setFocusable(true);
        myViewHolder.sp.setFocusableInTouchMode(true);
        myViewHolder.sp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.e("Values---focus---spin", String.valueOf(myViewHolder.getAdapterPosition()));
                    Interface_pass_position.pass_position(myViewHolder.getAdapterPosition(),"Spin");
                    @SuppressLint("RestrictedApi") InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(myViewHolder.sp.getWindowToken(), 0);

                }
            }
        });
    }

    private void setUpRecyclerView(final MyViewHolder myViewHolder) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        myViewHolder.rvCheckBox.setLayoutManager(mLayoutManager);
        myViewHolder.mAdapter = new CheckBoxAdapter(mList.get(myViewHolder.getAdapterPosition()).getmCheckBoxList(),
                mActivity, myViewHolder.rvCheckBox, false);
        myViewHolder.rvCheckBox.setAdapter(myViewHolder.mAdapter);
        myViewHolder.mAdapter.setOnItemClickedListener(new CheckBoxAdapter.OnItemClickedListener() {
            @Override
            public void OnItemClicked(int pos, int prePos, String data, boolean isChecked) {
                try {
                    mList.get(myViewHolder.getAdapterPosition()).getmCheckBoxList().get(pos).setChecked(isChecked);
//                    mList.get(myViewHolder.getAdapterPosition()).getmCheckBoxList().get(prePos).setChecked(false);
//                    notifyItemChanged(myViewHolder.getAdapterPosition());
                    mList.get(myViewHolder.getAdapterPosition()).setSelectedChoice(pos);
                    onItemClickedListener.OnItemClicked(myViewHolder.getAdapterPosition(), mList.get(myViewHolder.getAdapterPosition()));
                    Interface_pass_position.pass_position(myViewHolder.getAdapterPosition(),"check");
                    Log.e("Values------check", String.valueOf(myViewHolder.getAdapterPosition()));
                    @SuppressLint("RestrictedApi") InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(myViewHolder.rvCheckBox.getWindowToken(), 0);
                } catch (Exception e) {

                }
            }

            @Override
            public void OnItemClicked(final int prePos) {
               /* myViewHolder.rvCheckBox.post(new Runnable() {
                    @Override
                    public void run() {
                        *//**
                 ** Put Your Code here, exemple:
                 **//*
                        notifyItemChanged(prePos);
                    }
                });*/
            }
        });

    }

    @Override
    public final int getItemViewType(int position) {
        return mList.get(position).getIntType();
    }

    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup paramViewGroup, int viewType) {
       /* switch (viewType) {
            case VIEW_TYPE_SPINNER:
                myViewHolder = new MyViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.raw_spinner, paramViewGroup, false));
            case VIEW_TYPE_NUMBER:
                myViewHolder = new MyViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.raw_number, paramViewGroup, false));
            case VIEW_TYPE_CHECKBOX:
                myViewHolder = new MyViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.raw_row_checkbox, paramViewGroup, false));
            case VIEW_TYPE_IMAGE:
                myViewHolder = new MyViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.raw_row_images, paramViewGroup, false));
            case VIEW_TYPE_DATE:
                myViewHolder = new MyViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.raw_date, paramViewGroup, false));
            default:
                myViewHolder = new MyViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.raw_number, paramViewGroup, false));

        }*/
        MyViewHolder myViewHolder;
        switch (viewType) {
            case VIEW_TYPE_SPINNER:
                myViewHolder = new MyViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.raw_spinner, paramViewGroup, false));
                holders.add(myViewHolder);
                break;
            case VIEW_TYPE_NUMBER:
                myViewHolder = new MyViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.raw_number, paramViewGroup, false));
                holders.add(myViewHolder);
                break;
            case VIEW_TYPE_CHECKBOX:
                myViewHolder = new MyViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.raw_row_checkbox, paramViewGroup, false));
                holders.add(myViewHolder);
                break;
            case VIEW_TYPE_IMAGE:
                myViewHolder = new MyViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.raw_row_images, paramViewGroup, false));
                holders.add(myViewHolder);
                break;
            case VIEW_TYPE_DATE:
                myViewHolder = new MyViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.raw_date, paramViewGroup, false));
                holders.add(myViewHolder);
                break;
            default:
                myViewHolder = new MyViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.raw_number, paramViewGroup, false));

        }
        return myViewHolder;
    }

    @Override
    public void pass_position(int position, String view_type) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llMain;

        private RecyclerView rvCheckBox;
        private Spinner sp;
        private AppCompatEditText et;
        private LinearLayout linear_et;
        private CheckBoxAdapter mAdapter;
        private RelativeLayout rdate;
        private TextView date;

        //Image
        private ConstraintLayout clImg1, clImg2;
        private AppCompatImageView img_photo1, img_photo2, ivDelete1, ivDelete2, ivEdit1, ivEdit2, ivAdd1, ivAdd2;

        public MyViewHolder(View param1View) {
            super(param1View);
            this.rvCheckBox = param1View.findViewById(R.id.rvCheckBox);
            this.sp = param1View.findViewById(R.id.sp);
            this.et = param1View.findViewById(R.id.et);
            this.rdate = param1View.findViewById(R.id.rdate);
            this.date = param1View.findViewById(R.id.date);
            this.linear_et = param1View.findViewById(R.id.linear_et);

            //Image
            this.img_photo1 = param1View.findViewById(R.id.img_photo1);
            this.img_photo2 = param1View.findViewById(R.id.img_photo2);
            this.ivDelete1 = param1View.findViewById(R.id.ivDelete1);
            this.ivDelete2 = param1View.findViewById(R.id.ivDelete2);
            this.ivEdit1 = param1View.findViewById(R.id.ivEdit1);
            this.ivEdit2 = param1View.findViewById(R.id.ivEdit2);
            this.ivAdd1 = param1View.findViewById(R.id.ivAdd1);
            this.ivAdd2 = param1View.findViewById(R.id.ivAdd2);
        }
    }

    public void setOnItemClickedListener(OnItemClickedListener paramOnItemClickedListener) {
        this.onItemClickedListener = paramOnItemClickedListener;
    }

    public static interface OnItemClickedListener {
        void OnItemClicked(int pos, rowCheckBoxModel model);

        void OnItemClicked(int pos, String decimal);

        void OnItemClicked(int pos, int spinnerPos);

        void OnItemClicked(int pos, int ParentPos, int type); // 1=add, 2=edit, 3=delete
//        void OnItemClicked(int prePos);
    }

    public void setOnImageClickedListener(OnImageClickedListener onImageClickedListener) {
        this.onImageClickedListener = onImageClickedListener;
    }

    public static interface OnImageClickedListener {
        void OnItemClicked(int pos, int ParentPos, int type, int img_pos); // 1=add, 2=edit, 3=delete
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