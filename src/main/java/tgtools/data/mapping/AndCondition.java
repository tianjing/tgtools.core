package tgtools.data.mapping;

import tgtools.data.DataParameterCollection;
import tgtools.data.DataRow;

/**
 * @author tianjing
 */
public class AndCondition extends CompositeCondition {
    @Override
    public boolean isValid(DataRow pRow) {
        for (Condition condition : this.conditions) {
            if (!condition.isValid(pRow)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toSQL(DataParameterCollection pParams) {
        if (this.conditions.size() == 0) {
            return "";
        }
        StringBuffer sql = new StringBuffer();
        sql.append("(");
        for (Condition condition : this.conditions) {
            if (!"(".equals(sql.toString())) {
                sql.append(" and ");
            }
            sql.append(condition.toSQL(pParams));
        }
        sql.append(")");
        return sql.toString();
    }
}
