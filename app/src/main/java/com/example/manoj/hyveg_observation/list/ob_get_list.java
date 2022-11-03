package com.example.manoj.hyveg_observation.list;

import java.io.Serializable;

public class ob_get_list implements Serializable {

    private String id ,name ,date ,ob_id,pd_regnum,grower_code;


    public void get_id(String id) {
        this.id = id;
    }

    public void get_ob_id(String ob_id) {
        this.ob_id = ob_id;
    }

    public void get_name(String name) {
        this.name = name;
    }

    public void get_pd_number(String pd_regnum) {
        this.pd_regnum = pd_regnum;
    }

    public void get_growercode(String grower_code) {
        this.grower_code = grower_code;
    }

    public void get_date(String date) {
        this.date = date;
    }

    public String set_name() {
        return name;
    }

    public String set_pd_number() {
        return pd_regnum;
    }

    public String set_growercode() {
        return grower_code;
    }

    public String set_date() {
        return date;
    }

    public String set_id() {
        return id;
    }

    public String set_ob_id() {
        return ob_id;
    }
}
