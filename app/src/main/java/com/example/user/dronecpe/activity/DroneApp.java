package com.example.user.dronecpe.activity;

import android.app.Application;

import com.example.user.dronecpe.BuildConfig;
import com.example.user.dronecpe.model.DroneModel;
import com.facebook.stetho.Stetho;

/**
 * Created by USER on 8/5/2559.
 */
public class DroneApp extends Application {
    private static DroneModel droneModelInstance;

    public static DroneModel getInstanceDroneModel() {
        if (droneModelInstance == null) {
            droneModelInstance = new DroneModel();
        }
        return droneModelInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Contextor.getInstance().setContext(this);
        initStetho();
    }

    private void initStetho() {
        if (BuildConfig.DEBUG) {
            /**
             * Config for slqite
             */
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .build());

            /**
             * Config for realm db
             */
//            Stetho.initialize(
//                    Stetho.newInitializerBuilder(this)
//                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
//                            .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
//                            .build());

            /**
             * Config for realm db
             */
            /**
             RealmInspectorModulesProvider.builder(this)
             .withFolder(getCacheDir())
             .withEncryptionKey("encrypted.realm", key)
             .withMetaTables()
             .withDescendingOrder()
             .withLimit(1000)
             .databaseNamePattern(Pattern.compile(".+\\.realm"))
             .build()
             */
        }
    }

}
