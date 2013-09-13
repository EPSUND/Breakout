package hscore;

/**
 * The table model for Breakout high score entries 
 * @author Erik
 *
 */
public class BreakoutHighScoreTableModel extends HighScoreTableModel {

	/**
	 * The constructor for BreakoutHighScoreTableModel
	 */
	public BreakoutHighScoreTableModel()
	{
		super();
		columnNames = new String[]{"Name", "Score", "Level", "Date"};
	}
}
