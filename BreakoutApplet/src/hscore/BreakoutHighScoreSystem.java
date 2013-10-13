package hscore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import utils.Helpers;

/**
 * A class handling the Breakout high score list
 * @author Erik
 *
 */
public class BreakoutHighScoreSystem implements HighScoreSystem {
	/**
	 * The URL to the high score service
	 */
	private static final String HIGH_SCORE_SERVICE_URL = "http://highscoresystemes86.appspot.com/highscoresystem";
	
	private HighScoreListDialog highScoreDialog;
	
	/**
	 * The constructor for BreakoutHighScoreSystem
	 * @param highScoreListDialog The high score dialog that the system will use
	 */
	public BreakoutHighScoreSystem(HighScoreListDialog highScoreListDialog)
	{	
		this.highScoreDialog = highScoreListDialog;
	}
	
	public void registerScore(Object[] args)
	{
		int score = (Integer)args[0];
		int level = (Integer)args[1];
		
		String name = (String)JOptionPane.showInputDialog(
                null,
                "Please enter your name for the highscore list:",
                "High score",
                JOptionPane.INFORMATION_MESSAGE,
                null,
                null,
                "");
		
		if(name != null)
		{
			try
			{
				URLConnection hscoreConn = getHighScoreConnection(score, level, name);
				
				if(hscoreConn != null)
				{
					hscoreConn.connect();
					showHighScoreList(hscoreConn);
				}
			}
			catch(IOException e)
			{
				System.err.println("Breakout: Could not connect to the highscore service");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Gets a connection to the high score service with the parameters needed to add a highscore entry
	 * @param score The score
	 * @param level The level
	 * @param name The player's name
	 * @return A connection to the highscore service
	 */
	private URLConnection getHighScoreConnection(int score, int level, String name)
	{	
		try 
		{
			URL registerHighScoreURL = new URL(HIGH_SCORE_SERVICE_URL + "?highScoreList=breakout" + 
											   "&name=" + name + 
											   "&score=" + Integer.toString(score) + 
											   "&level=" + Integer.toString(level) + 
											   "&date=" + Helpers.getCurrentTimeUTC());
			
			return registerHighScoreURL.openConnection();
			
		} catch (IOException e) {
			System.err.println("Breakout: Could not get a connection to the high score service");
			e.printStackTrace();
			return null;
		} 
	}
	
	/**
	 * Gets a connection to the high score service
	 * @return A connection to the highscore service
	 */
	private URLConnection getHighScoreConnection()
	{	
		try 
		{
			URL registerHighScoreURL = new URL(HIGH_SCORE_SERVICE_URL + "?highScoreList=breakout");	
			return registerHighScoreURL.openConnection();	
		} catch (IOException e) {
			System.err.println("Breakout: Could not get a connection to the high score service");
			e.printStackTrace();
			return null;
		} 
	}
	
	/***
	 * Shows the high score list. Uses an already existing connection to the highscore service to get the highscore data
	 * @param highScoreServiceConn A connection to the highscore service
	 */
	public void showHighScoreList(URLConnection highScoreServiceConn)
	{
		InputStream highScoreStream = null;
		
		try {
			highScoreStream = highScoreServiceConn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(highScoreStream));
			
			String line;
			ArrayList<Object[]> highScoreList = new ArrayList<Object[]>();
			
			while((line = reader.readLine()) != null)
			{
				String[] highScoreData = line.split(",");
				Object[] objRow = new Object[4];
				
				for(String highScoreItem : highScoreData)
				{
					String[] components = highScoreItem.split("=");
					
					if(components[0].equals("name"))
					{
						objRow[0] = components[1];
					}
					else if(components[0].equals("score"))
					{
						objRow[1] = components[1];
					}
					else if(components[0].equals("level"))
					{
						objRow[2] = components[1];
					}
					else if(components[0].equals("date"))
					{
						objRow[3] = Helpers.utcToLocalTime(components[1]);
					}
				}
				
				highScoreList.add(objRow);
			}
			
			highScoreDialog.setHighScoreList(highScoreList);
			highScoreDialog.setVisible(true);
		} catch (IOException e) {
			System.err.println("Breakout: Could not show high score list");
			e.printStackTrace();
		}
		finally
		{
			try {
				highScoreStream.close();
			} catch (IOException e) {
				System.err.println("Breakout: Could not close highscore stream");
				e.printStackTrace();
			}
		}
	}
	
	public void showHighScoreList()
	{
		URLConnection highScoreConn = getHighScoreConnection();
		
		if(highScoreConn != null)
			showHighScoreList(highScoreConn);
	}
}
