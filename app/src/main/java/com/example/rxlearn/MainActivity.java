package com.example.rxlearn;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.TestNetworkApi;
import com.example.api.Api;
import com.example.bean.PostFileResult;
import com.example.base.NetworkApi;
import com.example.network.observer.BaseObserver;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ImageView mImageView;

    private static final int REQUEST_CODE = 1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.rx_image);

        int permissionResult =  checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionResult != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }

        List<MultipartBody.Part> parts = new ArrayList<>();
        MultipartBody.Part partOne = createPartByTypeAndKey("/storage/emulated/0/Download/1.jpeg", "file");
        parts.add(partOne);
        MultipartBody.Part partTwo = createPartByTypeAndKey("/storage/emulated/0/Download/1.jpeg", "file");
        parts.add(partTwo);
        MultipartBody.Part partThree = createPartByTypeAndKey("/storage/emulated/0/Download/1.jpeg", "file");
        parts.add(partThree);
        MultipartBody.Part partFour = createPartByTypeAndKey("/storage/emulated/0/Download/1.jpeg", "file");
        parts.add(partFour);
        TestNetworkApi.getService(Api.class).postFiles(parts).compose(TestNetworkApi.getInstance().applySchedulers(
                new BaseObserver<PostFileResult>() {
                    @Override
                    public void onSuccess(PostFileResult postFileResult) {
                        Log.d(TAG, new Gson().toJson(postFileResult));
                    }

                    @Override
                    public void onFailure(Throwable e) {

                    }
                }
        ));

//        doDrawableRxMethod();
    }

    private static MultipartBody.Part createPartByTypeAndKey(String path,String key){
        File file = new File(path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), requestBody);
        return part;
    }

    private void doSimpleStringRxMethod() {
        String[] list = {"数据结构", "计算机网络", "操作系统"};
        Observable.fromArray(list)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d("czhczh", s);
                    }
                });
    }

    private void doDrawableRxMethod() {
        final int resId = R.mipmap.ic_launcher;
        Observable.create(new ObservableOnSubscribe<Drawable>(){

            @Override
            public void subscribe(ObservableEmitter<Drawable> emitter) throws Exception {
                Drawable drawable = getTheme().getDrawable(resId);
                emitter.onNext(drawable);
                emitter.onComplete();
            }
        }).subscribe(new Observer<Drawable>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("czhczh", "onSubscribe");
            }

            @Override
            public void onNext(Drawable drawable) {
                mImageView.setImageDrawable(drawable);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d("czhczh", "onComplete");
            }
        });
    }
}
