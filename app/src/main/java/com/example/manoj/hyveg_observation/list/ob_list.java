package com.example.manoj.hyveg_observation.list;

import java.io.Serializable;

public class ob_list implements Serializable {

    private String f_key;
    private String f_name;
    private String f_type;
    private String req;
    private String mSel;
    private String heading;
    private static String decimal;
    private String ValKey;
    private String ValLabel;
    private String ValValue;
    private String ValBaseCheck;
    private static String min;
    private static String max;
    private String[] choice_desc, choice_val;
    private String value,value1,value2;

//    public String getText() {
//        return text;
//    }
//
//    public String  getEdit() {
//        return edit;
//    }
//
//    public String getSpin() {
//        return spin;
//    }
//
//    public String[] getStage() {return stage;}
//
//    public String getDate() {
//        return date;
//    }
//
//    public void setText(String mtext) {
//        this.text = mtext;
//    }
//
//    public void setEdit(String medit) {
//        this.edit = medit;
//    }
//
//    public void setSpin(String mspin) {
//        this.spin = mspin;
//    }
//
//    public void setDate(String mdate) {
//        this.edit = mdate;
//    }

    public void getKey(String f_key) {
        this.f_key = f_key;
    }

    public void getName(String f_name) {
        this.f_name = f_name;
    }

    public void getType(String f_type) {
        this.f_type = f_type;
    }



    public void getChoice_desc(String[] choice_desc) {
        this.choice_desc = choice_desc;
    }

    public void getChoice_val(String[] choice_val) {
        this.choice_val = choice_val;
    }

    public String setKey() {
        return f_key;
    }

    public String setName() {
        return f_name;
    }

    public  String setType() {
        return f_type;
    }


    public String[] setChoice_desc() {
        return choice_desc;
    }

    public String[] setChoice_val() {
        return choice_val;
    }

    public void getReq(String req) {
        this.req = req;
    }

    public String setReq() {
        return req;
    }

    public void get_multi(String msel) {
        this.mSel = msel;
    }

    public String set_multi() {
        return mSel;
    }

    public void get_head(String heading) {
        this.heading = heading;
    }

    public String set_head() {
        return heading;
    }

    public void get_decimal(String decimal) {
        this.decimal = decimal;
    }

    public static String set_decimal() {
        return decimal;
    }

    public void getValKey(String Key) {
        this.ValKey = Key;
    }

    public void getValLabel(String Label) {
        this.ValLabel = Label;
    }

    public void getValValue(String Value) {
        this.ValValue = Value;
    }

    public String setValKey() {
        return ValKey;
    }

    public String setValLabel() {
        return ValLabel;
    }

    public String setValValue() {
        return ValValue;
    }

    public void getValCheck(String Value) {
        this.ValBaseCheck = Value;
    }

    public String setValCheck() {
        return ValBaseCheck;
    }

    public void get_min(String min) {
        this.min = min;
    }

    public static String set_min() {
        return min;
    }

    public void get_max(String max) {
        this.max = max;
    }

    public static String set_max() {
        return max;
    }

    //edit text values

    public void getValue(String Value) {
        this.value = Value;
    }


    public  String setValue() {
        return value;
    }

    public void getValue1(String Value1) {
        this.value1 = Value1;
    }

    public String setValue1() {
        return value1;
    }

    public void getValue2(String Value2) {
        this.value2 = Value2;
    }
    public String setValue2() {
        return value2;
    }

}
