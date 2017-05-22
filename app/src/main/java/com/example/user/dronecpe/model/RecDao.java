package com.example.user.dronecpe.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nattapongpaka on 5/22/2017 AD.
 */

public class RecDao {

    /**
     * result : started
     * mode : manual
     * fname : /aa_2017-05-22_22-36.webm
     */

    @SerializedName("result")
    private String result;
    @SerializedName("mode")
    private String mode;
    @SerializedName("fname")
    private String fname;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }
}
