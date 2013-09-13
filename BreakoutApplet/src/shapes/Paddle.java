package shapes;
import main.Breakout;


/***
 * A class representing a paddle in the game
 * @author Erik
 *
 */
public class Paddle extends Rectangle {
	
	/***
	 * The constructor of Paddle
	 * @param x The x-value of the paddle
	 * @param y The y-value of the paddle
	 * @param width The width of the paddle
	 * @param height The height of the paddle
	 * @param imageName The image representing the paddle
	 */
	public Paddle(float x, float y, float width, float height, String imageName) {
		super(x, y, width, height, imageName);
	}
	
	/***
	 * Makes sure a paddle with a ball on top of it stays inside the game area
	 * @param pf_width The playing field width
	 * @param pf_height The playing field height
	 * @param ball The ball
	 * @return If the paddle is stuck against a wall
	 */
	public boolean maintainBounderies(float pf_width, float pf_height, Ball ball) {
		
		boolean paddleStuck = false;
		
		/*Check if the paddle or a ball resting on paddle is outside the playing field's bounderies and move the paddle inside if that is the case*/
		
		/*Check against the x bounderies*/
		if((ball.x - ball.radius) < 0) {// If some part of the ball is outside the negative x boundry of the playing field
			xMove(ball.radius - ball.x);
		}
		else if((ball.x + ball.radius) > pf_width) {// If some part of the ball is outside the positive x boundry of the playing field
			xMove(-(ball.x + ball.radius - pf_width));
		}
		
		if(x < 0) {// If some part of the paddle is outside the negative x boundry of the playing field
			setXPos(0);
			paddleStuck = true;
		}
		else if((x + width) > pf_width) {// If some part of the paddle is outside the positive x boundry of the playing field
			setXPos(pf_width - width);
			paddleStuck = true;
		}
		
		/*Check against the y bounderies*/
		if((ball.y - ball.radius) < Breakout.STATUS_FIELD_HEIGHT) {// If some part of the ball is outside the negative y boundry of the playing field
			yMove(Breakout.STATUS_FIELD_HEIGHT + ball.radius - ball.y);
		}
		else if((ball.y + ball.radius) > pf_height) {// If some part of the ball is outside the positive y boundry of the playing field
			yMove(-(ball.y + ball.radius - pf_height)); 
		}
		
		if(y < Breakout.STATUS_FIELD_HEIGHT) {// If some part of the paddle is outside the negative y boundry of the playing field
			setYPos(Breakout.STATUS_FIELD_HEIGHT);
			paddleStuck = true;
		}
		else if((y + height) > pf_height) {// If some part of the paddle is outside the positive y boundry of the playing field
			setYPos(pf_height - height);
			paddleStuck = true;
		}
		
		return paddleStuck;
	}
}
