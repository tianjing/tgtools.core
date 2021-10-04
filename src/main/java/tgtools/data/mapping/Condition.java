package tgtools.data.mapping;

import tgtools.data.DataParameterCollection;
import tgtools.data.DataRow;

/**
 *
 * @author tianjing
 */
public abstract class Condition {

    public static AndCondition and(Condition[] pConditions) {
        AndCondition andCondition = new AndCondition();
        for (Condition condition : pConditions) {
            andCondition.add(condition);
        }
        return andCondition;
    }

    public static OrCondition or(Condition[] pConditions) {
        OrCondition orCondition = new OrCondition();
        for (Condition condition : pConditions) {
            orCondition.add(condition);
        }
        return orCondition;
    }

    public static NotCondition not(Condition pCondition) {
        return new NotCondition(pCondition);
    }

    public static EqualCondition equal(String pFieldName, Object pFieldValue, int pDataType) {
        return new EqualCondition(pFieldName, pFieldValue, pDataType);
    }

    public static LikeCondition like(String pFieldName, String pExpression) {
        return new LikeCondition(pFieldName, pExpression);
    }

    /**
     * isValid
     * @param paramDataRow
     * @return
     */
    public abstract boolean isValid(DataRow paramDataRow);

    /**
     * toSQL
     * @param paramDataParameterCollection
     * @return
     */
    public abstract String toSQL(DataParameterCollection paramDataParameterCollection);
}

