package baitcontrol.interfaces;

import java.util.Date;

public class DropEvent {

	private Date date;
	private double lat, lng, angle;

	public DropEvent(Date date, double lat, double lng, double angle) {
		this.date = date;
		this.lat = lat;
		this.lng = lng;
		this.angle = angle;
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
		return Utils.dateToString(date) + " lat:" + Utils.numberToString(lat) + " lng:" + Utils.numberToString(lng) + " angle:" + Utils.numberToString(angle);
	}

}
