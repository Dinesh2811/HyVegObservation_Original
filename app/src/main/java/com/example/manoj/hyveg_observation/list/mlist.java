package com.example.manoj.hyveg_observation.list;

public class mlist {

    private String text,edit,date,spin;
    private String[] stage;

    public mlist(String text ,String edit ,String spin,String[] stage,String date) {
        this.text = text;
        this.edit = edit;
        this.spin=spin;
        this.stage=stage;
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public String  getEdit() {
        return edit;
    }

    public String getSpin() {
        return spin;
    }

    public String[] getStage() {return stage;}

    public String getDate() {
        return date;
    }

    public void setText(String mtext) {
        this.text = mtext;
    }

    public void setEdit(String medit) {
        this.edit = medit;
    }

    public void setSpin(String mspin) {
        this.spin = mspin;
    }

    public void setDate(String mdate) {
        this.edit = mdate;
    }
}
