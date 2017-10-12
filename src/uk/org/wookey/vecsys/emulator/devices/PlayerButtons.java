package uk.org.wookey.vecsys.emulator.devices;

import uk.org.wookey.vecsys.emulator.Device;
import uk.org.wookey.vecsys.emulator.LEDButton;

public class PlayerButtons extends Device {
	LEDButton player1;
	LEDButton player2;
	
	public PlayerButtons() {
		super("Player Buttons/LEDs");

		player1 = new LEDButton("Player1");
		player2 = new LEDButton("Player2");
	}
	@Override
	public int getByte(int addr, int id) {
		if (id == 0) {
			if (addr == 0) {
				return (player1.isSelected())?0x80:0;
			}
			else if (addr == 1) {
				return (player2.isSelected())?0x80:0;
			}
		}
		
		return 0;
	}

	@Override
	public void setByte(int addr, int val, int id) throws IllegalAccessException {
		if (id == 1) {
			if (addr == 0) {
				player1.setLED(val == 0);
			}
			else if (addr == 1) {
				player2.setLED(val == 0);
			}
		}
	}
	
	public LEDButton getPlayer1Button() {
		return player1;
	}

	public LEDButton getPlayer2Button() {
		return player2;
	}
}
