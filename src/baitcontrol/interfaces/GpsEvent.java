package baitcontrol.interfaces;

import java.util.Date;

public class GpsEvent {

	private double speed, lat, lng, angle;
	private Date time;

	public GpsEvent(Date time, double lat, double lng, double speed, double angle) {
		this.speed = speed;
		this.lat = lat;
		this.lng = lng;
		this.time = time;
		this.angle = angle;
	}

	public double getSpeed() {
		return speed;
	}

	public double getAngle() {
		return angle;
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
