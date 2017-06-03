package baitcontrol;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.Month;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
	public static Date startTime;

	private static GpioController gpio;

	public BaitController(GPSController speedController, DropController dropController, LCDController lcd, RelaisController relaisControl) {
		this.gpsController = speedController;
		this.dropController = dropController;
		this.relaisController = relaisControl;
		this.lcd = lcd;
		startTime = new Date(System.currentTimeMillis());
		System.out.println("--- STARTED --- " + Utils.dateToString(startTime));

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			System.out.println("Error " + e.getMessage());
			e.printStackTrace();
		}
	}

	public LCDController getLCD() {
		return this.lcd;
	}

	public RelaisController getRelais() {
		return this.relaisController;
	}

	public DropController getDropController() {
		return this.dropController;
	}

	public static void main(String[] args) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			System.out.println(e1.getMessage());
		}
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
										nmea = nmea.replaceAll(",", ", ");
										// FIXME
										double minSpeed = Math.random() * 80 + 20;

										// this.setSpeed(Math.max(minSpeed,
										// Math.abs(Double.parseDouble(gpsData[7]))));
										try {
											this.setSpeed(Double.parseDouble(gpsData[7]));
										} catch (Exception e) {
											System.out.println("speed error:" + e.getMessage());
											this.setSpeed(0.11);
										}
										speedHasBeenUpdated();
									} else if (nmea.contains("GPGGA")) {
										try {
											double latDegr = Double.parseDouble(gpsData[2].substring(0, 2));
											double latMin = Double.parseDouble(gpsData[2].substring(2, gpsData[2].length()));
											latDegr += latMin / 60;

											double lngDegr = Double.parseDouble(gpsData[4].substring(0, 3));
											double lngMin = Double.parseDouble(gpsData[4].substring(3, gpsData[4].length()));
											lngDegr += lngMin / 60;

											this.setLat(latDegr);
											this.setLng(lngDegr);
											Calendar ca = new GregorianCalendar();
											ca.set(Year.parse("2017").getValue(), Month.MAY.getValue(), 4, Integer.parseInt(gpsData[1].substring(0, 2) + 2),
													Integer.parseInt(gpsData[1].substring(2, 3)),
													Integer.parseInt(gpsData[1].substring(3, 4)));

											this.setTime(new Date(ca.getTimeInMillis()));
											gpsHasBeenUpdated();
										} catch (Exception e) {
											System.out.println("ERROR gpserror:" + e.getMessage());
										}
									} else {
									}
									nmeaHasBeenUpdated(nmea);
									nmea = "";
								} else {
									nmea += c;
								}
							}
						}

						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							System.out.println("ERROR " + e.getMessage());
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
							System.out.println(
									"current Delay: " + Utils.numberToString(this.getDelay(), 10, 3) + "ms at speed: " + gpsControl.getSpeed() + "km/h");
						} catch (InterruptedException e) {
							System.out.println(e.getMessage());
						}
						this.drop();
					}
				}
			};
			Thread.sleep(100);

			gpio = GpioFactory.getInstance();
			LCDController lcdControl = LCDController.getInstance();
			RelaisController relaisControl = RelaisController.getInstance(gpio);
			BaitController baitController = new BaitController(gpsControl, dropControl, lcdControl, relaisControl);

			baitController.gpsController.addGpsEventListener(new BaitGpsListener(baitController));
			baitController.dropController.addDropEventListener(
					new BaitDropListener("/media/pi/SD_CARD/flight_" + Utils.dateToString(BaitController.startTime) + "drop_coordinates.txt", baitController));

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("Error " + e.getMessage());
				e.printStackTrace();
			}
			new Thread(baitController.gpsController).start();
			Thread.sleep(5000);
			new Thread(baitController.dropController).start();
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
		}

	}

}

class BaitGpsListener implements GpsEventListener {

	private BaitController bait;

	public BaitGpsListener(BaitController bait) {
		this.bait = bait;
	}

	@Override
	public void notify(GpsEvent evt) {
		System.out.println("gps updated " + evt.toString());
		bait.getLCD().printLineToLCD("v" + Utils.numberToString(evt.getSpeed(), 6, 2) + " " + Utils.dateToTimeString(evt.getTime()), 1);
	}

}

class BaitDropListener implements DropEventListener {
	private BaitController bait;
	private String path;// "/media/pi/SD_CARD/gps_coordinates_"+
						// dateFormat.format(new
						// Date(System.currentTimeMillis())) + ".txt

	protected BaitDropListener(String path, BaitController bait) {
		this.path = path;
		this.bait = bait;
	}

	@Override
	public void notify(DropEvent evt) {
		System.out.println("dropevent:  " + evt.toString());
		addToTxt(evt.toString());
		bait.getLCD().printLineToLCD(Utils.dateToTimeString(evt.getDate()) + " " + Utils.numberToString(bait.getDropController().getDelay(), 6, 0), 0);
		// TODO DROP
		try {
			bait.getRelais().enableRelais();
			Thread.sleep((long) (DropController.TIME_TO_DROP * 1000l));
			bait.getRelais().disableRelais();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
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
