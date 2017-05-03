package baitcontrol.interfaces;

import java.util.Date;

public class GpsEvent {

	private double speed, lat, lng;
	private Date time;

	public GpsEvent(Date time, double lat, double lng, double speed) {
		this.speed = speed;
		this.lat = lat;
		this.lng = lng;
		this.time = time;
	}

	public double getSpeed() {
		return speed;
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}

	public Date getTime() {
		return time;
	}

	@Override
	public String toString() {
		return Utils.dateToString(time) + " lat:" + Utils.numberToString(lat) + " lng:" + Utils.numberToString(lng) + " speed:" + Utils.numberToString(speed);
	}

}
