package baitcontrol.interfaces;

import java.util.Date;

public class DropEvent {

	private Date date;
	private double lat, lng, angle, speed;

	public DropEvent(Date date, double lat, double lng, double angle, double speed) {
		this.date = date;
		this.lat = lat;
		this.lng = lng;
		this.angle = angle;
		this.speed = speed;
	}

	public Date getDate() {
		return date;
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}

	@Override
	public String toString() {
		return Utils.dateToString(date) + " lat:" + Utils.numberToString(lat) + " lng:" + Utils.numberToString(lng) + " angle:" + Utils.numberToString(angle)
				+ " speed:" + Utils.numberToString(speed);
	}

}
