package com.example.network.errorhandler;


import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * HttpErrorHandler处理以下两类网络错误
 * 1. http请求相关的错误，例如401，402，403
 * 2. 应用数据的错误会抛RuntimeException，最后也会走到这个函数处理
 */
public class HttpErrorHandler<T> implements Function<Throwable, Observable<T>> {
    @Override
    public Observable<T> apply(Throwable throwable) throws Exception {
        return Observable.error(ExceptionHandle.handleException(throwable));
    }

}
