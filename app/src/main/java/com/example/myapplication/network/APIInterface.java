package com.example.myapplication.network;

import com.example.myapplication.model.ItemData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("/wp-json/mobileApp/v1/getPressReleasesDocs")
    Call<ItemData> doGetListResources();


}