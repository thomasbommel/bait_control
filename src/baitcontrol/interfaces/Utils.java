package baitcontrol.interfaces;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
	public static final int NUMBER_LENGTH = 25, DECIMALPLACES = 12, STRINGLENGTH = 30;
	public static final String ENDING = "; ";

	public static String stringWithFixedLength(String s, int length) {
		return String.format("%1$" + length + "s", s);
	}

	public static String stringWithFixedLength(String s) {
		return stringWithFixedLength(s, STRINGLENGTH);
	}

	public static String numberToString(double number, int length, int decimalpl) {
		return String.format("%" + length + "." + decimalpl + "f", number);
	}

	public static String numberToString(double number) {
		return numberToString(number, NUMBER_LENGTH, DECIMALPLACES);
	}

	public static String dateToString(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		return dateFormat.format(date);
	}

	public static String dateToTimeString(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("HH-mm-ss");
		return dateFormat.format(date);
	}

}
