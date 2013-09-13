package shapes;
import gui.BreakoutCanvas;

import java.lang.Math;
import java.util.Vector;
import java.util.Random;

import main.Breakout;
import main.BrickConfiguration;
import main.GameEngine;
import main.Player;

/**
 * A class serving as the representation of the ball
 * @author Erik
 *
 */
public class Ball extends Circle {
	private float xVel, yVel, maxVel, vel_rand;
	private boolean isAtopPaddle;
	private static final float BALL_SENSITIVITY = 2.00f;
	
	/**
	 * Constructor for Ball
	 * @param x The x value
	 * @param y The y value
	 * @param xDir The direction x-wise. Between 0.0 and 1.0. Used with yDir to determine the direction of the ball
	 * @param yDir The direction y-wise. Between 0.0 and 1.0. Used with xDir to determine the direction of the ball 
	 * @param maxVel The maximum velocity
	 * @param radius The radius
	 * @param imageName The ball's image
	 */
	public Ball(float x, float y, float xDir, float yDir, float maxVel, float radius, String imageName) {
		super(x, y, radius, imageName);
		this.maxVel = maxVel;
		this.xVel = maxVel * xDir;
		this.yVel = maxVel * yDir * 2.0f;
		this.radius = radius;
		isAtopPaddle = true;
		vel_rand = maxVel / 5;
	}
	
	/**
	 * Updates the ball's position
	 * @param pf_width The playing field width
	 * @param pf_height The playing field height
	 * @param player The player
	 * @param bricks The level's bricks
	 * @param brickConfigurations The brick configurations
	 * @param canvas The canvas where everything is rendered
	 * @return If the movement of the ball resulted in the ball getting lost
	 */
	public boolean updatePosition(float pf_width, float pf_height, Player player, Vector<Brick> bricks, Vector<BrickConfiguration> brickConfigurations, BreakoutCanvas canvas) {
		/*Don't move the ball if it is atop the paddle*/
		if(isAtopPaddle) {
			/*Make sure the ball does not move outside the playing field*/
			return maintainBounderies(pf_width, pf_height);
		}
		
		/*Attempt to change the balls position*/
		x += xVel;
		y += yVel;
		
		/*Resolve any potential collision between the ball and the paddle*/
		resolveCollision(player.getPaddle());
		
		int score_sum = 0;
		boolean bricksStillInPlay = false;
		
		/*Resolve any potential collision between the ball and the level's bricks*/
		for(Brick brick : bricks) {
			if(brick.getIsAlive()) {//Only check bricks that are alive
				score_sum += resolveCollision(brick);
				brick.updateBrickColor(brickConfigurations);
				bricksStillInPlay = true;
			}
		}
		
		/*If no bricks are still in play has the player completed the level*/
		if(!bricksStillInPlay) {
			player.setLevelCompleted(true);
		}
		
		/*Add the points gained from hitting bricks*/
		player.addPoints(score_sum);
		
		/*Make sure the ball does not move outside the canvas*/
		return maintainBounderies(pf_width, pf_height);
	}
	
	/**
	 * Checks if the ball is outside the canvas boundaries 
	 * and moves it inside again if that is the case and changes the ball's direction.
	 * @param pf_width The playing field width
	 * @param pf_height The playing field height
	 * @return If the ball has passed outside a boundary that should kill it
	 */
	public boolean maintainBounderies(float pf_width, float pf_height) {
		if(x - radius < 0) {
			x = radius;
			xVel *= -1;
		}
		else if((x + radius) > pf_width) {
			x = pf_width - radius;
			xVel *= -1;
		}
		
		if(y - radius < Breakout.STATUS_FIELD_HEIGHT) {
			y = Breakout.STATUS_FIELD_HEIGHT + radius;
			yVel *= -1;
		}
		else if((y + radius > pf_height) && !Breakout.hasFloor) {
			/*The ball has hit the bottom of the playing field
			 *  and the ball should be lost if no floor exists*/
			return true;
		}
		else if((y + radius > pf_height - GameEngine.PADDLE_SPACING) && Breakout.hasFloor)
		{
			/*There is a floor and the ball should bounce up*/
			y = pf_height - GameEngine.PADDLE_SPACING - radius;
			yVel *= -1;
		}
		
		return false;
	}
	
	/**
	 * Resolves a collision between a ball and the paddle
	 * @param paddle The paddle
	 */
	public void resolveCollision(Paddle paddle) {
		/*Get the center point of the paddle*/
		float centerX = (2 * paddle.x + paddle.width) / 2;
		float centerY = (2 * paddle.y + paddle.height) / 2;
		
		/*Translate the ball's center point*/
		float bTransX = x - centerX;
		float bTransY = y - centerY;
		
		/*Get a (width / 2) and b (height / 2)*/
		float a = paddle.width / 2;
		float b = paddle.height / 2;
		
		/*Get the percent distance for x*/
		float percentDistX = bTransX / a;
		
		float paddleVel;
		
		Random rand = new Random();
		
		/*Broad-phase collision detection: Checks if the paddle and the ball can possibly collide*/
		if(((bTransX < (-a - radius)) || (bTransX > (a + radius))) &&
		   ((bTransY < (-b - radius)) || (bTransY > (b + radius)))){
			return;
		}
		
		if((bTransX > -a) && (bTransX < a) &&
				(bTransY > -b) && (bTransY < b)){//If the ball's center point is completely inside the paddle
			
			/*Make sure the ball is no longer intersecting the paddle*/
			
			float xDist, yDist, radSignX, radSignY;
			
			/*Get which vertical edge the ball's center point is closest to*/
			if((a - bTransX) > a) {
				xDist = -a - bTransX;
				radSignX = -1.0f;
			}
			else {
				xDist = a - bTransX;
				radSignX = 1.0f;
			}
			/*Get which horizontal edge the ball's center point is closest to*/
			if((b - bTransY) > b) {
				yDist = -b - bTransY;  
				radSignY = -1.0f;
			}
			else {
				yDist = b - bTransY;
				radSignY = 1.0f;
			}
			
			/*Move the center point and flip the ball's direction*/
			if(xDist < yDist) {
				x += (xDist + radius * radSignX); 
				xVel *= -1;
			}
			else if(yDist < xDist) {
				y += (yDist + radius * radSignY);
				
				/*Change the direction of the ball*/
				paddleVel = percentDistX * maxVel * BALL_SENSITIVITY - vel_rand + 2 * rand.nextFloat() * vel_rand;
				
				if((xVel + paddleVel) > maxVel) {
					xVel = maxVel;
				}
				else if((xVel + paddleVel) < -maxVel) {
					xVel = -maxVel;
				}
				else {
					xVel += paddleVel;
				}
				yVel *= -1;
			}
			else {
				x += (xDist + radius * radSignX);
				y += (yDist + radius * radSignY);
				
				/*Change the direction of the ball*/
				paddleVel = percentDistX * maxVel * BALL_SENSITIVITY - vel_rand + 2 * rand.nextFloat() * vel_rand;
				
				if((xVel + paddleVel) > maxVel) {
					xVel = maxVel;
				}
				else if((xVel + paddleVel) < -maxVel) {
					xVel = -maxVel;
				}
				else {
					xVel += paddleVel;
				}
				yVel *= -1;
			}	
		}
		else if((bTransX > -a) && (bTransX < a)) {//The ball's center point is either over or under the rectangle 
			if((bTransY < b + radius) && (bTransY > b)) {//If the ball is intersecting the rectangle's uppermost edge
				/*Make sure the ball is no longer intersecting the paddle*/
				y += (b - bTransY + radius);
				/*Change the direction of the ball*/
				paddleVel = percentDistX * maxVel * BALL_SENSITIVITY - vel_rand + 2 * rand.nextFloat() * vel_rand;
				
				if((xVel + paddleVel) > maxVel) {
					xVel = maxVel;
				}
				else if((xVel + paddleVel) < -maxVel) {
					xVel = -maxVel;
				}
				else {
					xVel += paddleVel;
				}
				yVel *= -1;
			}
			else if((bTransY > -b - radius) && (bTransY < -b)) {//If the ball is intersecting the rectangle's lowermost edge
				/*Make sure the ball is no longer intersecting the paddle*/
				y += (-b - bTransY - radius);
				/*Change the direction of the ball*/
				paddleVel = percentDistX * maxVel * BALL_SENSITIVITY - vel_rand + 2 * rand.nextFloat() * vel_rand;
				
				if((xVel + paddleVel) > maxVel) {
					xVel = maxVel;
				}
				else if((xVel + paddleVel) < -maxVel) {
					xVel = -maxVel;
				}
				else {
					xVel += paddleVel;
				}
				yVel *= -1;
			}
		}
		else if((bTransY > -b) && (bTransY < b)) {//The ball's center point is either to the left or right of the rectangle
			if((bTransX < a + radius) && (bTransX > a)) {//If the ball is intersecting the rectangle's rightmost edge
				/*Make sure the ball is no longer intersecting the paddle*/
				x += (a - bTransX + radius);
				/*Change the direction of the ball*/
				xVel *= -1;
			}
			else if((bTransX > -a - radius) && (bTransX < -a)) {//If the ball is intersecting the rectangle's leftmost edge	
				/*Make sure the ball is no longer intersecting the paddle*/
				x += (-a - bTransX - radius);
				/*Change the direction of the ball*/
				xVel *= -1;
			}
		}
		else if(Math.pow((Math.abs(bTransX) - a), 2) + 
				Math.pow((Math.abs(bTransY) - b), 2) < Math.pow(radius, 2)) {//If the balls center point is closest to one of the rectangle's corners	
			/*Make sure the ball is no longer intersecting the paddle*/
			if((bTransX > 0) && (bTransY > 0)) {
				x += (a - bTransX + radius);
				y += (b - bTransY + radius);
			}
			else if((bTransX > 0) && (bTransY < 0)) {
				x += (a - bTransX + radius);
				y += (-b - bTransY - radius);
			}
			else if((bTransX < 0) && (bTransY > 0)) {
				x += (-a - bTransX - radius);
				y += (b - bTransY + radius);
			}
			else {
				x += (-a - bTransX - radius);
				y += (-b - bTransY - radius);
			}
			
			/*Change the direction of the ball*/
			paddleVel = percentDistX * maxVel * BALL_SENSITIVITY - vel_rand + 2 * rand.nextFloat() * vel_rand;
			
			if((xVel + paddleVel) > maxVel) {
				xVel = maxVel;
			}
			else if((xVel + paddleVel) < -maxVel) {
				xVel = -maxVel;
			}
			else {
				xVel += paddleVel;
			}
			yVel *= -1;
		}
	}
	
	/**
	 * Resolves a collision between the ball and a brick
	 * @param brick A brick
	 * @return The score gained by colliding with the brick(if a collision occurred) or 0(if no collision occurred)
	 */
	public int resolveCollision(Brick brick) {
		/*Get the center point of the brick*/
		float centerX = (2 * brick.x + brick.width) / 2;
		float centerY = (2 * brick.y + brick.height) / 2;
		
		/*Translate the ball's center point*/
		float bTransX = x - centerX;//Är det så man translaterar?
		float bTransY = y - centerY;
		
		/*Get a (width / 2) and b (height / 2)*/
		float a = brick.width / 2;
		float b = brick.height / 2;
		
		/*Broad-phase collision detection: Checks if the brick and the ball can possibly collide*/
		if(((bTransX < (-a - radius)) || (bTransX > (a + radius))) &&
		   ((bTransY < (-b - radius)) || (bTransY > (b + radius)))){
			return 0;
		}
		
		if((bTransX > -a) && (bTransX < a) &&
				(bTransY > -b) && (bTransY < b)){//If the ball's center point is completly inside the brick
			
			/*Make sure the ball is no longer intersecting the brick*/
			
			float xDist, yDist, radSignX, radSignY;
			
			/*Get which vertical edge the ball's center point is closest to*/
			if((a - bTransX) > a) {
				xDist = -a - bTransX;
				radSignX = -1.0f;
			}
			else {
				xDist = a - bTransX;
				radSignX = 1.0f;
			}
			/*Get which horizontal edge the ball's center point is closest to*/
			if((b - bTransY) > b) {
				yDist = -b - bTransY;  
				radSignY = -1.0f;
			}
			else {
				yDist = b - bTransY;
				radSignY = 1.0f;
			}
			
			/*Move the center point and flip the ball's direction*/
			if(xDist < yDist) {
				x += (xDist + radius * radSignX); 
				
				/*Change the direction of the ball*/
				xVel *= -1;
			}
			else if(yDist < xDist) {
				y += (yDist + radius * radSignY);
				
				/*Change the direction of the ball*/
				yVel *= -1;
			}
			else {
				x += (xDist + radius * radSignX);
				y += (yDist + radius * radSignY); 
				
				/*Change the direction of the ball*/
				yVel *= -1;
			}
			
			/*Signal a hit to the brick*/
			brick.hit();
			/*Check if the hit elminated the brick*/
			if(brick.getIsAlive())
			{
				/*Award the player with the hit points*/
				return Breakout.HIT_SCORE;
			}
			else
			{
				/*Award the player the brick's score*/
				return brick.getScore();
			}
		}
		else if((bTransX > -a) && (bTransX < a)) {//The ball's center point is either over or under the rectangle 
			if((bTransY < b + radius) && (bTransY > b)) {//If the ball is intersecting the rectangle's uppermost edge
				/*Change the direction of the ball*/
				yVel *= -1;
				
				/*Make sure the ball is no longer intersecting the brick*/
				y += (b - bTransY + radius);
				
				/*Signal a hit to the brick*/
				brick.hit();
				/*Check if the hit elminated the brick*/
				if(brick.getIsAlive())
				{
					/*Award the player with the hit points*/
					return Breakout.HIT_SCORE;
				}
				else
				{
					/*Award the player the brick's score*/
					return brick.getScore();
				}
			}
			else if((bTransY > -b - radius) && (bTransY < -b)) {//If the ball is intersecting the rectangle's lowermost edge
				/*Change the direction of the ball*/
				yVel *= -1;
				
				/*Make sure the ball is no longer intersecting the brick*/
				y += (-b - bTransY - radius);
				
				/*Signal a hit to the brick*/
				brick.hit();
				/*Check if the hit elminated the brick*/
				if(brick.getIsAlive())
				{
					/*Award the player with the hit points*/
					return Breakout.HIT_SCORE;
				}
				else
				{
					/*Award the player the brick's score*/
					return brick.getScore();
				}
			}
			else {//Should never happen
				return 0;
			}
		}
		else if((bTransY > -b) && (bTransY < b)) {//The ball's center point is either to the left or right of the rectangle
			if((bTransX < a + radius) && (bTransX > a)) {//If the ball is intersecting the rectangle's rightmost edge
				/*Change the direction of the ball*/
				xVel *= -1;
				
				/*Make sure the ball is no longer intersecting the brick*/
				x += (a - bTransX + radius);
				
				/*Signal a hit to the brick*/
				brick.hit();
				/*Check if the hit elminated the brick*/
				if(brick.getIsAlive())
				{
					/*Award the player with the hit points*/
					return Breakout.HIT_SCORE;
				}
				else
				{
					/*Award the player the brick's score*/
					return brick.getScore();
				}
			}
			else if((bTransX > -a - radius) && (bTransX < -a)) {//If the ball is intersecting the rectangle's leftmost edge
				/*Change the direction of the ball*/
				xVel *= -1;
				
				/*Make sure the ball is no longer intersecting the brick*/
				x += (-a - bTransX - radius);
				
				/*Signal a hit to the brick*/
				brick.hit();
				/*Check if the hit elminated the brick*/
				if(brick.getIsAlive())
				{
					/*Award the player with the hit points*/
					return Breakout.HIT_SCORE;
				}
				else
				{
					/*Award the player the brick's score*/
					return brick.getScore();
				}
			}
			else {//Should never happen
				return 0;
			}
		}
		else if(Math.pow((Math.abs(bTransX) - a), 2) + 
				Math.pow((Math.abs(bTransY) - b), 2) < Math.pow(radius, 2)) {//If the balls center point is closest to one of the rectangle's corners
			/*Change the direction of the ball*/
			yVel *= -1;
			
			/*Make sure the ball is no longer intersecting the brick*/
			if((bTransX > 0) && (bTransY > 0)) {
				x += (a - bTransX + radius);
				y += (b - bTransY + radius);
			}
			else if((bTransX > 0) && (bTransY < 0)) {
				x += (a - bTransX + radius);
				y += (-b - bTransY - radius);
			}
			else if((bTransX < 0) && (bTransY > 0)) {
				x += (-a - bTransX - radius);
				y += (b - bTransY + radius);
			}
			else {
				x += (-a - bTransX - radius);
				y += (-b - bTransY - radius);
			}
			
			/*Signal a hit to the brick*/
			brick.hit();
			/*Check if the hit elminated the brick*/
			if(brick.getIsAlive())
			{
				/*Award the player with the hit points*/
				return Breakout.HIT_SCORE;
			}
			else
			{
				/*Award the player the brick's score*/
				return brick.getScore();
			}
		}
		else {//The ball is not colliding with the brick
			return 0;
		}
	}
	
	/**
	 * Set if the ball is atop the paddle
	 * @param val If the ball is atop the paddle
	 */
	public void setIsAtopPaddle(boolean val) {
		isAtopPaddle= val;
	}
	
	/**
	 * Get if the ball is atop the paddle
	 * @return If the ball is atop the paddle
	 */
	public boolean getIsAtopPaddle() {
		return isAtopPaddle;
	}
	
	/**
	 * Reset the velocity of the ball
	 */
	public void resetVelocity() {
		xVel = maxVel;
		yVel = maxVel * 2.0f;
	}
	
	/**
	 * Release the ball from the paddle(if it is on top of it)
	 */
	public void releaseBall() {
		if(isAtopPaddle) {//Check if the ball should be released
			/*Release the ball*/
			isAtopPaddle = false;
			resetVelocity();
		}
	}
}
