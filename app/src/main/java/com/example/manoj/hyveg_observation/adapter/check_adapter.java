package com.example.manoj.hyveg_observation.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.manoj.hyveg_observation.R;
import com.example.manoj.hyveg_observation.activity.Sessionsave;
import com.example.manoj.hyveg_observation.list.ob_list;

import java.util.ArrayList;

class check_adapter extends RecyclerView.Adapter<check_adapter.ViewHolder> {

    private Context mCtx;
    String[] choice_display ,choice_value ,sel_choice;
    ArrayList<String> sel_arr;
    String multi_sel ,from = "";
    ob_list ob_list;
    Sessionsave sessionsave;
    int pos = 0;

    check_adapter(Context mCtx, ob_list ob_list ,int pos ,String from) {
        this.mCtx = mCtx;
        this.ob_list = ob_list;
        choice_display = ob_list.setChoice_desc();
        choice_value = ob_list.setChoice_val();
        this.multi_sel = ob_list.set_multi();
        this.pos = pos;
        this.from = from;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_check, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        sel_choice = new String[choice_display.length];
        sel_arr = new ArrayList<>();
        sessionsave = new Sessionsave(mCtx);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.check_box.setText(choice_display[position]);

        for (String ignored : choice_display) {
            if (multi_sel.equals("0")) {
                if (choice_value[position].equals(ob_list.setValue())) {
                    holder.check_box.setChecked(true);
                    sel_arr.add(position + "");
                } else {
                    holder.check_box.setChecked(false);
                }
            }else {
                if (!ob_list.setValue().equals("")) {
                    String[] sel = ob_list.setValue().split(",");
                    for (String aSel : sel) {
                        if (aSel.trim().contains(choice_value[position])) {
                            holder.check_box.setChecked(true);
                            sel_arr.add(choice_value[position]);
                        }
//                        else {
//                            holder.check_box.setChecked(false);
//                        }
                    }
                }
            }
        }

        for (int i = 0 ; i < sel_choice.length ; i++) {
            sel_choice[i] = "0";
        }

        holder.check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (multi_sel.equals("1")){
                    if (b){
                        sel_choice[position] = "1";
                        sel_arr.add(choice_value[position]);
                        switch (from) {
                            case "m_list":
                                sessionsave.save_one("master_" + pos, String.valueOf(sel_arr));
                                sessionsave.save_one("master_key_" + pos, ob_list.setKey());
                                break;
                            case "g_list":
                                sessionsave.save_two("general_" + pos, String.valueOf(sel_arr));
                                sessionsave.save_two("general_key_" + pos, ob_list.setKey());

                                break;
                            case "o_list":
                                sessionsave.save_three("observe_" + pos, String.valueOf(sel_arr));
                                sessionsave.save_three("observe_key_" + pos, ob_list.setKey());
                                break;
                            case "u_list":
                                sessionsave.save_one("usp_" + pos, String.valueOf(sel_arr));
                                sessionsave.save_one("usp_key_" + pos, ob_list.setKey());
                                break;
                        }
                    }else {
                        sel_choice[position] = "0";
                        sel_arr.remove(position);
                    }
                }else {
                    if (b) {
                        if (sel_arr.size() == 0){
                            sel_arr.add(position+"");
                            ob_list.getValue(choice_value[position]);
                            switch (from) {
                                case "m_list":
                                    sessionsave.save_one("master_" + pos, choice_value[position]);
                                    sessionsave.save_one("master_key_" + pos, ob_list.setKey());
                                    break;
                                case "g_list":
                                    sessionsave.save_two("general_" + pos, choice_value[position]);
                                    sessionsave.save_two("general_key_" + pos, ob_list.setKey());
                                    break;
                                case "o_list":
                                    sessionsave.save_three("observe_" + pos, choice_value[position]);
                                    sessionsave.save_three("observe_key_" + pos, ob_list.setKey());
                                    break;
                                case "u_list":
                                    sessionsave.save_one("usp_" + pos, choice_value[position]);
                                    sessionsave.save_one("usp_key_" + pos, ob_list.setKey());
                                    break;
                            }
                        }else {
                            holder.check_box.setChecked(false);
                        }
                    }else {
                        sel_arr.remove(position+"");
                    }
//                    for (int i = 0 ; i < sel_choice.length ; i++){
//                        if (sel_choice[i].equals("1")){
//                            click = "1";
//                        }else {
//                            holder.check_box.setChecked(true);
//                            click = "0";
//                        }
//                    }
//                    if (click.equals("1")){
//                        if (b) {
//                            holder.check_box.setChecked(false);
//                        }
//                    }
                }
            }
        });

    }


    @Override
    public int getItemViewType(final int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return choice_value.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox check_box;

        ViewHolder(final View itemView) {
            super(itemView);

           check_box = itemView.findViewById(R.id.check_box);

//           check_box.setOnClickListener(new View.OnClickListener() {
//               @Override
//               public void onClick(View view) {
//                   click = "1";
//               }
//           });
//
//           check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//               @Override
//               public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                   if (b) {
//                       click = "1";
//                   }
//               }
//           });
        }
    }
}
