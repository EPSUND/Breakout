package usrinput;

import gui.BreakoutCanvas;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import main.Player;
import main.Shooter;

import shapes.Ball;

/***
 * A class handling mouse events
 * @author Erik
 *
 */
public class MouseControl implements MouseListener {

	private Ball ball;
	private Player player;
	private BreakoutCanvas canvas;
	
	/***
	 * The constructor for MouseControl
	 * @param ball The ball to control
	 * @param player The player object
	 * @param canvas The game canvas
	 */
	public MouseControl(Ball ball, Player player, BreakoutCanvas canvas) {
		this.ball = ball;
		this.player = player;
		this.canvas = canvas;
	}
	
	public synchronized void mouseClicked(MouseEvent me) {
		if(me.getButton() == MouseEvent.BUTTON1) {
			if(!player.getRelativeMouse()) {
				player.setRelativeMouse(true);
				canvas.hideCursor();
			}
			else {
				ball.releaseBall();
				
				Shooter shooter = player.getPaddleShooter();
				
				if(shooter != null)
				{
					shooter.shoot();
				}
			}
		}
		else if(me.getButton() == MouseEvent.BUTTON3) {
			player.setRelativeMouse(false);
			canvas.showCursor();
		}
	}

	public void mouseEntered(MouseEvent me) {
		//Nothing happens

	}

	public void mouseExited(MouseEvent me) {
		//Nothing happens

	}

	public void mousePressed(MouseEvent me) {
		//Nothing happens

	}

	public void mouseReleased(MouseEvent me) {
		//Nothing happens
	}

	/**
	 * Sets the ball to control with the mouse
	 * @param ball The ball
	 */
	public void setBall(Ball ball) {
		this.ball = ball;
	}
}
