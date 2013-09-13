package main;
import gui.BreakoutCanvas;

import java.util.Vector;

import shapes.Brick;
import shapes.Paddle;
import shapes.Shot;

/**
 * A shooter that can be used to eliminate bricks with projectiles
 * @author Erik
 *
 */
public class Shooter {
	private static final int SHOT_DELAY = 200;//In miliseconds
	
	private volatile Vector<Shot> shots;
	private Paddle paddle;
	private Object shooterLock;
	private long lastFiredShotTime;//In miliseconds
	
	/**
	 * The constructor for Shooter
	 * @param paddle The paddle to attach the shooter to
	 */
	public Shooter(Paddle paddle)
	{
		shots = new Vector<Shot>();
		shooterLock = new Object();
		lastFiredShotTime = 0;
		
		this.paddle = paddle;
	}
	
	/**
	 * Fires a shoot
	 */
	public void shoot()
	{
		long currentTime = System.currentTimeMillis();
		
		/*Apply a slight delay to shooting to prevent exploits*/
		if((currentTime - lastFiredShotTime) < SHOT_DELAY)
		{
			lastFiredShotTime = currentTime;
			return;
		}
		
		lastFiredShotTime = currentTime;
		
		synchronized(shooterLock)
		{
			/*Add a new shot at the left side of the paddle*/
			shots.add(new Shot(paddle.x, paddle.y, 0, -1));
			/*Add a new shot at the right side of the paddle*/
			shots.add(new Shot(paddle.x + paddle.width, paddle.y, 0, -1));
		}
		
		/*Play the shoot sound effect*/
		Breakout.breakoutSoundPlayer.playClip("Zap");
	}
	
	/**
	 * Update all active shots
	 * @param pf_width The playing field width
	 * @param pf_height The playing field height
	 * @param player The player object
	 * @param bricks The level's bricks
	 * @param brickConfigurations The game's brick configurations
	 * @param canvas The game's canvas
	 */
	public void updateShots(float pf_width, float pf_height, Player player, Vector<Brick> bricks, Vector<BrickConfiguration> brickConfigurations, BreakoutCanvas canvas)
	{
		synchronized(shooterLock)
		{
			int index = 0;
			boolean shotsUpdated = false;
			
			while(!shotsUpdated)
			{
				shotsUpdated = true;
				
				for(int i = index; i < shots.size(); i++)
				{
					if(shots.get(i).updatePosition(pf_width, pf_height, player, bricks, brickConfigurations, canvas))
					{
						shots.remove(i);
						shotsUpdated = false;
						index = i;
						break;
					}
				}
			}
		}
	}

	/**
	 * Gets the active shots
	 * @return The active shots
	 */
	public Vector<Shot> getShots() {
		return shots;
	}

	/**
	 * Gets the thread lock for the shooter
	 * @return The thread lock for the shooter
	 */
	public Object getShooterLock() {
		return shooterLock;
	}
}
