package shapes;
import gui.BreakoutCanvas;


/***
 * An abstract super class inherited by all geometric shapes used in the game
 * @author Erik
 *
 */
public abstract class Shape {
	public float x, y;
	protected String imageName;
	
	/***
	 * The constructor for Shape
	 * @param x The x-value for the shape
	 * @param y The y-value for the shape
	 * @param imageName The image representation of the shape
	 */
	public Shape(float x, float y, String imageName) {
		this.x = x;
		this.y = y;
		this.imageName = imageName;
	}
	
	/**
	 * Get the name of the image used by the shape
	 * @return The name of the image used by the shape
	 */
	public String getImageName() {
		return imageName;
	}
	
	/**
	 * Set the image used to represent the shape
	 * @param imageName The image to use
	 */
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	
	/**
	 * Sets the x-position
	 * @param x The x-position
	 */
	public void setXPos(float x){
		this.x = x;
	}
	
	/**
	 * Sets the y-position
	 * @param y The y-position
	 */
	public void setYPos(float y) {
		this.y = y;
	}
	
	/***
	 * Sets the position
	 * @param x The x-position
	 * @param y The y-position
	 */
	public void setPosition(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Makes sure the shape stays inside the game area
	 * @param canvas The game's canvas
	 */
	public void maintainBounderies(BreakoutCanvas canvas) {
		//No implementation for a general shape. Must be implemented by Shape's subclasses.
	}
	
	/**
	 * Moves the shape
	 * @param xMove Movement x-wise
	 * @param yMove Movement y-wise
	 */
	public void move(float xMove, float yMove) {
		x += xMove;
		y += yMove;
	}
	
	/**
	 * Moves the shape x-wise
	 * @param xMove Movement x-wise
	 */
	public void xMove(float xMove)
	{
		x += xMove;
	}
	
	/**
	 * Moves the shape y-wise
	 * @param yMove Movement y-wise
	 */
	public void yMove(float yMove)
	{
		y += yMove;
	}
}
