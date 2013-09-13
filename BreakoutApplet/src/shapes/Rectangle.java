package shapes;
import main.Breakout;

/***
 * A class representing a rectangle
 * @author Erik
 *
 */
public class Rectangle extends Shape {  
	public float width, height, left, right, top, bottom;
	
	/***
	 * The constructor of Rectangle
	 * @param x The x-value of the rectangle
	 * @param y The y-value of the rectangle
	 * @param width The width of the rectangle
	 * @param height The height of the rectangle
	 * @param imageName The image used for representing the rectangle
	 */
	public Rectangle(float x, float y, float width, float height, String imageName) {
		super(x, y, imageName);
		this.width = width;
		this.height = height;
		this.left = x;
		this.right = x + width;
		this.bottom = y + height;
		this.top = y;
	}
	
	/***
	 * Makes sure the rectangle stays inside the game area
	 * @param pf_width The width of the playing field
	 * @param pf_height The height of the playing field
	 */
	public void maintainBounderies(float pf_width, float pf_height) {
		/*Check if the rectangle is outside the canvas bounderies and move it inside if that is the case*/
		if(x < 0) {
			setXPos(0);
		}
		else if((x + width) > pf_width) {
			setXPos(pf_width - width);
		}
		if(y < Breakout.STATUS_FIELD_HEIGHT) {
			setYPos(Breakout.STATUS_FIELD_HEIGHT);
		}
		else if((y + height) > pf_height) {
			setYPos(pf_height - height);
		}
	}
	
	/***
	 * Check if the rectangle intersects another rectangle
	 * @param rect The other rectangle
	 * @return If the rectangles intersects
	 */
	public boolean checkForIntersection(Rectangle rect)
	{
		return ! ( rect.left > right 
				|| rect.right < left
				|| rect.top > bottom 
				|| rect.bottom < top);
	}
	
	public void move(float xMove, float yMove) {
		x += xMove;
		left += xMove;
		right += xMove;
		y += yMove;
		bottom += yMove;
		top += yMove;
	}
	
	public void xMove(float xMove)
	{
		x += xMove;
		left += xMove;
		right += xMove;
	}
	
	public void yMove(float yMove)
	{
		y += yMove;
		bottom += yMove;
		top += yMove;
	}
	
	public void setPosition(float x, float y)
	{
		this.x = x;
		this.left = x;
		this.right = x + width;
		this.y = y;
		this.bottom = y + height;
		this.top = y;
	}
	
	public void setXPos(float x)
	{
		this.x = x;
		this.left = x;
		this.right = x + width;
	}
	
	public void setYPos(float y)
	{
		this.y = y;
		this.bottom = y + height;
		this.top = y;
	}
}
