package tgtools.data.mapping;

import tgtools.data.DataParameterCollection;
import tgtools.data.DataRow;

/**
 * @author tianjing
 */
public class NotCondition extends Condition {
    private Condition innerCondition;

    public NotCondition(Condition pCondition) {
        this.innerCondition = pCondition;
    }

    @Override
    public boolean isValid(DataRow pRow) {
        return !this.innerCondition.isValid(pRow);
    }

    @Override
    public String toSQL(DataParameterCollection pParams) {
        String sql = this.innerCondition.toSQL(pParams);
        if ("".equals(sql)) {
            return "";
        }
        return "not " + sql;
    }
}
