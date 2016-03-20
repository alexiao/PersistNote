package com.dv.persistnote.base.network;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface GitService {

    @GET("/users/{user}")      // here is the other url part.best way is to start using /
    public void getFeed(@Path("user") String user, Callback<Gitmodel> response);
    // string user is for passing values from edittext for eg: user=basil2style,google
    // response is the response from the server which is now in the POJO
}