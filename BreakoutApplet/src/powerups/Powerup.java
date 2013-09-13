package powerups;
import main.Breakout;
import shapes.Brick;
import shapes.Rectangle;


/***
 * An abstract super class inherited by all powerups
 * @author Erik
 *
 */
public abstract class Powerup extends Rectangle {
	private Brick brick;
	private boolean released;
	protected boolean canHaveMultiple;
	
	/***
	 * The constructor of Powerup
	 * @param x The x-value of the powerup
	 * @param y The y-value of the powerup
	 * @param brick The brick the powerup is inside of
	 */
	public Powerup(float x, float y, Brick brick)
	{
		super(x, y, brick.width, brick.height, "");
		this.brick = brick;
		
		released = false;
		canHaveMultiple = false;
	}

	/**
	 * Set if the powerup has been released
	 * @param released If the powerup has been released
	 */
	public void setReleased(boolean released) {
		this.released = released;
	}

	/**
	 * Gets if the powerup has been released
	 * @return If the powerup has been released
	 */
	public boolean isReleased() {
		return released;
	}
	
	/***
	 * Get if the powerup's brick is alive
	 * @return If the powerup's brick is alive
	 */
	public boolean isBrickAlive()
	{
		if(brick == null)
		{
			return false;
		}
		
		return brick.getIsAlive();
	}
	
	/***
	 * Check if the powerup is inside the playing field's boundaries
	 * @param pf_width The playing field width
	 * @param pf_height The playing field height
	 * @return If the powerup is inside the playing field's boundaries
	 */
	public boolean insidePlayingField(float pf_width, float pf_height) {
		
		if(x < 0) {
			return false;
		}
		else if((x + brick.width) > pf_width) {
			return false;
		}
		
		if(y < Breakout.STATUS_FIELD_HEIGHT) {
			return false;
		}
		else if((y + brick.height) > pf_height) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Activates the powerup
	 */
	public abstract void activatePowerup();
	
	/***
	 * Deactivates the powerup
	 */
	public abstract void deactivatePowerup();

	/**
	 * Gets if it is possible to have multiple copies of the powerup
	 * @return If it is possible to have multiple copies
	 */
	public boolean getCanHaveMultiple() {
		return canHaveMultiple;
	}
}
