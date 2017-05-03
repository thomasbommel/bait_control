package baitcontrol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.pi4j.wiringpi.Serial;

import baitcontrol.interfaces.GpsEvent;
import baitcontrol.interfaces.GpsEventListener;

public abstract class GPSController implements Runnable {

	private List<GpsEventListener> speedListeners = new ArrayList<>();
	private int serialPort;

	private double lng, lat, speed, angle;
	private Date time;

	public GPSController(GpsEventListener... eventListeners) {
		speedListeners.addAll(Arrays.asList(eventListeners));
		init();
	}

	private void init() {
		this.serialPort = Serial.serialOpen("/dev/serial0", 9600);
		if (this.serialPort == -1) {
			System.out.println("Serial Port Failed");
			throw new RuntimeException("Serial Port Failed");
		}
	}

	public void gpsHasBeenUpdated() {
		if (this.time != null && this.lat > -1 && this.lng > -1 && this.speed > -1) {
			speedListeners.stream().forEach(x -> x.notify(new GpsEvent(this.time, this.lat, this.lng, this.speed)));
		}
	}

	public void addGpsEventListener(GpsEventListener listener) {
		this.speedListeners.add(listener);
	}

	public int getSerialPort() {
		return serialPort;
	}

	public void setSerialPort(int serialPort) {
		this.serialPort = serialPort;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public double getLng() {
		return lng;
	}

	public double getLat() {
		return lat;
	}

	public double getSpeed() {
		return speed;
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

}
