package com.example.user.dronecpe.preference;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.user.dronecpe.activity.Contextor;
import com.example.user.dronecpe.model.SettingModel;
import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by Dev on 7/10/2559.
 */

public class UtilPreference {
    private static UtilPreference instance;

    public static UtilPreference getInstance() {
        if (instance == null) {
            instance = new UtilPreference();
        }
        return instance;
    }

    public void setSharedPreference(String key, String value) {
        SharedPreferences sharedPreferencesCompat = PreferenceManager.getDefaultSharedPreferences(Contextor.getInstance().getContext());
        SharedPreferences.Editor mEditor = sharedPreferencesCompat.edit();
        mEditor.putString(key, value);
        mEditor.apply();
    }

    private SharedPreferences getSharedPreference(){
        return PreferenceManager.getDefaultSharedPreferences(Contextor.getInstance().getContext());
    }

    public Map<String, ?> getAllSharedPreference() {
        SharedPreferences sharedPreferencesCompat = PreferenceManager.getDefaultSharedPreferences(Contextor.getInstance().getContext());
        return sharedPreferencesCompat.getAll();
    }

    public String getIP(String key) {
        String result = getSharedPreference().getString(key,null);
        if(result != null && !result.isEmpty()){
            SettingModel settingModel = new Gson().fromJson(result,SettingModel.class);
            return settingModel.getTextValue();
        }
        return null;
    }

    public int getPort(String key) {
        String result = getSharedPreference().getString(key,null);
        if(result != null && !result.isEmpty()){
            try {
                SettingModel settingModel = new Gson().fromJson(result,SettingModel.class);
                return Integer.parseInt(settingModel.getTextValue());
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        return -1;
    }

}
