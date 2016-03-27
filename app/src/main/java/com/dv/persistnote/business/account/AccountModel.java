package com.dv.persistnote.business.account;

import com.dv.persistnote.base.network.TestServiceInterface;
import com.dv.persistnote.base.network.bean.Result;
import com.dv.persistnote.base.network.bean.TransResult;
import com.dv.persistnote.business.ConstDef;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hang on 2016/3/20.
 */
public class AccountModel {

    private static AccountModel mInstance;

    public static AccountModel getInstance() {
        if( mInstance == null ) {
            mInstance = new AccountModel();
        }
        return mInstance;
    }

    public void testPostRequest(String srcStr, Callback<Result> callback) {
        String API = "http://apis.baidu.com";

        RestAdapter restAdapter = new RestAdapter.Builder().
                setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(API).build();

        TestServiceInterface serviceInterface = restAdapter.create(TestServiceInterface.class);

        HashMap<String , String> params = new HashMap<>();
        try {
            params.put("query", URLEncoder.encode(srcStr, "UTF-8"));
            params.put("from", "en");
            params.put("to", "zh");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        serviceInterface.getTranslate(params, ConstDef.apikey,
                callback);

    }
}
