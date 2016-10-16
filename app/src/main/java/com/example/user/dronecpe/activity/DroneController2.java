//package com.example.user.dronecpe.activity;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.support.annotation.NonNull;
//import android.support.v4.content.LocalBroadcastManager;
//import android.util.Log;
//
//import com.example.user.dronecpe.model.DroneModel;
//import com.example.user.dronecpe.model.GPSTracker;
//import com.github.nkzawa.emitter.Emitter;
//import com.github.nkzawa.socketio.client.Ack;
//import com.github.nkzawa.socketio.client.IO;
//import com.github.nkzawa.socketio.client.Socket;
//import com.orhanobut.logger.Logger;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
////import java.net.ServerSocket;
////import java.net.Socket;
//import java.net.URISyntaxException;
//import java.net.UnknownHostException;
//
///**
// * Created by Dev on 15/10/2559.
// */
//
//public class DroneController2 implements DroneModel.OnJoystickMoveListener {
//
//
//    private Context context;
//    private String TAG = DroneController2.class.getSimpleName();
//    //private DroneController2.SocketIncomeThread mThread;
//    private String response = "";
//
//    private String dstAddress;  //drone ip or host ip
//    private int dstPort;        //drone port or host port  -> out come
//
//    public int PORT_IN = 6000;
//
//    private LocalBroadcastManager mBroadcastManager;
//    //private ServerSocket serverSocket;
//
//    private DroneModel mDroneModel = DroneApp.getInstanceDroneModel();
//    private GPSTracker mGpsTracker;
//    private long LOCATION_UPDATE_INTERVAL = 100;
//    private long LOCATION_TIMEOUT_IN_SECONDS = 10;
//    private float SUFFICIENT_ACCURACY = 100.0f;
//
//    private Socket socket;
//
//    public DroneController2(Context c, String dstAddress, int dstPort) {
//        this.mGpsTracker = new GPSTracker();
//        this.context = c;
//        this.dstAddress = dstAddress;
//        this.dstPort = dstPort;
//        mDroneModel.setOnJoystickMoveListener(this);
//        mBroadcastManager = LocalBroadcastManager.getInstance(c);
//        try {
//            //mThread = new DroneController2.SocketIncomeThread();
//            //mThread.start();
//
//            IO.Options options = new IO.Options();
//            options.port = dstPort;
//            options.forceNew = true;
//            options.multiplex = true;
//            options.reconnection = true;
//
//            String uri = "http://" + dstAddress + ":" + dstPort;
//            socket = IO.socket(uri, options);
//            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    Logger.d(args);
//                }
//            }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    Logger.d(args);
//                }
//            }).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    Logger.d(args);
//                }
//            }).on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    Logger.d(args);
//                }
//            }).on(Socket.EVENT_RECONNECT_ATTEMPT, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    Logger.d(args);
//                }
//            }).on(Socket.EVENT_RECONNECT_ERROR, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    Logger.d(args);
//                }
//            }).on(Socket.EVENT_RECONNECT_FAILED, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    Logger.d(args);
//                }
//            }).on(Socket.EVENT_RECONNECTING, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    Logger.d(args);
//                }
//            }).on(Socket.EVENT_ERROR, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    Logger.d(args);
//                }
//            });
//            socket.connect();
//            socket.send("First send init");
//            socket.emit(Socket.EVENT_MESSAGE,"Emit send message");
//        } catch (URISyntaxException ex) {
//            ex.printStackTrace();
//        }
//    }
//
//
//    public synchronized void stopSocketIncomeThread(){
////        if(mThread != null) {
////            mThread.setShouldStop(true);
////            mThread.interrupt();
////        }
//        if(socket != null) {
//            socket.disconnect();
//            socket.off(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//
//                }
//            });
//        }
//    }
//
//    private synchronized void SendData(String value) {
//        //DroneController2.SocketOutcomeTask mSocketOutcomeTask = new DroneController2.SocketOutcomeTask(this.dstAddress, dstPort, value);
//        //mSocketOutcomeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        //if(socket != null){
//            socket.emit(Socket.EVENT_MESSAGE, value, new Ack() {
//                @Override
//                public void call(Object... args) {
//                    Logger.d(args);
//                }
//            }).on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    Logger.d(args);
//                }
//            });
//            socket.open();
//        //}
//    }
//
//    public void onJoystickMoveListener(DroneModel droneModel) {
//        try{
//            SendData(droneModel.getJoyDirection());
//            socketLogger();
//            Log.i("DroneController", droneModel.getJoyDirection());
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//    }
//
//    public void socketLogger(){
//        if(socket != null){
//            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//
//                }
//            }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//
//                }
//            }).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//
//                }
//            }).on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//
//                }
//            }).on(Socket.EVENT_RECONNECT_ATTEMPT, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//
//                }
//            }).on(Socket.EVENT_RECONNECT_ERROR, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//
//                }
//            }).on(Socket.EVENT_RECONNECT_FAILED, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//
//                }
//            }).on(Socket.EVENT_RECONNECTING, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//
//                }
//            }).on(Socket.EVENT_ERROR, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//
//                }
//            }).on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    Object obj = args[0];
//                    Log.d(TAG, String.valueOf(obj));
//                }
//            });
//        }
//    }
//
//    /**
//     * Task send package outcome
//     */
//    private void SocketOutcomeTask(){
//
//    }
//
////    private class SocketOutcomeTask extends AsyncTask<Void, Void, Void> {
////        String msgOut;
////        String ip;
////        int port;
////
////        SocketOutcomeTask(String ip, int port, String msgOut) {
////            this.ip = ip;
////            this.port = port;
////            this.msgOut = msgOut;
////        }
////
////        @Override
////        protected Void doInBackground(Void... voids) {
////            Socket socket = null;
////            DataOutputStream dataOutputStream = null;
////            DataInputStream dataInputStream = null;
////
////            try {
////                socket = new Socket(dstAddress, dstPort);
////                dataOutputStream = new DataOutputStream(socket.getOutputStream());
////                dataInputStream = new DataInputStream(socket.getInputStream());
////
////                if (msgOut != null) {
////                    dataOutputStream.writeUTF(msgOut);
////                }
////
////                String msgResponse;
////                while ((msgResponse = dataInputStream.readLine()) != null) {
////                    Log.i("readByte", String.valueOf(msgResponse));
////                    ParseCommand(msgResponse);
////                }
////
////            } catch (UnknownHostException e) {
////                e.printStackTrace();
////                response = "UnknownHostException: " + e.toString();
////            } catch (IOException e) {
////                e.printStackTrace();
////                response = "IOException: " + e.toString();
////            } finally {
////                if (socket != null) {
////                    try {
////                        socket.close();
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                }
////
////                if (dataOutputStream != null) {
////                    try {
////                        dataOutputStream.close();
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                }
////
////                if (dataInputStream != null) {
////                    try {
////                        dataInputStream.close();
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                }
////            }
////            return null;
////        }
////    }
//    /**
//     * Thread Handler Package Incoming
//     */
////    private class SocketIncomeThread extends Thread {
////        Socket socket = null;
////        DataInputStream dataInputStream = null;
////        DataOutputStream dataOutputStream = null;
////        volatile boolean shouldStop = false;
////
////        public void setShouldStop(boolean shouldStop) {
////            this.shouldStop = shouldStop;
////        }
////
////        @Override
////        public void interrupt() {
////            super.interrupt();
////            Log.d(TAG,"interrupt shouldStop:"+String.valueOf(shouldStop));
////        }
////
////        @Override
////        public boolean isInterrupted() {
////            return shouldStop;
////        }
////
////        @Override
////        public void run() {
////            socket = null;
////            dataInputStream = null;
////            dataOutputStream = null;
////            try {
////                serverSocket = new ServerSocket(PORT_IN);
////                while (!shouldStop) {
////                    Log.d(TAG,"shouldStop in while : "+String.valueOf(shouldStop)+":isInterrupt : "+String.valueOf(isInterrupted()));
////                    socket = serverSocket.accept();
////                    dataInputStream = new DataInputStream(socket.getInputStream());
////                    String msgResponse = null;
////                    while ((msgResponse = dataInputStream.readLine()) != null) {
////                        ParseCommand(msgResponse);
////                    }
////                    Log.i("msg_form_server", String.valueOf(msgResponse));
////                }
////                if(isInterrupted()){
////                    Log.d(TAG,"shouldStop : "+String.valueOf(shouldStop));
////                }
////            } catch (IOException e) {
////                Thread.currentThread().isInterrupted();
////                e.printStackTrace();
////            } finally {
////                if (socket != null) {
////                    try {
////                        socket.close();
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                }
////
////                if (dataInputStream != null) {
////                    try {
////                        dataInputStream.close();
////                    } catch (IOException e) {
////                        // TODO Auto-generated catch block
////                        e.printStackTrace();
////                    }
////                }
////
////                if (dataOutputStream != null) {
////                    try {
////                        dataOutputStream.close();
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                }
////            }
////        }
////    }
//
//    /**
//     * Parse command before broadcast
//     * @param msgIn
//     */
//    private void ParseCommand(String msgIn) {
//        String[] msg1 = msgIn.split(":");
//        Log.i("msg_form_server", msgIn);
//        for (String m : msg1) {
//            String[] msg2 = m.split(",");
//            String KEY = msg2[0].trim();
//            String VALUE = msg2[1].trim();
//
//            if (KEY.equals(DroneAPI.DRONE_READY)) {
//                Log.i("DRONE_READY", VALUE);
//                mDroneModel.setReady(VALUE);
//            } else if (KEY.equals(DroneAPI.DRONE_BATTERY)) {
//                Log.i("DRONE_BATTERY", VALUE);
//                mDroneModel.setBattery(VALUE);
//            } else if (KEY.equals(DroneAPI.DRONE_SIGNAL_WIFI)) {
//                Log.i("DRONE_SIGNAL_WIFI", VALUE);
//                mDroneModel.setSignalWifi(VALUE);
//            } else if (KEY.equals(DroneAPI.DRONE_GPS)) {
//                Log.i("DRONE_GPS", VALUE);
//                mDroneModel.setGps(VALUE);
//            }
//            Log.i("loop_msg_form_server", KEY + "->" + VALUE);
//        }
//    }
//}
