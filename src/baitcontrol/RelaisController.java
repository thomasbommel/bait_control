package baitcontrol;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class RelaisController {

	private GpioPinDigitalOutput pin;

	private RelaisController(GpioController gpio) {
		this.pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "MyLED", PinState.HIGH);
		pin.setShutdownOptions(true, PinState.LOW);
		disableRelais();
	}

	public static RelaisController getInstance(GpioController gpio) {
		return new RelaisController(gpio);
	}

	public void enableRelais() {
		this.pin.low();
	}

	public void disableRelais() {
		this.pin.high();
	}
}
