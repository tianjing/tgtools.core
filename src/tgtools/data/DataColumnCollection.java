package tgtools.data;

import java.util.concurrent.ConcurrentHashMap;

public class DataColumnCollection extends ConcurrentHashMap<String, DataColumn>{


	/**
	 * 
	 */
	private static final long serialVersionUID = -5271389896116350157L;
	public boolean containsColumn(String p_columnName)
	  {
	    return containsKey(p_columnName.toUpperCase());
	  }
}
