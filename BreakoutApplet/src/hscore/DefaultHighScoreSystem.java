package hscore;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.swing.JOptionPane;

import utils.Helpers;

import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.*;
import com.google.gdata.client.spreadsheet.*;

/**
 * A default high score system
 * @author Erik
 *
 */
public class DefaultHighScoreSystem implements HighScoreSystem {
	
	private HighScoreListDialog highScoreDialog;
	
	private SpreadsheetService highScoreService;
	private URL readWSListFeedURL, writeWSListFeedURL;
	
	/**
	 * The constructor for DefaultHighScoreSystem
	 * @param unsortedListFeed The URL to the spreadsheet where high score entries will be written 
	 * @param sortedListFeed The URL to the spreadsheet where high score entries will be read
	 * @param highScoreListDialog The high score dialog that the system will use
	 */
	public DefaultHighScoreSystem(URL unsortedListFeed, URL sortedListFeed, HighScoreListDialog highScoreDialog)
	{
		//Make a spreadsheet service
		highScoreService = new SpreadsheetService("HighScoreService");
	    
	    writeWSListFeedURL = unsortedListFeed;
	    readWSListFeedURL = sortedListFeed;
	    
	    this.highScoreDialog = highScoreDialog;
	}
	
	/**
	 * Registers a default high score entry
	 * @param score The score
	 * @param name The player's name
	 */
	private void registerScore(int score, String name)
	{	
		try 
		{
			ListEntry newEntry = new ListEntry();
			newEntry.getCustomElements().setValueLocal("name", name);
			newEntry.getCustomElements().setValueLocal("score", Integer.toString(score));
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
	
	public void registerScore(Object[] args)
	{
		int score = (Integer)args[0];
		
		String name = (String)JOptionPane.showInputDialog(
                null/*Måste möjligtvis ändra detta*/,
                "Please enter your name for the highscore list:",
                "High score",
                JOptionPane.INFORMATION_MESSAGE,
                null,
                null,
                "");
		
		if(name != null)
		{
			registerScore(score, name);
			showHighScoreList();
		}
	}
	
	public void showHighScoreList()
	{
		try {
			ListFeed scoreRowFeed = highScoreService.getFeed(readWSListFeedURL, ListFeed.class);
			
			ArrayList<Object[]> highScoreList = new ArrayList<Object[]>();
			
			for(ListEntry row : scoreRowFeed.getEntries())
			{
				Object[] objRow = new Object[3];
				objRow[0] = row.getCustomElements().getValue("name");
				objRow[1] = row.getCustomElements().getValue("score");
				
				objRow[2] = row.getCustomElements().getValue("date");
				objRow[2] = Helpers.utcToLocalTime((String)objRow[2]);
				
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
