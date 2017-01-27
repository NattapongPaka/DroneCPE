package com.example.user.dronecpe.util;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.user.dronecpe.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;

import java.util.List;


/**
 * Created by Dev on 13/12/2559.
 */

public class LogUtil {

    private static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeSpecialFloatingPointValues()
            .serializeNulls()
            .create();

    public static void D(@NonNull Class<?> tag, @NonNull String logMessage) {
        if (BuildConfig.DEBUG) {
            Log.d(tag.getSimpleName(), logMessage);
        }
    }

    public static void D(@NonNull String condition, @NonNull Object... message){
        if(BuildConfig.DEBUG){
            Logger.d(condition,message);
        }
    }

//    public static void D(@NonNull String tag, @NonNull String logMessage) {
//        if (BuildConfig.DEBUG) {
//            Log.d(tag, logMessage);
//        }
//    }

    public static <T> void D(@NonNull String tag, @NonNull List<T> objList) {
        if (BuildConfig.DEBUG) {
            for (Object obj : objList) {
                Log.d(tag, gson.toJson(obj));
            }
        }
    }

    public static <T> void E(@NonNull Class<?> tag, Throwable ex){
        if(BuildConfig.DEBUG){
            Log.e(tag.getSimpleName(),"Error : "+ex.toString());

        }
    }

    public static <T> void E(@NonNull String condition, @NonNull Object... message){
        if(BuildConfig.DEBUG){
            Logger.e(condition,message);
        }
    }
}
