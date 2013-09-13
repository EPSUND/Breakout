package powerups;
import main.Player;
import main.Shooter;
import shapes.Brick;

/**
 * A powerup granting the player's paddle a shooter
 * @author Erik
 *
 */
public class ShooterPowerup extends Powerup {

	private static final String POWERUP_IMAGE_NAME = "red_powerup_sprites_BORDER.bmp";
	private static final String SHOOTER_PADDLE_IMAGE_NAME = "shooter_paddle.bmp";
	
	private Player player;
	private String oldPaddleImageName;
	
	/**
	 * The constructor for ShooterPowerup
	 * @param x The x-value of the powerup
	 * @param y The y-value of the powerup
	 * @param brick The brick that the powerup is inside
	 */
	public ShooterPowerup(float x, float y, Brick brick) {
		super(x, y, brick);
		imageName = POWERUP_IMAGE_NAME;
	}
	
	/**
	 * Sets the player that is to receive the shooter
	 * @param player The player
	 */
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	public void activatePowerup() {
		oldPaddleImageName = player.getPaddle().getImageName();
		player.getPaddle().setImageName(SHOOTER_PADDLE_IMAGE_NAME);
		
		player.setPaddleShooter(new Shooter(player.getPaddle()));
	}

	public void deactivatePowerup() {
		player.getPaddle().setImageName(oldPaddleImageName);
		
		player.setPaddleShooter(null);
	}

}
