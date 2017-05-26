package com.example.user.dronecpe.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.user.dronecpe.BuildConfig;
import com.example.user.dronecpe.manager.HttpManager;
import com.example.user.dronecpe.model.DroneModel;
import com.example.user.dronecpe.model.GPSTracker;
import com.example.user.dronecpe.model.RecDao;
import com.example.user.dronecpe.util.CommandUtil;
import com.example.user.dronecpe.util.LogUtil;
import com.example.user.dronecpe.util.NetworkUtil;
import com.example.user.dronecpe.util.Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by USER on 7/5/2559.
 */
public class DroneController implements
        DroneModel.OnJoystickMoveListener,
        DroneModel.OnTakeOffListener,
        DroneModel.OnResetListener,
        DroneModel.OnSeekBarThrottleListener,
        DroneModel.OnSeekBarYawListener {

    private Context context;
    private String TAG = DroneController.class.getSimpleName();
    private SocketIncomeThread mThread;
    private String response = "";

    private String dstAddress;  //drone ip or host ip
    private int dstPort;        //drone port or host port  -> out come

    public int PORT_IN = 6000;

    private ServerSocket serverSocket;
    private DroneModel mDroneModel = DroneApp.getInstanceDroneModel();
    private GPSTracker mGpsTracker;

    private long LOCATION_UPDATE_INTERVAL = 100;
    private long LOCATION_TIMEOUT_IN_SECONDS = 10;
    private float SUFFICIENT_ACCURACY = 100.0f;

    private String lastSpeed;

    public DroneController(Context c, String dstAddress, int dstPort) {
        this.mGpsTracker = new GPSTracker();
        this.context = c;
        this.dstAddress = dstAddress;
        this.dstPort = dstPort;
        registerModelListener();
        try {
            if (BuildConfig.FakeResponse) {
                fakeResponseFromRPI();
            } else {
                //mThread = new Thread(new SocketIncomeThread());
                mThread = new SocketIncomeThread();
                mThread.start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fakeResponseFromRPI() {
        Observable.interval(1000, TimeUnit.MILLISECONDS, Schedulers.newThread())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        String cmd = NetworkUtil.getInstance().generateFakeResponseFromRPI();
                        ParseCommand(cmd);
                    }
                });
    }

    private void registerModelListener() {
        mDroneModel.setOnJoystickMoveListener(this);
        mDroneModel.setOnTakeOffListener(this);
        mDroneModel.setOnResetListener(this);
        mDroneModel.setOnSeekBarThrottleListener(this);
        mDroneModel.setOnSeekBarYawListener(this);
    }

//    public synchronized void initThread(){
//        SocketOutcomeTask mSocketOutcomeTask = new SocketOutcomeTask(this.dstAddress, dstPort, "initSocket");
//        mSocketOutcomeTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
//    }

    public synchronized void stopSocketIncomeThread() {
        if (mThread != null) {
            mThread.setShouldStop(true);
            mThread.interrupt();
        }
    }

    private synchronized void SendData(String value) {
        SocketOutcomeTask mSocketOutcomeTask = new SocketOutcomeTask(this.dstAddress, dstPort, value);
        mSocketOutcomeTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    @Override
    public void onJoystickMoveListener(DroneModel droneModel) {
        try {
            //String droneRequest = droneModel.getDroneRequest();
            String droneMode = droneModel.getDroneControlMode();
            String droneSpeed = droneModel.getDroneControlSpeed();
            String droneAngle = droneModel.getDroneControlAngle();
            //String cmd = appendCommand(droneRequest,droneMode,droneSpeed,droneAngle);
            //LogUtil.D("isModeGimBal %s",droneModel.isModeGimBal());
            if (droneModel.isModeGimBal()) {
                String cmd = CommandUtil.getInstance().getGimbalProtocal(droneMode, Integer.parseInt(droneSpeed), droneAngle);
                if (lastSpeed != null && !lastSpeed.isEmpty() && !droneSpeed.equals(lastSpeed)) {
                    SendData(cmd);
                    LogUtil.D("Send mode gimbal %s", cmd);
                }
                lastSpeed = droneSpeed;
            } else {
                String cmd = CommandUtil.getInstance().getDirectionProtocol(droneMode, droneSpeed, droneAngle);
                if (lastSpeed != null && !lastSpeed.isEmpty() && !droneSpeed.equals(lastSpeed)) {
                    SendData(cmd);
                    LogUtil.D("Send direction %s", cmd);
                }
                lastSpeed = droneSpeed;

//                if (lastSpeed != null && !lastSpeed.isEmpty() && droneSpeed.equals(lastSpeed)) {
//                    LogUtil.E("Not send request : lastSpeed : %s ", cmd);
//                } else {
//                    SendData(cmd);
//                    lastSpeed = droneSpeed;
//                    LogUtil.D("Send direction %s",cmd);
//                }
            }
            //SendData(droneModel.getJoyDirection());
            //LogUtil.D(droneModel.getJoyDirection());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void onReset(DroneModel droneModel) {
        try {
            String cmd = CommandUtil.getInstance().getCMDProtocol(DroneAPI.DRONE_RESET_PARAM, droneModel.getDroneReset());
            //String cmd = appendCommand(DroneAPI.POST,DroneAPI.DRONE_RESET,droneModel.getDroneReset(),"0");
            LogUtil.D(cmd);
            SendData(cmd);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onTakeOff(DroneModel droneModel) {
        try {
            String cmd = CommandUtil.getInstance().getCMDProtocol(DroneAPI.DRONE_ST_PARAM, droneModel.getDroneTakeOff());
            //String cmd = appendCommand(DroneAPI.POST,DroneAPI.DRONE_TAKEOFF,droneModel.getDroneTakeOff(),"0");
            LogUtil.D(cmd);
            SendData(cmd);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onThrottleChange(DroneModel droneModel) {
        try {
            if (!droneModel.isModeGimBal()) {
                String cmd = CommandUtil.getInstance().getDirectionProtocol(DroneAPI.DRONE_THROTTLE_PARAM, droneModel.getThottle());
                LogUtil.D(cmd);
                SendData(cmd);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onYawChange(DroneModel droneModel) {
        try {
            if (droneModel.isModeGimBal()) {
                String cmd = CommandUtil.getInstance().getGimbalProtocal(DroneAPI.DRONE_GIMBAL_AXIS_YAW, droneModel.getYaw(), "");
                LogUtil.D(cmd);
                SendData(cmd);
            } else {
                String cmd = CommandUtil.getInstance().getDirectionProtocol(DroneAPI.DRONE_YAW_PARAM, droneModel.getYaw());
                LogUtil.D(cmd);
                SendData(cmd);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Task send package outcome
     */
    private class SocketOutcomeTask extends AsyncTask<Void, Void, Void> {
        String msgOut;
        String ip;
        int port;

        SocketOutcomeTask(String ip, int port, String msgOut) {
            this.ip = ip;
            this.port = port;
            this.msgOut = msgOut;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;
            try {
                socket = new Socket(dstAddress, dstPort);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                if (msgOut != null) {
                    dataOutputStream.writeUTF(msgOut);
                    dataOutputStream.flush();
                }

                String msgResponse;
                //LogUtil.D("Data available %d",dataInputStream.available());
                if (dataInputStream.available() > 0) {
                    while ((msgResponse = dataInputStream.readLine()) != null) {
                        //LogUtil.D("Data response %s",msgResponse);
                        ParseCommand(msgResponse);
                    }
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    /**
     * Thread Handler Package Incoming
     */
    private class SocketIncomeThread extends Thread {
        Socket socket = null;
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;
        volatile boolean shouldStop = false;

        public void setShouldStop(boolean shouldStop) {
            this.shouldStop = shouldStop;
        }

        @Override
        public void interrupt() {
            super.interrupt();
            Log.d(TAG, "interrupt shouldStop:" + String.valueOf(shouldStop));
        }

        @Override
        public boolean isInterrupted() {
            return shouldStop;
        }

        @Override
        public void run() {
            socket = null;
            dataInputStream = null;
            dataOutputStream = null;
            try {
                serverSocket = new ServerSocket(PORT_IN);
                while (!shouldStop) {
                    Log.d(TAG, "shouldStop in while : " + String.valueOf(shouldStop) + ":isInterrupt : " + String.valueOf(isInterrupted()));
                    socket = serverSocket.accept();
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    String msgResponse = null;
                    while ((msgResponse = dataInputStream.readLine()) != null) {
                        ParseCommand(msgResponse);
                    }
                    Log.i("msg_form_server", String.valueOf(msgResponse));
                }
                if (isInterrupted()) {
                    Log.d(TAG, "shouldStop : " + String.valueOf(shouldStop));
                }
            } catch (IOException e) {
                Thread.currentThread().isInterrupted();
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Parse command before broadcast
     *
     * @param msgIn
     */
    private void ParseCommand(String msgIn) {
        LogUtil.D("msg_form_server %s", msgIn);
        if (msgIn.startsWith("S") && msgIn.endsWith("E")) {
            String[] cmdIn = msgIn.split(",");
            String KEY = cmdIn != null && !cmdIn[1].isEmpty() && cmdIn[1].length() == 3 ? cmdIn[1] : "";
            String VALUE = cmdIn != null && !cmdIn[1].isEmpty() && cmdIn[2].length() == 4 ? cmdIn[2] : "";
            VALUE = VALUE.trim();
            int val = Integer.parseInt(VALUE);
            if (KEY.equals(DroneAPI.DRONE_READY_PARAM)) {
                LogUtil.D("DRONE_READY %s", val);
                mDroneModel.setReady(String.valueOf(val));
            } else if (KEY.equals(DroneAPI.DRONE_BATTERY_PARAM)) {
                LogUtil.D("DRONE_BATTERY %s", val);
                mDroneModel.setBattery(String.valueOf(val));
            } else if (KEY.equals(DroneAPI.DRONE_SIGNAL_WIFI_PARAM)) {
                LogUtil.D("DRONE_SIGNAL_WIFI %s", val);
                mDroneModel.setSignalWifi(String.valueOf(val));
            } else if (KEY.equals(DroneAPI.DRONE_GPS_PARAM)) {
                LogUtil.D("DRONE_GPS %s", val);
                mDroneModel.setGps(String.valueOf(val));
            }
        }
    }

    public void takePhoto() {
        HttpManager.getInstance().getService().takePhoto()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(String s) {
                        mDroneModel.setResponse("Take photo success");
                        LogUtil.D("TakePhoto Response %s", s);
                    }
                });

    }

    public void recordVideo(boolean isStartRecord) {
        if (isStartRecord) {
            startRecord();
        } else {
            stopRecord();
        }
    }

    private void startRecord() {
        String tag = Util.getInstance().getCurrentTimeStamp();
        HttpManager.getInstance().getService().startRecordVideo(tag)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<RecDao>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(RecDao s) {
                        LogUtil.D("Start Record Response %s", s);
                    }
                });
    }

    private void stopRecord() {
        HttpManager.getInstance().getService().stopRecordVideo()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<RecDao>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(RecDao s) {
                        mDroneModel.setResponse("Record Success");
                        LogUtil.D("Stop Record Response %s", s);
                    }
                });
    }


/**
 * Get location
 */
//    public void getLocation(){
//        LocationRequest req = LocationRequest.create()
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setExpirationDuration(TimeUnit.SECONDS.toMillis(LOCATION_TIMEOUT_IN_SECONDS))
//                .setInterval(LOCATION_UPDATE_INTERVAL);
//
//        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(context);
//        Observable<Location> goodEnoughQuicklyOrNothingObservable = locationProvider.getUpdatedLocation(req)
//                .filter(new Func1<Location, Boolean>() {
//                    @Override
//                    public Boolean call(Location location) {
//                        return location.getAccuracy() < SUFFICIENT_ACCURACY;
//                    }
//                })
//                .timeout(LOCATION_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS, Observable.just((Location) null), AndroidSchedulers.mainThread())
//                .first()
//                .observeOn(AndroidSchedulers.mainThread());
//
//        goodEnoughQuicklyOrNothingObservable.subscribe(new Observer<Location>() {
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onNext(Location location) {
//                if(location != null){
//                    double lat = location.getLatitude();
//                    double lng = location.getLongitude();
//                    LogUtil.D("Update location %.4f %.4f",lat,lng);
//                }
//            }
//        });
//    }
//
//   private String appendCommand(String req, String droneControlMode, String droneControlSpeed, String droneControlAngle) {
//        return req + "," + droneControlMode + "," + droneControlSpeed + "," + droneControlAngle;
//    }

}
