package com.example.manoj.hyveg_observation.list;

import java.util.ArrayList;

public class FormModel {
    private String id;
    private int db_id;
    private ArrayList<ListModel> listModels;
    private String is_synced,type;


    public FormModel(String id, int db_id, ArrayList<ListModel> listModels,String type) {
        this.id = id;
        this.db_id = db_id;
        this.listModels = listModels;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDb_id() {
        return db_id;
    }

    public void setDb_id(int db_id) {
        this.db_id = db_id;
    }

    public ArrayList<ListModel> getListModels() {
        return listModels;
    }

    public void setListModels(ArrayList<ListModel> listModels) {
        this.listModels = listModels;
    }

    public String getIs_synced() {
        return is_synced;
    }

    public void setIs_synced(String is_synced) {
        this.is_synced = is_synced;
    }
}
