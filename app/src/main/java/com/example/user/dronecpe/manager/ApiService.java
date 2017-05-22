package com.example.user.dronecpe.manager;

import com.example.user.dronecpe.model.RecDao;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.Observer;

/**
 * Created by nattapongpaka on 5/14/2017 AD.
 */

public interface ApiService {

    @GET("startvideo?force=1")
    Observable<RecDao> startRecordVideo(@Query("tag") String tag);

    @GET("stopvideo?force=1")
    Observable<RecDao> stopRecordVideo();

    @GET("photoaf_save_only.jpg")
    Observable<String> takePhoto();

}
