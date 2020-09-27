package com.example.base;

import com.example.environment.IEnvironment;
import com.example.network.INetworkRequiredInfo;
import com.example.network.errorhandler.HttpErrorHandler;
import com.example.network.interceptor.CommonRequestInterceptor;
import com.example.network.interceptor.CommonResponseInterceptor;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class NetworkApi implements IEnvironment {
    private final String TAG = "NetworkApi";
    private static INetworkRequiredInfo iNetworkRequiredInfo;
    private HashMap<String, Retrofit> retrofitHashMap = new HashMap<>();
    private String BASE_URL;
    private static boolean mIsFormal = false;

    protected NetworkApi(){
        if(!mIsFormal){
            BASE_URL = getTest();
        }else{
            BASE_URL = getFormal();
        }

    }

    public static void init(INetworkRequiredInfo networkRequiredInfo) {
        iNetworkRequiredInfo = networkRequiredInfo;
    }

    public OkHttpClient getOkHttpClient(){
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        if(getInterceptor() != null){
            okHttpClientBuilder.addInterceptor(getInterceptor());
        }
        okHttpClientBuilder.addInterceptor(new CommonRequestInterceptor(iNetworkRequiredInfo));
        okHttpClientBuilder.addInterceptor(new CommonResponseInterceptor());
        if(iNetworkRequiredInfo != null && iNetworkRequiredInfo.isDebug()){
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClientBuilder.addInterceptor(httpLoggingInterceptor);
        }
        return okHttpClientBuilder.build();
    }

    public Retrofit getRetrofit(Class service){
        if(retrofitHashMap.get(BASE_URL + service.getName()) != null){
            return retrofitHashMap.get(BASE_URL + service.getName());
        }
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .client(getOkHttpClient())
                .build();
        retrofitHashMap.put(BASE_URL + service, retrofit);
        return retrofit;
    }

    public <T>ObservableTransformer<T, T> applySchedulers(final Observer<T> observer){
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                Observable<T> observable = (Observable<T>)upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(getAppErrorHandler())
                        .onErrorResumeNext(new HttpErrorHandler<T>());
                observable.subscribe(observer);
                return observable;
            }
        };
    }

    protected abstract Interceptor getInterceptor();

    protected abstract <T> Function<T, T> getAppErrorHandler();
}
