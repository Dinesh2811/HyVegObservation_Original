package com.example.manoj.hyveg_observation.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;


import com.example.manoj.hyveg_observation.R;

import java.util.ArrayList;

public class HeadingAdapter extends RecyclerView.Adapter<HeadingAdapter.MyViewHolder> {
    private Activity mActivity;

    private ArrayList<String> mList = new ArrayList();

//    private OnItemClickedListener onItemClickedListener;

    int prePos = -1;

    public HeadingAdapter(ArrayList<String> mList, Activity mActivity) {
        this.mList = mList;
        this.mActivity = mActivity;
    }

    public int getItemCount() {
//        return 10;
        return this.mList.size();
    }

    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        myViewHolder.tvHeading.setText(mList.get(i));

    }

    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup paramViewGroup, int paramInt) {
        return new MyViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.row_heading, paramViewGroup, false));
    }

    /*public void setOnItemClickedListener(OnItemClickedListener paramOnItemClickedListener) {
        this.onItemClickedListener = paramOnItemClickedListener;
    }*/

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llMain;

        private AppCompatTextView tvHeading;

        public MyViewHolder(View param1View) {
            super(param1View);
            this.tvHeading = param1View.findViewById(R.id.tvHeading);
        }
    }

   /* public static interface OnItemClickedListener {
        void OnItemClicked(int pos, String data, boolean isChecked);
    }*/
}