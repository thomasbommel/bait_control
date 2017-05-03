package baitcontrol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.peertopark.java.geocalc.DegreeCoordinate;
import com.peertopark.java.geocalc.EarthCalc;
import com.peertopark.java.geocalc.Point;
import com.pi4j.wiringpi.Lcd;

import baitcontrol.interfaces.DropEvent;
import baitcontrol.interfaces.DropEventListener;

public abstract class DropController implements Runnable {

	private List<DropEventListener> dropListener = new ArrayList<>();
	private GPSController gps;
	public final static double TIME_TO_DROP = 0.3; // in seconds //TODO make
													// private
	private final long DISTANCE_BETWEEN_DROPS = 20; // in meter

	private DropEvent lastDropEvent = null;

	public DropController(GPSController gps, DropEventListener... dropListeners) {
		dropListener.addAll(Arrays.asList(dropListeners));
		this.gps = gps;
	}

	public void drop() {
		if (lastDropEvent == null) {
			lastDropEvent = new DropEvent(new Date(System.currentTimeMillis()), gps.getLat(), gps.getLng(), gps.getAngle());
			dropListener.forEach(x -> x.notify(lastDropEvent));
		} else {
			// TODO TIME_TO_DROP
			Point lastPoint = new Point(new DegreeCoordinate(lastDropEvent.getLat()), new DegreeCoordinate(lastDropEvent.getLng()));
			Point newPoint = EarthCalc.pointRadialDistance(lastPoint, gps.getAngle(), DISTANCE_BETWEEN_DROPS);
			DropEvent event = new DropEvent(new Date(System.currentTimeMillis()), newPoint.getLatitude(), newPoint.getLongitude(), gps.getAngle());
			this.dropListener.forEach(x -> x.notify(event));
		}

	}

	public void addDropEventListener(DropEventListener listener) {
		this.dropListener.add(listener);
	}

	public long getDelay() {
		double speed = this.gps.getSpeed();
		// System.out.println(this.gps.getSpeed() + ", delay: " + (long)
		// ((DISTANCE_BETWEEN_DROPS / (speed / 3.6d) * 1000) - TIME_TO_DROP *
		// 1000));
		// FIXME
		long delay = Math.max((long) ((DISTANCE_BETWEEN_DROPS / (speed / 3.6d) * 1000) - TIME_TO_DROP * 1000), 20);
		// long delay = Math.min(20000, Math.max((long) ((DISTANCE_BETWEEN_DROPS
		// / (speed / 3.6d) * 1000) - TIME_TO_DROP * 1000), 0));
	
		return delay;
	}

}
