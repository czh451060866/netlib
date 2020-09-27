package com.example.api;



import com.example.bean.GetWithParamResult;
import com.example.bean.PostFileResult;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;

public interface Api {
    @GET("/get/param")
    Call<GetWithParamResult> getWithParamMap(@QueryMap Map<String, Object> params);

    @Multipart
    @POST("/file/upload")
    Observable<PostFileResult> postFiles(@Part List<MultipartBody.Part> part);
}
