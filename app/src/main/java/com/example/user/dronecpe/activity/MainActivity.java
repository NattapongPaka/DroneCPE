package com.example.user.dronecpe.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.dronecpe.R;
import com.example.user.dronecpe.hotspot.WifiApManager;
import com.example.user.dronecpe.model.DroneModel;
import com.example.user.dronecpe.model.GPSTracker;
import com.example.user.dronecpe.model.GPSTracker.LocalBinder;
import com.example.user.dronecpe.preference.UtilPreference;
import com.example.user.dronecpe.qaction.ActionItem;
import com.example.user.dronecpe.qaction.QuickAction;
import com.example.user.dronecpe.view.AccelerometerView;
import com.example.user.dronecpe.view.DialogSetting;
import com.example.user.dronecpe.view.JoystickView;
import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements DroneModel.OnGyroSensorListener, DroneModel.OnReadyListener, DroneModel.OnBatteryListener, DroneModel.OnSignalWifiListener, DroneModel.OnGPSListener
        , OnClickListener, QuickAction.OnActionItemClickListener, DroneModel.OnGPSPlayerListener {
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    private ImageView arrow_up3;
    private ImageView arrow_up4;

    private TextView angleTextViewLeft;
    private TextView powerTextViewLeft;
    private TextView directionTextViewLeft;

    private TextView angleTextViewRight;
    private TextView powerTextViewRight;
    private TextView directionTextViewRight;

    private TextView txtLatLng;
    private TextView txtGPS;
    private ImageView imgGPS;

    private TextView txtBettery;
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
    private static final int ID_SETTING = 6;

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
    private LocalBinder myService;
    private boolean isBound = false;


    /**
     * Activity Life cycle
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (myService != null) {
            Intent intent = new Intent(this, GPSTracker.class);
            bindService(intent, myConnection, BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onDestroy() {
        if(wifiApManager != null){
            wifiApManager.setWifiApEnabled(null, false);
        }
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mySensorEventListener);
        }
        if (mDroneController != null) {
            mDroneController.stopSocketIncomeThread();
            mDroneController = null;
        }
        if (myService != null) {
            unbindService(myConnection);
            myService = null;
            isBound = false;
        }
        super.onDestroy();
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


    /**
     * Check permission for api >= 23
     */
    private void requestPermissionLocation() {
        List<String> permissionsNeeded = new ArrayList<String>();
        List<String> permissionsList = new ArrayList<String>();

        if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            permissionsNeeded.add("GPS");
        }
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionsNeeded.add("AGPS");
        }
        /**
         * Show dialog request permission with first run app
         */
        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++) {
                    message = message + ", " + permissionsNeeded.get(i);
                }
                showMessageOKCancel(message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                permissionsList.toArray(new String[permissionsList.size()]),
                                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                    }
                });
                return;
            }
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
    }

    /**
     * Check permission never ask again
     * @param permissionsList
     * @param permission
     * @return
     */
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
                return false;
        }
        return true;
    }

    /**
     * Generate alert dialog
     *
     * @param message
     * @param okListener
     */
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                HashMap<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    Toast.makeText(MainActivity.this, "Some Permission is Granted", Toast.LENGTH_SHORT).show();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;

            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
            break;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initializing
        InitLayout();
        InitView();
        InitEvent();
        requestPermissionLocation();
        wifiApManager = new WifiApManager(this);
        startService(new Intent(this, GPSTracker.class));
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
        ActionItem Open = new ActionItem(ID_OPEN, "OpenHotspot", ActivityCompat.getDrawable(this, R.drawable.ic_wifi_tethering_grey_600_48dp));
        ActionItem Close = new ActionItem(ID_CLOSE, "CloseHotspot", ActivityCompat.getDrawable(this, R.drawable.ic_portable_wifi_off_grey_600_48dp));
        //ActionItem Connect = new ActionItem(ID_CONNECT, "Connect", getResources().getDrawable(R.drawable.ic_wifi_icon));
        //ActionItem Rec = new ActionItem(ID_VDO, "Rec", getResources().getDrawable(R.drawable.ic_wifi_icon));
        ActionItem Camera = new ActionItem(ID_CAMERA, "Camera", ActivityCompat.getDrawable(this, R.drawable.ic_linked_camera_grey_600_48dp));
        ActionItem Setting = new ActionItem(ID_SETTING, "Setting", ActivityCompat.getDrawable(this, R.drawable.ic_edit_grey_600_48dp));

        mQuickAction = new QuickAction(this);
        mQuickAction.addActionItem(Open);
        mQuickAction.addActionItem(Close);
//        mQuickAction.addActionItem(Connect);
//        mQuickAction.addActionItem(Rec);
        mQuickAction.addActionItem(Camera);
        mQuickAction.addActionItem(Setting);
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
        arrow_up3 = (ImageView) findViewById(R.id.arrow_up3);
        arrow_up4 = (ImageView) findViewById(R.id.arrow_up4);
        txtGPS = (TextView) findViewById(R.id.txtGPS);
        txtLatLng = (TextView) findViewById(R.id.txtLatLng);
        imgGPS = (ImageView) findViewById(R.id.imgGPS);
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
        joystickLeft.setJoyID(JoystickView.JOY_LEFT);
        joystickRight = (JoystickView) findViewById(R.id.joystickView2);
        joystickRight.setJoyID(JoystickView.JOY_RIGHT);
        txtWifiSignal = (TextView) findViewById(R.id.txtWifiSignal);
        txtBettery = (TextView) findViewById(R.id.txtBettery);
        mjpegView = (MjpegView) findViewById(R.id.mjpegViewDefault);
        btnSetting.setOnClickListener(this);
        Setting();
    }

    public void InitEvent() {
        // Register Model Listener
        mDroneModel.setOnGyroSensorListener(this);
        mDroneModel.setOnReadyListener(this);
        mDroneModel.setOnBatteryListener(this);
        mDroneModel.setOnSignalWifiListener(this);
        mDroneModel.setOnGPSListener(this);
        mDroneModel.setOnGPSPlayerListener(this);
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
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSetting:
                mQuickAction.show(v);
                mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_LEFT);
                break;

//            case R.id.btnCameraMenu:
//                // custom dialog
//                final Dialog dialog = new Dialog(this);
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//                //dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//                dialog.setContentView(R.layout.layout_dialog);
//                dialog.show();
//                break;

            case R.id.btnInfo:
                break;

            case R.id.btnReset:
                //Reset drone
                mDroneModel.setDroneReset(DroneAPI.DRONE_RESET_VALUE);
                break;

            case R.id.btnTakeOff:
                //Drone Takeoff / landing
                Button button = (Button) v;
                if(mDroneModel.getDroneTakeOff() != null) {
                    if (mDroneModel.getDroneTakeOff().equals(DroneAPI.DRONE_TAKEOFF_VALUE)) {
                        button.setText(R.string.drone_taskOff);
                        arrow_up3.setImageResource(R.drawable.ic_drone_up_dark);
                        arrow_up4.setImageResource(R.drawable.ic_drone_up_dark);
                        mDroneModel.setDroneTakeOff(DroneAPI.DRONE_LANDING_VALUE);
                    } else {
                        button.setText(R.string.drone_landing);
                        arrow_up3.setImageResource(R.drawable.ic_drone_down_dark);
                        arrow_up4.setImageResource(R.drawable.ic_drone_down_dark);
                        mDroneModel.setDroneTakeOff(DroneAPI.DRONE_TAKEOFF_VALUE);
                    }
                }else{
                    //Set first task off
                    button.setText(R.string.drone_landing);
                    arrow_up3.setImageResource(R.drawable.ic_drone_down_dark);
                    arrow_up4.setImageResource(R.drawable.ic_drone_down_dark);
                    mDroneModel.setDroneTakeOff(DroneAPI.DRONE_TAKEOFF_VALUE);
                }
                break;
        }
    }

    /******************************************************************************
     * Joy stick left event
     * Throttle high
     * Throttle low
     * Yaw left
     * Yaw right
     *******************************************************************************/
    private JoystickView.OnJoystickMoveListener onJoystickMoveListenerLeft = new JoystickView.OnJoystickMoveListener() {
        String cmd;

        @Override
        public void onValueChanged(int angle, int power, int direction) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    angleTextViewLeft.setText(" " + String.valueOf(angle) + "°");
                    powerTextViewLeft.setText(" " + String.valueOf(power) + "%");
                    switch (direction) {
                        //Throttle high
                        case JoystickView.FRONT:
                            directionTextViewLeft.setText("front");
                            //cmd = DroneAPI.POST + "," + DroneAPI.DRONE_THROTTLE_UP + "," + String.valueOf(power) + "," + String.valueOf(angle);
                            mDroneModel.setJoyDirection(DroneAPI.POST,DroneAPI.DRONE_THROTTLE_UP,String.valueOf(power),String.valueOf(angle));
                            break;
                        case JoystickView.FRONT_RIGHT:
                            directionTextViewLeft.setText("front_right");
                            //cmd = DroneAPI.POST + "," + DroneAPI.DRONE_THROTTLE_UP + "," + String.valueOf(power) + "," + String.valueOf(angle);
                            mDroneModel.setJoyDirection(DroneAPI.POST,DroneAPI.DRONE_THROTTLE_UP,String.valueOf(power),String.valueOf(angle));
                            break;
                        case JoystickView.LEFT_FRONT:
                            directionTextViewLeft.setText("left_front");
                            //cmd = DroneAPI.POST + "," + DroneAPI.DRONE_THROTTLE_UP + "," + String.valueOf(power) + "," + String.valueOf(angle);
                            mDroneModel.setJoyDirection(DroneAPI.POST,DroneAPI.DRONE_THROTTLE_UP,String.valueOf(power),String.valueOf(angle));
                            break;

                        //Throttle low
                        case JoystickView.RIGHT_BOTTOM:
                            directionTextViewLeft.setText("right_bottom");
                            //cmd = DroneAPI.POST + "," + DroneAPI.DRONE_THROTTLE_DOWN + "," + String.valueOf(power) + "," + String.valueOf(angle);
                            mDroneModel.setJoyDirection(DroneAPI.POST,DroneAPI.DRONE_THROTTLE_DOWN,String.valueOf(power),String.valueOf(angle));
                            break;
                        case JoystickView.BOTTOM:
                            directionTextViewLeft.setText("bottom");
                            //cmd = DroneAPI.POST + "," + DroneAPI.DRONE_THROTTLE_DOWN + "," + String.valueOf(power) + "," + String.valueOf(angle);
                            mDroneModel.setJoyDirection(DroneAPI.POST,DroneAPI.DRONE_THROTTLE_DOWN,String.valueOf(power),String.valueOf(angle));
                            break;
                        case JoystickView.BOTTOM_LEFT:
                            directionTextViewLeft.setText("bottom_left");
                            //cmd = DroneAPI.POST + "," + DroneAPI.DRONE_THROTTLE_DOWN + "," + String.valueOf(power) + "," + String.valueOf(angle);
                            mDroneModel.setJoyDirection(DroneAPI.POST,DroneAPI.DRONE_THROTTLE_DOWN,String.valueOf(power),String.valueOf(angle));
                            break;

                        //Yaw right
                        case JoystickView.RIGHT:
                            directionTextViewLeft.setText("right");
                            //cmd = DroneAPI.POST + "," + DroneAPI.DRONE_YAW_RIGHT + "," + String.valueOf(power) + "," + String.valueOf(angle);
                            mDroneModel.setJoyDirection(DroneAPI.POST,DroneAPI.DRONE_YAW_RIGHT,String.valueOf(power),String.valueOf(angle));
                            break;

                        //Yaw left
                        case JoystickView.LEFT:
                            directionTextViewLeft.setText("left");
                            //cmd = DroneAPI.POST + "," + DroneAPI.DRONE_YAW_LEFT + "," + String.valueOf(power) + "," + String.valueOf(angle);
                            mDroneModel.setJoyDirection(DroneAPI.POST,DroneAPI.DRONE_YAW_LEFT,String.valueOf(power),String.valueOf(angle));
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
     * Pitch forward
     * Pitch backward
     * Roll left
     * Roll right
     *******************************************************************************/
    private JoystickView.OnJoystickMoveListener onJoystickMoveListenerRight = new JoystickView.OnJoystickMoveListener() {
        String cmd;

        @Override
        public void onValueChanged(int angle, int power, int direction) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    angleTextViewRight.setText(" " + String.valueOf(angle) + "°");
                    powerTextViewRight.setText(" " + String.valueOf(power) + "%");
                    switch (direction) {
                        //Pitch forward
                        case JoystickView.FRONT:
                            directionTextViewRight.setText("front");
                            //cmd = DroneAPI.POST + "," + DroneAPI.DRONE_PITCH_UP + "," + String.valueOf(power) + "," + String.valueOf(angle);
                            mDroneModel.setJoyDirection(DroneAPI.POST,DroneAPI.DRONE_PITCH_UP,String.valueOf(power),String.valueOf(angle));
                            break;
                        case JoystickView.FRONT_RIGHT:
                            directionTextViewRight.setText("front_right");
                            //cmd = DroneAPI.POST + "," + DroneAPI.DRONE_PITCH_UP + "," + String.valueOf(power) + "," + String.valueOf(angle);
                            mDroneModel.setJoyDirection(DroneAPI.POST,DroneAPI.DRONE_PITCH_UP,String.valueOf(power),String.valueOf(angle));
                            break;
                        case JoystickView.LEFT_FRONT:
                            directionTextViewRight.setText("left_front");
                            //cmd = DroneAPI.POST + "," + DroneAPI.DRONE_PITCH_UP + "," + String.valueOf(power) + "," + String.valueOf(angle);
                            mDroneModel.setJoyDirection(DroneAPI.POST,DroneAPI.DRONE_PITCH_UP,String.valueOf(power),String.valueOf(angle));
                            break;

                        //Pitch backward
                        case JoystickView.RIGHT_BOTTOM:
                            directionTextViewRight.setText("right_bottom");
                            //cmd = DroneAPI.POST + "," + DroneAPI.DRONE_PITCH_DOWN + "," + String.valueOf(power) + "," + String.valueOf(angle);
                            mDroneModel.setJoyDirection(DroneAPI.POST,DroneAPI.DRONE_PITCH_DOWN,String.valueOf(power),String.valueOf(angle));
                            break;
                        case JoystickView.BOTTOM:
                            directionTextViewRight.setText("bottom");
                            //cmd = DroneAPI.POST + "," + DroneAPI.DRONE_PITCH_DOWN + "," + String.valueOf(power) + "," + String.valueOf(angle);
                            mDroneModel.setJoyDirection(DroneAPI.POST,DroneAPI.DRONE_PITCH_DOWN,String.valueOf(power),String.valueOf(angle));
                            break;
                        case JoystickView.BOTTOM_LEFT:
                            directionTextViewRight.setText("bottom_left");
                            //cmd = DroneAPI.POST + "," + DroneAPI.DRONE_PITCH_DOWN + "," + String.valueOf(power) + "," + String.valueOf(angle);
                            mDroneModel.setJoyDirection(DroneAPI.POST,DroneAPI.DRONE_PITCH_DOWN,String.valueOf(power),String.valueOf(angle));
                            break;

                        //Roll right
                        case JoystickView.RIGHT:
                            directionTextViewRight.setText("right");
                            //cmd = DroneAPI.POST + "," + DroneAPI.DRONE_ROLL_RIGHT + "," + String.valueOf(power) + "," + String.valueOf(angle);
                            mDroneModel.setJoyDirection(DroneAPI.POST,DroneAPI.DRONE_ROLL_RIGHT,String.valueOf(power),String.valueOf(angle));
                            break;

                        //Roll left
                        case JoystickView.LEFT:
                            directionTextViewRight.setText("left");
                            //cmd = DroneAPI.POST + "," + DroneAPI.DRONE_ROLL_LEFT + "," + String.valueOf(power) + "," + String.valueOf(angle);
                            mDroneModel.setJoyDirection(DroneAPI.POST,DroneAPI.DRONE_ROLL_LEFT,String.valueOf(power),String.valueOf(angle));
                            break;

                        //None Action
                        default:
                            directionTextViewRight.setText("center");
                    }
                }
            });
        }
    };

    /**
     * Quick action event
     *
     * @param quickAction
     * @param pos
     * @param actionId
     */
    @Override
    public void onItemClick(QuickAction quickAction, int pos, int actionId) {
        ActionItem actionItem = quickAction.getActionItem(pos);
        if (actionId == ID_OPEN) {
            String droneIP = UtilPreference.getInstance().getIP(DialogSetting.HOST_IP_ID);
            int dronePort = UtilPreference.getInstance().getPort(DialogSetting.HOST_PORT_ID);

            if (droneIP != null && !droneIP.isEmpty() && dronePort != 0 && dronePort > 0) {
                //wifiApManager.setWifiApEnabled(null, true);
                mDroneController = new DroneController(this, droneIP, dronePort);
            } else {
                Toast.makeText(this, "Please setting!!!", Toast.LENGTH_SHORT).show();
                DialogSetting mDialogSetting = DialogSetting.newInstance();
                mDialogSetting.show(getSupportFragmentManager(), "TAG");
            }
        }
        if (actionId == ID_CLOSE) {
            //wifiApManager.setWifiApEnabled(null, false);
            if(mDroneController != null) {
                mDroneController.stopSocketIncomeThread();
            }
        }
        if (actionId == ID_SETTING) {
            DialogSetting mDialogSetting = DialogSetting.newInstance();
            mDialogSetting.show(getSupportFragmentManager(), "TAG");
        }
    }


    /**
     * Drone model listener
     *
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
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String msg = droneModel.getBattery();
                txtBettery.setText(msg);
            }
        });
    }

    @Override
    public void onGPSListener(DroneModel droneModel) {

    }

    @Override
    public void onReadyListener(DroneModel droneModel) {

    }

    @Override
    public void onSignalWifiListener(DroneModel droneModel) {
        Log.i("onSignalWifiListenerUI", droneModel.getSignalWifi());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String msg = droneModel.getSignalWifi();
                txtWifiSignal.setText(msg);
            }
        });
    }

    /**
     * Player GPS
     */
    private ServiceConnection myConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            myService = binder.getService();
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
            myService = null;
        }
    };

    @Override
    public void onLocationPlayer(DroneModel droneModel) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Location location = droneModel.getLocationPlayer();
                if (txtLatLng != null && location != null) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    txtLatLng.setText(String.format(Locale.getDefault(), "Lat %3.5f : Lng %3.5f", lat, lng));
                }
            }
        }, 10);
    }

    @Override
    public void onProviderEnabled(DroneModel droneModel) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (imgGPS != null) {
                    if (droneModel.isProviderEnabled()) {
                        imgGPS.setImageResource(R.drawable.ic_gps_fixed_black_24dp);
                        txtGPS.setText("On");
                    } else {
                        imgGPS.setImageResource(R.drawable.ic_gps_off_black_24dp);
                        txtGPS.setText("Off");
                        txtLatLng.setText("GPS signal not found");
                    }
                }
            }
        }, 10);
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
