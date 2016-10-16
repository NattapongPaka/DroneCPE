package com.example.user.dronecpe.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.user.dronecpe.model.DroneModel;
import com.example.user.dronecpe.model.GPSTracker;
import com.orhanobut.logger.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;

/**
 * Created by USER on 7/5/2559.
 */
public class DroneController implements DroneModel.OnJoystickMoveListener {

    private Context context;
    private String TAG = DroneController.class.getSimpleName();
    private SocketIncomeThread mThread;
    private String response = "";

    private String dstAddress;  //drone ip or host ip
    private int dstPort;        //drone port or host port  -> out come

    public int PORT_IN = 6000;

    private LocalBroadcastManager mBroadcastManager;
    private ServerSocket serverSocket;

    private DroneModel mDroneModel = DroneApp.getInstanceDroneModel();
    private GPSTracker mGpsTracker;
    private long LOCATION_UPDATE_INTERVAL = 100;
    private long LOCATION_TIMEOUT_IN_SECONDS = 10;
    private float SUFFICIENT_ACCURACY = 100.0f;

    public DroneController(Context c, String dstAddress, int dstPort) {
        this.mGpsTracker = new GPSTracker();
        this.context = c;
        this.dstAddress = dstAddress;
        this.dstPort = dstPort;
        mDroneModel.setOnJoystickMoveListener(this);
        mBroadcastManager = LocalBroadcastManager.getInstance(c);
        try {
            //mThread = new Thread(new SocketIncomeThread());
            mThread = new SocketIncomeThread();
            mThread.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void stopSocketIncomeThread(){
        if(mThread != null) {
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
        try{
            SendData(droneModel.getJoyDirection());
            Logger.d(droneModel.getJoyDirection());
        }catch (Exception ex){
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
                //Logger.d("Data available %d",dataInputStream.available());
                if(dataInputStream.available() > 0) {
                    while ((msgResponse = dataInputStream.readLine()) != null) {
                        //Logger.d("Data response %s",msgResponse);
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
            Log.d(TAG,"interrupt shouldStop:"+String.valueOf(shouldStop));
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
                    Log.d(TAG,"shouldStop in while : "+String.valueOf(shouldStop)+":isInterrupt : "+String.valueOf(isInterrupted()));
                    socket = serverSocket.accept();
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    String msgResponse = null;
                    while ((msgResponse = dataInputStream.readLine()) != null) {
                        ParseCommand(msgResponse);
                    }
                    Log.i("msg_form_server", String.valueOf(msgResponse));
                }
                if(isInterrupted()){
                    Log.d(TAG,"shouldStop : "+String.valueOf(shouldStop));
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
     * @param msgIn
     */
    private void ParseCommand(String msgIn) {
        String[] msg1 = msgIn.split(":");
        Log.i("msg_form_server", msgIn);
        for (String m : msg1) {
            String[] msg2 = m.split(",");
            String KEY = msg2[0].trim();
            String VALUE = msg2[1].trim();

            if (KEY.equals(DroneAPI.DRONE_READY)) {
                Log.i("DRONE_READY", VALUE);
                mDroneModel.setReady(VALUE);
            } else if (KEY.equals(DroneAPI.DRONE_BATTERY)) {
                Log.i("DRONE_BATTERY", VALUE);
                mDroneModel.setBattery(VALUE);
            } else if (KEY.equals(DroneAPI.DRONE_SIGNAL_WIFI)) {
                Log.i("DRONE_SIGNAL_WIFI", VALUE);
                mDroneModel.setSignalWifi(VALUE);
            } else if (KEY.equals(DroneAPI.DRONE_GPS)) {
                Log.i("DRONE_GPS", VALUE);
                mDroneModel.setGps(VALUE);
            }
            Log.i("loop_msg_form_server", KEY + "->" + VALUE);
        }
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
//                    Log.d(TAG,String.valueOf(lat)+":"+String.valueOf(lng));
//                }
//            }
//        });
//    }
}
