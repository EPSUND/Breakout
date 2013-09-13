package usrinput;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Point;
import java.awt.Robot;
import java.awt.AWTException;

/***
 * A class handling mouse motion events
 * @author Erik
 *
 */
public class MouseMotionControl implements MouseMotionListener {

	private volatile int dx, dy;
	private volatile boolean isRelative;
	private Component component;
	private Point center;
	private Robot robot;
	
	/***
	 * The constructor for MouseMotionControl
	 * @param component The component to monitor mouse motion for
	 */
	public MouseMotionControl(Component component) {
		isRelative = false;
		dx = 0;
		dy = 0;
		this.component = component;
		try {
			robot = new Robot();
	    } 
		catch(AWTException e) {
			System.err.println("MouseMotionControl: Plattform configuration does not allow low-level input control");
			System.exit(1);
	    }
		catch(SecurityException e) {
			System.err.println("MouseMotionControl: createRobot permission is not granted");
			System.exit(1);
		}

		/*Calculate the center of the component*/
		calcCenter();
	}
	
	/***
	 * Get if relative mouse motion is enabled
	 * @return If relative mouse motion is enabled
	 */
	public boolean getIsRelative() {
		return isRelative;
	}
	
	/***
	 * Set if relative mouse should be active
	 * @param val If relative mouse should be active
	 */
	public void setIsRelative(boolean val) {
		isRelative = val;
	}
	
	/***
	 * Get the distance moved in x-direction
	 * @return The distance moved in x-direction
	 */
	public int getDx() {
		return dx;
	}
	
	/***
	 * Set the distance moved in x-direction
	 * @param val The distance moved in x-direction
	 */
	public void setDx(int val) {
		dx = val;
	}
	
	/***
	 * Get the distance moved in y-direction
	 * @return The distance moved in y-direction 
	 */
	public int getDy() {
		return dy;
	}
	
	/***
	 * Set the distance moved in y-direction
	 * @param val The distance moved in y-direction
	 */
	public void setDy(int val) {
		dy = val;
	}
	
	public synchronized void mouseDragged(MouseEvent me) {
		mouseMoved(me);
	}
	
	public synchronized void mouseMoved(MouseEvent me) {
		if(isRelative) {
			Point p = me.getPoint();
		    dx += p.x - center.x;
		    dy += p.y - center.y;
		    if(!((dx == 0) && (dy == 0))) {
		    	centerMouse();
		    }
		}
	}
	
	/***
	 * Centers the mouse in the middle of the component
	 */
	public void centerMouse() {
		if(robot != null) {
			Point copy = new Point(center);
			SwingUtilities.convertPointToScreen(copy, component);
			robot.mouseMove(copy.x, copy.y);
		}
	}

	/***
	 * Calculates the center of the component
	 */
	public void calcCenter()
	{
		center = new Point(component.getWidth() / 2, component.getHeight() / 2);
	}
}
