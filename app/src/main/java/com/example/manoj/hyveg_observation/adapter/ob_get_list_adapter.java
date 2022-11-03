package com.example.manoj.hyveg_observation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.manoj.hyveg_observation.R;
import com.example.manoj.hyveg_observation.activity.Observation_view_list;
import com.example.manoj.hyveg_observation.list.ob_get_list;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ob_get_list_adapter extends RecyclerView.Adapter<ob_get_list_adapter.ViewHolder>{

    private Context mCtx;
    private List<ob_get_list> ob_get_lists;
    String mode;

    public ob_get_list_adapter(Context mCtx, List<ob_get_list> ob_get_lists,String mode) {
        super();
        this.ob_get_lists = ob_get_lists;
        this.mCtx = mCtx;
        this.mode = mode;
    }

    @NonNull
    @Override
    public ob_get_list_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_list, parent, false);
        ob_get_list_adapter.ViewHolder viewHolder = new ob_get_list_adapter.ViewHolder(v);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final ob_get_list_adapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final ob_get_list ob_get_list = ob_get_lists.get(position);

        holder.pos = position;
        holder.setIsRecyclable(false);


        holder.text_sl_no.setText((position+1)+"");
        try{
            holder.text_name.setText(ob_get_list.set_name());
        }catch (Exception e) {
            e.printStackTrace();

        }

        holder.text_id.setText(ob_get_list.set_id());
        holder.text_pd_number.setText(ob_get_list.set_pd_number());
        holder.text_grower_code.setText(ob_get_list.set_growercode());
        holder.text_ob_id.setText(ob_get_list.set_ob_id());
        if (mode.equals("offline")) {
            holder.text_date.setText(ob_get_list.set_date());
        } else {
            String date = null;
            try {
                JSONObject json = new JSONObject(ob_get_list.set_date());
                date = json.getString("date");
                holder.text_date.setText(date.substring(0, date.length() - 3));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return ob_get_lists.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView text_sl_no,text_name ,text_date ,text_id,text_ob_id, text_pd_number,text_grower_code;
        LinearLayout lin_;
        int pos;

        ViewHolder(View itemView) {
            super(itemView);
            text_sl_no = itemView.findViewById(R.id.text_sl_no);
            text_name = itemView.findViewById(R.id.text_crop_name);
            text_date = itemView.findViewById(R.id.text_date);
            text_id = itemView.findViewById(R.id.text_id);
            text_ob_id = itemView.findViewById(R.id.text_ob_id);
            text_pd_number = itemView.findViewById(R.id.text_pd_number);
            text_grower_code = itemView.findViewById(R.id.text_grower_code);

            lin_ = itemView.findViewById(R.id.linearLayout);

            if (mode.equals("offline")){
                text_date.setVisibility(View.VISIBLE);
                text_ob_id.setVisibility(View.GONE);
            }else {
                lin_.setOnClickListener(this);
                text_ob_id.setVisibility(View.VISIBLE);
                text_date.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.linearLayout){
                try {
                    Intent myIntent = new Intent(mCtx, Observation_view_list.class);
                    myIntent.putExtra("crop_name", text_name.getText().toString().trim());
                    myIntent.putExtra("crop_id", text_id.getText().toString().trim());
                    myIntent.putExtra("from", "edit");
                    mCtx.startActivity(myIntent);
                }catch (Exception e){
                    Intent myIntent = new Intent(mCtx, Observation_view_list.class);
                    myIntent.putExtra("crop_name", "");
                    myIntent.putExtra("crop_id", text_id.getText().toString().trim());
                    myIntent.putExtra("from", "edit");
                    mCtx.startActivity(myIntent);
                    e.printStackTrace();
                }
            }
        }
    }
}
