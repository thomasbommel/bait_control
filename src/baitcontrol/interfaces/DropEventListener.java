package baitcontrol.interfaces;

import java.util.EventListener;

public interface DropEventListener extends EventListener {

	public void notify(DropEvent evt);

}
