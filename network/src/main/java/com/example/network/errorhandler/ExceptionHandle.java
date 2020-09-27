package com.example.network.errorhandler;

import com.google.gson.JsonParseException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.ParseException;

import javax.net.ssl.SSLHandshakeException;

import androidx.annotation.Nullable;
import retrofit2.HttpException;

public class ExceptionHandle {

    // 401 Unauthorized
    public static final int UNAUTHORIZED = 401;

    // 403 Forbidden
    public static final int FORBIDDEN = 403;

    // 404 Not Found
    public static final int NOT_FOUND = 404;

    // 408 Request Timeout
    public static final int REQUEST_TIMEOUT = 408;

    //  --- 5xx Server Error ---

    // 500 Server Error
    public static final int INTERNAL_SERVER_ERROR = 500;

    // 502 Bad Gateway
    public static final int BAD_GATEWAY = 502;

    // 503 Service Unavailable
    public static final int SERVICE_UNAVAILABLE = 503;

    // 504 Gateway Timeout
    public static final int GATEWAY_TIMEOUT = 504;

    public static ResponseThrowable handleException(Throwable e){
        ResponseThrowable ex;
        if(e instanceof HttpException){
            HttpException httpException = (HttpException)e;
            ex = new ResponseThrowable(e, ERROR.HTTP_ERROR);
            switch (httpException.code()){
                case UNAUTHORIZED:
                case FORBIDDEN:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    ex.message = "网络错误";
                    break;
            }
            return ex;
        }else if(e instanceof ServerException){
            ServerException resultException = (ServerException)e;
            ex = new ResponseThrowable(resultException, resultException.code);
            ex.message = resultException.message;
            return ex;
        }else if(e instanceof JsonParseException ||
                e instanceof JSONException ||
                e instanceof ParseException){
            ex = new ResponseThrowable(e, ERROR.PARSE_ERROR);
            ex.message = "解析错误";
            return ex;
        }else if(e instanceof ConnectException){
            ex = new ResponseThrowable(e, ERROR.NETWORK_ERROR);
            ex.message = "连线失败";
            return ex;
        }else if(e instanceof SSLHandshakeException){
            ex = new ResponseThrowable(e, ERROR.SSL_ERROR);
            ex.message = "证书验证失败";
            return ex;
        }else if(e instanceof ConnectTimeoutException || e instanceof SocketTimeoutException){
            ex = new ResponseThrowable(e, ERROR.TIME_OUT_ERROR);
            ex.message = "链接超时";
            return ex;
        }else {
            ex = new ResponseThrowable(e, ERROR.UNKNOWN);
            ex.message = "未知错误";
            return ex;
        }

    }

    public class ERROR{
        /*
         *未知错误
         */
        public static final int UNKNOWN = 1000;
        /*
         *解析错误
         */
        public static final int PARSE_ERROR = 1001;
        /*
         *网络错误
         */
        public static final int NETWORK_ERROR = 1002;
        /*
         *协议出错
         */
        public static final int HTTP_ERROR = 1003;
        /*
         *证书出错
         */
        public static final int SSL_ERROR = 1005;
        /*
         *链接超时
         */
        public static final int TIME_OUT_ERROR = 1006;
    }

    public static class ResponseThrowable extends Exception{
        public int code;
        public String message;

        public ResponseThrowable(Throwable throwable, int code){
            super(throwable);
            this.code = code;
        }

        @Nullable
        @Override
        public String getMessage() {
            return message;
        }
    }

    public static class ServerException extends RuntimeException{
        public int code;
        public String message;
    }
}
