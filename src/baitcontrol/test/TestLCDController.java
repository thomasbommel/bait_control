package baitcontrol.test;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

import baitcontrol.LCDController;
import baitcontrol.RelaisController;

public class TestLCDController {

	public static void main(String[] args) throws InterruptedException {
		GpioController gpio = GpioFactory.getInstance();
		LCDController lcd = LCDController.getInstance();
		RelaisController relais = RelaisController.getInstance(gpio);
		relais.disableRelais();
		Thread.sleep(1000);
		relais.enableRelais();
		Thread.sleep(1000);
		relais.disableRelais();
		Thread.sleep(1000);
		relais.enableRelais();
		lcd.printLineToLCD("hallo", 1);
		Thread.sleep(1000);
		lcd.printLineToLCD("zzzzz", 1);
		Thread.sleep(1000);
		relais.disableRelais();
		lcd.printLineToLCD("7897987", 0);
		lcd.printLineToLCD("ttttt", 1);
		relais.enableRelais();
		Thread.sleep(10000);
	}

}
