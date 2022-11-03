package com.example.manoj.hyveg_observation.list;

import android.app.Activity;

import com.example.manoj.hyveg_observation.activity.Sessionsave;
import com.example.manoj.hyveg_observation.adapter.FormAdapter;

import java.util.ArrayList;

///added

public class ListModel {
    private int db_id;
    private String id;
    private String is_synced;
    private int db_form_id;
    private String fkey;
    private String fname;
    private String req;
    private String valKey;
    private String valLabel;
    private String valValue;
    private String f_type;
    private String msel;
    private String decimal;
    private String instruction;
    private String images;

    private String[] choice_desc;
    private String[] hv_codes;
    private ArrayList<rowCheckBoxModel> mCheckBoxList = new ArrayList<>();
    private ArrayList<rowCheckBoxModel> mSpinnerList = new ArrayList<>();
    private ArrayList<rowCheckBoxModel> mEditTextList = new ArrayList<>();
    //    private ArrayList<rowCheckBoxModel> mImageList = new ArrayList<>();
    private String[] choice_val;

    Activity mActivity;

    Sessionsave sessionsave;


    private int intType; //if 1=spinner, 2=number, 3=checkbox
    private double minVal;
    private double maxVal;
    private int[] selectedChoice ;
    private String[] decimals ;
    private String[] decimals_errors ;
    private String[] Images ;

    //For server by hvcode
    private String[] selected_choice_desc;
//    private String[] entered_decimal;

    public ListModel(Activity mActivity){

        sessionsave = new Sessionsave(mActivity);
        selectedChoice = new int[sessionsave.getCheck_hybrid()];
        decimals = new String[sessionsave.getCheck_hybrid()];
        decimals_errors = new String[sessionsave.getCheck_hybrid()];
        Images = new String[sessionsave.getCheck_hybrid()];
        selected_choice_desc = new String[sessionsave.getCheck_hybrid()];

        for (int i = 0; i < sessionsave.getCheck_hybrid(); i++){
            selectedChoice[i] = -1;
            selected_choice_desc[i] = "";
            decimals[i] = "";
            decimals_errors[i] = "";
//            entered_decimal[i] = "";
        }
    }

    public ListModel(int i){
        selectedChoice[i] = -1;
        selected_choice_desc[i] = "";
        decimals[i] = "";
//            entered_decimal[i] = "";
    }

    public void setUpSpinnerData(){
        for (int j = 0; j < sessionsave.getCheck_hybrid(); j++) {
            mSpinnerList.add(new rowCheckBoxModel(choice_desc, intType));
        }
    }

    public void setSpinnerList(int pos, String[] data){
        mSpinnerList.get(pos).setChoice_desc(data);
    }

    //    public void setUpImagetData(){
//        for (int j = 0; j < sessionsave.getCheck_hybrid(); j++) {
//            mEditTextList.add(new rowCheckBoxModel("", true, intType));
//        }
//    }
    public void setUpEditTextData(){
        for (int j = 0; j < sessionsave.getCheck_hybrid(); j++) {
            mEditTextList.add(new rowCheckBoxModel("", decimals_errors[j], true, intType,minVal,maxVal));
        }
    }



    public void setUpCheckBoxData(){
        for (int j = 0; j < sessionsave.getCheck_hybrid(); j++) {
            ArrayList<CheckBoxModel> list = new ArrayList<>();
            for (int i = 0; i < choice_desc.length; i++) {
                list.add(new CheckBoxModel(choice_desc[i], false));
            }
            mCheckBoxList.add(new rowCheckBoxModel(list, intType));
        }
    }

    public void setUpImageData(){
        String[] choice_desc = new String[2];
        for (int j = 0; j < sessionsave.getCheck_hybrid(); j++) {
            mSpinnerList.add(new rowCheckBoxModel(choice_desc, intType));
        }
    }

    public ListModel(int db_id, String id, String fkey, String fname, String req, String valKey, String valLabel, String valValue, String f_type, String msel, String decimal, String instruction, int intType, double minVal, double maxVal, String is_synced,Activity mActivity) {
        sessionsave = new Sessionsave(mActivity);
        selectedChoice = new int[sessionsave.getCheck_hybrid()];
        decimals = new String[sessionsave.getCheck_hybrid()];
        Images = new String[sessionsave.getCheck_hybrid()];
        selected_choice_desc = new String[sessionsave.getCheck_hybrid()];
        this.db_id = db_id;
        this.id = id;
        this.fkey = fkey;
        this.fname = fname;
        this.req = req;
        this.valKey = valKey;
        this.valLabel = valLabel;
        this.valValue = valValue;
        this.f_type = f_type;
        this.msel = msel;
        this.decimal = decimal;
        this.instruction = instruction;
        this.intType = intType;
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.is_synced = is_synced;
        this.mActivity = mActivity;
//        this.images = images;

    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }


    public String getFkey() {
        return fkey;
    }

    public void setFkey(String fkey) {
        this.fkey = fkey;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getReq() {
        return req;
    }

    public void setReq(String req) {
        this.req = req;
    }

    public String getValKey() {
        return valKey;
    }

    public void setValKey(String valKey) {
        this.valKey = valKey;
    }

    public String getValLabel() {
        return valLabel;
    }

    public void setValLabel(String valLabel) {
        this.valLabel = valLabel;
    }

    public String getValValue() {
        return valValue;
    }

    public void setValValue(String valValue) {
        this.valValue = valValue;
    }

    public String getF_type() {
        return f_type;
    }

    public void setF_type(String f_type) {
        this.f_type = f_type;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String[] getChoice_desc() {
        return choice_desc;
    }

    public String[] getSelectedChoice_desc() {
        return selected_choice_desc;
    }

    public void setSelected_choice_(String[] choice_desc) {
//        if (intType == 4) {
//            if (this.choice_desc == null || this.choice_desc.length == 0){
        this.selected_choice_desc = new String[choice_desc.length];
//            }
        for (int i = 0; i < choice_desc.length; i++) {
            this.selected_choice_desc[i] = choice_desc[i];
        }
//        }
    }

    public void setOneChoice(int pos, String choi){
        if (this.choice_desc != null && this.choice_desc.length > pos && this.choice_desc[pos] != null)
            this.choice_desc[pos] = choi;
    }

    public void removeOneChoice_desc(int pos) {
        if (this.choice_desc != null && this.choice_desc.length > pos && this.choice_desc[pos] != null) {
            this.choice_desc[pos] = "";
            /*ArrayList<String> ch_list = new ArrayList<String>();
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

    public void setChoice_desc(int pos, String[] choice_desc) {
        if (intType == 4){
//            if (this.choice_desc == null || this.choice_desc.length == 0){
            this.choice_desc = new String[choice_desc.length];
//            }
            for (int i = 0; i < choice_desc.length; i++) {
                this.choice_desc[i] = choice_desc[i];
            }
//            this.choice_desc = new String[choice_desc.length];
           /* if (pos == 0) {
                for (int i = 0; i < 2; i++) {
                    this.choice_desc[i] = choice_desc[i];
                }
                for (int i = 0; i < 2; i++) {
                    this.choice_desc[i] = choice_desc[i];
                }
            } else {
                for (int i = 2; i < 4; i++) {
                    this.choice_desc[i] = choice_desc[i];
                }
            }*/
        }
    }



    public void setChoice_desc(String[] choice_desc) {
        if (intType == 1) {
            this.choice_desc = new String[choice_desc.length + 1];
            this.choice_desc[0] = "Please Select";
            for (int i = 0; i < choice_desc.length; i++) {
                this.choice_desc[i + 1] = choice_desc[i];
            }
        } else {
            this.choice_desc = new String[choice_desc.length];
            for (int i = 0; i < choice_desc.length; i++) {
                this.choice_desc[i] = choice_desc[i];
            }
        }
       /* for (int j = 0; j < sessionsave.getCheck_hybrid(); j++) {
            ArrayList<CheckBoxModel> list = new ArrayList<>();
            for (int i = 0; i < choice_desc.length; i++) {
                list.add(new CheckBoxModel(choice_desc[i], false));
            }
//            mCheckBoxList.add(new rowCheckBoxModel(list, 3));
        }*/
    }

    public String[] getChoice_val() {
        return choice_val;
    }

    public void setChoice_val(String[] choice_val) {
        if (intType == 1) {
            this.choice_val = new String[choice_val.length + 1];
            this.choice_val[0] = "Please Select";
            for (int i = 0; i < choice_val.length; i++) {
                this.choice_val[i + 1] = choice_val[i];
            }
        } else {
            this.choice_val = new String[choice_val.length];
            for (int i = 0; i < choice_val.length; i++) {
                this.choice_val[i] = choice_val[i];
            }
        }
    }

    public double getIntType() {
        return intType;
    }

    public void setIntType(int intType) {
        this.intType = intType;
    }

    public double getMinVal() {
        return minVal;
    }

    public void setMinVal(double minVal) {
        this.minVal = minVal;
    }

    public double getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(double maxVal) {
        this.maxVal = maxVal;
    }

    public String getMsel() {
        return msel;
    }

    public void setMsel(String msel) {
        this.msel = msel;
    }

    public String getDecimal() {
        return decimal;
    }

    public void setDecimal(String decimal) {
        this.decimal = decimal;
    }

    public int[] getSelectedChoiceList() {
        return selectedChoice;
    }

    public void setSelectedChoiceList(int[] selectedChoice) {
        this.selectedChoice = selectedChoice;
    }

    public int getSelectedChoice(int pos){
        return this.selectedChoice[pos];
    }

    public void setSelectedChoice(int id, int pos){
        this.selectedChoice[pos] = id;
    }

    public String[] getDecimals() {
        return decimals;
    }

    public void setDecimals(String[] decimals) {
        this.decimals = decimals;
    }

    public String[] getDecimals_errors() {
        return decimals_errors;
    }

    public void setDecimals_errors(String[] decimals) {
        this.decimals_errors = decimals;
    }

    public String getSelectedDecimal(int pos){
        return this.decimals[pos];
    }

    public void setSelectedDecimal(String id, int pos){
        if (decimals == null || decimals.length == 0){
            decimals = new String[pos + 1];
        }
        this.decimals[pos] = id;
    }

    public String getSelectedDecimalError(int pos){
        return this.decimals_errors[pos];
    }

    public void setSelectedDecimalError(String id, int pos){
        if (decimals_errors == null || decimals_errors.length == 0){
            decimals_errors = new String[pos + 1];
        }
        if (mEditTextList.size() >= pos){
            this.mEditTextList.get(pos).setDecimal_error(id);
        }
        this.decimals_errors[pos] = id;
    }

    public String[] getImagess() {
        return Images;
    }

    public void setImagess(String[] Images) {
        this.Images = Images;
    }

    public String getSelectedImage(int pos){
        return this.Images[pos];
    }

    public void setSelectedImage(String id, int pos){
        this.Images[pos] = id;
    }

    public ArrayList<rowCheckBoxModel> getmCheckBoxList() {
        return mCheckBoxList;
    }

    public void setmCheckBoxList(ArrayList<rowCheckBoxModel> mCheckBoxList) {
        this.mCheckBoxList = mCheckBoxList;
    }

    public ArrayList<rowCheckBoxModel> getCheckBoxList(){
        return mCheckBoxList;
    }
/*
    public ArrayList<rowCheckBoxModel> getmImageList() {
        return mImageList;
    }

    public void setmImageList(ArrayList<rowCheckBoxModel> mImageList) {
        this.mImageList = mImageList;
    }

    public ArrayList<rowCheckBoxModel> getImageList(){
        return mImageList;
    }*/

    public ArrayList<rowCheckBoxModel> getSpinnerList(){
        return mSpinnerList;
    }

    public ArrayList<rowCheckBoxModel> getEditTextList(){
        return mEditTextList;
    }

    public void setmEditTextList(ArrayList<rowCheckBoxModel> mEditTextList) {
        this.mEditTextList = mEditTextList;
    }

    public int getDb_id() {
        return db_id;
    }

    public void setDb_id(int db_id) {
        this.db_id = db_id;
    }

    public String getIs_synced() {
        return is_synced;
    }

    public void setIs_synced(String is_synced) {
        this.is_synced = is_synced;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDb_form_id() {
        return db_form_id;
    }

    public void setDb_form_id(int db_form_id) {
        this.db_form_id = db_form_id;
    }

    public String[] getHv_codes() {
        return hv_codes;
    }

    public void setHv_codes(String[] hv_codes) {
        this.hv_codes = hv_codes;
    }

    public String[] getSelected_choice_desc() {
        return selected_choice_desc;
    }

    /*public void setSelected_choice_desc(String selected_choice_desc) {
        this.selected_choice_desc = selected_choice_desc;
    }*/

    public void setSelected_choice_desc(int pos, String selected_choice_desc) {
        this.selected_choice_desc[pos] = selected_choice_desc;
    }



    /*public String[] getEntered_decimal() {
        return entered_decimal;
    }

    public void setEntered_decimal(String entered_decimal) {
        this.entered_decimal = entered_decimal;
    }*/
}
