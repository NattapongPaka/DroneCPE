package com.example.user.dronecpe.activity;

/**
 * Created by USER on 8/5/2559.
 */
public class DroneAPI {
    /**
     * Protocal
     */
    public static final String POST = "P";
    public static final String GET = "G";
    /**
     * API Income cmd from drone
     * API Prefix Drone_CMD_XXX
     */
    public static final String DRONE_READY = "1";
    public static final String DRONE_BATTERY = "2";
    public static final String DRONE_SIGNAL_WIFI = "3";
    public static final String DRONE_GPS = "4";
    /**
     * API Outcome cmd to drone
     * API Prefix Drone_CMD_XXX
     */
    //Direction
    public static final String DRONE_PITCH_DOWN = "1";
    public static final String DRONE_PITCH_UP = "2";
    public static final String DRONE_ROLL_LEFT = "3";
    public static final String DRONE_ROLL_RIGHT = "4";
    public static final String DRONE_THROTTLE_UP = "5";
    public static final String DRONE_THROTTLE_DOWN = "6";
    public static final String DRONE_YAW_LEFT = "7";
    public static final String DRONE_YAW_RIGHT = "8";

    //Gimbal Axis
    public static final String DRONE_GIMBAL_AXIS_YAW = "9";
    public static final String DRONE_GIMBAL_AXIS_PITCH = "10";
    public static final String DRONE_GIMBAL_AXIS_ROLL = "11";

    //Action
    public static final String DRONE_RESET = "1";
    public static final String DRONE_TAKEOFF = "1";

}
