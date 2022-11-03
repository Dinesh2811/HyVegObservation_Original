package com.example.manoj.hyveg_observation.adapter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Choice {
    @SerializedName("cdesc")
    @Expose
    private String cdesc;
    @SerializedName("cval")
    @Expose
    private String cval;

    public String getCdesc() {
        return cdesc;
    }

    public void setCdesc(String cdesc) {
        this.cdesc = cdesc;
    }

    public String getCval() {
        return cval;
    }

    public void setCval(String cval) {
        this.cval = cval;
    }
}
