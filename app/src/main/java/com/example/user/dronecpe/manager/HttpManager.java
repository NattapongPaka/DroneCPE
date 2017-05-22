package com.example.user.dronecpe.manager;

import com.example.user.dronecpe.BuildConfig;
import com.example.user.dronecpe.preference.UtilPreference;
import com.example.user.dronecpe.util.ToStringConverterFactory;
import com.example.user.dronecpe.view.DialogSetting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dev on 27/12/2559.
 */

public class HttpManager {
    private static HttpManager instatance;
    private String port = "8080";

    public static HttpManager getInstance() {
        if (instatance == null) {
            instatance = new HttpManager();
        }
        return instatance;
    }

    private ApiService service;
    private Retrofit retrofit;

    private HttpManager() {
        String ip = UtilPreference.getInstance().getIP(DialogSetting.CAMERA_IP_ID);
        if (ip != null && !ip.isEmpty()) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://" + ip + ":" + port + "/")
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gsonBuilder()))     //Converter to gson
                    .addConverterFactory(new ToStringConverterFactory())    //Converter to string
                    .build();
            service = retrofit.create(ApiService.class);
        }
    }

    private Gson gsonBuilder() {
        return new GsonBuilder()
                .setLenient()
                .serializeNulls()
                .disableHtmlEscaping()
                .disableInnerClassSerialization()
                .create();
    }

    private OkHttpClient customClient() {
        //HttpLoggingInterceptor interceptor = initInterceptor();

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.retryOnConnectionFailure(true);
        okHttpClient.followRedirects(true);
        okHttpClient.followSslRedirects(true);

        //okHttpClient.addInterceptor(interceptor);
        return okHttpClient.build();

//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
//        return new OkHttpClient.Builder().addInterceptor(interceptor).build();
    }

//    private HttpLoggingInterceptor initInterceptor() {
//        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
//    }

    public ApiService getService() {
        return service;
    }

}
