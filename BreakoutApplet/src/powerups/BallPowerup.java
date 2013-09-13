package powerups;
import main.GameEngine;
import shapes.Brick;

/**
 * A powerup that gives the player a new ball
 * @author Erik
 *
 */
public class BallPowerup extends Powerup {
	private static final String POWERUP_IMAGE_NAME = "purple_powerup_sprites_BORDER.bmp";
	
	private GameEngine gameEngine;
	
	/**
	 * Constructor for BallPowerup
	 * @param x The x value
	 * @param y The y value
	 * @param brick The brick the powerup is inside
	 * @param gameEngine The game engine
	 */
	public BallPowerup(float x, float y, Brick brick, GameEngine gameEngine)
	{
		super(x, y, brick);
		
		imageName = POWERUP_IMAGE_NAME;
		this.gameEngine = gameEngine;
		canHaveMultiple = true;
	}
	
	/***
	 * Activates the powerup
	 */
	public void activatePowerup() {
		gameEngine.addBall(x, y);
	}

	/***
	 * Deactivates the powerup. Does nothing for this kind of powerup
	 */
	public void deactivatePowerup() {
		
	}

}
