package shapes;
/***
 * A class representing a circle in the game
 * @author Erik
 *
 */
public class Circle extends Shape {

	public float radius;
	
	/***
	 * Constructor for circle
	 * @param x x value
	 * @param y y value
	 * @param radius radius of the circle
	 * @param imageName the image representation of the circle
	 */
	public Circle(float x, float y, float radius, String imageName) {
		super(x, y, imageName);
		
		this.radius = radius;
	}
}
