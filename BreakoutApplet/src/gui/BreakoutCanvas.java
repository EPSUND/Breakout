package gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferStrategy;
import java.awt.Point;

import main.Breakout;

import powerups.Powerup;

import shapes.Brick;
import shapes.Circle;
import shapes.Paddle;
import shapes.Rectangle;
import usrinput.MouseControl;
import usrinput.MouseMotionControl;
import utils.Helpers;

/***
 * The game's canvas where everything associated with the game is drawn
 * @author Erik
 *
 */
public class BreakoutCanvas extends Canvas {
	
	private static final long serialVersionUID = 1L;
	public static final int FRAME_DELAY = 10;		// Update interval.
	public static final Color BACKGROUND_COLOR = Color.BLACK;
	private static final Color STATUS_TEXT_COLOR = Color.WHITE;
	private static final Color WIN_TEXT_COLOR = Color.GREEN;
	private static final Color LOSE_TEXT_COLOR = Color.RED; 
	
	/* Size of a single pane (Standard size of objects). */
	public final int PANE_SIZE = 30;
	
	private ImageCache imageCache;	// The image cache for the canvas.	
	
	private Graphics2D graphicsContext;
	
	public BufferStrategy strategy; // Double buffered strategy.
	
	/*Fonts*/
	private Font statusFieldFont; //The font of the text in the status field
	private Font winLoseFont; //The font to write the win or lose messages
	
	/*Cursors*/
	private Cursor invisible;
	
	private int pf_width, pf_height;
	
	/**
	 * BreakoutCanvas constructor
	 * Creates a canvas for the game.
	 */
	public BreakoutCanvas() {
		imageCache = new ImageCache();			// Create an image cache.
		
		/*Make the font for the text in the status field*/
		statusFieldFont = new Font("Serif", Font.BOLD, 20);
		/*Make the font for the text in the win/lose messages*/
		winLoseFont = new Font("SansSerif", Font.BOLD, 40);
		
		/*Make the invisible cursor*/
		invisible = getToolkit().createCustomCursor(
				new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB),
				new Point(0, 0), "null");
		
		/* Set the size of the canvas. */
		this.setBounds(0, 0, Breakout.DEFAULT_WINDOW_WIDTH, Breakout.DEFAULT_WINDOW_HEIGHT + Breakout.STATUS_FIELD_HEIGHT);
	}
	
	/***
	 * Intiates the mouse listeners associated with the canvas
	 * @param mouseMotionController The mouse motion controller
	 * @param mouseController The mouse controller
	 */
	public void initMouseListeners(MouseMotionControl mouseMotionController, MouseControl mouseController) {
		addMouseMotionListener(mouseMotionController);
		addMouseListener(mouseController);
	}

	/**
	 * createStrategy
	 * Creates a double buffering strategy for the canvas.
	 */
	public void createStrategy() {
		this.setVisible(true);	// Show the canvas.

		/* Make sure that the canvas is visible. */
		if (this.isDisplayable()) {
			/* Create double buffering. */
			this.createBufferStrategy(2);
			this.strategy = this.getBufferStrategy();

			/* Set the focus. */
			this.requestFocus();
		} else {
			System.err.println("Breakout: Could not enable double buffering.");
			System.exit(1);	// Exit Breakout.
		}
	}

	/**
	 * imageChache
	 * Gets the canvas-specific image cache.
	 * @return image cache
	 */
	public ImageCache getImageCache() {
		return imageCache;
	}
	
	/**
	 * initGraphicsContext
	 * Initiates the graphics context
	 */
	public void initGraphicsContext() {
		graphicsContext = (Graphics2D) strategy.getDrawGraphics();
	}
	
	/**
	 * showGraphicsBuffer
	 * Makes the next available graphics buffer visible
	 */
	public void showGraphicsBuffer() {
		/*Dispose the graphics object.*/
		graphicsContext.dispose();
		/*Show buffer.*/
		strategy.show();
	}
	
	/**
	 * hideCursor
	 * Hide the cursor by using the invisible cursor
	 * */
	public void hideCursor() {
		setCursor(invisible);
	}
	
	/**
	 * showCursor
	 * Show the cursor by using the canvas parent's cursor
	 * */
	public void showCursor() {
		setCursor(null);
	}
	
	/***
	 * Paints the background
	 * @param levelNumber The current level
	 */
	public void paintBackground(int levelNumber) {
		/* Set up the background */
		graphicsContext.setBackground(BACKGROUND_COLOR);
		/*Clear the canvas*/
		paint(graphicsContext);
		/*Draw the background image*/
		graphicsContext.drawImage(imageCache.getImage(Helpers.getResourceURIString(this, "levels/" + "level_" + levelNumber + ".bmp")), 0, 0, getWidth(), getHeight(), this);
	}
	
	/***
	 * Paints the status field
	 * @param score The current score
	 * @param num_lives The current number of lives
	 */
	public void paintStatusField(int score, int num_lives) {
		/*Make sure the graphics context uses the status field font*/
		graphicsContext.setFont(statusFieldFont);
		/*Draw the shadow of the text*/
		graphicsContext.setColor(Color.BLACK);
		graphicsContext.drawString("Score " + score + "     " + "Lives " + num_lives, 0 + 2, Breakout.STATUS_FIELD_HEIGHT);
		/*Draw the text in the status field*/
		graphicsContext.setColor(STATUS_TEXT_COLOR);
		graphicsContext.drawString("Score " + score + "     " + "Lives " + num_lives, 0, Breakout.STATUS_FIELD_HEIGHT - 2);	
		/*Draw the line separating the status field from the playing field*/
		graphicsContext.drawLine(0, Breakout.STATUS_FIELD_HEIGHT, pf_width, Breakout.STATUS_FIELD_HEIGHT);
	}
	
	/***
	 * Paints a floor
	 * @param y y value of the floor
	 */
	public void paintFloor(int y)
	{
		graphicsContext.drawLine(0, y, pf_width, y);
	}
	
	/**
	 * paintLoseMessage
	 * Paints the lose message
	 */
	public void paintLoseMessage() {
		/*Make sure the graphics context uses the win/lose font*/
		graphicsContext.setFont(winLoseFont);
		/*Draw the lose message's shadow*/
		graphicsContext.setColor(Color.BLACK);
		graphicsContext.drawString("GAME OVER", (pf_width / 2) - 170 + 2, pf_height / 2 + 2);
		/*Draw the text of the lose message*/
		graphicsContext.setColor(LOSE_TEXT_COLOR);
		graphicsContext.drawString("GAME OVER", (pf_width / 2) - 170, pf_height / 2);
	}
	
	/**
	 * paintWinMessage
	 * Paints the win message
	 */
	public void paintWinMessage() {
		/*Make sure the graphics context uses the win/lose font*/
		graphicsContext.setFont(winLoseFont);
		/*Draw the shadow of the win message*/
		graphicsContext.setColor(Color.BLACK);
		graphicsContext.drawString("YOU WIN!", (pf_width / 2) - 170 + 2, pf_height / 2 + 2);
		/*Draw the text of the win message*/
		graphicsContext.setColor(WIN_TEXT_COLOR);
		graphicsContext.drawString("YOU WIN!", (pf_width / 2) - 170, pf_height / 2);
	}
	
	/**
	 * paint
	 * Paints a rectangle
	 * @param rect the rectangle to paint
	 */
	public void paint(Rectangle rect) {
		if(rect.getClass() == Paddle.class) {
			graphicsContext.drawImage(imageCache.getTransparentImage(Helpers.getResourceURIString(this, "images/" + rect.getImageName())), (int)rect.x, (int)rect.y, (int)rect.width, (int)rect.height, this);
		}
		else if(rect.getClass() == Brick.class) {
			graphicsContext.drawImage(imageCache.getTransparentImage(Helpers.getResourceURIString(this, "images/brick/" + rect.getImageName())), (int)rect.x, (int)rect.y, (int)rect.width, (int)rect.height, this);
		}
	}
	
	/**
	 * paint
	 * Paints a circle
	 * @param circle the circle to paint
	 */
	public void paint(Circle circle) {
		graphicsContext.drawImage(imageCache.getTransparentImage(Helpers.getResourceURIString(this, "images/" + circle.getImageName())), (int)circle.x, (int)circle.y, (int)(2 * circle.radius), (int)(2 * circle.radius), this);
	}
	
	/**
	 * paint
	 * Paints a powerup (animated sprite)
	 * @param powerup the powerup to paint
	 */
	public void paint(Powerup powerup)
	{
		/*Draw the powerup*/
		graphicsContext.drawImage(imageCache.getAnimatedSprite(Helpers.getResourceURIString(this, "images/" + powerup.getImageName()), Breakout.POWERUP_SPRITE_ROWS, Breakout.POWERUP_SPRITE_COLUMNS).getCurrentImage(), (int)powerup.x, (int)powerup.y, (int)powerup.width, (int)powerup.height, this);
	}

	/***
	 * Sets the width of the playing field
	 * @param width The width
	 */
	public void setPlayingFieldWidth(int width) {
		pf_width = width;
	}

	/***
	 * Gets the width of the playing field
	 * @return The width of the playing field
	 */
	public int getPlayingFieldWidth() {
		return pf_width;
	}

	/***
	 * Sets the height of the playing field
	 * @param height The height
	 */
	public void setPlayingFieldHeight(int height) {
		pf_height = height;
	}

	/***
	 * Gets the height of the playing field
	 * @return The height of the playing field
	 */
	public int getPlayingFieldHeight() {
		return pf_height;
	}
}
