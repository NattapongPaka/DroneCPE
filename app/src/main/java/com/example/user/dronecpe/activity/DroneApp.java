package com.example.user.dronecpe.activity;

import android.app.Application;

import com.example.user.dronecpe.model.DroneModel;

/**
 * Created by USER on 8/5/2559.
 */
public class DroneApp extends Application {
    private static DroneModel droneModelInstance;

    public static DroneModel getInstanceDroneModel(){
        if(droneModelInstance == null){
            droneModelInstance = new DroneModel();
        }
        return droneModelInstance;
    }
}
