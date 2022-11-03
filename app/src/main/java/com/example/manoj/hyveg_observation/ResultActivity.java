package com.example.manoj.hyveg_observation;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import com.example.manoj.hyveg_observation.list.FormModel;
import com.example.manoj.hyveg_observation.list.ListModel;
import com.example.manoj.hyveg_observation.services.FeedReaderDbHelper;
import com.example.manoj.hyveg_observation.services.SQLHelper;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {
    private final static String BUNDLE_CODE = "code";
    private final static String BUNDLE_ID = "id";
    private TextView tvData, tvHvCode;
    String code;
    int id;
    private ArrayList<ListModel> listModel;
    private ArrayList<String> codesList;

    //DB
    public SQLiteDatabase db;
    public FeedReaderDbHelper dbHelper;

    public static void startActivity(Context context, ArrayList<String> codeList, int id) {
        Intent intent = new Intent(context, ResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_ID, id);
        bundle.putSerializable(BUNDLE_CODE, codeList);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        getBundle();
        initDB();
        init();
    }

    public void initDB() {
        dbHelper = new FeedReaderDbHelper((Context) this);
        db = dbHelper.getWritableDatabase();
    }

    public void getBundle() {
        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {
                if (getIntent().getExtras().getString(BUNDLE_CODE) != null) {
                    code = getIntent().getExtras().getString(BUNDLE_CODE);
                }
                if (getIntent().getExtras().getSerializable(BUNDLE_CODE) != null) {
                    codesList = (ArrayList<String>) getIntent().getExtras().getSerializable(BUNDLE_CODE);
                }
                id = getIntent().getExtras().getInt(BUNDLE_ID);
            }
        }
    }

    private void init() {
        tvHvCode = findViewById(R.id.tvHvCode);
        tvData = findViewById(R.id.tvData);



        tvHvCode.setText(code);
        String msg = "";

        for (int j = 0; j < codesList.size(); j++) {
            tvHvCode.setText(codesList.get(j));
            listModel = SQLHelper.getAllFormDataByHvCodeFromDB(db, id, codesList.get(j),this);
            for (int i = 0; i < listModel.size(); i++) {
                if (listModel.get(i).getIntType() == 2) {
                    msg = msg.concat(listModel.get(i).getFname().concat(" : ").concat(listModel.get(i).getDecimals()[0]).concat("\n"));
                } else {
                    msg = msg.concat(listModel.get(i).getFname().concat(" : ").concat(listModel.get(i).getSelected_choice_desc()[j]).concat("\n"));
                }
            }
        }
        tvData.setText(msg);
    }

    public void sendToServer(){
        ArrayList<FormModel> list;
        for (int j = 0; j < codesList.size(); j++) {
            list = SQLHelper.getAllUnSyncedFormFromDB(db, codesList.get(j),"observe",this);
            for (int l = 0; l < list.size(); l++) {
                ArrayList<ListModel> models = list.get(l).getListModels();
                for (int i = 0; i < models.size(); i++) {
                    //TODO get all form data and upload to server
                    if (models.get(i).getIntType() == 2) {
                        models.get(i).getFname().concat(" : ").concat(models.get(i).getDecimals()[0]).concat("\n");
                    } else {
                        models.get(i).getFname().concat(" : ").concat(models.get(i).getSelected_choice_desc()[j]).concat("\n");
                    }
                }
            }
            //TODO after uploading server you have to store is_synced = Y in local db
            SQLHelper.updateForm(list, db, "Y");
        }
    }
}
