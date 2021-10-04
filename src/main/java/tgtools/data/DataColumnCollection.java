package tgtools.data;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author tianjing
 */
public class DataColumnCollection extends ConcurrentHashMap<String, DataColumn> {


    /**
     *
     */
    private static final long serialVersionUID = -5271389896116350157L;

    public boolean containsColumn(String pColumnName) {
        if (this.values().size() > 0) {
            for (DataColumn column : this.values()) {
                return column.getTable().hasColumn(pColumnName);
            }
        }
        return false;
    }
}
