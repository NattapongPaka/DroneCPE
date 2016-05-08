package com.example.user.dronecpe.model;

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
	/*******************************************************************
	 * 
	 * 4.Method Setter
	 * 
	 *******************************************************************/
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

	public void setJoyDirection(String joyDirection) {
		this.joyDirection = joyDirection;
		if(onJoystickMoveListener != null){
			this.onJoystickMoveListener.onJoystickMoveListener(this);
		}
	}

	/*******************************************************************
	 * 
	 * 5.Method Getter
	 * 
	 *******************************************************************/
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
}
