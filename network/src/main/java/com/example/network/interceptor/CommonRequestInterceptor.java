package com.example.network.interceptor;

import com.example.network.INetworkRequiredInfo;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.http.Headers;

public class CommonRequestInterceptor implements Interceptor {
    private INetworkRequiredInfo requiredInfo;

    public CommonRequestInterceptor(INetworkRequiredInfo requiredInfo){
        this.requiredInfo = requiredInfo;
    }
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        String timeStr = String.valueOf(System.currentTimeMillis());
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("token", "sdfjkljadfiosahfjkadsk");
        builder.addHeader("client", "android");
        builder.addHeader("version", "1.0");
        return chain.proceed(builder.build());
    }
}
