package tgtools.data;

import java.util.concurrent.ConcurrentHashMap;

public class DataColumnCollection extends ConcurrentHashMap<String, DataColumn>{


	/**
	 * 
	 */
	private static final long serialVersionUID = -5271389896116350157L;
	public boolean containsColumn(String p_columnName)
	  {
		if(this.values().size()>0)
		{
			for(DataColumn column :this.values())
			{
				return column.getTable().hasColumn(p_columnName);
			}
		}
	  	return false;
	  }
}
