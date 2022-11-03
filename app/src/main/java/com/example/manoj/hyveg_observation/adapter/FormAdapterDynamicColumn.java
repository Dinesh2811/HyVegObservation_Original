package com.example.manoj.hyveg_observation.adapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class FormAdapterDynamicColumn extends RecyclerView.Adapter<FormAdapterDynamicColumn.MyViewHolder>{
    //    public static final int TOTAL_SPINNER = 10;
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_ITEM = 2;
    private Activity mActivity;
    private observation_new observe;

    private Context mCtx;

    private ArrayList<ListModel> mList = new ArrayList();

    private OnItemClickedListener onItemClickedListener;
    private OnImageClickedListener onImageClickedListener;

    private DatePickerDialog datePickerDialog;
    private DecimalFormat dcf = new DecimalFormat("00");
    //    private  MyViewHolder myViewHolder;
    private ArrayList<MyViewHolder> holders = new ArrayList<>();

    int prePos = -1;
//    int spPos = 0;

    public FormAdapterDynamicColumn(ArrayList<ListModel> mList, Activity mActivity) {
        this.mList = mList;
        this.mActivity = mActivity;

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
//
//        if (i == 0) {
//            myViewHolder.hvcodes.setVisibility(View.VISIBLE);
//        } else {
//            myViewHolder.hvcodes.setVisibility(View.GONE);
//
//        }

        if (mList.get(i).getReq().equalsIgnoreCase("1")){
            myViewHolder.tvName.setText(mList.get(i).getFname() + "*");
        } else {
            myViewHolder.tvName.setText(mList.get(i).getFname());
        }

        if(mList.get(i).getFname().length()>50)
        {
            Log.d("values----inform","--------1------------"+mList.get(i).getFname().length());

            ViewGroup.LayoutParams params= myViewHolder.linearlayout_main.getLayoutParams();
            double heighttoset=mList.get(i).getFname().length()/50;
            int heighttosetting= (int) (heighttoset*250);
            Log.d("values----inform","--------2------------"+heighttoset);
            Log.d("values----inform","--------3------------"+heighttosetting);

            params.height=heighttosetting;
            myViewHolder.linearlayout_main.setLayoutParams(params);
        }
//        if(mList.get(i).getFname().length()>50)
//        {

//        }


//        } else {
//        if (mList.get(i).getIntType() == 1) {
//            myViewHolder.llEditText.setVisibility(View.GONE);
//            myViewHolder.llSpinner.setVisibility(View.VISIBLE);
//            myViewHolder.llCheckBox.setVisibility(View.GONE);
//            myViewHolder.llImages.setVisibility(View.GONE);
//            myViewHolder.llDate.setVisibility(View.GONE);
//            setUpSpinnerRecyclerView(myViewHolder);
//        } else if (mList.get(i).getIntType() == 2) {
//            myViewHolder.llEditText.setVisibility(View.VISIBLE);
//            myViewHolder.llSpinner.setVisibility(View.GONE);
//            myViewHolder.llCheckBox.setVisibility(View.GONE);
//            myViewHolder.llImages.setVisibility(View.GONE);
//            myViewHolder.llDate.setVisibility(View.GONE);
//            setUpEditTextRecyclerView(myViewHolder);
//        } else if (mList.get(i).getIntType() == 3) {
//            myViewHolder.llEditText.setVisibility(View.GONE);
//            myViewHolder.llSpinner.setVisibility(View.GONE);
//            myViewHolder.llCheckBox.setVisibility(View.VISIBLE);
//            myViewHolder.llImages.setVisibility(View.GONE);
//            myViewHolder.llDate.setVisibility(View.GONE);
//            setUpCheckBoxRecyclerView(myViewHolder);
//        }
//        else if(mList.get(i).getIntType() == 4){
//            myViewHolder.llEditText.setVisibility(View.GONE);
//            myViewHolder.llSpinner.setVisibility(View.GONE);
//            myViewHolder.llCheckBox.setVisibility(View.GONE);
//            myViewHolder.llImages.setVisibility(View.VISIBLE);
//            myViewHolder.llDate.setVisibility(View.GONE);
//            setUpImageRecyclerView(myViewHolder);
//        }
//        else if(mList.get(i).getIntType() == 5){
//            myViewHolder.llEditText.setVisibility(View.GONE);
//            myViewHolder.llSpinner.setVisibility(View.GONE);
//            myViewHolder.llCheckBox.setVisibility(View.GONE);
//            myViewHolder.llImages.setVisibility(View.GONE);
//            myViewHolder.llDate.setVisibility(View.VISIBLE);
//            setUpDateRecyclerView(myViewHolder);
//        }
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
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.raw_form_dynamic_column, paramViewGroup, false));
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
        myViewHolder.mSAdapter = new RowCheckBoxAdapter(mList.get(myViewHolder.getAdapterPosition()).getSpinnerList(), mActivity,(pass_postion_onclick) mCtx);
        myViewHolder.rvRowSpinner.setAdapter(myViewHolder.mSAdapter);
        myViewHolder.mSAdapter.setOnItemClickedListener(new RowCheckBoxAdapter.OnItemClickedListener() {
            @Override
            public void OnItemClicked(int param1Int, rowCheckBoxModel model) {
                try {
                    mList.get(myViewHolder.getAdapterPosition()).getSpinnerList().set(param1Int, model);
                    mList.get(myViewHolder.getAdapterPosition()).setSelectedChoice(model.getSelectedChoice(), param1Int);
//                    notifyItemChanged(myViewHolder.getAdapterPosition());
                } catch (Exception e) {

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
        myViewHolder.mNAdapter = new RowCheckBoxAdapter(mList.get(myViewHolder.getAdapterPosition()).getEditTextList(), mActivity,(pass_postion_onclick) mCtx);
        myViewHolder.rvRowEditText.setAdapter(myViewHolder.mNAdapter);
        myViewHolder.mNAdapter.setOnItemClickedListener(new RowCheckBoxAdapter.OnItemClickedListener() {
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
        myViewHolder.mDAdapter = new RowCheckBoxAdapter(mList.get(myViewHolder.getAdapterPosition()).getEditTextList(), mActivity,(pass_postion_onclick) mCtx);
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
        myViewHolder.mCAdapter = new RowCheckBoxAdapter(mList.get(myViewHolder.getAdapterPosition()).getCheckBoxList(), mActivity,(pass_postion_onclick) mCtx);
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

            }

            @Override
            public void OnItemClicked(int pos, int spinnerPos) {

            }

            @Override
            public void OnItemClicked(int pos, int ParentPos, int type) {

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


    public class MyViewHolder extends RecyclerView.ViewHolder {
//        private LinearLayout llMain;

        private AppCompatTextView tvName;
        private AppCompatTextView hvcodes;

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
        private LinearLayout linearlayout_main;

        public MyViewHolder(View param1View) {
            super(param1View);
            this.tvName = param1View.findViewById(R.id.tvName);
            this.hvcodes = param1View.findViewById(R.id.hvcodes);
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
            this.linearlayout_main = param1View.findViewById(R.id.linearlayout_main);
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