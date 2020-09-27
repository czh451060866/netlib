package com.example;

import com.example.base.NetworkApi;

import io.reactivex.functions.Function;
import okhttp3.Interceptor;

public class TestNetworkApi extends NetworkApi {
    private static volatile TestNetworkApi sInstance;

    public static TestNetworkApi getInstance(){
        if(sInstance == null){
            synchronized (TestNetworkApi.class){
                if(sInstance == null){
                    sInstance = new TestNetworkApi();
                }
            }
        }
        return sInstance;
    }

    private TestNetworkApi(){
        super();
    }

    public static <T> T getService(Class<T> service){
        return getInstance().getRetrofit(service).create(service);
    }

    @Override
    protected Interceptor getInterceptor() {
        return null;
    }

    @Override
    protected <T> Function<T, T> getAppErrorHandler() {
        return null;
    }

    @Override
    public String getFormal() {
        return "http://10.4.141.11:9102"; //正式环境
    }

    @Override
    public String getTest() {
        return "http://10.4.141.11:9102";  //测试环境
    }
}
