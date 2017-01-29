package com.example.user.dronecpe.util;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.example.user.dronecpe.activity.Contextor;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by DEV on 29/1/2560.
 */

public class NetworkUtil {
    private static NetworkUtil instance;
    private Context context;

    public static NetworkUtil getInstance(){
        if (instance == null) {
            instance = new NetworkUtil();
        }
        return instance;
    }

    private NetworkUtil(){
        context = Contextor.getInstance().getContext();
    }

    public String getIPAddressIP4(){
        WifiManager wm = (WifiManager)context.getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

}
