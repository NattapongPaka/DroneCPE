package com.example.user.dronecpe.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.user.dronecpe.model.DroneModel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by USER on 7/5/2559.
 */
public class DroneController implements DroneModel.OnJoystickMoveListener {

    private String response = "";
    private String dstAddress;
    private int dstPort;

    public static String DRONE_IP = "192.168.43.176";
    public static final int PORT_OUT = 8000;
    public static final int PORT_IN = 9000;

    private LocalBroadcastManager mBroadcastManager;
    private ServerSocket serverSocket;

    private DroneModel mDroneModel = DroneApp.getInstanceDroneModel();

    public DroneController(Context c, String dstAddress, int dstPort) {
        this.dstAddress = dstAddress;
        this.dstPort = dstPort;
        mDroneModel.setOnJoystickMoveListener(this);
        mBroadcastManager = LocalBroadcastManager.getInstance(c);
        try {
            Thread mThread = new Thread(new SocketIncomeThread());
            mThread.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private synchronized void SendData(String value) {
        SocketOutcomeTask mSocketOutcomeTask = new SocketOutcomeTask(this.dstAddress, dstPort, value);
        mSocketOutcomeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onJoystickMoveListener(DroneModel droneModel) {
        try{
            SendData(droneModel.getJoyDirection());
            Log.i("DroneController", droneModel.getJoyDirection());
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

        public SocketOutcomeTask(String ip, int port, String msgOut) {
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
                }

                String msgResponse;
                while ((msgResponse = dataInputStream.readLine()) != null) {
                    Log.i("readByte", String.valueOf(msgResponse));
                    ParseCommand(msgResponse);
                }

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
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
            }
            return null;
        }
    }

    /**
     * Thread Handler Package Incoming
     */
    private class SocketIncomeThread extends Thread {
        Socket socket = null;
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;

        @Override
        public void run() {
            socket = null;
            dataInputStream = null;
            dataOutputStream = null;
            try {
                serverSocket = new ServerSocket(PORT_IN);
                while (true) {
                    socket = serverSocket.accept();
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    String msgResponse = null;
                    while ((msgResponse = dataInputStream.readLine()) != null) {
                        ParseCommand(msgResponse);
                    }
                    Log.i("msg_form_server", String.valueOf(msgResponse));
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
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
}
