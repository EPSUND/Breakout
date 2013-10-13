package main;
import gui.BreakoutCanvas;
import hscore.BreakoutHighScoreSystem;
import hscore.BreakoutHighScoreTableModel;
import hscore.HighScoreListDialog;
import hscore.HighScoreSystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import sound.Jukebox;
import sound.SoundPlayer;
import utils.Helpers;

/***
 * The main class of the game and the program's entry point. Is a JApplet.
 * @author Erik
 *
 */
public class Breakout extends JApplet implements Runnable {
	
	private static final long serialVersionUID = 1L;//Serialization version. Not really used, but I added it anyway to get rid of a warning
	
	public static final int DEFAULT_WINDOW_WIDTH = 545;
	public static final int DEFAULT_WINDOW_HEIGHT = 540;
	
	/*Sprite constants*/
	public static final int BALL_SPRITE_ROWS = 4;
	public static final int BALL_SPRITE_COLUMNS = 8;
	public static final int POWERUP_SPRITE_ROWS = 7;
	public static final int POWERUP_SPRITE_COLUMNS = 1;
	
	/*Misc constants*/
	public static final int STATUS_FIELD_HEIGHT = 30;
	public static final String DEFAULT_BACKGROUND = "default.bmp";
	public static final int HIT_SCORE = 10;
	
	/*Game resources*/
	public static SoundPlayer breakoutSoundPlayer;
	public static Jukebox breakoutJukebox;
	public static HighScoreSystem highScoreSystem;
	
	public JPanel breakoutPanel;
	public JFrame breakoutFrame;
	public BreakoutCanvas breakoutCanvas;
	public GameEngine gameEngine;
	public JMenuBar menuBar;
	public JMenu gameMenu;
	public JMenu optionsMenu;
	public JMenuItem newGameItem; 
	public JMenuItem restartItem; 
	public JMenuItem pauseItem; 
	public JMenuItem resumeItem;
	public JMenuItem highScoreItem;
	public JMenuItem muteSoundEffectsItem;
	public JMenuItem muteMusicItem;
	
	private Thread gameThread;
	
	private volatile boolean start;
	
	public static int levelNumber;
	
	public static boolean hasFloor = false;
	
	/***
	 * Release resources held by the game
	 */
	public void destroy()
	{
		//Dispose sound resources
		breakoutSoundPlayer.disposePlayer();
		breakoutJukebox.disposeJukebox();
	}
	
	/***
	 * Stops the applet
	 */
	public void stop()
	{
		//Pause the game
		pause();
	}
	
	/***
	 * Starts the applet
	 */
	public void start()
	{
		//Resume the game
		resume();
	}
	
	/***
	 * The run method of the applet
	 */
	public void run()
	{
		try
		{
			do {
				gameEngine.initWorld();	// Initialize game variables.
				/* Set the applet size to the size specified in the level XML file. Extend the height somewhat for the status field and the menu bar*/
				setSize(gameEngine.getPlayingFieldWidth(), gameEngine.getPlayingFieldHeight() + menuBar.getHeight());
				/*Start the background music*/
				breakoutJukebox.loopSong("song1");
				/*Start the game. */
				gameEngine.game();
				} while(gameEngine.getRestart() || gameEngine.moreLevels());
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, Helpers.getStackTraceString(e), "Breakout: An exception has occured", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	/***
	 * Initiates the applet
	 */
	public void init()
	{
		start = false;
		
	    try {
	    	//Create the applet's GUI on the event-dispatching thread
	        SwingUtilities.invokeAndWait(new Runnable() {
	            public void run() {
	                createGUI();
	            }
	        });
	        
	        //Create game resources
	        createGameResources();
	        //Start the game
	        newGame();
	        
	    } catch (Exception e) {
	        System.err.println("Breakout: The GUI could not be created properly");
	        e.printStackTrace();
	        return;
	    }
	}
	
	/***
	 * Creates the resources held by the game
	 */
	private void createGameResources()
	{
		/*Make the sound effect player*/
		breakoutSoundPlayer = makeBreakoutSoundPlayer();
		
		/*Make the jukebox*/
		breakoutJukebox = makeBreakoutJukebox();
		
		/*Make the game engine*/
		gameEngine = new GameEngine(breakoutCanvas);
		
		/*Make the high score system*/
		highScoreSystem = new BreakoutHighScoreSystem(new HighScoreListDialog(new BreakoutHighScoreTableModel()));
	}
	
	/***
	 * Creates the GUI
	 */
	private void createGUI()
	{
		/*Get the frame's panel*/
		breakoutPanel = (JPanel)getContentPane();

		/* Create the menu bar. */
		menuBar = new JMenuBar();

		/* Create the game menu */
		gameMenu = new JMenu("Game");
		
		/*Add game menu to menu bar*/
		menuBar.add(gameMenu);
		
		/* Create the game menu items */
		newGameItem = new JMenuItem("New game");
		restartItem = new JMenuItem("Restart");
		pauseItem = new JMenuItem("Pause");
		resumeItem = new JMenuItem("Resume");
		highScoreItem = new JMenuItem("High score");
		
		/*Add the game menu items to the menu*/
		gameMenu.add(newGameItem);
		gameMenu.add(pauseItem);
		gameMenu.add(highScoreItem);
		
		/*Create the options menu*/
		optionsMenu = new JMenu("Options");
		
		/*Add the options menu to the menu bar*/
		menuBar.add(optionsMenu);
		
		/*Create the options menu items*/
		muteSoundEffectsItem = new JMenuItem("Mute sound effects");
		muteMusicItem = new JMenuItem("Mute music");
		
		/*Add the options menu items to the menu*/
		optionsMenu.add(muteSoundEffectsItem);
		optionsMenu.add(muteMusicItem);
		
		/* Install the menu bar in the frame. */
		gameMenu.getPopupMenu().setLightWeightPopupEnabled(false);
		
		/*Set the menu bar*/
		setJMenuBar(menuBar);
		
		/*Make the canvas*/
		breakoutCanvas = new BreakoutCanvas();
		
		/* Add the canvas to the panel. */
		breakoutPanel.add(breakoutCanvas);
		
		/* Enable double buffering. */
		breakoutCanvas.createStrategy();
		
		/*Add listeners*/
		
		pauseItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				pause();
			}
		});
		
		resumeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				resume();
			}
		});
		
		newGameItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				newGame();
			}
		});
		
		restartItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				gameEngine.setRestart(true);
			}
		});
		
		highScoreItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				highScoreSystem.showHighScoreList();
			}
		});
		
		muteSoundEffectsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(breakoutSoundPlayer.isMuted())
				{
					breakoutSoundPlayer.unmute();
					muteSoundEffectsItem.setText("Mute sound effects");
				}
				else
				{
					breakoutSoundPlayer.mute();
					muteSoundEffectsItem.setText("Unmute sound effects");
				}
			}
		});
		
		muteMusicItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(breakoutJukebox.isMuted())
				{
					breakoutJukebox.unmute();
					muteMusicItem.setText("Mute music");
				}
				else
				{
					breakoutJukebox.mute();
					muteMusicItem.setText("Unmute music");
				}
			}
		});
	}
	
	/***
	 * Pauses the game
	 */
	private void pause() {
		if(start && !gameEngine.getPause())
		{
			gameEngine.pause();
			gameMenu.remove(1);
			gameMenu.add(resumeItem, 1);
		}
	}

	/***
	 * Resumes the game
	 */
	private void resume() {
		if(gameEngine.getPause())
		{
			gameEngine.resume();
			gameMenu.remove(1);
			gameMenu.add(pauseItem, 1);
		}
	}

	/***
	 * Make the game's sound player with it's associated sound clips
	 * @return The game's sound player
	 */
	private SoundPlayer makeBreakoutSoundPlayer()
	{
		SoundPlayer soundPlayer = new SoundPlayer();
		
		soundPlayer.loadClip("audio/effects/" + "bong.wav", "Bong");
		soundPlayer.loadClip("audio/effects/" + "boom.wav", "Boom");
		soundPlayer.loadClip("audio/effects/" + "zap.wav", "Zap");
		soundPlayer.loadClip("audio/effects/" + "wilhelmScream.wav", "WilhelmScream");
		soundPlayer.loadClip("audio/effects/" + "feelgood.wav", "FeelGood");
		
 		return soundPlayer;
	}
	
	/***
	 * Make the game's jukebox and load it's songs
	 * @return The game's jukebox
	 */
	private Jukebox makeBreakoutJukebox()
	{
		Jukebox jukebox = new Jukebox();
		
		jukebox.loadSong("audio/music/" + "Jackson_F_Smith_-_01_-_Cantina_Rag.wav", "song1");
		
		return jukebox;
	}

	/***
	 * Starts a new game
	 */
	private void newGame() {
		//Display an information dialog explaining how to get the mouse back
		JOptionPane.showMessageDialog(this, "To make the mouse visible either press the left mouse button or press ESC", "Breakout", JOptionPane.INFORMATION_MESSAGE);
		
		start=true;
		gameMenu.remove(0);
		gameMenu.add(restartItem, 0);
		
		//Start the game thread
	    gameThread = new Thread(this);
	    gameThread.start();
	}
}
