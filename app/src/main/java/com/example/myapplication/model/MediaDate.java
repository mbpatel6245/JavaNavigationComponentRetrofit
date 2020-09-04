package com.example.myapplication.model;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MediaDate implements Serializable {

    @SerializedName("dateString")
    @Expose
    private String dateString;
    @SerializedName("year")
    @Expose
    private String year;
    private final static long serialVersionUID = -6803925885942983680L;

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

}