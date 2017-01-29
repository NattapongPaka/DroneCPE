package com.example.user.dronecpe.util;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.example.user.dronecpe.activity.Contextor;
import com.example.user.dronecpe.activity.DroneAPI;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by DEV on 29/1/2560.
 */

public class NetworkUtil {
    private static NetworkUtil instance;
    private Context context;
    private Random random;

    public static NetworkUtil getInstance() {
        if (instance == null) {
            instance = new NetworkUtil();
        }
        return instance;
    }

    private NetworkUtil() {
        context = Contextor.getInstance().getContext();
        random = new Random();
    }

    public String getIPAddressIP4() {
        WifiManager wm = (WifiManager) context.getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    public String generateFakeResponseFromRPI(){
        String cmdFakeRandom[] = {DroneAPI.DRONE_READY_PARAM, DroneAPI.DRONE_BATTERY_PARAM, DroneAPI.DRONE_SIGNAL_WIFI_PARAM, DroneAPI.DRONE_GPS_PARAM, DroneAPI.DRONE_RESET_PARAM};
        String cmd = Util.getInstance().randomObject(Arrays.asList(cmdFakeRandom));
        String cmdOut = "";
        String value;
        switch (cmd) {
            case DroneAPI.DRONE_READY_PARAM:
                value =  String.format(Locale.getDefault(), "%04d", 1);
                cmdOut = DroneAPI.PREFIX + "," + cmd + "," + String.valueOf(value) + "," + DroneAPI.SUFFIX;
                break;

            case DroneAPI.DRONE_BATTERY_PARAM:
                value = String.format(Locale.getDefault(), "%04d", Util.getInstance().randomNum(100));
                cmdOut = DroneAPI.PREFIX + "," + cmd + "," + String.valueOf(value) + "," + DroneAPI.SUFFIX;
                break;

            case DroneAPI.DRONE_SIGNAL_WIFI_PARAM:
                value =  String.format(Locale.getDefault(), "%04d", Util.getInstance().randomNum(100));
                cmdOut = DroneAPI.PREFIX + "," + cmd + "," + String.valueOf(value) + "," + DroneAPI.SUFFIX;
                break;

            case DroneAPI.DRONE_GPS_PARAM:
                value = String.format(Locale.getDefault(), "%04d", 1);
                cmdOut = DroneAPI.PREFIX + "," + cmd + "," + String.valueOf(value) + "," + DroneAPI.SUFFIX;
                break;

            case DroneAPI.DRONE_RESET_PARAM:
                value = String.format(Locale.getDefault(), "%04d", 1);
                cmdOut = DroneAPI.PREFIX + "," + cmd + "," + String.valueOf(value) + "," + DroneAPI.SUFFIX;
                break;

            default:
                break;
        }
        return cmdOut;
    }



}
