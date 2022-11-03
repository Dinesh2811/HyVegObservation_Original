package com.example.manoj.hyveg_observation.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manoj.hyveg_observation.R;
import com.example.manoj.hyveg_observation.activity.Sessionsave;
import com.example.manoj.hyveg_observation.list.CheckBoxModel;

import java.util.ArrayList;

public class CheckBoxAdapter extends RecyclerView.Adapter<CheckBoxAdapter.MyViewHolder> {
    private Context mActivity;

    private ArrayList<CheckBoxModel> mList = new ArrayList();
    private Sessionsave sessionsave;

    private OnItemClickedListener onItemClickedListener;
    private RecyclerView recyclerView;
    private  boolean isImage;

    int prePos = -1;

    public CheckBoxAdapter(ArrayList<CheckBoxModel> mList, Context mActivity) {
        this.mList = mList;
        this.mActivity = mActivity;
    }

    public CheckBoxAdapter(ArrayList<CheckBoxModel> mList, Context mActivity, RecyclerView recyclerView, boolean isImage) {
        this.mList = mList;
        this.mActivity = mActivity;
        this.recyclerView = recyclerView;
        this.isImage = isImage;
    }

    public int getItemCount() {
//        return 10;
        return this.mList.size();
    }

    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        sessionsave = new Sessionsave(mActivity);
        if (isImage){
            myViewHolder.cb.setVisibility(View.GONE);
            myViewHolder.rlImage.setVisibility(View.VISIBLE);
//            setUpImageView(myViewHolder);
        } else {
            myViewHolder.cb.setVisibility(View.VISIBLE);
            myViewHolder.rlImage.setVisibility(View.GONE);
            myViewHolder.cb.setText(mList.get(i).getData());
            if (mList.get(i).isChecked()) {
                myViewHolder.cb.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_checked, 0, 0, 0);
            } else {
                myViewHolder.cb.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_uncheck, 0, 0, 0);
            }
//        myViewHolder.cb.setChecked(mList.get(i).isChecked());
            myViewHolder.cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isChecked = mList.get(myViewHolder.getAdapterPosition()).isChecked();
                    mList.get(myViewHolder.getAdapterPosition()).setChecked(!isChecked);
                    notifyItemChanged(myViewHolder.getAdapterPosition());
                    if (prePos >= 0) {
                        mList.get(prePos).setChecked(false);
                        notifyItemChanged(prePos);
                    }
                    onItemClickedListener.OnItemClicked(prePos);
                    onItemClickedListener.OnItemClicked(myViewHolder.getAdapterPosition(), prePos,
                            mList.get(myViewHolder.getAdapterPosition()).getData(), !isChecked);
                    prePos = myViewHolder.getAdapterPosition();
                }
            });
        }

    }

//    private void setUpImageView(final MyViewHolder myViewHolder) {
//
//        ArrayList<String> img_names = new ArrayList<>();
//        ArrayList<String> images = new ArrayList<>();
//        myViewHolder.text_click_img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent myIntent = new Intent(mActivity, .class);
//                myIntent.putExtra("image_name", "gc_img_"+myViewHolder.getAdapterPosition());
//                myIntent.putExtra("position", myViewHolder.getAdapterPosition()+"");
//                myIntent.putExtra("from", "g_list");
//                mActivity.startActivity(myIntent);
//            }
//        });
//        myViewHolder.text_change_img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent myIntent = new Intent(mActivity, Pick_image.class);
//                myIntent.putExtra("image_name", "gc_img_"+myViewHolder.getAdapterPosition());
//                myIntent.putExtra("position", myViewHolder.getAdapterPosition()+"");
//                myIntent.putExtra("from", "g_list_edit");
//                mActivity.startActivity(myIntent);
//            }
//        });
//        if (myViewHolder.text_click_img.getText().toString().contains("images added")){
//            myViewHolder.img_photo.setVisibility(View.VISIBLE);
////                holder.img_view.setImageBitmap(StringToBitMap(ob_list.setValue()));
//            myViewHolder.text_click_img.setVisibility(View.VISIBLE);
//            myViewHolder.text_click_img.setText(sessionsave.get_image("general_len")+" images added");
//            myViewHolder.text_change_img.setVisibility(View.VISIBLE);
//            myViewHolder.text_click_img.setEnabled(false);
//        }else {
//            myViewHolder.text_change_img.setVisibility(View.GONE);
//            myViewHolder.text_click_img.setEnabled(true);
//        }
//
//        Log.e("values",sessionsave.get_image("general_len")+" images added");
//        Log.e("values-----img",sessionsave.get_image("general_" + 0));
//        mList.get(myViewHolder.getAdapterPosition()).setData(sessionsave.get_image("general_len")+" images added");
//        onItemClickedListener.OnItemClicked(myViewHolder.getAdapterPosition(), -1, sessionsave.get_image("general_len")+" images added", false);
//        Log.e("values","imagess");
//    }

    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup paramViewGroup, int paramInt) {
        return new MyViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.row_checkbox, paramViewGroup, false));
    }

    public void setOnItemClickedListener(OnItemClickedListener paramOnItemClickedListener) {
        this.onItemClickedListener = paramOnItemClickedListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llMain;

        private AppCompatTextView cb;
        private RelativeLayout rlImage;
        private ImageView img_photo;
        private TextView text_click_img;
        private TextView text_change_img;
//        private CheckBox cb;

        public MyViewHolder(View param1View) {
            super(param1View);
            this.cb = param1View.findViewById(R.id.cb);
            this.rlImage = param1View.findViewById(R.id.rlImage);
            this.text_click_img = param1View.findViewById(R.id.text_click_img);
            this.text_change_img = param1View.findViewById(R.id.text_change_img);
            this.img_photo = param1View.findViewById(R.id.img_photo);
        }
    }

    public static interface OnItemClickedListener {
        void OnItemClicked(int pos, int prePos, String data, boolean isChecked);
        void OnItemClicked(int prePos);
    }
}