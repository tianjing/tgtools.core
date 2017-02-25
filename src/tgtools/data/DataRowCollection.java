package tgtools.data;

import java.util.ArrayList;

public class DataRowCollection extends ArrayList<DataRow>{
	  /**
	 * 
	 */
	private static final long serialVersionUID = 4921538670021716105L;

	public DataRow elementAt(int index)
	  {
	    return (DataRow)super.get(index);
	  }
}
