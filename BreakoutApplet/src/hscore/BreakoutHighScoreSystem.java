package hscore;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import utils.Helpers;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

/**
 * A class handling the Breakout high score list
 * @author Erik
 *
 */
public class BreakoutHighScoreSystem implements HighScoreSystem {
	
	private static final String USERNAME = "shmiagames@gmail.com";
	private static final String PASSWORD = "holmsund19";
	
	private HighScoreListDialog highScoreDialog;
	
	private SpreadsheetService highScoreService;
	private URL readWSListFeedURL, writeWSListFeedURL;
	
	/**
	 * The constructor for BreakoutHighScoreSystem
	 * @param unsortedListFeed The URL to the spreadsheet where high score entries will be written 
	 * @param sortedListFeed The URL to the spreadsheet where high score entries will be read
	 * @param highScoreListDialog The high score dialog that the system will use
	 */
	public BreakoutHighScoreSystem(URL unsortedListFeed, URL sortedListFeed, HighScoreListDialog highScoreListDialog)
	{	
		readWSListFeedURL = sortedListFeed;
		writeWSListFeedURL = unsortedListFeed;
		this.highScoreDialog = highScoreListDialog;
		
		highScoreService = new SpreadsheetService("HighScoreService");
		
		//Make a spreadsheet service
		try 
		{
			highScoreService.setUserCredentials(USERNAME, PASSWORD);
		} catch (AuthenticationException e) {
			System.err.println("Breakout: Could not authenicate user");
			e.printStackTrace();
		}
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
			registerScore(score, level, name);
			showHighScoreList();
		}
	}
	
	/**
	 * Registers a Breakout high score entry
	 * @param score The score
	 * @param level The level
	 * @param name The player's name
	 */
	private void registerScore(int score, int level, String name)
	{	
		try 
		{
			ListEntry newEntry = new ListEntry();
			newEntry.getCustomElements().setValueLocal("name", name);
			newEntry.getCustomElements().setValueLocal("score", Integer.toString(score));
			newEntry.getCustomElements().setValueLocal("level", Integer.toString(level));
			newEntry.getCustomElements().setValueLocal("date", Helpers.getCurrentTimeUTC());
			highScoreService.insert(writeWSListFeedURL, newEntry);
		} catch (IOException e) {
			System.err.println("Breakout: Could not add score entry");
			e.printStackTrace();
		} catch (ServiceException e) {
			System.err.println("Breakout: Could not add score entry");
			e.printStackTrace();
		}
	}
	
	public void showHighScoreList()
	{
		try {
			ListFeed scoreRowFeed = highScoreService.getFeed(readWSListFeedURL, ListFeed.class);
			
			ArrayList<Object[]> highScoreList = new ArrayList<Object[]>();
			
			for(ListEntry row : scoreRowFeed.getEntries())
			{
				Object[] objRow = new Object[4];
				objRow[0] = row.getCustomElements().getValue("name");
				objRow[1] = row.getCustomElements().getValue("score");
				objRow[2] = row.getCustomElements().getValue("level");
				
				objRow[3] = row.getCustomElements().getValue("date");
				objRow[3] = Helpers.utcToLocalTime((String)objRow[3]);
				
				highScoreList.add(objRow);
			}
			
			highScoreDialog.setHighScoreList(highScoreList);
			highScoreDialog.setVisible(true);
		} catch (IOException e) {
			System.err.println("Breakout: Could not load score rows");
			e.printStackTrace();
		} catch (ServiceException e) {
			System.err.println("Breakout: Could not load score rows");
			e.printStackTrace();
		}
	}
}
