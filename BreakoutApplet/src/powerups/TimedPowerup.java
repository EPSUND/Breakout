package powerups;

import java.util.Timer;

import shapes.Brick;

/***
 * A class representing timed powerups. A abstract super class all timed powerups inherit
 * @author Erik
 *
 */
public abstract class TimedPowerup extends Powerup {
	protected Timer timer;
	protected boolean timedOut;
	
	/***
	 * The constructor for TimedPowerup
	 * @param x The x-value of the powerup
	 * @param y The y-value of the powerup
	 * @param brick The brick containing the powerup
	 */
	public TimedPowerup(float x, float y, Brick brick) {
		super(x, y, brick);
		
		timer = new Timer();
		timedOut = false;
	}

	/***
	 * Returns if powerup has timed out
	 * @return If the powerup has timed out
	 */
	public boolean isTimedOut() {
		return timedOut;
	}
}
