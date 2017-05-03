package baitcontrol;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.wiringpi.Serial;

import baitcontrol.interfaces.DropEvent;
import baitcontrol.interfaces.DropEventListener;
import baitcontrol.interfaces.GpsEvent;
import baitcontrol.interfaces.GpsEventListener;
import baitcontrol.interfaces.Utils;

public class BaitController {
	private GPSController gpsController;
	private DropController dropController;
	private RelaisController relaisController;
	private LCDController lcd;
	private Date startTime;

	private static GpioController gpio;

	public BaitController(GPSController speedController, DropController dropController, LCDController lcd, RelaisController relaisControl) {
		this.gpsController = speedController;
		this.dropController = dropController;
		this.relaisController = relaisControl;
		this.lcd = lcd;
		startTime = new Date(System.currentTimeMillis());
		System.out.println("--- STARTED --- " + Utils.dateToString(startTime));

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public LCDController getLCD() {
		return this.lcd;
	}

	public static void main(String[] args) {
		try {
			GPSController gpsControl = new GPSController() {
				@Override
				public void run() {
					String nmea = "";
					Date time;

					while (true) {
						time = new Date(System.currentTimeMillis());
						this.setTime(time);

						if (Serial.serialDataAvail(getSerialPort()) > 0) {
							byte[] rawData = Serial.serialGetAvailableBytes(getSerialPort());
							for (byte dataByte : rawData) {
								char c = (char) dataByte;
								if (c == '\n') {
									// System.out.println("data: " + nmea);
									String[] gpsData = nmea.split(",");

									if (nmea.contains("GPVTG")) {
										// FIXME
										try {
											this.setSpeed(Math.max(3.0, Math.abs(Double.parseDouble(gpsData[7]))));
										} catch (Exception e) {
											try {
												this.setSpeed(Math.max(3.0, Math.abs(Double.parseDouble(gpsData[6]))));
											} catch (Exception f) {
												try {
													this.setSpeed(Math.max(3.0, Math.abs(Double.parseDouble(gpsData[5]))));
												} catch (Exception g) {
													this.setSpeed(Math.max(3.0, Math.abs(Double.parseDouble(gpsData[4]))));
												}
											}
										}
										// this.setSpeed(Double.parseDouble(gpsData[7]));
										gpsHasBeenUpdated();
									} else if (nmea.contains("GPGGA")) {
										double latDegr = Double.parseDouble(gpsData[2].substring(0, 2));
										double latMin = Double.parseDouble(gpsData[2].substring(2, gpsData[2].length()));
										latDegr += latMin / 60;

										double lngDegr = Double.parseDouble(gpsData[4].substring(0, 3));
										double lngMin = Double.parseDouble(gpsData[4].substring(3, gpsData[4].length()));
										lngDegr += lngMin / 60;

										this.setLat(latDegr);
										this.setLng(lngDegr);
									} else {
										this.setTime(new Date(System.currentTimeMillis()));
									}
									nmea = "";
								} else {
									nmea += c;
								}
							}
						}

						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};

			DropController dropControl = new DropController(gpsControl) {
				@Override
				public void run() {
					while (true) {
						try {
							Thread.sleep(this.getDelay());
						} catch (InterruptedException e) {
							System.out.println(e.getMessage());
						}
						this.drop();
					}
				}
			};
			gpio = GpioFactory.getInstance();
			LCDController lcdControl = LCDController.getInstance();
			RelaisController relaisControl = RelaisController.getInstance(gpio);
			BaitController baitController = new BaitController(gpsControl, dropControl, lcdControl, relaisControl);

			baitController.gpsController.addGpsEventListener(new BaitGpsListener());
			baitController.dropController.addDropEventListener(
					new BaitDropListener("/media/pi/SD_CARD/gps_coordinates_" + Utils.dateToString(baitController.startTime) + ".txt"));

			new Thread(baitController.gpsController).start();
			Thread.sleep(2000);
			new Thread(baitController.dropController).start();
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
		}

	}

}

class BaitGpsListener implements GpsEventListener {

	@Override
	public void notify(GpsEvent evt) {
		System.out.println("gps updated " + evt.toString());
	}

}

class BaitDropListener implements DropEventListener {

	private String path;// "/media/pi/SD_CARD/gps_coordinates_"+
						// dateFormat.format(new
						// Date(System.currentTimeMillis())) + ".txt

	protected BaitDropListener(String path) {
		this.path = path;
	}

	@Override
	public void notify(DropEvent evt) {
		System.out.println("dropevent:  " + evt.toString());
		addToTxt(evt.toString());

		// TODO DROP
		try {
			Thread.sleep((long) (DropController.TIME_TO_DROP * 1000l));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void addToTxt(String text) {
		try (FileWriter fw = new FileWriter(path,
				true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			// System.out.println(text);
			out.println(text);
			out.flush();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
