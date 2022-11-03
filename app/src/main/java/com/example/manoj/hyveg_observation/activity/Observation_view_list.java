package com.example.manoj.hyveg_observation.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.manoj.hyveg_observation.Api;
import com.example.manoj.hyveg_observation.R;
import com.example.manoj.hyveg_observation.list.ob_list;
import com.example.manoj.hyveg_observation.services.alertDialog;
import com.example.manoj.hyveg_observation.volley.CustomVolleyRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.NORMAL;

public class Observation_view_list extends AppCompatActivity {

    ExpandableStickyListHeadersListView expandableStickyListHeadersListView;
    String crop_choose, from, crop_id, myJSON = "", TAG = "Observation_view_list" ;
    List<ob_list> observation_list;
    alertDialog alert;
    Sessionsave sessionsave;
    ImageLoader imageLoader;
    String[] images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_view_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        expandableStickyListHeadersListView = findViewById(R.id.e_list);

        alert = new alertDialog(this);
        sessionsave = new Sessionsave(this);
        observation_list = new ArrayList<>();
        crop_choose = getIntent().getStringExtra("crop_name");
        from = getIntent().getStringExtra("from");
        Log.e("values------",from);
        if (from != null &&from.equals("edit")) {
            crop_id = getIntent().getStringExtra("crop_id");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        get_observation_data();
    }

    private void get_observation_data() {
        if (alert.isNetworkAvailable()) {
            class GetDataJSON extends AsyncTask<String, Void, String> {

                @Override
                protected String doInBackground(String... params) {
                    HttpPost httppost = null;
                    DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                    if (!from.equals("crop_select")) {
                        try {
                            httppost = new HttpPost(Api.ob_data(URLEncoder.encode(crop_id, "UTF-8"), sessionsave.get_company()));
                            Log.e(TAG ,Api.ob_data(URLEncoder.encode(crop_id, "UTF-8"), sessionsave.get_company()));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    // Depends on your web service
                    httppost.setHeader("Content-type", "application/json");

                    InputStream inputStream = null;
                    String result = null;
                    try {
                        HttpResponse response = httpclient.execute(httppost);
                        HttpEntity entity = response.getEntity();

                        inputStream = entity.getContent();
                        // json is UTF-8 by default
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                        StringBuilder sb = new StringBuilder();

                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        result = sb.toString();
                    } catch (Exception e) {
                        // Oops
                    } finally {
                        try {
                            if (inputStream != null) inputStream.close();
                        } catch (Exception squish) {
                        }
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(String result) {
                    myJSON = result;
                    Log.e(TAG ,"result "+myJSON);
                    try {
                        observation_list = new ArrayList<>();
                        spilt_data();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            GetDataJSON g = new GetDataJSON();
            g.execute();
        }else {

        }
    }

    private void spilt_data() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);

            JSONObject other = new JSONObject(jsonObj.getString("other"));

            Iterator<String> itr = other.keys();
            while (itr.hasNext()) {
                ob_list ob1 = new ob_list();
                String key = itr.next();
                try {
                    switch (key) {
                        case "obno":
                            ob1.getName("OB Number");
                            ob1.getType("text");
                            ob1.get_head("other");
                            Log.e(TAG ,"obno "+String.valueOf(other.get(key)));
                            ob1.getValue(String.valueOf(other.get(key)));
                            observation_list.add(ob1);
                            break;
                        case "RegNum":
                            ob1.getName("Pd RegNo");
                            ob1.getType("text");
                            ob1.get_head("other");
                            Log.e(TAG ,"obno "+String.valueOf(other.get(key)));
                            ob1.getValue(String.valueOf(other.get(key)));
                            observation_list.add(ob1);
                            break;
                        case "growerCode":
                            ob1.getName("Grower Code");
                            ob1.getType("text");
                            ob1.get_head("other");
                            Log.e(TAG ,"obno "+String.valueOf(other.get(key)));
                            ob1.getValue(String.valueOf(other.get(key)));
                            observation_list.add(ob1);
                            break;
//                        case "ccode":
//                            ob1.getName("Crop Code");
//                            ob1.getType("text");
//                            ob1.get_head("other");
//                            ob1.getValue(String.valueOf(other.get(key)));
//                            observation_list.add(ob1);
//                            break;
//                        case "cname":
//                            ob1.getName("Crop Name");
//                            ob1.getType("text");
//                            ob1.get_head("other");
//                            ob1.getValue(String.valueOf(other.get(key)));
//                            observation_list.add(ob1);
//                            break;
//                        case "brcode":
//                            ob1.getName("Breeder Code");
//                            ob1.getType("text");
//                            ob1.get_head("other");
//                            ob1.getValue(String.valueOf(other.get(key)));
//                            observation_list.add(ob1);
//                            break;
//                        case "hvcode":
//                            ob1.getName("HV Code");
//                            ob1.getType("text");
//                            ob1.get_head("other");
//                            ob1.getValue(String.valueOf(other.get(key)));
//                            observation_list.add(ob1);
//                            break;
//                        case "variety":
//                            ob1.getName("Variety");
//                            ob1.getType("text");
//                            ob1.get_head("other");
//                            ob1.getValue(String.valueOf(other.get(key)));
//                            observation_list.add(ob1);
//                            break;
//                        case "stage":
//                            ob1.getName("Stage");
//                            ob1.getType("text");
//                            ob1.get_head("other");
//                            ob1.getValue(String.valueOf(other.get(key)));
//                            observation_list.add(ob1);
//                            break;
//                        case "state":
//                            ob1.getName("State");
//                            ob1.getType("text");
//                            ob1.get_head("other");
//                            ob1.getValue(String.valueOf(other.get(key)));
//                            observation_list.add(ob1);
//                            break;
//                        case "city":
//                            ob1.getName("City");
//                            ob1.getType("text");
//                            ob1.get_head("other");
//                            ob1.getValue(String.valueOf(other.get(key)));
//                            observation_list.add(ob1);
//                            break;
                    }
                } catch (JSONException e) {
                    // Something went wrong!
                    Log.e(TAG ,"val based error "+e.toString());
                }
            }
            JSONObject c = null;
            if(jsonObj.has("gc")) {
                JSONArray gc = jsonObj.getJSONArray("gc");

                for (int i = 0; i < gc.length(); i++) {
                    ob_list ob = new ob_list();
                    try {
                        c = gc.getJSONObject(i);
                        ob.getKey(c.getString("fkey"));
                        ob.getName(c.getString("fname"));
//                    ob.getReq(c.getString("req"));
                        String f_type = c.getString("ftype");
                        ob.getType(f_type);
                        try {
                            if (c.getString("valbased") != null) {

                                JSONObject val_based = new JSONObject(c.getString("valbased"));

                                Iterator<String> iter = val_based.keys();
                                while (iter.hasNext()) {
                                    String key = iter.next();
                                    try {
                                        switch (key) {
                                            case "parentkey":
                                                ob.getValKey(String.valueOf(val_based.get(key)));
                                                break;
                                            case "parentlable":
                                                ob.getValLabel(String.valueOf(val_based.get(key)));
                                                break;
                                            case "parentval":
                                                ob.getValValue(String.valueOf(val_based.get(key)));
                                                break;
                                        }
                                    } catch (JSONException e) {
                                        // Something went wrong!
                                        Log.e(TAG, "val based error " + e.toString());
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error no val based ");
                        }
                        ob.get_head("gc");
                        if (f_type.equals("select") || f_type.equals("checkbox")) {
                            ob.get_multi(c.getString("msel"));
                            JSONArray choice = c.getJSONArray("choice");
                            String[] choice_desc = new String[choice.length()];
                            String[] choice_val = new String[choice.length()];
                            for (int j = 0; j < choice.length(); j++) {
                                choice_desc[j] = choice.getJSONObject(j).getString("cdesc");
                                choice_val[j] = choice.getJSONObject(j).getString("cval");
                            }
                            ob.getChoice_desc(choice_desc);
                            ob.getChoice_val(choice_val);
                        }
                        if (from.equals("edit")) {
                            String f_value = c.getString("value");
                            ob.getValue(f_value);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    observation_list.add(ob);
                }
            }
            else if(jsonObj.has("obt")) {
                try {
                    JSONArray obt = jsonObj.getJSONArray("obt");
                    for (int i = 0; i < obt.length(); i++) {
                        ob_list ob = new ob_list();
                        try {
                            c = obt.getJSONObject(i);
                            ob.getKey(c.getString("fkey"));
                            ob.getName(c.getString("fname"));
                            String f_type = c.getString("ftype");
                            ob.getType(f_type);
                            try {
                                if (c.getString("valbased") != null) {

                                    JSONObject val_based = new JSONObject(c.getString("valbased"));

                                    Iterator<String> iter = val_based.keys();
                                    while (iter.hasNext()) {
                                        String key = iter.next();
                                        try {
                                            switch (key) {
                                                case "parentkey":
                                                    ob.getValKey(String.valueOf(val_based.get(key)));
                                                    break;
                                                case "parentlable":
                                                    ob.getValLabel(String.valueOf(val_based.get(key)));
                                                    break;
                                                case "parentval":
                                                    ob.getValValue(String.valueOf(val_based.get(key)));
                                                    break;
                                            }
                                        } catch (JSONException e) {
                                            // Something went wrong!
                                            Log.e(TAG, "val based error " + e.toString());
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error no val based ");
                            }
                            ob.get_head("toc");
                            if (f_type.equals("select") || f_type.equals("checkbox")) {
                                JSONArray choice = c.getJSONArray("choice");
                                String[] choice_desc = new String[choice.length()];
                                String[] choice_val = new String[choice.length()];
                                for (int j = 0; j < choice.length(); j++) {
                                    choice_desc[j] = choice.getJSONObject(j).getString("cdesc");
                                    choice_val[j] = choice.getJSONObject(j).getString("cval");
                                }
                                ob.getChoice_desc(choice_desc);
                                ob.getChoice_val(choice_val);
                            }
                            if (from.equals("edit")) {
                                String f_value = c.getString("value");
                                ob.getValue(f_value);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        observation_list.add(ob);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "no data in obt");
                }
            } else if(jsonObj.has("dr")) {
                    try {
                        JSONArray dr = jsonObj.getJSONArray("dr");
                        for (int i = 0; i < dr.length(); i++) {
                            ob_list ob = new ob_list();
                            try {
                                c = dr.getJSONObject(i);
                                ob.getKey(c.getString("fkey"));
                                ob.getName(c.getString("fname"));
                                String f_type = c.getString("ftype");
                                ob.getType(f_type);
                                try {
                                    if (c.getString("valbased") != null) {

                                        JSONObject val_based = new JSONObject(c.getString("valbased"));

                                        Iterator<String> iter = val_based.keys();
                                        while (iter.hasNext()) {
                                            String key = iter.next();
                                            try {
                                                switch (key) {
                                                    case "parentkey":
                                                        ob.getValKey(String.valueOf(val_based.get(key)));
                                                        break;
                                                    case "parentlable":
                                                        ob.getValLabel(String.valueOf(val_based.get(key)));
                                                        break;
                                                    case "parentval":
                                                        ob.getValValue(String.valueOf(val_based.get(key)));
                                                        break;
                                                }
                                            } catch (JSONException e) {
                                                // Something went wrong!
                                                Log.e(TAG, "val based error " + e.toString());
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error no val based ");
                                }
                                ob.get_head("dr");
                                if (f_type.equals("select") || f_type.equals("checkbox")) {
                                    JSONArray choice = c.getJSONArray("choice");
                                    String[] choice_desc = new String[choice.length()];
                                    String[] choice_val = new String[choice.length()];
                                    for (int j = 0; j < choice.length(); j++) {
                                        choice_desc[j] = choice.getJSONObject(j).getString("cdesc");
                                        choice_val[j] = choice.getJSONObject(j).getString("cval");
                                    }
                                    ob.getChoice_desc(choice_desc);
                                    ob.getChoice_val(choice_val);
                                }
                                if (from.equals("edit")) {
                                    String f_value = c.getString("value");
                                    ob.getValue(f_value);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            observation_list.add(ob);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "no data in dr");
                    }
                }else if(jsonObj.has("cn")){
                try {
                    JSONArray cn = jsonObj.getJSONArray("cn");
                    for (int i = 0; i < cn.length(); i++) {
                        ob_list ob = new ob_list();
                        try {
                            c = cn.getJSONObject(i);
                            ob.getKey(c.getString("fkey"));
                            ob.getName(c.getString("fname"));
                            String f_type = c.getString("ftype");
                            ob.getType(f_type);
                            try {
                                if (c.getString("valbased") != null) {

                                    JSONObject val_based = new JSONObject(c.getString("valbased"));

                                    Iterator<String> iter = val_based.keys();
                                    while (iter.hasNext()) {
                                        String key = iter.next();
                                        try {
                                            switch (key) {
                                                case "parentkey":
                                                    ob.getValKey(String.valueOf(val_based.get(key)));
                                                    break;
                                                case "parentlable":
                                                    ob.getValLabel(String.valueOf(val_based.get(key)));
                                                    break;
                                                case "parentval":
                                                    ob.getValValue(String.valueOf(val_based.get(key)));
                                                    break;
                                            }
                                        } catch (JSONException e) {
                                            // Something went wrong!
                                            Log.e(TAG, "val based error " + e.toString());
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error no val based ");
                            }
                            ob.get_head("cn");
                            if (f_type.equals("select") || f_type.equals("checkbox")) {
                                JSONArray choice = c.getJSONArray("choice");
                                String[] choice_desc = new String[choice.length()];
                                String[] choice_val = new String[choice.length()];
                                for (int j = 0; j < choice.length(); j++) {
                                    choice_desc[j] = choice.getJSONObject(j).getString("cdesc");
                                    choice_val[j] = choice.getJSONObject(j).getString("cval");
                                }
                                ob.getChoice_desc(choice_desc);
                                ob.getChoice_val(choice_val);
                            }
                            if (from.equals("edit")) {
                                String f_value = c.getString("value");
                                ob.getValue(f_value);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                    assert c != null;
//                    if (!c.getString("ftype").equals("instruction")) {
                        observation_list.add(ob);
//                    }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "no data in cn");
                }
            }
            StickyListHeadersAdapter adapter = new MyAdapter(Observation_view_list.this, observation_list);
            expandableStickyListHeadersListView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class MyAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        List<ob_list> ob_lists;
        private LayoutInflater inflater;
        Context context;

        MyAdapter(Context context, List<ob_list> ob_lists) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.ob_lists = ob_lists;
        }

        @Override
        public int getCount() {
            return ob_lists.size();
        }

        @Override
        public Object getItem(int position) {
            return ob_lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.view_list_item, parent, false);
                holder.text_view = convertView.findViewById(R.id.text_view_name);
                holder.text_value = convertView.findViewById(R.id.text_view_value);
//                holder.img_list = convertView.findViewById(R.id.img_list);
//                holder.img_list1 = convertView.findViewById(R.id.img_list1);
//                holder.img_list2 = convertView.findViewById(R.id.img_list2);
//                holder.img_list3 = convertView.findViewById(R.id.img_list3);
//                holder.img_pro = convertView.findViewById(R.id.img_progress);
//                holder.img_pro.setVisibility(View.GONE);
                holder.linearLayout = convertView.findViewById(R.id.linearLayout);
                holder.rel_img = convertView.findViewById(R.id.rel_img);
                holder.recycle_img_list = convertView.findViewById(R.id.recycle_img_list);

                holder.recycle_img_list.setHasFixedSize(true);
                holder.recycle_img_list.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false));
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            for (int m = 0 ; m < ob_lists.size() ; m++){
                try {
                    if (ob_lists.get(position).setValue().equals(ob_lists.get(m).setValValue())) {
                        ob_lists.get(m).getValCheck("1");
                    }
                }catch (Exception e){
                    Log.e(TAG ,"null error "+e.toString());
                }
            }

//            if (ob_lists.get(position).setValKey() == null) {
                holder.text_view.setText(ob_lists.get(position).setName());
                if (ob_lists.get(position).setType().equals("select") || ob_lists.get(position).setType().equals("checkbox")) {
                    holder.text_view.setGravity(Gravity.LEFT);
                    holder.text_view.setTextColor(Color.parseColor("#000000"));
                    holder.text_view.setTypeface(null ,NORMAL);
                    holder.text_value.setVisibility(View.VISIBLE);
                    holder.text_value.setText("");
                    holder.rel_img.setVisibility(View.GONE);
                    String[] choice_desc_val_3 = ob_lists.get(position).setChoice_val();
                    String[] choice_desc_val_1 = ob_lists.get(position).setChoice_desc();

                    for (int i = 0; i < choice_desc_val_3.length; i++) {
                        if (choice_desc_val_3[i].equals(ob_lists.get(position).setValue())) {
                            holder.text_value.setText(choice_desc_val_1[i]);
                        }
                    }
                } else if (ob_lists.get(position).setType().equals("image")) {
                    holder.text_view.setGravity(Gravity.LEFT);
                    holder.text_view.setTextColor(Color.parseColor("#000000"));
                    holder.text_view.setTypeface(null ,NORMAL);
                    holder.text_value.setVisibility(View.GONE);
                    holder.rel_img.setVisibility(View.VISIBLE);
                    imageLoader = CustomVolleyRequest.getInstance(Observation_view_list.this).getImageLoader();
                    try {
                        images = ob_lists.get(position).setValue().split(",");
                        RecyclerView.Adapter adapter = new img_list_adapter(this, images);
                        holder.recycle_img_list.setAdapter(adapter);
//                        for (int i = 0 ; i < images.length ; i++) {
//                            final int finalI = i;
//                            imageLoader.get(Api.obd_get_image + images[i], new ImageLoader.ImageListener() {
//
//                                public void onErrorResponse(VolleyError arg0) {
//                                    // set an error image if the download fails
//                                }
//
//                                public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
//                                    if (response.getBitmap() != null) {
//                                        if (finalI == 0) {
//                                            holder.img_list.setImageBitmap(response.getBitmap());
//                                            holder.img_list.setVisibility(View.VISIBLE);
//                                        }else if (finalI == 1) {
//                                            holder.img_list1.setImageBitmap(response.getBitmap());
//                                            holder.img_list1.setVisibility(View.VISIBLE);
//                                        }else if (finalI == 2) {
//                                            holder.img_list2.setImageBitmap(response.getBitmap());
//                                            holder.img_list2.setVisibility(View.VISIBLE);
//                                        }else if (finalI == 3) {
//                                            holder.img_list3.setImageBitmap(response.getBitmap());
//                                            holder.img_list3.setVisibility(View.VISIBLE);
//                                        }
//                                        holder.img_pro.setVisibility(View.GONE);
//                                    }
//                                }
//                            });
//                        }
//
//                        holder.img_list.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Intent i = new Intent(Observation_view_list.this, view_image.class);
//                                i.putExtra("img_value", images[0]);
//                                startActivity(i);
//                            }
//                        });
//
//                        holder.img_list1.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Intent i = new Intent(Observation_view_list.this, view_image.class);
//                                i.putExtra("img_value", images[1]);
//                                startActivity(i);
//                            }
//                        });
//
//                        holder.img_list2.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Intent i = new Intent(Observation_view_list.this, view_image.class);
//                                i.putExtra("img_value", images[2]);
//                                startActivity(i);
//                            }
//                        });
//
//                        holder.img_list3.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Intent i = new Intent(Observation_view_list.this, view_image.class);
//                                i.putExtra("img_value", images[3]);
//                                startActivity(i);
//                            }
//                        });
                    }catch (Exception e) {
                        images[0] = ob_lists.get(position).setValue();
                        RecyclerView.Adapter adapter = new img_list_adapter(this, images);
                        holder.recycle_img_list.setAdapter(adapter);
//                        imageLoader.get(Api.obd_get_image + ob_lists.get(position).setValue(), new ImageLoader.ImageListener() {
//
//                            public void onErrorResponse(VolleyError arg0) {
//                                // set an error image if the download fails
//                            }
//
//                            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
//                                if (response.getBitmap() != null) {
//                                    holder.img_list.setImageBitmap(response.getBitmap());
//                                    holder.img_pro.setVisibility(View.GONE);
//                                }
//                            }
//                        });
//
//                        holder.img_list.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Intent i = new Intent(Observation_view_list.this, view_image.class);
//                                i.putExtra("img_value", ob_lists.get(position).setValue());
//                                startActivity(i);
//                            }
//                        });
                    }
//                    imageLoader.get(Api.obd_get_image + ob_lists.get(position).setValue(), new ImageLoader.ImageListener() {
//
//                        public void onErrorResponse(VolleyError arg0) {
//                            // set an error image if the download fails
//                        }
//
//                        public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
//                            if (response.getBitmap() != null) {
//                                holder.img_list.setImageBitmap(response.getBitmap());
//                                holder.img_pro.setVisibility(View.GONE);
//                            }
//                        }
//                    });
//                    holder.img_list.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent i = new Intent(Observation_view_list.this, view_image.class);
//                            i.putExtra("img_value", ob_lists.get(position).setValue());
//                            startActivity(i);
//                        }
//                    });
                } else if (ob_lists.get(position).setType().equals("instruction")) {
                    holder.text_value.setVisibility(View.GONE);
                    holder.text_view.setGravity(Gravity.CENTER);
                    holder.text_view.setTextColor(Color.parseColor("#1120b2"));
                    holder.text_view.setTypeface(null ,BOLD);
                } else {
                    holder.text_value.setVisibility(View.VISIBLE);
                    holder.rel_img.setVisibility(View.GONE);
                    holder.text_value.setText(ob_lists.get(position).setValue());
                    holder.text_view.setGravity(Gravity.LEFT);
                    holder.text_view.setTextColor(Color.parseColor("#000000"));
                    holder.text_view.setTypeface(null ,NORMAL);
                }
//            }else {
//                try { // value based field check
//                    holder.linearLayout.setVisibility(View.VISIBLE);
//                    if (ob_lists.get(position).setValCheck().equals("1")) {
//                        holder.linearLayout.setVisibility(View.VISIBLE);
//                        holder.text_view.setText(ob_lists.get(position).setName());
//                        if (ob_lists.get(position).setType().equals("select") || ob_lists.get(position).setType().equals("checkbox")) {
//                            holder.text_value.setVisibility(View.VISIBLE);
//                            holder.text_value.setText("");
//                            holder.rel_img.setVisibility(View.GONE);
//                            String[] choice_desc_val_3 = ob_lists.get(position).setChoice_val();
//                            String[] choice_desc_val_1 = ob_lists.get(position).setChoice_desc();
//
//                            for (int i = 0; i < choice_desc_val_3.length; i++) {
//                                if (choice_desc_val_3[i].equals(ob_lists.get(position).setValue())) {
//                                    holder.text_value.setText(choice_desc_val_1[i]);
//                                }
//                            }
//                        } else if (ob_lists.get(position).setType().equals("image")) {
//                            holder.text_value.setVisibility(View.GONE);
//                            holder.rel_img.setVisibility(View.VISIBLE);
//                            imageLoader = CustomVolleyRequest.getInstance(Observation_view_list.this).getImageLoader();
////                            imageLoader.get(Api.obd_get_image + ob_lists.get(position).setValue(), new ImageLoader.ImageListener() {
////
////                                public void onErrorResponse(VolleyError arg0) {
////                                    // set an error image if the download fails
////                                }
////
////                                public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
////                                    if (response.getBitmap() != null) {
////
////
////                                        holder.img_list.setImageBitmap(response.getBitmap());
////                                        holder.img_pro.setVisibility(View.GONE);
////                                    }
////                                }
////                            });
////                            holder.img_list.setOnClickListener(new View.OnClickListener() {
////                                @Override
////                                public void onClick(View view) {
////                                    Intent i = new Intent(Observation_view_list.this, view_image.class);
////                                    i.putExtra("img_value", ob_lists.get(position).setValue());
////                                    startActivity(i);
////                                }
////                            });
//                            try {
//                                images = ob_lists.get(position).setValue().split(",");
//                                RecyclerView.Adapter adapter = new img_list_adapter(this, images);
//                                holder.recycle_img_list.setAdapter(adapter);
////                                for (int i = 0 ; i < images.length ; i++) {
////                                    final int finalI = i;
////                                    imageLoader.get(Api.obd_get_image + images[i], new ImageLoader.ImageListener() {
////
////                                        public void onErrorResponse(VolleyError arg0) {
////                                            // set an error image if the download fails
////                                        }
////
////                                        public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
////                                            if (response.getBitmap() != null) {
////                                                if (finalI == 0) {
////                                                    holder.img_list.setImageBitmap(response.getBitmap());
////                                                    holder.img_list.setVisibility(View.VISIBLE);
////                                                }else if (finalI == 1) {
////                                                    holder.img_list1.setImageBitmap(response.getBitmap());
////                                                    holder.img_list1.setVisibility(View.VISIBLE);
////                                                }else if (finalI == 2) {
////                                                    holder.img_list2.setImageBitmap(response.getBitmap());
////                                                    holder.img_list2.setVisibility(View.VISIBLE);
////                                                }else if (finalI == 3) {
////                                                    holder.img_list3.setImageBitmap(response.getBitmap());
////                                                    holder.img_list3.setVisibility(View.VISIBLE);
////                                                }
////                                                holder.img_pro.setVisibility(View.GONE);
////                                            }
////                                        }
////                                    });
////                                }
////
////                                holder.img_list.setOnClickListener(new View.OnClickListener() {
////                                    @Override
////                                    public void onClick(View view) {
////                                        Intent i = new Intent(Observation_view_list.this, view_image.class);
////                                        i.putExtra("img_value", images[0]);
////                                        startActivity(i);
////                                    }
////                                });
////
////                                holder.img_list1.setOnClickListener(new View.OnClickListener() {
////                                    @Override
////                                    public void onClick(View view) {
////                                        Intent i = new Intent(Observation_view_list.this, view_image.class);
////                                        i.putExtra("img_value", images[1]);
////                                        startActivity(i);
////                                    }
////                                });
////
////                                holder.img_list2.setOnClickListener(new View.OnClickListener() {
////                                    @Override
////                                    public void onClick(View view) {
////                                        Intent i = new Intent(Observation_view_list.this, view_image.class);
////                                        i.putExtra("img_value", images[2]);
////                                        startActivity(i);
////                                    }
////                                });
////
////                                holder.img_list3.setOnClickListener(new View.OnClickListener() {
////                                    @Override
////                                    public void onClick(View view) {
////                                        Intent i = new Intent(Observation_view_list.this, view_image.class);
////                                        i.putExtra("img_value", images[3]);
////                                        startActivity(i);
////                                    }
////                                });
//                            }catch (Exception e) {
//                                images[0] = ob_lists.get(position).setValue();
//                                RecyclerView.Adapter adapter = new img_list_adapter(this, images);
//                                holder.recycle_img_list.setAdapter(adapter);
////                                imageLoader.get(Api.obd_get_image + ob_lists.get(position).setValue(), new ImageLoader.ImageListener() {
////
////                                    public void onErrorResponse(VolleyError arg0) {
////                                        // set an error image if the download fails
////                                    }
////
////                                    public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
////                                        if (response.getBitmap() != null) {
////                                            holder.img_list.setImageBitmap(response.getBitmap());
////                                            holder.img_pro.setVisibility(View.GONE);
////                                        }
////                                    }
////                                });
////
////                                holder.img_list.setOnClickListener(new View.OnClickListener() {
////                                    @Override
////                                    public void onClick(View view) {
////                                        Intent i = new Intent(Observation_view_list.this, view_image.class);
////                                        i.putExtra("img_value", ob_lists.get(position).setValue());
////                                        startActivity(i);
////                                    }
////                                });
//                            }
//                        } else {
//                            holder.text_value.setVisibility(View.VISIBLE);
//                            holder.rel_img.setVisibility(View.GONE);
//                            holder.text_value.setText(ob_lists.get(position).setValue());
//                        }
//                    }else {
//                        holder.linearLayout.setVisibility(View.GONE);
//                    }
//                }catch (Exception e){
//                    Log.e(TAG ,"error "+e.toString());
//                    holder.linearLayout.setVisibility(View.GONE);
//                }
//            }
            return convertView;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            HeaderViewHolder holder;
            if (convertView == null) {
                holder = new HeaderViewHolder();
                convertView = inflater.inflate(R.layout.header, parent, false);
                holder.text = convertView.findViewById(R.id.text_header);
                convertView.setTag(holder);
            } else {
                holder = (HeaderViewHolder) convertView.getTag();
            }
            //set header text as first char in name
            String headerText = "" + ob_lists.get(position).set_head();
            Log.e("values---header",headerText);
            switch (headerText) {
                case "gc":
                    holder.text.setText("General Conditions");
                    break;
                case "toc":
                    holder.text.setText("Observation Traits");
                    break;
                case "dr":
                    holder.text.setText("Disease Reaction");
                    break;
                case "cn":
                    holder.text.setText("Conclusion");
                    break;
                case "other":
                    holder.text.setText("Crop Info");
                    break;

            }

            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            //return the first character of the country as ID because this is what headers are based upon
            return ob_lists.get(position).set_head().subSequence(0, 1).charAt(0);
        }

        class HeaderViewHolder {
            TextView text;
        }

        class ViewHolder {
            TextView text_view, text_value;
            LinearLayout linearLayout;
            RelativeLayout rel_img;
//            ImageView img_list ,img_list1 ,img_list2 ,img_list3;
//            ProgressBar img_pro;
            RecyclerView recycle_img_list;
        }
    }

    private class img_list_adapter extends RecyclerView.Adapter<img_list_adapter.ViewHolder> {

        String[] images;

        public img_list_adapter(MyAdapter myAdapter, String[] imgs) {
            images = imgs;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_img, parent, false);
            img_list_adapter.ViewHolder viewHolder = new img_list_adapter.ViewHolder(v);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

            holder.pro_img.setVisibility(View.VISIBLE);

            imageLoader.get(Api.obd_get_image + images[position], new ImageLoader.ImageListener() {

                public void onErrorResponse(VolleyError arg0) {
                    // set an error image if the download fails
                }

                public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                    if (response.getBitmap() != null) {
                        holder.img_add.setImageBitmap(response.getBitmap());
                        holder.pro_img.setVisibility(View.GONE);
                    }
                }
            });

            holder.img_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Observation_view_list.this, view_image.class);
                    i.putExtra("img_value", images[position]);
                    startActivity(i);
                }
            });

        }

        @Override
        public int getItemCount() {
            return images.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView img_add ,img_close;
            ProgressBar pro_img;

            public ViewHolder(View v) {
                super(v);

                pro_img = v.findViewById(R.id.img_progress);
                img_add = v.findViewById(R.id.image_add);
                img_close = v.findViewById(R.id.image_close);

                img_close.setVisibility(View.GONE);
            }
        }
    }
}
