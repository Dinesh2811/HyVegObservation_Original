package com.example.manoj.hyveg_observation.adapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.manoj.hyveg_observation.R;
import com.example.manoj.hyveg_observation.fragments.observation_new;
import com.example.manoj.hyveg_observation.list.ListModel;
import com.example.manoj.hyveg_observation.list.rowCheckBoxModel;
import com.example.manoj.hyveg_observation.pass_postion_onclick;
import com.example.manoj.hyveg_observation.pass_postion_tofrag;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class FormAdapterDynamic extends RecyclerView.Adapter<FormAdapterDynamic.MyViewHolder> implements pass_postion_onclick,pass_postion_tofrag{
//    public static final int TOTAL_SPINNER = 10;
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_ITEM = 2;
    private Activity mActivity;
    private observation_new observe;

    Context mCtx;

    private ArrayList<ListModel> mList = new ArrayList();

    private OnItemClickedListener onItemClickedListener;
    private OnImageClickedListener onImageClickedListener;

    private DatePickerDialog datePickerDialog;
    private DecimalFormat dcf = new DecimalFormat("00");
//    private  MyViewHolder myViewHolder;
    private ArrayList<MyViewHolder> holders = new ArrayList<>();
    private ArrayList<String> mList_heading = new ArrayList();

    private pass_postion_tofrag Interface_pass_tofrag;
    private pass_postion_onclick Interface_pass_position;

    public static int position ;


    int prePos = -1;
//    int spPos = 0;

    public FormAdapterDynamic(ArrayList<ListModel> mList,ArrayList<String> mList_heading, Activity mActivity,pass_postion_tofrag pass_postion_tofrag,pass_postion_onclick pass_postion_onclick) {
        this.mList = mList;
        this.mActivity = mActivity;
        this.mList_heading = mList_heading;
        this.Interface_pass_tofrag = pass_postion_tofrag;
        this.Interface_pass_position = pass_postion_onclick;


    }

    @Override
    public int getItemCount() {
//        return 10;
        return this.mList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
//        if (i == 0){
//        myViewHolder.setIsRecyclable(false);
           Log.e("values-----infrom2", String.valueOf(mList.get(i).getMinVal()));

//           if (mList.get(i).getReq().equalsIgnoreCase("1")){
//               myViewHolder.tvName.setText(mList.get(i).getFname() + "*");
//           } else {
//               myViewHolder.tvName.setText(mList.get(i).getFname());
//           }
        myViewHolder.tvName.setVisibility(View.GONE);
//        } else {
            if (mList.get(i).getIntType() == 1) {
                myViewHolder.llEditText.setVisibility(View.GONE);
                myViewHolder.llSpinner.setVisibility(View.VISIBLE);
                myViewHolder.llCheckBox.setVisibility(View.GONE);
                myViewHolder.llImages.setVisibility(View.GONE);
                myViewHolder.llDate.setVisibility(View.GONE);
                setUpSpinnerRecyclerView(myViewHolder);
            } else if (mList.get(i).getIntType() == 2) {
                myViewHolder.llEditText.setVisibility(View.VISIBLE);
                myViewHolder.llSpinner.setVisibility(View.GONE);
                myViewHolder.llCheckBox.setVisibility(View.GONE);
                myViewHolder.llImages.setVisibility(View.GONE);
                myViewHolder.llDate.setVisibility(View.GONE);
                setUpEditTextRecyclerView(myViewHolder);
            } else if (mList.get(i).getIntType() == 3) {
                myViewHolder.llEditText.setVisibility(View.GONE);
                myViewHolder.llSpinner.setVisibility(View.GONE);
                myViewHolder.llCheckBox.setVisibility(View.VISIBLE);
                myViewHolder.llImages.setVisibility(View.GONE);
                myViewHolder.llDate.setVisibility(View.GONE);
                setUpCheckBoxRecyclerView(myViewHolder);
            }
            else if(mList.get(i).getIntType() == 4){
                myViewHolder.llEditText.setVisibility(View.GONE);
                myViewHolder.llSpinner.setVisibility(View.GONE);
                myViewHolder.llCheckBox.setVisibility(View.GONE);
                myViewHolder.llImages.setVisibility(View.VISIBLE);
                myViewHolder.llDate.setVisibility(View.GONE);
                setUpImageRecyclerView(myViewHolder);
            }
            else if(mList.get(i).getIntType() == 5){
                myViewHolder.llEditText.setVisibility(View.GONE);
                myViewHolder.llSpinner.setVisibility(View.GONE);
                myViewHolder.llCheckBox.setVisibility(View.GONE);
                myViewHolder.llImages.setVisibility(View.GONE);
                myViewHolder.llDate.setVisibility(View.VISIBLE);
                setUpDateRecyclerView(myViewHolder);
            }
        if(mList.get(i).getFname().length()>50)
        {
            Log.d("values----inform","--------1------------"+mList.get(i).getFname().length());

            ViewGroup.LayoutParams params=  myViewHolder.linearlayout_height.getLayoutParams();
            double heighttoset=mList.get(i).getFname().length()/50;
            int heighttosetting= (int) (heighttoset*250);
            Log.d("values----inform","--------2------------"+heighttoset);
            Log.d("values----inform","--------3------------"+heighttosetting);

            params.height=heighttosetting;
            myViewHolder.linearlayout_height.setLayoutParams(params);
        }


    }



   /* @Override
    public final int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }
*/
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup paramViewGroup, int viewType) {
//        if (viewType == VIEW_TYPE_HEADER) {
//            return new MyViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.raw_form_text, paramViewGroup, false));
//        } else {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.raw_form_dynamic, paramViewGroup, false));
        holders.add(myViewHolder);
        return myViewHolder;
    }

    public void setEditTextValidation(int pos, int headingPos, String msg){
        if (holders.get(pos) != null){
            holders.get(pos).mNAdapter.setEditTextValidation(pos, headingPos, msg);
        }
    }

    private void setUpSpinnerRecyclerView(final MyViewHolder myViewHolder) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false);
        myViewHolder.rvRowSpinner.setLayoutManager(mLayoutManager);
        myViewHolder.mSAdapter = new RowCheckBoxAdapter(mList.get(myViewHolder.getAdapterPosition()).getSpinnerList(), mActivity, this);
        myViewHolder.rvRowSpinner.setAdapter(myViewHolder.mSAdapter);
        myViewHolder.mSAdapter.setOnItemClickedListener(new RowCheckBoxAdapter.OnItemClickedListener() {
            @Override
            public void OnItemClicked(int param1Int, rowCheckBoxModel model) {
                try
                {
                    mList.get(myViewHolder.getAdapterPosition()).getSpinnerList().set(param1Int, model);
                    mList.get(myViewHolder.getAdapterPosition()).setSelectedChoice(model.getSelectedChoice(), param1Int);
                    Log.e("values----spin", String.valueOf(param1Int));

//                    notifyItemChanged(myViewHolder.getAdapterPosition());
                }
                catch (Exception e)
                {

                }
            }

            @Override
            public void OnItemClicked(int pos, String decimal) {

            }

            @Override
            public void OnItemClicked(int pos, int spinnerPos) {
//                mList.get(myViewHolder.getAdapterPosition()).getSpinnerList().set(pos);
                mList.get(myViewHolder.getAdapterPosition()).setSelectedChoice(spinnerPos, pos);

                String selected = mList.get(myViewHolder.getAdapterPosition()).getChoice_desc()[spinnerPos];
                mList.get(myViewHolder.getAdapterPosition()).setSelected_choice_desc(pos, selected);

                Log.e("values---spin1", String.valueOf(pos));
                observation_new.from_pass = "adapter";

//                Interface_pass_position.pass_position(pos,"spin");

               /* mList.get(myViewHolder.getAdapterPosition()).setSelected_choice_desc(mList.get(myViewHolder.getAdapterPosition()).getChoice_desc() [
                        mList.get(myViewHolder.getAdapterPosition()).getSelectedChoice(spinnerPos)]);*/
            }

            @Override
            public void OnItemClicked(int pos, int ParentPos, int type) {

            }
        });

    }

    private void setUpEditTextRecyclerView(final MyViewHolder myViewHolder) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false);
        myViewHolder.rvRowEditText.setLayoutManager(mLayoutManager);
        myViewHolder.mNAdapter = new RowCheckBoxAdapter(mList.get(myViewHolder.getAdapterPosition()).getEditTextList(), mActivity,this);
        myViewHolder.rvRowEditText.setAdapter(myViewHolder.mNAdapter);
        myViewHolder.mNAdapter.setOnItemClickedListener(new RowCheckBoxAdapter.OnItemClickedListener() {
            @Override
            public void OnItemClicked(int param1Int, rowCheckBoxModel model) {
                try {
                    mList.get(myViewHolder.getAdapterPosition()).getEditTextList().set(param1Int, model);
                    Log.e("values-----edit", String.valueOf(param1Int));
//                    notifyItemChanged(myViewHolder.getAdapterPosition());
                } catch (Exception e) {

                }
            }

            @Override
            public void OnItemClicked(int pos, String decimal) {
                mList.get(myViewHolder.getAdapterPosition()).setSelectedDecimal(decimal, pos);

                position = myViewHolder.getAdapterPosition();

//                Interface_pass_position.pass_position(pos,"edit");
                Log.d("intefaces-----adapter", String.valueOf(myViewHolder.getAdapterPosition()));
                observation_new.from_pass = "adapter";
            }

            @Override
            public void OnItemClicked(int pos, int spinnerPos) {

            }

            @Override
            public void OnItemClicked(int pos, int ParentPos, int type) {

            }
        });

    }

    private void setUpDateRecyclerView(final MyViewHolder myViewHolder) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false);
        myViewHolder.rvRowDate.setLayoutManager(mLayoutManager);
        myViewHolder.mDAdapter = new RowCheckBoxAdapter(mList.get(myViewHolder.getAdapterPosition()).getEditTextList(), mActivity,this);
        myViewHolder.rvRowDate.setAdapter(myViewHolder.mDAdapter);
        myViewHolder.mDAdapter.setOnItemClickedListener(new RowCheckBoxAdapter.OnItemClickedListener() {
            @Override
            public void OnItemClicked(int param1Int, rowCheckBoxModel model) {
                try {
                    mList.get(myViewHolder.getAdapterPosition()).getEditTextList().set(param1Int, model);
//                    notifyItemChanged(myViewHolder.getAdapterPosition());
                } catch (Exception e) {

                }
            }

            @Override
            public void OnItemClicked(int pos, String decimal) {
                mList.get(myViewHolder.getAdapterPosition()).setSelectedDecimal(decimal, pos);
                Log.e("values---date1", String.valueOf(pos));
//                Interface_pass_position.pass_position(pos,"date");
                observation_new.from_pass = "adapter";
            }

            @Override
            public void OnItemClicked(int pos, int spinnerPos) {

            }

            @Override
            public void OnItemClicked(int pos, int ParentPos, int type) {

            }
        });
    }


    private void setUpCheckBoxRecyclerView(final MyViewHolder myViewHolder) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false);
        myViewHolder.rvRowCheckBox.setLayoutManager(mLayoutManager);
        myViewHolder.mCAdapter = new RowCheckBoxAdapter(mList.get(myViewHolder.getAdapterPosition()).getCheckBoxList(), mActivity,this);
        myViewHolder.rvRowCheckBox.setAdapter(myViewHolder.mCAdapter);
        myViewHolder.mCAdapter.setOnItemClickedListener(new RowCheckBoxAdapter.OnItemClickedListener() {
            @Override
            public void OnItemClicked(int param1Int, rowCheckBoxModel model) {
                try {
                    mList.get(myViewHolder.getAdapterPosition()).getmCheckBoxList().set(param1Int, model);
                    mList.get(myViewHolder.getAdapterPosition()).setSelectedChoice(model.getSelectedChoice(), param1Int);
                    String selected = mList.get(myViewHolder.getAdapterPosition()).getChoice_desc()[model.getSelectedChoice()];
                    mList.get(myViewHolder.getAdapterPosition()).setSelected_choice_desc(param1Int, selected);

//                    notifyItemChanged(myViewHolder.getAdapterPosition());
                } catch (Exception e) {

                }
            }

            @Override
            public void OnItemClicked(int pos, String decimal) {

                Log.e("values---check", String.valueOf(pos));
//                Interface_pass_position.pass_position(pos,"check");
                observation_new.from_pass = "adapter";

            }

            @Override
            public void OnItemClicked(int pos, int spinnerPos) {
//                Interface_pass_position.pass_position(pos,"check");
                Log.e("values---check", String.valueOf(pos));
            }

            @Override
            public void OnItemClicked(int pos, int ParentPos, int type) {

//                Interface_pass_position.pass_position(pos,"check");
                Log.e("values---check", String.valueOf(pos));

            }
        });
    }



    private void setUpImageRecyclerView(final MyViewHolder myViewHolder) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false);
        myViewHolder.rvRowImage.setLayoutManager(mLayoutManager);
        myViewHolder.mIAdapter = new RowCheckBoxAdapter(mList.get(myViewHolder.getAdapterPosition()).getSpinnerList(), mActivity, myViewHolder.getAdapterPosition());
        myViewHolder.rvRowImage.setAdapter(myViewHolder.mIAdapter);
        myViewHolder.mIAdapter.setOnItemClickedListener(new RowCheckBoxAdapter.OnItemClickedListener() {
            @Override
            public void OnItemClicked(int param1Int, rowCheckBoxModel model) {
                try {
                    mList.get(myViewHolder.getAdapterPosition()).getSpinnerList().set(param1Int, model);
//                    mList.get(myViewHolder.getAdapterPosition()).setSelectedChoice(model.getSelectedChoice(), param1Int);
                    String selected = mList.get(myViewHolder.getAdapterPosition()).getChoice_desc()[0];
                    mList.get(myViewHolder.getAdapterPosition()).setSelected_choice_desc(0, selected);

                    String selected2 = mList.get(myViewHolder.getAdapterPosition()).getChoice_desc()[1];
                    mList.get(myViewHolder.getAdapterPosition()).setSelected_choice_desc(1, selected2);

//                    notifyItemChanged(myViewHolder.getAdapterPosition());
                } catch (Exception e) {

                }
            }

            @Override
            public void OnItemClicked(int pos, String decimal) {

                Log.e("values----images", String.valueOf(pos));
//                Interface_pass_position.pass_position(pos,"image");
                observation_new.from_pass = "adapter";

            }

            @Override
            public void OnItemClicked(int pos, int spinnerPos) {

            }

            @Override
            public void OnItemClicked(int pos, int ParentPos, int type) {

            }
        });

        myViewHolder.mIAdapter.setOnImageClickedListener(new RowCheckBoxAdapter.OnImageClickedListener() {
            @Override
            public void OnItemClicked(int pos, int ParentPos, int type, int img_pos) {
                onImageClickedListener.OnImageClicked(pos, ParentPos, type, img_pos);
            }
        });
    }

    @Override
    public void pass_position(int position, String view_type) {

        int pass_pos = position;
       String pass_string = view_type;


        Log.d("intefaces---top--adap", String.valueOf(pass_pos));
        Log.d("intefaces----ad_type", view_type);

        Interface_pass_tofrag.pass_po_tofrg(position,view_type);


    }

    @Override
    public void pass_po_tofrg(int position, String view_type) {

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
//        private LinearLayout llMain;

        private AppCompatTextView tvName;

        private RecyclerView rvRowSpinner;
        private RecyclerView rvRowEditText;
        private RecyclerView rvRowCheckBox;
        private RecyclerView rvRowDate;
        private RecyclerView rvRowImage;
        private RowCheckBoxAdapter mSAdapter;
        private RowCheckBoxAdapter mNAdapter;
        private RowCheckBoxAdapter mCAdapter;
        private RowCheckBoxAdapter mDAdapter;
        private RowCheckBoxAdapter mIAdapter;

        private LinearLayout llEditText;
        private LinearLayout llSpinner;
        private LinearLayout llCheckBox;
        private LinearLayout llDate;
        private LinearLayout llImages;
        private LinearLayout linearlayout_height;

        public MyViewHolder(View param1View) {
            super(param1View);
            this.tvName = param1View.findViewById(R.id.tvName);
            this.rvRowSpinner = param1View.findViewById(R.id.rvRowSpinner);
            this.rvRowEditText = param1View.findViewById(R.id.rvRowEditText);
            this.rvRowCheckBox = param1View.findViewById(R.id.rvRowCheckBox);
            this.rvRowDate = param1View.findViewById(R.id.rvRowDate);
            this.rvRowImage = param1View.findViewById(R.id.rvRowImage);

            this.llEditText = param1View.findViewById(R.id.llEditText);
            this.llSpinner = param1View.findViewById(R.id.llSpinner);
            this.llCheckBox = param1View.findViewById(R.id.llCheckBox);
            this.llDate = param1View.findViewById(R.id.llDate);
            this.llImages = param1View.findViewById(R.id.llImages);
            this.linearlayout_height = param1View.findViewById(R.id.linearlayout_height);
        }
    }

    public void setOnItemClickedListener(OnItemClickedListener paramOnItemClickedListener) {
        this.onItemClickedListener = paramOnItemClickedListener;
    }

    public void setOnImageClickedListener(OnImageClickedListener onImageClickedListener) {
        this.onImageClickedListener = onImageClickedListener;
    }

    public static interface OnItemClickedListener {
        void OnItemClicked(int param1Int);
    }
    public static interface OnImageClickedListener {
        void OnImageClicked(int pos, int parentPos, int type, int img_pos);
    }
}