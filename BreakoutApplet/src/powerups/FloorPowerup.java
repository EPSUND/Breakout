package powerups;

import java.util.TimerTask;

import main.Breakout;

import shapes.Brick;

/***
 * A powerup that grants a floor
 * @author Erik
 *
 */
public class FloorPowerup extends TimedPowerup {
	private static final String POWERUP_IMAGE_NAME = "blue_powerup_sprites_BORDER.bmp";
	public static final int POWERUP_DURATION = 15;
	
	/***
	 * Constructor for FloorPowerup
	 * @param x x value
	 * @param y y value
	 * @param brick the brick the powerup is contained in
	 */
	public FloorPowerup(float x, float y, Brick brick)
	{
		super(x, y, brick);
		imageName = POWERUP_IMAGE_NAME;
	}
	
	/***
	 * Activates the powerup
	 */
	public void activatePowerup() {
		Breakout.hasFloor = true;
		
		timer.schedule(new TimerTask() {
	        public void run() {
	            timedOut = true;
	        }
		}, POWERUP_DURATION * 1000);

	}

	/***
	 * Deactivates the powerup
	 */
	public void deactivatePowerup() {
		Breakout.hasFloor = false;
	}
}
