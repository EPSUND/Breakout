package main;
/***
 * A configuration for a brick
 * @author Erik
 *
 */
public class BrickConfiguration {
	private int numberOfHits, score;
    private String color;
    
    /***
     * Constructor for BrickConfiguration
     */
    public BrickConfiguration()
    {
        setNumberOfHits(0);
        setScore(0);
        setColor("Red");
    }

    /***
     * Constructor for BrickConfiguration
     * @param numberOfHits number of hits before the brick should be eliminated
     * @param score the score the brick should give
     * @param color the color of the brick
     */
    public BrickConfiguration(int numberOfHits, int score, String color)
    {
        this.setNumberOfHits(numberOfHits);
        this.setScore(score);
        this.setColor(color);
    }

	/**
	 * Sets the color
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * Gets the color
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * Sets the number of hits
	 * @param numberOfHits the numberOfHits to set
	 */
	public void setNumberOfHits(int numberOfHits) {
		this.numberOfHits = numberOfHits;
	}

	/**
	 * Gets the number of hits
	 * @return the numberOfHits
	 */
	public int getNumberOfHits() {
		return numberOfHits;
	}

	/**
	 * Sets the score
	 * @param score the score to set
	 */
	public void setScore(int score) {
		this.score = score;
	}

	/**
	 * Gets the score
	 * @return the score
	 */
	public int getScore() {
		return score;
	}
}
