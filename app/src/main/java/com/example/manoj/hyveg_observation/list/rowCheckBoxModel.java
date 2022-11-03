package com.example.manoj.hyveg_observation.list;

import java.util.ArrayList;

public class rowCheckBoxModel {
    private int id;
    private ArrayList<CheckBoxModel> mCheckBoxList;
    //    private ArrayList<ImageModel> mImageList;
    private String[] choice_desc;
    private String choice;
    private int selectedChoice = -1;
    private String decimal, decimal_error, date,s;
    private int intType;
    private double min,max;

    public rowCheckBoxModel(ArrayList<CheckBoxModel> mCheckBoxList, int intType) {
        this.mCheckBoxList = mCheckBoxList;
        this.intType = intType;
    }

    public rowCheckBoxModel(String[] choice, int intType) {
        this.choice_desc = choice;
        this.intType = intType;
    }

    public rowCheckBoxModel(String decimal, String decimal_error, boolean isDecimal, int intType,double min,double max) {
        this.choice = decimal;
        this.decimal_error = decimal_error;
        this.intType = intType;
        this.min = min;
        this.max =max;
    }

  /*  public rowCheckBoxModel(ArrayList<ImageModel> mImageList, int intType, String s) {
//        this.mImageList = mImageList;
        this.intType = intType;
        this.s = s;

    }*/
//    public rowCheckBoxModel(ArrayList<ImageModel> ImageModel, int intType) {
//        this.mImageList = ImageModel;
//        this.intType = intType;
//    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<CheckBoxModel> getmCheckBoxList() {
        return mCheckBoxList;
    }

    public void setmCheckBoxList(ArrayList<CheckBoxModel> mCheckBoxList) {
        this.mCheckBoxList = mCheckBoxList;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public String getDecimal() {
        return decimal;
    }

    public void setDecimal(String decimal) {
        this.decimal = decimal;
    }

    public int getIntType() {
        return intType;
    }

    public void setIntType(int intType) {
        this.intType = intType;
    }

    public String[] getChoice_desc() {
        return choice_desc;
    }

    public void setChoice_desc(String[] choice_desc) {
        this.choice_desc = choice_desc;
    }

    public void setOneChoice_desc(int pos, String choice_desc) {
        if (this.choice_desc != null && this.choice_desc.length > pos && this.choice_desc[pos] != null)
            this.choice_desc[pos] = choice_desc;
    }

    public void removeOneChoice_desc(int pos) {
        if (this.choice_desc != null && this.choice_desc.length > pos && this.choice_desc[pos] != null) {
            this.choice_desc[pos] = "";
           /* ArrayList<String> ch_list = new ArrayList<String>();
            for (int i = 0; i < this.choice_desc.length; i++) {
                if (pos == i) {
                    // No operation here
                } else {
                    ch_list.add(this.choice_desc[i]);
                }
            }
            this.choice_desc = ch_list.toArray(new String[ch_list.size()]);*/
        }
    }

    public int getSelectedChoice() {
        return selectedChoice;
    }

    public void setSelectedChoice(int selectedChoice) {
        this.selectedChoice = selectedChoice;
    }
    public void setSelectedChoice(int pos, String data) {
        this.choice_desc[pos] = data;
    }

    public String getDecimal_error() {
        return decimal_error;
    }

    public void setDecimal_error(String decimal_error) {
        this.decimal_error = decimal_error;
    }
}
