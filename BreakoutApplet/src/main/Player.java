package main;
import gui.BreakoutCanvas;

import java.util.Vector;
import java.lang.Class;

import powerups.PaddleLengthPowerup;
import powerups.Powerup;
import powerups.ShooterPowerup;
import powerups.TimedPowerup;

import shapes.Ball;
import shapes.Brick;
import shapes.Paddle;
import usrinput.KeyControl;
import usrinput.MouseControl;
import usrinput.MouseMotionControl;

/***
 * A class for handling player input, powerups, scoring and lives
 * @author Erik
 *
 */
public class Player {

	private static final int START_LIVES = 3;
	
	private final float MOVEMENT_STEP = 7;	// Length of move.
	protected int score, num_lives;//dx, dy
	private boolean won, levelCompleted, debugMode, stepFlag;

	/* List of powerups that the player has collected. */
	protected Vector<Powerup> powerups;
	/*The player's paddle*/
	private Paddle paddle;
	/*The paddle's shooter*/
	private Shooter shooter;
	/*The player's mouse motion controller*/
	private MouseMotionControl mouseMotionController;
	/*The player's mouse controller*/
	private MouseControl mouseController;
	/*The player's key controller*/
	private KeyControl keyController;

	/***
	 * The constructor of Player
	 * @param paddle The player's paddle
	 * @param ball The ball the player can control
	 * @param canvas The game's canvas
	 */
	public Player(Paddle paddle, Ball ball, BreakoutCanvas canvas) {
		num_lives = START_LIVES;
		powerups = new Vector<Powerup> ();
		score = 0;
		won = false;
		levelCompleted = false;
		debugMode = false;
		stepFlag = false;
		shooter = null;
		
		this.paddle = paddle;
		
		/*Initiate the player's mouse controllers*/
		mouseMotionController = new MouseMotionControl(canvas);
		mouseController = new MouseControl(ball, this, canvas);
		/*Initiate the player's key controller*/
		keyController = new KeyControl(this, canvas);
	}
	
	/***
	 * Adds a powerup to the player
	 * @param powerup The powerup
	 */
	public void addPowerup(Powerup powerup) {
		powerups.add(powerup);
		
		Class powerupClass = powerup.getClass();
		
		if(powerupClass == PaddleLengthPowerup.class)
		{
			((PaddleLengthPowerup)powerup).setPaddle(paddle);
		}
		else if(powerupClass == ShooterPowerup.class)
		{
			((ShooterPowerup)powerup).setPlayer(this);
		}
		
		powerup.activatePowerup();
	}
	
	/***
	 * Updates the player's powerups
	 */
	public void updatePowerups() {
		int index = 0;
		boolean powerupsUpdated = false;
		
		while(!powerupsUpdated)
		{
			powerupsUpdated = true;
			
			for (int i = index; i < powerups.size(); i++) {
				if(TimedPowerup.class.isAssignableFrom(powerups.get(i).getClass()))
				{
					if(((TimedPowerup)powerups.get(i)).isTimedOut())
					{
						powerups.get(i).deactivatePowerup();
						powerups.remove(i);
						powerupsUpdated = false;
						index = i;
						break;
					}
				}
			}
		}
	}

	/***
	 * Makes the player lose a life
	 */
	public void loseLife() {
		num_lives--;
	}
	
	/***
	 * Makes the player gain a life
	 */
	public void gainLife() {
		num_lives++;
	}
	
	/***
	 * Resets the player's lives
	 */
	public void resetLives() {
		num_lives = START_LIVES;
	}
	
	/***
	 * Gets the number of lives the player has
	 * @return The number of lives the player has
	 */
	public int getNumLives() {
		return num_lives;
	}
	
	/***
	 * Gets the player's paddle
	 * @return The player's paddle
	 */
	public Paddle getPaddle() {
		return paddle;
	}
	
	/***
	 * Updates the paddle's position
	 * @param pf_width The playing field width
	 * @param pf_height The playing field height
	 * @param ball The ball the player can control
	 */
	public void updatePaddlePosition(float pf_width, float pf_height, Ball ball) {
		float dx = 0;
		float dy = 0;//Är alltid 0 om inte paddlen kan flyga
		
		/*Calculate dx*/
		if(mouseMotionController.getDx() > 0) {
			dx = MOVEMENT_STEP;
		}
		else if(mouseMotionController.getDx() < 0) {
			dx = -MOVEMENT_STEP;
		}
		
		/*Attempt to change the paddle's position*/
		paddle.move(dx, dy);
		/*Move the ball as well if it is atop of the paddle and stationary*/
		if(ball.getIsAtopPaddle()) {
			/*Atempt to move the ball*/
			ball.move(dx, dy);
			/*Make sure the paddle does not move outside the playing field's bounderies*/
			if(paddle.maintainBounderies(pf_width, pf_height, ball)) {//Negate the move of the ball if the paddle is stuck
				ball.move(-dx, -dy);
			}
		}
		else {
			/*Make sure the paddle does not move outside the canvas playing field's bounderies*/
			paddle.maintainBounderies(pf_width, pf_height);
		}
		
		/*Reset the relative movement of the mouse*/
		mouseMotionController.setDx(0);
		mouseMotionController.setDy(0);
	}
	
	/***
	 * Awards the player with points
	 * @param points The points to add
	 */
	public void addPoints(int points) {
		score += points;
	}
	
	/***
	 * Get the player's score
	 * @return The score
	 */
	public int getScore() {
		return score;
	}
	
	/***
	 * Sets the paddle's position
	 * @param x The paddle's x-value
	 * @param y The paddle's y-value
	 */
	public void setPaddlePosition(float x, float y) {
		paddle.setPosition(x, y); 
	}
	
	/***
	 * Gets the paddle's width
	 * @return The paddle's width
	 */
	public float getPaddleWidth() {
		return paddle.width;
	}
	
	/***
	 * Gets the paddle's height
	 * @return The paddle's height
	 */
	public float getPaddleHeight() {
		return paddle.height;
	}
	
	/***
	 * Gets the paddle's x-position
	 * @return The paddle's x-position
	 */
	public float getPaddleXPos() {
		return paddle.x;
	}
	
	/***
	 * Gets the paddle's y-position
	 * @return The paddle's y-position
	 */
	public float getPaddleYPos() {
		return paddle.y;
	}
	
	/***
	 * Gets if the player has won
	 * @return If the player has won
	 */
	public boolean getWon() {
		return won;
	}
	
	/***
	 * Set if the player has won
	 * @param val If the player has won
	 */
	public void setWon(boolean val) {
		won = val;
	}
	
	/***
	 * Reset the player's score
	 */
	public void resetScore() {
		score = 0;
	}
	
	/***
	 * Clear the player's powerups
	 */
	public void clearPowerups()
	{
		for(Powerup powerup : powerups)
		{
			powerup.deactivatePowerup();
		}
		
		powerups.clear();
	}
	
	/***
	 * Toggle relative mouse
	 */
	public void toggleRelativeMouse() {
		mouseMotionController.setIsRelative(!mouseMotionController.getIsRelative());
	}
	
	/***
	 * Set if relative mouse should be on or off
	 * @param val If relative mouse should be turned on
	 */
	public void setRelativeMouse(boolean val) {
		mouseMotionController.setIsRelative(val);
	}
	
	/***
	 * Get if relative mouse is turned on
	 * @return If relative mouse is turned on
	 */
	public boolean getRelativeMouse() {
		return mouseMotionController.getIsRelative();
	}
	
	/***
	 * Get the mouse motion controller
	 * @return The mouse motion controller
	 */
	public MouseMotionControl getMouseMotionController() {
		return mouseMotionController;
	}
	
	/***
	 * Get the mouse controller
	 * @return The mouse controller
	 */
	public MouseControl getMouseController() {
		return mouseController;
	}
	
	/***
	 * Get the keyboard controller
	 * @return The keyboard controller
	 */
	public KeyControl getKeyController() {
		return keyController;
	}

	/**
	 * Set if the level has been completed
	 * @param levelCompleted If the level has been completed
	 */
	public void setLevelCompleted(boolean levelCompleted) {
		this.levelCompleted = levelCompleted;
	}

	/**
	 * Get if the level has been completed
	 * @return If the has been completed
	 */
	public boolean isLevelCompleted() {
		return levelCompleted;
	}
	
	/***
	 * Check if the player has a certain type of powerup
	 * @param powerupClass The powerup to check for
	 * @return If the player has the powerup
	 */
	public boolean hasPowerup(Class powerupClass)
	{
		for(Powerup powerup : powerups)
		{
			if(powerup.getClass() == powerupClass)
			{
				return true;
			}
		}
		return false;
	}
	
	/***
	 * Updates the mouse motion controller's center point
	 */
	public void updateMouseCenter()
	{
		mouseMotionController.calcCenter();
	}
	
	/***
	 * Set the ball controllable with the mouse
	 * @param ball The ball that is to be controllable with the mouse
	 */
	public void setMouseControllerBall(Ball ball)
	{
		mouseController.setBall(ball);
	}

	/**
	 * Sets if debug mode should be active 
	 * @param debugMode If debug mode should be active
	 */
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	/**
	 * Gets if debug mode should be active
	 * @return If debug mode should be active
	 */
	public boolean getDebugMode() {
		return debugMode;
	}

	/**
	 * Sets the value of the stepping flag
	 * @param stepFlag The value to use 
	 */
	public void setStepFlag(boolean stepFlag) {
		this.stepFlag = stepFlag;
	}

	/**
	 * Gets the value of the stepping flag
	 * @return The value of the stepping flag
	 */
	public boolean getStepFlag() {
		return stepFlag;
	}
	
	/***
	 * Updates the shots fired by the paddle shooter
	 * @param pf_width The playing field width
	 * @param pf_height The playing field height
	 * @param bricks The level's bricks
	 * @param brickConfigurations The game's brick configurations
	 * @param canvas The game's canvas
	 */
	public void updatePaddleShooter(float pf_width, float pf_height, Vector<Brick> bricks, Vector<BrickConfiguration> brickConfigurations, BreakoutCanvas canvas)
	{
		if(shooter != null)
		{
			shooter.updateShots(pf_width, pf_height, this, bricks, brickConfigurations, canvas);
		}
	}
	
	/***
	 * Sets the paddle shooter
	 * @param shooter The paddle shooter
	 */
	public void setPaddleShooter(Shooter shooter)
	{
		this.shooter = shooter;
	}
	
	/***
	 * Gets the paddle shooter
	 * @return The paddle shooter
	 */
	public Shooter getPaddleShooter()
	{
		return shooter;
	}
}
