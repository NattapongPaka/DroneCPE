package com.example.user.dronecpe.model;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.user.dronecpe.activity.DroneApp;
import com.example.user.dronecpe.activity.MainActivity;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Dev on 25/9/2559.
 */

public class GPSTracker extends Service implements LocationListener {

    private Context mContext;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;
    private Location location; // location
    private double latitude; // latitude
    private double longitude; // longitude
    private LocalBinder mLocalBinder = new LocalBinder();
    private LocationManager locationManager;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000; // 1 minute
    private String TAG = GPSTracker.class.getSimpleName();
    private DroneModel mDroneModel = DroneApp.getInstanceDroneModel();

    public class LocalBinder extends Binder {
        public LocalBinder getService() {
            return this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

//    public GPSTracker(Context context) {
//        this.mContext = context;
//        //getLocation();
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        location = getLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        mDroneModel.setLocationPlayer(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        mDroneModel.setProviderEnabled(true);
        updateLocation();
    }

    @Override
    public void onProviderDisabled(String s) {
        mDroneModel.setProviderEnabled(false);
        updateLocation().unsubscribe();
    }

    public boolean checkSelfPermission(Context context,String requestPermission){
            if (ContextCompat.checkSelfPermission(context,requestPermission) == PackageManager.PERMISSION_GRANTED){
                return true;
            }
        return false;
    }

    public Subscription updateLocation(){
        return Observable.timer(3000, TimeUnit.MILLISECONDS, Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Location location = getLocation();
                        if(location != null){
                            mDroneModel.setLocationPlayer(location);
                        }
                    }
                });
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                return null;
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    if(checkSelfPermission(mContext,Manifest.permission.ACCESS_FINE_LOCATION) && checkSelfPermission(mContext,Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }else{
                        Log.d(TAG, "getLocation: permission denied");
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        if(checkSelfPermission(mContext,Manifest.permission.ACCESS_FINE_LOCATION) && checkSelfPermission(mContext,Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }else {
                            Log.d(TAG, "getLocation: permission denied");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }
}
