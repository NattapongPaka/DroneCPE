package com.example.user.dronecpe.activity;

/**
 * Created by USER on 8/5/2559.
 */
public class DroneAPI {

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
    public static final String DRONE_PITCH_DOWN = "100";
    public static final String DRONE_PITCH_UP = "101";
    public static final String DRONE_ROLL_LEFT = "102";
    public static final String DRONE_ROLL_RIGHT = "103";
    public static final String DRONE_THROTTLE_UP = "104";
    public static final String DRONE_THROTTLE_DOWN = "105";
    public static final String DRONE_YAW_LEFT = "106";
    public static final String DRONE_YAW_RIGHT = "107";

    //Gimbal Axis
    public static final String DRONE_GIMBAL_AXIS_YAW = "108";
    public static final String DRONE_GIMBAL_AXIS_PITCH = "109";
    public static final String DRONE_GIMBAL_AXIS_ROLL = "110";

}
