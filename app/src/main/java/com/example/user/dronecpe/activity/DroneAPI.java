package com.example.user.dronecpe.activity;

/**
 * Created by USER on 8/5/2559.
 */
public class DroneAPI {
    /**
     * Protocal
     */
    public static final String POST = "P";
    public static final String PREFIX = "S";
    public static final String SUFFIX = "E";
    public static final String GET = "G";
    /**
     * API Income cmd from drone
     * API Prefix Drone_CMD_XXX
     */
    public static final String DRONE_DEFAULT_PARAM = "000";
    public static final String DRONE_READY_PARAM = "001";
    public static final String DRONE_BATTERY_PARAM = "002";
    public static final String DRONE_SIGNAL_WIFI_PARAM = "003";
    public static final String DRONE_GPS_PARAM = "004";
    //Command data
    public static final String DRONE_READY_DATA = "0000";
    public static final String DRONE_BATTERY_DATA = "0000";
    /**
     * API Outcome cmd to drone
     * API Prefix Drone_CMD_XXX
     */
    //Direction
    public static final String DRONE_PITCH_PARAM = "005";
    public static final String DRONE_ROLL_PARAM = "006";
    public static final String DRONE_THROTTLE_PARAM = "007";
    public static final String DRONE_YAW_PARAM = "008";
    //Direction constance
    public static final int DRONE_PITCH_MAX = 60;
    public static final int DRONE_ROLL_MAX = 60;
    public static final int DRONE_THROTTLE_MAX = 2000;
    public static final int DRONE_THROTTLE_MIN = 1100;
    public static final int DRONE_YAW_MAX = 180;

    //Gimbal Axis
    public static final String DRONE_GIMBAL_AXIS_YAW = "009";
    public static final String DRONE_GIMBAL_AXIS_PITCH = "010";
    public static final String DRONE_GIMBAL_AXIS_ROLL = "011";
    //Gimbal constance
    public static final int DRONE_GIMBAL_3AXIS = 180;

    //Action
    public static final String DRONE_RESET_PARAM = "012";
    public static final String DRONE_ST_PARAM = "007";
    //Action data
    public static final String DRONE_RESET_DATA = "0000";
    public static final String DRONE_START_DATA = "1100";
    public static final String DRONE_STOP_DATA = "1000";

//    public static final String DRONE_PITCH_UP = "2";
//    public static final String DRONE_ROLL_LEFT = "3";
//    public static final String DRONE_ROLL_RIGHT = "4";
//    public static final String DRONE_THROTTLE_UP = "5";
//    public static final String DRONE_THROTTLE_DOWN = "6";
//    public static final String DRONE_YAW_LEFT = "7";
//    public static final String DRONE_YAW_RIGHT = "8";
//    public static final String DRONE_TAKEOFF = "12";
//    public static final String DRONE_LANDING = "14";
//    public static final String DRONE_RESET_VALUE = "1";
//    public static final String DRONE_TAKEOFF_VALUE = "1";
//    public static final String DRONE_LANDING_VALUE = "0";


}
