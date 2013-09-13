package main;
import gui.BreakoutCanvas;

import java.util.Vector;
import java.util.Random;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import powerups.BallPowerup;
import powerups.FloorPowerup;
import powerups.PaddleLengthPowerup;
import powerups.Powerup;
import powerups.ShooterPowerup;

import shapes.Ball;
import shapes.Brick;
import shapes.Paddle;
import shapes.Shot;
import utils.Helpers;
import xml.BrickConfigurationHandler;
import xml.LevelHandler;

/***
 * The main class for running the game
 * @author Erik
 *
 */
public class GameEngine {
	
	/*Level constants*/
	private static final int NUM_LEVELS = 10;
	
	/*Paddle constants*/
	private static final float PADDLE_WIDTH = 60;
	private static final float PADDLE_HEIGHT = 20;
	public static final float PADDLE_SPACING = PADDLE_HEIGHT / 2;
	private static final String PADDLE_IMAGE_NAME = "paddle5.bmp";
	
	/*Ball constants*/
	private static final float BALL_RADIUS = 5;
	private static final float BALL_MAX_VEL = 2.3f;//2
	private static final String BALL_IMAGE_NAME = "ball2.bmp";
	
	/*Brick constants*/
	public static final float BRICK_WIDTH = 40;
	public static final float BRICK_HEIGHT = 20;
	
	/*Powerup constants*/
	private static final float POWERUP_PROB = 0.10f;
	private static final int POWERUP_TYPES = 4;
	private static final float POWERUP_SPEED = 1.0f;//0.55f
	
	private BreakoutCanvas canvas;
	private Player player;
	private Vector<Ball> balls;
	private Vector<BrickConfiguration> brickConfigurations;
	private Vector<Brick> bricks;
	private Vector<Powerup> powerups;
	private volatile boolean pause, restart;
	private int pf_width, pf_height, levelNumber = 0;
	
	private float paddleStartX;//125
	private float paddleStartY;//530
	
	/***
	 * The constructor for GameEngine
	 * @param canvas The game's canvas
	 */
	public GameEngine(BreakoutCanvas canvas) {
		this.canvas = canvas;
		pause = false;
		restart = false;
	}
	
	/***
	 * The game main loop. Calls the updating and painting of objects
	 * every FRAME_DELAY ms
	 */
	public void game() {
		/* Update the game as long as it is running. */
		while (canvas.isVisible() && !restart && !player.isLevelCompleted()) {
			if (!pause) {
				/*If the player have lives left and have not won*/
				if((player.getNumLives() > 0) && !(player.getWon())) {
					updateWorld();	// Update the world
				}
			}
			paintWorld();	// Paint the game world
			try { 
				Thread.sleep(BreakoutCanvas.FRAME_DELAY);		// Wait a given interval.
			} catch (InterruptedException e) {
				// Ignore.
			}
		}
		
		return;
	}
	
	/***
	 * Executes the initial creation of the world.
	 */
	public void initWorld() {
		if(restart) {//The player has restarted the game
			/*Reset the level counter*/
			levelNumber = 0;
			/*Load the first level*/
			loadNextLevel();
			/*Reset the paddle's position*/
			Paddle paddle = player.getPaddle();
			paddle.setXPos(paddleStartX);
			paddle.setYPos(paddleStartY);
			/*Reset the first ball's position*/
			Ball ball = balls.get(0);
			ball.setXPos(paddle.x + (paddle.width / 2));
			ball.setYPos(paddle.y - BALL_RADIUS);
			ball.resetVelocity();
			ball.setIsAtopPaddle(true);
			/*Remove all balls except the first*/
			balls.clear();
			balls.add(ball);
			/*Make sure the ball is controllable by the mouse controller*/
			player.setMouseControllerBall(ball);
			/*Set all the bricks as alive*/
			for(Brick brick : bricks) {
				brick.setIsAlive(true);
			}
			/*Reset the player's lives*/
			player.resetLives();
			/*Mark the player as not having won*/
			player.setWon(false);
			/*Reset the player's score*/
			player.resetScore();
			/*Clear the player's powerups*/
			player.clearPowerups();
			/*Set the restart flag as false*/
			restart = false;
			/*Make the powerups*/
			makePowerups();
			/*Enable relative mouse movement*/
			player.setRelativeMouse(true);
			/*Hide the mouse cursor*/
			canvas.hideCursor();
			/*Exit the function*/
			return;
		}
		
		if(moreLevels() && levelNumber != 0)//If this is a new level and not the first level
		{
			/*Load the level*/
			loadNextLevel();
			/*Reset the paddle's position*/
			Paddle paddle = player.getPaddle();
			paddle.setXPos(paddleStartX);
			paddle.setYPos(paddleStartY);
			/*Reset the first ball's position*/
			Ball ball = balls.get(0);
			ball.setXPos(paddle.x + (paddle.width / 2));
			ball.setYPos(paddle.y - BALL_RADIUS);
			ball.resetVelocity();
			ball.setIsAtopPaddle(true);
			/*Remove all balls except the first*/
			balls.clear();
			balls.add(ball);
			/*Clear the player's powerups*/
			player.clearPowerups();
			/*Make sure the ball is controllable by the mouse controller*/
			player.setMouseControllerBall(ball);
			/*Make the powerups*/
			makePowerups();
			/*Exit the function*/
			return;
		}
		
		/*Make the Brick vector*/
		bricks = new Vector<Brick>();
		/*Make the brick configuration vector*/
		brickConfigurations = new Vector<BrickConfiguration>();
		/*Make the powerup vector*/
		powerups = new Vector<Powerup>();
		/*Make the balls vector*/
		balls = new Vector<Ball>();
		/*Load the brick configurations*/
		loadBrickConfigurations();
		/*Load the level*/
		loadNextLevel();
		/*Make the paddle that is to be used by the player*/
		Paddle paddle = new Paddle(paddleStartX, paddleStartY, PADDLE_WIDTH, PADDLE_HEIGHT, PADDLE_IMAGE_NAME);
		/*Make the ball*/
		Ball ball = new Ball(paddle.x + (paddle.width / 2),
						paddle.y - BALL_RADIUS, 
						1, 1, BALL_MAX_VEL, BALL_RADIUS, BALL_IMAGE_NAME);
		/*Add the ball to the ball vector*/
		balls.add(ball);
		/*Make the player*/
		player = new Player(paddle, ball, canvas);
		/*Make the powerups*/
		makePowerups();
		/*Make the player's controllers listeners to the canvas*/
		canvas.initMouseListeners(player.getMouseMotionController(), player.getMouseController());
		canvas.addKeyListener(player.getKeyController());
		/*Enable relative mouse movement*/
		player.setRelativeMouse(true);
		/*Hide the mouse cursor*/
		canvas.hideCursor();
	}
	
	/***
	 * Updates the game world
	 */
	public void updateWorld() {
		/*Don't update the game world if we in debug mode and not stepping*/
		if(player.getDebugMode() && !player.getStepFlag() && !player.isLevelCompleted())
		{
			return;
		}
		
		/*Update the paddle's position*/
		player.updatePaddlePosition(pf_width, pf_height, balls.get(0));
		
		/*Update the shots of a potential paddle shooter*/
		player.updatePaddleShooter(pf_width, pf_height, bricks, brickConfigurations, canvas);
		
		boolean ballsUpdated = false;
		int index = 0;
		
		while(!ballsUpdated)
		{
			ballsUpdated = true;
			
			/*Update the balls positions*/
			for(int i = index; i < balls.size(); i++)
			{
				if(balls.get(i).updatePosition(pf_width, pf_height, player, bricks, brickConfigurations, canvas)) {
					if(balls.size() == 1)
					{	
						/*Use up a life if the ball hits the floor and we only have one ball*/
						useLife();
						/*Play the lost life or game over sound effect*/
						if(player.getNumLives() > 0)
						{
							Breakout.breakoutSoundPlayer.playClip("Boom");
						}
						else
						{
							Breakout.breakoutSoundPlayer.playClip("WilhelmScream");
						}
					}
					else
					{
						/*Remove the ball if we have multiple balls*/
						balls.remove(i);
						index = i;
						ballsUpdated = false;
						break;
					}
				}
			}
		}
		
		/*If the latest movement of the ball completed the level (by destroying the last brick)*/
		if(player.isLevelCompleted())
		{
			/*If there are no more levels has the player won*/
			if(!moreLevels())
			{
				player.setWon(true);
				player.setRelativeMouse(false);
				canvas.showCursor();
				Breakout.breakoutSoundPlayer.playClip("FeelGood");
			}
		}
		
		/*Update the playing field's powerups*/
		updatePowerups();
		
		/* Update the player's powerups */
		player.updatePowerups();
		
		/*Show the high score list and prompt the user to enter his/her name*/
		if(player.getWon() || player.getNumLives() == 0)
		{
			//Run the score registration in a seperate thread to prevent the rendering from being paused
			new Thread()
			{
				public void run()
				{
					Breakout.highScoreSystem.registerScore(new Object[]{player.getScore(), levelNumber});	
				}
			}.start();
		}
		
		/*Reset the step flag if we are in debug mode*/
		if(player.getDebugMode())
		{
			player.setStepFlag(false);
		}
	}
	
	/***
	 * Paints the game world
	 */
	public void paintWorld() {
		/* Initiate the graphics context */
		canvas.initGraphicsContext();
		
		/*Paint the background*/
		canvas.paintBackground(levelNumber);
		
		/*Paint the status bar*/
		canvas.paintStatusField(player.getScore(), player.getNumLives());
		
		if((player.getNumLives() > 0) && !(player.getWon())) {//If the game is still going on
			/*Paint the paddle*/
			canvas.paint(player.getPaddle());
			
			/*Paint the balls*/
			for(Ball ball : balls)
			{
				canvas.paint(ball);
			}
			
			/*Paint the bricks*/
			for(Brick brick : bricks) {
				/*Only a paint if the brick is still in play*/
				if(brick.getIsAlive()){
					canvas.paint(brick);
				}		
			}
			
			/*Paint the powerups*/
			for(Powerup powerup : powerups) {
				/*Only paint a powerup if it has been released*/
				if(powerup.isReleased()) {
					canvas.paint(powerup);
				}
			}
			
			/*Paint the floor if one exists*/
			if(Breakout.hasFloor)
			{
				canvas.paintFloor(pf_height - (int)(PADDLE_SPACING / 2));
			}
			
			/*Paint the shooter's shots*/
			Shooter shooter = player.getPaddleShooter();
			
			if(shooter != null)
			{
				synchronized(shooter.getShooterLock())
				{
					for(Shot shot : shooter.getShots())
					{
						canvas.paint(shot);
					}
				}
			}
		}
		else if(player.getWon()) {//If the player has won
			canvas.paintWinMessage();
		}
		else {//If the player has lost
			canvas.paintLoseMessage();
		}
		
		/*Show the graphics buffer*/
		canvas.showGraphicsBuffer();
	}
	
	/***
	 * Get if the game is paused
	 * @return If the game is paused
	 */
	public boolean getPause()
	{
		return pause;
	}
	
	/***
	 * Pauses the game, so that it does not update or paint the objects in the game.
	 */
	public void pause() {
		pause = true;
	}
	
	/***
	 * Resumes the updating and painting of the game after a pause.
	 */
	public void resume() {
		pause = false;
	}
	
	/***
	 * Set if the game should be restarted
	 * @param val If the game should be restarted
	 */
	public void setRestart(boolean val) {
		restart = val;
	}
	
	/***
	 * Get if the game should be restarted
	 * @return If the game should be restarted
	 */
	public boolean getRestart() {
		return restart;
	}
	
	/***
	 * Sets the width of the playing field(the area where the game takes place)
	 * @param width The new width of the playing field
	 */
	public void setPlayingFieldWidth(int width) {
		pf_width = width;
	}
	
	/***
	 * Gets the width of the playing field(the area where the game takes place)
	 * @return The width of the playing field
	 */
	public int getPlayingFieldWidth() {
		return pf_width;
	}
	
	/***
	 * Sets the height of the playing field(the area where the game takes place)
	 * @param height The new height of the playing field
	 */
	public void setPlayingFieldHeight(int height) {
		pf_height = height;
	}
	
	/***
	 * Gets the height of the playing field(the area where the game takes place)
	 * @return The height of the playing field
	 */
	public int getPlayingFieldHeight() {
		return pf_height;
	}
	
	/***
	 * Uses one of the players lives. Resets the position of the ball and the paddle and can result in the player losing the game.
	 */
	public void useLife() {
		/*Use up a life*/
		player.loseLife();
		/*Reset the paddle's position*/
		player.setPaddlePosition(paddleStartX, paddleStartY);
		/*Place the ball atop the paddle*/
		Ball ball = balls.get(0);
		ball.x = player.getPaddleXPos() + (player.getPaddleWidth() / 2);
		ball.y = player.getPaddleYPos() - ball.radius;
		ball.setIsAtopPaddle(true);
		/*Reset the ball's direction*/
		ball.resetVelocity();
		/*Make the ball controlable by the mouse*/
		player.setMouseControllerBall(ball);
		/*Clear the player's powerups*/
		player.clearPowerups();
		/*Clear all released powerups*/
		clearReleasedPowerups();
		
		/*If the player have lost his/her last life*/
		if(player.getNumLives() == 0) {
			/*Turn of relative mouse movement*/
			player.setRelativeMouse(false);
			/*Show the cursor*/
			canvas.showCursor();
		}
	}
	
	/***
	 * Gets the canvas where the game's objects are drawn
	 * @return The canvas
	 */
	public BreakoutCanvas getCanvas() {
		return canvas;
	}
	
	/***
	 * Gets the level's bricks
	 * @return The level's bricks
	 */
	public Vector<Brick> getBricks() {
		return bricks;
	}
	
	/***
	 * Gets the player object
	 * @return The player object
	 */
	public Player getPlayer() {
		return player;
	}
	
	/***
	 * Gets the brick configuration for a color
	 * @param color The color to get the brick configuration for
	 * @return The brick configuration for the color
	 */
	public BrickConfiguration getBrickConfiguration(String color) {
		for(BrickConfiguration config : brickConfigurations) {
			if(config.getColor().equals(color)) {
				return config;
			}
		}
		return null;
	}

	/***
	 * Sets the brick configurations to use
	 * @param brickConfigurations The brickConfigurations to use
	 */
	public void setBrickConfigurations(Vector<BrickConfiguration> brickConfigurations) {
		this.brickConfigurations = brickConfigurations;
	}

	/**
	 * Gets the brick configurations used by the game
	 * @return The brickConfigurations
	 */
	public Vector<BrickConfiguration> getBrickConfigurations() {
		return brickConfigurations;
	}
	
	/***
	 * Checks if there are more levels after this one
	 * @return If there are more levels after this one
	 */
	public boolean moreLevels()
	{
		return levelNumber < NUM_LEVELS;
	}
	
	/***
	 * Load the brick configurations from the brick configuration XML file
	 */
	public void loadBrickConfigurations(){
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
		    SAXParser saxParser = factory.newSAXParser();
		    BrickConfigurationHandler handler = new BrickConfigurationHandler(this);
		    saxParser.parse(Helpers.getResourceURIString(this, "levels/BrickConfigurations.xml"), handler);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/***
	 * Load the next level from the level's XML file
	 */
	public void loadNextLevel() {
		bricks.clear();
		
		/*Increment the level counter*/
		levelNumber++;
		
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
		    SAXParser saxParser = factory.newSAXParser();
		    LevelHandler handler = new LevelHandler(this);
		    saxParser.parse(Helpers.getResourceURIString(this, "levels/level_" + levelNumber + ".xml"), handler);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		/*Calculate the paddle's start position*/
		paddleStartX = pf_width / 2;//125
		paddleStartY = pf_height - PADDLE_HEIGHT - PADDLE_SPACING;//530 
		
		if(player != null)
		{
			/*Reset the levelCompleted indicator*/
			player.setLevelCompleted(false);
		}
		
		setCanvasSize(pf_width, pf_height);
	}
	
	/***
	 * Sets the size of the game's canvas
	 * @param width The width of the canvas
	 * @param height The height of the canvas
	 */
	private void setCanvasSize(int width, int height)
	{
		/*Set the canvas size*/
		canvas.setPlayingFieldWidth(width);
		canvas.setPlayingFieldHeight(height);
		canvas.setBounds(0, 0, width, height);
		
		if(player != null)
		{
			/*Update the mouse center point*/
			player.updateMouseCenter();
		}
	}
	
	/***
	 * Fill some bricks with powerups
	 */
	public void makePowerups() 
	{
		Random rand = new Random();
		
		/*Clear the powerup vector*/
		powerups.clear();
		
		/*Go through every brick and check which bricks should contain powerups*/
		for(Brick brick : bricks)
		{
			/*The brick should contain a powerup*/
			if(rand.nextFloat() <= POWERUP_PROB)
			{
				/*Add a random powerup*/
				int powerupType = rand.nextInt(POWERUP_TYPES);
				
				switch(powerupType)
				{
				case 0://Paddle size
					powerups.add(new PaddleLengthPowerup(brick.x, brick.y, brick));
					break;
				case 1://Floor
					powerups.add(new FloorPowerup(brick.x, brick.y, brick));
					break;
				case 2://Ball
					powerups.add(new BallPowerup(brick.x, brick.y, brick, this));
					break;
				case 3://Shooter
					powerups.add(new ShooterPowerup(brick.x, brick.y, brick));
					break;
				default:
					System.err.println("Breakout: Unknown powerup type.");
					System.exit(1);
				}
			}
		}
	}
	
	/***
	 * Update all powerups not collected by the player
	 */
	public void updatePowerups(){
		boolean powerupsUpdated = false;
		int index = 0;
		
		while(!powerupsUpdated)
		{
			powerupsUpdated = true;
			
			for(int i = index; i < powerups.size(); i++)
			{
				/*Move the powerup towards the floor if it has been released*/
				if(powerups.get(i).isReleased())
				{
					powerups.get(i).move(0, POWERUP_SPEED);
					
					/*Remove the powerup from play if it has moved under the floor.*/
					if(!powerups.get(i).insidePlayingField(pf_width, pf_height))
					{
						powerups.remove(i);
						index = i;
						powerupsUpdated = false;
						break;
					}
					
					/*Check if the paddle intersects the powerup and add it to the player if that is the case.*/
					if(player.getPaddle().checkForIntersection(powerups.get(i)))
					{
						if(!player.hasPowerup(powerups.get(i).getClass()) || powerups.get(i).getCanHaveMultiple())
						{
							player.addPowerup(powerups.get(i));
						}
						
						powerups.remove(i);
						index = i;
						powerupsUpdated = false;
						break;
					}
				}
				else//Check if the powerup should be released, which is when the brick the powerup is in has been eliminated. 
				{
					if(!powerups.get(i).isBrickAlive())
					{
						powerups.get(i).setReleased(true);
					}
				}
			}
		}
	}
	
	/***
	 * Adds a new ball
	 * @param x The x-value of the ball
	 * @param y The y-value of the ball
	 */
	public void addBall(float x, float y)
	{
		Ball ball = new Ball(x, y, 1, -1, BALL_MAX_VEL, BALL_RADIUS, BALL_IMAGE_NAME);
		ball.setIsAtopPaddle(false);
		balls.add(ball);
	}
	
	/***
	 * Clears all released powerups
	 */
	private void clearReleasedPowerups()
	{
		int index = 0;
		boolean releasedPowerupsCleared = false;
		
		while(!releasedPowerupsCleared)
		{
			releasedPowerupsCleared = true;
			
			for(int i = index; i < powerups.size(); i++)
			{
				if(powerups.get(i).isReleased())
				{
					powerups.remove(i);
					index = i;
					releasedPowerupsCleared = false;
					break;
				}
			}
		}
	}
}
