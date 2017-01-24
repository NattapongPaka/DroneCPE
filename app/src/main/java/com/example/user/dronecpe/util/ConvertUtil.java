package com.example.user.dronecpe.util;

/**
 * Created by DEV on 24/1/2560.
 */

public class ConvertUtil {
    private static ConvertUtil instance;

    public static ConvertUtil getInstance(){
        if (instance == null) {
            instance = new ConvertUtil();
        }
        return instance;
    }



}
