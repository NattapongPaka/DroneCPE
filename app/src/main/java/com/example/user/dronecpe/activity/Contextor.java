package com.example.user.dronecpe.activity;

import android.content.Context;

/**
 * Created by Dev on 7/10/2559.
 */

public class Contextor {

    private Context context;
    private static Contextor instance;

    public static Contextor getInstance() {
        if (instance == null) {
            instance = new Contextor();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext(){
        return context;
    }

}
