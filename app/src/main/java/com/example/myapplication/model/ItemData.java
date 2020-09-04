package com.example.myapplication.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemData implements Serializable {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("lang")
    @Expose
    private String lang;
    @SerializedName("content")
    @Expose
    private ArrayList<Content> content = null;
    private final static long serialVersionUID = 282041485649928540L;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public ArrayList<Content> getContent() {
        return content;
    }

    public void setContent(ArrayList<Content> content) {
        this.content = content;
    }

}