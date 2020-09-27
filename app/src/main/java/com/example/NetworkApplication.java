package com.example;

import android.app.Application;

import com.example.base.NetworkApi;

public class NetworkApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NetworkApi.init(new NetworkInfo());
    }
}
