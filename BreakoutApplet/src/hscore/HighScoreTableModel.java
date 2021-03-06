package hscore;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * A table model for high score entries
 * @author Erik
 *
 */
public class HighScoreTableModel extends AbstractTableModel
{
	protected String[] columnNames;
	private ArrayList<Object[]> rows;
	
	/**
	 * The constructor for HighScoreTableModel
	 */
	public HighScoreTableModel()
	{
		rows = new ArrayList<Object[]>();
		columnNames = new String[]{"Name", "Score", "Date"};
	}
	
	/**
	 * Sets the rows of the table
	 * @param rows The rows
	 */
	public void setRows(ArrayList<Object[]> rows)
	{
		this.rows = rows;
	}
	
	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(rowIndex < rows.size())
		{
			if(columnIndex < rows.get(rowIndex).length)
			{
				return rows.get(rowIndex)[columnIndex];
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}
	
	public String getColumnName(int col) 
	{
        return columnNames[col];
    }
	
	public Class getColumnClass(int c) 
	{
        return getValueAt(0, c).getClass();
    }
}
