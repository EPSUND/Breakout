package usrinput;

import gui.BreakoutCanvas;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import main.Player;

/***
 * A class handling input from the user with the keyboard
 * @author Erik
 *
 */
public class KeyControl extends KeyAdapter {
	
	private Player player;
	private BreakoutCanvas canvas;
	
	/***
	 * The constructor for KeyControl
	 * @param player The player object to associate with the KeyControl
	 * @param canvas The game canvas
	 */
	public KeyControl(Player player, BreakoutCanvas canvas) {
		this.player = player;
		this.canvas = canvas;
	}
	
	/***
	 * Handles the keyPressed event
	 * @param ke KeyEvent that occured
	 */
	public void keyPressed(KeyEvent ke) {
		/* Ignore keyboard if the player has lost. */
		if (player.getNumLives() == 0) {//Nödvändigt nu?
			return;
		}
		
		/* Calculate new movement offset. */
		switch (ke.getKeyCode()) {
			case KeyEvent.VK_ESCAPE :
				player.setRelativeMouse(false);
				canvas.showCursor();
				break;
			case KeyEvent.VK_D :
				if(ke.isShiftDown())
					player.setDebugMode(!player.getDebugMode());
				break;
			case KeyEvent.VK_S :
				if(player.getDebugMode())
					player.setStepFlag(true);
				break;
			case KeyEvent.VK_L :
				if(player.getDebugMode())
					player.setLevelCompleted(true);
				break;
		}
	}
}
