package baitcontrol.interfaces;

import java.util.EventListener;

@FunctionalInterface
public interface GpsEventListener extends EventListener {

	public void notify(GpsEvent evt);

}
