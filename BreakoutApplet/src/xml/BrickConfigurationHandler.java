package xml;

import main.BrickConfiguration;
import main.GameEngine;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/***
 * Handles the XML representation of a brick configuration
 * @author Erik
 *
 */
public class BrickConfigurationHandler extends DefaultHandler {
	private GameEngine gameEngine;
	private boolean inHits;
	private boolean inScore;
	private boolean inColor;
	private int hits, score;
	private String color;
	
	/***
	 * Constructor for BrickConfigurationHandler
	 * @param gameEngine the game engine
	 */
	public BrickConfigurationHandler(GameEngine gameEngine){
		this.gameEngine = gameEngine;
	}
	
	/***
	 * Handles the start tags
	 */
	public void startElement(String uri, String localName,
	        String qName, Attributes attributes)
	        throws SAXException {	
		if(qName.equals("hits")) {
			inHits = true;
		}
		
		if(qName.equals("score")) {
			inScore = true;
		}
		
		if(qName.equals("color")) {
			inColor = true;
		}
	}
	
	/***
	 * Handles the characters
	 */
	public void characters(char ch[], int start, int length)
    throws SAXException {
		
		if(inHits) {
			try {
				hits = Integer.parseInt(new String(ch, start, length));
			} catch (NumberFormatException e) {
				System.err.println("Breakout: Could not parse level_width");
				System.exit(1);
			}
		}
		
		if(inScore) {
			try {
				score = Integer.parseInt(new String(ch, start, length));
			} catch (NumberFormatException e) {
				System.err.println("Breakout: Could not parse level_width");
				System.exit(1);
			}
		}
		
		if(inColor) {
			color = new String(ch, start, length);
		}
	}
	
	/***
	 * Handles the end tags
	 */
	public void endElement(String uri, String localName,
	          String qName)
	          throws SAXException {
		if(qName.equals("hits")) {
			inHits = false;
		}
		
		if(qName.equals("score")) {
			inScore = false;
		}
		
		if(qName.equals("color")) {
			inColor = false;
		}
		
		if(qName.equals("brickConfiguration")) {
			gameEngine.getBrickConfigurations().add(new BrickConfiguration(hits, score, color));
		}
	}
}
