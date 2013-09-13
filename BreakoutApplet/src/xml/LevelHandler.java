package xml;

import main.Breakout;
import main.GameEngine;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import shapes.Brick;

/***
 * Handles level XML files
 * @author Erik
 *
 */
public class LevelHandler extends DefaultHandler {
	private GameEngine gameEngine;
	private boolean inLevelWidth = false;
	private boolean inLevelHeight = false;
	private boolean inX = false;
	private boolean inY = false;
	private boolean inColor = false;
	private int level_width, level_height, x, y;
	private String color;
	
	/***
	 * The constructor for LevelHandler
	 * @param gameEngine The game engine
	 */
	public LevelHandler(GameEngine gameEngine){
		this.gameEngine = gameEngine;
	}
	
	public void startElement(String uri, String localName,
	        String qName, Attributes attributes)
	        throws SAXException {
		if(qName.equals("levelWidth")) {
			inLevelWidth = true;
		}
		if(qName.equals("levelHeight")) {
			inLevelHeight = true;
		}
		if(qName.equals("x")) {
			inX = true;
		}
		if(qName.equals("y")) {
			inY = true;
		}
		if(qName.equals("color")) {
			inColor = true;
		}
	}
	
	public void characters(char ch[], int start, int length)
    throws SAXException {
		if(inLevelWidth) {
			try {
				level_width = Integer.parseInt(new String(ch, start, length));
			} catch (NumberFormatException e) {
				System.err.println("Breakout: Could not parse level_width");
				System.exit(1);
			}
		}
		
		if(inLevelHeight) {
			try {
				level_height = Integer.parseInt(new String(ch, start, length));
			} catch (NumberFormatException e) {
				System.err.println("Breakout: Could not parse level_height");
				System.exit(1);
			}
		}
		
		if(inX) {
			try {
				x = Integer.parseInt(new String(ch, start, length));
			} catch (NumberFormatException e) {
				System.err.println("Breakout: Could not parse x");
				System.exit(1);
			}
		}
		
		if(inY) {
			try {
				y = Integer.parseInt(new String(ch, start, length));
			} catch (NumberFormatException e) {
				System.err.println("Breakout: Could not parse y");
				System.exit(1);
			}
		}
		
		if(inColor) {
			color = new String(ch, start, length); 
		}
	}

	public void endElement(String uri, String localName,
	          String qName)
	          throws SAXException {
		if(qName.equals("levelWidth")) {
			gameEngine.setPlayingFieldWidth(level_width);
			inLevelWidth = false;
		}
		
		if(qName.equals("levelHeight")) {
			gameEngine.setPlayingFieldHeight(level_height + Breakout.STATUS_FIELD_HEIGHT);
			inLevelHeight = false;
		}
		
		if(qName.equals("x")) {
			inX = false;
		}
		
		if(qName.equals("y")) {
			inY = false;
		}
		
		if(qName.equals("color")) {
			inColor = false;
		}
		
		if(qName.equals("brick")) {
			gameEngine.getBricks().add(new Brick(x * GameEngine.BRICK_WIDTH, 
									   y * GameEngine.BRICK_HEIGHT, 
									   GameEngine.BRICK_WIDTH, 
									   GameEngine.BRICK_HEIGHT, 
									   gameEngine.getBrickConfiguration(color)));
		}
	}
}
