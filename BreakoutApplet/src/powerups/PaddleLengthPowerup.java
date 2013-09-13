package powerups;
import shapes.Brick;
import shapes.Paddle;


/***
 * A powerup making the paddle longer
 * @author Erik
 *
 */
public class PaddleLengthPowerup extends Powerup {
	private static final String POWERUP_IMAGE_NAME = "yellow_powerup_sprites_BORDER.bmp";
	public static final float WIDTH_INCREASE = 1.4f;
	
	private Paddle paddle;
	
	/***
	 * The constructor of PaddleLengthPowerup
	 * @param x The x-value of the powerup
	 * @param y The y-value of the powerup
	 * @param brick The brick the powerup is inside of
	 */
	public PaddleLengthPowerup(float x, float y, Brick brick) {
		super(x, y, brick);
		imageName = POWERUP_IMAGE_NAME;
	}
	
	/***
	 * Sets the paddle to increase the size of
	 * @param paddle The paddle
	 */
	public void setPaddle(Paddle paddle)
	{
		this.paddle = paddle;
	}
	
	/***
	 * Activates the powerup
	 */
	public void activatePowerup()
	{
		paddle.width *= WIDTH_INCREASE;
	}
	
	/***
	 * Deactivates the powerup
	 */
	public void deactivatePowerup()
	{
		paddle.width *= (1 / WIDTH_INCREASE);
	}
}
