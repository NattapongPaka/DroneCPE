package com.example.user.dronecpe.util;

import com.example.user.dronecpe.activity.DroneAPI;

import java.util.Comparator;
import java.util.Locale;

/**
 * Created by DEV on 24/1/2560.
 */

public class CommandUtil {
    private static CommandUtil instance;

    public static CommandUtil getInstance() {
        if (instance == null) {
            instance = new CommandUtil();
        }
        return instance;
    }

    /**
     * DRONE_READY	        001	0000	S0010000E
     * DRONE_BATTERY	    002	0000	S0020000E
     * DRONE_SIGNAL_WIFI	003	0000	S0030000E
     * DRONE_GPS	        004	0000	S0040000E
     * DRONE_RESET	        012	0000	S0120000E
     * DRONE_START	        007	1100	S0071100E
     * DRONE_STOP	        007	1000	S0071000E
     */
    public String getCMDProtocol(String param, String value) {
        return DroneAPI.PREFIX + param + value + DroneAPI.SUFFIX;
    }

    /**
     * DRONE_PITCH	            005	0000 to 0060	S0050030E
     * DRONE_ROLL	            006	0000 to 0060	S0060030E
     * DRONE_THROTTLE	        007	1100 to 2000	S0071100E
     * DRONE_YAW	            008	0000 to 0180	S0080030E
     * DRONE_GIMBAL_AXIS_YAW	009	0000 to 0180	S0090090E
     * DRONE_GIMBAL_AXIS_PITCH	010	0000 to 0180	S0100090E
     * DRONE_GIMBAL_AXIS_ROLL	011	0000 to 0180	S0110090E
     */
    public String getDirectionProtocol(String param, int value) {
        String cmd = null;
        switch (param) {
            case DroneAPI.DRONE_THROTTLE_PARAM:
                cmd = DroneAPI.PREFIX + param + String.format(Locale.getDefault(), "%04d", Math.min(DroneAPI.DRONE_THROTTLE_MAX, value)) + DroneAPI.SUFFIX;
                break;

            case DroneAPI.DRONE_YAW_PARAM:
                cmd = DroneAPI.PREFIX + param + String.format(Locale.getDefault(), "%04d", Math.min(DroneAPI.DRONE_YAW_MAX, value)) + DroneAPI.SUFFIX;
                break;

            case DroneAPI.DRONE_GIMBAL_AXIS_PITCH:
            case DroneAPI.DRONE_GIMBAL_AXIS_ROLL:
            case DroneAPI.DRONE_GIMBAL_AXIS_YAW:
                cmd = DroneAPI.PREFIX + param + String.format(Locale.getDefault(), "%04d", Math.min(DroneAPI.DRONE_GIMBAL_3AXIS, value)) + DroneAPI.SUFFIX;
                break;
        }
        return cmd;
    }

    public String getDirectionProtocol(String param, String value, String direction) {
        String cmd = null;
        switch (param) {
            case DroneAPI.DRONE_PITCH_PARAM:
            case DroneAPI.DRONE_ROLL_PARAM:
                int direc = Math.abs(Integer.parseInt(direction));
                int speed = 0;
                if (direc > 0 && direc < 90) {            //Forward
                    speed = map(Integer.parseInt(value), 0, 100, 0, 30);
                } else if (direc > 90 && direc < 180) {   //Backward
                    speed = map(Integer.parseInt(value), 0, 100, 30, 60);
                }
                cmd = DroneAPI.PREFIX + param + String.format(Locale.getDefault(), "%04d", speed) + DroneAPI.SUFFIX;
                break;
        }
        return cmd;
    }

    public int map(int x, int in_min, int in_max, int out_min, int out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
}
