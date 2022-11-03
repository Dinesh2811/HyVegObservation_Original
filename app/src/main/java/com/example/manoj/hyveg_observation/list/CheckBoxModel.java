package com.example.manoj.hyveg_observation.list;

public class CheckBoxModel {
    private int id;
    private String data;
    private boolean isChecked = false;

    public CheckBoxModel(String data, boolean isChecked) {
        this.data = data;
        this.isChecked = isChecked;
    }

    public CheckBoxModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
