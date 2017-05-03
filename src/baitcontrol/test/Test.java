package baitcontrol.test;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultCaret;

import com.pi4j.wiringpi.Serial;

public class Test {

	// GPGGA = lng, lat, GPRMC = minimum, GPVTG = speed

	public static void main(String[] args) {
		int serialPort = Serial.serialOpen("/dev/serial0", 9600);
		if (serialPort == -1) {
			System.out.println("Serial Port Failed");
			return;
		} else {
			System.out.println("Serial Port initialized");

			JFrame f = new JFrame("testgps");
			JPanel contentPane = (JPanel) f.getContentPane();
			f.setSize(new Dimension(800, 500));

			JTextArea textarea = new JTextArea();
			JScrollPane scrollPane = new JScrollPane(textarea);
			textarea.setEditable(false);
			contentPane.add(scrollPane);

			DefaultCaret caret = (DefaultCaret) textarea.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

			f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			f.setVisible(true);

			// DateFormat dateformat = new
			// SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			// try (FileWriter fw = new
			// FileWriter("/media/pi/SD_CARD/gps_coordinates_" +
			// dateformat.format(new Date(System.currentTimeMillis())) + ".txt",
			// true);
			// BufferedWriter bw = new BufferedWriter(fw);
			// PrintWriter out = new PrintWriter(bw)) {
			// // out.println, out.flush
			//
			// } catch (Exception e) {
			// System.out.println(e.getMessage());
			// }

			String nmea = "";
			int datacount = 0;
			while (true) {
				if (Serial.serialDataAvail(serialPort) > 0) {
					byte[] data = Serial.serialGetAvailableBytes(serialPort);

					for (byte b : data) {
						char c = (char) b;
						if (c == '\n') {
							System.out.println("data: " + nmea);

							if (nmea.contains("GPVTG")) {
								// textarea.setText(textarea.getText() + "\n" +
								// ++datacount + " " + nmea);
								String[] gpsData = nmea.split(",");
								System.out.println("speed: " + gpsData[7]);
								textarea.setText(nmea + "\nspeed: " + gpsData[7]);
							}
							nmea = "";
						} else {
							nmea += c;
						}

					}
				}
			}

		}

	}

}
