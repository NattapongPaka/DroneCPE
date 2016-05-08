package com.example.user.dronecpe.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.dronecpe.R;
import com.example.user.dronecpe.hotspot.WifiApManager;
import com.example.user.dronecpe.model.DroneModel;
import com.example.user.dronecpe.qaction.ActionItem;
import com.example.user.dronecpe.qaction.QuickAction;
import com.example.user.dronecpe.view.AccelerometerView;
import com.example.user.dronecpe.view.JoystickView;
import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegView;

public class MainActivity extends Activity implements DroneModel.OnGyroSensorListener,DroneModel.OnReadyListener,DroneModel.OnBatteryListener,DroneModel.OnSignalWifiListener,DroneModel.OnGPSListener
        ,OnClickListener,QuickAction.OnActionItemClickListener {
    private TextView angleTextViewLeft;
    private TextView powerTextViewLeft;
    private TextView directionTextViewLeft;

    private TextView angleTextViewRight;
    private TextView powerTextViewRight;
    private TextView directionTextViewRight;

    private TextView txtStatus;
    private TextView txtWifiSignal;
    // Importing also other views
    private JoystickView joystickLeft;
    private JoystickView joystickRight;
    private AccelerometerView mAccelerometer;
    private float[] acceleration = new float[3];

    private volatile Thread mThread;
    private Runnable mTimer1;
    private Handler mHandler = new Handler();

    private SensorManager mSensorManager;

    private ImageButton btnSetting;
    private QuickAction mQuickAction;

    private static final int ID_OPEN = 1;
    private static final int ID_CLOSE = 2;
    private static final int ID_CONNECT = 3;
    private static final int ID_VDO = 4;
    private static final int ID_CAMERA = 5;

//    private int count = 0;
//    private Socket mSocket;
//    private OutputStreamWriter mOutputStreamWriter;
//    private BufferedWriter mBufferedWriter;
//    private PrintWriter mPrintWriter;
//    private static final int SERVERPORT = 8888;
//    private static final String SERVER_IP = "192.168.43.195";

    private WifiApManager wifiApManager;
    private MjpegView mjpegView;

    private DroneController mDroneController;
    private DroneModel mDroneModel = DroneApp.getInstanceDroneModel();

    /**
     * Activity Life cycle
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        wifiApManager.setWifiApEnabled(null, false);
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mySensorEventListener);
        }
//        if (mThread != null) {
//            stopThread();
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mjpegView.stopPlayback();
        mSensorManager.unregisterListener(mySensorEventListener);
        mHandler.removeCallbacks(mTimer1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //loadIpCam();
        mTimer1 = new Runnable() {
            @Override
            public void run() {
                //mHandler.postDelayed(this,AccelerometerView.DEFAULT_LOOP_INTERVAL);
                //mDroneModel.setAcceleration(acceleration);
            }
        };
        mHandler.post(mTimer1);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initializing
        InitLayout();
        InitView();
        InitEvent();
        wifiApManager = new WifiApManager(this);
    }

    /**
     * All Method IP Camera
     */
    private DisplayMode calculateDisplayMode() {
        int orientation = getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_LANDSCAPE ?
                DisplayMode.FULLSCREEN : DisplayMode.BEST_FIT;
    }

    private void loadIpCam() {
        String userName = "";
        String pass = "";
        String url = "";
        int TIMEOUT = 5;
        Mjpeg.newInstance()
                .credential(userName, pass)
                .open(url, TIMEOUT)
                .subscribe(
                        inputStream -> {
                            mjpegView.setSource(inputStream);
                            mjpegView.setDisplayMode(calculateDisplayMode());
                            mjpegView.showFps(true);
                        },
                        throwable -> {
                            Log.e(getClass().getSimpleName(), "mjpeg error", throwable);
                            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
                        });
    }

    /**
     * Ref http://github.com/lorensiuswlt/NewQuickAction
     * Setting
     */
    public void Setting() {
        ActionItem Open = new ActionItem(ID_OPEN, "Open", getResources().getDrawable(R.drawable.ic_wifi_icon));
        ActionItem Close = new ActionItem(ID_CLOSE, "Close", getResources().getDrawable(R.drawable.ic_wifi_icon));
        ActionItem Connect = new ActionItem(ID_CONNECT, "Connect", getResources().getDrawable(R.drawable.ic_wifi_icon));
        ActionItem REC = new ActionItem(ID_VDO, "Rec", getResources().getDrawable(R.drawable.ic_wifi_icon));
        ActionItem CAMERA = new ActionItem(ID_CAMERA, "Camera", getResources().getDrawable(R.drawable.ic_wifi_icon));

        mQuickAction = new QuickAction(this);
        mQuickAction.addActionItem(Open);
        mQuickAction.addActionItem(Close);
        mQuickAction.addActionItem(Connect);
        mQuickAction.addActionItem(REC);
        mQuickAction.addActionItem(CAMERA);
        mQuickAction.setOnActionItemClickListener(this);
    }


    /**
     * All Initializing
     */
    public void InitLayout() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_main);
    }

    public void InitView() {
        mAccelerometer = (AccelerometerView) findViewById(R.id.gyroSensorView);
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mySensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        angleTextViewLeft = (TextView) findViewById(R.id.txt1);
        powerTextViewLeft = (TextView) findViewById(R.id.txt2);
        directionTextViewLeft = (TextView) findViewById(R.id.txt3);
        angleTextViewRight = (TextView) findViewById(R.id.txt4);
        powerTextViewRight = (TextView) findViewById(R.id.txt5);
        directionTextViewRight = (TextView) findViewById(R.id.txt6);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        btnSetting = (ImageButton) findViewById(R.id.btnSetting);
        joystickLeft = (JoystickView) findViewById(R.id.joystickView1);
        joystickRight = (JoystickView) findViewById(R.id.joystickView2);
        txtWifiSignal = (TextView) findViewById(R.id.txtWifiSignal);
        mjpegView = (MjpegView) findViewById(R.id.mjpegViewDefault);
        btnSetting.setOnClickListener(this);
        Setting();
    }

    public void InitEvent(){
        // Register Model Listener
        mDroneModel.setOnGyroSensorListener(this);
        mDroneModel.setOnReadyListener(this);
        mDroneModel.setOnBatteryListener(this);
        mDroneModel.setOnSignalWifiListener(this);
        mDroneModel.setOnGPSListener(this);
        // Register Joy Listener
        joystickLeft.setOnJoystickMoveListener(onJoystickMoveListenerLeft, JoystickView.DEFAULT_LOOP_INTERVAL);
        joystickRight.setOnJoystickMoveListener(onJoystickMoveListenerRight, JoystickView.DEFAULT_LOOP_INTERVAL);
    }
    /******************************************************************************
     * Sensor Listener
     *******************************************************************************/
    private SensorEventListener mySensorEventListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(event.values, 0, acceleration, 0, event.values.length);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    /**
     * All button event
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSetting:
                mQuickAction.show(v);
                mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_LEFT);
                break;

            case R.id.btnCameraMenu:
                // custom dialog
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                //dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                dialog.setContentView(R.layout.layout_dialog);
                dialog.show();
                break;

            case R.id.btnReset:
                //Reset drone
                break;

            case R.id.btnTakeOff:
                //Drone Takeoff / landing
                break;
        }
    }

    /******************************************************************************
     * Joy stick left event
     *******************************************************************************/
    private JoystickView.OnJoystickMoveListener onJoystickMoveListenerLeft = new JoystickView.OnJoystickMoveListener() {
        @Override
        public void onValueChanged(int angle, int power, int direction) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    angleTextViewLeft.setText(" " + String.valueOf(angle) + "°");
                    powerTextViewLeft.setText(" " + String.valueOf(power) + "%");
                    switch (direction) {
                        case JoystickView.FRONT:
                            directionTextViewLeft.setText("front");
                            break;
                        case JoystickView.FRONT_RIGHT:
                            directionTextViewLeft.setText("front_right");
                            break;
                        case JoystickView.RIGHT:
                            directionTextViewLeft.setText("right");
                            break;
                        case JoystickView.RIGHT_BOTTOM:
                            directionTextViewLeft.setText("right_bottom");
                            break;
                        case JoystickView.BOTTOM:
                            directionTextViewLeft.setText("bottom");
                            break;
                        case JoystickView.BOTTOM_LEFT:
                            directionTextViewLeft.setText("bottom_left");
                            break;
                        case JoystickView.LEFT:
                            directionTextViewLeft.setText("left");
                            break;
                        case JoystickView.LEFT_FRONT:
                            directionTextViewLeft.setText("left_front");
                            break;
                        default:
                            directionTextViewLeft.setText("center");
                    }
                }
            });
        }
    };

    /******************************************************************************
     * Joy stick right event
     *******************************************************************************/
    private JoystickView.OnJoystickMoveListener onJoystickMoveListenerRight = new JoystickView.OnJoystickMoveListener() {
        String buffer;
        String method = "POST,";
        @Override
        public void onValueChanged(int angle, int power, int direction) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    angleTextViewRight.setText(" " + String.valueOf(angle) + "°");
                    powerTextViewRight.setText(" " + String.valueOf(power) + "%");
                    switch (direction) {
                        case JoystickView.FRONT:
                            directionTextViewRight.setText("front");
                            buffer = method + DroneAPI.DRONE_PITCH_UP + "," + String.valueOf(power) + "," + String.valueOf(angle);
                            mDroneModel.setJoyDirection(buffer);
                            break;
                        case JoystickView.FRONT_RIGHT:
                            directionTextViewRight.setText("front_right");
                            mDroneModel.setJoyDirection(String.valueOf(power)+","+String.valueOf(angle));
                            break;
                        case JoystickView.RIGHT:
                            directionTextViewRight.setText("right");
                            mDroneModel.setJoyDirection(String.valueOf(power)+","+String.valueOf(angle));
                            break;
                        case JoystickView.RIGHT_BOTTOM:
                            directionTextViewRight.setText("right_bottom");
                            break;
                        case JoystickView.BOTTOM:
                            directionTextViewRight.setText("bottom");
                            break;
                        case JoystickView.BOTTOM_LEFT:
                            directionTextViewRight.setText("bottom_left");
                            break;
                        case JoystickView.LEFT:
                            directionTextViewRight.setText("left");
                            break;
                        case JoystickView.LEFT_FRONT:
                            directionTextViewRight.setText("left_front");
                            break;
                        default:
                            directionTextViewRight.setText("center");
                    }
                }
            });
        }
    };

    /**
     * Quick action event
     * @param quickAction
     * @param pos
     * @param actionId
     */
    @Override
    public void onItemClick(QuickAction quickAction, int pos, int actionId) {
        ActionItem actionItem = quickAction.getActionItem(pos);
        if (actionId == ID_OPEN) {
            wifiApManager.setWifiApEnabled(null, true);
            mDroneController = new DroneController(this,DroneController.DRONE_IP,DroneController.PORT_OUT);
        }
        if (actionId == ID_CLOSE) {
            wifiApManager.setWifiApEnabled(null, false);
        }
    }


    /**
     * Drone model listener
     * @param droneModel
     */
    @Override
    public void onGyroSensorChange(DroneModel droneModel) {
        try {
            float[] mValue = droneModel.getAcceleration();
            mAccelerometer.updateRotation(mValue);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void onBatteryListener(DroneModel droneModel) {

    }

    @Override
    public void onGPSListener(DroneModel droneModel) {

    }

    @Override
    public void onReadyListener(DroneModel droneModel) {

    }

    @Override
    public void onSignalWifiListener(DroneModel droneModel) {
        Log.i("onSignalWifiListenerUI",droneModel.getSignalWifi());
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                String msg = droneModel.getSignalWifi();
                txtWifiSignal.setText(msg);
            }
        });
    }

    /**
     * Broadcast
     */
//    private boolean mReceiversRegistered;
//    private void registerReceiver() {
//        unregisterReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(DroneController.ACTION_DRONE_READY);
//        intentFilter.addAction(DroneController.ACTION_DRONE_BATTERY);
//        intentFilter.addAction(DroneController.ACTION_DRONE_SIGNAL_WIFI);
//        intentFilter.addAction(DroneController.ACTION_DRONE_GPS);
//        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, intentFilter);
//        mReceiversRegistered = true;
//    }
//
//    private void unregisterReceiver() {
//        if (mReceiversRegistered) {
//            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
//            mReceiversRegistered = false;
//        }
//    }

//    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//        String value;
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(DroneController.ACTION_DRONE_READY)) {
//                value = intent.getStringExtra(DroneController.ACTION_DRONE_READY);
//            } else if (intent.getAction().equals(DroneController.ACTION_DRONE_BATTERY)) {
//                value = intent.getStringExtra(DroneController.ACTION_DRONE_BATTERY);
//            } else if (intent.getAction().equals(DroneController.ACTION_DRONE_SIGNAL_WIFI)) {
//                value = intent.getStringExtra(DroneController.ACTION_DRONE_SIGNAL_WIFI);
//            } else if (intent.getAction().equals(DroneController.ACTION_DRONE_GPS)) {
//                value = intent.getStringExtra(DroneController.ACTION_DRONE_GPS);
//            }
//        }
//    };

    /**
     * Code un use
     */
//    private void stopThread() {
//        boolean retry = true;
//        while (retry) {
//            try {
//                mThread.join();
//                retry = false;
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//    }

//    private void ScanClient() {
//        wifiApManager.getClientList(false, new FinishScanListener() {
//            @Override
//            public void onFinishScan(final ArrayList<ClientScanResult> clients) {
//                for (ClientScanResult clientScanResult : clients) {
//                    txtStatus.setText("Ip: " + clientScanResult.getIpAddr() + "\n");
//                    txtStatus.append("isReachable: " + clientScanResult.isReachable() + "\n");
//                }
//            }
//        });
//    }

//    private class UpdateClient extends TimerTask {
//        @Override
//        public void run() {
//            ScanClient();
//            mThread = new Thread(new SendDatatoClient());
//            mThread.setDaemon(true);
//            mThread.start();
//        }
//    }

//    private class SendDatatoClient implements Runnable {
//        @Override
//        public void run() {
//
//            try {
//                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
//                mSocket = new Socket(serverAddr, SERVERPORT);
//                mOutputStreamWriter = new OutputStreamWriter(mSocket.getOutputStream());
//                mBufferedWriter = new BufferedWriter(mOutputStreamWriter);
//                mPrintWriter = new PrintWriter(mBufferedWriter);
//
//            } catch (UnknownHostException e1) {
//                e1.printStackTrace();
//
//            } catch (IOException e1) {
//                e1.printStackTrace();
//                //Toast.makeText(getApplicationContext(), "Connot Connect", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
     /******************************************************************************
     // Sensor Register un use
     //*******************************************************************************/
//    public void GyroSensorRegister() {
//        if (mSensorManager != null) {
//            mSensorManager.registerListener(mySensorEventListener,
//                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//                    SensorManager.SENSOR_DELAY_NORMAL);
//        }
//    }



}
