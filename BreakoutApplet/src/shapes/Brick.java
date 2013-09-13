package shapes;

import java.util.Vector;

import main.Breakout;
import main.BrickConfiguration;

/***
 * A class modeling a brick in the game
 * @author Erik
 *
 */
public class Brick extends Rectangle {
	private boolean isAlive;
	private int hits;
	private BrickConfiguration  configuration;
	
	/***
	 * Constructor for Brick
	 * @param x x value
	 * @param y y value
	 * @param width width of the brick
	 * @param height height of the brick
	 * @param configuration the brick configuration to use
	 */
	public Brick(float x, float y, float width, float height, BrickConfiguration configuration) {
		super(x, y, width, height, configuration.getColor() + ".bmp");
		this.configuration = configuration;
		isAlive = true;
		hits = 0;
	}
	
	/***
	 * Get if the brick is alive
	 * @return If the brick is alive
	 */
	public boolean getIsAlive() {
		return isAlive;
	}
	
	/***
	 * Set if the brick is alive
	 * @param val If the brick is alive
	 */
	public void setIsAlive(boolean val) {
		isAlive = val;
	}
	
	/***
	 * Get the score of the brick
	 * @return The brick's score
	 */
	public int getScore()
	{
		return configuration.getScore();
	}
	
	/***
	 * Called when a brick is hit
	 */
	public void hit()
	{
		hits++;
		
		Breakout.breakoutSoundPlayer.playClip("Bong");
		
		if(hits >= configuration.getNumberOfHits())
		{
			isAlive = false;
		}
	}
	
	/***
	 * Update the brick's color
	 * @param brickConfigurations the available brick configurations
	 */
	public void updateBrickColor(Vector<BrickConfiguration> brickConfigurations)
	{
		for(BrickConfiguration config : brickConfigurations)
		{
			if(config.getNumberOfHits() == (configuration.getNumberOfHits() - hits))
			{
				setImageName(config.getColor() + ".bmp");
				break;
			}
		}
	}
}
