package com.shelton.onelook.util;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


public class HttpUtil {

    private OkHttpClient okHttpClient;

    private HttpUtil() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();
    }

    private static class Holder {
        private static HttpUtil httpUtil = new HttpUtil();
    }

    public static OkHttpClient getHttpClient() {
        return Holder.httpUtil.okHttpClient;
    }

    public static HttpUtil getInstance() {
        return Holder.httpUtil;
    }
}
