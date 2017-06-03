package baitcontrol;

import com.pi4j.wiringpi.Lcd;

public class LCDController {

	private static final int LCD_ROWS = 2;
	private static final int LCD_COLUMNS = 16;
	private static final int LCD_BITS = 4;

	private int lcdHandle;

	private LCDController() {
		this.lcdHandle = Lcd.lcdInit(LCD_ROWS, // number of row supported by LCD
				LCD_COLUMNS, // number of columns supported by LCD
				LCD_BITS, // number of bits used to communicate to LCD
				11, // LCD RS pin
				10, // LCD strobe pin
				0, // LCD data bit 1
				1, // LCD data bit 2
				2, // LCD data bit 3
				3, // LCD data bit 4
				0, // LCD data bit 5 (set to 0 if using 4 bit communication)
				0, // LCD data bit 6 (set to 0 if using 4 bit communication)
				0, // LCD data bit 7 (set to 0 if using 4 bit communication)
				0); // LCD data bit 8 (set to 0 if using 4 bit communication)

		// verify initialization
		if (lcdHandle == -1) {
			System.out.println(" ==>> LCD INIT FAILED");
			throw new RuntimeException("LCD init failed");
		}
		Lcd.lcdClear(lcdHandle);
		Lcd.lcdPosition(lcdHandle, 0, 0);
		Lcd.lcdPuts(lcdHandle, "   >> v0.1 <<");
		Lcd.lcdPosition(lcdHandle, 0, 1);
		Lcd.lcdPuts(lcdHandle, "by Thomas");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.out.println("ERROR " + e.getMessage());
		}
	}

	public static LCDController getInstance() {
		return new LCDController();
	}

	public void clearLCD() {
		Lcd.lcdClear(this.lcdHandle);
	}

	public void printLineToLCD(String text, int line) {
		if (text.length() > 16) {
			System.out.println("text:" + text + " " + (text.length() - 16) + " characters too long");
		}
		Lcd.lcdPosition(this.lcdHandle, 0, line);
		Lcd.lcdPuts(lcdHandle, text);
		for (int i = text.length(); i < 16; i++) {
			Lcd.lcdPuts(lcdHandle, " ");
		}
	}

}
