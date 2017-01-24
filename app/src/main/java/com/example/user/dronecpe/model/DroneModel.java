package com.example.user.dronecpe.model;

import android.location.Location;

public class DroneModel {

	private float[] acceleration = new float[3];
	private int angle;
	private int power;
	private int direction;

	private String battery;
	private String signalWifi;
	private String gps;
	private String ready;
	private String joyDirection;
	private String droneReset;
	private String droneTakeOff;
	private Location locationPlayer;
	private boolean isProviderEnabled;

	private String droneControlMode;
	private String droneControlSpeed;
	private String droneControlAngle;
	private String droneRequest;
	private int thottle;
	private int yaw;

	/*******************************************************************
	 * 
	 * 1.Method Interface Listener
	 * 
	 *******************************************************************/
	public interface OnGyroSensorListener {
		void onGyroSensorChange(DroneModel droneModel);
	}
	
	public interface OnJoystickMoveListener{
		void onJoystickMoveListener(DroneModel droneModel);
	}

	public interface OnReadyListener{
		void onReadyListener(DroneModel droneModel);
	}

	public interface OnBatteryListener{
		void onBatteryListener(DroneModel droneModel);
	}

	public interface OnSignalWifiListener{
		void onSignalWifiListener(DroneModel droneModel);
	}

	public interface OnGPSListener{
		void onGPSListener(DroneModel droneModel);
	}

	public interface OnResetListener{
		void onReset(DroneModel droneModel);
	}

	public interface OnTakeOffListener{
		void onTakeOff(DroneModel droneModel);
	}

	public interface OnGPSPlayerListener{
		void onLocationPlayer(DroneModel droneModel);
		void onProviderEnabled(DroneModel droneModel);
	}

	public interface OnSeekBarThrottleListener {
		void onThrottleChange(DroneModel droneModel);
	}

	public interface OnSeekBarYawListener {
		void onYawChange(DroneModel droneModel);
	}

	/*******************************************************************
	 * 
	 * 2.Call back register listener
	 * 
	 *******************************************************************/
	private OnGyroSensorListener onGyroSensorListener; // Listener
	private OnJoystickMoveListener onJoystickMoveListener;
	private OnReadyListener onReadyListener;
	private OnBatteryListener onBatteryListener;
	private OnSignalWifiListener onSignalWifiListener;
	private OnGPSListener onGPSListener;
	private OnResetListener onResetListener;
	private OnTakeOffListener onTakeOffListener;
	private OnGPSPlayerListener onGPSPlayerListener;
	private OnSeekBarThrottleListener onSeekBarThrottleListener;
	private OnSeekBarYawListener onSeekBarYawListener;
	/*******************************************************************
	 * 
	 * 3.Method Listener
	 * 
	 *******************************************************************/
	public void setOnGyroSensorListener(OnGyroSensorListener onGyroSensorListener) {
		this.onGyroSensorListener = onGyroSensorListener;
	}
	public void setOnJoystickMoveListener(OnJoystickMoveListener onJoystickMoveListener){
		this.onJoystickMoveListener = onJoystickMoveListener;
	}
	public void setOnReadyListener(OnReadyListener onReadyListener){
		this.onReadyListener = onReadyListener;
	}
	public void setOnBatteryListener(OnBatteryListener onBatteryListener){
		this.onBatteryListener = onBatteryListener;
	}
	public void setOnSignalWifiListener(OnSignalWifiListener onSignalWifiListener){
		this.onSignalWifiListener = onSignalWifiListener;
	}
	public void setOnGPSListener(OnGPSListener onGPSListener){
		this.onGPSListener = onGPSListener;
	}
	public void setOnResetListener(OnResetListener onResetListener){
		this.onResetListener = onResetListener;
	}
	public void setOnTakeOffListener(OnTakeOffListener onTakeOffListener){
		this.onTakeOffListener = onTakeOffListener;
	}

	public void setOnGPSPlayerListener(OnGPSPlayerListener onGPSPlayerListener){
		this.onGPSPlayerListener = onGPSPlayerListener;
	}

	public void setOnSeekBarThrottleListener(OnSeekBarThrottleListener onSeekBarThrottleListener){
		this.onSeekBarThrottleListener = onSeekBarThrottleListener;
	}

	public void setOnSeekBarYawListener(OnSeekBarYawListener onSeekBarYawListener){
		this.onSeekBarYawListener = onSeekBarYawListener;
	}
	/*******************************************************************
	 * 
	 * 4.Method Setter
	 * 
	 *******************************************************************/
	public void setProviderEnabled(boolean isEnabled){
		this.isProviderEnabled = isEnabled;
		if(onGPSPlayerListener != null){
			this.onGPSPlayerListener.onProviderEnabled(this);
		}
	}

	public void setLocationPlayer(Location locationPlayer){
		this.locationPlayer = locationPlayer;
		if(onGPSPlayerListener != null){
			this.onGPSPlayerListener.onLocationPlayer(this);
		}
	}

	public void setAcceleration(float[] acc) {
		this.acceleration = acc;
		if (onGyroSensorListener != null) {
			this.onGyroSensorListener.onGyroSensorChange(this);
		}
	}

	public void setBattery(String battery) {
		this.battery = battery;
		if(onBatteryListener != null){
			this.onBatteryListener.onBatteryListener(this);
		}
	}

	public void setSignalWifi(String signalWifi) {
		this.signalWifi = signalWifi;
		if(onSignalWifiListener != null){
			this.onSignalWifiListener.onSignalWifiListener(this);
		}
	}

	public void setGps(String gps) {
		this.gps = gps;
		if(onGPSListener != null){
			this.onGPSListener.onGPSListener(this);
		}
	}

	public void setReady(String ready) {
		this.ready = ready;
		if(onReadyListener != null){
			this.onReadyListener.onReadyListener(this);
		}
	}

//	public void setJoyDirection(String joyDirection) {
//		this.joyDirection = joyDirection;
//		if(onJoystickMoveListener != null){
//			this.onJoystickMoveListener.onJoystickMoveListener(this);
//		}
//	}

	public void setJoyDirection(String req,String droneControlMode,String droneControlSpeed,String droneControlAngle) {
		this.droneRequest = req;
		this.droneControlMode = droneControlMode;
		this.droneControlSpeed = droneControlSpeed;
		this.droneControlAngle = droneControlAngle;
		if(onJoystickMoveListener != null){
			this.onJoystickMoveListener.onJoystickMoveListener(this);
		}
	}

	public void setDroneReset(String reset){
		this.droneReset = reset;
		if(onResetListener != null){
			this.onResetListener.onReset(this);
		}
	}

	public void setDroneTakeOff(String takeOff){
		this.droneTakeOff = takeOff;
		if(onTakeOffListener != null){
			this.onTakeOffListener.onTakeOff(this);
		}
	}

	public void setThottle(int thottle){
		this.thottle = thottle;
		if(onSeekBarThrottleListener != null){
			this.onSeekBarThrottleListener.onThrottleChange(this);
		}
	}

	public void setYaw(int yaw){
		this.yaw = yaw;
		if(onSeekBarYawListener != null){
			this.onSeekBarYawListener.onYawChange(this);
		}
	}

	/*******************************************************************
	 * 
	 * 5.Method Getter
	 * 
	 *******************************************************************/
	public boolean isProviderEnabled() {
		return isProviderEnabled;
	}

	public Location getLocationPlayer() {
		return locationPlayer;
	}

	public float[] getAcceleration() {
		return acceleration;
	}

	public int getAngle() {
		return angle;
	}

	public int getPower() {
		return power;
	}

	public int getDirection() {
		return direction;
	}

	public String getBattery() {
		return battery;
	}

	public String getSignalWifi() {
		return signalWifi;
	}

	public String getGps() {
		return gps;
	}

	public String getReady() {
		return ready;
	}

	public String getJoyDirection() {
		return joyDirection;
	}

	public String getDroneReset(){
		return droneReset;
	}

	public String getDroneTakeOff(){
		return droneTakeOff;
	}

	public String getDroneControlAngle() {
		return droneControlAngle;
	}

	public String getDroneControlMode() {
		return droneControlMode;
	}

	public String getDroneControlSpeed() {
		return droneControlSpeed;
	}

	public String getDroneRequest(){
		return droneRequest;
	}

	public int getThottle() {
		return thottle;
	}

	public int getYaw() {
		return yaw;
	}
}
