package baitcontrol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.peertopark.java.geocalc.DegreeCoordinate;
import com.peertopark.java.geocalc.EarthCalc;
import com.peertopark.java.geocalc.Point;

import baitcontrol.interfaces.DropEvent;
import baitcontrol.interfaces.DropEventListener;

public abstract class DropController implements Runnable {

	private List<DropEventListener> dropListener = new ArrayList<>();
	private GPSController gps;
	public final static double TIME_TO_DROP = 0.6; // in seconds //TODO make
													// private
	private final long DISTANCE_BETWEEN_DROPS = 40; // in meter

	private DropEvent lastDropEvent = null;

	public DropController(GPSController gps, DropEventListener... dropListeners) {
		dropListener.addAll(Arrays.asList(dropListeners));
		this.gps = gps;
	}

	public void drop() {
		if (lastDropEvent == null) {
			lastDropEvent = new DropEvent(new Date(System.currentTimeMillis()), gps.getLat(), gps.getLng(), gps.getAngle(), gps.getSpeed());
			dropListener.forEach(x -> x.notify(lastDropEvent));
		} else {
			// TODO TIME_TO_DROP
			Point lastPoint = new Point(new DegreeCoordinate(lastDropEvent.getLat()), new DegreeCoordinate(lastDropEvent.getLng()));
			Point newPoint = EarthCalc.pointRadialDistance(lastPoint, gps.getAngle(), DISTANCE_BETWEEN_DROPS);
			DropEvent event = new DropEvent(new Date(System.currentTimeMillis()), newPoint.getLatitude(), newPoint.getLongitude(), gps.getAngle(),
					gps.getSpeed());
			this.dropListener.forEach(x -> x.notify(event));
		}

	}

	public void addDropEventListener(DropEventListener listener) {
		this.dropListener.add(listener);
	}

	/**
	 * in ms
	 * 
	 * @return
	 */
	public long getDelay() {
		double speed = this.gps.getSpeed();
		// long delay = Math.max((long) ((DISTANCE_BETWEEN_DROPS / (speed /
		// 3.6d) * 1000) - TIME_TO_DROP * 1000), 20);

		long delay = Math.max((long) (((DISTANCE_BETWEEN_DROPS / (speed / 3.6d) * 1000) - TIME_TO_DROP * 1000)), (long) (TIME_TO_DROP * 1000.0));
		return Math.min(delay, 20000);

	}

}
