package com.dv.persistnote.base.network;

import com.dv.persistnote.base.network.bean.Result;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

public interface TestServiceInterface {

    @GET("/apistore/tranlateservice/translate")      // here is the other url part.best way is to start using /
    public void getTranslate(@QueryMap Map<String, String> options,
                             @Header("apikey") String apikey,
                             Callback<Result> response);

    @GET("/apistore/tranlateservice/translate")      // here is the other url part.best way is to start using /
    public void getTranslate2(@Query("query") String query,
                              @Query("from") String from,
                              @Query("to") String to,
                             @Header("apikey") String apikey,
                             Callback<Result> response);

}